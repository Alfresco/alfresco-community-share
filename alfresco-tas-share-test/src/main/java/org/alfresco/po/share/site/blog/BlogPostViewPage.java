package org.alfresco.po.share.site.blog;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class BlogPostViewPage extends SiteCommon<BlogPostViewPage>
{
    
    public final By commentText = By.cssSelector("div[class ='comment-content'] p");
    @FindBy (xpath = "//tbody[@class = 'yui-dt-message']//div[@class = 'yui-dt-liner']")
    public WebElement noCommentsText;

    @FindBy (css = "div[id*='_blog-postview'] div.nodeTitle>a")
    private WebElement blogPostTitle;

    @FindBy (css = "div[id*='_blog-postview'] div.content")
    private WebElement blogPostContent;
    @FindBy (css = ".published .nodeAttrValue>a")
    private WebElement blogPostAuthor;
    @FindBy (css = ".nodeTitle .nodeStatus")
    private WebElement blogPostNote;
    @FindBy (css = "div[id*='_blog-postview'] .backLink>a")
    private WebElement blogPostListButton;
    @FindBy (css = "button[id$='_default-create-button-button']")
    private WebElement newPostButton;
    @FindBy (css = ".onEditBlogPost>a")
    private WebElement editButton;
    @FindBy (css = ".onDeleteBlogPost>a")
    private WebElement deleteButton;
    @FindBy (css = ".onAddCommentClick button")
    private WebElement addCommentButton;
    @FindAll (@FindBy (css = ".tag>a"))
    private List<WebElement> blogTags;
    private By commentAuthorName = By.xpath("//span[@class = 'info']/a");
    private By editCommentButton = By.xpath("//a[@title='Edit Comment']");
    private By deleteCommentButton = By.xpath("//a[@title = 'Delete Comment']");

    public BlogPostViewPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postview", getCurrentSiteName());
    }

    /**
     * Method to get the blog post title
     *
     * @return
     */
    public String getBlogPostTitle()
    {
        return blogPostTitle.getText();
    }

    /**
     * Method to get the blog post content
     *
     * @return
     */
    public String getBlogPostContent()
    {
        return blogPostContent.getText();
    }

    /**
     * Method to get the blog post Author name
     *
     * @return
     */

    public String getBlogPostAuthor()
    {
        return blogPostAuthor.getText();
    }

    /**
     * Method to get the blog post (Draft) note for the blog post that needs to be checked
     *
     * @return
     */
    public String getBlogPostNote()
    {
        return blogPostNote.getText();
    }

    /**
     * Method to click the blog post list button
     *
     * @return
     */
    public BlogPostListPage clickBlogPostListButton()
    {
        blogPostListButton.click();
        return new BlogPostListPage(webDriver);
    }

    /**
     * Method to click the New Post button
     */
    public CreateBlogPostPage clickNewPostButton()
    {
        newPostButton.click();
        return new CreateBlogPostPage(webDriver);
    }

    /**
     * Method to click the Edit button
     */
    public EditBlogPostPage clickEditButton()
    {
        editButton.click();
        return new EditBlogPostPage(webDriver);
    }

    /**
     * Method to click the Delete button for the selected blog post. The blog post is selected by title
     */

    public DeleteDialog clickDeleteButton()
    {
        webElementInteraction.waitUntilElementIsVisible(deleteButton);
        webElementInteraction.clickElement(deleteButton);
        return new DeleteDialog(webDriver);
    }

    /**
     * Method to click the Add Comment button
     */

    public BlogPromptWindow clickAddCommentButton()
    {
        addCommentButton.click();
        return new BlogPromptWindow(webDriver);
    }

    private WebElement selectComment(String user)
    {
        return webElementInteraction.findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec ')]//a[text() = '" + user + "']/../.."));
    }

    /**
     * Method to get the blog post Tags
     *
     * @return
     */
    public List<String> getBlogPostTags()
    {
        List<String> tagsList = new ArrayList<>();
        for (WebElement tag : blogTags)
        {
            tagsList.add(tag.getText());
        }
        return tagsList;
    }


    /**
     * Method to get the Comment author name
     */

    public String commentAuthorName(String user)
    {
        return selectComment(user).findElement(commentAuthorName).getText();
    }

    /**
     * Method to get the comment text
     */

    public String getCommentText(String user)
    {
        return selectComment(user).findElement(commentText).getText();
    }

    /**
     * Method to click on the Edit button for comment
     */
    public void clickEditComment(String user)
    {
        webElementInteraction.mouseOver(selectComment(user));
        webElementInteraction.clickElement(editCommentButton);
    }

    /**
     * Method to click on the Delete button for comment
     */
    public DeleteDialog clickDeleteComment(String user)
    {
        webElementInteraction.mouseOver(selectComment(user));
        webElementInteraction.clickElement(deleteCommentButton);
        return new DeleteDialog(webDriver);
    }

    /**
     * Method to get the text displayed for comments when no comments are available
     */

    public String getNoCommentsText()
    {
        return noCommentsText.getText();
    }
}