package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MyDiscussionsDashlet extends Dashlet<MyDiscussionsDashlet>
{
    private final By defaultDashletMessage = By.cssSelector("div.dashlet.forumsummary td div[class$='yui-dt-liner']");
    private final By dashletContainer = By.cssSelector("div.dashlet.forumsummary");
    private final By topicsButton = By.cssSelector("button[id$='default-topics-button']");
    private final By historyButton = By.cssSelector("button[id$='default-history-button']");
    private final By dropDownOptionsList = By.cssSelector("div.forumsummary div.visible ul.first-of-type li a");

    public MyDiscussionsDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public MyDiscussionsDashlet assertNoTopicsMessageEquals(String expectedMessage)
    {
        LOG.info("Assert No topics message equals:{}", expectedMessage);
        assertEquals(webElementInteraction.getElementText(defaultDashletMessage), expectedMessage,
            String.format("No topics message not equals %s", expectedMessage));

        return this;
    }

    public void clickOnTopicButton()
    {
        webElementInteraction.waitUntilElementIsVisible(topicsButton);
        webElementInteraction.clickElement(topicsButton);
    }

    public void clickHistoryButton()
    {
        webElementInteraction.clickElement(historyButton);
    }

    private List<String> getCurrentOptions()
    {
        List<String> options = new ArrayList<>();
        List<WebElement> dropdownOptions = webElementInteraction.waitUntilElementsAreVisible(dropDownOptionsList);
        for (WebElement option : dropdownOptions)
        {
            options.add(option.getText());
        }
        return options;
    }

    public MyDiscussionsDashlet assertMyTopicsDropdownOptionsEqual(List<String> myTopicsDropDownOptions)
    {
        LOG.info("Assert My Topics drop down options equal: {}",myTopicsDropDownOptions);
        clickOnTopicButton();
        List<WebElement> dropdownOptions = webElementInteraction.waitUntilElementsAreVisible(dropDownOptionsList);
        List<String> actualDropDownOptions = webElementInteraction.getTextFromElementList(dropdownOptions);
        assertEquals(myTopicsDropDownOptions, actualDropDownOptions,
            String.format("My Topics drop down options not equal %s", myTopicsDropDownOptions));

        return this;
    }

    public MyDiscussionsDashlet assertHistoryDropdownOptionsEqual(List<String> historyDropDownOptions)
    {
        LOG.info("Assert History drop down options equal: {}", historyDropDownOptions);
        clickHistoryButton();
        List<String> actualDropDownOptions = getCurrentOptions();
        assertEquals(actualDropDownOptions, historyDropDownOptions,
            String.format("History drop down options not equal %s", historyDropDownOptions));

        return this;
    }
}
