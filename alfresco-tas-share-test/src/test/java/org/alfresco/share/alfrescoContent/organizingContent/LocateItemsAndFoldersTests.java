package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.alfrescoContent.AlfrescoContentPage.DocumentsFilter;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.alfresco.utility.report.Bug;
import org.testng.annotations.*;

public class LocateItemsAndFoldersTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private ThreadLocal<DocumentLibraryPage2> documentLibraryPage = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage.set(new DocumentLibraryPage2(webDriver));

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        getRestApi().authenticateUser(user.get());

        setupAuthenticatedSession(user.get());
    }

    @Bug (id = "MNT-17556")
    @TestRail (id = "C7516")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateFile()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(file).assertThat().existsInRepo();

        documentLibraryPage.get().navigate(site.get())
            .usingContent(file).assertContentIsDisplayed();
        documentLibraryPage.get().selectFromDocumentsFilter(DocumentsFilter.RECENTLY_ADDED)
            .assertDocumentsFilterHeaderTitleEqualsTo(language.translate("documentLibrary.documentsFilter.recentlyAdded.title"))
            .usingContent(file)
            .clickLocate();
        documentLibraryPage.get().usingContent(file).assertContentIsHighlighted();
    }

    @Bug (id = "MNT-17556")
    @TestRail (id = "C7517")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateFolderDetailedView() throws Exception
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folder).assertThat().existsInRepo();
        getRestApi().withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

        documentLibraryPage.get().navigate(site.get())
            .usingContent(folder).assertContentIsDisplayed();
        documentLibraryPage.get().selectFromDocumentsFilter(DocumentsFilter.FAVORITES)
            .assertDocumentsFilterHeaderTitleEqualsTo(language.translate("documentLibrary.documentsFilter.favorites.title"))
            .usingContent(folder)
            .clickLocate();
        documentLibraryPage.get().usingContent(folder).assertContentIsHighlighted();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
