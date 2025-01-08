package org.alfresco.share.alfrescoContent.documentLibrary;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.site.SiteDashboardPage;
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

@Slf4j
public class LibraryViewTests extends BaseTest {
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private DocumentLibraryPage2 documentLibraryPage;
    private DocumentLibraryPage documentLibrary;
    private SiteDashboardPage siteDashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        documentLibrary = new DocumentLibraryPage(webDriver);

        log.info("Precondition: User & Site creation");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());
    }

    @TestRail(id = "C6946")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifySimpleViewOption() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Simple View\" option");
        documentLibraryPage
            .navigate(site.get())
            .clickOptions()
            .selectSimpleView();

        log.info("Step2: Verify basic details for file");
        documentLibrary
            .assertLikeButtonNotDisplayed(fileToCheck.getName())
            .assertCommentButtonNotDisplayed(fileToCheck.getName())
            .assertShareButtonNotDisplayed(fileToCheck.getName());
        List<String> expectedActionsForFile = Arrays
            .asList("More...", "Upload New Version", "Edit in Alfresco Share", "Edit Offline", "Edit in Google Docs™", "Copy to...", "Move to...", "Delete Document", "Start Workflow", "Manage Permissions", "Manage Aspects");
        documentLibrary
            .assertActionsAvailableForLibrary(fileToCheck.getName(), expectedActionsForFile);

        log.info("Step3: Verify basic details for folder");
        documentLibrary
            .assertLikeButtonNotDisplayed(folderToCheck.getName())
            .assertCommentButtonNotDisplayed(folderToCheck.getName());
        List<String> expectedActionsForFolder = Arrays
            .asList("More...", "Copy to...", "Move to...", "Manage Rules", "Delete Folder", "Manage Permissions", "Manage Aspects");
        documentLibrary
            .assertActionsAvailableForLibrary(folderToCheck.getName(), expectedActionsForFolder);
    }

    @TestRail(id = "C6947")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyDetailedViewOption() {
        log.info("Precondition: File & Folders are created");
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Step1: From \"Options\" menu, click \"Detailed View\" option");
        documentLibraryPage
            .navigate(site.get());

        log.info("Step2: Verify basic details for file");
        documentLibrary
            .assertLikeButtonIsDisplayed(fileToCheck.getName())
            .assertLikeButtonIsDisplayed(fileToCheck.getName())
            .assertLikeButtonIsDisplayed(fileToCheck.getName());
        List<String> expectedActionsForFile = Arrays
            .asList("Download", "View In Browser", "Edit Properties", "More...", "Upload New Version", "Edit in Alfresco Share", "Edit Offline", "Edit in Google Docs™", "Copy to...", "Move to...", "Delete Document", "Start Workflow", "Manage Permissions", "Manage Aspects");
        documentLibrary
            .assertActionsAvailableForLibrary(fileToCheck.getName(), expectedActionsForFile);

        log.info("Step3: Verify basic details for folder");
        List<String> expectedActionsForFolder = Arrays
            .asList("Download as Zip", "View Details", "Edit Properties", "More...", "Copy to...", "Move to...", "Manage Rules", "Delete Folder", "Manage Permissions", "Manage Aspects");
        documentLibrary
            .assertActionsAvailableForLibrary(folderToCheck.getName(), expectedActionsForFolder);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
