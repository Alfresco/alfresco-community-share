package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class FollowingPage extends SharePage<FollowingPage>
{
    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/following", getUserName());
    }

    public FollowingPage navigate(String userName)
    {
        setUserName(userName);
        return (FollowingPage) navigate();
    }

    /**
     * Open I'm Following page from the my profile navigation links
     *
     * @param myProfileNavigation
     * @return {@link FollowingPage}
     */
    public FollowingPage openFromNavigationLink(MyProfileNavigation myProfileNavigation)
    {
        myProfileNavigation.clickFollowing();
        return (FollowingPage) this.renderedPage();
    }
}
