package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditListDetailsPopUp extends BaseDialogComponent
{
    private final By titleField = By.cssSelector("input[id$='editList_prop_cm_title']");
    private final By descriptionField = By.cssSelector("textarea[id$='editList_prop_cm_description']");
    private final By saveButton = By.cssSelector("button[id$='editList-form-submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='editList-form-cancel-button']");
    private final By closeButton = By.cssSelector("div[id$='editList-dialog'] a");

    protected EditListDetailsPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void modifyTitle(String newTitle)
    {
        webElementInteraction.clearAndType(titleField, newTitle);
    }

    public void modifyDescription(String newDescription)
    {
        webElementInteraction.clearAndType(descriptionField, newDescription);
    }

    public void clickSaveButton()
    {
        webElementInteraction.clickElement(saveButton);
    }

    public void clickCancelButton()
    {
        webElementInteraction.clickElement(cancelButton);
    }

    public void clickCloseButton()
    {
        webElementInteraction.clickElement(closeButton);
    }

    public boolean isSaveButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(cancelButton);
    }

    public boolean isCloseButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(closeButton);
    }
}
