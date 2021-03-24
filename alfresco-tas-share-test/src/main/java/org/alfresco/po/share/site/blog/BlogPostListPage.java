package org.alfresco.po.share.site.blog;

import static org.alfresco.common.RetryTime.RETRY_TIME_30;
import static org.alfresco.common.Wait.WAIT_1;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class BlogPostListPage extends SiteCommon<BlogPostListPage>
{
    private final String ARIA_LABEL_ATTRIBUTE = "aria-label";
    private final String OPEN_PARENTHESIS = "(";
    private final String CLOSE_PARENTHESIS = ")";

    //Below fields will be delete in next PRs
    @FindBy (css = "[class='listTitle']")
    public WebElement pageTitle;

    @FindAll (@FindBy (css = "div[id*='archives'] a.filter-link"))
    private List<WebElement> archivesMonths;

    private final By blogContent = By.xpath(".//div[@class = 'content yuieditor']");
    private final By noBlogPostsFound = By.xpath(".//tbody[@class='yui-dt-message']");
    private final By simpleViewButton = By.cssSelector("button[id$='_default-simpleView-button-button']");
    private final By newPostButton = By.cssSelector("div.new-blog span[id*='_default-create-button']");
    private final By defaultBlogPostList = By.cssSelector("div[id$='_default-postlist']");
    private final By nodeTitle = By.xpath(".//span[@class = 'nodeTitle']");
    private final By simpleNodePost = By.cssSelector(".node.post.simple");
    private final By postDateTime = By.xpath(".//div[@class = 'published']//span[@class = 'nodeAttrValue']");
    private final By readLabel = By.xpath("div[@class = 'nodeFooter']//span[@class = 'nodeAttrValue']//a");

    private final By editButton = By.xpath(".//../div[@class = 'nodeEdit']//div[@class = 'onEditBlogPost']//a//span[text() = 'Edit']");
    private final By blogLinkName = By.id("HEADER_SITE_BLOG-POSTLIST");
    private final By allFilter = By.cssSelector("ul.filterLink span.all>a");
    private final By latestFilter = By.cssSelector("ul.filterLink span.new>a");
    private final By myDraftsFilter = By.cssSelector("ul.filterLink span.mydrafts>a");
    private final By listTitle = By.className("listTitle");
    private final By myPublished = By.cssSelector("ul.filterLink span.mypublished>a");
    private final By tag = By.xpath("//span[@class ='tag']/a");

    private final String postRowPath = "//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '%s']/../../../..";
    private final String labelPath = "//div[@class = 'nodeContent']//span/a[text() = '%s']/../..//span[@class='nodeAttrLabel' and normalize-space()= '%s']";
    private final String valuePath = "//div[@class = 'nodeContent']//span/a[text() = '%s']/../..//span[@class='nodeAttrValue' and normalize-space()='%s']";
    private final String postFooterPath = ".//div[@class = 'nodeFooter' ]//span[text() = '(%s)']";

    public BlogPostListPage(ThreadLocal<WebDriver> webDriver)
    {
      super(webDriver);
    }

    private WebElement getBlogPostRow(String blogTitle)
    {
        return waitWithRetryAndReturnWebElement(By.xpath(String.format(postRowPath, blogTitle)),
            WAIT_1.getValue(), RETRY_TIME_30.getValue());
    }

    public WebElement selectBlogPostWithTitle(String title)
    {
        return findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']/../.."));
    }

    private WebElement selectBlogPostFooter(String title)
    {
        return findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']/../../../.."));
    }

    private WebElement selectTagsByTagName(String tag)
    {
        return findElement(By.xpath(
            "//div[@id = 'alf-filters']//div[contains(@id, '_blog-postlist')]//div[@class = 'filter']//span[@class = 'tag']/a[text() = '" + tag + "']"));
    }

    private WebElement selectArchiveMonth(String month)
    {
        return findFirstElementWithValue(archivesMonths, month);
    }

    public WebElement blogPostTitle(String title)
    {
        return findElement(By.xpath("//div[@class ='nodeContent']//span[@class = 'nodeTitle']//a[text() = '" + title + "']"));
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postlist", getCurrentSiteName());
    }

    public BlogPostListPage assertBlogContentEqualsTo(String expectedBlogContentText)
    {
        log.info("Assert blog content equals to {}", expectedBlogContentText);
        String actualBlogContentText = getElementText(blogContent);
        assertEquals(actualBlogContentText, expectedBlogContentText,
            String.format("Blog content not equals %s ", expectedBlogContentText));
        return this;
    }

    public BlogPostListPage assertNoBlogPostFound(String expectedNoBlogPostsFoundLabel)
    {
        log.info("Assert no blog post found label equals to {}", expectedNoBlogPostsFoundLabel);
        String actualNoBlogPostsFoundLabel = getElementText(noBlogPostsFound);
        assertEquals(actualNoBlogPostsFoundLabel, expectedNoBlogPostsFoundLabel,
            String.format("No blog posts found label not equals %s ", expectedNoBlogPostsFoundLabel));
        return this;
    }

    public boolean isNewPostButtonDisplayed()
    {
        return isElementDisplayed(newPostButton);
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

    public boolean isEditButtonPresentForBlogPost(String title)
    {
        return selectBlogPostWithTitle(title).findElement(By.xpath("//div[@class = 'onEditBlogPost']")).isDisplayed();
    }

    public boolean isDeleteButtonPresentForBlogPost(String title)
    {
        return selectBlogPostWithTitle(title).findElement(By.xpath("//div[@class = 'onDeleteBlogPost']")).isDisplayed();
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

    //this method will be replace in the remained classes with assertBlogAuthorPostEqualsTo
    public String getBlogPostAuthor(String title)
    {
        WebElement post = getBlogPostRow(title);
        List<WebElement> listLabels = post.findElements(By.xpath(".//span[@class='nodeAttrLabel']"));

        int index;
        for (index = 0; index < listLabels.size(); index++)
        {
            if (listLabels.get(index).getText().trim().equals("Author:"))
            {
                break;
            }
        }
        if (index == listLabels.size())
        {
            throw new PageOperationException("Element not found");
        }

        List<WebElement> listAttribute = post.findElements(By.xpath(".//span[@class='nodeAttrValue']"));
        return listAttribute.get(index).getText();
    }

    public BlogPostListPage assertBlogTitleEqualsTo(String expectedBlogTitle)
    {
        log.info("Assert blog title equals to {}", expectedBlogTitle);
        String actualBlogTitle = getElementText(getBlogPostRow(expectedBlogTitle).findElement(nodeTitle));
        assertEquals(actualBlogTitle, expectedBlogTitle, String.format("Blog title not equals %s ", expectedBlogTitle));
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
            .findElement(By.xpath(String.format(labelPath, blogTitle, authorLabel))));
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

    public String getPageTitle()
    {
        return pageTitle.getText();
    }

    public boolean isBlogPostDisplayed(String title)
    {
        return isElementDisplayed(getBlogPostRow(title));
    }

    public BlogPostListPage navigateToAllFilter()
    {
        log.info("Navigate to all filter");
        clickElement(allFilter);
        return this;
    }

    public void clickLatestFilter()
    {
        clickElement(latestFilter);
        waitUntilElementContainsText(listTitle, "New Posts");
    }

    public BlogPostListPage navigateToMyDrafts()
    {
        clickElement(myDraftsFilter);
        waitUntilElementContainsText(listTitle, "My Draft Posts");
        return this;
    }

    public void clickMyPublishedFilter()
    {
        clickElement(myPublished);
        waitUntilElementContainsText(listTitle, "My Published Posts");
    }

    public void clickTag(String tag)
    {
        mouseOver(selectTagsByTagName(tag));
        clickElement(selectTagsByTagName(tag));
        waitUntilElementContainsText(listTitle, "Blog Post List");
    }

    public void clickArchiveMonth(String month)
    {
        selectArchiveMonth(month).click();
        waitUntilElementContainsText(pageTitle, "Posts for Month " + month);
    }

    public BlogPostViewPage readPost(String postTitle)
    {
        log.info("Read post {}", postTitle);
        waitUntilWebElementIsDisplayedWithRetry(getBlogPostRow(postTitle));
        clickElement(getBlogPostRow(postTitle).findElement(readLabel));
        return new BlogPostViewPage(webDriver);
    }

    public boolean isBlogPostContentDisplayed(String title)
    {
        return isElementDisplayed(By.xpath(".//div[@class = 'content yuieditor']"));
    }

    public String getButtonName()
    {
        return findElement(simpleViewButton).getText();
    }

    public BlogPostViewPage clickOnThePostTitle(String title)
    {
        findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']")).click();
        return new BlogPostViewPage(webDriver);
    }

    public EditBlogPostPage clickEditButton(String title)
    {
        selectBlogPostWithTitle(title).findElement(editButton).click();
        return new EditBlogPostPage(webDriver);
    }

    public CreateBlogPostPage clickNewPostButton()
    {
        clickElement(newPostButton);
        return new CreateBlogPostPage(webDriver);
    }

    public void clickDeleteButton(String title)
    {
        selectBlogPostWithTitle(title).findElement(By.xpath("//div[@class = 'onDeleteBlogPost']//span[text()='Delete']")).click();
    }

    public BlogPostListPage assertButtonTextEqualsTo(String expectedButtonText)
    {
        log.info("Assert button text equals to {}", expectedButtonText);
        assertEquals(getElementText(simpleViewButton), expectedButtonText,
            String.format("Button text not equals %s ", expectedButtonText));
        return this;
    }
}