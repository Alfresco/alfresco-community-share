package org.alfresco.share.sitesFeatures.wiki;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.wiki.EditWikiPage;
import org.alfresco.po.share.site.wiki.RevertVersionPopUp;
import org.alfresco.po.share.site.wiki.WikiDetailsPage;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.po.share.site.wiki.WikiPage;

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
public class ViewWikiPageTests extends BaseTest
{
    //@Autowired
    WikiMainPage wikiMainPage;

    //@Autowired
    EditWikiPage editWikiPage;

    //@Autowired
    WikiDetailsPage wikiDetailsPage;

    // @Autowired
    WikiListPage wikiListPage;

    //@Autowired
    WikiPage wikiPage;
    @Autowired
    SiteService siteService;
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    RevertVersionPopUp revertPopUp;
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String wikiPageName = "Page1";
    private String tagName = "tag1";
    private List<String> tags = new ArrayList<>();

    @BeforeMethod (alwaysRun = true)
    public void createUser()
    {

        log.info("Precondition: Any Test user is created");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        wikiMainPage = new WikiMainPage(webDriver);
        editWikiPage = new EditWikiPage(webDriver);
        wikiDetailsPage = new WikiDetailsPage(webDriver);
        wikiListPage = new WikiListPage(webDriver);
        wikiPage = new WikiPage(webDriver);
        tags.add(tagName);

        authenticateUsingLoginPage(testUser.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());
    }

    @TestRail (id = "C5536")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewWikiPageDetailsFromPageView()
    {
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());
        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), Page.WIKI, null);
        wikiMainPage.navigate(siteName.get());

        log.info("STEP 1: Click 'Edit Page' link");
        wikiMainPage.clickEditPageLink();

        log.info("STEP 2: Add [[Page1]] in Text box, 'tag1' in Tags and click 'Save' button");
        editWikiPage.addTag(tagName);
        editWikiPage.saveWikiContent("[[Page1]]");
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals(wikiPageName), "Main Page should contain a link to Page1");

        log.info("STEP 3: Click 'Details' link");
        wikiMainPage.clickOnDetailsPageLink();
        Assert.assertTrue(wikiDetailsPage.getTagsList().contains(tagName), "Tag is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getLinkedPagesList().contains(wikiPageName), "Page is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getVersion().equals("Version 1.0"), "Wrong version is displayed!");
        deleteSitesIfNotNull(siteName.get());

    }

    @TestRail (id = "C5537")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewWikiPageDetailsFromWikiPageList()
    {
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());
        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), Page.WIKI, null);
        sitePagesService.createWiki(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), wikiPageName, "[[Page2]]", tags);
        wikiListPage.navigate(siteName.get());

        log.info("STEP 1: Click on 'Details' link");
        wikiListPage.clickDetails(wikiPageName);
        Assert.assertTrue(wikiDetailsPage.getTagsList().contains("tag1"), "Tag is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getLinkedPagesList().contains("Page2"), "Page is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getVersion().equals("Version 1.0"), "Wrong version is displayed!");
        deleteSitesIfNotNull(siteName.get());

    }

    @TestRail (id = "C5540")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void revertWikiPageToAnEarlierVersion() {
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), Page.WIKI, null);
        sitePagesService.createWiki(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), wikiPageName, "[[Page2]]", tags);
        wikiListPage.navigate(siteName.get());

        log.info("STEP 1: Click on 'Edit' link");
        wikiListPage.clickEdit(wikiPageName);

        log.info("STEP 2: Change text with [[Page3]], remove tag1, then click 'Save' button");
        //   editWikiPage.clearWikiPageContent();
        editWikiPage.removeTag(tagName);
        editWikiPage.saveWikiContent("[[Page3]]");
        Assert.assertTrue(wikiPage.getWikiPageContent().equals("Page2Page3"), "Wrong wiki page content");

        log.info("STEP 3: Click on 'Details' link");
        wikiPage.clickOnDetailsLink();
        Assert.assertTrue(wikiDetailsPage.getTag().equals("(None)"), "Tag is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getLinkedPagesList().contains("Page3"), "Page is not displayed!");
        Assert.assertTrue(wikiDetailsPage.getVersion().equals("Version 1.1"), "Wrong version is displayed!");

        log.info("STEP 4: Change 'View version' to 1.0");
        wikiDetailsPage.selectVersion("1.0");
        Assert.assertTrue(wikiDetailsPage.getPageContentDetails().equals("Page2"), "Wrong page content!");

        log.info("STEP 5: Click on 'Revert' button from Version 1.0");
        wikiDetailsPage.clickOnVersion("Version 1.0");
        wikiDetailsPage.clickOnRevert();
        Assert.assertEquals(wikiDetailsPage.getRevertMessage(), "Are you sure you want to revert " + wikiPageName + " to version 1.0?", "Wrong revert message");

        log.info("STEP 6: Click 'Ok'");
        wikiDetailsPage.clickRevertOk();

        // TODO : Check that notification appears after revert is performed

        Assert.assertTrue(wikiDetailsPage.getVersionsList().contains("Version 1.2"), "New version 1.2 appears in Version History.");
        Assert.assertTrue(wikiDetailsPage.getLinkedPagesList().contains("Page2"), "Page is not displayed!");

        log.info("STEP 7: Click 'View Page' link");
        wikiDetailsPage.clickOnViewPageLink();
        Assert.assertTrue(wikiPage.getWikiPageContent().equals("Page2"), "Page should contain a link to Page2!");
        deleteSitesIfNotNull(siteName.get());

    }
}
