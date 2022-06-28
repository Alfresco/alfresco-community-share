package org.alfresco.po.share.site.dataLists;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class DataListsPage extends SiteCommon<DataListsPage>
{
    public Content currentContent = new NoListItemSelectedContent();
    private final By dataListsSection = By.cssSelector("div.datalists div.filter");
    private final By newListButton = By.cssSelector("button[id*='newListButton']");
    private final By editListButton = By.cssSelector(".filter-link>.edit");
    private final By editButtonDisabled = By.cssSelector("span[class='edit-disabled']");
    private final By deleteListButton = By.cssSelector(".filter-link>.delete");
    private final By noListDisplayed = By.cssSelector("div[class='no-lists']");
    private final By successfullyCreatedMessage = By.cssSelector("div[id='message_c'] span[class='message']");
    private final By listWithCreatedLists = By.cssSelector(".datalists ul");
    private final By editListItemButton = By.cssSelector(".yui-dt-col-actions .onActionEdit>a");
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

    private final String createNewItemForm = "//form[contains(@action, '%s')]";
    private final String listLinkLocator = "//a[@class='filter-link']";
    private final String listItemTitleLocator = "//div[@class='datagrid']//h2[text()='%s']";

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
        for (WebElement item : waitUntilElementsAreVisible(tableColumnHeader))
        {
            tableHeaderListString.add(item.getText());
        }

        return tableHeaderListString;
    }

    public boolean isNewItemPopupFormDisplayed(CreateNewItemPopUp.NewItemPopupForm listName)
    {
        return isElementDisplayed(By.xpath(String.format(createNewItemForm, listName.name)));
    }
}
