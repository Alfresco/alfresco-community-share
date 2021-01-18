package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.model.GroupModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DeleteGroupDialog extends BaseDialogComponent
{
    private final By dialogHeader = By.cssSelector("div[id*='deletegroupdialog_h']");
    private final By multiparentMessage = By.cssSelector("span[id*='multiparent-message']");
    private final By parent = By.cssSelector("div[id*='multiparent'] div[id*='parent']");
    private final By removeRow = By.cssSelector("span[id*='remove-message']");
    private final By deleteRow = By.cssSelector("div[id*='multiparent'] div[id*='-deleterow']");
    private final By deleteButton = By.cssSelector("button[id*='remove']");
    private final By cancelButton = By.cssSelector("div[id*='deletegroupdialog'] button[id*='cancel']");
    private final By removeRadioButton = By.cssSelector("input[id*='remove']");
    private final By deleteRadioButton = By.cssSelector("input[id*='delete']");

    public DeleteGroupDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public DeleteGroupDialog assertDialogHeaderIsCorrect()
    {
        LOG.info("Assert Delete Group dialog has the correct header");
        assertEquals(webElementInteraction.getElementText(dialogHeader), language.translate("adminTools.groups.deleteGroup.header"));
        return this;
    }

    public DeleteGroupDialog assertDialogMessageIsCorrect(String groupName)
    {
        LOG.info("Assert Delete group dialog message is correct");
        assertEquals(webElementInteraction.findElement(multiparentMessage).getText(),
            String.format(language.translate("adminTools.groups.deleteGroup.multiparentMessage"), groupName));
        return this;
    }

    public DeleteGroupDialog assertParentsAre(String... parentGroups)
    {
        LOG.info(String.format("Assert parents are: %s", Arrays.asList(parentGroups)));
        String[] values = Arrays.asList(webElementInteraction.getElementText(parent).split(", ")).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(parentGroups);
        assertTrue(Arrays.equals(values, parentGroups), "All available group parents are found");
        return this;
    }

    public DeleteGroupDialog assertRemoveRadioButtonIsDisplayed()
    {
        LOG.info("Assert remove radio button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(removeRadioButton), "Remove radio button is displayed");
        return this;
    }

    public DeleteGroupDialog assertDeleteRadioButtonIsDisplayed()
    {
        LOG.info("Assert delete radio button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(deleteRadioButton), "Delete radio button is displayed");
        return this;
    }

    public DeleteGroupDialog assertDeleteButtonIsDisplayed()
    {
        LOG.info("Assert Delete button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(deleteButton), "Delete button is displayed");
        return this;
    }

    public DeleteGroupDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public DeleteGroupDialog assertJustDeleteGroupLabelIsCorrect(GroupModel groupToDelete, GroupModel parentGroup)
    {
        return assertJustDeleteGroupLabelIsCorrect(groupToDelete.getDisplayName(), parentGroup.getDisplayName());
    }

    public DeleteGroupDialog assertJustDeleteGroupLabelIsCorrect(String groupToDelete, String parentGroup)
    {
        LOG.info("Assert just delete sub group from parent label is correct");
        assertEquals(webElementInteraction.findElement(removeRow).getText(),
            String.format(language.translate("adminTools.groups.deleteGroup.removeRow"), groupToDelete, parentGroup));
        return this;
    }

    public DeleteGroupDialog assertRemoveGroupFromAllLabelIsCorrect(String group)
    {
        LOG.info("Assert remove group from all label is correct");
        assertEquals(webElementInteraction.findElement(deleteRow).getText(), String.format(language.translate("adminTools.groups.deleteGroup.deleteRow"), group));
        return this;
    }

    public DeleteGroupDialog assertRemoveGroupFromAllLabelIsCorrect(GroupModel group)
    {
        return assertRemoveGroupFromAllLabelIsCorrect(group.getDisplayName());
    }

    public GroupsPage clickDelete()
    {
        webElementInteraction.clickElement(deleteButton);
        waitUntilNotificationMessageDisappears();
        return new GroupsPage(webDriver);
    }

    public boolean isRemoveRadioButtonSelected()
    {
        return webElementInteraction.findElement(removeRadioButton).isSelected();
    }

    public DeleteGroupDialog assertRemoveRadioButtonIsSelected()
    {
        LOG.info("Assert Remove radio button is selected");
        assertTrue(webElementInteraction.findElement(removeRadioButton).isSelected(), "Remove radio button is selected");
        return this;
    }

    public DeleteGroupDialog assertDeleteRadioButtonIsNotSelected()
    {
        LOG.info("Assert delete radio button is not selected");
        assertFalse(webElementInteraction.findElement(deleteRadioButton).isSelected(), "Delete radio button is selected");
        return this;
    }

    public boolean isDeleteRadioButtonSelected()
    {
        return webElementInteraction.findElement(deleteRadioButton).isSelected();
    }

    public DeleteGroupDialog selectRemoveFromGroupRadio()
    {
        if (!isRemoveRadioButtonSelected())
        {
            webElementInteraction.clickElement(removeRadioButton);
        }
        return this;
    }

    public DeleteGroupDialog selectDeleteGroupRadio()
    {
        if (!isDeleteRadioButtonSelected())
        {
            webElementInteraction.clickElement(deleteRadioButton);
        }
        return this;
    }
}
