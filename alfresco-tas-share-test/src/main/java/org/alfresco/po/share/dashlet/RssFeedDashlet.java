package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class RssFeedDashlet extends Dashlet<RssFeedDashlet>
{
    @Autowired
    private EnterFeedURLPopUp enterFeedURLPopUp;

    @RenderWebElement
    @FindBy (css = "div.dashlet.rssfeed")
    private WebElement dashletContainer;

    @FindAll (@FindBy (css = "div.headline h4 a"))
    private List<WebElement> feedsList;

    @FindBy (css = "div[class^='dashlet rssfeed'] .titleBarActions")
    private WebElement titleBarActions;

    @FindBy (css = "div[class^='dashlet rssfeed'] div[class='titleBarActionIcon edit']")
    private WebElement configureDashletButton;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public RssFeedDashlet asserDashletTitleContains(String expectedTitle)
    {
        Assert.assertTrue(getDashletTitle().contains(expectedTitle), "Rss feed dashlet title is correct");
        return this;
    }

    public boolean isConfigureDashletIconDisplayed()
    {
        browser.mouseOver(titleBarActions);
        browser.waitUntilElementHasAttribute(titleBarActions, "style", "opacity: 1;");
        return browser.isElementDisplayed(configureDashletButton);
    }

    public int getFeedsListSize()
    {
        return feedsList.size();
    }

    public EnterFeedURLPopUp configureDashlet()
    {
        browser.mouseOver(titleBarActions);
        browser.waitUntilElementHasAttribute(titleBarActions, "style", "opacity: 1;");
        configureDashletButton.click();
        return (EnterFeedURLPopUp) enterFeedURLPopUp.renderedPage();
    }

    public RssFeedDashlet clickOnRssLink(int position)
    {
        feedsList.get(position).click();
        return this;
    }

    public RssFeedDashlet assertRssFeedLinkIsOpened(String expectedUrlTitle)
    {
        LOG.info("Assert RSS Feed window is opened");
        getBrowser().switchWindow(1);
        getBrowser().waitUrlContains(expectedUrlTitle, 20);
        Assert.assertTrue(getBrowser().getCurrentUrl().contains(expectedUrlTitle) , "Rss feed title is correct");
        getBrowser().closeWindowAndSwitchBack();
        return this;
    }
}
