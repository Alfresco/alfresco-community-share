package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.common.GroupModelRoles.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MovingContentTests extends BaseTest
{
    private UserModel user;
    private SiteModel site;

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);

        getCmisApi().authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @TestRail (id = "C7345")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkMoveFileToFolderInSite()
    {
        FolderModel destination = FolderModel.getRandomFolderModel();
        FileModel fileToMove = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site).createFile(fileToMove).createFolder(destination);

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
        FolderModel destination = FolderModel.getRandomFolderModel();
        FolderModel folderToMove = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site)
            .createFolder(destination)
            .createFolder(folderToMove)
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