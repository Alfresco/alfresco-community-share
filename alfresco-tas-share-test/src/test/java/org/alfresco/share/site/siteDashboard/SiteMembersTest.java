package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataUser.ListUserWithRoles;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class SiteMembersTest extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "siteMembers.dashletTitle";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteMembers.help";
    private static final String EXPECTED_ADD_USERS_LINK_TEXT = "siteMembers.addUsers";
    private static final String EXPECTED_ALL_MEMBERS_LINK_TEXT = "siteMembers.allMembers";
    private static final String EXPECTED_DASHLET_PAGINATION = "dashlet.defaultPagination";
    private static final String EXPECTED_MEMBERS_LIST_MESSAGE = "siteMembers.emptyMembers";
    private static final String EXPECTED_MANAGER_ROLE = "siteMembers.managerRole";
    private static final String EXPECTED_COLLABORATOR_ROLE = "siteMembers.collaboratorRole";
    private static final String ALL_MEMBERS = "All Members";
    private static final String EMPTY_SPACE = " ";

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    private SiteMembersDashlet siteMembersDashlet;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();

        setupAuthenticatedSession(userModel);
        siteDashboardPage.navigate(siteModel);
    }

    @TestRail (id = "C2799")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void checkDisplayCreatedUserInSiteMembersDashlet()
    {
        siteDashboardPage.navigate(siteModel);
        siteMembersDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_MEMBERS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();

        siteMembersDashlet
            .assertAddUsersLinkTextEquals(language.translate(EXPECTED_ADD_USERS_LINK_TEXT))
            .assertAllMembersLinkTextEquals(language.translate(EXPECTED_ALL_MEMBERS_LINK_TEXT))
            .assertPaginationTextEquals(language.translate(EXPECTED_DASHLET_PAGINATION))
            .assertMembersListMessageEquals(language.translate(EXPECTED_MEMBERS_LIST_MESSAGE))
            .assertUsernameEquals(userModel.getFirstName(), userModel.getLastName())
            .assertUserRoleEquals(language.translate(EXPECTED_MANAGER_ROLE));
    }

    @TestRail(id = "C588529")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITE_DASHBOARD})
    public void shouldDisplayUsernameInUserProfilePage()
    {
        ListUserWithRoles userRoles = dataUser.addUsersWithRolesToSite(siteModel, UserRole.SiteCollaborator);
        userModel = userRoles.getOneUserWithRole(UserRole.SiteCollaborator);

        siteDashboardPage.navigate(siteModel);
        siteMembersDashlet.renderedPage();

        siteMembersDashlet
            .assertUsernameEquals(userModel.getFirstName(), userModel.getLastName())
            .assertUserRoleEquals(language.translate(EXPECTED_COLLABORATOR_ROLE));

        siteMembersDashlet
            .navigateToProfilePageOfGivenUser(userModel.getFirstName()
                .concat(EMPTY_SPACE).concat(userModel.getLastName()))
            .assertUsernameEquals(userModel.getFirstName(), userModel.getLastName());
    }

    @TestRail(id = "C588530")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITE_DASHBOARD})
    public void shouldDisplayAllCreatedUsersInSiteMembersPage()
    {
        ListUserWithRoles userRoles = dataUser
            .addUsersWithRolesToSite(siteModel, UserRole.SiteCollaborator,
                UserRole.SiteConsumer,
                UserRole.SiteContributor);

        siteDashboardPage.navigate(siteModel);
        siteMembersDashlet.renderedPage();
        siteMembersDashlet.clickAllMembersButton(ALL_MEMBERS);

        userModel = userRoles.getOneUserWithRole(UserRole.SiteCollaborator);
        siteMembersDashlet
            .assertUsernameEquals(userModel.getFirstName(), userModel.getLastName());

        userModel = userRoles.getOneUserWithRole(UserRole.SiteConsumer);
        siteMembersDashlet
            .assertUsernameEquals(userModel.getFirstName(), userModel.getLastName());

        userModel = userRoles.getOneUserWithRole(UserRole.SiteContributor);
        siteMembersDashlet
            .assertUsernameEquals(userModel.getFirstName(), userModel.getLastName());
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
