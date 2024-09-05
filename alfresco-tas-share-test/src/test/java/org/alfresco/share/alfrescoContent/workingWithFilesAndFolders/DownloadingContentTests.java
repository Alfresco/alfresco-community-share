package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;


import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;

import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;

/**
 * Refactored by Raja Singh
 */
@Slf4j
public class DownloadingContentTests extends BaseTest
{
    private DocumentLibraryPage documentLibraryPage;

    private FolderModel folderToCheck;
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
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
    @TestRail (id = "C7080")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "DownloadTest" })
    public void downloadFileFromAlfresco()
    {
        log.info("Create a file into site document library");
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());

        log.info("Navigate to the document libreary");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 1: Hover file and click 'Download' button");
        documentLibraryPage
            .selectItemActionFormFirstThreeAvailableOptions(fileToCheck.getName(), ItemActions.DOWNLOAD);
        documentLibraryPage
            .acceptAlertIfDisplayed();

        log.info("Step 2: Choose 'Save File' option and click 'OK' and verify that the file has been downloaded to the right location");
        Assert.assertTrue(isFileInDirectory(fileToCheck.getName(), null), "The file was not found in the specified location");

        log.info("Delete the downloaded file from the directory");
        File file = new File(testDataFolder + fileToCheck.getName());
        file.delete();
        Assert.assertFalse(file.exists(), "File should not exist!");
    }

    @TestRail (id = "C7087")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "DownloadTest" })
    public void nonEmptyFolderDownloadAsZip()
    {
        log.info("Create Folder in document library.");
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Create a file into the folder");
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileToCheck).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());
        log.info("Navigate to the document libreary");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Hover folder and click 'Download as Zip' button from Actions options");
        documentLibraryPage
            .selectItemActionFormFirstThreeAvailableOptions(folderToCheck.getName(), ItemActions.DOWNLOAD_AS_ZIP);
        documentLibraryPage
            .acceptAlertIfDisplayed();

        log.info("Choose Save option, location for folder to be downloaded and click OK button and verify folder is displayed in specified location");
        Assert.assertTrue(isFileInDirectory(folderToCheck.getName(), ".zip"), "The zip archive was not found in the specified location");

        log.info("Delete the downloaded zip folder from the directory");

        File file = new File(testDataFolder + folderToCheck.getName()+".zip");
        file.delete();
        Assert.assertFalse(file.exists(), "Folder should not exist!");
    }
}
