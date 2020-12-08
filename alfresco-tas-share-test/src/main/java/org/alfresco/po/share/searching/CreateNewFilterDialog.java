package org.alfresco.po.share.searching;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CreateNewFilterDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By dialogTitle = By.className("dijitDialogTitle");
    private final By showWithSearch = By.cssSelector("input[name='isEnabled']");
    private final By filterPropertyInput = By.id("FORM_FACET_QNAME_CONTROL");
    private final By filterPropertyDropdown = By.id("widget_FORM_FACET_QNAME_CONTROL_dropdown");
    private final By filterPropertyOptions = By.cssSelector("div[id^='FORM_FACET_QNAME_CONTROL_popup'].dijitMenuItem");
    private final By sortByInput = By.cssSelector("table#FORM_SORTBY_CONTROL  div.dijitButtonText");
    private final By sortByArrow = By.cssSelector("table#FORM_SORTBY_CONTROL input.dijitArrowButtonInner");
    private final By sortByDropdown = By.id("FORM_SORTBY_CONTROL_dropdown");
    private final By noFilters = By.cssSelector("div#FORM_MAX_FILTERS input[role='spinbutton']");
    private final By minFilterLength = By.cssSelector("div#FORM_MIN_FILTER_VALUE_LENGTH input[role='spinbutton']");
    private final By minRequiredResults = By.cssSelector("div#FORM_HIT_THRESHOLD input[role='spinbutton']");
    private final By filterAvailabilityInput = By.cssSelector("div#FORM_SCOPE div.dijitButtonText");
    private final By filterAvailabilityArrow = By.cssSelector("div#FORM_SCOPE input.dijitArrowButtonInner");
    private final By filterAvailabilityDropdown = By.id("FORM_SCOPE_CONTROL_dropdown");
    private final By sitesDoneEditingButton = By.xpath("//div[@id='FORM_SCOPED_SITES']//div[@class='button doneEditing']/img");
    private final By sitesCancelEditingButton = By.xpath("//div[@id='FORM_SCOPED_SITES']//div[@class='button cancelEditing']/img");
    private final By sitesSiteNameArrow = By.xpath("//div[@id='FORM_SCOPED_SITES']//div[@class='edit-display']//input[contains(@class, 'dijitArrowButtonInner')]");
    private final By sitesSiteNameDropdown = By.xpath("//div[contains(@id,'alfresco_forms_controls_') and @class='dijitPopup dijitMenuPopup' and not(contains(@style, 'display: none;'))]");
    private final By currentSitesEntries = By.cssSelector("div.entries div.read-display");
    @RenderWebElement
    private final By saveButton = By.cssSelector("span[id$='_OK']");
    private final By cancelButton = By.cssSelector("span[id$='_CANCEL_label']");
    private final By closeButton = By.xpath("//div[@role='dialog' and not(contains(@style, 'display: none'))]//span[@class='dijitDialogCloseIcon']");
    @RenderWebElement
    private final By filterIdInput = By.cssSelector("input[name='filterID']");
    @RenderWebElement
    private final By filterNameInput = By.cssSelector( "input[name='displayName']");
    private final By fieldIdDescription = By.cssSelector("#FORM_FILTER_ID div.description");
    private final By fieldNameDescription = By.cssSelector("#FORM_DISPLAY_NAME div.description");
    private final By fieldIdMandatory = By.cssSelector("#FORM_FILTER_ID .required");
    private final By fieldNameMandatory = By.cssSelector("#FORM_DISPLAY_NAME .required");
    private final By dropdownOptions = By.className("dijitMenuItemLabel");
    private final By sitesAddButton = By.cssSelector("#FORM_SCOPED_SITES div.button.add>img");

    public CreateNewFilterDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public Boolean isDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(dialogTitle);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public String getFilterNameValue()
    {
        return getBrowser().findElement(filterNameInput).getAttribute("value");
    }

    public String getFilterIdDescription()
    {
        return getElementText(fieldIdDescription);
    }

    public String getFilterNameDescription()
    {
        return getElementText(fieldNameDescription);
    }

    public boolean isFilterIdMandatory()
    {
        return getBrowser().isElementDisplayed(fieldIdMandatory);
    }

    public boolean isFilterNameMandatory()
    {
        return getBrowser().isElementDisplayed(fieldNameMandatory);
    }

    public String getFilterProperty()
    {
        return getElementText(filterPropertyInput);
    }

    public String getSortBy()
    {
        return getElementText(sortByInput);
    }

    public String getNoFilters()
    {
        return getElementText(noFilters);
    }

    public String getMinimumFilterLength()
    {
        return getElementText(minFilterLength);
    }

    public String getMinimumRequiredResults()
    {
        return getElementText(minRequiredResults);
    }

    public String getFilterAvailability()
    {
        return getElementText(filterAvailabilityInput);
    }

    public String[] getCurrentSelectedSites()
    {
        int i = 0;
        List<WebElement> list = getBrowser().findElements(currentSitesEntries);
        String[] currentSelectedSites = new String[list.size()];
        for (WebElement entry : list)
        {
            currentSelectedSites[i] = entry.getText();
            i++;
        }
        return currentSelectedSites;
    }

    public void selectFromFilterAvailability(String option)
    {
        getBrowser().findElement(filterAvailabilityArrow).click();
        WebElement dropdown = getBrowser().waitUntilElementVisible(filterAvailabilityDropdown);
        getBrowser().selectOptionFromFilterOptionsList(option, dropdown.findElements(dropdownOptions));
    }

    public void selectFromFilterProperty(String option)
    {
        clearAndType(filterPropertyInput, option);
        getBrowser().waitUntilElementVisible(filterPropertyDropdown);
        getBrowser().selectOptionFromFilterOptionsList(option, getBrowser().findElements(filterPropertyOptions));
    }

    public void selectFromSortBy(String option)
    {
        getBrowser().findElement(sortByArrow).click();
        WebElement dropdown = getBrowser().waitUntilElementVisible(sortByDropdown);
        getBrowser().selectOptionFromFilterOptionsList(option, dropdown.findElements(dropdownOptions));
    }

    public CreateNewFilterDialog typeFilterId(String value)
    {
        clearAndType(filterIdInput, value);
        return this;
    }

    public CreateNewFilterDialog typeFilterName(String value)
    {
        clearAndType(filterNameInput, value);
        return this;
    }

    public void typeNumberOfFilters(String value)
    {
        clearAndType(noFilters, value);
    }

    public void typeMinimumFilterLength(String value)
    {
        clearAndType(minFilterLength, value);
    }

    public void typeMinimumRequiredResults(String value)
    {
        clearAndType(minRequiredResults, value);
    }

    public void clickShowWithSearchResults()
    {
        if (!isShowWithSearchResultsChecked())
        {
            getBrowser().findElement(showWithSearch).click();
        }
    }

    public boolean isShowWithSearchResultsChecked()
    {
        return getBrowser().findElement(showWithSearch).isSelected();
    }

    public SearchManagerPage clickSave()
    {
        WebElement save = getBrowser().waitUntilElementVisible(saveButton);
        getBrowser().waitUntilElementHasAttribute(save, "aria-disabled", "false");
        getBrowser().mouseOver(save);
        save.click();
        SearchManagerPage searchManagerPage = new SearchManagerPage(browser);
        searchManagerPage.waitUntilNotificationMessageDisappears();

        return (SearchManagerPage) searchManagerPage.renderedPage();
    }

    public SearchManagerPage clickCancel()
    {
        getBrowser().findElement(cancelButton).click();
        return (SearchManagerPage) new SearchManagerPage(browser).renderedPage();
    }

    public void clickClose()
    {
        getBrowser().findElement(closeButton).click();
    }

    public void addSite(String siteName)
    {
        getBrowser().findElement(sitesAddButton).click();
        getBrowser().waitUntilElementClickable(sitesSiteNameArrow, 30).click();
        WebElement dropdown = getBrowser().waitUntilElementVisible(sitesSiteNameDropdown);
        getBrowser().selectOptionFromFilterOptionsList(siteName, dropdown.findElements(dropdownOptions));
        getBrowser().findElement(sitesDoneEditingButton).click();
    }

    public void cancelAddSite(String siteName)
    {
        getBrowser().findElement(sitesAddButton).click();
        getBrowser().waitUntilElementClickable(sitesSiteNameArrow, 30).click();
        WebElement dropdown = getBrowser().waitUntilElementVisible(sitesSiteNameDropdown);
        getBrowser().selectOptionFromFilterOptionsList(siteName, dropdown.findElements(dropdownOptions));
        getBrowser().findElement(sitesCancelEditingButton).click();
    }

    public void editSite(String siteNameToBeEdited, String newSiteName)
    {
        WebElement siteElement = getBrowser().findFirstElementWithValue(currentSitesEntries, siteNameToBeEdited);
        siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button edit']")).click();
        getBrowser().waitUntilElementClickable(sitesSiteNameArrow, 30).click();
        WebElement dropdown = getBrowser().waitUntilElementVisible(sitesSiteNameDropdown);
        getBrowser().selectOptionFromFilterOptionsList(newSiteName, dropdown.findElements(dropdownOptions));
        getBrowser().findElement(sitesDoneEditingButton).click();
    }

    public void cancelEditSite(String siteNameToBeEdited, String newSiteName)
    {
        WebElement siteElement = getBrowser().findFirstElementWithValue(currentSitesEntries, siteNameToBeEdited);
        siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button edit']")).click();
        getBrowser().waitUntilElementClickable(sitesSiteNameArrow, 30).click();
        WebElement dropdown = getBrowser().waitUntilElementVisible(sitesSiteNameDropdown);
        getBrowser().selectOptionFromFilterOptionsList(newSiteName, dropdown.findElements(dropdownOptions));
        getBrowser().findElement(sitesCancelEditingButton).click();
    }

    public void deleteSite(String siteName)
    {
        WebElement siteElement = getBrowser().findFirstElementWithValue(currentSitesEntries, siteName);
        siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button delete']")).click();
    }
}
