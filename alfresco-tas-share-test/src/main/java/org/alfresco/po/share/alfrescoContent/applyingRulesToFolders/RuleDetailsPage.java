package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import static org.alfresco.common.Wait.WAIT_1;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class RuleDetailsPage extends SiteCommon<RuleDetailsPage>
{
    private final By ruleTitle = By.cssSelector(".rule-details [id*='title']");
    private final By ruleDescription = By.cssSelector("span[id*='description']");
    private final By detailsSelector = By.cssSelector("div[id*='default-display'] div[class*='behaviour']");
    private final By whenCondition = By.cssSelector("ul[id*='ruleConfigType'] .name span");
    private final By ifAllCriteriaCondition = By.cssSelector("ul[id*='ruleConfigIfCondition'] .name span");
    private final By performAction = By.xpath(".//ul[contains(@id, 'ruleConfigAction')]//div[@class='parameters']");
    private final By rulesList = By.cssSelector(".rules-list-container .title");
    private final By runRulesOptions = By.cssSelector(".rules-actions .yuimenuitemlabel");
    private final String buttonSelector = "button[id*='%s']";
    private final By rulesSuccessfullMessage = By.cssSelector("#message .bd");

    public RuleDetailsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override public String getRelativePath()
    {
        return String.format("share/page/site/%s/rule-edit", getCurrentSiteName());
    }

    public String getRuleTitle()
    {
        return getElementText(ruleTitle);
    }

    public List<String> getDetailsList()
    {
        List<WebElement> descriptionDetailsList = waitUntilElementsAreVisible(detailsSelector);
        ArrayList<String> descriptionDetails = new ArrayList<>();

        if (!descriptionDetailsList.isEmpty())
        {
            for (WebElement descriptionDetail : descriptionDetailsList)
            {
                descriptionDetails.add(descriptionDetail.getText());
            }
        }
        else
        {
            descriptionDetails.add("'Description' details is empty!");
        }
        return descriptionDetails;
    }

    public String getWhenCondition()
    {
        return getElementText(whenCondition);
    }

    public String getIfAllCriteriaCondition()
    {
        return getElementText(ifAllCriteriaCondition);
    }

    public String getPerformAction()
    {
        return findElement(performAction).getText();
    }

    public List<String> getDisplayedRules()
    {
        ArrayList<String> rulesTextList = new ArrayList<>();
        List<WebElement> list = waitUntilElementsAreVisible(rulesList);

        if (!list.isEmpty())
        {
            for (WebElement aRulesList : list)
            {
                rulesTextList.add(aRulesList.getText());
            }
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
        waitInSeconds(WAIT_1.getValue());
    }

    public void openEditRuleForm()
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
        findElements(runRulesOptions).get(indexOfOption).click();
        waitUntilElementIsVisible(rulesSuccessfullMessage);
        waitUntilElementDisappears(rulesSuccessfullMessage);
    }

    public RuleDetailsPage assertRulesPageTitleEquals(String updatedRuleName)
    {
        log.info("Verify Rules Details Page Title");
        assertEquals(getRuleTitle(), updatedRuleName,
            String.format("Rule Details Page Title not match %s ", updatedRuleName));
        return this;
    }

    public RuleDetailsPage assertRuleDescriptionEquals(String expectedDescription)
    {
        log.info("Verify Rule Description {}", expectedDescription);
        String actualDescription = getElementText(ruleDescription);

        assertEquals(actualDescription, expectedDescription,
            String.format("Rule Description not match %s ", expectedDescription));
        return this;
    }

    public RuleDetailsPage assertRuleDescriptionDetailsEqualTo(List<String> expectedList)
    {
        log.info("Assert rule description detail equals {}", expectedList);
        assertEquals(getDetailsList().toString(), expectedList.toString(),
            String.format("Description List not match %s ", expectedList));

        return this;
    }

    public RuleDetailsPage assertWhenConditionTextEquals(String expectedText)
    {
        log.info("Verify When Condition from the Dropdown");
        assertEquals(getWhenCondition(), expectedText,
            String.format("When section is not met - Section %s ", expectedText));
        return this;
    }

    public RuleDetailsPage assertIfAllCriteriaConditionEquals(String expectedText)
    {
        log.info("Verify If all criteria condition");
        assertEquals(getIfAllCriteriaCondition(), expectedText,
            String.format("If all Criteria is not met - Section %s ", expectedText));
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