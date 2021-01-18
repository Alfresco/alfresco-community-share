package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class EditListDetailsPopUp extends ShareDialog
{
    @FindBy (css = "input[id$='editList_prop_cm_title']")
    protected WebElement titleField;
    @FindBy (css = "textarea[id$='editList_prop_cm_description']")
    protected WebElement descriptionField;
    @FindBy (css = "button[id$='editList-form-submit-button']")
    protected WebElement saveButton;
    @FindBy (css = "button[id$='editList-form-cancel-button']")
    protected WebElement cancelButton;
    @FindBy (css = "div[id$='editList-dialog'] a")
    protected WebElement closeButton;

    public void modifyTitle(String newTitle)
    {
        titleField.clear();
        titleField.sendKeys(newTitle);
    }

    public void modifyDescription(String newDescription)
    {
        descriptionField.clear();
        descriptionField.sendKeys(newDescription);
    }

    public void clickSaveButton()
    {
        saveButton.click();
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    public void clickCloseButton()
    {
        closeButton.click();
    }

    public boolean isSaveButtonDisplayed()
    {
        return browser.isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public boolean isCloseButtonDisplayed()
    {
        return browser.isElementDisplayed(closeButton);
    }

}
