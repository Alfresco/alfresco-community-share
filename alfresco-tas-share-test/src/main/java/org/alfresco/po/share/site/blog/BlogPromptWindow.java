package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BlogPromptWindow extends SiteCommon<BlogPromptWindow>
{
    private TinyMceEditor tinyMceEditor;

    @RenderWebElement
    private final By addCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Add Your Comment...']");
    private final By addCommentButton = By.xpath("//button[contains(@id, '_default-add-submit-button')]");
    private final By cancelButtonCommentWindow = By.xpath("//button[contains(@id, '_default-add-cancel')]");
    private final By saveButtonEditCommentWindow = By.xpath("//button[text()='Save']");
    private final By cancelButtonEditCommentWindow = By.xpath("//button[text()='Cancel']");
    private final By editCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']");

    public BlogPromptWindow(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void writeComment(String blogComment)
    {
        webElementInteraction.switchTo().frame(webElementInteraction.findElement(By.xpath(
            "//div[@class = 'comment-form']//form[contains(@id, '_default-add-form')]//div[@class = 'mce-tinymce mce-container mce-panel']//iframe")));
        WebElement element = webElementInteraction.findElement(By.id("tinymce"));
        element.clear();
        element.sendKeys(blogComment);
        webElementInteraction.switchTo().defaultContent();
    }

    public String getAddCommentLable()
    {
        return webElementInteraction.getElementText(addCommentBoxLabel);
    }

    public void clickAddCommentButton()
    {
        webElementInteraction.findElement(addCommentButton).click();
    }

    public void clickCancelOnAddCommentWindow()
    {
        webElementInteraction.findElement(cancelButtonCommentWindow).click();
    }

    public void clickSaveButtonOnEditComment()
    {
        webElementInteraction.findElement(saveButtonEditCommentWindow).click();
    }


    public void clickCancelButtonOnEditComment()
    {
        webElementInteraction.findElement(cancelButtonEditCommentWindow).click();
    }

    public boolean isEditCommentDisplayed()
    {
        return webElementInteraction.isElementDisplayed(By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']"));
    }

    public String getEditCommentBoxLabel()
    {
        return webElementInteraction.findElement(editCommentBoxLabel).getText();
    }

    public void testEditComment(String comment)
    {
        tinyMceEditor.setText(comment);
    }
}
