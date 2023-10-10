package org.alfresco.po.share.user.profile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author bogdan.bocancea
 */
@Slf4j
public class UserSitesListPage extends SharePage2<UserSitesListPage> implements AccessibleByMenuBar
{
    private String userName;

    private final By sitesMenuLink = By.cssSelector("span[id='HEADER_SITES_MENU']");
    private final By mySitesLink = By.cssSelector("span[id='HEADER_SITES_MENU_MY_SITES']");
    private final By noSitesMessage = By.cssSelector("div.viewcolumn p");
    private final By siteRows = By.cssSelector(".sites > li");
    private final By siteNameLocator = By.cssSelector("p a");

    public UserSitesListPage(ThreadLocal<WebDriver> webDriver)
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
        clickElement(sitesMenuLink);
        waitUntilElementIsVisible(mySitesLink).click();
        return new UserSitesListPage(webDriver);
    }

    private WebElement getSiteRow(SiteModel site)
    {
        List<WebElement> sites = waitUntilElementsAreVisible(siteRows);
        return sites.stream().filter(siteRow -> siteRow.findElement(siteNameLocator)
            .getAttribute("href").contains(site.getId())).findFirst().orElse(null);
    }

    public UserSitesListPage assertSiteIsDisplayed(SiteModel site)
    {
        log.info("Assert site {} is displayed", site.getId());
        assertTrue(isElementDisplayed(getSiteRow(site)), String.format("Site %s is displayed", site.getId()));
        return this;
    }

    public UserSitesListPage assertSiteIsNotDisplayed(SiteModel site)
    {
        log.info("Assert site {} is not displayed", site.getId());
        assertNull(getSiteRow(site), String.format("Site %s is displayed", site.getId()));
        return this;
    }

    public SiteDashboardPage clickSite(SiteModel site)
    {
        log.info("Click site {}", site.getId());
        clickElement(siteNameLocator.findElement(getSiteRow(site)));
        return new SiteDashboardPage(webDriver);
    }

    public UserSitesListPage assertUserHasNoSitesMessageIsDisplayed()
    {
        log.info("Assert User is not a member of any sites");
        assertEquals(getElementText(noSitesMessage), language.translate("user.sitesList.noSitesMessage"),
            "User is not a member of any sites is displayed");
        return this;
    }
}
