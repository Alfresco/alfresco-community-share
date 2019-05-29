package org.alfresco.po.share.user.profile;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.Table;
import ru.yandex.qatools.htmlelements.element.TextBlock;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserSitesListPage extends SharePage<UserSitesListPage> implements AccessibleByMenuBar
{
    @FindAll (@FindBy (css = "p a"))
    protected List<WebElement> sitesLinksList;
    @Autowired
    SiteDashboardPage siteDashboardPage;
    @FindBy (id = "HEADER_SITES_MENU")
    private Link sitesMenuLink;
    @FindBy (id = "HEADER_SITES_MENU_MY_SITES")
    private Link mySitesLink;
    @FindBy (css = "div.viewcolumn p")
    private TextBlock noSitesMessage;
    @FindBy (css = "ul[id$='default-sites'] li")
    private Table sitesList;

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/user-sites", getUserName());
    }

    public UserSitesListPage navigate(String userName)
    {
        setUserName(userName);
        return (UserSitesListPage) navigate();
    }

    @SuppressWarnings ("unchecked")
    @Override
    public UserSitesListPage navigateByMenuBar()
    {
        sitesMenuLink.click();
        mySitesLink.click();
        return (UserSitesListPage) renderedPage();
    }

    /**
     * Open User Sites list page from the my profile navigation links
     *
     * @param myProfileNavigation
     * @return {@link UserSitesListPage}
     */
    public UserSitesListPage openFromNavigationLink(MyProfileNavigation myProfileNavigation)
    {
        myProfileNavigation.clickInfo();
        return (UserSitesListPage) this.renderedPage();
    }

    /**
     * Get list of sites links displayed in User Profile --> Sites
     */
    public List<WebElement> getSitesLinks()
    {
        return sitesLinksList;
    }

    /**
     * Retrieves the link that match the site name.
     *
     * @param name identifier
     * @return {@link Link} that matches siteName
     */
    public WebElement getSite(final String siteName)
    {
        return browser.findFirstElementWithValue(sitesLinksList, siteName);
    }

    /**
     * Verify if a site name is displayed in User Profile --> Sites
     *
     * @param siteName
     * @return True if Site exists
     */
    public boolean isSitePresent(String siteName)
    {
        return browser.isElementDisplayed(getSite(siteName));
    }

    /**
     * Open site dashboard
     *
     * @param siteName
     * @return
     */
    public SiteDashboardPage clickSite(final String siteName)
    {
        getSite(siteName).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }
}
