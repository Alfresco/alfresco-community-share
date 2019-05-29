package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UpdateFileWithContentFromComputerTests extends ContextAwareWebTest
{

    @Autowired
    EditPropertiesPage editPropertiesPage;
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private UploadFileDialog uploadFileDialog;
    @Autowired
    private UploadContent uploadContent;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String testFileName = "testFileC7074.txt";
    private String fileContent = "Test Content C7074";
    private String newVersionFileName = "EditedTestFileC7074.txt";
    private String newVersionFilePath = testDataFolder + newVersionFileName;

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, testFileName, fileContent);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @Bug (id = "MNT-18059", status = Bug.Status.FIXED)
    @TestRail (id = "C7074")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void uploadFileUsingUploadNewVersion()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Click 'Upload new version' action for the test file");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Upload New Version", uploadFileDialog);
        LOG.info("Step2 - Click on 'Select files to upload' button, browse to the new version of the test file and select it. Click 'Upload' button.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        LOG.info("Step3 - Check the new title of the file displayed in Document Library.");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFileName), String.format("The file [%s] is not present", newVersionFileName));
        LOG.info("Steps4,5: Click on the file and check the version and content are updated.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "Edited content C7074", String.format("Contents of %s are wrong.", newVersionFileName));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFileName));
    }
}
