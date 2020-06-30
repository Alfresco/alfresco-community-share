package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.TextInput;

@PageObject
public class ConfigureSavedSearchDashletPopUp extends DashletPopUp
{
    @RenderWebElement
    @FindBy (css = "input[id$='searchTerm']")
    protected TextInput searchTermField;

    @FindBy (css = "input[id$='configDialog-title']")
    protected TextInput titleField;

    @FindBy (css = "select[name='limit']")
    protected Select searchLimit;

    @FindBy (css = "select[id$='_default-configDialog-limit']")
    private Select numberOfResultsToDisplay;

    public void setSearchTermField(String searchTerm)
    {
        searchTermField.sendKeys(searchTerm);
    }

    public void setTitleField(String title)
    {
        Utils.clearAndType(titleField, title);
    }

    public void setSearchLimitField(String searchLimit)
    {
        try
        {
            this.searchLimit.selectByValue(searchLimit);
        } catch (NoSuchElementException e)
        {
            throw new PageOperationException("Unable to find the Search Term box.");
        }
    }

    public boolean isSearchTermFieldDisplayed()
    {
        return searchTermField.isDisplayed();
    }

    public boolean isTitleFieldDisplayed()
    {
        return titleField.isDisplayed();
    }

    public boolean isSearchLimitDisplayed()
    {
        return searchLimit.isDisplayed();
    }

    public boolean isLimitValueDisplayed(String expectedValue)
    {
        List<String> availableOptions = new ArrayList<>();
        List<WebElement> actualOptions = numberOfResultsToDisplay.getOptions();
        for (WebElement option : actualOptions)
        {
            availableOptions.add(option.getText());
        }
        return availableOptions.contains(expectedValue);
    }
}
