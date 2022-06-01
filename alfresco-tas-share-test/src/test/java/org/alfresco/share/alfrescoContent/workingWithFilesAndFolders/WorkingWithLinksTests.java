package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.alfresco.utility.report.Bug;
import org.testng.annotations.*;

/**
 * @author Laura.Capsa
 */
@Slf4j
public class WorkingWithLinksTests extends BaseTest
{
    private String siteName1;
    private String siteName2;
    private String fileC42624;
    private String linkC42624;
    private String folderC42626;
    private String linkC42626;
    private final String newFileC42628 = "EditedFileC7074.txt";
    private final String linkC42628 = "Link to " + newFileC42628;
    private final String newVersionFilePath = testDataFolder + newFileC42628;
    private String fileC42629;
    private String linkC42629;
    private String fileC42630;
    private String linkC42630;
    private String folderC42631;
    private String linkC42631;

    private DocumentLibraryPage documentLibraryPage;
    private CopyMoveUnzipToDialog copyMoveUnzipToDialog;
    private SharedFilesPage sharedFilesPage;
    private DocumentDetailsPage documentDetailsPage;
    private GoogleDocsCommon googleDocsCommon;
    private UploadContent uploadContent;
    private RepositoryPage repositoryPage;
    private DeleteDialog deleteDialog;

    private FileModel fileToCheck;
    private FolderModel folderToCheck;
    private UserModel testUser1;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Creating a testuser1 and site1 created by testuser1");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        site.set(getDataSite().usingUser(testUser1).createPublicRandomSite());
        siteName1 = site.get().getTitle();

