package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EditingFilesInGoogleDocsTests extends ContextAwareWebTest
{
    private String userName;
    private String siteName;
    private String fileName;
    private String fileContent;
    private String editedInGoogleDocsTitle;
    private String editedInGoogleDocsContent;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    GoogleDocsCommon googleDocsCommon;

    @Autowired
    CreateContent createContent;

    @BeforeClass
    public void setupTest()

    {
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        fileName = "testFile";
        fileContent = "TestContent";
        editedInGoogleDocsTitle = "editedTestFile";
        editedInGoogleDocsContent = "edited";
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, DocumentType.MSWORD, fileName, fileContent);
    }

    @TestRail(id = "C7056")
    @Test()
    public void editFilesInGoogleDocs() throws Exception

    {
        logger.info("Preconditions: Login to Share/Google Docs and Navigate to document library page for the test site");
        googleDocsCommon.loginToGoogleDocs();
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Hover over the test file and click Edit in Google Docs option");
        documentLibraryPage.mouseOverFileName(fileName);
        googleDocsCommon.editInGoogleDocs();

        logger.info("Step2: Click OK on the Authorize with Google Docs pop-up message");
        googleDocsCommon.clickOkButton();

        logger.info("Step3,4: Provide edited input to Google Docs file and close Google Docs tab");
        googleDocsCommon.confirmFormatUpgrade();
        getBrowser().waitInSeconds(10);
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedInGoogleDocsTitle, editedInGoogleDocsContent);

        logger.info("Step5: Verify the file is locked and Google Drive icon is displayed");
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        logger.info("Step6: Click Check In Google Doc™ and verify Version Information pop-up is displayed");
        googleDocsCommon.checkInGoogleDoc(fileName);
        getBrowser().waitInSeconds(5);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true);

        logger.info("Step7: Click OK button on Version Information and verify the pop-up is closed");
        googleDocsCommon.clickOkButton();
        getBrowser().waitInSeconds(5);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false);

        logger.info("Step8: Verify the title for the document is changed");
        Assert.assertTrue(googleDocsCommon.isDocumentNameUpdated(editedInGoogleDocsTitle), "Name of the document was not updated");

        logger.info("Steps9, 10: Click on the document title and verify it's preview");
        googleDocsCommon.clickOnUpdatedName(editedInGoogleDocsTitle);
        Assert.assertTrue(documentDetailsPage.getContentText().replaceAll("\\s+", "").contains("editedTestContent"),
                String.format("Document: %s has incorrect contents.", editedInGoogleDocsTitle));

        getBrowser().cleanUpAuthenticatedSession();

    }

}
