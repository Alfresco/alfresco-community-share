
package org.alfresco.po.share.site.members;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.exception.PageOperationException;
import org.apache.commons.lang3.EnumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AddSiteGroupsPage extends SiteCommon<AddSiteGroupsPage>
{
    protected final By groupFinderWrapper = By.cssSelector("div[class='finder-wrapper']");
    protected final By defaultSearchText = By.cssSelector("div[class='title'] label");
    protected final By groupSearchBox = By.cssSelector("input[id*='group-finder']");
    protected final By groupSearchButton = By.cssSelector("button[id*='group-search-button']");
    protected final By groupsListTitle = By.cssSelector("div[class*='grouplistWrapper'] div[class*='title']");
    protected final By setRolesButton = By.cssSelector("button[id*='selectallroles']");
    protected final By addGroupsButton = By.cssSelector("button[id*='default-add-button']");
    protected final By backToSiteGroups = By.cssSelector("span[id*='backTo'] a");
    protected final By groupResultsList = By.cssSelector("div[id*='default-results'] tbody[class='yui-dt-data'] tr");
    protected final By groupInvitedList = By.cssSelector("div[id*='inviteelist'] tbody[class='yui-dt-data'] tr");
    protected final By allRolesFilterOptions = By.cssSelector("li[class*='yuimenuitem']");
    protected final By setAllRolesToButton = By.cssSelector("div[id*='invitationBar'] button");
    protected final By roleOptions = By.cssSelector("div[id*='inviteelist'] div.yui-menu-button-menu.visible a.yuimenuitemlabel");
    private final By searchResultsText = By.cssSelector("div[id*='default-results'] td[class='yui-dt-empty']");

    public AddSiteGroupsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/add-groups", getCurrentSiteName());
    }

    public String getDefaultSearchText()
    {
        return webElementInteraction.findElement(defaultSearchText).getText();
    }

    public String getGroupsListTitle()
    {
        return webElementInteraction.findElement(groupsListTitle).getText();
    }

    public boolean isGroupSearchBoxDisplayed()
    {
        return webElementInteraction.isElementDisplayed(groupSearchBox);
    }

    public boolean isGroupSearchButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(groupSearchButton);
    }

    public boolean isSetRolesButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(setRolesButton);
    }

    public boolean isAddGroupsButtonEnabled()
    {
        return webElementInteraction.findElement(addGroupsButton).isEnabled();
    }

    public boolean isGoBackToSiteGroupsDisplayed()
    {
        return webElementInteraction.isElementDisplayed(backToSiteGroups);
    }

    public SiteGroupsPage goBackToSiteGroupsPage()
    {
        webElementInteraction.clickElement(backToSiteGroups);
        return new SiteGroupsPage(webDriver);
    }

    public AddSiteGroupsPage searchForGroup(String groupName)
    {
        webElementInteraction.clearAndType(groupSearchBox, groupName);
        webElementInteraction.clickElement(groupSearchButton);

        return new AddSiteGroupsPage(webDriver);
    }

    public WebElement selectGroupInSearchResults(String group)
    {
        return webElementInteraction.findFirstElementWithValue(groupResultsList, group);
    }

    public WebElement selectGroupInInvitedList(String group)
    {
        return webElementInteraction.findFirstElementWithValue(groupInvitedList, group);
    }

    public boolean isGroupReturned(String groupName)
    {
        return webElementInteraction.isElementDisplayed(selectGroupInSearchResults(groupName));
    }

    public void addGroup(String groupName)
    {
        webElementInteraction.clickElement(selectGroupInSearchResults(groupName).findElement(By.cssSelector("button")));
    }

    public boolean isGroupInvited(String groupName)
    {
        return webElementInteraction.isElementDisplayed(selectGroupInInvitedList(groupName));
    }

    public List<String> getRolesFromFilter()
    {
        webElementInteraction.clickElement(setAllRolesToButton);
        List<String> roles = new ArrayList<>();
        for (WebElement allRolesFilterOption : webElementInteraction.findElements(allRolesFilterOptions))
        {
            roles.add(allRolesFilterOption.getText());
        }
        webElementInteraction.clickElement(setAllRolesToButton);
        return roles;
    }

    public boolean isRoleFilterValid()
    {
        List<String> roles = getRolesFromFilter();
        boolean isValid = true;
        for (String role : roles)
        {
            EnumUtils.getEnumList(GroupRoles.class);
            if (!EnumUtils.isValidEnum(GroupRoles.class, role))
            {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    public AddSiteGroupsPage setAllGroupRoles(String roleOption)
    {
        try
        {
            webElementInteraction.clickElement(setAllRolesToButton);
            webElementInteraction.selectOptionFromFilterOptionsList(roleOption, webElementInteraction.findElements(allRolesFilterOptions));
            return new AddSiteGroupsPage(webDriver);
        } catch (NoSuchElementException nse)
        {
            LOG.error("Set role option not present {}", nse.getMessage());
            throw new PageOperationException(roleOption + " option not present.");
        }

    }

    public AddSiteGroupsPage setGroupRole(String groupName, String roleOption)
    {
        try
        {
            webElementInteraction.clickElement(selectGroupInInvitedList(groupName).findElement(By.cssSelector("button")));
            List<WebElement> groupRoleOptions = webElementInteraction.waitUntilElementsAreVisible(roleOptions);
            webElementInteraction.selectOptionFromFilterOptionsList(roleOption, groupRoleOptions);
            return this;
        }
        catch (NoSuchElementException nse)
        {
            LOG.error("Set role option not present" + nse.getMessage());
            throw new PageOperationException(roleOption + " option not present.");
        }
    }

    public AddSiteGroupsPage addGroups()
    {
        webElementInteraction.waitUntilElementIsVisible(addGroupsButton);
        webElementInteraction.clickElement(addGroupsButton);
        return this;
    }

    public String getSearchText()
    {
        return webElementInteraction.findElement(searchResultsText).getText();
    }

    public String getSearchBoxContent()
    {
        return webElementInteraction.findElement(groupSearchBox).getAttribute("value");
    }

    public void addGroupWorkflow(String group, String role)
    {
        searchForGroup(group);
        assertTrue(isGroupReturned(group), "Group was not returned");

        addGroup(group);
        assertTrue(isGroupInvited(group), "Group is not on invited list");

        setGroupRole(group, role);
        addGroups();
    }

    public AddSiteGroupsPage clickAddGroupsButton()
    {
        webElementInteraction.clickElement(addGroupsButton);
        return this;
    }

    //todo: create it in a single Enum file
    public enum GroupRoles
    {
        Manager, Collaborator, Contributor, Consumer
    }
}