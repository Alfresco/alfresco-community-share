package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RuleDetailsPage extends SiteCommon<RuleDetailsPage>
{
    private EditRulesPage editRulesPage;

    private final By ruleTitle = By.cssSelector(".rule-details [id*='title']");
    private final By ruleDescription = By.cssSelector("span[id*='description']");
    private final By detailsSelector = By.cssSelector("div[id*='default-display'] div[class*='behaviour']");
    private final By whenCondition = By.cssSelector("ul[id*='ruleConfigType'] .name span");
    @RenderWebElement
    private final By ifAllCriteriaCondition = By.cssSelector("ul[id*='ruleConfigIfCondition'] .name span");
    private final By performAction = By.cssSelector(".//ul[contains(@id, 'ruleConfigAction')]//div[@class='parameters']");
    private final By rulesList = By.cssSelector(".rules-list-container .title");
    private final By runRulesOptions = By.cssSelector(".rules-actions .yuimenuitemlabel");
    private final String buttonSelector = "button[id*='%s']";

    public RuleDetailsPage(ThreadLocal<WebDriver> browser)
    {
        super(browser);
        editRulesPage = new EditRulesPage(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/rule-edit", getCurrentSiteName());
    }

    public String getRuleTitle()
    {
        return webElementInteraction.getElementText(ruleTitle);
    }

    public String getRuleDescription()
    {
        return webElementInteraction.getElementText(ruleDescription);
    }

    public List<String> getDetailsList()
    {
        List<WebElement> descriptionDetailsList = webElementInteraction.waitUntilElementsAreVisible(detailsSelector);
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
        return webElementInteraction.findElement(whenCondition).getText();
    }

    public String getIfAllCriteriaCondition()
    {
        return webElementInteraction.findElement(ifAllCriteriaCondition).getText();
    }

    public String getPerformAction()
    {
        return webElementInteraction.findElement(performAction).getText();
    }

    public List<String> getDisplayedRules()
    {
        ArrayList<String> rulesTextList = new ArrayList<>();
        List<WebElement> list = webElementInteraction.findElements(rulesList);
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
        webElementInteraction.clickElement(By.cssSelector(String.format(buttonSelector, buttonId)));
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
        webElementInteraction.findElements(runRulesOptions).get(indexOfOption);
    }
}