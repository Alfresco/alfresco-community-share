package org.alfresco.share.sitesFeatures.blog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.BlogPromptWindow;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BlogPostEditCommentsTests extends ContextAwareWebTest
{
    @Autowired
    BlogPostListPage blogPage;

    @Autowired
    BlogPostViewPage blogPostView;

    @Autowired
    BlogPromptWindow commentWindow;

    private String user = "C6061User" + DataUtil.getUniqueIdentifier();
    private String siteName = "C6061SiteName" + DataUtil.getUniqueIdentifier();
    private String description = "C6061SiteDescription" + DataUtil.getUniqueIdentifier();
    private String blogPostContentText = "C6061 post content text";
    private List<String> tags = Collections.singletonList("tagc6011");
    private String blogPostTitleC6061 = "C6061 blog post title";
    private String comment = "C6061 comment text";
    private String commentUser = user + " " + user;
    private String editedComment = "C6061 edited comment text";

    @BeforeClass
    public void setupTest()
    {
        List<Page> pagesToAdd = new ArrayList<Page>();
        pagesToAdd.add(Page.BLOG);
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        siteService.addPagesToSite(user, password, siteName, pagesToAdd);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C6061")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void editBlogPostComment()
    {
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC6061, blogPostContentText, false, tags);
        sitePagesService.commentBlog(user, password, siteName, blogPostTitleC6061, false, comment);

        blogPage.navigate(siteName);
        blogPage.clickReadBlogPost(blogPostTitleC6061);
        blogPostView.renderedPage();

        LOG.info("Step 1: Click Edit to the right of the comment.");
        blogPostView.clickEditComment(commentUser);
        getBrowser().waitUntilElementVisible(By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']"));
        Assert.assertEquals(commentWindow.getEditCommentBoxLabel(), "Edit Comment...");

        LOG.info("Step 2: Type your comment in the Add Your Comment box.");
        commentWindow.testEditComment(editedComment);
        
        LOG.info("Step 3: Click the Save button");
        commentWindow.clickSaveButtonOnEditComment();
        getBrowser().waitUntilElementVisible(blogPostView.commentText);
        Assert.assertEquals(blogPostView.getCommentText(commentUser), editedComment); 
    }
    
    @TestRail(id= "C6062")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    
    public void editDraftBlogPostComment()
    {
        LOG.info("Test setup");
        String blogPostTitleC6062 = "C6062 Blog post title";
        String commentC6062 = "C6062 Comment";
        String C6062editedComment = "C6062 edited comment";
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC6062, blogPostContentText, true, tags);
        sitePagesService.commentBlog(user, password, siteName, blogPostTitleC6062, true, commentC6062);
        
        LOG.info("Step 1: Click edit for Draft Comment");
        blogPage.navigate(siteName);
        blogPage.clickMyDraftsFilter();
        blogPage.clickReadBlogPost(blogPostTitleC6062);
        blogPostView.clickEditComment(commentUser);
        getBrowser().waitUntilElementVisible(By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']"));
        Assert.assertEquals(commentWindow.getEditCommentBoxLabel(), "Edit Comment...");
        
        LOG.info("Step 2: Type your comment in the Add Your Comment box.");
        commentWindow.testEditComment(C6062editedComment);
      
        LOG.info("Step 3: Click the Save button");
        commentWindow.clickSaveButtonOnEditComment();
        getBrowser().waitUntilElementVisible(blogPostView.commentText);
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(blogPostView.commentText), C6062editedComment);
        Assert.assertEquals(blogPostView.getCommentText(commentUser), C6062editedComment); 
    }
        
}
