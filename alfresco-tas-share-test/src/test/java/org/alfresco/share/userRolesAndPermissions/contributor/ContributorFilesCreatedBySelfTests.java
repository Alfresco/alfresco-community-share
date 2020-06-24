package org.alfresco.share.userRolesAndPermissions.contributor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.alfresco.utility.report.Bug.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ContributorFilesCreatedBySelfTests extends ContextAwareWebTest
{
    private final String userContributor = String.format("Contributor%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String fileWithVersions = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
    private final String fileWithVersionsForRevert = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
    private final String textFile = String.format("ContributorFile%s", RandomData.getRandomAlphanumeric());
    private final String toBeEdited = String.format("ToBeEdited%s", RandomData.getRandomAlphanumeric());
    private final String lockedByMe = String.format("LockedFileByContributor%s", RandomData.getRandomAlphanumeric());
    private final String wordFile = String.format("WordFile%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = "FileContent";
    private final String deletePath = String.format("Sites/%s/documentLibrary", siteName);
    @Autowired
    UploadContent uploadContent;
    @Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @Autowired
    CreateContent create;
    @Autowired
    EditInAlfrescoPage editInAlfresco;
    @Autowired
    GoogleDocsCommon docs;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, userContributor, userContributor);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteName, "SiteContributor");

        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, textFile, fileContent);
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, lockedByMe, fileContent);
        contentAction.checkOut(userContributor, password, siteName, lockedByMe);
        contentService.createDocument(userContributor, password, siteName, DocumentType.MSWORD, wordFile, fileContent);

        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileWithVersions, fileContent);
        contentAction.checkIn(userContributor, password, siteName, fileWithVersions, DocumentType.TEXT_PLAIN,
                fileContent + "edited", false, "");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileWithVersionsForRevert, fileContent);
        contentAction.checkIn(userContributor, password, siteName, fileWithVersionsForRevert, DocumentType.TEXT_PLAIN,
                fileContent + "edited", false, "");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, toBeEdited, fileContent);
        setupAuthenticatedSession(userContributor, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C8910")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void createContent()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: On the Document Library Page click on 'Create' button.");
        documentLibraryPage.clickCreateButton();
        LOG.info("Step2: From the Create Options menu select Create Plain Text and verify the new file is opened in Document Details page.");
        documentLibraryPage.clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        LOG.info("Step3: Provide input for name, title, description and click 'Create' button");
        create.sendInputForName("test");
        create.sendInputForContent("test");
        create.sendInputForTitle("test");
        create.sendInputForDescription("test");
        create.clickCreateButton();
        assertEquals(documentDetailsPage.getContentText(), "test", "File preview displayed");
    }

    @TestRail (id = "C8911")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void uploadContent()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String testFilePath = testDataFolder + fileName;
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: On the Document Library Page click on 'Upload' button.");
        uploadContent.uploadContent(testFilePath);
        LOG.info("Step2: Verify the file is successfully uploaded.");
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), String.format("The file [%s] is not present", fileName));
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8914")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void uploadNewVersionForItemCreatedBySelf()
    {
        String newVersionFileName = "EditedTestFileC8914.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");

        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Mouse over test File and check 'Upload new version' action is available.");
        documentLibraryPage.clickDocumentLibraryItemAction(textFile, ItemActions.UPLOAD_NEW_VERSION, uploadContent);
        LOG.info("Steps2,3: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        LOG.info("Steps4: Click on the file and check the content is updated.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFileName));
    }

    @TestRail (id = "C8917")
    @Test (groups = { TestGroup.SANITY, "user-roles", "office" })
    public void editOnlineForContentCreatedBySelf()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit in Microsoft Office' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(wordFile, ItemActions.EDIT_IN_MICROSOFT_OFFICE),
            "Edit in Microsoft Office available for Contributor user");
    }

    @TestRail (id = "C8919")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void editInlineForContentCreatedBySelf()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit in Alfresco' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(toBeEdited, ItemActions.EDIT_IN_ALFRESCO), "Edit in Alfresco available for Contributor user");
        LOG.info("Steps2: Click 'Edit in Alfresco'.");
        documentLibraryPage.clickDocumentLibraryItemAction(toBeEdited, ItemActions.EDIT_IN_ALFRESCO, editInAlfresco);
        LOG.info("Steps3: Edit content and save changes.");
        editInAlfresco.sendDocumentDetailsFields("editedName", "editedContent", "editedTitle", "editedDescription");
        editInAlfresco.clickButton("Save");
        LOG.info("Steps4: Click on test file to open file and check content.");
        documentLibraryPage.clickOnFile("editedName");
        Assert.assertEquals(documentDetailsPage.getContentText(), "editedContent");
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8921")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void editOfflineForContentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String newVersionFileName = "EditedTestFileC8921.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit Offline' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.EDIT_OFFLINE), "Edit Offline available for Contributor user");
        LOG.info("Steps2: Click 'Edit Offline' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, ItemActions.EDIT_OFFLINE, documentLibraryPage);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        LOG.info("Steps3: Check the file is locked for offline editing.");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(fileName), "This document is locked by you for offline editing.", "Document appears to be locked");
        LOG.info("Steps4,5: Upload a new version for the locked document");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.UPLOAD_NEW_VERSION), "Upload New Version available for Contributor user");
        LOG.info("Steps6: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, ItemActions.UPLOAD_NEW_VERSION, uploadContent);
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        getBrowser().waitInSeconds(2);
        LOG.info("Steps7: Click test file title link to open details page and check content.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFileName));
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, newVersionFileName));
    }

    @TestRail (id = "C8925")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void checkInOutGoogleDocsCreatedBySelf() throws Exception
    {
//        docs.loginToGoogleDocs();
        String googleDocPath = testDataFolder + "uploadedDoc.docx";
        LOG.info(
            "Preconditions: Create test site and add contributor member to site. As Contributor user, navigate to Document Library page for the test site and create Google Doc file");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        getBrowser().waitInSeconds(5);

        LOG.info("Steps1: Mouse over file and check 'Edit in Google Docs' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("uploadedDoc.docx", ItemActions.EDIT_IN_GOOGLE_DOCS),
            "Edit in Google Docs available for Contributor user");

        LOG.info("Steps2: Click 'Edit in Google Docs' action and add some content");
        documentLibraryPage.clickDocumentLibraryItemAction("uploadedDoc.docx", ItemActions.EDIT_IN_GOOGLE_DOCS, docs);
        getBrowser().waitInSeconds(5);
        docs.clickOkButtonOnTheAuthPopup();
        docs.switchToGoogleDocsWindowandAndEditContent("GDTitle", "Edited");
        LOG.info("Steps3: Check the test file's status in Document Library.");
        documentLibraryPage.renderedPage();
        assertTrue(docs.isLockedIconDisplayed(), "Locked icon displayed");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("uploadedDoc.docx"), "This document is locked by you.", "Document appears to be locked");
        assertTrue(docs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");
        LOG.info("Steps4, 5: Mouse over testFile name and Click 'Check In Google Doc'. Verify Version Information window is displayed.");
        documentLibraryPage.clickDocumentLibraryItemAction("uploadedDoc.docx", ItemActions.CHECK_IN_GOOGLE_DOC,
                documentLibraryPage);
        getBrowser().waitInSeconds(10);
        Assert.assertEquals(docs.isVersionInformationPopupDisplayed(), true, "Version information pop-up displayed");
        LOG.info("Steps6: Click 'Ok' on the Version Information window and verify it is is closed");
        docs.clickOkButton();
        getBrowser().waitInSeconds(10);
        Assert.assertEquals(docs.isVersionInformationPopupDisplayed(), false, "Version Information pop-up displayed");
        LOG.info("Steps7: Check the status for the file");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed("GDTitle"), "Document appears to be unlocked");
        LOG.info("Steps8: testFile name.");
        documentLibraryPage.clickOnFile("GDTitle");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        LOG.info("Steps9: Verify the file content is correct");
        Assert.assertTrue(documentDetailsPage.getContentText().replaceAll("\\s+", "").contains("Edited"), "File preview correctly displayed");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/GDTitle.docx", deletePath));

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8929")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void cancelEditingContentLockedBySelf()
    {
        LOG.info(
            "Preconditions: Create test site, add Contributor member to site and create test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(lockedByMe),
                "This document is locked by you for offline editing.", "Document appears to be locked");
        LOG.info("Steps2: Hover over testFile and check whether 'Cancel editing' action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(lockedByMe, ItemActions.CANCEL_EDITING),
            "Cancel Editing action available for Contributor user");
        LOG.info("Steps3: Click 'Cancel Editing' action and check whether the lock is removed for the test file");
        documentLibraryPage.clickDocumentLibraryItemAction(lockedByMe, ItemActions.CANCEL_EDITING, documentLibraryPage);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(lockedByMe), "Document appears to be locked");
    }


    @TestRail (id = "C8933")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void editInGoogleDocs() throws Exception
    {
//        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        String docsUrl = "https://docs.google.com/document";
        LOG.info("Preconditions: Create test site, add Contributor member to site.  As Contributor user, navigate to Document Library page for the test site and upload a .docx file.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        LOG.info("Step1: Hover over test File and check 'Edit In Google Docs action' is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS), "Edit in Google Docs action available");
        LOG.info("Step2: Click 'Edit In Google Docs action' and verify the file is opened in Google Docs");
        documentLibraryPage.clickDocumentLibraryItemAction(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS, docs);
        docs.clickOkButtonOnTheAuthPopup();
        getBrowser().switchWindow(docsUrl);
        assertTrue(getBrowser().getCurrentUrl().contains(docsUrl),
            "After clicking on Google Docs link, the title is: " + getBrowser().getCurrentUrl());
        closeWindowAndSwitchBack();
    }

    @TestRail (id = "C8934")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void startWorkflow()
    {
        LOG.info("Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Mouse over file and click 'Start Workflow' action.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(textFile, ItemActions.START_WORKFLOW), "Start Workflow action available for Contributor user");
        LOG.info("Steps2: Click 'Start Workflows' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(textFile, ItemActions.START_WORKFLOW, startWorkflowPage);
        LOG.info("Steps3: From the Select Workflow drop-down select New Task Workflow.");
        startWorkflowPage.selectAWorkflow("New Task");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Start Workflow", "Displayed page=");
    }

    @TestRail (id = "C8935")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void locateFile()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: In Documents Library, go to Documents sections and select Recently Added.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.RecentlyAdded.title);
        LOG.info("Step2: Hover over test file and click 'Locate File'.");
        documentLibraryPage.clickDocumentLibraryItemAction(textFile, ItemActions.LOCATE_FILE, documentLibraryPage);
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Collections.singletonList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb is 'Documents'.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(textFile),
                "User is redirected to location of the created document.");
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8936")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void downloadPreviousVersion()
    {
        LOG.info("Step1: Navigate to the created file with versions");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileWithVersions);

        LOG.info("Step5: Verify Version History section.");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"), "Initial version available");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.1"), "New minor version available");
        LOG.info("Steps6, 7: Click 'Download' button for previous version. Choose Save file option, location for file to be downloaded and click 'OK' button.");
        documentDetailsPage.clickDownloadPreviousVersion();
        Assert.assertTrue(isFileInDirectory(fileWithVersions, null), "The file was not found in the specified location");
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8937")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void revertToPreviousVersion()
    {
        LOG.info("Step1: Navigate to the created file with versions");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileWithVersionsForRevert);
        LOG.info("Step5: Verify Version History section.");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"), "Initial version available");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.1"), "New minor version available");
        Assert.assertTrue(documentDetailsPage.isRevertButtonAvailable(), "Revert button available");
        LOG.info("Step6: Click on 'Revert' action. Click 'Ok' button on the displayed pop-up for confirmation.");
        documentDetailsPage.clickRevertButton();
        documentDetailsPage.clickOkOnRevertPopup();
        Assert.assertEquals(documentDetailsPage.getContentText(), fileContent, "New version's content");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.2"), "New minor version created");
    }
}
