package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RepositoryTests extends ContextAwareWebTest
{
    @Autowired private UserDashboardPage userDashboardPage;

    @Autowired
    CreateContent create;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    GoogleDocsCommon googleDocs;
    
    @Autowired private RepositoryPage repositoryPage;

    private final String user = "C8154TestUser" + DataUtil.getUniqueIdentifier();
    private final String description = "C8154SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String siteName = "1C8154SiteName" + DataUtil.getUniqueIdentifier();
    private final String fileName1 = "C8154 file1";
    private final String fileName2 = "C8154 file2";
    private final String folderName = "folderNameSite1";
    private final String folderName2 ="folderNameSite2";
    private final String fileContent = "test content";
    private final String siteName2 = "2SecondTestSite"+ DataUtil.getUniqueIdentifier();
    

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        siteService.create(user, password, domain, siteName2, description, Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileName1, fileContent);
        contentService.createDocument(user, password, siteName2, DocumentType.TEXT_PLAIN, fileName2, fileContent);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createFolder(user, password, folderName2, siteName2);
       
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id ="C8154")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
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
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void checkTheFilesAndFoldersAvailabilityInRepository()
    {
        LOG.info("Step 1: Navigate to the Repository Page and check the default folders availability");
        repositoryPage.navigate();
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("Data Dictionary"), "Data Dictionary is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("Guest Home"), "Guest Home is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("Imap Attachments"), "Imap Attachments is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("IMAP Home"), "IMAP Home is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("Shared"), "Shared is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("Sites"), "Sites is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("User Homes"), "User Homes is not displayed in Repository");
        
        LOG.info("Step 2: Click on the Sites Folder");
        repositoryPage.clickFolderFromExplorerPanel("Sites");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(siteName), "First created site is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(siteName2), "Second created site is not displayed in Repository");
        
        LOG.info("Step 3: Click on your first created site.");
        repositoryPage.clickOnFolderName(siteName);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("documentLibrary"), "documentLibrary for the first created site is not displayed in Repository");
        
        LOG.info("Step 4: Click on documentLibrary folder.");
        repositoryPage.clickOnFolderName("documentLibrary");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileName1), "fileName1 is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderName), "fileName1 is not displayed in Repository");
        
        LOG.info("Step 5: Return to Repository/Sites");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("Sites");
        
        LOG.info("Step 6: Click on the second created site.");
        repositoryPage.clickOnFolderName(siteName2);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed("documentLibrary"), "documentLibrary for the second created site is not displayed in Repository");
        
        LOG.info("Step 7: Click on documentLibrary folder.");
        repositoryPage.clickOnFolderName("documentLibrary");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileName2), "fileName2 is not displayed in Repository");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderName2), "fileName2 is not displayed in Repository");
    }
}
