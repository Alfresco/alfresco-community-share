package org.alfresco.po.share.searching;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

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

    @FindBy(id = "FCTSRCH_RESULTS_COUNT_LABEL")
    private WebElement numberOfResultsLabel;

    @FindBy(id = "SELECTED_ITEMS_MENU_text")
    private WebElement selectedItemsMenu;

    @FindBy(id = "onActionCopyTo_text")
    private WebElement copyToSelectedItemsOption;

    @FindBy(css = ".propertiesCell .nameAndTitleCell a .value")
    private List<WebElement> resultsDetailedViewList;

    @RenderWebElement
    @FindBy(id = "FCTSRCH_TOP_MENU_BAR_SCOPE_LABEL")
    private WebElement searchInLabel;

    @RenderWebElement
    @FindBy(css = "span[class*='confirmationButton'] ")
    private WebElement searchButton;

    @FindBy(css = "[id=FCTSRCH_SCOPE_SELECTION_MENU_text]")
    private WebElement searchInDropdown;

    private By searchInDropdownOptionsSelector = By.cssSelector("div[id='FCTSRCH_SCOPE_SELECTION_MENU_GROUP'] td[class*='dijitMenuItemLabel']");

    @FindBy(css = ".label.alfresco-layout-Twister--open>h3")
    private List<WebElement> filterTypeList;

    @FindBy(css = ".showMore .details")
    private List<WebElement> showMore;

    @FindBy(css = ".showLess .details")
    private List<WebElement> showLess;

    @FindBy(id = "FCTSRCH_SORT_MENU_text")
    private WebElement sortDropdown;

    @FindBy(css = "tr[id*='alfresco_menus_AlfCheckableMenuItem'] td:nth-child(3)")
    private List<WebElement> sortOptions;

    @FindBy(css = ".filterLabel")
    private List<WebElement> allOptions;

    @FindBy(css = "#FCTSRCH_VIEWS_MENU span[class*='AlfMenuBarPopup__arrow']")
    private WebElement viewsDropdown;

    private By viewsDropdownOptionsSelector = By.cssSelector("#DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP .dijitMenuItemLabel");

    @FindBy(css = "div[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']")
    private List<WebElement> resultsGalleryViewList;

    @FindBy(css = ".dijitSliderIncrementIconH")
    private WebElement sliderIncrementIcon;

    @FindBy(css = ".dijitSliderDecrementIconH")
    private WebElement sliderDecrementIcon;

    @FindBy(css = ".dijitSliderMoveable")
    private WebElement sliderGalleryView;

    @FindBy(css = ".dateCell .value")
    private List<WebElement> resultModifiedByList;

    @FindBy(css = "#FCTSRCH_CONFIG_PAGE_LINK_text>a")
    private WebElement searchManager;

    @FindBy(css = "#FCTSRCH_SEARCH_RESULT_ACTIONS span[class*='dijitButtonContents']")
    private WebElement actionsLink;

    @FindAll(@FindBy(css = "#FCTSRCH_SEARCH_RESULT_ACTIONS_DROPDOWN tr td[id*='text']"))
    private List<WebElement> actionsOptions;

    @FindBy(id = "FCTSRCH_SEARCH_RESULT")
    public WebElement searchResult;

    @FindAll(@FindBy(css = "span[id*='SELECTOR']"))
    private List<WebElement> checkboxList;

    private int i;
    private List<WebElement> selectedCheckboxes;
    private By checkboxSelector = By.cssSelector("span[class*='selected']");

    @Override
    public String getRelativePath()
    {
        return "share/page/dp/ws/faceted-search#searchTerm=%s&scope=repo&sortField=Relevance";
    }

    @SuppressWarnings("unchecked")
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
        selectedItemsMenu.click();
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
            }
            else
            {
                browser.refresh();
                browser.waitUntilElementVisible(searchResult);
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
        // WebElement dropdown =
        // browser.waitUntilElementVisible(searchInDropdownSelector);
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
                    && sortOptions.get(3).getText().equals("Description") && sortOptions.get(4).getText().equals("Author")
                    && sortOptions.get(5).getText().equals("Modifier") && sortOptions.get(6).getText().equals("Modified date")
                    && sortOptions.get(7).getText().equals("Creator") && sortOptions.get(8).getText().equals("Created date")
                    && sortOptions.get(9).getText().equals("Size") && sortOptions.get(10).getText().equals("Mime type")
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
        browser.waitUntilElementsVisible(By.cssSelector(".propertiesCell .nameAndTitleCell a .value"));
        return resultsDetailedViewList.size() > 0;
    }

    /**
     * Verify that results are displayed as Gallery View
     */
    public boolean isSearchResultsListInGalleryView()
    {
        browser.waitUntilElementVisible(By.cssSelector("div[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']"));
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
        clickViewsDropdown();
        browser.waitInSeconds(2);
        browser.selectOptionFromFilterOptionsList("Detailed View", browser.findElements(viewsDropdownOptionsSelector));
        return (SearchPage) this.renderedPage();
    }

    public boolean isSliderGalleryViewDisplayed()
    {
        return (browser.isElementDisplayed(sliderGalleryView) && browser.isElementDisplayed(sliderIncrementIcon)
                && browser.isElementDisplayed(sliderDecrementIcon));
    }

    /**
     * Click the option from filterBy section
     *
     * @param option
     *            that needs to be selected
     * @param filterBy
     *            section from Filter By: creator, mimetype, modifier, created,
     *            content_size, modified
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
            System.out.print("Position = " + i);
            checkboxList.get(i).click();
            selectedCheckboxes = browser.findDisplayedElementsFromLocator(checkboxSelector);
        }
    }
}