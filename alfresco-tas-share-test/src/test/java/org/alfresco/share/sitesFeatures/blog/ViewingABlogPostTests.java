package org.alfresco.share.sitesFeatures.blog;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Collections;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.EditBlogPostPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ViewingABlogPostTests extends ContextAwareWebTest
{
   // @Autowired
    BlogPostListPage blogPostListPage;

   // @Autowired
    BlogPostViewPage blogPostViewPage;

   // @Autowired
    EditBlogPostPage editBlogPost;
    
    String blogContentDraft = "C6116 draft post User 2";
    String blogContent = "Test content for Blog Post for test C5528. This is a sample text: Alfresco Community Edition is designed to be deployed on a single server. As a result, it is shipped with a single Alfresco Community Edition Installer, which contains both the Alfresco Platform and Alfresco Share components. This is the same approach that is used in previous versions of Alfresco. Depending on your system, you can install Alfresco using one of the following methods: Using a setup wizard, which contains the required software and components you need for evaluating Alfresco; Using a standard WAR file to deploy Alfresco in a production environment";
    String sampleBlogContentDetailedView = "Test content for Blog Post for test C5528. This is a sample text: Alfresco Community Edition is designed to be deployed on a single server. As a result, it is shipped with a single Alfresco Community Edition Installer, which contains both the Alfresco Platform and Alfresco Share components. This is the same approach that is used in previous versions of Alfresco. Depending on your system, you can install Alfresco using one of the following methods: Using a setup wizard, which contains the required software a";
    private String user1 = String.format("C5528User1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("C6001User2%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C5528SiteName%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C5528SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String blogTitleUser1Published = "C5528" + "blogTitle published post User 1";
    private String blogTitleUser2Draft = "C6116" + "blogTitle draft post User 2";
    private List<String> tags = Collections.singletonList("tag1");

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, user1);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, user2);
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user2, siteName, "SiteManager");
        siteService.addPageToSite(user1, password, siteName, Page.BLOG, null);
        sitePagesService.createBlogPost(user2, password, siteName, blogTitleUser2Draft, blogContentDraft, true, tags);
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


    @TestRail (id = "C5528")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, priority=2)
    public void viewingABlogPostSmallAmountOfContent()
    {
        sitePagesService.createBlogPost(user1, password, siteName, blogTitleUser1Published, blogContent, false, tags);

        blogPostListPage.navigate(siteName);

        LOG.info("Step 1: Click 'Read' beneath the Post1.");
        blogPostListPage.readPost(blogTitleUser1Published);
        assertEquals(blogPostViewPage.getBlogPostTitle(), blogTitleUser1Published);
        assertEquals(blogPostViewPage.getBlogPostContent(), blogContent, "The post view displays the selected blog post entirely.");

        LOG.info("Step 2: Click 'Blog Post List'.");
        blogPostViewPage.navigateBackToBlogList();
        String expectedRelativePath = "share/page/site/" + siteName + "/blog-postlist";
        assertEquals(blogPostListPage.getRelativePath(), expectedRelativePath);

        LOG.info("Step 3: Set view toggle button to 'Simple View'.");
        blogPostListPage.openBlogSimpleView();
        assertFalse(blogPostListPage.isBlogPostContentDisplayed(blogTitleUser1Published), "Blog content is displayed while in Simple view mode");

        LOG.info("Step 4: Click the title of the post.");
        blogPostListPage.clickOnThePostTitle(blogTitleUser1Published);
        assertEquals(blogPostViewPage.getBlogPostContent(), blogContent, "The post view displays the selected blog post in its entirety.");

        LOG.info("Step 5: Go back on Blog page and set view toggle button on 'Detailed View'.");

        blogPostViewPage.navigateBackToBlogList();
        assertEquals(blogPostListPage.getBlogPostContent(blogTitleUser1Published), sampleBlogContentDetailedView,
            "A sample of the content is showing in the blog list.");

        cleanupAuthenticatedSession();
        sitePagesService.deleteBlogPost(user1, password, siteName, blogTitleUser1Published, false);
    }

    @TestRail (id = "C6116")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, priority=1)
    public void visibilityOfPublishedDraft()
    {
        setupAuthenticatedSession(user1, password);
        blogPostListPage.navigate(siteName);

        LOG.info("Step 1: Click 'All' view.");
        blogPostListPage.navigateToAllFilter();
        assertEquals(blogPostListPage.assertBlogContentEqualsTo(""), "No blog posts found");
        assertFalse(blogPostListPage.isBlogPostDisplayed(blogTitleUser2Draft), "Blog post draft of user 2 is visible for user one before it was published");

        LOG.info("Step 2: Logout and login as User2. Navigate to Blog post view for Post1. Click My Drafts view.");
        userService.logout();
        setupAuthenticatedSession(user2, password);
        blogPostListPage.navigate(siteName);
        blogPostListPage.navigateToMyDrafts();

        Assert.assertTrue(blogPostListPage.isBlogPostDisplayed(blogTitleUser2Draft), "Draft blog post of user 2 is not displayed");

        LOG.info("Step 3: Click Edit button");
        blogPostListPage.clickEditButton(blogTitleUser2Draft);
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.xpath("//div[@class = 'page-form-header']//h1[text() = 'Edit Blog Post']")),
            "Edit Blog Post");
        assertEquals(editBlogPost.getEditBlogPostPageTitle(), "Edit Blog Post");

        LOG.info("Step 4: Click 'Publish Internally' button.");
        editBlogPost.clickPublishInternally();
        // TODO get popup text for Blog post saved and assert

        LOG.info("Step 5: Logout and login as User1. Navigate to Blog post view for Post1.");
        userService.logout();
        setupAuthenticatedSession(user1, password);
        blogPostListPage.navigate(siteName);
        blogPostListPage.navigateToAllFilter();
//        assertEquals(blogPostListPage.getBlogPostTitle(blogPostListPage.getBlogPostTitle(blogTitleUser2Draft)), blogTitleUser2Draft);
    }
}
