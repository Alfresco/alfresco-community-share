package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CopyingContentTests extends BaseTests
{
    private DocumentLibraryPage2 documentLibraryPage;
    private FolderModel sharedFiles = new FolderModel("Shared Files");

    private UserModel testUser;
    private SiteModel testSite;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        testUser = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        cmisApi.authenticateUser(testUser);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        setupAuthenticatedSession(testUser);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(testUser);
        deleteSites(testSite);
    }

    @TestRail (id = "C7377")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkCopyFileToSharedFiles()
    {
        FileModel fileToCopy = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        cmisApi.usingSite(testSite).createFile(fileToCopy).assertThat().existsInRepo();

        documentLibraryPage.navigate(testSite)
            .usingContent(fileToCopy).clickCopyTo()
            .selectSharedFilesDestination()
            .selectFolder(sharedFiles)
            .clickCopyToButton();
        FileModel copiedFile = new FileModel(fileToCopy.getName());
        copiedFile.setCmisLocation(Utility.buildPath(cmisApi.getSharedPath(), fileToCopy.getName()));
        cmisApi.usingResource(copiedFile).assertThat().existsInRepo()
            .and().delete();
    }

    @TestRail (id = "C7378")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkCancelCopyFileToSharedFiles()
    {
        FileModel fileToCopy = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        cmisApi.usingSite(testSite).createFile(fileToCopy).assertThat().existsInRepo();

        documentLibraryPage.navigate(testSite)
            .usingContent(fileToCopy).clickCopyTo()
            .selectSharedFilesDestination()
            .clickCancelButton();
        FileModel copiedFile = new FileModel(fileToCopy.getName());
        copiedFile.setCmisLocation(Utility.buildPath(cmisApi.getSharedPath(), fileToCopy.getName()));

        cmisApi.usingResource(copiedFile).assertThat().doesNotExistInRepo();
    }

    @TestRail (id = "C7388")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkCopyFolderToPublicSite()
    {
        SiteModel siteDestination = dataSite.usingUser(testUser).createPublicRandomSite();
        FolderModel folderToCopy = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);

        cmisApi.usingSite(testSite).createFolder(folderToCopy)
            .usingResource(folderToCopy).createFile(subFile);
        documentLibraryPage.navigate(testSite)
            .usingContent(folderToCopy).clickCopyTo()
            .selectAllSitesDestination()
            .selectSite(siteDestination).clickCopyToButton();

        documentLibraryPage.navigate(siteDestination)
            .usingContent(folderToCopy).assertContentIsDisplayed()
            .selectFolder()
            .usingContent(subFile).assertContentIsDisplayed();

        dataSite.usingAdmin().deleteSite(siteDestination);
    }
}
