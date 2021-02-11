package org.alfresco.po.share.site;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteManagerDeleteSiteDialog extends BaseDialogComponent
{
    private final By deleteSiteConfirmFromSitesManager = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG'] .dialog-body");
    private final By cancelFromSitesManager = By.cssSelector("div#ALF_SITE_SERVICE_DIALOG:not([style*='display: none']) #ALF_SITE_SERVICE_DIALOG_CANCELLATION_label");
    private final By confirmFromSitesManager = By.cssSelector("span[widgetid='ALF_SITE_SERVICE_DIALOG_CONFIRMATION']>span");
    private final By deleteSiteWindow = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']");

    public SiteManagerDeleteSiteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SiteManagerDeleteSiteDialog assertConfirmMessageFromSiteManagerIsCorrect(String siteName)
    {
        assertEquals(webElementInteraction.getElementText(deleteSiteConfirmFromSitesManager),
            String.format(language.translate("deleteSite.confirmFromSitesManager"), siteName));
        return this;
    }

    public void clickCancelFromSitesManager()
    {
        webElementInteraction.clickElement(cancelFromSitesManager);
    }

    public void clickDeleteFromSitesManager()
    {
        WebElement confirmButton = webElementInteraction.waitUntilElementIsVisible(confirmFromSitesManager);
        webElementInteraction.mouseOver(confirmButton);
        webElementInteraction.clickElement(confirmButton);
        if(webElementInteraction.isElementDisplayed(deleteSiteWindow))
        {
            log.warn("Retry click confirm delete button");
            webElementInteraction.clickElement(confirmButton);
        }
        webElementInteraction.waitUntilElementDisappearsWithRetry(deleteSiteWindow, 10);
    }
}
