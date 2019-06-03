package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Table;

@PageObject
public class SiteSearchDashlet extends Dashlet<SiteSearchDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.sitesearch")
    private WebElement dashletContainer;

    @FindBy (css = "[id$=default-search-text]")
    private WebElement searchField;

    @FindBy (css = "span.first-child  [id*=default-search-button]")
    private WebElement searchButton;

    @FindBy (css = "[id$='default-resultSize-button']")
    private WebElement filterButton;

    @FindAll (@FindBy (css = "div.sitesearch div.bd ul.first-of-type li a"))
    private List<WebElement> dropDownFilterList;

    @FindBy (css = "div[id$='_default-search-results'] table")
    private Table resultsTable;

    @FindBy (css = "span[id$='_default-resultSize'] button")
    private WebElement setSizeButton;

    private By noResults = By.cssSelector("div[id$=search-results] tbody.yui-dt-message div");

    private WebElement selectSize(String size)
    {
        return browser.findElement(By.xpath("//div[contains(@class, 'yui-menu-button-menu visible')]//a[text()='" + size + "']"));
    }

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Method to verify that search filed is displayed
     */
    public boolean isSearchFieldDisplayed()
    {
        return browser.isElementDisplayed(searchField);
    }

    /**
     * Method to verify that search button is displayed
     */
    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton);
    }

    /**
     * Method to verify that drop down menu with the number of items is displayed
     */
    public boolean isDropDownMenuDisplayed()
    {
        return browser.isElementDisplayed(filterButton);
    }

    /**
     * Method to get the list of drop down values
     */
    public List<String> getDropDownValues()
    {
        List<WebElement> options = browser.findElements(By.cssSelector("div.sitesearch div.bd ul.first-of-type li a"));
        List<String> dropDownList = new ArrayList<>();
        filterButton.click();
        for (WebElement option : options)
        {
            dropDownList.add(option.getText());
        }
        return dropDownList;
    }

    public boolean checkValuesFromDropDownList()
    {
        String[] expectedValues = { "10", "25", "50", "100" };
        List<String> list = getDropDownValues();
        boolean match = true;
        for (int i = 0; i < expectedValues.length; i++)
        {
            if (!list.get(i).equals(expectedValues[i]))
            {
                match = false;
                break;
            }
        }
        return match;
    }

    public void clickSearchButton()
    {
        searchButton.click();
        this.renderedPage();
    }

    public boolean isMessageDisplayedInDashlet(String message)
    {
        int counter = 0;
        while (!browser.findElement(noResults).getText().equals(message) && counter < 5)
        {
            browser.waitInSeconds(1);
            counter++;
        }
        return browser.findElement(noResults).getText().equals(message);
    }

    public void sendInputToSearchField(String inputText)
    {
        getBrowser().waitUntilElementVisible(searchField);
        searchField.clear();
        searchField.sendKeys(inputText);
    }

    public boolean isResultDisplayed(String resultName)
    {
        getBrowser().waitInSeconds(1);
        String results = resultsTable.getRowsAsString().toString();
        LOG.info("results: " + results);
        return results.contains(resultName);
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
        browser.waitInSeconds(5);
        int allResults = resultsTable.getRowsAsString().size();
        int actualResultsNumber = allResults - 1;
        return actualResultsNumber;
    }
}
