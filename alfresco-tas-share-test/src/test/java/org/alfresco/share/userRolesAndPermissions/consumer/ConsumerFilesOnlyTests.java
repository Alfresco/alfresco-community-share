package org.alfresco.share.userRolesAndPermissions.consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConsumerFilesOnlyTests extends ContextAwareWebTest
{
    //@Autowired
    UploadContent uploadContent;

    //@Autowired
    DocumentLibraryPage documentLibraryPage;

    //@Autowired
    DocumentDetailsPage documentDetailsPage;

    //@Autowired
    EditInAlfrescoPage editInAlfrescoPage;

    @Autowired
    GoogleDocsCommon googleDocsCommon;

    //@Autowired
    CreateContentPage createContent;

   // @Autowired
    StartWorkflowPage startWorkflowPage;

    private String user = String.format("ConsumerUser%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C8882SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C8882SiteName%s", RandomData.getRandomAlphanumeric());
    private String textFile = "Text test file";
    private String textFileForWorkflow = "Workflow test file";
    private String locateTextFile = "Locate test file";
    private String lockedTextFile = "Locked text file";
    private String wordFile = "Word test file";
    private String lockedWordFile = "Locked word file";
    private String fileWithVersions = "Text file with versions";
    private String testContent = "testContent";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, siteName, "SiteConsumer");

        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, textFile, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
                textFileForWorkflow, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, locateTextFile, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, lockedTextFile, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.MSWORD, wordFile, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.MSWORD, lockedWordFile, testContent);

        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileWithVersions, testContent);

        contentAction.checkOut(adminUser, adminPassword, siteName, lockedTextFile);
        contentAction.checkOut(adminUser, adminPassword, siteName, lockedWordFile);
        contentAction.checkIn(adminUser, adminPassword, siteName, fileWithVersions, CMISUtil.DocumentType.TEXT_PLAIN,
                testContent + "edited", false, "");
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
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerCreateContent()
    {
        documentLibraryPage.navigate(siteName);
//        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        LOG.info("Step 1: On to Document Library page check that the Create button is grayed out.");
        Assert.assertEquals(documentLibraryPage.getCreateButtonStatusDisabled(), "true", "The create button is not disabled");

        LOG.info("Step 2: Click on the Create button.");
        documentLibraryPage.clickCreateButtonWithoutWait();
        assertFalse(documentLibraryPage.isCreateContentMenuDisplayed(), "Create content menu is displayed");
    }

    @TestRail (id = "C14500")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerUploadContent()
    {
        documentLibraryPage.navigate(siteName);
//        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        LOG.info("Step 1: On to Document Library page check that the Upload button is grayed out.");
        Assert.assertEquals(documentLibraryPage.getUploadButtonStatusDisabled(), "true", "The Upload button is not disabled");
    }

    @TestRail (id = "C8884")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerDownloadContent()
    {
        documentLibraryPage.navigate(siteName);
//        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        LOG.info("Step 1: Mouse over fileC8884 and check that Download action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(textFile, ItemActions.DOWNLOAD));

        LOG.info("Step 2: Click download button");
        documentLibraryPage.clickDocumentLibraryItemAction(textFile, ItemActions.DOWNLOAD);
        documentLibraryPage.acceptAlertIfDisplayed();

        LOG.info("Step 3: Choose 'Save File' option and click 'OK' and verify that the file has been downloaded to the right location");
        Assert.assertTrue(isFileInDirectory(textFile, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8885")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerViewInBrowser()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Mouse over test file and check that View In Browser action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(textFile, ItemActions.VIEW_IN_BROWSER), "View in browser is not available");

        LOG.info("Step 2: Click view in Browser");
        documentLibraryPage.clickDocumentLibraryItemAction(textFile, ItemActions.VIEW_IN_BROWSER);
        assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), testContent, "File content is not correct or file has not be opened in new window");
    }

    @TestRail (id = "C8887")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerUploadNewVersion()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over test file and confirm that Upload New Version action is not available");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(locateTextFile, ItemActions.UPLOAD_NEW_VERSION),
            "Upload New Version is available for user with Consumer role");
    }

    @TestRail (id = "C8888")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerUploadNewVersionForLockedFile()
    {
        LOG.info("Step 1: Mouse over lockedFile and check that Upload New Version option is not available");
        documentLibraryPage.navigate(siteName);
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(lockedTextFile, ItemActions.UPLOAD_NEW_VERSION),
            "Upload New Version is available for user with Consumer role");
    }

    @TestRail (id = "C8892")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerEditInAlfresco()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: mouse over fileC8892 and confirm that Edit in Alfresco option is not available");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(textFile, ItemActions.EDIT_IN_ALFRESCO),
            "Edit in Alfresco is available for user with Consumer role");
    }


    @TestRail (id = "C8905,C8894,C8890")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerHasNoEditActionsForWordDoc()
    {
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over wordFile and confirm that no Edit action is available");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(wordFile, ItemActions.EDIT_IN_MICROSOFT_OFFICE),
                "Edit in Microsoft Office™ is available for user with Consumer role");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(wordFile, ItemActions.EDIT_OFFLINE),
                "Edit Offline is available for user with Consumer role");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(wordFile, ItemActions.EDIT_IN_GOOGLE_DOCS),
            "Edit in Google Docs™ is available for user with Consumer role");
    }

    @TestRail (id = "C8902")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerCancelEditingLockedByOtherUser()
    {
        LOG.info("Step 1: Mouse over fileC8902 and Check that user with Consumer role does not have access to Cancel editing locked by other user. ");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.mouseOverContentItem(lockedWordFile);
        assertFalse(documentLibraryPage.isMoreMenuDisplayed(lockedWordFile), "More menu is available for user with Consumer role");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(lockedWordFile, ItemActions.CANCEL_EDITING),
            "Cancel Editing is available for user with Consumer role");
    }

    @TestRail (id = "C8903,C8904")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerViewOriginalDocumentAndWorkingCopy()
    {
        LOG.info("Step 1: Login with consumer user, navigate to document library.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 2: Mouse over lockedWordFile and check the availability of the View Original Document option");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(lockedWordFile, ItemActions.VIEW_ORIGINAL),
                "View Original Document is not available");
        documentLibraryPage.clickDocumentLibraryItemAction(lockedWordFile, ItemActions.VIEW_ORIGINAL);

        LOG.info("Step 5: Check View Working Copy action availability");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Working Copy"));
        documentDetailsPage.clickDocumentActionsOption("View Working Copy");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Original Document"));
    }

    @TestRail (id = "C8906")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerStartWorkflow()
    {
        LOG.info("Step 1: Mouse over textFileForWorkflow and confirm that Start Workflow option is available");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(textFileForWorkflow, ItemActions.START_WORKFLOW));

        LOG.info("Step 2: Click Start Workflow.");
        documentLibraryPage.clickDocumentLibraryItemAction(textFileForWorkflow, ItemActions.START_WORKFLOW);
