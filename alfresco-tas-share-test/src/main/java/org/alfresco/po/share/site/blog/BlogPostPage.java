package org.alfresco.po.share.site.blog;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class BlogPostPage extends SiteCommon<BlogPostPage>
{
    @Autowired
    BlogPostListPage blogPostListPage;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postview", getCurrentSiteName());
    }

    @RenderWebElement
    @FindBy(css = "div[id*='_blog-postview'] div.nodeTitle>a")
    private WebElement blogPostTitle;

    @RenderWebElement
    @FindBy(css = "div[id*='_blog-postview'] div.content")
    private WebElement blogPostContent;

    @FindBy(css = "div[id*='_blog-postview'] .backLink>a")
    private WebElement blogPostListButton;

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
     * Method to click the blog post list button
     * 
     * @return
     */
    public BlogPostListPage clickBlogPostListButton()
    {
        blogPostListButton.click();
        return (BlogPostListPage) blogPostListPage.renderedPage();
    }
}
