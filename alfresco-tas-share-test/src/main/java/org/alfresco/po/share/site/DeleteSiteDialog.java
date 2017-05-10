package org.alfresco.po.share.site;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
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

    @FindBy(css = "span[class*='default'] button")
    private Button cancel;

    @FindBy(css = "div[class='bd']")
    private WebElement deleteSiteConfirm;

    @FindBy(css = ".dialog-body")
    private WebElement deleteSiteConfirmFromSitesManager;

    @FindBy(xpath = "(//button)[1]")
    private Button yes;

    @FindBy(xpath = "(//button)[2]")
    private Button no;

    @FindBy(id = "ALF_SITE_SERVICE_DIALOG_CANCELLATION_label")
    private WebElement cancelFromSitesManager;

    private By sitesManager_actions_delete = By.id("ALF_SITE_SERVICE_DIALOG_CONFIRMATION_label");

    private By deleteSiteWindow = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']");
    public boolean isPopupDisplayed()
    {
        browser.waitUntilElementClickable(delete, 50);
        return browser.isElementDisplayed(delete);
    }

    public void clickDelete()
    {
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
        yes.click();
        browser.waitUntilElementDisappears(By.cssSelector(".bd"), 30);
        browser.waitInSeconds(5);
    }

    public void clickNo()
    {
        no.click();
    }

    /**
     * Click 'Ok' button from Delete site dialog, from Sites Manager page
     */
    public void clickDeleteFromSitesManager()
    {
        browser.waitUntilElementClickable(sitesManager_actions_delete, 50).click();
        browser.waitUntilElementDisappears(deleteSiteWindow, 5L);
    }
}