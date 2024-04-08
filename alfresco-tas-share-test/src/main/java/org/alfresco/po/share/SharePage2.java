package org.alfresco.po.share;

import static org.alfresco.common.Wait.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.util.UriUtils;

@Slf4j
public abstract class SharePage2<T> extends BasePage
{
    private final By pageHeader = By.cssSelector("#HEADER_TITLE");
    private final By alfrescoOneFooterLogo = By.cssSelector(".copyright>a>img");
    private final By shareVersionWarning = By.cssSelector("#HEADER_SHARE_SERVICES_WARNING");

    protected SharePage2(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    /**
     * Wire up the relative path in your Page Object class
     * example:
     * {@code}
     * public String relativePath() {
     * return "share/page";
     * }
     * {@code}
     */
    public abstract String getRelativePath();

    /**
     * @return the relativePath URL object
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public URL relativePathToURL()
    {
        URI relativeURI = null;
        try
        {
            relativeURI = defaultProperties.getShareUrl().toURI().resolve(getRelativePath());
            return relativeURI.toURL();
        }
        catch (URISyntaxException | MalformedURLException me)
        {
            throw new RuntimeException("URI: " + relativeURI + " invalid url");
        }
    }

    /**
     * Navigate directly to relative path of the object based on {@link #getRelativePath()} constructed
     */
    public synchronized T navigate()
    {
        log.info("Navigate to {}", relativePathToURL().toString());
        try
        {
            getUrl(relativePathToURL().toString());
            waitForSharePageToLoad();
            return (T) this;
        }
        catch (TimeoutException | NoSuchSessionException e)
        {
            log.info("Navigation to {} failed. {}", getRelativePath(), e.getMessage());
            refresh();
            waitForSharePageToLoad();
            getUrl(relativePathToURL().toString());
            return (T) this;
        }
    }

    public void navigateWithoutRender()
    {
        log.info("Navigate without render {}", relativePathToURL().getPath());
        navigateTo(relativePathToURL().toString());
    }

    public T assertPageHeadersEqualsTo(String expectedHeader)
    {
        String actualHeader = getElementText(pageHeader);
        assertEquals(actualHeader, expectedHeader);

        return (T) this;
    }

    public AboutPopUpPage openAboutPage()
    {
        clickElement(alfrescoOneFooterLogo);
        return new AboutPopUpPage(webDriver);
    }

    public T assertLastNotificationMessageEquals(String expectedMessage)
    {
        log.info("Assert last notification message is: {}", expectedMessage);
        assertEquals(notificationMessageThread.get(), expectedMessage, "Last notification message is not correct");
        return (T) this;
    }

    public boolean isAlfrescoLogoDisplayed()
    {
        return isElementDisplayed(alfrescoOneFooterLogo);
    }

    public T assertAlfrescoLogoIsDisplayedInPageFooter()
    {
        assertTrue(isElementDisplayed(alfrescoOneFooterLogo), "Alfresco logo is displayed");
        return (T) this;
    }

    public T assertBrowserPageTitleIs(String expectedTitle)
    {
        assertTrue(new WebDriverWait(webDriver.get(), defaultProperties.getExplicitWait())
            .until(ExpectedConditions.titleIs(expectedTitle)), "Page title is correct");
        return (T) this;
    }

    public T assertShareVersionWarningIsNotDisplayed()
    {
        log.info("Assert Share Version warning is not displayed");
        assertFalse(isElementDisplayed(shareVersionWarning), "Share version warning is displayed");
        return (T) this;
    }

    /**
     * Set the relative path for a page that contains the user name in the url
     *
     * @param username String user name
     * @param pageUrl  String page url
     * @return String page url
     * exceptionMessage exception if user is null
     */
    public String setRelativePathForUserPage(String pageUrl, String username)
    {
        if (!StringUtils.isEmpty(username))
        {
            try
            {
                return UriUtils.encodePath(String.format(pageUrl, username), "UTF-8");
            }
            catch (Exception e)
            {
                throw new RuntimeException(String.format("Unable to set the path for user {}. {}", username, e.getMessage()));
            }
        }
        throw new RuntimeException(String.format("Set the user name to navigate to %s page", this.getClass().getSimpleName()));
    }

    /**
     * If alert is displayed, accept it
     */
    public void acceptAlertIfDisplayed()
    {
        try
        {
            Alert alert = switchTo().alert();
            log.info(alert.getText());
            alert.accept();
        }
        catch (NoAlertPresentException noAlertPresentException)
        {
            // do nothing as alert is not present
        }
    }

    public void waitForSharePageToLoad()
    {
        try
        {
            waitUntilElementIsVisible(alfrescoOneFooterLogo);
        }
        catch (TimeoutException e)
        {
            refresh();
            waitInSeconds(WAIT_5.getValue());
            waitUntilElementIsVisible(alfrescoOneFooterLogo);
        }
    }
}
