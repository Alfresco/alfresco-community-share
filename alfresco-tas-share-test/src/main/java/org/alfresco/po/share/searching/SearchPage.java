package org.alfresco.po.share.searching;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SearchPage extends SharePage<SearchPage> implements AccessibleByMenuBar
{
    @Autowired
    SearchManagerPage searchManagerPage;

    @FindBy (id = "FCTSRCH_RESULTS_COUNT_LABEL")
    private WebElement numberOfResultsLabel;

    @FindBy (id = "SELECTED_ITEMS_MENU_text")
    private WebElement selectedItemsMenu;

    @FindBy (id = "onActionCopyTo_text")
    private WebElement copyToSelectedItemsOption;

    @FindBy (css = ".propertiesCell .nameAndTitleCell a .value")
    private List<WebElement> resultsDetailedViewList;

    @RenderWebElement
    @FindBy (id = "FCTSRCH_TOP_MENU_BAR_SCOPE_LABEL")
    private WebElement searchInLabel;

    @FindBy (css = "span[class*='confirmationButton'] ")
    private WebElement searchButton;

    @FindBy (css = "[id=FCTSRCH_SCOPE_SELECTION_MENU_text]")
    private WebElement searchInDropdown;

    private By searchInDropdownOptionsSelector = By.cssSelector("div[id='FCTSRCH_SCOPE_SELECTION_MENU_GROUP'] td[class*='dijitMenuItemLabel']");

    @FindBy (css = ".label.alfresco-layout-Twister--open>h3")
    private List<WebElement> filterTypeList;

    @FindBy (css = ".showMore .details")
    private List<WebElement> showMore;

    @FindBy (css = ".showLess .details")
    private List<WebElement> showLess;

    @FindBy (id = "FCTSRCH_SORT_MENU_text")
    private WebElement sortDropdown;

    @FindBy (css = "tr[id*='alfresco_menus_AlfCheckableMenuItem'] td:nth-child(3)")
    private List<WebElement> sortOptions;

    @FindBy (css = ".filterLabel")
    private List<WebElement> allOptions;

    @FindBy (css = "div[id='FCTSRCH_VIEWS_MENU'] img")
    private WebElement viewsDropdown;
    private By viewsDropdownOptionsSelector = By.cssSelector("#DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP .dijitMenuItemLabel");

    @FindBy (css = "[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']")
    private List<WebElement> resultsGalleryViewList;

    @FindBy (css = ".dijitSliderIncrementIconH")
    private WebElement sliderIncrementIcon;

    @FindBy (css = ".dijitSliderDecrementIconH")
    private WebElement sliderDecrementIcon;

    @FindBy (css = ".dijitSliderMoveable")
    private WebElement sliderGalleryView;

    @FindBy (css = ".dateCell .value")
    private List<WebElement> resultModifiedByList;

    @FindBy (css = "#FCTSRCH_CONFIG_PAGE_LINK_text>a")
    private WebElement searchManager;

    @FindBy (css = "#FCTSRCH_SEARCH_RESULT_ACTIONS span[class*='dijitButtonContents']")
    private WebElement actionsLink;

    @FindAll (@FindBy (css = "#FCTSRCH_SEARCH_RESULT_ACTIONS_DROPDOWN tr td[id*='text']"))
    private List<WebElement> actionsOptions;

    @FindBy (id = "FCTSRCH_SEARCH_RESULT")
    public WebElement searchResult;

    @FindAll (@FindBy (css = "span[id*='SELECTOR']"))
    private List<WebElement> checkboxList;

    @FindBy (id = "SELECTED_LIST_ITEMS")
    private WebElement selectedItemsList;

    @FindAll (@FindBy (css = "#SELECTED_LIST_ITEMS_dropdown tr td[id*='text']"))
    private List<WebElement> selectedItemsCheckboxOptions;

    @FindBy (css = "span[id='SELECTED_ITEMS_MENU_text']")
    private WebElement selectedItemsDropdown;

    @FindAll (@FindBy (css = "#SELECTED_ITEMS_ACTIONS_GROUP tr td[id*='text']"))
    private List<WebElement> selectedItemsOptions;

    @FindBy (css = "span#ALF_DELETE_CONTENT_DIALOG_CONFIRMATION")
    public WebElement deleteDialogConfirm;

    @FindBy (css = "span#ALF_DELETE_CONTENT_DIALOG_CANCELLATION")
    public WebElement deleteDialogCancel;

    @FindBy (css = "div[id='SELECTED_LIST_ITEMS'] img")
    private WebElement selectAllButton;

    @FindBy (css = "div[id='SELECTED_LIST_ITEMS_dropdown'] tr[title='All']")
    private WebElement allOption;

    @FindBy (id = "onActionCopyTo_text")
    private WebElement copyToAction;

    @FindAll (@FindBy (css = "tr[class$='dijitMenuItem']"))
    private List<WebElement> optionsList;

    @FindBy (css = "DIV[ID='SELECTED_LIST_ITEMS'] span.alfresco-menus-AlfMenuBarPopup__arrow")
    private WebElement selectedListItemsDropdownArrow;
    private int i;
    private List<WebElement> selectedCheckboxes;
    private By checkboxSelector = By.cssSelector("span[class*='selected']");
    private By nameHighlight = By.cssSelector("tr[id='FCTSRCH_SEARCH_RESULT'] td div span a span.value mark");
    private By contentHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_CONTENT_SNIPPET']/span/span[@class='value']/mark");
    private By descriptionHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_DESCRIPTION']/span/span[@class='value']/mark");
    private By titleHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_TITLE']/span/span[@class='value']/mark");

    @Override
    public String getRelativePath()
    {
        return "share/page/dp/ws/faceted-search#searchTerm=%s&scope=repo&sortField=Relevance";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SearchPage navigateByMenuBar()
    {
        return (SearchPage) renderedPage();
    }

    public String getNumberOfResultsText()
    {
        return numberOfResultsLabel.getText();
    }

    private void clickSelectedItemsMenu()
    {
        browser.waitUntilElementClickable(selectedItemsMenu, 3L).click();
        browser.waitUntilElementVisible(copyToSelectedItemsOption);
    }

    public void clickCopyToSelectedItemsOption()
    {
        clickSelectedItemsMenu();
        copyToSelectedItemsOption.click();
    }

    public boolean isResultFound(String query)
    {
        browser.waitInSeconds(5);
        WebElement webElement = browser.findFirstElementWithValue(resultsDetailedViewList, query);
        if (webElement == null)
        {
            browser.refresh();
            webElement = browser.findFirstElementWithValue(resultsDetailedViewList, query);
        }
        return webElement != null;
    }

    public boolean isResultFoundWithRetry(String query)
    {
        WebElement webElement = browser.findFirstElementWithExactValue(resultsDetailedViewList, query);
        for (int i = 0; i < 5; i++)
        {
            if (webElement != null)
            {
                break;
            } else
            {
                getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchResult, 6);
                webElement = browser.findFirstElementWithExactValue(resultsDetailedViewList, query);
            }
        }
        return browser.isElementDisplayed(webElement);
    }

    public void mouseOverResult(String query)
    {
        for (WebElement result : resultsDetailedViewList)
        {
            if (result.getText().contains(query))
                browser.mouseOver(result);
        }
    }

    /**
     * Verify filters in 'Filter By' section
     *
     * @return displayed filters
     */
    public List<String> getFilterTypeList()
    {
        List<String> filterList = new ArrayList<>();
        for (WebElement aFilterTypeList : filterTypeList)
        {
            filterList.add(aFilterTypeList.getText());
        }
        return filterList;
    }

    /**
     * Check if the given filter is displayed in Filter by panel
     *
     * @param filter
     * @return
     */
    public boolean isFilterTypePresent(String filter)
    {
        return browser.findFirstElementWithValue(filterTypeList, filter) != null;
    }

    /**
     * Check if the given option is displayed in the given filter section
     *
     * @param filterId
     * @param filterOption
     * @return
     */
    public boolean isFilterOptionDisplayed(String filterId, String filterOption)
    {
        WebElement filterElement = browser.findElement(By.id("FCTSRCH_" + filterId));
        return browser.findFirstElementWithValue(filterElement.findElements(By.cssSelector(".filterLabel")), filterOption) != null;
    }

    /**
     * Get the number of items that match the filter criteria
     *
     * @param filterOption
     * @return
     */
    public String getFilterOptionHits(String filterOption)
    {
        return browser.findFirstElementWithValue(allOptions, filterOption).findElement(By.xpath("following-sibling::*[1]")).getText();
    }

    public void clickSortDropdown()
    {
        sortDropdown.click();
    }

    public void clickSearchInDropdown()
    {
        searchInDropdown.click();
    }

    public String getSearchInDropdownSelectedValue()
    {
        return searchInDropdown.getText();
    }

    public boolean isSearchInLabelDisplayed()
    {
        return browser.isElementDisplayed(searchInLabel);
    }

    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton);
    }

    /**
     * Verify presence of following values in 'Search In' dropdown: "All sites"
     * and "Repository"
     *
     * @return 'Search In' dropdown values
     */
    public String getSearchInDropdownValues()
    {
        List<WebElement> searchInDropdownOptions = browser.waitUntilElementsVisible(searchInDropdownOptionsSelector);
        if (!searchInDropdownOptions.get(searchInDropdownOptions.size() - 2).getText().equals("All Sites"))
            return "'All Sites' value not displayed";
        if (!searchInDropdownOptions.get(searchInDropdownOptions.size() - 1).getText().equals("Repository"))
            return "'Repository' value not displayed";
        return "All Sites, Repository";
    }

    /**
     * Select the given option from 'Search In' dropdown
     *
     * @param option
     * @return
     */
    public SearchPage selectOptionFromSearchIn(String option)
    {
        clickSearchInDropdown();
        List<WebElement> searchInDropdownOptions = browser.findDisplayedElementsFromLocator(searchInDropdownOptionsSelector);
        for (WebElement optionSearchIn : searchInDropdownOptions)
        {
            if (optionSearchIn.getText().equals(option))
                optionSearchIn.click();
        }
        return (SearchPage) this.renderedPage();
    }

    public boolean isSortDropdownComplete()
    {
        if (sortOptions.size() == 12)
            if (sortOptions.get(0).getText().equals("Relevance") && sortOptions.get(1).getText().equals("Name") && sortOptions.get(2).getText().equals("Title")
                && sortOptions.get(3).getText().equals("Description") && sortOptions.get(4).getText().equals("Author") && sortOptions.get(5).getText()
                                                                                                                                     .equals("Modifier") && sortOptions.get(6).getText().equals("Modified date") && sortOptions.get(7).getText().equals("Creator") && sortOptions
                .get(8).getText().equals("Created date") && sortOptions.get(9).getText().equals("Size") && sortOptions.get(10).getText().equals("Mime type")
                && sortOptions.get(11).getText().equals("Type"))
                return true;
        return false;
    }

    public void clickViewsDropdown()
    {
        browser.waitInSeconds(3);
        viewsDropdown.click();
    }

    public ArrayList<String> getViewsDropdownOptions()
    {
        browser.waitInSeconds(5);
        List<WebElement> viewsDropdownOptions = browser.findElements(viewsDropdownOptionsSelector);
        ArrayList<String> viewsOptionsText = new ArrayList<>();
        for (WebElement viewsDropdownOption : viewsDropdownOptions)
        {
            viewsOptionsText.add(viewsDropdownOption.getText());
        }
        return viewsOptionsText;
    }

    /**
     * Verify that results are displayed as Detailed View
     */
    public boolean isSearchResultsListInDetailedView()
    {
        try
        {
            browser.waitUntilElementsVisible(By.cssSelector(".propertiesCell .nameAndTitleCell a .value"));
            return resultsDetailedViewList.size() > 0;
        } catch (TimeoutException ex)
        {
            browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector(".propertiesCell .nameAndTitleCell a .value"), 3);
        }
        return resultsDetailedViewList.size() > 0;
    }

    /**
     * Verify that results are displayed as Gallery View
     */
    public boolean isSearchResultsListInGalleryView()
    {
        browser.waitUntilElementVisible(By.cssSelector("[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']"));
        return resultsGalleryViewList.size() > 0;
    }

    public SearchPage clickGalleryView()
    {
        clickViewsDropdown();
        browser.waitInSeconds(2);
        browser.selectOptionFromFilterOptionsList("Gallery View", browser.findElements(viewsDropdownOptionsSelector));
        return (SearchPage) this.renderedPage();
    }

    public SearchPage clickDetailedView()
    {
        browser.waitInSeconds(2);

        clickViewsDropdown();
        //   getBrowser().waitUntilElementVisible(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP']"));
        getBrowser().waitInSeconds(5);
        browser.selectOptionFromFilterOptionsList("Detailed View", browser.findElements(viewsDropdownOptionsSelector));
        //     browser.waitUntilElementDisappears(By.cssSelector("table[class*='alfresco-documentlibrary-AlfGalleryViewSlider']"));
        return (SearchPage) this.renderedPage();
    }

    public boolean isSliderGalleryViewDisplayed()
    {
        return (browser.isElementDisplayed(sliderGalleryView) && browser.isElementDisplayed(sliderIncrementIcon) && browser
            .isElementDisplayed(sliderDecrementIcon));
    }

    /**
     * Click the option from filterBy section
     *
     * @param option   that needs to be selected
     * @param filterBy section from Filter By: creator, mimetype, modifier, created,
     *                 content_size, modified
     */
    public void clickFilterOption(String option, String filterBy)
    {
        By selector = By.cssSelector("div.alfresco-documentlibrary-AlfDocumentFilters[id$='" + filterBy + "'] .filterLabel");
        List<WebElement> filterOptionList = browser.findElements(selector);
        clickShowMore();
        for (WebElement filterOption : filterOptionList)
        {
            if (filterOption.getText().equals(option))
            {
                filterOption.click();
                browser.waitInSeconds(2);
                break;
            }
        }
    }

    public boolean isSearchResultsAsExpected(List<String> expectedResults)
    {
        int counter = 0;
        if (resultsDetailedViewList.size() == expectedResults.size())
            for (int i = 0; i < resultsDetailedViewList.size(); i++)
            {
                for (String expectedResult : expectedResults)
                {
                    if (resultsDetailedViewList.get(i).getText().equals(expectedResult))
                        counter++;
                }
                if (counter == resultsDetailedViewList.size())
                    return true;
            }
        return false;
    }

    public boolean isSearchManagerDisplayed()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(searchManager);
        return browser.isElementDisplayed(searchManager);
    }

    public SearchManagerPage clickSearchManagerLink()
    {
        searchManager.click();
        return (SearchManagerPage) searchManagerPage.renderedPage();
    }

    /**
     * Click on the visible Show More link
     */
    public void clickShowMore()
    {
        if (showMore.size() > 0)
            for (WebElement aShowMore : showMore)
            {
                if (aShowMore.isDisplayed())
                    aShowMore.click();
            }
    }

    /**
     * Click on the visible Show Fewer link
     */
    public void clickShowFewer()
    {
        if (showLess.size() > 0)
            for (WebElement showLes : showLess)
            {
                if (showLes.isDisplayed())
                    showLes.click();
            }
    }

    public int getFilterTypePosition(String filter)
    {
        return browser.findFirstElementWithValue(filterTypeList, filter)
                      .findElements(By.xpath("ancestor::div[contains(@id, 'FCTSRCH_filter_')]/preceding-sibling::*")).size() + 1;
    }

    public boolean isActionsLinkDisplayed()
    {
        browser.waitInSeconds(2);
        return browser.isElementDisplayed(actionsLink);
    }

    private void clickActionsMenuButton(String searchResult)
    {
        browser.mouseOver(browser.findFirstElementWithExactValue(resultsDetailedViewList, searchResult));
        browser.waitUntilElementVisible(actionsLink).click();
    }

    private boolean isActionDisplayed(String searchResult, String optionName)
    {
        clickActionsMenuButton(searchResult);
        WebElement action = browser.findFirstElementWithExactValue(actionsOptions, optionName);
        return browser.isElementDisplayed(action);
    }

    public void clickOptionFromActionsMenu(String searchResult, String optionName)
    {
        if (isActionDisplayed(searchResult, optionName))
        {
            WebElement action = browser.findFirstElementWithExactValue(actionsOptions, optionName);
            action.click();
        }
    }

    public void clickCheckbox(String searchResult)
    {
        if (isResultFound(searchResult))
        {
            for (i = 0; i < checkboxList.size(); i++)
            {
                System.out.print("Position = " + i);
                checkboxList.get(i).click();
                selectedCheckboxes = browser.findDisplayedElementsFromLocator(checkboxSelector);
            }
        }
    }

    public boolean isSelectedItemsListOptionDisplayed(String optionName)
    {
        WebElement action = browser.findFirstElementWithExactValue(selectedItemsCheckboxOptions, optionName);
        return browser.isElementDisplayed(action);
    }

    public void clickOptionFromSelectedItemsListCheckbox(String optionName)
    {
        if (isSelectedItemsListOptionDisplayed(optionName))
        {
            WebElement action = browser.findFirstElementWithExactValue(selectedItemsCheckboxOptions, optionName);
            action.click();
        }
    }

    public void clickSelectedItemsDropdown()
    {
        getBrowser().waitUntilElementClickable(selectedItemsDropdown, 5).click();
        getBrowser().waitUntilElementIsVisibleWithRetry(By.id("SELECTED_ITEMS_MENU_dropdown"), 3);
    }

    public boolean isSelectedItemsOptionDisplayed(String optionName)
    {
        //getBrowser().waitUntilElementsVisible(selectedItemsOptions);
        WebElement action = browser.findFirstElementWithExactValue(selectedItemsOptions, optionName);
        return browser.isElementDisplayed(action);
    }

    public boolean isCopyToActionPresent()
    {
        return getBrowser().isElementDisplayed(copyToAction);
    }

    public HtmlPage clickCopyTo(HtmlPage page)
    {
        int retryCount = 0;
        if (!isCopyToActionPresent() && retryCount <= 3)
        {
            clickSearchInDropdown();
            copyToAction.click();
            retryCount++;
        }

        return page.renderedPage();
    }

    public void clickOptionFromSelectedItemsDropdown(String optionName)
    {
        if (isSelectedItemsOptionDisplayed(optionName))
        {
            WebElement action = browser.findFirstElementWithExactValue(selectedItemsOptions, optionName);
            action.click();
        }
    }

    public boolean isALLItemsCheckboxChecked()
    {
        return browser
            .isElementDisplayed(By.xpath("//div[@id='SELECTED_LIST_ITEMS']/img[@alt='You have all items selected. Click this icon to deselect all.']"));
    }

    public boolean isNoneItemsCheckboxChecked()
    {
        return browser.isElementDisplayed(By.xpath("//div[@id='SELECTED_LIST_ITEMS']/img[@alt='You have no items selected. Click this icon to select all.']"));
    }

    public String getSelectedItemsState()
    {
        getBrowser().waitInSeconds(2);
        return getBrowser().findElement(By.cssSelector("div[id='SELECTED_ITEMS_MENU']")).getAttribute("aria-disabled");
    }

    public void deleteDocuments(boolean areYouSure)
    {
        getBrowser().waitUntilElementClickable(deleteDialogCancel);

        if (areYouSure)
        {
            while (browser.isElementDisplayed(deleteDialogConfirm))
            {
                deleteDialogConfirm.click();
                browser.waitInSeconds(3);
            }
        } else
        {
            while (browser.isElementDisplayed(deleteDialogCancel))
            {
                deleteDialogCancel.click();
                browser.waitInSeconds(3);
            }
        }
    }

    public void clickSelectAll()
    {
        getBrowser().findElement(By.cssSelector("div[id='SELECTED_LIST_ITEMS'] img")).click();
        browser.waitInSeconds(2);

        browser.mouseOver(allOption);
        allOption.sendKeys(Keys.ENTER);
    }

    public void clickSelectedItemsListDropdownArrow()
    {
        getBrowser().waitUntilElementClickable(selectedListItemsDropdownArrow, 5).click();
    }

    public boolean isNameHighlighted(String name)
    {
        List<WebElement> allNameHighlights = browser.findElements(nameHighlight);
        for (WebElement eachNameHighlight : allNameHighlights)
        {
            if (eachNameHighlight.getText().equals(name))
                return true;
        }
        return false;
    }

    public boolean isContentHighlighted(String content)
    {
        return browser.findElement(contentHighlight).getText().equals(content);
    }

    public boolean isDescriptionHighlighted(String description)
    {
        return browser.findElement(descriptionHighlight).getText().equals(description);
    }

    public boolean isTitleHighlighted(String title)
    {
        return browser.findElement(titleHighlight).getText().equals(title);
    }
}