package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class LinkedToRuleSetPage extends SiteCommon<LinkedToRuleSetPage>
{
    @Autowired
    RuleDetailsPage ruleDetailsPage;

    private String buttonSelector = "button[id*='%s']";

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/folder-rules", getCurrentSiteName());
    }

    /**
     * Click on any button from page
     * 
     * @param buttonId used in finding the button, values: done, view, change, unlink
     */
    public void clickButton(String buttonId)
    {
        WebElement button = browser.waitUntilElementClickable(By.cssSelector(String.format(buttonSelector, buttonId)), 40);
        button.click();
    }
}