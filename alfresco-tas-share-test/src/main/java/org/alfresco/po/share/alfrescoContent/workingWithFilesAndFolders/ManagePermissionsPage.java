package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.Utils;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.*;

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
    private By addUserGroupButton_ = By.cssSelector("div.add-user-group button");

    @FindBy (css = "button[id$='_default-okButton-button']")
    private WebElement saveButton;
    private By saveButton_ = By.cssSelector("button[id$='_default-okButton-button']");
    @FindBy (css = "button[id$='-cancelButton-button']")
    private WebElement cancelButton;
    private By cancelButton_ = By.cssSelector("button[id$='-cancelButton-button']");
    @FindBy (css = "div.search-text input")
    private WebElement searchUserInput;
    private By searchUserInput_ = By.cssSelector("div.search-text input");
    @FindBy (css = "div.authority-search-button button")
    private WebElement searchUserButton;
    private By searchUserButton_ = By.cssSelector("div.authority-search-button button");
    private By inheritPermissionButton = By.cssSelector("div[id$='_manage-permissions_x0023_default-inheritedButtonContainer']");
    private  By userNameLocator = By.cssSelector("td[class$='displayName']");
    private  By userRoleLocator = By.cssSelector("td[class*='role']");
    private By pageTitle_ = By.cssSelector("span[id$='_default-title']");
