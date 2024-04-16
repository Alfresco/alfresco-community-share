package org.alfresco.share.smartFolders;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentAspects;
import org.alfresco.po.share.SmartFolders;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class UsingSmartFoldersTests extends BaseTest
{
    @Autowired
    GoogleDocsCommon googleDocs;
    @Autowired
    ContentAspects contentAspects;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final String mainSmartFolder = "My content";
    private final String testFileName = "test.pdf";
    private final String newVersionFileName = "EditedTestFile8650.docx";
    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private EditPropertiesDialog editPropertiesDialog;
    private EditPropertiesPage editPropertiesPage;
    private SmartFolders smartFolders;
    private UploadContent uploadContent;
    private String folderName;
    private String testFilePath;
    private String newVersionFilePath;
    private String smartFolderTestFile = "SmartFolderTestData.docx";
    private String filePath = testDataFolder + smartFolderTestFile;
    private String pptxFile = "SmartFolderTest Presentation.pptx";
    private String pptxPath = testDataFolder + pptxFile;
    private String password = "password";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());
        testFilePath = testDataFolder + testFileName;
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), filePath);
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), pptxPath);
        newVersionFilePath = testDataFolder + newVersionFileName;

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        editPropertiesDialog = new EditPropertiesDialog(webDriver);
        editPropertiesPage = new EditPropertiesPage(webDriver);
        smartFolders = new SmartFolders(webDriver);
        uploadContent = new UploadContent(webDriver);

        authenticateUsingLoginPage(userName.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void createFolder()
    {
        folderName = String.format("testFolder%s", RandomData.getRandomAlphanumeric());
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());
        contentAspects.addAspect(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), folderName, CMISUtil.DocumentAspect.SYSTEM_SMART_FOLDER);
        log.info("Step1: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.selectItemAction(folderName, ItemActions.EDIT_PROPERTIES);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");
        log.info("Step2: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        Assert.assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags", "Smart Folder Template"), "Properties should be displayed");
        log.info("Step3: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");
        log.info("Step4: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");
    }

    @TestRail (id = "C8648")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void addFileToSmartFolder()
    {
        log.info("Steps5: Click Upload button and select a pdf file.");
        createFolder();
        uploadContent.uploadContent(testFilePath);
        log.info("Step6: Go to My Content -> All site content -> Documents -> PDF Documents and verify that the uploaded file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("PDF Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("test.pdf"), "The uploaded file displayed in PDF Documents list");
    }

    @TestRail (id = "C8649")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS, "SmartFolders" })
    public void createFileInSmartFolder() throws Exception
    {
        createFolder();
        googleDocs.loginToGoogleDocs();
        log.info("Step5: Press Create button -> Google Docs Document");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);
        googleDocs.clickOkButtonOnTheAuthPopup();
        log.info("Step6: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent("Test", "Test Content");
        log.info("Step7: Hover over document and press Check in Google Doc.");
        googleDocs.checkInGoogleDoc("Untitled Document");
        log.info("Step8: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.docx"), "The uploaded file displayed in Office Documents list");
    }

    @Bug (id = "MNT-18059", status = Bug.Status.FIXED)
    @TestRail (id = "C8650")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS, "SmartFolders" })
    public void updateFileInSmartFolder() throws Exception
    {
        createFolder();
        googleDocs.loginToGoogleDocs();
        log.info("Step5: Press Create button -> Google Docs Document");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);
        googleDocs.clickOkButtonOnTheAuthPopup();
        log.info("Step6: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent("Test", "Test Content");
        log.info("Step7: Hover over document and press Check in Google Doc.");
 //       getBrowser().waitInSeconds(2);
        googleDocs.checkInGoogleDoc("Untitled Document");
        log.info("Step8: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.docx"), "The uploaded file displayed in Office Documents list");
        log.info("Step9: Hover over the created file and click 'Upload new version'");
        documentLibraryPage.selectItemAction("Test.docx", ItemActions.UPLOAD_NEW_VERSION);
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Minor);
  //      getBrowser().waitInSeconds(2);
        log.info("Step10: Verify the document is updated and the version is increased");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFileName), "The uploaded file displayed in Office Documents list");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Content of %s is wrong.", newVersionFileName));
        assertEquals(documentDetailsPage.getFileVersion(), "1.1", String.format("Version of %s is wrong.", newVersionFileName));
    }

    @TestRail (id = "C8663")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void checkSmartFolderStructure()
    {
        createFolder();
        log.info("Step5: Click on 'My content' the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(mainSmartFolder);
        List<String> expectedContentList = Arrays.asList("All site content", "Contributions", "My content modified by other users", "User home",
            "Tagged 'Confidential'", "My Categorized Files", "Recent updates");
        for (String expectedContent : expectedContentList)
            Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(expectedContent), expectedContent + " folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(8), "SF icon displayed for all folders");
    }

    @TestRail (id = "C8664")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void checkFilesAreCorrectlyFilled()
    {
        createFolder();
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFolderName(folderName);
        log.info("Step5: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(pptxFile), "The uploaded file displayed in Office Documents list");
    }

    @TestRail (id = "C8647")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void checkAvailableActions()
    {
        createFolder();
        log.info("Step5: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(smartFolderTestFile), "The uploaded file displayed in Office Documents list");
        log.info("Step6: Hover over the created document and check available actions");
        List<String> expectedActions = Arrays.asList("Download", "View In Browser", "Edit Properties", "Upload New Version", "Edit in Microsoft Office™",
            "Edit Offline", "Start Workflow");
        List<String> notExpectedActions = Arrays.asList("Delete Document", "Unzip to...", "Sync to Cloud", "Locate File", "Move to...", "Copy to...",
            "Manage permissions");
        Assert.assertTrue(documentLibraryPage.areActionsAvailableForLibraryItem(smartFolderTestFile, expectedActions), "Expected actions");
        Assert.assertTrue(documentLibraryPage.areActionsNotAvailableForLibraryItem(smartFolderTestFile, notExpectedActions), "Not expected actions");
    }
}
