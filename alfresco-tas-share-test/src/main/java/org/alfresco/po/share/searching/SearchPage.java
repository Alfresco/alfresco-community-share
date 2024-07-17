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
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class SearchPage extends SharePage2<SearchPage> implements AccessibleByMenuBar
{
    private final By searchResultRows = By.id("FCTSRCH_SEARCH_RESULT");
    private final By deleteDialogConfirm = By.cssSelector("span#ALF_DELETE_CONTENT_DIALOG_CONFIRMATION");
    private final By deleteDialogCancel = By.cssSelector("span#ALF_DELETE_CONTENT_DIALOG_CANCELLATION");
    private final By numberOfResultsLabel = By.id("FCTSRCH_RESULTS_COUNT_LABEL");
    private final By selectedItemsMenu = By.id("SELECTED_ITEMS_MENU_text");
    private final By searchManagerButton = By.xpath("//a[text()='Search Manager']");
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
    SoftAssert softAssert = new SoftAssert();

    public SearchPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private WebElement docName(String docName)
    {
        return findElement(By.xpath("//span[@id='FCTSRCH_SEARCH_RESULT_DISPLAY_NAME']//mark[text()='" + docName + "']"));
    }

    private WebElement fileTypeOption(String option)
    {
        return findElement(By.xpath("//div[@id='FCTSRCH_filter_mimetype']//span[text()='" + option + "']"));
    }

    private WebElement image(String imageName)
    {
        return findElement(By.cssSelector("span[class^='alfresco-renderers-Thumbnail'] img[title='" + imageName + "']"));
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
        waitUntilElementIsVisible(searchButton);
    }

    private boolean isContentDisplayed(ContentModel contentModel)
    {
        if(isElementDisplayed(noResults))
        {
            return false;
        }
        else
        {
            List<WebElement> searchResults = findElements(searchResultRows);
            return searchResults.stream().anyMatch(row ->
                row.findElement(contentName).getText().equals(contentModel.getName()));
        }
    }

    protected WebElement getContentRowResult(ContentModel content)
    {
        List<WebElement> searchResults = waitUntilElementsAreVisible(searchResultRows);
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
            log.warn("Content {} not displayed - retry: {}", contentToFind.getName(), retryCount);
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
        while (findElements(searchResultRows).size() < contentModels.length &&
            isElementDisplayed(noResults) && retryCounter <= RETRY_TIME_80.getValue())
        {
            log.warn("Keyword {} not displayed - retry: {}", searchExpression, retryCounter);
            setSearchExpression(searchExpression);
            clickSearchButton();
            waitInSeconds(WAIT_3.getValue());
            retryCounter++;
        }
        return this;
    }

    public String getNumberOfResultsText()
    {
        return getElementText(numberOfResultsLabel);
    }
    public SearchPage assertNoResultsFoundDisplayed()
    {
        log.info("Assert no Results Found is displayed");
        Assert.assertEquals(getNumberOfResultsText(), language.translate("searchPage.noResultsFound"), "No results found");
        return this;
    }
    public boolean isTheFilterOptionVisible(String filterOption) {
        waitInSeconds(3);
        for (WebElement filterElement : findElements(By.className("filterLabel"))) {
            if (filterElement.getText().equals(filterOption)) {
                waitInSeconds(3);
                return true;
            }
            System.out.println("************"+filterElement.getText());
        }
        return false;

    }
    public boolean isFilterOptionNotVisible(String filterOption, String  text) {
        for (WebElement filterElement : findElements(By.xpath("(//span[text()='firstName3 lastName3'])[3]"))) {
            if (filterElement.getText().equals(filterOption)) {
                waitInSeconds(3);
                return true;
            }
            System.out.println("************"+filterElement.getText());
        }
        return false;

    }

    public SearchPage assertCheckForSearchManagerButtonIsDisplayed()
    {
        log.info("Assert search manager button is displayed");
        waitInSeconds(3);
        refresh();
        waitInSeconds(5);
        refresh();
        assertTrue(isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }


    public Object executeJavaScript(final String js, Object... args)
    {
        if (js == null || js.isEmpty())
        {
            throw new IllegalArgumentException("JS script is required");
        }
        return executeJavaScript(js, args);
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
        WebElement webElement = findFirstElementWithValue(resultsDetailedViewList, query);
        if (webElement == null)
        {
            refresh();
            webElement = findFirstElementWithValue(resultsDetailedViewList, query);
        }
        return webElement != null;
    }
    public boolean isResultFoundWithList(String query)
    {
        for (int i = 0; i < 10; i++) {
            if (isElementDisplayed(resultsDetailedViewList)) {
                break;
            }
            else
            {
                refresh();
                waitInSeconds(5);
            }

        }
        List<WebElement> elementList = waitUntilElementsAreVisible(resultsDetailedViewList);
        WebElement webElement = findFirstElementWithValue(elementList, query);
        for (int i = 0; i < 10; i++) {
            if (webElement == null) {
                refresh();
                waitInSeconds(2);
                elementList = waitUntilElementsAreVisible(resultsDetailedViewList);
                webElement = findFirstElementWithValue(elementList, query);
            }
            else
            {
                break;
            }
        }
//        System.out.println("from last : "+webElement);
        return webElement != null;
    }
    public boolean is_ResultFound()
    {
        waitInSeconds(3);
        for (int i = 0; i < 5; i++) {
            if (isElementDisplayed(resultsDetailedViewList)) {
            } else {
                refresh();
            }
        }
        return isElementDisplayed(resultsDetailedViewList);
    }
    public SearchPage assertCreatedDataIsDisplayed(String query)
    {
        log.info("Assert created data like wiki page, data list, event, blog is displayed");
        Assert.assertTrue(isResultFoundWithList(query), query+" is not displayed");
        return this;
    }
    public SearchPage assertIsXmlFileDisplayed(String query)
    {
        SoftAssert sa = new SoftAssert();
        sa.assertTrue(isResultFoundWithList(".xml"), ".xml" + " is not displayed");

        return this;
    }

    public SearchPage clickSortDropDown_CreatedDate()
    {
        clickSortDropdown();
        waitUntilElementIsPresent(By.cssSelector("tr[id*='alfresco_menus_AlfCheckableMenuItem'] td:nth-child(3)"));
        selectOptionFromFilterOptionsList("Created date", findElements(sortOptions));
        return this;
    }
    public SearchPage assertIsXmlFileNotDisplayed(String query)
    {
        SoftAssert sa = new SoftAssert();
        sa.assertFalse(isResultFound(".xml"), ".xml" + " is not displayed");

        return this;
    }
    public SearchPage assertIsXlsFileDisplayed(String query)
    {
        SoftAssert sa = new SoftAssert();
        sa.assertTrue(isResultFound(".xls"), ".xls" + " is not displayed");

        return this;
    }
    public SearchPage assertIsXlsFileNotDisplayed(String query)
    {
        SoftAssert sa = new SoftAssert();
        sa.assertFalse(isResultFound(".xls"), ".xls" + " is not displayed");

        return this;
    }
    public SearchPage assertIsHtmlFileNotDisplayed(String query)
    {
        SoftAssert sa = new SoftAssert();
        sa.assertFalse(isResultFound(".html"), ".html" + " is not displayed");

        return this;
    }

    public boolean isResultFoundWithRetry(String query)
    {
        refresh();
        waitInSeconds(2);
        refresh();
        WebElement webElement = findFirstElementWithValue(resultsDetailedViewList, query);
        for (int i = 0; i < 5; i++)
        {
            if (webElement != null)
            {
                break;
            }
            else
            {
                List<WebElement> results = findElements(resultsDetailedViewList);
                webElement = findFirstElementWithExactValue(results, query);
            }
        }
        return isElementDisplayed(webElement);
    }

    public void mouseOverResult(String query)
    {
        for (WebElement result : findElements(resultsDetailedViewList))
        {
            if (result.getText().contains(query))
                mouseOver(result);
        }
    }

    public List<String> getFilterTypeList()
    {
        List<String> filterList = new ArrayList<>();
        for (WebElement aFilterTypeList : findElements(filterTypeList))
        {
            filterList.add(aFilterTypeList.getText());
        }
        return filterList;
    }

    public boolean isFilterTypePresent(String filter)
    {
        waitInSeconds(4);
        return findFirstElementWithValue(filterTypeList, filter) != null;
    }
    public boolean isFilterPresent(String filter)
    {
        waitInSeconds(3);

        for (WebElement FilterType : findElements(filterTypeList))
        {
            if (FilterType.getText().contains(filter))
            {
                waitInSeconds(3);
                return true;
            }
        }

        return false;
    }

    public boolean isFilterOptionDisplayed(String filterId, String filterOption)
    {
        waitInSeconds(3);
        WebElement filterElement = findElement(By.id("FCTSRCH_" + filterId));
        return findFirstElementWithValue(filterElement.findElements(By.cssSelector(".filterLabel")), filterOption) != null;
    }

    public String getFilterOptionHits(String filterOption)
    {
        waitInSeconds(3);
        List<WebElement> optionList = findElements(allOptions);
        return findFirstElementWithValue(optionList, filterOption).findElement(By.xpath("following-sibling::*[1]")).getText();
    }

    public void clickSortDropdown()
    {
        clickElement(sortDropdownButton);
    }

    public SearchPage clickSearchInDropdown()
    {
        clickElement(searchInDropdown);
        return this;
    }

    public String getSearchInDropdownSelectedValue()
    {
        return  findElement(searchInDropdown).getText();
    }

    public boolean isSearchInLabelDisplayed()
    {
        return isElementDisplayed(searchInLabel);
    }

    public boolean isSearchButtonDisplayed()
    {
        return isElementDisplayed(searchButton);
    }

    public String getSearchInDropdownValues()
    {
        List<WebElement> searchInDropdownOptions = waitUntilElementsAreVisible(searchInDropdownOptionsSelector);
        if (!searchInDropdownOptions.get(searchInDropdownOptions.size() - 2).getText().equals("All Sites"))
            return "'All Sites' value not displayed";
        if (!searchInDropdownOptions.get(searchInDropdownOptions.size() - 1).getText().equals("Repository"))
            return "'Repository' value not displayed";
        return "All Sites, Repository";
    }

    public SearchPage selectOptionFromSearchIn(String option)
    {
        clickSearchInDropdown();
        List<WebElement> searchInDropdownOptions = findDisplayedElementsFromLocator(searchInDropdownOptionsSelector);
        for (WebElement optionSearchIn : searchInDropdownOptions)
        {
            if (optionSearchIn.getText().equals(option))
                optionSearchIn.click();
        }
        return this;
    }

    public boolean isSortDropdownComplete()
    {
        List<WebElement> sortList = findElements(sortOptions);
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
        clickElement(viewsDropdown);
        waitUntilElementIsVisible(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP']"));
    }

    public List<String> getViewsDropdownOptions()
    {
        List<WebElement> viewsDropdownOptions = findElements(viewsDropdownOptionsSelector);
        ArrayList<String> viewsOptionsText = new ArrayList<>();
        for (WebElement viewsDropdownOption : viewsDropdownOptions)
        {
            viewsOptionsText.add(viewsDropdownOption.getText());
        }
        return viewsOptionsText;
    }

    public boolean isSearchResultsListInDetailedView()
    {
        waitUntilElementsAreVisible(By.cssSelector(".propertiesCell .nameAndTitleCell a .value"));
        return !findElements(resultsDetailedViewList).isEmpty();
    }

    public boolean isSearchResultsListInGalleryView()
    {
        waitUntilElementIsVisible(By.cssSelector("[id*='FCTSRCH_GALLERY_VIEW_THUMBNAIL']"));
        return !findElements(resultsGalleryViewList).isEmpty();
    }

    public SearchPage clickGalleryView()
    {
        clickViewsDropdown();
        waitUntilElementIsPresent(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP'] td[class='dijitReset dijitMenuItemLabel']"));
        selectOptionFromFilterOptionsList("Gallery View", findElements(viewsDropdownOptionsSelector));
        return this;
    }

    public SearchPage clickDetailedView()
    {
        clickViewsDropdown();
        waitUntilElementIsPresent(By.cssSelector("div[id='DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP'] td[class='dijitReset dijitMenuItemLabel']"));
        selectOptionFromFilterOptionsList("Detailed View", findElements(viewsDropdownOptionsSelector));
        waitUntilElementDisappears(By.cssSelector("table[class*='alfresco-documentlibrary-AlfGalleryViewSlider']"));
        return this;
    }

    public boolean isSliderGalleryViewDisplayed()
    {
        return (isElementDisplayed(sliderGalleryView)
            && isElementDisplayed(sliderIncrementIcon)
            && isElementDisplayed(sliderDecrementIcon));
    }

    public void clickFilterOption(String option, String filterBy)
    {
        By selector = By.cssSelector("div.alfresco-documentlibrary-AlfDocumentFilters[id$='" + filterBy + "'] .filterLabel");
        List<WebElement> filterOptionList = findElements(selector);
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
        List<WebElement> results = findElements(resultsDetailedViewList);
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
        assertTrue(isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }

    public SearchPage assertSearchManagerButtonIsNotDisplayed()
    {
        log.info("Assert search manager button is NOT displayed");
        assertFalse(isElementDisplayed(searchManager), "Search manager is displayed");
        return this;
    }

    public SearchManagerPage clickSearchManagerLink()
    {
        clickElement(searchManager);
        return new SearchManagerPage(webDriver);
    }

    public void clickShowMore()
    {
        List<WebElement> showMoreList = findElements(showMore);
        if (!showMoreList.isEmpty())
            for (WebElement aShowMore : showMoreList)
            {
                if (aShowMore.isDisplayed())
                    aShowMore.click();
            }
    }

    public void clickShowFewer()
    {
        List<WebElement> showLessList = findElements(showLess);
        if (!showLessList.isEmpty())
            for (WebElement showLess : showLessList)
            {
                if (showLess.isDisplayed())
                    showLess.click();
            }
    }

    public int getFilterTypePosition(String filter)
    {
        waitInSeconds(2);
        return findFirstElementWithValue(filterTypeList, filter)
            .findElements(By.xpath("ancestor::div[contains(@id, 'FCTSRCH_filter_')]/preceding-sibling::*")).size() + 1;
    }

    public boolean isActionsLinkDisplayed()
    {
        return isElementDisplayed(actionsLink);
    }

    public SearchPage clickCheckbox(String searchResult)
    {
        if (isResultFound(searchResult))
        {
            List<WebElement> checkBoxElements = findElements(checkboxList);
            int i;
            for (i = 0; i < checkBoxElements.size(); i++)
            {
                log.info("Position {} ", i);
                checkBoxElements.get(i).click();
                findDisplayedElementsFromLocator(checkboxSelector);
            }
        }
        return this;
    }
    public SearchPage click_checkBox(int searchPosition){
        List<WebElement> checkBoxElements = findElements(checkboxList);
        checkBoxElements.get(searchPosition).click();
        return this;
    }
    public boolean isSelectedItemsListOptionDisplayed(String optionName)
    {
        WebElement action = findFirstElementWithValue(selectedItemsCheckboxOptions, optionName);
        return isElementDisplayed(action);
    }

    public SearchPage assert_isSelectedItemsListOptionDisplayed(String option_Name)
    {
        Assert.assertTrue(isSelectedItemsListOptionDisplayed(option_Name), option_Name+" not displayed");
        return this;
    }

    public void clickOptionFromSelectedItemsListCheckbox(String optionName)
    {
        if (isSelectedItemsListOptionDisplayed(optionName))
        {
            WebElement action = findFirstElementWithValue(selectedItemsCheckboxOptions, optionName);
            clickElement(action);
        }
    }

    public SearchPage clickSelectedItemsDropdown()
    {
        clickElement(selectedItemsDropdown);
        waitUntilElementIsVisibleWithRetry(By.id("SELECTED_ITEMS_MENU_dropdown"), 3);
        return this;
    }

    public boolean isSelectedItemsOptionDisplayed(String optionName)
    {
        WebElement action = findFirstElementWithValue(selectedItemsOptions, optionName);
        return isElementDisplayed(action);
    }

    public SearchPage assert_IsSelectedItemsOptionDisplayed(String option_Name)
    {
        Assert.assertTrue(isSelectedItemsOptionDisplayed(option_Name), option_Name +" not Displayed");
        return this;
    }

    public void clickOptionFromSelectedItemsDropdown(String optionName)
    {
        if (isSelectedItemsOptionDisplayed(optionName))
        {
            WebElement action = findFirstElementWithValue(selectedItemsOptions, optionName);
            clickElement(action);
        }
    }
    public void click_OptionFromSelectedItemsDropdown(String optionName )
    {
        List<WebElement> dropDownOptions = findElements(selectedItemsOptions);
        for (WebElement webElement : dropDownOptions)
        {
            if (webElement.getText().contains(optionName))
            {
                webElement.click();
                break;
            }
        }

    }
    public SearchPage click_OptionFromSelectedListItemsDropdown(String optionName )
    {
        List<WebElement> dropDownOptions = findElements(selectedItemsCheckboxOptions);
        for (WebElement webElement : dropDownOptions)
        {
            if (webElement.getText().contains(optionName))
            {
                webElement.click();
                break;
            }
        }
        return this;
    }
    protected void selectOptionFromFilterOptionsList(String option,
                                                     List<WebElement> filterOptionsList)
    {
        for (WebElement webElement : filterOptionsList)
        {
            if (webElement.getText().contains(option))
            {
                webElement.click();
                break;
            }
        }
    }

    public boolean isALLItemsCheckboxChecked()
    {
        return isElementDisplayed(By.xpath("//div[@id='SELECTED_LIST_ITEMS']/img[@alt='You have all items selected. Click this icon to deselect all.']"));
    }
    public SearchPage assert_isAllItemsCheckBoxChecked()
    {
        Assert.assertTrue(isALLItemsCheckboxChecked(), "Checkbox Unchecked");
        return this;
    }

    public boolean isNoneItemsCheckboxChecked()
    {
        waitInSeconds(3);
        return isElementDisplayed(By.xpath("//div[@id='SELECTED_LIST_ITEMS']/img[@alt='You have no items selected. Click this icon to select all.']"));
    }
    public SearchPage assert_isNoneItemsCheckBoxChecked()
    {
        Assert.assertTrue(isNoneItemsCheckboxChecked(), "Checkbox Unchecked");
        return this;
    }

    public String getSelectedItemsState()
    {
        waitInSeconds(2);
        return findElement(By.cssSelector("div[id='SELECTED_ITEMS_MENU']")).getAttribute("aria-disabled");
    }

    public void deleteDocuments(boolean areYouSure)
    {
        //       clickElement(deleteDialogCancel);

        if (areYouSure)
        {
            while (isElementDisplayed(deleteDialogConfirm))
            {
                waitInSeconds(3);
                clickElement(deleteDialogConfirm);
                waitInSeconds(3);
            }
        } else
        {
            while (isElementDisplayed(deleteDialogCancel))
            {
                clickElement(deleteDialogCancel);
            }
        }
    }

    public SearchPage clickSelectAll()
    {
        clickElement(By.cssSelector("div[id='SELECTED_LIST_ITEMS']"));
        waitUntilElementIsVisibleWithRetry(By.id("SELECTED_LIST_ITEMS_dropdown"), 5);
        clickElement(By.cssSelector("tr[title='All'] td[class*='dijitMenuItemLabel']"));
        return this;
    }

    public SearchPage clickSelectedItemsListDropdownArrow()
    {
        clickElement(selectedListItemsDropdownArrow);
        return this;
    }

    public boolean isNameHighlighted(String name)
    {
        List<WebElement> allNameHighlights = findElements(nameHighlight);
        for (WebElement eachNameHighlight : allNameHighlights)
        {
            if (eachNameHighlight.getText().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isResultsDisplayedInSearch(String query)
    {
        int retryCounter = 0;
        while (!isElementDisplayed(By.cssSelector(".propertiesCell .nameAndTitleCell a .value")) && retryCounter < 4)
        {
            waitInSeconds(2);
            refresh();
            retryCounter++;
        }
        waitInSeconds(3);
        List<WebElement> searchResults = findElements(resultsDetailedViewList);
        for (WebElement result : searchResults)
        {
            if (result.getText().contains(query))
                return true;
        }
        return false;
    }

    public boolean is_ContentHighlighted(String content)
    {
        List<WebElement> allContentHighlights = findElements(contentHighlight);
        for (WebElement eachContentHighlight : allContentHighlights)
        {
            if (eachContentHighlight.getText().equals(content))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isContentHighlighted(String content)
    {
        if (!isElementDisplayed(contentHighlight))
        {
            return false;
        } else {
            return findElement(contentHighlight).getText().equals(content);
        }
    }

    public boolean isDescriptionHighlighted(String description)
    {
        if (!isElementDisplayed(descriptionHighlight))
        {
            return false;
        } else {
            return findElement(descriptionHighlight).getText().equals(description);
        }
    }

    public boolean isTitleHighlighted(String title)
    {
        if (!isElementDisplayed(titleHighlight))
        {
            return false;
        } else {
            return findElement(titleHighlight).getText().equals(title);
        }
    }
    public SearchPage assertIsNameHighlighted(String name)
    {
        Assert.assertTrue(isNameHighlighted(name), name+" is not highlighted");
        return this;
    }
    public SearchPage assertNameNotHighlighted(String name)
    {
        waitInSeconds(3);
        Assert.assertFalse(isNameHighlighted(name), name+" is not highlighted");
        return this;
    }
    public SearchPage assertIsContentHighlighted(String content)
    {
        Assert.assertTrue(isContentHighlighted(content), content+" is not highlighted");
        return this;
    }
    public SearchPage assertIsDescriptionHighlighted(String name)
    {
        Assert.assertTrue(isDescriptionHighlighted(name), name+" is not highlighted");
        return this;
    }
    public SearchPage assertIsTitleHighlighted(String title)
    {
        Assert.assertTrue(isTitleHighlighted(title), title+" is not highlighted");
        return this;
    }

    public String getNoSearchResultsText()
    {
        return findElement(noSearchResults).getText();
    }

    public boolean isAnyFolderReturnedInResults()
    {
        return isElementDisplayed(folderResult);
    }

    public boolean isAnyFileReturnedInResults()
    {
        waitInSeconds(3);
        return isElementDisplayed(contentResult);
    }

    public boolean isConfirmDeletionDialogDisplayed()
    {
        return isElementDisplayed(confirmDeleteDialog);
    }
    public SearchPage assertConfirmDeletionDialogNotDisplayed()
    {
        Assert.assertFalse(isConfirmDeletionDialogDisplayed(),"Confirm deletion dialog is still displayed");
        return this;
    }

    public boolean isSiteHighlighted()
    {
        return isElementDisplayed(highlightedSite);
    }

    public boolean isModifiedOnHighlighted()
    {
        return isElementDisplayed(highlightedModifiedDate);
    }

    public void setSearchExpression(String searchExpression)
    {
        clearAndType(findElement(inputField), searchExpression);
    }

    public void clickSearchButton()
    {
        clickElement(searchButton);
    }
    public void clickSearchButtonAgain()
    {
        waitInSeconds(5);
        clickElement(searchButton);
        waitInSeconds(5);
        refresh();
        clickElement(searchButton);
        waitInSeconds(5);
        refresh();
        clickElement(searchButton);
        waitInSeconds(5);
        refresh();
    }

    public boolean confirmNoItemIsHighlighted()
    {
        waitInSeconds(3);
        return isElementDisplayed(highlight);
    }

    public void clickContentName(String contentName)
    {
        docName(contentName);
    }
    public void clickOnContentName(String contentName)
    {
        docName(contentName).click();
    }


    public void clickSortDropdownOption(String sortingOption)
    {
        findFirstDisplayedElement(By.xpath("//div[@id='DOCLIB_SORT_FIELD_SELECT_GROUP']//td[text()='" + sortingOption + "']")).click();
        waitUntilElementContainsText(findElement(currentSortFilter), sortingOption);
    }

    public boolean isSortOrderToggleButtonDisplayed()
    {
        return isElementDisplayed(sortOrderToggleButton);
    }

    public void clickToggleButton()
    {
        clickElement(sortOrderToggleButton);
    }

    public boolean isDescendingOrderSet()
    {
        return isElementDisplayed(By.cssSelector("div[title='Change sort order to descending']"));
    }

    public boolean isAscendingOrderSet()
    {
        return isElementDisplayed(By.cssSelector("div[title='Change sort order to ascending']"));
    }

    public String getSortFilter()
    {
        return findElement(currentSortFilter).getText().trim();
    }

    public int getResultsListSize()
    {
        waitInSeconds(3);
        return findElements(resultsList).size();
    }

    public boolean areResultsSortedByName()
    {
        boolean status;
        List<String> resultsListText = new ArrayList<>();
        List<WebElement> resultsName = findElements(By.cssSelector("tr[id='FCTSRCH_SEARCH_RESULT'] a[class^='alfresco-navigation'] span.value"));
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
        clickElement(sortDropdownButton);
        for (WebElement option : findElements(sortOptions))
        {
            availableOptions.add(option.getText());
        }

        log.info("Available options are: {}", availableOptions);

        return availableOptions;
    }

    public void selectFileTypeFilter(String fileType)
    {
        clickElement(fileTypeOption(fileType));
    }

    public int getGalleryViewResultsNumber()
    {
        return findElements(galleryViewResultsList).size();
    }

    public void openFileFromGalleryView(String fileName)
    {
        findFirstElementWithValue(galleryViewResultsList, fileName).click();
    }

    public void clickContentThumbnailByName(String contentName)
    {
        image(contentName).click();
    }

    public String getPreviewedImageName()
    {
        return findElement(imagePreview).getText();
    }

    public boolean isContentPreviewed(String docName)
    {
        return findElement(previewTitle).getText().equals(docName);
    }

    public boolean isConfigureSearchButtonDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(configureSearchButton);
    }

    public void clickModifiedBy(String modifierName)
    {
        findFirstElementWithValue(modifiedBy, modifierName).click();
    }

    public void closePicturePreview()
    {
        clickElement(closePicturePreview);
        waitUntilElementDisappears(By.cssSelector("img[id='aikauLightboxImage']"));
    }

    public void closeFilePreview()
    {
        clickElement(closeFilePreviewButton);
        waitUntilElementDisappears(By.cssSelector("div[class$='dialogDisplayed dijitDialog']"));
    }

    public SearchPage checkContent(ContentModel... contentModels)
    {
        for(ContentModel content : contentModels)
        {
            log.info("Check content: {}", content.getName());
            clickElement(getContentRowResult(content).findElement(contentCheckBox));
        }
        return this;
    }

    public SearchPage clickSelectedItems()
    {
        log.info("Click Selected Items...");
        clickElement(selectedItemsMenu);
        return this;
    }

    public SearchCopyMoveDialog clickCopyToForSelectedItems()
    {
        log.info("Click Copy to... from Selected Items");
        clickElement(copyToAction);
        return new SearchCopyMoveDialog(webDriver);
    }

    public SearchContentActionComponent usingContent(ContentModel contentModel)
    {
        return new SearchContentActionComponent(webDriver, contentModel);
    }
    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }
    public SearchPage pageRefresh()
    {
        waitInSeconds(10);
        getWebDriver().navigate().refresh();
        waitInSeconds(5);
        getWebDriver().navigate().refresh();
        waitInSeconds(5);
        getWebDriver().navigate().refresh();
        waitInSeconds(5);
        getWebDriver().navigate().refresh();
        return this;
    }

    public boolean SearchManagerButtonDisplayed() {
        {
            for (int i = 0; i < 6; i++) {
                if (isElementDisplayed(searchManager)) {
                    return true;
                } else {
                    refresh();
                    waitInSeconds(3);
                }
            }
        }
        return false;
    }
}
