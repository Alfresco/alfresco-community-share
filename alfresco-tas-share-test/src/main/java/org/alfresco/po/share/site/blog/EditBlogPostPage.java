package org.alfresco.po.share.site.blog;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EditBlogPostPage extends CreateBlogPostPage
{
    private final By editBlogPageTitle = By.xpath("//div[@class = 'page-form-header']//h1[text() = 'Edit Blog Post']");
    private final By iframe = By.xpath("//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']//iframe");
    private final By tinyMce = By.id("tinymce");

    public EditBlogPostPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public EditBlogPostPage setTitle(String editedTitle)
    {
        waitUntilElementIsVisible(titleField);
        clearAndType(titleField, editedTitle);
        return this;
    }

    @Override
    public EditBlogPostPage setContent(String blogPostContentText)
    {
        switchTo().frame(findElement(iframe));

        WebElement element = findElement(tinyMce);
        clearAndType(element, blogPostContentText);

        switchTo().defaultContent();
        return this;
    }

    public BlogPostViewPage updatePost()
    {
        clickElement(saveAsDraftButton);
        waitUntilNotificationMessageDisappears();
        return new BlogPostViewPage(webDriver);
    }

    public String getEditBlogPostPageTitle()
    {
        return getElementText(editBlogPageTitle);
    }
}
