package org.alfresco.po.share.searching;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import ru.yandex.qatools.htmlelements.element.CheckBox;
import ru.yandex.qatools.htmlelements.element.TextInput;

import java.util.List;

/**
 * Created by Claudia Agache on 8/16/2016.
 */
@Primary
@PageObject
public class CreateNewFilterPopup extends ShareDialog
{
    @Autowired
    SearchManagerPage searchManagerPage;

    @RenderWebElement
    @FindBy (className = "dijitDialogTitle")
    protected WebElement dialogTitle;

    @FindBy (id = "FORM_FILTER_ID")
    protected WebElement filterId;

    @FindBy (id = "FORM_DISPLAY_NAME")
    protected WebElement filterName;

    @FindBy (css = "input[name='isEnabled']")
    protected CheckBox showWithSearch;

    @FindBy (id = "FORM_FACET_QNAME_CONTROL")
    protected WebElement filterPropertyInput;

    protected By filterPropertyDropdown = By.id("widget_FORM_FACET_QNAME_CONTROL_dropdown");

    @FindBy (css = "div[id^='FORM_FACET_QNAME_CONTROL_popup'].dijitMenuItem")
    protected List<WebElement> filterPropertyOptions;

    @FindBy (css = "table#FORM_SORTBY_CONTROL  div.dijitButtonText")
    protected WebElement sortByInput;

    @FindBy (css = "table#FORM_SORTBY_CONTROL input.dijitArrowButtonInner")
    protected WebElement sortByArrow;

    protected By sortByDropdown = By.id("FORM_SORTBY_CONTROL_dropdown");

    @FindBy (css = "div#FORM_MAX_FILTERS input[role='spinbutton']")
    protected TextInput noFilters;

    @FindBy (css = "div#FORM_MIN_FILTER_VALUE_LENGTH input[role='spinbutton']")
    protected TextInput minFilterLength;

    @FindBy (css = "div#FORM_HIT_THRESHOLD input[role='spinbutton']")
    protected TextInput minRequiredResults;

    @FindBy (css = "div#FORM_SCOPE div.dijitButtonText")
    protected WebElement filterAvailabilityInput;

    @FindBy (css = "div#FORM_SCOPE input.dijitArrowButtonInner")
    protected WebElement filterAvailabilityArrow;

    protected By filterAvailabilityDropdown = By.id("FORM_SCOPE_CONTROL_dropdown");

    @FindBy (css = "#FORM_SCOPED_SITES div.button.add>img")
    private WebElement sitesAddButton;

    @FindBy (xpath = "//div[@id='FORM_SCOPED_SITES']//div[@class='button doneEditing']/img")
    protected WebElement sitesDoneEditingButton;

    @FindBy (xpath = "//div[@id='FORM_SCOPED_SITES']//div[@class='button cancelEditing']/img")
    protected WebElement sitesCancelEditingButton;

    @FindBy (xpath = "//div[@id='FORM_SCOPED_SITES']//div[@class='edit-display']//input[contains(@class, 'dijitArrowButtonInner')]")
    protected WebElement sitesSiteNameArrow;

    protected By sitesSiteNameDropdown = By.xpath("//div[contains(@id,'alfresco_forms_controls_') and @class='dijitPopup dijitMenuPopup' and not(contains(@style, 'display: none;'))]");

    @FindAll (@FindBy (css = "div.entries div.read-display"))
    protected List<WebElement> currentSitesEntries;

    @RenderWebElement
    @FindBy (css = "span[id$='_OK_label']")
    protected WebElement saveButton;

    @FindBy (css = "span[id$='_CANCEL_label']")
    protected WebElement cancelButton;

    @FindBy (xpath = "//div[@role='dialog' and not(contains(@style, 'display: none'))]//span[@class='dijitDialogCloseIcon']")
    protected WebElement closeButton;

    protected By fieldValue = By.cssSelector("input.dijitInputInner+span");
    protected By fieldInput = By.cssSelector("input.dijitInputInner");
    protected By fieldDescription = By.cssSelector("div.description");
    protected By requiredMark = By.className("required");
    protected By dropdownOptions = By.className("dijitMenuItemLabel");

