package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreatingGoogleDocsFilesTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private CreateContent create;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired private GoogleDocsCommon googleDocs;

    private String user;
    private String siteName;
    private String documentTitle;
    private String spreadsheetTitle;
    private String presentationTitle;
    private String content;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {

        user = "testUser" + DataUtil.getUniqueIdentifier();
        siteName = "testSiteGoogleDocs" + DataUtil.getUniqueIdentifier();
        documentTitle = "testDocument";
        spreadsheetTitle = "testSpreadsheet";
        presentationTitle = "testPresentation";
        content = "testcontent";
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, "Site used for Google Docs", Visibility.PUBLIC);

    }

    @TestRail(id = "C6990")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createGoogleDocsDocument() throws Exception
    {
        logger.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");
        googleDocs.loginToGoogleDocs();
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);

        logger.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'");
        documentLibraryPage.clickCreateButton();
        create.clickGoogleDocsDoc();

        logger.info("Step 2: Click Ok button on the Authorize with Google Docs pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        logger.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(documentTitle, content);
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Document"), "The file created with Google Docs present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        logger.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");
        googleDocs.checkInGoogleDoc("Untitled Document");
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false, "Locked label displayed");
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false, "Google Drive icon displayed");

        cleanupAuthenticatedSession();

    }

    @TestRail(id = "C6991")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createGoogleDocsSpreadsheet() throws Exception
    {
        logger.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");
        googleDocs.loginToGoogleDocs();
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);

        logger.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        documentLibraryPage.clickCreateButton();
        create.clickGoogleDocsSpreadsheet();

        logger.info("Step 2: Click Ok button on the 'Authorize with Google Docs' pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        logger.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(spreadsheetTitle, content);
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Spreadsheet"), "The file created with Google Docs present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        logger.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false, "Locked label displayed");
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false, "Google Drive icon displayed");

        cleanupAuthenticatedSession();

    }

    @TestRail(id = "C6992")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createGoogleDocsPresentation() throws Exception

    {
        logger.info("Preconditions: Login to Share/Google Docs and navigate to test site's Document Library page");
        googleDocs.loginToGoogleDocs();
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);

        logger.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        documentLibraryPage.clickCreateButton();
        create.clickGoogleDocsPresentation();

        logger.info("Step 2: Click Ok button on the Authorize with Google Docs pop-up");
        googleDocs.clickOkButtonOnTheAuthPopup();

        logger.info("Step 3: Edit the document in the Google Docs tab (enter some text).");
        googleDocs.switchToGooglePresentationsAndEditContent(presentationTitle);
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Presentation"), "The file created with Google Docs present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        logger.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore");

        googleDocs.checkInGoogleDoc("Untitled Presentation");
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false, "Locked label displayed");
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false, "Google Drive icon displayed");

        cleanupAuthenticatedSession();

    }
}
