package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import static org.alfresco.common.Utils.testDataFolder;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

/**
 * @author Laura.Capsa
 */
@Slf4j
public class WorkingWithLinksTests extends BaseTest
{
    private String testSite;
    private String testFile;
    private String testFileLink;
    private String testFolder;
    private String testFolderLink;
    private final String newEditedTestFile = "EditedFileC7074.txt";
    private final String newEditedTestFileLink = "Link to " + newEditedTestFile;
    private final String newVersionFilePath = testDataFolder + newEditedTestFile;

    private DocumentLibraryPage documentLibraryPage;
    private CopyMoveUnzipToDialog copyMoveUnzipToDialog;
    private SharedFilesPage sharedFilesPage;
    private DocumentDetailsPage documentDetailsPage;
    private UploadContent uploadContent;
    private RepositoryPage repositoryPage;
    private DeleteDialog deleteDialog;

    private FileModel fileToCheck;
    private FolderModel folderToCheck;
    private UserModel testUser;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Creating a testuser1 and site1 created by testuser1");
        testUser = dataUser.usingAdmin().createRandomTestUser();
        site.set(getDataSite().usingUser(testUser).createPublicRandomSite());
        testSite = site.get().getTitle();

        getCmisApi().authenticateUser(testUser);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        copyMoveUnzipToDialog = new CopyMoveUnzipToDialog(webDriver);
        sharedFilesPage  = new SharedFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();
        testFile = fileToCheck.getName();
        testFileLink = "Link to " + testFile;

        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();
        testFolder = folderToCheck.getName();
        testFolderLink = "Link to " + testFolder;

        authenticateUsingCookies(testUser);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C42624")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void verifyDisabledActionsForCreatedLink()
    {
        log.info("STEP1: Go to Document Library of the site"
            + "STEP2: For a file/folder, click on \"Copy to\", select a destination folder");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFile, ItemActions.COPY_TO);

        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + testFile + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP3: Go to the link's destination " + testFileLink);
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");

        sharedFilesPage
            .assertFileIsDisplayed(testFileLink)
            .assertLikeButtonNotDisplayed(testFileLink)
            .assertCommentButtonNotDisplayed(testFileLink)
            .assertShareButtonNotDisplayed(testFileLink);

        log.info("Step4: Delete the link from the shared folder.....");
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFileLink, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42625")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void linkToFileRedirectsToDocDetailsPage()
    {
        log.info("From Document actions, click on [Copy to] option and Select a destination folder and click [Create Link] button");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFile, ItemActions.COPY_TO);

        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + testFile + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Go to the link's location");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
        sharedFilesPage
            .assertFileIsDisplayed(testFileLink);

        log.info("STEP2: Click on the link to file");
        sharedFilesPage
            .clickOnFile(testFileLink);

        documentDetailsPage
            .assertPageTitleEquals("Alfresco » Document Details")
            .assertContentNameEquals(testFile);

        log.info("Step3: Delete the link from the shared folder.....");
        sharedFilesPage.navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFileLink, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42626")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void linkToFolderRedirectsToFolderContentsPage()
    {
        log.info("From Document actions, click on \"Copy to\" option");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFolder, ItemActions.COPY_TO);

        log.info("Select a destination folder and click \"Create Link\" button");
        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + testFolder + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Go to the link's location");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
        sharedFilesPage
            .assertFileIsDisplayed(testFolderLink);

        log.info("STEP2: Click on the link to folder");
        sharedFilesPage
            .clickLinkToFolder(testFolderLink);

        repositoryPage
            .assertBrowserPageTitleIs("Alfresco » Repository Browser");
        repositoryPage
            .assertBreadCrumbEquals(testFolder);

        log.info("Delete link from Shared folder...");
        sharedFilesPage.navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFolderLink, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42627")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION})
    public void linkToLockedDocRedirectsToOriginalDoc()
    {
        log.info("Precondition1: Login to Share/Google Docs and navigate to Document Library page for the test site; upload a .docx file"
            + "Precondition2: Go to Document Library of the site. Create link for document");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFile,ItemActions.COPY_TO);
        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + testFile + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Lock the document, e.g: edit it Google Docs");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFile,ItemActions.EDIT_OFFLINE);

        log.info("STEP2: Go to the location where the link was created");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
        sharedFilesPage
            .assertFileIsDisplayed(testFileLink);

        log.info("STEP3: Click on the link");
        documentLibraryPage
            .clickOnFile(testFileLink);
        documentDetailsPage
            .assertPageTitleEquals("Alfresco » Document Details")
            .assertContentNameEquals(testFile);

