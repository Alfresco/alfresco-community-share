package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;

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
