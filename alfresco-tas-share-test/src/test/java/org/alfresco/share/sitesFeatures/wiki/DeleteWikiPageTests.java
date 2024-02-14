package org.alfresco.share.sitesFeatures.wiki;

import java.util.Collections;

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
public class DeleteWikiPageTests extends BaseTest
{
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    private WikiPage wikiPage;
    private CreateWikiPage createWikiPage;
    private WikiMainPage wikiMainPage;
    private WikiListPage wikiListPage;
    private String password = "password";
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String wikiMainPageContent = "Wiki main page content";

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

    @TestRail (id = "C5515")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteWikiMainPageFromPageView()
    {
        log.info("Preconditions: create site and add wiki main page content");
        sitePagesService.updateWikiPage(user1.get().getUsername(), password, siteName.get().getId(), "Main Page", "Main Page", wikiMainPageContent, null);
        wikiMainPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click 'Delete' button");
        wikiMainPage.deleteWikiMainPage();
        Assert.assertEquals(wikiMainPage.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        log.info("STEP 2: Click 'Delete' button");
        wikiMainPage.confirmDeletion();
        Assert.assertEquals(wikiListPage.noWikiPageDisplay(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail (id = "C5516")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteWikiMainPageFromWikiPageList()
    {
        log.info("Preconditions: create site and wiki main page content");
        sitePagesService.updateWikiPage(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "Main Page", "Main Page", wikiMainPageContent, null);
        wikiListPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click 'Delete'  button for Main Page");
        wikiListPage.clickDeletePage("Main Page");
        Assert.assertEquals(wikiMainPage.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        log.info("STEP 2: Click 'Delete' button");
        wikiMainPage.confirmDeletion();
        Assert.assertEquals(wikiListPage.noWikiPageDisplay(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail (id = "C5517")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteWikiPageFromPageView()
    {
        log.info("Preconditions: create site and wiki page");
        sitePagesService.createWiki(user1.get().getUsername(), password, siteName.get().getId(), "Page1", "", null);
        wikiListPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on 'Page1'  title");
        wikiListPage.clickPageName("Page1");

        log.info("STEP 1: Click on 'Delete'  button");
        wikiPage.deleteWikiPage();
        Assert.assertEquals(wikiMainPage.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        log.info("STEP 2: Click 'Delete' button");
        wikiMainPage.confirmDeletion();
        Assert.assertEquals(wikiListPage.noWikiPageDisplay(), "There are currently no pages to display", "Wrong message displayed!");
    }

    @TestRail (id = "C5518")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteWikiPageFromWikiPageList()
    {
        log.info("Preconditions: create site and wiki pages");
        sitePagesService.createWiki(user1.get().getUsername(), password, siteName.get().getId(), "Page1", "[[Page2]]", Collections.singletonList("tag1"));
        sitePagesService.createWiki(user1.get().getUsername(), password, siteName.get().getId(), "Page2", "[[Page3]]", Collections.singletonList("tag2"));
        sitePagesService.createWiki(user1.get().getUsername(), password, siteName.get().getId(), "Page3", "Page3 content", Collections.singletonList("tag3"));

        wikiListPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click 'Delete' button for Page2");
        wikiListPage.clickDeletePage("Page2");
        Assert.assertEquals(wikiMainPage.getMessage(), "Are you sure you want to delete this page?", "Wrong confirmation message!");

        log.info("STEP 2: Click 'Delete'");
        wikiMainPage.confirmDeletion();
        Assert.assertEquals(wikiListPage.getWikiPageTitlesListSize(), 2, "Wiki page titles list size is: ");
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Page1") && wikiListPage.getWikiPageTitlesList().contains("Page3"),
            "Pages are not listed.");
        Assert.assertFalse(wikiListPage.getTagsList().contains("tag2" + " (1)"), "Tag is displayed in the list!");
    }

    @TestRail (id = "C5519")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingWikiPage()
    {
        log.info("Preconditions: create site and update wiki main page content");
        sitePagesService.createWiki(user1.get().getUsername(), password, siteName.get().getId(), "Page1", "", null);
        sitePagesService.updateWikiPage(user1.get().getUsername(), password, siteName.get().getId(), "Main Page", "Main Page", wikiMainPageContent, null);

        wikiListPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click 'Delete' for 'Main Page'");
        wikiListPage.clickDeletePage("Main Page");

        log.info("STEP 2: Choose not to delete page");
        wikiMainPage.clickCancel();
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Main Page"), "Main Page should be displayed in the wiki page list!");

        log.info("STEP 1: Click 'Delete' for 'Page1'");
        wikiListPage.clickDeletePage("Page1");

        log.info("STEP 2: Choose not to delete page");
        wikiMainPage.clickCancel();
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Page1"), "Page1 should be displayed in the wiki page list!");
    }
}
