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
        return (FollowersPage) navigate();
    }

    /**
     * Open Following Me page from the my profile navigation links
     *
     * @param myProfileNavigation
     * @return {@link FollowersPage}
     */
    public FollowersPage openFromNavigationLink(MyProfileNavigation myProfileNavigation)
    {
        myProfileNavigation.clickFollowingMe();
        return (FollowersPage) this.renderedPage();
    }
}
