package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 12/5/2016.
 */
@PageObject
public class DeleteModelDialogPage extends ShareDialog
{
    static final String dialogLocator ="CMM_DELETE_MODEL_DIALOG";

    @FindBy(id = "CMM_DELETE_MODEL_DIALOG_title")
    private WebElement deleteModelDialogTitle;

    @RenderWebElement
    @FindBy(id = dialogLocator)
    private WebElement deleteModelDialog;

    @RenderWebElement
    @FindBy (css = "#CMM_DELETE_MODEL_DIALOG .footer > span:nth-child(1) span[role='button']")
    private WebElement deleteButton;

    @FindBy (xpath = "//div[@id='CMM_DELETE_MODEL_DIALOG']//div[@class='dialog-body']")
    private WebElement deleteModelDialogText;

    @Autowired
    private ModelManagerPage modelManagerPage;

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
        return deleteModelDialogTitle.getText();
    }

    public ModelManagerPage clickDelete()
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        getBrowser().waitUntilElementDisappears(By.id(dialogLocator));
        modelManagerPage.refresh();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }

    public boolean isButtonDisplayed(String buttonName)
    {
        return browser.isElementDisplayed(selectButton(buttonName));
    }
}
