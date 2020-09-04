package org.alfresco.po.share.site;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class DeleteSiteDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "span[class*='primary-button'] button")
    private WebElement delete;

    @RenderWebElement
    @FindBy (css = "span[class*='default'] button")
    private WebElement cancel;

    @FindBy (css = "div[class='bd']")
    private WebElement deleteSiteConfirmMessage;

    @FindBy (css = ".button-group > span:nth-of-type(1) button")
    private WebElement yes;

    @FindBy (css = ".button-group > span:nth-of-type(2) button")
    private WebElement no;

    public boolean isPopupDisplayed()
    {
        browser.waitUntilElementClickable(delete, 50);
        return browser.isElementDisplayed(delete);
    }

    public DeleteSiteDialog clickDelete()
    {
        browser.waitUntilElementVisible(delete).click();
        return this;
    }

    public void clickCancel()
    {
        cancel.click();
    }

    public String getConfirmMessage()
    {
        return deleteSiteConfirmMessage.getText();
    }

    public void clickYes()
    {
        getBrowser().waitUntilElementVisible(yes).click();
        waitUntilMessageDisappears();
    }

    public void clickNo()
    {
        no.click();
    }
}