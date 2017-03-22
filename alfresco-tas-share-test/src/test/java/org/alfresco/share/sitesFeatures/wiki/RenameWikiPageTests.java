package org.alfresco.share.sitesFeatures.wiki;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.wiki.CreateWikiPage;
import org.alfresco.po.share.site.wiki.EditWikiPage;
import org.alfresco.po.share.site.wiki.RenameWikiMainPagePopup;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
public class RenameWikiPageTests extends ContextAwareWebTest
{
    @Autowired
    WikiMainPage wikiMainPage;

    @Autowired
    EditWikiPage editWikiPage;

    @Autowired
    RenameWikiMainPagePopup renameWikiMainPage;

    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    CreateWikiPage createWikiPage;

    private List<Page> pagesToAdd = new ArrayList<Page>();
    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName;
    private String newWikiMainPageTitle = "New wiki main page title";
    private String wikiMainPageContent = "Wiki main page content";
    private String wikiMainPagetTitle = "Main Page";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(Page.WIKI);
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, testUser);
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C5500")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void renameWikiMainPage()
    {
        LOG.info("Preconditions: create site and update wiki main page content");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.updateWikiPage(testUser, password, siteName, "Main Page", "Main Page", wikiMainPageContent, null);
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Rename' button");
        wikiMainPage.clickOnRenameWikiMainPageButton();

        LOG.info("STEP 2: Type the new name for the wiki page and click on 'Save' button");
        renameWikiMainPage.clearWikiTitle();
        renameWikiMainPage.typeNewMainPageName(newWikiMainPageTitle);
        renameWikiMainPage.clickOnSaveButton();
        getBrowser().waitInSeconds(5);
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), newWikiMainPageTitle, "Wrong wiki page title!");

        LOG.info("STEP 3: Click on 'Main Page' link");
        wikiMainPage.clickOnMainPageLink();
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), wikiMainPagetTitle);
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals("This page has been moved here."), "Wiki main page content is not correct!");

        LOG.info("STEP 4: Click on 'here' link");
        wikiMainPage.clickOnHereLink();
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), newWikiMainPageTitle);
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals(wikiMainPageContent), "Wiki main page content is not correct!");

        LOG.info("STEP 5: Click on 'Wiki Page List' link");
        wikiMainPage.clickOnWikiListLink();
        Assert.assertEquals(wikiListPage.getWikiPageTitlesListSize(), 2, "2 pages should be listed.");
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains(newWikiMainPageTitle), newWikiMainPageTitle +" page is not listed.");
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Main Page"), "Main Page is not listed.");
    }

    @TestRail(id = "C5501")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelRenameWikiMainPage()
    {
        LOG.info("Preconditions: create site and update wiki main page content");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.updateWikiPage(testUser, password, siteName, "Main Page", "Main Page", wikiMainPageContent, null);
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Rename' button");
        wikiMainPage.clickOnRenameWikiMainPageButton();

        LOG.info("STEP 2: Type the new name for the wiki page and click on 'X' button");
        renameWikiMainPage.closeRenamePopup();
        Assert.assertFalse(wikiMainPage.isRenameWikiMainPagePanelDisplayed(), "Pop up should not be diaplyed!");
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), wikiMainPagetTitle);
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals(wikiMainPageContent), "Wiki main page content is not correct!");
    }

    @TestRail(id = "C5502")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void renameWikiPage()
    {
        LOG.info("Preconditions: create site and two wiki pages");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.createWiki(testUser, password, siteName, "Page1", "Content", null);
        sitePagesService.createWiki(testUser, password, siteName, "Page2", "Content", null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Page1' title");
        wikiListPage.clickPageName("Page1");

        LOG.info("STEP 2: Click 'Rename' button");
        wikiMainPage.clickOnRenameWikiMainPageButton();

        LOG.info("STEP 3: Type the new name for the wiki page and click on 'Save' button");
        renameWikiMainPage.clearWikiTitle();
        renameWikiMainPage.typeNewMainPageName("NewPage1");
        renameWikiMainPage.clickOnSaveButton();
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), "NewPage1");

        LOG.info("STEP 3: Click on 'Wiki Page Link' link");
        wikiMainPage.clickOnWikiListLink();
        Assert.assertTrue(wikiListPage.getWikiPageTitlesList().contains("Page1") && wikiListPage.getWikiPageTitlesList().contains("Page2")
                && wikiListPage.getWikiPageTitlesList().contains("NewPage1"), "Pages are not listed.");

        LOG.info("STEP 3: Click on 'Page1' link");
        wikiListPage.clickPageName("Page1");
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), "Page1");
        Assert.assertTrue(wikiMainPage.getWikiPageContent().equals("This page has been moved here."), "Wiki main page content is not correct!");

        LOG.info("STEP 4: Click on 'here' link");
        wikiMainPage.clickOnHereLink();
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), "NewPage1");

    }

    @TestRail(id = "C5503")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelRenameWikiPage()
    {
        LOG.info("Preconditions: create site and a wiki page");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.createWiki(testUser, password, siteName, "Page1", "Content", null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Page1' title and then on 'Rename' button");
        wikiListPage.clickPageName("Page1");
        wikiMainPage.clickOnRenameWikiMainPageButton();

        LOG.info("STEP 2: Type the new name for the wiki page and click on 'X' button");
        renameWikiMainPage.closeRenamePopup();
        Assert.assertFalse(wikiMainPage.isRenameWikiMainPagePanelDisplayed(), "Pop up should not be diaplyed!");
        Assert.assertEquals(wikiMainPage.getWikiMainPageTitle(), "Page1");
    }
}
