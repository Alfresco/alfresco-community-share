package org.alfresco.share.alfrescoContent.buildingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateLinksTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final ThreadLocal<FileModel> file = new ThreadLocal<>();

    private DocumentLibraryPage2 documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private MyActivitiesDashlet myActivitiesDashlet;
    private SiteActivitiesDashlet siteActivitiesDashlet;
    private SearchPage searchPage;
    private SiteDashboardPage siteDashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        myActivitiesDashlet = new MyActivitiesDashlet(webDriver);
        siteActivitiesDashlet = new SiteActivitiesDashlet(webDriver);
        searchPage = new SearchPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        file.set(FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT));
        getCmisApi().authenticateUser(user.get()).usingSite(site.get()).createFile(file.get());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C42605")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibraryActions()
    {
        documentLibraryPage.navigate(site.get())
            .usingContent(file.get())
                .clickCopyTo().assertCreateLinkButtonIsDisplayed();
    }

    @TestRail (id = "C42606")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibrarySelectedItemsSingleItem()
    {
        documentLibraryPage.navigate(site.get())
            .checkContent(file.get())
                .clickSelectedItems().clickCopyToFromSelectedItems()
                    .assertCreateLinkButtonIsDisplayed();
    }

    @TestRail (id = "C42607")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibrarySelectedItemsMultipleItems()
    {
        FileModel file2 = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
                .createFile(file2)
                .createFolder(folder);

        documentLibraryPage.navigate(site.get())
            .checkContent(file.get(), file2, folder)
                .clickSelectedItems().clickCopyToFromSelectedItems()
                    .assertCreateLinkButtonIsDisplayed();
    }

    @TestRail (id = "C42608")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocumentDetailsActions()
    {
        documentDetailsPage.navigate(file.get())
            .clickCopyTo().assertCreateLinkButtonIsDisplayed();
    }

    @TestRail (id = "C42609")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromSearchResultsActions()
    {
        searchPage.navigate().searchForContentWithRetry(file.get())
            .usingContent(file.get()).assertIsDisplayed()
                .clickActions().clickCopyTo()
                    .assertCreateLinkButtonIsDisplayed().assertCreateLinkButtonIsDisabled();
    }

    @TestRail (id = "C42611, C42610")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromSearchResultsMultipleItemsSelected()
    {
        String randomName = RandomStringUtils.randomAlphanumeric(7);
        FileModel file1 = new FileModel(randomName + "-file1.txt", FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel file2 = new FileModel(randomName + "-file2.txt", FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
                .createFile(file1).createFile(file2);

        searchPage.navigate()
            .searchWithKeywordAndWaitForContents(randomName, file1, file2);
        searchPage.usingContent(file1).assertIsDisplayed();
        searchPage.usingContent(file2).assertIsDisplayed();
        searchPage.checkContent(file1, file2)
            .clickSelectedItems().clickCopyToForSelectedItems()
                .assertCreateLinkButtonIsDisplayed().assertCreateLinkButtonIsDisabled();
    }

    @TestRail (id = "C42613")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonInMoveToDialog()
    {
        documentLibraryPage.navigate(site.get())
            .usingContent(file.get())
                .clickMoveTo().assertCreateLinkButtonIsNotDisplayed();
    }

    @TestRail (id = "C42614")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLinkIsCreatedAtDestination()
    {
        FileModel linkedFile = new FileModel(String.format("Link to %s", file.get().getName()));
        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get()).usingSite(site.get()).createFolder(folder);

        documentLibraryPage.navigate(site.get())
            .usingContent(file.get()).clickCopyTo()
                .selectRecentSitesDestination()
                .selectSite(site.get()).selectFolder(folder)
                    .clickCreateLinkButton();
        documentLibraryPage.assertLastNotificationMessageEquals(String.format(language.translate("links.create.notificationMessage"), "1"))
            .usingContent(folder).selectFolder()
                .usingContent(linkedFile)
                    .assertContentIsDisplayed()
                    .assertThumbnailLinkTypeIsDisplayed();
    }

    @TestRail (id = "C42620")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void duplicateLinksAreNotAllowed()
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get()).usingSite(site.get()).createFolder(folder);

        documentLibraryPage.navigate(site.get())
            .usingContent(file.get()).clickCopyTo()
            .selectRecentSitesDestination()
            .selectSite(site.get()).selectFolder(folder)
                .clickCreateLinkButton();
        documentLibraryPage.usingContent(file.get())
                .clickCopyTo()
            .selectRecentSitesDestination().selectSite(site.get()).selectFolder(folder)
                .clickCreateLinkButton();
        documentLibraryPage.assertLastNotificationMessageEquals(language.translate("links.duplicate.notificationMessage"));
    }

    @TestRail (id = "C42621")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createdLinkDisplayedInMyActivitiesDashlet()
    {
        documentLibraryPage.navigate(site.get())
            .usingContent(file.get())
                .clickCopyTo().selectSite(site.get())
                .clickCreateLinkButton();
        userDashboardPage.navigate(user.get());
        myActivitiesDashlet.assertCreatedLinkActivityIsDisplayed(user.get(), file.get(), site.get());
    }

    @TestRail (id = "C42622")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createdLinkDisplayedInSiteActivitiesDashlet()
    {
        documentLibraryPage.navigate(site.get())
            .usingContent(file.get()).clickCopyTo().selectSite(site.get())
                .clickCreateLinkButton();
        siteDashboardPage.navigate(site.get());
        siteActivitiesDashlet.assertCreatedLinkActivityIsDisplayed(user.get(), file.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}