package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.user.profile.UserTrashcanPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RecoveringDeletedContentTests extends BaseTest
{
    private static final String FILE_CONTENT = "Share file content";

    private UserModel trashcanUser;
    private SiteModel trashcanSite;

    private UserTrashcanPage userTrashcanPage;
    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        trashcanUser = dataUser.usingAdmin().createRandomTestUser();
        trashcanSite = dataSite.usingUser(trashcanUser).createPublicRandomSite();
        cmisApi.authenticateUser(trashcanUser);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        userTrashcanPage = new UserTrashcanPage(browser);
        setupAuthenticatedSession(trashcanUser);
    }

    @TestRail (id = "C7570")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyRecoverDeletedDocument()
    {
        FileModel file1 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(trashcanSite).createFile(file1)
            .then().usingResource(file1).delete();

        userTrashcanPage.navigate(trashcanUser)
            .clickRecoverButton(file1);
        documentLibraryPage.navigate(trashcanSite)
            .usingContent(file1).assertContentIsDisplayed();
    }

    @TestRail (id = "C7571")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyRecoverDeletedFolder()
    {
        FolderModel folderToDelete = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.usingSite(trashcanSite).createFolder(folderToDelete)
            .then().usingResource(folderToDelete).createFile(subFile)
                .and().usingResource(folderToDelete).deleteFolderTree();

        userTrashcanPage.navigate(trashcanUser)
            .clickRecoverButton(folderToDelete);

        documentLibraryPage.navigate(trashcanSite)
            .usingContent(folderToDelete).assertContentIsDisplayed()
            .selectFolder()
            .usingContent(subFile).assertContentIsDisplayed();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(trashcanUser);
        deleteSites(trashcanSite);
    }
}