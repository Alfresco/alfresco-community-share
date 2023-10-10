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

public class RenamingContentTests extends BaseTest
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

    @TestRail (id = "C7419")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void renameFileByEditIcon()
    {
        FileModel fileToRename = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel newFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "");
        getCmisApi().usingSite(site.get()).createFile(fileToRename);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToRename)
            .clickRenameIcon()
            .typeNewName(newFile.getName())
            .clickSave();
        documentLibraryPage.usingContent(fileToRename).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(newFile).assertContentIsDisplayed();
    }

    @TestRail (id = "C7420")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void renameFolderByEditIcon()
    {
        FolderModel folderToRename = FolderModel.getRandomFolderModel();
        FolderModel newFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToRename);

        documentLibraryPage.navigate(site.get())
            .usingContent(folderToRename)
            .clickRenameIcon()
            .typeNewName(newFolder.getName())
            .clickSave();
        documentLibraryPage.usingContent(folderToRename).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(newFolder).assertContentIsDisplayed();
    }

    @TestRail (id = "C7431")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelRenamingContent()
    {
        FileModel fileToRename = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel newFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "");
        getCmisApi().usingSite(site.get()).createFile(fileToRename);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToRename)
            .clickRenameIcon()
            .typeNewName(newFile.getName())
            .clickCancel();
        documentLibraryPage.usingContent(fileToRename).assertContentIsDisplayed();
        documentLibraryPage.usingContent(newFile).assertContentIsNotDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}