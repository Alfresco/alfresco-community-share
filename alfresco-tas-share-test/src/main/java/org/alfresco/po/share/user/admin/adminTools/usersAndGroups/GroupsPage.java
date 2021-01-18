package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GroupsPage extends SharePage2<GroupsPage>
{
    private final By sectionTitle = By.cssSelector("label[for*='default-search-text']");
    private final By searchInput = By.cssSelector("div[style='display: block; opacity: 1; visibility: visible;'] input[id*='default-search-text']");
    private final By searchButton = By.cssSelector(".search-text button[id*='search']");
    private final By browseButton = By.cssSelector("button[id*='browse']");
    private final By searchBar = By.cssSelector("div[id*=default-search-bar-text]");
    private final By breadcrumbList = By.cssSelector("div[id*='breadcrumb'] span[class*='groups'] span[class*='item-text']");
    private final By newGroupPanelTitle = By.cssSelector("div[id*='create'] .title");
    private final By identifierFieldLabel = By.cssSelector(".create-main .field-row:nth-of-type(2) .crud-label");
    private final By displayNameFieldLabel = By.cssSelector(".create-main .field-row:nth-of-type(4) .crud-label");
    private final By groupIdentifierInput = By.cssSelector("input[id*='create-shortname']");
    private final By groupDisplayNameInput = By.cssSelector("input[id*='create-displayname']");
    private final By groupEditDisplayNameInput = By.cssSelector("input[id$='default-update-displayname']");
    private final By createGroupOKButton = By.cssSelector("button[id*='creategroup-ok']");
    private final By cancelCreateGroupButton = By.cssSelector("button[id*='creategroup-cancel']");
    private final By nextPageButton = By.cssSelector("a[class='yui-pg-next']");
    private final By firstPageButton = By.cssSelector("a[class='yui-pg-first']");
    private final By currentPage = By.cssSelector(".yui-pg-current");
    private final By searchResultTable = By.cssSelector(".yui-dt-data > tr");
    private final By groupBody = By.cssSelector(".yui-columnbrowser-column-body");
    private final By createAndCreateAnotherGroupButton = By.cssSelector("button[id*='creategroup-another']");
    private final By deleteGroupOKButton = By.cssSelector("button[id$='_default-remove-button-button']");
    private final By updateGroupOKButton = By.cssSelector("button[id$='_default-updategroup-save-button-button']");private final By updateGroupCancelButton = By.cssSelector("button[id$='_default-updategroup-cancel-button-button']");
    private final By deleteGroupButton = By.cssSelector(".groups-delete-button");
    private final By updateGroupButton = By.cssSelector(".groups-update-button");
    private final By searchShortName = By.cssSelector("td[headers$='shortName ']");
    private final By searchResults = By.cssSelector(".yui-dt-data > tr");
    private final By removeUser = By.cssSelector(".users-remove-button");

    private final String groupRow = "//div[@class='yui-columnbrowser-column-body']//span[text()='%s']/..";
    private final String columnGroupValues = "ul[class*='carousel'] li:nth-of-type(%s) span[class*='label']";
    private final String createSubGroupButton = "ul[class*='carousel'] li:nth-of-type(%s) span[class*='newgroup']";
    private final String addSubGroupButton = "ul[class*='carousel'] li:nth-of-type(%s) span[class*='addgroup']";
    private final String addUserGroupButton = "ul[class*='carousel'] li:nth-of-type(%s) span[class*='adduser']";

    public GroupsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/groups";
    }

    public GroupsPage assertSectionTitleEquals(String expectedSectionTitle)
    {
        webElementInteraction.waitUntilElementIsVisible(sectionTitle, 3000);
        assertEquals(webElementInteraction.findElement(sectionTitle).getText(),
            expectedSectionTitle, String.format("Section title not equals %s ", expectedSectionTitle));
        return this;
    }

    public GroupsPage clickSearch()
    {
        webElementInteraction.clickElement(searchButton);
        return this;
    }

    public GroupsPage clickBrowse()
    {
        webElementInteraction.clickElement(browseButton);
        return this;
    }

    public GroupsPage waitUntilSearchElementDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(searchResultTable, 3000);
        return this;
    }

    public List<String> getBreadcrumb()
    {
        return webElementInteraction.waitUntilElementsAreVisible(breadcrumbList).stream().map(WebElement::getText).collect(Collectors.toCollection(ArrayList::new));
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
        webElementInteraction.waitUntilElementsAreVisible(searchResultTable);
        webElementInteraction.waitUntilElementsAreVisible(searchShortName);
        List<WebElement> results = webElementInteraction.findElements(searchResults);
        for(WebElement searchedGroup : results)
        {
            webElementInteraction.waitUntilElementIsVisible(searchResultTable);
            webElementInteraction.waitUntilElementIsVisible(searchShortName);
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
        webElementInteraction.waitUntilElementIsVisible(searchInput);
        webElementInteraction.clearAndType(searchInput, searchItem);
        return this;
    }

    public GroupsPage assertGroupIsDisplayed(GroupModel groupModel)
    {
        assertTrue(webElementInteraction.isElementDisplayed(getItemGroup(groupModel.getDisplayName())),
            String.format("Group %s was found", groupModel.getDisplayName()));
        return this;
    }

    public GroupsPage assertGroupIsNotDisplayed(GroupModel groupModel)
    {
        assertFalse(isGroupDisplayed(groupModel.getDisplayName()),
            String.format("Group %s was found", groupModel.getDisplayName()));
        return this;
    }

    public GroupsPage assertUserIsNotDisplayed(UserModel userModel)
    {
        webElementInteraction.waitUntilElementDisappears(By.xpath(String.format(groupRow, getUserFormat(userModel))));
        assertFalse(isGroupDisplayed(getUserFormat(userModel)),
            String.format("User %s was found", userModel.getUsername()));
        return this;
    }

    private boolean isGroupDisplayed(String groupName)
    {
        return webElementInteraction.isElementDisplayed(By.xpath(String.format(groupRow, groupName)));
    }

    public WebElement getItemGroup(String name)
    {
        boolean found = isGroupDisplayed(name);
        int pageCount = 2;
        while(!found)
        {
            LOG.info("Move to next group page");
            if(webElementInteraction.isElementDisplayed(nextPageButton))
            {
                webElementInteraction.clickElement(nextPageButton);
                webElementInteraction.waitUntilElementContainsText(webElementInteraction.findElement(currentPage), String.format("( %s of ", pageCount));
                found = isGroupDisplayed(name);
                pageCount++;
            }
            else
            {
                break;
            }
        }
        return webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(groupRow, name)));
    }

    private void waitForColumnGroups(int column)
    {
        By columnElement = By.cssSelector(String.format(columnGroupValues, column));
        webElementInteraction.waitUntilElementIsPresent(columnElement);
        webElementInteraction.waitUntilElementsAreVisible(columnElement);
    }

    public List<String> getColumnGroups(int column)
    {
        List<WebElement> values = new ArrayList<>();
        try
        {
            values = webElementInteraction.findElements(By.cssSelector(String.format(columnGroupValues, column)));
        }
        catch (NoSuchElementException e)
        {
            LOG.info(String.format("No values found in column %s", column));
        }
        return values.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public GroupsPage assertColumnContainsGroup(int columnNr, String groupName)
    {
        webElementInteraction.waitUntilElementIsVisible(By.cssSelector(String.format(columnGroupValues, columnNr)));
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
        assertFalse(getColumnGroups(columnNr).contains(groupName),
            String.format("Group %s was found in column %s", groupName, columnNr));
        return this;
    }

    public GroupsPage selectGroup(String itemName)
    {
        LOG.info("Select group: {}", itemName);
        WebElement groupToClick = getItemGroup(itemName);
        webElementInteraction.mouseOver(groupToClick);
        webElementInteraction.clickElement(groupToClick);
        webElementInteraction.waitUntilElementHasAttribute(webElementInteraction.findElement
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
        webElementInteraction.clickElement(By.cssSelector(String.format(createSubGroupButton, column)));
        webElementInteraction.waitUntilElementIsVisible(newGroupPanelTitle);
        return this;
    }

    public AddGroupDialog clickAddGroup(int groupColumn)
    {
        webElementInteraction.clickElement(By.cssSelector(String.format(addSubGroupButton, groupColumn)));
        return new AddGroupDialog(webDriver);
    }

    public GroupsPage assertAddUserButtonIsDisplayed(int groupColumn)
    {
        WebElement addUser = webElementInteraction.waitUntilElementIsVisible(By.cssSelector(String.format(addUserGroupButton, groupColumn)));
        assertTrue(webElementInteraction.isElementDisplayed(addUser), "Add user button is displayed");
        return this;
    }

    public AddUserDialog clickAddUser(int groupColumn)
    {
        webElementInteraction.clickElement(By.cssSelector(String.format(addUserGroupButton, groupColumn)));
        return new AddUserDialog(webDriver);
    }

    public RemoveUserFromGroupDialog clickRemoveUser(UserModel user)
    {
        WebElement groupElement = mouseOverGroup(getUserFormat(user));
        WebElement remove = groupElement.findElement(removeUser);
        webElementInteraction.scrollToElement(remove);
        webElementInteraction.mouseOver(remove);
        webElementInteraction.clickElement(remove);

        return new RemoveUserFromGroupDialog(webDriver);
    }

    public DeleteGroupDialog clickDelete(String groupName)
    {
        WebElement groupElement = mouseOverGroup(groupName);
        WebElement delete = groupElement.findElement(deleteGroupButton);
        webElementInteraction.mouseOver(delete);
        webElementInteraction.clickElement(delete);
        return new DeleteGroupDialog(webDriver);
    }

    public GroupsPage assertNewGroupTitleIsDisplayed()
    {
        assertEquals(webElementInteraction.waitUntilElementIsVisible(newGroupPanelTitle).getText(),
            language.translate("adminTools.groups.newGroupPanelTitle"), "New Group title is displayed");
        return this;
    }

    public GroupsPage assertIdentifierInputFieldIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(groupIdentifierInput), "Identifier input is displayed");
        return this;
    }

    public void typeGroupIdentifier(String identifierName)
    {
        webElementInteraction.waitUntilElementIsVisible(groupIdentifierInput);
        webElementInteraction.clearAndType(groupIdentifierInput, identifierName);
    }

    public GroupsPage assertDisplayNameInputFieldIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(groupDisplayNameInput), "Display Name input is displayed");
        return this;
    }

    public GroupsPage assertGroupIdentifierFieldLabelIsCorrect()
    {
        assertEquals(webElementInteraction.findElement(identifierFieldLabel).getText(), language.translate("adminTools.groups.newGroupProperties.identifier"));
        return this;
    }

    public GroupsPage assertDisplayNameFieldLabelIsCorrect()
    {
        assertEquals(webElementInteraction.findElement(displayNameFieldLabel).getText(), language.translate("adminTools.groups.newGroupProperties.displayName"));
        return this;
    }

    public void typeGroupDisplayName(String identifierName)
    {
        webElementInteraction.clearAndType(groupDisplayNameInput, identifierName);
    }

    public GroupsPage assertCreateNewGroupButtonIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(createGroupOKButton), "Create group button is displayed");
        return this;
    }

    public void clickCreateGroupButton()
    {
        webElementInteraction.clickElement(createGroupOKButton);
        waitUntilNotificationMessageDisappears();
    }

    public GroupsPage assertCreateAndCreateAnotherGroupButtonDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(createAndCreateAnotherGroupButton), "Create and create another button is displayed");
        return this;
    }

    public GroupsPage assertCancelCreateNewGroupButtonDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(cancelCreateGroupButton), "Create and create another button is displayed");
        return this;
    }

    public GroupsPage createGroup(GroupModel groupModel)
    {
        typeGroupIdentifier(groupModel.getGroupIdentifier());
        typeGroupDisplayName(groupModel.getDisplayName());
        clickCreateGroupButton();
        return this;
    }

    public GroupsPage delete(GroupModel groupModel)
    {
        WebElement groupElement = mouseOverGroup(groupModel.getDisplayName());
        WebElement delete = groupElement.findElement(deleteGroupButton);
        webElementInteraction.scrollToElement(delete);
        webElementInteraction.mouseOver(delete);
        webElementInteraction.clickElement(delete);
        webElementInteraction.clickElement(deleteGroupOKButton);
        waitUntilNotificationMessageDisappears();

        return this;
    }

    private WebElement mouseOverGroup(String groupName)
    {
        WebElement element = getItemGroup(groupName);
        webElementInteraction.scrollIntoView(element);
        webElementInteraction.mouseOver(element);
        return element;
    }

    public GroupsPage editGroup(GroupModel group, String newName)
    {
        WebElement groupElement = mouseOverGroup(group.getDisplayName());
        WebElement edit = groupElement.findElement(updateGroupButton);
        webElementInteraction.mouseOver(edit);
        webElementInteraction.clickElement(edit);
        WebElement groupEditDisplayNameInputElement =  webElementInteraction.waitUntilElementIsVisible(groupEditDisplayNameInput);
        webElementInteraction.clearAndType(groupEditDisplayNameInputElement, newName);
        webElementInteraction.clickElement(updateGroupOKButton);
        waitUntilNotificationMessageDisappears();
        if( webElementInteraction.isElementDisplayed(firstPageButton))
        {
            webElementInteraction.clickElement(firstPageButton);
        }
        return this;
    }

    public GroupsPage assertSearchBarTextIs(String searchKeyword, int results)
    {
        assertEquals(webElementInteraction.findElement(searchBar).getText(), String.format(
            language.translate("adminTools.groups.searchResultHeader"), searchKeyword, results));
        return this;
    }

    public GroupsPage assertDeleteInfoMessageIsDisplayed(String group)
    {
        assertEquals(notificationMessageThread.get(),
            String.format(language.translate("adminTools.groups.deleteMessage"), group));
        return this;
    }

    private String getUserFormat(UserModel user)
    {
        return String.format("%s %s (%s)",
            user.getFirstName(), user.getLastName(), user.getUsername());
    }
}