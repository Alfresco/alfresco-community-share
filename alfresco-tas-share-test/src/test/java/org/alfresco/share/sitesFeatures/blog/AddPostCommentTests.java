package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.ALL_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.LATEST_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.MY_DRAFTS_POSTS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.BlogPromptWindow;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AddPostCommentTests extends BaseTest
{
    private final String ADD_YOUR_COMMENT_LABEL = "Add Your Comment...";
    private final String EXPECTED_NUMBER_OF_REPLIES = "1";

    private final String postTitle = "Post Title ".concat(randomAlphanumeric(5));
    private final String postContent = "Post Content ".concat(randomAlphanumeric(5));
    private final String postComment = "Post Comment ".concat(randomAlphanumeric(5));

    @Autowired
    private SiteService siteService;

    @Autowired
    protected SitePagesService sitePagesService;

    private BlogPostViewPage blogPostViewPage;
    private BlogPostListPage blogPostListPage;
    private BlogPromptWindow blogPromptWindow;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

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
        blogPromptWindow = new BlogPromptWindow(webDriver);
    }

    @TestRail(id = "C6011")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldAddCommentToBlogPost()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel())
            .assertBlogTitleEqualsTo(postTitle)
            .readPost(postTitle);

        blogPostViewPage
            .openCommentEditor();

        blogPromptWindow
            .assertCommentBoxLabelEqualsTo(ADD_YOUR_COMMENT_LABEL)
            .writePostComment(postComment)
            .addPostComment();

        blogPostViewPage
            .navigateBackToBlogList()
            .assertPostNumberOfRepliesEqualTo(postTitle, EXPECTED_NUMBER_OF_REPLIES);
    }

    @TestRail(id = "C6035")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldAddCommentToDraftBlogPost()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertPostInfoBarTitleEqualsTo(MY_DRAFTS_POSTS.getExpectedFilterLabel())
            .readPost(postTitle);

        blogPostViewPage
            .openCommentEditor();

        blogPromptWindow
            .assertCommentBoxLabelEqualsTo(ADD_YOUR_COMMENT_LABEL)
            .writePostComment(postComment)
            .addPostComment();

        blogPostViewPage
            .navigateBackToBlogList()
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertPostNumberOfRepliesEqualTo(postTitle, EXPECTED_NUMBER_OF_REPLIES);

        blogPostListPage
            .filterPostBy(ALL_POSTS)
            .assertPostInfoBarTitleEqualsTo(ALL_POSTS.getExpectedFilterLabel())
            .assertPostNumberOfRepliesEqualTo(postTitle, EXPECTED_NUMBER_OF_REPLIES);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}
