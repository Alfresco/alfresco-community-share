package org.alfresco.po.share.searching;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * Created by Claudia Agache on 8/16/2016.
 */
@PageObject
public class SearchManagerPage extends SharePage<SearchManagerPage>
{
    private static final By FILTER_ROWS = By.cssSelector("#SEARCH_CONFIG_FACET_LIST_VIEW_ROW");
    private static final By FILTER_REORDER_UP = By.cssSelector("td:nth-of-type(1) span.up>img");
    private static final By FILTER_REORDER_DOWN = By.cssSelector("td:nth-of-type(1) span.down>img");
    private static final By FILTER_ID = By.cssSelector("td:nth-of-type(2) span.inner");
    private static final By FILTER_NAME = By.cssSelector("td:nth-of-type(3) span.inlineEditValue");
    private static final By FILTER_PROPERTY = By.cssSelector("td[id^='SEARCH_CONFIG_FACET_QNAME_CELL_ITEM']");
    private static final By FILTER_TYPE = By.cssSelector("td:nth-of-type(5) span.inlineEditValue");
    private static final By FILTER_SHOW = By.cssSelector("td:nth-of-type(6) span.inlineEditValue");
    private static final By FILTER_DEFAULT = By.cssSelector("td:nth-of-type(7)");
    private static final By FILTER_AVAILABILITY = By.cssSelector("td:nth-of-type(8)");
    private static final By FILTER_DELETE_IMAGE = By.cssSelector("td[id^='SEARCH_CONFIG_ACTIONS_CELL_ITEM'] > span");
    private static final By EDIT_ICON = By.cssSelector("img.editIcon");
    private static final By EDIT_INPUT = By.cssSelector("input.dijitInputInner");
    private static final By EDIT_DROPDOWN = By.cssSelector("div.control-row table.dijitSelect");
    private static final By EDITPROPERTY_DROPDOWN = By.cssSelector("div.control-row table.dijitDownArrowButton");
    private static final By EDIT_DROPDOWN_POPUP = By.cssSelector("div[id$='_CONTROL_dropdown']");
    private static final By EDIT_DROPDOWN_ITEM = By.cssSelector("table[id$='_CONTROL_menu'] tr.dijitMenuItem td.dijitMenuItemLabel");
    private static final By EDIT_SAVE = By.cssSelector("span[class*='alfresco-buttons-AlfButton confirmationButton'] span");
    private static final By EDIT_CANCEL = By.cssSelector("span[class*='alfresco-buttons-AlfButton cancelButton']");
    private static final By PARENT = By.xpath("..");

    @Autowired
    private ConfirmDeletionDialog confirmDeletionDialog;

    @Autowired
    private CreateNewFilterDialog createNewFilterDialog;

    @RenderWebElement
    @FindBy (id = "CREATE_FACET_BUTTON_label")
    private WebElement createNewFilter;

    @FindAll (@FindBy (id = "SEARCH_CONFIG_FACET_LIST_VIEW_ROW"))
    private List<WebElement> filters;

    @FindAll (@FindBy (css = "#SEARCH_CONFIG_FACET_LIST_VIEW th>span.label"))
    private List<WebElement> filterTableColumns;

    @FindBy (css = "input.dijitReset.dijitInputField.dijitArrowButtonInner")
    private WebElement filterPropertyDropDownArrow;

    @FindAll (@FindBy (css = "div[id ='SEARCH_CONFIG_FACET_QNAME_ITEM_0_SELECT_CONTROL_dropdown'] tbody.dijitReset"))
    private List<WebElement> filterPropertyOptions;

    private String filterProperty = "//div[contains(@id, 'SEARCH_CONFIG_FACET_QNAME_ITEM_')]//td[text()='%s']";
    private By notificationMessage = By.cssSelector("div[class^='alfresco-notifications-AlfNotification']");

    @Override
    public String getRelativePath()
    {
        return "share/page/dp/ws/faceted-search-config";
    }

