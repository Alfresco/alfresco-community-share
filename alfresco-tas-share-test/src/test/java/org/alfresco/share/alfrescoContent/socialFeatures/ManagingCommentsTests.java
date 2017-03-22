package org.alfresco.share.alfrescoContent.socialFeatures;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ManagingCommentsTests extends ContextAwareWebTest
{
    @Autowired private DocumentDetailsPage documentPreviewPage;

    @Autowired private DocumentLibraryPage documentLibraryPage;
    
    private final String user = "C9934User" + DataUtil.getUniqueIdentifier();
    private final String siteName = "C9934SiteName" + DataUtil.getUniqueIdentifier();
    private final String description = "C9934SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String docName = "File-C9934";
    private final String comment = "Test comment for C9934-1";
    private final String editedComment = "Test comment edited for C9934";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, "content");
        setupAuthenticatedSession(user, password);
    }
    
    @TestRail(id = "C9934")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void commentsDeleteComment()
    {
        LOG.info("Precondition: Add a comment to the file created in the site.");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(docName);
        documentPreviewPage.clickOnCommentDocument();
        documentPreviewPage.addComment(comment);
        Assert.assertEquals(documentPreviewPage.getCommentContent(comment), comment);

        LOG.info("Step 1: Hover mouse over the comment and check that the Delete button is displayed");
        Assert.assertTrue(documentPreviewPage.isDeleteButtonDisplayedForComment(comment), "Delete button is not displayed for comment");
        
        LOG.info("Step 2: Click Delete and check that the Delete Prompt is displayed");
        documentPreviewPage.clickDeleteComment(comment);
        Assert.assertTrue(documentPreviewPage.isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");
        
        LOG.info("Step 3: Click Delete button on the Delete Comment prompt");
        documentPreviewPage.clickDeleteOnDeleteComment();
        getBrowser().waitUntilElementVisible(documentPreviewPage.noComments);
        Assert.assertEquals(documentPreviewPage.getNoCommentsText(), "No comments", "No comments notification is not displayed");     
    }   
    
    @TestRail(id= "C9935")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void commentsEditComment()
    {
        LOG.info("Precondition: Add a comment to the file created in the site.");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(docName);
        documentPreviewPage.clickOnCommentDocument();
        documentPreviewPage.addComment(comment);
        Assert.assertEquals(documentPreviewPage.getCommentContent(comment), comment);

        LOG.info("Step 1: Hover mouse over the comment and check that the Edit button is displayed");
        Assert.assertTrue(documentPreviewPage.isEditButtonDisplayedForComment(comment), "Edit button is not displayed for comment");
        
        LOG.info("Step 2: Click the Edit button");
        documentPreviewPage.clickOnEditComment(comment);
        Assert.assertTrue(documentPreviewPage.isEditCommentDisplayed(), "Edit comment is not displayed");
        
        LOG.info("Step 3: Edit comment text and click on Save");
        documentPreviewPage.editComment(editedComment);
        documentPreviewPage.clickOnSaveButtonEditComment();
        getBrowser().waitInSeconds(3);
        Assert.assertEquals(documentPreviewPage.getCommentContent(editedComment), editedComment, "Edited comment text is not correct");
    }
}
