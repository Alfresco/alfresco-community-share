package org.alfresco.po.share.searching;

import java.util.List;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CreateNewFilterDialog extends BaseDialogComponent
{
    private final By dialogBody = By.cssSelector("div[class^='alfresco-dialog-AlfDialog handleOverflow']");
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
    private final By saveButton = By.cssSelector("span[class*='confirmationButton']");
    private final By cancelButton = By.cssSelector("span[id$='_CANCEL_label']");
    private final By closeButton = By.xpath("//div[@role='dialog' and not(contains(@style, 'display: none'))]//span[@class='dijitDialogCloseIcon']");
    private final By filterIdInput = By.cssSelector("input[name='filterID']");
    private final By filterNameInput = By.cssSelector( "input[name='displayName']");
    private final By dropdownOptions = By.className("dijitMenuItemLabel");
    private final By sitesAddButton = By.cssSelector("#FORM_SCOPED_SITES div.button.add>img");

    public CreateNewFilterDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public Boolean isDialogDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dialogTitle);
    }

    public String getDialogTitle()
    {
        return webElementInteraction.getElementText(dialogTitle);
    }

    public String getFilterNameValue()
    {
        return webElementInteraction.findElement(filterNameInput).getAttribute("value");
    }

    public String getSortBy()
    {
        return webElementInteraction.getElementText(sortByInput);
    }

    public String getNoFilters()
    {
        return webElementInteraction.getElementText(noFilters);
    }

    public String getMinimumFilterLength()
    {
        return webElementInteraction.getElementText(minFilterLength);
    }

    public String getMinimumRequiredResults()
    {
        return webElementInteraction.getElementText(minRequiredResults);
    }

    public String getFilterAvailability()
    {
        return webElementInteraction.getElementText(filterAvailabilityInput);
    }

    public String[] getCurrentSelectedSites()
    {
        int i = 0;
        List<WebElement> list = webElementInteraction.findElements(currentSitesEntries);
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
        webElementInteraction.findElement(filterAvailabilityArrow);
        WebElement dropdown = webElementInteraction.waitUntilElementIsVisible(filterAvailabilityDropdown);
        webElementInteraction.selectOptionFromFilterOptionsList(option, dropdown.findElements(dropdownOptions));
    }

    public void selectFromFilterProperty(String option)
    {
        webElementInteraction.clearAndType(filterPropertyInput, option);
        webElementInteraction.waitUntilElementIsVisible(filterPropertyDropdown);
        webElementInteraction.selectOptionFromFilterOptionsList(option, webElementInteraction.findElements(filterPropertyOptions));
    }

    public void selectFromSortBy(String option)
    {
        webElementInteraction.clickElement(sortByArrow);
        WebElement dropdown = webElementInteraction.waitUntilElementIsVisible(sortByDropdown);
        webElementInteraction.selectOptionFromFilterOptionsList(option, dropdown.findElements(dropdownOptions));
    }

    public CreateNewFilterDialog typeFilterId(String value)
    {
        WebElement filterIdElement = webElementInteraction.waitUntilElementIsVisible(filterIdInput);
        webElementInteraction.mouseOver(filterIdElement);
        webElementInteraction.clickElement(filterIdElement);
        webElementInteraction.clearAndType(filterIdElement, value);
        return this;
    }

    public CreateNewFilterDialog typeFilterName(String value)
    {
        WebElement filterNameElement = webElementInteraction.waitUntilElementIsVisible(filterNameInput);
        webElementInteraction.mouseOver(filterNameElement);
        webElementInteraction.clickElement(filterNameElement);
        webElementInteraction.clearAndType(filterNameElement, value);
        return this;
    }

    public void typeNumberOfFilters(String value)
    {
        webElementInteraction.clearAndType(noFilters, value);
    }

    public void typeMinimumFilterLength(String value)
    {
        webElementInteraction.clearAndType(minFilterLength, value);
    }

    public void typeMinimumRequiredResults(String value)
    {
        webElementInteraction.clearAndType(minRequiredResults, value);
    }

    public void clickShowWithSearchResults()
    {
        if (!isShowWithSearchResultsChecked())
        {
            webElementInteraction.clickElement(showWithSearch);
        }
    }

    public boolean isShowWithSearchResultsChecked()
    {
        return webElementInteraction.findElement(showWithSearch).isSelected();
    }

    public SearchManagerPage clickSave()
    {
        WebElement save = webElementInteraction.waitUntilElementIsVisible(saveButton);
        webElementInteraction.mouseOver(save);
        webElementInteraction.clickElement(save);
        SearchManagerPage searchManagerPage = new SearchManagerPage(webDriver);
        searchManagerPage.waitUntilNotificationMessageDisappears();
        if(webElementInteraction.isElementDisplayed(dialogBody))
        {
            LOG.error("Failed to click Save button");
            webElementInteraction.clickElement(save);
            searchManagerPage.waitUntilNotificationMessageDisappears();
        }
        return searchManagerPage;
    }

    public SearchManagerPage clickCancel()
    {
        webElementInteraction.clickElement(cancelButton);
        return new SearchManagerPage(webDriver);
    }

    public void clickClose()
    {
        webElementInteraction.clickElement(closeButton);
    }

    public void addSite(String siteName)
    {
        webElementInteraction.clickElement(sitesAddButton);
        webElementInteraction.clickElement(sitesSiteNameArrow);
        WebElement dropdown = webElementInteraction.waitUntilElementIsVisible(sitesSiteNameDropdown);
        webElementInteraction.selectOptionFromFilterOptionsList(siteName, dropdown.findElements(dropdownOptions));
        webElementInteraction.clickElement(sitesDoneEditingButton);
    }

    public void cancelAddSite(String siteName)
    {
        webElementInteraction.clickElement(sitesAddButton);
        webElementInteraction.clickElement(sitesSiteNameArrow);
        WebElement dropdown = webElementInteraction.waitUntilElementIsVisible(sitesSiteNameDropdown);
        webElementInteraction.selectOptionFromFilterOptionsList(siteName, dropdown.findElements(dropdownOptions));
        webElementInteraction.clickElement(sitesCancelEditingButton);
    }

    public void editSite(String siteNameToBeEdited, String newSiteName)
    {
        WebElement siteElement = webElementInteraction.findFirstElementWithValue(currentSitesEntries, siteNameToBeEdited);
        siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button edit']"));
        webElementInteraction.clickElement(sitesSiteNameArrow);
        WebElement dropdown = webElementInteraction.waitUntilElementIsVisible(sitesSiteNameDropdown);
        webElementInteraction.selectOptionFromFilterOptionsList(newSiteName, dropdown.findElements(dropdownOptions));
        webElementInteraction.clickElement(sitesDoneEditingButton);
    }

    public void cancelEditSite(String siteNameToBeEdited, String newSiteName)
    {
        WebElement siteElement = webElementInteraction.findFirstElementWithValue(currentSitesEntries, siteNameToBeEdited);
        webElementInteraction.clickElement(siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button edit']")));
        webElementInteraction.clickElement(sitesSiteNameArrow);
        WebElement dropdown = webElementInteraction.waitUntilElementIsVisible(sitesSiteNameDropdown);
        webElementInteraction.selectOptionFromFilterOptionsList(newSiteName, dropdown.findElements(dropdownOptions));
        webElementInteraction.clickElement(sitesCancelEditingButton);
    }

    public void deleteSite(String siteName)
    {
        WebElement siteElement = webElementInteraction.findFirstElementWithValue(currentSitesEntries, siteName);
        webElementInteraction.clickElement(siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button delete']")));
    }
}
