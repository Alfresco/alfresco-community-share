package org.alfresco.po.share.site.members;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.site.SiteCommon;
import org.apache.commons.lang3.EnumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

@PageObject
public class AddSiteGroupsPage extends SiteCommon<AddSiteGroupsPage>
{
    @RenderWebElement
    @FindBy(css = "div[class='finder-wrapper']")
    protected WebElement groupFinderWrapper;

    @RenderWebElement
    @FindBy(css = "div[class='groupslist']")
    protected WebElement groupsListBox;

    @FindBy(css = "div[class='title'] label")
    protected WebElement defaultSearchText;

    @FindBy(css = "input[id*='group-finder']")
    protected WebElement groupSearchBox;

    @FindBy(css = "button[id*='group-search-button']")
    protected WebElement groupSearchButton;

    @FindBy(css = "div[class*='grouplistWrapper'] div[class*='title']")
    protected WebElement groupsListTitle;

    @FindBy(css = "button[id*='selectallroles']")
    protected WebElement setRolesButton;

    @FindBy(css = "button[id*='default-add-button']")
    protected WebElement addGroupsButton;

    @RenderWebElement
    @FindBy(css = "span[id*='backTo'] a")
    protected WebElement backToSiteGroups;

    @FindAll(@FindBy(css = "div[id*='default-results'] tbody[class='yui-dt-data'] tr"))
    protected List<WebElement> groupResultsList;

    @FindAll(@FindBy(css = "div[id*='inviteelist'] tbody[class='yui-dt-data'] tr"))
    protected List<WebElement> groupInvitedList;

    @FindAll(@FindBy(css = "li[class*='yuimenuitem']"))
    protected List<WebElement> allRolesFilterOptions;

    @FindBy(css = "div[id*='invitationBar'] button")
    protected WebElement setAllRolesToButton;

    protected By roleOptions = By.cssSelector("div[id*='inviteelist'] div.yui-menu-button-menu.visible a.yuimenuitemlabel");

    @FindBy(css = "div[id*='default-results'] td[class='yui-dt-empty']")
    private WebElement searchResultsText;

    public enum GroupRoles
    {
        Manager, Collaborator, Contributor, Consumer
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/add-groups", getCurrentSiteName());
    }

    /**
     * Get default search text
     * 
     * @return
     */
    public String getDefaultSearchText()
    {
        return defaultSearchText.getText();
    }

    /**
     * Get groups list title
     * 
     * @return
     */
    public String getGroupsListTitle()
    {
        return groupsListTitle.getText();
    }

    /**
     * Checks whether search box is displayed or not
     * 
     * @return
     */
    public boolean isGroupSearchBoxDisplayed()
    {
        return browser.isElementDisplayed(groupSearchBox);
    }

    /**
     * Checks whether search button is displayed or not
     * 
     * @return
     */
    public boolean isGroupSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(groupSearchButton);
    }

    /**
     * Checks whether set roles button is displayed or not
     * 
     * @return
     */
    public boolean isSetRolesButtonDisplayed()
    {
        return browser.isElementDisplayed(setRolesButton);
    }

    /**
     * Check if Add Groups button is enabled or disabled
     * 
     * @return
     */
    public boolean isAddGroupsButtonEnabled()
    {
        return addGroupsButton.isEnabled();
    }

    /**
     * Check if Go Back to Site Groups link is displayed
     * 
     * @return
     */
    public boolean isGoBackToSiteGroupsDisplayed()
    {
        return browser.isElementDisplayed(backToSiteGroups);
    }

    /**
     * Press Go Back To Site Groups link
     */
    public SiteGroupsPage goBackToSiteGroupsPage()
    {
        backToSiteGroups.click();

        return new SiteGroupsPage();
    }

    /**
     * Search for a specific group
     * 
     * @param groupName
     * @return
     */
    public AddSiteGroupsPage searchForGroup(String groupName)
    {
        groupSearchBox.clear();
        groupSearchBox.sendKeys(groupName);
        groupSearchButton.click();

        return (AddSiteGroupsPage) this.renderedPage();
    }

    public WebElement selectGroupInSearchResults(String group)
    {
        return browser.findFirstElementWithValue(groupResultsList, group);
    }

    public WebElement selectGroupInInvitedList(String group)
    {
        return browser.findFirstElementWithValue(groupInvitedList, group);
    }

    public boolean isGroupReturned(String groupName)
    {
        return browser.isElementDisplayed(selectGroupInSearchResults(groupName));
    }

    public void addGroup(String groupName)
    {
        selectGroupInSearchResults(groupName).findElement(By.cssSelector("button")).click();
    }

    public boolean isGroupInvited(String groupName)
    {
        return browser.isElementDisplayed(selectGroupInInvitedList(groupName));
    }

    public List<String> getRolesFromFilter()
    {
        setAllRolesToButton.click();
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < allRolesFilterOptions.size(); i++)
        {
            roles.add(allRolesFilterOptions.get(i).getText());
        }
        setAllRolesToButton.click();
        return roles;
    }

    public boolean isRoleFilterValid()
    {
        List<String> roles = getRolesFromFilter();
        boolean isValid = true;
        for (int i = 0; i < roles.size(); i++)
        {
            EnumUtils.getEnumList(GroupRoles.class);
            if (!EnumUtils.isValidEnum(GroupRoles.class, roles.get(i)))
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
            setAllRolesToButton.click();
            browser.selectOptionFromFilterOptionsList(roleOption, allRolesFilterOptions);
            return (AddSiteGroupsPage) this.renderedPage();
        }
        catch (NoSuchElementException nse)
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
            List<WebElement> groupRoleOptions = browser.waitUntilElementsVisible(roleOptions);
            browser.selectOptionFromFilterOptionsList(roleOption, groupRoleOptions);
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
        browser.waitUntilElementClickable(addGroupsButton, 30).click();
        return (AddSiteGroupsPage) this.renderedPage();
    }

    /**
     * Get the search text when looking for groups
     * 
     * @return
     */
    public String getSearchText()
    {
        return searchResultsText.getText();
    }

    /**
     * Get the search text
     * 
     * @return
     */
    public String getSearchBoxContent()
    {
        return groupSearchBox.getAttribute("value");
    }

    /**
     * Complete flow to add a group to a site
     * 
     * @param group
     * @param role
     */
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
        browser.waitUntilElementClickable(addGroupsButton, 30).click();
        browser.waitInSeconds(1);
        return (AddSiteGroupsPage) this.renderedPage();
    }
}