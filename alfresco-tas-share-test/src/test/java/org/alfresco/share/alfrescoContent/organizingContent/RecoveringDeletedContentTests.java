package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.user.profile.UserTrashcanPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RecoveringDeletedContentTests extends ContextAwareWebTest
{
    //@Autowired
    private UserTrashcanPage userTrashcanPage;

    //@Autowired
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel trashcanUser;
    private SiteModel trashcanSite;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        trashcanUser = dataUser.usingAdmin().createRandomTestUser();
        trashcanSite = dataSite.usingUser(trashcanUser).createPublicRandomSite();
        cmisApi.authenticateUser(trashcanUser);
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