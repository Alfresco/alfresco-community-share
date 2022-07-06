package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LibraryFilterTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C6333")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shouldOpenFoldersFromLibraryFilter()
    {
        FolderModel parentFolder = FolderModel.getRandomFolderModel();
        FolderModel subFolder = FolderModel.getRandomFolderModel();
        FileModel parentFile = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        FileModel subFile = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);

        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
                .createFolder(parentFolder).usingResource(parentFolder)
                    .createFile(parentFile)
                    .createFolder(subFolder)
                    .usingResource(subFolder)
                        .createFile(subFile);

        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
            .clickFolderFromFilter(parentFolder)
                .usingContent(parentFile).assertContentIsDisplayed();
        documentLibraryPage.usingContentFilters()
            .clickFolderFromFilter(subFolder)
                .usingContent(subFile).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
