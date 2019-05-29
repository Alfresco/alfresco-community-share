package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class MiniCalendar extends SiteCommon<MiniCalendar>
{
    @Autowired
    CalendarPage calendarPage;

    private By currentDayInMiniCalendar = By.xpath("//td[contains(@class, 'today')]");

    @RenderWebElement
    @FindBy (css = "a.calnav")
    private WebElement currentMonthInMiniCalendar;

    private By selectedDay = By.xpath("//td[contains(@class, 'selected')]/a");

    @FindBy (css = "a.calnavleft")
    private WebElement previousMonthButtonMiniCalendar;

    @FindBy (css = "a[id*='-thisMonth-button']")
    private WebElement thisMonthButton;

    @FindBy (css = "a.calnavright")
    private WebElement nextMonthButtonMiniCalendar;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/calendar", getCurrentSiteName());
    }

    /**
     * Method to get the current date highlighted in the Mini Calendar
     */

    public String getCurrentDayMiniCalendar()
    {
        return browser.findElement(currentDayInMiniCalendar).getText();
    }

    /**
     * Method to get the current Month displayed on the mini calendar
     *
     * @return
     */
    public String getCurrentMonthMiniCalendar()
    {
        return currentMonthInMiniCalendar.getText();
    }

    /**
     * method to click on the previous month button on the mini calendar
     */
    public MiniCalendar clickOnPreviousMonthButtonMiniCalendar()
    {
        previousMonthButtonMiniCalendar.click();
        return (MiniCalendar) this.renderedPage();
    }

    /**
     * Method to click on 'This Month' button
     */

    public MiniCalendar clickOnThisMonthButton()
    {
        thisMonthButton.click();
        return (MiniCalendar) this.renderedPage();
    }

    /**
     * Method to click on the next month button on the mini calendar
     */
    public MiniCalendar clickOnNextMonthButtonInMiniCalendar()
    {
        nextMonthButtonMiniCalendar.click();
        return (MiniCalendar) this.renderedPage();
    }

    /**
     * Method to get the value of the selected day in mini calendar
     *
     * @return
     */

    public String getSelectedDay()
    {
        return browser.findElement(selectedDay).getText();
    }

    /**
     * Method to click on the randomly selected date of month in the mini calendar. works together with generateRandomDateTime()
     */
    public CalendarPage clickOnRandomDate()
    {
        WebElement miniCalendar = browser.findElement(By.id("calendar_t"));
        List<WebElement> columns = miniCalendar.findElements(By.cssSelector("a.selector"));
        String randomDate = CalendarUtility.generateRandomDateTime(columns.size());
        for (WebElement cell : columns)
        {
            if (cell.getText().equals(randomDate))
            {
                cell.click();
                LOG.info(String.format("Clicked on day: %s in mini-calendar", randomDate));
                break;
            }
        }
        return (CalendarPage) calendarPage.renderedPage();
    }
}
