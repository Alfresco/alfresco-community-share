package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @FindAll (@FindBy (css = "div.sitesearch div.bd ul.first-of-type li a"))
    private List<WebElement> dropDownFilterList;

    @FindBy (css = "div[id$='_default-search-results'] table")
    private Table resultsTable;

    @FindBy (css = "span[id$='_default-resultSize'] button")
    private WebElement setSizeButton;

    @FindBy (css = "div.sitesearch .yui-dt-empty>div")
    private WebElement noResults;

    private By filterOption = By.cssSelector("div.sitesearch div.bd ul.first-of-type li a");

    private WebElement selectSize(String size)
    {
        return browser.findElement(By.xpath("//div[contains(@class, 'yui-menu-button-menu visible')]//a[text()='" + size + "']"));
    }

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public SiteSearchDashlet assertSearchFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchField), "Search field is displayed");
        return this;
    }

    public SiteSearchDashlet assertSearchButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public SiteSearchDashlet assertSearchResultDropdownIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(filterButton), "Search filter is displayed");
        return this;
    }

    private List<String> getDropDownValues()
    {
        filterButton.click();
        List<WebElement> options = browser.waitUntilElementsVisible(filterOption);
        return options.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public SiteSearchDashlet assertAllLimitValuesAreDisplayed()
    {
        Assert.assertTrue(getDropDownValues().equals(Arrays.asList(new String[]{"10", "25", "50", "100"})), "All limits are found");
        return this;
    }

    public SiteSearchDashlet clickSearchButton()
    {
        searchButton.click();
        return this;
    }

    public SiteSearchDashlet typeInSearch(String inputText)
    {
        searchField.clear();
        searchField.sendKeys(inputText);
        return this;
    }

    public boolean isResultDisplayed(String resultName)
    {
        String results = resultsTable.getRowsAsString().toString();
        return results.contains(resultName);
    }

    public SiteSearchDashlet assertNoResultsIsDisplayed()
    {
        browser.waitUntilElementVisible(noResults);
        Assert.assertTrue(browser.isElementDisplayed(noResults), "No results is displayed");
        Assert.assertEquals(noResults.getText(), language.translate("siteSearchDashlet.noResults"));
        return this;
    }

    public void setSize(String sizeToSet)
    {
        browser.mouseOver(setSizeButton);
        setSizeButton.click();
        getBrowser().waitUntilElementVisible(By.cssSelector("div[class*='yui-menu-button-menu visible']"));
        selectSize(sizeToSet).click();
        this.renderedPage();
    }

    public boolean isSearchLimitSetTo(String sizeLimit)
    {
        return setSizeButton.getText().contains(sizeLimit);
    }

    public int getResultsNumber()
    {
        int allResults = resultsTable.getRowsAsString().size();
        int actualResultsNumber = allResults - 1;
        return actualResultsNumber;
    }
}
