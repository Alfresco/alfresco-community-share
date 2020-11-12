package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

public class AddCategoryDialog extends ShareDialog2
{
    private CategoryManagerPage categoryManagerPage;

    @RenderWebElement
    private By addCategoryNameInput = By.cssSelector("div[id*=userInput] input[id*=alf-id]");
    private By addCategoryNameOKButton = By.cssSelector("div[id*=userInput] span.button-group span.default button");
    private By addCategoryNameCancelButton = By.cssSelector("div[id*=userInput] span.button-group span:not([class*=default]) button");

    public AddCategoryDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
        categoryManagerPage = new CategoryManagerPage(browser);
    }

    public CategoryManagerPage addCategory(String categoryName)
    {
        getBrowser().waitUntilElementVisible(addCategoryNameInput).sendKeys(categoryName);
        getBrowser().waitUntilElementClickable(addCategoryNameOKButton).click();
        waitUntilNotificationMessageDisappears();
        return (CategoryManagerPage) categoryManagerPage.renderedPage();
    }
}
