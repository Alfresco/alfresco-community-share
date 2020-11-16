package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class SavedSearchDashlet extends Dashlet<SavedSearchDashlet>
{
    @Autowired
    private ConfigureSavedSearchDashletDialog configureSavedSearchPopUp;

    @FindBy (css = "div.dashlet.savedsearch td div[class$='yui-dt-liner']")
    private WebElement defaultDashletMessage;

    @FindBy (css = "div.dashlet.savedsearch div[class$='titleBarActionIcon edit']")
    private WebElement configureDashletIcon;

    @FindBy (css = "div.dashlet.savedsearch div[class$='titleBarActions']")
    private WebElement titleBar;

    @RenderWebElement
    @FindBy (css = "div.dashlet.savedsearch")
    private WebElement dashletContainer;

    @FindBy (css = "div[id$='_default-search-results'] tbody div")
    private WebElement resultsText;

    @FindBy (css = "div[id$='_default-search-results'] table")
    private WebElement searchResults;

    private String searchRow = "//div[starts-with(@class,'dashlet savedsearch')]//a[text()='%s']/../../../..";
    private By inFolderLocation = By.cssSelector(".details");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public SavedSearchDashlet assertNoResultsFoundMessageEquals(String expectedNoResultsFoundMessage)
    {
        LOG.info("Assert No results found message equals: {}", expectedNoResultsFoundMessage);
        browser.waitUntilElementContainsText(defaultDashletMessage, expectedNoResultsFoundMessage);
        assertEquals(defaultDashletMessage.getText(), expectedNoResultsFoundMessage, String
            .format("No results found message not equals %s ", expectedNoResultsFoundMessage));

        return this;
    }

    public SavedSearchDashlet assertConfigureDashletButtonIsDisplayed()
    {
        LOG.info("Assert configure dashlet button is displayed");
        browser.mouseOver(titleBar);
        assertTrue(browser.isElementDisplayed(configureDashletIcon),
            "Configure dashlet button is not displayed");

        return this;
    }

    public ConfigureSavedSearchDashletDialog configureDashlet()
    {
        LOG.info("Configure dashlet");
        browser.mouseOver(titleBar);
        configureDashletIcon.click();
        return (ConfigureSavedSearchDashletDialog) configureSavedSearchPopUp.renderedPage();
    }

    private WebElement getSearchRow(String fileName)
    {
        return browser.waitWithRetryAndReturnWebElement
            (By.xpath(String.format(searchRow, fileName)), WAIT_1, RETRY_TIMES);
    }

    public SavedSearchDashlet assertFileIsDisplayed(String fileName)
    {
        LOG.info("Assert file is found in saved search dashlet: {}", fileName);
        assertTrue(browser.isElementDisplayed(getSearchRow(fileName)),
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
