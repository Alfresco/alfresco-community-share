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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DeletingACommentTests extends ContextAwareWebTest
{
    @Autowired
    BlogPostListPage blogPage;

    @Autowired
    BlogPostViewPage blogPostView;
    
    @Autowired
    BlogPromptWindow deleteCommentWindow;

    private String user = "C6063User" + DataUtil.getUniqueIdentifier();
    private String siteName = "C6063SiteName" + DataUtil.getUniqueIdentifier();
    private String description = "C6063SiteDescription" + DataUtil.getUniqueIdentifier();
    private String blogPostContentText = "C6063 post content text";
    private List<String> tags = Collections.singletonList("tagcC6063");
    private String blogPostTitleC6063 = "C6063 blog post title";
    private String comment = "C6063 comment text";
    private String commentUser = user+" "+user;
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        List<Page> pagesToAdd = new ArrayList<Page>();
        pagesToAdd.add(Page.BLOG);
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        siteService.addPagesToSite(user, password, siteName, pagesToAdd);
        setupAuthenticatedSession(user, password);
    }
    
    @TestRail(id = "C6063")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    
    public void deletingACommentOfABlogPost()
    {
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC6063, blogPostContentText, false, tags);
        sitePagesService.commentBlog(user, password, siteName, blogPostTitleC6063, false, comment);
        blogPage.navigate(siteName);
        blogPage.clickReadBlogPost(blogPostTitleC6063);
        
        LOG.info("Step 1: Click Delete Comment to the right of the comment.");
        blogPostView.clickDeleteComment(commentUser);
        Assert.assertEquals(deleteCommentWindow.getTextDisplayedOnThePromptWindow(), "Are you sure you want to delete this comment?");
        
        LOG.info("Step 2: Click Delete button");
        deleteCommentWindow.clickDeleteButton();
        getBrowser().waitUntilElementContainsText(blogPostView.noCommentsText, "No comments");
        Assert.assertEquals(blogPostView.getNoCommentsText(), "No comments");
    }
    
    @TestRail(id = "C6064")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    
    public void deletingACommentOfADraftPost()
    {
        String blogPostTitleC6064 = "C6064 Blog post title";
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC6064, blogPostContentText, true, tags);
        sitePagesService.commentBlog(user, password, siteName, blogPostTitleC6064, true, comment);
        
        blogPage.navigate(siteName);
        blogPage.clickMyDraftsFilter();
        blogPage.clickReadBlogPost(blogPostTitleC6064);
        
        LOG.info("Step 1: Click Delete Comment to the right of the comment.");
        blogPostView.clickDeleteComment(commentUser);
        Assert.assertEquals(deleteCommentWindow.getTextDisplayedOnThePromptWindow(), "Are you sure you want to delete this comment?");
        
        LOG.info("Step 2: Click Delete button");
        deleteCommentWindow.clickDeleteButton();
        getBrowser().waitUntilElementContainsText(blogPostView.noCommentsText, "No comments");
        Assert.assertEquals(blogPostView.getNoCommentsText(), "No comments");
    }
}
