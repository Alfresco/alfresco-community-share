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
    private UserModel user;
    private SiteModel site;

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        setupAuthenticatedSession(user);
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
    }

    @TestRail (id = "C7501")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyAddFileToFavorites()
    {
        FileModel favoriteFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(user)
            .usingSite(site).createFile(favoriteFile).assertThat().existsInRepo();

        documentLibraryPage.navigate(site)
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
        getCmisApi().authenticateUser(user)
            .usingSite(site).createFolder(favoriteFolder).assertThat().existsInRepo();

        documentLibraryPage.navigate(site)
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
        getCmisApi().authenticateUser(user)
            .usingSite(site).createFile(favoriteFile).assertThat().existsInRepo();
        getRestApi().authenticateUser(user)
            .withCoreAPI().usingAuthUser().addFileToFavorites(favoriteFile);

        documentLibraryPage.navigate(site)
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
        getCmisApi().authenticateUser(user)
            .usingSite(site).createFolder(folder).assertThat().existsInRepo();
        getRestApi().authenticateUser(user)
            .withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

        documentLibraryPage.navigate(site)
            .usingContent(folder)
                .assertRemoveFolderFromFavoritesTooltipEqualsWithExpected()
                .removeFromFavorites()
                .assertAddToFavoritesIsDisplayed();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(user);
        deleteSitesIfNotNull(site);
    }
}
