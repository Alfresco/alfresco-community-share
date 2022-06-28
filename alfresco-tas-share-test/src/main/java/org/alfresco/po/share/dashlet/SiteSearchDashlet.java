package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.*;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
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
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    private List<String> getDropDownValues()
    {
        List<WebElement> options = waitUntilElementsAreVisible(filterOption);
        return options.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public SiteSearchDashlet assertDropdownValuesEqual(List<String> dropDownValues)
    {
        log.info("Assert dropdown values equal: {}", dropDownValues);
        assertEquals(getDropDownValues(), dropDownValues,
            String.format("Drop down values not equal %s ", dropDownValues));

        return this;
    }

    public SiteSearchDashlet clickSearchButton()
    {
        log.info("Click Search button");
        clickElement(searchButton);
        return this;
    }

    public DocumentDetailsPage clickFileLinkName(String fileLinkName)
    {
        log.info("Click file link name: {}", fileLinkName);
        WebElement fileLink = waitWithRetryAndReturnWebElement(By.xpath(String.format(
            siteSearchRow, fileLinkName)), WAIT_2.getValue(), WAIT_60.getValue());
        scrollToElement(fileLink);
        clickElement(fileLink);

        return new DocumentDetailsPage(webDriver);
    }

    public SiteSearchDashlet typeInSearch(String inputText)
    {
        log.info("Type in search field: {}", inputText);
        clearAndType(searchField, inputText);
        return this;
    }

    public SiteSearchDashlet assertReturnedSearchResultEquals(String expectedSearchResult)
    {
        log.info("Assert returned search results equals: {}", expectedSearchResult);
        WebElement result = waitWithRetryAndReturnWebElement(
            By.xpath(String.format(siteSearchRow, expectedSearchResult)), WAIT_1.getValue(), WAIT_60.getValue());
        assertEquals(getElementText(result), expectedSearchResult, String.format("Returned search result not equals %s ", expectedSearchResult));

        return this;
    }

    public SiteSearchDashlet assertNoResultsFoundMessageEquals(String expectedSearchMessage)
    {
        log.info("Assert no results found message equals: {}", expectedSearchMessage);
        waitUntilElementIsVisible(noResultsMessage);
        assertEquals(getElementText(noResultsMessage), expectedSearchMessage,
            String.format("No results found message not equals %s ", expectedSearchMessage));

        return this;
    }

    public SiteSearchDashlet openSearchFilterDropdown()
    {
        log.info("Open search filter dropdown");
        clickElement(filterButton);
        return this;
    }
}
