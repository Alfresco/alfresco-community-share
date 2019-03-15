package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PageObject
public class MyDiscussionsDashlet extends Dashlet<MyDiscussionsDashlet>
{
    @RenderWebElement
    @FindBy(css = "div.dashlet.forumsummary")
    protected HtmlElement dashletContainer;
    
    @FindBy(css = "div.dashlet.forumsummary td div[class$='yui-dt-liner']")
    protected static HtmlElement defaultDashletMessage;
    
    @FindBy(css = "button[id$='default-topics-button']")
    private Button topicsButton;
    
    @FindBy(css = "button[id$='default-history-button']")
    private Button historyButton;
    
    @FindAll(@FindBy(css = "div.forumsummary div.visible ul.first-of-type li a"))
    private List<WebElement> dropDownOptionsList;
    
    @Override
    public String getDashletTitle()
    {
        getBrowser().waitUntilElementVisible(dashletTitle);
        return dashletContainer.findElement(dashletTitle).getText();
    }
    
    /**
     * Retrieves the default dashlet message.
     * 
     * @return String
     */
    public String getDefaultMessage()
    {
        return defaultDashletMessage.getText();
    }
    
    public void clickOnTopicButton()
    {
        getBrowser().waitUntilElementClickable(topicsButton);
        topicsButton.click();
    }
    
    public void clickHistoryButton()
    {
        historyButton.click();
    }
    
    private List<String> getCurrentOptions()
    {
        List<String> options = new ArrayList<>();
        for(WebElement option: dropDownOptionsList)
        {
            options.add(option.getText());
        }
        
        return options;
    }
    
    public Boolean checkTopicDropdownOptions()
    {
        clickOnTopicButton();
        List<String> currentOptions = getCurrentOptions();
        List<String> expectedValues = Arrays.asList("My Topics", "All Topics");
        if (currentOptions.size() != expectedValues.size())
        {
            return false;
        }
        for (String option : currentOptions)
        {
            if(!expectedValues.contains(option))
            {
                return false;
            }
        }
        return true;
    }
    
    public Boolean checkHistoryDropdownOptions()
    {
        clickHistoryButton();
        List<String> expectedValues = Arrays.asList("Topics updated in the last day", "Topics updated in the last 7 days", "Topics updated in the last 14 days", "Topics updated in the last 28 days");
        List<String> currentOptions = getCurrentOptions();
        if (currentOptions.size() != expectedValues.size())
        {
            return false;
        }
        for (String option : currentOptions)
        {
            if(!expectedValues.contains(option))
            {
                return false;
            }
        }
        return true;
    }
}
