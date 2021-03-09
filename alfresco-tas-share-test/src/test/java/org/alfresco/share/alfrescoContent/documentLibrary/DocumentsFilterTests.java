package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.alfresco.po.enums.DocumentsFilter.*;
import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.alfresco.utility.constants.UserRole.SiteManager;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DocumentsFilterTests extends BaseTest
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

    @TestRail (id = "C6320, C10598")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shouldDisplayFileWhenAllDocumentsFilterIsSelected()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
            .selectFromDocumentsFilter(ALL_DOCUMENTS)
                .usingContent(testFile).assertContentIsDisplayed();
        documentLibraryPage.usingContentFilters()
            .selectFromDocumentsFilter(RECENTLY_ADDED)
                .usingContent(testFile).assertContentIsDisplayed();
    }

    @TestRail (id = "C6321")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shouldDisplayFileWhenImEditingFilterIsSelected()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile).assertThat().existsInRepo()
                .then().usingResource(testFile).checkOut().assertThat().documentIsCheckedOut();

        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
            .selectFromDocumentsFilter(EDITING_ME)
            .usingContent(testFile).assertContentIsDisplayed();
    }

    @TestRail (id = "C10597")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shouldDisplayFileWhenOthersAreEditingFilterIsSelected()
    {
        UserModel invitedUser = getDataUser().usingAdmin().createRandomTestUser();
        getDataUser().usingUser(user.get()).addUserToSite(invitedUser, site.get(), SiteManager);

        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile).assertThat().existsInRepo();
        getCmisApi().authenticateUser(invitedUser)
            .usingResource(testFile).checkOut().assertThat().documentIsCheckedOut();

        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
            .selectFromDocumentsFilter(EDITING_OTHERS)
            .usingContent(testFile).assertContentIsDisplayed();
    }

    @TestRail (id = "C6325")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void viewFilesFromMyFavorites() throws Exception
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile).assertThat().existsInRepo();
        getRestApi().withCoreAPI().usingAuthUser().addFileToFavorites(testFile);

        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
                .selectFromDocumentsFilter(MY_FAVORITES)
            .usingContent(testFile).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}