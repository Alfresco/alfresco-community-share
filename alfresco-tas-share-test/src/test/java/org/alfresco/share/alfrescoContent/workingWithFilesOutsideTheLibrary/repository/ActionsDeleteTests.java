package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.DeleteDocumentOrFolderDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ActionsDeleteTests extends ContextAwareWebTest
{
    private final String user = String.format("C8308TestUser%s", RandomData.getRandomAlphanumeric());
    private final String fileName = "0-C8308_file" + RandomData.getRandomAlphanumeric();
    private final String fileContent = "C8308 content";
    private final String path = "User Homes/" + user;
    private final String folderName = "0-C8308_Folder" + RandomData.getRandomAlphanumeric();
    private final String fileNameC13749 = "0-Repo_file" + RandomData.getRandomAlphanumeric();
    private final String folderNameC13751 = "0-Repo_Folder" + RandomData.getRandomAlphanumeric();
    private final String path1 = "/";
    @Autowired
    DeleteDocumentOrFolderDialog deleteDocumentOrFolderDialog;
    @Autowired
    private RepositoryPage repositoryPage;
    @Autowired
    private DeleteDialog deleteDialog;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createDocumentInRepository(adminUser, adminPassword, path1, DocumentType.TEXT_PLAIN, fileNameC13749, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC13751, path1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        contentService.deleteTreeByPath(adminUser, adminPassword, folderNameC13751);

    }

    @TestRail (id = "C8308")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void deleteDocument()
    {
        // Precondition
        setupAuthenticatedSession(user, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileName), fileName + " is not available in Repository");

        LOG.info("Step 1: Hover over the file you want to delete and press More, select Delete Document");
        repositoryPage.clickDocumentLibraryItemAction(fileName, "Delete Document", deleteDialog);
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("documentLibrary.deleteDialogMessage"), fileName), "Delete dialog message= ");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "'Delete' button is not displayed.");
        assertTrue(deleteDialog.isCancelButtonDisplayed(), "'Cancel' button is not displayed.");

        LOG.info("Step 2: Press \"Delete\"");
        deleteDocumentOrFolderDialog.confirmDocumentOrFolderDelete();
        repositoryPage.navigate();
        assertFalse(repositoryPage.isContentNameDisplayed(fileName), fileName + " is displayed.");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8309")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void deleteFolder()
    {
        //Precondition
        setupAuthenticatedSession(user, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderName), folderName + " is not available in Repository");

        LOG.info("Step 1: Hover over the folder you want to delete and press More, select Delete Folder");
        repositoryPage.mouseOverContentItem(folderName);
        repositoryPage.clickDocumentLibraryItemAction(folderName, "Delete Folder", deleteDialog);
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("documentLibrary.deleteDialogMessage"), folderName), "Delete dialog message= ");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "'Delete' button is not displayed.");
        assertTrue(deleteDialog.isCancelButtonDisplayed(), "'Cancel' button is not displayed.");

        LOG.info("Step 2: Press \"Delete\"");
        deleteDocumentOrFolderDialog.confirmDocumentOrFolderDelete();
        repositoryPage.navigate();
        assertFalse(repositoryPage.isContentNameDisplayed(folderName), folderName + " is displayed.");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C13749")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void nonAdminUserCanNotDeleteFileOrFolderFromTheMainRepository()
    {
        //Precondition
        setupAuthenticatedSession(user, password);
        repositoryPage.navigate();

        LOG.info("Step 1: Mouse over file name and check that the More and the Delete Document option is not available");
        repositoryPage.mouseOverContentItem(fileNameC13749);
        Assert.assertFalse(repositoryPage.isMoreMenuDisplayed(fileNameC13749), "'More' menu displayed for " + fileNameC13749);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(fileNameC13749, "Delete Document"));

        LOG.info("Step 2: Mouse over folder name and check that the More and the  Delete Folder option is not available");
        repositoryPage.mouseOverContentItem(folderNameC13751);
        Assert.assertFalse(repositoryPage.isMoreMenuDisplayed(folderNameC13751), "'More' menu displayed for " + folderNameC13751);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(folderNameC13751, "Delete Folder"));
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C13751")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void adminUserCanDeleteFileOrFolderInMainRepository()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigate();

        LOG.info("Step 1: Mouse over file name and check that the More and the  Delete Document options are available");
        repositoryPage.mouseOverContentItem(fileNameC13749);
        Assert.assertTrue(repositoryPage.isMoreMenuDisplayed(fileNameC13749), "'More' menu displayed for " + fileNameC13749);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC13749, "Delete Document"));

        LOG.info("Step 2: Mouse over folder name and check that the More and the  Delete Folder options are available");
        repositoryPage.mouseOverContentItem(folderNameC13751);
        Assert.assertTrue(repositoryPage.isMoreMenuDisplayed(folderNameC13751), "'More' menu displayed for " + folderNameC13751);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(folderNameC13751, "Delete Folder"));
        cleanupAuthenticatedSession();
    }
}