//    private  By addUser = By.cssSelector("span[class$='button' span button]");
    private  By inheritPermissionTable_ = By.cssSelector("div[id$='_default-inheritedPermissions']");
    private  By inheritButtonStatus = By.cssSelector("div[class$='inherited-on']");
    private  By inheritButtonStatusDisabled = By.cssSelector("div[class$='inherited-off']");
    private List<WebElement> breadcrumbList;
    @FindAll (@FindBy (css = "div[id$='_default-directPermissions'] tr[class^='yui-dt-rec ']"))
    private List<WebElement> usersAndGroupsList;
    private  By locallySetPermissionsList = By.cssSelector("div[id$='_default-directContainer'] div[id$='_default-directPermissions']");
    private  By deleteButton = By.cssSelector("div.onActionDelete");
    private  By searchInputBox = By.cssSelector("input[id$='_default-authorityFinder-search-text']");
    private  By addUserGroupWindow = By.cssSelector("div[id$='_manage-permissions_x0023_default-authorityFinder-body']");
    private  By searchButton = By.cssSelector("button[id$='_manage-permissions_x0023_default-authorityFinder-authority-search-button-button']");
    private  By roleButton = By.cssSelector("td[class$='yui-dt-col-role'] button");
    private  By noPermissionsSet = By.cssSelector("div[id$='_default-directPermissions'] td[class='yui-dt-empty'] div");

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
        return waitUntilElementIsVisible(pageTitle_).getText();
    }

    /**
     * Click on Cancel button
     */
    public DocumentLibraryPage clickCancel()
    {
        waitInSeconds(1);
        findElement(cancelButton_).click();
        waitInSeconds(1);
        return new DocumentLibraryPage(webDriver);
    }

    public void clickInheritPermissionsButton()
    {
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount)
        {
            if (findElement(inheritPermissionButton).isDisplayed())
            {
                findElement(inheritPermissionButton).findElement(By.cssSelector("button")).click();
                break;
            } else
            {
                log.info("Wait for element after refresh: " + counter);
                refresh();
                counter++;
            }
        }
    }

    public void searchAndAddUserAndGroup(String searchText)
    {
        findElement(addUserGroupButton_).click();
        List<WebElement> searchRows = new ArrayList<>();
        for (int i = 0; i<10; i++)
        {
            int count=0;
            Utils.clearAndType(findElement(searchUserInput_), searchText);
            waitInSeconds(1);
            findElement(searchUserButton_).click();
            clickElement(searchUserButton_, 2);
            waitInSeconds(1);
            By DATA_ROWS = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
            for (WebElement element : findElements(DATA_ROWS))
            {
                searchRows.add(element);
            }
            for (WebElement searchRow : searchRows)
                if (searchRow.getText().contains(searchText))
                {
                    searchRow.findElement(By.cssSelector("span[class$='button'] span button")).click();
                    waitInSeconds(1);
                    count++;
                }
            if (count==1)
            {
                break;
            }
            for (WebElement element : findElements(DATA_ROWS))
            {
                searchRows.removeAll(searchRows);
            }
        }
    }

    public boolean isPermissionAddedForUser(String userProfile)
    {
        waitInSeconds(1);
        List<WebElement> userPermissionRows = findElements(By.cssSelector("div[id$='default-directPermissions'] tbody.yui-dt-data tr"));
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
        return findElement(inheritPermissionTable_).isDisplayed();
    }

    public boolean isInheritButtonStatusEnabled()
    {
        try
        {
            return findElement(inheritButtonStatus).isDisplayed();
        } catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public boolean isInheritButtonStatusDisabled()
    {
        try
        {
            return findElement(inheritButtonStatusDisabled).isDisplayed();
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
            waitInSeconds(1);
        }
        clickAddButton(add);
    }

    public void clickAddButton(int add)

    {
        addButtonsList.get(add).click();
    }

    public void clickAreYouSureDialog(ButtonType areYouSure)
    {
        for (WebElement button : findElements(By.cssSelector("span.button-group span span button")))
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
            findElement(inheritPermissionButton).findElement(By.cssSelector("button")).click();
        }
        if (!turnOn && buttonStatusOn)
        {
            findElement(inheritPermissionButton).findElement(By.cssSelector("button")).click();
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
        WebElement header = findElement(By.cssSelector("div[id='prompt'] div[id='prompt_h']"));
        WebElement body = findElement(By.cssSelector("div[id='prompt'] div[class='bd']"));
        WebElement footer = findElement(By.cssSelector("div[id='prompt'] div[class='ft'] span[class='button-group']"));
        boolean headerText = header.getText().equals("Turn off Permission Inheritance?");
        boolean bodyText = body.getText().equals("Are you sure you do not want to inherit permissions?" + '\n' + '\n'
            + "Only local permissions will apply to this document/folder.");
        return headerText && footer.isDisplayed() && bodyText;
    }

    public void deleteUserOrGroupFromPermission(String userName)
    {
        List<WebElement> userList = waitUntilElementsAreVisible(By.cssSelector("div[id$='default-directPermissions'] tr[class^='yui-dt-rec']"));
        for (WebElement webElement : userList)
        {
            if (webElement.findElement(By.cssSelector("td[class$='displayName']")).getText().contains(userName))
            {
                mouseOver(webElement.findElement(By.xpath("//td[contains(@class, 'yui-dt-col-actions')]/div")));
                WebElement deleteDivElement = webElement.findElement(By.cssSelector("td[class*='yui-dt-col-actions'] div div.action-set"));
                findElement(By.id(deleteDivElement.getAttribute("id"))).findElement(By.cssSelector("a")).click();
                waitInSeconds(2);
            }
        }
    }

    public String getRole(String userName)
    {
        WebElement userRow = waitUntilElementIsVisible(By.xpath(String.format(userRowLocator, userName)));
        return userRow.findElement(By.xpath("//td[contains(@class, 'role')]//button|//td[contains(@class, 'role')]//span")).getText();
    }

    public String setRole(String userName, String role)
    {
        WebElement userRow = waitUntilElementIsVisible(By.xpath(String.format(userRowLocator, userName)));
        userRow.findElement(By.xpath("//td[contains(@class, 'role')]//button"));
        waitUntilElementIsVisible(By.cssSelector(".yui-menu-button-menu.visible"));
        selectOptionFromFilterOptionsList(role, findElements(By.cssSelector(".yui-menu-button-menu.visible li>a")));
        return role;
    }

    public DocumentLibraryPage returnTo(String location)
    {
        findFirstElementWithValue(breadcrumbList, location).click();
        return new DocumentLibraryPage(webDriver);
    }

    public boolean isAddUserGroupButtonDisplayed()
    {
        waitUntilElementIsVisible(addUserGroupButton_);
        return isElementDisplayed(addUserGroupButton_);
    }

    public boolean isInheritPermissionsButtonDisplayed()
    {
        waitUntilElementIsVisible(inheritPermissionButton);
        return isElementDisplayed(inheritPermissionButton);
    }

    public boolean isTheSaveButtonDisplayed()
    {
        waitUntilElementIsVisible(saveButton_);
        return isElementDisplayed(saveButton_);
    }

    public boolean isCancelButtonDisplayed()
    {
        waitUntilElementIsVisible(cancelButton_);
        return isElementDisplayed(cancelButton_);
    }

    public boolean isLocallySetPermissionsListDisplayed()
    {
        waitUntilElementIsVisible(locallySetPermissionsList);
        return isElementDisplayed(locallySetPermissionsList);
    }

    public WebElement selectRowLocallySetPermissions(String rowDetails)
    {
        waitUntilElementIsVisible(usersAndGroupsLocallySetPermissions);
        return findFirstElementWithValue(usersAndGroupsLocallySetPermissions, rowDetails);
    }

    public String getRowDetails(String details)
    {
        return selectRowLocallySetPermissions(details).getText();
    }

    public boolean isDeleteButtonAvailable(String identifier)
    {
        mouseOver(selectRowLocallySetPermissions(identifier));
        return isElementDisplayed(deleteButton);
    }

    public ManagePermissionsPage clickAddUserGroupButton()
    {
        clickElement(addUserGroupButton_);
        waitUntilElementIsVisible(addUserGroupButton_);
        return this;
    }

    public ManagePermissionsPage sendSearchInput(String userName)
    {
        Utils.clearAndType(findElement(searchInputBox), userName);
        return this;
    }

    public ManagePermissionsPage clickSearchButton()
    {
        findElement(searchButton).click();
        return this;
    }

    public WebElement selectUser(String userName)
    {
        waitUntilElementIsVisible(searchResultsList);
        return findFirstElementWithValue(searchResultsList, userName);
    }

    public ManagePermissionsPage clickAddButtonForUser(String userName)
    {
        selectUser(userName).findElement(addButton).click();
        return this;
    }

    public ManagePermissionsPage clickRoleButton(String item)
    {
        waitInSeconds(2);
        selectRowLocallySetPermissions(item).findElement(By.cssSelector("td[class$='yui-dt-col-role'] button")).click();
        return this;
    }

    public void selectRole(String role)
    {
        waitUntilElementsAreVisible(roles);
        findFirstElementWithValue(roles, role).click();
    }
    public ManagePermissionsPage select_Role(String role)
    {
        waitInSeconds(2);
        List<WebElement> roleList = findElements(roles);
        findFirstElementWithValue(roleList, role).click();
        return this;
    }
    public DocumentLibraryPage clickSave()
    {
        waitInSeconds(1);
        findElement(saveButton_).click();
        waitInSeconds(1);
        return new DocumentLibraryPage(webDriver);
    }

    public WebElement selectRowInheritedPermissions(String rowDetails)
    {
        waitUntilElementIsVisible(inheritedPermissionsUsersAndGroups);
        return findFirstElementWithValue(inheritedPermissionsUsersAndGroups, rowDetails);
    }

    public String getInheritedPermissions(String identifier)
    {
        return selectRowInheritedPermissions(identifier).getText();
    }

    public boolean isAddUsersGroupsWindowDisplayed()
    {
        waitUntilElementIsVisible(addUserGroupWindow);
        return isElementDisplayed(addUserGroupWindow);
    }
    public ManagePermissionsPage assertIsAddUserGroupWindowDisplayed()
    {
        log.info("Verify Add user group window is Displayed");
        assertTrue(isAddUsersGroupsWindowDisplayed(),"Add users groups window is not displayed");
        return this;
    }

    public void deleteUserFromPermissionsList(String identifier)
    {
        mouseOver(selectRowLocallySetPermissions(identifier));
        waitUntilElementIsVisible(deleteButton).click();
    }

    public String getLocallySetPermissionsTextWhenNoUsersAreAdded()
    {
        waitUntilElementIsVisible(noPermissionsSet);
        return findElement(noPermissionsSet).getText();
    }

    public ManagePermissionsPage assertManagePermissionPageHeaderTitleEquals(String content)
    {
        log.info("Verify the Manage Permission Page Header Title.");
        assertEquals(getTitle(), "Manage Permissions: " + content, String.format("Displayed Title %s is not matched with %s ", getTitle(), "Manage Permissions: " + content));
        return this;
    }

    public ManagePermissionsPage assertIsPermissionAddedForUser(String userName)
    {
        log.info("Verify that the Permission should be added for the User.");
        assertTrue(isPermissionAddedForUser(userName), String.format("User [%s] is not added in permissions.", userName));
        return this;
    }

    public ManagePermissionsPage assertNoPermissionAddedForUser(String userName)
    {
        log.info("Verify that the Permission should not be added for the User.");
        assertFalse(isPermissionAddedForUser(userName), String.format("User [%s] is added in permissions.", userName));
        return this;
    }

    public ManagePermissionsPage assertIsTurnOffPermissionInheritanceDialogDisplayed()
    {
        log.info("Verify that Turn off inherit permissions dialog is displayed.");
        assertTrue(isTurnOffPermissionInheritanceDialogDisplayed(), "Turn off inherit permissions dialog isn't displayed.");
        return this;
    }

    public ManagePermissionsPage assertNoInheritPermissionsTableEnabled()
    {
        log.info("Verify that Inherit Permissions section isn't displayed.");
        assertFalse(isInheritPermissionsTableEnabled(), "Inherit Permissions section is displayed.");
        return this;
    }

    public ManagePermissionsPage assertIsInheritPermissionsTableEnabled()
    {
        log.info("Verify that Inherit Permissions section is displayed.");
        assertTrue(isInheritPermissionsTableEnabled(), "Inherit Permissions section is not displayed.");
        return this;
    }

    public ManagePermissionsPage assertIsInheritButtonStatusDisabled()
    {
        log.info("Verify that Inherit Permissions button status is Disabled.");
        assertTrue(isInheritButtonStatusDisabled(), "Inherit Permissions button status is incorrect.");
        return this;
    }

    public ManagePermissionsPage assertIsInheritButtonStatusEnabled()
    {
        log.info("Verify that Inherit Permissions button status is Enabled.");
        assertTrue(isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");
        return this;
    }

    public ManagePermissionsPage assertUserRoleAtPermissionPageEquals(String role, String userName)
    {
        log.info("Verify the user role at Manage Permission Page.");
        assertEquals(getRole(userName), role, String.format("User [%s] has incorrect role [%s].", userName, role));
        return this;
    }

    // todo into separate enum
    public enum ButtonType
    {
        Yes, No
    }
}
