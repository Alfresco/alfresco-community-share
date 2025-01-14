package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.LATEST_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.MY_DRAFTS_POSTS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

public class EditPostCommentsTests extends BaseTest
{
    private final String EMPTY_SPACE = " ";
    private final String EDIT_COMMENT_LABEL = "Edit Comment...";

    private final String postTitle = "Post Title ".concat(randomAlphanumeric(5));
    private final String postContent = "Post Content ".concat(randomAlphanumeric(5));
    private final String postComment = "Post Comment ".concat(randomAlphanumeric(5));
    private final String editedComment = "Edited Comment".concat(randomAlphanumeric(5));
    private String fullUsername;

    @Autowired
    private SiteService siteService;

    @Autowired
    protected SitePagesService sitePagesService;

    private BlogPostListPage blogPostListPage;
    private BlogPostViewPage blogPostViewPage;
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

        fullUsername = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        authenticateUsingCookies(userModel.get());

        blogPostViewPage = new BlogPostViewPage(webDriver);
        blogPostListPage = new BlogPostListPage(webDriver);
        blogPromptWindow = new BlogPromptWindow(webDriver);
    }

    @TestRail(id = "C6061")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldEditBlogPostComment()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        sitePagesService.commentBlog(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, false, postComment);

        blogPostListPage
            .navigate(siteModel.get())
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel())
            .readPost(postTitle);

        blogPostViewPage
            .openEditCommentEditor(fullUsername);

        blogPromptWindow
            .assertCommentBoxLabelEqualsTo(EDIT_COMMENT_LABEL)
            .editComment(editedComment)
            .saveEditedComment();

        blogPostViewPage
            .assertCommentEqualsTo(fullUsername, editedComment);
    }

    @TestRail(id = "C6058")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void cancelEditingCommentOfPost()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        sitePagesService.commentBlog(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, false, postComment);

        blogPostListPage
            .navigate(siteModel.get())
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel())
            .readPost(postTitle);

        blogPostViewPage
            .openEditCommentEditor(fullUsername);

        blogPromptWindow
            .assertCommentBoxLabelEqualsTo(EDIT_COMMENT_LABEL)
            .editComment(editedComment)
            .cancelEditedComment();

        blogPostViewPage
            .assertCommentEquals(fullUsername, postComment);
    }


    @TestRail(id = "C6062")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldEditDraftBlogPostComment()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        sitePagesService.commentBlog(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, true, postComment);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertPostInfoBarTitleEqualsTo(MY_DRAFTS_POSTS.getExpectedFilterLabel())
            .readPost(postTitle);

        blogPostViewPage
            .openEditCommentEditor(fullUsername);

        blogPromptWindow
            .assertCommentBoxLabelEqualsTo(EDIT_COMMENT_LABEL)
            .editComment(editedComment)
            .saveEditedComment();

        blogPostViewPage
            .assertCommentEqualsTo(fullUsername, editedComment);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteSitesIfNotNull(siteModel.get());
        deleteUsersIfNotNull(userModel.get());
    }
}
