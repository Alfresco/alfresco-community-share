package org.alfresco.po.share.site;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditSiteDetails extends BaseDialogComponent
{
    private final By closeCreateSitePopup = By.cssSelector("[class*='Close']");
    private final By nameInput = By.cssSelector("div[id='EDIT_SITE_FIELD_TITLE'] input[class$='InputInner']");
    private final By descriptionInput = By.cssSelector("div[id='EDIT_SITE_FIELD_DESCRIPTION'] textarea");
    private final By editSiteDetailsDialog = By.cssSelector("div[id='EDIT_SITE_DIALOG']");
    private final By saveButton = By.cssSelector("span[id='EDIT_SITE_DIALOG_OK_label']");
    private final By nameLabel = By.cssSelector("div[id='EDIT_SITE_FIELD_TITLE'] label.label");
    private final By descriptionLabel = By.cssSelector("div[id='EDIT_SITE_FIELD_DESCRIPTION'] label.label");
    private final By visibilityLabel = By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY'] label.label");
    private final By publicVisibilityRadioButton = By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] div[class*='dijitRadio'] input");
    private final By publicVisibilityDescription = By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] div.radio-button-label div");
    private final By moderatedVisibilityDescription = By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] div.radio-button-label div");
    private final By privateVisibilityRadioButton = By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] div[class*='dijitRadio'] input");
    private final By privateVisibilityDescription = By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] div.radio-button-label div");
    private final By cancelButton = By.cssSelector("span[id='EDIT_SITE_DIALOG_CANCEL_label']");

    public EditSiteDetails(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void typeDetails(String title, String description)
    {
        waitInSeconds(3);
        clearAndType(nameInput, title);
        waitInSeconds(3);
        clearAndType(descriptionInput, description);
    }
    public String getTitleInputText()
    {
        waitInSeconds(5);
        return findElement(nameInput).getAttribute("value");
    }
    public void clickSaveButton()
    {
        clickElement(saveButton);
    }
    public String getDescriptionInputText()
    {
        return findElement(descriptionInput).getAttribute("value");
    }
    public void clickCloseCreateSitePopup()
    {
        clickElement(closeCreateSitePopup);
    }
    public boolean isEditSiteDetailsDialogDisplayed()
    {
        waitInSeconds(2);
        return isElementDisplayed(editSiteDetailsDialog);
    }
}