    @Override
    public String waitUntilNotificationMessageDisappears()
    {
        String message = "";
        try
        {
            message = getBrowser().waitUntilElementVisible(notificationMessage, 5).getText();
            getBrowser().waitUntilElementDisappears(notificationMessage);
        }
        catch (TimeoutException exception)
        {
            // do nothing and carry on as this might be expected, meaning that the element might be expected to already disappear
        }
        return message;
    }

    public SearchManagerPage assertSearchManagerPageIsOpened()
    {
        LOG.info("Assert Search Manager page is opened");
        Assert.assertTrue(browser.getCurrentUrl().contains(getRelativePath()), "Search Manager page is opened");
        return this;
    }

    private WebElement selectFilterProperty(String filterPropertyText)
    {
        return browser.waitUntilElementVisible(By.xpath(String.format(filterProperty, filterPropertyText)));
    }

    private WebElement getFilterRowById(String filterId)
    {
        return browser.findFirstElementWithValue(filters, filterId);
    }

    public SearchManagerPage assertFilterIsDisplayed(String filterId)
    {
        LOG.info(String.format("Assert filter %s is displayed", filterId));
        Assert.assertNotNull(getFilterRowById(filterId), String.format("Filter %s is displayed", filterId));
        return this;
    }

    public SearchManagerPage assertFilterIsNotDisplayed(String filterId)
    {
        LOG.info(String.format("Assert filter %s is NOT displayed", filterId));
        Assert.assertNull(getFilterRowById(filterId), String.format("Filter %s is displayed", filterId));
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
        for (WebElement column : filterTableColumns)
        {
            if (!column.getText().isEmpty())
            {
                columnsTitle.add(column.getText());
            }
        }
        return columnsTitle;
    }

    /**
     * Check if Create New Filter button is displayed or not on the page
     *
     * @return
     */
    public boolean isCreateNewFilterDisplayed()
    {
        return browser.isElementDisplayed(createNewFilter);
    }

    /**
     * Check if the specified filter is present in the filters list
     *
     * @param filter
     * @return
     */
    public boolean isFilterAvailable(String filter)
    {
        browser.waitUntilElementIsDisplayedWithRetry(FILTER_ROWS);
        return browser.findFirstElementWithValue(filters, filter) != null;
    }

    /**
     * Click on the Create New Filter button
     *
     * @return
     */
    public CreateNewFilterDialog createNewFilter()
    {
        createNewFilter.click();
        return (CreateNewFilterDialog) createNewFilterDialog.renderedPage();
    }

    /**
     * Click on the given filter in Filter Id column
     *
     * @param filterId
     */
    public EditFilterPopup clickFilterId(String filterId)
    {
        browser.findFirstElementWithValue(FILTER_ID, filterId).click();
        return new EditFilterPopup();
    }

    /**
     * Get Filter Name for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getFilterName(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_NAME).getText();
    }

    /**
     * Get Filter Property for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getFilterProperty(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_PROPERTY).getText();
    }

    public SearchManagerPage assertFilterPropertyIs(String filterId, String expectedProperty)
    {
        Assert.assertEquals(getFilterProperty(filterId), expectedProperty, String.format("Filter property '%s' is set", expectedProperty));
        return this;
    }

    /**
     * Get Filter Type for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getFilterType(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_TYPE).getText();
    }

    /**
     * Get Show with Search Results for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getShowWithSearchResults(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_SHOW).getText();
    }

    /**
     * Get Filter Availability for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getFilterAvailability(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_AVAILABILITY).getText();
    }

    /**
     * Check if the given filter has Delete icon displayed or not
     *
     * @param filterId
     * @return false if filterId is a default filter, true if it is custom
     */
    public boolean isFilterDeletable(String filterId)
    {
        return getFilterRowById(filterId).findElements(FILTER_DELETE_IMAGE).size() != 0;
    }

    public ConfirmDeletionDialog clickDeleteFilter(String filterId)
    {
        getFilterRowById(filterId).findElement(FILTER_DELETE_IMAGE).click();
        return (ConfirmDeletionDialog) confirmDeletionDialog.renderedPage();
    }

    public SearchManagerPage deleteFilter(String filterId)
    {
        clickDeleteFilter(filterId).clickOKButton();
        waitUntilNotificationMessageDisappears();
        return this;
    }