//        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Start Workflow", "User is not redirected to the Start Workflow page");
    }

    @TestRail (id = "C8907")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerLocateFile()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: In Documents Library, go to Documents sections and select Recently Added.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.RecentlyAdded.title);

        LOG.info("Step 2: Mouse over testFile and confirm the presence of Locate File.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(locateTextFile, ItemActions.LOCATE_FILE));

        LOG.info("Step 3: Click Locate File");
        documentLibraryPage.clickDocumentLibraryItemAction(locateTextFile, ItemActions.LOCATE_FILE);
        Assert.assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents]", "Folder was not identified as beeing in Documents folder");
    }

    @TestRail (id = "C8908")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerDownloadPreviousVersion()
    {
        LOG.info("Step 1: Navigate to DocumentLibrary and click on fileWithVersions");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileWithVersions);

        LOG.info("Step 2: Previous version (1.0) is available in Version History sections. Press Download");
        Assert.assertEquals(documentDetailsPage.getFileVersion(), "1.1", "Latest version is not 1.1");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"));
        documentDetailsPage.isDownloadButtonDisplayed();
        documentDetailsPage.clickDownloadPreviousVersion();

        LOG.info("Step 3: Choose Save file option, location for file to be downloaded and click OK button.");
        Assert.assertTrue(isFileInDirectory(fileWithVersions, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8909")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerRevertToPreviousVersion()
    {
        LOG.info("Step 1: Navigate to DocumentLibrary and click on fileWithVersions");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileWithVersions);

        LOG.info("Step 2: Previous version (1.0) is available in Version History sections.");
        Assert.assertEquals(documentDetailsPage.getFileVersion(), "1.1", "Latest version is not 1.1");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"));

        LOG.info("Step 3: Check that revert option is not available.");
        assertFalse(documentDetailsPage.isRevertButtonAvailable(), "Revert button is available for user with Consumer rights");
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
        documentLibraryPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);
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
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Untitled Document.docx", ItemActions.CANCEL_EDITING_IN_GOOGLE_DOC));
        cleanupAuthenticatedSession();
        LOG.info("Step 4: Login with admin account and check-in document");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction("Untitled Document.docx", ItemActions.CHECK_IN_GOOGLE_DOC);
        cleanupAuthenticatedSession();
        LOG.info("Step 5: Login with user with consumer role and check available options");
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Untitled Document.docx", ItemActions.EDIT_IN_GOOGLE_DOCS));
    }
}
