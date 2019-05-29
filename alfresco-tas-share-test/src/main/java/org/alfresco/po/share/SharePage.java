package org.alfresco.po.share;

import static org.alfresco.utility.report.log.Step.STEP;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.renderer.ElementState;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Image;
import ru.yandex.qatools.htmlelements.element.TextBlock;

/**
 * handle common cases related to a share page
 *
 * @author Paul.Brodner
 */
public abstract class SharePage<T> extends HtmlPage
{
    public String userName;
    @Autowired
    AboutPopUpPage pop;
    @RenderWebElement (state = ElementState.PAGE_LOADED)
    @FindBy (id = "Share")
    private WebElement body;
    @RenderWebElement (state = ElementState.PRESENT)
    @FindBy (id = "HEADER_TITLE")
    private TextBlock pageHeader;
    @FindBy (id = "HEADER_LOGO")
    private Image headerLogo;
    @FindBy (css = ".copyright>a>img")
    private WebElement alfrescoOneFooterLogo;
    @FindBy (id = "HEADER_USER_MENU_POPUP")
    private WebElement userMenu;
    @FindBy (id = "HEADER_USER_MENU_LOGOUT")
    private WebElement logoutLink;

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
        } catch (URISyntaxException | MalformedURLException me)
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
        getBrowser().waitInSeconds(15);
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
     * Click Alfresco One footer logo and open {@link AboutPopUpPage.class}
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

    /**
     * Set the relative path for a page that contains the user name in the url
     *
     * @param username         String user name
     * @param url              String page url
     * @param exceptionMessage exception if user is null
     * @return String page url
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

    /**
     * Get background color of element or color of element (font color)
     *
     * @param locator    By
     * @param background if needed to find color of element's background - param must be true
     *                   if needed to find color of element itself - param must be false
     * @return hex
     * return color in Hex color model
     */
    public String getColor(WebElement element, boolean background)
    {
        String hex = "";
        String color;
        try
        {
            if (background)
            {
                color = element.getCssValue("background-color");
            } else
            {
                color = element.getCssValue("color");
            }

            String[] numbers = color.replace("rgba(", "").replace(")", "").split(",");
            int number1 = Integer.parseInt(numbers[0]);
            numbers[1] = numbers[1].trim();
            int number2 = Integer.parseInt(numbers[1]);
            numbers[2] = numbers[2].trim();
            int number3 = Integer.parseInt(numbers[2]);
            hex = String.format("#%02x%02x%02x", number1, number2, number3);
        } catch (TimeoutException e)
        {
            LOG.error("Exceeded time to find " + element);

        }
        return hex;
    }
}
