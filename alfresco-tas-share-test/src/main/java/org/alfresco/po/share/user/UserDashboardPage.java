package org.alfresco.po.share.user;

import org.alfresco.po.share.HideWelcomePanelDialog;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserDashboardPage extends SharePage<UserDashboardPage> implements AccessibleByMenuBar
{
    @FindBy (id = "HEADER_CUSTOMIZE_USER_DASHBOARD")
    private WebElement customizeUserDashboard;

    @Autowired
    private CustomizeUserDashboardPage customizeUserDashboardPage;

    @Autowired
    private HideWelcomePanelDialog hideWelcomePanelDialogue;

    @RenderWebElement
    @FindBy (id = "HEADER_HOME")
    private WebElement homeMenuLink;

    @FindBy (css = "div[class*='grid columnSize']")
    private WebElement dashboardLayout;

    @FindBy (xpath = "//img[@src='/share/res/components/images/alfresco-logo.svg']")
    private WebElement alfrescoLogo;

    @FindBy (xpath = "/share/res/components/images/alfresco-share-logo-enterprise.png']")
    private WebElement oldAlfrescoLogo;

    @RenderWebElement
    @FindBy (css = "div[id='HEADER_LOGO']")
    private WebElement alfrescoLogoContainer;

    @FindBy (css = "[id$=get-started-panel-container]")
    private WebElement welcomePanel;

    @FindBy (css = ".welcome-info")
    private WebElement welcomePanelInfo;

    private By welcomePanelHideButton = By.cssSelector("button[id$='_default-hide-button-button']");
    private By welcomePanelInfoGetStarted = By.cssSelector(".welcome-info h1");
    private String dashletOnDashboard = "//div[contains(text(),'%s')]/../../../div[contains(@id,'component-%d-%d')]";
    private String webViewDashletLocation = "//div[@class='webview-default']//span[contains(@id, 'component-%d-%d')][1]";

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/dashboard", getUserName());
    }

    @SuppressWarnings ("unchecked")
    @Override
    public UserDashboardPage navigateByMenuBar()
    {
        homeMenuLink.click();
        return (UserDashboardPage) renderedPage();
    }

    /**
     * Navigate to user dashboard for a specific user
     *
     * @param userName String user name
     * @return {@link UserDashboardPage}
     */
    public UserDashboardPage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }

    /**
     * Navigate to user dashboard for a specific user
     *
     * @param userModel String user name
     * @return {@link UserDashboardPage}
     */
    public UserDashboardPage navigate(UserModel userModel)
    {
        setUserName(userModel.getUsername());
        return navigate();
    }

    /**
     * Click customize user dashboard
     */
    public CustomizeUserDashboardPage clickCustomizeUserDashboard()
    {
        LOG.info("Click Customize user dashboard button");
        customizeUserDashboard.click();
        return (CustomizeUserDashboardPage) customizeUserDashboardPage.renderedPage();
    }

    /**
     * Verify if customize user dashboard button is displayed
     *
     * @return true if displayed
     */
    public boolean isCustomizeUserDashboardDisplayed()
    {
        return browser.isElementDisplayed(customizeUserDashboard);
    }

    public UserDashboardPage assertCustomizeUserDashboardIsDisplayed()
    {
        LOG.info("Assert Customize User Dashboard button is displayed");
        browser.waitUntilElementVisible(customizeUserDashboard);
        Assert.assertTrue(browser.isElementDisplayed(customizeUserDashboard),
            "Customize User Dashboard button is displayed");
        return this;
    }

    public UserDashboardPage assertNumberOfDashletColumnsIs(int columnsNumber)
    {
        LOG.info(String.format("Assert dashboard has %s columns", columnsNumber));
        String strCol = dashboardLayout.getAttribute("class");
        Assert.assertEquals(Character.getNumericValue(strCol.charAt(strCol.length() - 1)), columnsNumber);
        return this;
    }

    /**
     * Verify if a dashlet is located in the right column
     *
     * @param dashlet          Dashlets dashlet to verify
     * @param column           int column (must be > 0 <=4)
     * @param locationInColumn int location in column (must be > 0 <=5)
     * @return true if found
     */
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
            return browser.isElementDisplayed(By.xpath(String.format(webViewDashletLocation, column, locationInColumn)));
        }
        return browser.isElementDisplayed(By.xpath(String.format(dashletOnDashboard, dashlet.getDashletName(), column, locationInColumn)));
    }

    public UserDashboardPage assertDashletIsAddedInPosition(Dashlets dashlet, int column, int locationInColumn)
    {
        Assert.assertTrue(isDashletAddedInPosition(dashlet, column, locationInColumn));
        return this;
    }

    public WebElement getCustomizeUserDashboard()
    {
        return customizeUserDashboard;
    }

    /**
     * Verify if new alfresco logo is displayed on the page footer
     *
     * @return true if displayed, otherwise return false
     */
    public boolean isNewAlfrescoLogoDisplayed()
    {
        return browser.isElementDisplayed(alfrescoLogo);
    }

    /**
     * Verify if old alfresco logo is displayed on the page footer
     *
     * @return true if displayed, otherwise return false
     */
    public boolean isOldAlfrescoLogoDisplayed()
    {
        return browser.isElementDisplayed(oldAlfrescoLogo);
    }

    public UserDashboardPage assertUserDashboardPageIsOpened()
    {
        LOG.info("Assert User Dashboard page is opened");
        Assert.assertTrue(browser.isElementDisplayed(customizeUserDashboard), "User home page is not opened");
        return this;
    }

    public UserDashboardPage assertUserDashboardPageTitleIsCorrect()
    {
        LOG.info("Assert User Dashboard page title is correct");
        Assert.assertEquals(getPageTitle(), language.translate("userDashboard.PageTitle"), "User dashboard page title is correct");
        return this;
    }

    public UserDashboardPage assertPageHeaderIsCorrect(UserModel userModel)
    {
        LOG.info("Assert User Dashboard header title is correct");
        Assert.assertEquals(getPageHeader(), String.format(language.translate("userDashboard.headerTitle"),
            userModel.getFirstName(), userModel.getLastName()));
        return this;
    }

    public UserDashboardPage assertWelcomePanelIsDisplayed()
    {
        LOG.info("Assert Welcome panel is displayed");
        Assert.assertTrue(browser.isElementDisplayed(welcomePanel), "Welcome panel is displayed");
        return this;
    }

    public UserDashboardPage assertWelcomePanelIsNotDisplayed()
    {
        LOG.info("Assert Welcome panel is NOT displayed");
        Assert.assertFalse(browser.isElementDisplayed(welcomePanel), "Welcome panel is displayed");
        return this;
    }

    public UserDashboardPage assertWelcomePanelMessageIsCorrect()
    {
        LOG.info("Assert Welcome panel message is correct");
        Assert.assertEquals(welcomePanelInfo.getText(), language.translate("userDashboard.welcomeMessage"),
            "Welcome panel message is correct");
        return this;
    }

    public UserDashboardPage assertHideWelcomePanelButtonIsDisplayed()
    {
        LOG.info("Assert Hide welcome panel button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(welcomePanelHideButton), "Hide button is displayed");
        return this;
    }

    public UserDashboardPage assertAlfrescoDocumentationPageIsOpenedFromWelcomePanel()
    {
        LOG.info("Assert Alfresco Documentation page is opened from welcome panel");
        browser.waitUntilElementClickable(welcomePanelInfoGetStarted).click();
        getBrowser().waitUrlContains("https://docs.alfresco.com/", 5);
        Assert.assertTrue(getBrowser().getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");
        getBrowser().navigate().back();
        renderedPage();
        return this;
    }

    public HideWelcomePanelDialog clickHideWelcomePanel()
    {
        LOG.info("Click Hide Welcome Panel");
        browser.waitUntilElementClickable(welcomePanelHideButton).click();
        return (HideWelcomePanelDialog) hideWelcomePanelDialogue.renderedPage();
    }
}
