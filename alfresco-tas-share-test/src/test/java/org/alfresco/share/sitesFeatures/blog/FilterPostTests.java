package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.ALL_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.LATEST_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.MY_DRAFTS_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.MY_PUBLISHED_POSTS;
import static org.alfresco.utility.constants.UserRole.SiteManager;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FilterPostTests extends BaseTest
{
    private final String AUTHOR_LABEL = "Author:";
    private final String EMPTY_SPACE = " ";
    private final String POSTS_FOR_MONTH = "Posts for Month ";
    private final String MMMM_YYYY = "MMMM yyyy";

    private final String postTitle = "Post Title ".concat(randomAlphanumeric(5));
    private final String postContent = "Post Content ".concat(randomAlphanumeric(5));
    private final List<String> tags = Collections.synchronizedList(new ArrayList<>());
    private final String tag = "Tag ".concat(randomAlphanumeric(5));
    private String expectedAuthorValue;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SitePagesService sitePagesService;

    private BlogPostListPage blogPostListPage;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();


    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(getDataUser().usingAdmin().createRandomTestUser());
        siteModel.set(getDataSite().usingUser(userModel.get()).createPublicRandomSite());
        siteService.addPageToSite(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), Page.BLOG, null);

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());
        authenticateUsingCookies(userModel.get());

        blogPostListPage = new BlogPostListPage(webDriver);
    }

    @TestRail(id = "C6001")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostFilteredByNewPosts()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(manager, siteModel.get(), SiteManager);

        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(ALL_POSTS)
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogAuthorPostEqualsTo(postTitle, AUTHOR_LABEL, expectedAuthorValue)
            .assertBlogPostIsNotDisplayed(manager.getUsername());
    }

    @TestRail (id = "C6004")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES, "singlePipelineFailure"})
    public void shouldDisplayBlogPostFilteredByLatestPosts()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(manager, siteModel.get(), SiteManager);

        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(LATEST_POSTS)
            .assertBlogTitleEqualsTo(postTitle)
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel());

        blogPostListPage
            .assertBlogAuthorPostEqualsTo(postTitle, AUTHOR_LABEL, expectedAuthorValue)
            .assertBlogPostIsNotDisplayed(manager.getUsername());
    }

    @TestRail(id = "C6005")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostFilteredByMyDrafts()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(manager, siteModel.get(), SiteManager);

        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertPostInfoBarTitleEqualsTo(MY_DRAFTS_POSTS.getExpectedFilterLabel());

        blogPostListPage
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogAuthorPostEqualsTo(postTitle, AUTHOR_LABEL, expectedAuthorValue)
            .assertBlogPostIsNotDisplayed(manager.getUsername());
    }

    @TestRail(id = "C6006")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostFilteredByMyPublishedPosts()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(manager, siteModel.get(), SiteManager);

        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_PUBLISHED_POSTS)
            .assertPostInfoBarTitleEqualsTo(MY_PUBLISHED_POSTS.getExpectedFilterLabel());

        blogPostListPage
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogAuthorPostEqualsTo(postTitle, AUTHOR_LABEL, expectedAuthorValue)
            .assertBlogPostIsNotDisplayed(manager.getUsername());
    }


    @TestRail (id = "C6008")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostFilteredByTags()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(manager, siteModel.get(), SiteManager);

        tags.add(tag);
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, tags);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostByTag(tag)
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogAuthorPostEqualsTo(postTitle, AUTHOR_LABEL, expectedAuthorValue)
            .assertBlogPostIsNotDisplayed(manager.getUsername());
    }

    @TestRail (id = "C6010")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void shouldDisplayBlogPostFilteredByArchive()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(manager, siteModel.get(), SiteManager);

        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        String currentMonthAndYear = new SimpleDateFormat(MMMM_YYYY).format(Calendar.getInstance().getTime());

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostByMonthAndYearFromArchive(currentMonthAndYear)
            .assertPostInfoBarTitleEqualsTo(POSTS_FOR_MONTH.concat(currentMonthAndYear))
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogPostIsNotDisplayed(manager.getUsername());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}