package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.alfresco.utility.report.log.Step.STEP;

public class SearchServicePages<T> extends SharePage2<SearchServicePages<T>> {

    private By contentTrackingCheckBox = By.xpath("//div[@id='solrSearch6']//div[@class='column-left']//input[@type='checkbox']");
    private By solrSslPort = By.xpath("//div[@id='solrSearch6']//input[contains(@name, 'solr.port.ssl')]");
    private By solrPortNonSsl = By.cssSelector("div[id='solrSearch6'] input[name$='solr.port']");
    private By saveButton = By.xpath("//input[@value='Save'][@type='submit']");

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
}
