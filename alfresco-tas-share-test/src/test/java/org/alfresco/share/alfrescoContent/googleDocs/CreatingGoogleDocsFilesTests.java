package org.alfresco.share.alfrescoContent.googleDocs;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreatingGoogleDocsFilesTests extends ContextAwareWebTest
{
    private final String user = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("testSiteGoogleDocs%s", RandomData.getRandomAlphanumeric());
    private final String content = "testcontent";
    private final String documentTitle = "testDocument";
    private final String spreadsheetTitle = "testSpreadsheet";
    private final String presentationTitle = "testPresentation";
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private GoogleDocsCommon googleDocs;

    @BeforeClass (alwaysRun = true)
    public void createUserAndSite()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, "Site used for Google Docs", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6990")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void createGoogleDocsDocument() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");

        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);

        LOG.info("Step 2: Click Ok button on the Authorize with Google Docs pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(documentTitle, content);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Document"), "The file created with Google Docs present");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("Untitled Document"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");
        googleDocs.checkInGoogleDoc("Untitled Document");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(documentTitle), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

    }

    @TestRail (id = "C6991")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void createGoogleDocsSpreadsheet() throws Exception
    {
        //      googleDocs.loginToGoogleDocs();
        LOG.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_SPREADSHEET);

        LOG.info("Step 2: Click Ok button on the 'Authorize with Google Docs' pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(spreadsheetTitle, content);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Spreadsheet"), "The file created with Google Docs present");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("Untitled Spreadsheet"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(spreadsheetTitle), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");
    }

    @TestRail (id = "C6992")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void createGoogleDocsPresentation() throws Exception
    {
        //       googleDocs.loginToGoogleDocs();
        LOG.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_PRESENTATION);

        LOG.info("Step 2: Click Ok button on the Authorize with Google Docs pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGooglePresentationsAndEditContent(presentationTitle);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Presentation"), "The file created with Google Docs present");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("Untitled Presentation"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");

        googleDocs.checkInGoogleDoc("Untitled Presentation");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(presentationTitle), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");
    }
}
