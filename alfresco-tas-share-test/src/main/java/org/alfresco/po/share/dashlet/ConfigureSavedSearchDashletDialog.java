package org.alfresco.po.share.dashlet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

@PageObject
public class ConfigureSavedSearchDashletDialog extends DashletPopUp<ConfigureSavedSearchDashletDialog>
{
    @RenderWebElement
    @FindBy (css = "input[id$='searchTerm']")
    protected WebElement searchTermField;

    @FindBy (css = "input[id$='configDialog-title']")
    protected WebElement titleField;

    @FindBy (css = "select[name='limit']")
    protected WebElement searchLimitElement;

    public ConfigureSavedSearchDashletDialog setSearchTermField(String searchTerm)
    {
        searchTermField.clear();
        searchTermField.sendKeys(searchTerm);
        return this;
    }

    public ConfigureSavedSearchDashletDialog setTitleField(String title)
    {
        titleField.clear();
        titleField.sendKeys(title);
        return this;
    }

    public void setSearchLimitField(String searchLimit)
    {
        Select limits = new Select(searchLimitElement);
        limits.selectByValue(searchLimit);
    }

    public ConfigureSavedSearchDashletDialog assertSearchTermFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchTermField), "Search term field is displayed");
        return this;
    }

    public ConfigureSavedSearchDashletDialog assertTitleFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(titleField), "Title field is displayed");
        return this;
    }

    public ConfigureSavedSearchDashletDialog assertSearchLimitIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchLimitElement), "Search limit is displayed");
        return this;
    }

    public ConfigureSavedSearchDashletDialog assertAllLimitValuesAreDisplayed()
    {
        Select limits = new Select(searchLimitElement);
        List<WebElement> actualOptions = limits.getOptions();
        List<String> availableOptions = actualOptions.stream().map(WebElement::getText).collect(Collectors.toList());
        Assert.assertTrue(availableOptions.equals(Arrays.asList(new String[]{"10", "25", "50", "100"})), "All limits are found");
        return this;
    }
}
