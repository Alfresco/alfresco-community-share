package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RenamingContentTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage2 documentLibraryPage2;

    private UserModel user;
    private SiteModel site;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        cmisApi.authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }

    @TestRail (id = "C7419")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void renameFileByEditIcon()
    {
        FileModel fileToRename = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel newFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "");
        cmisApi.usingSite(site).createFile(fileToRename);

        documentLibraryPage2.navigate(site)
            .usingContent(fileToRename)
                .clickRenameIcon()
                .typeNewNameAndSave(newFile.getName());
        documentLibraryPage2.usingContent(fileToRename).assertContentIsNotDisplayed();
        documentLibraryPage2.usingContent(newFile).assertContentIsDisplayed();
    }

    @TestRail (id = "C7420")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void renameFolderByEditIcon()
    {
        FolderModel folderToRename = FolderModel.getRandomFolderModel();
        FolderModel newFolder = FolderModel.getRandomFolderModel();
        cmisApi.usingSite(site).createFolder(folderToRename);

        documentLibraryPage2.navigate(site)
            .usingContent(folderToRename)
            .clickRenameIcon()
            .typeNewNameAndSave(newFolder.getName());
        documentLibraryPage2.usingContent(folderToRename).assertContentIsNotDisplayed();
        documentLibraryPage2.usingContent(newFolder).assertContentIsDisplayed();
    }

    @TestRail (id = "C7431")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelRenamingContent()
    {
        FileModel fileToRename = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel newFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "");
        cmisApi.usingSite(site).createFile(fileToRename);

        documentLibraryPage2.navigate(site)
            .usingContent(fileToRename)
            .clickRenameIcon()
            .typeNewNameAndCancel(newFile.getName());
        documentLibraryPage2.usingContent(fileToRename).assertContentIsDisplayed();
        documentLibraryPage2.usingContent(newFile).assertContentIsNotDisplayed();
    }
}