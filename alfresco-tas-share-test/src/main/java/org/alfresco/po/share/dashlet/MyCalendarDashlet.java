package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.List;

/**
 * Created by Claudia Agache on 7/19/2016.
 */
@PageObject
public class MyCalendarDashlet extends Dashlet<MyCalendarDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.user-calendar")
    private HtmlElement dashletContainer;

    @FindAll (@FindBy (css = "td.yui-dt-col-event div.detail a"))
    private List<WebElement> userEventsList;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    private WebElement findEvent(String eventName)
    {
        return browser.findFirstElementWithValue(userEventsList, eventName);
    }

    /**
     * Check if the specific event is displayed in My Calendar Dashlet
     *
     * @param eventName
     * @return true if it is displayed in My Calendar Dashlet
     */
    public boolean isEventPresentInList(String eventName)
    {
        return findEvent(eventName) != null;
    }

    /**
     * Gel all the details of the specified event
     *
     * @param eventName
     * @return the String of details
     */
    public String getEventDetails(String eventName)
    {
        return findEvent(eventName).findElement(By.xpath("../following-sibling::*[1]")).getText();
    }
}
