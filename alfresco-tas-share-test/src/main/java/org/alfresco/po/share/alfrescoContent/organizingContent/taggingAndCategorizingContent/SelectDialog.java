package org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import java.util.List;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SelectDialog extends BaseDialogComponent
{
    private EditPropertiesDialog editPropertiesDialog;

    private final By dialogTitle = By.cssSelector("div[id*='prop_cm_taggable-cntrl-picker_c'] div[id*='cntrl-picker-head']");
    private final By tagInputField = By.cssSelector("input.create-new-input");
    private final By createNewIcon = By.cssSelector(".createNewIcon");
    private final By resultsPicker = By.cssSelector("div.yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] .yui-dt-data");
    private final By selectedItemsPicker = By.cssSelector("div.yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-selectedItems'] tbody.yui-dt-data");
    private final By okButton = By.cssSelector("div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-ok-button']");
    private final By cancelButton = By.cssSelector("div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-cancel-button']");
    private final By itemsRows = By.cssSelector("tr.yui-dt-rec:not(.create-new-row)");
    private final By addIcon = By.cssSelector("a[class*='add-item']");
    private final By removeIcon = By.cssSelector("a[class*='remove-item']");

    public SelectDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return webElementInteraction.getElementText(dialogTitle);
    }

    private WebElement getItemElementFromResultsPicker(String item)
    {
        List<WebElement> results = webElementInteraction.findElement(resultsPicker).findElements(itemsRows);
        return webElementInteraction.findFirstElementWithValue(results, item);
    }

    private WebElement getItemElementFromSelectedItemsPicker(String item)
    {
        List<WebElement> selectedItems = webElementInteraction.findElement(selectedItemsPicker).findElements(itemsRows);
        return webElementInteraction.findFirstElementWithValue(selectedItems, item);
    }

    public void selectItems(List<String> items)
    {
        for (String item : items)
        {
            try
            {
                WebElement result = getItemElementFromResultsPicker(item);
                webElementInteraction.clickElement(result);
            }
            catch (NoSuchElementException noSuchElementExp)
            {
                LOG.error("Add icon for item: " + item + " is not present.", noSuchElementExp);
            }
        }
    }

    public void removeItems(List<String> items)
    {
        for (String item : items)
        {
            try
            {
                WebElement selectedItem = getItemElementFromSelectedItemsPicker(item);
                webElementInteraction.findElement(selectedItem).findElement(removeIcon).click();
            } catch (NoSuchElementException noSuchElementExp)
            {
                LOG.error("Remove icon for item: " + item + " is not present.", noSuchElementExp);
            }
        }
    }

    public boolean isItemSelectable(String item)
    {
        return webElementInteraction.isElementDisplayed(getItemElementFromResultsPicker(item).findElement(addIcon));
    }

    public boolean isItemRemovable(String item)
    {
        return webElementInteraction.isElementDisplayed(getItemElementFromSelectedItemsPicker(item).findElement(removeIcon));
    }

    public boolean isItemSelected(String item)
    {
        return webElementInteraction.isElementDisplayed(getItemElementFromSelectedItemsPicker(item));
    }

    public EditPropertiesDialog clickOk()
    {
        webElementInteraction.clickElement(okButton);
        return (EditPropertiesDialog) editPropertiesDialog.renderedPage();
    }

    public void clickCancel()
    {
        webElementInteraction.clickElement(cancelButton);
    }

    public void typeTag(String tagName)
    {
        webElementInteraction.clearAndType(tagInputField, tagName);
    }

    public void clickCreateNewIcon()
    {
        webElementInteraction.clickElement(createNewIcon);
        webElementInteraction.waitUntilElementIsVisible(removeIcon);
    }
}