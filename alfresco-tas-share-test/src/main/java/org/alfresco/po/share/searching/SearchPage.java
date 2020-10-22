package org.alfresco.po.share.searching;


import java.util.*;

import org.alfresco.common.Utils;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.searching.dialogs.SearchCopyMoveDialog;
import org.alfresco.po.share.site.dataLists.Content;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Table;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SearchPage extends SharePage<SearchPage> implements AccessibleByMenuBar
{
    @Autowired
    private SearchManagerPage searchManagerPage;

    @Autowired
    private SearchCopyMoveDialog copyMoveDialog;

    @FindBy(id = "FCTSRCH_SEARCH_RESULT")
    public WebElement searchResult;

    @FindAll (@FindBy (id = "FCTSRCH_SEARCH_RESULT"))
    public List<WebElement> searchResultRows;

    @FindBy (css = "span#ALF_DELETE_CONTENT_DIALOG_CONFIRMATION")
    public WebElement deleteDialogConfirm;

    @FindBy (css = "span#ALF_DELETE_CONTENT_DIALOG_CANCELLATION")
    public WebElement deleteDialogCancel;

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

    @RenderWebElement
    @FindBy (css = "div[id='FCTSRCH_TOP_MENU_BAR']")
    private WebElement searchTopMenu;

    @RenderWebElement
    @FindBy (css = "span[class*='confirmationButton'] span")
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

    @FindBy (css = "div[id='FCTSRCH_SORT_MENU'] span[class$='arrow']")
    private WebElement sortDropdownButton;

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

    @FindBy (css = "div[id='SELECTED_LIST_ITEMS'] img")
    private WebElement selectAllButton;

    @FindBy (css = "div[id='FCTSRCH_SEARCH_ADVICE_NO_RESULTS'] div[title='Search suggestions:']")
    private WebElement noSearchResults;

    @FindBy (id = "onActionCopyTo_text")
    private WebElement copyToAction;

    @FindAll (@FindBy (css = "tr[class$='dijitMenuItem']"))
    private List<WebElement> optionsList;

    @FindBy (css = "img[src*='folder']")
    private WebElement folderResult;

    @FindBy (css = "img[src*='content']")
    private WebElement contentResult;

    @FindBy (id = "FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS")
    private Table resultsTable;

    @FindBy (id = "ALF_DELETE_CONTENT_DIALOG")
    private WebElement confirmDeleteDialog;

    @FindBy (css = "tbody[id=\"FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS\"]")
    private Table searchResults;

    @FindBy (css = "div[id='FCTSRCH_SORT_MENU_dropdown'] tbody[class='dijitReset']")
    private List<WebElement> sortDropdown;

    @FindBy (css = "div[id='FCTSRCH_SORT_ORDER_TOGGLE'] img")
    private WebElement sortOrderToggleButton;

    @FindBy (css = "span[id='FCTSRCH_SEARCH_RESULT_SELECTOR']")
    private WebElement selector;

    @RenderWebElement
    @FindBy (css = "input[id$='_CONTROL']")
    private WebElement inputField;

    @FindBy (css = "span[id='FCTSRCH_SORT_MENU_text']")
    private WebElement currentSortFilter;

    @FindBy (css = "tbody[id='FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS'] tr")
    private List<WebElement> resultsList;

    @FindBy (css = "[id='FCTSRCH_GALLERY_VIEW_ITEMS'] td")
    private List<WebElement> galleryViewResultsList;

    @FindBy (css = "div[class='alfresco-dialog-AlfDialog dialogDisplayed dijitDialog'] span.dijitDialogCloseIcon span")
    private WebElement closeInfo;

    @FindBy (css = "div[id='aikauLightbox'] div[id='aikauLightboxCaption']")
    private WebElement imagePreview;

    @FindBy (css = "span.dijitDialogTitle")
    private WebElement previewTitle;

    @FindAll (@FindBy (css = "span.alfresco-renderers-DateLink"))
    private List<WebElement> modifiedBy;

    @FindBy (css = "div[id='FCTSRCH_CONFIG_PAGE_LINK'] span")
    private WebElement configureSearchButton;

    @FindBy (css = "img[id='aikauCloseButton']")
    private WebElement closePicturePreview;

    @FindBy (css = "div[class*='dialogDisplayed dijitDialog'] span.dijitDialogCloseIcon")
    private WebElement closeFilePreviewButton;

    @FindBy (css = "DIV[ID='SELECTED_LIST_ITEMS'] span.alfresco-menus-AlfMenuBarPopup__arrow")
    private WebElement selectedListItemsDropdownArrow;

    private By highlightedSite = By.cssSelector("span[id='FCTSRCH_SEARCH_RESULT_SITE'] span mark");
    private By highlightedModifiedDate = By.cssSelector("span[id='FCTSRCH_SEARCH_RESULT_DATE'] span mark");

    private int i;
    private List<WebElement> selectedCheckboxes;
    private By checkboxSelector = By.cssSelector("span[class*='selected']");
    private By nameHighlight = By.cssSelector("tr[id='FCTSRCH_SEARCH_RESULT'] td div span a span.value mark");
    private By contentHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_CONTENT_SNIPPET']/span/span[@class='value']/mark");
    private By descriptionHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_DESCRIPTION']/span/span[@class='value']/mark");
    private By titleHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_TITLE']/span/span[@class='value']/mark");
    private By highlight = By.cssSelector("span.inner span mark");

    private By contentName = By.cssSelector(".nameAndTitleCell .value");
    private By contentCheckBox = By.cssSelector("span[class^='alfresco-renderers-Selector']");
    private By noResults =  By.cssSelector(".alfresco-search-NoSearchResults");

    private WebElement docName(String docName)
    {
        return browser.findElement(By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_DISPLAY_NAME']//mark[text()='" + docName + "']"));
    }

    private WebElement fileTypeOption(String option)
    {
        return browser.findElement(By.xpath("//div[@id='FCTSRCH_filter_mimetype']//span[text()='" + option + "']"));
    }

    private WebElement galleryViewItemSelector(String fileName)
    {
        return browser.findElement(By.cssSelector("//table[@id='FCTSRCH_GALLERY_VIEW_ITEMS']//td//div[text()='" + fileName + "']/../.."));
    }

    private WebElement image(String imageName)
    {
        return browser.findElement(By.cssSelector("span[class^='alfresco-renderers-Thumbnail'] img[title='" + imageName + "']"));
    }

    @Override
    public String getRelativePath()
    {
        return "/share/page/dp/ws/faceted-search";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SearchPage navigateByMenuBar()
    {
        return (SearchPage) renderedPage();
    }

    public void waitForPageToLoad()
    {
        browser.waitUntilElementVisible(searchButton, WAIT_60);
    }

    private boolean isContentDisplayed(ContentModel contentModel)
    {
        if(browser.isElementDisplayed(noResults))
        {
            return false;
        }
        else
        {
            return searchResultRows.stream().anyMatch(row ->
                row.findElement(contentName).getText().equals(contentModel.getName()));
        }
    }

    protected WebElement getContentRowResult(ContentModel content)
    {
        for(WebElement row : searchResultRows)
        {
            if(row.findElement(contentName).getText().equals(content.getName()))
            {
                return row;
            }
        }
        throw new NoSuchElementException(String.format("Content %s was not found", content.getName()));
    }

    public SearchPage searchForContentWithRetry(ContentModel contentToFind)
    {
        LOG.info("Search for content {} with retry");
        boolean found = isContentDisplayed(contentToFind);
        int counter = 0;
        while (!found && counter <= WAIT_60)
        {
            Utility.waitToLoopTime(1, String.format("Wait for content to be displayed in search: %s", contentToFind.getName()));
            setSearchExpression(contentToFind.getName());
            clickSearchButton();
            found = isContentDisplayed(contentToFind);
            counter++;
        }
        return this;
    }

    public SearchPage searchWithKeywordAndWaitForContents(String searchExpression, ContentModel... contentModels)
    {
        int counter = 0;
        boolean allFound = false;
        while(!allFound && counter <= WAIT_60)
        {
            Utility.waitToLoopTime(1, String.format("Wait for content to be displayed in search for: %s", searchExpression));
            setSearchExpression(searchExpression);
            clickSearchButton();
            if(browser.isElementDisplayed(noResults))
            {
                counter++;
                continue;
            }
            if(searchResultRows.size() < contentModels.length)
            {
                counter++;
            }
            else
            {
                allFound = true;
            }
        }
        return this;
    }

    public String getNumberOfResultsText()
    {
        return numberOfResultsLabel.getText();
    }

    public Object executeJavaScript(final String js, Object... args)
    {
        if (js == null || js.isEmpty())
        {
            throw new IllegalArgumentException("JS script is required");
        }
        return ((JavascriptExecutor) getBrowser()).executeScript(js, args);
    }

    public void scrollToPageBottom()
    {
        executeJavaScript(
                "window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));", "");
    }

    public void scrollSome(int distance)
    {
        executeJavaScript("window.scrollTo(0," + distance + ");", "");
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
                //getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchResult, 6);
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
        getBrowser().waitUntilElementClickable(sortDropdownButton, 6L);
        sortDropdownButton.click();
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
        browser.waitUntilElementClickable(viewsDropdown).click();
        browser.waitUntilElementVisible(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP']"));
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
        browser.waitUntilElementVisible(By.cssSelector("[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']"));
        return resultsGalleryViewList.size() > 0;
    }

    public SearchPage clickGalleryView()
    {
        clickViewsDropdown();
        browser.waitUntilElementIsPresent(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP'] td[class='dijitReset dijitMenuItemLabel']"));
        browser.selectOptionFromFilterOptionsList("Gallery View", browser.findElements(viewsDropdownOptionsSelector));
        return (SearchPage) this.renderedPage();
    }

    public SearchPage clickDetailedView()
    {
        clickViewsDropdown();
        //getBrowser().waitUntilElementVisible(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP']"));
        browser.waitUntilElementIsPresent(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP'] td[class='dijitReset dijitMenuItemLabel']"));
        browser.selectOptionFromFilterOptionsList("Detailed View", browser.findElements(viewsDropdownOptionsSelector));
        browser.waitUntilElementDisappears(By.cssSelector("table[class*='alfresco-documentlibrary-AlfGalleryViewSlider']"));
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

    public SearchPage assertSearchManagerButtonIsDisplayed()
    {
        LOG.info("Assert search manager button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }

    public SearchPage assertSearchManagerButtonIsNotDisplayed()
    {
        LOG.info("Assert search manager button is NOT displayed");
        Assert.assertFalse(browser.isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }

    public SearchManagerPage clickSearchManagerLink()
    {
        browser.waitUntilElementVisible(searchManager).click();
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
        getBrowser().findElement(By.cssSelector("div[id='SELECTED_LIST_ITEMS']")).click();
        getBrowser().waitUntilElementIsVisibleWithRetry(By.id("SELECTED_LIST_ITEMS_dropdown"), 5);
        getBrowser().waitUntilElementClickable(By.cssSelector("tr[title='All'] td[class*='dijitMenuItemLabel']"), 3).click();
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
        boolean isHighlighted = false;
        if (browser.isElementDisplayed(contentHighlight) == false)
        {
            isHighlighted = false;
            return isHighlighted;
        }
        if (browser.isElementDisplayed(contentHighlight) == true)
        {
            isHighlighted = browser.findElement(contentHighlight).getText().equals(content);
            return isHighlighted;
        }
        return isHighlighted;
    }

    public boolean isDescriptionHighlighted(String description)
    {
        boolean isHighlighted = false;
        if (browser.isElementDisplayed(descriptionHighlight) == false)
        {
            isHighlighted = false;
            return isHighlighted;
        }

        if (browser.isElementDisplayed(descriptionHighlight) == true)
        {
            isHighlighted = browser.findElement(descriptionHighlight).getText().equals(description);
            return isHighlighted;
        }

        return isHighlighted;
    }

    public boolean isTitleHighlighted(String title)
    {
        boolean isHighlighted = false;
        if (browser.isElementDisplayed(titleHighlight) == false)
        {
            isHighlighted = false;
            return isHighlighted;
        }

        if (browser.isElementDisplayed(titleHighlight) == true)
        {
            isHighlighted = browser.findElement(titleHighlight).getText().equals(title);
        }
        return isHighlighted;
    }


    public String getNoSearchResultsText()
    {
        return noSearchResults.getText();
    }

    public boolean isAnyFolderReturnedInResults()
    {
        return browser.isElementDisplayed(folderResult);
    }

    public boolean isAnyFileReturnedInResults()
    {
        return browser.isElementDisplayed(contentResult);
    }

    public boolean isConfirmDeletionDialogDisplayed()
    {
        return browser.isElementDisplayed(confirmDeleteDialog);
    }

    public boolean isSiteHighlighted()
    {
        return browser.isElementDisplayed(highlightedSite);
    }

    public boolean isModifiedOnHighlighted()
    {
        return browser.isElementDisplayed(highlightedModifiedDate);
    }

    public void setSearchExpression(String searchExpression)
    {
        inputField.clear();
        inputField.sendKeys(searchExpression);
    }

    public void clickSearchButton()
    {
        searchButton.click();
    }

    public boolean confirmNoItemIsHighlighted()
    {
        browser.waitUntilElementDeletedFromDom(highlight);
        return browser.isElementDisplayed(highlight);
    }

    public HtmlPage clickContentName(String contentName, HtmlPage page)
    {
        docName(contentName).click();
        return page.renderedPage();
    }

    public HtmlPage clickSortDropdownOption(String sortingOption)
    {
        getBrowser().findFirstDisplayedElement(By.xpath("//div[@id='DOCLIB_SORT_FIELD_SELECT_GROUP']//td[text()='" + sortingOption + "']")).click();
        getBrowser().waitUntilElementContainsText(currentSortFilter, sortingOption);
        return renderedPage();
    }

    public boolean isSortOrderToggleButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(sortOrderToggleButton);
    }

    public void clickToggleButton()
    {
        sortOrderToggleButton.click();
    }

    public boolean isDescendingOrderSet()
    {
        return getBrowser().isElementDisplayed(By.cssSelector("div[title='Change sort order to descending']"));
    }

    public boolean isAscendingOrderSet()
    {
        return getBrowser().isElementDisplayed(By.cssSelector("div[title='Change sort order to ascending']"));
    }

    public String getSortFilter()
    {
        return currentSortFilter.getText().trim();
    }

    public int getResultsListSize()
    {
        LOG.info("List size is: " + resultsList.size());
        return resultsList.size();
    }

    public boolean areResultsSortedByName()
    {
        boolean status;
        List<String> resultsListText = new ArrayList<>();
        List<WebElement> resultsName = browser.findElements(By.cssSelector("tr[id='FCTSRCH_SEARCH_RESULT'] a[class^='alfresco-navigation'] span.value"));
        List<String> sortedList = new ArrayList<>();
        for (WebElement item : resultsName)
        {
            resultsListText.add(item.getText());
            sortedList.add(item.getText());
        }
        Collections.sort(sortedList);
        LOG.info("Actual list: " + resultsListText);
        LOG.info("Sorted list: " + sortedList);

        if (!resultsListText.equals(sortedList))
        {
            status = false;
        } else
        {
            status = true;
        }
        return status;
    }

    public List<String> getAvailableFilters()
    {
        List<String> availableOptions = new ArrayList<>();
        getBrowser().waitUntilElementClickable(sortDropdownButton);
        sortDropdownButton.click();
        for (WebElement option : sortOptions)
        {
            availableOptions.add(option.getText());
        }

        LOG.info("Available options are: " + availableOptions);

        return availableOptions;
    }

    public HtmlPage selectFileTypeFilter(String fileType, HtmlPage page)
    {
        fileTypeOption(fileType).click();
        return page.renderedPage();
    }

    public int getGalleryViewResultsNumber()
    {
        LOG.info("Results number is: " + galleryViewResultsList.size());
        return galleryViewResultsList.size();
    }

    public HtmlPage openFileFromGalleryView(String fileName, HtmlPage page)
    {
        browser.findFirstElementWithExactValue(galleryViewResultsList, fileName).click();
        return page.renderedPage();
    }

    public void clickContentThumbnailByName(String contentName)
    {
        image(contentName).click();
    }

    public String getPreviewedImageName()
    {
        return imagePreview.getText();
    }

    public boolean isContentPreviewed(String docName)
    {
        return previewTitle.getText().equals(docName);
    }

    public boolean isConfigureSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(configureSearchButton);
    }

    public HtmlPage clickConfigureButton(HtmlPage page)
    {
        configureSearchButton.click();
        return page.renderedPage();
    }

    public HtmlPage clickModifiedBy(String modifierName, HtmlPage page)
    {
        browser.findFirstElementWithValue(modifiedBy, modifierName).click();
        return page.renderedPage();
    }

    public void closePicturePreview()
    {
        closePicturePreview.click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("img[id='aikauLightboxImage']"));
    }

    public void closeFilePreview()
    {
        closeFilePreviewButton.click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("div[class$='dialogDisplayed dijitDialog']"));
    }

    public SearchPage checkContent(ContentModel... contentModels)
    {
        for(ContentModel content : contentModels)
        {
            LOG.info(String.format("Check content: %s", content.getName()));
            getContentRowResult(content).findElement(contentCheckBox).click();
        }
        return this;
    }

    public SearchPage clickSelectedItems()
    {
        LOG.info("Click Selected Items...");
        browser.waitUntilElementClickable(selectedItemsMenu).click();
        return this;
    }

    public SearchCopyMoveDialog clickCopyToForSelectedItems()
    {
        LOG.info("Click Copy to... from Selected Items");
        browser.waitUntilElementVisible(copyToSelectedItemsOption).click();
        return (SearchCopyMoveDialog) copyMoveDialog.renderedPage();
    }

    public SearchResultContentAction usingContent(ContentModel contentModel)
    {
        return new SearchResultContentAction(contentModel, this, copyMoveDialog);
    }
}