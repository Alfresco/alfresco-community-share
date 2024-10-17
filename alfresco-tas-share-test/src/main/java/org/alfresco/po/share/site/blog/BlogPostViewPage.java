package org.alfresco.po.share.site.blog;

import static org.alfresco.common.RetryTime.RETRY_TIME_30;
import static org.alfresco.common.Wait.WAIT_1;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class BlogPostViewPage extends SiteCommon<BlogPostViewPage>
{
    public final By noCommentsText = By.xpath("//tbody[@class = 'yui-dt-message']//div[@class = 'yui-dt-liner']");
    private final By commentText = By.cssSelector("div[class ='comment-content'] p");
    private final By blogCommentText = By.cssSelector("div[class ='comment-content']");
    private final By blogPostTitle = By.cssSelector("div[id*='_blog-postview'] div.nodeTitle>a");
    private final By blogPostContent = By.cssSelector("div[id*='_blog-postview'] div.content");
    private final By blogPostStatus = By.cssSelector(".nodeTitle .nodeStatus");
    private final By blogPostListButton = By.cssSelector("div[id*='_blog-postview'] .backLink>a");
    private final By editButton = By.cssSelector(".onEditBlogPost>a");
    private final By deleteButton = By.cssSelector(".onDeleteBlogPost>a");
    private final By addCommentButton = By.cssSelector(".onAddCommentClick button");
    private final By editCommentButton = By.xpath("//a[@title='Edit Comment']");
    private final By deleteCommentButton = By.xpath("//a[@title = 'Delete Comment']");
    private final By newPostButton = By.cssSelector("button[id$='_default-create-button-button']");
    private final By nodeTitle = By.xpath("//div[@class = 'nodeTitle']//a");
    private final By blogTitle = By.xpath("//span[@class = 'nodeTitle']//a");
    private final By blogContent =  By.xpath("//div[@class='content yuieditor']");

    private final String postRowPath = "//div[@class = 'nodeContent']//div[@class='nodeTitle']//a[text()= '%s']/../../../..";
    private final String blogPostRowPath = "//div[@class='node post']//span[@class = 'nodeTitle']//a[text()= '%s']/../../../..";

    private final String valuePath = "//div[@class='published']//span[@class='nodeAttrValue' and normalize-space() = '%s']";
    private final String labelPath = "//div[@class='published']//span[@class='nodeAttrLabel' and normalize-space() = '%s']";
    private final String labelTagPath = "//div[@class='published']//span[@class='nodeAttrLabel tagLabel' and normalize-space() = '%s']";
    private final String blogLabelTagPath = "//div[@class='nodeFooter']//span[@class='nodeAttrLabel tagLabel' and normalize-space() = '%s']";
    private final String blogValueTagPath = "(//div[@class='nodeFooter']//span[@class='nodeAttrValue' = '%s'])[3]";
    private final String valueTagPath = "//span[@class='tag']//a[normalize-space()='%s']";
    private final String userRow = "//tr[contains(@class, 'yui-dt-rec ')]//a[text() = '%s']/../..";

    public BlogPostViewPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postview", getCurrentSiteName());
    }

    public String getBlogPostTitle()
    {
        return getElementText(blogPostTitle);
    }

    public BlogPostViewPage assertBlogTitleEqualsTo(String expectedBlogTitle)
    {
        log.info("Assert blog title equals to {}", expectedBlogTitle);
        waitUntilElementIsVisible(getBlogPostRow(expectedBlogTitle).findElement(nodeTitle));

        String actualBlogTitle = getElementText(getBlogPostRow(expectedBlogTitle).findElement(nodeTitle));
        assertEquals(actualBlogTitle, expectedBlogTitle, String.format("Blog title not equals %s ", expectedBlogTitle));
        return this;
    }

    public BlogPostViewPage assertBlogPostStatus(String expectedStatus)
    {
        log.info("Assert blog post status {}", expectedStatus);
        String actualStatus = getElementText(blogPostStatus);
        assertEquals(actualStatus, expectedStatus);
        return this;
    }

    public BlogPostViewPage assertBlogAuthorPostEqualsTo(String blogTitle, String expectedAuthorLabel, String expectedAuthorValue)
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

    public BlogPostViewPage assertBlogPostTagEqualsTo(String blogTitle, String expectedTagsLabel, String expectedTagsValue)
    {
        log.info("Assert blog tags equals to {}", expectedTagsValue);
        String label = getTags(blogTitle, expectedTagsLabel, labelTagPath);
        String value = getTags(blogTitle, expectedTagsValue, valueTagPath);
        String actualAuthorValue = label.concat(value);

        assertEquals(actualAuthorValue, expectedTagsLabel.concat(expectedTagsValue),
            String.format("Blog author not equals %s ", expectedTagsValue));
        return this;
    }

    private String getTags(String blogTitle, String tagsLabel, String labelPath)
    {
        return getElementText(getBlogPostRow(blogTitle)
            .findElement(By.xpath(String.format(labelPath, tagsLabel))));
    }

    private WebElement getBlogPostRow(String blogTitle)
    {
        return waitWithRetryAndReturnWebElement(By.xpath(String.format(postRowPath, blogTitle)),
            WAIT_1.getValue(), RETRY_TIME_30.getValue());
    }

    public BlogPostViewPage assertBlogPostContentEqualsTo(String expectedContent)
    {
        log.info("Assert blog content equals to {}", expectedContent);
        String actualContent = getElementText(blogPostContent);
        assertEquals(actualContent, expectedContent);
        return this;
    }

    public String getBlogPostContent()
    {
        return getElementText(blogPostContent);
    }

    public BlogPostListPage navigateBackToBlogList()
    {
        log.info("Navigate back to blog list");
        clickElement(blogPostListButton);
        return new BlogPostListPage(webDriver);
    }

    public CreateBlogPostPage openCreateNewPostForm()
    {
        clickElement(newPostButton);
        return new CreateBlogPostPage(webDriver);
    }

    public EditBlogPostPage openEditForm(String title)
    {
        clickElement(getBlogPostRow(title).findElement(editButton));
        return new EditBlogPostPage(webDriver);
    }

    public DeleteDialog deletePost()
    {
        waitUntilElementIsVisible(deleteButton);
        clickElement(deleteButton);
        return new DeleteDialog(webDriver);
    }

    public BlogPromptWindow openCommentEditor()
    {
        log.info("Open add comment editor");
        clickElement(addCommentButton);
        return new BlogPromptWindow(webDriver);
    }

    private WebElement getCommentUserRow(String user)
    {
        return findElement(By.xpath(String.format(userRow, user)));
    }

    public BlogPostViewPage assertCommentEqualsTo(String user, String expectedComment)
    {
        log.info("Assert comment equals {}", expectedComment);
        String actualComment = getElementText(getCommentUserRow(user).findElement(commentText));
        assertEquals(actualComment, expectedComment, String.format("Comment not equals %s ", expectedComment));
        return this;
    }

    public void openEditCommentEditor(String user)
    {
        log.info("Open edit comment editor");
        mouseOver(getCommentUserRow(user));
        clickElement(editCommentButton);
    }

    public DeleteDialog deleteComment(String user)
    {
        mouseOver(getCommentUserRow(user));
        clickElement(deleteCommentButton);
        return new DeleteDialog(webDriver);
    }

    public BlogPostViewPage assertNoCommentsLabelEqualsTo(String expectedLabel)
    {
        log.info("Assert no comments label equals to {}", expectedLabel);
        String actualLabel = getElementText(noCommentsText);
        assertEquals(actualLabel, expectedLabel, String.format("Label not equals %s ", expectedLabel));
        return this;
    }

    public BlogPostViewPage assertBlogTitleEquals(String expectedBlogTitle)
    {
        log.info("Assert blog title equals to {}", expectedBlogTitle);
        String actualBlogTitle = findElement(blogTitle).getText();
        assertEquals(actualBlogTitle, expectedBlogTitle, String.format("Blog title not equals %s ", expectedBlogTitle));
        return this;
    }

    public BlogPostViewPage assertBlogPostContentEquals(String expectedContent)
    {
        log.info("Assert blog content equals to {}", expectedContent);
        String actualContent = getElementText(blogContent);
        assertEquals(actualContent, expectedContent);
        return this;
    }

    public BlogPostViewPage assertBlogPostTagEquals(String blogTitle, String expectedTagsLabel, String expectedTagsValue)
    {
        log.info("Assert blog tags equals to {}", expectedTagsValue);
        String label = getBlogTags(blogTitle, expectedTagsLabel, blogLabelTagPath);
        String value = getBlogTags(blogTitle, expectedTagsValue, blogValueTagPath);
        String actualAuthorValue = label.concat(value);

        assertEquals(actualAuthorValue, expectedTagsLabel.concat(expectedTagsValue),
            String.format("Blog author not equals %s ", expectedTagsValue));
        return this;
    }

    private String getBlogTags(String blogTitle, String tagsLabel, String labelPath)
    {
        return getElementText(getBlogPost(blogTitle)
            .findElement(By.xpath(String.format(labelPath, tagsLabel))));
    }

    private WebElement getBlogPost(String blogTitle)
    {
        return waitWithRetryAndReturnWebElement(By.xpath(String.format(blogPostRowPath, blogTitle)),
            WAIT_1.getValue(), RETRY_TIME_30.getValue());
    }

    public BlogPostViewPage assertCommentEquals(String user, String expectedComment)
    {
        log.info("Assert comment equals {}", expectedComment);
        String actualComment = getElementText(getCommentUserRow(user).findElement(blogCommentText));
        assertEquals(actualComment, expectedComment, String.format("Comment not equals %s ", expectedComment));
        return this;
    }
}