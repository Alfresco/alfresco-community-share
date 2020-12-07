package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AddCategoryDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By addCategoryNameInput = By.cssSelector("div[id*=userInput] input[id*=alf-id]");
    private final By addCategoryNameOKButton = By.cssSelector("div[id*=userInput] span.button-group span.default button");

    public AddCategoryDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
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
