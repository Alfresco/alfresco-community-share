package org.alfresco.share.userRolesAndPermissions.contributor;

import static org.testng.Assert.assertEquals;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ContributorFilesCreatedByOthersTests extends ContextAwareWebTest
{
    private final String userContributor = String.format("Contributor%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String adminFile = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
    private final String lockedFileByAdmin = String.format("LockedFileByAdmin%s", RandomData.getRandomAlphanumeric());
    private final String adminWordFile = String.format("WordFile%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = "FileContent";
    private final String deletePath = String.format("Sites/%s/documentLibrary", siteName);
    //@Autowired
    UploadContent uploadContent;
    //@Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @Autowired
    CreateContentPage create;
   // @Autowired
    EditInAlfrescoPage editInAlfresco;
    @Autowired
    GoogleDocsCommon docs;

    //@Autowired
    StartWorkflowPage startWorkflowPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, userContributor, userContributor);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteName, "SiteContributor");

        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, adminFile, fileContent);
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, lockedFileByAdmin, fileContent);
        contentAction.checkOut(adminUser, adminPassword, siteName, lockedFileByAdmin);
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.MSWORD, adminWordFile, fileContent);

        setupAuthenticatedSession(userContributor, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C8912")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void downloadContent()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1, 2: Mouse over the test file  from Document Library and click 'Download'.");
        documentLibraryPage.clickDocumentLibraryItemAction(adminFile, ItemActions.DOWNLOAD);
        documentLibraryPage.acceptAlertIfDisplayed();
        Assert.assertTrue(isFileInDirectory(adminFile, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8913")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void viewInBrowser()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Mouse over testFile and check 'View in Browser' is available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, ItemActions.VIEW_IN_BROWSER), "View in browser available");
        LOG.info("Step2: Click View in browser and verify the file is opened in a new browser window.");
        documentLibraryPage.clickDocumentLibraryItemAction(adminFile, ItemActions.VIEW_IN_BROWSER);
        assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), fileContent, "Correct file content/ file opened in new window");
    }

    @TestRail (id = "C8915")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void uploadNewVersionForItemCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step1: Mouse over test File and check 'Upload new version' action is not available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, ItemActions.UPLOAD_NEW_VERSION),
            "Upload New Version available for Contributor user");
    }

    @TestRail (id = "C8916")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void uploadNewVersionForItemLockedByUser()
    {
        LOG.info("Steps1:  Navigate to test site's doc lib and verify the lockedFileByAdmin is locked by admin");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getLockedByUserName(lockedFileByAdmin), "Administrator",
                "Document appears to be locked by admin");
        LOG.info("Steps2: Verify 'Upload new Version' option is not available for Contributor user, since the file is locked by admin");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(lockedFileByAdmin, ItemActions.UPLOAD_NEW_VERSION),
            "Upload New Version available for Contributor user");
    }

    @TestRail (id = "C8918")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "office" })
    public void editOnlineForContentCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit in Microsoft Office' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminWordFile, ItemActions.EDIT_IN_MICROSOFT_OFFICE),
            "Edit in Microsoft Office available for Contributor user");
    }

    @TestRail (id = "C8920")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editInlineForContentCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit in Alfresco' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, ItemActions.EDIT_IN_ALFRESCO),
            "Edit in Alfresco available for Contributor user");
    }

    @TestRail (id = "C8922")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editOfflineForContentCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Steps1: Mouse over file and check 'Edit Offline' action is available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, ItemActions.EDIT_OFFLINE), "Edit Offline available for Contributor user");
    }

    @TestRail (id = "C8926")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void editInGoogleDocForContentCreatedByOthers()
    {
//        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        LOG.info(
            "Preconditions: Create test site, add contributor member to site. As admin, navigate to Document Library page for the test site and create Google Doc file");
        //     docs.loginToGoogleDocs();
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        LOG.info("Steps1: Login as Contributor user, go to site's doc lib and check whether 'Edit in Google Docs' action is available.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS),
            "Edit in Google Docs available for Contributor user");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, googleDocName));

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8928")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void checkInGoogleDocForContentCreatedByOthers() throws Exception
    {
        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        LOG.info(
            "Preconditions: Create test site and add contributor member to site. As Admin user, navigate to Document Library page for the test site and create Google Doc file. Check out the file in google Docs.");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        uploadContent.uploadContent(googleDocPath);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS);
        getBrowser().waitInSeconds(5);
        docs.clickOkButtonOnTheAuthPopup();
        docs.switchToGoogleDocsWindowandAndEditContent("GDTitle", "Google Doc test content");
        LOG.info("Steps1: Login as Contributor user, go to site's doc lib and check whether 'Edit in Google Docs' action is available.");
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, ItemActions.CHECK_IN_GOOGLE_DOC),
            "Check In Google Doc available for Contributor user");
    }

    @TestRail (id = "C8930")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void cancelEditingContentLockedByOthers()
    {
        LOG.info("Step2: Login as Contributor user and check whether the file appears as locked by Admin");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getLockedByUserName(lockedFileByAdmin), "Administrator", "The document is not locked");
        LOG.info("Step3: Hover over test file and check whether 'Cancel Editing' action is missing");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(lockedFileByAdmin, ItemActions.CANCEL_EDITING),
                "Cancel Editing available for Contributor user");
    }

    @TestRail (id = "C8931, C8932")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void viewOriginalDocumentAndWorkingCopy()
    {
        LOG.info("Steps2: Logout and login as Contributor user; hover over testFile and check whether 'View Original Document' action is available");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(lockedFileByAdmin, ItemActions.VIEW_ORIGINAL),
            "View Original Document' action available for Contributor user");
        LOG.info("Steps3: Click 'View Original Document' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(lockedFileByAdmin, ItemActions.VIEW_ORIGINAL);
        Assert.assertEquals(documentDetailsPage.getLockedMessage(), "This document is locked by Administrator.", "Document appears to be locked by admin user");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Working Copy"));
        LOG.info("Steps3: Click 'View Working Copy' action");
        documentDetailsPage.clickDocumentActionsOption("View Working Copy");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Original Document"));
    }
}
