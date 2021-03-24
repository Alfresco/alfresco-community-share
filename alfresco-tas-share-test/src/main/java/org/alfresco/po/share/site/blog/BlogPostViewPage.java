package org.alfresco.po.share.site.blog;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class BlogPostViewPage extends SiteCommon<BlogPostViewPage>
{
    public final By commentText = By.cssSelector("div[class ='comment-content'] p");
    public final By noCommentsText = By.xpath("//tbody[@class = 'yui-dt-message']//div[@class = 'yui-dt-liner']");
    private final By blogPostTitle = By.cssSelector("div[id*='_blog-postview'] div.nodeTitle>a");
    private final By blogPostContent = By.cssSelector("div[id*='_blog-postview'] div.content");
    private final By blogPostAuthor = By.cssSelector(".published .nodeAttrValue>a");
    private final By blogPostNote = By.cssSelector(".nodeTitle .nodeStatus");
    private final By blogPostListButton = By.cssSelector("div[id*='_blog-postview'] .backLink>a");
    private final By editButton = By.cssSelector(".onEditBlogPost>a");
    private final By deleteButton = By.cssSelector(".onDeleteBlogPost>a");
    private final By addCommentButton = By.cssSelector(".onAddCommentClick button");
    private final By commentAuthorName = By.xpath("//span[@class = 'info']/a");
    private final By editCommentButton = By.xpath("//a[@title='Edit Comment']");
    private final By deleteCommentButton = By.xpath("//a[@title = 'Delete Comment']");
    private final By newPostButton = By.cssSelector("button[id$='_default-create-button-button']");

    @FindAll (@FindBy (css = ".tag>a"))
    private List<WebElement> blogTags;

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

    public String getBlogPostContent()
    {
        return getElementText(blogPostContent);
    }

    public String getBlogPostAuthor()
    {
        return getElementText(blogPostAuthor);
    }

    public String getBlogPostNote()
    {
        return getElementText(blogPostNote);
    }

    public BlogPostListPage navigateBackToBlogList()
    {
        log.info("Navigate back to blog list");
        clickElement(blogPostListButton);
        return new BlogPostListPage(webDriver);
    }

    public CreateBlogPostPage clickNewPostButton()
    {
        clickElement(newPostButton);
        return new CreateBlogPostPage(webDriver);
    }

    public EditBlogPostPage clickEditButton()
    {
        clickElement(editButton);
        return new EditBlogPostPage(webDriver);
    }

    public DeleteDialog clickDeleteButton()
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

    private WebElement selectComment(String user)
    {
        return findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec ')]//a[text() = '" + user + "']/../.."));
    }

    public List<String> getBlogPostTags()
    {
        List<String> tagsList = new ArrayList<>();
        for (WebElement tag : blogTags)
        {
            tagsList.add(tag.getText());
        }
        return tagsList;
    }

    public String commentAuthorName(String user)
    {
        return getElementText(selectComment(user).findElement(commentAuthorName));
    }

    public String getCommentText(String user)
    {
        return getElementText(selectComment(user).findElement(commentText));
    }

    public void clickEditComment(String user)
    {
        mouseOver(selectComment(user));
        clickElement(editCommentButton);
    }

    public DeleteDialog clickDeleteComment(String user)
    {
        mouseOver(selectComment(user));
        clickElement(deleteCommentButton);
        return new DeleteDialog(webDriver);
    }

    public String getNoCommentsText()
    {
        return getElementText(noCommentsText);
    }
}