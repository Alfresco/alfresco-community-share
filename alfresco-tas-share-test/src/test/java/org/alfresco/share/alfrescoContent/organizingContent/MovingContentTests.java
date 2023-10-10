package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class MovingContentTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7345")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkMoveFileToFolderInSite()
    {
        FolderModel destination = FolderModel.getRandomFolderModel();
        FileModel fileToMove = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToMove).createFolder(destination);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToMove)
            .clickMoveTo()
            .selectRecentSitesDestination()
            .selectSite(site.get())
            .selectFolder(destination)
            .clickMoveButton();

        documentLibraryPage.usingContent(fileToMove).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(destination).selectFolder()
            .usingContent(fileToMove).assertContentIsDisplayed();
    }

    @TestRail (id = "C7346")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkMoveFolderWithChildrenInSite()
    {
        FolderModel destination = FolderModel.getRandomFolderModel();
        FolderModel folderToMove = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFolder(destination)
            .createFolder(folderToMove)
                .then().usingResource(folderToMove).createFile(subFile);

        documentLibraryPage.navigate(site.get())
            .usingContent(folderToMove).clickMoveTo()
            .selectRecentSitesDestination()
            .selectSite(site.get())
            .selectFolder(destination)
            .clickMoveButton();
        documentLibraryPage.usingContent(folderToMove).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(destination).selectFolder()
            .usingContent(folderToMove)
            .assertContentIsDisplayed()
            .selectFolder()
            .usingContent(subFile)
            .assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}