package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.Utils;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class ManagePermissionsPage extends SiteCommon<ManagePermissionsPage>
{
    private final String userRowLocator = "//div[contains(@id, 'default-directPermissions')]//td//div[contains(text(), '%s')]/../..";
    @FindAll (@FindBy (css = "button[id*='yui-gen']"))
    protected List<WebElement> addButtonsList;

    @FindBy (css = "div.add-user-group button")
    private WebElement addUserGroupButton;

    @FindBy (css = "button[id$='_default-okButton-button']")
    private WebElement saveButton;
    @FindBy (css = "button[id$='-cancelButton-button']")
    private WebElement cancelButton;
    @FindBy (css = "div.search-text input")
    private WebElement searchUserInput;
    @FindBy (css = "div.authority-search-button button")
    private WebElement searchUserButton;
    @FindBy (css = "div[id$='_manage-permissions_x0023_default-inheritedButtonContainer']")
    private WebElement inheritPermissionButton;
    @FindBy (css = "td[class$='displayName']")
    private WebElement userNameLocator;
    @FindBy (css = "td[class*='role']")
    private WebElement userRoleLocator;
    @FindBy (css = "span[id$='_default-title']")
    private WebElement pageTitle;
    @FindBy (css = "span[class$='button' span button]")
    private WebElement addUser;
    @FindBy (css = "div[id$='_default-inheritedPermissions']")
    private WebElement inheritPermissionTable;
    @FindBy (css = "div[class$='inherited-on']")
    private WebElement inheritButtonStatus;
    @FindBy (css = "span.folder-link a")
    private List<WebElement> breadcrumbList;
    @FindAll (@FindBy (css = "div[id$='_default-directPermissions'] tr[class^='yui-dt-rec ']"))
    private List<WebElement> usersAndGroupsList;
    @FindBy (css = "div[id$='_default-directContainer'] div[id$='_default-directPermissions']")
    private WebElement locallySetPermissionsList;
    @FindBy (css = "div.onActionDelete")
    private WebElement deleteButton;
    @FindBy (css = "input[id$='_default-authorityFinder-search-text']")
    private WebElement searchInputBox;
    @FindBy (css = "div[id$='_manage-permissions_x0023_default-authorityFinder-body']")
    private WebElement addUserGroupWindow;
    @FindBy (css = "button[id$='_manage-permissions_x0023_default-authorityFinder-authority-search-button-button']")
    private WebElement searchButton;
    @FindBy (css = "td[class$='yui-dt-col-role'] button")
    private WebElement roleButton;
    @FindBy (css = "div[id$='_default-directPermissions'] td[class='yui-dt-empty'] div")
    private WebElement noPermissionsSet;

    private final By searchResultsList = By.cssSelector("td[class$='yui-dt-col-fullName'] span");
    private final By addButton = By.xpath("//button[text()='Add ']");
    private final By roles = By.cssSelector("div.yui-module.yui-overlay.yuimenu.yui-button-menu.yui-menu-button-menu.visible li");
    private final By usersAndGroupsLocallySetPermissions = By.cssSelector("div[id$='_default-directPermissions'] tr[class^='yui-dt-rec ']");
    private final By inheritedPermissionsUsersAndGroups = By.cssSelector("div[id$='_default-inheritedPermissions'] tr[class^='yui-dt-rec ']");

    public ManagePermissionsPage(ThreadLocal<WebDriver> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/manage-permissions?nodeRef=workspace://SpacesStore/43f06861-c507-470a-ba0c-08f46a53d1b9", getCurrentSiteName());
    }

    public String getTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(pageTitle).getText();
    }

    /**
     * Click on Cancel button
     */
    public DocumentLibraryPage clickCancel()
    {
        cancelButton.click();
        return new DocumentLibraryPage(webDriver);
    }

    public void clickInheritPermissionsButton()
    {
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount)
        {
            if (inheritPermissionButton.isDisplayed())
            {
                inheritPermissionButton.findElement(By.cssSelector("button")).click();
                break;
            } else
            {
                log.info("Wait for element after refresh: " + counter);
                webElementInteraction.refresh();
                counter++;
            }
        }
    }

    public void searchAndAddUserAndGroup(String searchText)
    {
        addUserGroupButton.click();
        List<WebElement> searchRows = new ArrayList<>();
        Utils.clearAndType(searchUserInput, searchText);
        searchUserButton.click();
        By DATA_ROWS = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
        for (WebElement element : webElementInteraction.findElements(DATA_ROWS))
        {
            searchRows.add(element);
        }
        for (WebElement searchRow : searchRows)
            if (searchRow.getText().contains(searchText))
                searchRow.findElement(By.cssSelector("span[class$='button'] span button")).click();
    }

    public boolean isPermissionAddedForUser(String userProfile)
    {
        List<WebElement> userPermissionRows = webElementInteraction.findElements(By.cssSelector("div[id$='default-directPermissions'] tbody.yui-dt-data tr"));
        for (WebElement userPermissionRow : userPermissionRows)
        {
            String name = userPermissionRow.findElement(By.cssSelector("td[class$='-displayName']")).getText();
            if (name.contains(userProfile) || name.contains("firstName lastName"))
                return true;
        }
        return false;
    }

    public boolean isInheritPermissionsTableEnabled()
    {
        return inheritPermissionTable.isDisplayed();
    }

    public boolean isInheritButtonStatusEnabled()
    {
        try
        {
            return inheritButtonStatus.isDisplayed();
        } catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public void searchAndAddUserOrGroup(String searchQuery, int add)
    {
        addUserGroupButton.click();
        searchUserInput.clear();
        searchUserInput.sendKeys(searchQuery);
        for (int i = 1; i < 5; i++)
        {
            searchUserButton.click();
            webElementInteraction.waitInSeconds(1);
        }
        clickAddButton(add);
    }

    public void clickAddButton(int add)

    {
        addButtonsList.get(add).click();
    }

    public void clickAreYouSureDialog(ButtonType areYouSure)
    {
        for (WebElement button : webElementInteraction.findElements(By.cssSelector("span.button-group span span button")))
        {
            if (areYouSure.toString().equals(button.getText()))
            {
                button.click();
                break;
            }
        }
    }

    public ManagePermissionsPage toggleInheritPermissions(boolean turnOn, ButtonType areYouSure)
    {
        boolean buttonStatusOn = isInheritButtonStatusEnabled();
        if (turnOn && !buttonStatusOn)
        {
            inheritPermissionButton.findElement(By.cssSelector("button")).click();
        }
        if (!turnOn && buttonStatusOn)
        {
            inheritPermissionButton.findElement(By.cssSelector("button")).click();
            try
            {
                clickAreYouSureDialog(areYouSure);
            } catch (Exception e)
            {
                //carry on
            }
        }
        return this;
    }

    public boolean isTurnOffPermissionInheritanceDialogDisplayed()
    {
        WebElement header = webElementInteraction.findElement(By.cssSelector("div[id='prompt'] div[id='prompt_h']"));
        WebElement body = webElementInteraction.findElement(By.cssSelector("div[id='prompt'] div[class='bd']"));
        WebElement footer = webElementInteraction.findElement(By.cssSelector("div[id='prompt'] div[class='ft'] span[class='button-group']"));
        boolean headerText = header.getText().equals("Turn off Permission Inheritance?");
        boolean bodyText = body.getText().equals("Are you sure you do not want to inherit permissions?" + '\n' + '\n'
            + "Only local permissions will apply to this document/folder.");
        return headerText && footer.isDisplayed() && bodyText;
    }

    public void deleteUserOrGroupFromPermission(String userName)
    {
        List<WebElement> userList = webElementInteraction.waitUntilElementsAreVisible(By.cssSelector("div[id$='default-directPermissions'] tr[class^='yui-dt-rec']"));
        for (WebElement webElement : userList)
        {
            if (webElement.findElement(By.cssSelector("td[class$='displayName']")).getText().contains(userName))
            {
                webElementInteraction.mouseOver(webElement.findElement(By.xpath("//td[contains(@class, 'yui-dt-col-actions')]/div")));
                WebElement deleteDivElement = webElement.findElement(By.cssSelector("td[class*='yui-dt-col-actions'] div div.action-set"));
                webElementInteraction.findElement(By.id(deleteDivElement.getAttribute("id"))).findElement(By.cssSelector("a"));
            }
        }
    }

    public String getRole(String userName)
    {
        WebElement userRow = webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(userRowLocator, userName)));
        return userRow.findElement(By.xpath("//td[contains(@class, 'role')]//button|//td[contains(@class, 'role')]//span")).getText();
    }

    public String setRole(String userName, String role)
    {
        WebElement userRow = webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(userRowLocator, userName)));
        userRow.findElement(By.xpath("//td[contains(@class, 'role')]//button"));
        webElementInteraction.waitUntilElementIsVisible(By.cssSelector(".yui-menu-button-menu.visible"));
        webElementInteraction.selectOptionFromFilterOptionsList(role, webElementInteraction.findElements(By.cssSelector(".yui-menu-button-menu.visible li>a")));
        return role;
    }

    public DocumentLibraryPage returnTo(String location)
    {
        webElementInteraction.findFirstElementWithValue(breadcrumbList, location).click();
        return new DocumentLibraryPage(webDriver);
    }

    public boolean isAddUserGroupButtonDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(addUserGroupButton);
        return webElementInteraction.isElementDisplayed(addUserGroupButton);
    }

    public boolean isInheritPermissionsButtonDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(inheritPermissionButton);
        return webElementInteraction.isElementDisplayed(inheritPermissionButton);
    }

    public boolean isTheSaveButtonDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(saveButton);
        return webElementInteraction.isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(cancelButton);
        return webElementInteraction.isElementDisplayed(cancelButton);
    }

    public boolean isLocallySetPermissionsListDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(locallySetPermissionsList);
        return webElementInteraction.isElementDisplayed(locallySetPermissionsList);
    }

    public WebElement selectRowLocallySetPermissions(String rowDetails)
    {
        webElementInteraction.waitUntilElementIsVisible(usersAndGroupsLocallySetPermissions);
        return webElementInteraction.findFirstElementWithValue(usersAndGroupsLocallySetPermissions, rowDetails);
    }

    public String getRowDetails(String details)
    {
        return selectRowLocallySetPermissions(details).getText();
    }

    public boolean isDeleteButtonAvailable(String identifier)
    {
        webElementInteraction.mouseOver(selectRowLocallySetPermissions(identifier));
        return webElementInteraction.isElementDisplayed(deleteButton);
    }

    public void clickAddUserGroupButton()
    {
        webElementInteraction.clickElement(addUserGroupButton);
        webElementInteraction.waitUntilElementIsVisible(addUserGroupWindow);
    }

    public void sendSearchInput(String userName)
    {
        Utils.clearAndType(searchInputBox, userName);
    }

    public void clickSearchButton()
    {
        searchButton.click();
    }

    public WebElement selectUser(String userName)
    {
        webElementInteraction.waitUntilElementIsVisible(searchResultsList);
        return webElementInteraction.findFirstElementWithValue(searchResultsList, userName);
    }

    public ManagePermissionsPage clickAddButtonForUser(String userName)
    {
        selectUser(userName).findElement(addButton).click();
        return this;
    }

    public void clickRoleButton(String item)
    {
        webElementInteraction.clickElement(roleButton);
        selectRowLocallySetPermissions(item).findElement(By.cssSelector("td[class$='yui-dt-col-role'] button")).click();
    }

    public void selectRole(String role)
    {
        webElementInteraction.waitUntilElementsAreVisible(roles);
        webElementInteraction.findFirstElementWithValue(roles, role).click();
    }

    public DocumentLibraryPage clickSave()
    {
        saveButton.click();
        return new DocumentLibraryPage(webDriver);
    }

    public WebElement selectRowInheritedPermissions(String rowDetails)
    {
        webElementInteraction.waitUntilElementIsVisible(inheritedPermissionsUsersAndGroups);
        return webElementInteraction.findFirstElementWithValue(inheritedPermissionsUsersAndGroups, rowDetails);
    }

    public String getInheritedPermissions(String identifier)
    {
        return selectRowInheritedPermissions(identifier).getText();
    }

    public boolean isAddUsersGroupsWindowDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(addUserGroupWindow);
        return webElementInteraction.isElementDisplayed(addUserGroupWindow);
    }

    public void deleteUserFromPermissionsList(String identifier)
    {
        webElementInteraction.mouseOver(selectRowLocallySetPermissions(identifier));
        webElementInteraction.waitUntilElementIsVisible(deleteButton).click();
    }

    public String getLocallySetPermissionsTextWhenNoUsersAreAdded()
    {
        webElementInteraction.waitUntilElementIsVisible(noPermissionsSet);
        return noPermissionsSet.getText();
    }

    // todo into separate enum
    public enum ButtonType
    {
        Yes, No
    }
}
