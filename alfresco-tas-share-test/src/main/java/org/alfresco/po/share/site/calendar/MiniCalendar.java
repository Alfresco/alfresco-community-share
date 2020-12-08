package org.alfresco.po.share.site.calendar;

import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MiniCalendar extends SiteCommon<MiniCalendar>
{
    //@Autowired
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

    public MiniCalendar(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

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
        return getBrowser().findElement(currentDayInMiniCalendar).getText();
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
        return getBrowser().findElement(selectedDay).getText();
    }

    /**
     * Method to click on the randomly selected date of month in the mini calendar. works together with generateRandomDateTime()
     */
    public CalendarPage clickOnRandomDate()
    {
        WebElement miniCalendar = getBrowser().findElement(By.id("calendar_t"));
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
