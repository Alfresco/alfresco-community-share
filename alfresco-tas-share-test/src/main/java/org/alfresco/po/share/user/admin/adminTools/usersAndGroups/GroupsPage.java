package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.testng.Assert.assertTrue;

public class GroupsPage extends AdminToolsPage
{
    private RemoveUserFromGroupDialog removeUserFromGroupDialog;
    private DeleteGroupDialog deleteGroupDialog;
    private AddUserDialog addUserDialog;
    private AddGroupDialog addGroupDialog;

    @RenderWebElement
    private By sectionTitle = By.cssSelector("label[for*='default-search-text']");
    @RenderWebElement
    private By showSystemGroupsCheckbox = By.cssSelector("input[id*='show-all']");
    @RenderWebElement
    private By searchInput = By.cssSelector("input[id*='default-search-text']");
    private By searchButton = By.cssSelector(".search-text button[id*='search']");
    private By browseButton = By.cssSelector("button[id*='browse']");
    private By searchBar = By.cssSelector("div[id*=default-search-bar-text]");
    private By breadcrumbList = By.cssSelector("div[id*='breadcrumb'] span[class*='groups'] span[class*='item-text']");
    private By selectedItemsList = By.cssSelector(".yui-columnbrowser-item-selected");
    private By newGroupPanelTitle = By.cssSelector("div[id*='create'] .title");
    private By identifierFieldLabel = By.cssSelector(".create-main .field-row:nth-of-type(2) .crud-label");
    private By displayNameFieldLabel = By.cssSelector(".create-main .field-row:nth-of-type(4) .crud-label");
    private By groupIdentifierInput = By.cssSelector("input[id*='create-shortname']");
    private By groupDisplayNameInput = By.cssSelector("input[id*='create-displayname']");
    private By groupEditDisplayNameInput = By.cssSelector("input[id$='default-update-displayname']");
    private By createGroupOKButton = By.cssSelector("button[id*='creategroup-ok']");
    private By cancelCreateGroupButton = By.cssSelector("button[id*='creategroup-cancel']");
    private By nextPageButton = By.cssSelector("a[class='yui-pg-next']");
    private By firstPageButton = By.cssSelector("a[class='yui-pg-first']");
    private By currentPage = By.cssSelector(".yui-pg-current");
    private By searchResultTable = By.cssSelector(".yui-dt-data > tr");
    private By groupBody = By.cssSelector(".yui-columnbrowser-column-body");
    private By groupBy = By.cssSelector("div.yui-columnbrowser-column-body span[class*=item-label]");
    private By createAndCreateAnotherGroupButton = By.cssSelector("button[id*='creategroup-another']");
    private By deleteGroupOKButton = By.cssSelector("button[id$='_default-remove-button-button']");
    private By deleteGroupCancelButton = By.cssSelector("button[id$='_default-cancel-button-button']");
    private By updateGroupOKButton = By.cssSelector("button[id$='_default-updategroup-save-button-button']");
    private By updateGroupCancelButton = By.cssSelector("button[id$='_default-updategroup-cancel-button-button']");
    private By deleteMessage = By.cssSelector("div[id='message_c'");
    private String groupRow = "//div[@class='yui-columnbrowser-column-body']//span[text()='%s']/..";
    private By deleteGroupButton = By.cssSelector(".groups-delete-button");
    private By updateGroupButton = By.cssSelector(".groups-update-button");
    private By searchShortName = By.cssSelector("td[headers$='shortName ']");
    private By searchResults = By.cssSelector(".yui-dt-data > tr");
    private By removeUser = By.cssSelector(".users-remove-button");
    private String columnGroupValues = "ul[class*='carousel'] li:nth-of-type(%s) span[class*='label']";
    private String createSubGroupButton = "ul[class*='carousel'] li:nth-of-type(%s) span[class*='newgroup']";
    private String addSubGroupButton = "ul[class*='carousel'] li:nth-of-type(%s) span[class*='addgroup']";
    private String addUserGroupButton = "ul[class*='carousel'] li:nth-of-type(%s) span[class*='adduser']";

