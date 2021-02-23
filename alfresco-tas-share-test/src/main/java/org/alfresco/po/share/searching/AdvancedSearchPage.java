package org.alfresco.po.share.searching;

import static org.testng.Assert.assertTrue;

import java.util.List;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class AdvancedSearchPage extends SharePage2<AdvancedSearchPage> implements AccessibleByMenuBar
{
    private final By keywordsSearchField = By.cssSelector("input[id$='default-search-text']");
    private final By firstSearchButton = By.cssSelector("button[id$='_default-search-button-1-button']");
    private final By secondSearchButton = By.cssSelector("button[id$='_default-search-button-2-button']");
    private final By lookForDropdownButton = By.cssSelector(".selected-form-button button");
    private final By lookForDropdownOptions = By.cssSelector(".selected-form-button .yuimenuitem");
    private final By nameInput = By.cssSelector("input[id$='prop_cm_name']");
    private final By titleTextarea = By.cssSelector("textarea[id$='prop_cm_title']");
    private final By descriptionTextarea = By.cssSelector("textarea[id$='prop_cm_description']");
    private final By mimetypeDropdown = By.cssSelector("select[id$='prop_mimetype']");
    private final By dateFromPicker = By.cssSelector("a[id$='prop_cm_modified-cntrl-icon-from'] img");
    private final By dateToPicker = By.cssSelector("a[id$='prop_cm_modified-cntrl-icon-to'] img");
    private final By modifierInput = By.cssSelector("input[id$='prop_cm_modifier']");
    private final By lookForDropdownOptionLabel = By.cssSelector(".yuimenuitemlabel");
    private final By lookForDropdownOptionDescription = By.cssSelector(".form-type-description");
    private final By fromDateInputField = By.cssSelector("input[id$='_default_0_prop_cm_modified-cntrl-date-from']");
    private final By toDateInputField = By.cssSelector("input[id$='_default_0_prop_cm_modified-cntrl-date-to']");
    private final By titleBox = By.cssSelector("textarea[id$='_prop_cm_title']");

    public AdvancedSearchPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/advsearch";
    }

    @Override
    public AdvancedSearchPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickAdvancedSearch();
    }

    public AdvancedSearchPage assertAdvancedSearchPageIsOpened()
    {
        assertTrue(getCurrentUrl().contains(getRelativePath()), "Advanced Search page is opened");
        return this;
    }

    public boolean isKeywordsSearchFieldDisplayed()
    {
        return isElementDisplayed(keywordsSearchField);
    }

    public SearchPage clickFirstSearchButton()
    {
        clickElement(firstSearchButton);
        return new SearchPage(webDriver);
    }

    public SearchPage clickSecondSearchButton()
    {
        clickElement(secondSearchButton);
        return new SearchPage(webDriver);
    }

    public void typeKeywords(String keyword)
    {
        clearAndType(findElement(keywordsSearchField), keyword);
    }

    public boolean isTopSearchButtonDisplayed()
    {
        return isElementDisplayed(firstSearchButton);
    }

    public boolean isBottomSearchButtonDisplayed()
    {
        return isElementDisplayed(secondSearchButton);
    }

    public void clickOnLookForDropdown()
    {
        clickElement(lookForDropdownButton);
    }

    public boolean isLookForDropdownOptionDisplayed(String label, String description)
    {
        boolean status = false;
        List<WebElement> dropdownOptions = findElements(lookForDropdownOptions);
        for (WebElement htmlElement : dropdownOptions)
        {
            if (htmlElement.findElement(lookForDropdownOptionLabel).getText().equals(label))
                if (htmlElement.findElement(lookForDropdownOptionDescription).getText().equals(description))
                {
                    status = true;
                    break;
                }
        }
        return status;
    }

    public void clickOnLookForDropdownOption(String label)
    {
        List<WebElement> dropdownOptions = findElements(lookForDropdownOptions);
        for (WebElement htmlElement : dropdownOptions)
        {
            if (htmlElement.findElement(lookForDropdownOptionLabel).getText().equals(label))
            {
                htmlElement.click();
                break;
            }
        }
    }

    public boolean isKeywordsInputDisplayed()
    {
        return isElementDisplayed(keywordsSearchField);
    }

    public boolean isNameInputDisplayed()
    {
        return isElementDisplayed(nameInput);
    }

    public boolean isTitleTextareaDisplayed()
    {
        return isElementDisplayed(titleTextarea);
    }

    public boolean isDescriptionTextareaDisplayed()
    {
        return isElementDisplayed(descriptionTextarea);
    }

    public boolean isMimetypeDropDownDisplayed()
    {
        return isElementDisplayed(mimetypeDropdown);
    }

    public boolean isDateFromPickerDisplayed()
    {
        return isElementDisplayed(dateFromPicker);
    }

    public boolean isDateToPickerDisplayed()
    {
        return isElementDisplayed(dateToPicker);
    }

    public boolean isModifierInputDisplayed()
    {
        return isElementDisplayed(modifierInput);
    }

    public void typeName(String name)
    {
        clearAndType(findElement(nameInput), name);
    }

    public void typeTitle(String title)
    {
        clearAndType(findElement(titleTextarea), title);
    }

    public void typeDescription(String description)
    {
        clearAndType(findElement(descriptionTextarea), description);
    }

    public void selectMimetype(String mimetype)
    {
        Select mimeTypeSelect = new Select(findElement(mimetypeDropdown));
        mimeTypeSelect.selectByValue(mimetype);
    }

    public void setFromDate(String dateToBeSet)
    {
        waitUntilElementIsVisible(fromDateInputField);
        clearAndType(findElement(fromDateInputField), dateToBeSet);
    }

    public void setToDate(String dateToBeSet)
    {
        waitUntilElementIsVisible(toDateInputField);
        clearAndType(toDateInputField, dateToBeSet);
    }

    public String getSelectedContentTypeOption()
    {
        return findElement(lookForDropdownButton).getText();
    }

    public void setTitle(String criteria)
    {
        waitUntilElementIsVisible(titleBox);
        clearAndType(titleBox, criteria);
    }
}
