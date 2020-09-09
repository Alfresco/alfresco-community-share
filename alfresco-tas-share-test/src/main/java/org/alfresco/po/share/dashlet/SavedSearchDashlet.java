package org.alfresco.po.share.dashlet;

import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

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

    public SavedSearchDashlet assertNoResultsMessageIsDisplayed()
    {
        LOG.info("Assert No results found message is displayed");
        browser.waitUntilElementContainsText(defaultDashletMessage, language.translate("savedSearchDashlet.noResults"));
        Assert.assertEquals(defaultDashletMessage.getText(), language.translate("savedSearchDashlet.noResults"));
        return this;
    }

    public SavedSearchDashlet assertConfigureDashletButtonIsDisplayed()
    {
        browser.mouseOver(titleBar);
        Assert.assertTrue(browser.isElementDisplayed(configureDashletIcon), "Configure dashlet button is displayed");
        return this;
    }

    /**
     * Click on configure dashlet icon.
     */
    public ConfigureSavedSearchDashletDialog clickConfigureDashlet()
    {
        browser.mouseOver(titleBar);
        configureDashletIcon.click();
        return (ConfigureSavedSearchDashletDialog) configureSavedSearchPopUp.renderedPage();
    }

    private WebElement getSearchRow(String fileName)
    {
        return browser.waitWithRetryAndReturnWebElement
            (By.xpath(String.format(searchRow, fileName)), 1, 30);
    }

    public SavedSearchDashlet assertFileIsDisplayed(String fileName)
    {
        LOG.info(String.format("Assert file %s is found in saved search dashlet", fileName));
        Assert.assertTrue(browser.isElementDisplayed(getSearchRow(fileName)), String.format("File %s was found", fileName));
        return this;
    }

    public SavedSearchDashlet assertFileIsDisplayed(FileModel file)
    {
        return assertFileIsDisplayed(file.getName());
    }

    public SavedSearchDashlet assertInFolderPathIs(String fileName, String folderPath)
    {
        Assert.assertEquals(getSearchRow(fileName).findElement(inFolderLocation).getText(),
            String.format(language.translate("savedSearchDashlet.item.inFolderPath"), folderPath));
        return this;
    }
}
