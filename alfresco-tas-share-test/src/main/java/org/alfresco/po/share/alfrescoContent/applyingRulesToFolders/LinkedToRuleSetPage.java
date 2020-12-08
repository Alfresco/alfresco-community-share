package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LinkedToRuleSetPage extends SiteCommon<LinkedToRuleSetPage>
{
    private String buttonSelector = "button[id*='%s']";

    public LinkedToRuleSetPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/folder-rules", getCurrentSiteName());
    }

    public void clickButton(String buttonId)
    {
        WebElement button = getBrowser().waitUntilElementClickable(By.cssSelector(String.format(buttonSelector, buttonId)), 40);
        button.click();
    }
}