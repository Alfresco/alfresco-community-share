package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 9/2/2016.
 */
public class DeletingContentTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel user;
    private SiteModel testSite;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(user).createPublicRandomSite();
        cmisApi.authenticateUser(user);
        setupAuthenticatedSession(user);
        documentLibraryPage.navigate(testSite);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(testSite);
    }

    @TestRail (id = "C9544")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyDeleteDocument()
    {
        FileModel fileToDelete = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(testSite).createFile(fileToDelete).assertThat().existsInRepo();

        documentLibraryPage.navigate(testSite)
            .usingContent(fileToDelete)
            .clickDelete()
                .assertDeleteDialogHeaderEqualsTo(language.translate("documentLibrary.deleteDocument"))
                .assertConfirmDeleteMessageEqualsTo(fileToDelete)
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

        cmisApi.usingSite(testSite).createFolder(folderToDelete)
            .usingResource(folderToDelete).createFolder(subFolder).createFile(subFile);
        documentLibraryPage.navigate(testSite)
            .usingContent(folderToDelete)
            .clickDelete()
                .assertDeleteDialogHeaderEqualsTo(language.translate("documentLibrary.deleteFolder"))
                .assertConfirmDeleteMessageEqualsTo(folderToDelete)
                .clickDelete();

        documentLibraryPage.usingContent(folderToDelete).assertContentIsNotDisplayed();
        cmisApi.usingResource(folderToDelete).assertThat().doesNotExistInRepo()
               .usingResource(subFile).assertThat().doesNotExistInRepo()
               .usingResource(subFolder).assertThat().doesNotExistInRepo();
    }

    @TestRail (id = "C6968")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelDeletingFolder()
    {
        FolderModel folderToCancel = FolderModel.getRandomFolderModel();
        cmisApi.usingSite(testSite).createFolder(folderToCancel);

        documentLibraryPage.navigate(testSite)
            .usingContent(folderToCancel)
                .clickDelete().clickCancel();
        documentLibraryPage.usingContent(folderToCancel).assertContentIsDisplayed();
    }
}
