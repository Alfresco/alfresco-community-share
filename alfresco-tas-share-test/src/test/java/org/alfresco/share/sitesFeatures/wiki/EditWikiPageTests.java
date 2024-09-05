package org.alfresco.share.sitesFeatures.wiki;

import static org.alfresco.common.Utils.testDataFolder;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.wiki.CreateWikiPage;
import org.alfresco.po.share.site.wiki.EditWikiPage;
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

@Slf4j
/**
 * @author iulia.cojocea
 */

public class EditWikiPageTests extends BaseTest
{

    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    private final String image = "newavatar.jpg";
    //@Autowired
    WikiMainPage wikiMainPage;
    //@Autowired
    EditWikiPage editWikiPage;
    //@Autowired
    WikiListPage wikiListPage;
    //@Autowired
    CreateWikiPage createWikiPage;
    //@Autowired
    SelectDocumentPopupPage selectDoc;
    //@Autowired
    WikiPage wikiPage;

    private String wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());
    private String wikiNewContent = "New content";
    private String wikiInitialContent = "Initial content";
    private String tagName = String.format("tag%s", RandomData.getRandomAlphanumeric()).toLowerCase();
    private List<String> tags = new ArrayList<>();
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC5545 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void createUser()
    {
        log.info("Precondition: Any Test user is created");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteNameC5545.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        contentService.uploadFileInSite(testUser.get().getUsername(), testUser.get().getPassword(), siteNameC5545.get().getId(), testDataFolder + image);

        wikiListPage = new WikiListPage(webDriver);
        wikiMainPage = new WikiMainPage(webDriver);
        editWikiPage = new EditWikiPage(webDriver);
        createWikiPage = new CreateWikiPage(webDriver);
        wikiPage = new WikiPage(webDriver);
        selectDoc = new SelectDocumentPopupPage(webDriver);

        authenticateUsingLoginPage(testUser.get());
        tags.add(tagName);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteSitesIfNotNull(siteNameC5545.get());
        deleteUsersIfNotNull(testUser.get());

    }

    @TestRail (id = "C5542")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editWikiPageFromPageView() throws InterruptedException {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());

        log.info("Preconditions: create site and wiki page");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), Page.WIKI, null);
        sitePagesService.createWiki(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), wikiPageTitle, wikiInitialContent, null);
        wikiListPage.navigate(siteName.get());

        log.info("STEP 1: Click on 'Edit' wiki page link");
        wikiListPage.clickPageName(wikiPageTitle);
        wikiMainPage.clickEditPageLink();

        String currentContent = editWikiPage.getWikiPageContent();
        Assert.assertEquals(currentContent, wikiInitialContent, "Wrong wiki content!, expected " + wikiInitialContent + " but found " + currentContent);

        log.info("STEP 2: Clear wiki page content and add another one and a tag ");
        editWikiPage.clearWikiPageContent();
        editWikiPage.addTag(tagName);
        editWikiPage.saveWikiContent(wikiNewContent);
