package org.alfresco.po.share.searching;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class CreateNewFilterDialog extends BaseDialogComponent
{
    private final By dialogBody          = By.cssSelector("div[class^='alfresco-dialog-AlfDialog handleOverflow']");
    private final By dialogTitle         = By.className("dijitDialogTitle");
    private final By showWithSearch      = By.cssSelector("input[name='isEnabled']");
    private final By filterPropertyInput = By.id("FORM_FACET_QNAME_CONTROL");
    private final By editIcon            = By.xpath("//img[@title=\"Edit the current entry\"]");
    private final By selectedSiteDetails = By.className("read-display");
    private final By sitesDropDownArrow  = By.xpath("(//input[@type=\"text\"])[35]");
    private final By sitesNameDropdown   = By.className("dijitReset dijitMenuItemLabel");
    private final By dropButton = By.xpath("(//input[@class=\"dijitReset dijitInputField dijitArrowButtonInner\"])[4]");
    private final By dropdown            = By.xpath("//td[@class=\"dijitReset dijitMenuItemLabel\"]");

    private final By filterPropertyDropdown     = By.id("widget_FORM_FACET_QNAME_CONTROL_dropdown");
    private final By filterPropertyOptions = By.cssSelector("div[id^='FORM_FACET_QNAME_CONTROL_popup'].dijitMenuItem");
    private final By sortByInput                = By.cssSelector("table#FORM_SORTBY_CONTROL  div.dijitButtonText");
    private final By sortByArrow = By.cssSelector("table#FORM_SORTBY_CONTROL input.dijitArrowButtonInner");
    private final By sortByDropdown             = By.id("FORM_SORTBY_CONTROL_dropdown");
    private final By noFilters                  = By.cssSelector("div#FORM_MAX_FILTERS input[role='spinbutton']");
    private final By minFilterLength = By.cssSelector("div#FORM_MIN_FILTER_VALUE_LENGTH input[role='spinbutton']");
    private final By minRequiredResults         = By.cssSelector("div#FORM_HIT_THRESHOLD input[role='spinbutton']");
    private final By filterAvailabilityInput    = By.cssSelector("div#FORM_SCOPE div.dijitButtonText");
    private final By filterAvailabilityArrow    = By.cssSelector("div#FORM_SCOPE input.dijitArrowButtonInner");
    private final By filterAvailabilityDropdown = By.id("FORM_SCOPE_CONTROL_dropdown");
    private final By sitesDoneEditingButton = By.xpath("//div[@id='FORM_SCOPED_SITES']//div[@class='button doneEditing']/img");
    private final By sitesCancelEditingButton = By.xpath("//div[@id='FORM_SCOPED_SITES']//div[@class='button cancelEditing']/img");
    private final By sitesSiteNameArrow = By.xpath("//div[@id='FORM_SCOPED_SITES']//div[@class='edit-display']//input[contains(@class, 'dijitArrowButtonInner')]");
    private final By sitesSiteNameDropdown = By.xpath("//div[contains(@id,'alfresco_forms_controls_') and @class='dijitPopup dijitMenuPopup' and not(contains(@style, 'display: none;'))]");
    private final By currentSitesEntries        = By.cssSelector("div.entries div.read-display");
    private final By saveButton                 = By.cssSelector("span[class*='confirmationButton']");
    private final By cancelButton               = By.cssSelector("span[id$='_CANCEL_label']");
    private final By closeButton = By.xpath("//div[@role='dialog' and not(contains(@style, 'display: none'))]//span[@class='dijitDialogCloseIcon']");
    private final By filterIdInput              = By.cssSelector("input[name='filterID']");
    private final By filterNameInput            = By.cssSelector("input[name='displayName']");
    private final By dropdownOptions            = By.className("dijitMenuItemLabel");
    private final By sitesAddButton             = By.cssSelector("#FORM_SCOPED_SITES div.button.add>img");

    public CreateNewFilterDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isDialogDisplayed()
    {
        return isElementDisplayed(dialogTitle);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public String getFilterNameValue()
    {
        return findElement(filterNameInput).getAttribute("value");
    }

    public String getSortBy()
    {
        return getElementText(sortByInput);
    }

    public String getNoFilters()
    {
        waitInSeconds(3);
        return getElementText(noFilters);
    }
    public String getNumberOfFilters()
    {
        waitInSeconds(3);
        return findElement(noFilters).getAttribute("value");

    }

    public String getMinimumFilterLength()
    {
        return getElementText(minFilterLength);
    }
    public String getMiniFilterLength()
    {
        waitInSeconds(3);
        return findElement(minFilterLength).getAttribute("value");
    }
    public String getMiniRequiredResults()
    {
        waitInSeconds(2);
        return findElement(minRequiredResults).getAttribute("value");
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
        List<WebElement> list = findElements(currentSitesEntries);
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
        findElement(filterAvailabilityArrow);
        WebElement dropdown = waitUntilElementIsVisible(filterAvailabilityDropdown);
        selectOptionFromFilterOptionsList(option, dropdown.findElements(dropdownOptions));
    }

    public void selectFromFilterProperty(String option)
    {
        clearAndType(filterPropertyInput, option);
        waitUntilElementIsVisible(filterPropertyDropdown);
        selectOptionFromFilterOptionsList(option, findElements(filterPropertyOptions));
    }

    public void selectFromSortBy(String option)
    {
        clickElement(sortByArrow);
        WebElement dropdown = waitUntilElementIsVisible(sortByDropdown);
        selectOptionFromFilterOptionsList(option, dropdown.findElements(dropdownOptions));
    }

    public String getSelectedSiteDetail()
    {
        WebElement selectedSite = findElement(selectedSiteDetails);
        return selectedSite.getText();
    }
    public void selectInFilterAvailability(String option)
    {
        clickElement(dropButton);
        for (WebElement dropDownList : findElements(dropdown))
        {
            if (dropDownList.getText().equals(option))
            {
                waitInSeconds(3);
                dropDownList.click();
            }
        }

    }

    public void deselectShowWithSearchResults()
    {
        clickElement(showWithSearch);
        waitInSeconds(3);
    }
    public void clickEditIcon()
    {
        waitInSeconds(3);
        clickElement(editIcon);
        clickElement(sitesDropDownArrow);
    }
    public void editSiteName(String query)
    {
        for (WebElement sitedropdown : findElements(dropdownOptions))
        {
            if (sitedropdown.getText().contains(query))
            {
                waitInSeconds(5);
                sitedropdown.click();
            }

        }
        clickElement(sitesDoneEditingButton);
        waitInSeconds(3);

    }
    public void cancelEditSiteName(String query)
    {
        waitInSeconds(3);
        clickElement(editIcon);
        clickElement(sitesSiteNameArrow);

        for (WebElement sitedropdown : findElements(dropdownOptions))
        {

            if (sitedropdown.getText().contains(query))
            {
                waitInSeconds(3);
                sitedropdown.click();
            }
        }
        waitInSeconds(3);
        clickElement(sitesCancelEditingButton);
    }
    public void cancelAddSiteName(String query)
    {
        waitInSeconds(3);
        clickElement(sitesAddButton);
        clickElement(sitesSiteNameArrow);
        for (WebElement sitedropdown : findElements(dropdownOptions))
        {
            if (sitedropdown.getText().contains(query))
            {
                waitInSeconds(3);
                sitedropdown.click();
            }
        }
        waitInSeconds(3);
        clickElement(sitesCancelEditingButton);
    }




    public CreateNewFilterDialog typeFilterId(String value)
    {
        WebElement filterIdElement = waitUntilElementIsVisible(filterIdInput);
        mouseOver(filterIdElement);
        clickElement(filterIdElement);
        clearAndType(filterIdElement, value);
        return this;
    }

    public CreateNewFilterDialog typeFilterName(String value)
    {
        WebElement filterNameElement = waitUntilElementIsVisible(filterNameInput);
        mouseOver(filterNameElement);
        clickElement(filterNameElement);
        clearAndType(filterNameElement, value);
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
            clickElement(showWithSearch);
        }
    }

    public boolean isShowWithSearchResultsChecked()
    {
        return findElement(showWithSearch).isSelected();
    }

    public SearchManagerPage clickSave()
    {
        waitInSeconds(1);
        WebElement save = waitUntilElementIsVisible(saveButton);
        mouseOver(save);
        clickElement(save);
        SearchManagerPage searchManagerPage = new SearchManagerPage(webDriver);
        searchManagerPage.waitUntilNotificationMessageDisappears();
        if (isElementDisplayed(dialogBody))
        {
            log.warn("Failed to click Save button");
            clickElement(save);
            searchManagerPage.waitUntilNotificationMessageDisappears();
        }
        return searchManagerPage;
    }

    public SearchManagerPage clickCancel()
    {
        clickElement(cancelButton);
        return new SearchManagerPage(webDriver);
    }

    public void clickClose()
    {
        clickElement(closeButton);
    }

    public void addSite(String siteName)
    {
        clickElement(sitesAddButton);
        waitInSeconds(2);
        clickElement(sitesSiteNameArrow);
        WebElement dropdown = waitUntilElementIsVisible(sitesSiteNameDropdown);
        selectOptionFromFilterOptionsList(siteName, dropdown.findElements(dropdownOptions));
        clickElement(sitesDoneEditingButton);
    }


    public void cancelAddSite(String siteName)
    {
        clickElement(sitesAddButton);
        clickElement(sitesSiteNameArrow);
        WebElement dropdown = waitUntilElementIsVisible(sitesSiteNameDropdown);
        selectOptionFromFilterOptionsList(siteName, dropdown.findElements(dropdownOptions));
        clickElement(sitesCancelEditingButton);
    }

    public void editSite(String siteNameToBeEdited, String newSiteName)
    {
        WebElement siteElement = findFirstElementWithValue(currentSitesEntries, siteNameToBeEdited);
        siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button edit']"));
        clickElement(sitesSiteNameArrow);
        WebElement dropdown = waitUntilElementIsVisible(sitesSiteNameDropdown);
        selectOptionFromFilterOptionsList(newSiteName, dropdown.findElements(dropdownOptions));
        clickElement(sitesDoneEditingButton);
    }

    public void cancelEditSite(String siteNameToBeEdited, String newSiteName)
    {
        WebElement siteElement = findFirstElementWithValue(currentSitesEntries, siteNameToBeEdited);
        clickElement(siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button edit']")));
        clickElement(sitesSiteNameArrow);
        WebElement dropdown = waitUntilElementIsVisible(sitesSiteNameDropdown);
        selectOptionFromFilterOptionsList(newSiteName, dropdown.findElements(dropdownOptions));
        clickElement(sitesCancelEditingButton);
    }

    public void deleteSite(String siteName)
    {
        WebElement siteElement = findFirstElementWithValue(currentSitesEntries, siteName);
        clickElement(siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button delete']")));
    }
}
