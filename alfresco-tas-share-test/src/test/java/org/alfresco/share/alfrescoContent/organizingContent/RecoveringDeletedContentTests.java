package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.common.GroupModelRoles.FILE_CONTENT;

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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RecoveringDeletedContentTests extends BaseTest
{
    private UserModel trashcanUser;
    private SiteModel trashcanSite;

    private UserTrashcanPage userTrashcanPage;
    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        trashcanUser = dataUser.usingAdmin().createRandomTestUser();
        trashcanSite = dataSite.usingUser(trashcanUser).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        userTrashcanPage = new UserTrashcanPage(browser);
        getCmisApi().authenticateUser(trashcanUser);
        setupAuthenticatedSession(trashcanUser);
    }

    @TestRail (id = "C7570")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyRecoverDeletedDocument()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(trashcanSite).createFile(file)
            .then().usingResource(file).delete();

        userTrashcanPage.navigate(trashcanUser)
            .clickRecoverButton(file);
        documentLibraryPage.navigate(trashcanSite)
            .usingContent(file).assertContentIsDisplayed();
    }

    @TestRail (id = "C7571")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyRecoverDeletedFolder()
    {
        FolderModel folderToDelete = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().usingSite(trashcanSite).createFolder(folderToDelete)
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