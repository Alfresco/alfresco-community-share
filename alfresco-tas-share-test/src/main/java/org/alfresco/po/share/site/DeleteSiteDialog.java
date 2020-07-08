package org.alfresco.po.share.site;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * @author Laura.Capsa
 */
@PageObject
public class DeleteSiteDialog extends ShareDialog
{
    @Autowired
    private SitesManagerPage sitesManagerPage;

    private By delete = By.cssSelector("span[class*='primary-button'] button");

    @FindBy (css = "span[class*='default'] button")
    private Button cancel;

    @FindBy (css = "div[class='bd']")
    private WebElement deleteSiteConfirm;

    @FindBy (css = "div[id='ALF_SITE_SERVICE_DIALOG'] .dialog-body")
    private WebElement deleteSiteConfirmFromSitesManager;

    @FindBy (xpath = "(//button)[1]")
    private Button yes;

    @FindBy (xpath = "(//button)[2]")
    private Button no;

    @RenderWebElement
    @FindBy (css = "div#ALF_SITE_SERVICE_DIALOG:not([style*='display: none']) #ALF_SITE_SERVICE_DIALOG_CANCELLATION_label")
    private WebElement cancelFromSitesManager;

    @RenderWebElement
    @FindBy (css = "span[widgetid='ALF_SITE_SERVICE_DIALOG_CONFIRMATION']>span")
    private WebElement confirmFromSitesManager;

    private By deleteSiteWindow = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']");

    public boolean isPopupDisplayed()
    {
        browser.waitUntilElementClickable(delete, 50);
        return browser.isElementDisplayed(delete);
    }

    public void clickDelete()
    {
        browser.waitUntilElementVisible(delete);

        browser.findElement(delete).click();
    }

    public void clickCancel()
    {
        cancel.click();
    }

    public void clickCancelFromSitesManager()
    {
        cancelFromSitesManager.click();
    }

    public String getConfirmMessage()
    {
        return deleteSiteConfirm.getText();
    }

    public String getConfirmMessageFromSitesManager()
    {
        browser.waitUntilElementVisible(deleteSiteConfirmFromSitesManager);
        return deleteSiteConfirmFromSitesManager.getText();
    }

    public void clickYes()
    {
        getBrowser().waitUntilElementVisible(yes);
        getBrowser().waitUntilElementClickable(yes).click();
        waitUntilMessageDisappears();
    }

    public void clickNo()
    {
        no.click();
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