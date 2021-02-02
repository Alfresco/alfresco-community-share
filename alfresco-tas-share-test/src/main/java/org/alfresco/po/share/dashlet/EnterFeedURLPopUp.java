package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class EnterFeedURLPopUp extends DashletPopUp<EnterFeedURLPopUp>
{
    private final By urlField = By.cssSelector("input[name='url']");
    private final By newWindowCheckbox = By.cssSelector("input[id$='default-configDialog-new_window']");
    private final By numberOfItems = By.cssSelector("select[id$='default-configDialog-limit']");
    private final By enterFeedURLPopUpLocator = By.cssSelector("div[class^='config-feed']");
    private final By urlErrorMessage = By.cssSelector("input[id$='default-configDialog-url'][alf-validation-msg*='error']");

    protected EnterFeedURLPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public EnterFeedURLPopUp setUrlValue(String url)
    {
        LOG.info("Set url value: {}", url);
        webElementInteraction.clearAndType(urlField, url);
        return this;
    }

    public EnterFeedURLPopUp selectNumberOfItemsToDisplay(String dropDownValue)
    {
        LOG.info("Select number of item to display from drop-down: {}", dropDownValue);
        WebElement itemsSelect = webElementInteraction.waitUntilElementIsVisible(numberOfItems);
        Select items = new Select(itemsSelect);
        items.selectByValue(dropDownValue);
        return this;
    }

    public EnterFeedURLPopUp selectNumberOfItemsToDisplay(int dropDownValue)
    {
        selectNumberOfItemsToDisplay(String.valueOf(dropDownValue));
        return this;
    }

    public EnterFeedURLPopUp selectOpenLinksInNewWindowCheckboxFromDialog()
    {
        LOG.info("Select open links in new window checkbox from dialog");
        webElementInteraction.clickElement(newWindowCheckbox);

        return this;
    }

    public EnterFeedURLPopUp assertNumberOfItemsToDisplayFromDropDownIs(String expectedNumberOfItems)
    {
        WebElement itemsSelect = webElementInteraction.waitUntilElementIsVisible(numberOfItems);
        Select items = new Select(itemsSelect);
        assertEquals(items.getFirstSelectedOption().getText(), expectedNumberOfItems,
            "Number of items to be displayed from drop down not equals with expected");

        return this;
    }

    public EnterFeedURLPopUp assertNewWindowIsChecked()
    {
        LOG.info("Assert new window is checked");
        assertTrue(webElementInteraction.waitUntilElementIsVisible(newWindowCheckbox).isSelected(),
            "New window checkbox is not checked");
        return this;
    }

    public boolean isEnterFeedURLPopUpDisplayed()
    {
        return webElementInteraction.waitUntilElementIsVisible(enterFeedURLPopUpLocator).isDisplayed();
    }

    public boolean isUrlErrorMessageDisplayed()
    {
        return webElementInteraction.isElementDisplayed(urlErrorMessage);
    }
}
