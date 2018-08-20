package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class RssFeedDashlet extends Dashlet<RssFeedDashlet>
{
    @Autowired
    EnterFeedURLPopUp enterFeedURLPopUp;

    @RenderWebElement
    @FindBy(css = "div.dashlet.rssfeed")
    private WebElement dashletContainer;

    @RenderWebElement
    @FindAll(@FindBy(css = "div.headline h4 a"))
    private List<WebElement> feedsList;

    private By configureDashlet = By.cssSelector("div.dashlet.rssfeed [class$='edit']");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Method to verify that configure this dashlet icon is displayed
     */
    public boolean isConfigureDashletIconDisplayed()
    {
        browser.mouseOver(browser.findElement(configureDashlet));
        return browser.waitUntilElementVisible(configureDashlet).isDisplayed();
    }

    /**
     * Method to get feeds list size
     * 
     * @return
     */
    public int getFeedsListSize()
    {
        return feedsList.size();
    }

    /**
     * This method is used to click on configure this dashlet icon
     */
    public EnterFeedURLPopUp clickOnConfigureRssFeedDashlet()
    {
        getBrowser().waitInSeconds(5);
        browser.mouseOver(browser.findElement(configureDashlet));
        browser.findElement(configureDashlet).click();
        return (EnterFeedURLPopUp) enterFeedURLPopUp.renderedPage();
    }

    /**
     * Method to click on the link from position in the feeds list
     *
     * @param position : position of the link to be clicked
     */
    public void clickOnRssLink(int position)
    {
        feedsList.get(position).click();
        getBrowser().waitInSeconds(5);
        browser.switchWindow();
    }
}
