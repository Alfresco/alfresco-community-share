package org.alfresco.po.adminconsole.supporttools;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.adminconsole.supporttools.Node.NodeProperty;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.alfresco.utility.report.log.Step.STEP;
import static org.alfresco.utility.web.AbstractWebTest.getBrowser;
@Slf4j
public class NodeBrowserQueryPages<T> extends SharePage2<NodeBrowserQueryPages<T>> {

    private By searchAdvancedSettings = By.cssSelector("a[class='action toggler']");
    private By maxResultsFiled = By.name("nodebrowser-query-maxresults");
    private By skipCountField = By.name("nodebrowser-query-skipcount");
    private By rootList = By.cssSelector("input[value='Root List']");
    private By propertiesTable = By.id("properties-table");
    private By aspectsTable = By.id("aspects-table");
    private By deleteProperty = By.xpath("//*[@id=\"properties-table\"]//tr[2]/td[5]/b/a");

    public NodeBrowserQueryPages(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/s/admin/admin-nodebrowser";
    }

    public NodeBrowserQueryPages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public void clickSearchAdvancedSettings()
    {
        waitUntilElementClickable(searchAdvancedSettings).click();
    }

    public boolean isMaxResultsFieldDisplayed()
    {
        return isElementDisplayed(maxResultsFiled);
    }

    public void setMaxResults(int maxResults)
    {
        if (!isMaxResultsFieldDisplayed())
        {
            findElement(searchAdvancedSettings).click();
        }
        waitUntilElementIsVisible(findElement(maxResultsFiled)).clear();
        findElement(maxResultsFiled).sendKeys(Integer.toString(maxResults));
    }

    public boolean isSkipCountFieldDisplayed()
    {
        return isElementDisplayed(skipCountField);
    }

    public void setSkipCount(int skipCount)
    {
        if (!isSkipCountFieldDisplayed())
        {
            findElement(searchAdvancedSettings).click();
        }
        waitUntilElementIsVisible(skipCountField).clear();
        findElement(skipCountField).sendKeys(Integer.toString(skipCount));
    }

    public void clickRootList()
    {
        waitInSeconds(5);
        findElement(rootList).click();
        waitInSeconds(5);
        findElement(rootList).click();
    }

    public List<String> getAspects()
    {
        ArrayList<String> aspects = new ArrayList<String>();
        List<List<WebElement>> rows = Collections.singletonList(findElements(aspectsTable));
        for (List<WebElement> aspectRow : rows)
        {
            aspects.add(aspectRow.get(0).getText());
        }

        return aspects;
    }

    public void handleModalDialogAcceptingAlert() {
        if (this.isAlertPresent()) {
            Alert alert = this.switchTo().alert();
            String alertText = alert.getText().trim();
            log.info("Alert data: " + alertText);
            alert.accept();
        }

    }

    public void clickDelete()
    {
        findElement(deleteProperty).click();
        handleModalDialogAcceptingAlert();
    }

    public List<NodeProperty> getProperties()
    {
        waitInSeconds(10);
        ArrayList<NodeProperty> properties = new ArrayList<NodeProperty>();
        List<List<WebElement>> rows = Collections.singletonList(findElements(propertiesTable));
        for (List<WebElement> rowInfo : rows)
        {
            NodeProperty np = new NodeProperty(rowInfo, getBrowser());
            properties.add(np);
        }

        return properties;
    }

    public void assertAspectsArePresent(List<String> aspects)
    {
        SoftAssert softAssert = new SoftAssert();
        List<String> aspectsDisplayed = getAspects();
        for (String aspect : aspects)
        {
            System.out.print(aspect);
            softAssert.assertFalse(aspectsDisplayed.indexOf(aspect) == -1, String.format("[%s] is not displayed in Aspects table", aspect));
        }
    }
}
