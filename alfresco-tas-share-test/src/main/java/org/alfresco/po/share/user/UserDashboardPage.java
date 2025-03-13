package org.alfresco.po.share.user;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.HideWelcomePanelDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * @author bogdan.bocancea
 */
@Slf4j
public class UserDashboardPage extends SharePage2<UserDashboardPage> implements AccessibleByMenuBar
{
    private String userName;

    private final By homeMenuLink = By.cssSelector("span[id='HEADER_HOME_text']");
    private final By customizeUserDashboard = By.id("HEADER_CUSTOMIZE_USER_DASHBOARD");
    private final By dashboardLayout = By.cssSelector("div[class*='grid columnSize']");
    private final By alfrescoLogo = By.xpath("//img[@src='/share/res/components/images/alfresco-logo.svg']");
    private final By welcomePanel = By.cssSelector("[id$=get-started-panel-container]");
    private final By welcomePanelInfo = By.cssSelector( ".welcome-info");
    private final By welcomePanelHideButton = By.cssSelector("button[id$='_default-hide-button-button']");
    private final By welcomePanelInfoGetStarted = By.cssSelector(".welcome-info h1");
    private final By selectDashlet = By.xpath("//li[@class=\"usedDashlet dnd-draggable\"]");
    private final String dashletOnDashboard = "//div[contains(text(),'%s')]/../../../div[contains(@id,'component-%d-%d')]";
    private final String webViewDashletLocation = "//div[@class='webview-default']//span[contains(@id, 'component-%d-%d')][1]";

    public UserDashboardPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
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
        clickElement(homeMenuLink);
        return new UserDashboardPage(webDriver);
    }

    public synchronized UserDashboardPage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }

    public synchronized UserDashboardPage navigate(UserModel userModel)
    {
        return navigate(userModel.getUsername());
    }

    public void navigateWithoutRender(UserModel userModel)
    {
        setUserName(userModel.getUsername());
        super.navigateWithoutRender();
    }

    public CustomizeUserDashboardPage clickCustomizeUserDashboard()
    {
        log.info("Click Customize user dashboard button");
        clickElement(customizeUserDashboard);
        return new CustomizeUserDashboardPage(webDriver);
    }

    public boolean isCustomizeUserDashboardDisplayed()
    {
        return isElementDisplayed(customizeUserDashboard);
    }

    public UserDashboardPage assertCustomizeUserDashboardIsDisplayed()
    {
        log.info("Assert Customize User Dashboard button is displayed");
        waitUntilElementIsVisible(customizeUserDashboard);
        assertTrue(isElementDisplayed(customizeUserDashboard), "Customize User Dashboard button is displayed");
        return this;
    }
    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public UserDashboardPage assertNumberOfDashletColumnsIs(int columnsNumber)
    {
        log.info("Assert dashboard has {} columns", columnsNumber);
        String strCol = waitUntilElementIsVisible(dashboardLayout).getAttribute("class");
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
            WebElement webView = waitUntilElementIsVisible(
                By.xpath(String.format(webViewDashletLocation, column, locationInColumn)));
            return isElementDisplayed(webView);
        }
        WebElement dashletToCheck = waitUntilElementIsVisible(
            By.xpath(String.format(dashletOnDashboard, dashlet.getDashletName(), column, locationInColumn)));
        return isElementDisplayed(dashletToCheck);
    }
    public boolean isDashletAvailable(String option)
    {
        for (WebElement dashlets : findElements(selectDashlet))
        {
            if (dashlets.getText().contains(option))
            {
                waitInSeconds(3);
               return true;
    }
        }
        return false;
    }
    public UserDashboardPage assertDashletIsAddedInPosition(Dashlets dashlet, int column, int locationInColumn)
    {
        assertTrue(isDashletAddedInPosition(dashlet, column, locationInColumn));
        return this;
    }

    public boolean isNewAlfrescoLogoDisplayed()
    {
        return isElementDisplayed(alfrescoLogo);
    }

    public UserDashboardPage assertUserDashboardPageIsOpened()
    {
        log.info("Assert User Dashboard page is opened");
        waitUntilElementIsVisible(customizeUserDashboard);
        assertTrue(isElementDisplayed(customizeUserDashboard), "User home page is not opened");
        return this;
    }

    public UserDashboardPage assertUserDashboardPageTitleIsCorrect()
    {
        log.info("Assert User Dashboard page title is correct");
        assertEquals(getPageTitle(), language.translate("userDashboard.PageTitle"), "User dashboard page title is correct");
        return this;
    }

    public UserDashboardPage assertPageHeaderIsCorrect(UserModel userModel)
    {
        log.info("Assert User Dashboard header title is correct");
        assertPageHeadersEqualsTo(String
            .format(language.translate("userDashboard.headerTitle"), userModel.getFirstName(),
                userModel.getLastName()));

        return this;
    }

    public UserDashboardPage assertWelcomePanelIsDisplayed()
    {
        log.info("Assert Welcome panel is displayed");
        waitUntilElementIsVisible(welcomePanel);
        assertTrue(isElementDisplayed(welcomePanel), "Welcome panel is displayed");
        return this;
    }

    public UserDashboardPage assertWelcomePanelIsNotDisplayed()
    {
        log.info("Assert Welcome panel is NOT displayed");
        Assert.assertFalse(isElementDisplayed(welcomePanel), "Welcome panel is displayed");
        return this;
    }

    public UserDashboardPage assertWelcomePanelMessageIsCorrect()
    {
        log.info("Assert Welcome panel message is correct");
        assertEquals(waitUntilElementIsVisible(welcomePanelInfo).getText(), language.translate("userDashboard.welcomeMessage"),
            "Welcome panel message is correct");
        assertEquals(getElementText(welcomePanelInfo), language.translate("userDashboard.welcomeMessage"),
                "Welcome panel message is correct");
        return this;
    }

    public UserDashboardPage assertHideWelcomePanelButtonIsDisplayed()
    {
        log.info("Assert Hide welcome panel button is displayed");
        waitUntilElementIsVisible(welcomePanelHideButton);
        assertTrue(isElementDisplayed(welcomePanelHideButton), "Hide button is displayed");
        return this;
    }

    public UserDashboardPage assertAlfrescoDocumentationPageIsOpenedFromWelcomePanel()
    {
        log.info("Assert Alfresco Documentation page is opened from welcome panel");
        clickElement(welcomePanelInfoGetStarted);
        waitUrlContains("https://support.hyland.com/p/alfresco", WAIT_10.getValue());
        assertTrue(getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");
        navigateBack();

        return this;
    }

    public HideWelcomePanelDialog clickHideWelcomePanel()
    {
        log.info("Click Hide Welcome Panel");
        clickElement(welcomePanelHideButton);
        return new HideWelcomePanelDialog(webDriver);
    }
}