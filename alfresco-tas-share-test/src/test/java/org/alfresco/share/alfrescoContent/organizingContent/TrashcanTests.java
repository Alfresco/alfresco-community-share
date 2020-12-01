package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.common.GroupModelRoles.FILE_CONTENT;

import org.alfresco.po.share.user.profile.UserTrashcanPage;
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

public class TrashcanTests extends BaseTest
{
    private UserModel trashUser, cleanUser;
    private SiteModel trashSite;

    private UserTrashcanPage userTrashcanPage;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        trashUser = dataUser.usingAdmin().createRandomTestUser();
        cleanUser = dataUser.usingAdmin().createRandomTestUser();
        trashSite = dataSite.usingUser(trashUser).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userTrashcanPage = new UserTrashcanPage(browser);
    }

    @TestRail (id = "C10506")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyEmptyTrashcan()
    {
        FolderModel folderToDelete = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(cleanUser).usingShared()
            .createFolder(folderToDelete).createFile(file);
        getCmisApi().usingResource(file).delete()
            .and().usingResource(folderToDelete).deleteFolderTree();

        setupAuthenticatedSession(cleanUser);

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
        getCmisApi().authenticateUser(trashUser)
            .usingSite(trashSite).createFile(file)
            .then().usingResource(file).delete();
        setupAuthenticatedSession(trashUser);

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
        getCmisApi().authenticateUser(trashUser)
            .usingSite(trashSite).createFolder(folder)
                .then().usingResource(folder).delete();

        setupAuthenticatedSession(trashUser);
        userTrashcanPage.navigate(trashUser)
            .clickDeleteButton(folder)
            .clickDelete();
        userTrashcanPage.assertContentIsNotDisplayed(folder);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(trashUser, cleanUser);
        deleteSites(trashSite);
    }
}