package org.alfresco.po.share.site.calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Claudia Agache on 7/19/2016.
 */
@PageObject
public class CalendarPicker extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "#calendarcontainer[style*='display: block'] .calheader")
    private WebElement calendarPickerHeader;

    @FindBy (css = "#calendarcontainer[style*='display: block'] .calnavright")
    private WebElement nextMonthArrow;

    @FindBy (css = "#calendarcontainer[style*='display: block'] .calnavleft")
    private WebElement previousMonthArrow;

    @FindAll (@FindBy (css = "#calendarcontainer[style*='display: block'] a.selector"))
    private List<WebElement> dates;

    private ArrayList<String> monthValues = new ArrayList<>(
        Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));

    private String getYearHeader()
    {
        String header = calendarPickerHeader.getText();
        return header.split("\n")[1].split(" ")[1];
    }

    private String getMonthHeader()
    {
        String header = calendarPickerHeader.getText();
        return header.split("\n")[1].split(" ")[0];
    }

    private boolean isCurrentYear(int year)
    {
        return Integer.parseInt(getYearHeader()) == year;
    }

    private boolean isCurrentMonth(int month)
    {
        return monthValues.indexOf(getMonthHeader()) + 1 == month;
    }

    private void navigateToYear(int year)
    {
        while (!isCurrentYear(year))
        {
            if (Integer.parseInt(getYearHeader()) < year)
                nextMonthArrow.click();
            else
                previousMonthArrow.click();
        }
    }

    private void navigateToMonth(int month)
    {
        while (!isCurrentMonth(month))
        {
            int count = month - (monthValues.indexOf(getMonthHeader()) + 1);
            if (count > 0)
                nextMonthArrow.click();
            else
                previousMonthArrow.click();
        }

    }

    public void selectDate(int day, int month, int year)
    {
        navigateToYear(year);
        navigateToMonth(month);
        browser.findFirstElementWithValue(dates, String.valueOf(day)).click();
        browser.waitUntilElementDisappears(By.id("calendarcontainer"), 15);
    }
}
