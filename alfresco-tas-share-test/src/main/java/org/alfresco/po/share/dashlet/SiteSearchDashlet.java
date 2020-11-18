package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SiteSearchDashlet extends Dashlet<SiteSearchDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.sitesearch")
    private WebElement dashletContainer;

    @RenderWebElement
    @FindBy (css = "input[id$=default-search-text]")
    private WebElement searchField;

    @FindBy (css = "button[id$=default-search-button]")
    private WebElement searchButton;

    @FindBy (css = "button[id$='default-resultSize-button']")
    private WebElement filterButton;

    @FindBy (css = "div.sitesearch .yui-dt-empty>div")
    private WebElement noResultsMessage;

    private final By filterOption = By.cssSelector("div.sitesearch div.bd ul.first-of-type li a");
    private final String siteSearchRow = "//h3[@class='itemname']//a[normalize-space()='%s']";

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    private List<String> getDropDownValues()
    {
        List<WebElement> options = browser.waitUntilElementsVisible(filterOption);
        return options.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public SiteSearchDashlet assertDropdownValuesEqual(List<String> dropDownValues)
    {
        LOG.info("Assert dropdown values equal: {}", dropDownValues);
        assertEquals(getDropDownValues(), dropDownValues,
            String.format("Drop down values not equal %s ", dropDownValues));

        return this;
    }

    public SiteSearchDashlet clickSearchButton()
    {
        LOG.info("Click Search button");
        searchButton.click();
        return this;
    }

    public SiteSearchDashlet clickFileLinkName(String fileLinkName)
    {
        LOG.info("Click file link name: {}", fileLinkName);
        browser.waitWithRetryAndReturnWebElement(By.xpath(String.format(siteSearchRow, fileLinkName)), WAIT_1, RETRY_TIMES);
        browser.scrollToElement(browser.findElement(By.xpath(String.format(siteSearchRow, fileLinkName))));
        browser.findElement(By.xpath(String.format(siteSearchRow, fileLinkName))).click();

        return this;
    }

    public SiteSearchDashlet typeInSearch(String inputText)
    {
        LOG.info("Type in search field: {}", inputText);
        searchField.clear();
        searchField.sendKeys(inputText);
        return this;
    }

    public SiteSearchDashlet assertReturnedSearchResultEquals(String expectedSearchResult)
    {
        LOG.info("Assert returned search results equals: {}", expectedSearchResult);
        browser.waitWithRetryAndReturnWebElement(
            By.xpath(String.format(siteSearchRow, expectedSearchResult)), WAIT_1, RETRY_TIMES);
        assertEquals(
            browser.findElement(By.xpath(String.format(siteSearchRow, expectedSearchResult)))
                .getText(), expectedSearchResult, String.format("Returned search result not equals %s ", expectedSearchResult));

        return this;
    }

    public SiteSearchDashlet assertNoResultsFoundMessageEquals(String expectedSearchMessage)
    {
        LOG.info("Assert no results found message equals: {}", expectedSearchMessage);
        browser.waitUntilElementVisible(noResultsMessage);
        assertEquals(noResultsMessage.getText(), expectedSearchMessage,
            String.format("No results found message not equals %s ", expectedSearchMessage));

        return this;
    }

    public SiteSearchDashlet openSearchFilterDropdown() {
        LOG.info("Open search filter dropdown");
        filterButton.click();
        return this;
    }
}
