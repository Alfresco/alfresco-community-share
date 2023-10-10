package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.MY_DRAFTS_POSTS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.alfresco.dataprep.AlfrescoHttpClient;
import org.alfresco.dataprep.AlfrescoHttpClientFactory;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.BlogPostViewPage;
import org.alfresco.po.share.site.blog.CreateBlogPostPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreatePostTests extends BaseTest
{
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy HH:mm:ss");

    private final String NO_BLOG_POSTS_FOUND = "blogPost.noPostsFound.label";
    private final String CREATE_BLOG_POST = "blogPost.header.title";
    private final String NONE_LABEL = "blogPost.none";
    private final String DRAFT_LABEL = "blogPost.draft";
    private final String TAGS_LABEL = "blogPost.tags";
    private final String PUBLISHED_ON_LABEL = "blogPost.publishedOn.label";
    private final String AUTHOR_LABEL = "blogPost.author.label";
    private final String EMPTY_SPACE = " ";

    private final String postTitle = "Post Title ".concat(randomAlphanumeric(5));
    private final String postContent = "Post Content ".concat(randomAlphanumeric(5));
    private final String postTag = "tag".concat(randomAlphabetic(3)).toLowerCase();
    private String expectedAuthorValue;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SitePagesService sitePagesService;

    @Autowired
    private AlfrescoHttpClientFactory alfrescoHttpClientFactory;

    private CreateBlogPostPage createBlogPostPage;
    private BlogPostViewPage blogPostViewPage;
    private BlogPostListPage blogPostListPage;

    private final List<String> tags = Collections.synchronizedList(new ArrayList<>());

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

        createBlogPostPage = new CreateBlogPostPage(webDriver);
        blogPostViewPage = new BlogPostViewPage(webDriver);
        blogPostListPage = new BlogPostListPage(webDriver);
    }

    @TestRail(id = "C5533")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayPostDetailsWhenCreatedFromBlogPage()
    {
        blogPostListPage
            .navigate(siteModel.get())
            .openCreateNewPostForm();

        createBlogPostPage
            .assertPageFormHeaderEqualsTo(language.translate(CREATE_BLOG_POST));

        createBlogPostPage
            .setTitle(postTitle)
            .setContent(postContent)
            .setTag(postTag)
            .addTag()
            .publishPostInternally();

        blogPostListPage
            .navigate(siteModel.get())
            .assertBlogPostHaveTagEqualsTo(postTitle, postTag)
            .assertBlogTitleEqualsTo(postTitle);
    }

    @TestRail(id = "C5535")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayNoBlogPostsFoundWhenCancelledFromBlogPage()
    {
        blogPostListPage
            .navigate(siteModel.get())
            .openCreateNewPostForm();

        createBlogPostPage
            .assertPageFormHeaderEqualsTo(language.translate(CREATE_BLOG_POST));

        createBlogPostPage
            .setTitle(postTitle)
            .setContent(postContent)
            .setTag(postTag)
            .addTag()
            .clickCancelButton();

        blogPostListPage
            .navigate(siteModel.get())
            .assertNoBlogPostFound(language.translate(NO_BLOG_POSTS_FOUND));
    }

    @TestRail(id = "5541")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostDraftDetailsWhenCreatedFromBlogPage()
    {
        blogPostListPage.navigate(siteModel.get());
        blogPostListPage.openCreateNewPostForm();

        createBlogPostPage
            .assertPageFormHeaderEqualsTo(language.translate(CREATE_BLOG_POST));

        createBlogPostPage
            .setTitle(postTitle)
            .setContent(postContent)
            .setTag(postTag)
            .addTag()
            .savePostAsDraft();

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        blogPostViewPage
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogPostStatus(language.translate(DRAFT_LABEL))
            .assertBlogAuthorPostEqualsTo(postTitle, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostTagEqualsTo(postTitle, language.translate(TAGS_LABEL), postTag)
            .assertBlogPostContentEqualsTo(postContent);

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogPostStatusEqualsTo(language.translate(DRAFT_LABEL))
            .assertPostInfoBarTitleEqualsTo(MY_DRAFTS_POSTS.getExpectedFilterLabel())
            .assertBlogAuthorPostEqualsTo(postTitle, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostContentEqualsTo(postContent);
    }

    @TestRail(id = "C6119")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostDetailsWhenCreatedFromBlogPostViewPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, tags);

        blogPostListPage.navigate(siteModel.get());
        blogPostListPage.navigateToBlogPostViewPage(postTitle);

        blogPostViewPage
            .openCreateNewPostForm();

        createBlogPostPage
            .assertPageFormHeaderEqualsTo(language.translate(CREATE_BLOG_POST));

        createBlogPostPage
            .setTitle(postTitle)
            .setContent(postContent)
            .setTag(postTag)
            .addTag()
            .publishPostInternally();

        blogPostViewPage
            .assertBlogTitleEqualsTo(postTitle);

        blogPostViewPage
            .navigateBackToBlogList();

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        blogPostListPage
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogPublishDateContains(postTitle, language.translate(PUBLISHED_ON_LABEL),
                getPublishDateFromResponse(formatter), formatter)
            .assertBlogAuthorPostEqualsTo(postTitle, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostContentEqualsTo(postContent)
            .assertBlogPostDontHaveTag(postTitle, language.translate(NONE_LABEL));
    }

    @TestRail(id = "C6120")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostDraftDetailsWhenCreatedFroBlogPostViewPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, tags);

        blogPostListPage.navigate(siteModel.get());
        blogPostListPage.navigateToBlogPostViewPage(postTitle);

        blogPostViewPage.openCreateNewPostForm();

        createBlogPostPage
            .assertPageFormHeaderEqualsTo(language.translate(CREATE_BLOG_POST));

        createBlogPostPage
            .setTitle(postTitle)
            .setContent(postContent)
            .setTag(postTag)
            .addTag()
            .savePostAsDraft();

        blogPostViewPage
            .navigateBackToBlogList();

        blogPostListPage
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertPostInfoBarTitleEqualsTo(MY_DRAFTS_POSTS.getExpectedFilterLabel());

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        blogPostListPage
            .assertBlogTitleEqualsTo(postTitle)
            .assertBlogPostStatusEqualsTo(language.translate(DRAFT_LABEL))
            .assertBlogAuthorPostEqualsTo(postTitle, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostContentEqualsTo(postContent)
            .assertBlogPostHaveTagEqualsTo(postTitle, postTag);
    }

    private LocalDateTime getPublishDateFromResponse(DateTimeFormatter formatter)
    {
        HttpResponse response = getPostsResponse();
        String responseAsString = response.toString();
        String formattedPostDateFromServer = formatServerDateTime(responseAsString);
        return LocalDateTime.parse(formattedPostDateFromServer, formatter);
    }

    private HttpResponse getPostsResponse()
    {
        AlfrescoHttpClient client = alfrescoHttpClientFactory.getObject();
        String url = client.getApiUrl() + "blog/site/" + siteModel.get().getId() + "/blog/posts";
        HttpGet get = new HttpGet(url);
        return client.execute(userModel.get().getUsername(), userModel.get().getPassword(), get);
    }

    private String formatServerDateTime(String response)
    {
        String getDateTime = response.substring(response.indexOf("Date: "));
        String serverDateTime = getDateTime.substring(0, getDateTime.indexOf("GMT"));
        return serverDateTime.substring(serverDateTime.indexOf(" ")).replace(",", "").trim();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}
