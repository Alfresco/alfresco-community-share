package org.alfresco.po.share.site;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class SiteManagerDeleteSiteDialog extends ShareDialog
{
    @Autowired
    private SitesManagerPage sitesManagerPage;

    @RenderWebElement
    @FindBy(css = "div[id='ALF_SITE_SERVICE_DIALOG'] .dialog-body")
    private WebElement deleteSiteConfirmFromSitesManager;

    @RenderWebElement
    @FindBy (css = "div#ALF_SITE_SERVICE_DIALOG:not([style*='display: none']) #ALF_SITE_SERVICE_DIALOG_CANCELLATION_label")
    private WebElement cancelFromSitesManager;

    @FindBy (css = "span[widgetid='ALF_SITE_SERVICE_DIALOG_CONFIRMATION']>span")
    private WebElement confirmFromSitesManager;

    private By deleteSiteWindow = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']");

    public SiteManagerDeleteSiteDialog assertConfirmMessageFromSiteManagerIsCorrect(String siteName)
    {
        Assert.assertEquals(deleteSiteConfirmFromSitesManager.getText(),
            String.format(language.translate("deleteSite.confirmFromSitesManager"), siteName));
        return this;
    }

    public SitesManagerPage clickCancelFromSitesManager()
    {
        cancelFromSitesManager.click();
        return (SitesManagerPage) sitesManagerPage.renderedPage();
    }

    /**
     * Click 'Ok' button from Delete site dialog, from Sites Manager page
     */
    public SitesManagerPage clickDeleteFromSitesManager()
    {
        browser.waitUntilElementClickable(confirmFromSitesManager).click();
        browser.waitUntilElementDisappearsWithRetry(deleteSiteWindow, 10);
        return (SitesManagerPage) sitesManagerPage.renderedPage();
    }
}
