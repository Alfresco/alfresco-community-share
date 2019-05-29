package org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import org.alfresco.po.share.ShareDialog;
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

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SelectDialog extends ShareDialog
{
    @Autowired
    EditPropertiesDialog editPropertiesDialog;

    @Autowired
    EditPropertiesPage editPropertiesPage;

    @FindBy (css = "div[id*='prop_cm_taggable-cntrl-picker_c'] div[id*='cntrl-picker-head']")
    private WebElement dialogTitle;

    @FindBy (css = "input.create-new-input")
    private WebElement tagInputField;

    @FindBy (css = ".createNewIcon")
    private WebElement createNewIcon;

    @FindBy (css = "div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-picker-navigator-button']")
    private Button navigatorButton;

    @FindBy (css = "div.yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] .yui-dt-data")
    private WebElement resultsPicker;

    @FindBy (css = "div.yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-selectedItems'] tbody.yui-dt-data")
    private WebElement selectedItemsPicker;

    @RenderWebElement
    @FindBy (css = "div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-ok-button']")
    private WebElement okButton;

    @FindBy (css = "div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-cancel-button']")
    private WebElement cancelButton;

    private By itemsRows = By.cssSelector("tr.yui-dt-rec:not(.create-new-row)");
    private By addIcon = By.cssSelector("a[class*='add-item']");
    private By removeIcon = By.cssSelector("a[class*='remove-item']");

    public String getDialogTitle()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(dialogTitle);
        if (browser.isElementDisplayed(dialogTitle))
            return dialogTitle.getText();
        return "isn't displayed!";
    }

    private WebElement getItemElementFromResultsPicker(String item)
    {
        List<WebElement> results = resultsPicker.findElements(itemsRows);
        return browser.findFirstElementWithValue(results, item);
    }

    private WebElement getItemElementFromSelectedItemsPicker(String item)
    {
        List<WebElement> selectedItems = selectedItemsPicker.findElements(itemsRows);
        return browser.findFirstElementWithValue(selectedItems, item);
    }

    /**
     * Click "Add" icon for any tags from left side picker
     *
     * @param items list of tags to be added
     */
    public void selectItems(List<String> items)
    {
        browser.waitInSeconds(2);
        for (String item : items)
        {
            try
            {
                WebElement result = getItemElementFromResultsPicker(item);
                Parameter.checkIsMandotary("Select item result", result);
                result.findElement(addIcon).click();
            } catch (NoSuchElementException noSuchElementExp)
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

    /**
     * Check if add icon is displayed next to item name from results picker
     *
     * @param item
     * @return true if add icon is displayed, false otherwise
     */
    public boolean isItemSelectable(String item)
    {
        return browser.isElementDisplayed(getItemElementFromResultsPicker(item).findElement(addIcon));
    }

    /**
     * Check if remove icon is displayed next to item name from selected items picker
     *
     * @param item
     * @return true if add icon is displayed, false otherwise
     */
    public boolean isItemRemovable(String item)
    {
        return browser.isElementDisplayed(getItemElementFromSelectedItemsPicker(item).findElement(removeIcon));
    }

    /**
     * Check if the given item is displayed in Selected Items picker
     *
     * @param item
     * @return
     */
    public boolean isItemSelected(String item)
    {
        return browser.isElementDisplayed(getItemElementFromSelectedItemsPicker(item));
    }

    public EditPropertiesDialog clickOk()
    {
        okButton.click();
        return (EditPropertiesDialog) editPropertiesDialog.renderedPage();
    }

    public EditPropertiesPage clickOkAndRenderPropertiesPage()
    {
        okButton.click();
        return (EditPropertiesPage) editPropertiesPage.renderedPage();
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

    /**
     * @param tagName to be typed into Create New input field
     */
    public void typeTag(String tagName)
    {
        browser.waitUntilElementVisible(tagInputField);
        tagInputField.clear();
        tagInputField.sendKeys(tagName);
    }

    public void clickCreateNewIcon()
    {
        browser.waitUntilElementClickable(createNewIcon, 5);
        createNewIcon.click();
        browser.waitUntilElementClickable(removeIcon, 20L);
    }
}