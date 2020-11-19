package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AddCategoryDialog extends ShareDialog2
{
    @RenderWebElement
    private By addCategoryNameInput = By.cssSelector("div[id*=userInput] input[id*=alf-id]");
    private By addCategoryNameOKButton = By.cssSelector("div[id*=userInput] span.button-group span.default button");
    private By addCategoryNameCancelButton = By.cssSelector("div[id*=userInput] span.button-group span:not([class*=default]) button");

    public AddCategoryDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public CategoryManagerPage addCategory(String categoryName)
    {
        clearAndType(addCategoryNameInput, categoryName);
        WebElement addButton = getBrowser().waitUntilElementVisible(addCategoryNameOKButton);
        getBrowser().mouseOver(addButton);
        getBrowser().waitUntilElementClickable(addButton).click();
        waitUntilNotificationMessageDisappears();
        return (CategoryManagerPage) new CategoryManagerPage(browser).renderedPage();
    }
}
