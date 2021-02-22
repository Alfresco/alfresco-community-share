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

    /**
     * Method to send input to the Title field
     *
     * @return
     */

    public void editTitle(String editedTitle)
    {
        waitUntilElementIsVisible(titleField);
        titleField.clear();
        titleField.sendKeys(editedTitle);
    }

    /**
     * Method to send input for the Blog content
     *
     * @param blogPostContentText
     */

    public void sendBlogPostTextInput(String blogPostContentText)
    {
        switchTo().frame(findElement(By.xpath("//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']//iframe")));
        WebElement element = findElement(By.id("tinymce"));
        element.clear();
        element.sendKeys(blogPostContentText);
        switchTo().defaultContent();
    }

    /**
     * Method to click the Update button
     */
    public BlogPostViewPage clickUpdateButton()
    {
        saveAsDraftButton.click();
        return new BlogPostViewPage(webDriver);
    }

    /**
     * Method to get the page title for the Edit Blog Post page
     *
     * @return
     */
    public String getEditBlogPostPageTitle()
    {
        return findElement(editBlogPageTitle).getText();
    }
}
