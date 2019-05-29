package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserNotificationsPage extends SharePage<UserNotificationsPage>
{
    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/user-notifications", getUserName());
    }

    public UserNotificationsPage navigate(String userName)
    {
        setUserName(userName);
        return (UserNotificationsPage) navigate();
    }

    /**
     * Open Notifications page from the my profile navigation links
     *
     * @param myProfileNavigation
     * @return {@link UserNotificationsPage}
     */
    public UserNotificationsPage openFromNavigationLink(MyProfileNavigation myProfileNavigation)
    {
        myProfileNavigation.clickContent();
        return (UserNotificationsPage) this.renderedPage();
    }
}