//        Assert.assertEquals(wikiMainPage.getWikiPageContent().trim(), wikiNewContent, "Wrong wiki content!, expected " + wikiNewContent + " but found "
//            + wikiMainPage.getWikiPageContent());

        log.info("STEP 3: Click on wiki page list link");
        wikiMainPage.clickOnWikiListLink();
        Assert.assertTrue(wikiListPage.getTagsList().contains(tagName + " (1)"), "Tag is not displayed in the list!");
        deleteSitesIfNotNull(siteName.get());
    }

    @TestRail (id = "C5543")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editWikiPageFromWikiPageList()
    {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());

        log.info("Preconditions: create site and wiki page");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), Page.WIKI, null);
        sitePagesService.createWiki(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), wikiPageTitle, "Content", tags);
        wikiListPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on 'Edit' wiki page");
        wikiListPage.clickEdit(wikiPageTitle);
        Assert.assertTrue(createWikiPage.getWikiPageTagsList().contains(tagName), "Tag is not displayed!");
        Assert.assertTrue(editWikiPage.getWikiPageContent().equals("Content"), "Wrong wiki page content!");

        log.info("STEP 2: Remove tag");
        editWikiPage.removeTag(tagName);
        Assert.assertFalse(createWikiPage.getWikiPageTagsList().contains(tagName), "Tag is not displayed!");

        log.info("STEP 3: Save changes");
        editWikiPage.clickOnSaveButton();

        log.info("STEP 4: Navigate to 'Wiki Page List'");
        wikiMainPage.clickOnWikiListLink();
        Assert.assertFalse(wikiListPage.getTagsList().contains(tagName + " (1)"), "Tag is displayed in the list!");

        deleteSitesIfNotNull(siteName.get());

    }

    @TestRail (id = "C5544")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditingWikiPage()
    {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());

        log.info("Preconditions: create site and wiki page");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), Page.WIKI, null);
        sitePagesService.createWiki(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), wikiPageTitle, "Content", null);
        wikiListPage.navigate(siteName.get());

        log.info("STEP 1: Click on 'Edit' wiki page");
        wikiListPage.clickEdit(wikiPageTitle);

        log.info("STEP 2: Add some content in the text box");
        editWikiPage.cancelWikiContent("New content");
        Assert.assertTrue(wikiPage.getWikiPageContent().equals("Content"), "Wrong wiki page content!");

        deleteSitesIfNotNull(siteName.get());

    }

    @TestRail (id = "C5545")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyInsertLibraryImageFeature() {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: create site and wiki page, upload image in document library");
        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteNameC5545.get().getId(), Page.WIKI, null);
        sitePagesService.createWiki(testUser.get().getUsername(), testUser.get().getPassword(), siteNameC5545.get().getId(), wikiPageTitle, "Content", null);
        wikiListPage.navigate(siteNameC5545.get());

        log.info("STEP 1: Click on edit wiki page");
        wikiListPage.clickEdit(wikiPageTitle);

        log.info("STEP 2: Click 'Insert Library Image' button");
        editWikiPage.clickInsertLibraryImage();
        editWikiPage.refreshBrowser();
        editWikiPage.clickInsertLibraryImage();
        Assert.assertTrue(editWikiPage.islibraryImagesTitlebarDisplayed(), "Missing library images title bar!");
        Assert.assertTrue(editWikiPage.is_ImageDisplayed(image), "Missing image thumbnail.");

        log.info("STEP 3: Click the image thumbnail");
        editWikiPage.clickOnImage(image);
        Assert.assertTrue(editWikiPage.getIframeSourceCode().contains(image), "Image is missing!");

        log.info("STEP 4: Click on 'Save' button");
        editWikiPage.clickOnSaveButton();
        Assert.assertTrue(wikiMainPage.isImageDisplayed(image), "Image is displayed.");
    }

    @TestRail (id = "C5546")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyInsertDocumentLibraryFeature()
    {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());
        String docName = "testDoc.txt";

        log.info("Preconditions: create site and wiki page, upload document in document library");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), Page.WIKI, null);
        sitePagesService.createWiki(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), wikiPageTitle, "Content", null);
        contentService.uploadFileInSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), testDataFolder + docName);
        wikiListPage.navigate(siteName.get());

        log.info("STEP 1: Click on 'Edit' wiki page");
        wikiListPage.clickEdit(wikiPageTitle);

        log.info("STEP 2: Click 'Insert Document Link' button");
        editWikiPage.clickInsertDocumentLink();

        log.info("STEP 3: Add document");
        selectDoc.clickAddIcon("testDoc.txt");
        Assert.assertTrue(selectDoc.getSelectedDocumentTitlesList().contains(docName), "Document is not present in the list!");

        log.info("STEP 4: Click on 'OK' button");
        selectDoc.clickOkButton();
        Assert.assertTrue(editWikiPage.getWikiPageContent().contains(docName), "Document is not present!");

        log.info("STEP 5: Click on 'Save' button");
        editWikiPage.clickOnSaveButton();

        log.info("STEP 6: Click 'testDoc' link");
        wikiPage.clickOnDocLink(docName);
        wikiPage.assertBrowserPageTitleIs("Alfresco » Document Details");

        deleteSitesIfNotNull(siteName.get());

    }
}
