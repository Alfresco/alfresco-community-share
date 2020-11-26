package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.user.profile.UserTrashcanPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TrashcanTests extends BaseTest
{
    private final static String FILE_CONTENT = "Share file content";

    private UserModel trashUser;
    private SiteModel trashSite;

    private UserTrashcanPage userTrashcanPage;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        trashUser = dataUser.usingAdmin().createRandomTestUser();
        trashSite = dataSite.usingUser(trashUser).createPublicRandomSite();
        cmisApi.authenticateUser(trashUser);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userTrashcanPage = new UserTrashcanPage(browser);
        setupAuthenticatedSession(trashUser);
    }

    @TestRail (id = "C10506")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyEmptyTrashcan()
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
            .assertNoItemsExistMessageEqualTo(language.translate("emptyTrashcan.noItems"));
    }

    @TestRail (id = "C7572")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyTrashcanDeleteFile()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(trashSite).createFile(file)
            .then().usingResource(file).delete();

        userTrashcanPage.navigate(trashUser)
            .clickDeleteButton(file)
            .clickDelete();
        userTrashcanPage.assertContentIsNotDisplayed(file);
    }

    @TestRail (id = "C7573")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyTrashcanDeleteFolder()
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