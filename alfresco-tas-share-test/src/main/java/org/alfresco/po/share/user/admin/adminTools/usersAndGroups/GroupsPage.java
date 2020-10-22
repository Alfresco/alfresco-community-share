package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class GroupsPage extends AdminToolsPage
{
    private final RemoveUserFromGroupDialog removeUserFromGroupDialog;
    private final DeleteGroupDialog deleteGroupDialog;
    private final AddUserDialog addUserDialog;
    private final AddGroupDialog addGroupDialog;

    @RenderWebElement
    @FindBy (css = "label[for*='default-search-text']")
    private WebElement sectionTitle;

    @RenderWebElement
    @FindBy (css = "input[id*='show-all']")
    private WebElement showSystemGroupsCheckbox;

    @RenderWebElement
    @FindBy (css = "input[id*='default-search-text']")
    private WebElement searchInput;

    @RenderWebElement
    @FindBy (css = ".search-text button[id*='search']")
    private WebElement searchButton;

    @RenderWebElement
    @FindBy (css = "button[id*='browse']")
    private WebElement browseButton;

    @FindBy (css = "div[id*=default-search-bar-text]")
    private WebElement searchBar;

    @FindAll (@FindBy (css = "div[id*='breadcrumb'] span[class*='groups'] span[class*='item-text']"))
    private List<WebElement> breadcrumbList;

    @FindBy (css = ".yui-columnbrowser-item-selected")
    private List<WebElement> selectedItemsList;

    @FindBy (css = "div[id*='create'] .title")
    private WebElement newGroupPanelTitle;

    @FindBy (css = ".create-main .field-row:nth-of-type(2) .crud-label")
    private WebElement identifierFieldLabel;

    @FindBy (css = ".create-main .field-row:nth-of-type(4) .crud-label")
    private WebElement displayNameFieldLabel;

    @FindBy (css = "input[id*='create-shortname']")
    private WebElement groupIdentifierInput;

    @FindBy (css = "input[id*='create-displayname']")
    private WebElement groupDisplayNameInput;

    private By groupEditDisplayNameInput = By.cssSelector("input[id$='default-update-displayname']");

    @FindBy (css = "button[id*='creategroup-ok']")
    private WebElement createGroupOKButton;

    @FindBy (css = "button[id*='creategroup-cancel']")
    private WebElement cancelCreateGroupButton;

    @FindBy (css = "a[class='yui-pg-next']")
    private WebElement nextPageButton;

    @FindBy (css = "a[class='yui-pg-first']")
    private WebElement firstPageButton;

    @FindBy (css = ".yui-pg-current")
    private WebElement currentPage;

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

    public GroupsPage(AddUserDialog addUserDialog,
                      DeleteGroupDialog deleteGroupDialog,
                      RemoveUserFromGroupDialog removeUserFromGroupDialog,
                      AddGroupDialog addGroupDialog)
    {
        this.addUserDialog = addUserDialog;
        this.deleteGroupDialog = deleteGroupDialog;
        this.removeUserFromGroupDialog = removeUserFromGroupDialog;
        this.addGroupDialog = addGroupDialog;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/groups";
    }

    public GroupsPage assertSectionTitleIsGroups()
    {
        Assert.assertEquals(sectionTitle.getText(), language.translate("adminTools.groups.title"));
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
        browser.waitUntilElementClickable(browseButton).click();
        browser.waitUntilElementVisible(groupBody);
        return this;
    }

    public GroupsPage waitUntilSearchElementDisplayed()
    {
        browser.waitUntilElementVisible(searchResultTable);
        return this;
    }

    /**
     * @return List of breadcrumb path text items
     */
    public ArrayList<String> getBreadcrumb()
    {
        browser.waitUntilElementsVisible(breadcrumbList);
        return breadcrumbList.stream().map(WebElement::getText).collect(Collectors.toCollection(ArrayList::new));
    }

    public GroupsPage assertGroupIsInBreadcrumbPath(GroupModel groupModel)
    {
        Assert.assertTrue(getBreadcrumb().contains(groupModel.getGroupIdentifier()),
            String.format("Group %s was found in breadcrumb path", groupModel.getGroupIdentifier()));
        return this;
    }

    public GroupsPage assertGroupIsFoundInSearch(GroupModel groupModel)
    {
        boolean found = false;
        browser.waitUntilElementIsPresent(searchResultTable);
        browser.waitUntilElementIsPresent(searchShortName);
        List<WebElement> results = browser.waitUntilElementsVisible(searchResults);
        for(WebElement searchedGroup : results)
        {
            browser.waitUntilElementIsPresent(searchResultTable);
            browser.waitUntilElementIsPresent(searchShortName);
            if(searchedGroup.findElement(searchShortName).getText().equals(groupModel.getGroupIdentifier()))
            {
                found = true;
            }
        }
        Assert.assertTrue(found, String.format("Group %s was found", groupModel.getGroupIdentifier()));
        return this;
    }

    public GroupsPage writeInSearchInput(String searchItem)
    {
        searchInput.clear();
        searchInput.sendKeys(searchItem);
        return this;
    }

    public void checkShowSystemGroupsCheckbox()
    {
        showSystemGroupsCheckbox.click();
    }

    public GroupsPage assertGroupIsDisplayed(GroupModel groupModel)
    {
        Assert.assertTrue(browser.isElementDisplayed(getItemGroup(groupModel.getDisplayName())),
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
        Assert.assertFalse(isGroupDisplayed(getUserFormat(userModel)),
            String.format("User %s was found", userModel.getUsername()));
        return this;
    }

    private boolean isGroupDisplayed(String groupName)
    {
        return browser.isElementDisplayed(By.xpath(String.format(groupRow, groupName)));
    }

    public WebElement getItemGroup(String name)
    {
        boolean found = isGroupDisplayed(name);
        int pageCount = 2;
        while(!found)
        {
            LOG.info("Move to next group page");
            if(browser.isElementDisplayed(nextPageButton))
            {
                nextPageButton.click();
                browser.waitUntilElementContainsText(currentPage, String.format("( %s of ", pageCount));
                found = isGroupDisplayed(name);
                pageCount++;
            }
            else
            {
                break;
            }
        }
        return browser.waitUntilElementVisible(By.xpath(String.format(groupRow, name)));
    }

    private void waitForColumnGroups(int column)
    {
        By columnElement = By.cssSelector(String.format(columnGroupValues, column));
        browser.waitUntilElementIsPresent(columnElement);
        browser.waitUntilElementsVisible(columnElement);
    }

    public List<String> getColumnGroups(int column)
    {
        List<WebElement> values = new ArrayList<>();
        try
        {
            values = browser.findElements(By.cssSelector(String.format(columnGroupValues, column)));
        }
        catch (NoSuchElementException e)
        {
            LOG.info(String.format("No values found in column %s", column));
        }
        return values.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public GroupsPage assertColumnContainsGroup(int columnNr, String groupName)
    {
        browser.waitUntilElementVisible(By.cssSelector(String.format(columnGroupValues, columnNr)));
        Assert.assertTrue(getColumnGroups(columnNr).contains(groupName),
            String.format("Group %s was found in column %s", groupName, columnNr));
        return this;
    }

    public GroupsPage assertColumnContainsUser(int columnNr, UserModel userModel)
    {
        waitForColumnGroups(columnNr);
        Assert.assertTrue(getColumnGroups(columnNr).contains(getUserFormat(userModel)),
            String.format("Group %s was found in column %s", userModel.getUsername(), columnNr));
        return this;
    }

    public GroupsPage assertColumnDoesNotContainsGroup(int columnNr, String groupName)
    {
        Assert.assertFalse(getColumnGroups(columnNr).contains(groupName),
            String.format("Group %s was found in column %s", groupName, columnNr));
        return this;
    }

    /**
     * Click on any item from Groups page lists
     *
     * @param itemName to be clicked
     */
    public GroupsPage selectGroup(String itemName)
    {
        LOG.info(String.format("Select group %s", itemName));
        getItemGroup(itemName).click();
        browser.waitUntilElementHasAttribute(browser.findElement
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
        browser.waitUntilElementVisible(By.cssSelector(String.format(createSubGroupButton, column))).click();
        browser.waitUntilElementVisible(newGroupPanelTitle);
        return this;
    }

    public AddGroupDialog clickAddGroup(int groupColumn)
    {
        browser.waitUntilElementVisible(By.cssSelector(String.format(addSubGroupButton, groupColumn))).click();
        return (AddGroupDialog) addGroupDialog.renderedPage();
    }

    public GroupsPage assertAddUserButtonIsDisplayed(int groupColumn)
    {
        WebElement addUser = browser.waitUntilElementVisible(By.cssSelector(String.format(addUserGroupButton, groupColumn)));
        Assert.assertTrue(browser.isElementDisplayed(addUser), "Add user button is displayed");
        return this;
    }

    public AddUserDialog clickAddUser(int groupColumn)
    {
        browser.waitUntilElementVisible(By.cssSelector(String.format(addUserGroupButton, groupColumn))).click();
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
        browser.waitUntilElementVisible(remove);
        browser.scrollToElement(remove);
        browser.mouseOver(remove);
        browser.waitUntilElementClickable(remove).click();
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
        browser.mouseOver(delete);
        browser.waitUntilElementClickable(delete).click();
        return (DeleteGroupDialog) deleteGroupDialog.renderedPage();
    }

    public GroupsPage assertNewGroupTitleIsDisplayed()
    {
        Assert.assertEquals(browser.waitUntilElementVisible(newGroupPanelTitle).getText(),
            language.translate("adminTools.groups.newGroupPanelTitle"), "New Group title is displayed");
        return this;
    }

    public GroupsPage assertIdentifierInputFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(groupIdentifierInput), "Identifier input is displayed");
        return this;
    }

    public void typeGroupIdentifier(String identifierName)
    {
        browser.waitUntilElementVisible(groupIdentifierInput);
        groupIdentifierInput.clear();
        groupIdentifierInput.sendKeys(identifierName);
    }

    public GroupsPage assertDisplayNameInputFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(groupDisplayNameInput), "Display Name input is displayed");
        return this;
    }

    public GroupsPage assertGroupIdentifierFieldLabelIsCorrect()
    {
        Assert.assertEquals(identifierFieldLabel.getText(), language.translate("adminTools.groups.newGroupProperties.identifier"));
        return this;
    }

    public GroupsPage assertDisplayNameFieldLabelIsCorrect()
    {
        Assert.assertEquals(displayNameFieldLabel.getText(), language.translate("adminTools.groups.newGroupProperties.displayName"));
        return this;
    }

    public void typeGroupDisplayName(String identifierName)
    {
        groupDisplayNameInput.clear();
        groupDisplayNameInput.sendKeys(identifierName);

    }

    public GroupsPage assertCreateNewGroupButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(createGroupOKButton), "Create group button is displayed");
        return this;
    }

    public void clickCreateGroupButton()
    {
        browser.waitUntilElementVisible(createGroupOKButton);
        browser.waitUntilElementClickable(createGroupOKButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public GroupsPage assertCreateAndCreateAnotherGroupButtonDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(createAndCreateAnotherGroupButton),
            "Create and create another button is displayed");
        return this;
    }

    public GroupsPage assertCancelCreateNewGroupButtonDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(cancelCreateGroupButton),
            "Create and create another button is displayed");
        return this;
    }

    private void clickCancelCreateNewGroupButton()
    {
        browser.waitUntilElementClickable(cancelCreateGroupButton, 5).click();
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
        browser.waitUntilElementVisible(delete);
        browser.scrollToElement(delete);
        browser.mouseOver(delete);
        browser.waitUntilElementClickable(delete).click();
        browser.waitUntilElementVisible(deleteGroupOKButton).click();
        waitUntilNotificationMessageDisappears();
        return this;
    }

    private WebElement mouseOverGroup(String groupName)
    {
        WebElement element = getItemGroup(groupName);
        browser.scrollIntoView(element);
        browser.mouseOver(element);
        return element;
    }

    public GroupsPage editGroup(GroupModel group, String newName)
    {
        WebElement groupElement = mouseOverGroup(group.getDisplayName());
        WebElement edit = groupElement.findElement(updateGroupButton);
        browser.mouseOver(edit);
        browser.waitUntilElementClickable(edit).click();
        WebElement groupEditDisplayNameInputElement = browser.waitUntilElementVisible(groupEditDisplayNameInput);
        groupEditDisplayNameInputElement.clear();
        groupEditDisplayNameInputElement.sendKeys(newName);
        browser.findElement(updateGroupOKButton).click();
        waitUntilNotificationMessageDisappears();
        if(browser.isElementDisplayed(firstPageButton))
        {
            firstPageButton.click();
        }
        return this;
    }

    public String getSearchBarText()
    {
        return searchBar.getText();
    }

    public GroupsPage assertSearchBarTextIs(String searchKeyword, int results)
    {
        Assert.assertEquals(searchBar.getText(), String.format(
            language.translate("adminTools.groups.searchResultHeader"), searchKeyword, results));
        return this;
    }

    public GroupsPage assertDeleteInfoMessageIsDisplayed(String group)
    {
        browser.waitUntilElementVisible(deleteMessage);
        Assert.assertEquals(browser.findElement(deleteMessage).getText(),
            String.format(language.translate("adminTools.groups.deleteMessage"), group));
        browser.waitUntilElementDisappears(deleteMessage);
        return this;
    }

    private String getUserFormat(UserModel user)
    {
        return String.format("%s %s (%s)",
            user.getFirstName(), user.getLastName(), user.getUsername());
    }
}