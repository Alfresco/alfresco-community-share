package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_15;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteLinksDashlet extends Dashlet<SiteLinksDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.site-links");
    private final By createLink = By.cssSelector("a[href='links-linkedit']");
    private final By siteLinksList = By.cssSelector("div.dashlet.site-links .link>a");
    private final By emptyDashletMessage = By.cssSelector("div.dashlet.site-links .detail-list-item span");
    private final By linkDetails = By.xpath("../following-sibling::*[@class = 'actions']/a");

    private final String linkNameLocator = "//div[@class='link']//a[text()='%s']";

    public SiteLinksDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SiteLinksDashlet assertDashletEmptyMessageEquals(String expectedEmptyMessage)
    {
        log.info("Assert dashlet empty message equals: {}", expectedEmptyMessage);
        assertEquals(getElementText(emptyDashletMessage), expectedEmptyMessage,
            String.format("Empty message not equals %s", expectedEmptyMessage));

        return this;
    }

    private WebElement findLink(String link)
    {
        return findFirstElementWithValue(siteLinksList, link);
    }

    public SiteLinksDashlet assertDashletLinkNameEquals(String expectedLinkName)
    {
        log.info("Assert dashlet link name equals: {}", expectedLinkName);
        String actualLinkName = getElementText(By.xpath(String.format(linkNameLocator, expectedLinkName)));
        assertEquals(actualLinkName, expectedLinkName,
            String.format("Link name not equals %s ", expectedLinkName));

        return this;
    }

    public void clickLinkDetailsButton(String linkName)
    {
        log.info("Click link details: {}", linkName);
        findLink(linkName).findElement(linkDetails).click();
    }

    public void clickCreateLinkButton()
    {
        log.info("Click Create Link button");
        clickElement(createLink);
    }

    public SiteLinksDashlet clickLinkByName(String linkName)
    {
        log.info("Click link with name: {}", linkName);
        clickElement(By.xpath(String.format(linkNameLocator, linkName)));
        return this;
    }

    public SiteLinksDashlet assertUrlContains(String expectedUrl)
    {
        log.info("Assert url contains: {}", expectedUrl);
        switchWindow(1);
        waitUrlContains(expectedUrl, WAIT_15.getValue());
        assertTrue(getCurrentUrl().contains(expectedUrl),
            String.format("Url not contains %s ", expectedUrl));

        return this;
    }
}
