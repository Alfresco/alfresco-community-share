package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.ShareDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Mirela Tifui on 12/9/2016.
 */
@PageObject
public class DeleteUserDialogPage extends ShareDialog
{
    @RenderWebElement
    private By deleteUserWindow = By.id("deleteDialog_c");

    private By deleteUserText = By.cssSelector("div[id='deleteDialog_c'] div.yui-u");

    public boolean isDeleteUserWindowDisplayed()
    {
        browser.waitUntilElementsVisible(deleteUserWindow);
        return browser.isElementDisplayed(deleteUserWindow);
    }

    public HtmlPage clickButton(String buttonName, HtmlPage page)
    {
        browser.findElement(By.xpath("//button[text()='" + buttonName + "']" )).click();
        return page.renderedPage();
    }

    public String getDeleteUserWindowText()
    {
        return browser.findElement(deleteUserText).getText();
    }
}
