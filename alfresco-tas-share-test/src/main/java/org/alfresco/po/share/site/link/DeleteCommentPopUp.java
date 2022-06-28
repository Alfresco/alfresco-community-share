package org.alfresco.po.share.site.link;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class DeleteCommentPopUp extends ShareDialog
{
    @RenderWebElement
    @FindBy (xpath = "//button[contains(text(), 'Delete')]")
    private WebElement deleteButton;

    @RenderWebElement
    @FindBy (xpath = "//button[contains(text(), 'Cancel')]")
    private WebElement cancelDeleteButton;

    @FindBy (css = "[id=prompt] [class=bd]")
    private WebElement deleteMessage;

    public void clickDelete()
    {
        deleteButton.click();
        browser.waitInSeconds(3);
    }

    public void clickCancel()
    {
        cancelDeleteButton.click();
    }

    public String getDeleteMessage()
    {
        return deleteMessage.getText();
    }
}
