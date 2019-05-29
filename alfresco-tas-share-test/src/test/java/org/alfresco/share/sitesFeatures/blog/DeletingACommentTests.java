package org.alfresco.share.sitesFeatures.blog;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class DeletingACommentTests extends ContextAwareWebTest
{
    @Autowired
    BlogPostListPage blogPage;

    @Autowired
    BlogPostViewPage blogPostView;

    @Autowired
    DeleteDialog deleteDialog;

    private String user = String.format("C6063User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C6063SiteName%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C6063SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String blogPostContentText = "C6063 post content text";
    private List<String> tags = Collections.singletonList("tagcC6063");
    private String blogPostTitleC6063 = "C6063 blog post title";
    private String comment = "C6063 comment text";
    private String commentUser = user + " " + user;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user, password, siteName, Page.BLOG, null);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C6063")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void deletingACommentOfABlogPost()
    {
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC6063, blogPostContentText, false, tags);
        sitePagesService.commentBlog(user, password, siteName, blogPostTitleC6063, false, comment);
        blogPage.navigate(siteName);
        blogPage.clickReadBlogPost(blogPostTitleC6063);

        LOG.info("Step 1: Click Delete Comment to the right of the comment.");
        blogPostView.clickDeleteComment(commentUser);
        Assert.assertEquals(deleteDialog.getMessage(), "Are you sure you want to delete this comment?");

        LOG.info("Step 2: Click Delete button");
        deleteDialog.clickDelete();
        getBrowser().waitUntilElementContainsText(blogPostView.noCommentsText, "No comments");
        Assert.assertEquals(blogPostView.getNoCommentsText(), "No comments");
    }

    @TestRail (id = "C6064")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

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
        Assert.assertEquals(deleteDialog.getMessage(), "Are you sure you want to delete this comment?");

        LOG.info("Step 2: Click Delete button");
        deleteDialog.clickDelete();
        getBrowser().waitUntilElementContainsText(blogPostView.noCommentsText, "No comments");
        Assert.assertEquals(blogPostView.getNoCommentsText(), "No comments");
    }
}
