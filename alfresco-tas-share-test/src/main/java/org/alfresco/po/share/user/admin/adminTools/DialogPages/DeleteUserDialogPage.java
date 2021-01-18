package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DeleteUserDialogPage extends BaseDialogComponent
{
    private final By deleteUserWindow = By.id("deleteDialog_c");
    private final By deleteUserText = By.cssSelector("div[id='deleteDialog_c'] div.yui-u");
    private final By deleteUserButton = By.cssSelector(".button-group>span:nth-of-type(1) button");

    public DeleteUserDialogPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public DeleteUserDialogPage assertDeleteUserDialogIsOpened()
    {
        assertTrue(webElementInteraction.isElementDisplayed(deleteUserWindow), "Delete user dialog is displayed");
        return this;
    }

    public void clickDelete()
    {
        webElementInteraction.clickElement(deleteUserButton);
        waitUntilNotificationMessageDisappears();
    }

    public DeleteUserDialogPage assertDeleteUserDialogTextIsCorrect()
    {
        assertEquals(webElementInteraction.getElementText(deleteUserText), language.translate("deleteUser.dialog"));
        return this;
    }
}
