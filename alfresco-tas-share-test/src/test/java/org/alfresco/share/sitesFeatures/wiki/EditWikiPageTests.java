package org.alfresco.share.sitesFeatures.wiki;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.wiki.*;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iulia.cojocea
 */

public class EditWikiPageTests extends ContextAwareWebTest
{

    @Autowired
    WikiMainPage wikiMainPage;

    @Autowired
    EditWikiPage editWikiPage;

    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    CreateWikiPage createWikiPage;

    @Autowired
    SelectDocumentPopupPage selectDoc;

    @Autowired
    WikiPage wikiPage;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName;
    private String wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());
    private String wikiNewContent = "New content";
    private String wikiInitialContent = "Initial content";
    private String tagName = String.format("tag%s", RandomData.getRandomAlphanumeric()).toLowerCase();
    private List<String> tags = new ArrayList<>();
    private final String image = "newavatar.jpg";
    private final String siteNameC5545 = String.format("siteNameC5545%s", RandomData.getRandomAlphanumeric());

    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, testUser);
        siteService.create(testUser, password, domain, siteNameC5545, siteNameC5545, SiteService.Visibility.PUBLIC);
        contentService.uploadFileInSite(testUser, password, siteNameC5545, testDataFolder + image);

        setupAuthenticatedSession(testUser, password);
        tags.add(tagName);
    }

    @TestRail(id = "C5542")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editWikiPageFromPageView()
    {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: create site and wiki page");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageTitle, wikiInitialContent, null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Edit' wiki page link");
        wikiListPage.clickPageName(wikiPageTitle);
        wikiMainPage.clickEditPageLink();

        String currentContent = editWikiPage.getWikiPageContent();
        Assert.assertEquals(currentContent, wikiInitialContent, "Wrong wiki content!, expected " + wikiInitialContent + " but found " + currentContent);

        LOG.info("STEP 2: Clear wiki page content and add another one and a tag ");
        editWikiPage.clearWikiPageContent();
        editWikiPage.addTag(tagName);
        editWikiPage.saveWikiContent(wikiNewContent);
        Assert.assertEquals(wikiMainPage.getWikiPageContent().trim(), wikiNewContent, "Wrong wiki content!, expected " + wikiNewContent + " but found "
                + wikiMainPage.getWikiPageContent());

        LOG.info("STEP 3: Click on wiki page list link");
        wikiMainPage.clickOnWikiListLink();
        Assert.assertTrue(wikiListPage.getTagsList().contains(tagName + " (1)"), "Tag is not displayed in the list!");
    }

    @TestRail(id = "C5543")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editWikiPageFromWikiPageList()
    {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: create site and wiki page");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageTitle, "Content", tags);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Edit' wiki page");
        wikiListPage.clickEdit(wikiPageTitle);
        Assert.assertTrue(createWikiPage.getWikiPageTagsList().contains(tagName), "Tag is not displayed!");
        Assert.assertTrue(editWikiPage.getWikiPageContent().equals("Content"), "Wrong wiki page content!");

        LOG.info("STEP 2: Remove tag");
        editWikiPage.removeTag(tagName);
        Assert.assertFalse(createWikiPage.getWikiPageTagsList().contains(tagName), "Tag is not displayed!");

        LOG.info("STEP 3: Save changes");
        editWikiPage.clickOnSaveButton();

        LOG.info("STEP 4: Navigate to 'Wiki Page List'");
        wikiMainPage.clickOnWikiListLink();
        Assert.assertFalse(wikiListPage.getTagsList().contains(tagName + " (1)"), "Tag is displayed in the list!");
    }

    @TestRail(id = "C5544")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditingWikiPage()
    {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: create site and wiki page");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageTitle, "Content", null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Edit' wiki page");
        wikiListPage.clickEdit(wikiPageTitle);

        LOG.info("STEP 2: Add some content in the text box");
        editWikiPage.cancelWikiContent("New content");
        Assert.assertTrue(wikiPage.getWikiPageContent().equals("Content"), "Wrong wiki page content!");
    }

    @TestRail(id = "C5545")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyInsertLibraryImageFeature()
    {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: create site and wiki page, upload image in document library");
        siteService.addPageToSite(testUser, password, siteNameC5545, Page.WIKI, null);
        sitePagesService.createWiki(testUser, password, siteNameC5545, wikiPageTitle, "Content", null);
        wikiListPage.navigate(siteNameC5545);

        LOG.info("STEP 1: Click on edit wiki page");
        wikiListPage.clickEdit(wikiPageTitle);

        LOG.info("STEP 2: Click 'Insert Library Image' button");
        editWikiPage.clickInsertLibraryImage();
        Assert.assertTrue(editWikiPage.islibraryImagesTitlebarDisplayed(), "Missing library images title bar!");
        Assert.assertTrue(editWikiPage.isImageDisplayed(image), "Missing image thumbnail.");

        LOG.info("STEP 3: Click the image thumbnail");
        editWikiPage.clickOnImage(image);
        Assert.assertTrue(editWikiPage.getIframeSourceCode().contains(image), "Image is missing!");

        LOG.info("STEP 4: Click on 'Save' button");
        editWikiPage.clickOnSaveButton();
        Assert.assertTrue(wikiMainPage.isImageDisplayed(image), "Image is displayed.");
    }

    @TestRail(id = "C5546")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyInsertDocumentLibraryFeature()
    {
        wikiPageTitle = String.format("WikiPage%s", RandomData.getRandomAlphanumeric());
        String docName = "testDoc.txt";

        LOG.info("Preconditions: create site and wiki page, upload document in document library");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageTitle, "Content", null);
        contentService.uploadFileInSite(testUser, password, siteName, testDataFolder + docName);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Edit' wiki page");
        wikiListPage.clickEdit(wikiPageTitle);

        LOG.info("STEP 2: Click 'Insert Document Link' button");
        editWikiPage.clickInsertDocumentLink();

        LOG.info("STEP 3: Add document");
        selectDoc.clickAddIcon("testDoc.txt");
        Assert.assertTrue(selectDoc.getSelectedDocumentTitlesList().contains(docName), "Document is not present in the list!");

        LOG.info("STEP 4: Click on 'OK' button");
        selectDoc.clickOkButton();
        Assert.assertTrue(editWikiPage.getWikiPageContent().contains(docName), "Document is not present!");

        LOG.info("STEP 4: Click on 'Save' button");
        editWikiPage.clickOnSaveButton();
        wikiPage.clickOnDocLink(docName);
    }
}
