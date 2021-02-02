package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_15;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SiteLinksDashlet assertDashletEmptyMessageEquals(String expectedEmptyMessage)
    {
        LOG.info("Assert dashlet empty message equals: {}", expectedEmptyMessage);
        assertEquals(webElementInteraction.getElementText(emptyDashletMessage), expectedEmptyMessage,
            String.format("Empty message not equals %s", expectedEmptyMessage));

        return this;
    }

    private WebElement findLink(String link)
    {
        return webElementInteraction.findFirstElementWithValue(siteLinksList, link);
    }

    public SiteLinksDashlet assertDashletLinkNameEquals(String expectedLinkName)
    {
        LOG.info("Assert dashlet link name equals: {}", expectedLinkName);
        String actualLinkName = webElementInteraction.getElementText(By.xpath(String.format(linkNameLocator, expectedLinkName)));
        assertEquals(actualLinkName, expectedLinkName,
            String.format("Link name not equals %s ", expectedLinkName));

        return this;
    }

    public void clickLinkDetailsButton(String linkName)
    {
        LOG.info("Click link details: {}", linkName);
        findLink(linkName).findElement(linkDetails).click();
    }

    public void clickCreateLinkButton()
    {
        LOG.info("Click Create Link button");
        webElementInteraction.clickElement(createLink);
    }

    public SiteLinksDashlet clickLinkByName(String linkName)
    {
        LOG.info("Click link with name: {}", linkName);
        webElementInteraction.clickElement(By.xpath(String.format(linkNameLocator, linkName)));
        return this;
    }

    public SiteLinksDashlet assertUrlContains(String expectedUrl)
    {
        LOG.info("Assert url contains: {}", expectedUrl);
        webElementInteraction.switchWindow(1);
        webElementInteraction.waitUrlContains(expectedUrl, WAIT_15.getValue());
        assertTrue(webElementInteraction.getCurrentUrl().contains(expectedUrl),
            String.format("Url not contains %s ", expectedUrl));

        return this;
    }
}
