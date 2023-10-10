package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public abstract class DashletPopUp<T> extends BasePage
{
    private final By closeButton = By.cssSelector("a.container-close");
    private final By okButton = By.cssSelector("button[id$='configDialog-ok-button']");
    private final By cancelButton = By.cssSelector("button[id$='configDialog-cancel-button']");
    private final By dialogTitle = By.cssSelector("div[id$='configDialog_h']");
    private final By dialogContainer = By.cssSelector("div[id$='configDialog_c']");

    protected DashletPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void clickOk()
    {
        log.info("Click OK from dialog");
        clickElement(okButton);
        waitUntilElementDisappears(dialogContainer);
    }

    public void clickOkButtonSimple()
    {
        clickElement(okButton);
    }

    public T assertOKButtonIsDisplayed()
    {
        waitUntilElementIsVisible(okButton);
        assertTrue(isElementDisplayed(okButton), "Ok button is displayed");
        return (T) this;
    }

    public void clickCancelButton()
    {
        clickElement(cancelButton);
    }

    public T assertCancelButtonIsDisplayed()
    {
        waitUntilElementIsVisible(cancelButton);
        assertTrue(isElementDisplayed(cancelButton), "Cancel button is displayed");
        return (T) this;
    }

    public void clickClose()
    {
        clickElement(closeButton);
    }

    public T assertCloseButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(closeButton), "Close button is not displayed");
        return (T) this;
    }

    public T assertDialogTitleEquals(String expectedDialogTitle)
    {
        log.info("Assert dialog title equals: {}", expectedDialogTitle);
        assertEquals(getElementText(dialogTitle), expectedDialogTitle,
            String.format("Dialog title not equals %s ", expectedDialogTitle));

        return (T) this;
    }
}
