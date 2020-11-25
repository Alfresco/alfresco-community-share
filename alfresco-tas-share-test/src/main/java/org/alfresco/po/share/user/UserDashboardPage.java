package org.alfresco.po.share.user;

import org.alfresco.po.share.HideWelcomePanelDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.testng.Assert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author bogdan.bocancea
 */
public class UserDashboardPage extends SharePage2<UserDashboardPage> implements AccessibleByMenuBar
{
    private String userName;

    @RenderWebElement
    private By homeMenuLink = By.id("HEADER_HOME");
    @RenderWebElement
    private By customizeUserDashboard = By.id("HEADER_CUSTOMIZE_USER_DASHBOARD");
    private By dashboardLayout = By.cssSelector("div[class*='grid columnSize']");
    private By alfrescoLogo = By.xpath("//img[@src='/share/res/components/images/alfresco-logo.svg']");
    private By welcomePanel = By.cssSelector("[id$=get-started-panel-container]");
    private By welcomePanelInfo = By.cssSelector( ".welcome-info");
    private By welcomePanelHideButton = By.cssSelector("button[id$='_default-hide-button-button']");
    private By welcomePanelInfoGetStarted = By.cssSelector(".welcome-info h1");
    private String dashletOnDashboard = "//div[contains(text(),'%s')]/../../../div[contains(@id,'component-%d-%d')]";
    private String webViewDashletLocation = "//div[@class='webview-default']//span[contains(@id, 'component-%d-%d')][1]";

    private By okButtonHidePanel = By.cssSelector("span[class$='alf-primary-button']>span>button");
    private By panelText = By.cssSelector(".alf-confirmation-panel-text");
    private By panelTextSettingsIcon = By.cssSelector(" #prompt p");
    private By configIcon = By.cssSelector( " #prompt .alf-configure-icon");
    private By startedPanelContainer = By.cssSelector("[id$=get-started-panel-container]");

    public UserDashboardPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/dashboard", getUserName());
    }
    @SuppressWarnings ("unchecked")
    @Override
    public UserDashboardPage navigateByMenuBar()
    {
        getBrowser().findElement(homeMenuLink).click();
        return (UserDashboardPage) renderedPage();
    }

    public UserDashboardPage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }

    public UserDashboardPage navigate(UserModel userModel)
    {
        return navigate(userModel.getUsername());
    }

    public CustomizeUserDashboardPage clickCustomizeUserDashboard()
    {
        LOG.info("Click Customize user dashboard button");
        getBrowser().findElement(customizeUserDashboard).click();
        return (CustomizeUserDashboardPage) new CustomizeUserDashboardPage(browser).renderedPage();
    }

    public boolean isCustomizeUserDashboardDisplayed()
    {
        return getBrowser().isElementDisplayed(customizeUserDashboard);
    }

    public UserDashboardPage assertCustomizeUserDashboardIsDisplayed()
    {
        LOG.info("Assert Customize User Dashboard button is displayed");
        getBrowser().waitUntilElementVisible(customizeUserDashboard);
        assertTrue(getBrowser().isElementDisplayed(customizeUserDashboard), "Customize User Dashboard button is displayed");
        return this;
    }

    public UserDashboardPage assertNumberOfDashletColumnsIs(int columnsNumber)
    {
        LOG.info("Assert dashboard has {} columns", columnsNumber);
        String strCol = getBrowser().findElement(dashboardLayout).getAttribute("class");
        assertEquals(Character.getNumericValue(strCol.charAt(strCol.length() - 1)), columnsNumber);
        return this;
    }

    public boolean isDashletAddedInPosition(final Dashlets dashlet, final int column, final int locationInColumn)
    {
        if (column < 1 || column > 4)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }
        if (locationInColumn < 1 || locationInColumn > 5)
        {
            throw new IllegalArgumentException("Location in column should be between 1 and 5");
        }
        if (dashlet.equals(Dashlets.WEB_VIEW))
        {
            return getBrowser().isElementDisplayed(By.xpath(String.format(webViewDashletLocation, column, locationInColumn)));
        }
        return getBrowser().isElementDisplayed(By.xpath(String.format(dashletOnDashboard, dashlet.getDashletName(), column, locationInColumn)));
    }

    public UserDashboardPage assertDashletIsAddedInPosition(Dashlets dashlet, int column, int locationInColumn)
    {
        assertTrue(isDashletAddedInPosition(dashlet, column, locationInColumn));
        return this;
    }

    public boolean isNewAlfrescoLogoDisplayed()
    {
        return getBrowser().isElementDisplayed(alfrescoLogo);
    }

    public UserDashboardPage assertUserDashboardPageIsOpened()
    {
        LOG.info("Assert User Dashboard page is opened");
        assertTrue(getBrowser().isElementDisplayed(customizeUserDashboard), "User home page is not opened");
        return this;
    }

    public UserDashboardPage assertUserDashboardPageTitleIsCorrect()
    {
        LOG.info("Assert User Dashboard page title is correct");
        assertEquals(getPageTitle(), language.translate("userDashboard.PageTitle"), "User dashboard page title is correct");
        return this;
    }

    public UserDashboardPage assertPageHeaderIsCorrect(UserModel userModel)
    {
        LOG.info("Assert User Dashboard header title is correct");
        assertEquals(getPageHeader(), String.format(language.translate("userDashboard.headerTitle"),
            userModel.getFirstName(), userModel.getLastName()));
        return this;
    }

    public UserDashboardPage assertWelcomePanelIsDisplayed()
    {
        LOG.info("Assert Welcome panel is displayed");
        assertTrue(getBrowser().isElementDisplayed(welcomePanel), "Welcome panel is displayed");
        return this;
    }

    public UserDashboardPage assertWelcomePanelIsNotDisplayed()
    {
        LOG.info("Assert Welcome panel is NOT displayed");
        Assert.assertFalse(getBrowser().isElementDisplayed(welcomePanel), "Welcome panel is displayed");
        return this;
    }

    public UserDashboardPage assertWelcomePanelMessageIsCorrect()
    {
        LOG.info("Assert Welcome panel message is correct");
        assertEquals(getBrowser().waitUntilElementVisible(welcomePanelInfo).getText(), language.translate("userDashboard.welcomeMessage"),
            "Welcome panel message is correct");
        return this;
    }

    public UserDashboardPage assertHideWelcomePanelButtonIsDisplayed()
    {
        LOG.info("Assert Hide welcome panel button is displayed");
        assertTrue(getBrowser().isElementDisplayed(welcomePanelHideButton), "Hide button is displayed");
        return this;
    }

    public UserDashboardPage assertAlfrescoDocumentationPageIsOpenedFromWelcomePanel()
    {
        LOG.info("Assert Alfresco Documentation page is opened from welcome panel");
        getBrowser().waitUntilElementClickable(welcomePanelInfoGetStarted).click();
        getBrowser().waitUrlContains("https://docs.alfresco.com/", 5);
        assertTrue(getBrowser().getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");
        getBrowser().navigate().back();
        renderedPage();

        return this;
    }

    public HideWelcomePanelDialog clickHideWelcomePanel()
    {
        LOG.info("Click Hide Welcome Panel");
        getBrowser().waitUntilElementClickable(welcomePanelHideButton).click();
        return (HideWelcomePanelDialog) new HideWelcomePanelDialog(browser).renderedPage();
    }
}
