package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class AddCategoryDialog extends BaseDialogComponent
{
    private final By addCategoryNameInput = By.cssSelector("div[id*=userInput] input[id*=alf-id]");
    private final By addCategoryNameOKButton = By.cssSelector("div[id*=userInput] span.button-group span.default button");

    public AddCategoryDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public CategoryManagerPage addCategory(String categoryName)
    {
        clearAndType(addCategoryNameInput, categoryName);
        WebElement addButton = waitUntilElementIsVisible(addCategoryNameOKButton);
        mouseOver(addButton);
        clickElement(addButton, 2000);
        waitUntilNotificationMessageDisappears();
        return new CategoryManagerPage(webDriver);
    }
}
