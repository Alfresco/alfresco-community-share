package org.alfresco.po.share.site.members;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Primary
@PageObject
public class SiteMembersPage extends SiteCommon<SiteMembersPage>
{
    @FindAll(@FindBy(css = "td+td>div.yui-dt-liner>h3"))
    private List<WebElement> namesList;

    @FindBy(css = "a[id*='site-members-link']")
    private WebElement siteUsers;

    @FindBy(css = "a[id*='site-groups-link']")
    private WebElement siteGroups;

    @FindBy(css = "a[id*='pending-invites-link']")
    private WebElement pendingInvites;

    @FindAll(@FindBy(css = "tbody[class='yui-dt-data'] tr"))
    private List<WebElement> siteMemberRow;

    @FindAll(@FindBy(css = "div.visible ul.first-of-type li a"))
    private List<WebElement> dropDownOptionsList;

    private By currentRoleButton = By.cssSelector("td[class*='role'] button");
    private By removeButton = By.cssSelector(".uninvite button");
    private By currentRole = By.cssSelector("td[class*='role'] div :first-child");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-members", getCurrentSiteName());
    }

    /**
     * Method returns if the user or group has the specified role
     *
     * @param role String
     * @param name String
     * @return True if role is selected for the specified name
     */
    public boolean isRoleSelected(String role, String name)
    {
        return selectMember(name).findElement(currentRole).getText().contains(role);
    }

    public String getRole(String name)
    {
        return selectMember(name).findElement(currentRole).getText();
    }

    public boolean isRemoveButtonEnabledForMember(String name)
    {
        return selectMember(name).findElement(removeButton).isEnabled();
    }

    /**
     * Method returns if the role drop down button is displayed for the specified name
     * 
     * @param name String
     * @return True if the role drop down button is displayed for the specified name
     */
    public boolean isRoleButtonDisplayed(String name)
    {
        return browser.isElementDisplayed(selectMember(name), currentRoleButton);
    }

    /**
     * Navigate to site groups page
     * 
     * @return
     */
    public SiteGroupsPage openSiteGroupsPage()
    {
        siteGroups.click();
        return new SiteGroupsPage();
    }

    /**
     * Navigate to site users page
     * 
     * @return
     */
    public SiteUsersPage openSiteUsersPage()
    {
        siteUsers.click();
        return new SiteUsersPage();
    }

    /**
     * Get all site users or groups names
     * 
     * @return
     */
    public List<String> getSiteMembersList()
    {
        List<String> names = new ArrayList<String>();
        for (WebElement aNamesList : namesList)
        {
            names.add(aNamesList.getText());
        }

        return names;
    }

    /**
     * Check if a user or a group is a site member
     *
     * @param name
     * @return true if name is found in the members list, false otherwise
     */
    public boolean isASiteMember(String name)
    {
        for (String member : getSiteMembersList())
        {
            if (member.equals(name))
                return true;
        }
        return false;
    }

    /**
     * Select row corresponding to an user or a group
     * 
     * @param name
     * @return
     */
    public WebElement selectMember(String name)
    {
        return browser.findFirstElementWithValue(siteMemberRow, name);
    }

    /**
     * Change the role for the specified userName
     * 
     * @param newRole String
     * @param userName String
     */
    public void changeRoleForMember(String newRole, String userName)
    {
        selectMember(userName).findElement(currentRoleButton).click();
        browser.selectOptionFromFilterOptionsList(newRole, dropDownOptionsList);
        browser.waitInSeconds(3);
    }

    public boolean isPendingInvitesDisplayed()
    {
        return browser.isElementDisplayed(pendingInvites);
    }
}