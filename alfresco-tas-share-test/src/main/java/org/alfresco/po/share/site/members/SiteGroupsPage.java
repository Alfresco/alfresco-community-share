package org.alfresco.po.share.site.members;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class SiteGroupsPage extends SiteMembersPage
{
    @FindBy (css = "a[id*='addGroups']")
    private WebElement addGroups;

    @RenderWebElement
    @FindBy (css = "input[class*='search-term']")
    private TextInput searchGroupField;

    @RenderWebElement
    @FindBy (css = "button[id$='site-groups_x0023_default-button-button']")
    private Button searchButton;

    @FindBy (css = "input[id$='_default-search-text']")
    private WebElement searchInputBox;

    @FindBy (css = "button[id$='_default-group-search-button-button']")
    private WebElement searchButtonGroup;

    @FindBy (css = "span[class$='-button-button'] button")
    private WebElement addButton;

    @FindAll (@FindBy (css = "a.yuimenuitemlabel"))
    private List<WebElement> roles;

    @FindBy (css = "td.yui-dt7-col-role.yui-dt-col-role button")
    private WebElement selectRoleButton;

    @FindBy (css = "button[id$='_default-add-button-button']")
    private WebElement addGroupsButton;

    @FindAll (@FindBy (css = "span.attr-value"))
    private List<WebElement> groupId;

    @FindAll (@FindBy (css = "td+td>div.yui-dt-liner>h3"))
    private List<WebElement> groupNameList;

    @FindBy (css = "div.detail span")
    private WebElement idLabel;

    @FindAll (@FindBy (css = "div.detail"))
    private List<WebElement> idInSearchResults;

    public By waitMessage = By.cssSelector("span.wait");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-groups", getCurrentSiteName());
    }

    /**
     * Press Add Group button
     */
    public AddSiteGroupsPage addGroup()
    {
        addGroups.click();
        return new AddSiteGroupsPage();
    }

    /**
     * Type searchGroup
     *
     * @param searchGroup String
     */
    public void typeSearchGroup(String searchGroup)
    {
        searchGroupField.sendKeys(searchGroup);
    }

    /**
     * Click Search button
     */

    public void clickSearch()
    {
        searchButton.click();
    }

    /**
     * Method returns if 'Remove' button is displayed for a specified group
     *
     * @param groupName String
     * @return True if button is displayed
     */
    public boolean isRemoveButtonDisplayedForGroup(String groupName)
    {
        return browser.isElementDisplayed(By.cssSelector("span[id$='_default-button-GROUP_" + groupName + "']>span>span>button"));
    }

    /**
     * Removes specific group from site
     *
     * @param groupName
     */
    public void removeGroup(String groupName)
    {
        WebElement groupRemoveButton = browser.findElement(By.cssSelector("span[id$='_default-button-GROUP_" + groupName + "']>span>span>button"));
        groupRemoveButton.click();
    }

    /**
     * Checks whether add groups button is visible or not
     *
     * @return
     */
    public boolean isAddGroupsButtonDisplayed()
    {
        return browser.isElementDisplayed(addGroups);
    }

    public void clickAddGroupsButton()
    {
        browser.waitUntilElementClickable(addGroups, 5);
        addGroups.click();
    }

    public void typeInSearchGroup(String groupName)
    {
        searchInputBox.clear();
        searchInputBox.sendKeys(groupName);
    }

    public void clickSearchButton()
    {
        browser.waitUntilElementClickable(searchButtonGroup, 5);
        searchButtonGroup.click();
    }

    public void clickTheAddButton()
    {
        browser.waitUntilElementClickable(addButton, 60);
        addButton.click();
    }

    public void selectRole(String roleName)
    {
        WebElement role = browser.findFirstElementWithValue(roles, roleName);
        browser.waitUntilElementClickable(role, 50).click();
        browser.waitInSeconds(2);
    }

    public void clickSelectRoleButton()
    {
        browser.waitUntilElementClickable(selectRoleButton, 10L);
        selectRoleButton.click();
    }

    public void clickTheAddGroupsButton()
    {
        browser.waitUntilElementClickable(addGroupsButton, 10L);
        addGroupsButton.click();
    }

    public List<String> getIds()
    {
        List<String> id = new ArrayList<>();
        for (WebElement aGroupId : groupId)
        {
            id.add(aGroupId.getText());
        }

        return id;
    }

    public List<String> getSearchResultsGroupName()
    {
        List<String> groupName = new ArrayList<>();
        for (WebElement aGroupNameList : groupNameList)
        {
            groupName.add(aGroupNameList.getText());
        }
        return groupName;
    }

    public String getIdLabel()
    {
        browser.waitUntilElementVisible(idLabel);
        return idLabel.getText();
    }


    public List<String> getIdInSearchResults()
    {
        List<String> id = new ArrayList<>();

        for (WebElement idInSearchResult : idInSearchResults)
        {
            id.add(idInSearchResult.getText());
        }
        return id;
    }
}