package org.alfresco.share.userRolesAndPermissions.contributor;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;
import static org.alfresco.utility.web.AbstractWebTest.getBrowser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.alfresco.utility.report.Bug.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ContributorFilesCreatedBySelfTests extends BaseTest
{
    @Autowired
    UserService userService;
    @Autowired
    ContentActions contentAction;
    @Autowired
    GoogleDocsCommon docs;
    private final ThreadLocal<UserModel> userContributor = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final String fileWithVersions = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
    private final String fileWithVersionsForRevert = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
    private final String textFile = String.format("ContributorFile%s", RandomData.getRandomAlphanumeric());
    private final String toBeEdited = String.format("ToBeEdited%s", RandomData.getRandomAlphanumeric());
    private final String lockedByMe = String.format("LockedFileByContributor%s", RandomData.getRandomAlphanumeric());
    private final String wordFile = String.format("WordFile%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = "FileContent";
    private UploadContent uploadContent;
    private  DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private CreateContentPage create;
    private EditInAlfrescoPage editInAlfresco;
    private StartWorkflowPage startWorkflowPage;
    private String testFile = "WordFileCreatedBySelf.docx";
    private String filePath = testDataFolder + testFile;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userContributor.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), userContributor.get().getUsername(), siteName.get().getId(), "SiteContributor");

        contentService.createDocument(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, textFile, fileContent);
        contentService.createDocument(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, lockedByMe, fileContent);
        contentAction.checkOut(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), lockedByMe);
        contentService.createDocument(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), DocumentType.MSWORD2007, wordFile, fileContent);

        contentService.createDocument(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileWithVersions, fileContent);
        contentAction.checkIn(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), fileWithVersions, DocumentType.TEXT_PLAIN,
                fileContent + "edited", false, "");
        contentService.createDocument(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileWithVersionsForRevert, fileContent);
        contentAction.checkIn(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), fileWithVersionsForRevert, DocumentType.TEXT_PLAIN,
                fileContent + "edited", false, "");
        contentService.createDocument(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, toBeEdited, fileContent);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        editInAlfresco = new EditInAlfrescoPage(webDriver);
        startWorkflowPage = new StartWorkflowPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        create = new CreateContentPage(webDriver);

        authenticateUsingLoginPage(userContributor.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userContributor.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userContributor.get());
    }

    @TestRail (id = "C8910")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void createContent()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: On the Document Library Page click on 'Create' button.");
        documentLibraryPage.clickCreateButton();
        log.info("Step2: From the Create Options menu select Create Plain Text and verify the new file is opened in Document Details page.");
        documentLibraryPage.clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        log.info("Step3: Provide input for name, title, description and click 'Create' button");
        create.typeName("test");
        create.typeContent("test");
        create.typeTitle("test");
        create.typeDescription("test");
        create.clickCreate();
        assertEquals(documentDetailsPage.getContentText(), "test", "File preview displayed");
    }

    @TestRail (id = "C8911")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void uploadContent()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String testFilePath = testDataFolder + fileName;
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());

        log.info("Step1: On the Document Library Page click on 'Upload' button.");
        uploadContent.uploadContent(testFilePath);
        log.info("Step2: Verify the file is successfully uploaded.");

        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), String.format("The file [%s] is not present", fileName));
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8914")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void uploadNewVersionForItemCreatedBySelf()
    {
        String newVersionFileName = "EditedTestFileC8914.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());

        log.info("Step1: Mouse over test File and check 'Upload new version' action is available.");
        documentLibraryPage.selectItemAction(textFile, ItemActions.UPLOAD_NEW_VERSION);

        log.info("Steps2,3: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);

        log.info("Steps4: Click on the file and check the content is updated.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFileName));
    }

    @TestRail (id = "C8917")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "office" })
    public void editOnlineForContentCreatedBySelf()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());

        log.info("Step1: On the Document Library Page click on 'Upload' button.");
        uploadContent.uploadContent(filePath);

        log.info("Steps1: Mouse over file and check 'Edit in Microsoft Office' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(testFile, ItemActions.EDIT_IN_MICROSOFT_OFFICE),
            "Edit in Microsoft Office available for Contributor user");
    }

    @TestRail (id = "C8919")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editInlineForContentCreatedBySelf()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Steps1: Mouse over file and check 'Edit in Alfresco' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(toBeEdited, ItemActions.EDIT_IN_ALFRESCO), "Edit in Alfresco available for Contributor user");
        log.info("Steps2: Click 'Edit in Alfresco'.");
        documentLibraryPage.selectItemAction(toBeEdited, ItemActions.EDIT_IN_ALFRESCO);
        log.info("Steps3: Edit content and save changes.");
        editInAlfresco.enterDocumentDetails("editedName", "editedContent", "editedTitle", "editedDescription");
        editInAlfresco.clickSaveButton();
        log.info("Steps4: Click on test file to open file and check content.");
        documentLibraryPage.clickOnFile("editedName");
        Assert.assertEquals(documentDetailsPage.getContentText(), "editedContent");
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8921")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editOfflineForContentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String newVersionFileName = "EditedTestFileC8921.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Steps1: Mouse over file and check 'Edit Offline' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.EDIT_OFFLINE), "Edit Offline available for Contributor user");
        log.info("Steps2: Click 'Edit Offline' action.");
        documentLibraryPage.selectItemAction(fileName, ItemActions.EDIT_OFFLINE);

        log.info("Steps3: Check the file is locked for offline editing.");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(fileName), "This document is locked by you for offline editing.", "Document appears to be locked");
        log.info("Steps4,5: Upload a new version for the locked document");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.UPLOAD_NEW_VERSION), "Upload New Version available for Contributor user");
        log.info("Steps6: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        documentLibraryPage.selectItemAction(fileName, ItemActions.UPLOAD_NEW_VERSION);
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        log.info("Steps7: Click test file title link to open details page and check content.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFileName));
    }

    @TestRail (id = "C8925")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void checkInOutGoogleDocsCreatedBySelf() throws Exception
    {
        docs.loginToGoogleDocs();
        String googleDocPath = testDataFolder + "uploadedDoc.docx";
        log.info(
            "Preconditions: Create test site and add contributor member to site. As Contributor user, navigate to Document Library page for the test site and create Google Doc file");
        documentLibraryPage.navigate(siteName.get().getId());
        uploadContent.uploadContent(googleDocPath);

        log.info("Steps1: Mouse over file and check 'Edit in Google Docs' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("uploadedDoc.docx", ItemActions.EDIT_IN_GOOGLE_DOCS),
            "Edit in Google Docs available for Contributor user");

        log.info("Steps2: Click 'Edit in Google Docs' action and add some content");
        documentLibraryPage.selectItemAction("uploadedDoc.docx", ItemActions.EDIT_IN_GOOGLE_DOCS);
        docs.clickOkButtonOnTheAuthPopup();
        docs.switchToGoogleDocsWindowandAndEditContent("GDTitle", "Edited");
        log.info("Steps3: Check the test file's status in Document Library.");

        assertTrue(docs.isLockedIconDisplayed(), "Locked icon displayed");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("uploadedDoc.docx"), "This document is locked by you.", "Document appears to be locked");
        assertTrue(docs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");
        log.info("Steps4, 5: Mouse over testFile name and Click 'Check In Google Doc'. Verify Version Information window is displayed.");
        documentLibraryPage.selectItemAction("uploadedDoc.docx", ItemActions.CHECK_IN_GOOGLE_DOC);
        Assert.assertEquals(docs.isVersionInformationPopupDisplayed(), true, "Version information pop-up displayed");
        log.info("Steps6: Click 'Ok' on the Version Information window and verify it is is closed");
        docs.clickOkButton();
        Assert.assertEquals(docs.isVersionInformationPopupDisplayed(), false, "Version Information pop-up displayed");
        log.info("Steps7: Check the status for the file");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed("GDTitle"), "Document appears to be unlocked");
        log.info("Steps8: testFile name.");
        documentLibraryPage.clickOnFile("GDTitle");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        log.info("Steps9: Verify the file content is correct");
        Assert.assertTrue(documentDetailsPage.getContentText().replaceAll("\\s+", "").contains("Edited"), "File preview correctly displayed");
    }

    @TestRail (id = "C8929")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void cancelEditingContentLockedBySelf()
    {
        log.info(
            "Preconditions: Create test site, add Contributor member to site and create test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(lockedByMe),
                "This document is locked by you for offline editing.", "Document appears to be locked");
        log.info("Steps2: Hover over testFile and check whether 'Cancel editing' action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(lockedByMe, ItemActions.CANCEL_EDITING),
            "Cancel Editing action available for Contributor user");
        log.info("Steps3: Click 'Cancel Editing' action and check whether the lock is removed for the test file");
        documentLibraryPage.selectItemAction(lockedByMe, ItemActions.CANCEL_EDITING);

        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(lockedByMe), "Document appears to be locked");
    }


    @TestRail (id = "C8933")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void editInGoogleDocs() throws Exception
    {
        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        String docsUrl = "https://docs.google.com/document";
        log.info("Preconditions: Create test site, add Contributor member to site.  As Contributor user, navigate to Document Library page for the test site and upload a .docx file.");
        documentLibraryPage.navigate(siteName.get().getId());
        uploadContent.uploadContent(googleDocPath);
        log.info("Step1: Hover over test File and check 'Edit In Google Docs action' is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS), "Edit in Google Docs action available");
        log.info("Step2: Click 'Edit In Google Docs action' and verify the file is opened in Google Docs");
        documentLibraryPage.selectItemAction(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS);
        docs.clickOkButtonOnTheAuthPopup();
        getBrowser().switchWindow(docsUrl);
        assertTrue(getBrowser().getCurrentUrl().contains(docsUrl),
            "After clicking on Google Docs link, the title is: " + getBrowser().getCurrentUrl());
    }

    @TestRail (id = "C8934")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void startWorkflow()
    {
        log.info("Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Mouse over file and click 'Start Workflow' action.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(textFile, ItemActions.START_WORKFLOW), "Start Workflow action available for Contributor user");
        log.info("Steps2: Click 'Start Workflows' action.");
        documentLibraryPage.selectItemAction(textFile, ItemActions.START_WORKFLOW);
        log.info("Steps3: From the Select Workflow drop-down select New Task Workflow.");
        startWorkflowPage.selectAWorkflow("New Task");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Start Workflow", "Displayed page=");
    }

    @TestRail (id = "C8935")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void locateFile()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: In Documents Library, go to Documents sections and select Recently Added.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.RecentlyAdded.title);
        log.info("Step2: Hover over test file and click 'Locate File'.");
        documentLibraryPage.browserRefresh();
        documentLibraryPage.selectItemAction(textFile, ItemActions.LOCATE_FILE);
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Collections.singletonList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb is 'Documents'.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(textFile),
                "User is redirected to location of the created document.");
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8936")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "DownloadTest" })
    public void downloadPreviousVersion()
    {
        log.info("Step1: Navigate to the created file with versions");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFile(fileWithVersions);

        log.info("Step5: Verify Version History section.");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"), "Initial version available");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.1"), "New minor version available");
        log.info("Steps6, 7: Click 'Download' button for previous version. Choose Save file option, location for file to be downloaded and click 'OK' button.");
        documentDetailsPage.clickDownloadPreviousVersion();
        Assert.assertTrue(isFileInDirectory(fileWithVersions, null), "The file was not found in the specified location");
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8937")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void revertToPreviousVersion()
    {
        log.info("Step1: Navigate to the created file with versions");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFile(fileWithVersionsForRevert);
        log.info("Step5: Verify Version History section.");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"), "Initial version available");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.1"), "New minor version available");
        Assert.assertTrue(documentDetailsPage.isRevertButtonAvailable(), "Revert button available");
        log.info("Step6: Click on 'Revert' action. Click 'Ok' button on the displayed pop-up for confirmation.");
        documentDetailsPage.clickRevertButton();
        documentDetailsPage.clickOkOnRevertPopup();
        Assert.assertEquals(documentDetailsPage.getContentText(), fileContent, "New version's content");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.2"), "New minor version created");
    }
}
