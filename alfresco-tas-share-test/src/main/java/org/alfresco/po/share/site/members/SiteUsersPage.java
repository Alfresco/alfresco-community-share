package org.alfresco.po.share.site.members;

import static org.testng.Assert.assertFalse;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteUsersPage extends SiteMembersPage
{
    private final By searchBox = By.cssSelector(".search-term");
    private final By searchButton = By.cssSelector("button[id*='site-members']");
    private final By removeButton = By.cssSelector("td[class*='uninvite'] button");

    private final String removeButtonPath = "//button[contains(text(),'Remove')]";
    private final String userRow = "//a[text()='%s']/../../../..";

    public SiteUsersPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-members", getCurrentSiteName());
    }

    public void clickSearch()
    {
        clickElement(searchButton);
    }

    public SiteUsersPage searchUserWithName(String username)
    {
        log.info("Search user with name {}", username);
        clearAndType(searchBox, username);
        clickSearch();
        return this;
    }

    public SiteUsersPage assertRemoveButtonIsNotDisplayedForUser(String username)
    {
        log.info("Assert remove button is not displayed for user {}", username);
        assertFalse(isRemoveButtonDisplayedForUser(username),
            String.format("Remove button is displayed for user %s", username));
        return this;
    }

    private boolean isRemoveButtonDisplayedForUser(String userName)
    {
        return isElementDisplayed(
            By.xpath(String.format(userRow, userName).concat(removeButtonPath)));
    }

    public SiteUsersPage assertRemoveButtonIsDisabledForUser(String userName)
    {
        log.info("Assert user {} remove button is disabled", userName);
        assertFalse(getMemberName(userName).findElement(removeButton).isEnabled(),
            String.format("Remove button is enabled for user %s", userName));
        return this;
    }

    public SiteUsersPage removeUser(String userName)
    {
        log.info("Remove user {}", userName);
        clickElement(getUserRow(userName).findElement(removeButton));
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public SiteUsersPage assertUserIsNotDisplayed(String username)
    {
        log.info("Assert username {} is not displayed", username);
        boolean isUserDisplayed = isElementDisplayed(By.xpath(String.format(userRow, username)));
        assertFalse(isUserDisplayed, String.format("User is displayed %s ", username));
        return this;
    }

    public UserProfilePage clickUser(String userName)
    {
        clickElement(findFirstDisplayedElement(By.xpath("//td//a[normalize-space(text())='" + userName + "']")));
        return new UserProfilePage(webDriver);
    }

    private WebElement getUserRow(String username)
    {
        return waitUntilElementIsVisible(By.xpath(String.format(userRow, username)));
    }
}
