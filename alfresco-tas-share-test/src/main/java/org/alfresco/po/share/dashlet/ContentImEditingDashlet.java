package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_60;
import static org.testng.Assert.*;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ContentImEditingDashlet extends Dashlet<ContentImEditingDashlet>
{
    private final By dashletContainer = By.cssSelector("div[id$='_default-my-docs-dashlet']");
    private final By helpIcon = By.cssSelector("div[id$='_default-my-docs-dashlet'] div[class='titleBarActionIcon help']");
    private final By titleBar = By.cssSelector("div[id$='_default-my-docs-dashlet'] .title");
    private final By dashletContentHeaders = By.cssSelector( "div.hdr h3");
    private final By editedDocumentName = By.cssSelector("h4 > a");
    private final By editDocumentSite = By.cssSelector("a[class$='site-link']");

    private String editedDocumentRow = "//div[@class='detail-list-item']//a[text()='%s']/../../..";

    public ContentImEditingDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public ContentImEditingDashlet clickHelpIcon()
    {
        webElementInteraction.mouseOver(titleBar);
        webElementInteraction.clickElement(helpIcon);
        return this;
    }

    public ContentImEditingDashlet assertAllHeadersAreDisplayed()
    {
        LOG.info("Assert all headers are displayed for Content I'm editing dashlet");
        List<WebElement> headers = webElementInteraction.waitUntilElementsAreVisible(dashletContentHeaders);
        assertNotNull(webElementInteraction.findFirstElementWithExactValue(headers,
            language.translate("contentImEditingDashlet.documents")));
        assertNotNull(webElementInteraction.findFirstElementWithExactValue(headers,
            language.translate("contentImEditingDashlet.blogPosts")));
        assertNotNull(webElementInteraction.findFirstElementWithExactValue(headers,
            language.translate("contentImEditingDashlet.wikiPages")));
        assertNotNull(webElementInteraction.findFirstElementWithExactValue(headers,
            language.translate("contentImEditingDashlet.forumPosts")));

        return this;
    }

    public void clickDocument(FileModel document)
    {
        waitForDocumentToBeDisplayed(document).findElement(editedDocumentName).click();
    }

    public SiteDashboardPage clickSite(FileModel document)
    {
        waitForDocumentToBeDisplayed(document).findElement(editDocumentSite).click();
        return new SiteDashboardPage(webDriver);
    }

    private boolean isDocumentDisplayedInDashlet(FileModel file)
    {
        return webElementInteraction.isElementDisplayed(By.xpath(String.format(editedDocumentRow, file.getName())));
    }

    private WebElement waitForDocumentToBeDisplayed(FileModel file)
    {
        boolean found = isDocumentDisplayedInDashlet(file);
        int retryCount = 0;
        while(retryCount < WAIT_60.getValue() && !found)
        {
            retryCount++;
            LOG.error("Wait for document {} to be displayed: {}", file.getName(), retryCount);
            webElementInteraction.refresh();
            webElementInteraction.waitInSeconds(WAIT_1.getValue());
            webElementInteraction.waitUntilElementIsVisible(dashletContainer);
            found = isDocumentDisplayedInDashlet(file);
        }
        return webElementInteraction.findElement(By.xpath(String.format(editedDocumentRow, file.getName())));
    }

    public ContentImEditingDashlet assertDocumentIsDisplayed(FileModel file)
    {
        LOG.info("Assert document {} is displayed", file.getName());
        waitForDocumentToBeDisplayed(file);
        assertTrue(isDocumentDisplayedInDashlet(file),
            String.format("File %s is not displayed in content I'm editing dashlet", file.getName()));
        return this;
    }
}
