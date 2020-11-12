package org.alfresco.po.share.site.members;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class SiteMembersPage extends SiteCommon<SiteMembersPage>
{
    private SiteGroupsPage siteGroupsPage;
    private SiteUsersPage siteUsersPage;

    private By namesList = By.cssSelector("td+td>div.yui-dt-liner>h3");
    @RenderWebElement
    private By siteUsers = By.cssSelector("a[id*='site-members-link']");
    private By siteGroups = By.cssSelector("a[id*='site-groups-link']");
    private By pendingInvites = By.cssSelector("a[id*='pending-invites-link']");
    private By siteMemberRow = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private By dropDownOptionsList = By.cssSelector("div.visible ul.first-of-type li a");
    private By currentRoleButton = By.cssSelector("td[class*='role'] button");
    private By removeButton = By.cssSelector(".uninvite button");
    private By currentRole = By.cssSelector("td[class*='role'] div :first-child");
    private String memberName = "td>a[href$='%s/profile']";

    public SiteMembersPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
        siteGroupsPage = new SiteGroupsPage(browser);
        siteUsersPage = new SiteUsersPage(browser);
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
        return getBrowser().isElementDisplayed(selectMember(name), currentRoleButton);
    }

    public SiteGroupsPage openSiteGroupsPage()
    {
        getBrowser().findElement(siteGroups).click();
        return (SiteGroupsPage) siteGroupsPage.renderedPage();
    }

    public SiteUsersPage openSiteUsersPage()
    {
        getBrowser().findElement(siteUsers).click();
        return (SiteUsersPage) siteUsersPage.renderedPage();
    }

    public List<String> getSiteMembersList()
    {
        getBrowser().waitUntilElementsVisible(namesList);
        List<String> names = new ArrayList<>();
        for (WebElement aNamesList : getBrowser().findElements(namesList))
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
        getBrowser().waitUntilElementDisappears(getBrowser().findElement(By.cssSelector(String.format(memberName, siteMember))));
    }

    public WebElement selectMember(String name)
    {
        return getBrowser().findFirstElementWithValue(siteMemberRow, name);
    }

    public void changeRoleForMember(String newRole, String userName)
    {
        selectMember(userName).findElement(currentRoleButton).click();
        getBrowser().waitUntilElementsVisible(dropDownOptionsList);
        getBrowser().selectOptionFromFilterOptionsList(newRole, getBrowser().findElements(dropDownOptionsList));
        waitUntilNotificationMessageDisappears();
    }

    public boolean isPendingInvitesDisplayed()
    {
        return getBrowser().isElementDisplayed(pendingInvites);
    }
}