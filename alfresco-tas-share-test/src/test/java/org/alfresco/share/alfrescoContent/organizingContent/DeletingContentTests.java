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

public class DeletingContentTests extends BaseTest
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

    @TestRail (id = "C9544")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyDeleteDocument()
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        FileModel fileToDelete = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFolder(folder)
            .then().usingResource(folder)
                .createFile(fileToDelete).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get())
            .usingContent(folder).selectFolder()
                .usingContent(fileToDelete)
                .clickDelete()
                .assertDeleteDialogHeaderEqualsTo(language.translate("documentLibrary.deleteDocument"))
                .assertConfirmDeleteMessageForContentEqualsTo(fileToDelete)
                .confirmDeletion();
        documentLibraryPage.usingContent(fileToDelete).assertContentIsNotDisplayed();
    }

    @TestRail (id = "C6968")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyDeleteFolderWithChildren()
    {
        FolderModel folderToDelete = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        FolderModel subFolder = FolderModel.getRandomFolderModel();

        getCmisApi().usingSite(site.get()).createFolder(folderToDelete)
            .usingResource(folderToDelete).createFolder(subFolder).createFile(subFile);
        documentLibraryPage.navigate(site.get())
            .usingContent(folderToDelete)
            .clickDelete()
            .assertDeleteDialogHeaderEqualsTo(language.translate("documentLibrary.deleteFolder"))
            .assertConfirmDeleteMessageForContentEqualsTo(folderToDelete)
            .confirmDeletion();

        documentLibraryPage.usingContent(folderToDelete).assertContentIsNotDisplayed();
        getCmisApi().usingResource(folderToDelete).assertThat().doesNotExistInRepo()
            .usingResource(subFile).assertThat().doesNotExistInRepo()
            .usingResource(subFolder).assertThat().doesNotExistInRepo();
    }

    @TestRail (id = "C6968")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelDeletingFolder()
    {
        FolderModel folderToCancel = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCancel);

        documentLibraryPage.navigate(site.get())
            .usingContent(folderToCancel)
            .clickDelete().clickCancel();
        documentLibraryPage.usingContent(folderToCancel).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
