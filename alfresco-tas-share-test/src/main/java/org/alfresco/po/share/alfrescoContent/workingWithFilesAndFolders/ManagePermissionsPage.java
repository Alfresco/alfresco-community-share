package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
public class ManagePermissionsPage extends SiteCommon<ManagePermissionsPage>
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @RenderWebElement
    @FindBy(css = "div.add-user-group button")
    private WebElement addUserGroupButton;

    @RenderWebElement
    @FindBy(css = "button[id$='-okButton-button']")
    private WebElement saveButton;

    @FindBy(css = "button[id$='-cancelButton-button']")
    private WebElement cancelButton;

    @FindBy(css = "div.search-text input")
    private WebElement searchUserInput;

    @FindBy(css = "div.authority-search-button button")
    private WebElement searchUserButton;

    @FindBy(css = "div[id$='_default-inheritedButtonContainer']")
    private WebElement inheritPermissionButton;

    @FindBy(css = "td[class$='displayName']")
    private WebElement userNameLocator;

    @FindBy(css = "td[class*='role']")
    private WebElement userRoleLocator;

    @FindBy(css = "span[id$='_default-title']")
    private WebElement pageTitle;

    @FindBy (css = "span[class$='button' span button]")
    private WebElement addUser;

    @FindBy (css = "div[id$='_default-inheritedPermissions']")
    private WebElement inheritPermissionTable;

    @FindBy (css = "div[class$='inherited-on']")
    private WebElement inheritButtonStatus;
    
    @FindAll(@FindBy(css = "button[id*='yui-gen']"))
    protected List<WebElement> addButtonsList;

    private final String userRowLocator = "//div[contains(@id, 'default-directPermissions')]//td//div[contains(text(), '%s')]/../..";

    public enum ButtonType
    {
        Yes, No;
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/manage-permissions?nodeRef=workspace://SpacesStore/43f06861-c507-470a-ba0c-08f46a53d1b9", getCurrentSiteName());
    }

    /**
     * Check if title is displayed correctly
     */
    public String getTitle()
    {
        return pageTitle.getText();
    }

    /**
     * Click on a Save or Cancel buttons
     *
     * @param buttonName to be clicked: Save, Cancel
     */
    public DocumentLibraryPage clickButton(String buttonName)
    {
        if(buttonName.equals("Save"))
            saveButton.click();
        else
            cancelButton.click();

        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    /**
     * Click on Inherit Permissions button
     *
     */
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
            }
            else
            {
                LOG.info("Wait for element after refresh: " + counter);
                browser.refresh();
                counter++;
            }
        }
    }

    /**
     * Search and Add User or Group
     *
     * @param searchText String
     */
    public void searchAndAddUserAndGroup(String searchText)
    {
        addUserGroupButton.click();
        List<WebElement> searchRows = new ArrayList<>();
        searchUserInput.clear();
        searchUserInput.sendKeys(searchText);
        searchUserButton.click();
        browser.waitInSeconds(5);
        By DATA_ROWS = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
        for (WebElement element : browser.findElements(DATA_ROWS))
        {
            searchRows.add(element);
        }
        for (WebElement searchRow : searchRows)
            if (searchRow.getText().contains(searchText))
                searchRow.findElement(By.cssSelector("span[class$='button'] span button")).click();
    }

    /**
     * Check if User has permissions added in Locally Set Permissions
     *
     * @param userProfile String
     */
    public boolean isPermissionAddedForUser(String userProfile)
    {
        List<WebElement> userPermissionRows = null;
        userPermissionRows = browser.findElements(By.cssSelector("div[id$='default-directPermissions'] tbody.yui-dt-data tr"));
        for (WebElement userPermissionRow : userPermissionRows)
        {
            String name = userPermissionRow.findElement(By.cssSelector("td[class$='-displayName']")).getText();
            if (name.contains(userProfile) || name.contains("firstName lastName"))
                return true;
        }
        return false;
    }

    /**
     * Checks if Inherit Permissions is enabled
     *
     */
    public boolean isInheritPermissionsTableEnabled()
    {
        return inheritPermissionTable.isDisplayed();
    }

    /**
     * Checks status (On or Off) of Inherit Permissions button
     *
     */
    public boolean isInheritButtonStatusEnabled()
    {
        try
        {
            return inheritButtonStatus.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    
    
    public void searchAndAddUserOrGroup(String searchQuery, int add)
    {
        addUserGroupButton.click();
        searchUserInput.clear();
        browser.waitInSeconds(3);
        searchUserInput.sendKeys(searchQuery);
        browser.waitInSeconds(2);
        for (int i = 1; i < 5; i++)
        {
            searchUserButton.click();
            browser.waitInSeconds(1);
        }
        clickAddButton(add);
    }
    
    public void clickAddButton(int add)

    {
        addButtonsList.get(add).click();
    }

    /**
     * Click Yes or No option in the toggle Inherit Permissions dialog
     *
     * @param  areYouSure ButtonType
     */
    public void clickAreYouSureDialog(ButtonType areYouSure)
    {
        for (WebElement button : browser.findElements(By.cssSelector("span.button-group span span button")))
        {
            if (areYouSure.toString().equals(button.getText()))
            {
                button.click();
                break;
            }
        }
    }

    /**
     * Toggle On/Off Inherit Permissions option
     *
     * @param turnOn boolean
     * @param areYouSure ButtonType
     *
     */
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
            }
            catch (Exception e)
            {
                //carry on
            }
        }
        return new ManagePermissionsPage();
    }

    /**
     * Verify if the dialog pop-up is displayed when Inherit Permissions is to be turned-off
     *
     */
    public boolean isTurnOffPermissionInheritanceDialogDisplayed()
    {
        WebElement header = browser.findElement(By.cssSelector("div[id='prompt'] div[id='prompt_h']"));
        WebElement body = browser.findElement(By.cssSelector("div[id='prompt'] div[class='bd']"));
        WebElement footer = browser.findElement(By.cssSelector("div[id='prompt'] div[class='ft'] span[class='button-group']"));
        boolean headerText = header.getText().equals("Turn off Permission Inheritance?");
        boolean bodyText = body.getText().equals("Are you sure you do not want to inherit permissions?" + '\n' + '\n'
                                                    + "Only local permissions will apply to this document/folder.");
        if (headerText && footer.isDisplayed() && bodyText)
            return true;
        else
            return false;
    }

    /**
     * Delete user or group from permission table
     *
     * @param userName String
     *
     */
    public void deleteUserOrGroupFromPermission(String userName)
    {
        List<WebElement> userList = browser.waitUntilElementsVisible(By.cssSelector("div[id$='default-directPermissions'] tr[class^='yui-dt-rec']"));
        for (WebElement webElement : userList)
        {
            if (webElement.findElement(By.cssSelector("td[class$='displayName']")).getText().contains(userName))
            {
                browser.mouseOver(webElement.findElement(By.xpath("//td[contains(@class, 'yui-dt-col-actions')]/div")));
                WebElement deleteDivElement = webElement.findElement(By.cssSelector("td[class*='yui-dt-col-actions'] div div.action-set"));
                browser.findElement(By.id(deleteDivElement.getAttribute("id"))).findElement(By.cssSelector("a")).click();
            }
        }
    }

    /**
     * Method to get the role for a specified user
     *
     * @param userName String
     *
     */
    public String getRole(String userName)
    {
        WebElement userRow = browser.waitUntilElementVisible(By.xpath(String.format(userRowLocator, userName)));
        String role = userRow.findElement(By.xpath("//td[contains(@class, 'role')]//button")).getText();
        return role;
    }
}
