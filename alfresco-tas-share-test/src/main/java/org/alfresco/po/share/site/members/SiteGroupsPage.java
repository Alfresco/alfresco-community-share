package org.alfresco.po.share.site.members;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SiteGroupsPage extends SiteMembersPage
{
    private final By addGroups = By.cssSelector("a[id*='addGroups']");
    private final By searchGroupField = By.cssSelector("input[class*='search-term']");
    private final By searchButton = By.cssSelector("button[id$='site-groups_x0023_default-button-button']");
    private final By searchInputBox = By.cssSelector("input[id$='_default-search-text']");
    private final By searchButtonGroup = By.cssSelector("button[id$='_default-group-search-button-button']");
    private final By addButton = By.cssSelector("span[class$='-button-button'] button");
    private final By roles = By.cssSelector("a.yuimenuitemlabel");
    private final By selectRoleButton = By.cssSelector("td.yui-dt7-col-role.yui-dt-col-role button");
    private final By addGroupsButton = By.cssSelector("button[id$='_default-add-button-button']");
    private final By groupId = By.cssSelector("span.attr-value");
    private final By groupNameList = By.cssSelector("td+td>div.yui-dt-liner>h3");
    private final By idLabel = By.cssSelector( "div.detail span");
    private final By idInSearchResults = By.cssSelector("div.detail");

    public SiteGroupsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-groups", getCurrentSiteName());
    }

    public void typeSearchGroup(String searchGroup)
    {
        webElementInteraction.findElement(searchGroupField).sendKeys(searchGroup);
    }

    public void clickSearch()
    {
        webElementInteraction.clickElement(searchButton);
    }

    public boolean isRemoveButtonDisplayedForGroup(String groupName)
    {
        return webElementInteraction.isElementDisplayed(By.cssSelector("span[id$='_default-button-GROUP_" + groupName + "']>span>span>button"));
    }

    public void removeGroup(String groupName)
    {
        WebElement groupRemoveButton = webElementInteraction.findElement(By.cssSelector("span[id$='_default-button-GROUP_" + groupName + "']>span>span>button"));
        webElementInteraction.clickElement(groupRemoveButton);
    }

    public boolean isAddGroupsButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(addGroups);
    }

    public void clickAddGroupsButton()
    {
        webElementInteraction.clickElement(addGroups);
    }

    public void clickSearchButton()
    {
        webElementInteraction.clickElement(searchButtonGroup);
    }

    public void clickTheAddButton()
    {
        webElementInteraction.clickElement(addButton);
    }

    public void selectRole(String roleName)
    {
        WebElement role = webElementInteraction.findFirstElementWithValue(roles, roleName);
        webElementInteraction.clickElement(role);
    }

    public void clickSelectRoleButton()
    {
        webElementInteraction.clickElement(selectRoleButton);
    }

    public void clickTheAddGroupsButton()
    {
        webElementInteraction.clickElement(addGroupsButton);
    }

    public List<String> getIds()
    {
        List<String> id = new ArrayList<>();
        for (WebElement aGroupId : webElementInteraction.findElements(groupId))
        {
            id.add(aGroupId.getText());
        }

        return id;
    }

    public List<String> getSearchResultsGroupName()
    {
        List<String> groupName = new ArrayList<>();
        for (WebElement aGroupNameList : webElementInteraction.findElements(groupNameList))
        {
            groupName.add(aGroupNameList.getText());
        }
        return groupName;
    }

    public String getIdLabel()
    {
        return webElementInteraction.waitUntilElementIsVisible(idLabel).getText();
    }

    public List<String> getIdInSearchResults()
    {
        List<String> id = new ArrayList<>();

        for (WebElement idInSearchResult : webElementInteraction.findElements(idInSearchResults))
        {
            id.add(idInSearchResult.getText());
        }
        return id;
    }
}