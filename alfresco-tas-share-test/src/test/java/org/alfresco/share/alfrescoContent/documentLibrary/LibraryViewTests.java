package org.alfresco.share.alfrescoContent.documentLibrary;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.testng.Assert.assertTrue;

@Slf4j
public class LibraryViewTests extends BaseTest {
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final String SET_DETAILED_VIEW_AS_DEFAULT = "documentLibrary.options.defaultDetailedView";
    private final String REMOVE_DETAILED_VIEW_AS_DEFAULT = "documentLibrary.options.removeDetailedView";
    private DocumentLibraryPage2 documentLibraryPage;
    private DocumentLibraryPage documentLibrary;
    private DocumentDetailsPage documentDetailsPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        documentLibrary = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);

        log.info("Precondition: User & Site creation");
        user.set(getDataUser().usingAdmin()
            .createRandomTestUser());
        site.set(getDataSite().usingUser(user.get())
            .createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());
    }

    @TestRail(id = "C6946")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifySimpleViewOption() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat()
            .existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Simple View\" option");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectSimpleView();

        log.info("Step2: Verify basic details for file");
        documentLibrary.assertLikeButtonNotDisplayed(fileToCheck.getName())
            .assertCommentButtonNotDisplayed(fileToCheck.getName())
            .assertShareButtonNotDisplayed(fileToCheck.getName());
        List<String> expectedActionsForFile =
            Arrays.asList("More...", "Upload New Version", "Edit in Alfresco Share", "Edit Offline",
                "Edit in Google Docs™", "Copy to...", "Move to...", "Delete Document", "Start Workflow",
                "Manage Permissions", "Manage Aspects");
        documentLibrary.assertActionsAvailableForLibraryItems(fileToCheck.getName(), expectedActionsForFile);

        log.info("Step3: Verify basic details for folder");
        documentLibrary.assertLikeButtonNotDisplayed(folderToCheck.getName())
            .assertCommentButtonNotDisplayed(folderToCheck.getName());
        List<String> expectedActionsForFolder =
            Arrays.asList("More...", "Copy to...", "Move to...", "Manage Rules", "Delete Folder", "Manage Permissions",
                "Manage Aspects");
        documentLibrary.assertActionsAvailableForLibraryItems(folderToCheck.getName(), expectedActionsForFolder);
    }

    @TestRail(id = "C6947")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyDetailedViewOption() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat()
            .existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Detailed View\" option");
        documentLibraryPage.navigate(site.get());

        log.info("Step2: Verify basic details for file");
        documentLibrary.assertLikeButtonIsDisplayed(fileToCheck.getName())
            .assertLikeButtonIsDisplayed(fileToCheck.getName())
            .assertLikeButtonIsDisplayed(fileToCheck.getName());
        List<String> expectedActionsForFile =
            Arrays.asList("Download", "View In Browser", "Edit Properties", "More...", "Upload New Version",
                "Edit in Alfresco Share", "Edit Offline", "Edit in Google Docs™", "Copy to...", "Move to...",
                "Delete Document", "Start Workflow", "Manage Permissions", "Manage Aspects");
        documentLibrary.assertActionsAvailableForLibraryItems(fileToCheck.getName(), expectedActionsForFile);

        log.info("Step3: Verify basic details for folder");
        List<String> expectedActionsForFolder =
            Arrays.asList("Download as Zip", "View Details", "Edit Properties", "More...", "Copy to...", "Move to...",
                "Manage Rules", "Delete Folder", "Manage Permissions", "Manage Aspects");
        documentLibrary.assertActionsAvailableForLibraryItems(folderToCheck.getName(), expectedActionsForFolder);
    }

    @TestRail(id = "C6948")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyGalleryViewOption() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat()
            .existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Gallery View\" option");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectgalleryView();

        log.info("Step2: Click \"More info\" icon for file & verify available actions");
        List<String> expectedActionsForFile =
            Arrays.asList("More...", "Upload New Version", "Edit in Alfresco Share", "Edit Offline",
                "Edit in Google Docs™", "Copy to...", "Move to...", "Delete Document", "Start Workflow",
                "Manage Permissions", "Manage Aspects");
        documentLibrary.assertActionsAvailableForLibraryInGalleryView(fileToCheck.getName(), expectedActionsForFile);

        log.info("Step2: Click \"More info\" icon for folder & verify available actions");
        documentLibrary.assertLikeButtonNotDisplayed(folderToCheck.getName())
            .assertCommentButtonNotDisplayed(folderToCheck.getName());
        List<String> expectedActionsForFolder =
            Arrays.asList("More...", "Copy to...", "Move to...", "Manage Rules", "Delete Folder", "Manage Permissions",
                "Manage Aspects");
        documentLibrary.assertActionsAvailableForLibraryInGalleryView(folderToCheck.getName(), expectedActionsForFolder);
    }

    @TestRail(id = "C6951")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyAudioViewOption() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat()
            .existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Audio View\" option");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectAudioView();

        log.info("Step 2: Verify Audio view table options");
        documentLibrary
            .assertContentIsDisplayed(fileToCheck)
            .assertContentIsDisplayed(folderToCheck);
        List<String> expectedList =
            Arrays.asList("Name", "Title", "Album", "Artist", "Genre", "Track Number", "Sample Rate", "Encoding", "Actions");
        for (String anExpectedList : expectedList) {
            assertTrue(documentLibrary.getFilterTypeList()
                .contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail(id = "C6952")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyMediaViewOption() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat()
            .existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Audio View\" option");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectMediaView();

        log.info("Step 2: Verify Audio view table options");
        documentLibrary
            .assertContentIsDisplayed(fileToCheck)
            .assertContentIsDisplayed(folderToCheck);

        List<String> expectedList =
            Arrays.asList("Name", "Description", "Tags", "Width", "Height", "Size", "This version has:", "Actions");
        for (String anExpectedList : expectedList) {
            assertTrue(documentLibrary.getFilterTypeList()
                .contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail(id = "C6954")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void removeViewAsDefaultView() {
        log.info("Precondition: Create a folder and add a file in it");
        FolderModel folder = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
            .createFolder(folder)
            .usingResource(folder).createFile(file).assertThat().existsInRepo();

        log.info("Step1: Navigate to Document Library and click the created folder");
        documentLibraryPage.navigate(site.get())
            .usingContent(folder)
            .selectFolder();
        log.info("Verify file is displayed as created in precondition");
        documentLibrary.assertFileIsDisplayed(file.getName());

        log.info("Step2: From \"Options\" menu, click \"Remove X as default for this folder\", where X is the current view, e.g: Remove \"Detailed View\" as default for this folder.\n" +
            "Click \"Options\" menu");
        documentLibraryPage.clickOptions()
            .setDefaultView()
            .clickOptions()
            .assertRemoveDefaultViewForFolderEqualsTo(language.translate(REMOVE_DETAILED_VIEW_AS_DEFAULT));
        documentLibraryPage.pageRefresh()
            .clickOptions()
            .removeDefaultView();

        log.info("Verify set detailed view option is available");
        documentLibraryPage.clickOptions()
            .assertSetDefaultViewForFolderEqualsTo(language.translate(SET_DETAILED_VIEW_AS_DEFAULT));
    }

    @TestRail(id = "C6958")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void simpleViewOpenContent() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .usingResource(folderToCheck).createFile(file).assertThat().existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Simple View\" option");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectSimpleView();

        log.info("Step 2: Verify Files & Folders in Simple View Option");
        documentLibrary.clickOnFile(fileToCheck.getName());
        documentDetailsPage.assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName());

        documentLibrary.navigate(site.get())
            .clickOnFolderName(folderToCheck.getName())
            .assertFileIsDisplayed(file.getName());
    }

    @TestRail(id = "C6959")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void detailedViewOpenContent() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .usingResource(folderToCheck).createFile(file).assertThat().existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Detailed View\" option");
        documentLibraryPage.navigate(site.get());

        log.info("Step 2: Verify Files & Folders in Detailed View Option");
        documentLibrary.clickOnFile(fileToCheck.getName());
        documentDetailsPage.assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName());

        documentLibrary.navigate(site.get())
            .clickOnFolderName(folderToCheck.getName())
            .assertFileIsDisplayed(file.getName());
    }

    @TestRail(id = "C6960")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void galleryViewOpenContent() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .usingResource(folderToCheck).createFile(file).assertThat().existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Gallery View\" option");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectgalleryView();

        log.info("Step 2: Verify Files & Folders from Gallery view");
        documentLibrary.clickOnFileInGalleryView(fileToCheck.getName());
        documentDetailsPage.assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName());

        documentLibrary.navigate(site.get())
            .clickOnFileInGalleryView(folderToCheck.getName());
    }

    @TestRail(id = "C6961")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void audioViewOpenContent() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .usingResource(folderToCheck).createFile(file).assertThat().existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Audio View\" option");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectAudioView();

        log.info("Step 2: Verify Files & Folders from Audio view table");
        documentLibrary.clickOnFile(fileToCheck.getName());
        documentDetailsPage.assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName());

        documentLibrary.navigate(site.get())
            .clickOnFolderName(folderToCheck.getName())
            .assertFileIsDisplayed(file.getName());
    }

    @TestRail(id = "C6962")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void mediaViewOpenContent() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .usingResource(folderToCheck).createFile(file).assertThat().existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Media View\" option");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectMediaView();

        log.info("Step 2: Verify Audio view table options");
        documentLibrary.assertContentIsDisplayed(fileToCheck)
            .assertContentIsDisplayed(folderToCheck);

        log.info("Step 3: Verify Files & Folders from Media view table");
        documentLibrary.clickOnFile(fileToCheck.getName());
        documentDetailsPage.assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName());

        documentLibrary.navigate(site.get())
            .clickOnFolderName(folderToCheck.getName())
            .assertFileIsDisplayed(file.getName());
    }


    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
