package org.alfresco.po.share.site.members;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.lang3.EnumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class AddSiteGroupsPage extends SiteCommon<AddSiteGroupsPage>
{
    private SiteGroupsPage siteGroupsPage;

    @RenderWebElement
    protected By groupFinderWrapper = By.cssSelector("div[class='finder-wrapper']");
    @RenderWebElement
    protected By groupsListBox = By.cssSelector("div[class='groupslist']");
    protected By defaultSearchText = By.cssSelector("div[class='title'] label");
    protected By groupSearchBox = By.cssSelector("input[id*='group-finder']");
    protected By groupSearchButton = By.cssSelector("button[id*='group-search-button']");
    protected By groupsListTitle = By.cssSelector("div[class*='grouplistWrapper'] div[class*='title']");
    protected By setRolesButton = By.cssSelector("button[id*='selectallroles']");
    protected By addGroupsButton = By.cssSelector("button[id*='default-add-button']");
    @RenderWebElement
    protected By backToSiteGroups = By.cssSelector("span[id*='backTo'] a");
    protected By groupResultsList = By.cssSelector("div[id*='default-results'] tbody[class='yui-dt-data'] tr");
    protected By groupInvitedList = By.cssSelector("div[id*='inviteelist'] tbody[class='yui-dt-data'] tr");
    protected By allRolesFilterOptions = By.cssSelector("li[class*='yuimenuitem']");
    protected By setAllRolesToButton = By.cssSelector("div[id*='invitationBar'] button");
    protected By roleOptions = By.cssSelector("div[id*='inviteelist'] div.yui-menu-button-menu.visible a.yuimenuitemlabel");
    private By searchResultsText = By.cssSelector("div[id*='default-results'] td[class='yui-dt-empty']");

    public AddSiteGroupsPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        siteGroupsPage = new SiteGroupsPage(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/add-groups", getCurrentSiteName());
    }

    public String getDefaultSearchText()
    {
        return getBrowser().findElement(defaultSearchText).getText();
    }

    public String getGroupsListTitle()
    {
        return getBrowser().findElement(groupsListTitle).getText();
    }

    public boolean isGroupSearchBoxDisplayed()
    {
        return getBrowser().isElementDisplayed(groupSearchBox);
    }

    public boolean isGroupSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(groupSearchButton);
    }

    public boolean isSetRolesButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(setRolesButton);
    }

    public boolean isAddGroupsButtonEnabled()
    {
        return getBrowser().findElement(addGroupsButton).isEnabled();
    }

    public boolean isGoBackToSiteGroupsDisplayed()
    {
        return getBrowser().isElementDisplayed(backToSiteGroups);
    }

    public SiteGroupsPage goBackToSiteGroupsPage()
    {
        getBrowser().findElement(backToSiteGroups).click();
        return (SiteGroupsPage) this.renderedPage();
    }

    public AddSiteGroupsPage searchForGroup(String groupName)
    {
        clearAndType(getBrowser().findElement(groupSearchBox), groupName);
        getBrowser().findElement(groupSearchButton).click();

        return (AddSiteGroupsPage) this.renderedPage();
    }

    public WebElement selectGroupInSearchResults(String group)
    {
        return getBrowser().findFirstElementWithValue(groupResultsList, group);
    }

    public WebElement selectGroupInInvitedList(String group)
    {
        return getBrowser().findFirstElementWithValue(groupInvitedList, group);
    }

    public boolean isGroupReturned(String groupName)
    {
        return getBrowser().isElementDisplayed(selectGroupInSearchResults(groupName));
    }

    public void addGroup(String groupName)
    {
        selectGroupInSearchResults(groupName).findElement(By.cssSelector("button")).click();
    }

    public boolean isGroupInvited(String groupName)
    {
        return getBrowser().isElementDisplayed(selectGroupInInvitedList(groupName));
    }

    public List<String> getRolesFromFilter()
    {
        getBrowser().findElement(setAllRolesToButton).click();
        List<String> roles = new ArrayList<>();
        for (WebElement allRolesFilterOption : getBrowser().findElements(allRolesFilterOptions))
        {
            roles.add(allRolesFilterOption.getText());
        }
        getBrowser().findElement(setAllRolesToButton).click();
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
            getBrowser().findElement(setAllRolesToButton).click();
            getBrowser().selectOptionFromFilterOptionsList(roleOption, getBrowser().findElements(allRolesFilterOptions));
            return (AddSiteGroupsPage) this.renderedPage();
        } catch (NoSuchElementException nse)
        {
            LOG.error("Set role option not present" + nse.getMessage());
            throw new PageOperationException(roleOption + " option not present.");
        }

    }

    public AddSiteGroupsPage setGroupRole(String groupName, String roleOption)
    {
        try
        {
            selectGroupInInvitedList(groupName).findElement(By.cssSelector("button")).click();
            List<WebElement> groupRoleOptions = getBrowser().waitUntilElementsVisible(roleOptions);
            getBrowser().selectOptionFromFilterOptionsList(roleOption, groupRoleOptions);
            return (AddSiteGroupsPage) this.renderedPage();
        }
        catch (NoSuchElementException nse)
        {
            LOG.error("Set role option not present" + nse.getMessage());
            throw new PageOperationException(roleOption + " option not present.");
        }
    }

    public AddSiteGroupsPage addGroups()
    {
        getBrowser().waitUntilElementVisible(addGroupsButton);
        getBrowser().waitUntilElementClickable(addGroupsButton).click();
        return (AddSiteGroupsPage) this.renderedPage();
    }

    public String getSearchText()
    {
        return getBrowser().findElement(searchResultsText).getText();
    }

    public String getSearchBoxContent()
    {
        return getBrowser().findElement(groupSearchBox).getAttribute("value");
    }

    public void addGroupWorkflow(String group, String role)
    {
        searchForGroup(group);
        Assert.assertTrue(isGroupReturned(group), "Group was not returned");

        addGroup(group);
        Assert.assertTrue(isGroupInvited(group), "Group is not on invited list");

        setGroupRole(group, role);
        addGroups();
    }

    public AddSiteGroupsPage clickAddGroupsButton()
    {
        getBrowser().waitUntilElementClickable(addGroupsButton, 30).click();
        return (AddSiteGroupsPage) this.renderedPage();
    }

    public enum GroupRoles
    {
        Manager, Collaborator, Contributor, Consumer
    }
}