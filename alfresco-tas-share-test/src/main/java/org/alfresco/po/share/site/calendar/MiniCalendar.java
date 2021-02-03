package org.alfresco.po.share.site.calendar;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class MiniCalendar extends SiteCommon<MiniCalendar>
{
    private CalendarPage calendarPage;

    private By currentDayInMiniCalendar = By.xpath("//td[contains(@class, 'today')]");

    @FindBy (css = "a.calnav")
    private WebElement currentMonthInMiniCalendar;

    private By selectedDay = By.xpath("//td[contains(@class, 'selected')]/a");

    @FindBy (css = "a.calnavleft")
    private WebElement previousMonthButtonMiniCalendar;

    @FindBy (css = "a[id*='-thisMonth-button']")
    private WebElement thisMonthButton;

    @FindBy (css = "a.calnavright")
    private WebElement nextMonthButtonMiniCalendar;

    public MiniCalendar(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/calendar", getCurrentSiteName());
    }

    public String getCurrentDayMiniCalendar()
    {
        return webElementInteraction.findElement(currentDayInMiniCalendar).getText();
    }

    public String getCurrentMonthMiniCalendar()
    {
        return currentMonthInMiniCalendar.getText();
    }

    public MiniCalendar clickOnPreviousMonthButtonMiniCalendar()
    {
        previousMonthButtonMiniCalendar.click();
        return this;
    }

    public MiniCalendar clickOnThisMonthButton()
    {
        thisMonthButton.click();
        return this;
    }

    public MiniCalendar clickOnNextMonthButtonInMiniCalendar()
    {
        nextMonthButtonMiniCalendar.click();
        return this;
    }

    public String getSelectedDay()
    {
        return webElementInteraction.findElement(selectedDay).getText();
    }

    public CalendarPage clickOnRandomDate()
    {
        WebElement miniCalendar = webElementInteraction.findElement(By.id("calendar_t"));
        List<WebElement> columns = miniCalendar.findElements(By.cssSelector("a.selector"));
        String randomDate = CalendarUtility.generateRandomDateTime(columns.size());
        for (WebElement cell : columns)
        {
            if (cell.getText().equals(randomDate))
            {
                cell.click();
                log.info("Clicked on day: {} in mini-calendar", randomDate);
                break;
            }
        }
        return new CalendarPage(webDriver);
    }
}
