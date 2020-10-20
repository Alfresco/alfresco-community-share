package org.alfresco.po.share.site.dataLists;

import static org.alfresco.common.DataUtil.isEnumContainedByList;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class CreateDataListDialog extends ShareDialog
{
    protected static By balloon = By.cssSelector("div[style*='visible'] div[class='balloon'] div[class='text']");
    protected static By balloonCloseButton = By.cssSelector("div[style*='visible'] div[class='balloon'] div[class='closeButton']");

    @FindBy (css = "div[id$='itemTypesContainer'] a")
    protected List<WebElement> typesOfList;

    @FindBy (css = "input[id$='newList_prop_cm_title']")
    protected WebElement titleField;

    @FindBy (css = "textarea[id$='newList_prop_cm_description']")
    protected WebElement descriptionField;

    @RenderWebElement
    @FindBy (css = "button[id$='form-submit-button']")
    protected WebElement saveButton;

    @RenderWebElement
    @FindBy (css = "button[id$='form-cancel-button']")
    protected WebElement cancelButton;

    @FindBy (css = "div[id$='newList-dialog']")
    private WebElement newListPopup;

    @FindBy (css = "label[for*='default-newList'][for$='title']")
    private WebElement titleLabel;

    @FindBy (css = "label[for*='default-newList'][for$='title'] span[class='mandatory-indicator']")
    private WebElement titleMandatoryIndicator;

    @FindBy (css = "label[for*='default-newList'][for$='description']")
    private WebElement descriptionLabel;

    protected By typeSelected = By.cssSelector("div[class='theme-bg-selected'] a");
    private String typeOfListDescription = "//div[contains(@id, 'itemTypesContainer')]//a[text()='Contact List']//ancestor::*//div[contains(@id, 'itemTypesContainer')]//span";

    @Autowired
    private DataListsPage dataListsPage;
    /**
     * This method is checking if all the elements that should be in 'New List' popup are actually displayed.
     * Checking if all the elements from the enum list exists in getValuesFromElements() list method.
     *
     * @return - true if all the elements from the enum are displayed in 'New List' popup.
     * - false if there is at least one element missing.
     */
    public boolean isDataListComplete()
    {
        return isEnumContainedByList(DataListTypes.class, getValuesFromElements());
    }

    /**
     * Get a list of 'Data List' type names from New List popup.
     *
     * @return list of strings.
     */
    private List<String> getValuesFromElements()
    {
        List<String> typesOfListString = new ArrayList<>();

        for (WebElement item : typesOfList)
        {
            typesOfListString.add(item.getText());
        }

        return typesOfListString;
    }

    /**
     * Verify presence of "New List" popup.
     *
     * @return true if displayed or false if is not.
     */
    public boolean isNewListPopupDisplayed()
    {
        return browser.isElementDisplayed(newListPopup);
    }

    /**
     * Get the description for a specific list type.
     *
     * @param listType - the data list type from DataListTypes enum, which description is wanted. (eg. getTypeOfListDescription(DataListTypes.ContactList) => to get 'Contact List' description)
     * @return String list type description.
     */
    public String getTypeOfListDescription(DataListTypes listType)
    {
        return browser.waitUntilElementVisible(By.xpath(String.format(typeOfListDescription, listType.toString()))).getText();
    }

    /**
     * Get the text of 'Title' label.
     *
     * @return String 'Title' label.
     */
    public String getTitleLabelText()
    {
        return browser.waitUntilElementVisible(titleLabel).getText();
    }

    /**
     * Verify presence of 'Title' Mandatory Indicator "*".
     *
     * @return true if displayed or false if is not.
     */
    public boolean isTitleMandatoryIndicatorDisplayed()
    {
        return browser.isElementDisplayed(titleMandatoryIndicator);
    }

    /**
     * Verify presence of "Title" Input Field.
     *
     * @return true if displayed or false if is not.
     */
    public boolean isTitleFieldDisplayed()
    {
        return browser.isElementDisplayed(titleField);
    }

    /**
     * Get the text of 'Description' label.
     *
     * @return String 'Description' label.
     */
    public String getDescriptionLabelText()
    {
        return browser.waitUntilElementVisible(descriptionLabel).getText();
    }

    /**
     * Verify presence of "Description" input field.
     *
     * @return true if displayed or false if is not.
     */
    public boolean isDescriptionFieldDisplayed()
    {
        return browser.isElementDisplayed(descriptionField);
    }

    public CreateDataListDialog selectType(String type)
    {
        browser.findFirstElementWithValue(typesOfList, type).click();
        return this;
    }

    public boolean isExpectedTypeSelected(String expectedType)
    {
        return browser.findElement(typeSelected).getText().equals(expectedType);
    }

    public CreateDataListDialog typeTitle(String title)
    {
        LOG.info("Clear and type title: {}", title);
        titleField.clear();
        titleField.sendKeys(title);
        return this;
    }

    /**
     * Get the text from the 'Title' Field.
     *
     * @return String 'Title' field text.
     */
    public String getTitleValue()
    {
        return titleField.getAttribute("value");
    }

    public CreateDataListDialog typeDescription(String description)
    {
        LOG.info("Clear and type description: {}", description);
        descriptionField.clear();
        descriptionField.sendKeys(description);
        return this;
    }

    /**
     * Get the text from the 'Description' TextArea.
     *
     * @return String 'Description' input text.
     */
    public String getDescriptionValue()
    {
        return titleField.getAttribute("value");
    }

    public String getInvalidDataListBalloonMessage()
    {
        return browser.findElement(balloon).getText();
    }

    public String invalidTitleBalloonMessage()
    {
        return titleField.getAttribute("title");
    }

    /**
     * Verify presence of "Save" button.
     *
     * @return true if displayed.
     */
    public boolean isSaveButtonDisplayed()
    {
        return browser.isElementDisplayed(saveButton);
    }

    /**
     * Verify presence of "Cancel" button.
     *
     * @return true if displayed or false if is not.
     */
    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public DataListsPage clickSaveButton()
    {
        LOG.info("Click \"Save\" button");
        saveButton.click();
        waitUntilMessageDisappears();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public DataListsPage clickCancelButton()
    {
        LOG.info("Click \"Cancel\" button");
        cancelButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public enum DataListTypes
    {
        ContactList("Contact List", "Contacts list including first name, last name, full name, email, job title, phone (office), phone (mobile)."),
        EventAgenda("Event Agenda", "Manage event agenda items including session names, presenters, start and end times."),
        EventList("Event List", "Events list including title, description, location, start and end date/time."),
        IssueList("Issue List", "Issues list including ID, status, priority, description, due data, comments, assign to, related issues."),
        LocationList("Location List", "Locations/Addresses list"),
        MeetingAgenda("Meeting Agenda", "Manage meeting agenda items including description, owner, allocated time."),
        TaskListAdvanced("Task List (Advanced)", "Advanced tasks list including title, description, start and end dates, priority, status, comments, assignees and attachments."),
        TaskListSimple("Task List (Simple)", "Simple tasks list including title, description, due date, priority, status, comments."),
        ToDoList("To Do List", "A simple to do list with optional assignee.");

        public final String title;
        public final String description;

        DataListTypes(String title, String description)
        {
            this.title = title;
            this.description = description;
        }

        @Override
        public String toString()
        {
            return this.title;
        }
    }
}
