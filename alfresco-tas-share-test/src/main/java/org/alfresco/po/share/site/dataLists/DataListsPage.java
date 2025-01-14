package org.alfresco.po.share.site.dataLists;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class DataListsPage extends SiteCommon<DataListsPage>
{
    public Content currentContent = new NoListItemSelectedContent();
    private final By dataListCurrentPage = By.cssSelector("div.title-bar [id$=default-viewButtons]");
    private final By clickSaveButton = By.cssSelector("#template_x002e_datalists_x002e_data-lists_x0023_default-newList-form-submit-button");

    private final By dataListsSection = By.cssSelector("div.datalists div.filter");
    private final By newListButton = By.cssSelector("button[id*='newListButton']");
    private final By editListButton = By.cssSelector(".filter-link>.edit");
    private final By editButtonDisabled = By.cssSelector("span[class='edit-disabled']");
    private final By deleteListButton = By.cssSelector(".filter-link>.delete");
    private final By noListDisplayed = By.cssSelector("div[class='no-lists']");
    private final By noListItemsDisplayMessage = By.xpath("//div[text()=\"No list items\"]");
    private final By successfullyCreatedMessage = By.cssSelector("div[id='message_c'] span[class='message']");
    private final By balloon = By.cssSelector("div[class='bd'] span[class='message']");
    private final By listWithCreatedLists = By.cssSelector(".datalists ul");
    private final By editListItemButton = By.cssSelector(".yui-dt-col-actions .onActionEdit>a");
    private final By deleteListItemButton = By.cssSelector(".yui-dt-col-actions .onActionDelete>a");
    private final By duplicateListItemButton = By.cssSelector(".yui-dt-col-actions .onActionDuplicate>a");
    private final By listItemActionsField = By.cssSelector("td[headers*='actions']");
    private final By listSelected = By.cssSelector("[class='selected'] a[class='filter-link']");
    private final By tableColumnHeader = By.cssSelector("div[id$='default-grid'] th span");
    private final By newListDialogTitle = By.cssSelector(".hd");
    private final By listTitleTextInput = By.cssSelector("input[name$='prop_cm_title']");
    private final By listDescriptionTextAreaInput = By.cssSelector("textarea[title$='Content Description']");
    private final By newItemButton = By.cssSelector("div[class$='new-row'] span span button[id$='_default-newRowButton-button']");
    private final By newListSaveButton = By.cssSelector(".bdft button[id*='submit-button']");
    private final By newListCancelButton = By.cssSelector(".bdft button[id*='cancel-button']");
    private final By listMessage = By.cssSelector(".select-list-message");
    private final By createDataListLinkLocator = By.cssSelector("a[href='data-lists#new']");
    private final By listItemData = By.className("yui-dt-liner");
    private final By listItemRowPagination= By.className("yui-pg-current");
    private final String createNewItemForm = "//form[contains(@action, '%s')]";
    private final String listLinkLocator = "//a[@class='filter-link']";
    private final String listItemTitleLocator = "//div[@class='datagrid']//h2[text()='%s']";

    private final By  selectButtonSelector = By.cssSelector("button[id*='itemSelect']");

    private final By selectItemsButtonSelector = By.cssSelector("button[id*='selectedItems']");

    protected By duplicate = By.cssSelector("a[title='Duplicate']");
    protected By actionsColumn = By.cssSelector("td[class*='col-actions'] div");
    private By previousNavigationLinkLocator = By.cssSelector("[class*='pg-previous']");
    private By nextNavigationLinkLocator = By.cssSelector("[class*='pg-next']");
    private By noListItems = By.cssSelector("div[id$='default-grid'] table tbody tr");
    private By currentPageLocator = By.cssSelector("span[class*='current-page']");
    private String selectPage = "span[class*='pages'] a[title='Page %s']";
    private String selectedColumnItems = "table tbody td[class*='%s']";
    private String selectColumn = "div[id$='default-grid'] table thead tr div[id*='%s'] span a";

    public DataListsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/data-lists", getCurrentSiteName());
    }

    public DataListsPage assertDataListPageIsOpened()
    {
        log.info("Assert data list page is opened");
        assertTrue(getCurrentUrl().contains("data-lists"), "Data List page is not opened");
        return this;
    }
    public DataListsPage getCurrentPageTitle()
    {
        findElement(dataListCurrentPage);
        return this;
    }

    public CreateDataListDialog clickOnSaveButton()
    {
        clickElement(clickSaveButton);
        return new CreateDataListDialog(webDriver);
    }

    public DataListsPage assertEmptyListMessageEquals(String emptyMessageExpected)
    {
        log.info("Assert empty list message equals: {}", emptyMessageExpected);
        assertEquals(getElementText(listMessage), emptyMessageExpected,
            String.format("Empty list message not equals %s ", emptyMessageExpected));

        return this;
    }

    public DataListsPage clickOnCreateDataListLink()
    {
        log.info("Click New List button");
        waitInSeconds(2);
        clickElement(findElement(createDataListLinkLocator));
        return this;
    }

    public DataListsPage setTitle(String title)
    {
        log.info("Set title value: {}", title);
        clearAndType(listTitleTextInput, title);
        return this;
    }

    public DataListsPage setDescription(String description)
    {
        log.info("Set description value: {}", description);
        clearAndType(listDescriptionTextAreaInput, description);
        return this;
    }

    public List<String> getListsItemsTitle()
    {
        waitUntilElementIsVisible(listWithCreatedLists);

        List<WebElement> linksList = waitUntilElementIsVisible(dataListsSection)
            .findElements(By.cssSelector("a.filter-link"));
        List<String> dataListsName = new ArrayList<>(linksList.size());
        for (WebElement list : linksList)
        {
            dataListsName.add(list.getText());
        }
        return dataListsName;
    }

    private WebElement getDataListElement(String listName)
    {
        List<WebElement> linksList = waitUntilElementIsVisible(dataListsSection)
            .findElements(By.cssSelector("a.filter-link"));
        return findFirstElementWithValue(linksList, listName);
    }

    public boolean noListDisplayed()
    {
        return isElementDisplayed(noListDisplayed);
    }

    public boolean noListItemsDisplayed()
    {
        return isElementDisplayed(noListItemsDisplayMessage);
    }

    public boolean isNewItemButtonDisplayed()
    {
        return isElementDisplayed(newItemButton);
    }

    private DataListsPage clickDataList(String listName, Class c)
    {
        getDataListElement(listName).click();
        try
        {
            currentContent = (Content) c.getDeclaredConstructor().newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public DataListsPage clickContactListItem(String listName)
    {
        return clickDataList(listName, ContactListSelectedContent.class);
    }

    public DataListsPage clickEventAgendaListItem(String listName)
    {
        return clickDataList(listName, EventAgendaSelectedContent.class);
    }

    public DataListsPage clickEventListItem(String listName)
    {
        return clickDataList(listName, EventListSelectedContent.class);
    }

    public DataListsPage clickLocationListItem(String listName)
    {
        return clickDataList(listName, LocationListSelectedContent.class);
    }

    public DataListsPage clickIssueListItem(String listName)
    {
        return clickDataList(listName, IssueListSelectedContent.class);
    }

    public DataListsPage clickMeetingAgendaListItem(String listName)
    {
        return clickDataList(listName, MeetingAgendaListSelectedContent.class);
    }

    public DataListsPage clickAdvancedTaskListItem(String listName)
    {
        return clickDataList(listName, AdvancedTaskListSelectedContent.class);
    }

    public DataListsPage clickSimpleTaskListItem(String listName)
    {
        return clickDataList(listName, SimpleTaskListSelectedContent.class);
    }

    public DataListsPage clickToDoListItem(String listName)
    {
        return clickDataList(listName, ToDoListSelectedContent.class);
    }

    public DataListsPage clickVisitorFeedbackListItem(String listName)
    {
        return clickDataList(listName, VisitorFeedbackListSelectedContent.class);
    }

    public boolean isEditButtonDisplayedForList(String listName)
    {
        mouseOver(getDataListElement(listName));
        return isElementDisplayed(editListButton);
    }

    public boolean isDeleteButtonDisplayedForList(String listName)
    {
        mouseOver(getDataListElement(listName));
        return isElementDisplayed(deleteListButton);
    }

    public EditListDetailsPopUp clickEditButtonForList(String listName)
    {
        mouseOver(getDataListElement(listName));
        clickElement(editListButton);
        return new EditListDetailsPopUp(webDriver);
    }


    public DeleteListPopUp clickDeleteButtonForList(String listName)
    {
        mouseOver(getDataListElement(listName));
        clickElement(deleteListButton);
        return new DeleteListPopUp(webDriver);
    }

    public DeleteListPopUp clickDeleteButtonForListItem()
    {
        mouseOver(listItemActionsField);
        clickElement(deleteListItemButton);
        return new DeleteListPopUp(webDriver);
    }

    public CreateDataListDialog clickOnNewListButton()
    {
        clickElement(newListButton);
        return new CreateDataListDialog(webDriver);
    }

    public String successfullyCreatedDataListMessage()
    {
        return getElementText(successfullyCreatedMessage);
    }

    public boolean isEditButtonDisabled(String listName)
    {
        mouseOver(getDataListElement(listName));
        return isElementDisplayed(editButtonDisabled);
    }

    public void clickOnDisabledEditButton(String listName)
    {
        mouseOver(getDataListElement(listName));
        clickElement(editButtonDisabled);
    }

    public DataListsPage assertDataListItemTitleEquals(String itemTitle)
    {
        log.info("Assert data list item title equals: {}", itemTitle);
        WebElement actualTitle = findElement(By.xpath(String.format(listItemTitleLocator, itemTitle)));
        assertEquals( actualTitle.getText(), itemTitle, String.format("Data list item title not equals %s: ", itemTitle));

        return this;
    }

    public EditItemPopUp clickEditButtonForListItem()
    {
        waitInSeconds(2);
        mouseOver(listItemActionsField);
        waitUntilElementIsVisible(editListItemButton).click();
        return new EditItemPopUp(webDriver);
    }

    public CreateNewItemPopUp clickNewItemButton()
    {
        waitUntilElementIsVisible(newItemButton).click();
        return new CreateNewItemPopUp(webDriver);
    }

    public List<String> getTextOfTableColumnHeader()
    {
        List<String> tableHeaderListString = new ArrayList<>();
        for (WebElement item : findElements(tableColumnHeader))
        {
            tableHeaderListString.add(item.getText());
        }

        return tableHeaderListString;
    }

    public boolean isNewItemPopupFormDisplayed(CreateNewItemPopUp.NewItemPopupForm listName)
    {
        waitInSeconds(3);
        return isElementDisplayed(By.xpath(String.format(createNewItemForm, listName.name)));
    }

    public String messageDisplayed()
    {
        return getElementText(balloon);
    }

    public List<String> getFilterTypeList()
    {
        List<String> listItems = new ArrayList<>();
        waitInSeconds(2);
        for (WebElement listItemNames : findElements(listItemData))
        {
            listItems.add(listItemNames.getText());
        }
        return listItems;
    }

    public void clickNewItemButtons()
    {
        waitInSeconds(3);
        findElement(newItemButton).click();
    }

    public EditItemPopUp clickDuplicateButtonForListItem()
    {
        mouseOver(listItemActionsField);
        waitUntilElementIsVisible(duplicateListItemButton).click();
        return new EditItemPopUp(webDriver);
    }

    public boolean isNewListItemRowAdded(String rowNumber)
    {
        waitInSeconds(2);
        for (WebElement listItems : findElements(listItemRowPagination))
        {
            if (listItems.getText().contains(rowNumber))
            {
                return true;
            }
        }
        return false;
    }

    public List<String> getFilterOptionsValues()
    {
        List<WebElement> filterOptions = findElements(By.cssSelector("ul[class='filterLink'] a"));
        List<String> filterOptionsValues = new ArrayList<>(filterOptions.size());
        for (WebElement option : filterOptions)
        {
            filterOptionsValues.add(option.getText());
        }
        return filterOptionsValues;
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

    public boolean isSelectButtonDisplayed()
    {
        return isElementDisplayed(findElement(selectButtonSelector));
    }

    public boolean isSelectItemsButtonEnabled()
    {
        return findElement(selectItemsButtonSelector).isEnabled();
    }

    public boolean areNavigationLinksDisplayed()
    {
        waitUntilElementIsDisplayedWithRetry(previousNavigationLinkLocator);
        return findElement(previousNavigationLinkLocator).isDisplayed() && findElement(nextNavigationLinkLocator).isDisplayed();
    }

    public boolean isAnyListItemDisplayed()
    {
        List<WebElement> listItemsList = findElements(noListItems);
        return !(listItemsList.size() == 1 && listItemsList.get(0).findElement(By.cssSelector("td div")).getText().equals("No list items"));
    }

    public DataListsPage pageRefresh()
    {
        waitInSeconds(2);
        getWebDriver().navigate().refresh();
        return this;
    }

    public String getCurrentPageNumber()
    {
        return findElement(currentPageLocator).getText();
    }

    public void clickNextNavigationItem()
    {
        waitInSeconds(2);
        findElement(nextNavigationLinkLocator).click();
    }

    public void clickPreviousNavigationItem()
    {
        waitUntilElementIsDisplayedWithRetry(previousNavigationLinkLocator);
        findElement(previousNavigationLinkLocator).click();
    }

    public void clickOnSpecificPage(String number)
    {
        findElement(By.cssSelector(String.format(selectPage, number))).click();
    }

    private List<String> retrieveSpecificColumnItems(String column)
    {
        waitUntilElementIsDisplayedWithRetry(By.cssSelector(String.format(selectedColumnItems, column)));
        List<WebElement> items = findElements(By.cssSelector(String.format(selectedColumnItems, column)));
        List<String> text = new ArrayList<>(items.size());
        for (WebElement item : items)
        {
            text.add(item.getText());
        }
        return text;
    }

    private void clickOnSpecificColumn(String column)
    {
        findElement(By.cssSelector(String.format(selectColumn, column))).click();
    }

    public boolean areItemsSortedByColumnAfterClickingTheColumn(String column)
    {
        List<String> currentOrder = retrieveSpecificColumnItems(column);
        Collections.sort(currentOrder);
        clickOnSpecificColumn(column);
        return currentOrder.equals(retrieveSpecificColumnItems(column));
    }

    public boolean isAttachmentsUpdated(String attachment)
    {
        if (getFilterTypeList().contains(attachment)){
            return true;
        }
        return false;
    }
}
