package org.alfresco.po.share.site.members;

import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;

@PageObject
public class SiteUsersPage extends SiteMembersPage
{
    @Autowired
    UserProfilePage userProfilePage;

    @FindBy (css = "a[id*='invitePeople']")
    private WebElement addUsers;

    @FindBy (css = ".search-term")
    private WebElement searchBox;

    private String removeButton = "//button[contains(text(),'Remove')]";

    @RenderWebElement
    @FindBy (css = "button[id*='site-members']")
    private Button searchButton;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-members", getCurrentSiteName());
    }

    /**
     * Navigate to add users page
     *
     * @return
     */
    public AddSiteUsersPage goToAddUsersPage()
    {
        addUsers.click();
        return new AddSiteUsersPage();
    }

    /**
     * Click Search button
     */

    public void clickSearch()
    {
        searchButton.click();
    }

    /**
     * Search for a specific user in site members page
     *
     * @param userName
     * @return
     */
    public void searchForSiteMembers(String userName)
    {
        searchBox.clear();
        searchBox.sendKeys(userName);
    }

    /**
     * Method returns if 'Remove' button is displayed for a specified user
     *
     * @param userName String
     * @return True if button is displayed
     */
    public boolean isRemoveButtonDisplayedForUser(String userName)
    {
        return browser.isElementDisplayed(
            By.xpath("//td[descendant::a[normalize-space(text())='" + userName + "']]/../td[contains(@class,'uninvite')]" + removeButton));
    }

    /**
     * Checks whether remove button is enabled or not for an user or a group
     *
     * @param name
     * @return
     */
    public boolean isRemoveButtonEnabled(String name)
    {
        return selectMember(name).findElement(By.cssSelector("td[class*='uninvite'] button")).isEnabled();
    }

    /**
     * Clicks on the "Remove" button corresponding to the username
     *
     * @param username represents users firstName + lastName
     */
    public void removeUser(String username)
    {
        WebElement remove = browser
            .findElement(By.xpath("//td[descendant::a[normalize-space(text())='" + username + "']]/../td[contains(@class,'uninvite')]" + removeButton));
        remove.click();
    }

    /**
     * Method returns if the user is the original Manager of the site
     *
     * @param userName
     * @return True if user is Original Manager
     */
    public boolean isUserRoleNotChangeable(String role, String userName)
    {
        return browser.isElementDisplayed(
            By.xpath("//td[descendant::a[normalize-space(text())='" + userName + "']]/../td[contains(@class,'role')]/div/div[text()='" + role + "']"));
    }

    public UserProfilePage clickUser(String userName)
    {
        browser.findFirstDisplayedElement(By.xpath("//td//a[normalize-space(text())='" + userName + "']")).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }
}
