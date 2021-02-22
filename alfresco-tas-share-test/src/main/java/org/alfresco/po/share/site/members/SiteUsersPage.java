package org.alfresco.po.share.site.members;

import static org.testng.Assert.assertFalse;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class SiteUsersPage extends SiteMembersPage
{
    private final By addUsers = By.cssSelector("a[id*='invitePeople']");
    private final By searchBox = By.cssSelector(".search-term");
    private final By searchButton = By.cssSelector("button[id*='site-members']");
    private final By removeButton = By.cssSelector("td[class*='uninvite'] button");

    private final String unInviteButtonPath = "td[class*='uninvite'] button";
    private final String removeButtonPath = "//button[contains(text(),'Remove')]";
    private final String pattern = "//td[descendant::a[normalize-space(text())='";

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
        clickElement(addUsers);
        return new AddSiteUsersPage(webDriver);
    }

    public void clickSearch()
    {
        findElement(searchButton).click();
    }

    public void searchForSiteMembers(String userName)
    {
        clearAndType(findElement(searchBox), userName);
    }

    public boolean isRemoveButtonDisplayedForUser(String userName)
    {
        return isElementDisplayed(
            By.xpath(pattern.concat(userName).concat(unInviteButtonPath).concat(removeButtonPath)));
    }

    public SiteUsersPage assertRemoveButtonIsDisabledForUser(String userName)
    {
        log.info("Assert user {} remove button is disabled", userName);
        assertFalse(getMemberName(userName).findElement(removeButton).isEnabled(),
            "Remove button is enabled");
        return this;
    }

    public void clickRemoveUser(String userName)
    {
        log.info("Click remove user");
        clickElement(By.xpath(
            pattern.concat(userName).concat(unInviteButtonPath).concat(removeButtonPath)));
    }

    public boolean isUserRoleNotChangeable(String role, String userName)
    {
        return isElementDisplayed(By.xpath(
            pattern + userName + "']]/../td[contains(@class,'role')]/div/div[text()='" + role + "']"));
    }

    public UserProfilePage clickUser(String userName)
    {
        findFirstDisplayedElement(By.xpath("//td//a[normalize-space(text())='" + userName + "']")).click();
        return new UserProfilePage(webDriver);
    }
}
