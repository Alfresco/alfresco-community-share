package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentService;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RepositoryTests extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    CreateContent create;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    GoogleDocsCommon googleDocs;
    
    @Autowired
    ContentService contentService;
    
    @Autowired
    RepositoryPage repositoryPage;

    private String user = "C8154TestUser" + DataUtil.getUniqueIdentifier();
    private String description = "C8154SiteDescription" + DataUtil.getUniqueIdentifier();
    private String siteName = "1C8154SiteName" + DataUtil.getUniqueIdentifier();
    private String fileName1 = "C8154 file1";
    private String fileName2 = "C8154 file2";
    private String folderName = "folderNameSite1";
    private String folderName2 ="folderNameSite2";
    private String fileContent = "test content";
    private String siteName2 = "2SecondTestSite"+ DataUtil.getUniqueIdentifier();
    

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        siteService.create(user, password, domain, siteName2, description, Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileName1, fileContent);
        contentService.createDocument(user, password, siteName2, DocumentType.TEXT_PLAIN, fileName2, fileContent);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createFolder(user, password, folderName2, siteName2);
       
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id ="C8154")
    @Test
    
    public void checkTheRepositoryIsAvailableInTheToolBar()
    {
        userDashboardPage.navigate(siteName);
        
        LOG.info("Step 1: Check that the Repository link is available in the toolbar");
        Assert.assertTrue(repositoryPage.isRepositoryAvailableInToolbar(), "The repository link is not available in Toolbar");
        
        LOG.info("Step 2: Access the Repository via link in toolbar");
        repositoryPage.clickOnRepository();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not redirected to the repository page");     
    }
    
    @TestRail(id ="C8155")
    @Test
    
    public void checkTheFilesAndFoldersAvailabilityInRepository()
    {
        LOG.info("Step 1: Navigate to the Repository Page and check the default folders availability");
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        Assert.assertTrue(repositoryPage.isContentDisplayed("Data Dictionary"), "Data Dictionary is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed("Guest Home"), "Guest Home is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed("Imap Attachments"), "Imap Attachments is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed("IMAP Home"), "IMAP Home is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed("Shared"), "Shared is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed("Sites"), "Sites is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed("User Homes"), "User Homes is not displayed in Repository");
        
        LOG.info("Step 2: Click on the Sites Folder");
        repositoryPage.clickFolderFromExplorerPanel("Sites");
        Assert.assertTrue(repositoryPage.isContentDisplayed(siteName), "First created site is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed(siteName2), "Second created site is not displayed in Repository");
        
        LOG.info("Step 3: Click on your first created site.");
        repositoryPage.clickOnFolderName(siteName);
        Assert.assertTrue(repositoryPage.isContentDisplayed("documentLibrary"), "documentLibrary for the first created site is not displayed in Repository");
        
        LOG.info("Step 4: Click on documentLibrary folder.");
        repositoryPage.clickOnFolderName("documentLibrary");
        Assert.assertTrue(repositoryPage.isContentDisplayed(fileName1), "fileName1 is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed(folderName), "fileName1 is not displayed in Repository");
        
        LOG.info("Step 5: Return to Repository/Sites");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("Sites");
        
        LOG.info("Step 6: Click on the second created site.");
        repositoryPage.clickOnFolderName(siteName2);
        Assert.assertTrue(repositoryPage.isContentDisplayed("documentLibrary"), "documentLibrary for the second created site is not displayed in Repository");
        
        LOG.info("Step 7: Click on documentLibrary folder.");
        repositoryPage.clickOnFolderName("documentLibrary");
        Assert.assertTrue(repositoryPage.isContentDisplayed(fileName2), "fileName2 is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentDisplayed(folderName2), "fileName2 is not displayed in Repository");
    }
}
