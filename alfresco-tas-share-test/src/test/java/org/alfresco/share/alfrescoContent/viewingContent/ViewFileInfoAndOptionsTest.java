package org.alfresco.share.alfrescoContent.viewingContent;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.joda.time.DateTime;

import org.testng.Assert;
import org.testng.annotations.*;

import static org.alfresco.common.Utils.testDataFolder;
import java.io.File;

/**
 * @author iulia.cojocea
 */

@Slf4j
public class ViewFileInfoAndOptionsTest extends BaseTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final DateTime currentDate = new DateTime();
    private final String description = "C5920-SiteDescription-%s-" + random;
    private final String comment = "C5920-Comment-%s-" + random;

    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;

    private FolderModel folderToCheck;
    private FileModel fileToCheck, fileToCheckPDF;

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
        documentDetailsPage = new DocumentDetailsPage(webDriver);

        log.info("Create Folder in document library.");
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Create a file into the folder");
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Create a pdf file into the folder");
        fileToCheckPDF = FileModel.getRandomFileModel(FileType.PDF, description);
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileToCheckPDF).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        File file = new File(testDataFolder + fileToCheck.getName());
        file.delete();
        Assert.assertFalse(file.exists(), "File should not exist!");
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
    @TestRail (id = "C5883")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "DownloadTest" })
    public void viewFileInfoAndOptions()
    {
        log.info("STEP 1: Navigate to 'Document Library' page for 'siteName' and verify folder name");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(folderToCheck.getName());

        log.info("STEP 2: Click on folder name and verify the file present in folder then click on file name");
        documentLibraryPage
            .clickOnFolderName(folderToCheck.getName())
            .assertFileIsDisplayed(fileToCheck.getName())
            .clickOnFile(fileToCheck.getName());

        log.info("Stem 3: Verify 'File Name', 'File Version', 'Item Modifire' & 'Modified Date' on preview page.");
        documentDetailsPage
            .assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName())
            .assertVerifyFileVersion("1.0")
            .assertVerifyItemModifire(user.get().getFirstName(), user.get().getLastName())
            .assertVerifyModifiedDate(currentDate.toString("EEE d MMM yyyy"));

        log.info("STEP 4: Click 'Download' icon to download testFile");
        documentDetailsPage
            .clickDownloadButton();

        log.info("Step 5: Verify Downloaded file present in the directory.");
        documentDetailsPage
            .assertVerifyFileInDirectory(fileToCheck.getName());

        log.info("STEP 6: Click 'Like' icon to like testFile");
        documentDetailsPage
            .clickOnLikeUnlike();

        log.info("Step 7: Verify No of like should be 1 and then click on like button again");
        documentDetailsPage
            .assertVerifyNoOfLikes(1)
            .clickOnLikeUnlike();

        log.info("Step 8: Verify No of like should be 0");
        documentDetailsPage
            .assertVerifyNoOfLikes(0);

        log.info("STEP 9: Click 'Favorite' icon to favorite testFile");
        documentDetailsPage
            .clickOnFavoriteUnfavoriteLink();

        log.info("Step 10: Verify that file marked as Favorite and then click on UnFavorite");
        documentDetailsPage
            .assertContantMarkedAsFavorite()
            .clickOnFavoriteUnfavoriteLink();

        log.info("Step 11: Verify File is not marked as Favorite");
        documentDetailsPage
            .assertContantNotMarkedAsFavorite();

        log.info("STEP 12: Click 'Comment' icon");
        documentDetailsPage
            .clickOnCommentDocument();

        log.info("Step 13: Add comment and verify that comment added successfully");
        documentDetailsPage
            .addComment(comment)
            .assertVerifyCommentContent(comment);

        log.info("STEP 14: Click 'Shared' icon and verify the Shared popup");
        documentDetailsPage
            .clickOnSharedLink();
    }

    @TestRail (id = "MNT-24825")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "DownloadTest" })
    public void verifyPDFdownload() {
        log.info("STEP 1: Navigate to 'Document Library' page for 'siteName' and verify folder name");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(folderToCheck.getName());

        log.info("STEP 2: Click on folder name and verify the file present in folder then click on file name");
        documentLibraryPage
            .clickOnFolderName(folderToCheck.getName())
            .assertFileIsDisplayed(fileToCheckPDF.getName())
            .clickOnFile(fileToCheckPDF.getName());

        log.info("Stem 3: Verify 'File Name' on preview page.");
        documentDetailsPage
            .assertIsFileNameDisplayedOnPreviewPage(fileToCheckPDF.getName());

        log.info("STEP 4: Click 'Download' icon to download testFile");
        documentDetailsPage
            .clickDownloadButton();

        log.info("Step 5: Verify Downloaded file present in the directory.");
        documentDetailsPage
            .assertVerifyFileInDirectory(fileToCheckPDF.getName());
    }
}
