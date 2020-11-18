package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.testng.Assert;

public class DeleteUserDialogPage extends ShareDialog2
{
    @RenderWebElement
    private By deleteUserWindow = By.id("deleteDialog_c");
    private By deleteUserText = By.cssSelector("div[id='deleteDialog_c'] div.yui-u");
    @RenderWebElement
    private By deleteUserButton = By.cssSelector("#yui-gen0 button");

    public DeleteUserDialogPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public DeleteUserDialogPage assertDeleteUserDialogIsOpened()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(deleteUserWindow), "Delete user dialog is displayed");
        return this;
    }

    public void clickDelete()
    {
        getBrowser().waitUntilElementClickable(deleteUserButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public DeleteUserDialogPage assertDeleteUserDialogTextIsCorrect()
    {
        Assert.assertEquals(getElementText(deleteUserText), language.translate("deleteUser.dialog"));
        return this;
    }
}
