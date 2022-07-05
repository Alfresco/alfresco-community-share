package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class MyFilesDeleteTests extends BaseTest
{
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String testFolder = String.format("testFolder%s", RandomData.getRandomAlphanumeric());

    private MyFilesPage myFilesPage;

    private SiteDashboardPage sitePage;
    private DocumentDetailsPage documentDetailsPage;
    private DeleteDialog deleteDialog;

    private NewFolderDialog newContentDialog;
    private DocumentLibraryPage documentLibraryPage;
    private UploadContent uploadContent;

    private SocialFeatures social;
    private UserModel testUser;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("PreCondition: Creating a TestUser");
        testUser = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(testUser);
        authenticateUsingCookies(testUser);

        uploadContent = new UploadContent(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        newContentDialog = new NewFolderDialog(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C7896")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesDeleteDocument()
    {
        log.info("Precondition: Navigate to My Files page and upload a file.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .assertIsContantNameDisplayed(testFile);

        log.info("STEP1: Hover over the file. STEP2: Click 'More...' link. Click 'Delete Document' link");
        myFilesPage
            .select_ItemAction(testFile, ItemActions.DELETE_DOCUMENT)
            .assertDialogTitleEquals("Delete Document")
            .assertConfirmationMessage(String.format(language.translate("confirmDeletion.message"), testFile));

        log.info("STEP3: Click 'Delete' button");
        deleteDialog
            .confirmDeletion();

        log.info("STEP4: Verify that the file was deleted");
        myFilesPage
            .assertIsContentDeleted(testFile);
    }

    @TestRail (id = "C7896")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesDeleteFolder()
    {
        log.info("Precondition: Navigate to My Files page and create a folder.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        myFilesPage
            .click_CreateButton()
            .click_FolderLink()
            .assertDialogTitleEquals("New Folder");
        newContentDialog
            .typeName(testFolder)
            .clickSave();
        myFilesPage
            .assertIsContantNameDisplayed(testFolder);

        log.info("STEP1: Hover over the folder. STEP2: Click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        myFilesPage
            .select_ItemAction(testFolder, ItemActions.DELETE_FOLDER)
            .assertDialogTitleEquals("Delete Folder")
            .assertConfirmationMessage(String.format(language.translate("confirmDeletion.message"), testFolder));

        log.info("STEP3: Click 'Delete' button");
        deleteDialog
            .confirmDeletion();

        log.info("STEP4: Verify that the folder was deleted");
        myFilesPage
            .assertIsContentDeleted(testFolder);
    }
}
