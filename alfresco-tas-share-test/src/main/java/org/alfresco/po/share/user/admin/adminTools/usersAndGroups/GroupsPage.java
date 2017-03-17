package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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
    @FindBy(css = "label[for*='default-search-text']")
    private WebElement sectionTitle;

    @RenderWebElement
    @FindBy(css = "input[id*='show-all']")
    private WebElement showSystemGroupsCheckbox;

    @RenderWebElement
    @FindBy(css = "input[id*='default-search-text']")
    private WebElement searchInput;

    @RenderWebElement
    @FindBy(css = ".search-text button[id*='search']")
    private WebElement searchButton;

    @RenderWebElement
    @FindBy(css = "button[id*='browse']")
    private WebElement browseButton;

    @FindAll(@FindBy(css = "div[id*='breadcrumb'] span[class*='groups'] span[class*='item-text']"))
    private List<WebElement> breadcrumbList;

    @FindBy(css = "ul[class*='carousel'] li:nth-of-type(2) span[class*='newgroup']")
    private WebElement newSubGroupButton;

    @FindBy(css = "ul[class*='carousel'] li:nth-of-type(2) span[class*='addgroup']")
    private WebElement addGroupButton;

    @FindBy(css = "ul[class*='carousel'] li:nth-of-type(2) span[class*='adduser']")
    private WebElement addUserButton;

    @FindBy(css = ".yui-columnbrowser-item-selected")
    private List<WebElement> selectedItemsList;

    @FindAll(@FindBy(css = "span[class*='item-label']"))
    private List<WebElement> itemsList;

    @FindAll(@FindBy(css = "ul[class*='carousel'] li:nth-of-type(2) span[class*='label']"))
    private List<WebElement> secondColumnItemsList;

    @FindAll(@FindBy(css = ".users-remove-button"))
    private List<WebElement> removeUserButtonList;

    @FindAll(@FindBy(css = "ul[class*='carousel'] li:nth-of-type(2) .groups-delete-button"))
    private List<WebElement> deleteGroupButtonList;

    @FindBy(css = "div[id*='create'] .title")
    private WebElement newGroupPanelTitle;

    @FindAll(@FindBy(css = "form[id*='create'] .crud-label"))
    private List<WebElement> newGroupPropertiesLabelsList;

    @FindBy(css = "input[id*='create-shortname']")
    private WebElement groupIdentifierInput;

    @FindBy(css = "input[id*='create-displayname']")
    private WebElement groupDisplayNameInput;

    private By groupEditDisplayNameInput = By.cssSelector("input[id$='default-update-displayname']");
    private By newGroupButton = By.cssSelector("span[title='New Group']");
    private By createGroupOKButton = By.cssSelector("button[id*='creategroup-ok']");
    private By cancelCreateGroupButton = By.cssSelector("button[id*='creategroup-cancel']");
    private By createAndCreateAnotherGroupButton = By.cssSelector("button[id*='creategroup-another']");
    private By deleteGroupOKButton = By.cssSelector("button[id$='_default-remove-button-button']");
    private By deleteGroupCancelButton = By.cssSelector("button[id$='_default-cancel-button-button']");
    private By updateGroupOKButton = By.cssSelector("button[id$='_default-updategroup-save-button-button']");
    private By updateGroupCancelButton = By.cssSelector("button[id$='_default-updategroup-cancel-button-button']");
    
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
        browser.waitUntilElementClickable(searchButton, 5).click();
    }

    public void clickBrowseButton()
    {
        browser.waitUntilElementClickable(browseButton, 5).click();
        browser.waitUntilElementVisible(By.cssSelector(".yui-columnbrowser-column-body"));
    }

    /**
     * @return List of breadcrumb path text items
     */
    public ArrayList<String> getBreadcrumb()
    {
        this.renderedPage();
        ArrayList<String> breadcrumbTextList = new ArrayList<>();
        for (WebElement breadcrumbItem : breadcrumbList)
        {
            breadcrumbTextList.add(breadcrumbItem.getText());
        }
        return breadcrumbTextList;
    }

    public boolean isGroupPresentInSearchResult(String groupName)
    {
        return browser.isElementDisplayed(By.xpath(String.format("//tbody/tr/td/div[text()='%s']", groupName)));
    }

    public void writeInSearchInput(String searchItem)
    {
        searchInput.clear();
        browser.waitInSeconds(2);
        searchInput.sendKeys(searchItem);
        browser.waitInSeconds(2);
    }

    public void checkShowSystemGroupsCheckbox()
    {
        showSystemGroupsCheckbox.click();
    }

    /**
     * @param itemName whose presence to be checked
     * @param columnNumber number of the column in which the itemName is verified. Possible values: 1, 2
     * @return true if item is displayed
     */
    public boolean isItemDisplayedInSpecifiedColumn(String itemName, int columnNumber)
    {
        this.renderedPage();
        boolean displayed = false;
        List<String> firstColumn = getFirstColumnGroupsList();
        List<String> secondColumn = getSecondColumnItemsList();

        switch (columnNumber)
        {
            case 1:
                displayed = firstColumn.contains(itemName);
                break;
            case 2:
                displayed = secondColumn.contains(itemName);
                break;
        }

        return displayed;
    }

    /**
     * @return list of groups from first column
     */
    public List<String> getFirstColumnGroupsList()
    {
        int refreshCount = 1;
        List<String> groupList = new ArrayList<>();
        List<WebElement> listOfGroups = new ArrayList<>();
        while (refreshCount <= 5)
        {
            try
            {
                listOfGroups = browser.waitUntilElementsVisible(By.cssSelector("div a[class*='columnbrowser-item groups-item-group'] span[class$='item-label']"));
                break;
            }
            catch (TimeoutException | NoSuchElementException e)
            {
                browser.refresh();
                //browser.waitInSeconds(6);
                refreshCount++;
            }
        }
        for (WebElement eachGroup : listOfGroups)
        {
            groupList.add(eachGroup.getText());
        }
        return groupList;
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
        for (WebElement item : itemsList)
        {
            if (item.getText().equals(itemName))
                item.click();
        }
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
        browser.waitUntilElementClickable(newGroupButton, 5).click();
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

    public boolean isAddUSerButtonDisplayed()
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
        removeUserButtonList.get(index).click();
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
        List<String> items = getSecondColumnItemsList();
        int index = items.indexOf(groupName);

        browser.mouseOver(secondColumnItemsList.get(index));
        deleteGroupButtonList.get(index).click();

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
        browser.waitInSeconds(1);
    }

    public boolean isDisplayNameInputFieldDisplayed()
    {
        return browser.isElementDisplayed(groupDisplayNameInput);
    }

    public void typeGroupDisplayName(String identifierName)
    {
        groupDisplayNameInput.clear();
        browser.waitInSeconds(2);
        groupDisplayNameInput.sendKeys(identifierName);
        browser.waitInSeconds(2);
    }

    public boolean isCreateNewGroupButtonDisplayed()
    {
        return browser.isElementDisplayed(createGroupOKButton);
    }

    public void clickCreateGroupButton()
    {
        browser.waitUntilElementClickable(createGroupOKButton, 5).click();
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
        clickBrowseButton();
        clickNewGroupButton();
        typeGroupIdentifier(groupName);
        typeGroupDisplayName(groupName);
        if (areYouSure)
        {
            clickCreateGroupButton();
        }
        else
        {
            clickCancelCreateNewGroupButton();
        }
    }

    public void deleteGroup(String groupName, boolean areYouSure)
    {
        By deleteGroupButton = By.xpath(String.format("//span[contains(@class,'item-label') and text()='%s']/..//span[@title='Delete Group']", groupName));
        browser.mouseOver(browser.findFirstElementWithValue(
                browser.waitUntilElementsVisible(By.cssSelector("div a[class*='columnbrowser-item groups-item-group'] span[class$='item-label']")), groupName));
        browser.waitUntilElementVisible(deleteGroupButton).click();
        browser.waitInSeconds(3);
        if (areYouSure)
        {
            browser.findFirstDisplayedElement(deleteGroupOKButton).click();
        }
        else
        {
            browser.findFirstDisplayedElement(deleteGroupCancelButton).click();
        }
    }

    public void editGroup(String groupName, String newName, boolean areYouSure)
    {
        By editGroupButton = By.xpath(String.format("//span[contains(@class,'item-label') and text()='%s']/..//span[@title='Edit Group']", groupName));
        browser.mouseOver(browser.findFirstElementWithValue(
                browser.findElements(By.cssSelector("div a[class*='columnbrowser-item groups-item-group'] span[class$='item-label']")), groupName));
        browser.waitUntilElementClickable(editGroupButton, 4).click();
        WebElement groupEditDisplayNameInputElement = browser.waitUntilElementVisible(groupEditDisplayNameInput);
        groupEditDisplayNameInputElement.clear();
        browser.waitInSeconds(1);
        groupEditDisplayNameInputElement.sendKeys(newName);
        if (areYouSure)
        {
            browser.waitUntilElementClickable(updateGroupOKButton, 4).click();
        }
        else
        {
            browser.waitUntilElementClickable(updateGroupCancelButton, 4).click();
        }
    }
}