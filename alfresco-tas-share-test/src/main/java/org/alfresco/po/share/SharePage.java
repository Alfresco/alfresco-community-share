package org.alfresco.po.share;

import static org.alfresco.utility.report.log.Step.STEP;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.renderer.ElementState;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

/**
 * handle common cases related to a share page
 *
 * @author Paul.Brodner
 */
public abstract class SharePage<T> extends SharePageObject
{
    private final By loadingMessage = By.cssSelector("div[class$='alfresco-lists-AlfList--loading']");
    public String userName;

    private AboutPopUpPage pop;
    public Toolbar toolbar;

    @FindBy (id = "Share")
    private WebElement body;

    @RenderWebElement (state = ElementState.PRESENT)
    @FindBy (id = "HEADER_TITLE")
    private WebElement pageHeader;

    @FindBy (id = "HEADER_LOGO")
    private WebElement headerLogo;

    @FindBy (css = ".copyright>a>img")
    private WebElement alfrescoOneFooterLogo;

    @FindBy (id = "HEADER_USER_MENU_POPUP")
    private WebElement userMenu;

    @FindBy (id = "HEADER_USER_MENU_LOGOUT")
    private WebElement logoutLink;

    private By shareVersionWarning = By.id("HEADER_SHARE_SERVICES_WARNING");

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
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
    @SuppressWarnings ("unchecked")
    public T navigate()
    {
        STEP(String.format("Navigate to: %s", relativePathToURL().getPath()));
        browser.navigate().to(relativePathToURL());
        return (T) renderedPage();
    }

    public void navigateWithoutRender()
    {
        STEP(String.format("Navigate to: %s", relativePathToURL().getPath()));
        browser.navigate().to(relativePathToURL());
    }

    public String getPageHeader()
    {
        return pageHeader.getText();
    }

    /**
     * Get the current url
     *
     * @return String url
     */
    public String getCurrentUrl()
    {
        return browser.getCurrentUrl();
    }

    public T waiUntilLoadingMessageDisappears()
    {
        LOG.info("Wait for loading message to disappear");
        try
        {
            browser.waitUntilElementVisible(loadingMessage,2);
            browser.waitUntilElementDisappears(loadingMessage);
        }
        catch (TimeoutException e)
        {
            LOG.info("Timeout exception for loading message {}", e.getMessage());
        }
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
            Alert alert = browser.switchTo().alert();
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
            browser.waitUntilElementVisible(alfrescoOneFooterLogo, 60);
        }
        catch (TimeoutException e)
        {
            browser.refresh();
            browser.waitUntilElementVisible(alfrescoOneFooterLogo, 20);
        }
    }
}
