package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_60;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SavedSearchDashlet extends Dashlet<SavedSearchDashlet>
{
    private final By defaultDashletMessage = By.cssSelector("div.dashlet.savedsearch td div[class$='yui-dt-liner']");
    private final By configureDashletIcon = By.cssSelector("div.dashlet.savedsearch div[class$='titleBarActionIcon edit']");
    private final By titleBarActions = By.cssSelector("div.dashlet.savedsearch div[class$='titleBarActions']");
    private final By titleBar = By.cssSelector("div.dashlet.savedsearch .title");
    private final By dashletContainer = By.cssSelector("div.dashlet.savedsearch");
    private final By resultsText = By.cssSelector("div[id$='_default-search-results'] tbody div");
    private final By searchResults = By.cssSelector("div[id$='_default-search-results'] table");
    private final By inFolderLocation = By.cssSelector(".details");

    private String searchRow = "//div[starts-with(@class,'dashlet savedsearch')]//a[text()='%s']/../../../..";

    public SavedSearchDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SavedSearchDashlet assertNoResultsFoundMessageEquals(String expectedNoResultsFoundMessage)
    {
        LOG.info("Assert No results found message equals: {}", expectedNoResultsFoundMessage);
        webElementInteraction.waitUntilElementContainsText(defaultDashletMessage, expectedNoResultsFoundMessage);
        assertEquals(webElementInteraction.getElementText(defaultDashletMessage), expectedNoResultsFoundMessage, String
            .format("No results found message not equals %s ", expectedNoResultsFoundMessage));

        return this;
    }

    public SavedSearchDashlet assertConfigureDashletButtonIsDisplayed()
    {
        LOG.info("Assert configure dashlet button is displayed");
        webElementInteraction.mouseOver(titleBar);
        webElementInteraction.mouseOver(titleBarActions);
        assertTrue(webElementInteraction.isElementDisplayed(configureDashletIcon),
            "Configure dashlet button is not displayed");

        return this;
    }

    public ConfigureSavedSearchDashletDialog configureDashlet()
    {
        LOG.info("Configure dashlet");
        webElementInteraction.mouseOver(titleBar);
        webElementInteraction.mouseOver(titleBarActions);
        webElementInteraction.clickElement(configureDashletIcon);
        return new ConfigureSavedSearchDashletDialog(webDriver);
    }

    private WebElement getSearchRow(String fileName)
    {
        return webElementInteraction.waitWithRetryAndReturnWebElement
            (By.xpath(String.format(searchRow, fileName)), WAIT_1.getValue(), WAIT_60.getValue());
    }

    public SavedSearchDashlet assertFileIsDisplayed(String fileName)
    {
        LOG.info("Assert file is found in saved search dashlet: {}", fileName);
        assertTrue(webElementInteraction.isElementDisplayed(getSearchRow(fileName)),
            String.format("File %s was not found", fileName));

        return this;
    }

    public SavedSearchDashlet assertInFolderPathEquals(String fileName, String folderPath)
    {
        LOG.info("Assert In Folder path equals: {}", folderPath);
        assertEquals(getSearchRow(fileName).findElement(inFolderLocation).getText(), folderPath,
            String.format("In Folder path not equals :%s ", folderPath));

        return this;
    }
}
