package org.alfresco.po.share.user;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Link;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserDashboardPage extends SharePage<UserDashboardPage> implements AccessibleByMenuBar
{
    @FindBy (id = "HEADER_CUSTOMIZE_USER_DASHBOARD")
    public WebElement customizeUserDashboard;
    @Autowired
    CustomizeUserDashboardPage customizeUserDashboardPage;
    @FindBy (css = "div[id$='get-started-panel-container']")
    private WebElement getStartedPanel;

    @RenderWebElement
    @FindBy (id = "HEADER_HOME")
    private Link homeMenuLink;

    @FindBy (css = "div[class*='grid columnSize']")
    private WebElement dashboardLayout;

    @FindBy (xpath = "//img[@src='/share/res/components/images/alfresco-logo.svg']")
    private WebElement alfrescoLogo;

    @FindBy (xpath = "/share/res/components/images/alfresco-share-logo-enterprise.png']")
    private WebElement oldAlfrescoLogo;

    @RenderWebElement
    @FindBy (css = "div[id='HEADER_LOGO']")
    private WebElement alfrescoLogoContainer;

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
     * Navigate to user dashbord for a specific user
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
     * Click customize user dashboard
     */
    public void clickCustomizeUserDashboard()
    {
        customizeUserDashboard.click();
    }

    /**
     * Verify if get started panel is displayed on user dashsboard page
     *
     * @return true if displayed
     */
    public boolean isGetStartedPanelDisplayed()
    {
        return browser.isElementDisplayed(getStartedPanel);
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

    /**
     * Get the number of columns from User Dashboard page
     *
     * @return number of columns
     */
    public int getNumerOfColumns()
    {
        String strCol = dashboardLayout.getAttribute("class");
        return Character.getNumericValue(strCol.charAt(strCol.length() - 1));
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
            return browser.isElementDisplayed(By.xpath(String.format("//div[@class='title']/span[contains(@id, 'component-%d-%d')][1]", column,
                    locationInColumn)));
        }
        String dashletLocation = String.format("//div[contains(text(),'%s')]/../../../div[contains(@id,'component-%d-%d')]", dashlet.getDashletName(), column,
                locationInColumn);
        return browser.isElementDisplayed(By.xpath(dashletLocation));
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

    public boolean isCreateSiteDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(By.id("CREATE_SITE_DIALOG"));
    }
}
