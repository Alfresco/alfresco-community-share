package org.alfresco.po.share.searching;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

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
    private static final By FILTER_PROPERTY = By.cssSelector("td:nth-of-type(4) span.inlineEditValue");
    private static final By FILTER_TYPE = By.cssSelector("td:nth-of-type(5) span.inlineEditValue");
    private static final By FILTER_SHOW = By.cssSelector("td:nth-of-type(6) span.inlineEditValue");
    private static final By FILTER_DEFAULT = By.cssSelector("td:nth-of-type(7)");
    private static final By FILTER_AVAILABILITY = By.cssSelector("td:nth-of-type(8)");
    private static final By FILTER_DELETE_IMAGE = By.cssSelector("td:nth-of-type(9)>span");
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
    ConfirmDeletionDialog confirmDeletionDialog;
    @RenderWebElement
    @FindBy (id = "CREATE_FACET_BUTTON_label")
    private WebElement createNewFilter;
    //@RenderWebElement
    //@FindAll(@FindBy(css = "#SEARCH_CONFIG_FACET_LIST_VIEW_ITEMS > tr"))
    @FindAll (@FindBy (css = "#SEARCH_CONFIG_FACET_LIST_VIEW_ROW"))
    private List<WebElement> filters;
    @FindAll (@FindBy (css = "#SEARCH_CONFIG_FACET_LIST_VIEW th>span.label"))
    private List<WebElement> filterTableColumns;
    @FindBy (css = "input.dijitReset.dijitInputField.dijitArrowButtonInner")
    private WebElement filterPropertyDropDownArrow;
    @FindAll (@FindBy (css = "div[id ='SEARCH_CONFIG_FACET_QNAME_ITEM_0_SELECT_CONTROL_dropdown'] tbody.dijitReset"))
    private List<WebElement> filterPropertyOptions;

    private WebElement selectFilterProperty(String filterPropertyText)
    {
        browser.waitUntilElementVisible(By.xpath("//div[contains(@id, 'SEARCH_CONFIG_FACET_QNAME_ITEM_')]//td[text()='" + filterPropertyText + "']"));
        return browser.findElement(By.xpath("//div[contains(@id, 'SEARCH_CONFIG_FACET_QNAME_ITEM_')]//td[text()='" + filterPropertyText + "']"));
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/dp/ws/faceted-search-config";
    }

    private WebElement getFilterById(String filterId)
    {
        return browser.findFirstElementWithValue(filters, filterId);
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
    public CreateNewFilterPopup createNewFilter()
    {
        createNewFilter.click();
        return new CreateNewFilterPopup();
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
        return getFilterById(filterId).findElement(FILTER_NAME).getText();
    }

    /**
     * Get Filter Property for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getFilterProperty(String filterId, String text)
    {
        browser.waitUntilElementContainsText(getFilterById(filterId).findElement(FILTER_PROPERTY), text);
        return getFilterById(filterId).findElement(FILTER_PROPERTY).getText();
    }

    /**
     * Get Filter Type for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getFilterType(String filterId)
    {
        return getFilterById(filterId).findElement(FILTER_TYPE).getText();
    }

    /**
     * Get Show with Search Results for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getShowWithSearchResults(String filterId)
    {
        return getFilterById(filterId).findElement(FILTER_SHOW).getText();
    }

    /**
     * Get Filter Availability for the given filterId
     *
     * @param filterId
     * @return
     */
    public String getFilterAvailability(String filterId)
    {
        return getFilterById(filterId).findElement(FILTER_AVAILABILITY).getText();
    }

    /**
     * Check if the given filter has Delete icon displayed or not
     *
     * @param filterId
     * @return false if filterId is a default filter, true if it is custom
     */
    public boolean isFilterDeletable(String filterId)
    {
        return getFilterById(filterId).findElements(FILTER_DELETE_IMAGE).size() != 0;
    }

    public ConfirmDeletionDialog deleteFilter(String filterId)
    {
        getFilterById(filterId).findElement(FILTER_DELETE_IMAGE).click();
        return (ConfirmDeletionDialog) confirmDeletionDialog.renderedPage();
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

    public SearchManagerPage editFilterProperty(String filterProperty, String newFilterProperty)
    {
        WebElement filter = browser.findFirstElementWithValue(FILTER_PROPERTY, filterProperty);
        WebElement filterParent = filter.findElement(PARENT);
        browser.mouseOver(filter);
        browser.waitUntilElementClickable(filterParent.findElement(EDIT_ICON), 6L);
        filterParent.findElement(EDIT_ICON).click();
        filterPropertyDropDownArrow.click();
        selectFilterProperty(newFilterProperty).click();
        filterParent.findElement(EDIT_SAVE).click();
        return (SearchManagerPage) this.renderedPage();
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
        return getFilterById(filterId).findElement(FILTER_REORDER_UP).getAttribute("title");
    }

    /**
     * Get the tooltip for the filter's down image
     *
     * @param filterId
     * @return
     */
    public String getDownTooltipForFilter(String filterId)
    {
        return getFilterById(filterId).findElement(FILTER_REORDER_DOWN).getAttribute("title");
    }

    /**
     * Move the given filter one position up
     *
     * @param filterId
     * @return
     */
    public SearchManagerPage moveFilterUp(String filterId)
    {
        getFilterById(filterId).findElement(FILTER_REORDER_UP).click();
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
        getFilterById(filterId).findElement(FILTER_REORDER_DOWN).click();
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
                this.deleteFilter(filters.get(i).findElement(FILTER_ID).getText());
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
