package org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.openqa.selenium.By;
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
    private final By selectButton = By.xpath(" //button[text()='Select']");
    private final By cancelButton = By.cssSelector("div.yui-dialog[style*='visibility: visible'] button[id$='cntrl-cancel-button']");
    private final By itemsRows = By.cssSelector("tr.yui-dt-rec:not(.create-new-row)");
    private final By addIcon = By.cssSelector("a[class^='add-item']");
    private final By removeIcon = By.cssSelector("a[class*='remove-item']");
    private final By leftAreaResults = By.cssSelector("div[id$='prop_cm_categories-cntrl-picker-results']");

    private final String addTagItemRow = "//div[contains(@id, 'prop_cm_taggable-cntrl-picker-left')]//h3[text()='%s']/../../..";
    private final String removeTagItemRow = "//div[contains(@id, 'prop_cm_taggable-cntrl-picker-right')]//h3[text()='%s']/../../..";
    private final String addCategoryItemRow = "//div[contains(@id, 'prop_cm_categories-cntrl-picker-left')]//a[text()='%s']/../../../..";
    private final String removeCategoryItemRow = "//div[contains(@id, 'prop_cm_categories-cntrl-picker-right')]//h3[text()='%s']/../../..";

    public SelectDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public void waitForLeftAreaResults()
    {
        waitUntilElementIsVisible(leftAreaResults);
    }

    private WebElement getItemElementFromResultsPicker(String item)
    {
        List<WebElement> results = waitUntilElementIsVisible(resultsPicker).findElements(itemsRows);
        return findFirstElementWithValue(results, item);
    }

    private WebElement getItemElementFromSelectedItemsPicker(String item)
    {
        List<WebElement> selectedItems = waitUntilElementIsVisible(selectedItemsPicker).findElements(itemsRows);
        return findFirstElementWithValue(selectedItems, item);
    }

    public void selectItems(List<String> items)
    {
        items.forEach(this::selectTag);
    }

    public SelectDialog selectTag(String tag)
    {
        log.info("Select tag {}", tag);
        WebElement itemRow = findElement(By.xpath(String.format(addTagItemRow, tag)));
        clickElement(itemRow.findElement(addIcon));
        return this;
    }

    public SelectDialog selectCategory(String category)
    {
        log.info("Select category {}", category);
        By categoryLocator = By.xpath(String.format(addCategoryItemRow, category));
        clickElement(waitUntilElementIsVisible(categoryLocator).findElement(addIcon));
        return this;
    }

    public boolean isItemSelectable(String item)
    {
        return isElementDisplayed(getItemElementFromResultsPicker(item).findElement(addIcon));
    }

    public SelectDialog assertTagIsNotSelectable(String tag)
    {
        log.info("Assert tag {} is not selectable", tag);
        WebElement itemRow = findElement(By.xpath(String.format(addTagItemRow, tag)));
        assertFalse(isElementDisplayed(itemRow.findElement(addIcon)),
            String.format("Tag %s is selectable", tag));
        return this;
    }

    public SelectDialog assertCategoryIsNotSelectable(String category)
    {
        log.info("Assert category {} is not selectable", category);
        WebElement itemRow = findElement(By.xpath(String.format(addCategoryItemRow, category)));
        assertFalse(isElementDisplayed(itemRow.findElement(addIcon)),
            String.format("Tag %s is selectable", category));
        return this;
    }

    public boolean isItemSelected(String item)
    {
        return isElementDisplayed(getItemElementFromSelectedItemsPicker(item));
    }

    public SelectDialog assertTagIsSelected(String tag)
    {
        log.info("Assert tag {} is selected", tag);
        WebElement selectedItem = waitUntilElementIsVisible(By.xpath(String.format(removeTagItemRow, tag)));
        assertTrue(isElementDisplayed(selectedItem),
            String.format("Tag is not selected %s", tag));
        return this;
    }

    public SelectDialog assertIsItemSelected(String tagName)
    {
        log.info("Verify that the item is selected.");
        assertTrue(isItemSelected(tagName), tagName.toLowerCase() + " is displayed in selected categories list.");
        return this;
    }

    public SelectDialog assertCategoryIsSelected(String category)
    {
        log.info("Assert category {} is selected", category);
        WebElement selectedItem = waitUntilElementIsVisible(By.xpath(String.format(removeCategoryItemRow, category)));
        assertTrue(isElementDisplayed(selectedItem),
            String.format("Category is not selected %s", category));
        return this;
    }

    public SelectDialog removeCategory(String category)
    {
        log.info("Remove category {}", category);
        WebElement selectedItem = waitUntilElementIsVisible(By.xpath(String.format(removeCategoryItemRow, category)));
        clickElement(selectedItem.findElement(removeIcon));
        return this;
    }

    public EditPropertiesDialog clickOk()
    {
        clickElement(okButton);
        return new EditPropertiesDialog(webDriver);
    }

    public void clickCancel()
    {
        clickElement(cancelButton);
    }

    public SelectDialog typeTag(String tagName)
    {
        log.info("Type tag {}", tagName);
        waitUntilElementIsVisible(tagInputField);
        clearAndType(tagInputField, tagName);

        return this;
    }

    public SelectDialog typeTagWithRetry(String tagName)
    {
        By tagRow = By.xpath(String.format(addTagItemRow, tagName));
        int retryCount = 0;
        typeTag(tagName);
        while(retryCount < RETRY_TIME_80.getValue() && !isElementDisplayed(tagRow))
        {
            log.warn("Tag {} not displayed - retry: {}", tagName, retryCount);
            typeTag(tagName);
            waitToLoopTime(WAIT_2.getValue());
            retryCount++;
        }
        return this;
    }

    public SelectDialog clickCreateNewIcon()
    {
        log.info("Click Create new icon");
        clickElement(createNewIcon);
        waitUntilElementIsVisible(removeIcon);

        return this;
    }

    public SelectDialog clickSelectButton() {
        waitInSeconds(2);
        clickElement(selectButton);
        return this;
    }
}