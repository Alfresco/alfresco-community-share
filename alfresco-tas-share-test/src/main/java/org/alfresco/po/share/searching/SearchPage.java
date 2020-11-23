package org.alfresco.po.share.searching;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.searching.dialogs.SearchCopyMoveDialog;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SearchPage extends SharePage2<SearchPage> implements AccessibleByMenuBar
{
    public By searchResult = By.id("FCTSRCH_SEARCH_RESULT");
    public By searchResultRows = By.id("FCTSRCH_SEARCH_RESULT");
    public By deleteDialogConfirm = By.cssSelector("span#ALF_DELETE_CONTENT_DIALOG_CONFIRMATION");
    public By deleteDialogCancel = By.cssSelector("span#ALF_DELETE_CONTENT_DIALOG_CANCELLATION");
    private By numberOfResultsLabel = By.id("FCTSRCH_RESULTS_COUNT_LABEL");
    private By selectedItemsMenu = By.id("SELECTED_ITEMS_MENU_text");
    private By resultsDetailedViewList = By.cssSelector(".propertiesCell .nameAndTitleCell a .value");
    @RenderWebElement
    private By searchInLabel = By.id("FCTSRCH_TOP_MENU_BAR_SCOPE_LABEL");
    private By searchTopMenu = By.id("FCTSRCH_TOP_MENU_BAR");
    @RenderWebElement
    private By searchButton = By.cssSelector("span[class*='confirmationButton'] span");
    private By searchInDropdown = By.cssSelector("[id=FCTSRCH_SCOPE_SELECTION_MENU_text]");
    private By searchInDropdownOptionsSelector = By.cssSelector("div[id='FCTSRCH_SCOPE_SELECTION_MENU_GROUP'] td[class*='dijitMenuItemLabel']");
    private By filterTypeList = By.cssSelector(".label.alfresco-layout-Twister--open>h3");
    private By showMore = By.cssSelector(".showMore .details");
    private By showLess = By.cssSelector(".showLess .details");
    private By sortDropdownButton = By.cssSelector("div[id='FCTSRCH_SORT_MENU'] span[class$='arrow']");
    private By sortOptions = By.cssSelector("tr[id*='alfresco_menus_AlfCheckableMenuItem'] td:nth-child(3)");
    private By allOptions = By.cssSelector(".filterLabel");
    private By viewsDropdown = By.cssSelector("div[id='FCTSRCH_VIEWS_MENU'] img");
    private By viewsDropdownOptionsSelector = By.cssSelector("#DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP .dijitMenuItemLabel");
    private By resultsGalleryViewList = By.cssSelector("[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']");
    private By sliderIncrementIcon = By.cssSelector(".dijitSliderIncrementIconH");
    private By sliderDecrementIcon = By.cssSelector(".dijitSliderDecrementIconH");
    private By sliderGalleryView = By.cssSelector(".dijitSliderMoveable");
    private By resultModifiedByList = By.cssSelector(".dateCell .value");
    private By searchManager = By.cssSelector("#FCTSRCH_CONFIG_PAGE_LINK_text>a");
    private By actionsLink = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS span[class*='dijitButtonContents']");
    private By actionsOptions = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS_DROPDOWN tr td[id*='text']");
    private By checkboxList = By.cssSelector("span[id*='SELECTOR']");
    private By selectedItemsList = By.id("SELECTED_LIST_ITEMS");
    private By selectedItemsCheckboxOptions = By.cssSelector("#SELECTED_LIST_ITEMS_dropdown tr td[id*='text']");
    private By selectedItemsDropdown = By.cssSelector("span[id='SELECTED_ITEMS_MENU_text']");
    private By selectedItemsOptions = By.cssSelector("#SELECTED_ITEMS_ACTIONS_GROUP tr td[id*='text']");
    private By selectAllButton = By.cssSelector("div[id='SELECTED_LIST_ITEMS'] img");
    private By noSearchResults = By.cssSelector("div[id='FCTSRCH_SEARCH_ADVICE_NO_RESULTS'] div[title='Search suggestions:']");
    private By copyToAction = By.id("onActionCopyTo_text");
    private By optionsList = By.cssSelector("tr[class$='dijitMenuItem']");
    private By folderResult = By.cssSelector("img[src*='folder']");
    private By contentResult = By.cssSelector("img[src*='content']");
    private By resultsTable = By.id("FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS");
    private By confirmDeleteDialog = By.id("ALF_DELETE_CONTENT_DIALOG");
    private By sortDropdown = By.cssSelector("div[id='FCTSRCH_SORT_MENU_dropdown'] tbody[class='dijitReset']");
    private By sortOrderToggleButton = By.cssSelector("div[id='FCTSRCH_SORT_ORDER_TOGGLE'] img");
    private By selector = By.cssSelector("span[id='FCTSRCH_SEARCH_RESULT_SELECTOR']");
    @RenderWebElement
    private By inputField = By.cssSelector("input[id$='_CONTROL']");
    private By currentSortFilter = By.cssSelector("span[id='FCTSRCH_SORT_MENU_text']");
    private By resultsList = By.cssSelector("tbody[id='FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS'] tr");
    private By galleryViewResultsList = By.cssSelector("[id='FCTSRCH_GALLERY_VIEW_ITEMS'] td");
    private By closeInfo = By.cssSelector("div[class='alfresco-dialog-AlfDialog dialogDisplayed dijitDialog'] span.dijitDialogCloseIcon span");
    private By imagePreview = By.cssSelector("div[id='aikauLightbox'] div[id='aikauLightboxCaption']");
    private By previewTitle = By.cssSelector("span.dijitDialogTitle");
    private By modifiedBy = By.cssSelector("span.alfresco-renderers-DateLink");
    private By configureSearchButton = By.cssSelector("div[id='FCTSRCH_CONFIG_PAGE_LINK'] span");
    private By closePicturePreview = By.cssSelector("img[id='aikauCloseButton']");
    private By closeFilePreviewButton = By.cssSelector("div[class*='dialogDisplayed dijitDialog'] span.dijitDialogCloseIcon");
    private By selectedListItemsDropdownArrow = By.cssSelector("DIV[ID='SELECTED_LIST_ITEMS'] span.alfresco-menus-AlfMenuBarPopup__arrow");
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

    public SearchPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    private WebElement docName(String docName)
    {
        return getBrowser().findElement(By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_DISPLAY_NAME']//mark[text()='" + docName + "']"));
    }

    private WebElement fileTypeOption(String option)
    {
        return getBrowser().findElement(By.xpath("//div[@id='FCTSRCH_filter_mimetype']//span[text()='" + option + "']"));
    }

    private WebElement galleryViewItemSelector(String fileName)
    {
        return getBrowser().findElement(By.cssSelector("//table[@id='FCTSRCH_GALLERY_VIEW_ITEMS']//td//div[text()='" + fileName + "']/../.."));
    }

    private WebElement image(String imageName)
    {
        return getBrowser().findElement(By.cssSelector("span[class^='alfresco-renderers-Thumbnail'] img[title='" + imageName + "']"));
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
        getBrowser().waitUntilElementVisible(searchButton, WAIT_60);
    }

    private boolean isContentDisplayed(ContentModel contentModel)
    {
        if(getBrowser().isElementDisplayed(noResults))
        {
            return false;
        }
        else
        {
            List<WebElement> searchResults = getBrowser().findElements(searchResultRows);
            return searchResults.stream().anyMatch(row ->
                row.findElement(contentName).getText().equals(contentModel.getName()));
        }
    }

    protected WebElement getContentRowResult(ContentModel content)
    {
        List<WebElement> searchResults = getBrowser().findElements(searchResultRows);
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
            if(getBrowser().isElementDisplayed(noResults))
            {
                counter++;
                continue;
            }
            if(getBrowser().findElements(searchResultRows).size() < contentModels.length)
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
        return getBrowser().findElement(numberOfResultsLabel).getText();
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
        WebElement webElement = getBrowser().findFirstElementWithValue(resultsDetailedViewList, query);
        if (webElement == null)
        {
            getBrowser().refresh();
            webElement = getBrowser().findFirstElementWithValue(resultsDetailedViewList, query);
        }
        return webElement != null;
    }

    public boolean isResultFoundWithRetry(String query)
    {
        WebElement webElement = getBrowser().findFirstElementWithValue(resultsDetailedViewList, query);
        for (int i = 0; i < 5; i++)
        {
            if (webElement != null)
            {
                break;
            }
            else
            {
                //getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchResult, 6);
                List<WebElement> results = getBrowser().findElements(resultsDetailedViewList);
                webElement = getBrowser().findFirstElementWithExactValue(results, query);
            }
        }
        return getBrowser().isElementDisplayed(webElement);
    }

    public void mouseOverResult(String query)
    {
        for (WebElement result : getBrowser().findElements(resultsDetailedViewList))
        {
            if (result.getText().contains(query))
                getBrowser().mouseOver(result);
        }
    }

    public List<String> getFilterTypeList()
    {
        List<String> filterList = new ArrayList<>();
        for (WebElement aFilterTypeList : getBrowser().findElements(filterTypeList))
        {
            filterList.add(aFilterTypeList.getText());
        }
        return filterList;
    }

    public boolean isFilterTypePresent(String filter)
    {
        return getBrowser().findFirstElementWithValue(filterTypeList, filter) != null;
    }

    public boolean isFilterOptionDisplayed(String filterId, String filterOption)
    {
        WebElement filterElement = getBrowser().findElement(By.id("FCTSRCH_" + filterId));
        return getBrowser().findFirstElementWithValue(filterElement.findElements(By.cssSelector(".filterLabel")), filterOption) != null;
    }

    public String getFilterOptionHits(String filterOption)
    {
        return getBrowser().findFirstElementWithValue(allOptions, filterOption).findElement(By.xpath("following-sibling::*[1]")).getText();
    }

    public void clickSortDropdown()
    {
        getBrowser().waitUntilElementClickable(sortDropdownButton).click();
    }

    public void clickSearchInDropdown()
    {
        getBrowser().findElement(searchInDropdown).click();
    }

    public String getSearchInDropdownSelectedValue()
    {
        return  getBrowser().findElement(searchInDropdown).getText();
    }

    public boolean isSearchInLabelDisplayed()
    {
        return getBrowser().isElementDisplayed(searchInLabel);
    }

    public boolean isSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(searchButton);
    }

    public String getSearchInDropdownValues()
    {
        List<WebElement> searchInDropdownOptions = getBrowser().waitUntilElementsVisible(searchInDropdownOptionsSelector);
        if (!searchInDropdownOptions.get(searchInDropdownOptions.size() - 2).getText().equals("All Sites"))
            return "'All Sites' value not displayed";
        if (!searchInDropdownOptions.get(searchInDropdownOptions.size() - 1).getText().equals("Repository"))
            return "'Repository' value not displayed";
        return "All Sites, Repository";
    }

    public SearchPage selectOptionFromSearchIn(String option)
    {
        clickSearchInDropdown();
        List<WebElement> searchInDropdownOptions = getBrowser().findDisplayedElementsFromLocator(searchInDropdownOptionsSelector);
        for (WebElement optionSearchIn : searchInDropdownOptions)
        {
            if (optionSearchIn.getText().equals(option))
                optionSearchIn.click();
        }
        return (SearchPage) this.renderedPage();
    }

    public boolean isSortDropdownComplete()
    {
        List<WebElement> sortList = getBrowser().findElements(sortOptions);
        if (sortList.size() == 12)
            if (sortList.get(0).getText().equals("Relevance")
                    && sortList.get(1).getText().equals("Name")
                    && sortList.get(2).getText().equals("Title")
                    && sortList.get(3).getText().equals("Description")
                    && sortList.get(4).getText().equals("Author")
                    && sortList.get(5).getText().equals("Modifier")
                    && sortList.get(6).getText().equals("Modified date")
                    && sortList.get(7).getText().equals("Creator")
                    && sortList.get(8).getText().equals("Created date")
                    && sortList.get(9).getText().equals("Size") && sortList.get(10).getText().equals("Mime type")
                    && sortList.get(11).getText().equals("Type"))
            {
                return true;
            }
        return false;
    }

    public void clickViewsDropdown()
    {
        getBrowser().waitUntilElementClickable(viewsDropdown).click();
        getBrowser().waitUntilElementVisible(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP']"));
    }

    public ArrayList<String> getViewsDropdownOptions()
    {
        List<WebElement> viewsDropdownOptions = getBrowser().findElements(viewsDropdownOptionsSelector);
        ArrayList<String> viewsOptionsText = new ArrayList<>();
        for (WebElement viewsDropdownOption : viewsDropdownOptions)
        {
            viewsOptionsText.add(viewsDropdownOption.getText());
        }
        return viewsOptionsText;
    }

    public boolean isSearchResultsListInDetailedView()
    {
        getBrowser().waitUntilElementsVisible(By.cssSelector(".propertiesCell .nameAndTitleCell a .value"));
        return getBrowser().findElements(resultsDetailedViewList).size() > 0;
    }

    public boolean isSearchResultsListInGalleryView()
    {
        getBrowser().waitUntilElementVisible(By.cssSelector("[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']"));
        return getBrowser().findElements(resultsGalleryViewList).size() > 0;
    }

    public SearchPage clickGalleryView()
    {
        clickViewsDropdown();
        getBrowser().waitUntilElementIsPresent(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP'] td[class='dijitReset dijitMenuItemLabel']"));
        getBrowser().selectOptionFromFilterOptionsList("Gallery View", getBrowser().findElements(viewsDropdownOptionsSelector));
        return (SearchPage) this.renderedPage();
    }

    public SearchPage clickDetailedView()
    {
        clickViewsDropdown();
        //getBrowser().waitUntilElementVisible(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP']"));
        getBrowser().waitUntilElementIsPresent(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP'] td[class='dijitReset dijitMenuItemLabel']"));
        getBrowser().selectOptionFromFilterOptionsList("Detailed View", getBrowser().findElements(viewsDropdownOptionsSelector));
        getBrowser().waitUntilElementDisappears(By.cssSelector("table[class*='alfresco-documentlibrary-AlfGalleryViewSlider']"));
        return (SearchPage) this.renderedPage();
    }

    public boolean isSliderGalleryViewDisplayed()
    {
        return (getBrowser().isElementDisplayed(sliderGalleryView)
                && getBrowser().isElementDisplayed(sliderIncrementIcon)
                && getBrowser().isElementDisplayed(sliderDecrementIcon));
    }

    public void clickFilterOption(String option, String filterBy)
    {
        By selector = By.cssSelector("div.alfresco-documentlibrary-AlfDocumentFilters[id$='" + filterBy + "'] .filterLabel");
        List<WebElement> filterOptionList = getBrowser().findElements(selector);
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
        List<WebElement> results = getBrowser().findElements(resultsDetailedViewList);
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
        LOG.info("Assert search manager button is displayed");
        assertTrue(getBrowser().isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }

    public SearchPage assertSearchManagerButtonIsNotDisplayed()
    {
        LOG.info("Assert search manager button is NOT displayed");
        assertFalse(getBrowser().isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }

    public SearchManagerPage clickSearchManagerLink()
    {
        getBrowser().waitUntilElementVisible(searchManager).click();
        return (SearchManagerPage) new SearchManagerPage(browser).renderedPage();
    }

    public void clickShowMore()
    {
        List<WebElement> showMoreList = getBrowser().findElements(showMore);
        if (showMoreList.size() > 0)
            for (WebElement aShowMore : showMoreList)
            {
                if (aShowMore.isDisplayed())
                    aShowMore.click();
            }
    }

    public void clickShowFewer()
    {
        List<WebElement> showLessList = getBrowser().findElements(showLess);
        if (showLessList.size() > 0)
            for (WebElement showLes : showLessList)
            {
                if (showLes.isDisplayed())
                    showLes.click();
            }
    }

    public int getFilterTypePosition(String filter)
    {
        return getBrowser().findFirstElementWithValue(filterTypeList, filter)
            .findElements(By.xpath("ancestor::div[contains(@id, 'FCTSRCH_filter_')]/preceding-sibling::*")).size() + 1;
    }

    public boolean isActionsLinkDisplayed()
    {
        return getBrowser().isElementDisplayed(actionsLink);
    }

    private void clickActionsMenuButton(String searchResult)
    {
        getBrowser().mouseOver(getBrowser().findFirstElementWithValue(resultsDetailedViewList, searchResult));
        getBrowser().waitUntilElementVisible(actionsLink).click();
    }

    private boolean isActionDisplayed(String searchResult, String optionName)
    {
        clickActionsMenuButton(searchResult);
        WebElement action = getBrowser().findFirstElementWithValue(actionsOptions, optionName);
        return getBrowser().isElementDisplayed(action);
    }

    public void clickOptionFromActionsMenu(String searchResult, String optionName)
    {
        if (isActionDisplayed(searchResult, optionName))
        {
            WebElement action = getBrowser().findFirstElementWithValue(actionsOptions, optionName);
            action.click();
        }
    }

    public void clickCheckbox(String searchResult)
    {
        if (isResultFound(searchResult))
        {
            List<WebElement> checkBoxElements = getBrowser().findElements(checkboxList);
            for (i = 0; i < checkBoxElements.size(); i++)
            {
                LOG.info("Position {} ", i);
                checkBoxElements.get(i).click();
                selectedCheckboxes = getBrowser().findDisplayedElementsFromLocator(checkboxSelector);
            }
        }
    }

    public boolean isSelectedItemsListOptionDisplayed(String optionName)
    {
        WebElement action = getBrowser().findFirstElementWithValue(selectedItemsCheckboxOptions, optionName);
        return getBrowser().isElementDisplayed(action);
    }

    public void clickOptionFromSelectedItemsListCheckbox(String optionName)
    {
        if (isSelectedItemsListOptionDisplayed(optionName))
        {
            WebElement action = getBrowser().findFirstElementWithValue(selectedItemsCheckboxOptions, optionName);
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
        WebElement action = getBrowser().findFirstElementWithValue(selectedItemsOptions, optionName);
        return getBrowser().isElementDisplayed(action);
    }

    public boolean isCopyToActionPresent()
    {
        return getBrowser().isElementDisplayed(copyToAction);
    }

    public void clickOptionFromSelectedItemsDropdown(String optionName)
    {
        if (isSelectedItemsOptionDisplayed(optionName))
        {
            WebElement action = getBrowser().findFirstElementWithValue(selectedItemsOptions, optionName);
            action.click();
        }
    }

    public boolean isALLItemsCheckboxChecked()
    {
        return getBrowser().isElementDisplayed(By.xpath("//div[@id='SELECTED_LIST_ITEMS']/img[@alt='You have all items selected. Click this icon to deselect all.']"));
    }

    public boolean isNoneItemsCheckboxChecked()
    {
        return getBrowser().isElementDisplayed(By.xpath("//div[@id='SELECTED_LIST_ITEMS']/img[@alt='You have no items selected. Click this icon to select all.']"));
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
            while (getBrowser().isElementDisplayed(deleteDialogConfirm))
            {
                getBrowser().findElement(deleteDialogConfirm).click();
            }
        } else
        {
            while (getBrowser().isElementDisplayed(deleteDialogCancel))
            {
                getBrowser().findElement(deleteDialogCancel).click();
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
        List<WebElement> allNameHighlights = getBrowser().findElements(nameHighlight);
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
        if (getBrowser().isElementDisplayed(contentHighlight) == false)
        {
            isHighlighted = false;
            return isHighlighted;
        }
        if (getBrowser().isElementDisplayed(contentHighlight) == true)
        {
            isHighlighted = getBrowser().findElement(contentHighlight).getText().equals(content);
            return isHighlighted;
        }
        return isHighlighted;
    }

    public boolean isDescriptionHighlighted(String description)
    {
        boolean isHighlighted = false;
        if (getBrowser().isElementDisplayed(descriptionHighlight) == false)
        {
            isHighlighted = false;
            return isHighlighted;
        }

        if (getBrowser().isElementDisplayed(descriptionHighlight) == true)
        {
            isHighlighted = getBrowser().findElement(descriptionHighlight).getText().equals(description);
            return isHighlighted;
        }

        return isHighlighted;
    }

    public boolean isTitleHighlighted(String title)
    {
        boolean isHighlighted = false;
        if (getBrowser().isElementDisplayed(titleHighlight) == false)
        {
            isHighlighted = false;
            return isHighlighted;
        }

        if (getBrowser().isElementDisplayed(titleHighlight) == true)
        {
            isHighlighted = getBrowser().findElement(titleHighlight).getText().equals(title);
        }
        return isHighlighted;
    }


    public String getNoSearchResultsText()
    {
        return getBrowser().findElement(noSearchResults).getText();
    }

    public boolean isAnyFolderReturnedInResults()
    {
        return getBrowser().isElementDisplayed(folderResult);
    }

    public boolean isAnyFileReturnedInResults()
    {
        return getBrowser().isElementDisplayed(contentResult);
    }

    public boolean isConfirmDeletionDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(confirmDeleteDialog);
    }

    public boolean isSiteHighlighted()
    {
        return getBrowser().isElementDisplayed(highlightedSite);
    }

    public boolean isModifiedOnHighlighted()
    {
        return getBrowser().isElementDisplayed(highlightedModifiedDate);
    }

    public void setSearchExpression(String searchExpression)
    {
        clearAndType(getBrowser().findElement(inputField), searchExpression);
    }

    public void clickSearchButton()
    {
        getBrowser().findElement(searchButton).click();
    }

    public boolean confirmNoItemIsHighlighted()
    {
        getBrowser().waitUntilElementDeletedFromDom(highlight);
        return getBrowser().isElementDisplayed(highlight);
    }

    public void clickContentName(String contentName)
    {
        docName(contentName).click();
    }

    public void clickSortDropdownOption(String sortingOption)
    {
        getBrowser().findFirstDisplayedElement(By.xpath("//div[@id='DOCLIB_SORT_FIELD_SELECT_GROUP']//td[text()='" + sortingOption + "']")).click();
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(currentSortFilter), sortingOption);
    }

    public boolean isSortOrderToggleButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(sortOrderToggleButton);
    }

    public void clickToggleButton()
    {
        getBrowser().findElement(sortOrderToggleButton).click();
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
        return getBrowser().findElement(currentSortFilter).getText().trim();
    }

    public int getResultsListSize()
    {
        return getBrowser().findElements(resultsList).size();
    }

    public boolean areResultsSortedByName()
    {
        boolean status;
        List<String> resultsListText = new ArrayList<>();
        List<WebElement> resultsName = getBrowser().findElements(By.cssSelector("tr[id='FCTSRCH_SEARCH_RESULT'] a[class^='alfresco-navigation'] span.value"));
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
        getBrowser().findElement(sortDropdownButton).click();
        for (WebElement option : getBrowser().findElements(sortOptions))
        {
            availableOptions.add(option.getText());
        }

        LOG.info("Available options are: " + availableOptions);

        return availableOptions;
    }

    public void selectFileTypeFilter(String fileType)
    {
        fileTypeOption(fileType).click();
    }

    public int getGalleryViewResultsNumber()
    {
        return getBrowser().findElements(galleryViewResultsList).size();
    }

    public void openFileFromGalleryView(String fileName)
    {
        getBrowser().findFirstElementWithValue(galleryViewResultsList, fileName).click();
    }

    public void clickContentThumbnailByName(String contentName)
    {
        image(contentName).click();
    }

    public String getPreviewedImageName()
    {
        return getBrowser().findElement(imagePreview).getText();
    }

    public boolean isContentPreviewed(String docName)
    {
        return getBrowser().findElement(previewTitle).getText().equals(docName);
    }

    public boolean isConfigureSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(configureSearchButton);
    }

    public void clickModifiedBy(String modifierName)
    {
        getBrowser().findFirstElementWithValue(modifiedBy, modifierName).click();
    }

    public void closePicturePreview()
    {
        getBrowser().findElement(closePicturePreview).click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("img[id='aikauLightboxImage']"));
    }

    public void closeFilePreview()
    {
        getBrowser().findElement(closeFilePreviewButton).click();
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
        getBrowser().waitUntilElementClickable(selectedItemsMenu).click();
        return this;
    }

    public SearchCopyMoveDialog clickCopyToForSelectedItems()
    {
        LOG.info("Click Copy to... from Selected Items");
        getBrowser().waitUntilElementVisible(copyToAction).click();
        return (SearchCopyMoveDialog) new SearchCopyMoveDialog(browser).renderedPage();
    }

    public SearchResultContentAction usingContent(ContentModel contentModel)
    {
        return new SearchResultContentAction(contentModel, this, new SearchCopyMoveDialog(browser));
    }
}