package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 7/22/2016.
 */
@PageObject
public class SiteLinksDashlet extends Dashlet<SiteLinksDashlet>
{
    //@Autowired
    CreateLinkPage createLinkPage;

    //@Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.site-links")
    private HtmlElement dashletContainer;

    @FindBy (css = "a[href='links-linkedit']")
    private Link createLink;

    @FindAll (@FindBy (css = "div.dashlet.site-links .link>a"))
    private List<WebElement> siteLinksList;

    @FindBy (css = "div.dashlet.site-links .detail-list-item span")
    private HtmlElement emptyDashletMessage;

    private By linkDetails = By.xpath("../following-sibling::*[@class = 'actions']/a");
    private String linkNameLocator = "//div[@class='link']//a[text()='%s']";

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public SiteLinksDashlet assertDashletEmptyMessageEquals(String expectedEmptyMessage)
    {
        LOG.info("Assert dashlet empty message equals: {}", expectedEmptyMessage);
        assertEquals(emptyDashletMessage.getText(), expectedEmptyMessage,
            String.format("Empty message not equals %s", expectedEmptyMessage));

        return this;
    }

    private WebElement findLink(String link)
    {
        return browser.findFirstElementWithValue(siteLinksList, link);
    }

    public SiteLinksDashlet assertDashletLinkNameEquals(String expectedLinkName)
    {
        LOG.info("Assert dashlet link name equals: {}", expectedLinkName);
        String actualLinkName = browser.findElement(By.xpath(String.format(linkNameLocator, expectedLinkName))).getText();
        assertEquals(actualLinkName, expectedLinkName,
            String.format("Link name not equals %s ", expectedLinkName));

        return this;
    }

    public LinkDetailsViewPage clickLinkDetailsButton(String linkName)
    {
        LOG.info("Click link details: {}", linkName);
        findLink(linkName).findElement(linkDetails).click();
        return (LinkDetailsViewPage) linkDetailsViewPage.renderedPage();
    }

    public CreateLinkPage clickCreateLinkButton()
    {
        LOG.info("Click Create Link button");
        createLink.click();
        return (CreateLinkPage) createLinkPage.renderedPage();
    }

    public SiteLinksDashlet clickLinkByName(String linkName)
    {
        LOG.info("Click link with name: {}", linkName);
        browser.findElement(By.xpath(String.format(linkNameLocator, linkName))).click();
        return this;
    }

    public SiteLinksDashlet assertUrlContains(String expectedUrl)
    {
        LOG.info("Assert url contains: {}", expectedUrl);
        browser.switchWindow(1);
        assertTrue(new WebDriverWait(browser, WAIT_15).until(ExpectedConditions.urlContains(expectedUrl)),
            String.format("Url not contains %s ", expectedUrl));

        return this;
    }
}
