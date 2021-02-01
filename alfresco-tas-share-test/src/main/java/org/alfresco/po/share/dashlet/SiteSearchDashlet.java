package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_60;

import java.util.List;
import java.util.stream.Collectors;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SiteSearchDashlet extends Dashlet<SiteSearchDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.sitesearch");
    private final By searchField = By.cssSelector("input[id$=default-search-text]");
    private final By searchButton = By.cssSelector("button[id$=default-search-button]");
    private final By noResultsMessage = By.cssSelector("div.sitesearch .yui-dt-empty>div");
    private final By filterOption = By.cssSelector("div.sitesearch div.bd ul.first-of-type li a");
    private final By filterButton = By.cssSelector("button[id$='default-resultSize-button']");
    private final String siteSearchRow = "//h3[@class='itemname']//a[normalize-space()='%s']";

    public SiteSearchDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    private List<String> getDropDownValues()
    {
        List<WebElement> options = webElementInteraction.waitUntilElementsAreVisible(filterOption);
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
        webElementInteraction.clickElement(searchButton);
        return this;
    }

    public DocumentDetailsPage clickFileLinkName(String fileLinkName)
    {
        LOG.info("Click file link name: {}", fileLinkName);
        WebElement fileLink = webElementInteraction.waitWithRetryAndReturnWebElement(By.xpath(String.format(
            siteSearchRow, fileLinkName)), WAIT_1.getValue(), WAIT_60.getValue());
        webElementInteraction.scrollToElement(fileLink);
        webElementInteraction.clickElement(fileLink);

        return new DocumentDetailsPage(webDriver);
    }

    public SiteSearchDashlet typeInSearch(String inputText)
    {
        LOG.info("Type in search field: {}", inputText);
        webElementInteraction.clearAndType(searchField, inputText);
        return this;
    }

    public SiteSearchDashlet assertReturnedSearchResultEquals(String expectedSearchResult)
    {
        LOG.info("Assert returned search results equals: {}", expectedSearchResult);
        WebElement result = webElementInteraction.waitWithRetryAndReturnWebElement(
            By.xpath(String.format(siteSearchRow, expectedSearchResult)), WAIT_1.getValue(), WAIT_60.getValue());
        assertEquals(webElementInteraction.getElementText(result), expectedSearchResult, String.format("Returned search result not equals %s ", expectedSearchResult));

        return this;
    }

    public SiteSearchDashlet assertNoResultsFoundMessageEquals(String expectedSearchMessage)
    {
        LOG.info("Assert no results found message equals: {}", expectedSearchMessage);
        webElementInteraction.waitUntilElementIsVisible(noResultsMessage);
        assertEquals(webElementInteraction.getElementText(noResultsMessage), expectedSearchMessage,
            String.format("No results found message not equals %s ", expectedSearchMessage));

        return this;
    }

    public SiteSearchDashlet openSearchFilterDropdown()
    {
        LOG.info("Open search filter dropdown");
        webElementInteraction.clickElement(filterButton);
        return this;
    }
}
