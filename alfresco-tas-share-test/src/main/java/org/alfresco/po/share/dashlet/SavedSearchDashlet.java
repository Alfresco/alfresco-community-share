package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SavedSearchDashlet extends Dashlet<SavedSearchDashlet>
{
    private final By defaultDashletMessage = By.cssSelector("div.dashlet.savedsearch td div[class$='yui-dt-liner']");
    private final By configureDashletIcon = By.cssSelector("div.dashlet.savedsearch div[class$='titleBarActionIcon edit']");
    private final By titleBarActions = By.cssSelector("div.dashlet.savedsearch div[class$='titleBarActions']");
    private final By titleBar = By.cssSelector("div.dashlet.savedsearch .title");
    private final By dashletContainer = By.cssSelector("div.dashlet.savedsearch");
    private final By inFolderLocation = By.cssSelector(".details");

    private String searchRow = "//div[starts-with(@class,'dashlet savedsearch')]//a[text()='%s']/../../../..";

    public SavedSearchDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SavedSearchDashlet assertNoResultsFoundMessageEquals(String expectedNoResultsFoundMessage)
    {
        log.info("Assert No results found message equals: {}", expectedNoResultsFoundMessage);
        waitUntilElementContainsText(defaultDashletMessage, expectedNoResultsFoundMessage);
        assertEquals(getElementText(defaultDashletMessage), expectedNoResultsFoundMessage, String
            .format("No results found message not equals %s ", expectedNoResultsFoundMessage));

        return this;
    }

    public SavedSearchDashlet assertConfigureDashletButtonIsDisplayed()
    {
        log.info("Assert configure dashlet button is displayed");
        mouseOver(titleBar);
        mouseOver(titleBarActions);
        assertTrue(isElementDisplayed(configureDashletIcon),
            "Configure dashlet button is not displayed");

        return this;
    }

    public ConfigureSavedSearchDashletDialog configureDashlet()
    {
        log.info("Configure dashlet");
        mouseOver(titleBar);
        mouseOver(titleBarActions);
        clickElement(configureDashletIcon);
        return new ConfigureSavedSearchDashletDialog(webDriver);
    }

    private WebElement getSearchRow(String fileName)
    {
        return waitWithRetryAndReturnWebElement
            (By.xpath(String.format(searchRow, fileName)), WAIT_2.getValue(), RETRY_TIME_80.getValue());
    }

    public SavedSearchDashlet assertFileIsDisplayed(String fileName)
    {
        log.info("Assert file is found in saved search dashlet: {}", fileName);
        assertTrue(isElementDisplayed(getSearchRow(fileName)),
            String.format("File %s was not found", fileName));

        return this;
    }

    public SavedSearchDashlet assertInFolderPathEquals(String fileName, String folderPath)
    {
        log.info("Assert In Folder path equals: {}", folderPath);
        assertEquals(getSearchRow(fileName).findElement(inFolderLocation).getText(), folderPath,
            String.format("In Folder path not equals :%s ", folderPath));

        return this;
    }
}
