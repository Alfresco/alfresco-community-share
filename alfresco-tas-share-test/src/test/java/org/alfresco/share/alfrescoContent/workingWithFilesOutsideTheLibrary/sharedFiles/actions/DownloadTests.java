package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.constants.ShareGroups;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;

@Slf4j
public class DownloadTests extends BaseTest
{
    private DeleteDialog deleteDialog;
    private SharedFilesPage sharePage;
    private UserModel testUser1;
    private FileModel testFile;
    private FolderModel testFolder;
    private boolean setupSucceeded = false;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        sharePage = new SharedFilesPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        Assert.assertNotNull(testUser1, "Precondition failed: test user was not created");

        // If your UserModel can be partially created, assert username too:
        Assert.assertNotNull(testUser1.getUsername(), "Precondition failed: test user username is null");

        getCmisApi().authenticateUser(getAdminUser());

        authenticateUsingLoginPage(testUser1);

        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingAdmin().usingShared().createFolder(testFolder).assertThat().existsInRepo();

        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingAdmin().usingShared().createFile(testFile).assertThat().existsInRepo();

        authenticateUsingCookies(getAdminUser());

        setupSucceeded = true;
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {

        if (!setupSucceeded)
        {
            deleteUsersIfNotNull(testUser1);
            return;
        }

        try
        {
            if (testFile != null)
            {
                getCmisApi().usingAdmin().usingResource(testFile).delete();
            }
            if (testFolder != null)
            {
                getCmisApi().usingAdmin().usingResource(testFolder).delete();
            }
        }
        catch (Exception ignored)
        {
            log.warn("No test file or folder present");
        }

        deleteUsersIfNotNull(testUser1);
    }

    @TestRail (id = "C8024")
    @AlfrescoTest(jira="XAT-10704")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, ShareGroups.SHARE_PRIORITY_1})
    public void downloadFileFromAlfresco()
    {
        sharePage.navigateByMenuBar();

        log.info("Step 1: Mouse over file, click Download");
        sharePage.selectItemActionFormFirstThreeAvailableOptions(testFile.getName(),ItemActions.DOWNLOAD);
        sharePage.acceptAlertIfDisplayed();

        log.info("Step 2: Check the file was saved locally");
        Assert.assertTrue(isFileInDirectory(testFile.getName(), null), "The file was not found in the specified location");

        log.info("Delete the downloaded file from the directory");
        File file = new File(testDataFolder + testFile.getName());
        file.delete();
        Assert.assertFalse(file.exists(), "File should not exist!");
    }

    @TestRail (id = "C8027")
    @AlfrescoTest (jira="XAT-10705")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, ShareGroups.SHARE_PRIORITY_1 })
    public void downloadFolder()
    {
        sharePage.navigateByMenuBar();

        log.info("Step 1: Mouse over folder, click Download");
        sharePage.selectItemActionFormFirstThreeAvailableOptions(testFolder.getName(),ItemActions.DOWNLOAD_AS_ZIP);
        sharePage.acceptAlertIfDisplayed();
        log.info("Step 2: Check the folder was saved locally");
        Assert.assertTrue(isFileInDirectory(testFolder.getName(), ".zip"), "The folder was not found in the specified location");

        log.info("Delete the downloaded folder from the directory");
        File file = new File(testDataFolder + testFolder.getName());
        file.delete();
        Assert.assertFalse(file.exists(), "File should not exist!");
    }
}

