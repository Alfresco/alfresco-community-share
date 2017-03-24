package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.testng.Assert.assertTrue;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ActionsSelectTests extends ContextAwareWebTest
{
    @Autowired private RepositoryPage repositoryPage;
    
    @Autowired private HeaderMenuBar menuBar;

    private final String user = "8163TestUser" + DataUtil.getUniqueIdentifier();
    private final String fileName = "C8163 file";
    private final String fileContent ="8163 content";
    private final String path = "User Homes/"+ user;
    private final String folderName ="C8164 Folder";
    
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        setupAuthenticatedSession(user, password);            
    }

    @TestRail(id = "8163")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void selectFile()
    {
        //Precondition
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        
        LOG.info("Step 1: Click on the Select button and select Documents option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.documents"));
        //getBrowser().waitUntilElementVisible(getBrowser().findElement(By.name("fileChecked")));
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(repositoryPage.isContentSelected(fileName), fileName + " is not selected.");  
        
        LOG.info("Step 2: Click on the Select button and select Invert Selection.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.invertSelection"));
        Assert.assertFalse(repositoryPage.isContentSelected(fileName), fileName + " is selected.");
        
        LOG.info("Step 3: Click on the Select button and select All option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
        Assert.assertTrue(repositoryPage.isContentSelected(fileName), fileName + " is not selected."); 
        Assert.assertTrue(repositoryPage.isContentSelected(folderName), folderName + " is not selected.");  
        
        LOG.info("Step 4: Click on the Select button and select None option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.none"));
        Assert.assertFalse(repositoryPage.isContentSelected(fileName), fileName + " is selected.");
        Assert.assertFalse(repositoryPage.isContentSelected(folderName), folderName + " is selected.");
        
        LOG.info("Step 5: Click on the Select button and select Folders option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.folders"));
        Assert.assertFalse(repositoryPage.isContentSelected(fileName), fileName + " is selected.");
        Assert.assertTrue(repositoryPage.isContentSelected(folderName), folderName + " is not selected.");  
        
        LOG.info("Step 6: Click on the checkbox next to the testFile.");
        repositoryPage.clickCheckBox(fileName);
        assertTrue(repositoryPage.isContentSelected(fileName), fileName + " is selected.");
        
    }
    
    @TestRail(id ="C8164")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void selectFolder()
    {
        //Precondition
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        getBrowser().waitInSeconds(2);
        LOG.info("Step 1: On My Files page click Select and select Folders option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption("Folders");
        getBrowser().waitUntilElementVisible(getBrowser().findElement(By.name("fileChecked")));
        Assert.assertTrue(repositoryPage.isContentSelected(folderName), folderName + " is not selected.");  
        
        LOG.info("Step 2: Click Select and choose Invert Selection option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.invertSelection"));
        Assert.assertFalse(repositoryPage.isContentSelected(folderName), folderName + " is selected.");
        
        LOG.info("Step 3: Click Select and choose All option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
        Assert.assertTrue(repositoryPage.isContentSelected(fileName), fileName + " is not selected."); 
        Assert.assertTrue(repositoryPage.isContentSelected(folderName), folderName + " is not selected.");  
        
        LOG.info("Step 4: Click Select and choose None option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.none"));
        Assert.assertFalse(repositoryPage.isContentSelected(fileName), fileName + " is selected.");
        Assert.assertFalse(repositoryPage.isContentSelected(folderName), folderName + " is selected.");
        
        LOG.info("Step 5: Click Select and choose Documents option.");
        menuBar.clickSelectMenu();
        menuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.documents"));
        Assert.assertTrue(repositoryPage.isContentSelected(fileName), fileName + " is not selected.");
        Assert.assertFalse(repositoryPage.isContentSelected(folderName), folderName + " is selected.");
        
        LOG.info("Step 6: Click on the checkbox next to the TestFolder.");
        repositoryPage.clickCheckBox(folderName);
        assertTrue(repositoryPage.isContentSelected(folderName), folderName + " is selected.");
        
    }
}