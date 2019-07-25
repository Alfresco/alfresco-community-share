package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class DeleteGroupDialog extends ShareDialog
{
    @Autowired
    private GroupsPage groupsPage;

    @RenderWebElement
    @FindBy (css = "div[id*='deletegroupdialog_h']")
    private WebElement dialogHeader;

    @RenderWebElement
    @FindBy (css = "span[id*='multiparent-message']")
    private WebElement multiparentMessage;

    @RenderWebElement
    @FindBy (css = "div[id*='multiparent'] div[id*='parent']")
    private WebElement parent;

    @RenderWebElement
    @FindBy (css = "span[id*='remove-message']")
    private WebElement removeRow;

    @FindBy (css = "div[id*='multiparent'] div[id*='-deleterow']")
    private WebElement deleteRow;

    @RenderWebElement
    @FindBy (css = "button[id*='remove']")
    private WebElement deleteButton;

    @FindBy (css = "div[id*='deletegroupdialog'] button[id*='cancel']")
    private WebElement cancelButton;

    @FindBy (css = "input[id*='remove']")
    private WebElement removeRadioButton;

    @FindBy (css = "input[id*='delete']")
    private WebElement deleteRadioButton;

    public String getDialogHeader()
    {
        this.renderedPage();
        return dialogHeader.getText();
    }

    public String getMultiparentMessage()
    {
        return multiparentMessage.getText();
    }

    public String getParent()
    {
        return parent.getText();
    }

    public String getRemoveRow()
    {
        if (browser.isElementDisplayed(removeRow))
            return removeRow.getText();
        return "Remove row isn't displayed!";
    }

    public String getDeleteRow()
    {
        if (browser.isElementDisplayed(deleteRow))
            return deleteRow.getText();
        return "Delete row isn't displayed!";
    }

    public boolean isDeleteButtonDisplayed()
    {
        return browser.isElementDisplayed(deleteButton);
    }

    public GroupsPage clickDeleteButton()
    {
        deleteButton.click();
        return (GroupsPage) groupsPage.renderedPage();
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public boolean isRemoveRadioButtonDisplayed()
    {
        return removeRadioButton.isDisplayed();
    }

    public boolean isRemoveRadioButtonSelected()
    {
        return removeRadioButton.isSelected();
    }

    public boolean isDeleteRadioButtonDisplayed()
    {
        return deleteRadioButton.isDisplayed();
    }

    public boolean isDeleteRadioButtonSelected()
    {
        return deleteRadioButton.isSelected();
    }

    public void selectRemoveFromGroupRadio()
    {
        if (!isRemoveRadioButtonSelected())
        {
            removeRadioButton.click();
        }
    }

    public void selectDeleteGroupRadio()
    {
        if (!isDeleteRadioButtonSelected())
        {
            deleteRadioButton.click();
        }
    }
}
