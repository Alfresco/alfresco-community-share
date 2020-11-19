package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class WikiDashlet extends Dashlet<WikiDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.wiki")
    private WebElement dashletContainer;

    @FindBy (css = "div.dashlet.wiki [class$='rich-content dashlet-padding']")
    private WebElement defaultDashletMessage;

    private final By wikiDashletTitle = By.cssSelector("div.dashlet.wiki [class$='title']");
    private final By configureDashlet = By.cssSelector("div.dashlet.wiki [class$='edit']");
    private final By wikiDashletText = By.cssSelector("div.dashlet.wiki [class$='body scrollablePanel']");
    private final String dashletLinkTitleLocator = "//a[contains(text(),'%s')]";

    @Override
    protected String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public WikiDashlet assertWikiDashletEmptyMessageEquals(String expectedEmptyMessage)
    {
        LOG.info("Assert wiki dashlet empty message equals: {}", expectedEmptyMessage);
        assertEquals(defaultDashletMessage.getText(), expectedEmptyMessage,
            String.format("Empty message not equals %s ", expectedEmptyMessage));

        return this;
    }

    public void clickOnConfigureDashletIcon()
    {
        browser.findElement(configureDashlet).click();
    }

    public String getWikiDashletTitle()
    {
        return browser.findElement(wikiDashletTitle).getText();
    }

    public WikiDashlet assertWikiDashletMessageEquals(String expectedWikiDashletMessage)
    {
        LOG.info("Assert wiki dashlet message equals: {}", expectedWikiDashletMessage);
        assertEquals(browser.findElement(wikiDashletText).getText(), expectedWikiDashletMessage);
        return this;
    }

    public WikiDashlet clickDashletLinkTitle(String wikiDashletLinkTitle)
    {
        LOG.info("Click dashlet link title: {}", wikiDashletLinkTitle);
        browser.findElement(By.xpath(String.format(dashletLinkTitleLocator, wikiDashletLinkTitle))).click();

        return this;
    }
}
