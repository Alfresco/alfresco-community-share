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
import org.alfresco.po.share.site.blog.CreateBlogPostPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DeletingABlogPostTests extends ContextAwareWebTest
{
    @Autowired
    BlogPostListPage blogPage;

    @Autowired
    CreateBlogPostPage createBlogPost;

    @Autowired
    BlogPostViewPage blogPostView;

    @Autowired
    BlogPromptWindow promptWindows;

    private String user = "C5955User" + DataUtil.getUniqueIdentifier();
    private String siteName = "C5955SiteName" + DataUtil.getUniqueIdentifier();
    private String description = "C5955SiteDescription" + DataUtil.getUniqueIdentifier();
    private String blogPostContentText = "first post content text";
    private List<String> tags = Collections.singletonList("tag1");
    private String blogPostTitleC5955 = "C5955 blog post title";

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
    
    @TestRail(id = "C5955")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void deletingABlogDirectlyFromBlogPage()
    {
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC5955, blogPostContentText, false, tags);
        blogPage.navigate(siteName);
        LOG.info("Step 1: Click Delete for blog post.");
        blogPage.clickDeleteButton(blogPostTitleC5955);
        Assert.assertEquals(promptWindows.getTextDisplayedOnThePromptWindow(), "Do you really want to delete blog post 'C5955 blog post title'?");

        LOG.info("Step 2: Click Delete button on the Delete Blog Post prompt");
        promptWindows.clickDeleteButtonOnDeleteBlogPost();
        // TODO get popup text for Post Deleted
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.cssSelector("tbody.yui-dt-message")), "No blog posts found");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
    }

    @TestRail(id = "C5957")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

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
        Assert.assertEquals(promptWindows.getTextDisplayedOnThePromptWindow(), "Do you really want to delete blog post 'C5957 blog post title'?");

        LOG.info("Step 2: Click Delete button on the Delete Blog Post prompt");
        promptWindows.clickDeleteButtonOnDeleteBlogPost();
        // TODO get popup text for Post Deleted
        blogPage.clickMyDraftsFilter();
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.cssSelector("tbody.yui-dt-message")), "No blog posts found");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
    }

    @TestRail(id = "C5959")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void deletingABlogPostFromBlogPostView()
    {
        LOG.info("Test Setup");
        String blogPostTitleC5959 = "C5959 blog post title";
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC5959, blogPostContentText, false, tags);
        
        blogPage.navigate(siteName);
        blogPage.clickOnThePostTitle(blogPostTitleC5959);
        getBrowser().waitUntilElementVisible(blogPostView.blogPostTitle(blogPostTitleC5959));
        LOG.info("Test Steps");
        LOG.info("Step 1: Click Delete for blog post");
        blogPostView.clickDeleteButton(blogPostTitleC5959);
        Assert.assertEquals(promptWindows.getTextDisplayedOnThePromptWindow(), "Do you really want to delete blog post 'C5959 blog post title'?");

        LOG.info("Step 2: Click Delete button on the Delete Blog Post prompt");
        promptWindows.clickDeleteButtonOnDeleteBlogPost();
        getBrowser().waitUntilElementContainsText(blogPage.pageTitle, "New Posts");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
        // TODO get popup text for Post Deleted
        blogPage.clickAllFilter();
        getBrowser().waitUntilElementContainsText(blogPage.pageTitle, "All Posts");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
    }
    
    @TestRail(id ="C5967")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    
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
        blogPostView.clickDeleteButton(blogPostTitleC5967);
        Assert.assertEquals(promptWindows.getTextDisplayedOnThePromptWindow(), "Do you really want to delete blog post 'C5967 blog post title'?");
        
        LOG.info("Step 2: Click Delete button on the Delete Blog Post prompt");
        promptWindows.clickDeleteButtonOnDeleteBlogPost();
        // TODO get popup text for Post Deleted
        getBrowser().waitUntilElementContainsText(blogPage.pageTitle, "New Posts");
        Assert.assertEquals(blogPage.getPageTitle(), "New Posts");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
        blogPage.clickMyDraftsFilter();
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.cssSelector("tbody.yui-dt-message")), "No blog posts found");
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
    }
}
