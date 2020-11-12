package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

public class LinkedToRuleSetPage extends SiteCommon<LinkedToRuleSetPage>
{
    private String buttonSelector = "button[id*='%s']";

    public LinkedToRuleSetPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
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