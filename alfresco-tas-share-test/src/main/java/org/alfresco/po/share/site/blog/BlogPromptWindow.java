package org.alfresco.po.share.site.blog;

import org.alfresco.po.TinyMce.TinyMceEditor;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class BlogPromptWindow extends SiteCommon<BlogPromptWindow>
{
    @Autowired
    TinyMceEditor tinyMceEditor;

    private By deleteButton = By.xpath("//div[@class = 'ft']//button[text()='Delete']");

    private By cancelButton = By.xpath("//div[@class = 'ft']//button[text()='Cancel']");

    private By textOnThePrompt = By.xpath("//div[@class = 'bd']");

    private By addCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Add Your Comment...']");

    private By addCommentButton = By.xpath("//button[contains(@id, '_default-add-submit-button')]");

    private By cancelButtonCommentWindow = By.xpath("//button[contains(@id, '_default-add-cancel')]");

    private By saveButtonEditCommentWindow = By.xpath("//button[text()='Save']");

    private By cancelButtonEditCommentWindow = By.xpath("//button[text()='Cancel']");

    private By editCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']");
    
    private By deleteCommentButton = By.xpath("//button[text()='Delete']");
    
    private By cancelButtonDeleteComment = By.xpath("//span[contains(@class, 'yui-push-button default')]//button[text()='Cancel']");

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method to click on the Delete button on the Delete Blog Post prompt window
     */
    public void clickDeleteButtonOnDeleteBlogPost()
    {
        browser.findElement(deleteButton).click();
    }

    /**
     * Method to click on the Cancel button on the Delete Blog Post prompt window
     */
    public void clickCancelButtonOnDeleteBlogPost()
    {
        browser.findElement(cancelButton).click();
    }

    /**
     * Method to get the text displayed on the Delete Blog Post prompt window
     * 
     * @return
     */
    public String getTextDisplayedOnThePromptWindow()
    {
        return browser.findElement(textOnThePrompt).getText();
    }

    /**
     * Method to write into the Comment text field
     */

    public void writeComment(String blogComment)
    {
        browser.switchTo().frame(browser.findElement(By.xpath(
                "//div[@class = 'comment-form']//form[contains(@id, '_default-add-form')]//div[@class = 'mce-tinymce mce-container mce-panel']//iframe")));
        WebElement element = browser.findElement(By.id("tinymce"));
        element.clear();
        element.sendKeys(blogComment);
        browser.switchTo().defaultContent();
    }

    /**
     * Method to get the Add Comment box lable
     * 
     * @return
     */
    public String getAddCommentLable()
    {
        return browser.findElement(addCommentBoxLabel).getText();
    }

    /**
     * Method to click Add Comment button
     */

    public void clickAddCommentButton()
    {
        browser.findElement(addCommentButton).click();
    }

    /**
     * Method to click Cancel button on the Add Comment
     */

    public void clickCancelOnAddCommentWindow()
    {
        browser.findElement(cancelButtonCommentWindow).click();
    }

    /**
     * Method to click the Save button on Edit Comment window
     */

    public void clickSaveButtonOnEditComment()
    {
        browser.findElement(saveButtonEditCommentWindow).click();
    }

    /**
     * Method to click the Cancel button on the Edit comment window
     */
    public void clickCancelButtonOnEditComment()
    {
        browser.findElement(cancelButtonEditCommentWindow).click();
    }

    /**
     * Method to check if the Edit comment box is displayed
     */

    public boolean isEditCommentDisplayed()
    {
        return browser.isElementDisplayed(By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']"));
    }

    /**
     * Method to get the edit comment box label
     */
    public String getEditCommentBoxLabel()
    {
        return browser.findElement(editCommentBoxLabel).getText();
    }

    /**
     * Method to edit comment
     * @param comment
     */
    public void testEditComment(String comment)
    {
        tinyMceEditor.setText(comment);
    }
    
    /**
     * Method to click the Delete button on the Delete Comment prompt
     */
    public void clickDeleteButton()
    {
        browser.findElement(deleteCommentButton).click();
    }
    
    /**
     * Method to click the Cancel button on the Delete Comment prompt
     */
    
    public void clickCancelButton()
    {
        browser.findElement(cancelButtonDeleteComment).click();
    }
}
