package org.alfresco.share.sitesFeatures.wiki;


import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by Argint Alex
 */
@Slf4j
public class AccessingTheWikiTests extends BaseTest
{

    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    private CustomizeSitePage customizeSitePage;
    private WikiMainPage wikiMainPage;
    private WikiListPage wikiListPage;
    private SiteDashboardPage siteDashboardPage;
    private String password = "password";
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());
        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.WIKI, null);

        wikiMainPage = new WikiMainPage(webDriver);
        wikiListPage = new WikiListPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        customizeSitePage = new CustomizeSitePage(webDriver);

        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C5493")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, TestGroup.INTEGRATION })
    public void accessWikiMainPageTest() throws DataPreparationException
    {
        log.info("Step 1 : Click on wiki tab and inspect the elements on the wiki main page");
        siteDashboardPage.navigate(siteName.get().getId());
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.WIKI);
        Assert.assertTrue(wikiMainPage.isNewPageButtonDisplayed(), "'New page' button is not displayed");
        Assert.assertTrue(wikiMainPage.isDeleteButtonDisplayed(), "'Delete' button is not displayed");
        Assert.assertFalse(wikiMainPage.isDeleteButtonEnabled(), "'Delete' button is enabled");
        Assert.assertTrue(wikiMainPage.isRenameButtonDisplayed(), "'Rename' button is not displayed");
        Assert.assertTrue(wikiMainPage.isDisabledViewLinkDisplayed(), "'View' link is not displayed");
        Assert.assertTrue(wikiMainPage.isEditPagelinkDisplayed(), "'Edit Page' link is not displayed");
        Assert.assertTrue(wikiMainPage.isDetailsLinkDisplayed(), "'Details' link is not displayed");

        log.info("Step 2 : Open customize site and rename 'Wiki' to 'newWiki'");
        customizeSitePage.navigate(siteName.get().getId());
        customizeSitePage.renameSitePage(SitePageType.WIKI, "newWiki");
        Assert.assertEquals(customizeSitePage.getPageDisplayName(SitePageType.WIKI), "newWiki", "Wiki page not renamed correctly");

        log.info("Step 3 : Click 'Ok' button and verify that the wiki page has been renamed on site dashboard");
        customizeSitePage.saveChanges();
        siteDashboardPage.navigate(siteName.get().getId());
        Assert.assertEquals(siteDashboardPage.getPageDisplayName(SitePageType.WIKI), "newWiki", "'Wiki' page not correctly renamed on site dashboard");

        log.info("Step 4 : Click on 'newWiki' link and verify that 'Main Page' is opeend");
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.WIKI);
        Assert.assertTrue(wikiMainPage.isNewPageButtonDisplayed(), "Wiki 'Main Page' is not opened");
    }

    @TestRail (id = "C5494")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void accessWikiPageListTest() throws DataPreparationException
    {
        List<String> siteTitles = new ArrayList<>();
        siteTitles.add("Page1");
        siteTitles.add("Page2");

        log.info("Starting test C5494");
        wikiMainPage.navigate(siteName.get().getId());
        for (String siteTitle : siteTitles)
        {
            sitePagesService.createWiki(user1.get().getUsername(), password, siteName.get().getId(), siteTitle, siteTitle, null);
        }
        wikiMainPage.navigate(siteName.get().getId());

        log.info("Step 1 : Click on 'Wiki Page List' link");
        wikiMainPage.clickOnWikiListLink();
        List<String> actualPageList = wikiListPage.getWikiPageTitlesList();

        Assert.assertEquals(actualPageList.size(), siteTitles.size(), "The number of listed wiki pages is not the same with the number of created wiki pages");
        for (int i = 0; i < actualPageList.size(); i++)
        {
            // the first wiki page listed is the last created
            Assert.assertEquals(actualPageList.get(i), siteTitles.get(actualPageList.size() - i - 1), "The page " + siteTitles.get(i) + " is not listed in the wiki page list");
        }

        log.info("Step 2 : Verify the actions available for wiki pages");
        for (String anActualPageList : actualPageList)
        {
            Assert.assertTrue(wikiListPage.areActionsAvailableForPage(anActualPageList),
                "One or more actions are not available for " + anActualPageList + " wiki page");
        }

        log.info("Step 3 : Click on 'Main Page' button");
        wikiListPage.clickMainPageButton();
        wikiMainPage.isRenameWikiMainPagePanelDisplayed();
    }

}
