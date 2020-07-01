package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class GroupsPage extends AdminToolsPage
{
    @Autowired
    private RemoveUserFromGroupDialog removeUserFromGroupDialog;

    @Autowired
    private DeleteGroupDialog deleteGroupDialog;

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

    @FindAll (@FindBy (css = "span[class*='groups-item-group'] span[class*=item-text]"))
    private WebElement breadcrumb;

    @FindBy (css = "ul[class*='carousel'] li:nth-of-type(2) span[class*='newgroup']")
    private WebElement newSubGroupButton;

    @FindBy (css = "ul[class*='carousel'] li:nth-of-type(2) span[class*='addgroup']")
    private WebElement addGroupButton;

    @FindBy (css = "ul[class*='carousel'] li:nth-of-type(2) span[class*='adduser']")
    private WebElement addUserButton;

    @FindBy (css = ".yui-columnbrowser-item-selected")
    private List<WebElement> selectedItemsList;

    @FindAll (@FindBy (css = "ul[class*='carousel'] li:nth-of-type(2) span[class*='label']"))
    private List<WebElement> secondColumnItemsList;

    @FindAll (@FindBy (css = ".users-remove-button"))
    private List<WebElement> removeUserButtonList;

    @FindAll (@FindBy (css = "ul[class*='carousel'] li:nth-of-type(2) .groups-delete-button"))
    private List<WebElement> deleteGroupButtonList;

    @FindBy (css = "div[id*='create'] .title")
    private WebElement newGroupPanelTitle;

    @FindAll (@FindBy (css = "form[id*='create'] .crud-label"))
    private List<WebElement> newGroupPropertiesLabelsList;

    @FindBy (css = "input[id*='create-shortname']")
    private WebElement groupIdentifierInput;

    @FindBy (css = "input[id*='create-displayname']")
    private WebElement groupDisplayNameInput;

    @FindBy (css = "span.groups-newgroup-button")
    private WebElement newGroupButton;

    private By groupEditDisplayNameInput = By.cssSelector("input[id$='default-update-displayname']");

    @FindBy (css = "button[id*='creategroup-ok']")
    private WebElement createGroupOKButton;

    @FindBy (css = "button[id*='creategroup-cancel']")
    private WebElement cancelCreateGroupButton;

    @FindBy (css = ".yui-dt-rec div")
    private WebElement searchResult;

    private By groupBy = By.cssSelector("div.yui-columnbrowser-column-body span[class*=item-label]");
    private By createAndCreateAnotherGroupButton = By.cssSelector("button[id*='creategroup-another']");
    private By deleteGroupOKButton = By.cssSelector("button[id$='_default-remove-button-button']");
    private By deleteGroupCancelButton = By.cssSelector("button[id$='_default-cancel-button-button']");
    private By updateGroupOKButton = By.cssSelector("button[id$='_default-updategroup-save-button-button']");
    private By updateGroupCancelButton = By.cssSelector("button[id$='_default-updategroup-cancel-button-button']");
    private By deleteMessage = By.cssSelector("div[id='message_c'");
    private By browseNextPage = By.cssSelector("a[class='yui-pg-next']");
    private String groupRow = "//div[@class='yui-columnbrowser-column-body']//span[text()='%s']/..";

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/groups";
    }

    /**
     * @return Groups section title
     */
    public String getSectionTitle()
    {
        if (browser.isElementDisplayed(sectionTitle))
            return sectionTitle.getText();
        return sectionTitle + ", isn't displayed!";
    }

    public void clickSearchButton()
    {
        getBrowser().waitUntilElementVisible(searchButton);
        getBrowser().waitUntilElementClickable(searchButton).click();
        browser.waitUntilElementVisible(By.cssSelector("a[class=\'update\']"));
    }

    public void clickBrowseButton()
    {
        browser.waitUntilElementClickable(browseButton, properties.getImplicitWait()).click();
        browser.waitUntilElementVisible(By.cssSelector(".yui-columnbrowser-column-body"));
    }

    public void waitUntilSearchElementDisplayed()
    {
        browser.waitUntilElementVisible(searchResult);
    }

    /**
     * @return List of breadcrumb path text items
     */
    public ArrayList<String> getBreadcrumb()
    {
        ArrayList<String> breadcrumbTextList = new ArrayList<>();
        for (WebElement breadcrumbItem : breadcrumbList)
        {
            breadcrumbTextList.add(breadcrumbItem.getText());
        }
        return breadcrumbTextList;
    }

    public boolean isGroupPresentInSearchResult(String groupName)
    {
        By groupBy = By.xpath(String.format("//td[contains(@class,'yui-dt-col-displayName')]/div[text()='%s']", groupName));
        browser.waitUntilElementsVisible(groupBy);
        return browser.isElementDisplayed(groupBy);
    }

    public void writeInSearchInput(String searchItem)
    {
        searchInput.clear();
        searchInput.sendKeys(searchItem);
    }

    public void checkShowSystemGroupsCheckbox()
    {
        showSystemGroupsCheckbox.click();
    }

    /**
     * @return list of groups from first column
     */
    public void checkGroupIsInList(String name)
    {
        boolean isGroupPresent = false;
        WebElement nextPage;
        while (!isGroupPresent)
        {
            WebElement element = getItemGroup(name);
            if (element != null)
            {
                Assert.assertNotNull(element, String.format("%s group is displayed.", name));
                isGroupPresent = true;
            } else
            {
                nextPage = browser.findElement(browseNextPage);
                nextPage.click();
                browser.waitUntilElementVisible(browser.findElement(browseNextPage));
            }
        }
    }

    public WebElement getItemGroup(String name)
    {
        return browser.waitUntilElementVisible(By.xpath(String.format(groupRow, name)));
    }

    /**
     * @return items from Groups page: second column
     */
    public List<String> getSecondColumnItemsList()
    {
        List<String> items = new ArrayList<>();

        for (WebElement item : secondColumnItemsList)
        {
            items.add(item.getText());
        }

        return items;
    }

    /**
     * Click on any item from Groups page lists
     *
     * @param itemName to be clicked
     */
    public void clickItemFromList(String itemName)
    {
        WebElement item = getItemGroup(itemName);
        item.click();
    }

    /**
     * @return items that are selected
     */
    public ArrayList<String> getSelectedItems()
    {
        ArrayList<String> selectedItems = new ArrayList<>();

        for (WebElement item : selectedItemsList)
        {
            selectedItems.add(item.getText());
        }

        return selectedItems;
    }

    private void clickNewGroupButton()
    {
        browser.waitUntilElementVisible(newGroupButton);
        newGroupButton.click();
    }

    /**
     * Click "New Subgroup" button from second column header
     */
    public void clickNewSubgroupButton()
    {
        browser.waitUntilElementVisible(newSubGroupButton).click();
        browser.waitUntilElementVisible(newGroupPanelTitle);
    }

    /**
     * Click "Add Group" button from second column header
     */
    public void clickAddGroupButton()
    {
        browser.waitUntilElementVisible(addGroupButton).click();
    }

    public boolean isAddUserButtonDisplayed()
    {
        return browser.isElementDisplayed(addUserButton);
    }

    /**
     * Click "Add User" button from second column header
     */
    public void clickAddUserButton()
    {
        browser.waitUntilElementVisible(addUserButton).click();
    }

    /**
     * Click 'Remove User' button from second column
     *
     * @param userName to be removed
     * @return 'Remove User' dialog
     */
    public RemoveUserFromGroupDialog clickRemoveUserIcon(String userName)
    {
        List<String> secondColumnItems = getSecondColumnItemsList();
        int index = secondColumnItems.indexOf(userName);

        browser.mouseOver(secondColumnItemsList.get(index));
        removeUserButtonList.get(0).click();
        return (RemoveUserFromGroupDialog) removeUserFromGroupDialog.renderedPage();
    }

    /**
     * Click 'Delete Group' button for a group from second column
     *
     * @param groupName to be deleted
     * @return 'Delete Group' dialog
     */
    public DeleteGroupDialog clickDeleteGroupButtonFromSecondColumn(String groupName)
    {
        WebElement element = getItemGroup(groupName);
        element.click();
        browser.waitUntilChildElementIsPresent(element, By.cssSelector(".groups-delete-button")).click();
        return (DeleteGroupDialog) deleteGroupDialog.renderedPage();
    }

    public String getNewGroupTitle()
    {
        return newGroupPanelTitle.getText();
    }

    /**
     * @return list of New Group: Properties labels
     */
    public ArrayList<String> getNewGroupPropertiesLabels()
    {
        ArrayList<String> propertiesLabels = new ArrayList<>();

        for (WebElement propertyLabel : newGroupPropertiesLabelsList)
        {
            propertiesLabels.add(propertyLabel.getText());
        }

        return propertiesLabels;
    }

    public boolean isIdentifierInputFieldDisplayed()
    {
        return browser.isElementDisplayed(groupIdentifierInput);
    }

    public void typeGroupIdentifier(String identifierName)
    {
        browser.waitUntilElementVisible(groupIdentifierInput);
        groupIdentifierInput.clear();
        groupIdentifierInput.sendKeys(identifierName);
    }

    public boolean isDisplayNameInputFieldDisplayed()
    {
        return browser.isElementDisplayed(groupDisplayNameInput);
    }

    public void typeGroupDisplayName(String identifierName)
    {
        groupDisplayNameInput.clear();
        groupDisplayNameInput.sendKeys(identifierName);

    }

    public boolean isNewGroupButtonDisplayed()
    {
        return browser.isElementDisplayed(newGroupButton);
    }

    public boolean isAddGroupButtonDisplayed()
    {
        return browser.isElementDisplayed(addGroupButton);
    }

    public boolean isCreateNewGroupButtonDisplayed()
    {
        return browser.isElementDisplayed(createGroupOKButton);
    }

    public void clickCreateGroupButton()
    {
        browser.waitUntilElementVisible(createGroupOKButton);
        browser.waitUntilElementClickable(createGroupOKButton).click();

        this.renderedPage();
    }

    public boolean isCreateAndCreateAnotherGroupButtonDisplayed()
    {
        return browser.isElementDisplayed(createAndCreateAnotherGroupButton);
    }

    public boolean isCancelCreateNewGroupButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelCreateGroupButton);
    }

    private void clickCancelCreateNewGroupButton()
    {
        browser.waitUntilElementClickable(cancelCreateGroupButton, 5).click();
    }

    public void createNewGroup(String groupName, boolean areYouSure)
    {
        clickNewGroupButton();
        typeGroupIdentifier(groupName);
        typeGroupDisplayName(groupName);
        if (areYouSure)
        {
            clickCreateGroupButton();
        } else
        {
            clickCancelCreateNewGroupButton();
        }
    }

    public void createGroup(String groupName)
    {
        clickNewGroupButton();
        typeGroupIdentifier(groupName);
        typeGroupDisplayName(groupName);

        clickCreateGroupButton();
    }

    public void deleteGroup(String groupName, boolean areYouSure)
    {
        WebElement element = getItemGroup(groupName);
        browser.mouseOver(element);

        By deleteGroupButton = By.xpath(String.format(
            "//a[@class='yui-columnbrowser-item groups-item-group yui-columnbrowser-item-active']//span[@class='groups-delete-button']", groupName));
        browser.waitUntilElementVisible(deleteGroupButton).click();

        browser.waitUntilElementVisible(deleteGroupOKButton);
        if (areYouSure)
        {
            browser.findElement(deleteGroupOKButton).click();
        } else
        {
            browser.findElement(deleteGroupCancelButton).click();
        }
    }

    public void checkGroupIsRemoved(String name)
    {
        By groupBy = By.xpath(String.format("//div[@class='yui-columnbrowser-column-body']//span[contains(@class,'item-label') and text()='%s']", name));
        browser.waitUntilElementDisappears(groupBy, properties.getImplicitWait());

        Assert.assertFalse(browser.isElementDisplayed(groupBy), String.format("%s group is removed ", name));
    }

    public void editGroup(String groupName, String newName, boolean areYouSure)
    {
        WebElement element = getItemGroup(groupName);
        browser.mouseOver(element);

        By editGroupButton = By.xpath(String.format(
            "//a[@class='yui-columnbrowser-item groups-item-group yui-columnbrowser-item-active']//span[@class='groups-update-button']", groupName));
        browser.waitUntilElementVisible(editGroupButton).click();

        WebElement groupEditDisplayNameInputElement = browser.waitUntilElementVisible(groupEditDisplayNameInput);
        groupEditDisplayNameInputElement.clear();
        groupEditDisplayNameInputElement.sendKeys(newName);
        if (areYouSure)
        {
            browser.findElement(updateGroupOKButton).click();
            this.renderedPage();
        } else
        {
            browser.findElement(updateGroupCancelButton).click();
            this.renderedPage();
        }
    }

    public String getSeachBarText()
    {
        return searchBar.getText();
    }

    public String getDeleteMessage()
    {
        browser.waitUntilElementVisible(deleteMessage);
        return browser.findElement(deleteMessage).getText();
    }
}