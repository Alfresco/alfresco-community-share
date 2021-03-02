package org.alfresco.share.site.members;

import static org.alfresco.po.enums.GroupRoles.COLLABORATOR;
import static org.alfresco.po.enums.GroupRoles.CONSUMER;
import static org.alfresco.po.enums.GroupRoles.MANAGER;
import static org.alfresco.utility.constants.UserRole.SiteCollaborator;
import static org.alfresco.utility.constants.UserRole.SiteConsumer;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataUser.ListUserWithRoles;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RemoveMembersOrGroupsTest extends BaseTest
{
    private final String EMPTY_SPACE = " ";
    private final String JOIN_SITE = "join.site";
    private final String NO_GROUPS_FOUND = "peopleFinder.noGroupsFound";
    private final String REQUEST_TO_JOIN = "requestToJoin.site.option";

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    private SiteUsersPage siteUsersPage;
    private SiteDashboardPage siteDashboardPage;
    private SiteGroupsPage siteGroupsPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(dataUser.createRandomTestUser());
        siteModel.set(dataSite.usingUser(userModel.get()).createPublicRandomSite());

        authenticateUsingCookies(userModel.get());

        siteUsersPage = new SiteUsersPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        siteGroupsPage = new SiteGroupsPage(webDriver);
    }

    @TestRail(id = "C2882")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldRemoveUserCollaboratorFromPublicSite()
    {
        ListUserWithRoles userRoles = getDataUser()
            .addUsersWithRolesToSite(siteModel.get(), SiteCollaborator);

        UserModel collaborator = userRoles.getOneUserWithRole(SiteCollaborator);
        authenticateUsingCookies(userModel.get());

        String managerFullName = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());
        String collaboratorFullName = collaborator.getFirstName().concat(EMPTY_SPACE).concat(collaborator.getLastName());

        siteUsersPage
            .navigate(siteModel.get())
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), managerFullName)
            .assertSelectedRoleEqualsTo(COLLABORATOR.getValue(), collaboratorFullName);

        siteUsersPage
            .removeUser(collaboratorFullName)
            .assertUserIsNotDisplayed(collaboratorFullName);

        authenticateUsingCookies(collaborator);

        siteDashboardPage
            .navigate(siteModel.get())
            .openSiteConfiguration()
            .assertOptionEqualsTo(language.translate(JOIN_SITE));
    }

    @TestRail(id = "C2883")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldRemoveSiteGroup()
    {
        GroupModel consumerGroup = dataGroup.usingAdmin().createRandomGroup();
        UserModel consumer = dataUser.usingAdmin().createRandomTestUser();

        dataGroup.usingUser(consumer).addUserToGroup(consumerGroup);
        dataGroup.usingUser(userModel.get()).addGroupToSite(consumerGroup, siteModel.get(), SiteConsumer);

        authenticateUsingCookies(userModel.get());

        siteUsersPage
            .navigate(siteModel.get())
            .openSiteGroupsPage()
            .assertSiteMemberNameEqualsTo(consumerGroup.getDisplayName())
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), consumerGroup.getDisplayName());

        siteGroupsPage
            .assertRemoveGroupButtonIsDisplayed(consumerGroup.getDisplayName());

        siteGroupsPage
            .removeGroup(consumerGroup.getDisplayName())
            .searchGroupByName(consumerGroup.getDisplayName())
            .assertNoGroupsFoundLabelEqualsTo(language.translate(NO_GROUPS_FOUND));

        siteUsersPage
            .navigate(siteModel.get())
            .assertSiteMemberNameIsNotDisplayed(consumerGroup.getDisplayName());
    }

    @TestRail(id = "C2890")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void managerIsAbleToRemoveMemberFromPrivateSite()
    {
        ListUserWithRoles consumersList = getDataUser()
            .addUsersWithRolesToSite(siteModel.get(), SiteConsumer);
        UserModel consumer = consumersList.getOneUserWithRole(SiteConsumer);

        String managerFullName = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());
        String consumerFullName = consumer.getFirstName().concat(EMPTY_SPACE).concat(consumer.getLastName());

        authenticateUsingCookies(userModel.get());

        siteUsersPage
            .navigate(siteModel.get())
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), managerFullName)
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), consumerFullName);

        siteUsersPage
            .assertSiteMemberRemoveButtonIsEnabled(managerFullName)
            .assertSiteMemberRemoveButtonIsEnabled(consumerFullName);

        siteUsersPage
            .removeUser(consumerFullName)
            .searchUserWithName(consumerFullName)
            .assertUserIsNotDisplayed(consumerFullName);
    }

    @TestRail(id = "C2892")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void removedUserShouldBeAbleToRejoinSite()
    {
        ListUserWithRoles consumersList = getDataUser()
            .addUsersWithRolesToSite(siteModel.get(), SiteCollaborator);
        UserModel collaborator = consumersList.getOneUserWithRole(SiteCollaborator);

        SiteModel moderatedSite = dataSite.usingAdmin().createModeratedRandomSite();

        String managerFullName = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());
        String collaboratorFullName = collaborator.getFirstName().concat(EMPTY_SPACE).concat(collaborator.getLastName());

        authenticateUsingCookies(userModel.get());

        siteUsersPage
            .navigate(siteModel.get())
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), managerFullName)
            .assertSelectedRoleEqualsTo(COLLABORATOR.getValue(), collaboratorFullName);

        siteUsersPage
            .assertSiteMemberRemoveButtonIsEnabled(managerFullName)
            .assertSiteMemberRemoveButtonIsEnabled(collaboratorFullName);

        siteUsersPage
            .removeUser(collaboratorFullName)
            .searchUserWithName(collaboratorFullName)
            .assertUserIsNotDisplayed(collaboratorFullName);

        authenticateUsingCookies(collaborator);

        siteDashboardPage
            .navigate(moderatedSite)
            .openSiteConfiguration()
            .assertOptionEqualsTo(language.translate(REQUEST_TO_JOIN));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}