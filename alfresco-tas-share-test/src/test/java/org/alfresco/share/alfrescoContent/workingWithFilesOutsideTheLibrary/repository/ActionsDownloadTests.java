package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;

@Slf4j
public class ActionsDownloadTests extends BaseTest
{
    private RepositoryPage repositoryPage;
    private DeleteDialog deleteDialog;
    private HeaderMenuBar headerMenuBar;
    private UserModel testUser1;
    private FileModel testFile;
    private FolderModel testFolder;


    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {

        repositoryPage = new RepositoryPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
        headerMenuBar = new HeaderMenuBar(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Create a Folder and File in Admin Repository-> User Homes ");
        authenticateUsingLoginPage(getAdminUser());

        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingAdmin().usingUserHome(testUser1.getUsername()).createFolder(testFolder).assertThat().existsInRepo();

        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingAdmin().usingUserHome(testUser1.getUsername()).createFile(testFile).assertThat().existsInRepo();
        authenticateUsingCookies(getAdminUser());

    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
    }

    @TestRail (id = "C8240")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void downloadFileFromAlfresco()
    {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigateByMenuBar()
            .click_FolderName("User Homes")
            .clickOnFolderName(testUser1.getUsername())
            .assertFileIsDisplayed(testFile.getName());

        log.info("Step 1: Mouse over file, click Download");
        repositoryPage.selectItemActionFormFirstThreeAvailableOptions(testFile.getName(),ItemActions.DOWNLOAD);
        repositoryPage.acceptAlertIfDisplayed();

        log.info("Step 2: Check the file was saved locally");
        Assert.assertTrue(isFileInDirectory(testFile.getName(), null), "The file was not found in the specified location");

        log.info("Delete the downloaded file from the directory");
        File file = new File(testDataFolder + testFile.getName());
        file.delete();
        Assert.assertFalse(file.exists(), "File should not exist!");
    }

    @TestRail (id = " C8243")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void downloadFolder()
    {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigateByMenuBar()
            .click_FolderName("User Homes")
            .clickOnFolderName(testUser1.getUsername())
            .assertFileIsDisplayed(testFolder.getName());

        log.info("Step 1: Mouse over folder, click Download");
        repositoryPage.selectItemActionFormFirstThreeAvailableOptions(testFolder.getName(),ItemActions.DOWNLOAD_AS_ZIP);
        repositoryPage.acceptAlertIfDisplayed();

        log.info("Step 2: Check the folder was saved locally");
        Assert.assertTrue(isFileInDirectory(testFolder.getName(), ".zip"), "The folder was not found in the specified location");

        log.info("Delete the downloaded file from the directory");
        File file = new File(testDataFolder + testFolder.getName());
        file.delete();
        Assert.assertFalse(file.exists(), "File should not exist!");
    }

}
