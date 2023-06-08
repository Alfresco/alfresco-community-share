package org.alfresco.po.share.site;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteManagerDeleteSiteDialog extends BaseDialogComponent
{
    private final By deleteSiteConfirmFromSitesManager = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG'] .dialog-body");
    private final By confirmFromSitesManager = By.cssSelector("span[widgetid='ALF_SITE_SERVICE_DIALOG_CONFIRMATION']>span");
    private final By deleteSiteWindow = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']");
    private final By cancel = By.xpath("//span[text()=\"Cancel\"]");

    public SiteManagerDeleteSiteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SiteManagerDeleteSiteDialog assertConfirmMessageFromSiteManagerIsCorrect(String siteName)
    {
        assertEquals(getElementText(deleteSiteConfirmFromSitesManager),
            String.format(language.translate("deleteSite.confirmFromSitesManager"), siteName));
        return this;
    }

    public SitesManagerPage clickDeleteFromSitesManager()
    {
        WebElement confirmButton = waitUntilElementIsVisible(confirmFromSitesManager);
        mouseOver(confirmButton);
        clickElement(confirmButton);
        if(isElementDisplayed(deleteSiteWindow))
        {
            log.warn("Retry click confirm delete button");
            clickElement(confirmButton);
        }
        waitUntilElementDisappearsWithRetry(deleteSiteWindow, WAIT_10.getValue());
        SitesManagerPage sitesManagerPage = new SitesManagerPage(webDriver);
        sitesManagerPage.waitForSitesTableHeaderToBeVisible();

        return sitesManagerPage;
    }
    public void clickCancelFromSitesManager() {
        clickElement(cancel);
    }
}