    /**
     * Change the given filterName with the newFilterName value and click on Save button
     *
     * @param filterName
     * @param newFilterName
     * @return
     */
    public SearchManagerPage editFilterName(String filterName, String newFilterName)
    {
        WebElement filter = browser.findFirstElementWithValue(FILTER_NAME, filterName);
        WebElement filterParent = filter.findElement(PARENT);
        browser.mouseOver(filter);
        filterParent.findElement(EDIT_ICON).click();
        filterParent.findElement(EDIT_INPUT).clear();
        filterParent.findElement(EDIT_INPUT).sendKeys(newFilterName);
        filterParent.findElement(EDIT_SAVE).click();
        return (SearchManagerPage) this.renderedPage();
    }

    /**
     * Change the given filterProperty and click on Save button
     */

    public SearchManagerPage editFilterProperty(String filterId, String newFilterProperty)
    {
        WebElement filterRow = getFilterRowById(filterId);
        WebElement filterProp = filterRow.findElement(FILTER_PROPERTY);
        WebElement editIcon = filterProp.findElement(EDIT_ICON);
        browser.mouseOver(editIcon);
        browser.waitUntilElementClickable(editIcon).click();
        browser.waitUntilElementVisible(filterPropertyDropDownArrow).click();
        selectFilterProperty(newFilterProperty).click();
        browser.waitUntilElementClickable(filterRow.findElement(EDIT_SAVE)).click();
        waitUntilNotificationMessageDisappears();
        return this;
    }

    /**
     * Change the given filterName with the newFilterName value and click on Cancel button
     *
     * @param filterName
     * @param newFilterName
     * @return
     */
    public SearchManagerPage cancelEditFilterName(String filterName, String newFilterName)
    {
        WebElement filter = browser.findFirstElementWithValue(FILTER_NAME, filterName);
        WebElement filterParent = filter.findElement(PARENT);
        browser.mouseOver(filter);
        filterParent.findElement(EDIT_ICON).click();
        filterParent.findElement(EDIT_INPUT).clear();
        filterParent.findElement(EDIT_INPUT).sendKeys(newFilterName);
        filterParent.findElement(EDIT_CANCEL).click();
        return (SearchManagerPage) this.renderedPage();
    }

    /**
     * Get the tooltip for the filter's up image
     *
     * @param filterId
     * @return
     */
    public String getUpTooltipForFilter(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_REORDER_UP).getAttribute("title");
    }

    /**
     * Get the tooltip for the filter's down image
     *
     * @param filterId
     * @return
     */
    public String getDownTooltipForFilter(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_REORDER_DOWN).getAttribute("title");
    }

    /**
     * Move the given filter one position up
     *
     * @param filterId
     * @return
     */
    public SearchManagerPage moveFilterUp(String filterId)
    {
        getFilterRowById(filterId).findElement(FILTER_REORDER_UP).click();
        return (SearchManagerPage) this.renderedPage();
    }

    /**
     * Move the given filter one position down
     *
     * @param filterId
     * @return
     */
    public SearchManagerPage moveFilterDown(String filterId)
    {
        getFilterRowById(filterId).findElement(FILTER_REORDER_DOWN).click();
        return (SearchManagerPage) this.renderedPage();
    }

    public int getFilterPosition(String filterId)
    {
        return browser.findFirstElementWithValue(FILTER_ID, filterId).findElements(By.xpath("ancestor::tr/preceding-sibling::*")).size() + 1;
    }

    public void deleteAllNonDefaultFilters()
    {
        int i = 0, maxTimes = filters.size() * 2;
        while (i < filters.size() && maxTimes > 0)
        {
            if (filters.get(i).findElement(FILTER_DEFAULT).getText().equals("No"))
            {
                this.clickDeleteFilter(filters.get(i).findElement(FILTER_ID).getText());
                confirmDeletionDialog.clickOKButton();
                browser.refresh();
                browser.waitUntilElementIsDisplayedWithRetry(FILTER_ROWS);
                filters = browser.findElements(FILTER_ROWS);
                i--;
            }
            i++;
            maxTimes--;
        }
    }
}
