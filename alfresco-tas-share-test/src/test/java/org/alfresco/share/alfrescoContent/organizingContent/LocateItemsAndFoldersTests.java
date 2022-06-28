package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.po.enums.DocumentsFilter.FAVORITES;
import static org.alfresco.po.enums.DocumentsFilter.RECENTLY_ADDED;
import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.alfresco.utility.report.Bug;
import org.testng.annotations.*;

public class LocateItemsAndFoldersTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(user.get());
     //   getRestApi().authenticateUser(user.get());
        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()));

        authenticateUsingLoginPage(user.get());
    }

    @Bug (id = "MNT-17556")
    @TestRail (id = "C7516")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateFile()
    {
        SiteModel site = getDataSite().usingUser(user.get()).createPublicRandomSite();
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site)
            .createFile(file).assertThat().existsInRepo();

        documentLibraryPage.navigate(site)
            .usingContent(file).assertContentIsDisplayed();
        documentLibraryPage.usingContentFilters()
            .selectFromDocumentsFilter(RECENTLY_ADDED)
                .assertDocumentsFilterHeaderTitleEqualsTo(language.translate("documentLibrary.documentsFilter.recentlyAdded.title"))
            .usingContent(file)
            .clickLocate();
        documentLibraryPage.usingContent(file).assertContentIsHighlighted();

        getDataSite().usingAdmin().deleteSite(site);
    }

    @Bug (id = "MNT-17556")
    @TestRail (id = "C7517")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateFolderDetailedView() throws Exception
    {
        SiteModel site = getDataSite().usingUser(user.get()).createPublicRandomSite();

        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site).createFolder(folder).assertThat().existsInRepo();
        getRestApi().withCoreAPI().usingAuthUser().addFolderToFavorites(folder);
     //   setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
       //     .withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

        documentLibraryPage.navigate(site)
            .usingContent(folder).assertContentIsDisplayed();
        documentLibraryPage.usingContentFilters()
            .selectFromDocumentsFilter(FAVORITES)
                .assertDocumentsFilterHeaderTitleEqualsTo(language.translate("documentLibrary.documentsFilter.favorites.title"))
            .usingContent(folder)
            .clickLocate();
        documentLibraryPage.usingContent(folder).assertContentIsHighlighted();

        getDataSite().usingAdmin().deleteSite(site);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
    }
}
