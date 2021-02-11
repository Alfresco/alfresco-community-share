package org.alfresco.po.share.searching;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.*;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.searching.dialogs.SearchCopyMoveDialog;
import org.alfresco.utility.model.ContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SearchPage extends SharePage2<SearchPage> implements AccessibleByMenuBar
{
    private final By searchResultRows = By.id("FCTSRCH_SEARCH_RESULT");
    private final By deleteDialogConfirm = By.cssSelector("span#ALF_DELETE_CONTENT_DIALOG_CONFIRMATION");
    private final By deleteDialogCancel = By.cssSelector("span#ALF_DELETE_CONTENT_DIALOG_CANCELLATION");
    private final By numberOfResultsLabel = By.id("FCTSRCH_RESULTS_COUNT_LABEL");
    private final By selectedItemsMenu = By.id("SELECTED_ITEMS_MENU_text");
    private final By resultsDetailedViewList = By.cssSelector(".propertiesCell .nameAndTitleCell a .value");
    private final By searchInLabel = By.id("FCTSRCH_TOP_MENU_BAR_SCOPE_LABEL");
    private final By searchButton = By.cssSelector("span[class*='confirmationButton'] span");
    private final By searchInDropdown = By.cssSelector("[id=FCTSRCH_SCOPE_SELECTION_MENU_text]");
    private final By searchInDropdownOptionsSelector = By.cssSelector("div[id='FCTSRCH_SCOPE_SELECTION_MENU_GROUP'] td[class*='dijitMenuItemLabel']");
    private final By filterTypeList = By.cssSelector(".label.alfresco-layout-Twister--open>h3");
    private final By showMore = By.cssSelector(".showMore .details");
    private final By showLess = By.cssSelector(".showLess .details");
    private final By sortDropdownButton = By.cssSelector("div[id='FCTSRCH_SORT_MENU'] span[class$='arrow']");
    private final By sortOptions = By.cssSelector("tr[id*='alfresco_menus_AlfCheckableMenuItem'] td:nth-child(3)");
    private final By allOptions = By.cssSelector(".filterLabel");
    private final By viewsDropdown = By.cssSelector("div[id='FCTSRCH_VIEWS_MENU'] img");
    private final By viewsDropdownOptionsSelector = By.cssSelector("#DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP .dijitMenuItemLabel");
    private final By resultsGalleryViewList = By.cssSelector("[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']");
    private final By sliderIncrementIcon = By.cssSelector(".dijitSliderIncrementIconH");
    private final By sliderDecrementIcon = By.cssSelector(".dijitSliderDecrementIconH");
    private final By sliderGalleryView = By.cssSelector(".dijitSliderMoveable");
    private final By searchManager = By.cssSelector("#FCTSRCH_CONFIG_PAGE_LINK_text>a");
    private final By actionsLink = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS span[class*='dijitButtonContents']");
    private final By checkboxList = By.cssSelector("span[id*='SELECTOR']");
    private final By selectedItemsCheckboxOptions = By.cssSelector("#SELECTED_LIST_ITEMS_dropdown tr td[id*='text']");
    private final By selectedItemsDropdown = By.cssSelector("span[id='SELECTED_ITEMS_MENU_text']");
    private final By selectedItemsOptions = By.cssSelector("#SELECTED_ITEMS_ACTIONS_GROUP tr td[id*='text']");
    private final By noSearchResults = By.cssSelector("div[id='FCTSRCH_SEARCH_ADVICE_NO_RESULTS'] div[title='Search suggestions:']");
    private final By copyToAction = By.id("onActionCopyTo_text");
    private final By folderResult = By.cssSelector("img[src*='folder']");
    private final By contentResult = By.cssSelector("img[src*='content']");
    private final By confirmDeleteDialog = By.id("ALF_DELETE_CONTENT_DIALOG");
    private final By sortOrderToggleButton = By.cssSelector("div[id='FCTSRCH_SORT_ORDER_TOGGLE'] img");
    private final By inputField = By.cssSelector("input[id$='_CONTROL']");
    private final By currentSortFilter = By.cssSelector("span[id='FCTSRCH_SORT_MENU_text']");
    private final By resultsList = By.cssSelector("tbody[id='FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS'] tr");
    private final By galleryViewResultsList = By.cssSelector("[id='FCTSRCH_GALLERY_VIEW_ITEMS'] td");
    private final By imagePreview = By.cssSelector("div[id='aikauLightbox'] div[id='aikauLightboxCaption']");
    private final By previewTitle = By.cssSelector("span.dijitDialogTitle");
    private final By modifiedBy = By.cssSelector("span.alfresco-renderers-DateLink");
    private final By configureSearchButton = By.cssSelector("div[id='FCTSRCH_CONFIG_PAGE_LINK'] span");
    private final By closePicturePreview = By.cssSelector("img[id='aikauCloseButton']");
    private final By closeFilePreviewButton = By.cssSelector("div[class*='dialogDisplayed dijitDialog'] span.dijitDialogCloseIcon");
    private final By selectedListItemsDropdownArrow = By.cssSelector("DIV[ID='SELECTED_LIST_ITEMS'] span.alfresco-menus-AlfMenuBarPopup__arrow");
    private final By highlightedSite = By.cssSelector("span[id='FCTSRCH_SEARCH_RESULT_SITE'] span mark");
    private final By highlightedModifiedDate = By.cssSelector("span[id='FCTSRCH_SEARCH_RESULT_DATE'] span mark");

    private final By checkboxSelector = By.cssSelector("span[class*='selected']");
    private final By nameHighlight = By.cssSelector("tr[id='FCTSRCH_SEARCH_RESULT'] td div span a span.value mark");
    private final By contentHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_CONTENT_SNIPPET']/span/span[@class='value']/mark");
    private final By descriptionHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_DESCRIPTION']/span/span[@class='value']/mark");
    private final By titleHighlight = By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_TITLE']/span/span[@class='value']/mark");
    private final By highlight = By.cssSelector("span.inner span mark");

    private final By contentName = By.cssSelector(".nameAndTitleCell .value");
    private final By contentCheckBox = By.cssSelector("span[class^='alfresco-renderers-Selector']");
    private final By noResults =  By.cssSelector(".alfresco-search-NoSearchResults");

    public SearchPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private WebElement docName(String docName)
    {
        return webElementInteraction.findElement(By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_DISPLAY_NAME']//mark[text()='" + docName + "']"));
    }

    private WebElement fileTypeOption(String option)
    {
        return webElementInteraction.findElement(By.xpath("//div[@id='FCTSRCH_filter_mimetype']//span[text()='" + option + "']"));
    }

    private WebElement image(String imageName)
    {
        return webElementInteraction.findElement(By.cssSelector("span[class^='alfresco-renderers-Thumbnail'] img[title='" + imageName + "']"));
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
        return this;
    }

    public void waitForPageToLoad()
    {
        webElementInteraction.waitUntilElementIsVisible(searchButton);
    }

    private boolean isContentDisplayed(ContentModel contentModel)
    {
        if(webElementInteraction.isElementDisplayed(noResults))
        {
            return false;
        }
        else
        {
            List<WebElement> searchResults = webElementInteraction.findElements(searchResultRows);
            return searchResults.stream().anyMatch(row ->
                row.findElement(contentName).getText().equals(contentModel.getName()));
        }
    }

    protected WebElement getContentRowResult(ContentModel content)
    {
        List<WebElement> searchResults = webElementInteraction.waitUntilElementsAreVisible(searchResultRows);
        for(WebElement row : searchResults)
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
        int retryCount = 0;
        while (retryCount <= RETRY_TIME_80.getValue() && !isContentDisplayed(contentToFind))
        {
            log.warn("Content {} not displayed - retry: {}", contentToFind, retryCount);
            setSearchExpression(contentToFind.getName());
            clickSearchButton();
            waitToLoopTime(WAIT_2.getValue());
            retryCount++;
        }
        return this;
    }

    public SearchPage searchWithKeywordAndWaitForContents(String searchExpression, ContentModel... contentModels)
    {
        int retryCounter = 0;
        boolean allFound = false;
        while(!allFound && retryCounter < RETRY_TIME_80.getValue())
        {
            log.warn("Keyword {} not displayed - retry: {}", searchExpression, retryCounter);
            setSearchExpression(searchExpression);
            clickSearchButton();
            webElementInteraction.waitInSeconds(WAIT_2.getValue());
            if(webElementInteraction.isElementDisplayed(noResults))
            {
                retryCounter++;
                continue;
            }
            if(webElementInteraction.findElements(searchResultRows).size() < contentModels.length)
            {
                retryCounter++;
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
        return webElementInteraction.getElementText(numberOfResultsLabel);
    }

    public Object executeJavaScript(final String js, Object... args)
    {
        if (js == null || js.isEmpty())
        {
            throw new IllegalArgumentException("JS script is required");
        }
        return webElementInteraction.executeJavaScript(js, args);
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
        WebElement webElement = webElementInteraction.findFirstElementWithValue(resultsDetailedViewList, query);
        if (webElement == null)
        {
            webElementInteraction.refresh();
            webElement = webElementInteraction.findFirstElementWithValue(resultsDetailedViewList, query);
        }
        return webElement != null;
    }

    public boolean isResultFoundWithRetry(String query)
    {
        WebElement webElement = webElementInteraction.findFirstElementWithValue(resultsDetailedViewList, query);
        for (int i = 0; i < 5; i++)
        {
            if (webElement != null)
            {
                break;
            }
            else
            {
                List<WebElement> results = webElementInteraction.findElements(resultsDetailedViewList);
                webElement = webElementInteraction.findFirstElementWithExactValue(results, query);
            }
        }
        return webElementInteraction.isElementDisplayed(webElement);
    }

    public void mouseOverResult(String query)
    {
        for (WebElement result : webElementInteraction.findElements(resultsDetailedViewList))
        {
            if (result.getText().contains(query))
                webElementInteraction.mouseOver(result);
        }
    }

    public List<String> getFilterTypeList()
    {
        List<String> filterList = new ArrayList<>();
        for (WebElement aFilterTypeList : webElementInteraction.findElements(filterTypeList))
        {
            filterList.add(aFilterTypeList.getText());
        }
        return filterList;
    }

    public boolean isFilterTypePresent(String filter)
    {
        return webElementInteraction.findFirstElementWithValue(filterTypeList, filter) != null;
    }

    public boolean isFilterOptionDisplayed(String filterId, String filterOption)
    {
        WebElement filterElement = webElementInteraction.findElement(By.id("FCTSRCH_" + filterId));
        return webElementInteraction.findFirstElementWithValue(filterElement.findElements(By.cssSelector(".filterLabel")), filterOption) != null;
    }

    public String getFilterOptionHits(String filterOption)
    {
        return webElementInteraction.findFirstElementWithValue(allOptions, filterOption).findElement(By.xpath("following-sibling::*[1]")).getText();
    }

    public void clickSortDropdown()
    {
        webElementInteraction.clickElement(sortDropdownButton);
    }

    public void clickSearchInDropdown()
    {
        webElementInteraction.clickElement(searchInDropdown);
    }

    public String getSearchInDropdownSelectedValue()
    {
        return  webElementInteraction.findElement(searchInDropdown).getText();
    }

    public boolean isSearchInLabelDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchInLabel);
    }

    public boolean isSearchButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchButton);
    }

    public String getSearchInDropdownValues()
    {
        List<WebElement> searchInDropdownOptions = webElementInteraction.waitUntilElementsAreVisible(searchInDropdownOptionsSelector);
        if (!searchInDropdownOptions.get(searchInDropdownOptions.size() - 2).getText().equals("All Sites"))
            return "'All Sites' value not displayed";
        if (!searchInDropdownOptions.get(searchInDropdownOptions.size() - 1).getText().equals("Repository"))
            return "'Repository' value not displayed";
        return "All Sites, Repository";
    }

    public SearchPage selectOptionFromSearchIn(String option)
    {
        clickSearchInDropdown();
        List<WebElement> searchInDropdownOptions = webElementInteraction.findDisplayedElementsFromLocator(searchInDropdownOptionsSelector);
        for (WebElement optionSearchIn : searchInDropdownOptions)
        {
            if (optionSearchIn.getText().equals(option))
                optionSearchIn.click();
        }
        return this;
    }

    public boolean isSortDropdownComplete()
    {
        List<WebElement> sortList = webElementInteraction.findElements(sortOptions);
        if (sortList.size() == 12)
            return sortList.get(0).getText().equals("Relevance")
                && sortList.get(1).getText().equals("Name")
                && sortList.get(2).getText().equals("Title")
                && sortList.get(3).getText().equals("Description")
                && sortList.get(4).getText().equals("Author")
                && sortList.get(5).getText().equals("Modifier")
                && sortList.get(6).getText().equals("Modified date")
                && sortList.get(7).getText().equals("Creator")
                && sortList.get(8).getText().equals("Created date")
                && sortList.get(9).getText().equals("Size") && sortList.get(10).getText()
                .equals("Mime type")
                && sortList.get(11).getText().equals("Type");
        return false;
    }

    public void clickViewsDropdown()
    {
        webElementInteraction.clickElement(viewsDropdown);
        webElementInteraction.waitUntilElementIsVisible(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP']"));
    }

    public List<String> getViewsDropdownOptions()
    {
        List<WebElement> viewsDropdownOptions = webElementInteraction.findElements(viewsDropdownOptionsSelector);
        ArrayList<String> viewsOptionsText = new ArrayList<>();
        for (WebElement viewsDropdownOption : viewsDropdownOptions)
        {
            viewsOptionsText.add(viewsDropdownOption.getText());
        }
        return viewsOptionsText;
    }

    public boolean isSearchResultsListInDetailedView()
    {
        webElementInteraction.waitUntilElementsAreVisible(By.cssSelector(".propertiesCell .nameAndTitleCell a .value"));
        return !webElementInteraction.findElements(resultsDetailedViewList).isEmpty();
    }

    public boolean isSearchResultsListInGalleryView()
    {
        webElementInteraction.waitUntilElementIsVisible(By.cssSelector("[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']"));
        return !webElementInteraction.findElements(resultsGalleryViewList).isEmpty();
    }

    public SearchPage clickGalleryView()
    {
        clickViewsDropdown();
        webElementInteraction.waitUntilElementIsPresent(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP'] td[class='dijitReset dijitMenuItemLabel']"));
        webElementInteraction.selectOptionFromFilterOptionsList("Gallery View", webElementInteraction.findElements(viewsDropdownOptionsSelector));
        return this;
    }

    public SearchPage clickDetailedView()
    {
        clickViewsDropdown();
        webElementInteraction.waitUntilElementIsPresent(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP'] td[class='dijitReset dijitMenuItemLabel']"));
        webElementInteraction.selectOptionFromFilterOptionsList("Detailed View", webElementInteraction.findElements(viewsDropdownOptionsSelector));
        webElementInteraction.waitUntilElementDisappears(By.cssSelector("table[class*='alfresco-documentlibrary-AlfGalleryViewSlider']"));
        return this;
    }

    public boolean isSliderGalleryViewDisplayed()
    {
        return (webElementInteraction.isElementDisplayed(sliderGalleryView)
                && webElementInteraction.isElementDisplayed(sliderIncrementIcon)
                && webElementInteraction.isElementDisplayed(sliderDecrementIcon));
    }

    public void clickFilterOption(String option, String filterBy)
    {
        By selector = By.cssSelector("div.alfresco-documentlibrary-AlfDocumentFilters[id$='" + filterBy + "'] .filterLabel");
        List<WebElement> filterOptionList = webElementInteraction.findElements(selector);
        clickShowMore();
        for (WebElement filterOption : filterOptionList)
        {
            if (filterOption.getText().equals(option))
            {
                filterOption.click();
                break;
            }
        }
    }

    public boolean isSearchResultsAsExpected(List<String> expectedResults)
    {
        int counter = 0;
        List<WebElement> results = webElementInteraction.findElements(resultsDetailedViewList);
        if (results.size() == expectedResults.size())
            for (int i = 0; i < results.size(); i++)
            {
                for (String expectedResult : expectedResults)
                {
                    if (results.get(i).getText().equals(expectedResult))
                        counter++;
                }
                if (counter == results.size())
                    return true;
            }
        return false;
    }

    public SearchPage assertSearchManagerButtonIsDisplayed()
    {
        log.info("Assert search manager button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }

    public SearchPage assertSearchManagerButtonIsNotDisplayed()
    {
        log.info("Assert search manager button is NOT displayed");
        assertFalse(webElementInteraction.isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }

    public SearchManagerPage clickSearchManagerLink()
    {
        webElementInteraction.clickElement(searchManager);
        return new SearchManagerPage(webDriver);
    }

    public void clickShowMore()
    {
        List<WebElement> showMoreList = webElementInteraction.findElements(showMore);
        if (!showMoreList.isEmpty())
            for (WebElement aShowMore : showMoreList)
            {
                if (aShowMore.isDisplayed())
                    aShowMore.click();
            }
    }

    public void clickShowFewer()
    {
        List<WebElement> showLessList = webElementInteraction.findElements(showLess);
        if (!showLessList.isEmpty())
            for (WebElement showLess : showLessList)
            {
                if (showLess.isDisplayed())
                    showLess.click();
            }
    }

    public int getFilterTypePosition(String filter)
    {
        return webElementInteraction.findFirstElementWithValue(filterTypeList, filter)
            .findElements(By.xpath("ancestor::div[contains(@id, 'FCTSRCH_filter_')]/preceding-sibling::*")).size() + 1;
    }

    public boolean isActionsLinkDisplayed()
    {
        return webElementInteraction.isElementDisplayed(actionsLink);
    }

    public void clickCheckbox(String searchResult)
    {
        if (isResultFound(searchResult))
        {
            List<WebElement> checkBoxElements = webElementInteraction.findElements(checkboxList);
            int i;
            for (i = 0; i < checkBoxElements.size(); i++)
            {
                log.info("Position {} ", i);
                checkBoxElements.get(i).click();
                webElementInteraction.findDisplayedElementsFromLocator(checkboxSelector);
            }
        }
    }

    public boolean isSelectedItemsListOptionDisplayed(String optionName)
    {
        WebElement action = webElementInteraction.findFirstElementWithValue(selectedItemsCheckboxOptions, optionName);
        return webElementInteraction.isElementDisplayed(action);
    }

    public void clickOptionFromSelectedItemsListCheckbox(String optionName)
    {
        if (isSelectedItemsListOptionDisplayed(optionName))
        {
            WebElement action = webElementInteraction.findFirstElementWithValue(selectedItemsCheckboxOptions, optionName);
            webElementInteraction.clickElement(action);
        }
    }

    public void clickSelectedItemsDropdown()
    {
        webElementInteraction.clickElement(selectedItemsDropdown);
        webElementInteraction.waitUntilElementIsVisibleWithRetry(By.id("SELECTED_ITEMS_MENU_dropdown"), 3);
    }

    public boolean isSelectedItemsOptionDisplayed(String optionName)
    {
        WebElement action = webElementInteraction.findFirstElementWithValue(selectedItemsOptions, optionName);
        return webElementInteraction.isElementDisplayed(action);
    }

    public void clickOptionFromSelectedItemsDropdown(String optionName)
    {
        if (isSelectedItemsOptionDisplayed(optionName))
        {
            WebElement action = webElementInteraction.findFirstElementWithValue(selectedItemsOptions, optionName);
            webElementInteraction.clickElement(action);
        }
    }

    public boolean isALLItemsCheckboxChecked()
    {
        return webElementInteraction.isElementDisplayed(By.xpath("//div[@id='SELECTED_LIST_ITEMS']/img[@alt='You have all items selected. Click this icon to deselect all.']"));
    }

    public boolean isNoneItemsCheckboxChecked()
    {
        return webElementInteraction.isElementDisplayed(By.xpath("//div[@id='SELECTED_LIST_ITEMS']/img[@alt='You have no items selected. Click this icon to select all.']"));
    }

    public String getSelectedItemsState()
    {
        webElementInteraction.waitInSeconds(2);
        return webElementInteraction.findElement(By.cssSelector("div[id='SELECTED_ITEMS_MENU']")).getAttribute("aria-disabled");
    }

    public void deleteDocuments(boolean areYouSure)
    {
        webElementInteraction.clickElement(deleteDialogCancel);

        if (areYouSure)
        {
            while (webElementInteraction.isElementDisplayed(deleteDialogConfirm))
            {
                webElementInteraction.clickElement(deleteDialogConfirm);
            }
        } else
        {
            while (webElementInteraction.isElementDisplayed(deleteDialogCancel))
            {
                webElementInteraction.clickElement(deleteDialogCancel);
            }
        }
    }

    public void clickSelectAll()
    {
        webElementInteraction.clickElement(By.cssSelector("div[id='SELECTED_LIST_ITEMS']"));
        webElementInteraction.waitUntilElementIsVisibleWithRetry(By.id("SELECTED_LIST_ITEMS_dropdown"), 5);
        webElementInteraction.clickElement(By.cssSelector("tr[title='All'] td[class*='dijitMenuItemLabel']"));
    }

    public void clickSelectedItemsListDropdownArrow()
    {
        webElementInteraction.clickElement(selectedListItemsDropdownArrow);
    }

    public boolean isNameHighlighted(String name)
    {
        List<WebElement> allNameHighlights = webElementInteraction.findElements(nameHighlight);
        for (WebElement eachNameHighlight : allNameHighlights)
        {
            if (eachNameHighlight.getText().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isContentHighlighted(String content)
    {
        if (!webElementInteraction.isElementDisplayed(contentHighlight))
        {
            return false;
        } else {
            return webElementInteraction.findElement(contentHighlight).getText().equals(content);
        }
    }

    public boolean isDescriptionHighlighted(String description)
    {
        if (!webElementInteraction.isElementDisplayed(descriptionHighlight))
        {
            return false;
        } else {
            return webElementInteraction.findElement(descriptionHighlight).getText().equals(description);
        }
    }

    public boolean isTitleHighlighted(String title)
    {
        if (!webElementInteraction.isElementDisplayed(titleHighlight))
        {
            return false;
        } else {
            return webElementInteraction.findElement(titleHighlight).getText().equals(title);
        }
    }

    public String getNoSearchResultsText()
    {
        return webElementInteraction.findElement(noSearchResults).getText();
    }

    public boolean isAnyFolderReturnedInResults()
    {
        return webElementInteraction.isElementDisplayed(folderResult);
    }

    public boolean isAnyFileReturnedInResults()
    {
        return webElementInteraction.isElementDisplayed(contentResult);
    }

    public boolean isConfirmDeletionDialogDisplayed()
    {
        return webElementInteraction.isElementDisplayed(confirmDeleteDialog);
    }

    public boolean isSiteHighlighted()
    {
        return webElementInteraction.isElementDisplayed(highlightedSite);
    }

    public boolean isModifiedOnHighlighted()
    {
        return webElementInteraction.isElementDisplayed(highlightedModifiedDate);
    }

    public void setSearchExpression(String searchExpression)
    {
        webElementInteraction.clearAndType(webElementInteraction.findElement(inputField), searchExpression);
    }

    public void clickSearchButton()
    {
        webElementInteraction.clickElement(searchButton);
    }

    public boolean confirmNoItemIsHighlighted()
    {
        webElementInteraction.waitUntilElementDeletedFromDom(highlight);
        return webElementInteraction.isElementDisplayed(highlight);
    }

    public void clickContentName(String contentName)
    {
        docName(contentName);
    }

    public void clickSortDropdownOption(String sortingOption)
    {
        webElementInteraction.findFirstDisplayedElement(By.xpath("//div[@id='DOCLIB_SORT_FIELD_SELECT_GROUP']//td[text()='" + sortingOption + "']")).click();
        webElementInteraction.waitUntilElementContainsText(webElementInteraction.findElement(currentSortFilter), sortingOption);
    }

    public boolean isSortOrderToggleButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(sortOrderToggleButton);
    }

    public void clickToggleButton()
    {
        webElementInteraction.clickElement(sortOrderToggleButton);
    }

    public boolean isDescendingOrderSet()
    {
        return webElementInteraction.isElementDisplayed(By.cssSelector("div[title='Change sort order to descending']"));
    }

    public boolean isAscendingOrderSet()
    {
        return webElementInteraction.isElementDisplayed(By.cssSelector("div[title='Change sort order to ascending']"));
    }

    public String getSortFilter()
    {
        return webElementInteraction.findElement(currentSortFilter).getText().trim();
    }

    public int getResultsListSize()
    {
        return webElementInteraction.findElements(resultsList).size();
    }

    public boolean areResultsSortedByName()
    {
        boolean status;
        List<String> resultsListText = new ArrayList<>();
        List<WebElement> resultsName = webElementInteraction.findElements(By.cssSelector("tr[id='FCTSRCH_SEARCH_RESULT'] a[class^='alfresco-navigation'] span.value"));
        List<String> sortedList = new ArrayList<>();
        for (WebElement item : resultsName)
        {
            resultsListText.add(item.getText());
            sortedList.add(item.getText());
        }
        Collections.sort(sortedList);
        log.info("Actual list: {}", resultsListText);
        log.info("Sorted list: {}", sortedList);

        status = resultsListText.equals(sortedList);
        return status;
    }

    public List<String> getAvailableFilters()
    {
        List<String> availableOptions = new ArrayList<>();
        webElementInteraction.clickElement(sortDropdownButton);
        for (WebElement option : webElementInteraction.findElements(sortOptions))
        {
            availableOptions.add(option.getText());
        }

        log.info("Available options are: {}", availableOptions);

        return availableOptions;
    }

    public void selectFileTypeFilter(String fileType)
    {
        webElementInteraction.clickElement(fileTypeOption(fileType));
    }

    public int getGalleryViewResultsNumber()
    {
        return webElementInteraction.findElements(galleryViewResultsList).size();
    }

    public void openFileFromGalleryView(String fileName)
    {
        webElementInteraction.findFirstElementWithValue(galleryViewResultsList, fileName).click();
    }

    public void clickContentThumbnailByName(String contentName)
    {
        image(contentName);
    }

    public String getPreviewedImageName()
    {
        return webElementInteraction.findElement(imagePreview).getText();
    }

    public boolean isContentPreviewed(String docName)
    {
        return webElementInteraction.findElement(previewTitle).getText().equals(docName);
    }

    public boolean isConfigureSearchButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(configureSearchButton);
    }

    public void clickModifiedBy(String modifierName)
    {
        webElementInteraction.findFirstElementWithValue(modifiedBy, modifierName).click();
    }

    public void closePicturePreview()
    {
        webElementInteraction.clickElement(closePicturePreview);
        webElementInteraction.waitUntilElementDisappears(By.cssSelector("img[id='aikauLightboxImage']"));
    }

    public void closeFilePreview()
    {
        webElementInteraction.clickElement(closeFilePreviewButton);
        webElementInteraction.waitUntilElementDisappears(By.cssSelector("div[class$='dialogDisplayed dijitDialog']"));
    }

    public SearchPage checkContent(ContentModel... contentModels)
    {
        for(ContentModel content : contentModels)
        {
            log.info("Check content: {}", content.getName());
            webElementInteraction.clickElement(getContentRowResult(content).findElement(contentCheckBox));
        }
        return this;
    }

    public SearchPage clickSelectedItems()
    {
        log.info("Click Selected Items...");
        webElementInteraction.clickElement(selectedItemsMenu);
        return this;
    }

    public SearchCopyMoveDialog clickCopyToForSelectedItems()
    {
        log.info("Click Copy to... from Selected Items");
        webElementInteraction.clickElement(copyToAction);
        return new SearchCopyMoveDialog(webDriver);
    }

    public SearchResultContentAction usingContent(ContentModel contentModel)
    {
        return new SearchResultContentAction(webDriver, contentModel, this);
    }
}