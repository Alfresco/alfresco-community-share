package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class AddCategoryDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "div[id*=userInput] input[id*=alf-id]")
    private WebElement addCategoryNameInput;

    @FindBy (css = "div[id*=userInput] span.button-group span.default button")
    private WebElement addCategoryNameOKButton;

    @FindBy (css = "div[id*=userInput] span.button-group span:not([class*=default]) button")
    private WebElement addCategoryNameCancelButton;

    @Autowired
    private CategoryManagerPage categoryManagerPage;

    public CategoryManagerPage addCategory(String categoryName)
    {
        browser.waitUntilElementVisible(addCategoryNameInput);
        addCategoryNameInput.sendKeys(categoryName);
        getBrowser().waitUntilElementClickable(addCategoryNameOKButton);
        addCategoryNameOKButton.click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("div.bd span.message"));
        return (CategoryManagerPage) categoryManagerPage.renderedPage();
    }
}
