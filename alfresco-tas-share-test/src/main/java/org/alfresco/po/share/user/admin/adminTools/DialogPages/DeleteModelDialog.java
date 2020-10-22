package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 12/5/2016.
 */
@PageObject
public class DeleteModelDialog extends ShareDialog
{
    @FindBy(id = "CMM_DELETE_MODEL_DIALOG_title")
    private WebElement deleteModelDialogTitle;

    @RenderWebElement
    @FindBy(id = "CMM_DELETE_MODEL_DIALOG")
    private WebElement deleteModelDialog;

    @RenderWebElement
    @FindBy (css = "#CMM_DELETE_MODEL_DIALOG .footer > span:nth-child(1) span[role='button']")
    private WebElement deleteButton;

    @RenderWebElement
    @FindBy (css = "#CMM_DELETE_MODEL_DIALOG .footer > span:nth-child(2) span[role='button']")
    private WebElement cancelButton;

    @FindBy (xpath = "//div[@id='CMM_DELETE_MODEL_DIALOG']//div[@class='dialog-body']")
    private WebElement deleteModelDialogText;

    @Autowired
    private ModelManagerPage modelManagerPage;

    public String getDeleteModelDialogText()
    {
        return deleteModelDialogText.getText();
    }

    public DeleteModelDialog assertDeleteModelDialogTextIsCorrect(String modelName)
    {
        Assert.assertEquals(deleteModelDialogText.getText(), String.format(language.translate("deleteModelDialog.text"), modelName));
        return this;
    }

    public boolean isDeleteModelDialogDisplayed()
    {
        return browser.isElementDisplayed(deleteModelDialog);
    }

    public DeleteModelDialog assertDeleteModelDialogIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(deleteModelDialog), "Delete model dialog is displayed");
        return this;
    }

    public String getDeleteModelDialogTitle()
    {
        return deleteModelDialogTitle.getText();
    }

    public ModelManagerPage clickDelete()
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        getBrowser().waitUntilElementDisappears(deleteModelDialog);
        modelManagerPage.waiUntilLoadingMessageDisappears();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }

    public DeleteModelDialog assertDeleteButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(deleteButton), "Delete button is displayed");
        return this;
    }

    public DeleteModelDialog assertCancelButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }
}
