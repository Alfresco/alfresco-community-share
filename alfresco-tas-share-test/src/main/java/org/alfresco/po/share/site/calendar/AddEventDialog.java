package org.alfresco.po.share.site.calendar;

import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Created by Claudia Agache on 7/11/2016.
 */
@Primary
@PageObject
public class AddEventDialog extends ShareDialog
{
    @FindBy (id = "eventEditPanel-dialog_h")
    protected WebElement dialogHeader;
    CalendarPage calendarPage;
    @Autowired
    CalendarPicker calendarPicker;

    @FindBy (id = "eventEditPanel-ok-button")
    private WebElement saveButton;

    @FindBy (css = "label[for='eventEditPanel-title']")
    private WebElement eventTitleLabel;

    @RenderWebElement
    @FindBy (id = "eventEditPanel-title")
    private TextInput eventTitle;

    @FindBy (css = "label[for='eventEditPanel-location']")
    private WebElement eventLocationLabel;

    @FindBy (id = "eventEditPanel-location")
    private TextInput eventLocation;

    @FindBy (css = "label[for='eventEditPanel-description']")
    private WebElement eventDescriptionLabel;

    @FindBy (id = "eventEditPanel-description")
    private WebElement eventDescription;

    @FindBy (css = "label[for='eventEditPanel-allday']")
    private WebElement eventAllDayLabel;

    @FindBy (id = "eventEditPanel-allday")
    private WebElement allday;

    @FindBy (css = "label[for='fd']")
    private WebElement eventStartDateLabel;

    @FindBy (id = "fd")
    private WebElement eventStartDate;

    @FindBy (css = "label[for='td']")
    private WebElement eventEndDateLabel;

    @FindBy (id = "td")
    private WebElement eventEndDate;

    @FindBy (id = "eventEditPanel-start")
    private WebElement eventStartTime;

    @FindBy (id = "eventEditPanel-end")
    private WebElement eventEndTime;

    @FindBy (id = "eventEditPanel-tag-input-field")
    private WebElement eventTag;

    @FindBy (id = "eventEditPanel-add-tag-button-button")
    private WebElement addTagButton;

    @FindBy (id = "eventEditPanel-load-popular-tags-link")
    private WebElement popularTagsLink;

    @FindBy (id = "eventEditPanel-cancel-button")
    private WebElement cancelButton;

    @FindAll (@FindBy (css = "#eventEditPanel-current-tags .taglibrary-action>span"))
    private List<WebElement> currentTagList;

    @FindAll (@FindBy (css = "#eventEditPanel-popular-tags .taglibrary-action>span"))
    private List<WebElement> popularTagList;

    @FindBy (css = ".balloon>.text>div")
    private WebElement balloon;

    @FindBy (id = "calendarendpicker-button")
    private WebElement endCalendarPickerButton;

    @FindBy (id = "calendarpicker-button")
    private WebElement startCalendarPickerButton;

    private By parentSection = By.xpath("../../preceding-sibling::*[@class = 'yui-g'][1]/*");

    /**
     * Check if Add Event dialog is displayed or not
     *
     * @return true if it is displayed
     */
    public Boolean isDialogDisplayed()
    {
        return browser.isElementDisplayed(dialogHeader) && dialogHeader.getText().equals("Add Event");
    }

    /**
     * Get "What:" input text
     *
     * @return "What:" value
     */
    public String getEventTitle()
    {
        return eventTitle.getText();
    }

    /**
     * Type title in 'What:' input
     *
     * @param title
     */
    public void typeInEventTitleInput(String title)
    {
        Parameter.checkIsMandotary("Event title", title);
        clearAndType(eventTitle, title);
    }

    /**
     * Check if "What:" field is displayed and mandatory
     *
     * @return true if event title is displayed and mandatory
     */
    public boolean isEventTitleDisplayedAndMandatory()
    {
        return eventTitle.getWrappedElement().getAttribute("class").contains("mandatory") && eventTitleLabel.getText().equals("What:");
    }

    /**
     * Get "Where:" input text
     *
     * @return "Where:" value
     */
    public String getEventLocation()
    {
        return eventLocation.getText();
    }

    /**
     * Type location in 'Where:' input
     *
     * @param location
     */
    public void typeInEventLocationInput(String location)
    {
        clearAndType(eventLocation, location);
    }

    /**
     * Check if "Where:" label and field are displayed
     *
     * @return true if event location is displayed
     */
    public boolean isEventLocationDisplayed()
    {
        return eventLocationLabel.getText().equals("Where:") && eventLocation.isDisplayed();
    }

