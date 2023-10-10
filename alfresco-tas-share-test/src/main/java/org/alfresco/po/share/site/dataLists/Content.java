package org.alfresco.po.share.site.dataLists;

import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.browser.WebDriverAware;

@PageObject
public abstract class Content extends WebDriverAware
{
    public boolean areNavigationLinksDisplayed()
    {
        return false;
    }

    public String getCurrentPageNumber()
    {
        return null;
    }

    public void clickNextNavigationItem()
    {
    }

    public void clickPreviousNavigationItem()
    {
    }

    public void clickOnSpecificPage(String number)
    {
    }

    public boolean areItemsSortedByColumnAfterClickingTheColumn(String column)
    {
        return false;
    }

    public boolean isAnyListItemDisplayed()
    {
        return false;
    }

    public boolean isDataListContentDisplayed()
    {
        return false;
    }

    public boolean isSelectAllButtonOptionDisplayed()
    {
        return false;
    }

    public boolean isSelectNoneButtonOptionDisplayed()
    {
        return false;
    }

    public boolean isInvertSelectionButtonOptionEnabled()
    {
        return false;
    }

    public void clickNewItemButton()
    {
    }

    public boolean isListItemDisplayed(List<String> listDetails)
    {
        return false;
    }

    public abstract boolean isNewItemButtonDisplayed();

    public abstract boolean allFilterOptionsAreDisplayed();

    public abstract boolean isSelectButtonDisplayed();

    public abstract boolean isSelectItemsButtonDisplayed();

    public abstract boolean isSelectItemsButtonEnabled();

    public String successfullyCreatedListItemMessage()
    {
        return null;
    }

    public abstract void editItem(List<String> listDetails);

    public abstract void duplicateItem(List<String> listDetails);

    public abstract void deleteItem(List<String> listDetails);

    public abstract String messageDisplayed();

    public abstract boolean duplicatedRows(List<String> listDetails);

    public abstract void clickSelectButton();

    public abstract void clickSelectAllOption();

    public abstract void clickInvertSelectionOption();

    public abstract void clickSelectNoneOption();

    public abstract void clickSelectedItemsButton();

    public abstract void clickDuplicateItemsOption();

    public abstract void clickDeleteItemsOption();

    public abstract void clickDeselectAllItemsOption();
}
