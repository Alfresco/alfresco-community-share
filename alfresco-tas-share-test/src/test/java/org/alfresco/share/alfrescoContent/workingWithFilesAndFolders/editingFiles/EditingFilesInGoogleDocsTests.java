package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EditingFilesInGoogleDocsTests extends ContextAwareWebTest
{
    private String userName;
    private String siteName;
    String googleDocName = DataUtil.getUniqueIdentifier() + "googleDoc.docx";
    String googleDocPath = testDataFolder + googleDocName;
    String uniqueIdentifier = DataUtil.getUniqueIdentifier();

    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private GoogleDocsCommon googleDocsCommon;

    @Autowired
    CreateContent createContent;

    @Autowired UploadContent uploadContent;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userName = "User" + uniqueIdentifier;
        siteName = "SiteName" + uniqueIdentifier;
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
    }

    @TestRail(id = "C7056")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void editFilesInGoogleDocs() throws Exception

    {
        String editedInGoogleDocsTitle = uniqueIdentifier + "editedTestFile.docx";
        String editedInGoogleDocsContent = "Edited";
        
        logger.info("Preconditions: Login to Share/Google Docs and navigate to document library page for the test site");
        googleDocsCommon.loginToGoogleDocs();
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);

        logger.info("Step1: Hover over the test file and click Edit in Google Docs option");
        documentLibraryPage.clickDocumentLibraryItemAction(googleDocName, "Edit in Google Docs™", googleDocsCommon);

        logger.info("Step2: Click OK on the Authorize with Google Docs pop-up message");
        googleDocsCommon.clickOkButtonOnTheAuthPopup();

        logger.info("Step3,4: Provide edited input to Google Docs file and close Google Docs tab");
        getBrowser().waitInSeconds(15);
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedInGoogleDocsTitle, editedInGoogleDocsContent);

        logger.info("Step5: Verify the file is locked and Google Drive icon is displayed");
        documentLibraryPage.renderedPage();
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked icon displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        logger.info("Step6: Click Check In Google Doc™ and verify Version Information pop-up is displayed");
        googleDocsCommon.checkInGoogleDoc(googleDocName);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true, "Version information pop-up displayed");

        logger.info("Step7: Click OK button on Version Information and verify the pop-up is closed");
        googleDocsCommon.clickOkButton();
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false, "Version Information pop-up displayed");

        logger.info("Step8: Verify the title for the document is changed");
        Assert.assertTrue(googleDocsCommon.isDocumentNameUpdated(editedInGoogleDocsTitle), "Name of the document updated");

        logger.info("Steps9, 10: Click on the document title and verify it's preview");
        googleDocsCommon.clickOnUpdatedName(editedInGoogleDocsTitle);
        documentDetailsPage.renderedPage();
        Assert.assertTrue(documentDetailsPage.getContentText().replaceAll("\\s+", "").contains("Edited"),
                String.format("Document: %s has contents.", editedInGoogleDocsTitle));

        getBrowser().cleanUpAuthenticatedSession();

    }

}