    /**
     * Get "Description:" input text
     *
     * @return "Description:" value
     */
    public String getEventDescription()
    {
        return eventDescription.getAttribute("value");
    }

    /**
     * Type description in 'Description:' input
     *
     * @param description
     */
    public void typeInEventDescriptionInput(String description)
    {
        clearAndType(eventDescription, description);
    }

    /**
     * Check if "Description:" label and field are displayed
     *
     * @return true if event description is displayed
     */
    public boolean isEventDescriptionDisplayed()
    {
        return eventDescriptionLabel.getText().equals("Description:") && browser.isElementDisplayed(eventDescription);
    }

    /**
     * Add tag in event's tags list
     *
     * @param tag
     */
    public void addTag(String tag)
    {
        eventTag.clear();
        eventTag.sendKeys(tag);
        addTagButton.click();
        this.renderedPage();
    }

    /**
     * Remove tag from event's tags list
     *
     * @param tag
     */
    public void removeTag(String tag)
    {
        browser.findFirstElementWithValue(currentTagList, tag).click();
    }

    /**
     * Check if the specified tag is displayed in current tags list
     *
     * @param tag
     * @return true if it is displayed
     */
    public boolean isTagDisplayedInList(String tag)
    {
        return browser.findFirstElementWithValue(currentTagList, tag) != null;
    }

    /**
     * Add popular tag in event's tags list by clicking on add button for the specified tag
     *
     * @param tag
     */
    public void addPopularTagByClickingAddButton(String tag)
    {
        WebElement popularTag = browser.findFirstElementWithValue(popularTagList, tag);
        browser.mouseOver(popularTag);
        popularTag.findElement(By.xpath("following-sibling::*[1][@class = 'add']")).click();
    }

    /**
     * Add popular tag in event's tags list by clicking on the specified tag
     *
     * @param tag
     */
    public void addPopularTagByClickingTag(String tag)
    {
        browser.findFirstElementWithValue(popularTagList, tag).click();
    }

    /**
     * Check if the specified tag is displayed in popular tags list
     *
     * @param tag
     * @return true if it is displayed
     */
    public boolean isTagDisplayedInPopularList(String tag)
    {
        return browser.findFirstElementWithValue(popularTagList, tag) != null;
    }

    /**
     * Click on Save button
     *
     * @return CalendarPage
     */
    public void clickSaveButton()
    {
        getBrowser().waitUntilElementVisible(saveButton);
        getBrowser().waitUntilElementClickable(saveButton).click();
        browser.refresh();
    }

    public void clickSave()
    {
        browser.waitInSeconds(1);
        saveButton.click();
    }

    /**
     * Click on Cancel button
     *
     * @return CalendarPage
     */
    public void clickCancelButton()
    {
        cancelButton.click();
    }

    /**
     * Check if "All Day:" checkbox is displayed and unchecked
     *
     * @return true if all day checkbox is displayed and unchecked
     */
    public boolean isAllDayCheckBoxDisplayedAndUnchecked()
    {
        return eventAllDayLabel.getText().equals("All Day:") && !allday.isSelected();
    }

    /**
     * Check if "Start Date:" field is displayed and has the specified date value
     *
     * @param date
     * @return true if "Start Date:" field is displayed and has the specified date value
     */
    public boolean hasStartDateValue(DateTime date)
    {
        return eventStartDateLabel.getText().equals("Start Date:") &&
            eventStartDate.getAttribute("value").equals(date.toString("EEEE, d MMMM, yyyy"));
    }

    /**
     * Check if "Start Date:" field is displayed and has the specified date value
     *
     * @param date
     * @return true if "Start Date:" field is displayed and has the specified date value
     */
    public boolean hasEndDateValue(DateTime date)
    {
        return eventEndDateLabel.getText().equals("End Date:") &&
            eventEndDate.getAttribute("value").equals(date.toString("EEEE, d MMMM, yyyy"));
    }

    /**
     * Check if start time field has the specified value
     *
     * @param time
     * @return true if start time field has the specified value
     */
    public boolean hasStartTimeValue(String time)
    {
        return eventStartTime.getAttribute("value").equals(time);
    }

    /**
     * Check if end time field has the specified value
     *
     * @param time
     * @return true if end time field has the specified value
     */
    public boolean hasEndTimeValue(String time)
    {
        return eventEndTime.getAttribute("value").equals(time);
    }

