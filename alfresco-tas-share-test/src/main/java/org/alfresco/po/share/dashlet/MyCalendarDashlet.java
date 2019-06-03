package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

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

    @FindAll (@FindBy (css = "td.yui-dt-col-event div.detail a[class*='theme-link']"))
    private List<WebElement> eventSiteLocationList;

    @FindBy (css = "div[id$='_default-events'] tbody.yui-dt-message div")
    private WebElement emptyDashletText;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    private WebElement findEvent(String eventName)
    {
        return browser.findFirstElementWithValue(userEventsList, eventName);
    }

    private WebElement findSiteName(String siteName)
    {
        return browser.findFirstElementWithValue(eventSiteLocationList, siteName);
    }


    /**
     * Check if the specific event is displayed in My Calendar Dashlet
     *
     * @param eventName
     * @return true if it is displayed in My Calendar Dashlet
     */
    public boolean isEventPresentInList(String eventName)
    {
        try
        {
            getBrowser().waitUntilWebElementIsDisplayedWithRetry(findEvent(eventName));
        } catch (Exception Ex)
        {
            LOG.info(Ex.getStackTrace().toString());
        }
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

    public String getEmptyDashletText()
    {
        return getBrowser().waitUntilElementVisible(emptyDashletText).getText();
    }

    public HtmlPage clickOnEvent(String eventName, HtmlPage page)
    {
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(findEvent(eventName));
        findEvent(eventName).click();
        return page.renderedPage();
    }

    public HtmlPage clickOnSiteName(String siteName, HtmlPage page)
    {
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(findSiteName(siteName));
        findSiteName(siteName).click();
        return page.renderedPage();
    }
}

