package org.alfresco.share.sitesFeatures.blog;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BrowsingBlogPostsTests extends ContextAwareWebTest
{
    //@Autowired
    BlogPostListPage blogPage;

    private String user1 = String.format("C6001User1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("C6001User2%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C6001SiteName%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C6001SiteDescription-%s", RandomData.getRandomAlphanumeric());
    private String blogTitleUser1Published = "C6001-" + "blogTitle published post User 1";
    private String blogTitleUser1Draft = "C6001-" + "blogTitle draft User 1";
    private String blogTitleUser2Published = "C6001-" + "User2 Blog title Published";
    private String blogTitleUser2Draft = "C6001-" + "User2 Blog title draft";
    private String blogContent = "C6001-" + "Blog content";
    private String blogContent1 = "C6001-" + "Second Blog";
    private List<String> tags = Collections.singletonList("tag1");
    private List<String> tagsSecondPost = Collections.singletonList("tag2");
    private String author1 = user1 + " " + user1;
    private String author2 = user2 + " " + user2;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, user1);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, user2);
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user2, siteName, "SiteManager");
        siteService.addPageToSite(user1, password, siteName, Page.BLOG, null);
        sitePagesService.createBlogPost(user1, password, siteName, blogTitleUser1Published, blogContent, false, tags);
        sitePagesService.createBlogPost(user1, password, siteName, blogTitleUser1Draft, blogContent1, true, tags);
        sitePagesService.createBlogPost(user2, password, siteName, blogTitleUser2Published, blogContent, false, tags);
        sitePagesService.createBlogPost(user2, password, siteName, blogTitleUser2Draft, blogContent1, true, tags);
        sitePagesService.createBlogPost(user1, password, siteName, blogTitleUser1Published + "C6008", blogContent + "C6008", false, tags);
        sitePagesService.createBlogPost(user1, password, siteName, blogTitleUser1Published + "C6008SecondTag", blogContent + "C6008SecondTag", false,
            tagsSecondPost);
        setupAuthenticatedSession(user1, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6001")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseTheBlogPosts()
    {
        // Precondition
        // Post created eight days ago is added to the Blog by User1(e.g.: Post3). -> can not create post any number of days in the past or the future
        blogPage.navigate(siteName);

        LOG.info("Step 1: Click the 'All' view.");
        blogPage.clickAllFilter();

        LOG.info("Step 2: Check that all published blog posts are displayed");
        assertEquals(blogPage.getPageTitle(), "All Posts");
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published), blogTitleUser1Published);
        assertEquals(blogPage.getBlogPostAuthor(blogTitleUser1Published), author1);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Draft), blogTitleUser1Draft + " " + "(Draft)");
        assertEquals(blogPage.getBlogPostAuthor(blogTitleUser1Draft), author1);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser2Published), blogTitleUser2Published);
        assertEquals(blogPage.getBlogPostAuthor(blogTitleUser2Published), author2);
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Draft), "Draft blog post of user 2 is displayed");
    }

    @TestRail (id = "C6004")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseTheBlogPostsLatestPosts()
    {
        blogPage.navigate(siteName);
        blogPage.clickAllFilter();

        LOG.info("Step 1: Click on Latest filter");
        blogPage.clickLatestFilter();

        LOG.info("Step 2: Check that only published blog posts are displayed. Drafts should not be displayed");
        assertEquals(blogPage.getPageTitle(), "New Posts");
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published), blogTitleUser1Published);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser2Published), blogTitleUser2Published);
        assertEquals(blogPage.getBlogPostAuthor(blogTitleUser1Published), author1);
        assertEquals(blogPage.getBlogPostAuthor(blogTitleUser2Published), author2);
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Draft), "blog post is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser1Draft), "blog post is displayed");
    }

    @TestRail (id = "C6005")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseTheBlogPostsMyDrafts()
    {
        blogPage.navigate(siteName);

        LOG.info("Step 1: Click on My Drafts filter");
        blogPage.clickMyDraftsFilter();

        LOG.info("Step 2: Check that only the draft blog posts of user1 are displayed");
        assertEquals(blogPage.getPageTitle(), "My Draft Posts");
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Draft), blogTitleUser1Draft + " " + "(Draft)");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser1Published), "Blog post published by User 1 is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Published), "Blog post published by User 2 is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Draft), "Draft blog post User 2 is displayed");
    }

    @TestRail (id = "C6006")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseTheBlogPostsMyPublishedPosts()
    {
        blogPage.navigate(siteName);

        LOG.info("Step 1: Click on My Published Filter");
        blogPage.clickMyPublishedFilter();

        LOG.info("Step 2 : Check that only blog posts published by User1 are displayed");
        assertEquals(blogPage.getPageTitle(), "My Published Posts");
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published), blogTitleUser1Published);
        assertEquals(blogPage.getBlogPostAuthor(blogTitleUser1Published), author1);
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Published), "Blog post published by User 2 is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Draft), "Draft blog post User 2 is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser1Draft), "Draft blog post of User 1 is displayed");
    }


    @TestRail (id = "C6008")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseTheBlogPostsByTags()
    {
        blogPage.navigate(siteName);

        LOG.info("Step 1: Click tag1 tag in Tags area.");
        blogPage.clickTag("tag1");
        getBrowser().waitUntilElementVisible(blogPage.blogPostTitle(blogTitleUser1Published));
        assertEquals(blogPage.getPageTitle(), "Blog Post List");

        LOG.info("Step 2: Check that only posts with tag1 are displayed");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPage.selectBlogPostWithtitle(blogTitleUser1Published), 6);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPage.selectBlogPostWithtitle(blogTitleUser2Published), 6);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPage.selectBlogPostWithtitle(blogTitleUser1Draft), 6);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPage.selectBlogPostWithtitle(blogTitleUser1Published + "C6008"), 6);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published), blogTitleUser1Published);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser2Published), blogTitleUser2Published);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Draft), blogTitleUser1Draft + " " + "(Draft)");
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published + "C6008"), blogTitleUser1Published + "C6008");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser1Published + "C6008SecondTag"), "Blog post with tag2 is displayed");

        LOG.info("Step 3: Click tag2 tag in Tags area.");
        blogPage.clickTag("tag2");

        LOG.info("Step 4: Check that only posts with tag2 are displayed");
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published + "C6008SecondTag"), blogTitleUser1Published + "C6008SecondTag");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser1Published), "Blog post published by user 1 with tag1 is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Published), "Blog post published by user 2 with tag1 is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser1Draft), "User 1 draft with tag1 is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser1Published + "C6008"), "Blog post published by user 1 with tag1 is displayed");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Draft), "Draft blog post of User 2 is displayed");
    }

    @TestRail (id = "C6010")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseTheBlogPostsArchive()
    {
        // Can not create blog posts in the past, can not check for posts from previous month
        String currentMonth = new SimpleDateFormat("MMMM yyyy").format(Calendar.getInstance().getTime());
        System.out.println(currentMonth);

        blogPage.navigate(siteName);
        blogPage.clickArchiveMonth(currentMonth);
        assertEquals(blogPage.getPageTitle(), "Posts for Month " + currentMonth);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published + "C6008SecondTag"), blogTitleUser1Published + "C6008SecondTag");
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Draft), blogTitleUser1Draft + " " + "(Draft)");
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published), blogTitleUser1Published);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser2Published), blogTitleUser2Published);
        assertEquals(blogPage.getBlogPostTitle(blogTitleUser1Published + "C6008"), blogTitleUser1Published + "C6008");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Draft), "Draft blog post of User 2 is displayed");
    }
}