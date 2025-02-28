package org.alfresco.share.alfrescoContent.viewingContent;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.FileType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

@Slf4j
public class ViewingFolderDetailsTest extends BaseTest
{
    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private FolderModel folderToCheck;
    private FileModel fileToCheck;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Creating a random user and a random public site");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        getCmisApi().authenticateUser(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);

        log.info("Create Folder in document library.");
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Create File in document library.");
        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C5850")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyViewFolderDetails()
    {
        log.info("Step 1: Navigate to Document Library page for testSite");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 2: Verify the DocumentListDisplayed.");
        documentLibraryPage
            .assertVerifyDocumentListDisplayed();

        log.info("Step 3: Verify the 'Options' menu. Make sure that 'Hide Folders' isn't selected. Otherwise check 'Show Folders'.\"");
        documentLibraryPage
            .assertVerifyOptionsMenuDisplayed();
        documentLibraryPage
            .assertVerifyHideFoldersMenuOptionDisplayed();

        log.info("Step 4 - Hover over a folder (e.g. testFolder) in the file list in the document 'Library' and click on 'View Details' icon.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.VIEW_DETAILS);
        documentDetailsPage
            .assertVerifyFilePropertiesDetailsDisplayed()
            .assertVerifyFolderActionsPanelDisplayed()
            .assertVerifySocialFeaturesActionsPanelDisplayed()
            .assertVerifyTagsFeaturePanelDisplayed();

        log.info("Step 5 - Click the folder in the breadcrumb trail at the top of the screen to return to the item list for that folder.");
        documentDetailsPage
            .clickOnFolderFromBreadcrumbTrail();
        documentLibraryPage
            .assertVerifyDocumentListDisplayed();
    }

    @TestRail (id = "C7001")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void hideFolders()
    {
        log.info("Step 1: Navigate to Document Library page for testSite");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 2: Verify the DocumentListDisplayed.");
        documentLibraryPage
            .assertVerifyDocumentListDisplayed();

        log.info("Step 3: Verify the hide folder");
        documentLibraryPage.clickOnHideFolder();
        documentLibraryPage.assertVerifyDocumentListDisplayed();
        assertFalse(documentDetailsPage.isFolderActionsPanelDisplayed(), "Folder actions panel is not displayed");
    }

    @TestRail (id = "C7002")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyViewDetails()
    {
        log.info("Step 1: Navigate to Document Library page for testSite");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 2: Verify the View Details");
        documentLibraryPage.selectItemAction(folderToCheck.getName(), ItemActions.VIEW_DETAILS);
        documentDetailsPage.assertVerifyFolderActionsPanelDisplayed();
    }

    @TestRail (id = "C7003")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyBreadcrumbLinks()
    {
        log.info("Step 1: Navigate to Documents Library page for testSite");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 2: Verify the Breadcrumb Links");
        documentLibraryPage.selectItemAction(folderToCheck.getName(), ItemActions.VIEW_DETAILS);
        documentDetailsPage.clickOnFolderFromBreadcrumbTrail();
        documentLibraryPage.assertVerifyDocumentListDisplayed();
    }
}

