package org.alfresco.share.sitesFeatures.wiki;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
@Slf4j
public class RenameWikiPageTests extends BaseTest
{
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    private WikiMainPage wikiMainPage;
    private WikiListPage wikiListPage;
    private String password = "password";
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String newWikiMainPageTitle = "New wiki main page title";
    private String wikiMainPageContent = "Wiki main page content";
    private String wikiMainPagetTitle = "Main Page";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());
        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.WIKI, null);

        wikiMainPage = new WikiMainPage(webDriver);
        wikiListPage = new WikiListPage(webDriver);

        authenticateUsingLoginPage(testUser.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(testUser.get());
    }

    @TestRail (id = "C5500")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void renameWikiMainPage()
    {
        log.info("Preconditions: create site and update wiki main page content");
        sitePagesService.updateWikiPage(testUser.get().getUsername(), password, siteName.get().getId(), "Main Page", "Main Page", wikiMainPageContent, null);
        wikiMainPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click 'Rename' button");
        wikiMainPage.clickOnRenameWikiMainPageButton();

        log.info("STEP 2: Type the new name for the wiki page and click on 'Save' button");
        wikiListPage.clearWikiTitle();
        wikiListPage.typeNewMainPageName(newWikiMainPageTitle);
        wikiListPage.clickOnSaveButton();
        Assert.assertEquals(wikiMainPage.getWikiPageTitlesList(), newWikiMainPageTitle, "Wrong wiki page title!");

        log.info("STEP 3: Click on 'Main Page' link");
        wikiMainPage.clickOnMainPageLink();
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), wikiMainPagetTitle);
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals("This page has been moved here."), "Wiki main page content is not correct!");

        log.info("STEP 4: Click on 'here' link");
        wikiMainPage.clickOnHereLink();
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), newWikiMainPageTitle);
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals(wikiMainPageContent), "Wiki main page content is not correct!");

        log.info("STEP 5: Click on 'Wiki Page List' link");
        wikiMainPage.clickOnWikiListLink();
        Assert.assertEquals(wikiListPage.getWikiPageTitlesListSize(), 2, "2 pages should be listed.");
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains(newWikiMainPageTitle), newWikiMainPageTitle + " page is not listed.");
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Main Page"), "Main Page is not listed.");
    }

    @TestRail (id = "C5501")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelRenameWikiMainPage()
    {
        log.info("Preconditions: create site and update wiki main page content");
        sitePagesService.updateWikiPage(testUser.get().getUsername(), password, siteName.get().getId(), "Main Page", "Main Page", wikiMainPageContent, null);
        wikiMainPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click 'Rename' button");
        wikiMainPage.clickOnRenameWikiMainPageButton();

        log.info("STEP 2: Type the new name for the wiki page and click on 'X' button");
        wikiListPage.closeRenamePopup();
        Assert.assertFalse(wikiMainPage.isRenameWikiMainPagePanelDisplayed(), "Pop up should not be diaplyed!");
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), wikiMainPagetTitle);
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals(wikiMainPageContent), "Wiki main page content is not correct!");
    }

    @TestRail (id = "C5502")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void renameWikiPage()
    {
        log.info("Preconditions: create site and two wiki pages");
        sitePagesService.createWiki(testUser.get().getUsername(), password, siteName.get().getId(), "Page1", "Content", null);
        sitePagesService.createWiki(testUser.get().getUsername(), password, siteName.get().getId(), "Page2", "Content", null);
        wikiListPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on 'Page1' title");
        wikiListPage.clickPageName("Page1");

        log.info("STEP 2: Click 'Rename' button");
        wikiMainPage.clickOnRenameWikiMainPageButton();

        log.info("STEP 3: Type the new name for the wiki page and click on 'Save' button");
        wikiListPage.clearWikiTitle();
        wikiListPage.typeNewMainPageName("NewPage1");
        wikiListPage.clickOnSaveButton();
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), "NewPage1");

        log.info("STEP 3: Click on 'Wiki Page Link' link");
        wikiMainPage.clickOnWikiListLink();
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Page1") && wikiListPage.getWikiPageTitlesList().contains("Page2")
            && wikiListPage.getWikiPageTitlesList().contains("NewPage1"), "Pages are not listed.");

        log.info("STEP 3: Click on 'Page1' link");
        wikiListPage.clickPageName("Page1");
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), "Page1");
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals("This page has been moved here."), "Wiki main page content is not correct!");

        log.info("STEP 4: Click on 'here' link");
        wikiMainPage.clickOnHereLink();
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), "NewPage1");

    }

    @TestRail (id = "C5503")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelRenameWikiPage()
    {
        log.info("Preconditions: create site and a wiki page");
        sitePagesService.createWiki(testUser.get().getUsername(), password, siteName.get().getId(), "Page1", "Content", null);
        wikiListPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on 'Page1' title and then on 'Rename' button");
        wikiListPage.clickPageName("Page1");
        wikiMainPage.clickOnRenameWikiMainPageButton();

        log.info("STEP 2: Type the new name for the wiki page and click on 'X' button");
        wikiListPage.closeRenamePopup();
        Assert.assertFalse(wikiMainPage.isRenameWikiMainPagePanelDisplayed(), "Pop up should not be diaplyed!");
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), "Page1");
    }
}
