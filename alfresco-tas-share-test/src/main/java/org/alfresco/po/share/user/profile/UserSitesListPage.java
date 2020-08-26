package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.List;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserSitesListPage extends SharePage<UserSitesListPage> implements AccessibleByMenuBar
{
    @RenderWebElement
    @FindBy (css = ".header-bar")
    private WebElement headerBar;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @FindBy (id = "HEADER_SITES_MENU")
    private WebElement sitesMenuLink;

    @FindBy (id = "HEADER_SITES_MENU_MY_SITES")
    private WebElement mySitesLink;

    @FindBy (css = "div.viewcolumn p")
    private WebElement noSitesMessage;

    @FindAll (@FindBy (css = ".sites > li"))
    private List<WebElement> siteRows;

    private By siteNameLocator = By.cssSelector("p a");

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/user-sites", getUserName());
    }

    public UserSitesListPage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }

    public UserSitesListPage navigate(UserModel user)
    {
        return navigate(user.getUsername());
    }

    @SuppressWarnings ("unchecked")
    @Override
    public UserSitesListPage navigateByMenuBar()
    {
        sitesMenuLink.click();
        mySitesLink.click();
        return (UserSitesListPage) renderedPage();
    }

    private WebElement getSiteRow(SiteModel site)
    {
        browser.waitUntilElementsVisible(siteRows);
        return siteRows.stream().filter(siteRow -> siteRow.findElement(siteNameLocator)
            .getAttribute("href").contains(site.getId())).findFirst().orElse(null);
    }

    public UserSitesListPage assertSiteIsDisplayed(SiteModel site)
    {
        LOG.info(String.format("Assert site %s is displayed", site.getId()));
        Assert.assertTrue(browser.isElementDisplayed(getSiteRow(site)), String.format("Site %s is displayed", site.getId()));
        return this;
    }

    public UserSitesListPage assertSiteIsNotDisplayed(SiteModel site)
    {
        LOG.info(String.format("Assert site %s is NOT displayed", site.getId()));
        Assert.assertNull(getSiteRow(site), String.format("Site %s is displayed", site.getId()));
        return this;
    }

    /**
     * Open site dashboard
     *
     * @param {@link SiteModel} site
     * @return
     */
    public SiteDashboardPage clickSite(SiteModel site)
    {
        LOG.info(String.format("Click site %s", site.getId()));
        getSiteRow(site).findElement(siteNameLocator).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public UserSitesListPage assertUserHasNoSitesMessageIsDisplayed()
    {
        LOG.info("Assert User is not a member of any sites");
        Assert.assertEquals(browser.waitUntilElementVisible(noSitesMessage).getText(), language.translate("user.sitesList.noSitesMessage"),
            "User is not a member of any sites is displayed");
        return this;
    }
}
