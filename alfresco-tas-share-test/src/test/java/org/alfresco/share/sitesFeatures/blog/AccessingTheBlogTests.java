package org.alfresco.share.sitesFeatures.blog;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AccessingTheBlogTests extends ContextAwareWebTest
{
    @Autowired
    CustomizeSitePage customizeSite;

    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    BlogPostListPage blogPage;

    private String user = String.format("C3155User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C3155SiteName%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C3155SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String newBlogName = "newBlog";
    private String blogTitle = "C5527" + "blogTitle2";
    private String blogTitle1 = "C5527" + "blogTitle1 first blog";
    private String blogContent = "C5527" + "Blog content";
    private String blogContent1 = "C5527" + "Second Blog";
    private List<String> tags = Collections.singletonList("tag1");
    private List<String> noTags = new ArrayList<>();
    private String author = user + " " + user;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(adminUser, adminPassword, user, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(adminUser, adminPassword, siteName, Page.BLOG, null);
        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C5526")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed"  })

    public void renameTheBlogPage()
    {
        LOG.info("Open Site1's dashboard and click on Blog link.");
        blogPage.navigate(siteName);
        Assert.assertEquals(blogPage.getBlogContentText(), "No blog posts found");
        Assert.assertTrue(blogPage.isNewPostButtonDisplayed());
        Assert.assertTrue(blogPage.isSimpleViewButtonDisplayed());

        LOG.info("Open Customize Site page for Site1 and rename Blog page to newBlog.");
        customizeSite.navigate(siteName);
        customizeSite.renamePage(SitePageType.BLOG, newBlogName);
        customizeSite.clickOk();
        siteDashboard.navigate(siteName);
        Assert.assertEquals(blogPage.blogPageLinkName(), newBlogName);

        LOG.info("Step 3: Click on newBlog link.");
        blogPage.clickOnBlogLink();
        String expectedRelativePath = "share/page/site/" + siteName + "/blog-postlist";
        assertEquals(blogPage.getRelativePath(), expectedRelativePath, "User is redirected to blog posts page");
    }

    @TestRail (id = "C5527")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void simpleAndDetailedViewOfCreatedPosts()
    {
        /**
         * Precondition
         */
        sitePagesService.createBlogPost(user, password, siteName, blogTitle, blogContent, false, tags);
        sitePagesService.createBlogPost(user, password, siteName, blogTitle1, blogContent1, false, noTags);

        /**
         * Test Steps
         */
        LOG.info("Step 1: Open Site's dashboard and click on Blog link.");
        blogPage.navigate(siteName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPage.selectBlogPostWithtitle(blogTitle1), 6);
        Assert.assertTrue(blogPage.isBlogPostDisplayed(blogTitle1));
        Assert.assertTrue(blogPage.isBlogPostDisplayed(blogTitle));

        LOG.info("Step 2: Verify actions available for Blog posts.");

        Assert.assertTrue(blogPage.isEditButtonPresentForBlogPost(blogTitle));
        Assert.assertTrue(blogPage.isEditButtonPresentForBlogPost(blogTitle1));
        Assert.assertTrue(blogPage.isDeleteButtonPresentForBlogPost(blogTitle));
        Assert.assertTrue(blogPage.isDeleteButtonPresentForBlogPost(blogTitle1));

        LOG.info("Step 3: Verify info available for posts in 'Detailed View', selected as default.");

        Assert.assertEquals(blogPage.getBlogPostTitle(blogTitle1), blogTitle1);
        Assert.assertEquals(blogPage.getBlogPostTitle(blogTitle), blogTitle);
        Assert.assertEquals(blogPage.getBlogPostStatus(blogTitle), "Published on:");
        Assert.assertEquals(blogPage.getBlogPostStatus(blogTitle1), "Published on:");
        Assert.assertTrue(blogPage.blogDateTimeComparator(blogTitle));
        Assert.assertTrue(blogPage.blogDateTimeComparator(blogTitle1));
        Assert.assertEquals(blogPage.getBlogPostAuthor(blogTitle), author);
        Assert.assertEquals(blogPage.getBlogPostAuthor(blogTitle1), author);
        Assert.assertEquals(blogPage.getBlogPostContent(blogTitle), blogContent);
        Assert.assertEquals(blogPage.getBlogPostContent(blogTitle1), blogContent1);
        Assert.assertEquals(blogPage.getBlogPostNumberOfReplies(blogTitle), "(0)");
        Assert.assertEquals(blogPage.getBlogPostNumberOfReplies(blogTitle1), "(0)");
        Assert.assertEquals(blogPage.getBlogPostTagsWhenTagsAreAvailable(blogTitle), "tag1");
        Assert.assertEquals(blogPage.getBlogPostTagsWhenNoTagsAreAvailable(blogTitle1), "(None)");

        LOG.info("Step 4: Verify info available for posts in 'Detailed View', selected as default.");
        blogPage.clickSimpleViewButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(blogPage.getBlogPostTitle(blogTitle1), blogTitle1);
        Assert.assertEquals(blogPage.getBlogPostTitle(blogTitle), blogTitle);
        Assert.assertTrue(blogPage.blogDateTimeComparator(blogTitle));
        Assert.assertTrue(blogPage.blogDateTimeComparator(blogTitle1));
        Assert.assertEquals(blogPage.getBlogPostStatus(blogTitle), "Published on:");
        Assert.assertEquals(blogPage.getBlogPostStatus(blogTitle1), "Published on:");
        Assert.assertEquals(blogPage.getBlogPostAuthor(blogTitle), author);
        Assert.assertEquals(blogPage.getBlogPostAuthor(blogTitle1), author);

    }
}
