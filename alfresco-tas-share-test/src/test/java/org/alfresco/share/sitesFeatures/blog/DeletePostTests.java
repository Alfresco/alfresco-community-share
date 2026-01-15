package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.ALL_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.MY_DRAFTS_POSTS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.enums.BlogPostFilters;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeletePostTests extends BaseTest
{
    private final String CONFIRMATION_QUESTION = "blog.dialog.confirmation";
    private final String QUESTION_MARK = "blog.dialog.question.mark";
    private final String NO_BLOG_POSTS_FOUND = "blogPost.noPostsFound.label";

    private final String postTitle = "Post Title ".concat(randomAlphanumeric(5));
    private final String postContent = "Post Content ".concat(randomAlphanumeric(5));

    @Autowired
    private SiteService siteService;

    @Autowired
    private SitePagesService sitePagesService;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    private BlogPostListPage blogPostListPage;
    private BlogPostViewPage blogPostViewPage;
    private DeleteDialog deleteDialog;

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
        deleteDialog = new DeleteDialog(webDriver);
    }

    @TestRail(id = "C5955")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayNoPostsFoundWhenDeleteBlogPostFromBlogListPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .deletePost(postTitle);

        deleteDialog
            .assertConfirmDeleteMessageEqualsTo(language.translate(CONFIRMATION_QUESTION)
                .concat(postTitle).concat(language.translate(QUESTION_MARK)))
            .confirmDeletion();

        blogPostListPage
            .assertNoBlogPostFound(language.translate(NO_BLOG_POSTS_FOUND));
    }

    @TestRail(id = "C5957")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayNoPostsFoundWhenDeleteDraftPost()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(BlogPostFilters.MY_DRAFTS_POSTS)
            .deletePost(postTitle);

        deleteDialog
            .assertConfirmDeleteMessageEqualsTo(language.translate(CONFIRMATION_QUESTION)
                .concat(postTitle).concat(language.translate(QUESTION_MARK)))
            .confirmDeletion();

        blogPostListPage
            .filterPostBy(BlogPostFilters.MY_DRAFTS_POSTS)
            .assertNoBlogPostFound(language.translate(NO_BLOG_POSTS_FOUND));
    }

    @TestRail(id = "C5959")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayNoPostsFoundWhenDeletePostFromBlogViewPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .navigateToBlogPostViewPage(postTitle);

        blogPostViewPage
            .deletePost();

        deleteDialog
            .assertConfirmDeleteMessageEqualsTo(language.translate(CONFIRMATION_QUESTION)
                .concat(postTitle).concat(language.translate(QUESTION_MARK)))
            .confirmDeletion();

        blogPostListPage
            .filterPostBy(ALL_POSTS)
            .assertNoBlogPostFound(language.translate(NO_BLOG_POSTS_FOUND));

        blogPostListPage
            .filterPostBy(ALL_POSTS)
            .assertNoBlogPostFound(language.translate(NO_BLOG_POSTS_FOUND));
    }

    @TestRail(id = "C5967")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayNoPostsFoundWhenDeleteDraftPostFromBlogViewPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .readPost(postTitle);

        blogPostViewPage
            .deletePost();

        deleteDialog
            .assertConfirmDeleteMessageEqualsTo(language.translate(CONFIRMATION_QUESTION)
                .concat(postTitle).concat(language.translate(QUESTION_MARK)))
            .confirmDeletion();

        blogPostListPage
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertNoBlogPostFound(language.translate(NO_BLOG_POSTS_FOUND));

        blogPostListPage
            .filterPostBy(ALL_POSTS)
            .assertNoBlogPostFound(language.translate(NO_BLOG_POSTS_FOUND));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}
