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
import org.openqa.selenium.WebDriver;

@Slf4j
public class Authenticator
{
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
        if (dataAIS.isEnabled()) // if identity-service is enabled do the login using the UI
        {
            logoutIfAlfrescoLogoIsDisplayed(userDashboardPage, new Toolbar(webDriver));
            authenticateUsingLoginPage(user, userDashboardPage);
        }
        else
        {
            authenticateUsingBrowserCookies(user, url);
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
        deleteAllCookiesIfNotNull();
        getLoginPage().navigate().login(userModel);
        userDashboardPage.waitForSharePageToLoad();
    }

    private static final int LOGIN_MAX_ATTEMPTS = 3;
    private static final long LOGIN_RETRY_DELAY_MILLIS = 500L;

    private void authenticateUsingBrowserCookies(UserModel user, String url)
    {
        navigateToAppUrl(url);
        HttpState state = loginWithRetry(user);
        deleteAllCookiesIfNotNull();

        Arrays.stream(state.getCookies()).forEach(cookie -> {
            cookie.setPath("/share");
            webDriver.get().manage().addCookie(new Cookie(cookie.getName(), cookie.getValue(), cookie.getPath()));
        });
    }

    /**
     * Performs login and retrieves the {@link HttpState} used to seed browser cookies.
     * The underlying dataprep {@code login()} call can transiently return {@code null}
     * (e.g. target server not fully up yet in CI), so a few retries are attempted before
     * failing fast with a descriptive exception instead of letting an NPE surface downstream.
     */
    private HttpState loginWithRetry(UserModel user)
    {
        HttpState state = null;
        for (int attempt = 1; attempt <= LOGIN_MAX_ATTEMPTS && state == null; attempt++)
        {
            state = getUserService().login(user.getUsername(), user.getPassword());
            if (state == null && attempt < LOGIN_MAX_ATTEMPTS)
            {
                log.warn("Cookie auth login attempt {}/{} for user {} returned no HTTP state, retrying...",
                    attempt, LOGIN_MAX_ATTEMPTS, user.getUsername());
                sleepQuietly(LOGIN_RETRY_DELAY_MILLIS);
            }
        }

        if (state == null)
        {
            throw new AuthenticationFailedException(
                "Unable to authenticate user '" + user.getUsername() + "' using cookies: login() returned no HTTP "
                    + "state after " + LOGIN_MAX_ATTEMPTS + " attempts. The target server may not be reachable/ready.");
        }
        return state;
    }

    private void sleepQuietly(long millis)
    {
        try
        {
            Thread.sleep(millis);
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
