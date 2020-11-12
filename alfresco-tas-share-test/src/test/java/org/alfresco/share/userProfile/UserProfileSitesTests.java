package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserProfileSitesTests extends BaseShareWebTests
{
    private UserSitesListPage userSitesPage;

    private UserModel user, invitedUser, noSitesUser;
    private SiteModel inviteSite, publicSite, notInvitedSite;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userSitesPage = new UserSitesListPage(browser);
    }

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        invitedUser = dataUser.createRandomTestUser();
        noSitesUser = dataUser.createRandomTestUser();

        inviteSite = dataSite.usingUser(user).createPrivateRandomSite();
        notInvitedSite = dataSite.usingUser(user).createPublicRandomSite();
        publicSite = dataSite.usingUser(invitedUser).createPublicRandomSite();
        dataUser.usingUser(user).addUserToSite(invitedUser, inviteSite, UserRole.SiteConsumer);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        deleteSites(inviteSite, notInvitedSite, publicSite);
        removeUserFromAlfresco(user, invitedUser, noSitesUser);
    }

    @TestRail (id = "C2154")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewSitesWhereUserHasMembershipTest()
    {
        setupAuthenticatedSession(invitedUser);
        userSitesPage.navigate(invitedUser)
            .assertSiteIsDisplayed(inviteSite)
            .assertSiteIsDisplayed(publicSite)
            .assertSiteIsNotDisplayed(notInvitedSite)
                .clickSite(publicSite).assertSiteDashboardPageIsOpened();
    }

    @TestRail (id = "C2309")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void userWithNoSitesTest()
    {
        setupAuthenticatedSession(invitedUser);
        userSitesPage.navigate(noSitesUser)
            .assertUserHasNoSitesMessageIsDisplayed();
    }
}
