package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateLinksTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage2 documentLibraryPage;

    private String randomName = RandomStringUtils.randomAlphanumeric(5);
    private UserModel linksUser;
    private SiteModel testSite;
    private FileModel file1;
    private FileModel file2;
    private FolderModel testFolder;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private MyActivitiesDashlet myActivitiesDashlet;

    @Autowired
    private SiteActivitiesDashlet siteActivitiesDashlet;

    @Autowired
    private SearchPage searchPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        linksUser = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(linksUser).createPublicRandomSite();

        file1 = new FileModel(randomName + "-file1.txt", FileType.TEXT_PLAIN, FILE_CONTENT);
        file2 = new FileModel(randomName + "-file2.txt", FileType.TEXT_PLAIN, FILE_CONTENT);
        testFolder = FolderModel.getRandomFolderModel();
        cmisApi.authenticateUser(linksUser).usingSite(testSite)
            .createFile(file1).createFile(file2).createFolder(testFolder);
        setupAuthenticatedSession(linksUser);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(linksUser);
        deleteSites(testSite);
    }

    @TestRail (id = "C42605")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibraryActions()
    {
        documentLibraryPage.navigate(testSite)
            .usingContent(file1)
                .clickCopyTo().assertCreateLinkButtonIsDisplayed();
    }

    @TestRail (id = "C42606")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibrarySelectedItemsSingleItem()
    {
        documentLibraryPage.navigate(testSite)
            .checkContent(file1)
                .clickSelectItems().clickCopyToFromSelectedItems()
                    .assertCreateLinkButtonIsDisplayed();
    }

    @TestRail (id = "C42607")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibrarySelectedItemsMultipleItems()
    {
        documentLibraryPage.navigate(testSite)
            .checkContent(file1, file2, testFolder)
                .clickSelectItems().clickCopyToFromSelectedItems()
                    .assertCreateLinkButtonIsDisplayed();
    }

    @TestRail (id = "C42608")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocumentDetailsActions()
    {
        documentDetailsPage.navigate(file1)
            .clickCopyTo().assertCreateLinkButtonIsDisplayed();
    }

    @TestRail (id = "C42609")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromSearchResultsActions()
    {
        searchPage.navigate().searchForContentWithRetry(file1)
            .usingContent(file1).assertIsDisplayed()
                .clickActions().clickCopyTo()
                    .assertCreateLinkButtonIsDisplayed().assertCreateLinkButtonIsDisabled();
    }

    @TestRail (id = "C42611, C42610")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromSearchResultsMultipleItemsSelected()
    {
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
        documentLibraryPage.navigate(testSite)
            .usingContent(file1)
                .clickMoveTo().assertCreateLinkButtonIsNotDisplayed();
    }

    @TestRail (id = "C42614")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLinkIsCreatedAtDestination()
    {
        FileModel linkedFile = new FileModel(String.format("Link to %s", file1.getName()));
        documentLibraryPage.navigate(testSite)
            .usingContent(file1).clickCopyTo()
                .selectRecentSitesDestination()
                .selectSite(testSite).selectFolder(testFolder)
                    .clickCreateLink();
        documentLibraryPage.assertLastNotificationMessageIs(String.format(language.translate("links.create.notificationMessage"), "1"))
            .usingContent(testFolder).selectFolder()
                .usingContent(linkedFile).assertContentIsDisplayed().assertThumbnailIsLinkType();
    }

    @TestRail (id = "C42620")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void duplicateLinksAreNotAllowed()
    {
        documentLibraryPage.navigate(testSite)
            .usingContent(file2).clickCopyTo()
            .selectRecentSitesDestination()
            .selectSite(testSite).selectFolder(testFolder)
                .clickCreateLink();
        documentLibraryPage.usingContent(file2)
                .clickCopyTo()
            .selectRecentSitesDestination().selectSite(testSite).selectFolder(testFolder)
                .clickCreateLink();
        documentLibraryPage.assertLastNotificationMessageIs(language.translate("links.duplicate.notificationMessage"));
    }

    @TestRail (id = "C42621")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createdLinkDisplayedInMyActivitiesDashlet()
    {
        FileModel userFile = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        cmisApi.usingSite(testSite).createFile(userFile);
        documentLibraryPage.navigate(testSite)
            .usingContent(userFile).clickCopyTo().selectSite(testSite).selectFolder(testFolder)
                .clickCreateLink();
        userDashboard.navigate(linksUser);
        myActivitiesDashlet.assertCreatedLinkActivityIsDisplayed(linksUser, userFile, testSite);
    }

    /*@TestRail (id = "C42622")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void createdLinkDisplayedInSiteActivitiesDashlet()
    {
        String name = firstName + " " + lastName;
        String activity = name + " created link to " + fileName3;
        documentLibraryPage.navigate(siteName2);
        LOG.info("STEP1: For a file/folder, click 'Copy to' option, select a destination folder and click 'Create Link' button");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName3, ItemActions.COPY_TO, copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileName3 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.clickDestinationButton("Recent Sites");
        copyMoveUnzipToDialog.clickSite(siteName2);
        copyMoveUnzipToDialog.clickCreateLink(documentLibraryPage);
        documentLibraryPage.selectDocumentLibraryItemRow(linkFile3);
        assertTrue(documentLibraryPage.isContentNameDisplayed(linkFile3),
            linkFile3 + " is displayed in destination of copy file, Document Library of " + siteName2);
        LOG.info("STEP2: Navigate to Site Dashboard page and verify Site Activities dashlet");
        siteDashboardPage.navigate(siteName2);
        assertEquals(siteDashboardPage.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        assertTrue(siteActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity),
            "Activity: '" + activity + "' is displayed in 'Site Activities' dashlet.");
    }*/
}