package org.alfresco.share.sitesFeatures.wiki;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.wiki.EditWikiPage;
import org.alfresco.po.share.site.wiki.RevertVersionPopUp;
import org.alfresco.po.share.site.wiki.WikiDetailsPage;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */

public class ViewWikiPageTests extends ContextAwareWebTest
{
    @Autowired
    WikiMainPage wikiMainPage;

    @Autowired
    EditWikiPage editWikiPage;

    @Autowired
    WikiDetailsPage wikiDetailsPage;

    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    WikiPage wikiPage;

    @Autowired
    RevertVersionPopUp revertPopUp;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName;
    private String wikiPageName = "Page1";
    private String tagName = "tag1";
    private List<String> tags = new ArrayList<>();

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, testUser);
        setupAuthenticatedSession(testUser, password);
        tags.add(tagName);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @TestRail (id = "C5536")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewWikiPageDetailsFromPageView()
    {
        LOG.info("Preconditions: create site");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Edit Page' link");
        wikiMainPage.clickEditPageLink();

        LOG.info("STEP 2: Add [[Page1]] in Text box, 'tag1' in Tags and click 'Save' button");
        editWikiPage.addTag(tagName);
        editWikiPage.saveWikiContent("[[Page1]]");
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals(wikiPageName), "Main Page should contain a link to Page1");

        LOG.info("STEP 3: Click 'Details' link");
        wikiMainPage.clickOnDetailsPageLink();
        Assert.assertTrue(wikiDetailsPage.getTagsList().contains(tagName), "Tag is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getLinkedPagesList().contains(wikiPageName), "Page is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getVersion().equals("Version 1.0"), "Wrong version is displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C5537")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewWikiPageDetailsFromWikiPageList()
    {
        LOG.info("Preconditions: create site and wiki page");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageName, "[[Page2]]", tags);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Details' link");
        wikiListPage.clickDetails(wikiPageName);
        Assert.assertTrue(wikiDetailsPage.getTagsList().contains("tag1"), "Tag is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getLinkedPagesList().contains("Page2"), "Page is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getVersion().equals("Version 1.0"), "Wrong version is displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C5540")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void revertWikiPageToAnEarlierVersion()
    {
        LOG.info("Preconditions: create site and wiki page");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageName, "[[Page2]]", tags);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Edit' link");
        wikiListPage.clickEdit(wikiPageName);

        LOG.info("STEP 2: Change text with [[Page3]], remove tag1, then click 'Save' button");
        //   editWikiPage.clearWikiPageContent();
        editWikiPage.removeTag(tagName);
        editWikiPage.saveWikiContent("[[Page3]]");
        Assert.assertTrue(wikiPage.getWikiPageContent().equals("Page3Page2"), "Wrong wiki page content");

        LOG.info("STEP 3: Click on 'Details' link");
        wikiPage.clickOnDetailsLink();
        Assert.assertTrue(wikiDetailsPage.getTag().equals("(None)"), "Tag is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getLinkedPagesList().contains("Page3"), "Page is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getVersion().equals("Version 1.1"), "Wrong version is displayed!");

        LOG.info("STEP 4: Change 'View version' to 1.0");
        wikiDetailsPage.selectVersion("1.0");
        Assert.assertTrue(wikiDetailsPage.getPageContentDetails().equals("Page2"), "Wrong page content!");

        LOG.info("STEP 5: Click on 'Revert' button from Version 1.0");
        wikiDetailsPage.clickOnVersion("Version 1.0");
        wikiDetailsPage.clickOnRevert();
        Assert.assertEquals(revertPopUp.getRevertMessage(), "Are you sure you want to revert " + wikiPageName + " to version 1.0?", "Wrong revert message");

        LOG.info("STEP 6: Click 'Ok'");
        revertPopUp.clickRevertOk();

        // TODO : Check that notification appears after revert is performed

        wikiDetailsPage.renderedPage();
        Assert.assertTrue(wikiDetailsPage.getVersionsList().contains("Version 1.2"), "New version 1.2 appears in Version History.");
        Assert.assertTrue(wikiDetailsPage.getLinkedPagesList().contains("Page2"), "Page is not displayed!");

        LOG.info("STEP 7: Click 'View Page' link");
        wikiDetailsPage.clickOnViewPageLink();
        Assert.assertTrue(wikiPage.getWikiPageContent().equals("Page2"), "Page should contain a link to Page2!");
        siteService.delete(adminUser, adminPassword, siteName);

    }
}
