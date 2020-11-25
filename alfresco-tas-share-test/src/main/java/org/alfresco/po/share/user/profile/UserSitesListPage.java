package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

/**
 * @author bogdan.bocancea
 */
public class UserSitesListPage extends SharePage2<UserSitesListPage> implements AccessibleByMenuBar
{
    private String userName;

    @RenderWebElement
    private By headerBar = By.cssSelector(".header-bar");
    private By sitesMenuLink = By.id("HEADER_SITES_MENU");
    private By mySitesLink = By.id("HEADER_SITES_MENU_MY_SITES");
    private By noSitesMessage = By.cssSelector("div.viewcolumn p");
    private By siteRows = By.cssSelector(".sites > li");
    private By siteNameLocator = By.cssSelector("p a");

    public UserSitesListPage(ThreadLocal<WebBrowser> browser)
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

    @Override
    public UserSitesListPage navigateByMenuBar()
    {
        getBrowser().findElement(sitesMenuLink).click();
        getBrowser().waitUntilElementVisible(mySitesLink).click();
        return (UserSitesListPage) renderedPage();
    }

    private WebElement getSiteRow(SiteModel site)
    {
        List<WebElement> sites = getBrowser().waitUntilElementsVisible(siteRows);
        return sites.stream().filter(siteRow -> siteRow.findElement(siteNameLocator)
            .getAttribute("href").contains(site.getId())).findFirst().orElse(null);
    }

    public UserSitesListPage assertSiteIsDisplayed(SiteModel site)
    {
        LOG.info(String.format("Assert site %s is displayed", site.getId()));
        assertTrue(getBrowser().isElementDisplayed(getSiteRow(site)), String.format("Site %s is displayed", site.getId()));
        return this;
    }

    public UserSitesListPage assertSiteIsNotDisplayed(SiteModel site)
    {
        LOG.info(String.format("Assert site %s is NOT displayed", site.getId()));
        assertNull(getSiteRow(site), String.format("Site %s is displayed", site.getId()));
        return this;
    }

    public SiteDashboardPage clickSite(SiteModel site)
    {
        LOG.info(String.format("Click site %s", site.getId()));
        getSiteRow(site).findElement(siteNameLocator).click();
        return (SiteDashboardPage) new SiteDashboardPage(browser).renderedPage();
    }

    public UserSitesListPage assertUserHasNoSitesMessageIsDisplayed()
    {
        LOG.info("Assert User is not a member of any sites");
        assertEquals(getBrowser().waitUntilElementVisible(noSitesMessage).getText(), language.translate("user.sitesList.noSitesMessage"),
            "User is not a member of any sites is displayed");
        return this;
    }
}
