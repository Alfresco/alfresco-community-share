package org.alfresco.share.userRolesAndPermissions.consumer;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.utility.web.AbstractWebTest.getBrowser;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ConsumerFilesOnlyTests extends BaseTest
{
    @Autowired
    GoogleDocsCommon googleDocsCommon;
    @Autowired
    UserService userService;
    @Autowired
    ContentActions contentAction;
    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String textFile = "Text test file";
    private String textFileForWorkflow = "Workflow test file";
    private String locateTextFile = "Locate test file";
    private String lockedTextFile = "Locked text file";
    private String wordFile = "Word test file";
    private String lockedWordFile = "Locked word file";
    private String fileWithVersions = "Text file with versions";
    private String testContent = "testContent";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(getAdminUser()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), siteName.get().getId(), "SiteConsumer");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, textFile, testContent);
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN,
                textFileForWorkflow, testContent);
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, locateTextFile, testContent);
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, lockedTextFile, testContent);
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.MSWORD, wordFile, testContent);
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.MSWORD, lockedWordFile, testContent);

        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileWithVersions, testContent);

        contentAction.checkOut(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), lockedTextFile);
        contentAction.checkOut(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), lockedWordFile);
        contentAction.checkIn(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), fileWithVersions, CMISUtil.DocumentType.TEXT_PLAIN,
                testContent + "edited", false, "");

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C14502")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerCreateContent()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: On to Document Library page check that the Create button is grayed out.");
        Assert.assertEquals(documentLibraryPage.getCreateButtonStatusDisabled(), "true", "The create button is not disabled");

        log.info("Step 2: Click on the Create button.");
        documentLibraryPage.clickCreateButtonWithoutWait();
        assertFalse(documentLibraryPage.isCreateContentMenuDisplayed(), "Create content menu is displayed");
    }

    @TestRail (id = "C14500")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerUploadContent()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: On to Document Library page check that the Upload button is grayed out.");
        Assert.assertEquals(documentLibraryPage.getUploadButtonStatusDisabled(), "true", "The Upload button is not disabled");
    }

    @TestRail (id = "C8884")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "DownloadTest" })
    public void consumerDownloadContent()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: Mouse over fileC8884 and check that Download action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForConsumerLibraryItem(textFile, ItemActions.DOWNLOAD));

        log.info("Step 2: Click download button");
        documentLibraryPage.selectConsumerItemAction(textFile, ItemActions.DOWNLOAD);
        documentLibraryPage.acceptAlertIfDisplayed();

        log.info("Step 3: Choose 'Save File' option and click 'OK' and verify that the file has been downloaded to the right location");
        Assert.assertTrue(isFileInDirectory(textFile, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8885")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerViewInBrowser()
    {
        documentLibraryPage.navigate(siteName.get().getId());

        log.info("Step 1: Mouse over test file and check that View In Browser action is available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForConsumerLibraryItem(textFile, ItemActions.VIEW_IN_BROWSER), "View in browser is not available");

        log.info("Step 2: Click view in Browser");
        documentLibraryPage.selectConsumerItemAction(textFile, ItemActions.VIEW_IN_BROWSER);
       // assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), testContent, "File content is not correct or file has not be opened in new window");
    }

    @TestRail (id = "C8887")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerUploadNewVersion()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step 1: Mouse over test file and confirm that Upload New Version action is not available");
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(locateTextFile, ItemActions.UPLOAD_NEW_VERSION),
            "Upload New Version is available for user with Consumer role");
    }

    @TestRail (id = "C8888")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerUploadNewVersionForLockedFile()
    {
        log.info("Step 1: Mouse over lockedFile and check that Upload New Version option is not available");
        documentLibraryPage.navigate(siteName.get().getId());
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(lockedTextFile, ItemActions.UPLOAD_NEW_VERSION),
            "Upload New Version is available for user with Consumer role");
    }

    @TestRail (id = "C8892")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerEditInAlfresco()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step 1: mouse over fileC8892 and confirm that Edit in Alfresco option is not available");
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(textFile, ItemActions.EDIT_IN_ALFRESCO),
            "Edit in Alfresco is available for user with Consumer role");
    }


    @TestRail (id = "C8905,C8894,C8890")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerHasNoEditActionsForWordDoc()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step 1: Mouse over wordFile and confirm that no Edit action is available");
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(wordFile, ItemActions.EDIT_IN_MICROSOFT_OFFICE),
                "Edit in Microsoft Office™ is available for user with Consumer role");
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(wordFile, ItemActions.EDIT_OFFLINE),
                "Edit Offline is available for user with Consumer role");
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(wordFile, ItemActions.EDIT_IN_GOOGLE_DOCS),
            "Edit in Google Docs™ is available for user with Consumer role");
    }

    @TestRail (id = "C8902")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerCancelEditingLockedByOtherUser()
    {
        log.info("Step 1: Mouse over fileC8902 and Check that user with Consumer role does not have access to Cancel editing locked by other user. ");
        documentLibraryPage.navigate(siteName.get().getId());
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(lockedWordFile, ItemActions.CANCEL_EDITING),
            "Cancel Editing is available for user with Consumer role");
        assertFalse(documentLibraryPage.isMoreMenuDisplayed(lockedWordFile), "More menu is available for user with Consumer role");
    }

    @TestRail (id = "C8903,C8904")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerViewOriginalDocumentAndWorkingCopy()
    {
        log.info("Step 1: Login with consumer user, navigate to document library.");
        documentLibraryPage.navigate(siteName.get().getId());

        log.info("Step 2: Mouse over lockedWordFile and check the availability of the View Original Document option");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForConsumerLibraryItem(lockedWordFile, ItemActions.VIEW_ORIGINAL),
                "View Original Document is not available");
        documentLibraryPage.selectConsumerItemAction(lockedWordFile, ItemActions.VIEW_ORIGINAL);

        log.info("Step 5: Check View Working Copy action availability");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Working Copy"));
        documentDetailsPage.clickDocumentActionsOption("View Working Copy");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Original Document"));
    }

    @TestRail (id = "C8906")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerStartWorkflow()
    {
        log.info("Step 1: Mouse over textFileForWorkflow and confirm that Start Workflow option is available");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(documentLibraryPage.isActionAvailableForConsumerLibraryItem(textFileForWorkflow, ItemActions.START_WORKFLOW));

        log.info("Step 2: Click Start Workflow.");
        documentLibraryPage.selectConsumerItemAction(textFileForWorkflow, ItemActions.START_WORKFLOW);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Start Workflow", "User is not redirected to the Start Workflow page");
    }

    @TestRail (id = "C8907")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerLocateFile()
    {
        documentLibraryPage.navigate(siteName.get().getId());

        log.info("Step 1: In Documents Library, go to Documents sections and select Recently Added.");
        documentLibraryPage.clickDocumentsConsumerFilterOption(DocumentLibraryPage.DocumentsFilters.RecentlyAdded.title);

        log.info("Step 2: Mouse over testFile and confirm the presence of Locate File.");
        documentLibraryPage.browserRefresh();
        Assert.assertTrue(documentLibraryPage.isActionAvailableForConsumerLibraryItem(locateTextFile, ItemActions.LOCATE_FILE));

        log.info("Step 3: Click Locate File");
        documentLibraryPage.selectConsumerItemAction(locateTextFile, ItemActions.LOCATE_FILE);
        Assert.assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents]", "Folder was not identified as beeing in Documents folder");
    }

    @TestRail (id = "C8908")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "DownloadTest" })
    public void consumerDownloadPreviousVersion()
    {
        log.info("Step 1: Navigate to DocumentLibrary and click on fileWithVersions");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFile(fileWithVersions);

        log.info("Step 2: Previous version (1.0) is available in Version History sections. Press Download");
        Assert.assertEquals(documentDetailsPage.getFileVersion(), "1.1", "Latest version is not 1.1");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"));
        documentDetailsPage.isDownloadButtonDisplayed();
        documentDetailsPage.clickDownloadPreviousVersion();

        log.info("Step 3: Choose Save file option, location for file to be downloaded and click OK button.");
        Assert.assertTrue(isFileInDirectory(fileWithVersions, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8909")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerRevertToPreviousVersion()
    {
        log.info("Step 1: Navigate to DocumentLibrary and click on fileWithVersions");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFile(fileWithVersions);

        log.info("Step 2: Previous version (1.0) is available in Version History sections.");
        Assert.assertEquals(documentDetailsPage.getFileVersion(), "1.1", "Latest version is not 1.1");
        Assert.assertTrue(documentDetailsPage.isVersionAvailable("1.0"));

        log.info("Step 3: Check that revert option is not available.");
        assertFalse(documentDetailsPage.isRevertButtonAvailable(), "Revert button is available for user with Consumer rights");
    }

    @TestRail (id = "C8898")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void consumerCheckoutToGoogleDocsFileCreateByOtherUser() throws Exception
    {
        googleDocsCommon.loginToGoogleDocs();
        log.info("Step 1: Create Google Doc document using admin user");
        authenticateUsingLoginPage(user.get());
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);
        googleDocsCommon.clickOkButtonOnTheAuthPopup();
        getBrowser().switchWindow(1);
        googleDocsCommon.editGoogleDocsContent("testC8898");
        getBrowser().closeWindowAndSwitchBack();

        documentLibraryPage.navigate(siteName.get().getId());
        assertTrue(documentLibraryPage.isContentNameDisplayed("Untitled Document.docx"));

        log.info("Step 2: Logout and login using the Consumer user");
        cleanup();
        authenticateUsingLoginPage(user.get());

        log.info("Step 3: Navigate to Document Library and check that user with consumer role does not have access to Cancel Editing or Edit Google Doc");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Untitled Document.docx", ItemActions.CANCEL_EDITING_IN_GOOGLE_DOC));
        cleanup();
        log.info("Step 4: Login with admin account and check-in document");
        authenticateUsingLoginPage(user.get());
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.selectItemAction("Untitled Document.docx", ItemActions.CHECK_IN_GOOGLE_DOC);
        cleanup();
        log.info("Step 5: Login with user with consumer role and check available options");
        authenticateUsingLoginPage(user.get());
        documentLibraryPage.navigate(siteName.get().getId());
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Untitled Document.docx", ItemActions.EDIT_IN_GOOGLE_DOCS));
    }
}
