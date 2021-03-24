package org.alfresco.share.sitesFeatures.blog;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlogPostAddCommentTests extends BaseTest
{
    private final String ADD_YOUR_COMMENT_LABEL = "Add Your Comment...";
    private final String EXPECTED_NUMBER_OF_REPLIES = "1";

    @Autowired
    private SiteService siteService;

    @Autowired
    protected SitePagesService sitePagesService;

    private BlogPostViewPage blogPostViewPage;
    private BlogPostListPage blogPostListPage;
    private BlogPromptWindow blogPromptWindow;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    private final String blogTitle = "Blog Title ".concat(randomAlphanumeric(5));
    private final String blogContent = "Blog Content ".concat(randomAlphanumeric(5));
    private final String blogComment = "Blog Comment ".concat(randomAlphanumeric(5));
    private final List<String> noTags = Collections.synchronizedList(new ArrayList<>());

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
            siteModel.get().getId(), blogTitle, blogContent, false, noTags);

        blogPostListPage
            .navigate(siteModel.get())
            .readPost(blogTitle)
            .openCommentEditor()
            .assertAddCommentLabelEqualsTo(ADD_YOUR_COMMENT_LABEL);

        blogPromptWindow
            .writePostComment(blogComment)
            .addPostComment();

        blogPostViewPage
            .navigateBackToBlogList()
            .assertPostNumberOfRepliesEqualTo(blogTitle, EXPECTED_NUMBER_OF_REPLIES);
    }

    @TestRail(id = "C6035")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void addCommentToDraftBlogPost()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), blogTitle, blogContent, true, noTags);

        blogPostListPage
            .navigate(siteModel.get())
            .navigateToMyDrafts()
            .readPost(blogTitle);

        blogPostViewPage
            .openCommentEditor()
            .assertAddCommentLabelEqualsTo(ADD_YOUR_COMMENT_LABEL)
            .writePostComment(blogComment)
            .addPostComment();

        blogPostViewPage
            .navigateBackToBlogList()
            .navigateToMyDrafts()
            .assertPostNumberOfRepliesEqualTo(blogTitle, EXPECTED_NUMBER_OF_REPLIES);

        blogPostListPage
            .navigateToAllFilter()
            .assertPostNumberOfRepliesEqualTo(blogTitle, EXPECTED_NUMBER_OF_REPLIES);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}
