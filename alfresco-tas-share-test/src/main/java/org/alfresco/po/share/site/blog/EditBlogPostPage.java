package org.alfresco.po.share.site.blog;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class EditBlogPostPage extends SiteCommon<EditBlogPostPage>
{

    @FindBy(xpath = "//button[contains(@id , '_default-publish-button-button')]")
    private WebElement publishInternally;

    public By editBlogPageTitle = By.xpath("//div[@class = 'page-form-header']//h1[text() = 'Edit Blog Post']");

    @FindBy(css = "input[id*='_default-title']")
    private WebElement titleField;

    @FindBy(xpath = "//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']")
    private WebElement frame;

    @FindBy(xpath = "//div[@class = 'taglibrary']//input")
    private WebElement tagsField;

    private By updateButton = By.cssSelector("button[id$='_default-save-button-button']");

    private By addTagButton = By.cssSelector("button[id$='_default-add-tag-button-button']");

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method to click the Publish Internally button
     */

    public void clickPublishInternally()
    {
        publishInternally.click();
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
     * Method to send intput to the Tag field
     * 
     * @param tag
     */
    public void sendTag(String tag)
    {
        tagsField.sendKeys(tag);
    }

    /**
     * Method to click the Update button
     */
    public void clickUpdateButton()
    {
        browser.findElement(updateButton).click();
    }

    /**
     * Method to click the Add tag button
     */
    public void clickAddTagButton()
    {
        browser.findElement(addTagButton).click();
    }

}
