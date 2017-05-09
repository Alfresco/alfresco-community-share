package org.alfresco.share.smartFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.SmartFolders;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class UsingSmartFoldersTests extends ContextAwareWebTest
{
    private final String userName = String.format("User%s", DataUtil.getUniqueIdentifier());
    private final String siteName = String.format("SiteName%s", DataUtil.getUniqueIdentifier());
    private final String mainSmartFolder = "My content";
    private final String testFileName = "test.pdf";
    private final String newVersionFileName = "EditedTestFile8650.docx";
    @Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @Autowired
    EditPropertiesDialog editPropertiesDialog;
    @Autowired
    EditPropertiesPage editPropertiesPage;
    @Autowired
    SmartFolders smartFolders;
    @Autowired
    UploadContent uploadContent;
    @Autowired
    GoogleDocsCommon googleDocs;
    @Autowired
    CreateContent createContent;
    private String folderName;
    private String testFilePath;
    private String newVersionFilePath;

    @BeforeClass(alwaysRun = true)
    public void createUserAndSite()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        testFilePath = testDataFolder + testFileName;
        newVersionFilePath = testDataFolder + newVersionFileName;
        setupAuthenticatedSession(userName, password);
        googleDocs.loginToGoogleDocs();
    }

    @BeforeMethod(alwaysRun = true)
    public void createFolder()
    {
        folderName = String.format("testFolder%s", DataUtil.getUniqueIdentifier());
        contentService.createFolder(userName, password, folderName, siteName);
        contentAspects.addAspect(userName, password, siteName, folderName, CMISUtil.DocumentAspect.SYSTEM_SMART_FOLDER);

        LOG.info("Step1: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        LOG.info("Step2: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        Assert.assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags", "Smart Folder Template"), "Properties should be displayed");

        LOG.info("Step3: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        LOG.info("Step4: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");
    }

    @TestRail(id = "C8648")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addFileToSmartFolder()
    {
        LOG.info("Steps5: Click Upload button and select a pdf file.");
        uploadContent.uploadContent(testFilePath);

        LOG.info("Step6: Go to My Content -> All site content -> Documents -> PDF Documents and verify that the uploaded file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("PDF Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("test.pdf"), "The uploaded file displayed in PDF Documents list");
    }

    @TestRail(id = "C8649")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void createFileInSmartFolder() throws Exception
    {
        LOG.info("Step5: Press Create button -> Google Docs Document");
        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        getBrowser().waitInSeconds(1);
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step6: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent("Test", "Test Content");

        LOG.info("Step7: Hover over document and press Check in Google Doc.");
        googleDocs.checkInGoogleDoc("Untitled Document");

        LOG.info("Step8: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.docx"), "The uploaded file displayed in Office Documents list");
    }

    @TestRail(id = "C8650")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void updateFileInSmartFolder() throws Exception
    {
        LOG.info("Step5: Press Create button -> Google Docs Document");
        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        getBrowser().waitInSeconds(1);
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step6: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent("Test", "Test Content");

        LOG.info("Step7: Hover over document and press Check in Google Doc.");
        getBrowser().waitInSeconds(2);
        googleDocs.checkInGoogleDoc("Untitled Document");

        LOG.info("Step8: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.docx"), "The uploaded file displayed in Office Documents list");

        LOG.info("Step9: Hover over the created file and click 'Upload new version'");
        documentLibraryPage.clickDocumentLibraryItemAction("Test.docx", "Upload New Version", uploadContent);
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Minor);
        getBrowser().waitInSeconds(2);

        LOG.info("Step10: Verify the document is updated and the version is increased");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFileName), "The uploaded file displayed in Office Documents list");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Content of %s is wrong.", newVersionFileName));
        assertEquals(documentDetailsPage.getFileVersion(), "1.1", String.format("Version of %s is wrong.", newVersionFileName));

    }

    @TestRail(id = "C8663")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkSmartFolderStructure()
    {
        LOG.info("Step5: Click on 'My content' the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(mainSmartFolder);
        List<String> expectedContentList = Arrays.asList("All site content", "Contributions", "My content modified by other users", "User home",
                "Tagged 'Confidential'", "My Categorized Files", "Recent updates");
        for (String expectedContent : expectedContentList)
            Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(expectedContent), expectedContent + " folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(8), "SF icon displayed for all folders");
    }

    @TestRail(id = "C8664")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void checkFilesAreCorrectlyFilled() throws Exception
    {
        LOG.info("Step5: Press Create button -> Google Docs Document");

        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsSpreadsheet();
        getBrowser().waitInSeconds(1);
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step6: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent("Test", "Test Content");

        LOG.info("Step7: Hover over document and press Check in Google Doc.");
        documentLibraryPage.renderedPage();
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet.xlsx");

        LOG.info("Step8: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.xlsx"), "The uploaded file displayed in Office Documents list");

    }

    @TestRail(id = "C8647")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void checkAvailableActions() throws Exception
    {
        LOG.info("Step5: Verify that 'Create' button is available");
        Assert.assertTrue(documentLibraryPage.isCreateButtonDisplayed(), "Create button displayed");

        LOG.info("Step6: Press Create button -> Google Docs Document");
        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        getBrowser().waitInSeconds(1);
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step7: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent("Test", "Test Content");

        LOG.info("Step8: Hover over document and press Check in Google Doc.");
        getBrowser().waitInSeconds(2);
        googleDocs.checkInGoogleDoc("Untitled Document");

        LOG.info("Step9: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.docx"), "The uploaded file displayed in Office Documents list");

        LOG.info("Step10: Hover over the created document and check available actions");
        List<String> expectedActions = Arrays.asList("Download", "View In Browser", "Edit Properties", "Upload New Version", "Edit in Microsoft Office™",
                "Edit Offline", "Start Workflow");
        List<String> notExpectedActions = Arrays.asList("Delete Document", "Unzip to...", "Sync to Cloud", "Locate File", "Move to...", "Copy to...",
                "Manage permissions");
        Assert.assertTrue(documentLibraryPage.areActionsAvailableForLibraryItem("Test.docx", expectedActions), "Expected actions");
        Assert.assertTrue(documentLibraryPage.areActionsNotAvailableForLibraryItem("Test.docx", notExpectedActions), "Not expected actions");
    }
}
