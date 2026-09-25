package org.alfresco.share;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginAimsPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.httpclient.HttpState;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

@Slf4j
public class Authenticator
{
    private static final int LOGIN_MAX_ATTEMPTS = 3;
    private static final int LOGIN_RETRY_WAIT_SECONDS = 2;

    private final DataAIS dataAIS;
    private ThreadLocal<UserService> userService;
    private final ThreadLocal<WebDriver> webDriver;

    protected Authenticator(DataAIS dataAIS, ThreadLocal<UserService> userService, ThreadLocal<WebDriver> webDriver)
    {
        this.dataAIS = dataAIS;
        this.userService = userService;
        this.webDriver = webDriver;
    }

    protected Authenticator(ThreadLocal<WebDriver> webDriver, DataAIS dataAIS)
    {
        this.webDriver = webDriver;
        this.dataAIS = dataAIS;
    }

    protected synchronized void authenticateUsingCookies(UserModel user,
                                                         UserDashboardPage userDashboardPage,
                                                         String url)
    {
        log.info("Setup authenticated session for user {}", user.getUsername());
        logoutIfAlfrescoLogoIsDisplayed(userDashboardPage, new Toolbar(webDriver));

        if (dataAIS.isEnabled()) // if identity-service is enabled do the login using the UI
        {
            authenticateUsingLoginPage(user, userDashboardPage);
        }
        else
        {
            authenticateUsingCookiesWithFallback(user, userDashboardPage, url);
        }
    }

    private void authenticateUsingCookiesWithFallback(UserModel user,
                                                      UserDashboardPage userDashboardPage,
                                                      String url)
    {
        if (userService == null || getUserService() == null)
        {
            log.warn("UserService is not available for cookie authentication, falling back to UI login for user {}",
                user.getUsername());
            authenticateUsingLoginPage(user, userDashboardPage);
            return;
        }

        try
        {
            authenticateUsingBrowserCookies(user, url);
            userDashboardPage.navigate(user);
            userDashboardPage.waitForSharePageToLoad();
        }
        catch (RuntimeException e)
        {
            log.warn("Cookie authentication failed for user {}, falling back to UI login: {}",
                user.getUsername(), e.getMessage());
            authenticateUsingLoginPage(user, userDashboardPage);
        }
    }

    private void logoutIfAlfrescoLogoIsDisplayed(UserDashboardPage userDashboardPage, Toolbar toolbar)
    {
        if (userDashboardPage.isAlfrescoLogoDisplayed())
        {
            toolbar.clickUserMenu().clickLogout();
        }
    }

    protected synchronized void authenticateUsingLoginPage(UserModel userModel,
                                                           UserDashboardPage userDashboardPage)
    {
        authenticateUsingLoginPage(userModel, userDashboardPage, LOGIN_MAX_ATTEMPTS);
    }

    // The login page/dashboard can occasionally take longer than the explicit wait to load under CI load; retry the whole navigate+login flow instead of failing on the first slow load.
    private void authenticateUsingLoginPage(UserModel userModel, UserDashboardPage userDashboardPage, int attemptsLeft)
    {
        deleteAllCookiesIfNotNull();
        try
        {
            getLoginPage().navigate().login(userModel);
            userDashboardPage.waitForSharePageToLoad();
        }
        catch (TimeoutException e)
        {
            if (attemptsLeft <= 1)
            {
                throw new TimeoutException(
                    "Login page/dashboard did not load for user " + userModel.getUsername()
                        + " after " + LOGIN_MAX_ATTEMPTS + " attempts", e);
            }
            log.warn("Login page/dashboard did not load in time for user {}, retrying ({} attempts left): {}",
                userModel.getUsername(), attemptsLeft - 1, e.getMessage());
            authenticateUsingLoginPage(userModel, userDashboardPage, attemptsLeft - 1);
        }
    }

    private void authenticateUsingBrowserCookies(UserModel user, String url)
    {
        navigateToAppUrl(url);
        HttpState state = loginWithRetry(user, LOGIN_MAX_ATTEMPTS);
        deleteAllCookiesIfNotNull();

        Arrays.stream(state.getCookies()).forEach(cookie -> {
            cookie.setPath("/share");
            webDriver.get().manage().addCookie(new Cookie(cookie.getName(), cookie.getValue(), cookie.getPath()));
        });
    }

    // UserService.login() returns null (instead of throwing) when the login POST doesn't get a 302 redirect, which can happen transiently under CI load; retry a few times before failing with a clear error.
    private HttpState loginWithRetry(UserModel user, int attemptsLeft)
    {
        HttpState state = getUserService().login(user.getUsername(), user.getPassword());
        if (state == null)
        {
            if (attemptsLeft <= 1)
            {
                throw new IllegalStateException(
                    "Login failed for user " + user.getUsername() + ": UserService.login() returned no session state after " + LOGIN_MAX_ATTEMPTS + " attempts");
            }
            log.warn("Login returned no session state for user {}, retrying ({} attempts left)", user.getUsername(), attemptsLeft - 1);
            waitInSeconds(LOGIN_RETRY_WAIT_SECONDS);
            return loginWithRetry(user, attemptsLeft - 1);
        }
        return state;
    }

    private void waitInSeconds(int seconds)
    {
        try
        {
            Thread.sleep(seconds * 1000L);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private void navigateToAppUrl(String url)
    {
        webDriver.get().get(url);
    }

    private CommonLoginPage getLoginPage()
    {
        if (dataAIS.isEnabled())
        {
            return new LoginAimsPage(webDriver);
        }
        return new LoginPage(webDriver);
    }

    protected void deleteAllCookiesIfNotNull()
    {
        if (webDriver.get().manage().getCookies() != null)
        {
            log.info("Delete all cookies");
            webDriver.get().manage().deleteAllCookies();
        }
    }

    private UserService getUserService()
    {
        return userService.get();
    }
}
