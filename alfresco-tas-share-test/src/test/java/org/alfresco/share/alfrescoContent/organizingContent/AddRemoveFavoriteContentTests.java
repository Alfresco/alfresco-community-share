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

public class AddRemoveFavoriteContentTests extends BaseTest
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
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7501")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyAddFileToFavorites()
    {
        FileModel favoriteFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(favoriteFile).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get())
            .usingContent(favoriteFile)
                .assertAddFileToFavoritesTooltipEqualsWithExpected()
                .addToFavorites()
                .assertRemoveFromFavoritesIsDisplayed();
    }

    @TestRail (id = "C7502")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void favoriteFolder()
    {
        FolderModel favoriteFolder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFolder(favoriteFolder).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get())
            .usingContent(favoriteFolder)
                .assertAddFolderToFavoritesTooltipEqualsWithExpected()
                .addToFavorites()
                .assertRemoveFromFavoritesIsDisplayed();
    }

    @TestRail (id = "C7503")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeFavoriteForFile() throws Exception
    {
        FileModel favoriteFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(favoriteFile).assertThat().existsInRepo();
       // getRestApi().authenticateUser(user.get())
      //      .withCoreAPI().usingAuthUser().addFileToFavorites(favoriteFile);

        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
            .withCoreAPI().usingAuthUser().addFileToFavorites(favoriteFile);

        documentLibraryPage.navigate(site.get())
            .usingContent(favoriteFile)
                .assertRemoveFileFromFavoritesTooltipEqualsWithExpected()
                .removeFromFavorites()
                .assertAddToFavoritesIsDisplayed();
    }

    @TestRail (id = "C7504")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeFavoriteForFolder() throws Exception
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFolder(folder).assertThat().existsInRepo();
       // getRestApi().authenticateUser(user.get())
         //   .withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
            .withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

        documentLibraryPage.navigate(site.get())
            .usingContent(folder)
                .assertRemoveFolderFromFavoritesTooltipEqualsWithExpected()
                .removeFromFavorites()
                .assertAddToFavoritesIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
