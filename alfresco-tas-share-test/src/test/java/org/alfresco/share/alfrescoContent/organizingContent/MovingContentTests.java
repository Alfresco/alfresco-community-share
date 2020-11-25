package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MovingContentTests extends BaseTests
{
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel user;
    private SiteModel site;
    private FolderModel destination;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        destination = FolderModel.getRandomFolderModel();

        cmisApi.authenticateUser(user).usingSite(site).createFolder(destination);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        setupAuthenticatedSession(user);
    }

    @TestRail (id = "C7345")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkMoveFileToFolderInSite()
    {
        FileModel fileToMove = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(site).createFile(fileToMove);

        documentLibraryPage.navigate(site)
            .usingContent(fileToMove)
            .clickMoveTo()
            .selectRecentSitesDestination()
            .selectSite(site)
            .selectFolder(destination)
            .clickMoveButton();

        documentLibraryPage.usingContent(fileToMove).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(destination).selectFolder()
            .usingContent(fileToMove).assertContentIsDisplayed();
    }

    @TestRail (id = "C7346")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkMoveFolderWithChildrenInSite()
    {
        FolderModel folderToMove = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(site).createFolder(folderToMove)
            .then().usingResource(folderToMove).createFile(subFile);

        documentLibraryPage.navigate(site)
            .usingContent(folderToMove).clickMoveTo()
            .selectRecentSitesDestination()
            .selectSite(site)
            .selectFolder(destination)
            .clickMoveButton();
        documentLibraryPage.usingContent(folderToMove).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(destination).selectFolder()
            .usingContent(folderToMove)
            .assertContentIsDisplayed()
            .selectFolder()
            .usingContent(subFile)
            .assertContentIsDisplayed();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }
}