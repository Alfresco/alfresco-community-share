package org.alfresco.share.sitesFeatures.blog;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.CreateBlogPostPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class DeletingABlogPostTests extends ContextAwareWebTest
{
    @Autowired
    BlogPostListPage blogPage;

    @Autowired
    CreateBlogPostPage createBlogPost;

    @Autowired
    BlogPostViewPage blogPostView;

    @Autowired
    DeleteDialog deleteDialog;

    private String user = String.format("C5955User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C5955SiteName%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C5955SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String blogPostContentText = "first post content text";
    private List<String> tags = Collections.singletonList("tag1");
    private String blogPostTitleC5955 = "C5955 blog post title";

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


    @TestRail (id = "C5955")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void deletingABlogDirectlyFromBlogPage()
    {
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC5955, blogPostContentText, false, tags);
        blogPage.navigate(siteName);
        LOG.info("Step 1: Click Delete for blog post.");
        blogPage.clickDeleteButton(blogPostTitleC5955);
        Assert.assertEquals(deleteDialog.getMessage(), "Do you really want to delete blog post 'C5955 blog post title'?");

        LOG.info("Step 2: Click Delete button on the Delete Blog Post prompt");
        deleteDialog.clickDelete();
        // TODO get popup text for Post Deleted
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.cssSelector("tbody.yui-dt-message")), "No blog posts found");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
    }

    @TestRail (id = "C5957")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void deletingADraftBlogPostDirectlyFromBlogPage()
    {
        LOG.info("Test setup");
        String blogPostContentTextC5957 = "C5957 post content text";
        String blogPostTitleC5957 = "C5957 blog post title";
        List<String> tags = Collections.singletonList("c5957tag");
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC5957, blogPostContentTextC5957, true, tags);
        blogPage.navigate(siteName);
        blogPage.clickMyDraftsFilter();

        LOG.info("Test steps");
        LOG.info("Step 1: Click Delete for draft blog post");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPage.selectBlogPostWithtitle(blogPostTitleC5957));
        blogPage.clickDeleteButton(blogPostTitleC5957);
        Assert.assertEquals(deleteDialog.getMessage(), "Do you really want to delete blog post 'C5957 blog post title'?");

        LOG.info("Step 2: Click Delete button on the Delete Blog Post prompt");
        deleteDialog.clickDelete();
        // TODO get popup text for Post Deleted
        blogPage.clickMyDraftsFilter();
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.cssSelector("tbody.yui-dt-message")), "No blog posts found");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
    }

    @TestRail (id = "C5959")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void deletingABlogPostFromBlogPostView()
    {
        LOG.info("Test Setup");
        String blogPostTitleC5959 = "C5959 blog post title";
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC5959, blogPostContentText, false, tags);

        blogPage.navigate(siteName);
        blogPage.clickOnThePostTitle(blogPostTitleC5959);
        LOG.info("Test Steps");
        LOG.info("Step 1: Click Delete for blog post");
        blogPostView.clickDeleteButton();
        Assert.assertEquals(deleteDialog.getMessage(), "Do you really want to delete blog post 'C5959 blog post title'?");

        LOG.info("Step 2: Click Delete button on the Delete Blog Post prompt");
        deleteDialog.clickDelete();
        getBrowser().waitUntilElementContainsText(blogPage.pageTitle, "New Posts");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
        // TODO get popup text for Post Deleted
        blogPage.clickAllFilter();
        getBrowser().waitUntilElementContainsText(blogPage.pageTitle, "All Posts");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
    }

    @TestRail (id = "C5967")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void deletingABlogDraftPostFromBlogPostView()
    {
        LOG.info("Test Setup");
        String blogPostTitleC5967 = "C5967 blog post title";
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC5967, blogPostContentText, true, tags);
        blogPage.navigate(siteName);
        blogPage.clickMyDraftsFilter();
        blogPage.clickReadBlogPost(blogPostTitleC5967);

        LOG.info("Test Steps");
        LOG.info("Step 1: Click Delete for blog post");
        blogPostView.clickDeleteButton();
        Assert.assertEquals(deleteDialog.getMessage(), "Do you really want to delete blog post 'C5967 blog post title'?");

        LOG.info("Step 2: Click Delete button on the Delete Blog Post prompt");
        deleteDialog.clickDelete();
        // TODO get popup text for Post Deleted
        getBrowser().waitUntilElementContainsText(blogPage.pageTitle, "New Posts");
        Assert.assertEquals(blogPage.getPageTitle(), "New Posts");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
        blogPage.clickMyDraftsFilter();
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.cssSelector("tbody.yui-dt-message")), "No blog posts found");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
    }
}
