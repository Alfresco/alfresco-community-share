package org.alfresco.po.share.site.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

@Primary
@PageObject
public class ListItemSelectedContent extends NoListItemSelectedContent
{
    protected static By balloon = By.cssSelector("div[class='bd'] span[class='message']");
    @RenderWebElement
    @FindBy (css = "h2[id$='default-title']")
    private WebElement listTitle;

    @RenderWebElement
    @FindBy (css = "div[id$='default-paginatorBottom']")
    private WebElement paginatorBottom;

    private By successfullyCreatedMessage = By.cssSelector("div[id='message_c'] span[class='message']");
    private By previousNavigationLinkLocator = By.cssSelector("[class*='pg-previous']");
    private By nextNavigationLinkLocator = By.cssSelector("[class*='pg-next']");
    private By currentPageLocator = By.cssSelector("span[class*='current-page']");
    private By dataListDescription = By.cssSelector(".datagrid-description");
    private By selectAllButtonOption = By.cssSelector(".datagrid-bar.flat-button .selectAll");
    private By selectNoneButtonOption = By.cssSelector(".datagrid-bar.flat-button .selectNone");
    private By invertSelectionButtonOption = By.cssSelector(".datagrid-bar.flat-button .selectInvert");
    private By itemDataTable = By.cssSelector("div[class='grid yui-dt'] table");
    private String selectPage = "span[class*='pages'] a[title='Page %s']";

    public ListItemSelectedContent()
    {
        super();
    }

    public boolean allFilterOptionsAreDisplayed()
    {
        String[] expectedValues = { "All", "Recently Added", "Recently Modified", "Created by Me" };
        List<String> list = getFilterOptionsValues();
        if (list.size() != expectedValues.length)
            return false;
        for (int i = 0; i < expectedValues.length; i++)
        {
            if (!list.get(i).equals(expectedValues[i]))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to get the filter options values
     */
    public List<String> getFilterOptionsValues()
    {
        List<WebElement> filterOptions = browser.findElements(By.cssSelector("ul[class='filterLink'] a"));
        List<String> filterOptionsValues = new ArrayList<>(filterOptions.size());
        for (WebElement option : filterOptions)
        {
            filterOptionsValues.add(option.getText());
        }
        return filterOptionsValues;
    }

    public boolean areNavigationLinksDisplayed()
    {
        browser.waitUntilElementIsDisplayedWithRetry(previousNavigationLinkLocator);
        return browser.findElement(previousNavigationLinkLocator).isDisplayed() && browser.findElement(nextNavigationLinkLocator).isDisplayed();
    }

    public void clickNextNavigationItem()
    {
        browser.waitInSeconds(3);
        browser.waitUntilElementVisible(nextNavigationLinkLocator);
        browser.findElement(nextNavigationLinkLocator).click();
        browser.waitInSeconds(3);
    }

    public void clickPreviousNavigationItem()
    {
        browser.waitUntilElementIsDisplayedWithRetry(previousNavigationLinkLocator);
        browser.findElement(previousNavigationLinkLocator).click();
        browser.waitInSeconds(3);
    }

    public String getCurrentPageNumber()
    {
        return browser.findElement(currentPageLocator).getText();
    }

    public void clickOnSpecificPage(String number)
    {
        browser.findElement(By.cssSelector(String.format(selectPage, number))).click();
    }

    public boolean isDataListContentDisplayed()
    {
        return browser.isElementDisplayed(browser.findElement(dataListDescription));
    }

    public boolean isSelectAllButtonOptionDisplayed()
    {
        return browser.findElement(selectAllButtonOption).isEnabled();
    }

    public boolean isSelectNoneButtonOptionDisplayed()
    {
        return browser.findElement(selectNoneButtonOption).isEnabled();
    }

    public boolean isInvertSelectionButtonOptionEnabled()
    {
        return browser.findElement(invertSelectionButtonOption).isEnabled();
    }

    @Override
    public String successfullyCreatedListItemMessage()
    {
        return browser.findElement(successfullyCreatedMessage).getText();
    }

    @Override
    public String messageDisplayed()
    {
        browser.waitUntilElementVisible(balloon);
        return browser.findElement(balloon).getText();
    }

    public void clickSelectButton()
    {
        browser.waitUntilElementVisible(itemDataTable);
        browser.findElement(selectButtonSelector).click();
    }

    public void clickSelectAllOption()
    {
        clickSelectButton();
        browser.waitUntilElementVisible(selectAllButtonOption);
        browser.findElement(selectAllButtonOption).click();
    }

    public void clickInvertSelectionOption()
    {
        clickSelectButton();
        browser.waitUntilElementVisible(invertSelectionButtonOption);
        browser.findElement(invertSelectionButtonOption).click();
    }

    public void clickSelectNoneOption()
    {
        clickSelectButton();
        browser.waitUntilElementVisible(selectNoneButtonOption);
        browser.findElement(selectNoneButtonOption).click();
    }

    public void clickSelectedItemsButton()
    {
        browser.waitUntilElementVisible(itemDataTable);
        browser.findElement(selectItemsButtonSelector).click();
    }

    public void clickDuplicateItemsOption()
    {
        clickSelectedItemsButton();
        browser.waitUntilElementVisible(duplicateItemsSelector);
        browser.findElement(duplicateItemsSelector).click();
    }

    public void clickConfirmDelete()
    {
        browser.waitUntilElementVisible(confirmDeleteButtonSelector);
        browser.findElement(confirmDeleteButtonSelector).click();
    }

    public void clickCancelDelete()
    {
        browser.waitUntilElementVisible(cancelDeleteButtonSelector);
        browser.findElement(cancelDeleteButtonSelector).click();
    }

    public void clickDeleteItemsOption(boolean delete)
    {
        clickSelectedItemsButton();
        browser.waitUntilElementVisible(deleteItemsSelector);
        browser.findElement(deleteItemsSelector).click();
        if (delete)
            clickConfirmDelete();
        else
            clickCancelDelete();
    }

    public void clickDeselectAllItemsOption()
    {
        clickSelectedItemsButton();
        browser.waitUntilElementVisible(deselectAllItemsSelector);
        browser.findElement(deselectAllItemsSelector).click();
    }
}