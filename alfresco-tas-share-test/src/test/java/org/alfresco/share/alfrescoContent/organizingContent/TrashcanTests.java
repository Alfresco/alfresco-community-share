package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.user.profile.UserTrashcanPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TrashcanTests extends ContextAwareWebTest
{
    @Autowired
    private UserTrashcanPage userTrashcanPage;

    private UserModel trashUser;
    private SiteModel trashSite;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        trashUser = dataUser.usingAdmin().createRandomTestUser();
        trashSite = dataSite.usingUser(trashUser).createPublicRandomSite();

        cmisApi.authenticateUser(trashUser);
        setupAuthenticatedSession(trashUser);
    }

    @TestRail (id = "C10506")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void emptyTrashcan()
    {
        FolderModel folderToDelete = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.usingSite(trashSite)
            .createFolder(folderToDelete).createFile(file)
                .and().assertThat().existsInRepo();
        cmisApi.usingResource(file).delete()
            .and().usingResource(folderToDelete).deleteFolderTree();

        userTrashcanPage.navigate(trashUser)
            .clickEmptyButton()
                .assertDeleteDialogHeaderEqualsTo(language.translate("emptyTrashcan.title"))
                .assertConfirmDeleteMessageEqualsTo(language.translate("emptyTrashcan.message"))
                .clickDelete();
        userTrashcanPage.assertNoItemsExistMessageIsDisplayed()
            .assertNoItemsExistsMessageIsCorrect();
    }

    @TestRail (id = "C7572")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void trashcanDeleteFile()
    {
        FileModel file1 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(trashSite).createFile(file1)
            .then().usingResource(file1).delete();

        userTrashcanPage.navigate(trashUser)
            .clickDeleteButton(file1)
            .clickDelete();
        userTrashcanPage.assertContentIsNotDisplayed(file1);
    }

    @TestRail (id = "C7573")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void trashcanDeleteFolder()
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        cmisApi.usingSite(trashSite).createFolder(folder)
            .then().usingResource(folder).delete();

        userTrashcanPage.navigate(trashUser)
            .clickDeleteButton(folder)
            .clickDelete();
        userTrashcanPage.assertContentIsNotDisplayed(folder);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(trashUser);
        deleteSites(trashSite);
    }
}