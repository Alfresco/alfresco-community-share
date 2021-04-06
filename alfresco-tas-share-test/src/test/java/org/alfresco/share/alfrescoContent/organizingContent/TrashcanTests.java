package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.user.profile.UserTrashcanPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class TrashcanTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private UserTrashcanPage userTrashcanPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userTrashcanPage = new UserTrashcanPage(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C10506")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyEmptyTrashcan()
    {
        FolderModel folderToDelete = FolderModel.getRandomFolderModel();
        FileModel file = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get()).usingShared()
            .createFolder(folderToDelete).createFile(file);
        getCmisApi().usingResource(file).delete()
            .and().usingResource(folderToDelete).deleteFolderTree();

        userTrashcanPage.navigate(user.get())
            .clickEmptyButton()
            .assertDeleteDialogHeaderEqualsTo(language.translate("emptyTrashcan.title"))
            .assertConfirmDeleteMessageEqualsTo(language.translate("emptyTrashcan.message"))
            .confirmDeletion();
        userTrashcanPage.assertNoItemsExistMessageIsDisplayed()
            .assertNoItemsExistMessageEqualTo(language.translate("emptyTrashcan.noItems"));
    }

    @TestRail (id = "C7572")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyTrashcanDeleteFile()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file)
            .then().usingResource(file).delete();

        userTrashcanPage.navigate(user.get())
            .clickDeleteButton(file)
            .confirmDeletion();
        userTrashcanPage.assertContentIsNotDisplayed(file);
    }

    @TestRail (id = "C7573")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyTrashcanDeleteFolder()
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFolder(folder)
                .then().usingResource(folder).delete();

        userTrashcanPage.navigate(user.get())
            .clickDeleteButton(folder)
            .confirmDeletion();
        userTrashcanPage.assertContentIsNotDisplayed(folder);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}