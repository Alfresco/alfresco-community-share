package org.alfresco.po.share.site.calendar;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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

    public Boolean isDialogDisplayed()
    {
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
}
