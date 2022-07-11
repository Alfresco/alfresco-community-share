package org.alfresco.po.share.site.blog;

import static org.alfresco.common.RetryTime.RETRY_TIME_30;
import static org.alfresco.common.Wait.WAIT_1;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.BlogPostFilters;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class BlogPostListPage extends SiteCommon<BlogPostListPage>
{
    private final String BLOG_POST_LIST = "Blog Post List";
    private final String POSTS_FOR_MONTH = "Posts for Month ";
    private final String ARIA_LABEL_ATTRIBUTE = "aria-label";
    private final String OPEN_PARENTHESIS = "(";
    private final String CLOSE_PARENTHESIS = ")";

    private final By pageTitle = By.cssSelector("[class='listTitle']");
    private final By archivesMonths = By.cssSelector("div[id*='archives'] a.filter-link");
    private final By blogContent = By.xpath(".//div[@class = 'content yuieditor']");
    private final By noBlogPostsFound = By.xpath(".//tbody[@class='yui-dt-message']");
    private final By simpleViewButton = By.cssSelector("button[id$='_default-simpleView-button-button']");
    private final By newPostButton = By.cssSelector("div.new-blog span[id*='_default-create-button']");
    private final By nodeTitle = By.xpath("//span[@class = 'nodeTitle']//a");
    private final By simpleNodePost = By.cssSelector(".node.post.simple");
    private final By postDateTime = By.xpath(".//div[@class = 'published']//span[@class = 'nodeAttrValue']");
    private final By readLabel = By.xpath("div[@class = 'nodeFooter']//span[@class = 'nodeAttrValue']//a");
    private final By editButton = By.xpath(".//../div[@class = 'nodeEdit']//div[@class = 'onEditBlogPost']//a//span[text() = 'Edit']");
    private final By blogLinkName = By.id("HEADER_SITE_BLOG-POSTLIST");
    private final By listTitle = By.className("listTitle");
    private final By tag = By.xpath("//span[@class ='tag']/a");
    private final By blogPostStatus = By.cssSelector(".nodeTitle .nodeStatus");
    private final By deleteButton = By.xpath("//div[@class = 'onDeleteBlogPost']//a[@class='blogpost-action-link-div']");

    private final String postRowPath = "//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '%s']/../../../..";
    private final String valuePath = "//div[@class='published']//span[@class='nodeAttrValue' and normalize-space() = '%s']";
    private final String labelPath = "//div[@class='published']//span[@class='nodeAttrLabel' and normalize-space() = '%s']";
    private final String postFooterPath = "//div[@class = 'nodeFooter' ]//span[text() = '(%s)']";
    private final String tagPath = "//div[@id = 'alf-filters']//div[contains(@id, '_blog-postlist')]//div[@class = 'filter']//span[@class = 'tag']/a[text() = '%s']";
    private final String postViewPagePath = "//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text()='%s']";

    public BlogPostListPage(ThreadLocal<WebDriver> webDriver)
    {
      super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postlist", getCurrentSiteName());
    }

    private WebElement getBlogPostRow(String blogTitle)
    {
        return waitWithRetryAndReturnWebElement(By.xpath(String.format(postRowPath, blogTitle)),
            WAIT_1.getValue(), RETRY_TIME_30.getValue());
    }

    public BlogPostListPage assertNoBlogPostFound(String expectedNoBlogPostsFoundLabel)
    {
        log.info("Assert no blog post found label equals to {}", expectedNoBlogPostsFoundLabel);
        String actualNoBlogPostsFoundLabel = getElementText(noBlogPostsFound);
        assertEquals(actualNoBlogPostsFoundLabel, expectedNoBlogPostsFoundLabel,
            String.format("No blog posts found label not equals %s ", expectedNoBlogPostsFoundLabel));
        return this;
    }

    public BlogPostListPage assertNewBlogIsDisplayedInMenuBar(String expectedBlogName)
    {
        log.info("Assert new blog is displayed in menu bar {}", expectedBlogName);
        waitUntilElementIsVisible(blogLinkName);
        String actualBlogName = findElement(blogLinkName).getAttribute(ARIA_LABEL_ATTRIBUTE).trim();
        assertEquals(actualBlogName, expectedBlogName, String.format("Blog name not equals %s ", expectedBlogName));
        return this;
    }

    public BlogPostListPage navigateToBlogPage()
    {
        log.info("Navigate to blog page");
        clickElement(blogLinkName);
        return this;
    }

    public BlogPostListPage openBlogSimpleView()
    {
        log.info("Open blog simple view");
        clickElement(simpleViewButton);
        waitUntilElementIsVisible(simpleNodePost);
        return this;
    }

    private String getPostPublishedDateTime(String title)
    {
        return getElementText(getBlogPostRow(title).findElement(postDateTime));
    }

    /**
     * Method to compare if blog post date from UI contains blog post date from server
     *
     * @implNote
     * We need to update method which creates a blog post, createBlogPost, in order to have full control on creation date
     */
    public BlogPostListPage assertBlogPublishDateContains(String title,String expectedLabel, LocalDateTime serverDate, DateTimeFormatter formatter)
    {
        LocalDateTime postDateUI = LocalDateTime.parse(getPostPublishedDateTime(title), formatter);
        assertTrue(expectedLabel.concat(formatDate(postDateUI)).contains(formatDate(serverDate)),
            String.format("Post date from UI %s not contains post date from server %s ", postDateUI, serverDate));
        return this;
    }

    private String formatDate(LocalDateTime localDateTime)
    {
        String dayOfWeek = localDateTime.truncatedTo(ChronoUnit.DAYS).toLocalDate().getDayOfWeek()
            .getDisplayName(TextStyle.SHORT, Locale.US);

        String dayOfMonth = String
            .valueOf(localDateTime.truncatedTo(ChronoUnit.DAYS).toLocalDate().getDayOfMonth());

        String month = localDateTime.truncatedTo(ChronoUnit.DAYS).toLocalDate().getMonth()
            .getDisplayName(TextStyle.SHORT, Locale.US);

        String year = String
            .valueOf(localDateTime.truncatedTo(ChronoUnit.DAYS).toLocalDate().getYear());
        String emptySpace = " ";

        return dayOfWeek.concat(emptySpace).concat(dayOfMonth).concat(emptySpace).concat(month)
            .concat(emptySpace).concat(year);
    }

    public BlogPostListPage assertBlogTitleEqualsTo(String expectedBlogTitle)
    {
        log.info("Assert blog title equals to {}", expectedBlogTitle);
        waitUntilElementIsVisible(getBlogPostRow(expectedBlogTitle).findElement(nodeTitle));

        String actualBlogTitle = getElementText(getBlogPostRow(expectedBlogTitle).findElement(nodeTitle));
        assertEquals(actualBlogTitle, expectedBlogTitle, String.format("Blog title not equals %s ", expectedBlogTitle));
        return this;
    }

    public BlogPostListPage assertBlogPostStatusEqualsTo(String expectedStatus)
    {
        log.info("Assert blog post status equals to {}", expectedStatus);
        String actualStatus = getElementText(blogPostStatus);
        assertEquals(actualStatus, expectedStatus, String.format("Blog post status not equals %s ", expectedStatus));
        return this;
    }

    public BlogPostListPage assertBlogAuthorPostEqualsTo(String blogTitle, String expectedAuthorLabel, String expectedAuthorValue)
    {
        log.info("Assert blog author equals to {}", expectedAuthorValue);
        String label = getAuthor(blogTitle, expectedAuthorLabel, labelPath);
        String value = getAuthor(blogTitle, expectedAuthorValue, valuePath);
        String actualAuthorValue = label.concat(value);

        assertEquals(actualAuthorValue, expectedAuthorLabel.concat(expectedAuthorValue),
            String.format("Blog author not equals %s ", expectedAuthorValue));
        return this;
    }

    private String getAuthor(String blogTitle, String authorLabel, String labelPath)
    {
        return getElementText(getBlogPostRow(blogTitle)
            .findElement(By.xpath(String.format(labelPath, authorLabel))));
    }

    public String getBlogPostContent(String title)
    {
        WebElement post = getBlogPostRow(title);
        return getElementText(post.findElement(blogContent));
    }

    public BlogPostListPage assertPostNumberOfRepliesEqualTo(String title, String expectedNumberOfReplies)
    {
        log.info("Assert blog post number of replies equal to {}", expectedNumberOfReplies);
        waitUntilElementIsVisible(By.xpath(String.format(postFooterPath, expectedNumberOfReplies)));

        String actualNumberOfReplies = getActualNumberOfReplies(title, expectedNumberOfReplies);
        assertEquals(actualNumberOfReplies, formattedExpectedFooterLabel(expectedNumberOfReplies),
        String.format("Number of replies not equals %s ", formattedExpectedFooterLabel(expectedNumberOfReplies)));
        return this;
    }

    private String getActualNumberOfReplies(String title, String expectedNumberOfReplies)
    {
        return getElementText(getBlogPostRow(title)
            .findElement(By.xpath(String.format(postFooterPath, expectedNumberOfReplies))));
    }

    public BlogPostListPage assertBlogPostDontHaveTag(String title, String expectedNoneTag)
    {
        log.info("Assert blog post have tag label equals to {}", expectedNoneTag);
        waitUntilElementIsVisible(By.xpath(String.format(postFooterPath, expectedNoneTag)));

        String actualTag = getElementText(getBlogPostRow(title)
            .findElement(By.xpath(String.format(postFooterPath, expectedNoneTag))));

        assertEquals(actualTag, formattedExpectedFooterLabel(expectedNoneTag),
            String.format("Tag not equals %s ", expectedNoneTag));
        return this;
    }

    private String formattedExpectedFooterLabel(String expectedNoneTag)
    {
        return OPEN_PARENTHESIS.concat(expectedNoneTag).concat(CLOSE_PARENTHESIS);
    }

    public BlogPostListPage assertBlogPostHaveTagEqualsTo(String title, String expectedTag)
    {
        log.info("Assert blog post have tag equals to {}", expectedTag);
        String actualTag = getElementText(getBlogPostRow(title).findElement(tag));
        assertEquals(actualTag, expectedTag.toLowerCase(), String.format("Tag not equals %s ", expectedTag));
        return this;
    }

    public BlogPostListPage assertPostInfoBarTitleEqualsTo(String expectedPageTitle)
    {
        log.info("Assert post info bar title equals to {}", expectedPageTitle);
        String actualPageTitle = getElementText(pageTitle);
        assertEquals(actualPageTitle, expectedPageTitle,
            String.format("Post info bar title not equals %s", expectedPageTitle));
        return this;
    }

    public BlogPostListPage assertBlogPostContentEqualsTo(String expectedContent)
    {
        log.info("Assert blog content equals to {}", expectedContent);
        String actualContent = getElementText(blogContent);
        assertEquals(actualContent, expectedContent, String.format("Blog content not equals %s ", expectedContent));
        return this;
    }

    public BlogPostListPage assertBlogPostContentContains(String expectedContent)
    {
        log.info("Assert blog content contains to {}", expectedContent);
        String actualContent = getElementText(blogContent);
        assertTrue(actualContent.contains(expectedContent), String.format("Blog content not equals %s ", expectedContent));
        return this;
    }

    public BlogPostListPage assertBlogPostIsNotDisplayed(String title)
    {
        log.info("Assert blog post {} is not displayed", title);
        By blogPost = By.xpath(String.format(postRowPath, title));
        assertFalse(isElementDisplayed(blogPost), String.format("Blog post is displayed %s", title));
        return this;
    }

    public BlogPostListPage filterPostBy(BlogPostFilters blogPostFilters)
    {
        log.info("Filter blog post by {}", blogPostFilters);
        By filter = By.cssSelector(blogPostFilters.getLocator());
        clickElement(filter);
        waitUntilElementContainsText(listTitle, blogPostFilters.getExpectedFilterLabel());
        return this;
    }

    public BlogPostListPage filterPostByTag(String tag)
    {
        log.info("Filter post by tag {}", tag);
        By tagLocator = By.xpath(String.format(tagPath, tag.toLowerCase()));
        clickElement(tagLocator);
        waitUntilElementContainsText(listTitle, BLOG_POST_LIST);
        return this;
    }

    public BlogPostViewPage readPost(String postTitle)
    {
        log.info("Read post {}", postTitle);
        waitUntilWebElementIsDisplayedWithRetry(getBlogPostRow(postTitle));
        clickElement(getBlogPostRow(postTitle).findElement(readLabel));
        return new BlogPostViewPage(webDriver);
    }

    public BlogPostViewPage navigateToBlogPostViewPage(String title)
    {
        log.info("Navigate to blog post view page {}", title);
        WebElement viewPageLink = findElement(By.xpath(String.format(postViewPagePath, title)));
        clickElement(viewPageLink);
        return new BlogPostViewPage(webDriver);
    }

    public EditBlogPostPage openEditForm(String title)
    {
        clickElement(getBlogPostRow(title).findElement(editButton));
        return new EditBlogPostPage(webDriver);
    }

    public CreateBlogPostPage openCreateNewPostForm()
    {
        clickElement(newPostButton);
        return new CreateBlogPostPage(webDriver);
    }

    public BlogPostListPage deletePost(String title)
    {
        log.info("Delete post {}", title);
        clickElement(getBlogPostRow(title).findElement(deleteButton));
        return this;
    }

    public BlogPostListPage assertButtonTextEqualsTo(String expectedButtonText)
    {
        log.info("Assert button text equals to {}", expectedButtonText);
        assertEquals(getElementText(simpleViewButton), expectedButtonText,
            String.format("Button text not equals %s ", expectedButtonText));
        return this;
    }

    public BlogPostListPage filterPostByMonthAndYearFromArchive(String monthAndYear)
    {
        log.info("filter post by month and year from archive {}", monthAndYear);
        clickElement(selectArchiveMonth(monthAndYear));
        waitUntilElementContainsText(pageTitle, POSTS_FOR_MONTH.concat(monthAndYear));
        return  this;
    }

    private WebElement selectArchiveMonth(String month)
    {
        return findFirstElementWithValue(archivesMonths, month);
    }
}
