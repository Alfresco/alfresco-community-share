package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.GroupsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

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

    public MyDiscussionsDashlet assertNoTopicsMessageIsDisplayed()
    {
        LOG.info("Assert No topics message is displayed");
        Assert.assertEquals(defaultDashletMessage.getText(), language.translate("myDiscussionDashlet.noTopics"));
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

    public MyDiscussionsDashlet assertTopicDropdownHasAllOptions()
    {
        LOG.info("Assert all options from topics filter are displayed");
        clickOnTopicButton();
        List<String> currentOptions = browser.getTextFromElementList(dropDownOptionsList);
        List<String> expectedValues = Arrays.asList(language.translate("myDiscussionDashlet.myTopics"),
            language.translate("myDiscussionDashlet.allTopics"));
        Assert.assertTrue(currentOptions.equals(expectedValues), "All options are found");
        return this;
    }

    public MyDiscussionsDashlet assertHistoryDropdownHasAllOptions()
    {
        LOG.info("Assert all options from history filter are displayed");
        clickHistoryButton();
        List<String> expectedValues = Arrays.asList(
            language.translate("myDiscussionDashlet.lastDay"),
            language.translate("myDiscussionDashlet.last7Days"),
            language.translate("myDiscussionDashlet.last14Days"),
            language.translate("myDiscussionDashlet.last28Days"));
        List<String> currentOptions = getCurrentOptions();
        Assert.assertTrue(currentOptions.equals(expectedValues), "All options are found");
        return this;
    }
}
