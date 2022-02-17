package org.alfresco.share.sitesFeatures.blog;

import static org.alfresco.po.enums.BlogPostFilters.LATEST_POSTS;
import static org.alfresco.po.share.site.SitePageType.BLOG;
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
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AccessBlogTests extends BaseTest
{
    private final String NONE = "blogPost.none";
    private final String DETAILED_VIEW = "blogPost.detailedView.button";
    private final String ZERO_REPLIES = "blogPost.zero.replies";
    private final String AUTHOR_LABEL = "blogPost.author.label";
    private final String PUBLISHED_ON_LABEL = "blogPost.publishedOn.label";
    private final String NO_BLOG_POSTS_FOUND = "blogPost.noPostsFound.label";

    private final String ALFRESCO_PAGE_TITLE = "blogPage.title";
    private final String NEW_BLOG = "newBlog";
    private final String EMPTY_SPACE = " ";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy HH:mm:ss");
    private final String blogTitle = "Blog Title ".concat(randomAlphanumeric(5));
    private final String blogContent = "Blog Content ".concat(randomAlphanumeric(5));
    private final String tag = "tag ".concat(randomAlphanumeric(5));
    private String authorValue;

    private final List<String> tags = Collections.synchronizedList(new ArrayList<>());
    private final List<String> noTags = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    private SiteService siteService;

    @Autowired
    protected SitePagesService sitePagesService;

    @Autowired
    private AlfrescoHttpClientFactory alfrescoHttpClientFactory;

    private CustomizeSitePage customizeSitePage;
    private SiteDashboardPage siteDashboardPage;
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

        authenticateUsingCookies(userModel.get());

        customizeSitePage = new CustomizeSitePage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        blogPostListPage = new BlogPostListPage(webDriver);
    }

    @TestRail(id = "C5526")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES, TestGroup.INTEGRATION})
    public void shouldDisplayRenamedBlogInMenuBar()
    {
        blogPostListPage
            .navigate(siteModel.get())
            .assertNoBlogPostFound(language.translate(NO_BLOG_POSTS_FOUND));

        customizeSitePage
            .navigate(siteModel.get());

        customizeSitePage
            .renameSitePage(BLOG, NEW_BLOG)
            .saveChanges();

        siteDashboardPage
            .navigate(siteModel.get());

        blogPostListPage
            .assertNewBlogIsDisplayedInMenuBar(NEW_BLOG)
            .navigateToBlogPage()
            .assertBrowserPageTitleIs(language.translate(ALFRESCO_PAGE_TITLE).concat(EMPTY_SPACE).concat(NEW_BLOG));
    }

    @TestRail(id = "C5527")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostSimpleDetails()
    {
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), blogTitle, blogContent, false, noTags);

        authorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());
        blogPostListPage.navigate(siteModel.get());

        blogPostListPage
            .assertBlogPostDontHaveTag(blogTitle, language.translate(NONE))
            .openBlogSimpleView()
            .assertButtonTextEqualsTo(language.translate(DETAILED_VIEW))
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel())
            .assertBlogTitleEqualsTo(blogTitle)
            .assertBlogPublishDateContains(blogTitle, language.translate(PUBLISHED_ON_LABEL),
                getPublishDateFromResponse(formatter), formatter)
            .assertBlogAuthorPostEqualsTo(blogTitle, language.translate(AUTHOR_LABEL), authorValue);
    }

    @TestRail(id = "C5527")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void shouldDisplayBlogPostDetailedView()
    {
        tags.add(tag);
        sitePagesService.createBlogPost(userModel.get().getUsername(), userModel.get().getPassword(),
            siteModel.get().getId(), blogTitle, blogContent, false, tags);

        authorValue = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());
        blogPostListPage.navigate(siteModel.get());

        blogPostListPage
            .assertPostInfoBarTitleEqualsTo(LATEST_POSTS.getExpectedFilterLabel())
            .assertBlogTitleEqualsTo(blogTitle)
            .assertBlogPublishDateContains(
                blogTitle, language.translate(PUBLISHED_ON_LABEL),
            getPublishDateFromResponse(formatter),
            formatter);

        blogPostListPage
            .assertBlogAuthorPostEqualsTo(blogTitle, language.translate(AUTHOR_LABEL), authorValue)
            .assertBlogPostContentEqualsTo(blogContent)
            .assertPostNumberOfRepliesEqualTo(blogTitle, language.translate(ZERO_REPLIES))
            .assertBlogPostHaveTagEqualsTo(blogTitle, tag);
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

    @AfterClass(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}
