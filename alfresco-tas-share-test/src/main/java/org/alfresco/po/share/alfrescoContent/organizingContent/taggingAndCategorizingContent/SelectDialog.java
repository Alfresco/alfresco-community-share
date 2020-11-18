package org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;

public class SelectDialog extends ShareDialog2
{
    @Autowired
    EditPropertiesDialog editPropertiesDialog;

    private By dialogTitle = By.cssSelector("div[id*='prop_cm_taggable-cntrl-picker_c'] div[id*='cntrl-picker-head']");
    private By tagInputField = By.cssSelector("input.create-new-input");
    private By createNewIcon = By.cssSelector(".createNewIcon");
    private By navigatorButton = By.cssSelector("div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-picker-navigator-button']");
    private By resultsPicker = By.cssSelector("div.yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] .yui-dt-data");
    private By selectedItemsPicker = By.cssSelector("div.yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-selectedItems'] tbody.yui-dt-data");
    @RenderWebElement
    private By okButton = By.cssSelector("div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-ok-button']");

    private By cancelButton = By.cssSelector("div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-cancel-button']");
    private By itemsRows = By.cssSelector("tr.yui-dt-rec:not(.create-new-row)");
    private By addIcon = By.cssSelector("a[class*='add-item']");
    private By removeIcon = By.cssSelector("a[class*='remove-item']");

    public String getDialogTitle()
    {
        return getBrowser().waitUntilElementVisible(dialogTitle).getText();
    }

    private WebElement getItemElementFromResultsPicker(String item)
    {
        List<WebElement> results = getBrowser().findElement(resultsPicker).findElements(itemsRows);
        return getBrowser().findFirstElementWithValue(results, item);
    }

    private WebElement getItemElementFromSelectedItemsPicker(String item)
    {
        List<WebElement> selectedItems = getBrowser().findElement(selectedItemsPicker).findElements(itemsRows);
        return getBrowser().findFirstElementWithValue(selectedItems, item);
    }

    public void selectItems(List<String> items)
    {
        for (String item : items)
        {
            try
            {
                WebElement result = getItemElementFromResultsPicker(item);
                Parameter.checkIsMandotary("Select item result", result);
                result.findElement(addIcon).click();
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
                Parameter.checkIsMandotary("Selected item", selectedItem);
                selectedItem.findElement(removeIcon).click();
            } catch (NoSuchElementException noSuchElementExp)
            {
                LOG.error("Remove icon for item: " + item + " is not present.", noSuchElementExp);
            }
        }
    }

    public boolean isItemSelectable(String item)
    {
        return getBrowser().isElementDisplayed(getItemElementFromResultsPicker(item).findElement(addIcon));
    }

    public boolean isItemRemovable(String item)
    {
        return getBrowser().isElementDisplayed(getItemElementFromSelectedItemsPicker(item).findElement(removeIcon));
    }

    public boolean isItemSelected(String item)
    {
        return getBrowser().isElementDisplayed(getItemElementFromSelectedItemsPicker(item));
    }

    public EditPropertiesDialog clickOk()
    {
        getBrowser().findElement(okButton).click();
        return (EditPropertiesDialog) editPropertiesDialog.renderedPage();
    }

    public void clickCancel()
    {
        getBrowser().findElement(cancelButton).click();
    }

    public void typeTag(String tagName)
    {
        clearAndType(tagInputField, tagName);
    }

    public void clickCreateNewIcon()
    {
        getBrowser().waitUntilElementClickable(createNewIcon).click();
        getBrowser().waitUntilElementVisible(removeIcon);
    }
}