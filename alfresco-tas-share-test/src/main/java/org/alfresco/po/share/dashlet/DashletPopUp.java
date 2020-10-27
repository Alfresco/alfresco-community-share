package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.common.Language;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DashletPopUp<T> extends HtmlPage
{
    @Autowired
    protected Language language;

    @RenderWebElement
    @FindBy (css = "a.container-close")
    protected WebElement closeButton;

    @RenderWebElement
    @FindBy (css = "button[id$='configDialog-ok-button']")
    protected WebElement okButton;

    @RenderWebElement
    @FindBy (css = "button[id$='configDialog-cancel-button']")
    protected WebElement cancelButton;

    @FindBy (css = "a.container-close")
    protected WebElement title;

    @RenderWebElement
    @FindBy (css = "div[id$='configDialog_h']")
    protected WebElement dialogTitle;

    protected By dialogContainer = By.cssSelector("div[id$='configDialog_c']");

    public String getTitle()
    {
        return title.getText();
    }

    public void clickOk()
    {
        LOG.info("Click OK from dialog");
        browser.waitUntilElementVisible(okButton).click();
        browser.waitUntilElementDisappears(dialogContainer);
    }

    /**
     * Click on popup "OK" button.
     * To be used when no need to redirect to a page.
     */
    public void clickOkButtonSimple()
    {
        okButton.click();
    }

    public boolean isOkButtonDisplayed()
    {
        return okButton.isDisplayed();
    }

    public T assertOKButtonIsDisplayed()
    {
        assertTrue(browser.isElementDisplayed(okButton), "Ok button is displayed");
        return (T) this;
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    public T assertCancelButtonIsDisplayed()
    {
        assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return (T) this;
    }

    public boolean isCancelButtonDisplayed()
    {
        return cancelButton.isDisplayed();
    }

    public void clickClose()
    {
        closeButton.click();
    }

    public boolean isCloseButtonDisplayed()
    {
        return closeButton.isDisplayed();
    }

    public T assertCloseButtonIsDisplayed()
    {
        assertTrue(browser.isElementDisplayed(closeButton), "Close button is not displayed");
        return (T) this;
    }

    public T assertDialogTitleEquals(String expectedDialogTitle)
    {
        LOG.info("Assert dialog title equals: {}", expectedDialogTitle);
        assertEquals(dialogTitle.getText(), expectedDialogTitle,
            String.format("Dialog title not equals %s ", expectedDialogTitle));

        return (T) this;
    }
}
