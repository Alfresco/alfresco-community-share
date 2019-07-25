package org.alfresco.share.userRolesAndPermissions.consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConsumerFilesOnlyTests extends ContextAwareWebTest
{
    @Autowired
    UploadContent uploadContent;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    EditInAlfrescoPage editInAlfrescoPage;

    @Autowired
    GoogleDocsCommon googleDocsCommon;

    @Autowired
    CreateContent createContent;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @Autowired
    Download download;

    private String user = String.format("ConsumerUser%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C8882SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C8882SiteName%s", RandomData.getRandomAlphanumeric());
    private String fileC8884 = "C8884 test file";
    private String fileC8885 = "C8885 test file";
    private String fileC8887 = "C8887 test file";
    private String fileC8888 = "C8888 test file";
    private String fileC8890 = "C8890 test file";
    private String fileC8892 = "C8892 test file";
    private String fileC8894 = "C8894 test file";
    private String fileC8905 = "C8905 test file";
    private String fileC8902 = "C8902 test file";
    private String fileC8903 = "C8903 test file";
    private String fileC8904 = "C8904 test file";
    private String fileC8906 = "C8906 test file";
    private String fileC8907 = "C8907 test file";
    private String fileC8908 = "C8908 test file";
    private String fileC8909 = "C8909 test file";
    private String testContent = "testContent";
    private String windowsUser;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, siteName, "SiteConsumer");
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8884, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8885, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8887, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8888, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.MSWORD, fileC8890, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8892, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.MSWORD, fileC8894, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.MSWORD, fileC8905, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.MSWORD, fileC8902, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.MSWORD, fileC8903, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.MSWORD, fileC8904, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8906, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8907, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8908, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8909, testContent);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @BeforeMethod (alwaysRun = true)
    public void authenticateConsumer()
    {
        setupAuthenticatedSession(user, password);
    }

    @TestRail (id = "C14502")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerCreateContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        LOG.info("Step 1: On to Document Library page check that the Create button is grayed out.");
        Assert.assertEquals(documentLibraryPage.getCreateButtonStatusDisabled(), "true", "The create button is not disabled");

        LOG.info("Step 2: Click on the Create button.");
        documentLibraryPage.clickCreateButtonWithoutWait();
        Assert.assertFalse(documentLibraryPage.isCreateContentMenuDisplayed(), "Create content menu is displayed");
    }

    @TestRail (id = "C14500")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerUploadContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        LOG.info("Step 1: On to Document Library page check that the Upload button is grayed out.");
        Assert.assertEquals(documentLibraryPage.getUploadButtonStatusDisabled(), "true", "The Upload button is not disabled");

        LOG.info("Step 2: Click on the Upload button.");
        documentLibraryPage.clickUpload();
        Assert.assertFalse(uploadContent.isUploadFilesToDialogDisplayed(), "Upload files dialog is displayed");
    }

    @TestRail (id = "C8884")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerDownloadContent()
    {
        windowsUser = System.getProperty("user.name");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        LOG.info("Step 1: Mouse over fileC8884 and check that Download action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileC8884, "Download"));

        LOG.info("Step 2: Click download button");
        documentLibraryPage.mouseOverContentItem(fileC8884);
        documentLibraryPage.clickDownloadForItem(fileC8884);

        download.acceptAlertIfDisplayed();
        getBrowser().waitInSeconds(2);

        LOG.info("Step 3: Choose 'Save File' option and click 'OK' and verify that the file has been downloaded to the right location");
        Assert.assertTrue(download.isFileInDirectory(fileC8884, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8885")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerViewInBrowser()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Mouse over test file and check that View In Browser action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileC8885, "View In Browser"), "View in browser is not available");

        LOG.info("Step 2: Click view in Browser");
        documentLibraryPage.clickAction(fileC8885, "View In Browser");
        getBrowser().waitInSeconds(2);
        assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), testContent, "File content is not correct or file has not be opened in new window");
    }

    @TestRail (id = "C8887")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerUploadNewVersionCreatedByOtherUser()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over test file and confirm that Upload New Version action is not available");

        documentLibraryPage.mouseOverContentItem(fileC8887);
        Assert.assertFalse(documentLibraryPage.isMoreMenuDisplayed(fileC8887), "More menu is available for user with Consumer role");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8887, "Upload New Version"),
            "Upload New Version is available for user with Consumer role");
    }

    @TestRail (id = "C8888")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerUploadNewVersionLockedByOtherUser()
    {
        LOG.info("Preconditions: log in with different user and lock file for editing");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8888, "Edit Offline", documentLibraryPage);
        getBrowser().waitInSeconds(2);
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user, password);

        LOG.info("Step 1: Mouse over fileC8888 and check that Upload New Version option is not available");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.mouseOverContentItem(fileC8888);
        Assert.assertFalse(documentLibraryPage.isMoreMenuDisplayed(fileC8888), "More menu is available for user with Consumer role");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8888, "Upload New Version"),
            "Upload New Version is available for user with Consumer role");
    }

    @TestRail (id = "C8890")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerEditOnlineCreatedAddedByOtherUser()
    {
        LOG.info("Step 1: Mouse over fileC8890 and confirm that edit in Microsoft Office is not available");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.mouseOverContentItem(fileC8890);
        Assert.assertFalse(documentLibraryPage.isMoreMenuDisplayed(fileC8890), "More menu is available for user with Consumer role");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8890, "Edit in Microsoft Office™"),
            "Edit in Microsoft Office™ is available for user with Consumer role");
    }

    @TestRail (id = "C8892")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerEditInlineCreatedAddedByOtherUser()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: mouse over fileC8892 and confirm that Edit in Alfresco option is not available");
        documentLibraryPage.mouseOverContentItem(fileC8892);
        Assert.assertFalse(documentLibraryPage.isMoreMenuDisplayed(fileC8892), "More menu is available for user with Consumer role");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8892, "Edit in Alfresco"),
            "Edit in Alfresco is available for user with Consumer role");
    }

    @TestRail (id = "C8894")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerEditOfflineCreatedAddedByOtherUser()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: mouse over fileC8892 and confirm that Edit Offline option is not available");
        documentLibraryPage.mouseOverContentItem(fileC8894);
        Assert.assertFalse(documentLibraryPage.isMoreMenuDisplayed(fileC8894), "More menu is available for user with Consumer role");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8894, "Edit Offline"),
            "Edit Offline is available for user with Consumer role");
    }

    @TestRail (id = "C8905")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })

    public void consumerEditInGoogleDocs()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: mouse over fileC8892 and confirm that Edit Offline option is not available");
        documentLibraryPage.mouseOverContentItem(fileC8905);
        Assert.assertFalse(documentLibraryPage.isMoreMenuDisplayed(fileC8905), "More menu is available for user with Consumer role");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8905, "Edit in Google Docs™"),
            "Edit in Google Docs™ is available for user with Consumer role");
    }

    @TestRail (id = "C8902")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerCancelEditingLockedByOtherUser()
    {
        LOG.info("Preconditions: log in with different user and lock file for editing");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8902, "Edit Offline", documentLibraryPage);
        getBrowser().waitInSeconds(2);
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user, password);

        LOG.info("Step 1: Mouse over fileC8902 and Check that user with Consumer role does not have access to Cancel editing locked by other user. ");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.mouseOverContentItem(fileC8902);
        Assert.assertFalse(documentLibraryPage.isMoreMenuDisplayed(fileC8902), "More menu is available for user with Consumer role");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8902, "Cancel Editing"),
            "Cancel Editing is available for user with Consumer role");
    }

    @TestRail (id = "C8903")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerViewOriginalDocument()
    {
        LOG.info("Step 1: Login with admin user and Mouse over testFile and click Edit Offline");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8903, "Edit Offline", documentLibraryPage);
        getBrowser().waitInSeconds(2);

        LOG.info("Step 2 & Step 3: Logout and Login with collaborator user, navigate to document library.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 4: Mouse over testFile and check the availability of the View Original Document option");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileC8903, "View Original Document"), "View Original Document is not available");
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8903, "View Original Document", documentDetailsPage);
        // Assert.assertEquals(documentDetailsPage.getContentText(), testContent, "Document content is not correct");
    }

    @TestRail (id = "C8904")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void consumerViewWorkingCopy()
    {
        LOG.info("Step 1: Login with admin user and Mouse over testFile and click Edit Offline");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8904, "Edit Offline", documentLibraryPage);
        getBrowser().waitInSeconds(2);

        LOG.info("Step 2 & Step 3: Logout and Login with collaborator user, navigate to document library.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 4: Mouse over testFile and check the availability of the View Original Document option");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileC8904, "View Original Document"), "View Original Document is not available");
        documentLibraryPage.mouseOverContentItem(fileC8904);
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8904, "View Original Document", documentDetailsPage);

        LOG.info("Step 5: Check View Working Copy action availability");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Working Copy"));
        documentDetailsPage.clickDocumentActionsOption("View Working Copy");
        // Assert.assertEquals(documentDetailsPage.getContentText(), testContent, "Document content is not correct");
    }

    @TestRail (id = "C8906")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerStartWorkflow()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Mouse over fileC8906 and confirm that Start Workflow option is available");
        documentLibraryPage.mouseOverContentItem(fileC8906);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileC8906, "Start Workflow"));

        LOG.info("Step 2: Click Start Workflow.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8906, "Start Workflow", startWorkflowPage);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Start Workflow", "User is not redirected to the Start Workflow page");
    }

    @TestRail (id = "C8907")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerLocateFile()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: In Documents Library, go to Documents sections and select Recently Added.");
        documentLibraryPage.clickDocumentsFilterOption("Recently Added");

        LOG.info("Step 2: Mouse over testFile and confirm the presence of Locate File.");

        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileC8907, "Locate File"));

        LOG.info("Step 3: Click Locate File");
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8907, "Locate File", documentLibraryPage);
        getBrowser().waitInSeconds(2);
        Assert.assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents]", "Folder was not identified as beeing in Documents folder");
    }

    @TestRail (id = "C8908")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerDownloadPreviousVersion()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over the testFile and click More -> Edit in Alfresco");
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8908, "Edit in Alfresco", editInAlfrescoPage);

        LOG.info("Step 2: Type 'test' in the content of the file. Press Save.");
        editInAlfrescoPage.typeContent("test edited content");
        editInAlfrescoPage.clickSaveButton();
        cleanupAuthenticatedSession();
        LOG.info("Step 3: Logout and login with consumer user");
        setupAuthenticatedSession(user, password);

        LOG.info("Step 4: Navigate to DocumentLibrary and click on fileC8908");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileC8908);

        LOG.info("Step 5: Previous version (1.0) is available in Version History sections. Press Download");
        Assert.assertEquals(documentDetailsPage.getFileVersion(), "1.1", "Latest version is not 1.1");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"));
        documentDetailsPage.isDownloadButtonDisplayed();
        documentDetailsPage.clickDownloadPreviousVersion();
        download.acceptAlertIfDisplayed();
        getBrowser().waitInSeconds(2);

        LOG.info("Step 6: Choose Save file option, location for file to be downloaded and click OK button.");
        Assert.assertTrue(download.isFileInDirectory(fileC8908, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8909")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })

    public void consumerRevertToPreviousVersion()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over the testFile and click More -> Edit in Alfresco");
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8909, "Edit in Alfresco", editInAlfrescoPage);

        LOG.info("Step 2: Type 'test' in the content of the file. Press Save.");
        editInAlfrescoPage.typeContent("test edited content");
        editInAlfrescoPage.clickSaveButton();
        cleanupAuthenticatedSession();
        LOG.info("Step 3: Logout and login with consumer user");
        setupAuthenticatedSession(user, password);

        LOG.info("Step 4: Navigate to DocumentLibrary and click on fileC8908");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileC8909);

        LOG.info("Step 5: Previous version (1.0) is available in Version History sections. Press Download");
        Assert.assertEquals(documentDetailsPage.getFileVersion(), "1.1", "Latest version is not 1.1");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"));

        LOG.info("Step 6: Check that revert option is not available.");
        Assert.assertFalse(documentDetailsPage.isRevertButtonAvailable(), "Revert button is available for user with Consumer rights");
    }

    @TestRail (id = "C8898")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void consumerCheckoutToGoogleDocsFileCreateByOtherUser() throws Exception
    {
        googleDocsCommon.loginToGoogleDocs();
        LOG.info("Step 1: Create Google Doc document using admin user");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        googleDocsCommon.clickOkButtonOnTheAuthPopup();
        getBrowser().switchWindow(1);
        googleDocsCommon.editGoogleDocsContent("testC8898");
        getBrowser().closeWindowAndSwitchBack();

        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Document.docx"));

        LOG.info("Step 2: Logout and login using the Consumer user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user, password);

        LOG.info("Step 3: Navigate to Document Library and check that user with consumer role does not have access to Cancel Editing or Edit Google Doc");
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Untitled Document.docx", "Cancel Editing in Google Docs™"));
        cleanupAuthenticatedSession();
        LOG.info("Step 4: Login with admin account and check-in document");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction("Untitled Document.docx", "Check In Google Doc™", documentLibraryPage);
        cleanupAuthenticatedSession();
        LOG.info("Step 5: Login with user with consumer role and check available options");
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Untitled Document.docx", "Edit in Google Docs™"));
    }
}
