package org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SelectDialog extends BaseDialogComponent
{
    private final By dialogTitle = By.cssSelector("div[id*='prop_cm_taggable-cntrl-picker_c'] div[id*='cntrl-picker-head']");
    private final By tagInputField = By.cssSelector("input.create-new-input");
    private final By createNewIcon = By.cssSelector(".createNewIcon");
    private final By resultsPicker = By.cssSelector("div.yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] .yui-dt-data");
    private final By selectedItemsPicker = By.cssSelector("div.yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-selectedItems'] tbody.yui-dt-data");
    private final By okButton = By.cssSelector("div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-ok-button']");
    private final By cancelButton = By.cssSelector("div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-cancel-button']");
    private final By itemsRows = By.cssSelector("tr.yui-dt-rec:not(.create-new-row)");
    private final By addIcon = By.cssSelector("a[class^='add-item']");
    private final By removeIcon = By.cssSelector("a[class*='remove-item']");

    private final String addItemRow = "//div[contains(@id, 'prop_cm_taggable-cntrl-picker-left')]//h3[text()='%s']/../../..";
    private final String removeItemRow = "//div[contains(@id, 'prop_cm_taggable-cntrl-picker-right')]//h3[text()='%s']/../../..";

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
        List<WebElement> results = webElementInteraction.waitUntilElementIsVisible(resultsPicker).findElements(itemsRows);
        return webElementInteraction.findFirstElementWithValue(results, item);
    }

    private WebElement getItemElementFromSelectedItemsPicker(String item)
    {
        List<WebElement> selectedItems = webElementInteraction.waitUntilElementIsVisible(selectedItemsPicker).findElements(itemsRows);
        return webElementInteraction.findFirstElementWithValue(selectedItems, item);
    }

    public void selectItems(List<String> items)
    {
        items.forEach(this::selectItem);
    }

    public SelectDialog selectItem(String item)
    {
        log.info("Select item {}", item);
        WebElement itemRow = webElementInteraction.findElement(By.xpath(String.format(addItemRow, item)));
        webElementInteraction.clickElement(itemRow.findElement(addIcon));
        return this;
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
                log.warn("Remove icon for item {} is not present.", item, noSuchElementExp);
            }
        }
    }

    public boolean isItemSelectable(String item)
    {
        return webElementInteraction.isElementDisplayed(getItemElementFromResultsPicker(item).findElement(addIcon));
    }

    public SelectDialog assertItemIsNotSelectable(String item)
    {
        log.info("Assert item {} is not selectable", item);
        WebElement itemRow = webElementInteraction.findElement(By.xpath(String.format(addItemRow, item)));
        assertFalse(webElementInteraction.isElementDisplayed(itemRow.findElement(addIcon)),
            String.format("Item %s is selectable", item));
        return this;
    }

    public boolean isItemSelected(String item)
    {
        return webElementInteraction.isElementDisplayed(getItemElementFromSelectedItemsPicker(item));
    }

    public SelectDialog assertItemIsSelected(String item)
    {
        log.info("Assert item {} is selected", item);
        WebElement selectedItem = webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(removeItemRow, item)));
        assertTrue(webElementInteraction.isElementDisplayed(selectedItem),
            String.format("Item is not selected %s", item));
        return this;
    }

    public EditPropertiesDialog clickOk()
    {
        webElementInteraction.clickElement(okButton);
        return new EditPropertiesDialog(webDriver);
    }

    public void clickCancel()
    {
        webElementInteraction.clickElement(cancelButton);
    }

    public SelectDialog typeTag(String tagName)
    {
        log.info("Type tag {}", tagName);
        webElementInteraction.waitUntilElementIsVisible(tagInputField);
        webElementInteraction.clearAndType(tagInputField, tagName);

        return this;
    }

    public SelectDialog typeTagWithRetry(String tagName)
    {
        By tagRow = By.xpath(String.format(addItemRow, tagName));
        int retryCount = 0;
        typeTag(tagName);
        while(retryCount < RETRY_TIME_80.getValue() && !webElementInteraction.isElementDisplayed(tagRow))
        {
            log.warn("Tag {} not displayed - retry: {}", tagName, retryCount);
            waitToLoopTime(WAIT_2.getValue());
            typeTag(tagName);
            retryCount++;
        }
        return this;
    }

    public SelectDialog clickCreateNewIcon()
    {
        log.info("Click Create new icon");
        webElementInteraction.clickElement(createNewIcon);
        webElementInteraction.waitUntilElementIsVisible(removeIcon);

        return this;
    }
}