    public Boolean isDialogDisplayed()
    {
        return browser.isElementDisplayed(dialogTitle) && dialogTitle.getText().equals("Create New Filter");
    }

    public String getFilterIdValue()
    {
        return filterId.findElement(fieldValue).getText();
    }

    public String getFilterNameValue()
    {
        return filterName.findElement(fieldInput).getAttribute("value");
    }

    public String getFilterIdDescription()
    {
        return filterId.findElement(fieldDescription).getText();
    }

    public String getFilterNameDescription()
    {
        return filterName.findElement(fieldDescription).getText();
    }

    public boolean isFilterIdMandatory()
    {
        return browser.isElementDisplayed(filterId, requiredMark);
    }

    public boolean isFilterNameMandatory()
    {
        return browser.isElementDisplayed(filterId, requiredMark);
    }

    public String getFilterProperty()
    {
        return filterPropertyInput.getText();
    }

    public String getSortBy()
    {
        return sortByInput.getText();
    }

    public String getNoFilters()
    {
        return noFilters.getText();
    }

    public String getMinimumFilterLength()
    {
        return minFilterLength.getText();
    }

    public String getMinimumRequiredResults()
    {
        return minRequiredResults.getText();
    }

    public String getFilterAvailability()
    {
        return filterAvailabilityInput.getText();
    }

    public String[] getCurrentSelectedSites()
    {
        int i = 0;
        String[] currentSelectedSites = new String[currentSitesEntries.size()];
        for (WebElement entry : currentSitesEntries)
        {
            currentSelectedSites[i] = entry.getText();
            i++;
        }
        return currentSelectedSites;
    }

    /**
     * Select given option from Filter Availability dropdown
     *
     * @param option
     */
    public void selectFromFilterAvailability(String option)
    {
        filterAvailabilityArrow.click();
        WebElement dropdown = browser.waitUntilElementVisible(filterAvailabilityDropdown);
        browser.selectOptionFromFilterOptionsList(option, dropdown.findElements(dropdownOptions));
    }

    /**
     * Select given option from Filter Property dropdown
     *
     * @param option
     */
    public void selectFromFilterProperty(String option)
    {
        filterPropertyInput.clear();
        filterPropertyInput.sendKeys(option);
        browser.waitUntilElementVisible(filterPropertyDropdown);
        browser.selectOptionFromFilterOptionsList(option, filterPropertyOptions);
    }

    /**
     * Select given option from Sort By dropdown
     *
     * @param option
     */
    public void selectFromSortBy(String option)
    {
        sortByArrow.click();
        WebElement dropdown = browser.waitUntilElementVisible(sortByDropdown);
        browser.selectOptionFromFilterOptionsList(option, dropdown.findElements(dropdownOptions));
    }

    /**
     * Type value in Filter Id field
     *
     * @param value
     */
    public void typeFilterId(String value)
    {
        filterId.findElement(fieldInput).clear();
        filterId.findElement(fieldInput).sendKeys(value);
    }

    /**
     * Type value in Filter Name field
     *
     * @param value
     */
    public void typeFilterName(String value)
    {
        filterName.findElement(fieldInput).clear();
        filterName.findElement(fieldInput).sendKeys(value);
    }

    /**
     * Type value in Number of Filters field
     *
     * @param value
     */
    public void typeNumberOfFilters(String value)
    {
        noFilters.clear();
        noFilters.sendKeys(value);
    }

    /**
     * Type value in Minimum Filter Length field
     *
     * @param value
     */
    public void typeMinimumFilterLength(String value)
    {
        minFilterLength.clear();
        minFilterLength.sendKeys(value);
    }

    /**
     * Type value in Minimum Required Results field
     *
     * @param value
     */
    public void typeMinimumRequiredResults(String value)
    {
        minRequiredResults.clear();
        minRequiredResults.sendKeys(value);
    }

