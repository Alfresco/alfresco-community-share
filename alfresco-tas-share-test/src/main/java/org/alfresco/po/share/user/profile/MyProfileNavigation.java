package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Link;

/**
 * Manage the navigation beetween user profile pages(Sites, Content, Trashcan, etc.)
 *
 * @author bogdan.bocancea
 */
@PageObject
public class MyProfileNavigation extends SharePage<MyProfileNavigation>
{
    @Autowired
    UserTrashcanPage userTrashcanPage;

    @RenderWebElement
    @FindBy (css = "a[id$='default-profile-link']")
    private Link info;

    @RenderWebElement
    @FindBy (css = "a[id$='default-user-sites-link']")
    private Link sites;

    @RenderWebElement
    @FindBy (css = "a[id$='user-content-link']")
    private Link content;

    @FindBy (css = "a[id$='following-link']")
    private Link following;

    @FindBy (css = "a[id$='followers-link']")
    private Link followers;

    @FindBy (css = "a[id$='change-password-link']")
    private Link changePassword;

    @FindBy (css = "a[id$='user-notifications-link']")
    private Link notification;

    @FindBy (css = "a[id$='user-trashcan-link']")
    private Link trashcan;

    public String myProfilePageUrl = "";

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/profile", getUserName());
    }

    public MyProfileNavigation navigate(String userName)
    {
        setUserName(userName);
        return (MyProfileNavigation) navigate();
    }

    /**
     * Click Info link
     *
     * @return {@link UserProfilePage}
     */
    public void clickInfo()
    {
        info.click();
    }

    /**
     * Verify if Sites link is displayed
     *
     * @return true if displayed
     */
    public boolean isInfoDisplayed()
    {
        return info.isDisplayed();
    }

    /**
     * Click Sites link
     *
     * @return {@link UserSitesListPage}
     */
    public void clickSites()
    {
        sites.click();
    }

    /**
     * Verify if Sites link is displayed
     *
     * @return true if displayed
     */
    public boolean isSitesDisplayed()
    {
        return sites.isDisplayed();
    }

    /**
     * Click Content link
     *
     * @return {@link UserContentPage}
     */
    public void clickContent()
    {
        content.click();
    }

    /**
     * Verify if Content link is displayed
     *
     * @return true if displayed
     */
    public boolean isContentDisplayed()
    {
        return content.isDisplayed();
    }

    /**
     * Click I'm Following link
     *
     * @return {@link FollowingPage}
     */
    public void clickFollowing()
    {
        following.click();
    }

    /**
     * Verify if I'm Following link is displayed
     *
     * @return true if displayed
     */
    public boolean isFollowingDisplayed()
    {
        return following.isDisplayed();
    }

    /**
     * Click Following Me link
     *
     * @return {@link FollowingPage}
     */
    public void clickFollowingMe()
    {
        followers.click();
    }

    /**
     * Verify if Following Me link is displayed
     *
     * @return true if displayed
     */
    public boolean isFollowingMeDisplayed()
    {
        return followers.isDisplayed();
    }

    /**
     * Click Change Password link
     *
     * @return {@link FollowingPage}
     */
    public void clickChangePassword()
    {
        changePassword.click();
    }

    /**
     * Verify if Change Password link is displayed
     *
     * @return true if displayed
     */
    public boolean isChangePasswordDisplayed()
    {
        return changePassword.isDisplayed();
    }

    /**
     * Click Nofification link
     *
     * @return {@link FollowingPage}
     */
    public void clickNotification()
    {
        notification.click();
    }

    /**
     * Verify if Notification link is displayed
     *
     * @return true if displayed
     */
    public boolean isNotificationDisplayed()
    {
        return notification.isDisplayed();
    }

    /**
     * Click Trashcan link
     *
     * @return {@link FollowingPage}
     */
    public UserTrashcanPage clickTrashcan()
    {
        trashcan.click();
        return (UserTrashcanPage) userTrashcanPage.renderedPage();
    }

    /**
     * Verify if Trashcan link is displayed
     *
     * @return true if displayed
     */
    public boolean isTrashcanDisplayed()
    {
        return trashcan.isDisplayed();
    }


}
