package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

/**
 * Created by Claudia Agache on 7/13/2016.
 */
@PageObject
public class SiteCalendarDashlet extends Dashlet<SiteCalendarDashlet>
{
    @Autowired
    CalendarPage calendarPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.calendar")
    private HtmlElement dashletContainer;

    @FindAll (@FindBy (css = "div.dashlet.calendar .detail-list-item span>a"))
    private List<WebElement> siteEventsNameList;

    @FindBy (css = "div.dashlet.calendar .dashlet-padding>h3")
    private WebElement dashletMessage;

    private By eventStartDate = By.xpath("ancestor::*[@class='details2']//a");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    private WebElement findEvent(String eventName)
    {
        return browser.findFirstElementWithValue(siteEventsNameList, eventName);
    }

    /**
     * Check if the specific event is displayed in Site Calendar Dashlet
     *
     * @param eventName
     * @return true if it is displayed in Site Calendar Dashlet
     */
    public boolean isEventPresentInList(String eventName)
    {
        return findEvent(eventName) != null;
    }

    /**
     * Get event duration details
     *
     * @param eventName
     * @return
     */
    public String getEventDetails(String eventName)
    {
        return findEvent(eventName).findElement(By.xpath("..")).getText();
    }

    /**
     * Get event start date
     *
     * @param eventName
     * @return
     */
    public String getEventStartDate(String eventName)
    {
        return findEvent(eventName).findElement(eventStartDate).getText();
    }

    /**
     * Get the message when no events are displayed in Site Calendar Dashlet
     *
     * @return
     */
    public String getDashletMessage()
    {
        return dashletMessage.getText();
    }

    /**
     * Click on the event name from Site Calendar dashlet
     *
     * @param eventName
     * @return CalendarPage
     */
    public CalendarPage clickEvent(String eventName)
    {
        browser.findFirstElementWithValue(siteEventsNameList, eventName).click();
        return (CalendarPage) calendarPage.renderedPage();
    }
}
