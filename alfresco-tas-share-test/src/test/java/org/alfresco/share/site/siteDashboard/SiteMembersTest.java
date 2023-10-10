package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataUser.ListUserWithRoles;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SiteMembersDashlet siteMembersDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteMembersDashlet = new SiteMembersDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2799")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void checkDisplayCreatedUserInSiteMembersDashlet()
    {
        siteDashboardPage.navigate(site.get());
        siteMembersDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .clickOnHelpIcon(DashletHelpIcon.SITE_MEMBERS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();

        siteMembersDashlet
            .assertAddUsersLinkTextEquals(language.translate(EXPECTED_ADD_USERS_LINK_TEXT))
            .assertAllMembersLinkTextEquals(language.translate(EXPECTED_ALL_MEMBERS_LINK_TEXT))
            .assertPaginationTextEquals(language.translate(EXPECTED_DASHLET_PAGINATION))
            .assertMembersListMessageEquals(language.translate(EXPECTED_MEMBERS_LIST_MESSAGE))
            .assertUsernameEquals(user.get().getFirstName(), user.get().getLastName())
            .assertUserRoleEquals(language.translate(EXPECTED_MANAGER_ROLE));
    }

    @TestRail(id = "C588529")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayUsernameInUserProfilePageWhenAccessedFromSiteMembersDashlet()
    {
        ListUserWithRoles userRoles = getDataUser().addUsersWithRolesToSite(site.get(), UserRole.SiteCollaborator);
        UserModel collaborator = userRoles.getOneUserWithRole(UserRole.SiteCollaborator);

        siteDashboardPage.navigate(site.get());

        siteMembersDashlet
            .assertUsernameEquals(collaborator.getFirstName(), collaborator.getLastName())
            .assertUserRoleEquals(language.translate(EXPECTED_COLLABORATOR_ROLE));

        siteMembersDashlet
            .clickUser(collaborator.getFirstName().concat(EMPTY_SPACE).concat(collaborator.getLastName()))
                .assertUserProfilePageIsOpened();

        getDataUser().usingAdmin().deleteUser(collaborator);
    }

    @TestRail(id = "C588530")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITE_DASHBOARD})
    public void shouldDisplayAllCreatedUsersInSiteMembersPage()
    {
        ListUserWithRoles userRoles = getDataUser()
            .addUsersWithRolesToSite(site.get(),
                UserRole.SiteCollaborator,
                UserRole.SiteConsumer,
                UserRole.SiteContributor);

        siteDashboardPage.navigate(site.get());
        siteMembersDashlet.clickAllMembersButton(ALL_MEMBERS);

        UserModel collaborator = userRoles.getOneUserWithRole(UserRole.SiteCollaborator);
        siteMembersDashlet
            .assertUsernameEquals(collaborator.getFirstName(), collaborator.getLastName());

        UserModel consumer = userRoles.getOneUserWithRole(UserRole.SiteConsumer);
        siteMembersDashlet
            .assertUsernameEquals(consumer.getFirstName(), consumer.getLastName());

        UserModel contributor = userRoles.getOneUserWithRole(UserRole.SiteContributor);
        siteMembersDashlet
            .assertUsernameEquals(contributor.getFirstName(), contributor.getLastName());

        deleteUsersIfNotNull(collaborator, consumer, contributor);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
