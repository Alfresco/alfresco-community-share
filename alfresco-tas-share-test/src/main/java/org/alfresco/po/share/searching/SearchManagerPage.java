package org.alfresco.po.share.searching;

import org.alfresco.po.share.BasePages;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class SearchManagerPage extends BasePages<SearchManagerPage>
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
    @RenderWebElement
    private By createNewFilter = By.id("CREATE_FACET_BUTTON_label");
    private By filters = By.id("SEARCH_CONFIG_FACET_LIST_VIEW_ROW");
    private By filterTableColumns = By.cssSelector("#SEARCH_CONFIG_FACET_LIST_VIEW th>span.label");
    private By filterPropertyDropDownArrow = By.cssSelector("input.dijitReset.dijitInputField.dijitArrowButtonInner");
    private By filterPropertyOptions = By.cssSelector("div[id ='SEARCH_CONFIG_FACET_QNAME_ITEM_0_SELECT_CONTROL_dropdown'] tbody.dijitReset");
    private By notificationMessage = By.cssSelector("div[class^='alfresco-notifications-AlfNotification']");
    private String filterProperty = "//div[contains(@id, 'SEARCH_CONFIG_FACET_QNAME_ITEM_')]//td[text()='%s']";

    public SearchManagerPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
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
            notificationMessageThread.set(getBrowser().waitUntilElementVisible(MESSAGE_LOCATOR, 5).getText());
            getBrowser().waitUntilElementDisappears(notificationMessage);
        }
        catch (TimeoutException exception)
        {
            // do nothing and carry on as this might be expected, meaning that the element might be expected to already disappear
        }
        return notificationMessageThread;
    }

    public SearchManagerPage assertSearchManagerPageIsOpened()
    {
        LOG.info("Assert Search Manager page is opened");
        Assert.assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "Search Manager page is opened");
        return this;
    }

    private WebElement selectFilterProperty(String filterPropertyText)
    {
        return getBrowser().waitUntilElementVisible(By.xpath(String.format(filterProperty, filterPropertyText)));
    }

    private WebElement getFilterRowById(String filterId)
    {
        return getBrowser().findFirstElementWithValue(filters, filterId);
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
        for (WebElement column : getBrowser().findElements(filterTableColumns))
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
        return getBrowser().isElementDisplayed(createNewFilter);
    }

    public boolean isFilterAvailable(String filter)
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(FILTER_ROWS);
        return getBrowser().findFirstElementWithValue(filters, filter) != null;
    }

    public CreateNewFilterDialog createNewFilter()
    {
        getBrowser().findElement(createNewFilter).click();
        return (CreateNewFilterDialog) new CreateNewFilterDialog(browser).renderedPage();
    }

    public CreateNewFilterDialog clickFilterId(String filterId)
    {
        getBrowser().findFirstElementWithValue(FILTER_ID, filterId).click();
        return (CreateNewFilterDialog) new CreateNewFilterDialog(browser).renderedPage();
    }

    public String getFilterName(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_NAME).getText();
    }

    public String getFilterProperty(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_PROPERTY).getText();
    }

    public SearchManagerPage assertFilterPropertyIs(String filterId, String expectedProperty)
    {
        Assert.assertEquals(getFilterProperty(filterId), expectedProperty, String.format("Filter property '%s' is set", expectedProperty));
        return this;
    }

    public String getFilterType(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_TYPE).getText();
    }

    public String getShowWithSearchResults(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_SHOW).getText();
    }

    public String getFilterAvailability(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_AVAILABILITY).getText();
    }

    public boolean isFilterDeletable(String filterId)
    {
        return getFilterRowById(filterId).findElements(FILTER_DELETE_IMAGE).size() != 0;
    }

    public ConfirmDeletionDialog clickDeleteFilter(String filterId)
    {
        getFilterRowById(filterId).findElement(FILTER_DELETE_IMAGE).click();
        return (ConfirmDeletionDialog) new ConfirmDeletionDialog(browser).renderedPage();
    }

    public SearchManagerPage deleteFilter(String filterId)
    {
        clickDeleteFilter(filterId).clickOKButton();
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public SearchManagerPage editFilterName(String filterName, String newFilterName)
    {
        WebElement filter = getBrowser().findFirstElementWithValue(FILTER_NAME, filterName);
        WebElement filterParent = filter.findElement(PARENT);
        getBrowser().mouseOver(filter);
        filterParent.findElement(EDIT_ICON).click();
        filterParent.findElement(EDIT_INPUT).clear();
        filterParent.findElement(EDIT_INPUT).sendKeys(newFilterName);
        filterParent.findElement(EDIT_SAVE).click();
        return (SearchManagerPage) this.renderedPage();
    }

    public SearchManagerPage editFilterProperty(String filterId, String newFilterProperty)
    {
        WebElement filterRow = getFilterRowById(filterId);
        WebElement filterProp = filterRow.findElement(FILTER_PROPERTY);
        WebElement editIcon = filterProp.findElement(EDIT_ICON);
        getBrowser().mouseOver(editIcon);
        getBrowser().waitUntilElementClickable(editIcon).click();
        getBrowser().waitUntilElementVisible(filterPropertyDropDownArrow).click();
        selectFilterProperty(newFilterProperty).click();
        WebElement saveBtn = filterRow.findElement(EDIT_SAVE);
        getBrowser().waitUntilElementClickable(saveBtn).click();
        waitUntilNotificationMessageDisappears();
        getBrowser().waitUntilElementContainsText(filterProp, newFilterProperty);

        return this;
    }

    public SearchManagerPage cancelEditFilterName(String filterName, String newFilterName)
    {
        WebElement filter = getBrowser().findFirstElementWithValue(FILTER_NAME, filterName);
        WebElement filterParent = filter.findElement(PARENT);
        getBrowser().mouseOver(filter);
        filterParent.findElement(EDIT_ICON).click();
        filterParent.findElement(EDIT_INPUT).clear();
        filterParent.findElement(EDIT_INPUT).sendKeys(newFilterName);
        filterParent.findElement(EDIT_CANCEL).click();

        return (SearchManagerPage) this.renderedPage();
    }

    public String getUpTooltipForFilter(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_REORDER_UP).getAttribute("title");
    }

    public String getDownTooltipForFilter(String filterId)
    {
        return getFilterRowById(filterId).findElement(FILTER_REORDER_DOWN).getAttribute("title");
    }

    public SearchManagerPage moveFilterUp(String filterId)
    {
        getFilterRowById(filterId).findElement(FILTER_REORDER_UP).click();
        return (SearchManagerPage) this.renderedPage();
    }

    public SearchManagerPage moveFilterDown(String filterId)
    {
        getFilterRowById(filterId).findElement(FILTER_REORDER_DOWN).click();
        return (SearchManagerPage) this.renderedPage();
    }

    public int getFilterPosition(String filterId)
    {
        return getBrowser().findFirstElementWithValue(FILTER_ID, filterId).findElements(By.xpath("ancestor::tr/preceding-sibling::*")).size() + 1;
    }
}