        getCmisApi().authenticateUser(testUser1);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        copyMoveUnzipToDialog = new CopyMoveUnzipToDialog(webDriver);
        sharedFilesPage  = new SharedFilesPage(webDriver);
        googleDocsCommon = new GoogleDocsCommon();
        uploadContent = new UploadContent(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();
        fileC42624 = fileToCheck.getName();
        linkC42624 = "Link to " + fileC42624;

        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();
        folderC42626 = folderToCheck.getName();
        linkC42626 = "Link to " + folderC42626;

        site.set(getDataSite().usingUser(testUser1).createPublicRandomSite());
        siteName2 = site.get().getTitle();

        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();
        fileC42629 = fileToCheck.getName();
        linkC42629 = "Link to " + fileC42629;

        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();
        fileC42630 = fileToCheck.getName();
        linkC42630 = "Link to " + fileC42630;

        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();
        folderC42631 = folderToCheck.getName();
        linkC42631 = "Link to " + folderC42631;

        authenticateUsingCookies(testUser1);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C42624")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyDisabledActionsForCreatedLink()
    {
        log.info("STEP1: Go to Document Library of the site"
            + "STEP2: For a file/folder, click on \"Copy to\", select a destination folder");
        documentLibraryPage
            .navigate(siteName1)
            .selectItemAction(fileC42624, ItemActions.COPY_TO);

        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + fileC42624 + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP3: Go to the link's destination " + linkC42624);
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");

        sharedFilesPage
            .assertFileIsDisplayed(linkC42624)
            .assertLikeButtonNotDisplayed(linkC42624)
            .assertCommentButtonNotDisplayed(linkC42624)
            .assertShareButtonNotDisplayed(linkC42624);

        log.info("Step4: Delete the link from the shared folder.....");
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(linkC42624, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42625")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void linkToFileRedirectsToDocDetailsPage()
    {
        log.info("From Document actions, click on [Copy to] option and Select a destination folder and click [Create Link] button");
        documentLibraryPage
            .navigate(siteName1)
            .selectItemAction(fileC42624, ItemActions.COPY_TO);

        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + fileC42624 + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Go to the link's location");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
        sharedFilesPage
            .assertFileIsDisplayed(linkC42624);

        log.info("STEP2: Click on the link to file");
        sharedFilesPage
            .clickOnFile(linkC42624);

        documentDetailsPage
            .assertPageTitleEquals("Alfresco » Document Details")
            .assertContentNameEquals(fileC42624);

        log.info("Step3: Delete the link from the shared folder.....");
        sharedFilesPage.navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(linkC42624, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42626")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void linkToFolderRedirectsToFolderContentsPage()
    {
        log.info("From Document actions, click on \"Copy to\" option");
        documentLibraryPage
            .navigate(siteName1)
            .selectItemAction(folderC42626, ItemActions.COPY_TO);

        log.info("Select a destination folder and click \"Create Link\" button");
        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + folderC42626 + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Go to the link's location");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
        sharedFilesPage
            .assertFileIsDisplayed(linkC42626);

        log.info("STEP2: Click on the link to folder");
        sharedFilesPage
            .clickLinkToFolder(linkC42626);

        repositoryPage
            .assertBrowserPageTitleIs("Alfresco » Repository Browser");
        repositoryPage
            .assertBreadCrumbEquals(folderC42626);

        log.info("Delete link from Shared forlder...");
        sharedFilesPage.navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(linkC42626, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42627")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void linkToLockedDocRedirectsToOriginalDoc()
    {
        logger.info("Precondition1: Login to Share/Google Docs and navigate to Document Library page for the test site; upload a .docx file");
        //setupAuthenticatedSession(userName, password);
        log.info("Precondition2: Go to Document Library of the site. Create link for document");
        documentLibraryPage.navigate(siteName1);
        documentLibraryPage.selectItemAction(fileC42624, ItemActions.COPY_TO);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileC42624 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.selectSharedFilesDestination();
        copyMoveUnzipToDialog.clickCreateLinkButton();
        log.info("STEP1: Lock the document, e.g: edit it Google Docs");
        documentLibraryPage.navigate(siteName1);
        documentLibraryPage.selectItemAction(fileC42624, ItemActions.EDIT_OFFLINE);
        log.info("STEP2: Go to the location where the link was created");
        sharedFilesPage.navigate();
        //        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(linkC42624), linkC42624 + " is displayed in destination of copy file, Shared Files.");
        log.info("STEP3: Click on the link");
        documentLibraryPage.clickOnFile(linkC42624);
        //        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        assertEquals(documentDetailsPage.getFileName(), fileC42624, "Document name=");
        sharedFilesPage
            .navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(linkC42624, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    //    @Bug (id = "MNT-18059", status = Bug.Status.FIXED)
    @TestRail (id = "C42628")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void linkToMultipleVersionsDocRedirectsToLastVersion()
    {
        documentLibraryPage.navigate(siteName1);
        log.info("Precondition1: 'Upload new version' for a file");
        documentLibraryPage.selectItemAction(fileC42624, ItemActions.UPLOAD_NEW_VERSION);
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFileC42628), String.format("File [%s] is displayed.", newFileC42628));
        log.info("Precondition2: Create link for the file with new version " + newFileC42628);
        documentLibraryPage.selectItemAction(newFileC42628, ItemActions.COPY_TO);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + newFileC42628 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.selectSharedFilesDestination();
        copyMoveUnzipToDialog.clickCreateLinkButton();
        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        //        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        log.info("STEP2: Click on the created link");
        sharedFilesPage.clickOnFile(linkC42628);
        //        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        assertEquals(documentDetailsPage.getFileName(), newFileC42628, "Document name=");

        sharedFilesPage
            .navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(linkC42628, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42629")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void verifyDisplayedActionsForLinkToFile()
    {
        log.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.selectItemAction(fileC42629, ItemActions.COPY_TO);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileC42629 + " to...", "Displayed dialog=");
        log.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog.selectSharedFilesDestination();
        copyMoveUnzipToDialog.clickCreateLinkButton();
        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        //        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(linkC42629), linkC42629 + " is displayed in destination of copy file, Shared Files.");
        log.info("STEP2: Verify available actions for " + linkC42629);
        assertTrue(sharedFilesPage.isActionAvailableForLibraryItem(linkC42629, ItemActions.LOCATE_LINKED_ITEM),
            "'Locate Linked Item' action is displayed for " + linkC42629);
        assertTrue(sharedFilesPage.isActionAvailableForLibraryItem(linkC42629, ItemActions.DELETE_LINK),
            "'Delete Link' action is displayed for " + linkC42629);
        assertTrue(sharedFilesPage.isActionAvailableForLibraryItem(linkC42629, ItemActions.COPY_TO),
            "'Copy to...' action is displayed for " + linkC42629);
        assertTrue(sharedFilesPage.isActionAvailableForLibraryItem(linkC42629, ItemActions.MOVE_TO),
            "'Move to...' action is displayed for " + linkC42629);
    }

    @Bug (id = "MNT-17556", description = "Step 2: file is not selected")
    @TestRail (id = "C42630")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateLinkedItemRedirectsToOriginalDoc()
    {
        log.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.selectItemAction(fileC42630, ItemActions.COPY_TO);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileC42630 + " to...", "Displayed dialog=");
        log.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog.selectSharedFilesDestination();
        copyMoveUnzipToDialog.clickCreateLinkButton();
        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        //        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42630), linkC42630 + " is displayed in destination of copy file, Shared Files.");
        log.info("STEP2: Mouse over the link and click on 'Locate Linked Item' option");
        sharedFilesPage.selectItemAction(linkC42630, ItemActions.LOCATE_LINKED_ITEM);
        //        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "Displayed page=");
        assertTrue(repositoryPage.isContentSelected(fileC42630), fileC42630 + " is selected");
    }

    @Bug (id = "MNT-17556", description = "Step 2: folder is not selected")
    @TestRail (id = "C42631")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateLinkedItemRedirectsToFolder()
    {
        log.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.selectItemAction(folderC42631, ItemActions.COPY_TO);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + folderC42631 + " to...", "Displayed dialog=");
        log.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog.selectSharedFilesDestination();
        copyMoveUnzipToDialog.clickCreateLinkButton();
        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        //        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42631), linkC42631 + " is displayed in destination of copy file, Shared Files.");
        log.info("STEP2: Mouse over the link and click on 'Locate Linked Item' option");
        sharedFilesPage.selectItemAction(linkC42631, ItemActions.LOCATE_LINKED_ITEM);
        //        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "Displayed page=");
        assertTrue(repositoryPage.isContentSelected(folderC42631), folderC42631 + " is selected");
    }

    @Bug (id = "MNT-17556", description = "Step 2: folder is not selected")
    @TestRail (id = "C42632")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void deleteLinkRemovesLink()
    {
        log.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.selectItemAction(folderC42631, ItemActions.COPY_TO);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + folderC42631 + " to...", "Displayed dialog=");
        log.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog.selectSharedFilesDestination();
        copyMoveUnzipToDialog.clickCreateLinkButton();
        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage.navigate();
        //        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkC42631), linkC42631 + " is displayed in destination of copy file, Shared Files.");
        log.info("STEP2: Mouse over the link and click on 'Delete Link' option");
        sharedFilesPage.selectItemAction(linkC42631, ItemActions.DELETE_LINK);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.contentActions.deleteDocument"), "Displayed dialog=");
        log.info("STEP3: In the Delete confirmation dialog press 'Delete' button");
        deleteDialog.confirmDeletion();
        assertFalse(sharedFilesPage.isContentNameDisplayed(linkC42631), linkC42631 + " is displayed in destination of copy file, Shared Files.");
    }
}