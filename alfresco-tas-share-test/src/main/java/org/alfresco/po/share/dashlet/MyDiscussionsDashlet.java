package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

@PageObject
public class MyDiscussionsDashlet extends Dashlet<MyDiscussionsDashlet>
{
    @FindBy (css = "div.dashlet.forumsummary td div[class$='yui-dt-liner']")
    private WebElement defaultDashletMessage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.forumsummary")
    protected WebElement dashletContainer;

    @FindBy (css = "button[id$='default-topics-button']")
    private WebElement topicsButton;

    @FindBy (css = "button[id$='default-history-button']")
    private WebElement historyButton;

    @FindAll (@FindBy (css = "div.forumsummary div.visible ul.first-of-type li a"))
    private List<WebElement> dropDownOptionsList;

    @Override
    public String getDashletTitle()
    {
        getBrowser().waitUntilElementVisible(dashletTitle);
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public MyDiscussionsDashlet assertNoTopicsMessageEquals(String expectedMessage)
    {
        LOG.info("Assert No topics message equals:{}", expectedMessage);
        assertEquals(defaultDashletMessage.getText(), expectedMessage,
            String.format("No topics message not equals %s", expectedMessage));

        return this;
    }

    public void clickOnTopicButton()
    {
        getBrowser().waitUntilElementClickable(topicsButton).click();
    }

    public void clickHistoryButton()
    {
        historyButton.click();
    }

    private List<String> getCurrentOptions()
    {
        List<String> options = new ArrayList<>();
        for (WebElement option : dropDownOptionsList)
        {
            options.add(option.getText());
        }
        return options;
    }

    public MyDiscussionsDashlet assertMyTopicsDropdownOptionsEqual(List<String> myTopicsDropDownOptions)
    {
        LOG.info("Assert My Topics drop down options equal: {}",myTopicsDropDownOptions);
        clickOnTopicButton();
        List<String> actualDropDownOptions = browser.getTextFromElementList(dropDownOptionsList);
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
