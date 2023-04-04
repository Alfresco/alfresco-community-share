package org.alfresco.po.share.searching;

import static org.testng.Assert.assertTrue;

import java.util.List;
import com.beust.ah.A;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;



public class AdvancedSearchPage extends SharePage2<AdvancedSearchPage> implements AccessibleByMenuBar
{
    private final By keywordsSearchField = By.cssSelector("input[id$='default-search-text']");
    private final By firstSearchButtons = By.xpath("(//button[text()='Search'])[1]");

    private final By firstSearchButton = By.cssSelector("button[id$='_default-search-button-1-button']");
    private final By firstSearchButtonAgain = By.cssSelector("#uniqName_12_0_label");
    private final By secondSearchButton = By.cssSelector("button[id$='_default-search-button-2-button']");
    private final By lookForDropdownButton = By.cssSelector(".selected-form-button button");
    private final By lookForDropdownOptions = By.cssSelector(".selected-form-button .yuimenuitem");
    private final By contentNameInput = By.cssSelector("input[id$='prop_cm_name']");

    private final By folderNameInput = By.cssSelector("input[id$='1_prop_cm_name']");
    private final By titleTextarea = By.cssSelector("textarea[id$='prop_cm_title']");
    private final By folderTitleTextarea = By.cssSelector("textarea[id$='1_prop_cm_title']");
    private final By descriptionTextarea = By.cssSelector("textarea[id$='0_prop_cm_description']");
    private final By folderDescriptionTextarea = By.cssSelector("textarea[id$='1_prop_cm_description']");
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
        waitInSeconds(15);
        clickElement(firstSearchButton);
        return new SearchPage(webDriver);
    }
    public SearchPage clickFirstSearchButtonAndRefresh()
    {
        waitInSeconds(15);
        clickElement(firstSearchButton);
        waitInSeconds(2);
        refresh();
        return new SearchPage(webDriver);
    }


    public SearchPage clickSecondSearchButton()
    {
        clickElement(secondSearchButton);
        return new SearchPage(webDriver);
    }

    public AdvancedSearchPage clickOnFirstSearchButtons()
    {

        clickElement(firstSearchButtons);
        waitInSeconds(2);
        return this;
    }


    public void typeKeywords(String keyword)
    {
        clearAndType(findElement(keywordsSearchField), keyword);
    }

    public boolean isTopSearchButtonDisplayed()
    {
        return isElementDisplayed(firstSearchButton);
    }
    public AdvancedSearchPage assertIsTopSearchButtonDisplayed()
    {
        Assert.assertTrue(isTopSearchButtonDisplayed(), "Top search button is displayed");
        return this;
    }
    public AdvancedSearchPage assertPageTitle()
    {
        Assert.assertEquals(getPageTitle(), language.translate("advancedSearchPage.pageTitle"), "Page title");
        return this;
    }

    public boolean isBottomSearchButtonDisplayed()
    {
        return isElementDisplayed(secondSearchButton);
    }
    public AdvancedSearchPage assertIsBottomSearchButtonDisplayed()
    {
        Assert.assertTrue(isBottomSearchButtonDisplayed(), "Bottom search button is displayed");
        return this;
    }

    public AdvancedSearchPage clickOnLookForDropdown()
    {
        waitUntilElementIsVisible(lookForDropdownButton);
        clickElement(lookForDropdownButton);
        return this;
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
    public AdvancedSearchPage assertIsLookForDropdownOptionDisplayed(String label, String description)
    {
        Assert.assertTrue(isLookForDropdownOptionDisplayed(language.translate(label), language.translate(description)));
        assertTrue(getCurrentUrl().contains(getRelativePath()), "Advanced Search page is opened");
        return this;
    }


    public AdvancedSearchPage clickOnLookForDropdownOption(String label)
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
        return this;
    }

    public boolean isKeywordsInputDisplayed()
    {
        waitUntilElementIsVisible(keywordsSearchField);
        return isElementDisplayed(keywordsSearchField);
    }
    public AdvancedSearchPage assertisKeywordsInputDisplayed()
    {
        waitInSeconds(2);
        Assert.assertTrue(isKeywordsInputDisplayed(), "Keywords input is displayed");
        return this;
    }

    public boolean isNameInputDisplayed()
    {
        return isElementDisplayed(contentNameInput);
    }
    public AdvancedSearchPage assertIsTitleTextareaDisplayed()
    {
        Assert.assertTrue(isTitleTextareaDisplayed(), "Title textarea is displayed");
        return this;
    }
    public boolean isFolderNameInputDisplayed()
    {
        return isElementDisplayed(folderNameInput);
    }
    public AdvancedSearchPage assertIsFolderNameInputDisplayed()
    {
        Assert.assertTrue(isFolderNameInputDisplayed(), "Name input is displayed");
        return this;
    }

    public boolean isTitleTextareaDisplayed()
    {
        return isElementDisplayed(titleTextarea);
    }
    public AdvancedSearchPage assertisNameInputDisplayed()
    {
        Assert.assertTrue(isNameInputDisplayed(), "Name input is displayed");
        return this;
    }
    public boolean isFolderTitleTextareaDisplayed()
    {
        return isElementDisplayed(folderTitleTextarea);
    }
    public AdvancedSearchPage assertIsFolderTitleTextareaDisplayed()
    {
        Assert.assertTrue(isFolderTitleTextareaDisplayed(), "Title textarea is displayed");
        return this;
    }

    public boolean isDescriptionTextareaDisplayed()
    {
        return isElementDisplayed(descriptionTextarea);
    }
    public AdvancedSearchPage assertIsDescriptionTextareaDisplayed()
    {
        Assert.assertTrue(isDescriptionTextareaDisplayed(), "Description textarea is displayed");
        return this;
    }
    public boolean isFolderDescriptionTextareaDisplayed()
    {
        return isElementDisplayed(folderDescriptionTextarea);
    }
    public AdvancedSearchPage assertIsFolderDescriptionTextareaDisplayed()
    {
        Assert.assertTrue(isFolderDescriptionTextareaDisplayed(), "Description textarea is displayed");
        return this;
    }

    public boolean isMimetypeDropDownDisplayed()
    {
        return isElementDisplayed(mimetypeDropdown);
    }
    public AdvancedSearchPage assertIsMimetypeDropDownDisplayed()
    {
        Assert.assertTrue(isMimetypeDropDownDisplayed(), "Mimetype input is displayed");
        return this;
    }

    public boolean isDateFromPickerDisplayed()
    {
        return isElementDisplayed(dateFromPicker);
    }
    public AdvancedSearchPage assertIsDateFromPickerDisplayed()
    {
        Assert.assertTrue(isDateFromPickerDisplayed(), "Date From picker is displayed");
        return this;
    }

    public boolean isDateToPickerDisplayed()
    {
        return isElementDisplayed(dateToPicker);
    }
    public AdvancedSearchPage assertIsDateToPickerDisplayed()
    {
        Assert.assertTrue(isDateFromPickerDisplayed(), "Date From picker is displayed");
        return this;
    }

    public boolean isModifierInputDisplayed()
    {
        return isElementDisplayed(modifierInput);
    }
    public AdvancedSearchPage assertIsModifierInputDisplayed()
    {
        Assert.assertTrue(isDateFromPickerDisplayed(), "Date From picker is displayed");
        return this;
    }

    public void typeName(String name)
    {
        clearAndType(findElement(contentNameInput), name);
    }
    public AdvancedSearchPage typeNameFolder(String name)
    {
        clearAndType(findElement(folderNameInput), name);
        waitInSeconds(3);
        return this;
    }

    public AdvancedSearchPage typeTitle(String title)
    {
        clearAndType(findElement(titleTextarea), title);
        waitInSeconds(10);
        return this;
    }
    public void folderTypeTitle(String title)
    {
        clearAndType(findElement(folderTitleTextarea), title);
        waitInSeconds(3);
    }

    public AdvancedSearchPage typeDescription(String description)
    {
        waitUntilElementIsVisible(descriptionTextarea);
        clearAndType(findElement(descriptionTextarea), description);
        waitInSeconds(3);
        return this;
    }
    public AdvancedSearchPage folderTypeDescription(String description)
    {
        waitUntilElementIsVisible(folderDescriptionTextarea);
        clearAndType(findElement(folderDescriptionTextarea), description);
        waitInSeconds(2);
        return this;
    }

    public SearchPage clickFirstSearchButtonAgain()
    {
        waitInSeconds(15);
        clickElement(firstSearchButton);
        refresh();
        return new SearchPage(webDriver);
    }
    public SearchPage clickOnFirstSearchButton()
    {
        waitInSeconds(15);
        clickElement(firstSearchButton);
        waitInSeconds(2);
        clickElement(firstSearchButtonAgain);
        return new SearchPage(webDriver);
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
