package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class RuleDetailsPage extends SiteCommon<RuleDetailsPage>
{
    @Autowired
    EditRulesPage editRulesPage;

    @FindBy (css = ".rule-details [id*='title']")
    private WebElement ruleTitle;

    @FindBy (css = "span[id*='description']")
    private WebElement ruleDescription;

    private By detailsSelector = By.cssSelector("div[id*='default-display'] div[class*='behaviour']");

    //@RenderWebElement
    @FindBy (css = "ul[id*='ruleConfigType'] .name span")
    private WebElement whenCondition;

    @RenderWebElement
    @FindBy (css = "ul[id*='ruleConfigIfCondition'] .name span")
    private WebElement ifAllCriteriaCondition;

    @FindBy (xpath = ".//ul[contains(@id, 'ruleConfigAction')]//div[@class='parameters']")
    private WebElement performAction;

    @FindBy (css = ".rules-list-container .title")
    private List<WebElement> rulesList;

    @FindBy (css = ".rules-actions .yuimenuitemlabel")
    private List<WebElement> runRulesOptions;

    private String buttonSelector = "button[id*='%s']";

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/rule-edit", getCurrentSiteName());
    }

    public String getRuleTitle()
    {
        if (browser.isElementDisplayed(ruleTitle))
            return ruleTitle.getText();
        return "isn't displayed!";
    }

    public String getRuleDescription()
    {
        if (browser.isElementDisplayed(ruleDescription))
            return ruleDescription.getText();
        return "isn't displayed!";
    }

    public ArrayList<String> getDetailsList()
    {
        List<WebElement> descriptionDetailsList = browser.waitUntilElementsVisible(detailsSelector);
        ArrayList<String> descriptionDetailsText = new ArrayList<>();
        if (descriptionDetailsList.size() > 0)
            for (WebElement aDescriptionDetailsList : descriptionDetailsList)
            {
                descriptionDetailsText.add(aDescriptionDetailsList.getText());
            }
        else
            descriptionDetailsText.add("'Description' details is empty!");
        return descriptionDetailsText;
    }

    public String getWhenCondition()
    {
        if (browser.isElementDisplayed(whenCondition))
            return whenCondition.getText();
        return "isn't displayed!";
    }

    public String getIfAllCriteriaCondition()
    {
        if (browser.isElementDisplayed(ifAllCriteriaCondition))
            return ifAllCriteriaCondition.getText();
        return "isn't displayed!";
    }

    public String getPerformAction()
    {
        if (browser.isElementDisplayed(performAction))
            return performAction.getText();
        return "isn't displayed!";
    }

    public ArrayList<String> getDisplayedRules()
    {
        ArrayList<String> rulesTextList = new ArrayList<>();
        if (rulesList.size() > 0)
            for (WebElement aRulesList : rulesList)
            {
                rulesTextList.add(aRulesList.getText());
            }
        else
            rulesTextList.add("No rules displayed!");
        return rulesTextList;
    }

    /**
     * Click on any button from page
     *
     * @param buttonId id used in selector to find the button, can be: edit, delete, view, change, unlink, inheritButton, runRules, newRule, done
     */
    public void clickButton(String buttonId)
    {
        browser.waitUntilElementClickable(By.cssSelector(String.format(buttonSelector, buttonId)), properties.getExplicitWait()).click();
    }

    public void clickEditButton()
    {
        clickButton("edit");
        editRulesPage.renderedPage();
    }

    public void clickUnlinkButton()
    {
        clickButton("unlink");
        browser.refresh();
        browser.waitInSeconds(3);
    }

    public void clickRunRulesButton()
    {
        clickButton("runRules");
    }

    /**
     * Click on any option from "Run Rules..." menu
     *
     * @param indexOfOption 0 = Run rules for this folder; 1 = Run rules for this folder and its subfolders
     */
    public void clickOnRunRulesOption(int indexOfOption)
    {
        runRulesOptions.get(indexOfOption).click();
    }
}