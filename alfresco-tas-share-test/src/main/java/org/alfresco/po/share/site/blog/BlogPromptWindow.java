package org.alfresco.po.share.site.blog;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class BlogPromptWindow extends SiteCommon<BlogPromptWindow>
{
    private final By addCommentButton = By.xpath("//button[contains(@id, '_default-add-submit-button')]");
    private final By saveButtonEditCommentWindow = By.xpath("//button[text()='Save']");
    private final By cancelButtonEditCommentWindow = By.xpath("(//button[text()='Cancel'])[2]");
    private final By editorIframe = By.xpath("//div[@class = 'comment-form']//form[contains(@id, '_default-add-form')]//div[@class = 'mce-tinymce mce-container mce-panel']//iframe");
    private final By editor = By.id("tinymce");

    private final String commentBoxLabel = "//div[@class = 'comment-form']//h2[text()='%s']";

    public BlogPromptWindow(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "Empty relative path!";
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

    public BlogPromptWindow assertCommentBoxLabelEqualsTo(String expectedLabel)
    {
        log.info("Assert comment box label equals to {}", expectedLabel);
        String actualLabel = getElementText(By.xpath(String.format(commentBoxLabel, expectedLabel)));
        assertEquals(actualLabel, expectedLabel, String.format("Comment box label not equals %s", expectedLabel));
        return this;
    }

    public BlogPromptWindow addPostComment()
    {
        log.info("Add post comment");
        clickElement(addCommentButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public void saveEditedComment()
    {
        log.info("Save edited comment");
        clickElement(saveButtonEditCommentWindow);
        waitUntilNotificationMessageDisappears();
    }

    public void cancelEditedComment()
    {
        log.info("Save edited comment");
        clickElement(cancelButtonEditCommentWindow);
        waitUntilNotificationMessageDisappears();
    }

    public BlogPromptWindow editComment(String comment)
    {
        log.info("Edit comment {}", comment);
        TinyMceEditor tinyMceEditor = new TinyMceEditor(webDriver);
        tinyMceEditor.setText(comment);
        return this;
    }
}
