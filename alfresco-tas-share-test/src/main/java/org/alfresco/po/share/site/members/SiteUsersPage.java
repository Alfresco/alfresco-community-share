package org.alfresco.po.share.site.members;

import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SiteUsersPage extends SiteMembersPage
{
    private UserProfilePage userProfilePage;
    private AddSiteUsersPage addSiteUsersPage;

    private By addUsers = By.cssSelector("a[id*='invitePeople']");
    private By searchBox = By.cssSelector(".search-term");
    private String removeButton = "//button[contains(text(),'Remove')]";
    @RenderWebElement
    private By searchButton = By.cssSelector("button[id*='site-members']");

    public SiteUsersPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        userProfilePage = new UserProfilePage(browser);
        addSiteUsersPage = new AddSiteUsersPage(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-members", getCurrentSiteName());
    }

    public AddSiteUsersPage goToAddUsersPage()
    {
        getBrowser().findElement(addUsers).click();
        return (AddSiteUsersPage) addSiteUsersPage.renderedPage();
    }

    public void clickSearch()
    {
        getBrowser().findElement(searchButton).click();
    }

    public void searchForSiteMembers(String userName)
    {
        clearAndType(getBrowser().findElement(searchBox), userName);
    }

    public boolean isRemoveButtonDisplayedForUser(String userName)
    {
        return getBrowser().isElementDisplayed(
            By.xpath("//td[descendant::a[normalize-space(text())='" + userName + "']]/../td[contains(@class,'uninvite')]" + removeButton));
    }

    public boolean isRemoveButtonEnabled(String name)
    {
        return selectMember(name).findElement(By.cssSelector("td[class*='uninvite'] button")).isEnabled();
    }

    public void removeUser(String username)
    {
        getBrowser().findElement(By.xpath(
            "//td[descendant::a[normalize-space(text())='" + username + "']]/../td[contains(@class,'uninvite')]" + removeButton)).click();
    }

    public boolean isUserRoleNotChangeable(String role, String userName)
    {
        return getBrowser().isElementDisplayed(By.xpath(
            "//td[descendant::a[normalize-space(text())='" + userName + "']]/../td[contains(@class,'role')]/div/div[text()='" + role + "']"));
    }

    public UserProfilePage clickUser(String userName)
    {
        getBrowser().findFirstDisplayedElement(By.xpath("//td//a[normalize-space(text())='" + userName + "']")).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }
}
