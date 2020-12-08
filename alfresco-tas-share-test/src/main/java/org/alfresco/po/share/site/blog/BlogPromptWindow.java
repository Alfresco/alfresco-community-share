package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class BlogPromptWindow extends SiteCommon<BlogPromptWindow>
{
    //@Autowired
    TinyMceEditor tinyMceEditor;

    @RenderWebElement
    private final By addCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Add Your Comment...']");
    private final By addCommentButton = By.xpath("//button[contains(@id, '_default-add-submit-button')]");
    private final By cancelButtonCommentWindow = By.xpath("//button[contains(@id, '_default-add-cancel')]");
    private final By saveButtonEditCommentWindow = By.xpath("//button[text()='Save']");
    private final By cancelButtonEditCommentWindow = By.xpath("//button[text()='Cancel']");
    private final By editCommentBoxLabel = By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']");

    public BlogPromptWindow(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void writeComment(String blogComment)
    {
        getBrowser().switchTo().frame(getBrowser().findElement(By.xpath(
            "//div[@class = 'comment-form']//form[contains(@id, '_default-add-form')]//div[@class = 'mce-tinymce mce-container mce-panel']//iframe")));
        WebElement element = getBrowser().findElement(By.id("tinymce"));
        element.clear();
        element.sendKeys(blogComment);
        getBrowser().switchTo().defaultContent();
    }

    public String getAddCommentLable()
    {
        return getElementText(addCommentBoxLabel);
    }

    public void clickAddCommentButton()
    {
        getBrowser().findElement(addCommentButton).click();
    }

    public void clickCancelOnAddCommentWindow()
    {
        getBrowser().findElement(cancelButtonCommentWindow).click();
    }

    public void clickSaveButtonOnEditComment()
    {
        getBrowser().findElement(saveButtonEditCommentWindow).click();
    }


    public void clickCancelButtonOnEditComment()
    {
        getBrowser().findElement(cancelButtonEditCommentWindow).click();
    }

    public boolean isEditCommentDisplayed()
    {
        return getBrowser().isElementDisplayed(By.xpath("//div[@class = 'comment-form']//h2[text()='Edit Comment...']"));
    }

    public String getEditCommentBoxLabel()
    {
        return getBrowser().findElement(editCommentBoxLabel).getText();
    }

    public void testEditComment(String comment)
    {
        tinyMceEditor.setText(comment);
    }
}
