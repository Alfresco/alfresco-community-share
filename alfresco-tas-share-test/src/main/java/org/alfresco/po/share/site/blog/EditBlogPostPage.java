package org.alfresco.po.share.site.blog;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EditBlogPostPage extends CreateBlogPostPage
{
    public By editBlogPageTitle = By.xpath("//div[@class = 'page-form-header']//h1[text() = 'Edit Blog Post']");

    public EditBlogPostPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void editTitle(String editedTitle)
    {
        waitUntilElementIsVisible(titleField);
        clearAndType(titleField, editedTitle);
    }

    public EditBlogPostPage setContent(String blogPostContentText)
    {
        switchTo().frame(findElement(By.xpath("//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']//iframe")));
        WebElement element = findElement(By.id("tinymce"));
        element.clear();
        element.sendKeys(blogPostContentText);
        switchTo().defaultContent();
        return this;
    }

    public BlogPostViewPage clickUpdateButton()
    {
        clickElement(saveAsDraftButton);
        return new BlogPostViewPage(webDriver);
    }

    public String getEditBlogPostPageTitle()
    {
        return findElement(editBlogPageTitle).getText();
    }
}