    public GroupsPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        addUserDialog = new AddUserDialog(browser);
        deleteGroupDialog = new DeleteGroupDialog(browser);
        removeUserFromGroupDialog = new RemoveUserFromGroupDialog(browser);
        addGroupDialog = new AddGroupDialog(browser);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/groups";
    }

    public GroupsPage assertSectionTitleIsGroups()
    {
        Assert.assertEquals(getBrowser().findElement(sectionTitle).getText(), language.translate("adminTools.groups.title"));
        return this;
    }

    public GroupsPage clickSearch()
    {
        getBrowser().waitUntilElementVisible(searchButton);
        getBrowser().waitUntilElementClickable(searchButton).click();
        return this;
    }

    public GroupsPage clickBrowse()
    {
        getBrowser().waitUntilElementClickable(browseButton).click();
        getBrowser().waitUntilElementVisible(groupBody);
        return this;
    }

    public GroupsPage waitUntilSearchElementDisplayed()
    {
        getBrowser().waitUntilElementVisible(searchResultTable);
        return this;
    }

    public ArrayList<String> getBreadcrumb()
    {
        return getBrowser().waitUntilElementsVisible(breadcrumbList).stream().map(WebElement::getText).collect(Collectors.toCollection(ArrayList::new));
    }

    public GroupsPage assertGroupIsInBreadcrumbPath(GroupModel groupModel)
    {
        assertTrue(getBreadcrumb().contains(groupModel.getGroupIdentifier()),
            String.format("Group %s was found in breadcrumb path", groupModel.getGroupIdentifier()));
        return this;
    }

    public GroupsPage assertGroupIsFoundInSearch(GroupModel groupModel)
    {
        boolean found = false;
        getBrowser().waitUntilElementIsPresent(searchResultTable);
        getBrowser().waitUntilElementIsPresent(searchShortName);
        List<WebElement> results = getBrowser().waitUntilElementsVisible(searchResults);
        for(WebElement searchedGroup : results)
        {
            getBrowser().waitUntilElementIsPresent(searchResultTable);
            getBrowser().waitUntilElementIsPresent(searchShortName);
            if(searchedGroup.findElement(searchShortName).getText().equals(groupModel.getGroupIdentifier()))
            {
                found = true;
            }
        }
        assertTrue(found, String.format("Group %s was found", groupModel.getGroupIdentifier()));
        return this;
    }

    public GroupsPage writeInSearchInput(String searchItem)
    {
        clearAndType(searchInput, searchItem);
        return this;
    }

    public GroupsPage assertGroupIsDisplayed(GroupModel groupModel)
    {
        assertTrue(getBrowser().isElementDisplayed(getItemGroup(groupModel.getDisplayName())),
            String.format("Group %s was found", groupModel.getDisplayName()));
        return this;
    }

    public GroupsPage assertGroupIsNotDisplayed(GroupModel groupModel)
    {
        Assert.assertFalse(isGroupDisplayed(groupModel.getDisplayName()),
            String.format("Group %s was found", groupModel.getDisplayName()));
        return this;
    }

    public GroupsPage assertUserIsNotDisplayed(UserModel userModel)
    {
        getBrowser().waitUntilElementDisappears(By.xpath(String.format(groupRow, getUserFormat(userModel))));
        Assert.assertFalse(isGroupDisplayed(getUserFormat(userModel)),
            String.format("User %s was found", userModel.getUsername()));
        return this;
    }

    private boolean isGroupDisplayed(String groupName)
    {
        return getBrowser().isElementDisplayed(By.xpath(String.format(groupRow, groupName)));
    }

    public WebElement getItemGroup(String name)
    {
        boolean found = isGroupDisplayed(name);
        int pageCount = 2;
        while(!found)
        {
            LOG.info("Move to next group page");
            if(getBrowser().isElementDisplayed(nextPageButton))
            {
                getBrowser().findElement(nextPageButton).click();
                getBrowser().waitUntilElementContainsText(getBrowser().findElement(currentPage), String.format("( %s of ", pageCount));
                found = isGroupDisplayed(name);
                pageCount++;
            }
            else
            {
                break;
            }
        }
        return getBrowser().waitUntilElementVisible(By.xpath(String.format(groupRow, name)));
    }

    private void waitForColumnGroups(int column)
    {
        By columnElement = By.cssSelector(String.format(columnGroupValues, column));
        getBrowser().waitUntilElementIsPresent(columnElement);
        getBrowser().waitUntilElementsVisible(columnElement);
    }

    public List<String> getColumnGroups(int column)
    {
        List<WebElement> values = new ArrayList<>();
        try
        {
            values = getBrowser().findElements(By.cssSelector(String.format(columnGroupValues, column)));
        }
        catch (NoSuchElementException e)
        {
            LOG.info(String.format("No values found in column %s", column));
        }
        return values.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public GroupsPage assertColumnContainsGroup(int columnNr, String groupName)
    {
        getBrowser().waitUntilElementVisible(By.cssSelector(String.format(columnGroupValues, columnNr)));
        assertTrue(getColumnGroups(columnNr).contains(groupName),
            String.format("Group %s was found in column %s", groupName, columnNr));
        return this;
    }

    public GroupsPage assertColumnContainsUser(int columnNr, UserModel userModel)
    {
        waitForColumnGroups(columnNr);
        assertTrue(getColumnGroups(columnNr).contains(getUserFormat(userModel)),
            String.format("Group %s was found in column %s", userModel.getUsername(), columnNr));
        return this;
    }

    public GroupsPage assertColumnDoesNotContainsGroup(int columnNr, String groupName)
    {
        Assert.assertFalse(getColumnGroups(columnNr).contains(groupName),
            String.format("Group %s was found in column %s", groupName, columnNr));
        return this;
    }

    public GroupsPage selectGroup(String itemName)
    {
        LOG.info(String.format("Select group %s", itemName));
        getItemGroup(itemName).click();
        getBrowser().waitUntilElementHasAttribute(getBrowser().findElement
            (By.xpath(String.format(groupRow, itemName))), "class", "item-selected");
        return this;
    }

    public GroupsPage selectGroup(GroupModel groupModel)
    {
        selectGroup(groupModel.getDisplayName());
        return this;
    }

    public GroupsPage clickCreateNewGroup(int column)
    {
        getBrowser().waitUntilElementVisible(By.cssSelector(String.format(createSubGroupButton, column))).click();
        getBrowser().waitUntilElementVisible(newGroupPanelTitle);
        return this;
    }

    public AddGroupDialog clickAddGroup(int groupColumn)
    {
        getBrowser().waitUntilElementVisible(By.cssSelector(String.format(addSubGroupButton, groupColumn))).click();
        return (AddGroupDialog) addGroupDialog.renderedPage();
    }

    public GroupsPage assertAddUserButtonIsDisplayed(int groupColumn)
    {
        WebElement addUser = getBrowser().waitUntilElementVisible(By.cssSelector(String.format(addUserGroupButton, groupColumn)));
        assertTrue(getBrowser().isElementDisplayed(addUser), "Add user button is displayed");
        return this;
    }

    public AddUserDialog clickAddUser(int groupColumn)
    {
        getBrowser().waitUntilElementVisible(By.cssSelector(String.format(addUserGroupButton, groupColumn))).click();
        return (AddUserDialog) addUserDialog.renderedPage();
    }

    /**
     * Click 'Remove User' button from second column
     *
     * @param user to be removed
     * @return 'Remove User' dialog
     */
    public RemoveUserFromGroupDialog clickRemoveUser(UserModel user)
    {
        WebElement groupElement = mouseOverGroup(getUserFormat(user));
        WebElement remove = groupElement.findElement(removeUser);
        getBrowser().waitUntilElementVisible(remove);
        getBrowser().scrollToElement(remove);
        getBrowser().mouseOver(remove);
        getBrowser().waitUntilElementClickable(remove).click();
        return (RemoveUserFromGroupDialog) removeUserFromGroupDialog.renderedPage();
    }

    /**
     * Click 'Delete Group' button
     *
     * @param groupName to be deleted
     * @return 'Delete Group' dialog
     */
    public DeleteGroupDialog clickDelete(String groupName)
    {
        WebElement groupElement = mouseOverGroup(groupName);
        WebElement delete = groupElement.findElement(deleteGroupButton);
        getBrowser().mouseOver(delete);
        getBrowser().waitUntilElementClickable(delete).click();
        return (DeleteGroupDialog) deleteGroupDialog.renderedPage();
    }

    public GroupsPage assertNewGroupTitleIsDisplayed()
    {
        Assert.assertEquals(getBrowser().waitUntilElementVisible(newGroupPanelTitle).getText(),
            language.translate("adminTools.groups.newGroupPanelTitle"), "New Group title is displayed");
        return this;
    }

    public GroupsPage assertIdentifierInputFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(groupIdentifierInput), "Identifier input is displayed");
        return this;
    }

    public void typeGroupIdentifier(String identifierName)
    {
        getBrowser().waitUntilElementVisible(groupIdentifierInput);
        clearAndType(groupIdentifierInput, identifierName);
    }

    public GroupsPage assertDisplayNameInputFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(groupDisplayNameInput), "Display Name input is displayed");
        return this;
    }

    public GroupsPage assertGroupIdentifierFieldLabelIsCorrect()
    {
        Assert.assertEquals(getBrowser().findElement(identifierFieldLabel).getText(), language.translate("adminTools.groups.newGroupProperties.identifier"));
        return this;
    }

    public GroupsPage assertDisplayNameFieldLabelIsCorrect()
    {
        Assert.assertEquals(getBrowser().findElement(displayNameFieldLabel).getText(), language.translate("adminTools.groups.newGroupProperties.displayName"));
        return this;
    }

    public void typeGroupDisplayName(String identifierName)
    {
        clearAndType(groupDisplayNameInput, identifierName);
    }

    public GroupsPage assertCreateNewGroupButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(createGroupOKButton), "Create group button is displayed");
        return this;
    }

    public void clickCreateGroupButton()
    {
        getBrowser().waitUntilElementVisible(createGroupOKButton);
        getBrowser().waitUntilElementClickable(createGroupOKButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public GroupsPage assertCreateAndCreateAnotherGroupButtonDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(createAndCreateAnotherGroupButton), "Create and create another button is displayed");
        return this;
    }

    public GroupsPage assertCancelCreateNewGroupButtonDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(cancelCreateGroupButton), "Create and create another button is displayed");
        return this;
    }

    private void clickCancelCreateNewGroupButton()
    {
        getBrowser().waitUntilElementClickable(cancelCreateGroupButton, 5).click();
    }

    public GroupsPage createGroup(GroupModel groupModel)
    {
        typeGroupIdentifier(groupModel.getGroupIdentifier());
        typeGroupDisplayName(groupModel.getDisplayName());
        clickCreateGroupButton();
        return this;
    }

    public void createGroup(String groupName, int groupColumn)
    {
        clickCreateNewGroup(groupColumn);
        typeGroupIdentifier(groupName);
        typeGroupDisplayName(groupName);
        clickCreateGroupButton();
    }

    public GroupsPage delete(GroupModel groupModel)
    {
        WebElement groupElement = mouseOverGroup(groupModel.getDisplayName());
        WebElement delete = groupElement.findElement(deleteGroupButton);
        getBrowser().waitUntilElementVisible(delete);
        getBrowser().scrollToElement(delete);
        getBrowser().mouseOver(delete);
        getBrowser().waitUntilElementClickable(delete).click();
        getBrowser().waitUntilElementVisible(deleteGroupOKButton).click();
        waitUntilNotificationMessageDisappears();
        return this;
    }

    private WebElement mouseOverGroup(String groupName)
    {
        WebElement element = getItemGroup(groupName);
        getBrowser().scrollIntoView(element);
        getBrowser().mouseOver(element);
        return element;
    }

    public GroupsPage editGroup(GroupModel group, String newName)
    {
        WebElement groupElement = mouseOverGroup(group.getDisplayName());
        WebElement edit = groupElement.findElement(updateGroupButton);
        getBrowser().mouseOver(edit);
        getBrowser().waitUntilElementClickable(edit).click();
        WebElement groupEditDisplayNameInputElement =  getBrowser().waitUntilElementVisible(groupEditDisplayNameInput);
        groupEditDisplayNameInputElement.clear();
        groupEditDisplayNameInputElement.sendKeys(newName);
        getBrowser().findElement(updateGroupOKButton).click();
        waitUntilNotificationMessageDisappears();
        if( getBrowser().isElementDisplayed(firstPageButton))
        {
            getBrowser().findElement(firstPageButton).click();
        }
        return this;
    }

    public String getSearchBarText()
    {
        return getBrowser().findElement(searchBar).getText();
    }

    public GroupsPage assertSearchBarTextIs(String searchKeyword, int results)
    {
        Assert.assertEquals(getBrowser().findElement(searchBar).getText(), String.format(
            language.translate("adminTools.groups.searchResultHeader"), searchKeyword, results));
        return this;
    }

    public GroupsPage assertDeleteInfoMessageIsDisplayed(String group)
    {
        getBrowser().waitUntilElementVisible(deleteMessage);
        Assert.assertEquals( getBrowser().findElement(deleteMessage).getText(),
            String.format(language.translate("adminTools.groups.deleteMessage"), group));
        getBrowser().waitUntilElementDisappears(deleteMessage);
        return this;
    }

    private String getUserFormat(UserModel user)
    {
        return String.format("%s %s (%s)",
            user.getFirstName(), user.getLastName(), user.getUsername());
    }
}