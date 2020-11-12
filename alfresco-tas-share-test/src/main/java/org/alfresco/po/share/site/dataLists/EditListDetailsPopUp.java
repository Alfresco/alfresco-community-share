package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class EditListDetailsPopUp extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "input[id$='editList_prop_cm_title']")
    protected WebElement titleField;
    @RenderWebElement
    @FindBy (css = "textarea[id$='editList_prop_cm_description']")
    protected WebElement descriptionField;
    @FindBy (css = "button[id$='editList-form-submit-button']")
    protected WebElement saveButton;
    @FindBy (css = "button[id$='editList-form-cancel-button']")
    protected WebElement cancelButton;
    @FindBy (css = "div[id$='editList-dialog'] a")
    protected WebElement closeButton;
    //@Autowired
    DataListsPage dataListsPage;

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

    public DataListsPage clickSaveButton()
    {
        saveButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public DataListsPage clickCancelButton()
    {
        cancelButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public DataListsPage clickCloseButton()
    {
        closeButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
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
