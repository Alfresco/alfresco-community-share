package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
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
        WebElement button = waitUntilElementIsVisible(By.cssSelector(String.format(buttonSelector, buttonId)));
        clickElement(button);
    }

    public LinkedToRuleSetPage assertRelativePathEquals(String siteName)
    {
        log.info("Verify Relative Path of Page{}", "share/page/site/" + siteName + "/folder-rules");
        assertEquals(getRelativePath(), "share/page/site/" + siteName + "/folder-rules", String.format("Relative Path '"+getRelativePath()+"' not matched %s ", "share/page/site/" + siteName + "/folder-rules"));
        return this;
    }


}