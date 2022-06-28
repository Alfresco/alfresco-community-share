package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class FollowersPage extends SharePage<FollowersPage>
{
    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/followers", getUserName());
    }

    public FollowersPage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }
}
