package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.alfresco.common.Utils.testDataFolder;

@Slf4j
public class UpdateFileWithContentFromComputerTests extends BaseTest
{
    private DocumentLibraryPage documentLibraryPage;
    private UploadFileDialog uploadFileDialog;
    private UploadContent uploadContent;
    private DocumentDetailsPage documentDetailsPage;

    private String newVersionFileName = "EditedTestFileC7074.txt";
    private String newVersionFilePath = testDataFolder + newVersionFileName;
    private String editedContent = "Edited content C7074";

    private FileModel fileToCheck;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();


    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Creating a random user and a random public site");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        getCmisApi().authenticateUser(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        uploadFileDialog = new UploadFileDialog(webDriver);
        uploadContent = new UploadContent(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);

        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7074")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void uploadFileUsingUploadNewVersion()
    {
        documentLibraryPage.navigate(site.get().getTitle());

        log.info("Steps1: Click 'Upload new version' action for the test file");
        documentLibraryPage.selectItemAction(fileToCheck.getName(), ItemActions.UPLOAD_NEW_VERSION);

        log.info(
            "Step2 - Click on 'Select files to upload' button, browse to the new version of the test file and select it. Click 'Upload' button.");
        uploadContent
            .updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);

        log.info("Step3 - Check the new title of the file displayed in Document Library.");
        documentLibraryPage.navigate(site.get().getTitle())
            .assertFileIsDisplayed(newVersionFileName);

        log.info("Steps4,5: Click on the file and check the version and content are updated.");
        documentLibraryPage.clickOnFile(newVersionFileName);

        documentDetailsPage.assertFileContentEquals("Edited content C7074")
            .assertVerifyFileVersion("2.0");

    }
}