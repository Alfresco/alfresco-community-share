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
}
