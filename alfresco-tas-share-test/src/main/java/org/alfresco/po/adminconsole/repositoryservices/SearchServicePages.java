package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.alfresco.utility.report.log.Step.STEP;

public class SearchServicePages<T> extends SharePage2<SearchServicePages<T>> {

    private final By contentTrackingCheckBox = By.xpath("//div[@id='solrSearch6']//div[@class='column-left']//input[@type='checkbox']");
    private final By solrSslPort = By.xpath("//div[@id='solrSearch6']//input[contains(@name, 'solr.port.ssl')]");
    private final By solrPortNonSsl = By.cssSelector("div[id='solrSearch6'] input[name$='solr.port']");
    private final By saveButton = By.xpath("//input[@value='Save'][@type='submit']");
    private final By serviceStatusTab = By.xpath("//input[@id='tc1'][@value='Service Status']");
    private final By indexedDocumentCount = By.xpath("//div[@id='elasticsearchDocumentCount']/descendant::span[text()='Indexed document count:']/following::span[1]");
    private final By indexableDocumentCount = By.xpath("//div[@id='repositoryNodesCount']/descendant::span[text()='Indexable document count:']/following::span[1]");
    private final By contentIndexingSuccessCount = By.xpath("//div[@id='elasticsearchContentIndexingSuccessCount']/descendant::span[text()='Content indexing success count:']/following::span[1]");
    private final By contentIndexingFailureCount = By.xpath("//div[@id='elasticsearchContentIndexingFailuresCount']/descendant::span[text()='Content indexing failures count:']/following::span[1]");
    private final By startHealthCheckButton = By.xpath("//input[@id='start-healthcheck-button'][@value='Start Healthcheck']");
    private final By startHealthCheckPopUp = By.xpath("//div[@class='title']/descendant::h1[text()='Start Elasticsearch healthcheck']");
    private final By startHealthCheckIFrame = By.xpath("//iframe[contains(@src,'admin-searchservice-elasticsearch-start-healthcheck')]");
    private final By startHealthCheckIFrameCloseButton = By.xpath("//input[@value='Close'][@class='cancel']");
    private final By healthCheckStatus = By.xpath("//div[@class='column-left']/descendant::div[@class='control status']/descendant::span[3]");
    private final By healthCheckStartTime = By.xpath("//div[@class='column-left']/descendant::div[@class='control field']/descendant::span[text()='Start Time:']/following-sibling::span");
    private final By healthCheckEndTime = By.xpath("//div[@class='column-left']/descendant::div[@class='control field']/descendant::span[text()='Finish Time:']/following-sibling::span");

    public SearchServicePages(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }
    @Override
    public String getRelativePath() {
        return "alfresco/s/enterprise/admin/admin-searchservice";
    }

    public void ClickOnSaveButton()
    {
        findElement(saveButton).click();
    }

    public String getSolrSslPort()
    {
        return findElement(solrSslPort).getAttribute("value");
    }

    public String getSolrPortNonSsl()
    {
        waitInSeconds(3);
        return findElement(solrPortNonSsl).getAttribute("value");
    }

    public void fillValuesolrPortNonSsl()
    {
        findElement(solrPortNonSsl).clear();
        findElement(solrPortNonSsl).sendKeys("5678");
    }

    public void fillValueSolrPort()
    {
        findElement(solrSslPort).clear();
        findElement(solrSslPort).sendKeys("1234");
    }

    public boolean contentTrackingCheckBox()
    {
        return findElement(contentTrackingCheckBox).isSelected();
    }

    public void ClickOncontentTrackingCheckBox()
    {
        findElement(contentTrackingCheckBox).click();
    }

    public boolean displayedContentTrackingCheckBox()
    {
        return isElementDisplayed(contentTrackingCheckBox);
    }

    public SearchServicePages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public void refreshPageBySeconds(int seconds)
    {
        waitInSeconds(seconds);
        getWebDriver().navigate().refresh();
    }

    public void clickServiceStatus()
    {
        waitUntilElementIsVisible(serviceStatusTab);
        findElement(serviceStatusTab).click();
    }

    public String getIndexedDocumentCount()
    {
        waitUntilElementIsVisible(indexedDocumentCount);
        return findElement(indexedDocumentCount).getText();
    }

    public String getIndexableDocumentCount()
    {
        waitUntilElementIsVisible(indexableDocumentCount);
        return findElement(indexableDocumentCount).getText();
    }

    public String getContentIndexingSuccessCount()
    {
        waitUntilElementIsVisible(contentIndexingSuccessCount);
        return findElement(contentIndexingSuccessCount).getText();
    }

    public void clickStartHealthCheckButton()
    {
        waitUntilElementIsVisible(startHealthCheckButton);
        findElement(startHealthCheckButton).click();
    }

    public void isStartHealthCheckPopUpDisplayed()
    {
        waitUntilElementIsVisible(startHealthCheckPopUp);
    }

    public void switchToStartHealthCheckPopUp()
    {
        switchToFrame(findElement(startHealthCheckIFrame).getAttribute("id"));
    }

    public void closeStartHealthCheckPopUp()
    {
        waitUntilElementIsVisible(startHealthCheckIFrameCloseButton);
        findElement(startHealthCheckIFrameCloseButton).click();
        switchToDefaultContent();
    }

    public String getHealthCheckStatus()
    {
        waitUntilElementIsVisible(healthCheckStatus);
        return findElement(healthCheckStatus).getText();
    }

    public String getHealthCheckStartTime() {
        if (isElementDisplayed(healthCheckStartTime)) {
            return findElement(healthCheckStartTime).getText();
        } else {
            return "";
        }
    }

    public String getHealthCheckEndTime()
    {
        if (isElementDisplayed(healthCheckEndTime)) {
            return findElement(healthCheckEndTime).getText();
        } else {
            return "";
        }
    }

    public String getContentIndexingFailureCount()
    {
        waitUntilElementIsVisible(contentIndexingFailureCount);
        return findElement(contentIndexingFailureCount).getText();
    }
}
