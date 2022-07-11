package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
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
        return waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public MyDiscussionsDashlet assertNoTopicsMessageEquals(String expectedMessage)
    {
        log.info("Assert No topics message equals:{}", expectedMessage);
        assertEquals(getElementText(defaultDashletMessage), expectedMessage,
            String.format("No topics message not equals %s", expectedMessage));

        return this;
    }

    public void clickOnTopicButton()
    {
        waitUntilElementIsVisible(topicsButton);
        clickElement(topicsButton);
    }

    public void clickHistoryButton()
    {
        clickElement(historyButton);
    }

    private List<String> getCurrentOptions()
    {
        List<String> options = new ArrayList<>();
        List<WebElement> dropdownOptions = waitUntilElementsAreVisible(dropDownOptionsList);
        for (WebElement option : dropdownOptions)
        {
            options.add(option.getText());
        }
        return options;
    }

    public MyDiscussionsDashlet assertMyTopicsDropdownOptionsEqual(List<String> myTopicsDropDownOptions)
    {
        log.info("Assert My Topics drop down options equal: {}",myTopicsDropDownOptions);
        clickOnTopicButton();
        List<WebElement> dropdownOptions = waitUntilElementsAreVisible(dropDownOptionsList);
        List<String> actualDropDownOptions = getTextFromElementList(dropdownOptions);
        assertEquals(myTopicsDropDownOptions, actualDropDownOptions,
            String.format("My Topics drop down options not equal %s", myTopicsDropDownOptions));

        return this;
    }

    public MyDiscussionsDashlet assertHistoryDropdownOptionsEqual(List<String> historyDropDownOptions)
    {
        log.info("Assert History drop down options equal: {}", historyDropDownOptions);
        clickHistoryButton();
        List<String> actualDropDownOptions = getCurrentOptions();
        assertEquals(actualDropDownOptions, historyDropDownOptions,
            String.format("History drop down options not equal %s", historyDropDownOptions));

        return this;
    }
}
