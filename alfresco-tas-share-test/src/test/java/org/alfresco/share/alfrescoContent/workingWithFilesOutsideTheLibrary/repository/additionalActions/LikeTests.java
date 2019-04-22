package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository.additionalActions;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LikeTests extends ContextAwareWebTest
{
    @Autowired private RepositoryPage repositoryPage;
    
    @Autowired private SocialFeatures socialFeatures;


    private final String user = String.format("C8301TestUser%s", RandomData.getRandomAlphanumeric());
    private final String fileNameC8301 = "C8301 file";
    private final String fileNameC8303 = "C8303 file";
    private final String fileContent = "test file content";
    private final String path = "User Homes/" + user;
    private final String folderNameC8302 = "C8302 Folder";
    private final String folderNameC8304 = "C8304 Folder";
   
   
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8301, fileContent);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8303, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC8302, path);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC8304, path);
        
        setupAuthenticatedSession(user, password);
    }

    @AfterClass(alwaysRun = true)

    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, path);
    }
    
    @TestRail(id ="C8301")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void likeFile()
    {
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8301), fileNameC8301 + " is not available in Repository");
        
        LOG.info("Step 1: Hover over the file Like link.");
        
        Assert.assertTrue(repositoryPage.isLikeButtonDisplayed(fileNameC8301), "Documents link is not present");
        Assert.assertEquals(socialFeatures.getLikeButtonMessage(fileNameC8301), "Like this document", "Like Button message is not correct");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileNameC8301), 0, "The number of likes is not correct");
        
        LOG.info("Step 2: Click on the Like button");
        socialFeatures.clickLikeButton(fileNameC8301);
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileNameC8301), 1, "The number of likes is not correct"); 
        Assert.assertTrue(socialFeatures.isLikeButtonEnabled(fileNameC8301), "Like button is not enabled");
                
    }
    
    @TestRail(id ="C8302")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void likeFolder()
    {
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderNameC8302), folderNameC8302 + " is not available in Repository");
        
        LOG.info("Step 1: Hover over a document and check the Like button");
        
        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertTrue(repositoryPage.isLikeButtonDisplayed(folderNameC8302), "Documents link is not present");
        Assert.assertEquals(socialFeatures.getLikeButtonMessage(folderNameC8302), "Like this folder", "Like Button message is not correct");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(folderNameC8302), 0, "The number of likes is not correct");
        
        LOG.info("Step 2: Click on the Like button");
        socialFeatures.clickLikeButton(folderNameC8302);
        Assert.assertEquals(socialFeatures.getNumberOfLikes(folderNameC8302), 1, "The number of likes is not correct");   
        Assert.assertTrue(socialFeatures.isLikeButtonEnabled(folderNameC8302), "Like button is not enabled");
    }
    
    @TestRail(id ="C8303")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void unlikeFile()
    {
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8303), fileNameC8303 + " is not available in Repository");
        
        socialFeatures.clickLikeButton(fileNameC8303);
        
        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertEquals(socialFeatures.getLikeButtonEnabledText(fileNameC8303), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileNameC8303), 1, "The number of likes is not correct");
        
        LOG.info("Step 2: Click on Unlike");
        socialFeatures.clickUnlike(fileNameC8303);
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileNameC8303), 0, "The number of likes is not correct");       
    }
    
    @TestRail(id ="C8304")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void unlikeFolder()
    {
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderNameC8304), folderNameC8304 + " is not available in Repository");
        
        socialFeatures.clickLikeButton(folderNameC8304);
        
        LOG.info("Step 1: Hover over the folder Like link.");
        Assert.assertEquals(socialFeatures.getLikeButtonEnabledText(folderNameC8304), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(folderNameC8304), 1, "The number of likes is not correct");
        
        LOG.info("Step 2: Click on Unlike");
        socialFeatures.clickUnlike(folderNameC8304);
        Assert.assertEquals(socialFeatures.getNumberOfLikes(folderNameC8304), 0, "The number of likes is not correct");       
    }
}
