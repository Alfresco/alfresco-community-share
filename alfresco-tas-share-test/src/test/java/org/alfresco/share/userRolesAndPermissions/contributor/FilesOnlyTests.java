package org.alfresco.share.userRolesAndPermissions.contributor;

import org.alfresco.common.DataUtil;
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
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class FilesOnlyTests extends ContextAwareWebTest
{
    private final String userContributor = "Contributor" + DataUtil.getUniqueIdentifier();
    private final String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
    private final String description = "SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String adminFile = "AdminFile" + DataUtil.getUniqueIdentifier();
    private final String fileContent = "FileContent";
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
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, userContributor, userContributor);
        siteService.create(adminUser, adminPassword, domain, siteName, description, Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteName, "SiteContributor");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, adminFile, fileContent);
        setupAuthenticatedSession(userContributor, password);
    }

    @TestRail(id = "C8910")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void createContent()
    {
        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: On the Document Library Page click on 'Create' button.");
        documentLibraryPage.clickCreateButton();
        Assert.assertTrue(documentLibraryPage.isCreateContentMenuDisplayed(), "The Create Options Menu opened");

        logger.info("Step2: From the Create Options menu select Create Plain Text and verify the new file is opened in Document Details page.");
        create.clickPlainTextButton();
        logger.info("Step3: Provide input for name, title, description and click 'Create' button");
        create.sendInputForName("test");
        create.sendInputForContent("test");
        create.sendInputForTitle("test");
        create.sendInputForDescription("test");
        create.clickCreateButton();
        documentDetailsPage.renderedPage();
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), "test", "File preview displayed");
    }

    @TestRail(id = "C8911")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadContent()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();
        String testFilePath = testDataFolder + fileName;

        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: On the Document Library Page click on 'Upload' button.");
        uploadContent.uploadContent(testFilePath);

        logger.info("Step2: Verify the file is successfully uploaded.");
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), String.format("The file [%s] is not present", fileName));
    }

    @TestRail(id = "C8912")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void downloadContent()
    {
        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1, 2: Mouse over the test file  from Document Library and click 'Download'.");
        documentLibraryPage.clickDocumentLibraryItemAction(adminFile, "Download", download);
        download.acceptAlertIfDisplayed();
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(download.isFileInDirectory(adminFile, null), "The file was not found in the specified location");
    }

    @TestRail(id = "C8913")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewInBrowser()
    {
        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Mouse over testFile and check 'View in Browser' is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "View In Browser"), "View in browser available");

        logger.info("Step2: Click View in browser and verify the file is opened in a new browser window.");
        documentLibraryPage.clickAction(adminFile, "View In Browser");
        getBrowser().waitInSeconds(2);
        assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), fileContent, "Correct file content/ file opened in new window");
    }

    @TestRail(id = "C8914")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadNewVersionForItemCreatedBySelf()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();
        String newVersionFileName = "EditedTestFileC8914.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;

        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Mouse over test File and check 'Upload new version' action is available.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Upload New Version", uploadContent);

        logger.info("Steps2,3: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();

        logger.info("Steps4: Click on the file and check the content is updated.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFileName));
    }

    @TestRail(id = "C8915")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadNewVersionForItemCreatedByOthers()
    {
        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Mouse over test File and check 'Upload new version' action is not available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "Upload New Version"),
                "Upload New Version available for Contributor user");
    }

    @TestRail(id = "C8916")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadNewVersionForItemLockedByUser()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();

        logger.info("Preconditions: Create test site, add Contributor user to site. Create a test file in site. Login as admin and navigate to site's doc lib");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1,2: Mouse over file and click 'Edit offline' action");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);

        logger.info("Steps3: Logout and login as Contributor user.");

        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userContributor, password);

        logger.info("Steps4:  Navigate to test site's doc lib and verify the file is locked by admin");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getLockedByUserName(), "Administrator", "Document appears to be locked by admin");

        logger.info("Steps5: Verify 'Upload new Version' option is not available for Contributor user, since the file is locked by admin");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Upload New Version"),
                "Upload New Version available for Contributor user");
    }

    @TestRail(id = "C8917")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editOnlineForContentCreatedBySelf()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();
        String fileContent = "FileContent" + DataUtil.getUniqueIdentifier();

        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.MSWORD, fileName, fileContent);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and check 'Edit in Microsoft Office' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Edit in Microsoft Office"),
                "Edit in Microsoft Office available for Contributor user");

    }

    @TestRail(id = "C8918")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editOnlineForContentCreatedByOthers()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();

        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.MSWORD, fileName, fileContent);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and check 'Edit in Microsoft Office' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Edit in Microsoft Office"),
                "Edit in Microsoft Office available for Contributor user");
    }

    @TestRail(id = "C8919")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editInlineForContentCreatedBySelf()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();

        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and check 'Edit in Alfresco' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Edit in Alfresco"), "Edit in Alfresco available for Contributor user");

        logger.info("Steps2: Click 'Edit in Alfresco'.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit in Alfresco", editInAlfresco);

        logger.info("Steps3: Edit content and save changes.");
        editInAlfresco.sendDocumentDetailsFields("editedName", "editedContent", "editedTitle", "editedDescription");
        editInAlfresco.clickButton("Save");

        logger.info("Steps4: Click on test file to open file and check content.");
        documentLibraryPage.clickOnFile("editedName");
        Assert.assertEquals(documentDetailsPage.getContentText(), "editedContent");
    }

    @TestRail(id = "C8920")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editInlineForContentCreatedByOthers()
    {
        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and check 'Edit in Alfresco' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "Edit in Alfresco"),
                "Edit in Alfresco available for Contributor user");
    }

    @TestRail(id = "C8921")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editOfflineForContentCreatedBySelf()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();
        String newVersionFileName = "EditedTestFileC8921.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;

        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and check 'Edit Offline' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Edit Offline"), "Edit Offline available for Contributor user");

        logger.info("Steps2: Click 'Edit Offline' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);

        logger.info("Steps3: Check the file is locked for offline editing.");
        Assert.assertTrue(docs.isLockedDocumentMessageDisplayed(), "Document locked for offline editing");

        logger.info("Steps4,5: Upload a new version for the locked document");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Upload New Version"),
                "Upload New Version available for Contributor user");
        documentLibraryPage.renderedPage();

        logger.info("Steps6: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Upload New Version", uploadContent);

        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        getBrowser().waitInSeconds(2);

        logger.info("Steps7: Click test file title link to open details page and check content.");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFileName));
    }

    @TestRail(id = "C8922")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editOfflineForContentCreatedByOthers()
    {
        logger.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and check 'Edit Offline' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "Edit Offline"), "Edit Offline available for Contributor user");
    }

    @TestRail(id = "C8925")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void checkInOutGoogleDocsCreatedBySelf() throws Exception
    {
        String googleDocPath = testDataFolder + "uploadedDoc.docx";

        logger.info(
                "Preconditions: Create test site and add contributor member to site. As Contributor user, navigate to Document Library page for the test site and create Google Doc file");
        docs.loginToGoogleDocs();
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        // documentLibraryPage.uploadNewFile(googleDocPath);
        uploadContent.uploadContent(googleDocPath);
        getBrowser().waitInSeconds(5);

        logger.info("Steps1: Mouse over file and check 'Edit in Google Docs' action is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("uploadedDoc.docx", "Edit in Google Docs"),
                "Edit in Google Docs available for Contributor user");

        logger.info("Steps2: Click 'Edit in Google Docs' action and add some content");
        documentLibraryPage.clickDocumentLibraryItemAction("uploadedDoc.docx", "Edit in Google Docs", docs);
        getBrowser().waitInSeconds(5);
        docs.clickOkButtonOnTheAuthPopup();
        docs.switchToGoogleDocsWindowandAndEditContent("GDTitle", "Edited");

        logger.info("Steps3: Check the test file's status in Document Library.");
        // getBrowser().waitUntilElementVisible(By.xpath("//img[contains(@title,'Locked by you')]"));
        documentLibraryPage.renderedPage();
        assertTrue(docs.isLockedIconDisplayed(), "Locked icon displayed");
        assertTrue(docs.isLockedDocumentMessageDisplayed(), "Message about the file being locked displayed");
        assertTrue(docs.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        logger.info("Steps4: Mouse over testFile name and verify 'Check In Google Doc' action is available");

        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("uploadedDoc.docx", "Check In Google Doc"),
                "Check In Google Doc available for Contributor user");

        logger.info("Steps5: Click 'Check In Google Doc' and verify Version Information window is displayed.");
        documentLibraryPage.clickDocumentLibraryItemAction("uploadedDoc.docx", "Check In Google Doc", documentLibraryPage);
        getBrowser().waitInSeconds(10);
        Assert.assertEquals(docs.isVersionInformationPopupDisplayed(), true, "Version information pop-up displayed");

        logger.info("Steps6: Click 'Ok' on the Version Information window and verify it is is closed");
        docs.clickOkButton();
        getBrowser().waitInSeconds(10);
        Assert.assertEquals(docs.isVersionInformationPopupDisplayed(), false, "Version Information pop-up displayed");

        logger.info("Steps7: Check the status for the file");
        Assert.assertEquals(docs.checkLockedLAbelIsDisplayed(), false, "Document aapers to be unlocked");

        logger.info("Steps8: testFile name.");
        documentLibraryPage.clickOnFile("GDTitle");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        logger.info("Steps9: Verify the file content is correct");
        Assert.assertTrue(documentDetailsPage.getContentText().replaceAll("\\s+", "").contains("Edited"), "File preview correctly displayed");

    }

    @TestRail(id = "C8926")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editInGoogleDocForContentCreatedByOthers()
    {
        String googleDocName = DataUtil.getUniqueIdentifier() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;

        logger.info(
                "Preconditions: Create test site, add contributor member to site. As admin, navigate to Document Library page for the test site and create Google Doc file");
        docs.loginToGoogleDocs();
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);

        logger.info("Steps1: Login as Contributor user, go to site's doc lib and check whether 'Edit in Google Docs' action is available.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, "Edit in Google Docs"),
                "Edit in Google Docs available for Contributor user");

    }

    @TestRail(id = "C8928")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void checkInGoogleDocForContentCreatedByOthers() throws Exception
    {
        String googleDocName = DataUtil.getUniqueIdentifier() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;

        logger.info(
                "Preconditions: Create test site and add contributor member to site. As Admin user, navigate to Document Library page for the test site and create Google Doc file. Check out the file in google Docs.");
        docs.loginToGoogleDocs();
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        // getBrowser().waitInSeconds(5);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(googleDocName, "Edit in Google Docs", docs);
        getBrowser().waitInSeconds(5);
        docs.clickOkButtonOnTheAuthPopup();
        docs.switchToGoogleDocsWindowandAndEditContent("GDTitle", "Google Doc test content");

        logger.info("Steps1: Login as Contributor user, go to site's doc lib and check whether 'Edit in Google Docs' action is available.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, "Check In Google Doc"),
                "Check In Google Doc available for Contributor user");
    }

    @TestRail(id = "C8929")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void cancelEditingContentLockedBySelf() throws Exception
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();

        logger.info(
                "Preconditions: Create test site, add Contributor member to site and create test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and click 'Edit Offline' action. Verify the file appears as locked.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        getBrowser().refresh();
        Assert.assertTrue(docs.checkLockedLAbelIsDisplayed(), "Document appears to be locked");

        logger.info("Steps2: Hover over testFile and check whether 'Cancel editing' action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Cancel Editing"),
                "Cancel Editing action available for Contributor user");

        logger.info("Steps3: Click 'Cancel Editing' action and check whether the lock is removed for the test file");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Cancel Editing", documentLibraryPage);
        getBrowser().refresh();
        Assert.assertFalse(docs.checkLockedLAbelIsDisplayed(), "Document appears to be locked");
    }

    @TestRail(id = "C8930")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void cancelEditingContentLockedByOthers() throws Exception
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();

        logger.info(
                "Preconditions: Create test site and add Contributor member to site. Create a file in the Document Library for the test site, as admin user.");
        contentService.createDocument(adminUser, adminUser, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);

        logger.info("Step1: Login as admin and lock test file - e.g. for offline editing");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        getBrowser().waitUntilElementVisible(By.xpath("//div[contains(text(), 'This document is locked by you')]"));
        Assert.assertTrue(docs.checkLockedLAbelIsDisplayed(), "Document appears to be locked");

        logger.info("Step2: Login as Contributor user and check whether the file appears as locked by Admin");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getLockedByUserName(), "Administrator", "The document is not locked");

        logger.info("Step3: Hover over test file and check whether 'Cancel Editing' action is missing");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Cancel Editing"), "Cancel Editing available for Contributor user");

    }

    @TestRail(id = "C8931")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewOriginalVersion() throws Exception
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();
        String content = "FileContent" + DataUtil.getUniqueIdentifier();

        logger.info(
                "Preconditions: Create test site, add Contributor member to site and create test file. Navigate to Document Library page for the test site, as admin user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, content);
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and click 'Edit Offline' action. Verify the file appears as locked.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        getBrowser().waitUntilElementVisible(By.xpath("//div[contains(text(), 'This document is locked by you')]"));
        Assert.assertTrue(docs.checkLockedLAbelIsDisplayed(), "Document appears to be locked");

        logger.info("Steps2: Logout and login as Contributor user; hover over testFile and check whether 'View Original Document' action is available");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "View Original Document"),
                "View Original Document' action available for Contributor user");

        logger.info("Steps3: Click 'View Original Document' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "View Original Document", documentDetailsPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), content, "File preview successfully displayed");

        Assert.assertEquals(documentDetailsPage.getLockedMessage(), "This document is locked by Administrator.", "Document appears to be locked by admin user");

    }

    @TestRail(id = "C8932")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewWorkingCopy() throws Exception
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();
        String content = "FileContent" + DataUtil.getUniqueIdentifier();

        logger.info(
                "Preconditions: Create test site, add Contributor member to site and create test file. Navigate to Document Library page for the test site, as admin user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, content);
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps1: Mouse over file and click 'Edit Offline' action. Verify the file appears as locked.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Offline", documentLibraryPage);
        documentLibraryPage.refresh();
        documentLibraryPage.renderedPage();
        Assert.assertTrue(docs.checkLockedLAbelIsDisplayed(), "Document appears to be locked");

        logger.info("Steps2: Logout and login as Contributor user; hover over testFile and click 'View Original Document' option");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "View Original Document", documentDetailsPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), content, "File preview successfully displayed");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Working Copy"));

        logger.info("Steps3: Click 'View Working Copy' action");
        documentDetailsPage.clickDocumentActionsOption("View Working Copy");
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), content, "File preview successfully displayed");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Original Document"));

    }

    @TestRail(id = "C8933")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void editInGoogleDocs() throws Exception
    {
        String googleDocName = DataUtil.getUniqueIdentifier() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        // String googleDocsPageName = googleDocName + " - Google Docs";
        String docsUrl = "https://docs.google.com/document";

        logger.info(
                "Preconditions: Create test site, add Contributor member to site.  As Contributor user, navigate to Document Library page for the test site and upload a .docx file.");
        docs.loginToGoogleDocs();
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);

        logger.info("Step1: Hover over test File and check 'Edit In Google Docs action' is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, "Edit in Google Docs"), "Edit in Google Docs action available");

        logger.info("Step2: Click 'Edit In Google Docs action' and verify the file is opened in Google Docs");

        documentLibraryPage.clickDocumentLibraryItemAction(googleDocName, "Edit in Google Docs", docs);
        docs.clickOkButtonOnTheAuthPopup();
        switchWindow();

        assertTrue(getBrowser().getCurrentUrl().contains(docsUrl),
                "After clicking on Google Docs link, the title is: " + getBrowser().getCurrentUrl());
        // assertEquals(documentLibraryPage.getPageTitle(), googleDocsPageName, "Displayed page=");
        closeWindowAndSwitchBack();
    }

    @TestRail(id = "C8934")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void startWorkflow()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();

        logger.info(
                "Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Mouse over file and click 'Start Workflowe' action. Verify the file appears as locked.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, "Start Workflow"),
                "Start Workflow action available for Contributor user");

        logger.info("Steps2: Click 'Start Workflows' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Start Workflow", startWorkflowPage);

        logger.info("Steps3: From the Select Workflow drop-down select New Task Workflow.");
        startWorkflowPage.selectAWorkflow();
        startWorkflowPage.selectWorkflowToStartFromDropdownList("New Task");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Start Workflow", "Displayed page=");
    }

    @TestRail(id = "C8935")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void locateFile()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();

        logger.info(
                "Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: In Documents Library, go to Documents sections and select Recently Added.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.RecentlyAdded.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentLibraryPage.DocumentsFilters.RecentlyAdded.header,
                "Recently added documents are displayed.");
        if(!documentLibraryPage.isContentNameDisplayed(fileName))
            getBrowser().refresh();

        logger.info("Step2: Hover over test file and click 'Locate File'.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Locate File", documentLibraryPage);
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Collections.singletonList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb is 'Documents'.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), "User is redirected to location of the created document.");
       // On 5.2, the file it's checked
       // assertTrue(documentLibraryPage.isContentSelected(fileName), "Document is checked.");

    }

    @TestRail(id = "C8936")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void downloadPreviousVersion()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();
        String newVersionFileName = "NewVersionC8936.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;

        logger.info(
                "Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, "");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Hover over the test file and click More -> Upload New Version");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Upload New Version", uploadContent);

        logger.info("Steps2,3: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Minor);
        getBrowser().waitInSeconds(2);
        // getBrowser().refresh();
        // documentLibraryPage.renderedPage();

        logger.info("Steps4: Click on name of testFile and verify content is updated");
        documentLibraryPage.clickOnFile(newVersionFileName);
        Assert.assertEquals(documentDetailsPage.getContentText(), "updated by upload new version");

        logger.info("Step5: Verify Version History section.");
        Assert.assertTrue(documentDetailsPage.isRevertButtonAvailable(), "'Download previous version' button available");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"), "Initial version available");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.1"), "New minor version available");

        logger.info(
                "Steps6, 7: Click 'Download' button for previous version. Choose Save file option, location for file to be downloaded and click 'OK' button.");
        documentDetailsPage.clickDownloadPreviousVersion();
        download.acceptAlertIfDisplayed();
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(download.isFileInDirectory(fileName, null), "The file was not found in the specified location");
    }

    @TestRail(id = "C8937")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void revertToPreviousVersion()
    {
        String fileName = "FileName" + DataUtil.getUniqueIdentifier();
        String newVersionFileName = "NewVersionC8936.txt";
        String newVersionFilePath = testDataFolder + newVersionFileName;

        logger.info(
                "Preconditions: Create test site, add Contributor member to site and create a test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, "original content");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Hover over the test file and click More -> Upload New Version");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Upload New Version", uploadContent);

        logger.info("Steps2,3: Click 'Upload New Version' select the updated version for the test file and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Minor);
        documentLibraryPage.renderedPage();
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);

        logger.info("Steps4: Click on name of testFile and verify content is updated");
        documentLibraryPage.clickOnFile(newVersionFileName);
        Assert.assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", "New version's content");

        logger.info("Step5: Verify Version History section.");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"), "Initial version available");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.1"), "New minor version available");
        Assert.assertTrue(documentDetailsPage.isRevertButtonAvailable(), "Revert button available");

        logger.info("Step6: Click on 'Revert' action. Click 'Ok' button on the displayed pop-up for confirmation.");
        documentDetailsPage.clickRevertButton();
        documentDetailsPage.clickOkOnRevertPopup();
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.xpath("//*[contains(text(), 'original content')]"));
        Assert.assertEquals(documentDetailsPage.getContentText(), "original content", "New version's content");
        Assert.assertTrue(documentDetailsPage.isNewVersionAvailable("1.2"), "New minor version created");
    }
}
