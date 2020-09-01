package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TextBlock;

public abstract class DashletPopUp extends HtmlPage
{
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
    protected WebElement popUpTitleField;

    protected By dialogContainer = By.cssSelector("div[id$='configDialog_c']");

    public String getTitle()
    {
        return title.getText();
    }

    public void clickOkButton()
    {
        browser.waitUntilElementVisible(okButton);
        browser.waitUntilElementClickable(okButton).click();
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

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    public boolean isCancelButtonDisplayed()
    {
        return cancelButton.isDisplayed();
    }

    public void clickCloseButton()
    {
        closeButton.isDisplayed();
        closeButton.click();
    }

    public boolean isCloseButtonDisplayed()
    {
        return closeButton.isDisplayed();
    }

    public String getPopUpTitle()
    {
        return popUpTitleField.getText();
    }

    public void assertPopUpTitleIs(String expectedTitle)
    {
        Assert.assertEquals(popUpTitleField.getText(), expectedTitle, "Popup title is correct");
    }
}
