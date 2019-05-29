package org.alfresco.po.share.dashlet;

import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.NoSuchElementException;
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

    public void setSearchTermField(String searchTerm)
    {
        searchTermField.sendKeys(searchTerm);
    }

    public void setTitleField(String title)
    {
        titleField.sendKeys(title);
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
}
