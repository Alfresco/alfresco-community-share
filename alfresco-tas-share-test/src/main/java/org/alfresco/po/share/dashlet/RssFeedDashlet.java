package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_20;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class RssFeedDashlet extends Dashlet<RssFeedDashlet>
{
    private final int SECOND_TAB = 1;
    private final By dashletContainer = By.cssSelector("div.dashlet.rssfeed");
    private final By feedsList = By.cssSelector("div.headline h4 a");
    private final By titleBarActions = By.cssSelector("div[class^='dashlet rssfeed'] .titleBarActions");
    private final By dashletTitleBar = By.cssSelector("div[class^='dashlet rssfeed'] .title");
    private final By configureDashletButton = By.cssSelector("div[class^='dashlet rssfeed'] div[class='titleBarActionIcon edit']");

    public RssFeedDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public RssFeedDashlet assertDashletTitleContains(String expectedTitle)
    {
        log.info("Assert dashlet title contains: {}", expectedTitle);
        assertTrue(getDashletTitle().contains(expectedTitle), "Rss feed dashlet title is not correct");

        return this;
    }

    public EnterFeedURLPopUp configureDashlet()
    {
        log.info("Configure dashlet");
        mouseOver(dashletTitleBar);
        mouseOver(titleBarActions);
        waitUntilElementHasAttribute(titleBarActions, "style", "opacity: 1;");
        clickElement(configureDashletButton);

        return new EnterFeedURLPopUp(webDriver);
    }

    public RssFeedDashlet clickOnRssLink(int position)
    {
        log.info("Click on rss link found at position: {}", position);
        waitUntilElementsAreVisible(feedsList).get(position).click();

        return this;
    }

    public RssFeedDashlet assertRssFeedLinkIsOpenedInNewBrowserTab(String expectedUrlTitle)
    {
        log.info("Assert RSS Feed window is opened");
        int tabs = getWindowHandles().size();
        int retry = 0;
        while(tabs != 2 && retry < WAIT_20.getValue())
        {
            log.error("Wait for RSS tab to open");
            tabs = getWindowHandles().size();
            retry++;
        }
        switchWindow(SECOND_TAB);
        waitUrlContains(expectedUrlTitle, WAIT_20.getValue());
        assertTrue(getCurrentUrl().contains(expectedUrlTitle) , "Rss feed title is correct");
        closeWindowAndSwitchBack();

        return this;
    }

    public RssFeedDashlet assertListSizeEquals(int expectedListSize)
    {
        log.info("Assert list size equals: {}", expectedListSize);
        assertEquals(waitUntilElementsAreVisible(feedsList).size(), expectedListSize,
            String.format("List size not equals %d ", expectedListSize));

        return this;
    }

    public RssFeedDashlet assertFeedListIsEmpty()
    {
        log.info("Assert feed list is empty");
        assertFalse(isElementDisplayed(feedsList),"Feed list is not empty");
        return this;
    }
}
