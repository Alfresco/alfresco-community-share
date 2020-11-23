package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import java.util.Arrays;

import static org.testng.Assert.*;

public class DeleteGroupDialog extends ShareDialog2
{
    private GroupsPage groupsPage;

    @RenderWebElement
    private By dialogHeader = By.cssSelector("div[id*='deletegroupdialog_h']");
    @RenderWebElement
    private By multiparentMessage = By.cssSelector("span[id*='multiparent-message']");
    @RenderWebElement
    private By parent = By.cssSelector("div[id*='multiparent'] div[id*='parent']");
    private By removeRow = By.cssSelector("span[id*='remove-message']");
    private By deleteRow = By.cssSelector("div[id*='multiparent'] div[id*='-deleterow']");
    @RenderWebElement
    private By deleteButton = By.cssSelector("button[id*='remove']");
    private By cancelButton = By.cssSelector("div[id*='deletegroupdialog'] button[id*='cancel']");
    private By removeRadioButton = By.cssSelector("input[id*='remove']");
    private By deleteRadioButton = By.cssSelector("input[id*='delete']");

    public DeleteGroupDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
        groupsPage = new GroupsPage(browser);
    }

    public DeleteGroupDialog assertDialogHeaderIsCorrect()
    {
        LOG.info("Assert Delete Group dialog has the correct header");
        assertEquals(getBrowser().findElement(dialogHeader).getText(), language.translate("adminTools.groups.deleteGroup.header"));
        return this;
    }

    public DeleteGroupDialog assertDialogMessageIsCorrect(String groupName)
    {
        LOG.info("Assert Delete group dialog message is correct");
        assertEquals(getBrowser().findElement(multiparentMessage).getText(),
            String.format(language.translate("adminTools.groups.deleteGroup.multiparentMessage"), groupName));
        return this;
    }

    public DeleteGroupDialog assertParentsAre(String... parentGroups)
    {
        LOG.info(String.format("Assert parents are: %s", Arrays.asList(parentGroups)));
        String[] values = Arrays.asList(getBrowser().findElement(parent).getText().split(", ")).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(parentGroups);
        assertTrue(Arrays.equals(values, parentGroups), "All available group parents are found");
        return this;
    }

    public DeleteGroupDialog assertRemoveRadioButtonIsDisplayed()
    {
        LOG.info("Assert remove radio button is displayed");
        assertTrue(getBrowser().isElementDisplayed(removeRadioButton), "Remove radio button is displayed");
        return this;
    }

    public DeleteGroupDialog assertDeleteRadioButtonIsDisplayed()
    {
        LOG.info("Assert delete radio button is displayed");
        assertTrue(getBrowser().isElementDisplayed(deleteRadioButton), "Delete radio button is displayed");
        return this;
    }

    public DeleteGroupDialog assertDeleteButtonIsDisplayed()
    {
        LOG.info("Assert Delete button is displayed");
        assertTrue(getBrowser().isElementDisplayed(deleteButton), "Delete button is displayed");
        return this;
    }

    public DeleteGroupDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public DeleteGroupDialog assertJustDeleteGroupLabelIsCorrect(GroupModel groupToDelete, GroupModel parentGroup)
    {
        return assertJustDeleteGroupLabelIsCorrect(groupToDelete.getDisplayName(), parentGroup.getDisplayName());
    }

    public DeleteGroupDialog assertJustDeleteGroupLabelIsCorrect(String groupToDelete, String parentGroup)
    {
        LOG.info("Assert just delete sub group from parent label is correct");
        assertEquals(getBrowser().findElement(removeRow).getText(),
            String.format(language.translate("adminTools.groups.deleteGroup.removeRow"), groupToDelete, parentGroup));
        return this;
    }

    public DeleteGroupDialog assertRemoveGroupFromAllLabelIsCorrect(String group)
    {
        LOG.info("Assert remove group from all label is correct");
        assertEquals(getBrowser().findElement(deleteRow).getText(), String.format(language.translate("adminTools.groups.deleteGroup.deleteRow"), group));
        return this;
    }

    public DeleteGroupDialog assertRemoveGroupFromAllLabelIsCorrect(GroupModel group)
    {
        return assertRemoveGroupFromAllLabelIsCorrect(group.getDisplayName());
    }

    public GroupsPage clickDelete()
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        waitUntilNotificationMessageDisappears();
        return (GroupsPage) groupsPage.renderedPage();
    }

    public boolean isRemoveRadioButtonSelected()
    {
        return getBrowser().findElement(removeRadioButton).isSelected();
    }

    public DeleteGroupDialog assertRemoveRadioButtonIsSelected()
    {
        LOG.info("Assert Remove radio button is selected");
        assertTrue(getBrowser().findElement(removeRadioButton).isSelected(), "Remove radio button is selected");
        return this;
    }

    public DeleteGroupDialog assertDeleteRadioButtonIsNotSelected()
    {
        LOG.info("Assert delete radio button is not selected");
        assertFalse(getBrowser().findElement(deleteRadioButton).isSelected(), "Delete radio button is selected");
        return this;
    }

    public boolean isDeleteRadioButtonSelected()
    {
        return getBrowser().findElement(deleteRadioButton).isSelected();
    }

    public DeleteGroupDialog selectRemoveFromGroupRadio()
    {
        if (!isRemoveRadioButtonSelected())
        {
            getBrowser().findElement(removeRadioButton).click();
        }
        return this;
    }

    public DeleteGroupDialog selectDeleteGroupRadio()
    {
        if (!isDeleteRadioButtonSelected())
        {
            getBrowser().findElement(deleteRadioButton).click();
        }
        return this;
    }
}
