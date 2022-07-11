package org.alfresco.po.share.site.dataLists;

import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;

@PageObject
public class NoListItemSelectedContent extends Content
{

    protected By selectItemsButtonSelector = By.cssSelector("button[id*='selectedItems']");
    protected By newItemButtonSelector = By.cssSelector("button[id*='newRowButton']");
    protected By selectButtonSelector = By.cssSelector("button[id*='itemSelect']");
    protected By duplicateItemsSelector = By.cssSelector("span[class='onActionDuplicate']");
    protected By deleteItemsSelector = By.cssSelector("span[class='onActionDelete']");
    protected By deselectAllItemsSelector = By.cssSelector("span[class='onActionDeselectAll']");
    protected By confirmDeleteButtonSelector = By.cssSelector("span[class*='alf-primary-button'] button");
    protected By cancelDeleteButtonSelector = By.cssSelector("span[class*='yui-push-button default'] button");

    public NoListItemSelectedContent()
    {
    }

    public boolean isNewItemButtonDisplayed()
    {
        return browser.isElementDisplayed(browser.findElement(newItemButtonSelector));
    }

    @Override
    public boolean allFilterOptionsAreDisplayed()
    {
        return false;
    }

    public boolean isSelectButtonDisplayed()
    {
        return browser.isElementDisplayed(browser.findElement(selectButtonSelector));
    }

    public boolean isSelectItemsButtonDisplayed()
    {
        return browser.findElement(selectItemsButtonSelector).isDisplayed();
    }

    public boolean isSelectItemsButtonEnabled()
    {
        return browser.findElement(selectItemsButtonSelector).isEnabled();
    }

    public boolean isDuplicateItemsDisplayed()
    {
        return browser.findElement(duplicateItemsSelector).isDisplayed();
    }

    public boolean isDeleteSelectedItemsDisplayed()
    {
        return browser.findElement(deleteItemsSelector).isDisplayed();
    }

    public boolean isDeselectSelectedItemsDisplayed()
    {
        return browser.findElement(deselectAllItemsSelector).isDisplayed();
    }

    public void editItem(List<String> listDetails)
    {
    }

    public void duplicateItem(List<String> listDetails)
    {
    }

    public void deleteItem(List<String> listDetails)
    {
    }

    public String messageDisplayed()
    {
        return null;
    }

    public boolean duplicatedRows(List<String> listDetails)
    {
        return false;
    }

    public void clickSelectButton()
    {
    }

    public void clickSelectAllOption()
    {
    }

    public void clickInvertSelectionOption()
    {
    }

    public void clickSelectNoneOption()
    {
    }

    public void clickSelectedItemsButton()
    {
    }

    public void clickDuplicateItemsOption()
    {
    }

    public void clickDeleteItemsOption()
    {
    }

    public void clickDeselectAllItemsOption()
    {
    }

}
