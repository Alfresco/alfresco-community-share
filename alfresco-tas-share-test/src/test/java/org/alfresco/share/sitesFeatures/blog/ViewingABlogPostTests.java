package org.alfresco.share.sitesFeatures.blog;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostPage;
import org.alfresco.po.share.site.blog.EditBlogPostPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class ViewingABlogPostTests extends ContextAwareWebTest
{
    @Autowired
    SitePagesService sitePagesService;

    @Autowired
    BlogPostListPage blogPage;

    @Autowired
    BlogPostPage blogPostPage;

    @Autowired
    EditBlogPostPage editBlogPost;

    private String user1 = "C5528User1" + DataUtil.getUniqueIdentifier();
    private String user2 = "C6001User2" + DataUtil.getUniqueIdentifier();

    private String siteName = "C5528SiteName" + DataUtil.getUniqueIdentifier();
    private String description = "C5528SiteDescription" + DataUtil.getUniqueIdentifier();
    private String blogTitleUser1Published = "C5528" + "blogTitle published post User 1";
    private String blogTitleUser2Draft = "C6116" + "blogTitle draft post User 2";
    private List<String> tags = Arrays.asList("tag1");
    String blogContentDraft = "C6116 draft post User 2";
    String blogContent = "Test content for Blog Post for test C5528. This is a sample text: Alfresco Community Edition is designed to be deployed on a single server. As a result, it is shipped with a single Alfresco Community Edition Installer, which contains both the Alfresco Platform and Alfresco Share components. This is the same approach that is used in previous versions of Alfresco. Depending on your system, you can install Alfresco using one of the following methods: Using a setup wizard, which contains the required software and components you need for evaluating Alfresco; Using a standard WAR file to deploy Alfresco in a production environment";
    String sampleBlogContentDetailedView = "Test content for Blog Post for test C5528. This is a sample text: Alfresco Community Edition is designed to be deployed on a single server. As a result, it is shipped with a single Alfresco Community Edition Installer, which contains both the Alfresco Platform and Alfresco Share components. This is the same approach that is used in previous versions of Alfresco. Depending on your system, you can install Alfresco using one of the following methods: Using a setup wizard, which contains the required software a";

    @BeforeClass
    public void setupTest()
    {
        List<Page> pagesToAdd = new ArrayList<Page>();
        pagesToAdd.add(Page.BLOG);
        userService.create(adminUser, adminPassword, user1, password, user1 + "@tests.com", user1, user1);
        userService.create(adminUser, adminPassword, user2, password, user2 + "@tests.com", user2, user2);
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user2, siteName, "SiteManager");
        siteService.addPagesToSite(user1, password, siteName, pagesToAdd);
        sitePagesService.createBlogPost(user2, password, siteName, blogTitleUser2Draft, blogContentDraft, true, tags);
        setupAuthenticatedSession(user1, password);
    }

    @TestRail(id = "C5528")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void viewingABlogPostSmallAmountOfContent()
    {
        sitePagesService.createBlogPost(user1, password, siteName, blogTitleUser1Published, blogContent, false, tags);
        blogPage.navigate(siteName);

        LOG.info("Step 1: Click 'Read' beneath the Post1.");
        blogPage.clickReadBlogPost(blogTitleUser1Published);
        assertEquals(blogPostPage.getBlogPostTitle(), blogTitleUser1Published);
        assertEquals(blogPostPage.getBlogPostContent(), blogContent, "The post view displays the selected blog post entirely.");

        LOG.info("Step 2: Click 'Blog Post List'.");
        blogPostPage.clickBlogPostListButton();
        String expectedRelativePath = "share/page/site/" + siteName + "/blog-postlist";
        assertEquals(blogPage.getRelativePath(), expectedRelativePath);

        LOG.info("Step 3: Set view toggle button to 'Simple View'.");
        blogPage.clickSimpleViewButton();
        assertFalse(blogPage.isBlogPostContentDisplayed(blogTitleUser1Published), "Blog content is displayed while in Simple view mode");

        LOG.info("Step 4: Click the title of the post.");
        blogPage.clickOnThePostTitle(blogTitleUser1Published);
        assertEquals(blogPostPage.getBlogPostContent(), blogContent, "The post view displays the selected blog post in its entirety.");

        LOG.info("Step 5: Go back on Blog page and set view toggle button on 'Detailed View'.");

        blogPostPage.clickBlogPostListButton();
        assertEquals(blogPage.getBlogPostContent(blogTitleUser1Published), sampleBlogContentDetailedView,
                "A sample of the content is showing in the blog list.");

        cleanupAuthenticatedSession();
        sitePagesService.deleteBlogPost(user1, password, siteName, blogTitleUser1Published, false);
    }

    @TestRail(id = "C6116")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void visibilityOfPublishedDraft()
    {
        setupAuthenticatedSession(user1, password);
        blogPage.navigate(siteName);
        blogPage.renderedPage();

        LOG.info("Step 1: Click 'All' view.");
        blogPage.clickAllFilter();
        assertEquals(blogPage.getBlogContentText(), "No blog posts found");
        assertFalse(blogPage.isBlogPostDisplayed(blogTitleUser2Draft), "Blog post draft of user 2 is visible for user one before it was published");

        LOG.info("Step 2: Logout and login as User2. Navigate to Blog post view for Post1. Click My Drafts view.");
        userService.logout();
        setupAuthenticatedSession(user2, password);
        blogPage.navigate(siteName);
        blogPage.renderedPage();
        blogPage.clickMyDraftsFilter();

        Assert.assertTrue(blogPage.isBlogPostDisplayed(blogTitleUser2Draft), "Draft blog post of user 2 is not displayed");

        LOG.info("Step 3: Click Edit button");
        blogPage.clickEditButton(blogTitleUser2Draft);
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.xpath("//div[@class = 'page-form-header']//h1[text() = 'Edit Blog Post']")),
                "Edit Blog Post");
        assertEquals(editBlogPost.getEditBlogPostPageTitle(), "Edit Blog Post");

        LOG.info("Step 4: Click 'Publish Internally' button.");
        editBlogPost.clickPublishInternally();
        // TODO get popup text for Blog post saved and assert

        LOG.info("Step 5: Logout and login as User1. Navigate to Blog post view for Post1.");
        userService.logout();
        setupAuthenticatedSession(user1, password);
        blogPage.navigate(siteName);
        blogPage.clickAllFilter();
        assertEquals(blogPage.getBlogPostTitle(blogPage.getBlogPostTitle(blogTitleUser2Draft)), blogTitleUser2Draft);
    }
}
