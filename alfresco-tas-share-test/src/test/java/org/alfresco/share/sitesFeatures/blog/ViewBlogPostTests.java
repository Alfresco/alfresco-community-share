package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.MY_DRAFTS_POSTS;
import static org.alfresco.utility.constants.UserRole.SiteManager;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.EditBlogPostPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ViewBlogPostTests extends BaseTest
{
    private final String postTitle = "Post Title ".concat(randomAlphanumeric(5));
    private final String postContent = "Post Content ".concat(randomAlphanumeric(5));

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    @Autowired
    private SiteService siteService;

    @Autowired
    private SitePagesService sitePagesService;

    private BlogPostListPage blogPostListPage;
    private BlogPostViewPage blogPostViewPage;
    private EditBlogPostPage editBlogPostPage;

    private final String blogContent = "Test content for Blog Post for test C5528. This is a sample text: Alfresco Community Edition is designed to be deployed on a single server. As a result, it is shipped with a single Alfresco Community Edition Installer, which contains both the Alfresco Platform and Alfresco Share components. This is the same approach that is used in previous versions of Alfresco. Depending on your system, you can install Alfresco using one of the following methods: Using a setup wizard, which contains the required software and components you need for evaluating Alfresco; Using a standard WAR file to deploy Alfresco in a production environment";
    private final String contentPreview = "Test content for Blog Post for test C5528. This is a sample text: Alfresco Community Edition is designed to be deployed on a single server. As a result, it is shipped with a single Alfresco Community Edition Installer, which contains both the Alfresco Platform and Alfresco Share components. This is the same approach that is used in previous versions of Alfresco. Depending on your system, you can install Alfresco using one of the following methods: Using a setup wizard, which contains the required software a";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(getDataUser().usingAdmin().createRandomTestUser());
        siteModel.set(getDataSite().usingUser(userModel.get()).createPublicRandomSite());
        siteService.addPageToSite(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), Page.BLOG, null);

        authenticateUsingCookies(userModel.get());

        blogPostViewPage = new BlogPostViewPage(webDriver);
        blogPostListPage = new BlogPostListPage(webDriver);
        editBlogPostPage = new EditBlogPostPage(webDriver);
    }

    @TestRail(id = "C5528")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayPartiallyPostContentWhenLargeContent()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(manager, siteModel.get(), SiteManager);

        sitePagesService.createBlogPost(manager.getUsername(), manager.getPassword(),
            siteModel.get().getId(), postTitle, blogContent, false, null);

        authenticateUsingCookies(manager);

        blogPostListPage
            .navigate(siteModel.get())
            .readPost(postTitle);

        blogPostViewPage
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogPostContentEqualsTo(blogContent);

        blogPostViewPage
            .navigateBackToBlogList();

        blogPostListPage
            .openBlogSimpleView()
            .navigateToBlogPostViewPage(postTitle);

        blogPostViewPage
            .assertBlogPostContentEqualsTo(blogContent);

        blogPostViewPage
            .navigateBackToBlogList();

        blogPostListPage
            .assertBlogPostContentContains(contentPreview);
    }

    @TestRail (id = "C6116")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, priority=1)
    public void postShouldBeVisibleToManagerUser()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(manager, siteModel.get(), SiteManager);

        sitePagesService.createBlogPost(manager.getUsername(), manager.getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        authenticateUsingCookies(manager);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .readPost(postTitle);

        blogPostViewPage
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogPostContentEqualsTo(postContent);

        blogPostViewPage
            .navigateBackToBlogList();

        blogPostListPage
            .filterPostBy(MY_DRAFTS_POSTS)
            .openEditForm(postTitle);

        editBlogPostPage
            .publishPostInternally();

        authenticateUsingCookies(userModel.get());

        blogPostListPage
            .navigate(siteModel.get())
            .readPost(postTitle);

        blogPostViewPage
            .assertBlogTitleEqualsTo(postTitle);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}
