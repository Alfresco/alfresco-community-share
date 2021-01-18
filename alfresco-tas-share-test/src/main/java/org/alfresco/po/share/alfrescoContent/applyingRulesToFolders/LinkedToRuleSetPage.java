package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LinkedToRuleSetPage extends SiteCommon<LinkedToRuleSetPage>
{
    private String buttonSelector = "button[id*='%s']";

    public LinkedToRuleSetPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/folder-rules", getCurrentSiteName());
    }

    public void clickButton(String buttonId)
    {
        WebElement button = webElementInteraction.waitUntilElementIsVisible(By.cssSelector(String.format(buttonSelector, buttonId)));
        webElementInteraction.clickElement(button);
    }
}