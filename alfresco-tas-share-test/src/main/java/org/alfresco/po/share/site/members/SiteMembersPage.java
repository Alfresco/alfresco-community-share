package org.alfresco.po.share.site.members;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SiteMembersPage extends SiteCommon<SiteMembersPage>
{
    private final By namesList = By.cssSelector("td+td>div.yui-dt-liner>h3");
    private final By siteUsers = By.cssSelector("a[id*='site-members-link']");
    private final By siteGroups = By.cssSelector("a[id*='site-groups-link']");
    private final By pendingInvites = By.cssSelector("a[id*='pending-invites-link']");
    private final By siteMemberRow = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private final By dropDownOptionsList = By.cssSelector("div.visible ul.first-of-type li a");
    private final By currentRoleButton = By.cssSelector("td[class*='role'] button");
    private final By removeButton = By.cssSelector(".uninvite button");
    private final By currentRole = By.cssSelector("td[class*='role'] div :first-child");
    private final String memberName = "td>a[href$='%s/profile']";

    public SiteMembersPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-members", getCurrentSiteName());
    }

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
        return webElementInteraction.isElementDisplayed(selectMember(name), currentRoleButton);
    }

    public SiteGroupsPage openSiteGroupsPage()
    {
        webElementInteraction.clickElement(siteGroups);
        return new SiteGroupsPage(webDriver);
    }

    public SiteUsersPage openSiteUsersPage()
    {
        webElementInteraction.clickElement(siteUsers);
        return new SiteUsersPage(webDriver);
    }

    public List<String> getSiteMembersList()
    {
        webElementInteraction.waitUntilElementsAreVisible(namesList);
        List<String> names = new ArrayList<>();
        for (WebElement aNamesList : webElementInteraction.findElements(namesList))
        {
            names.add(aNamesList.getText());
        }
        return names;
    }

    public boolean isASiteMember(String name)
    {
        return getSiteMembersList().stream().anyMatch(member -> member.equals(name));
    }

    public boolean isSiteMember(UserModel userModel)
    {
        return isASiteMember(userModel.getFirstName() + " " + userModel.getLastName());
    }

    public void waitSiteMemberToDisappear(String siteMember)
    {
        webElementInteraction.waitUntilElementDisappears(
            webElementInteraction.findElement(By.cssSelector(String.format(memberName, siteMember))));
    }

    public WebElement selectMember(String name)
    {
        return webElementInteraction.findFirstElementWithValue(siteMemberRow, name);
    }

    public void changeRoleForMember(String newRole, String userName)
    {
        webElementInteraction.clickElement(selectMember(userName).findElement(currentRoleButton));
        webElementInteraction.waitUntilElementsAreVisible(dropDownOptionsList);
        webElementInteraction.selectOptionFromFilterOptionsList(newRole, webElementInteraction.findElements(dropDownOptionsList));
        waitUntilNotificationMessageDisappears();
    }

    public boolean isPendingInvitesDisplayed()
    {
        return webElementInteraction.isElementDisplayed(pendingInvites);
    }
}