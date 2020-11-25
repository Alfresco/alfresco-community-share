package org.alfresco.po.share.searching;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class AdvancedSearchPage extends SharePage2<AdvancedSearchPage> implements AccessibleByMenuBar
{
    @RenderWebElement
    private By keywordsSearchField = By.cssSelector("input[id$='default-search-text']");
    @RenderWebElement
    private By searchButton1 = By.cssSelector("button[id$='_default-search-button-1-button']");
    @RenderWebElement
    private By searchButton2 = By.cssSelector("button[id$='_default-search-button-2-button']");
    private By lookForDropdownButton = By.cssSelector(".selected-form-button button");
    private By lookForDropdownOptions = By.cssSelector(".selected-form-button .yuimenuitem");
    private By nameInput = By.cssSelector("input[id$='prop_cm_name']");
    private By titleTextarea = By.cssSelector("textarea[id$='prop_cm_title']");
    private By descriptionTextarea = By.cssSelector("textarea[id$='prop_cm_description']");
    private By mimetypeDropdown = By.cssSelector("select[id$='prop_mimetype']");
    private By dateFromPicker = By.cssSelector("a[id$='prop_cm_modified-cntrl-icon-from'] img");
    private By dateToPicker = By.cssSelector("a[id$='prop_cm_modified-cntrl-icon-to'] img");
    private By modifierInput = By.cssSelector("input[id$='prop_cm_modifier']");
    private By lookForDropdownOptionLabel = By.cssSelector(".yuimenuitemlabel");
    private By lookForDropdownOptionDescription = By.cssSelector(".form-type-description");
    private By fromDateInputField = By.cssSelector("input[id$='_default_0_prop_cm_modified-cntrl-date-from']");
    private By toDateInputField = By.cssSelector("input[id$='_default_0_prop_cm_modified-cntrl-date-to']");
    private By nameInputField = By.cssSelector("input[id$='_prop_cm_name']");
    private By descriptionBox = By.cssSelector("textarea[id$='prop_cm_description']");
    private By titleBox = By.cssSelector("textarea[id$='_prop_cm_title']");

    public AdvancedSearchPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/advsearch";
    }

    @Override
    public AdvancedSearchPage navigateByMenuBar()
    {
        return (AdvancedSearchPage) new Toolbar(browser).clickAdvancedSearch().renderedPage();
    }

    public AdvancedSearchPage assertAdvancedSearchPageIsOpened()
    {
        assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "Advanced Search page is opened");
        return this;
    }

    public boolean isKeywordsSearchFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(keywordsSearchField);
    }

    public SearchPage click1stSearch()
    {
        getBrowser().waitUntilElementClickable(searchButton1).click();
        return (SearchPage) new SearchPage(browser).renderedPage();
    }

    public SearchPage click2ndSearchButton()
    {
        getBrowser().waitUntilElementClickable(searchButton2).click();
        return (SearchPage) new SearchPage(browser).renderedPage();
    }

    public void typeKeywords(String keyword)
    {
        clearAndType(getBrowser().findElement(keywordsSearchField), keyword);
    }

    public boolean isTopSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(searchButton1);
    }

    public boolean isBottomSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(searchButton2);
    }

    public void clickOnLookForDropdown()
    {
        getBrowser().waitUntilElementClickable(lookForDropdownButton).click();
    }

    public boolean isLookForDropdownOptionDisplayed(String label, String description)
    {
        boolean status = false;
        List<WebElement> dropdownOptions = getBrowser().findElements(lookForDropdownOptions);
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
        List<WebElement> dropdownOptions = getBrowser().findElements(lookForDropdownOptions);
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
        return getBrowser().isElementDisplayed(keywordsSearchField);
    }

    public boolean isNameInputDisplayed()
    {
        return getBrowser().isElementDisplayed(nameInput);
    }

    public boolean isTitleTextareaDisplayed()
    {
        return getBrowser().isElementDisplayed(titleTextarea);
    }

    public boolean isDescriptionTextareaDisplayed()
    {
        return getBrowser().isElementDisplayed(descriptionTextarea);
    }

    public boolean isMimetypeDropDownDisplayed()
    {
        return getBrowser().isElementDisplayed(mimetypeDropdown);
    }

    public boolean isDateFromPickerDisplayed()
    {
        return getBrowser().isElementDisplayed(dateFromPicker);
    }

    public boolean isDateToPickerDisplayed()
    {
        return getBrowser().isElementDisplayed(dateToPicker);
    }

    public boolean isModifierInputDisplayed()
    {
        return getBrowser().isElementDisplayed(modifierInput);
    }

    public void typeName(String name)
    {
        clearAndType(getBrowser().findElement(nameInput), name);
    }

    public void typeTitle(String title)
    {
        clearAndType(getBrowser().findElement(titleTextarea), title);
    }

    public void typeDescription(String description)
    {
        clearAndType(getBrowser().findElement(descriptionTextarea), description);
    }

    public void selectMimetype(String mimetype)
    {
        Select mimeTypeSelect = new Select(getBrowser().findElement(mimetypeDropdown));
        mimeTypeSelect.selectByValue(mimetype);
    }

    public void typeModifier(String modifier)
    {
        clearAndType(getBrowser().findElement(modifierInput), modifier);
    }

    public void setFromDate(String dateToBeSet)
    {
        getBrowser().waitUntilElementVisible(fromDateInputField);
        clearAndType(getBrowser().findElement(fromDateInputField), dateToBeSet);
    }

    public void setToDate(String dateToBeSet)
    {
        getBrowser().waitUntilElementVisible(toDateInputField);
        clearAndType(getBrowser().findElement(toDateInputField), dateToBeSet);
    }

    public String getSelectedContentTypeOption()
    {
        return getBrowser().findElement(lookForDropdownButton).getText();
    }

    public void setTitle(String criteria)
    {
        getBrowser().waitUntilElementVisible(titleBox);
        clearAndType(getBrowser().findElement(titleBox), criteria);
    }
}
