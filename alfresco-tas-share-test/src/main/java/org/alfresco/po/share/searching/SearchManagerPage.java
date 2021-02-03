package org.alfresco.po.share.searching;

import static org.alfresco.common.Wait.WAIT_5;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SearchManagerPage extends SharePage2<SearchManagerPage>
{
    private final By filterTable = By.id("SEARCH_CONFIG_FACET_LIST_VIEW");
    private final By filterRows = By.id("SEARCH_CONFIG_FACET_LIST_VIEW_ROW");
    private final By filterReorderUp = By.cssSelector("td:nth-of-type(1) span.up>img");
    private final By filterReorderDown = By.cssSelector("td:nth-of-type(1) span.down>img");
    private final By filterId = By.cssSelector("td:nth-of-type(2) span.inner");
    private final By filterName = By.cssSelector("td:nth-of-type(3) span.inlineEditValue");
    private final By filterProperty = By.cssSelector("td[id^='SEARCH_CONFIG_FACET_QNAME_CELL_ITEM']");
    private final By filterShow = By.cssSelector("td:nth-of-type(6) span.inlineEditValue");
    private final By filterAvailability = By.cssSelector("td:nth-of-type(8)");
    private final By filterDeleteImage = By.cssSelector("td[id^='SEARCH_CONFIG_ACTIONS_CELL_ITEM'] > span");
    private final By editIcon = By.cssSelector("img.editIcon");
    private final By editInput = By.cssSelector("input.dijitInputInner");
    private final By editSave = By.cssSelector("span[class*='alfresco-buttons-AlfButton confirmationButton'] span");
    private final By editCancel = By.cssSelector("span[class*='alfresco-buttons-AlfButton cancelButton']");
    private final By parent = By.xpath("..");
    private final By createNewFilter = By.cssSelector("span[aria-labelledby='CREATE_FACET_BUTTON_label']");
    private final By filters = By.cssSelector("tr[id='SEARCH_CONFIG_FACET_LIST_VIEW_ROW']");
    private final By filterTableColumns = By.cssSelector("#SEARCH_CONFIG_FACET_LIST_VIEW th>span.label");
    private final By filterPropertyDropDownArrow = By.cssSelector("input.dijitReset.dijitInputField.dijitArrowButtonInner");
    private final By notificationMessage = By.cssSelector("div[class^='alfresco-notifications-AlfNotification']");
    private final String filterPropertyPath = "//div[contains(@id, 'SEARCH_CONFIG_FACET_QNAME_ITEM_')]//td[text()='%s']";
    private final String filterRow = "//span[text()='%s']/../../../..";

    public SearchManagerPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/dp/ws/faceted-search-config";
    }

    @Override
    public ThreadLocal<String> waitUntilNotificationMessageDisappears()
    {
        try
        {
            notificationMessageThread.set(webElementInteraction.getElementText(notificationMessage, WAIT_5.getValue()));
            webElementInteraction.waitUntilElementDisappears(notificationMessage);
        }
        catch (TimeoutException exception)
        {
            // do nothing and carry on as this might be expected, meaning that the element might be expected to already disappear
        }
        return notificationMessageThread;
    }

    public SearchManagerPage assertSearchManagerPageIsOpened()
    {
        log.info("Assert Search Manager page is opened");
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "Search Manager page is opened");
        return this;
    }

    private WebElement selectFilterProperty(String filterPropertyText)
    {
        return webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(
            filterPropertyPath, filterPropertyText)));
    }

    private WebElement getFilterRowById(String filterId)
    {
        return webElementInteraction.findFirstElementWithValue(filterRows, filterId);
    }

    public SearchManagerPage assertFilterIsNotDisplayed(String filterId)
    {
        log.info("Assert filter {} is not displayed", filterId);
        By filter = By.xpath(String.format(filterRow, filterId));
        assertFalse(webElementInteraction.isElementDisplayed(filter), String.format("Filter %s is displayed", filterId));
        return this;
    }

    /**
     * Method gets default filters table columns titles
     *
     * @return
     */
    public List<String> getFiltersTableColumns()
    {
        List<String> columnsTitle = new ArrayList<>();
        for (WebElement column : webElementInteraction.findElements(filterTableColumns))
        {
            if (!column.getText().isEmpty())
            {
                columnsTitle.add(column.getText());
            }
        }
        return columnsTitle;
    }

    public boolean isCreateNewFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(createNewFilter);
    }

    public boolean isFilterAvailable(String filter)
    {
        webElementInteraction.waitUntilElementIsDisplayedWithRetry(filterRows);
        return webElementInteraction.findFirstElementWithValue(filters, filter) != null;
    }

    public CreateNewFilterDialog createNewFilter()
    {
        webElementInteraction.clickElement(createNewFilter);
        return new CreateNewFilterDialog(webDriver);
    }

    public CreateNewFilterDialog clickFilterId(String filterId)
    {
        webElementInteraction.waitUntilElementIsVisible(this.filterId);
        webElementInteraction.findFirstElementWithValue(this.filterId, filterId).click();
        return new CreateNewFilterDialog(webDriver);
    }

    public String getFilterName(String filterId)
    {
        return getFilterRowById(filterId).findElement(filterName).getText();
    }

    public String getFilterProperty(String filterId)
    {
        return getFilterRowById(filterId).findElement(filterProperty).getText();
    }

    public SearchManagerPage assertFilterPropertyIs(String filterId, String expectedProperty)
    {
        assertEquals(getFilterProperty(filterId), expectedProperty, String.format("Filter property '%s' is set", expectedProperty));
        return this;
    }

    public String getShowWithSearchResults(String filterId)
    {
        return getFilterRowById(filterId).findElement(filterShow).getText();
    }

    public String getFilterAvailability(String filterId)
    {
        return getFilterRowById(filterId).findElement(filterAvailability).getText();
    }

    public boolean isFilterDeletable(String filterId)
    {
        return !getFilterRowById(filterId).findElements(filterDeleteImage).isEmpty();
    }

    public ConfirmDeletionDialog clickDeleteFilter(String filterId)
    {
        webElementInteraction.clickElement(getFilterRowById(filterId).findElement(filterDeleteImage));
        return new ConfirmDeletionDialog(webDriver);
    }

    public SearchManagerPage deleteFilter(String filterId)
    {
        log.info("Delete filter {}", filterId);
        clickDeleteFilter(filterId).clickOKButton();
        webElementInteraction.waitUntilElementIsVisible(filterTable);

        return this;
    }

    public SearchManagerPage editFilterName(String filterName, String newFilterName)
    {
        WebElement filter = webElementInteraction.findFirstElementWithValue(this.filterName, filterName);
        WebElement filterParent = filter.findElement(parent);
        webElementInteraction.mouseOver(filter);
        webElementInteraction.clickElement(filterParent.findElement(editIcon));
        filterParent.findElement(editInput).clear();
        filterParent.findElement(editInput).sendKeys(newFilterName);
        webElementInteraction.clickElement(filterParent.findElement(editSave));
        return this;
    }

    public SearchManagerPage editFilterProperty(String filterId, String newFilterProperty)
    {
        WebElement filterRow = getFilterRowById(filterId);
        WebElement filterProp = filterRow.findElement(filterProperty);
        webElementInteraction.mouseOver(filterProp);
        WebElement editIconElement = filterProp.findElement(editIcon);
        webElementInteraction.mouseOver(editIconElement);
        webElementInteraction.clickElement(editIconElement);
        webElementInteraction.clickElement(filterPropertyDropDownArrow);
        webElementInteraction.clickElement(selectFilterProperty(newFilterProperty));
        WebElement saveBtn = filterRow.findElement(editSave);
        webElementInteraction.clickElement(saveBtn);
        waitUntilNotificationMessageDisappears();
        webElementInteraction.waitUntilElementContainsText(filterProp, newFilterProperty);

        return this;
    }

    public SearchManagerPage cancelEditFilterName(String filterName, String newFilterName)
    {
        WebElement filter = webElementInteraction.findFirstElementWithValue(this.filterName, filterName);
        WebElement filterParent = filter.findElement(parent);
        webElementInteraction.mouseOver(filter);
        webElementInteraction.clickElement(filterParent.findElement(editIcon));
        filterParent.findElement(editInput).clear();
        filterParent.findElement(editInput).sendKeys(newFilterName);
        webElementInteraction.clickElement(filterParent.findElement(editCancel));

        return this;
    }

    public String getUpTooltipForFilter(String filterId)
    {
        return getFilterRowById(filterId).findElement(filterReorderUp).getAttribute("title");
    }

    public String getDownTooltipForFilter(String filterId)
    {
        return getFilterRowById(filterId).findElement(filterReorderDown).getAttribute("title");
    }

    public SearchManagerPage moveFilterUp(String filterId)
    {
        getFilterRowById(filterId).findElement(filterReorderUp);
        return this;
    }

    public SearchManagerPage moveFilterDown(String filterId)
    {
        webElementInteraction.clickElement(getFilterRowById(filterId).findElement(filterReorderDown));
        return this;
    }

    public int getFilterPosition(String filterId)
    {
        return webElementInteraction.findFirstElementWithValue(this.filterId, filterId).findElements(By.xpath("ancestor::tr/preceding-sibling::*")).size() + 1;
    }
}
