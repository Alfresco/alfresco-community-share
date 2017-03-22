package org.alfresco.share.sitesFeatures.blog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.CreateBlogPostPage;
import org.alfresco.po.share.site.blog.EditBlogPostPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EditingABlogPostTests extends ContextAwareWebTest
{
    @Autowired
    BlogPostListPage blogPage;

    @Autowired
    CreateBlogPostPage createBlogPost;

    @Autowired
    BlogPostViewPage blogPostView;

    @Autowired
    EditBlogPostPage editBlogPost;

    private String user = "C5560User" + DataUtil.getUniqueIdentifier();
    private String siteName = "C5560SiteName" + DataUtil.getUniqueIdentifier();
    private String description = "C5560SiteDescription" + DataUtil.getUniqueIdentifier();
    private String blogPostContentText = "first post content text";
    private List<String> tags = Collections.singletonList("tag1");
    private String blogPostTitleC5560 = "C5560 blog post title";
    private String expectedPageTitle = "Edit Blog Post";
    private String newTitleC5560 = "Test";
    private String newBlogPostContentC5560 = "Test content";
    private String tagC5560 = "tagc5560";
    private String C5561EditedTitle = "C5561 title edited";
    private String C5561EditedContent = "C5561 content edited";
    private String tagC5561 = "c5561tag";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        List<Page> pagesToAdd = new ArrayList<Page>();
        pagesToAdd.add(Page.BLOG);
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        siteService.addPagesToSite(user, password, siteName, pagesToAdd);
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC5560, blogPostContentText, false, tags);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C5560")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void editABlogPostDirectlyFromBlogPage()
    {
        LOG.info("Step 1: Click Edit for blog post");
        blogPage.navigate(siteName);
        blogPage.renderedPage();
        blogPage.clickEditButton(blogPostTitleC5560);
        Assert.assertEquals(editBlogPost.getEditBlogPostPageTitle(), expectedPageTitle);

        LOG.info("Step 2: Type 'test' in Text: box, Title: box and add a new, unused tag, then click Update button.");

        editBlogPost.editTitle(newTitleC5560);
        editBlogPost.sendBlogPostTextInput(newBlogPostContentC5560);
        editBlogPost.sendTag(tagC5560);
        editBlogPost.clickAddTagButton();
        editBlogPost.clickUpdateButton();
        // TODO Check Pop-up is displayed: "Blog post saved".
        getBrowser().waitInSeconds(2);
        String actualTitle = blogPostView.getBlogPostTitle(newTitleC5560) + " " + blogPostView.getBlogPostNote(newTitleC5560);
        String expectedTitle = newTitleC5560 + " " + "(Updated)";
        Assert.assertEquals(actualTitle, expectedTitle);
        Assert.assertEquals(blogPostView.getBlogPostContent(newTitleC5560).trim(), newBlogPostContentC5560);
        Assert.assertEquals(blogPostView.getBlogPostTags(newTitleC5560, tagC5560), tagC5560);
    }

    @TestRail(id = "C5561")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void editBlogPostFromBlogPostView()
    {
        LOG.info("Test Setup");
        String blogPostTitleC5561 = "C5561 blog post title";
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC5561, blogPostContentText, false, tags);
        blogPage.navigate(siteName);
        blogPage.clickReadBlogPost(blogPostTitleC5561);

        LOG.info("Step 1: Click 'Edit' button.");

        getBrowser().waitUntilElementClickable(blogPostView.editButton, 30L);
        blogPostView.clickEditButton();
        getBrowser().waitUntilElementsVisible(editBlogPost.editBlogPageTitle);
        Assert.assertEquals(editBlogPost.getEditBlogPostPageTitle(), expectedPageTitle);

        LOG.info("Step 2: Update title, content, tag then click Update button.");

        editBlogPost.editTitle(C5561EditedTitle);
        editBlogPost.sendBlogPostTextInput(C5561EditedContent);
        editBlogPost.sendTag(tagC5561);
        editBlogPost.clickAddTagButton();
        editBlogPost.clickUpdateButton();
        // TODO Check Pop-up is displayed: "Blog post saved".
        getBrowser().waitUntilElementIsDisplayedWithRetry(blogPostView.blogPostTitle(C5561EditedTitle), 20);
        String actualTitle = blogPostView.getBlogPostTitle(C5561EditedTitle) + " " + blogPostView.getBlogPostNote(C5561EditedTitle);
        String expectedTitle = C5561EditedTitle + " " + "(Updated)";
        Assert.assertEquals(actualTitle, expectedTitle);
        Assert.assertEquals(blogPostView.getBlogPostContent(C5561EditedTitle).trim(), C5561EditedContent);
        Assert.assertEquals(blogPostView.getBlogPostTags(C5561EditedTitle, tagC5561), tagC5561);
    }

    @TestRail(id = "C6107")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void editABlogDraftPostDirectlyFromBlogPage()
    {
        LOG.info("Test setup");
        String blogTitle = "C6107Title";
        String blogContent = "C6107Content";
        List<String> tags = Collections.singletonList("c6107tag");
        String newTitle = "C6107 edited title";
        String newContent = "C6107 edited content";
        String newTag = "c6107editedtag";
        sitePagesService.createBlogPost(user, password, siteName, blogTitle, blogContent, true, tags);
        blogPage.navigate(siteName);

        LOG.info("Test steps");
        LOG.info("Step 1: Select the draft post and click the Edit button");
        blogPage.clickAllFilter();
        blogPage.selectBlogPostWithtitle(blogTitle);
        blogPage.clickEditButton(blogTitle);
        Assert.assertEquals(editBlogPost.getEditBlogPostPageTitle(), expectedPageTitle);

        LOG.info("Step 2: Update title, content, tag then click Update button.");
        editBlogPost.editTitle(newTitle);
        editBlogPost.sendBlogPostTextInput(newContent);
        editBlogPost.sendTag(newTag);
        editBlogPost.clickAddTagButton();
        editBlogPost.clickUpdateButton();
        // TODO Check Pop-up is displayed: "Blog post saved".
        blogPostView.renderedPage();
        getBrowser().waitUntilElementVisible(blogPostView.blogPostTitle(newTitle));
        String actualTitle = blogPostView.getBlogPostTitle(newTitle) + " " + blogPostView.getBlogPostNote(newTitle);
        String expectedTitle = newTitle + " " + "(Draft)";
        Assert.assertEquals(actualTitle, expectedTitle);
        Assert.assertEquals(blogPostView.getBlogPostContent(newTitle).trim(), newContent);
        Assert.assertEquals(blogPostView.getBlogPostTags(newTitle, newTag), newTag);
    }

    @TestRail(id = "C6108")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    private void editBlogDraftPostFromBlogPostView()
    {
        LOG.info("Test Setup");
        String blogPostTitleC6108 = "C5967 blog post title";
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC6108, blogPostContentText, true, tags);
        blogPage.navigate(siteName);
        blogPage.clickMyDraftsFilter();
        blogPage.clickReadBlogPost(blogPostTitleC6108);
        
        String newTitle = "C6108 edited title";
        String newContent = "C6108 edited content";
        String newTag = "c6108editedtag";

        LOG.info("Test Steps");
        
        LOG.info("Step 1: Navigate to Post View Page, click the Edit button for Draft blog post");
        getBrowser().waitInSeconds(2);
        blogPostView.clickEditButton();
        Assert.assertEquals(editBlogPost.getEditBlogPostPageTitle(), expectedPageTitle);

        LOG.info("Step 2: Update title, content, tag then click Update button.");
        editBlogPost.editTitle(newTitle);
        editBlogPost.sendBlogPostTextInput(newContent);
        editBlogPost.sendTag(newTag);
        editBlogPost.clickAddTagButton();
        editBlogPost.clickUpdateButton();
        // TODO Check Pop-up is displayed: "Blog post saved".
        getBrowser().waitInSeconds(2);
        String actualTitle = blogPostView.getBlogPostTitle(newTitle) + " " + blogPostView.getBlogPostNote(newTitle);
        String expectedTitle = newTitle + " " + "(Draft)";
        Assert.assertEquals(actualTitle, expectedTitle);
        Assert.assertEquals(blogPostView.getBlogPostContent(newTitle).trim(), newContent);
        Assert.assertEquals(blogPostView.getBlogPostTags(newTitle, newTag), newTag);
    }
    
    @TestRail(id ="C6110")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    
    public void editABlogDraftPostAndPublishIt()
    {
        LOG.info("Test setup");
        String blogTitle = " C6110 Title";
        String blogContent = "C6110 Content";
        List<String> tags = Collections.singletonList("c6110tag");
        String newTitle = "C6110 edited title";
        String newContent = "C6110 edited content";
        String newTag = "c6110editedtag";
        sitePagesService.createBlogPost(user, password, siteName, blogTitle, blogContent, true, tags);
        blogPage.navigate(siteName);

        LOG.info("Test steps");
        LOG.info("Step 1: Select the draft post and click the Edit button");
        blogPage.clickAllFilter();
        blogPage.selectBlogPostWithtitle(blogTitle);
        blogPage.clickEditButton(blogTitle);
        Assert.assertEquals(editBlogPost.getEditBlogPostPageTitle(), expectedPageTitle);
        
        LOG.info("Step 2:  Update title, content, tag then click Publish Internally button.");
        editBlogPost.editTitle(newTitle);
        editBlogPost.sendBlogPostTextInput(newContent);
        editBlogPost.sendTag(newTag);
        editBlogPost.clickAddTagButton();
        editBlogPost.clickPublishInternally();
        // TODO Check Pop-up is displayed: "Blog post saved".
        getBrowser().waitUntilElementVisible(blogPostView.blogPostTitle(newTitle));
        System.out.println(blogPostView.getBlogPostTitle(newTitle));
        Assert.assertEquals(blogPostView.getBlogPostTitle(newTitle), newTitle);
        Assert.assertEquals(blogPostView.getBlogPostContent(newTitle).trim(), newContent);
        Assert.assertEquals(blogPostView.getBlogPostTags(newTitle, newTag), newTag);
        
        LOG.info("Step 3: Go to Blog post list");
        blogPostView.clickBlogPostListButton();
        blogPage.clickLatestFilter();
        getBrowser().waitUntilElementContainsText(blogPage.pageTitle, "New Posts");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPage.blogPostTitle(newTitle), 10);
        Assert.assertTrue(blogPage.isBlogPostDisplayed(newTitle), "The blog post is not visible in the Latest view");
        blogPage.clickMyDraftsFilter();
        getBrowser().waitUntilElementContainsText(blogPage.pageTitle, "My Draft Posts");
        getBrowser().waitUntilElementsVisible(By.xpath("//td[@class = 'yui-dt-empty']//div[text() = 'No blog posts found']"));
        Assert.assertFalse(blogPage.isBlogPostDisplayed(newTitle), "The blog post is still displayed in My Drafts view");
        Assert.assertFalse(blogPage.isBlogPostDisplayed(blogTitle), "The original draft blog post is still displayed in My Drafts view");
    }
}
