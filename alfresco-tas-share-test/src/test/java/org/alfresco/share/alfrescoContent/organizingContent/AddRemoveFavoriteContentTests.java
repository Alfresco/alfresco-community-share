package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AddRemoveFavoriteContentTests extends BaseTests
{
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel user;
    private SiteModel site;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        getCmisApi().authenticateUser(user);
        getRestApi().authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @TestRail (id = "C7501")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyAddFileToFavorites()
    {
        FileModel favoriteFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().usingSite(site).createFile(favoriteFile).assertThat().existsInRepo();

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
        getCmisApi().usingSite(site).createFolder(favoriteFolder).assertThat().existsInRepo();

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
        getCmisApi().usingSite(site).createFile(favoriteFile).assertThat().existsInRepo();
        getRestApi().withCoreAPI().usingAuthUser().addFileToFavorites(favoriteFile);

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
        getCmisApi().usingSite(site).createFolder(folder).assertThat().existsInRepo();
        getRestApi().withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

        documentLibraryPage.navigate(site)
            .usingContent(folder)
                .assertRemoveFolderFromFavoritesTooltipEqualsWithExpected()
                .removeFromFavorites()
                .assertAddToFavoritesIsDisplayed();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }
}
