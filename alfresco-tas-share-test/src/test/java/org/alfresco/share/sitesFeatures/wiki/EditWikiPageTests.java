package org.alfresco.share.sitesFeatures.wiki;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.wiki.CreateWikiPage;
import org.alfresco.po.share.site.wiki.EditWikiPage;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

    private List<Page> pagesToAdd = new ArrayList<Page>();
    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName;
    private String wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();
    private String wikiNewContent = "New content";
    private String wikiInitialContent = "Initial content";
    private String tagName = "tag" + DataUtil.getUniqueIdentifier();
    private List<String> tags = new ArrayList<String>();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(Page.WIKI);
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, testUser);
        setupAuthenticatedSession(testUser, password);
        tags.add(tagName);
    }

    @TestRail(id = "C5542")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void editWikiPageFromPageView()
    {
        wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions: create site and wiki page");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageTitle, "Initial content", null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Edit' wiki page link");
        wikiListPage.clickPageName(wikiPageTitle);
        wikiMainPage.clickEditPageLink();

        String curentContent = editWikiPage.getWikiPageContent();
        Assert.assertEquals(curentContent, wikiInitialContent, "Wrong wiki content!, expected " + wikiInitialContent + " but found " + curentContent);

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
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void editWikiPageFromWikiPageList()
    {
        wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions: create site and wiki page");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
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
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelEditingWikiPage()
    {
        wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions: create site and wiki page");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageTitle, "Content", null);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on 'Edit' wiki page");
        wikiListPage.clickEdit(wikiPageTitle);

        LOG.info("STEP 2: Add some content in the text box");
        editWikiPage.cancelWikiContent("New content");
        Assert.assertTrue(wikiPage.getWikiPageContent().equals("Content"), "Wrong wiki page content!");
    }

    @TestRail(id = "C5545")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyInsertLibraryImageFeature()
    {
        wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();
        String image = "newavatar.jpg";

        LOG.info("Preconditions: create site and wiki page, upload image in document library");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        sitePagesService.createWiki(testUser, password, siteName, wikiPageTitle, "Content", null);
        contentService.uploadFileInSite(testUser, password, siteName, testDataFolder + image);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on edit wiki page");
        wikiListPage.clickEdit(wikiPageTitle);

        LOG.info("STEP 2: Click 'Insert Library Image' button");
        editWikiPage.clickInsertLibraryImage();
        int i = 0;
        while (editWikiPage.existsElement(image) != true && i < 3)
        {
            editWikiPage.clickInsertLibraryImage();
            i++;
        }

        Assert.assertTrue(editWikiPage.islibraryImagesTitlebarDisplayed(), "Missing library images title bar!");
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(editWikiPage.isImageDisplayed(image), "Missing image thumbnail.");

        LOG.info("STEP 3: Click the image thumbnail");
        editWikiPage.clickOnImage(image);
        Assert.assertTrue(editWikiPage.getIframeSourceCode().contains(image), "Image is missing!");

        LOG.info("STEP 4: Click on 'Save' button");
        editWikiPage.clickOnSaveButton();
        Assert.assertTrue(wikiMainPage.isImageDisplayed(image), "Image is displayed.");
    }

    @TestRail(id = "C5546")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyInsertDocumentLibraryFeature()
    {
        wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();
        String docName = "testDoc.txt";

        LOG.info("Preconditions: create site and wiki page, upload document in document library");
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
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
