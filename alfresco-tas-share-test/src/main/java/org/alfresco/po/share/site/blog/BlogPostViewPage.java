package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class BlogPostViewPage extends SiteCommon<BlogPostViewPage>
{
    @Autowired
    CreateBlogPostPage createBlogPostPage;

    @RenderWebElement
    @FindBy(css = "div[id$='_default-postview']")
    private WebElement postView;

    public By blogPostTitle(String title)
    {
        return By.xpath("//div[@class = 'sticky-wrapper']//div[@id = 'bd']//div[@class = 'nodeTitle']//a[text() = '" + title + "']");
    }

    private WebElement blogPostDraftNote(String title)
    {
        return browser.findElement(blogPostTitle(title)).findElement(By.xpath("//div[@class= 'nodeTitle']//span"));
    }

    private WebElement blogPostAuthor(String title)
    {
        return browser.findElement(blogPostTitle(title)).findElement(By.xpath("./../..//div[@class = 'published']//span[@class = 'nodeAttrValue']/a"));
    }

    private WebElement blogPostTags(String title, String tag)
    {
        return browser.findElement(blogPostTitle(title))
                .findElement(By.xpath("./../..//div[@class = 'published']//span[@class = 'tag']/a[text() = '" + tag + "']"));
    }

    private WebElement blogPostContent(String title)
    {
        return browser.findElement(blogPostTitle(title)).findElement(By.xpath("./../..//div[@class = 'content yuieditor']"));
    }

    //@RenderWebElement
    private By newPostButton = By.cssSelector("button[id$='_default-create-button-button']");

    public By editButton = By.cssSelector("div[class='onEditBlogPost'] span");

    public WebElement selectBlogPostWithtitle(String title)
    {
        return browser.findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span//a[text() = '" + title + "']/../.."));
    }

    //@RenderWebElement
    @FindBy(css = "span.backLink a")
    private WebElement blogPostListButton;

    private By deleteButton = By.xpath("//div[@class ='onDeleteBlogPost']//span[text() = 'Delete']");

    public By addCommentButton = By.xpath("//div[@class = 'comments-list-actions']//button[text()='Add Comment']");

    private WebElement selectComment(String user)
    {
        return browser.findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec ')]//a[text() = '" + user + "']/../.."));
    }

    private By commentAuthorName = By.xpath("//span[@class = 'info']/a");

    public By commentText = By.cssSelector("div[class ='comment-content'] p");
    
    private By editCommentButton = By.xpath("//a[@title='Edit Comment']");

    private By deleteCommentButton = By.xpath("//a[@title = 'Delete Comment']");
    
    @FindBy(xpath ="//tbody[@class = 'yui-dt-message']//div[@class = 'yui-dt-liner']")
    public WebElement noCommentsText;
   
    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postview", getCurrentSiteName());
    }

    /**
     * Method to get the blog post title text for the blog post that needs to be checked. The blog post is identified by title.
     * 
     * @param title
     * @return
     */
    public String getBlogPostTitle(String title)
    {
        browser.waitUntilElementsVisible(blogPostTitle(title));
        return browser.findElement(blogPostTitle(title)).getText();
    }

    /**
     * Method to get the blog post (Draft) note for the blog post that needs to be checked. The blog post is identified by title.
     * 
     * @param title
     * @return
     */
    public String getBlogPostNote(String title)
    {
        browser.waitUntilElementVisible(blogPostDraftNote(title));
        return blogPostDraftNote(title).getText();
    }

    /**
     * Method to get the blog post Author name for the blog post that needs to be checked. The blog post is identified by title.
     * 
     * @param title
     * @return
     */

    public String getBlogPostAuthor(String title)
    {
        return blogPostAuthor(title).getText();
    }

    /**
     * Method to get the blog post Tags. The blog post is identified by title.
     * 
     * @param title
     * @return
     */
    public String getBlogPostTags(String title, String tag)
    {
        return blogPostTags(title, tag).getText();
    }

    /**
     * Method to get the blog post content. The blog post is identified by title.
     * 
     * @param title
     * @return
     */
    public String getBlogPostContent(String title)
    {
        browser.waitUntilElementVisible(blogPostContent(title));
        return blogPostContent(title).getText();
    }

    /**
     * Method to click the New Post button
     */
    public CreateBlogPostPage clickNewPostButton()
    {
        browser.findElement(newPostButton).click();
        return (CreateBlogPostPage) createBlogPostPage.renderedPage();
    }

    /**
     * Method to click the Edit button
     */
    public void clickEditButton()
    {
        browser.findElement(editButton).click();
    }

    /**
     * Method to click the Blog Post List button
     */
    public void clickBlogPostListButton()
    {
        blogPostListButton.click();
    }

    /**
     * Method to click the Delete button for the selected blog post. The blog post is selected by title
     * 
     * @param title
     */

    public void clickDeleteButton(String title)
    {
        browser.findElement(blogPostTitle(title)).findElement(deleteButton).click();
    }

    /**
     * Method to click the Add Comment button
     */

    public void clickAddCommentButton()
    {
        browser.findElement(addCommentButton).click();
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
       browser.mouseOver(selectComment(user));
       browser.findElement(editCommentButton).click();
    }
    
    /**
     * Method to click on the Delete button for comment
     */
    public void clickDeleteComment(String user)
    {
        browser.mouseOver(selectComment(user));
        browser.findElement(deleteCommentButton).click();
    }
    
    /**
     * Method to get the text displayed for comments when no comments are available
     */
    
    public String getNoCommentsText()
    {
        return noCommentsText.getText();
    }
}