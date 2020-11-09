package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AddRemoveFavoriteContentTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage2 documentLibraryPage2;

    private UserModel user;
    private SiteModel site;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        cmisApi.authenticateUser(user);
        restApi.authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }

    @TestRail (id = "C7501")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyAddFileToFavorites()
    {
        FileModel favoriteFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.usingSite(site).createFile(favoriteFile).assertThat().existsInRepo();

        documentLibraryPage2.navigate(site)
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

        documentLibraryPage2.navigate(site)
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

        documentLibraryPage2.navigate(site)
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

        documentLibraryPage2.navigate(site)
            .usingContent(folder)
                .assertRemoveFolderFromFavoritesTooltipEqualsWithExpected()
                .removeFromFavorites()
                .assertAddToFavoritesIsDisplayed();
    }
}
