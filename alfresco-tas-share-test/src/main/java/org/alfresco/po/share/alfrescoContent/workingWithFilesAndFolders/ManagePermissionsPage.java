package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.common.Utils;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class ManagePermissionsPage extends SiteCommon<ManagePermissionsPage>
{
    private final String userRowLocator = "//div[contains(@id, 'default-directPermissions')]//td//div[contains(text(), '%s')]/../..";
    @FindAll (@FindBy (css = "button[id*='yui-gen']"))
    protected List<WebElement> addButtonsList;

    @RenderWebElement
    @FindBy (css = "div.add-user-group button")
    private WebElement addUserGroupButton;
    @RenderWebElement
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

    public ManagePermissionsPage(ThreadLocal<WebBrowser> browser)
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
        return getBrowser().waitUntilElementVisible(pageTitle).getText();
    }

    /**
     * Click on Cancel button
     */
    public DocumentLibraryPage clickCancel()
    {
        cancelButton.click();
        return (DocumentLibraryPage) new DocumentLibraryPage(browser).renderedPage();
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
                LOG.info("Wait for element after refresh: " + counter);
                getBrowser().refresh();
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
        for (WebElement element : getBrowser().findElements(DATA_ROWS))
        {
            searchRows.add(element);
        }
        for (WebElement searchRow : searchRows)
            if (searchRow.getText().contains(searchText))
                searchRow.findElement(By.cssSelector("span[class$='button'] span button")).click();
    }

    public boolean isPermissionAddedForUser(String userProfile)
    {
        List<WebElement> userPermissionRows = getBrowser().findElements(By.cssSelector("div[id$='default-directPermissions'] tbody.yui-dt-data tr"));
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
            getBrowser().waitInSeconds(1);
        }
        clickAddButton(add);
    }

    public void clickAddButton(int add)

    {
        addButtonsList.get(add).click();
    }

    public void clickAreYouSureDialog(ButtonType areYouSure)
    {
        for (WebElement button : getBrowser().findElements(By.cssSelector("span.button-group span span button")))
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
        return (ManagePermissionsPage) this.renderedPage();
    }

    public boolean isTurnOffPermissionInheritanceDialogDisplayed()
    {
        WebElement header = getBrowser().findElement(By.cssSelector("div[id='prompt'] div[id='prompt_h']"));
        WebElement body = getBrowser().findElement(By.cssSelector("div[id='prompt'] div[class='bd']"));
        WebElement footer = getBrowser().findElement(By.cssSelector("div[id='prompt'] div[class='ft'] span[class='button-group']"));
        boolean headerText = header.getText().equals("Turn off Permission Inheritance?");
        boolean bodyText = body.getText().equals("Are you sure you do not want to inherit permissions?" + '\n' + '\n'
            + "Only local permissions will apply to this document/folder.");
        return headerText && footer.isDisplayed() && bodyText;
    }

    public void deleteUserOrGroupFromPermission(String userName)
    {
        List<WebElement> userList = getBrowser().waitUntilElementsVisible(By.cssSelector("div[id$='default-directPermissions'] tr[class^='yui-dt-rec']"));
        for (WebElement webElement : userList)
        {
            if (webElement.findElement(By.cssSelector("td[class$='displayName']")).getText().contains(userName))
            {
                getBrowser().mouseOver(webElement.findElement(By.xpath("//td[contains(@class, 'yui-dt-col-actions')]/div")));
                WebElement deleteDivElement = webElement.findElement(By.cssSelector("td[class*='yui-dt-col-actions'] div div.action-set"));
                getBrowser().findElement(By.id(deleteDivElement.getAttribute("id"))).findElement(By.cssSelector("a")).click();
            }
        }
    }

    public String getRole(String userName)
    {
        WebElement userRow = getBrowser().waitUntilElementVisible(By.xpath(String.format(userRowLocator, userName)));
        return userRow.findElement(By.xpath("//td[contains(@class, 'role')]//button|//td[contains(@class, 'role')]//span")).getText();
    }

    public String setRole(String userName, String role)
    {
        WebElement userRow = getBrowser().waitUntilElementVisible(By.xpath(String.format(userRowLocator, userName)));
        userRow.findElement(By.xpath("//td[contains(@class, 'role')]//button")).click();
        getBrowser().waitUntilElementVisible(By.cssSelector(".yui-menu-button-menu.visible"));
        getBrowser().selectOptionFromFilterOptionsList(role, getBrowser().findElements(By.cssSelector(".yui-menu-button-menu.visible li>a")));
        return role;
    }

    public DocumentLibraryPage returnTo(String location)
    {
        getBrowser().findFirstElementWithValue(breadcrumbList, location).click();
        return (DocumentLibraryPage) new DocumentLibraryPage(browser).renderedPage();
    }

    public boolean isAddUserGroupButtonDisplayed()
    {
        getBrowser().waitUntilElementVisible(addUserGroupButton);
        return getBrowser().isElementDisplayed(addUserGroupButton);
    }

    public boolean isInheritPermissionsButtonDisplayed()
    {
        getBrowser().waitUntilElementVisible(inheritPermissionButton);
        return getBrowser().isElementDisplayed(inheritPermissionButton);
    }

    public boolean isTheSaveButtonDisplayed()
    {
        getBrowser().waitUntilElementVisible(saveButton);
        return getBrowser().isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        getBrowser().waitUntilElementVisible(cancelButton);
        return getBrowser().isElementDisplayed(cancelButton);
    }

    public boolean isLocallySetPermissionsListDisplayed()
    {
        getBrowser().waitUntilElementVisible(locallySetPermissionsList);
        return getBrowser().isElementDisplayed(locallySetPermissionsList);
    }

    public WebElement selectRowLocallySetPermissions(String rowDetails)
    {
        getBrowser().waitUntilElementVisible(usersAndGroupsLocallySetPermissions);
        return getBrowser().findFirstElementWithValue(usersAndGroupsLocallySetPermissions, rowDetails);
    }

    public String getRowDetails(String details)
    {
        return selectRowLocallySetPermissions(details).getText();
    }

    public boolean isDeleteButtonAvailable(String identifier)
    {
        getBrowser().mouseOver(selectRowLocallySetPermissions(identifier));
        return getBrowser().isElementDisplayed(deleteButton);
    }

    public void clickAddUserGroupButton()
    {
        getBrowser().waitUntilElementClickable(addUserGroupButton).click();
        getBrowser().waitUntilElementVisible(addUserGroupWindow);
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
        getBrowser().waitUntilElementVisible(searchResultsList);
        return getBrowser().findFirstElementWithValue(searchResultsList, userName);
    }

    public ManagePermissionsPage clickAddButtonForUser(String userName)
    {
        selectUser(userName).findElement(addButton).click();
        return (ManagePermissionsPage) this.renderedPage();
    }

    public void clickRoleButton(String item)
    {
        getBrowser().waitUntilElementClickable(roleButton);
        selectRowLocallySetPermissions(item).findElement(By.cssSelector("td[class$='yui-dt-col-role'] button")).click();
    }

    public void selectRole(String role)
    {
        getBrowser().waitUntilElementsVisible(roles);
        getBrowser().findFirstElementWithValue(roles, role).click();
    }

    public DocumentLibraryPage clickSave()
    {
        saveButton.click();
        return (DocumentLibraryPage) new DocumentLibraryPage(browser).renderedPage();
    }

    public WebElement selectRowInheritedPermissions(String rowDetails)
    {
        getBrowser().waitUntilElementVisible(inheritedPermissionsUsersAndGroups);
        return getBrowser().findFirstElementWithValue(inheritedPermissionsUsersAndGroups, rowDetails);
    }

    public String getInheritedPermissions(String identifier)
    {
        return selectRowInheritedPermissions(identifier).getText();
    }

    public boolean isAddUsersGroupsWindowDisplayed()
    {
        getBrowser().waitUntilElementVisible(addUserGroupWindow);
        return getBrowser().isElementDisplayed(addUserGroupWindow);
    }

    public void deleteUserFromPermissionsList(String identifier)
    {
        getBrowser().mouseOver(selectRowLocallySetPermissions(identifier));
        getBrowser().waitUntilElementVisible(deleteButton).click();
    }

    public String getLocallySetPermissionsTextWhenNoUsersAreAdded()
    {
        getBrowser().waitUntilElementVisible(noPermissionsSet);
        return noPermissionsSet.getText();
    }

    public enum ButtonType
    {
        Yes, No
    }
}
