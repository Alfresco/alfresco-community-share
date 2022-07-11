package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
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

public class RecoveringDeletedContentTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> trashcanSite = new ThreadLocal<>();

    private UserTrashcanPage userTrashcanPage;
    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        userTrashcanPage = new UserTrashcanPage(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        trashcanSite.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7570")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyRecoverDeletedDocument()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(trashcanSite.get()).createFile(file)
            .then().usingResource(file).delete();

        userTrashcanPage.navigate(user.get())
            .clickRecoverButton(file);
        documentLibraryPage.navigate(trashcanSite.get())
            .usingContent(file).assertContentIsDisplayed();
    }

    @TestRail (id = "C7571")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyRecoverDeletedFolder()
    {
        FolderModel folderToDelete = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().usingSite(trashcanSite.get()).createFolder(folderToDelete)
            .then().usingResource(folderToDelete).createFile(subFile)
                .and().usingResource(folderToDelete).deleteFolderTree();

        userTrashcanPage.navigate(user.get())
            .clickRecoverButton(folderToDelete);

        documentLibraryPage.navigate(trashcanSite.get())
            .usingContent(folderToDelete).assertContentIsDisplayed()
            .selectFolder()
            .usingContent(subFile).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(trashcanSite.get());
    }
}