package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 12/5/2016.
 */
@PageObject
public class DeleteModelDialogPage extends ShareDialog
{
    @RenderWebElement
    private By deleteModelDialogTitle = By.id("CMM_DELETE_MODEL_DIALOG_title");

    @RenderWebElement
    private By deleteModelDialog = By.id("CMM_DELETE_MODEL_DIALOG");

    @RenderWebElement
    @FindBy (css = "#CMM_DELETE_MODEL_DIALOG .footer > span:nth-child(1) span[role='button']")
    private WebElement deleteButton;

    @RenderWebElement
    private By closeDeleteModelButton = By.cssSelector("div[id ='CMM_DELETE_MODEL_DIALOG'] span.dijitDialogCloseIcon");

    @FindBy (xpath = "//div[@id='CMM_DELETE_MODEL_DIALOG']//div[@class='dialog-body']")
    private WebElement deleteModelDialogText;

    private WebElement selectButton(String buttonName)
    {
        return getBrowser().waitUntilElementClickable
            (browser.findElement(By.xpath("//div[@id='CMM_DELETE_MODEL_DIALOG']//span[text()='" + buttonName + "']")));
    }

    public String getDeleteModelDialogText()
    {
        return deleteModelDialogText.getText();
    }

    public boolean isDeleteModelDialogDisplayed()
    {
        return browser.isElementDisplayed(deleteModelDialog);
    }

    public String getDeleteModelDialogTitle()
    {
        return browser.findElement(deleteModelDialogTitle).getText();
    }

    public void clickButton(String buttonName)
    {
        selectButton(buttonName).click();
    }

    public void clickDelete()
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
    }

    public boolean isCloseXButtonDisplayedOnDeleteModelDialog()
    {
        return browser.isElementDisplayed(closeDeleteModelButton);
    }

    public boolean isButtonDisplayed(String buttonName)
    {
        return browser.isElementDisplayed(selectButton(buttonName));
    }
}
