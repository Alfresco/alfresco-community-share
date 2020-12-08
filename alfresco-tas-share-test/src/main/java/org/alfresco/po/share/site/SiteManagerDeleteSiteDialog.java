package org.alfresco.po.share.site;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.testng.Assert;

public class SiteManagerDeleteSiteDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By deleteSiteConfirmFromSitesManager = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG'] .dialog-body");
    @RenderWebElement
    private final By cancelFromSitesManager = By.cssSelector("div#ALF_SITE_SERVICE_DIALOG:not([style*='display: none']) #ALF_SITE_SERVICE_DIALOG_CANCELLATION_label");
    private final By confirmFromSitesManager = By.cssSelector("span[widgetid='ALF_SITE_SERVICE_DIALOG_CONFIRMATION']>span");
    private final By deleteSiteWindow = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']");

    public SiteManagerDeleteSiteDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public SiteManagerDeleteSiteDialog assertConfirmMessageFromSiteManagerIsCorrect(String siteName)
    {
        Assert.assertEquals(getBrowser().findElement(deleteSiteConfirmFromSitesManager).getText(),
            String.format(language.translate("deleteSite.confirmFromSitesManager"), siteName));
        return this;
    }

    public void clickCancelFromSitesManager()
    {
        getBrowser().findElement(cancelFromSitesManager).click();
    }

    public void clickDeleteFromSitesManager()
    {
        getBrowser().waitUntilElementClickable(confirmFromSitesManager).click();
        getBrowser().waitUntilElementDisappearsWithRetry(deleteSiteWindow, 10);
    }
}
