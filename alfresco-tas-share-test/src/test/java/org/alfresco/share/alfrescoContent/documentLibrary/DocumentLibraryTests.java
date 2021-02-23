package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DocumentLibraryTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C6935, C6907")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAvailableOptionsForFolder()
    {
        FolderModel folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        siteDashboardPage.navigate(site.get())
            .clickDocumentLibrary();

        documentLibraryPage.assertBrowserPageTitleIs(language.translate("documentLibrary.browserTitle"));
        documentLibraryPage.usingContent(folderToCheck)
            .assertActionsAreAvailable(
                language.translate("documentLibrary.contentActions.downloadAsZip"),
                language.translate("documentLibrary.contentActions.viewDetails"),
                language.translate("documentLibrary.contentActions.editProperties"),
                language.translate("documentLibrary.contentActions.copyTo"),
                language.translate("documentLibrary.contentActions.moveTo"),
                language.translate("documentLibrary.contentActions.manageRules"),
                language.translate("documentLibrary.deleteFolder"),
                language.translate("documentLibrary.contentActions.managePermissions"),
                language.translate("documentLibrary.contentActions.manageAspects")
            );
    }

    @TestRail (id = "C6936")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAvailableOptionsForFile()
    {
        FileModel fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToCheck)
            .assertActionsAreAvailable(
                language.translate("documentLibrary.contentActions.download"),
                language.translate("documentLibrary.contentActions.viewInBrowser"),
                language.translate("documentLibrary.contentActions.editProperties"),
                language.translate("documentLibrary.contentAction.uploadNewVersion"),
                language.translate("documentLibrary.contentActions.editOffline"),
                language.translate("documentLibrary.contentActions.copyTo"),
                language.translate("documentLibrary.contentActions.moveTo"),
                language.translate("documentLibrary.deleteDocument"),
                language.translate("documentLibrary.contentActions.startWorkflow"),
                language.translate("documentLibrary.contentActions.managePermissions"),
                language.translate("documentLibrary.contentActions.manageAspects")
            );
    }

    @TestRail (id = "C6931, C6933")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void hideAndShowBreadcrumb()
    {
        documentLibraryPage.navigate(site.get())
            .assertDocumentsRootBreadcrumbIsDisplayed()
            .clickOptions()
                .selectHideBreadcrumbFromOptions()
                .assertDocumentsRootBreadcrumbIsNotDisplayed()
            .clickOptions()
                .selectShowBreadcrumbFromOptions()
                .assertDocumentsRootBreadcrumbIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}