        log.info("STEP4: Deleting Link from Share Folder");
        sharedFilesPage
            .navigate(site.get())
            .selectItemActionFormFirstThreeAvailableOptions(testFileLink, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }
    @TestRail (id = "C42628")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void linkToMultipleVersionsDocRedirectsToLastVersion()
    {
        log.info("Precondition1: 'Upload new version' for a file");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFile, ItemActions.UPLOAD_NEW_VERSION);

        uploadContent
            .updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);

        log.info("Precondition2: Create link for the file with new version " + newEditedTestFile);
        documentLibraryPage
            .assertIsContantNameDisplayed(newEditedTestFile)
            .selectItemAction(newEditedTestFile, ItemActions.COPY_TO);

        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + newEditedTestFile + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");

        log.info("STEP2: Click on the created link");
        sharedFilesPage
            .clickOnFile(newEditedTestFileLink);

        documentDetailsPage
            .assertPageTitleEquals("Alfresco » Document Details")
            .assertContentNameEquals(newEditedTestFile);

        log.info("STEP3: Deleting the Link from Share Folder");
        sharedFilesPage
            .navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(newEditedTestFileLink, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42629")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyDisplayedActionsForLinkToFile()
    {
        log.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFile,ItemActions.COPY_TO);

        log.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + testFile + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
        sharedFilesPage
            .assertFileIsDisplayed(testFileLink);

        log.info("STEP2: Verify available actions for " + testFileLink);
        sharedFilesPage
            .assertIsActionAvailableForLibraryItem(testFileLink,ItemActions.LOCATE_LINKED_ITEM);
        sharedFilesPage
            .assertIsActionAvailableForLibraryItem(testFileLink,ItemActions.DELETE_LINK);
        sharedFilesPage
            .assertIsActionAvailableForLibraryItem(testFileLink,ItemActions.COPY_TO);
        sharedFilesPage
            .assertIsActionAvailableForLibraryItem(testFileLink,ItemActions.MOVE_TO);

    }

    @TestRail (id = "C42630")
    @Test (groups =  { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateLinkedItemRedirectsToOriginalDoc()
    {
        log.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFile, ItemActions.COPY_TO);

        log.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + testFile + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
        sharedFilesPage
            .assertFileIsDisplayed(testFileLink);

        log.info("STEP2: Mouse over the link and click on 'Locate Linked Item' option");
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFileLink, ItemActions.LOCATE_LINKED_ITEM);

        log.info("Step3: Verify that Page redirects to the Document Library view of the location of the original file/Folder.");
        repositoryPage
            .assertBrowserPageTitleIs("Alfresco » Repository Browser");
        repositoryPage
            .assertFileIsDisplayed(testFile);

        log.info("Step4: Delete the link from the shared folder.....");
        sharedFilesPage
            .navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFileLink, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42631")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateLinkedItemRedirectsToFolder()
    {
        log.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFolder, ItemActions.COPY_TO);

        log.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + testFolder + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();

        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
        sharedFilesPage
            .assertFileIsDisplayed(testFolderLink);

        log.info("STEP2: Mouse over the link and click on 'Locate Linked Item' option");
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFolderLink, ItemActions.LOCATE_LINKED_ITEM);

        log.info("Step3: Verify that Page redirects to the Document Library view of the location of the original file/Folder.");
        repositoryPage
            .assertBrowserPageTitleIs("Alfresco » Repository Browser");
        repositoryPage
            .assertFileIsDisplayed(testFolder);

        log.info("Step4: Delete the link from the shared folder.....");
        sharedFilesPage
            .navigate();
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFolderLink, ItemActions.DELETE_LINK);
        sharedFilesPage
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C42632")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void deleteLinkRemovesLink()
    {
        log.info("Precondition: For a file click 'Copy to' option");
        documentLibraryPage
            .navigate(testSite)
            .selectItemAction(testFolder, ItemActions.COPY_TO);

        log.info("Precondition: Click \"Create Link\" button");
        copyMoveUnzipToDialog
            .assertDialogTitleEquals("Copy " + testFolder + " to...")
            .selectSharedFilesDestination()
            .clickCreateLinkButton();;

        log.info("STEP1: Go to the location where the link was created");
        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");

        log.info("STEP2: Mouse over the link and click on 'Delete Link' option");
        sharedFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFolderLink, ItemActions.DELETE_LINK);

        log.info("STEP3: In the Delete confirmation dialog press 'Delete' button");
        deleteDialog
            .assertDeleteDialogHeaderEquals(language.translate("documentLibrary.contentActions.deleteDocument"))
            .confirmDeletion();

        log.info("Step4: Verify that the link should not be present in the shared File");
        sharedFilesPage
            .assertFileIsNotDisplayed(testFolderLink);
    }
}