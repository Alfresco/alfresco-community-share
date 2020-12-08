package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class DataListsPage extends SiteCommon<DataListsPage>
{
    public Content currentContent = new NoListItemSelectedContent();

    @RenderWebElement
    @FindBy (css = "div.datalists div.filter")
    protected WebElement dataListsSection;

    @RenderWebElement
    @FindBy (className = "datagrid")
    protected WebElement dataListsBody;

    @RenderWebElement
    @FindBy (css = "button[id*='newListButton']")
    protected WebElement newListButton;

    @FindBy (css = ".filter-link>.edit")
    protected WebElement editListButton;

    @FindBy (css = "span[class='edit-disabled']")
    protected WebElement editButtonDisabled;

    @FindBy (css = ".filter-link>.delete")
    protected WebElement deleteListButton;

    @FindBy (css = "div[class='no-lists']")
    protected WebElement noListDisplayed;

    @FindBy (css = "div[id='message_c'] span[class='message']")
    protected WebElement successfullyCreatedMessage;

    @FindBy (css = ".datalists ul")
    protected WebElement listWithCreatedLists;

    protected By editListItemButton = By.cssSelector(".yui-dt-col-actions .onActionEdit>a");

    @FindBy (css = "td[headers*='actions']")
    protected WebElement listItemActionsField;

    protected By listSelected = By.cssSelector("[class='selected'] a[class='filter-link']");
    protected String createNewItemForm = "//form[contains(@action, '%s')]";

    @FindAll (@FindBy (css = "div[id$='default-grid'] th span"))
    protected List<WebElement> tableColumnHeader;

    @FindBy (css = ".hd")
    protected WebElement newListDialogTitle;

    @FindBy (css = "input[name$='prop_cm_title']")
    protected WebElement listTitleTextInput;

    @FindBy (css = "textarea[title$='Content Description']")
    protected WebElement listDescriptionTextAreaInput;

    @FindBy (css = "div[class$='new-row'] span span button[id$='_default-newRowButton-button']")
    private WebElement newItemButton;

    @FindBy (css = ".bdft button[id*='submit-button']")
    protected WebElement newListSaveButton;

    @FindBy (css = ".bdft button[id*='cancel-button']")
    protected WebElement newListCancelButton;

    @FindBy (css = ".item-types div")
    protected WebElement listType;

    @FindBy (css = ".select-list-message")
    protected WebElement listMessage;

    @Autowired
    private CreateDataListDialog createDataListDialog;

    @Autowired
    private EditListDetailsPopUp editListDetailsPopUp;

    @Autowired
    private DeleteListPopUp deleteListPopUp;

    //@Autowired
    private EditItemPopUp editItemPopUp;

    @Autowired
    private CreateNewItemPopUp createNewItemPopUp;

    private final String listLinkLocator = "//a[@class='filter-link']";
    private final String listItemTitleLocator = "//div[@class='datagrid']//h2[text()='%s']";
    private final By createDataListLinkLocator = By.cssSelector("a[href='data-lists#new']");
    private final By newListWindowLocator = By.cssSelector(".hd");

    public DataListsPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/data-lists", getCurrentSiteName());
    }

    public DataListsPage assertDataListPageIsOpened()
    {
        LOG.info("Assert data list page is opened");
        assertTrue(getBrowser().getCurrentUrl().contains("data-lists"), "Data List page is not opened");
        return this;
    }

    public DataListsPage assertNoDataListSelectedMessageIsDisplayed()
    {
        LOG.info("Assert no data list selected message is displayed");
        assertEquals(listMessage.getText(), language.translate("dataListPage.noListSelected.message"),
            "No list message is not displayed");
        return this;
    }

    public DataListsPage assertEmptyListMessageEquals(String emptyMessageExpected)
    {
        LOG.info("Assert empty list message equals: {}", emptyMessageExpected);
        assertEquals(listMessage.getText(), emptyMessageExpected,
            String.format("Empty list message not equals %s ", emptyMessageExpected));

        return this;
    }

    public boolean isExpectedListSelected(String expectedList)
    {
        return getBrowser().findElement(listSelected).getText().equals(expectedList);
    }

    public DataListsPage setListItemSelectedContent()
    {
        LOG.info("Set list item selected content");
        currentContent = new ListItemSelectedContent();
        currentContent.setBrowser(getBrowser());
        return this;
    }

    public DataListsPage assertDataListContentIsDisplayed()
    {
        LOG.info("Assert data list content is displayed");
        assertTrue(currentContent.isDataListContentDisplayed(), "Data list content is not displayed");
        return this;
    }

    public DataListsPage assertNewListDialogIsNotDisplayed()
    {
        LOG.info("Assert new list dialog is not displayed");
        assertFalse(getBrowser().isElementDisplayed(newListWindowLocator), "");
        return this;
    }

    public DataListsPage clickOnCreateDataListLink()
    {
        LOG.info("Click \"New List\" button");
        getBrowser().findElement(createDataListLinkLocator).click();
        return this;
    }

    public DataListsPage assertNewListDialogTitleEquals(String expectedDialogTitle)
    {
        LOG.info("Assert \"New List\" dialog title is: {}", expectedDialogTitle);
        assertEquals(newListDialogTitle.getText(), expectedDialogTitle,
            String.format("Dialog title not equals %s", expectedDialogTitle));
        return this;
    }

    public DataListsPage setTitle(String title)
    {
        LOG.info("Set title value: {}", title);
        clearAndType(listTitleTextInput, title);
        return this;
    }

    public DataListsPage setDescription(String description)
    {
        LOG.info("Set description value: {}", description);
        clearAndType(listDescriptionTextAreaInput, description);
        return this;
    }

    public DataListsPage assertDialogInputDescriptionEquals(String expectedDialogInputDescription)
    {
        LOG.info("Assert list dialog input description equals with: {}", expectedDialogInputDescription);
        assertEquals(listDescriptionTextAreaInput.getAttribute("value"), expectedDialogInputDescription,
            String.format("List dialog input description not equals with %s ", expectedDialogInputDescription));
        return this;
    }

    public DataListsPage clickDialogSaveButton()
    {
        LOG.info("Click save button from new list dialog");
        newListSaveButton.click();
        return this;
    }

    public DataListsPage clickDialogCancelButton()
    {
        LOG.info("Click cancel button from new list dialog");
        newListCancelButton.click();
        return this;
    }

    public DataListsPage assertDialogInputTitleEquals(String expectedDialogInputTitle)
    {
        LOG.info("Assert list dialog input title equals with: {}", expectedDialogInputTitle);
        assertEquals(listTitleTextInput.getAttribute("value"), expectedDialogInputTitle,
            String.format("List dialog input title not equals with %s ", expectedDialogInputTitle));
        return this;
    }

    public DataListsPage assertDataListLinkDescriptionEquals(String expectedListDescription)
    {
        LOG.info("Assert data list link description equals: {}", expectedListDescription);
        WebElement linkLocator = getBrowser().findElement(By.xpath(listLinkLocator));
        assertEquals(linkLocator.getText(), expectedListDescription,
            String.format("List link description not equals %s", linkLocator.getText()));
        return this;
    }

    public List<String> getListsItemsTitle()
    {
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(listWithCreatedLists);

        List<WebElement> linksList = dataListsSection.findElements(By.cssSelector("a.filter-link"));
        List<String> dataListsName = new ArrayList<>(linksList.size());
        for (WebElement list : linksList)
        {
            dataListsName.add(list.getText());
        }
        return dataListsName;
    }

    private WebElement getDataListElement(String listName)
    {
        List<WebElement> linksList = dataListsSection.findElements(By.cssSelector("a.filter-link"));
        return getBrowser().findFirstElementWithValue(linksList, listName);
    }

    public boolean noListDisplayed()
    {
        return getBrowser().isElementDisplayed(noListDisplayed);
    }

    public DataListsPage assertNewListButtonIsDisplayed()
    {
        LOG.info("Assert new list button is displayed");
        assertTrue(getBrowser().isElementDisplayed(newListButton), "New list button is not displayed");
        return this;
    }

    public DataListsPage assertSelectItemsButtonIsDisplayed()
    {
        LOG.info("Assert select items button is displayed");
        assertTrue(currentContent.isSelectItemsButtonDisplayed(), "Select items button is not displayed");
        return this;
    }

    public DataListsPage assertSelectButtonIsDisplayed()
    {
        LOG.info("Assert select button is displayed");
        assertTrue(currentContent.isSelectButtonDisplayed(), "Select button is not displayed");
        return this;
    }

    public DataListsPage assertSelectAllButtonOptionIsDisplayed()
    {
        LOG.info("Assert select all button option is displayed");
        assertTrue(currentContent.isSelectAllButtonOptionDisplayed(), "Select all button option is not displayed");
        return this;
    }

    public DataListsPage assertSelectNoneButtonOptionIsDisplayed()
    {
        LOG.info("Assert select none button option is displayed");
        assertTrue(currentContent.isSelectNoneButtonOptionDisplayed(), "Select none button option is not displayed");
        return this;
    }

    public DataListsPage assertInvertSelectionButtonOptionIsEnabled()
    {
        LOG.info("Assert invert selection button option is enabled");
        assertTrue(currentContent.isInvertSelectionButtonOptionEnabled(),
            "Invert selection button option is disabled");
        return this;
    }

    public boolean isNewItemButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(newItemButton);
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
        currentContent.setBrowser(getBrowser());
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
        getBrowser().mouseOver(getDataListElement(listName));
        return getBrowser().isElementDisplayed(editListButton);
    }

    public boolean isDeleteButtonDisplayedForList(String listName)
    {
        getBrowser().mouseOver(getDataListElement(listName));
        return getBrowser().isElementDisplayed(deleteListButton);
    }

    public EditListDetailsPopUp clickEditButtonForList(String listName)
    {
        getBrowser().mouseOver(getDataListElement(listName));
        editListButton.click();
        return (EditListDetailsPopUp) editListDetailsPopUp.renderedPage();
    }


    public DeleteListPopUp clickDeleteButtonForList(String listName)
    {
        getBrowser().mouseOver(getDataListElement(listName));
        deleteListButton.click();
        return (DeleteListPopUp) deleteListPopUp.renderedPage();
    }

    public CreateDataListDialog clickOnNewListButton()
    {
        newListButton.click();
        return (CreateDataListDialog) createDataListDialog.renderedPage();
    }

    public String successfullyCreatedDataListMessage()
    {
        return successfullyCreatedMessage.getText();
    }

    public boolean isEditButtonDisabled(String listName)
    {
        getBrowser().mouseOver(getDataListElement(listName));
        return getBrowser().isElementDisplayed(editButtonDisabled);
    }

    public void clickOnDisabledEditButton(String listName)
    {
        getBrowser().mouseOver(getDataListElement(listName));
        editButtonDisabled.click();
    }

    public DataListsPage assertDataListItemTitleEquals(String itemTitle)
    {
        LOG.info("Assert data list item title equals: {}", itemTitle);
        WebElement actualTitle = getBrowser().findElement(By.xpath(String.format(listItemTitleLocator, itemTitle)));
        assertEquals( actualTitle.getText(), itemTitle, String.format("Data list item title not equals %s: ", itemTitle));

        return this;
    }

    public boolean isListWithCreatedListsDisplayed()
    {
        return getBrowser().isElementDisplayed(listWithCreatedLists);
    }

    public EditItemPopUp clickEditButtonForListItem()
    {
        getBrowser().mouseOver(listItemActionsField);
        getBrowser().waitUntilElementVisible(editListItemButton).click();
        return (EditItemPopUp) editItemPopUp.renderedPage();
    }

    public CreateNewItemPopUp clickNewItemButton()
    {
        getBrowser().waitUntilElementVisible(newItemButton).click();
        return (CreateNewItemPopUp) createNewItemPopUp.renderedPage();
    }

    public List<String> getTextOfTableColumnHeader()
    {
        List<String> tableHeaderListString = new ArrayList<>();
        for (WebElement item : tableColumnHeader)
        {
            tableHeaderListString.add(item.getText());
        }

        return tableHeaderListString;
    }

    public boolean isNewItemPopupFormDisplayed(CreateNewItemPopUp.NewItemPopupForm listName)
    {
        return getBrowser().isElementDisplayed(By.xpath(String.format(createNewItemForm, listName.name)));
    }
}
