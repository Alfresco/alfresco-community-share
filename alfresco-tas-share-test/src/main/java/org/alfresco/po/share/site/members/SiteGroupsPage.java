package org.alfresco.po.share.site.members;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class SiteGroupsPage extends SiteMembersPage
{
    private AddSiteGroupsPage addSiteGroupsPage;

    private By addGroups = By.cssSelector("a[id*='addGroups']");
    @RenderWebElement
    private By searchGroupField = By.cssSelector("input[class*='search-term']");
    @RenderWebElement
    private By searchButton = By.cssSelector("button[id$='site-groups_x0023_default-button-button']");
    private By searchInputBox = By.cssSelector("input[id$='_default-search-text']");
    private By searchButtonGroup = By.cssSelector("button[id$='_default-group-search-button-button']");
    private By addButton = By.cssSelector("span[class$='-button-button'] button");
    private By roles = By.cssSelector("a.yuimenuitemlabel");
    private By selectRoleButton = By.cssSelector("td.yui-dt7-col-role.yui-dt-col-role button");
    private By addGroupsButton = By.cssSelector("button[id$='_default-add-button-button']");
    private By groupId = By.cssSelector("span.attr-value");
    private By groupNameList = By.cssSelector("td+td>div.yui-dt-liner>h3");
    private By idLabel = By.cssSelector( "div.detail span");
    private By idInSearchResults = By.cssSelector("div.detail");

    public SiteGroupsPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        addSiteGroupsPage = new AddSiteGroupsPage(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-groups", getCurrentSiteName());
    }

    public AddSiteGroupsPage addGroup()
    {
        getBrowser().findElement(addGroups).click();
        return (AddSiteGroupsPage) addSiteGroupsPage.renderedPage();
    }

    public void typeSearchGroup(String searchGroup)
    {
        getBrowser().findElement(searchGroupField).sendKeys(searchGroup);
    }

    public void clickSearch()
    {
        getBrowser().findElement(searchButton).click();
    }

    public boolean isRemoveButtonDisplayedForGroup(String groupName)
    {
        return getBrowser().isElementDisplayed(By.cssSelector("span[id$='_default-button-GROUP_" + groupName + "']>span>span>button"));
    }

    public void removeGroup(String groupName)
    {
        WebElement groupRemoveButton = getBrowser().findElement(By.cssSelector("span[id$='_default-button-GROUP_" + groupName + "']>span>span>button"));
        groupRemoveButton.click();
    }

    public boolean isAddGroupsButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(addGroups);
    }

    public void clickAddGroupsButton()
    {
        getBrowser().waitUntilElementClickable(addGroups, 5).click();
    }

    public void typeInSearchGroup(String groupName)
    {
        clearAndType(getBrowser().findElement(searchInputBox), groupName);
    }

    public void clickSearchButton()
    {
        getBrowser().waitUntilElementClickable(searchButtonGroup).click();
    }

    public void clickTheAddButton()
    {
        getBrowser().waitUntilElementClickable(addButton).click();
    }

    public void selectRole(String roleName)
    {
        WebElement role = getBrowser().findFirstElementWithValue(roles, roleName);
        getBrowser().waitUntilElementClickable(role).click();
    }

    public void clickSelectRoleButton()
    {
        getBrowser().waitUntilElementClickable(selectRoleButton).click();
    }

    public void clickTheAddGroupsButton()
    {
        getBrowser().waitUntilElementClickable(addGroupsButton).click();
    }

    public List<String> getIds()
    {
        List<String> id = new ArrayList<>();
        for (WebElement aGroupId : getBrowser().findElements(groupId))
        {
            id.add(aGroupId.getText());
        }

        return id;
    }

    public List<String> getSearchResultsGroupName()
    {
        List<String> groupName = new ArrayList<>();
        for (WebElement aGroupNameList : getBrowser().findElements(groupNameList))
        {
            groupName.add(aGroupNameList.getText());
        }
        return groupName;
    }

    public String getIdLabel()
    {
        return getBrowser().waitUntilElementVisible(idLabel).getText();
    }


    public List<String> getIdInSearchResults()
    {
        List<String> id = new ArrayList<>();

        for (WebElement idInSearchResult : getBrowser().findElements(idInSearchResults))
        {
            id.add(idInSearchResult.getText());
        }
        return id;
    }
}