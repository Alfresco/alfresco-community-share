package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertEquals;

@Slf4j public class RuleDetailsPage extends SiteCommon<RuleDetailsPage>
{
    private EditRulesPage editRulesPage;

    private final By ruleTitle = By.cssSelector(".rule-details [id*='title']");
    private final By ruleDescription = By.cssSelector("span[id*='description']");
    private final By detailsSelector = By
        .cssSelector("div[id*='default-display'] div[class*='behaviour']");
    private final By whenCondition = By.cssSelector("ul[id*='ruleConfigType'] .name span");
    private final By ifAllCriteriaCondition = By
        .cssSelector("ul[id*='ruleConfigIfCondition'] .name span");
    private final By performAction = By
        .xpath(".//ul[contains(@id, 'ruleConfigAction')]//div[@class='parameters']");
    private final By rulesList = By.cssSelector(".rules-list-container .title");
    private final By runRulesOptions = By.cssSelector(".rules-actions .yuimenuitemlabel");
    private final String buttonSelector = "button[id*='%s']";
    private final By deleteButton = By.xpath("//button[text()=\"Delete\"]");

    public RuleDetailsPage(ThreadLocal<WebDriver> browser)
    {
        super(browser);
        editRulesPage = new EditRulesPage(browser);
    }

    @Override public String getRelativePath()
    {
        return String.format("share/page/site/%s/rule-edit", getCurrentSiteName());
    }

    public String getRuleTitle()
    {
        return getElementText(ruleTitle);
    }

    public String getRuleDescription()
    {
        return getElementText(ruleDescription);
    }

    public List<String> getDetailsList()
    {
        List<WebElement> descriptionDetailsList = waitUntilElementsAreVisible(detailsSelector);
        ArrayList<String> descriptionDetailsText = new ArrayList<>();
        if (!descriptionDetailsList.isEmpty())
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
        return findElement(whenCondition).getText();
    }

    public String getIfAllCriteriaCondition()
    {
        return findElement(ifAllCriteriaCondition).getText();
    }

    public String getPerformAction()
    {
        return findElement(performAction).getText();
    }

    public List<String> getDisplayedRules()
    {
        ArrayList<String> rulesTextList = new ArrayList<>();
        List<WebElement> list = findElements(rulesList);
        if (!list.isEmpty())
            for (WebElement aRulesList : list)
            {
                rulesTextList.add(aRulesList.getText());
            }
        else
        {
            rulesTextList.add("No rules displayed!");
        }

        return rulesTextList;
    }

    public void clickButton(String buttonId)
    {
        clickElement(By.cssSelector(String.format(buttonSelector, buttonId)));
    }

    public void clickEditButton()
    {
        clickButton("edit");
    }

    public void clickUnlinkButton()
    {
        clickButton("unlink");
    }

    public void clickRunRulesButton()
    {
        clickButton("runRules");
    }

    public void clickOnRunRulesOption(int indexOfOption)
    {
        findElements(runRulesOptions).get(indexOfOption);
    }

    public RuleDetailsPage assertRulesPageTitleEquals(String updatedRuleName)
    {
        log.info("Verify Rules Details Page Title");
        assertEquals(getRuleTitle(), updatedRuleName,
            String.format("Rule Details Page Title not match %s ", updatedRuleName));
        return this;
    }

    public RuleDetailsPage assertRuleDescriptionEquals(String updatedDescription)
    {
        log.info("Verify Rule Description");
        assertEquals(getRuleDescription(), updatedDescription,
            String.format("Rule Description not match %s ", updatedDescription));
        return this;
    }

    public RuleDetailsPage assertRuleDetailsListEquals(String expectedDescriptionDetails)
    {
        log.info("Verify Rule Details List");
        assertEquals(getDetailsList().toString(), expectedDescriptionDetails,
            String.format("Description List not match %s ", expectedDescriptionDetails));
        return this;
    }

    public RuleDetailsPage assertWhenConditionEquals(String selectedOptionFromDropdown)
    {
        log.info("Verify When Condition from the Dropdown");
        assertEquals(getWhenCondition(), selectedOptionFromDropdown,
            String.format("When section is not met - Section %s ", selectedOptionFromDropdown));
        return this;
    }

    public RuleDetailsPage assertIfAllCriteriaConditionEquals(String selectedOptionFromDropdown)
    {
        log.info("Verify If all criteria condition");
        assertEquals(getIfAllCriteriaCondition(), selectedOptionFromDropdown,
            String.format("If all Criteria is not met - Section %s ", selectedOptionFromDropdown));
        return this;
    }

    public RuleDetailsPage assertPerformedActionEquals(String expectedAction)
    {
        log.info("Verify Performed Action");
        assertEquals(getPerformAction(), expectedAction,
            String.format("Performed Action not equal %s ", expectedAction));
        return this;
    }
}