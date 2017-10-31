package org.alfresco.share.sitesFeatures.blog;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.CreateBlogPostPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;

public class CreatingABlogPostTests extends ContextAwareWebTest
{
    @Autowired
    CreateBlogPostPage createBlogPost;

    @Autowired
    BlogPostViewPage blogPostView;

    @Autowired
    BlogPostListPage blogPostList;

    private String user = String.format("C5533User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C5533SiteName%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C5533SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String blogPostTitle = "Post1 Title";
    private String blogPostContentText = "first post content text";
    private String Tag = "tag1";
    private String blogPostTitleC5541 = "C5541 blog post title";
    private String tagC5541 = "c5541tag";
    private String blogPostContentC5541 = "C5541 blog post content";
    private String blogPostTitleC6119 = "c6119 blog post title";
    private String blogPostTagC6119 = "tagc6119";
    private String blogPostContentC6119 = "C6119 Blog post content";
    private String blogPostTitleC6120 = "C6120 Title";
    private String blogPostTagC6120 = "tagc6120";
    private String blogPostContentC6120 = "Blog post content C6120";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user, password, siteName, Page.BLOG, null);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C5533")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void creatingANewBlogPostFromBlogPage()
    {
        blogPostList.navigate(siteName);

        LOG.info("Step 1: Click 'New Post' button.");
        blogPostList.clickNewPostButton();
        Assert.assertEquals(createBlogPost.getPageTitle(), "Create Blog Post");

        LOG.info("Step 2: Type a Title for the post (e.g. 'Post1').");
        createBlogPost.sendTitleInput(blogPostTitle);

        LOG.info("Step 3: Type the post Content(e.g. 'first post content') in the Text box.");
        createBlogPost.sendBlogPostTextInput(blogPostContentText);

        LOG.info("Step 4: Type a Tag and click 'Add' button.");
        createBlogPost.sendTagsInput(Tag);
        createBlogPost.clickAddTagButton();
        Assert.assertEquals(createBlogPost.getTagText(Tag), Tag);

        LOG.info("Step 5: Delete the tag.");

        Assert.assertTrue(createBlogPost.isDeleteButtonAvailable(Tag));
        createBlogPost.clickDeleteTag(Tag);
        Assert.assertFalse(createBlogPost.isTagPresent(Tag));

        LOG.info("Step 6: Add a Tag and click 'Publish internally' button.");
        createBlogPost.sendTagsInput(Tag);
        createBlogPost.clickAddTagButton();
        Assert.assertEquals(createBlogPost.getTagText(Tag), Tag);
        createBlogPost.clickPublishInternally();

        LOG.info("Step 7: Navigate to 'Blog Post List'.");
        blogPostList.navigate(siteName);
        Assert.assertEquals(blogPostList.getBlogPostTitle(blogPostTitle), blogPostTitle);
    }

    @TestRail(id = "C5535")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void cancelCreatingNewBlogPost()
    {
        blogPostList.navigate(siteName);

        LOG.info("Step 1: Click 'New Post' button.");
        blogPostList.clickNewPostButton();
        Assert.assertEquals(createBlogPost.getPageTitle(), "Create Blog Post");

        LOG.info("Step 2: Type a Title for the postb and a content");
        createBlogPost.sendTitleInput(blogPostTitle);
        createBlogPost.sendBlogPostTextInput(blogPostContentText);

        LOG.info("Step 3: Type a Tag and click 'Add' button.");
        createBlogPost.sendTagsInput(Tag);
        createBlogPost.clickAddTagButton();
        Assert.assertEquals(createBlogPost.getTagText(Tag), Tag);

        LOG.info("Step 4: Click Cancel button");
        createBlogPost.clickCancelButton();

        Assert.assertEquals(blogPostList.getBlogContentText(), "No blog posts found");
    }

