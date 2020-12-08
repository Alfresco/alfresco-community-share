package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

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

    public RuleDetailsPage(ThreadLocal<WebBrowser> browser)
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
        return getBrowser().findElement(ruleTitle).getText();
    }

    public String getRuleDescription()
    {
        return getBrowser().findElement(ruleDescription).getText();
    }

    public List<String> getDetailsList()
    {
        List<WebElement> descriptionDetailsList = getBrowser().waitUntilElementsVisible(detailsSelector);
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
        return getBrowser().findElement(whenCondition).getText();
    }

    public String getIfAllCriteriaCondition()
    {
        return getBrowser().findElement(ifAllCriteriaCondition).getText();
    }

    public String getPerformAction()
    {
        return getBrowser().findElement(performAction).getText();
    }

    public List<String> getDisplayedRules()
    {
        ArrayList<String> rulesTextList = new ArrayList<>();
        List<WebElement> list = getBrowser().findElements(rulesList);
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
        getBrowser().waitUntilElementClickable(By.cssSelector(String.format(buttonSelector, buttonId))).click();
    }

    public void clickEditButton()
    {
        clickButton("edit");
        editRulesPage.renderedPage();
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
        getBrowser().findElements(runRulesOptions).get(indexOfOption).click();
    }
}