package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.model.GroupModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
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
        log.info("Assert Delete Group dialog has the correct header");
        assertEquals(getElementText(dialogHeader), language.translate("adminTools.groups.deleteGroup.header"));
        return this;
    }

    public DeleteGroupDialog assertDialogMessageIsCorrect(String groupName)
    {
        log.info("Assert Delete group dialog message is correct");
        assertEquals(findElement(multiparentMessage).getText(),
            String.format(language.translate("adminTools.groups.deleteGroup.multiparentMessage"), groupName));
        return this;
    }

    public DeleteGroupDialog assertParentsAre(String... parentGroups)
    {
        log.info(String.format("Assert parents are: %s", Arrays.asList(parentGroups)));
        String[] values = Arrays.asList(getElementText(parent).split(", ")).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(parentGroups);
        assertTrue(Arrays.equals(values, parentGroups), "All available group parents are found");
        return this;
    }

    public DeleteGroupDialog assertRemoveRadioButtonIsDisplayed()
    {
        log.info("Assert remove radio button is displayed");
        assertTrue(isElementDisplayed(removeRadioButton), "Remove radio button is displayed");
        return this;
    }

    public DeleteGroupDialog assertDeleteRadioButtonIsDisplayed()
    {
        log.info("Assert delete radio button is displayed");
        assertTrue(isElementDisplayed(deleteRadioButton), "Delete radio button is displayed");
        return this;
    }

    public DeleteGroupDialog assertDeleteButtonIsDisplayed()
    {
        log.info("Assert Delete button is displayed");
        assertTrue(isElementDisplayed(deleteButton), "Delete button is displayed");
        return this;
    }

    public DeleteGroupDialog assertCancelButtonIsDisplayed()
    {
        log.info("Assert Cancel button is displayed");
        assertTrue(isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public DeleteGroupDialog assertJustDeleteGroupLabelIsCorrect(GroupModel groupToDelete, GroupModel parentGroup)
    {
        return assertJustDeleteGroupLabelIsCorrect(groupToDelete.getDisplayName(), parentGroup.getDisplayName());
    }

    public DeleteGroupDialog assertJustDeleteGroupLabelIsCorrect(String groupToDelete, String parentGroup)
    {
        log.info("Assert just delete sub group from parent label is correct");
        assertEquals(findElement(removeRow).getText(),
            String.format(language.translate("adminTools.groups.deleteGroup.removeRow"), groupToDelete, parentGroup));
        return this;
    }

    public DeleteGroupDialog assertRemoveGroupFromAllLabelIsCorrect(String group)
    {
        log.info("Assert remove group from all label is correct");
        assertEquals(findElement(deleteRow).getText(), String.format(language.translate("adminTools.groups.deleteGroup.deleteRow"), group));
        return this;
    }

    public DeleteGroupDialog assertRemoveGroupFromAllLabelIsCorrect(GroupModel group)
    {
        return assertRemoveGroupFromAllLabelIsCorrect(group.getDisplayName());
    }

    public GroupsPage clickDelete()
    {
        clickElement(deleteButton);
        waitUntilNotificationMessageDisappears();
        return new GroupsPage(webDriver);
    }

    public boolean isRemoveRadioButtonSelected()
    {
        return findElement(removeRadioButton).isSelected();
    }

    public DeleteGroupDialog assertRemoveRadioButtonIsSelected()
    {
        log.info("Assert Remove radio button is selected");
        assertTrue(findElement(removeRadioButton).isSelected(), "Remove radio button is selected");
        return this;
    }

    public DeleteGroupDialog assertDeleteRadioButtonIsNotSelected()
    {
        log.info("Assert delete radio button is not selected");
        assertFalse(findElement(deleteRadioButton).isSelected(), "Delete radio button is selected");
        return this;
    }

    public boolean isDeleteRadioButtonSelected()
    {
        return findElement(deleteRadioButton).isSelected();
    }

    public DeleteGroupDialog selectRemoveFromGroupRadio()
    {
        if (!isRemoveRadioButtonSelected())
        {
            clickElement(removeRadioButton);
        }
        return this;
    }

    public DeleteGroupDialog selectDeleteGroupRadio()
    {
        if (!isDeleteRadioButtonSelected())
        {
            clickElement(deleteRadioButton);
        }
        return this;
    }
}
