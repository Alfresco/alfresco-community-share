package org.alfresco.po.share.site.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class DataListsPage extends SiteCommon<DataListsPage>
{

    public Content currentContent = (Content) new NoListItemSelectedContent();
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
    @Autowired
    private CreateDataListPopUp createDataListPopUp;
    @Autowired
    private EditListDetailsPopUp editListDetailsPopUp;
    @Autowired
    private DeleteListPopUp deleteListPopUp;
    @Autowired
    private EditItemPopUp editItemPopUp;
    @Autowired
    private CreateNewItemPopUp createNewItemPopUp;
    @FindBy (css = "div[class$='new-row'] span span button[id$='_default-newRowButton-button']")
    private WebElement newItemButton;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/data-lists", getCurrentSiteName());
    }


    /**
     * Checking if the created data list displayed in 'List' is highlighted (this is happening only after the created list is clicked).
     *
     * @param expectedList - name of created list
     * @return true if the list is highlighted and false if is not highlighted.
     */
    public boolean isExpectedListSelected(String expectedList)
    {
        return browser.findElement(listSelected).getText().equals(expectedList);
    }

    public void setListItemSelectedContent()
    {
        currentContent = new ListItemSelectedContent();
        currentContent.setBrowser(browser);
    }

    public List<String> getListsDisplayName()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(listWithCreatedLists);

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
        return browser.findFirstElementWithValue(linksList, listName);
    }

    public boolean noListDisplayed()
    {
        return browser.isElementDisplayed(noListDisplayed);
    }

    public boolean isNewListButtonDisplayed()
    {
        return browser.isElementDisplayed(newListButton);
    }

    /**
     * Checking if New Item button is displayed in selected Data List page.
     *
     * @return true if the button is displayed and false if the button is not displayed.
     */
    public boolean isNewItemButtonDisplayed()
    {
        return browser.isElementDisplayed(newItemButton);
    }

    private DataListsPage clickDataList(String listName, Class c)
    {
        getDataListElement(listName).click();
        browser.waitInSeconds(3);
        try
        {
            currentContent = (Content) c.getDeclaredConstructor().newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        currentContent.setBrowser(browser);
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
        browser.mouseOver(getDataListElement(listName));
        return browser.isElementDisplayed(editListButton);
    }

    public boolean isDeleteButtonDisplayedForList(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        return browser.isElementDisplayed(deleteListButton);
    }

    public EditListDetailsPopUp clickEditButtonForList(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        editListButton.click();
        return (EditListDetailsPopUp) editListDetailsPopUp.renderedPage();
    }


    public DeleteListPopUp clickDeleteButtonForList(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        deleteListButton.click();
        return (DeleteListPopUp) deleteListPopUp.renderedPage();
    }

    public CreateDataListPopUp clickOnNewListButton()
    {
        newListButton.click();
        return (CreateDataListPopUp) createDataListPopUp.renderedPage();
    }

    public String successfullyCreatedDataListMessage()
    {
        return successfullyCreatedMessage.getText();
    }

    public boolean isEditButtonDisabled(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        return browser.isElementDisplayed(editButtonDisabled);
    }

    public void clickOnDisabledEditButton(String listName)
    {
        browser.mouseOver(getDataListElement(listName));
        editButtonDisabled.click();
    }

    public boolean isDataListTitleDisplayed(String listsDisplayName)
    {
        return getListsDisplayName().contains(listsDisplayName);
    }

    public boolean isListWithCreatedListsDisplayed()
    {
        return browser.isElementDisplayed(listWithCreatedLists);
    }

    public EditItemPopUp clickEditButtonForListItem()
    {
        browser.mouseOver(listItemActionsField);
        browser.waitUntilElementVisible(editListItemButton).click();
        return (EditItemPopUp) editItemPopUp.renderedPage();
    }

    public CreateNewItemPopUp clickNewItemButton()
    {
        browser.waitUntilElementVisible(newItemButton).click();
        return (CreateNewItemPopUp) createNewItemPopUp.renderedPage();
    }

    /**
     * Get a list of 'New Item' table column header names.
     *
     * @return list of strings.
     */
    public List<String> getTextOfTableColumnHeader()
    {
        List<String> tableHeaderListString = new ArrayList<>();
        for (WebElement item : tableColumnHeader)
        {
            tableHeaderListString.add(item.getText());
        }

        return tableHeaderListString;
    }

    /**
     * Check if 'Create New Item' popup is displayed with the correct form.
     *
     * @param listName - the keyword contained by the form, used in 'action' form attribute.
     * @return true if the form that contains that specific keyword is displayed, else false.
     */
    public boolean isNewItemPopupFormDisplayed(CreateNewItemPopUp.NewItemPopupForm listName)
    {
        return browser.isElementDisplayed(By.xpath(String.format(createNewItemForm, listName.name)));
    }
}
