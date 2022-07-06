package org.alfresco.po.share.site;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class DeleteSiteDialog extends BaseDialogComponent
{
    private final By delete = By.cssSelector("span[class*='primary-button'] button");
    private final By cancel = By.cssSelector("span[class*='default'] button");
    private final By deleteSiteConfirmMessage = By.cssSelector("div[class='bd']");
    private final By yes = By.cssSelector(".button-group > span:nth-of-type(1) button");
    private final By no = By.cssSelector(".button-group > span:nth-of-type(2) button");

    public DeleteSiteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isPopupDisplayed()
    {
        waitUntilElementIsVisible(delete);
        return isElementDisplayed(delete);
    }

    public DeleteSiteDialog clickDelete()
    {
        clickElement(delete);
        return this;
    }

    public void clickCancel()
    {
        clickElement(cancel);
    }

    public String getConfirmMessage()
    {
        return getElementText(deleteSiteConfirmMessage);
    }

    public void clickYes()
    {
        clickElement(yes);
        waitUntilNotificationMessageDisappears();
        if(isElementDisplayed(notificationMessageLocator))
        {
            log.info("Wait for the second message");
            waitUntilNotificationMessageDisappears();
        }
    }

    public void clickNo()
    {
        clickElement(no);
    }
}