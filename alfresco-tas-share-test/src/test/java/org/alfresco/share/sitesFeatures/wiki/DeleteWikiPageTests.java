package org.alfresco.share.sitesFeatures.wiki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.wiki.DeleteWikiPagePopUp;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */

public class DeleteWikiPageTests extends ContextAwareWebTest
{
    @Autowired
    WikiMainPage wikiMainPage;

    @Autowired
    DeleteWikiPagePopUp deleteWikiPagePopUp;

    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    WikiPage wikiPage;

    private List<Page> pagesToAdd = new ArrayList<Page>();
    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName;
    private String wikiMainPageContent = "Wiki main page content";

    @BeforeMethod
    public void setupTest()
    {
        pagesToAdd.add(Page.WIKI);
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, testUser);
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C5515")
    @Test
    public void deleteWikiMainPageFromPageView()
    {
        LOG.info("Preconditions: create site and add wiki main page content");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.updateWikiPage(testUser, password, siteName, "Main Page", "Main Page", wikiMainPageContent, null);
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Delete' button");
        wikiMainPage.deleteWikiMainPage();
        Assert.assertEquals(deleteWikiPagePopUp.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        LOG.info("STEP 2: Click 'Delete' button");
        deleteWikiPagePopUp.clickDeleteWikiPage();
        Assert.assertEquals(wikiListPage.noWikiPageDisplayed(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail(id = "C5516")
    @Test
    public void deleteWikiMainPageFromWikiPageList()
    {
        LOG.info("Preconditions: create site and wiki main page content");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.updateWikiPage(testUser, password, siteName, "Main Page", "Main Page", wikiMainPageContent, null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Delete'  button for Main Page");
        wikiListPage.clickDeletePage("Main Page");
        Assert.assertEquals(deleteWikiPagePopUp.getMessage(),"Are you sure you want to delete this page?", "Wrong confirmation message!");

        LOG.info("STEP 2: Click 'Delete' button");
        deleteWikiPagePopUp.clickDeleteWikiPage();
        Assert.assertEquals(wikiListPage.noWikiPageDisplayed(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail(id = "C5517")
    @Test
    public void deleteWikiPageFromPageView()
    {
        LOG.info("Preconditions: create site and wiki page");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.createWiki(testUser, password, siteName, "Page1", "", null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Page1'  title");
        wikiListPage.clickPageName("Page1");

        LOG.info("STEP 1: Click on 'Delete'  button");
        wikiPage.deleteWikiPage();
        Assert.assertEquals(deleteWikiPagePopUp.getMessage(),"Are you sure you want to delete this page?", "Wrong confirmation message!");

        LOG.info("STEP 3: Click 'Delete' button");
        deleteWikiPagePopUp.clickDeleteWikiPage();
        Assert.assertEquals(wikiListPage.noWikiPageDisplayed(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail(id = "C5518")
    @Test
    public void deleteWikiPageFromWikiPageList()
    {
        LOG.info("Preconditions: create site and wiki pages");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.createWiki(testUser, password, siteName, "Page1", "[[Page2]]", Arrays.asList("tag1"));
        sitePagesService.createWiki(testUser, password, siteName, "Page2", "[[Page3]]", Arrays.asList("tag2"));
        sitePagesService.createWiki(testUser, password, siteName, "Page3", "Page3 content", Arrays.asList("tag3"));

        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Delete' button for Page2");
        wikiListPage.clickDeletePage("Page2");
        Assert.assertEquals(deleteWikiPagePopUp.getMessage(),"Are you sure you want to delete this page?", "Wrong confirmation message!");

        LOG.info("STEP 2: Click 'Delete'");
        deleteWikiPagePopUp.clickDeleteWikiPage();
        Assert.assertEquals(wikiListPage.getWikiPageTitlesListSize(), 2, "Wiki page titles list size is: ");
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Page1") && wikiListPage.getWikiPageTitlesList().contains("Page3"),
                "Pages are not listed.");
        Assert.assertFalse(wikiListPage.getTagsList().contains("tag2" + " (1)"), "Tag is displayed in the list!");
        Assert.assertEquals(wikiListPage.getMissingPageTextColor("Page1"), "#cc2200");
    }

    @TestRail(id = "C5519")
    @Test
    public void cancelDeletingWikiPage()
    {
        LOG.info("Preconditions: create site and update wiki main page content");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
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
