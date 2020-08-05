package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 12/9/2016.
 */
@PageObject
public class DeleteUserDialogPage extends ShareDialog
{
    @RenderWebElement
    @FindBy (id = "deleteDialog_c")
    private WebElement deleteUserWindow;

    @FindBy (css = "div[id='deleteDialog_c'] div.yui-u")
    private WebElement deleteUserText;

    @RenderWebElement
    @FindBy (css = "#yui-gen0 button")
    private WebElement deleteUserButton;

    public DeleteUserDialogPage assertDeleteUserDialogIsOpened()
    {
        Assert.assertTrue(browser.isElementDisplayed(deleteUserWindow), "Delete user dialog is displayed");
        return this;
    }

    public void clickDelete()
    {
        getBrowser().waitUntilElementClickable(deleteUserButton).click();
    }

    public DeleteUserDialogPage assertDeleteUserDialogTextIsCorrect()
    {
        Assert.assertEquals(deleteUserText.getText(), language.translate("deleteUser.dialog"));
        return this;
    }
}
