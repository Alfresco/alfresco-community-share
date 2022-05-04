package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import static org.alfresco.common.Utils.testDataFolder;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EditingFilesInGoogleDocsTests extends ContextAwareWebTest
{
    String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
    String googleDocPath = testDataFolder + googleDocName;
    String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    //@Autowired
    UploadContent uploadContent;
    private String userName;
    private String siteName;
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private GoogleDocsCommon googleDocsCommon;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userName = "User" + uniqueIdentifier;
        siteName = "SiteName" + uniqueIdentifier;
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7056")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled=false)
    public void editFilesInGoogleDocs() throws Exception

    {
        String editedInGoogleDocsTitle = uniqueIdentifier + "editedTestFile.docx";
        String editedInGoogleDocsContent = "Edited";

        LOG.info("Preconditions: Login to Share/Google Docs and navigate to document library page for the test site");
        googleDocsCommon.loginToGoogleDocs();
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);

        LOG.info("Step1: Hover over the test file and click Edit in Google Docs option");
        documentLibraryPage.selectItemAction(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS);

        LOG.info("Step2: Click OK on the Authorize with Google Docs pop-up message");
        googleDocsCommon.clickOkButtonOnTheAuthPopup();

        LOG.info("Step3,4: Provide edited input to Google Docs file and close Google Docs tab");
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedInGoogleDocsTitle, editedInGoogleDocsContent);

        LOG.info("Step5: Verify the file is locked and Google Drive icon is displayed");
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked icon displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        LOG.info("Step6: Click Check In Google Doc™ and verify Version Information pop-up is displayed");
        googleDocsCommon.checkInGoogleDoc(googleDocName);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true, "Version information pop-up displayed");

        LOG.info("Step7: Click OK button on Version Information and verify the pop-up is closed");
        googleDocsCommon.clickOkButton();
        Assert.assertFalse(googleDocsCommon.isVersionInformationPopupDisplayed(), "Version Information pop-up displayed");

        LOG.info("Step8: Verify the title for the document is changed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedInGoogleDocsTitle), "Name of the document updated");

        LOG.info("Steps9, 10: Click on the document title and verify it's preview");
        documentLibraryPage.clickOnFile(editedInGoogleDocsTitle);
        Assert.assertTrue(documentDetailsPage.getContentText().replaceAll("\\s+", "").contains("Edited"),
            String.format("Document: %s has contents.", editedInGoogleDocsTitle));
    }

}
