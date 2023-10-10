package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class OrganizingFoldersTests extends BaseTest
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

    @TestRail (id = "C6276, C6277")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createNewFolderUsingMenu()
    {
        String randomFolder = RandomData.getRandomFolder();
        FolderModel testFolder = new FolderModel(randomFolder, randomFolder, randomFolder);

        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreate().clickFolder()
            .assertDialogTitleEquals(language.translate("newFolderDialog.title"))
            .assertNameInputIsDisplayed()
            .assertMandatoryIndicatorForNameIsDisplayed()
            .assertTitleInputIsDisplayed()
            .assertDescriptionInputIsDisplayed()
            .assertSaveButtonIsDisplayed()
            .assertCancelButtonIsDisplayed()
            .fillInDetails(testFolder.getName(), testFolder.getTitle(), testFolder.getDescription())
            .clickSave();
        documentLibraryPage.usingContent(testFolder).assertContentIsDisplayed()
            .selectFolder();
        documentLibraryPage.assertFolderIsDisplayedInBreadcrumb(testFolder)
            .usingContentFilters()
            .assertFolderIsDisplayedInFilter(testFolder);
    }

    @TestRail (id = "C6278")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelCreatingFolder()
    {
        FolderModel cancelFolder = FolderModel.getRandomFolderModel();
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreate().clickFolder()
            .typeName(cancelFolder.getName())
            .clickCancel();
        documentLibraryPage.usingContent(cancelFolder).assertContentIsNotDisplayed();
    }

    @TestRail (id = "C6291")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkFolderStructure()
    {
        FolderModel parentFolder = FolderModel.getRandomFolderModel();
        FolderModel subFolder = FolderModel.getRandomFolderModel();

        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFolder(parentFolder)
                .usingResource(parentFolder).createFolder(subFolder);

        documentLibraryPage.navigate(site.get())
            .usingContent(parentFolder).assertContentIsDisplayed();
        documentLibraryPage.usingContentFilters()
            .assertFolderIsDisplayedInFilter(parentFolder)
            .clickFolderFromFilter(parentFolder)
                .assertFolderIsDisplayedInBreadcrumb(parentFolder)
                .usingContentFilters()
                .assertFolderIsDisplayedInFilter(subFolder)
                    .usingContent(subFolder).assertContentIsDisplayed();
        documentLibraryPage
            .usingContentFilters()
            .clickFolderFromFilter(subFolder)
            .assertFolderIsDisplayedInBreadcrumb(parentFolder)
            .assertFolderIsDisplayedInBreadcrumb(subFolder)
                .clickFolderUpButton().waitForCurrentFolderBreadcrumb(parentFolder)
                    .assertFolderIsDisplayedInBreadcrumb(parentFolder)
                .clickFolderFromBreadcrumb(language.translate("documentLibrary.breadcrumb.select.documents"))
                    .usingContent(parentFolder).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}