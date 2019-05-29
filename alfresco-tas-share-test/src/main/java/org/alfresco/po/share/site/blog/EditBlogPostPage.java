package org.alfresco.po.share.site.blog;

import org.alfresco.utility.web.annotation.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class EditBlogPostPage extends CreateBlogPostPage
{
    @Autowired
    BlogPostViewPage blogPostViewPage;

    public By editBlogPageTitle = By.xpath("//div[@class = 'page-form-header']//h1[text() = 'Edit Blog Post']");

    /**
     * Method to send input to the Title field
     *
     * @return
     */

    public void editTitle(String editedTitle)
    {
        browser.waitUntilElementVisible(titleField);
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
        browser.switchTo().frame(browser.findElement(By.xpath("//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']//iframe")));
        WebElement element = browser.findElement(By.id("tinymce"));
        element.clear();
        element.sendKeys(blogPostContentText);
        browser.switchTo().defaultContent();
    }

    /**
     * Method to click the Update button
     */
    public BlogPostViewPage clickUpdateButton()
    {
        saveAsDraftButton.click();
        return (BlogPostViewPage) blogPostViewPage.renderedPage();
    }

    /**
     * Method to get the page title for the Edit Blog Post page
     *
     * @return
     */
    public String getEditBlogPostPageTitle()
    {
        return browser.findElement(editBlogPageTitle).getText();
    }

}
