package org.alfresco.share.sitesFeatures.blog;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.BlogPromptWindow;
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

public class BlogPostAddCommentTests extends ContextAwareWebTest
{
    @Autowired
    BlogPostListPage blogPage;

    @Autowired
    BlogPostViewPage blogPostView;

    @Autowired
    BlogPromptWindow commentWindow;

    private String user = String.format("C6011User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C6011SiteName%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C6011SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String blogPostContentText = "C6011 post content text";
    private List<String> tags = Collections.singletonList("tagc6011");
    private String blogPostTitleC6011 = "C6011 blog post title";
    private String comment = "C6011 comment text";
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

    @TestRail (id = "C6011")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void addingACommentToABlogPost()
    {
        LOG.info("Preconditions: ");
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC6011, blogPostContentText, false, tags);
        blogPage.navigate(siteName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPage.selectBlogPostWithtitle(blogPostTitleC6011));
        blogPage.clickReadBlogPost(blogPostTitleC6011);

        LOG.info("Step 1: Click Add comment button");
        blogPostView.clickAddCommentButton();
        Assert.assertEquals(commentWindow.getAddCommentLable(), "Add Your Comment...");

        LOG.info("Step 2: Type your comment in the Add Your Comment box.");
        commentWindow.writeComment(comment);

        LOG.info("Step 3: Click the Add Comment button");
        commentWindow.clickAddCommentButton();
        getBrowser().waitUntilElementVisible(blogPostView.commentText);

        LOG.info("Step 4: Click Blog Post List button");

        blogPostView.clickBlogPostListButton();
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.xpath("//div[@class='nodeContent']//a[text()='" + blogPostTitleC6011 + "']"), 6);
        //getBrowser().waitUntilWebElementIsDisplayedWithRetry(blogPostListPage.selectBlogPostWithtitle(blogPostTitleC6011));
        Assert.assertEquals(blogPage.getBlogPostNumberOfReplies(blogPostTitleC6011), "(1)", "Blog Post" + blogPostTitleC6011 + "is not displayed");
    }

    @TestRail (id = "C6035")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })

    public void addCommentToDraftBlogPost()
    {
        String blogPostTitleC6035 = "C6035 blog post title";
        sitePagesService.createBlogPost(user, password, siteName, blogPostTitleC6035, blogPostContentText, true, tags);

        blogPage.navigate(siteName);
        blogPage.clickMyDraftsFilter();
        blogPage.clickReadBlogPost(blogPostTitleC6035);

        LOG.info("Step 1: Click Add Comment");
        blogPostView.clickAddCommentButton();
        Assert.assertEquals(commentWindow.getAddCommentLable(), "Add Your Comment...");

        LOG.info("Step 2: Type your comment in the Add Your Comment box.");
        commentWindow.writeComment(comment);

        LOG.info("Step 3: Click the Add Comment button");
        commentWindow.clickAddCommentButton();
        getBrowser().waitUntilElementVisible(blogPostView.commentText);

        LOG.info("Step 4: Click Blog Post List button and My Drafts");
        blogPostView.clickBlogPostListButton();
        blogPage.clickMyDraftsFilter();
        Assert.assertEquals(blogPage.getBlogPostNumberOfReplies(blogPostTitleC6035), "(1)");
        blogPage.clickAllFilter();
        Assert.assertEquals(blogPage.getBlogPostNumberOfReplies(blogPostTitleC6035), "(1)");
    }
}
