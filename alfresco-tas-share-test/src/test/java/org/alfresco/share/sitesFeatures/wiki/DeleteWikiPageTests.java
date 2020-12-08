package org.alfresco.share.sitesFeatures.wiki;

import java.util.Collections;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */

public class DeleteWikiPageTests extends ContextAwareWebTest
{
    //@Autowired
    WikiMainPage wikiMainPage;

    //@Autowired
    DeleteDialog deleteWikiPagePopUp;

    //@Autowired
    WikiListPage wikiListPage;

    //@Autowired
    WikiPage wikiPage;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName;
    private String wikiMainPageContent = "Wiki main page content";

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        setupAuthenticatedSession(testUser, password);
    }

    @BeforeMethod (alwaysRun = true)
    public void createSite()
    {
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.WIKI, null);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }


    @AfterMethod (alwaysRun = true)
    public void cleanupMethod()
    {
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5515")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteWikiMainPageFromPageView()
    {
        LOG.info("Preconditions: create site and add wiki main page content");
        sitePagesService.updateWikiPage(testUser, password, siteName, "Main Page", "Main Page", wikiMainPageContent, null);
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Delete' button");
        wikiMainPage.deleteWikiMainPage();
        Assert.assertEquals(deleteWikiPagePopUp.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        LOG.info("STEP 2: Click 'Delete' button");
        deleteWikiPagePopUp.clickDelete();
        Assert.assertEquals(wikiListPage.noWikiPageDisplayed(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail (id = "C5516")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteWikiMainPageFromWikiPageList()
    {
        LOG.info("Preconditions: create site and wiki main page content");
        sitePagesService.updateWikiPage(testUser, password, siteName, "Main Page", "Main Page", wikiMainPageContent, null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Delete'  button for Main Page");
        wikiListPage.clickDeletePage("Main Page");
        Assert.assertEquals(deleteWikiPagePopUp.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        LOG.info("STEP 2: Click 'Delete' button");
        deleteWikiPagePopUp.clickDelete();
        Assert.assertEquals(wikiListPage.noWikiPageDisplayed(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail (id = "C5517")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteWikiPageFromPageView()
    {
        LOG.info("Preconditions: create site and wiki page");
        sitePagesService.createWiki(testUser, password, siteName, "Page1", "", null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Page1'  title");
        wikiListPage.clickPageName("Page1");

        LOG.info("STEP 1: Click on 'Delete'  button");
        wikiPage.deleteWikiPage();
        Assert.assertEquals(deleteWikiPagePopUp.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        LOG.info("STEP 3: Click 'Delete' button");
        deleteWikiPagePopUp.clickDelete();
        Assert.assertEquals(wikiListPage.noWikiPageDisplayed(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail (id = "C5518")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteWikiPageFromWikiPageList()
    {
        LOG.info("Preconditions: create site and wiki pages");
        sitePagesService.createWiki(testUser, password, siteName, "Page1", "[[Page2]]", Collections.singletonList("tag1"));
        sitePagesService.createWiki(testUser, password, siteName, "Page2", "[[Page3]]", Collections.singletonList("tag2"));
        sitePagesService.createWiki(testUser, password, siteName, "Page3", "Page3 content", Collections.singletonList("tag3"));

        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Delete' button for Page2");
        wikiListPage.clickDeletePage("Page2");
        Assert.assertEquals(deleteWikiPagePopUp.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        LOG.info("STEP 2: Click 'Delete'");
        deleteWikiPagePopUp.clickDelete();
        Assert.assertEquals(wikiListPage.getWikiPageTitlesListSize(), 2, "Wiki page titles list size is: ");
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Page1") && wikiListPage.getWikiPageTitlesList().contains("Page3"),
            "Pages are not listed.");
        Assert.assertFalse(wikiListPage.getTagsList().contains("tag2" + " (1)"), "Tag is displayed in the list!");
        Assert.assertEquals(wikiListPage.getMissingPageTextColor("Page1"), "#cc2200");
    }

    @TestRail (id = "C5519")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingWikiPage()
    {
        LOG.info("Preconditions: create site and update wiki main page content");
        sitePagesService.createWiki(testUser, password, siteName, "Page1", "", null);
        sitePagesService.updateWikiPage(testUser, password, siteName, "Main Page", "Main Page", wikiMainPageContent, null);

        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Delete' for 'Main Page'");
        wikiListPage.clickDeletePage("Main Page");

        LOG.info("STEP 2: Choose not to delete page");
        deleteWikiPagePopUp.clickCancel();
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Main Page"), "Main Page should be displayed in the wiki page list!");

        LOG.info("STEP 1: Click 'Delete' for 'Page1'");
        wikiListPage.clickDeletePage("Page1");

        LOG.info("STEP 2: Choose not to delete page");
        deleteWikiPagePopUp.clickCancel();
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Page1"), "Page1 should be displayed in the wiki page list!");
    }
}
