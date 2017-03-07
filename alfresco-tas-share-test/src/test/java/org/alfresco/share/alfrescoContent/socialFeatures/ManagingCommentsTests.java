package org.alfresco.share.alfrescoContent.socialFeatures;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.PreviewFileActionsSection;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

public class ManagingCommentsTests extends ContextAwareWebTest
{
    
    @Autowired
    SitePagesService sitePagesService;

    @Autowired
    UserService userService;

    @Autowired
    DocumentDetailsPage documentPreviewPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    PreviewFileActionsSection fileActions;
    
    private String user = "C9934User" + DataUtil.getUniqueIdentifier();
    private String siteName = "C9934SiteName" + DataUtil.getUniqueIdentifier();
    private String description = "C9934SiteDescription" + DataUtil.getUniqueIdentifier();
    private String testDataFolder = srcRoot + "testdata" + File.separator;
    private String docName = "File-C9934";
    private String comment = "Test comment for C9934";
    private String editedComment = "Test comment edited for C9934";

    @BeforeClass
    public void setupTest()
    {

        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName + ".docx");
    }
    
    @TestRail(id = "C9934")
    @Test
    public void commentsDeleteComment()
    {
        // Precondition
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(docName);
        documentPreviewPage.clickOnCommentDocument();
        documentPreviewPage.addComment(comment);
        Assert.assertEquals(documentPreviewPage.getCommentContent(comment), comment);
        
        // Test Steps
        
        LOG.info("Step 1: Hoover mouse over the comment and check that the Delete button is displayed");
        Assert.assertTrue(documentPreviewPage.isDeleteButtonDisplayedForComment(comment), "Delete button is not displayed for comment");
        
        LOG.info("Step 2: Click Delete and check that the Delete Prompt is displayed");
        
        documentPreviewPage.clickDeleteComment(comment);
        Assert.assertTrue(documentPreviewPage.isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");
        
        LOG.info("Step 3: Click Delete button on the Delete Comment prompt");
        documentPreviewPage.clickDeleteOnDeleteComment();
        browser.waitUntilElementVisible(documentPreviewPage.noComments);
        Assert.assertEquals(documentPreviewPage.getNoCommentsText(), "No comments", "No comments notification is not displayed");     
    }   
    
    @TestRail(id= "C9935")
    @Test
    
    public void commentsEditComment()
    {
        // Precondition
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(docName);
        documentPreviewPage.clickOnCommentDocument();
        documentPreviewPage.addComment(comment);
        Assert.assertEquals(documentPreviewPage.getCommentContent(comment), comment);
        
        // Test Steps
        
        LOG.info("Step 1: Hoover mouse over the comment and check that the Edit button is displayed");
        Assert.assertTrue(documentPreviewPage.isEditButtonDisplayedForComment(comment), "Edit button is not displayed for comment");
        
        LOG.info("Step 2: Click the Edit button");
        documentPreviewPage.clickOnEditComment(comment);
        Assert.assertTrue(documentPreviewPage.isEditCommentDisplayed(), "Edit comment is not displayed");
        
        LOG.info("Step 3: Edit comment text and click on Save");
        documentPreviewPage.editComment(editedComment);
        documentPreviewPage.clickOnSaveButtonEditComment();
        browser.waitInSeconds(1);
        Assert.assertEquals(documentPreviewPage.getCommentContent(editedComment), editedComment, "Edited comment text is not correct");
    }
}
