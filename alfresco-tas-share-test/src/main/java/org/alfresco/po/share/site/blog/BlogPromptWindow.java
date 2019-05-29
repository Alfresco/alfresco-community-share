package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class BlogPromptWindow extends SiteCommon<BlogPromptWindow>
{
    @Autowired
    TinyMceEditor tinyMceEditor;

    @RenderWebElement
    @FindBy (xpath = "//div[@class = 'comment-form']//h2[text()='Add Your Comment...']")
    private WebElement addCommentBoxLabel;

    private By addCommentButton = By.xpath("//button[contains(@id, '_default-add-submit-button')]");

    private By cancelButtonCommentWindow = By.xpath("//button[contains(@id, '_default-add-cancel')]");

    private By saveButtonEditCommentWindow = By.xpath("//button[text()='Save']");

    private By cancelButtonEditCommentWindow = By.xpath("//button[text()='Cancel']");

    private By editCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']");

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
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
     * Method to get the Add Comment box label
     *
     * @return
     */
    public String getAddCommentLable()
    {
        return addCommentBoxLabel.getText();
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
     *
     * @param comment
     */
    public void testEditComment(String comment)
    {
        tinyMceEditor.setText(comment);
    }
}
