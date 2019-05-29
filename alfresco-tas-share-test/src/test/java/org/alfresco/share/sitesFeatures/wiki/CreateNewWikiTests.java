package org.alfresco.share.sitesFeatures.wiki;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.site.wiki.CreateWikiPage;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author iulia.nechita
 */

public class CreateNewWikiTests extends ContextAwareWebTest
{

    @Autowired
    WikiMainPage wikiMainPage;

    @Autowired
    WikiPage wikiPage;

    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    CreateWikiPage createWikiPage;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName;
    private String wikiPageHeader = "Create Wiki Page";
    private String wikiPageTitle;
    private String wikiPageContent;

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        setupAuthenticatedSession(testUser, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @BeforeMethod (alwaysRun = true)
    public void createSite()
    {
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());
        wikiPageContent = String.format("WikiContent%s", RandomData.getRandomAlphanumeric());

        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.WIKI, null);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanupMethod()
    {
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C5504")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewWikiPage()
    {
        // precondition
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        LOG.info("STEP 2: Add a title and a content for the new wiki page");
        createWikiPage.typeWikiPageTitle(wikiPageTitle);
        createWikiPage.typeWikiPageContent(wikiPageContent);

        LOG.info("STEP 3: Click on Save button");
        createWikiPage.saveWikiPage();

        LOG.info("STEP 4: Click on wiki page list");
        wikiPage.clickOnWikiListLink();
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains(wikiPageTitle), "Wiki page is not displayed in the list!");
    }

    @TestRail (id = "C5504")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreationOfNewWikiPage()
    {
        // precondition
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        LOG.info("STEP 2: Add a title and a content for the new wiki page");
        createWikiPage.typeWikiPageTitle(wikiPageTitle);
        createWikiPage.typeWikiPageContent(wikiPageContent);

        LOG.info("STEP 3: Click on Cancel button");
        createWikiPage.cancelWikiPage();

        LOG.info("STEP 4: Check that wiki page is not present on the list");
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().isEmpty(), "There are wiki pages displayed in the list!");
        Assert.assertTrue(wikiListPage.noWikiPageDisplayed().equals("There are currently no pages to display"), "Wrong message displayed!");
    }

    @TestRail (id = "C5524")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createWikiPageFromWikiPageList()
    {
        String tagName = String.format("tag%s", RandomData.getRandomAlphanumeric());

        // precondition
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        LOG.info("STEP 2: Add a title, a content for the new wiki page and a tag");
        createWikiPage.typeWikiPageTitle(wikiPageTitle);
        createWikiPage.typeWikiPageContent(wikiPageContent);
        createWikiPage.addTag(tagName);
        Assert.assertTrue(createWikiPage.getWikiPageTagsList().contains(tagName), "Tag is not displayed!");

        LOG.info("STEP 3: Click on Save button");
        createWikiPage.saveWikiPage();

        LOG.info("STEP 4: Navigate to Wiki Page List page");
        wikiPage.clickOnWikiListLink();
        Assert.assertTrue(wikiListPage.getTagsList().contains(tagName.toLowerCase() + " (1)"), "Tag is not displayed in the list!");
    }
}
