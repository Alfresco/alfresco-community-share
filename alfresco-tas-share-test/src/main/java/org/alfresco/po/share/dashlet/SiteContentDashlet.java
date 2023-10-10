package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_5;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteContentDashlet extends Dashlet<SiteContentDashlet>
{
    private final By filters = By.cssSelector("div[class^='dashlet docsummary'] .yuimenuitemlabel");
    private final By dashletContainer = By.cssSelector("div.dashlet.docsummary");
    private final By simpleViewIcon = By.cssSelector("div.dashlet.docsummary span[class$='first-child'] [title='Simple View']");
    private final By detailedViewIcon = By.cssSelector("div.dashlet.docsummary span[class$='first-child'] [title='Detailed View']");
    private final By defaultFilterButton = By.cssSelector("[id$='default-filters']");
    private final By emptyMessage = By.cssSelector("div[id$='default-documents'] .empty");
    protected String documentRow = "//div[starts-with(@class, 'dashlet docsummary')]//a[text()='%s']/../../../..";

    public SiteContentDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    protected String getDashletTitle()
    {
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    /**
     * Method to get document row with retry
     *
     * @param documentName document name
     * @return document row web element
     *
     * @implNote The default value of solr indexing resources in database is 10 seconds. In order to
     * avoid reaching maximum number of retries and have failing tests, we decided to increase that
     * wait time within while loop to 5 seconds.
     * <p>
     * https://github.com/Alfresco/SearchServices/blob/master/search-services/alfresco-search/src/main/resources/solr/instance/templates/rerank/conf/solrcore.properties#L58
     */
    protected WebElement getDocumentRowWithRetry(String documentName)
    {
        By documentRowElement = By.xpath(String.format(documentRow, documentName));

        int retryCount = 0;
        while (retryCount < RETRY_TIME_80.getValue() && !isElementDisplayed(documentRowElement))
        {
            log.warn("Document {} not displayed - retry: {}", documentName, retryCount);
            refresh();
            waitInSeconds(WAIT_5.getValue());
            waitUntilElementIsVisible(dashletContainer);
            retryCount++;
        }
        return findElement(documentRowElement);
    }

    public SiteContentDashlet assertEmptySiteContentMessageIsCorrect()
    {
        log.info("Assert empty site content message is correct");
        assertEquals(getElementText(emptyMessage), language.translate("siteContentDashlet.emptyList"),
        "Empty list site content dashlet message is not correct");

        return this;
    }

    public SiteContentDashlet clickSimpleViewIcon()
    {
        log.info("Click simple view icon");
        clickElement(simpleViewIcon);
        return this;
    }

    public SiteContentDashlet openFilterDropdown()
    {
        log.info("Open filter dropdown");
        clickElement(defaultFilterButton);
        return this;
    }

    public SiteContentDashlet assertFilterLabelEquals(String expectedFilterLabel)
    {
        log.info("Assert filter label equals: {}", expectedFilterLabel);
        WebElement dropdownFilterLabel = findFirstElementWithValue(filters, expectedFilterLabel);
        assertEquals(getElementText(dropdownFilterLabel), expectedFilterLabel,
            String.format("Filter label not equals %s ", expectedFilterLabel));

        return this;
    }

    public SiteContentDashlet assertDetailedViewIconIsDisplayed()
    {
        log.info("Assert detailed view icon is displayed");
        waitUntilElementIsVisible(detailedViewIcon);
        assertTrue(isElementDisplayed(detailedViewIcon), "Detailed view icon is not displayed");

        return this;
    }

    public SiteContentDashlet clickDetailedViewButton()
    {
        log.info("Click detailed view button");
        clickElement(detailedViewIcon);
        return this;
    }

    public SiteContentDashletComponent usingDocument(FileModel file)
    {
        return new SiteContentDashletComponent(webDriver, file);
    }
}
