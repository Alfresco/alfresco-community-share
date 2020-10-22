package org.alfresco.po.share.searching;

import java.util.List;

import org.alfresco.common.Utils;
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

/**
 * Created by Claudia Agache on 8/16/2016.
 */
@Primary
@PageObject
public class CreateNewFilterDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (className = "dijitDialogTitle")
    protected WebElement dialogTitle;

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
    @FindBy (css = "span[id$='_OK']")
    protected WebElement saveButton;

    @FindBy (css = "span[id$='_CANCEL_label']")
    protected WebElement cancelButton;

    @FindBy (xpath = "//div[@role='dialog' and not(contains(@style, 'display: none'))]//span[@class='dijitDialogCloseIcon']")
    protected WebElement closeButton;

    @RenderWebElement
    @FindBy (css = "input[name='filterID']")
    private WebElement filterIdInput;

    @RenderWebElement
    @FindBy (css = "input[name='displayName']")
    protected WebElement filterNameInput;

    @FindBy (css = "#FORM_FILTER_ID div.description")
    private WebElement fieldIdDescription;

    @FindBy (css = "#FORM_DISPLAY_NAME div.description")
    private WebElement fieldNameDescription;

    @FindBy (css = "#FORM_FILTER_ID .required")
    private WebElement fieldIdMandatory;

    @FindBy (css = "#FORM_DISPLAY_NAME .required")
    private WebElement fieldNameMandatory;

    protected By requiredMark = By.className("required");
    protected By dropdownOptions = By.className("dijitMenuItemLabel");

    @Autowired
    private SearchManagerPage searchManagerPage;

    @FindBy (css = "#FORM_SCOPED_SITES div.button.add>img")
    private WebElement sitesAddButton;

    public Boolean isDialogDisplayed()
    {
        return browser.isElementDisplayed(dialogTitle) && dialogTitle.getText().equals("Create New Filter");
    }

    public String getFilterNameValue()
    {
        return filterNameInput.getAttribute("value");
    }

    public String getFilterIdDescription()
    {
        return fieldIdDescription.getText();
    }

    public String getFilterNameDescription()
    {
        return fieldNameDescription.getText();
    }

    public boolean isFilterIdMandatory()
    {
        return browser.isElementDisplayed(fieldIdMandatory);
    }

    public boolean isFilterNameMandatory()
    {
        return browser.isElementDisplayed(fieldNameMandatory);
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
    public CreateNewFilterDialog typeFilterId(String value)
    {
        filterIdInput.clear();
        filterIdInput.sendKeys(value);
        return this;
    }

    /**
     * Type value in Filter Name field
     *
     * @param value
     */
    public CreateNewFilterDialog typeFilterName(String value)
    {
        filterNameInput.clear();
        filterNameInput.sendKeys(value);
        return this;
    }

    /**
     * Type value in Number of Filters field
     *
     * @param value
     */
    public void typeNumberOfFilters(String value)
    {
        Utils.clearAndType(noFilters, value);
    }

    /**
     * Type value in Minimum Filter Length field
     *
     * @param value
     */
    public void typeMinimumFilterLength(String value)
    {
        Utils.clearAndType(minFilterLength, value);
    }

    /**
     * Type value in Minimum Required Results field
     *
     * @param value
     */
    public void typeMinimumRequiredResults(String value)
    {
        Utils.clearAndType(minRequiredResults, value);
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
        browser.waitUntilElementHasAttribute(saveButton, "aria-disabled", "false");
        browser.waitUntilElementClickable(saveButton).click();
        searchManagerPage.waitUntilNotificationMessageDisappears();
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
