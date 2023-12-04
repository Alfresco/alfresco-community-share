package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditListDetailsPopUpPages extends BaseDialogComponent
{
    protected WebBrowser browser;
    private By titleField = By.cssSelector("input[id$='editList_prop_cm_title']");
    private By descriptionField = By.cssSelector("textarea[id$='editList_prop_cm_description']");
    private By saveButton = By.cssSelector("button[id$='editList-form-submit-button']");
    private By cancelButton = By.cssSelector("button[id$='editList-form-cancel-button']");
    private By closeButton = By.cssSelector("div[id$='editList-dialog'] a");

    public EditListDetailsPopUpPages(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void modifyTitle(String newTitle)
    {
        clearAndType(titleField, newTitle);
    }

    public void modifyDescription(String newDescription)
    {
        clearAndType(descriptionField, newDescription);
    }

    public void clickSaveButton()
    {
        clickElement(saveButton);
    }

    public void clickCancelButton()
    {
        clickElement(cancelButton);
    }

    public void clickCloseButton()
    {
        clickElement(closeButton);
    }

    public boolean isSaveButtonDisplayed()
    {
        return isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(cancelButton);
    }

    public boolean isCloseButtonDisplayed()
    {
        return  isElementDisplayed(closeButton);
    }
}
