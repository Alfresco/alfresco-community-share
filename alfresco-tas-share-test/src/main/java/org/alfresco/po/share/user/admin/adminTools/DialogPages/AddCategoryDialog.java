package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.alfresco.common.Wait.WAIT_10;
import static org.alfresco.utility.Utility.waitToLoopTime;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
        webElementInteraction.clearAndType(addCategoryNameInput, categoryName);
        WebElement addButton = webElementInteraction.waitUntilElementIsVisible(addCategoryNameOKButton);
        webElementInteraction.mouseOver(addButton);
        webElementInteraction.clickElement(addButton, 2000);
        waitUntilNotificationMessageDisappears();
        return new CategoryManagerPage(webDriver);
    }

    //todo: temporary workaround when CRUD operations on category return 500 status code
    private void refreshPageAndClickIfUnableToPerformCrudActionsOnCategory(String categoryName)
    {
        int counter = 0;
        while (webElementInteraction.getElementText(notificationMessageLocator).contains("Category could not be added.") && counter < WAIT_10.getValue())
        {
            LOG.error("500 status code is returned when tried to delete/edit category");
            waitToLoopTime(1);
            webElementInteraction.clearAndType(addCategoryNameInput, categoryName);
            WebElement addButton = webElementInteraction.waitUntilElementIsVisible(addCategoryNameOKButton);
            webElementInteraction.mouseOver(addButton);
            webElementInteraction.clickElement(addButton, 2000);
            counter++;
        }
    }
}
