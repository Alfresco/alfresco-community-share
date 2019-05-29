package org.alfresco.po.share.searching;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Image;
import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.TextBlock;
import ru.yandex.qatools.htmlelements.element.TextInput;

@PageObject
public class AdvancedSearchPage extends SharePage<AdvancedSearchPage> implements AccessibleByMenuBar
{
    @Autowired
    Toolbar toolbar;

    @Autowired
    SearchPage searchPage;

    @RenderWebElement
    @FindBy (css = "input[id$='default-search-text']")
    private TextInput keywordsSearchField;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-search-button-1-button']")
    private Button searchButton1;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-search-button-2-button']")
    private Button searchButton2;

    @FindBy (css = ".selected-form-button button")
    private Button lookForDropdownButton;

    @FindAll (@FindBy (css = ".selected-form-button .yuimenuitem"))
    private List<HtmlElement> lookForDropdownOptions;

    @FindAll (@FindBy (css = "input[id$='prop_cm_name']"))
    private List<TextInput> nameInputList;

    @FindAll (@FindBy (css = "textarea[id$='prop_cm_title']"))
    private List<TextBlock> titleTextareaList;

    @FindAll (@FindBy (css = "textarea[id$='prop_cm_description']"))
    private List<TextBlock> descriptionTextareaList;

    @FindBy (css = "select[id$='prop_mimetype']")
    private Select mimetypeDropdown;

    @FindBy (css = "a[id$='prop_cm_modified-cntrl-icon-from'] img")
    private Image dateFromPicker;

    @FindBy (css = "a[id$='prop_cm_modified-cntrl-icon-to'] img")
    private Image dateToPicker;

    @FindBy (css = "input[id$='prop_cm_modifier']")
    private TextInput modifierInput;

    private By lookForDropdownOptionLabel = By.cssSelector(".yuimenuitemlabel");
    private By lookForDropdownOptionDescription = By.cssSelector(".form-type-description");

    @Override
    public String getRelativePath()
    {
        return "share/page/advsearch";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public AdvancedSearchPage navigateByMenuBar()
    {
        toolbar.clickAdvancedSearch();
        return (AdvancedSearchPage) renderedPage();
    }

    public boolean isKeywordsSearchFieldDisplayed()
    {
        return browser.isElementDisplayed(keywordsSearchField.getWrappedElement());
    }

    public SearchPage click1stSearch()
    {
        searchButton1.click();
        return (SearchPage) searchPage.renderedPage();
    }

    public void typeKeywords(String keyword)
    {
        keywordsSearchField.clear();
        keywordsSearchField.sendKeys(keyword);
    }

    public boolean isTopSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton1.getWrappedElement());
    }

    public boolean isBottomSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton2.getWrappedElement());
    }

    public void clickOnLookForDropdown()
    {
        lookForDropdownButton.click();
    }

    public boolean isLookForDropdownOptionDisplayed(String label, String description)
    {
        boolean status = false;
        for (HtmlElement htmlElement : lookForDropdownOptions)
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
        for (HtmlElement htmlElement : lookForDropdownOptions)
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
        return browser.isElementDisplayed(keywordsSearchField.getWrappedElement());
    }

    public boolean isNameInputDisplayed()
    {
        for (TextInput nameInput : nameInputList)
        {
            if (browser.isElementDisplayed(nameInput.getWrappedElement()))
                return true;
        }
        return false;
    }

    public boolean isTitleTextareaDisplayed()
    {
        for (TextBlock titleTextarea : titleTextareaList)
        {
            if (browser.isElementDisplayed(titleTextarea.getWrappedElement()))
                return true;
        }
        return false;
    }

    public boolean isDescriptionTextareaDisplayed()
    {
        for (TextBlock descriptionTextarea : descriptionTextareaList)
        {
            if (browser.isElementDisplayed(descriptionTextarea.getWrappedElement()))
                return true;
        }
        return false;
    }

    public boolean isMimetypeDropDownDisplayed()
    {
        return browser.isElementDisplayed(mimetypeDropdown.getWrappedElement());
    }

    public boolean isDateFromPickerDisplayed()
    {
        return browser.isElementDisplayed(dateFromPicker.getWrappedElement());
    }

    public boolean isDateToPickerDisplayed()
    {
        return browser.isElementDisplayed(dateToPicker.getWrappedElement());
    }

    public boolean isModifierInputDisplayed()
    {
        return browser.isElementDisplayed(modifierInput.getWrappedElement());
    }

    public void typeName(String name)
    {
        for (TextInput nameInput : nameInputList)
        {
            if (browser.isElementDisplayed(nameInput.getWrappedElement()))
            {
                nameInput.clear();
                nameInput.sendKeys(name);
            }
        }
    }

    public void typeTitle(String title)
    {
        for (TextBlock titleTextarea : titleTextareaList)
        {
            if (browser.isElementDisplayed(titleTextarea.getWrappedElement()))
            {
                titleTextarea.getWrappedElement().clear();
                titleTextarea.getWrappedElement().sendKeys(title);
            }
        }
    }

    public void typeDescription(String description)
    {
        for (TextBlock descriptionTextarea : descriptionTextareaList)
        {
            if (browser.isElementDisplayed(descriptionTextarea.getWrappedElement()))
            {
                descriptionTextarea.getWrappedElement().clear();
                descriptionTextarea.getWrappedElement().sendKeys(description);
            }
        }
    }

    public void selectMimetype(String mimetype)
    {
        mimetypeDropdown.selectByValue(mimetype);
    }

    public void typeModifier(String modifier)
    {
        modifierInput.clear();
        modifierInput.sendKeys(modifier);
    }
}
