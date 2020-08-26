package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;

public class UserProfileSitesTests extends ContextAwareWebTest
{
    @Autowired
    private UserSitesListPage userSites;

    private UserModel user, invitedUser, noSitesUser;
    private SiteModel inviteSite, publicSite, notInvitedSite;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
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
        userSites.navigate(invitedUser)
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
        userSites.navigate(noSitesUser)
            .assertUserHasNoSitesMessageIsDisplayed();
    }
}
