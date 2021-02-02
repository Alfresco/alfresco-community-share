package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigureSavedSearchDashletDialog extends DashletPopUp<ConfigureSavedSearchDashletDialog>
{
    private final By searchTermField = By.cssSelector("input[id$='searchTerm']");
    private final By titleField = By.cssSelector("input[id$='configDialog-title']");
    private final By searchLimitElement = By.cssSelector("select[name='limit']");

    public ConfigureSavedSearchDashletDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ConfigureSavedSearchDashletDialog setSearchTermField(String searchTerm)
    {
        webElementInteraction.clearAndType(searchTermField, searchTerm);
        return this;
    }

    public ConfigureSavedSearchDashletDialog setTitleField(String title)
    {
        webElementInteraction.clearAndType(titleField, title);
        return this;
    }

    public ConfigureSavedSearchDashletDialog assertSearchTermFieldIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(searchTermField);
        assertTrue(webElementInteraction.isElementDisplayed(searchTermField), "Search term field is displayed");
        return this;
    }

    public ConfigureSavedSearchDashletDialog assertTitleFieldIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(titleField);
        assertTrue(webElementInteraction.isElementDisplayed(titleField), "Title field is displayed");
        return this;
    }

    public ConfigureSavedSearchDashletDialog assertSearchLimitIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(searchLimitElement);
        assertTrue(webElementInteraction.isElementDisplayed(searchLimitElement), "Search limit is displayed");
        return this;
    }

    public ConfigureSavedSearchDashletDialog assertAllLimitValuesAreDisplayed()
    {
        WebElement select = webElementInteraction.waitUntilElementIsVisible(searchLimitElement);
        Select limits = new Select(select);
        List<WebElement> actualOptions = limits.getOptions();
        List<String> availableOptions = actualOptions.stream().map(WebElement::getText).collect(Collectors.toList());
        assertEquals(availableOptions, Arrays.asList("10", "25", "50", "100"), "All limits are found");
        return this;
    }
}
