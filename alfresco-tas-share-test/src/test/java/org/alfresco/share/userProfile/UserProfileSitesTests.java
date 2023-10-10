package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserProfileSitesTests extends BaseTest
{
    private UserSitesListPage userSitesPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(dataUser.usingAdmin().createRandomTestUser());
        userSitesPage = new UserSitesListPage(webDriver);
    }

    @TestRail (id = "C2154")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewSitesWhereUserHasMembershipTest()
    {
        UserModel invitedUser = dataUser.usingAdmin().createRandomTestUser();
        SiteModel inviteSite = dataSite.usingUser(user.get()).createPrivateRandomSite();
        SiteModel notInvitedSite = dataSite.usingUser(user.get()).createPublicRandomSite();
        SiteModel publicSite = dataSite.usingUser(invitedUser).createPublicRandomSite();
        dataUser.usingUser(user.get()).addUserToSite(invitedUser, inviteSite, UserRole.SiteConsumer);

        authenticateUsingCookies(invitedUser);
        userSitesPage.navigate(invitedUser)
            .assertSiteIsDisplayed(inviteSite)
            .assertSiteIsDisplayed(publicSite)
            .assertSiteIsNotDisplayed(notInvitedSite)
                .clickSite(publicSite).assertSiteDashboardPageIsOpened();

        deleteSitesIfNotNull(inviteSite, notInvitedSite, publicSite);
    }

    @TestRail (id = "C2309")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, TestGroup.SSO })
    public void userWithNoSitesTest()
    {
        authenticateUsingCookies(user.get());
        userSitesPage.navigate(user.get())
            .assertUserHasNoSitesMessageIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown()
    {
        deleteUsersIfNotNull(user.get());
    }
}
