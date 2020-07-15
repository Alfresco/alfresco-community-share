package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.Arrays;

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

    public DeleteGroupDialog assertDialogHeaderIsCorrect()
    {
        LOG.info("Assert Delete Group dialog has the correct header");
        Assert.assertEquals(dialogHeader.getText(), language.translate("adminTools.groups.deleteGroup.header"));
        return this;
    }

    public DeleteGroupDialog assertDialogMessageIsCorrect(String groupName)
    {
        LOG.info("Assert Delete group dialog message is correct");
        Assert.assertEquals(multiparentMessage.getText(),
            String.format(language.translate("adminTools.groups.deleteGroup.multiparentMessage"), groupName));
        return this;
    }

    public DeleteGroupDialog assertParentsAre(String... parentGroups)
    {
        LOG.info(String.format("Assert parents are: %s", Arrays.asList(parentGroups)));
        String[] values = Arrays.asList(parent.getText().split(", ")).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(parentGroups);
        Assert.assertTrue(Arrays.equals(values, parentGroups), "All available group parents are found");
        return this;
    }

    public DeleteGroupDialog assertRemoveRadioButtonIsDisplayed()
    {
        LOG.info("Assert remove radio button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(removeRadioButton), "Remove radio button is displayed");
        return this;
    }

    public DeleteGroupDialog assertDeleteRadioButtonIsDisplayed()
    {
        LOG.info("Assert delete radio button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(deleteRadioButton), "Delete radio button is displayed");
        return this;
    }

    public DeleteGroupDialog assertDeleteButtonIsDisplayed()
    {
        LOG.info("Assert Delete button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(deleteButton), "Delete button is displayed");
        return this;
    }

    public DeleteGroupDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public DeleteGroupDialog assertJustDeleteGroupLabelIsCorrect(GroupModel groupToDelete, GroupModel parentGroup)
    {
        return assertJustDeleteGroupLabelIsCorrect(groupToDelete.getDisplayName(), parentGroup.getDisplayName());
    }

    public DeleteGroupDialog assertJustDeleteGroupLabelIsCorrect(String groupToDelete, String parentGroup)
    {
        LOG.info("Assert just delete sub group from parent label is correct");
        Assert.assertEquals(removeRow.getText(),
            String.format(language.translate("adminTools.groups.deleteGroup.removeRow"), groupToDelete, parentGroup));
        return this;
    }

    public DeleteGroupDialog assertRemoveGroupFromAllLabelIsCorrect(String group)
    {
        LOG.info("Assert remove group from all label is correct");
        Assert.assertEquals(deleteRow.getText(), String.format(language.translate("adminTools.groups.deleteGroup.deleteRow"), group));
        return this;
    }

    public DeleteGroupDialog assertRemoveGroupFromAllLabelIsCorrect(GroupModel group)
    {
        return assertRemoveGroupFromAllLabelIsCorrect(group.getDisplayName());
    }

    public GroupsPage clickDelete()
    {
        deleteButton.click();
        return (GroupsPage) groupsPage.renderedPage();
    }

    public boolean isRemoveRadioButtonSelected()
    {
        return removeRadioButton.isSelected();
    }

    public DeleteGroupDialog assertRemoveRadioButtonIsSelected()
    {
        LOG.info("Assert Remove radio button is selected");
        Assert.assertTrue(removeRadioButton.isSelected(), "Remove radio button is selected");
        return this;
    }

    public DeleteGroupDialog assertDeleteRadioButtonIsNotSelected()
    {
        LOG.info("Assert delete radio button is not selected");
        Assert.assertFalse(deleteRadioButton.isSelected(), "Delete radio button is selected");
        return this;
    }

    public boolean isDeleteRadioButtonSelected()
    {
        return deleteRadioButton.isSelected();
    }

    public DeleteGroupDialog selectRemoveFromGroupRadio()
    {
        if (!isRemoveRadioButtonSelected())
        {
            removeRadioButton.click();
        }
        return this;
    }

    public DeleteGroupDialog selectDeleteGroupRadio()
    {
        if (!isDeleteRadioButtonSelected())
        {
            deleteRadioButton.click();
        }
        return this;
    }
}
