package org.alfresco.share.sitesFeatures.wiki;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.wiki.CreateWikiPage;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.nechita
 */
@Slf4j
public class CreateNewWikiTests extends BaseTest
{
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    private WikiPage wikiPage;
    private CreateWikiPage createWikiPage;
    private WikiMainPage wikiMainPage;
    private WikiListPage wikiListPage;
    private String wikiPageHeader = "Create Wiki Page";
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
        createWikiPage = new CreateWikiPage(webDriver);
        wikiPage = new WikiPage(webDriver);

        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C5504")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewWikiPage()
    {
        // precondition
        wikiMainPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        log.info("STEP 2: Add a title and a content for the new wiki page");
        createWikiPage.typeWikiPageTitle("wikiPageTitle");
        createWikiPage.typeWikiPageContent("wikiPageContent");

        log.info("STEP 3: Click on Save button");
        createWikiPage.saveWikiPage();

        log.info("STEP 4: Click on wiki page list");
        wikiPage.clickOnWikiListLink();
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("wikiPageTitle"), "Wiki page is not displayed in the list!");
    }

    @TestRail (id = "C5504")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, TestGroup.INTEGRATION })
    public void cancelCreationOfNewWikiPage()
    {
        // precondition
        wikiMainPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        log.info("STEP 2: Add a title and a content for the new wiki page");
        createWikiPage.typeWikiPageTitle("wikiPageTitle");
        createWikiPage.typeWikiPageContent("wikiPageContent");

        log.info("STEP 3: Click on Cancel button");
        createWikiPage.cancelWikiPage();

        log.info("STEP 4: Check that wiki page is not present on the list");
        Assert.assertTrue(wikiListPage.noWikiPageDisplayed().equals("There are currently no pages to display"));
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().isEmpty(), "There are wiki pages displayed in the list!");
    }

    @TestRail (id = "C5524")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createWikiPageFromWikiPageList()
    {
        String tagName = String.format("tag%s", RandomData.getRandomAlphanumeric());

        // precondition
        wikiListPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        log.info("STEP 2: Add a title, a content for the new wiki page and a tag");
        createWikiPage.typeWikiPageTitle("wikiPageTitle");
        createWikiPage.typeWikiPageContent("wikiPageContent");
        createWikiPage.addTag(tagName);
        Assert.assertTrue(createWikiPage.getWikiPageTagsList().contains(tagName), "Tag is not displayed!");

        log.info("STEP 3: Click on Save button");
        createWikiPage.saveWikiPage();

        log.info("STEP 4: Navigate to Wiki Page List page");
        wikiPage.clickOnWikiListLink();
        Assert.assertTrue(wikiListPage.getTagsList().contains(tagName.toLowerCase() + " (1)"), "Tag is not displayed in the list!");
    }
}
