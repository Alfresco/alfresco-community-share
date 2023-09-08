package org.alfresco.po.share.site.calendar;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.common.Parameter;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class AddEventDialogPage extends BaseDialogComponent {

    CalendarPage calendarPage;
    public AddEventDialogPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }
    private final By dialogHeader = By.id("eventEditPanel-dialog_h");
    private final By eventTitle = By.id("eventEditPanel-title");
    private final By allday = By.id("eventEditPanel-allday");
    private final By saveButton = By.id("eventEditPanel-ok-button");
    private final By endCalendarPickerButton = By.id("calendarendpicker-button");
    private final By startCalendarPickerButton = By.id("calendarpicker-button");
    private final By eventStartTime = By.id("eventEditPanel-start");
    private final By eventEndTime = By.id("eventEditPanel-end");

    private final By eventTitles = By.id("eventEditPanel-title");

    private final By eventTitleLabel = By.cssSelector("label[for='eventEditPanel-title']");

    private final By eventLocationLabel = By.cssSelector("label[for='eventEditPanel-location']");

    private final By eventLocation = By.id("eventEditPanel-location");

    private final By eventDescriptionLabel = By.cssSelector("label[for='eventEditPanel-description']");

    private final By eventDescription = By.id("eventEditPanel-description");

    private final By eventAllDayLabel = By.cssSelector("label[for='eventEditPanel-allday']");

    private final By eventStartDateLabel = By.cssSelector("label[for='fd']");

    private final By clickOnTheCalendarDayView = By.cssSelector(".fc-view-agendaDay .fc-agenda-slots .fc-widget-content");

    private final By clickOnTheCalendarWeekView = By.cssSelector(".fc-view-agendaWeek .fc-agenda-slots .fc-widget-content");

    private final By eventStartDate = By.id("fd");

    private final By eventEndDateLabel = By.cssSelector("label[for='td']");

    private final By eventEndDate = By.id("td");


    private final By eventTag = By.id("eventEditPanel-tag-input-field");

    private final By addTagButton = By.id("eventEditPanel-add-tag-button-button");

    private final By popularTagsLink = By.id("eventEditPanel-load-popular-tags-link");

    private final By cancelButton = By.id("eventEditPanel-cancel-button");

    private final By currentTagList = By.cssSelector("#eventEditPanel-current-tags .taglibrary-action>span");

    private final By popularTagList = By.cssSelector("#eventEditPanel-popular-tags .taglibrary-action>span");

    private final By balloon = By.cssSelector(".balloon>.text>div");

    private By parentSection = By.xpath("../../preceding-sibling::*[@class = 'yui-g'][1]/*");

    private final By agendaAddEvent = By.cssSelector("a.addEvent");

    public Boolean isDialogDisplayed()
    {
        waitInSeconds(2);
        return isElementDisplayed(dialogHeader) && findElement(dialogHeader).getText().equals("Add Event");
    }

    public void typeInEventTitleInput(String title)
    {
        clearAndType(eventTitle, title);
    }

    public void checkAllDayCheckBox()
    {
        findElement(allday).click();
        waitInSeconds(2);
    }

    public void clickSaveButton()
    {
        waitUntilElementIsVisible(saveButton);
        clickElement(saveButton);
    }

    public void selectEndDateFromCalendarPicker(int day, int month, int year)
    {
        clickElement(endCalendarPickerButton);
        waitInSeconds(1);

        calendarPage = new CalendarPage(webDriver);
        calendarPage.navigateToYear(year);
        calendarPage.navigateToMonth(month);
        calendarPage.selectDate(day);
    }

    public void selectStartDateFromCalendarPicker(int day, int month, int year)
    {
        clickElement(startCalendarPickerButton);
        waitInSeconds(1);

        calendarPage = new CalendarPage(webDriver);
        calendarPage.navigateToYear(year);
        calendarPage.navigateToMonth(month);
        calendarPage.selectDate(day);
    }

    public void typeInStartTimeInput(String time)
    {
        clearAndType(eventStartTime, time);
    }

    public void typeInEndTimeInput(String time)
    {
        clearAndType(eventEndTime, time);
    }

    public boolean isEventTitleDisplayedAndMandatory()
    {
        return findElement(eventTitles).getAttribute("class").contains("mandatory") && findElement(eventTitleLabel).getText().equals("What:");
    }

    public boolean isEventLocationDisplayed()
    {
        return findElement(eventLocationLabel).getText().equals("Where:") && findElement(eventLocation).isDisplayed();
    }

    public boolean isEventDescriptionDisplayed()
    {
        return findElement(eventDescriptionLabel).getText().equals("Description:") && isElementDisplayed(eventDescription);
    }

    public boolean checkEventDetailsSectionContainsFields()
    {
        return isEventTitleDisplayedAndMandatory() &&
            findElement(eventTitleLabel).findElement(parentSection).getText().equals("Event Details") &&
            isEventLocationDisplayed() &&
            findElement(eventLocationLabel).findElement(parentSection).getText().equals("Event Details") &&
            isEventDescriptionDisplayed() &&
            findElement(eventDescriptionLabel).findElement(parentSection).getText().equals("Event Details");
    }

    public boolean isAllDayCheckBoxDisplayedAndUnchecked()
    {
        return findElement(eventAllDayLabel).getText().equals("All Day:") && !findElement(allday).isSelected();
    }

    public boolean hasStartDateValue(DateTime date)
    {
        return findElement(eventStartDateLabel).getText().equals("Start Date:") &&
            findElement(eventStartDate).getAttribute("value").equals(date.toString("EEEE, d MMMM, yyyy"));
    }

    public boolean hasEndDateValue(DateTime date)
    {
        return findElement(eventEndDateLabel).getText().equals("End Date:") &&
            findElement(eventEndDate).getAttribute("value").equals(date.toString("EEEE, d MMMM, yyyy"));
    }

    public boolean hasStartTimeValue(String time)
    {
        return findElement(eventStartTime).getAttribute("value").equals(time);
    }

    public boolean hasEndTimeValue(String time)
    {
        return findElement(eventEndTime).getAttribute("value").equals(time);
    }

    public boolean checkTimeSectionDefaultValues(DateTime date)
    {
        return isAllDayCheckBoxDisplayedAndUnchecked() &&
            findElement(eventAllDayLabel).findElement(parentSection).getText().equals("Time") &&
            hasStartDateValue(date) &&
            findElement(eventStartDateLabel).findElement(parentSection).getText().equals("Time") &&
            hasEndDateValue(date) &&
            findElement(eventEndDateLabel).findElement(parentSection).getText().equals("Time") &&
            hasStartTimeValue("12:00 PM") &&
            hasEndTimeValue("1:00 PM");
    }

    public boolean isTagsSectionDisplayed()
    {
        return findElement(popularTagsLink).getText().equals("Choose from popular tags in this site") &&
            findElement(eventTag).isDisplayed() &&
            findElement(addTagButton).isDisplayed();
    }

    public boolean isSaveButtonEnabled()
    {
        return findElement(saveButton).isEnabled();
    }

    public boolean isCancelButtonEnabled()
    {
        return findElement(cancelButton).isEnabled();
    }

    public void typeInEventLocationInput(String location)
    {
        clearAndType(eventLocation, location);
    }

    public void typeInEventDescriptionInput(String description)
    {
        clearAndType(eventDescription, description);
    }

    public void clickSave()
    {
        waitInSeconds(1);
        scrollToElement(findElement(saveButton));
        findElement(saveButton).click();
    }

    public void clickOnTheCalendarDayView()
    {
        log.info("Click the calendar day view");
        findElement(clickOnTheCalendarDayView).click();
    }

    public void clickOnTheCalendarWeekView()
    {
        log.info("Click the calendar day view");
        findElement(clickOnTheCalendarWeekView).click();
    }

    public void clickAddEventToThisCalendar()
    {
        log.info("Click add event to this calendar");
        findElement(agendaAddEvent).click();
    }

    public String getBalloonMessage()
    {
        return findElement(balloon).getText();
    }

    public boolean isEventTitleInvalid()
    {
        return findElement(eventTitles).getAttribute("Class").contains("invalid");
    }

    public boolean isStartTimeDisplayed()
    {
        return findElement(eventStartTime).isDisplayed();
    }

    public boolean isEndTimeDisplayed()
    {
        return findElement(eventEndTime).isDisplayed();
    }

    public void addTag(String tag)
    {
        findElement(eventTag).clear();
        findElement(eventTag).sendKeys(tag);
        findElement(addTagButton).click();
    }

    public boolean isTagDisplayedInLists(String tag)
    {
        for (WebElement tagList : findElements(currentTagList))
        {
            if (tagList.getText().contains(tag))
            {
                waitInSeconds(3);
                return true;
            }
        }
        return false;
    }

    public void choosePopularTagsInSite()
    {
        findElement(popularTagsLink).click();
    }

    public boolean isTagDisplayedInPopularLists(String tag)
    {
        waitInSeconds(6);
        for (WebElement addTag : findElements(popularTagList))
        {
            if (addTag.getText().contains(tag))
            {
                return true;
            }
        }
        return false;
    }

    public void addPopularTagByClickingAddButton(String tag)
    {
        findElement(eventTag).clear();
        findElement(eventTag).sendKeys(tag);
        findElement(addTagButton).click();
    }

    public String getEventTitle()
    {
        waitInSeconds(7);
        return findElement(eventTitle).getAttribute("value");
    }

    public void clickCancelButton()
    {
        findElement(cancelButton).click();
    }
}