    /**
     * Check if Event Details section contains: What, Where and Description fields
     *
     * @return true if contains
     */
    public boolean checkEventDetailsSectionContainsFields()
    {
        return isEventTitleDisplayedAndMandatory() &&
            eventTitleLabel.findElement(parentSection).getText().equals("Event Details") &&
            isEventLocationDisplayed() &&
            eventLocationLabel.findElement(parentSection).getText().equals("Event Details") &&
            isEventDescriptionDisplayed() &&
            eventDescriptionLabel.findElement(parentSection).getText().equals("Event Details");
    }

    /**
     * Check if Time section contains:
     * "All Day" check box unchecked by default
     * "Start Date" field  with selected date displayed by default
     * "End Date" field  with selected date displayed by default
     * Start time field has 12:00 PM default value
     * End time field has 1:00 PM default value
     *
     * @param date, the selected date
     * @return true if all specified above elements are displayed
     */
    public boolean checkTimeSectionDefaultValues(DateTime date)
    {
        return isAllDayCheckBoxDisplayedAndUnchecked() &&
            eventAllDayLabel.findElement(parentSection).getText().equals("Time") &&
            hasStartDateValue(date) &&
            eventStartDateLabel.findElement(parentSection).getText().equals("Time") &&
            hasEndDateValue(date) &&
            eventEndDateLabel.findElement(parentSection).getText().equals("Time") &&
            hasStartTimeValue("12:00 PM") &&
            hasEndTimeValue("1:00 PM");
    }

    /**
     * Check if Tags section contains:
     * "Tags" field
     * "Add" button
     * "Choose from popular tags in this site" link
     *
     * @return true if all specified above elements are displayed
     */
    public boolean isTagsSectionDisplayed()
    {
        return popularTagsLink.getText().equals("Choose from popular tags in this site") &&
            eventTag.isDisplayed() &&
            addTagButton.isDisplayed();
    }

    /**
     * Check if "Save" button is available on the form
     *
     * @return true if "Save" button is enabled
     */
    public boolean isSaveButtonEnabled()
    {
        return saveButton.isEnabled();
    }

    /**
     * Check if "Cancel" button is available on the form
     *
     * @return true if "Cancel" button is enabled
     */
    public boolean isCancelButtonEnabled()
    {
        return cancelButton.isEnabled();
    }

    /**
     * Check if "What:" field is invalid or not
     *
     * @return true if it is invalid
     */
    public boolean isEventTitleInvalid()
    {
        return eventTitle.getWrappedElement().getAttribute("Class").contains("invalid");
    }

    /**
     * Get balloon error message
     *
     * @return
     */
    public String getBalloonMessage()
    {
        return balloon.getText();
    }

    /**
     * Check All Day checkbox
     */
    public AddEventDialog checkAllDayCheckBox()
    {
        allday.click();
        browser.waitInSeconds(3);
//        browser.waitUntilElementDisappears(By.id("eventEditPanel-start"), 15);
//        browser.waitUntilElementDisappears(By.id("eventEditPanel-end"), 15);
        return (AddEventDialog) this.renderedPage();
    }

    /**
     * Check if Start time field is displayed or not
     *
     * @return true if it is displayed
     */
    public boolean isStartTimeDisplayed()
    {
        return eventStartTime.isDisplayed();
    }

    /**
     * Check if End time field is displayed or not
     *
     * @return true if it is displayed
     */
    public boolean isEndTimeDisplayed()
    {
        return eventEndTime.isDisplayed();
    }

    /**
     * Select End Date from Calendar Picker
     */
    public void selectEndDateFromCalendarPicker(int day, int month, int year)
    {
        endCalendarPickerButton.click();
        browser.waitInSeconds(1);
        calendarPicker.renderedPage();
        calendarPicker.selectDate(day, month, year);
    }

    /**
     * Select Start Date from Calendar Picker
     */
    public void selectStartDateFromCalendarPicker(int day, int month, int year)
    {
        startCalendarPickerButton.click();
        calendarPicker.renderedPage();
        calendarPicker.selectDate(day, month, year);
    }

    public void choosePopularTagsInSite()
    {
        popularTagsLink.click();
    }

    /**
     * Type the specified time in the event start time input
     *
     * @param time
     */
    public void typeInStartTimeInput(String time)
    {
        eventStartTime.clear();
        eventStartTime.sendKeys(time);
    }

    /**
     * Type the specified time in the event end time input
     *
     * @param time
     */
    public void typeInEndTimeInput(String time)
    {
        eventEndTime.clear();
        eventEndTime.sendKeys(time);
    }
}
