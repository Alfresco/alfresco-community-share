package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
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
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private CreateContent create;
    @Autowired
    private GoogleDocsCommon googleDocs;

    @BeforeClass(alwaysRun = true)
    public void createUserAndSite()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, "Site used for Google Docs", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C6990")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void createGoogleDocsDocument() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");

        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'");
        documentLibraryPage.clickCreateButton();
        create.clickGoogleDocsDoc();

        LOG.info("Step 2: Click Ok button on the Authorize with Google Docs pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(documentTitle, content);
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Document"), "The file created with Google Docs present");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("Untitled Document"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");
        googleDocs.checkInGoogleDoc("Untitled Document");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(documentTitle), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

    }

    @TestRail(id = "C6991")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void createGoogleDocsSpreadsheet() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        documentLibraryPage.clickCreateButton();
        create.clickGoogleDocsSpreadsheet();

        LOG.info("Step 2: Click Ok button on the 'Authorize with Google Docs' pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(spreadsheetTitle, content);
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Spreadsheet"), "The file created with Google Docs present");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("Untitled Spreadsheet"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(spreadsheetTitle), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");
    }

    @TestRail(id = "C6992")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void createGoogleDocsPresentation() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        documentLibraryPage.clickCreateButton();
        create.clickGoogleDocsPresentation();

        LOG.info("Step 2: Click Ok button on the Authorize with Google Docs pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGooglePresentationsAndEditContent(presentationTitle);
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Presentation"), "The file created with Google Docs present");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("Untitled Presentation"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");

        googleDocs.checkInGoogleDoc("Untitled Presentation");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(presentationTitle), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");
    }
}
