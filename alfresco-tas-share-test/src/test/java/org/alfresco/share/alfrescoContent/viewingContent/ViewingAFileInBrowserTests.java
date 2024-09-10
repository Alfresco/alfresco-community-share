package org.alfresco.share.alfrescoContent.viewingContent;


import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.Assert;
import org.testng.annotations.*;

import static org.alfresco.common.Utils.isFileInDirectory;


@Slf4j
public class ViewingAFileInBrowserTests extends BaseTest
{

    private final String description = "description-" ;

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
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileToCheck);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C5920")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "DownloadTest" })
    public void verifyViewAFileInBrowser()
    {
        log.info("Step 1: Navigate to Document Library page for testSite and click on foldername.");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFolderName(folderToCheck.getName());

        log.info("Step 2: Verify the ActionsAvailableForLibraryItem.");
        documentLibraryPage
            .assertAreActionsAvailableForLibraryItems(fileToCheck.getName());
        documentLibraryPage
            .assertisMoreMenuDisplayed(fileToCheck.getName());

        log.info("Step 3: Click View In Browser.");
        documentLibraryPage
            .selectItemActionFormFirstThreeAvailableOptions(fileToCheck.getName(),ItemActions.VIEW_IN_BROWSER);
        Assert.assertTrue(isFileInDirectory(fileToCheck.getName(), null), "The file was not found in the specified location");
    }
}

