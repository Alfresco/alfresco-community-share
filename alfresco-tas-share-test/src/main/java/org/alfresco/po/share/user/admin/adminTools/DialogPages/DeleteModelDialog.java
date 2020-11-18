package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DeleteModelDialog extends ShareDialog2
{
    private final By deleteModelDialogTitle = By.id("CMM_DELETE_MODEL_DIALOG_title");
    @RenderWebElement
    private final By deleteModelDialog = By.id("CMM_DELETE_MODEL_DIALOG");
    @RenderWebElement
    private final By deleteButton = By.cssSelector("#CMM_DELETE_MODEL_DIALOG .footer > span:nth-child(1) span[role='button']");
    @RenderWebElement
    private final By cancelButton = By.cssSelector("#CMM_DELETE_MODEL_DIALOG .footer > span:nth-child(2) span[role='button']");
    private final By deleteModelDialogText = By.xpath("//div[@id='CMM_DELETE_MODEL_DIALOG']//div[@class='dialog-body']");

    public DeleteModelDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public String getDeleteModelDialogText()
    {
        return getElementText(deleteModelDialogText);
    }

    public DeleteModelDialog assertDeleteModelDialogTextIsCorrect(String modelName)
    {
        assertEquals(getDeleteModelDialogText(), String.format(language.translate("deleteModelDialog.text"), modelName));
        return this;
    }

    public boolean isDeleteModelDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(deleteModelDialog);
    }

    public DeleteModelDialog assertDeleteModelDialogIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(deleteModelDialog), "Delete model dialog is displayed");
        return this;
    }

    public String getDeleteModelDialogTitle()
    {
        return getElementText(deleteModelDialogTitle);
    }

    public ModelManagerPage clickDelete()
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        getBrowser().waitUntilElementDisappears(deleteModelDialog);
        ModelManagerPage modelManagerPage = new ModelManagerPage(browser);
        modelManagerPage.waiUntilLoadingMessageDisappears();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }

    public DeleteModelDialog assertDeleteButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(deleteButton), "Delete button is displayed");
        return this;
    }

    public DeleteModelDialog assertCancelButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }
}
