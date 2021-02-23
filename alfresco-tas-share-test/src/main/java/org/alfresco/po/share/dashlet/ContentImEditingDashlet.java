package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
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
        return waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public ContentImEditingDashlet clickHelpIcon()
    {
        mouseOver(titleBar);
        clickElement(helpIcon);
        return this;
    }

    public ContentImEditingDashlet assertAllHeadersAreDisplayed()
    {
        log.info("Assert all headers are displayed for Content I'm editing dashlet");
        List<WebElement> headers = waitUntilElementsAreVisible(dashletContentHeaders);
        assertNotNull(findFirstElementWithExactValue(headers,
            language.translate("contentImEditingDashlet.documents")));
        assertNotNull(findFirstElementWithExactValue(headers,
            language.translate("contentImEditingDashlet.blogPosts")));
        assertNotNull(findFirstElementWithExactValue(headers,
            language.translate("contentImEditingDashlet.wikiPages")));
        assertNotNull(findFirstElementWithExactValue(headers,
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
        return isElementDisplayed(By.xpath(String.format(editedDocumentRow, file.getName())));
    }

    private WebElement waitForDocumentToBeDisplayed(FileModel file)
    {
        boolean found = isDocumentDisplayedInDashlet(file);
        int retryCount = 0;
        while(retryCount < RETRY_TIME_80.getValue() && !found)
        {
            log.warn("File {} not displayed - retry: {}", file.getName(), retryCount);
            refresh();
            waitInSeconds(WAIT_2.getValue());
            found = isDocumentDisplayedInDashlet(file);
            retryCount++;
        }
        return findElement(By.xpath(String.format(editedDocumentRow, file.getName())));
    }

    public ContentImEditingDashlet assertDocumentIsDisplayed(FileModel file)
    {
        log.info("Assert document {} is displayed", file.getName());
        waitForDocumentToBeDisplayed(file);
        assertTrue(isDocumentDisplayedInDashlet(file),
            String.format("File %s is not displayed in content I'm editing dashlet", file.getName()));
        return this;
    }
}
