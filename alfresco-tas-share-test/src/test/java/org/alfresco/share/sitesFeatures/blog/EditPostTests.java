package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.ALL_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.LATEST_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.MY_DRAFTS_POSTS;
import static org.alfresco.po.enums.BlogPostFilters.MY_PUBLISHED_POSTS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.alfresco.dataprep.AlfrescoHttpClient;
import org.alfresco.dataprep.AlfrescoHttpClientFactory;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EditPostTests extends BaseTest
{
    private final String EDIT_BLOG_POST = "Edit Blog Post";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy HH:mm:ss");

    private final String UPDATED = "blogPost.updated";
    private final String DRAFT_STATUS = "blogPost.draft";
    private final String TAGS_LABEL = "blogPost.tags";
    private final String PUBLISHED_ON_LABEL = "blogPost.publishedOn.label";
    private final String AUTHOR_LABEL = "blogPost.author.label";
    private final String EMPTY_SPACE = " ";
    public String expectedAuthorValue;

    private final String postTitle = "Post Title ".concat(randomAlphanumeric(5));
    private final String postTitleEdited = "Edited title ".concat(randomAlphanumeric(5));
    private final String postContent = "Post Content ".concat(randomAlphanumeric(5));
    private final String postTag = "tag".concat(randomAlphabetic(3)).toLowerCase();

    @Autowired
    private SiteService siteService;

    @Autowired
    private SitePagesService sitePagesService;

    @Autowired
    private AlfrescoHttpClientFactory alfrescoHttpClientFactory;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    private BlogPostListPage blogPostListPage;
    private BlogPostViewPage blogPostViewPage;
    private EditBlogPostPage editBlogPostPage;

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

    @TestRail(id = "C5555")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void cancellingEditingBlogPost() {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel());

        blogPostListPage
            .openEditForm(postTitle);

        editBlogPostPage
            .assertPageFormHeaderEqualsTo(EDIT_BLOG_POST);

        editBlogPostPage
            .setTitle(postTitleEdited)
            .setContent(postContent)
            .setTag(postTag)
            .addTag();

        editBlogPostPage
            .clickCancelButton();

        blogPostViewPage
            .assertBlogTitleEquals(postTitle)
            .assertBlogPostContentEquals(postContent)
            .assertBlogPostTagEquals(postTitle, language.translate(TAGS_LABEL), "(None)");
    }

    @TestRail(id = "C5560")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostDetailsWhenEditFromBlogPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        blogPostListPage
            .navigate(siteModel.get())
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel());

        blogPostListPage
            .openEditForm(postTitle);

        editBlogPostPage
            .assertPageFormHeaderEqualsTo(EDIT_BLOG_POST);

        editBlogPostPage
            .setTitle(postTitleEdited)
            .setContent(postContent)
            .setTag(postTag)
            .addTag();

        editBlogPostPage
            .updatePost();

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        blogPostViewPage
            .navigateBackToBlogList();

        blogPostListPage
            .assertBlogTitleEqualsTo(postTitleEdited)
            .assertBlogPublishDateContains(postTitleEdited, language.translate(PUBLISHED_ON_LABEL),
                getPublishDateFromResponse(formatter), formatter)
            .assertBlogAuthorPostEqualsTo(postTitleEdited, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostContentEqualsTo(postContent);
    }

    @TestRail(id = "C5561")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostDetailsWhenEditFromBlogViewPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, false, null);

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        blogPostListPage
            .navigate(siteModel.get())
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel())
            .readPost(postTitle);

        blogPostViewPage
            .openEditForm(postTitle);

        editBlogPostPage
            .assertPageFormHeaderEqualsTo(EDIT_BLOG_POST);

        editBlogPostPage
            .setTitle(postTitleEdited)
            .setContent(postContent)
            .setTag(postTag)
            .addTag();

        editBlogPostPage
            .updatePost();

        blogPostViewPage
            .assertBlogTitleEqualsTo(postTitleEdited)
            .assertBlogPostStatus(language.translate(UPDATED))
            .assertBlogAuthorPostEqualsTo(postTitleEdited, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostTagEqualsTo(postTitleEdited, language.translate(TAGS_LABEL), postTag)
            .assertBlogPostContentEqualsTo(postContent);
    }

    @TestRail(id = "C6107")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostDraftDetailsWhenEditedFromBlogPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertPostInfoBarTitleEqualsTo(MY_DRAFTS_POSTS.getExpectedFilterLabel());

        blogPostListPage
            .openEditForm(postTitle);

        editBlogPostPage
            .assertPageFormHeaderEqualsTo(EDIT_BLOG_POST);

        editBlogPostPage
            .setTitle(postTitleEdited)
            .setContent(postContent)
            .setTag(postTag)
            .addTag();

        editBlogPostPage
            .updatePost();

        blogPostViewPage
            .navigateBackToBlogList();

        blogPostListPage
            .filterPostBy(MY_DRAFTS_POSTS);

        blogPostListPage
            .assertBlogTitleEqualsTo(postTitleEdited)
            .assertBlogAuthorPostEqualsTo(postTitleEdited, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostContentEqualsTo(postContent);
    }

    @TestRail(id = "C6108")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    private void shouldDisplayBlogPostDraftDetailsWhenEditedFromBlogViewPage()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(MY_DRAFTS_POSTS)
            .assertPostInfoBarTitleEqualsTo(MY_DRAFTS_POSTS.getExpectedFilterLabel())
            .readPost(postTitle);

        blogPostViewPage
            .openEditForm(postTitle);

        editBlogPostPage
            .assertPageFormHeaderEqualsTo(EDIT_BLOG_POST);

        editBlogPostPage
            .setTitle(postTitleEdited)
            .setContent(postContent)
            .setTag(postTag)
            .addTag();

        editBlogPostPage
            .updatePost();

        blogPostViewPage
            .assertBlogTitleEqualsTo(postTitleEdited)
            .assertBlogPostStatus(language.translate(DRAFT_STATUS))
            .assertBlogAuthorPostEqualsTo(postTitleEdited, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostTagEqualsTo(postTitleEdited, language.translate(TAGS_LABEL), postTag)
            .assertBlogPostContentEqualsTo(postContent);
    }

    @TestRail(id = "C6110")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogDraftPostWhenPublishItInternally()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), postTitle, postContent, true, null);

        expectedAuthorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());

        blogPostListPage
            .navigate(siteModel.get())
            .filterPostBy(ALL_POSTS)
            .assertPostInfoBarTitleEqualsTo(ALL_POSTS.getExpectedFilterLabel())
            .readPost(postTitle);

        blogPostViewPage
            .openEditForm(postTitle);

        editBlogPostPage
            .assertPageFormHeaderEqualsTo(EDIT_BLOG_POST);

        editBlogPostPage
            .setTitle(postTitleEdited)
            .setContent(postContent)
            .setTag(postTag)
            .addTag();

        editBlogPostPage
            .publishPostInternally();

        blogPostViewPage
            .assertBlogTitleEqualsTo(postTitleEdited)
            .assertBlogAuthorPostEqualsTo(postTitleEdited, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostTagEqualsTo(postTitleEdited, language.translate(TAGS_LABEL), postTag)
            .assertBlogPostContentEqualsTo(postContent);

        blogPostViewPage
            .navigateBackToBlogList();

        blogPostListPage
            .filterPostBy(MY_PUBLISHED_POSTS);

        blogPostListPage
            .assertBlogTitleEqualsTo(postTitleEdited)
            .assertBlogPublishDateContains(postTitleEdited, language.translate(PUBLISHED_ON_LABEL),
                getPublishDateFromResponse(formatter), formatter)
            .assertBlogAuthorPostEqualsTo(postTitleEdited, language.translate(AUTHOR_LABEL), expectedAuthorValue)
            .assertBlogPostContentEqualsTo(postContent)
            .assertBlogPostHaveTagEqualsTo(postTitleEdited, postTag);
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
