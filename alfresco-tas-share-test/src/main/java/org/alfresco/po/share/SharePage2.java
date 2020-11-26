package org.alfresco.po.share;

import org.alfresco.utility.exception.PageRenderTimeException;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.alfresco.utility.report.log.Step.STEP;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

public abstract class SharePage2<T> extends BasePage
{
    private final By loadingMessage = By.cssSelector("div[class$='alfresco-lists-AlfList--loading']");
    @RenderWebElement
    private By body= By.id("Share");
    @RenderWebElement
    private final By pageHeader = By.id("HEADER_TITLE");
    private final By alfrescoOneFooterLogo = By.cssSelector(".copyright>a>img");
    private final By shareVersionWarning = By.id("HEADER_SHARE_SERVICES_WARNING");

    public SharePage2(ThreadLocal<WebBrowser> browser) {
        super(browser);
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
            relativeURI = properties.getShareUrl().toURI().resolve(getRelativePath());
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
    public T navigate()
    {
        STEP(String.format("Navigate to: %s", relativePathToURL().getPath()));
        getBrowser().navigate().to(relativePathToURL());
        try
        {
            getBrowser().waitUntilElementVisible(body, WAIT_60);
            return (T) renderedPage();
        }
        catch (PageRenderTimeException | TimeoutException e)
        {
            LOG.error("Navigation to {} failed. Error: {}", getRelativePath(), e.getMessage());
            getBrowser().navigate().to(relativePathToURL());
            return (T) renderedPage();
        }
    }

    public void navigateWithoutRender()
    {
        STEP(String.format("Navigate to: %s", relativePathToURL().getPath()));
        getBrowser().navigate().to(relativePathToURL());
    }

    public String getPageHeader()
    {
        return getBrowser().findElement(pageHeader).getText();
    }

    public AboutPopUpPage openAboutPage()
    {
        getBrowser().findElement(alfrescoOneFooterLogo).click();
        return (AboutPopUpPage) new AboutPopUpPage(browser).renderedPage();
    }

    public String getCurrentUrl()
    {
        return getBrowser().getCurrentUrl();
    }

    public T assertLastNotificationMessageEquals(String expectedMessage)
    {
        LOG.info("Assert last notification message is: {}", expectedMessage);
        assertEquals(notificationMessageThread.get(), expectedMessage, "Last notification message is not correct");
        return (T) renderedPage();
    }

    public T waiUntilLoadingMessageDisappears()
    {
        LOG.info("Wait for loading message to disappear");
        try
        {
            getBrowser().waitUntilElementVisible(loadingMessage,2);
            getBrowser().waitUntilElementDisappears(loadingMessage);
        }
        catch (TimeoutException e)
        {
            LOG.info("Timeout exception for loading message {}", e.getMessage());
        }
        return (T) this;
    }

    public boolean isAlfrescoLogoDisplayed()
    {
        return getBrowser().isElementDisplayed(alfrescoOneFooterLogo);
    }

    public T assertAlfrescoLogoIsDisplayedInPageFooter()
    {
        assertTrue(getBrowser().isElementDisplayed(alfrescoOneFooterLogo), "Alfresco logo is displayed");
        return (T) renderedPage();
    }

    public T assertBrowserPageTitleIs(String expectedTitle)
    {
        assertEquals(getPageTitle(), expectedTitle, "Page title is correct");
        return (T) renderedPage();
    }

    public T assertShareVersionWarningIsNotDisplayed()
    {
        LOG.info("Assert Share Version warning is not displayed");
        assertFalse(getBrowser().isElementDisplayed(shareVersionWarning), "Share version warning is displayed");
        return (T) renderedPage();
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
            return String.format(pageUrl, username);
        }
        else
        {
            throw new RuntimeException(String.format("Set the user name to navigate to %s page", this.getClass().getSimpleName()));
        }
    }

    /**
     * If alert is displayed, accept it
     */
    public void acceptAlertIfDisplayed()
    {
        try
        {
            Alert alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
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
            getBrowser().waitUntilElementVisible(alfrescoOneFooterLogo, 60);
        }
        catch (TimeoutException e)
        {
            getBrowser().refresh();
            getBrowser().waitUntilElementVisible(alfrescoOneFooterLogo, 20);
        }
    }
}
