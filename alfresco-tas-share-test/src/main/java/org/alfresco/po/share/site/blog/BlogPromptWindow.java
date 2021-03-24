package org.alfresco.po.share.site.blog;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertEquals;

@Slf4j
public class BlogPromptWindow extends SiteCommon<BlogPromptWindow>
{
    private TinyMceEditor tinyMceEditor;

    private final By addCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Add Your Comment...']");
    private final By addCommentButton = By.xpath("//button[contains(@id, '_default-add-submit-button')]");
    private final By cancelButtonCommentWindow = By.xpath("//button[contains(@id, '_default-add-cancel')]");
    private final By saveButtonEditCommentWindow = By.xpath("//button[text()='Save']");
    private final By cancelButtonEditCommentWindow = By.xpath("//button[text()='Cancel']");
    private final By editCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']");
    private final By editorIframe = By.xpath("//div[@class = 'comment-form']//form[contains(@id, '_default-add-form')]//div[@class = 'mce-tinymce mce-container mce-panel']//iframe");
    private final By editor = By.id("tinymce");

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

    public BlogPromptWindow writePostComment(String postComment)
    {
        log.info("Write post comment {}", postComment);
        switchTo().frame(findElement(editorIframe));
        WebElement addCommentEditor = findElement(editor);
        clearAndType(addCommentEditor, postComment);
        switchTo().defaultContent();
        return this;
    }

    public BlogPromptWindow assertAddCommentLabelEqualsTo(String expectedLabel)
    {
        log.info("Assert Add comment label equals to {}", expectedLabel);
        String actualLabel = getElementText(addCommentBoxLabel);
        assertEquals(actualLabel, expectedLabel, String.format("Add comment label not equals %s", expectedLabel));
        return this;
    }

    public BlogPromptWindow addPostComment()
    {
        log.info("Add post comment");
        clickElement(addCommentButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public void clickCancelOnAddCommentWindow()
    {
        findElement(cancelButtonCommentWindow).click();
    }

    public void clickSaveButtonOnEditComment()
    {
        findElement(saveButtonEditCommentWindow).click();
    }


    public void clickCancelButtonOnEditComment()
    {
        findElement(cancelButtonEditCommentWindow).click();
    }

    public boolean isEditCommentDisplayed()
    {
        return isElementDisplayed(By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']"));
    }

    public String getEditCommentBoxLabel()
    {
        return findElement(editCommentBoxLabel).getText();
    }

    public void testEditComment(String comment)
    {
        tinyMceEditor.setText(comment);
    }
}
