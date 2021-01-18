package org.alfresco.po.share.site.members;

import org.alfresco.po.share.user.profile.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SiteUsersPage extends SiteMembersPage
{
    private final By addUsers = By.cssSelector("a[id*='invitePeople']");
    private final By searchBox = By.cssSelector(".search-term");
    private final String removeButton = "//button[contains(text(),'Remove')]";
    public final String pattern = "//td[descendant::a[normalize-space(text())='";
    private By searchButton = By.cssSelector("button[id*='site-members']");

    public SiteUsersPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-members", getCurrentSiteName());
    }

    public AddSiteUsersPage goToAddUsersPage()
    {
        webElementInteraction.clickElement(addUsers);
        return new AddSiteUsersPage(webDriver);
    }

    public void clickSearch()
    {
        webElementInteraction.findElement(searchButton).click();
    }

    public void searchForSiteMembers(String userName)
    {
        webElementInteraction.clearAndType(webElementInteraction.findElement(searchBox), userName);
    }

    public boolean isRemoveButtonDisplayedForUser(String userName)
    {
        return webElementInteraction.isElementDisplayed(
            By.xpath(pattern + userName + "']]/../td[contains(@class,'uninvite')]" + removeButton));
    }

    public boolean isRemoveButtonEnabled(String name)
    {
        return selectMember(name).findElement(By.cssSelector("td[class*='uninvite'] button")).isEnabled();
    }

    public void removeUser(String username)
    {
        webElementInteraction.findElement(By.xpath(
            pattern + username + "']]/../td[contains(@class,'uninvite')]" + removeButton)).click();
    }

    public boolean isUserRoleNotChangeable(String role, String userName)
    {
        return webElementInteraction.isElementDisplayed(By.xpath(
            pattern + userName + "']]/../td[contains(@class,'role')]/div/div[text()='" + role + "']"));
    }

    public UserProfilePage clickUser(String userName)
    {
        webElementInteraction.findFirstDisplayedElement(By.xpath("//td//a[normalize-space(text())='" + userName + "']")).click();
        return new UserProfilePage(webDriver);
    }
}
