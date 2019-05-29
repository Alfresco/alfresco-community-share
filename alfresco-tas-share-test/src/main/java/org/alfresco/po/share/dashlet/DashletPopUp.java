package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TextBlock;

public abstract class DashletPopUp extends HtmlPage
{
    @RenderWebElement
    @FindBy (css = "a.container-close")
    protected Button closeButton;

    @RenderWebElement
    @FindBy (css = "button[id$='configDialog-ok-button']")
    protected WebElement okButton;

    @RenderWebElement
    @FindBy (css = "button[id$='configDialog-cancel-button']")
    protected Button cancelButton;

    @FindBy (css = "a.container-close")
    protected TextBlock title;

    @RenderWebElement
    @FindBy (css = "div[id$='configDialog_h']")
    protected HtmlElement popUpTitleField;

    protected By dialogContainer = By.cssSelector("div[id$='configDialog_c']");

    public String getTitle()
    {
        return title.getText();
    }

    public void clickOkButton()
    {
        browser.waitUntilElementVisible(okButton);
        browser.waitUntilElementClickable(okButton).click();
        try
        {
            int counter = 0;
            while (browser.isElementDisplayed(dialogContainer) && counter < 10)
            {
                System.out.println("Dialog is visible. Counter: " + counter);
                okButton.click();
                browser.waitInSeconds(5);
                counter++;
            }
        } catch (NoSuchElementException nse)
        {
            nse.printStackTrace(); // print but simulate .ignoring() by catching exception
        }
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
}
