package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AddRemoveFavoriteContentTests extends BaseTest
{
    private static final String FILE_CONTENT = "Share file content";

    private UserModel user;
    private SiteModel site;

    private DocumentLibraryPage2 documentLibraryPage;

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
        cmisApi.authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @TestRail (id = "C7501")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyAddFileToFavorites()
    {
        FileModel favoriteFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.usingSite(site).createFile(favoriteFile).assertThat().existsInRepo();

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
        cmisApi.usingSite(site).createFolder(favoriteFolder).assertThat().existsInRepo();

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
        cmisApi.usingSite(site).createFile(favoriteFile).assertThat().existsInRepo();
        restApi.withCoreAPI().usingAuthUser().addFileToFavorites(favoriteFile);

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
        cmisApi.usingSite(site).createFolder(folder).assertThat().existsInRepo();
        restApi.withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

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
