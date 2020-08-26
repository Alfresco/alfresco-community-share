package org.alfresco.po.share;

import static org.alfresco.utility.report.log.Step.STEP;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.j2objc.annotations.AutoreleasePool;
import org.alfresco.common.Language;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.renderer.ElementState;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Image;
import ru.yandex.qatools.htmlelements.element.TextBlock;

/**
 * handle common cases related to a share page
 *
 * @author Paul.Brodner
 */
public abstract class SharePage<T> extends HtmlPage
{
    public static final int WAIT_1 = 1;
    public static final int WAIT_5 = 5;
    public static final int WAIT_10 = 10;
    public static final int WAIT_15 = 15;
    public static final int WAIT_30 = 30;
    public static final int DEFAULT_RETRY = 3;
    public static final By MESSAGE_LOCATOR = By.cssSelector("div.bd span.message");

    public String userName;

    @Autowired
    AboutPopUpPage pop;

    @Autowired
    public Language language;

    @Autowired
    public Environment env;

    @Autowired
    public Toolbar toolbar;

    @RenderWebElement (state = ElementState.PAGE_LOADED)
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

    private By loadingMessage = By.cssSelector("div[class$='alfresco-lists-AlfList--loading']");
    public static String LAST_MODIFICATION_MESSAGE = "";

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
     * Click Alfresco One footer logo and open {@link AboutPopUpPage .class}
     */
    public AboutPopUpPage openAboutPage()
    {
        alfrescoOneFooterLogo.click();
        return (AboutPopUpPage) pop.renderedPage();
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

    /**
     * Verify if alfresco logo is displayed on the page footer
     *
     * @return true if displayed
     */
    public boolean isAlfrescoLogoDisplayed()
    {
        return browser.isElementDisplayed(alfrescoOneFooterLogo);
    }

    public T assertAlfrescoLogoIsDisplayedInPageFooter()
    {
        Assert.assertTrue(browser.isElementDisplayed(alfrescoOneFooterLogo), "Alfresco logo is displayed");
        return (T) renderedPage();
    }

    public T assertBrowserPageTitleIs(String expectedTitle)
    {
        Assert.assertEquals(getPageTitle(), expectedTitle, "Page title is correct");
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
        } else
        {
            throw new RuntimeException(String.format("Set the user name to navigate to %s page", this.getClass().getSimpleName()));
        }
    }

    public void waitUntilMessageDisappears()
    {
        try
        {
            getBrowser().waitUntilElementVisible(MESSAGE_LOCATOR, 5);
            getBrowser().waitUntilElementDisappears(MESSAGE_LOCATOR);
        }
        catch (TimeoutException exception)
        {
            // do nothing and carry on as this might be expected, meaning that the element might be expected to already disappear
        }
    }

    public String getLastNotificationMessage()
    {
        LAST_MODIFICATION_MESSAGE = browser.waitUntilElementVisible(MESSAGE_LOCATOR, 5).getText();
        getBrowser().waitUntilElementDisappears(MESSAGE_LOCATOR);
        return LAST_MODIFICATION_MESSAGE;
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

    public void clearAndType(WebElement webElement, String value)
    {
        webElement.clear();
        webElement.sendKeys(value);
    }

    public T waitForLoadingMessageToDisappear()
    {
        try
        {
            browser.waitUntilElementVisible(loadingMessage,5);
            browser.waitUntilElementDisappears(loadingMessage);
        }
        catch (TimeoutException e)
        {
            //continue
        }
        return (T) renderedPage();
    }

    public void waitForSharePageToLoad()
    {
        browser.waitUntilElementVisible(alfrescoOneFooterLogo, 60);
    }
}
