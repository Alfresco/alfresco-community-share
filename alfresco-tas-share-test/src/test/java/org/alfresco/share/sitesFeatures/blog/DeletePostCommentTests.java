package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.MY_DRAFTS_POSTS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
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

public class DeletePostCommentTests extends BaseTest
{
    private final String EMPTY_SPACE = " ";
    private final String CONFIRM_DELETE_MESSAGE = "blog.dialog.confirmation.comment";
    private final String NO_COMMENTS = "blog.post.delete.comment";
    private final String ZERO_REPLIES = "blog.post.0.replies";

    private final String postTitle = "Post Title ".concat(randomAlphanumeric(5));
    private final String postContent = "Post Content ".concat(randomAlphanumeric(5));
    private final String postComment = "Post Comment ".concat(randomAlphanumeric(5));

    @Autowired
    private SiteService siteService;

    @Autowired
    private SitePagesService sitePagesService;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();
    private String fullUsername;

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

        fullUsername = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        authenticateUsingCookies(userModel.get());

        blogPostViewPage = new BlogPostViewPage(webDriver);
        blogPostListPage = new BlogPostListPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
    }

    @TestRail(id = "C6063")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void deleteCommentBlogPost()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        sitePagesService.commentBlog(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, false, postComment);

        blogPostListPage
            .navigate(siteModel.get())
            .readPost(postTitle);

        blogPostViewPage
            .deleteComment(fullUsername);

        deleteDialog
            .assertConfirmDeleteMessageEqualsTo(language.translate(CONFIRM_DELETE_MESSAGE))
            .confirmDeletion();

        blogPostViewPage
            .assertNoCommentsLabelEqualsTo(language.translate(NO_COMMENTS))
            .navigateBackToBlogList();

        blogPostListPage
            .assertPostNumberOfRepliesEqualTo(postTitle, language.translate(ZERO_REPLIES));
    }

    @TestRail(id = "C6064")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void deleteCommentDraftPost()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        sitePagesService.commentBlog(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, true, postComment);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .readPost(postTitle);

        blogPostViewPage
            .deleteComment(fullUsername);

        deleteDialog
            .assertConfirmDeleteMessageEqualsTo(language.translate(CONFIRM_DELETE_MESSAGE))
            .confirmDeletion();

        blogPostViewPage
            .assertNoCommentsLabelEqualsTo(language.translate(NO_COMMENTS))
            .navigateBackToBlogList();

    blogPostListPage
        .filterPostBy(MY_DRAFTS_POSTS)
        .assertPostNumberOfRepliesEqualTo(postTitle, language.translate(ZERO_REPLIES));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}
