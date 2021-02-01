package org.alfresco.po.share.site.dataLists;

import static org.alfresco.common.DataUtil.isEnumContainedByList;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CreateDataListDialog extends BaseDialogComponent
{
    private final By balloon = By.cssSelector("div[style*='visible'] div[class='balloon'] div[class='text']");
    private final By typesOfList = By.cssSelector("div[id$='itemTypesContainer'] a");
    private final By titleField = By.cssSelector("input[id$='newList_prop_cm_title']");
    private final By descriptionField = By.cssSelector("textarea[id$='newList_prop_cm_description']");
    private final By saveButton = By.cssSelector("button[id$='form-submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    private final By newListPopup = By.cssSelector("div[id$='newList-dialog']");
    private final By titleLabel = By.cssSelector("label[for*='default-newList'][for$='title']");
    private final By titleMandatoryIndicator = By.cssSelector("label[for*='default-newList'][for$='title'] span[class='mandatory-indicator']");
    private final By descriptionLabel = By.cssSelector("label[for*='default-newList'][for$='description']");
    private final By typeSelected = By.cssSelector("div[class='theme-bg-selected'] a");

    private String typeOfListDescription = "//div[contains(@id, 'itemTypesContainer')]//a[text()='Contact List']//ancestor::*//div[contains(@id, 'itemTypesContainer')]//span";

    public CreateDataListDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isDataListComplete()
    {
        return isEnumContainedByList(DataListTypes.class, getValuesFromElements());
    }

    private List<String> getValuesFromElements()
    {
        List<String> typesOfListString = new ArrayList<>();

        for (WebElement item : webElementInteraction.waitUntilElementsAreVisible(typesOfList))
        {
            typesOfListString.add(item.getText());
        }

        return typesOfListString;
    }

    public boolean isNewListPopupDisplayed()
    {
        return webElementInteraction.isElementDisplayed(newListPopup);
    }

    public String getTypeOfListDescription(DataListTypes listType)
    {
        return webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(typeOfListDescription,
                listType.toString()))).getText();
    }

    public String getTitleLabelText()
    {
        return webElementInteraction.waitUntilElementIsVisible(titleLabel).getText();
    }

    public boolean isTitleMandatoryIndicatorDisplayed()
    {
        return webElementInteraction.isElementDisplayed(titleMandatoryIndicator);
    }

    public boolean isTitleFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(titleField);
    }

    public String getDescriptionLabelText()
    {
        return webElementInteraction.waitUntilElementIsVisible(descriptionLabel).getText();
    }

    public boolean isDescriptionFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(descriptionField);
    }

    public CreateDataListDialog selectType(String type)
    {
        webElementInteraction.findFirstElementWithValue(typesOfList, type).click();
        return this;
    }

    public boolean isExpectedTypeSelected(String expectedType)
    {
        return webElementInteraction.findElement(typeSelected).getText().equals(expectedType);
    }

    public CreateDataListDialog typeTitle(String title)
    {
        LOG.info("Clear and type title: {}", title);
        webElementInteraction.clearAndType(titleField, title);
        return this;
    }

    public String getTitleValue()
    {
        return webElementInteraction.findElement(titleField).getAttribute("value");
    }

    public CreateDataListDialog typeDescription(String description)
    {
        LOG.info("Clear and type description: {}", description);
        webElementInteraction.clearAndType(descriptionField, description);
        return this;
    }

    public String getDescriptionValue()
    {
        return webElementInteraction.waitUntilElementIsVisible(titleField).getAttribute("value");
    }

    public String getInvalidDataListBalloonMessage()
    {
        return webElementInteraction.getElementText(balloon);
    }

    public String invalidTitleBalloonMessage()
    {
        return webElementInteraction.waitUntilElementIsVisible(titleField).getAttribute("title");
    }

    public boolean isSaveButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(cancelButton);
    }

    public void clickSaveButton()
    {
        LOG.info("Click Save button");
        webElementInteraction.clickElement(saveButton);
        waitUntilNotificationMessageDisappears();
    }

    public DataListsPage clickCancelButton()
    {
        LOG.info("Click Cancel button");
        webElementInteraction.clickElement(cancelButton);

        return new DataListsPage(webDriver);
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