    @TestRail(id = "5541")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void creatingANewDraftPost()
    {
        blogPostList.navigate(siteName);

        LOG.info("Step 1: Click 'New Post' button.");
        blogPostList.clickNewPostButton();
        Assert.assertEquals(createBlogPost.getPageTitle(), "Create Blog Post");

        LOG.info("Step 2: Type a Title for the post and a content");
        createBlogPost.sendTitleInput(blogPostTitleC5541);
        createBlogPost.sendBlogPostTextInput(blogPostContentC5541);

        LOG.info("Step 3: Type a Tag and click 'Add' button.");
        createBlogPost.sendTagsInput(tagC5541);
        createBlogPost.clickAddTagButton();
        Assert.assertEquals(createBlogPost.getTagText(tagC5541), tagC5541);

        LOG.info("Step 4: Click 'Save as draft' button");
        createBlogPost.clickSaveAsDraftButton();
        String blogPostViewTitle = blogPostView.getBlogPostTitle() + " " + blogPostView.getBlogPostNote();
        Assert.assertEquals(blogPostViewTitle, "C5541 blog post title (Draft)");
        String expectedAuthorName = user + " " + user;
        Assert.assertEquals(blogPostView.getBlogPostAuthor(), expectedAuthorName);
        Assert.assertEquals(blogPostView.getBlogPostTags(), Collections.singletonList(tagC5541));
        Assert.assertEquals(blogPostView.getBlogPostContent(), blogPostContentC5541);

        LOG.info("Step 5: Navigate to 'Blog Post List'");
        blogPostList.navigate(siteName);
        Assert.assertFalse(blogPostList.isBlogPostDisplayed(blogPostTitleC5541), "Blog post is displayed on the Blog Post List View");

        LOG.info("Step 6: Click the All filter'");
        blogPostList.clickAllFilter();
        Assert.assertTrue(blogPostList.isBlogPostDisplayed(blogPostTitleC5541),
                "Blog post is not displayed on the Blog Post List View when All filter is applied");
    }

    @TestRail(id = "C6119")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void creatingANewBlogPostFromBlogPostView()
    {
        sitePagesService.createBlogPost(user, password, siteName, "Test Blog", blogPostContentText, false, null);
        blogPostList.navigate(siteName);
        blogPostList.clickOnThePostTitle("Test Blog");

        LOG.info("Step 1: Click 'New Post' button.");

        blogPostView.clickNewPostButton();
        Assert.assertEquals(createBlogPost.getPageTitle(), "Create Blog Post");

        LOG.info("Step 2: Provide data for the blog post (title, content, tag)");

        createBlogPost.sendTitleInput(blogPostTitleC6119);
        createBlogPost.sendBlogPostTextInput(blogPostContentC6119);
        createBlogPost.sendTagsInput(blogPostTagC6119);
        createBlogPost.clickAddTagButton();

        LOG.info("Step 3: Click Publish Internally button");

        createBlogPost.clickPublishInternally();
        Assert.assertEquals(blogPostView.getBlogPostTitle(), blogPostTitleC6119);

        LOG.info("Step 4 : Navigate to blog post list");
        blogPostView.clickBlogPostListButton();
        Assert.assertTrue(blogPostList.isBlogPostDisplayed(blogPostTitleC6119), "Blog Post is not displayed on the blog post list page");
    }

    @TestRail(id = "C6120")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void creatingANewDraftBlogPostFromBlogPostView()
    {
        sitePagesService.createBlogPost(user, password, siteName, "Test Blog", blogPostContentText, false, null);
        blogPostList.navigate(siteName);
        blogPostList.clickOnThePostTitle("Test Blog");

        LOG.info("Step 1: Click 'New Post' button.");
        blogPostView.clickNewPostButton();
        Assert.assertEquals(createBlogPost.getPageTitle(), "Create Blog Post");

        LOG.info("Step 2: Provide data for the Blog post (title, content, tag)");
        createBlogPost.sendTitleInput(blogPostTitleC6120);
        createBlogPost.sendBlogPostTextInput(blogPostContentC6120);
        createBlogPost.sendTagsInput(blogPostTagC6120);
        createBlogPost.clickAddTagButton();

        LOG.info("Step 3: Click Save as draft button");
        createBlogPost.clickSaveAsDraftButton();
        Assert.assertEquals(blogPostView.getBlogPostTitle(), blogPostTitleC6120);

        LOG.info("Step 4: Navigate to blog post list");
        blogPostView.clickBlogPostListButton();
        Assert.assertFalse(blogPostList.isBlogPostDisplayed(blogPostTitleC6120), "Draft blog post is displayed on the Blog Post List page.");

        LOG.info("Step 5: Click All filter");
        blogPostList.clickAllFilter();
        Assert.assertTrue(blogPostList.isBlogPostDisplayed(blogPostTitleC6120), "Draft blog post is not displayed when All filter is applied.");
    }
}
