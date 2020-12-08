package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

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

public class DeletingContentTests extends BaseTest
{
    private UserModel user;
    private SiteModel testSite;

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        getCmisApi().authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @TestRail (id = "C9544")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyDeleteDocument()
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        FileModel fileToDelete = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(testSite).createFolder(folder)
            .then().usingResource(folder)
                .createFile(fileToDelete).assertThat().existsInRepo();

        documentLibraryPage.navigate(testSite)
            .usingContent(folder).selectFolder()
                .usingContent(fileToDelete)
                .clickDelete()
                .assertDeleteDialogHeaderEqualsTo(language.translate("documentLibrary.deleteDocument"))
                .assertConfirmDeleteMessageForContentEqualsTo(fileToDelete)
                .clickDelete();
        documentLibraryPage.usingContent(fileToDelete).assertContentIsNotDisplayed();
    }

    @TestRail (id = "C6968")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyDeleteFolderWithChildren()
    {
        FolderModel folderToDelete = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        FolderModel subFolder = FolderModel.getRandomFolderModel();

        getCmisApi().usingSite(testSite).createFolder(folderToDelete)
            .usingResource(folderToDelete).createFolder(subFolder).createFile(subFile);
        documentLibraryPage.navigate(testSite)
            .usingContent(folderToDelete)
            .clickDelete()
            .assertDeleteDialogHeaderEqualsTo(language.translate("documentLibrary.deleteFolder"))
            .assertConfirmDeleteMessageForContentEqualsTo(folderToDelete)
            .clickDelete();

        documentLibraryPage.usingContent(folderToDelete).assertContentIsNotDisplayed();
        getCmisApi().usingResource(folderToDelete).assertThat().doesNotExistInRepo()
            .usingResource(subFile).assertThat().doesNotExistInRepo()
            .usingResource(subFolder).assertThat().doesNotExistInRepo();
    }

    @TestRail (id = "C6968")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelDeletingFolder()
    {
        FolderModel folderToCancel = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(testSite).createFolder(folderToCancel);

        documentLibraryPage.navigate(testSite)
            .usingContent(folderToCancel)
            .clickDelete().clickCancel();
        documentLibraryPage.usingContent(folderToCancel).assertContentIsDisplayed();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(testSite);
    }
}