    /**
     * Click on the Show With Search Results checkbox
     */
    public void clickShowWithSearchResults()
    {
        if (isShowWithSearchResultsChecked())
        {
            showWithSearch.deselect();
        } else
        {
            showWithSearch.select();
        }
    }

    public boolean isShowWithSearchResultsChecked()
    {
        return showWithSearch.isSelected();
    }

    /**
     * Click on Save button
     *
     * @return
     */
    public SearchManagerPage clickSave()
    {
        //    browser.waitInSeconds(5);
        browser.waitUntilElementClickable(saveButton);
        saveButton.click();
        browser.waitInSeconds(5);
        return (SearchManagerPage) searchManagerPage.renderedPage();
    }

    /**
     * Click on Cancel button
     *
     * @return
     */
    public SearchManagerPage clickCancel()
    {
        cancelButton.click();
        browser.waitInSeconds(2);
        return (SearchManagerPage) searchManagerPage.renderedPage();
    }

    /**
     * Click on Close button
     */
    public void clickClose()
    {
        closeButton.click();
    }

    /**
     * Add site in 'Sites' section which appears after selecting 'Selected Sites' option from 'Filter Availability' dropdown
     *
     * @param siteName
     */
    public void addSite(String siteName)
    {
        sitesAddButton.click();
        browser.waitUntilElementClickable(sitesSiteNameArrow, 30).click();
        WebElement dropdown = browser.waitUntilElementVisible(sitesSiteNameDropdown);
        browser.selectOptionFromFilterOptionsList(siteName, dropdown.findElements(dropdownOptions));
        sitesDoneEditingButton.click();
    }

    /**
     * Cancel Adding site in 'Sites' section which appears after selecting 'Selected Sites' option from 'Filter Availability' dropdown
     *
     * @param siteName
     */
    public void cancelAddSite(String siteName)
    {
        sitesAddButton.click();
        browser.waitUntilElementClickable(sitesSiteNameArrow, 30).click();
        WebElement dropdown = browser.waitUntilElementVisible(sitesSiteNameDropdown);
        browser.selectOptionFromFilterOptionsList(siteName, dropdown.findElements(dropdownOptions));
        sitesCancelEditingButton.click();
    }

    /**
     * Edit current selected site siteNameToBeEdited by changing it with newSiteName
     *
     * @param siteNameToBeEdited
     * @param newSiteName
     */
    public void editSite(String siteNameToBeEdited, String newSiteName)
    {
        WebElement siteElement = browser.findFirstElementWithValue(currentSitesEntries, siteNameToBeEdited);
        siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button edit']")).click();
        browser.waitUntilElementClickable(sitesSiteNameArrow, 30).click();
        WebElement dropdown = browser.waitUntilElementVisible(sitesSiteNameDropdown);
        browser.selectOptionFromFilterOptionsList(newSiteName, dropdown.findElements(dropdownOptions));
        sitesDoneEditingButton.click();
    }

    /**
     * Change current selected site siteNameToBeEdited with newSiteName and then cancel the editing
     *
     * @param siteNameToBeEdited
     * @param newSiteName
     */
    public void cancelEditSite(String siteNameToBeEdited, String newSiteName)
    {
        WebElement siteElement = browser.findFirstElementWithValue(currentSitesEntries, siteNameToBeEdited);
        siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button edit']")).click();
        browser.waitUntilElementClickable(sitesSiteNameArrow, 30).click();
        WebElement dropdown = browser.waitUntilElementVisible(sitesSiteNameDropdown);
        browser.selectOptionFromFilterOptionsList(newSiteName, dropdown.findElements(dropdownOptions));
        sitesCancelEditingButton.click();
    }

    /**
     * Delete the specified site from current selected sites
     *
     * @param siteName
     */
    public void deleteSite(String siteName)
    {
        WebElement siteElement = browser.findFirstElementWithValue(currentSitesEntries, siteName);
        siteElement.findElement(By.xpath("ancestor::*[@class='alfresco-forms-controls-MultipleEntryElementWrapper']//div[@class='button delete']")).click();
    }
}
