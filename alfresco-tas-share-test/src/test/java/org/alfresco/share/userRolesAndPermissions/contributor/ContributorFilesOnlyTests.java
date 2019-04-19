package org.alfresco.share.userRolesAndPermissions.contributor;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.Download;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ContributorFilesOnlyTests extends ContextAwareWebTest {
    private final String userContributor = String.format("Contributor%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String adminFile = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
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
    Download download;
    @Autowired
    StartWorkflowPage startWorkflowPage;

    @BeforeClass(alwaysRun = true)
    public void setupTest(){
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, userContributor, userContributor);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteName, "SiteContributor");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, adminFile, fileContent);
    }

    @TestRail(id = "C8910")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void createContent() {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: On the Document Library Page click on 'Create' button.");
        documentLibraryPage.clickCreateButton();
        Assert.assertTrue(documentLibraryPage.isCreateContentMenuDisplayed(), "The Create Options Menu opened");
        LOG.info("Step2: From the Create Options menu select Create Plain Text and verify the new file is opened in Document Details page.");
        create.clickPlainTextButton();
        LOG.info("Step3: Provide input for name, title, description and click 'Create' button");
        create.sendInputForName("test");
        create.sendInputForContent("test");
        create.sendInputForTitle("test");
        create.sendInputForDescription("test");
        create.clickCreateButton();
        documentDetailsPage.renderedPage();
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), "test", "File preview displayed");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/test", deletePath));
    }

    @TestRail(id = "C8911")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadContent() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String testFilePath = testDataFolder + fileName;
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: On the Document Library Page click on 'Upload' button.");
        uploadContent.uploadContent(testFilePath);
        LOG.info("Step2: Verify the file is successfully uploaded.");
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), String.format("The file [%s] is not present", fileName));
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, fileName));
    }

    @TestRail(id = "C8912")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void downloadContent() {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1, 2: Mouse over the test file  from Document Library and click 'Download'.");
        documentLibraryPage.clickDocumentLibraryItemAction(adminFile, "Download", download);
        download.acceptAlertIfDisplayed();
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(download.isFileInDirectory(adminFile, null), "The file was not found in the specified location");
    }

    @TestRail(id = "C8913")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewInBrowser() {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Mouse over testFile and check 'View in Browser' is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "View In Browser"), "View in browser available");
        LOG.info("Step2: Click View in browser and verify the file is opened in a new browser window.");
        documentLibraryPage.clickAction(adminFile, "View In Browser");
        getBrowser().waitInSeconds(2);
        assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), fileContent, "Correct file content/ file opened in new window");
    }

    @Bug(id="MNT-18059",status = Bug.Status.OPENED)
    @TestRail(id = "C8914")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadNewVersionForItemCreatedBySelf() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String newVersionFileName = "EditedTestFileC8914.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Mouse over test File and check 'Upload new version' action is available.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Upload New Version", uploadContent);
        LOG.info("Steps2,3: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        LOG.info("Steps4: Click on the file and check the content is updated.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFileName));
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, newVersionFileName));
    }

    @TestRail(id = "C8915")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadNewVersionForItemCreatedByOthers() {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Mouse over test File and check 'Upload new version' action is not available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "Upload New Version"),
                "Upload New Version available for Contributor user");
    }

    @TestRail(id = "C8916")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadNewVersionForItemLockedByUser() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String siteNameC8916 = String.format("SiteNameC8916%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: Create test site, add Contributor user to site. Create a test file in site. Login as admin and navigate to site's doc lib");
        siteService.create(adminUser, adminPassword, domain, siteNameC8916, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteNameC8916, "SiteContributor");
        contentService.createDocument(userContributor, password, siteNameC8916, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteNameC8916);
        LOG.info("Steps1,2: Mouse over file and click 'Edit offline' action");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        LOG.info("Steps3: Logout and login as Contributor user.");
        setupAuthenticatedSession(userContributor, password);
        LOG.info("Steps4:  Navigate to test site's doc lib and verify the file is locked by admin");
        documentLibraryPage.navigate(siteNameC8916);
        Assert.assertEquals(documentLibraryPage.getLockedByUserName(), "Administrator", "Document appears to be locked by admin");
        LOG.info("Steps5: Verify 'Upload new Version' option is not available for Contributor user, since the file is locked by admin");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Upload New Version"),
                "Upload New Version available for Contributor user");
    }

    @TestRail(id = "C8917")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editOnlineForContentCreatedBySelf() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.MSWORD, fileName, fileContent);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit in Microsoft Office' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Edit in Microsoft Office"),
                "Edit in Microsoft Office available for Contributor user");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, fileName));
    }

    @TestRail(id = "C8918")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editOnlineForContentCreatedByOthers() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.MSWORD, fileName, fileContent);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit in Microsoft Office' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Edit in Microsoft Office"),
                "Edit in Microsoft Office available for Contributor user");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, fileName));
    }

    @TestRail(id = "C8919")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editInlineForContentCreatedBySelf() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit in Alfresco' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Edit in Alfresco"), "Edit in Alfresco available for Contributor user");
        LOG.info("Steps2: Click 'Edit in Alfresco'.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit in Alfresco", editInAlfresco);
        LOG.info("Steps3: Edit content and save changes.");
        editInAlfresco.sendDocumentDetailsFields("editedName", "editedContent", "editedTitle", "editedDescription");
        editInAlfresco.clickButton("Save");
        LOG.info("Steps4: Click on test file to open file and check content.");
        documentLibraryPage.clickOnFile("editedName");
        Assert.assertEquals(documentDetailsPage.getContentText(), "editedContent");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/editedName", deletePath));
    }

    @TestRail(id = "C8920")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editInlineForContentCreatedByOthers() {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit in Alfresco' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "Edit in Alfresco"),
                "Edit in Alfresco available for Contributor user");
    }

    @Bug(id="MNT-18059",status = Bug.Status.OPENED)
    @TestRail(id = "C8921")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editOfflineForContentCreatedBySelf() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String newVersionFileName = "EditedTestFileC8921.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit Offline' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Edit Offline"), "Edit Offline available for Contributor user");
        LOG.info("Steps2: Click 'Edit Offline' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        LOG.info("Steps3: Check the file is locked for offline editing.");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(fileName), "This document is locked by you for offline editing.", "Document appears to be locked");
        LOG.info("Steps4,5: Upload a new version for the locked document");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Upload New Version"),"Upload New Version available for Contributor user");
        LOG.info("Steps6: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Upload New Version", uploadContent);
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        getBrowser().waitInSeconds(2);
        LOG.info("Steps7: Click test file title link to open details page and check content.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFileName));
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, newVersionFileName));
    }

    @TestRail(id = "C8922")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editOfflineForContentCreatedByOthers() {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit Offline' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "Edit Offline"), "Edit Offline available for Contributor user");
    }

    @TestRail(id = "C8925")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void checkInOutGoogleDocsCreatedBySelf() throws Exception {
        docs.loginToGoogleDocs();
        String googleDocPath = testDataFolder + "uploadedDoc.docx";
        LOG.info(
                "Preconditions: Create test site and add contributor member to site. As Contributor user, navigate to Document Library page for the test site and create Google Doc file");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        getBrowser().waitInSeconds(5);

        LOG.info("Steps1: Mouse over file and check 'Edit in Google Docs' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("uploadedDoc.docx", "Edit in Google Docs"),
                "Edit in Google Docs available for Contributor user");

        LOG.info("Steps2: Click 'Edit in Google Docs' action and add some content");
        documentLibraryPage.clickDocumentLibraryItemAction("uploadedDoc.docx", "Edit in Google Docs", docs);
        getBrowser().waitInSeconds(5);
        docs.clickOkButtonOnTheAuthPopup();
        docs.switchToGoogleDocsWindowandAndEditContent("GDTitle", "Edited");
        LOG.info("Steps3: Check the test file's status in Document Library.");
        documentLibraryPage.renderedPage();
        assertTrue(docs.isLockedIconDisplayed(), "Locked icon displayed");
        Assert.assertEquals(documentLibraryPage.getInfoBannerText("uploadedDoc.docx"), "This document is locked by you.", "Document appears to be locked");
        assertTrue(docs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");
        LOG.info("Steps4, 5: Mouse over testFile name and Click 'Check In Google Doc'. Verify Version Information window is displayed.");
        documentLibraryPage.clickDocumentLibraryItemAction("uploadedDoc.docx", "Check In Google Doc", documentLibraryPage);
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
    }

    @TestRail(id = "C8926")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void editInGoogleDocForContentCreatedByOthers() {
        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        LOG.info(
                "Preconditions: Create test site, add contributor member to site. As admin, navigate to Document Library page for the test site and create Google Doc file");
        docs.loginToGoogleDocs();
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        LOG.info("Steps1: Login as Contributor user, go to site's doc lib and check whether 'Edit in Google Docs' action is available.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, "Edit in Google Docs"),
                "Edit in Google Docs available for Contributor user");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, googleDocName));
    }

    @TestRail(id = "C8928")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void checkInGoogleDocForContentCreatedByOthers() throws Exception {
        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        LOG.info(
                "Preconditions: Create test site and add contributor member to site. As Admin user, navigate to Document Library page for the test site and create Google Doc file. Check out the file in google Docs.");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(googleDocName, "Edit in Google Docs", docs);
        getBrowser().waitInSeconds(5);
        docs.clickOkButtonOnTheAuthPopup();
        docs.switchToGoogleDocsWindowandAndEditContent("GDTitle", "Google Doc test content");
        LOG.info("Steps1: Login as Contributor user, go to site's doc lib and check whether 'Edit in Google Docs' action is available.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, "Check In Google Doc"),
                "Check In Google Doc available for Contributor user");
    }

    @TestRail(id = "C8929")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void cancelEditingContentLockedBySelf() throws Exception {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        LOG.info(
                "Preconditions: Create test site, add Contributor member to site and create test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and click 'Edit Offline' action. Verify the file appears as locked.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(fileName), "This document is locked by you for offline editing.", "Document appears to be locked");
        LOG.info("Steps2: Hover over testFile and check whether 'Cancel editing' action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Cancel Editing"),
                "Cancel Editing action available for Contributor user");
        LOG.info("Steps3: Click 'Cancel Editing' action and check whether the lock is removed for the test file");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Cancel Editing", documentLibraryPage);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(fileName), "Document appears to be locked");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, fileName));
    }

    @TestRail(id = "C8930")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void cancelEditingContentLockedByOthers() throws Exception {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String siteNameC8930 = String.format("SiteC8930%s", RandomData.getRandomAlphanumeric());
        LOG.info(
                "Preconditions: Create test site and add Contributor member to site. Create a file in the Document Library for the test site, as admin user.");
        siteService.create(adminUser, adminPassword, domain, siteNameC8930, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteNameC8930, "SiteContributor");
        contentService.createDocument(adminUser, adminPassword, siteNameC8930, DocumentType.TEXT_PLAIN, fileName, fileContent);
        LOG.info("Step1: Login as admin and lock test file - e.g. for offline editing");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteNameC8930);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(fileName), "This document is locked by you for offline editing.", "Document appears to be locked");
        LOG.info("Step2: Login as Contributor user and check whether the file appears as locked by Admin");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteNameC8930);
        Assert.assertEquals(documentLibraryPage.getLockedByUserName(), "Administrator", "The document is not locked");
        LOG.info("Step3: Hover over test file and check whether 'Cancel Editing' action is missing");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Cancel Editing"), "Cancel Editing available for Contributor user");
    }

    @TestRail(id = "C8931")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewOriginalVersion() throws Exception {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String content = String.format("FileContent%s", RandomData.getRandomAlphanumeric());
        String siteNameC8931 = String.format("SiteC8931%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: Create test site, add Contributor member to site and create test file. Navigate to Document Library page for the test site, as admin user.");
        siteService.create(adminUser, adminPassword, domain, siteNameC8931, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteNameC8931, "SiteContributor");
        contentService.createDocument(adminUser, adminPassword, siteNameC8931, DocumentType.TEXT_PLAIN, fileName, content);
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteNameC8931);
        LOG.info("Steps1: Mouse over file and click 'Edit Offline' action. Verify the file appears as locked.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(fileName), "This document is locked by you for offline editing.", "Document appears to be locked");
        LOG.info("Steps2: Logout and login as Contributor user; hover over testFile and check whether 'View Original Document' action is available");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteNameC8931);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "View Original Document"),
                "View Original Document' action available for Contributor user");
        LOG.info("Steps3: Click 'View Original Document' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "View Original Document", documentDetailsPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), content, "File preview successfully displayed");
        Assert.assertEquals(documentDetailsPage.getLockedMessage(), "This document is locked by Administrator.", "Document appears to be locked by admin user");
    }

    @TestRail(id = "C8932")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewWorkingCopy() throws Exception {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String content = String.format("FileContent%s", RandomData.getRandomAlphanumeric());
        String siteNameC8932 = String.format("SiteC8932%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: Create test site, add Contributor member to site and create test file. Navigate to Document Library page for the test site, as admin user.");
        siteService.create(adminUser, adminPassword, domain, siteNameC8932, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteNameC8932, "SiteContributor");
        contentService.createDocument(adminUser, adminPassword, siteNameC8932, DocumentType.TEXT_PLAIN, fileName, content);
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteNameC8932);
        LOG.info("Steps1: Mouse over file and click 'Edit Offline' action. Verify the file appears as locked.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        Assert.assertEquals(documentLibraryPage.getInfoBannerText(fileName), "This document is locked by you for offline editing.", "Document appears to be locked");
        LOG.info("Steps2: Logout and login as Contributor user; hover over testFile and click 'View Original Document' option");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteNameC8932);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "View Original Document", documentDetailsPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), content, "File preview successfully displayed");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Working Copy"));
        LOG.info("Steps3: Click 'View Working Copy' action");
        documentDetailsPage.clickDocumentActionsOption("View Working Copy");
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), content, "File preview successfully displayed");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Original Document"));
    }

    @TestRail(id = "C8933")
    @Test(groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void editInGoogleDocs() throws Exception {
        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        String docsUrl = "https://docs.google.com/document";
        LOG.info("Preconditions: Create test site, add Contributor member to site.  As Contributor user, navigate to Document Library page for the test site and upload a .docx file.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        LOG.info("Step1: Hover over test File and check 'Edit In Google Docs action' is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, "Edit in Google Docs"), "Edit in Google Docs action available");
        LOG.info("Step2: Click 'Edit In Google Docs action' and verify the file is opened in Google Docs");
        documentLibraryPage.clickDocumentLibraryItemAction(googleDocName, "Edit in Google Docs", docs);
        docs.clickOkButtonOnTheAuthPopup();
        getBrowser().switchWindow(docsUrl);
        assertTrue(getBrowser().getCurrentUrl().contains(docsUrl),
                "After clicking on Google Docs link, the title is: " + getBrowser().getCurrentUrl());
        closeWindowAndSwitchBack();
    }

    @TestRail(id = "C8934")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void startWorkflow() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Mouse over file and click 'Start Workflow' action.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Start Workflow"), "Start Workflow action available for Contributor user");
        LOG.info("Steps2: Click 'Start Workflows' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Start Workflow", startWorkflowPage);
        LOG.info("Steps3: From the Select Workflow drop-down select New Task Workflow.");
        startWorkflowPage.selectAWorkflow("New Task");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Start Workflow", "Displayed page=");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, fileName));
    }

    @TestRail(id = "C8935")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void locateFile() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String siteNameC8935 = String.format("SiteC8932%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        siteService.create(adminUser, adminPassword, domain, siteNameC8935, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteNameC8935, "SiteContributor");
        contentService.createDocument(userContributor, password, siteNameC8935, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteNameC8935);
        LOG.info("Step1: In Documents Library, go to Documents sections and select Recently Added.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.RecentlyAdded.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentLibraryPage.DocumentsFilters.RecentlyAdded.header,
                "Recently added documents are displayed.");
        LOG.info("Step2: Hover over test file and click 'Locate File'.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Locate File", documentLibraryPage);
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Collections.singletonList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb is 'Documents'.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), "User is redirected to location of the created document.");
       // On 5.2, the file it's checked
    }

    @Bug(id="MNT-18059",status = Bug.Status.OPENED)
    @TestRail(id = "C8936")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void downloadPreviousVersion() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String newVersionFileName = "NewVersionC8936.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;
        LOG.info("Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, "");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Hover over the test file and click More -> Upload New Version");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Upload New Version", uploadContent);
        LOG.info("Steps2,3: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Minor);
        getBrowser().waitInSeconds(2);
        LOG.info("Steps4: Click on name of testFile and verify content is updated");
        documentLibraryPage.clickOnFile(newVersionFileName);
        Assert.assertEquals(documentDetailsPage.getContentText(), "updated by upload new version");
        LOG.info("Step5: Verify Version History section.");
        Assert.assertTrue(documentDetailsPage.isRevertButtonAvailable(), "'Download previous version' button available");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"), "Initial version available");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.1"), "New minor version available");
        LOG.info("Steps6, 7: Click 'Download' button for previous version. Choose Save file option, location for file to be downloaded and click 'OK' button.");
        documentDetailsPage.clickDownloadPreviousVersion();
        download.acceptAlertIfDisplayed();
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(download.isFileInDirectory(fileName, null), "The file was not found in the specified location");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, newVersionFileName));
    }

    @Bug(id="MNT-18059",status = Bug.Status.OPENED)
    @TestRail(id = "C8937")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void revertToPreviousVersion() {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String newVersionFileName = "NewVersionC8937.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;
        LOG.info("Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, "original content");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Hover over the test file and click More -> Upload New Version");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Upload New Version", uploadContent);
        LOG.info("Steps2,3: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Minor);
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps4: Click on name of testFile and verify content is updated");
        documentLibraryPage.clickOnFile(newVersionFileName);
        Assert.assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", "New version's content");
        LOG.info("Step5: Verify Version History section.");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"), "Initial version available");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.1"), "New minor version available");
        Assert.assertTrue(documentDetailsPage.isRevertButtonAvailable(), "Revert button available");
        LOG.info("Step6: Click on 'Revert' action. Click 'Ok' button on the displayed pop-up for confirmation.");
        documentDetailsPage.clickRevertButton();
        documentDetailsPage.clickOkOnRevertPopup();
        Assert.assertEquals(documentDetailsPage.getContentText(), "original content", "New version's content");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.2"), "New minor version created");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, fileName));
    }
}
