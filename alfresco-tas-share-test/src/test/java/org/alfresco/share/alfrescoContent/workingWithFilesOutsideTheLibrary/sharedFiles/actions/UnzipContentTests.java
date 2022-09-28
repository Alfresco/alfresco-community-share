package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class UnzipContentTests extends BaseTest
{

    private final String zipContent = "testFile1";
    UploadContent uploadContent;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    //@Autowired
    private CopyMoveUnzipToDialog unzipToDialog;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private RepositoryPage repositoryPage;
    private DeleteDialog deleteDialog;
    private MyFilesPage myFilesPage;
    private final String sharedFolderName = "Shared";
    private final String zipFile = "testFileC8256.zip";
    private final String zipFilePath = testDataFolder + zipFile;
    private final String acpFile = "archiveC8257.acp";
    private final String acpPath = testDataFolder + acpFile;


    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("PreCondition: Creating a TestUser");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(user.get());

        repositoryPage = new RepositoryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        unzipToDialog = new CopyMoveUnzipToDialog(webDriver);
        deleteDialog = new  DeleteDialog(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }


    @TestRail (id = "C8040")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void unzipZipArchiveToSharedFile()
    {
        log.info("Precondition: Login to share and navigate to Repository->Shared ");
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(zipFilePath);
        log.info("STEP1: Click archive name, e.g: testArchive");
        myFilesPage
            .clickOnFile(zipFile);
        documentDetailsPage
            .assertContentNameEquals(zipFile);
        log.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage
            .clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog
                .getDialogTitle(),
            "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");

        log.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog
            .selectSharedFilesDestination();
        log.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog
            .clickUnzipButton();
        documentDetailsPage
            .clickOpenedFloder();
        myFilesPage
            .assertIsContantNameDisplayed(zipContent);
        log.info("Delete unzip and text file ");
        repositoryPage
            .select_ItemsAction(zipContent, ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
        repositoryPage
            .select_ItemsAction(zipFile, ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
    }

    @TestRail (id = "C8041")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT },enabled = false)
    public void unzipAcpArchiveToSharedFiles()
    {
        log.info("Precondition: Login to share and navigate to Repository->Shared ");
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(acpPath);
        log.info("STEP1: Click archive name, e.g: testArchive");
        myFilesPage
            .clickOnFile(acpFile);
        documentDetailsPage
            .assertContentNameEquals(acpFile);
        log.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage
            .clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog
                .getDialogTitle(),
            "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");
    }
}