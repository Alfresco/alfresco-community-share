package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ActionsDeleteTests extends ContextAwareWebTest
{
    @Autowired private RepositoryPage repositoryPage;

    @Autowired private DeleteDialog deleteDialog;


    private final String user = String.format("C8308TestUser%s", DataUtil.getUniqueIdentifier());
    private final String fileName = "C8308 file";
    private final String fileContent = "C8308 content";
    private final String path = "User Homes/" + user;
    private final String folderName = "C8308 Folder";
    private final String fileNameC13749 = "C13749 file"+DataUtil.getUniqueIdentifier();
    private final String folderNameC13751 = "C13751 Folder"+DataUtil.getUniqueIdentifier();
    private final String path1 ="";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createDocumentInRepository(adminUser, adminPassword, path1, DocumentType.TEXT_PLAIN, fileNameC13749, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC13751, path1);
    }

    @TestRail(id = "C8308")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})

    public void deleteDocument()
    {
        // Precondition
        setupAuthenticatedSession(user, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileName), fileName + " is not available in Repository");
        
        LOG.info("Step 1: Hover over the file you want to delete and press More, select Delete Document");
        repositoryPage.mouseOverFileName(fileName);
        repositoryPage.clickOnAction(fileName, "Delete Document");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("documentLibrary.deleteDialogMessage"), fileName), "Delete dialog message= ");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "'Delete' button is not displayed.");
        assertTrue(deleteDialog.isCancelButtonDisplayed(), "'Cancel' button is not displayed.");
        
        LOG.info("Step 2: Press \"Delete\"");
        deleteDialog.clickDelete();
        assertFalse(repositoryPage.isContentNameDisplayed(fileName), fileName + " is displayed.");
        getBrowser().refresh();
        assertFalse(repositoryPage.isContentNameDisplayed(fileName), fileName + " is displayed.");
        cleanupAuthenticatedSession();
    }

    @TestRail(id ="C8309")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
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
        repositoryPage.clickOnAction(folderName, "Delete Folder");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("documentLibrary.deleteDialogMessage"), folderName), "Delete dialog message= ");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "'Delete' button is not displayed.");
        assertTrue(deleteDialog.isCancelButtonDisplayed(), "'Cancel' button is not displayed.");  
        
        LOG.info("Step 2: Press \"Delete\"");
        deleteDialog.clickDelete();
        assertFalse(repositoryPage.isContentNameDisplayed(folderName), folderName + " is displayed.");
        getBrowser().refresh();
        assertFalse(repositoryPage.isContentNameDisplayed(folderName), folderName + " is displayed.");
        cleanupAuthenticatedSession();
    }
    
    @TestRail(id ="C13749")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void nonAdminUserCanNotDeleteFileOrFolderFromTheMainRepository()
    {
        //Precondition
        setupAuthenticatedSession(user, password);
        repositoryPage.navigate();
        
        LOG.info("Step 1: Mouse over file name and check that the More and the Delete Document option is not available");
        repositoryPage.mouseOverContentItem(fileNameC13749);
        getBrowser().waitInSeconds(2);
        Assert.assertFalse(repositoryPage.isMoreMenuDisplayed(fileNameC13749), "'More' menu displayed for " + fileNameC13749);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(fileNameC13749, "Delete Document"));
        
        LOG.info("Step 2: Mouse over folder name and check that the More and the  Delete Folder option is not available");
        repositoryPage.mouseOverContentItem(folderNameC13751);
        Assert.assertFalse(repositoryPage.isMoreMenuDisplayed(folderNameC13751), "'More' menu displayed for " + folderNameC13751);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(folderNameC13751, "Delete Folder"));
        cleanupAuthenticatedSession();
    }
    
    @TestRail(id ="C13751")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void adminUserCanDeleteFileOrFolderInMainRepository()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigate();
        
        LOG.info("Step 1: Mouse over file name and check that the More and the  Delete Document options are available");
        repositoryPage.mouseOverFileName(fileNameC13749);
        Assert.assertTrue(repositoryPage.isMoreMenuDisplayed(fileNameC13749), "'More' menu displayed for " + fileNameC13749);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC13749, "Delete Document"));
        
        LOG.info("Step 2: Mouse over folder name and check that the More and the  Delete Folder options are available");
        repositoryPage.mouseOverContentItem(folderNameC13751);
        Assert.assertTrue(repositoryPage.isMoreMenuDisplayed(folderNameC13751), "'More' menu displayed for " + folderNameC13751);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(folderNameC13751, "Delete Folder"));
        cleanupAuthenticatedSession();
    }
}
