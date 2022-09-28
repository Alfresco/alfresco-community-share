package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

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
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@Slf4j

public class ActionsUnzippingContentTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final String zipFile = "testFileC8256.zip";
    private final String zipFilePath = testDataFolder + zipFile;
    private final String sharedFolderName = "Shared";
    private final String zipContent = "testFile1";

    private final String acpFile = "archiveC8257.acp";
    private final String acpPath = testDataFolder + acpFile;
    private final String acpContent = "fileC8257";
    //@Autowired
    private RepositoryPage repositoryPage;
    private DeleteDialog deleteDialog;
    //@Autowired
    private CopyMoveUnzipToDialog unzipToDialog;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    private MyFilesPage myFilesPage;
    private UploadContent uploadContent;

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
         deleteDialog = new DeleteDialog(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C8256")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void unzipZipFileToRepository()
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
            .selectMyFilesDestination();
        log.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog
            .clickUnzipButton();
        myFilesPage
            .navigate();
        myFilesPage
            .assertIsContantNameDisplayed(zipContent);
        log.info("Delete unzip and text file ");
        repositoryPage
            .select_ItemsAction(zipContent, ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        repositoryPage
            .select_ItemsAction(zipFile, ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
    }

    @TestRail (id = "C8257")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, enabled = false)
    public void unzipAcpFileToRepository()
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

        log.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog
            .selectMyFilesDestination();
        log.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog
            .clickUnzipButton();
        myFilesPage
            .navigate();
        myFilesPage
            .assertIsContantNameDisplayed(zipContent);

    }
}
