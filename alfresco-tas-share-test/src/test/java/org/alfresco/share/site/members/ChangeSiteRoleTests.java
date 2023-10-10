package org.alfresco.share.site.members;

import static org.alfresco.po.enums.GroupRoles.COLLABORATOR;
import static org.alfresco.po.enums.GroupRoles.CONSUMER;
import static org.alfresco.po.enums.GroupRoles.CONTRIBUTOR;
import static org.alfresco.po.enums.GroupRoles.MANAGER;
import static org.alfresco.utility.constants.UserRole.SiteCollaborator;
import static org.alfresco.utility.constants.UserRole.SiteManager;

import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ChangeSiteRoleTests extends BaseTest
{
    private final String EMPTY_SPACE = " ";

    private SiteGroupsPage siteGroupsPage;
    private SiteDashboardPage siteDashboardPage;
    private SiteMembersDashlet siteMembersDashlet;
    private SiteUsersPage siteUsersPage;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(dataUser.createRandomTestUser());
        authenticateUsingCookies(userModel.get());

        siteUsersPage = new SiteUsersPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        siteMembersDashlet = new SiteMembersDashlet(webDriver);
        siteGroupsPage = new SiteGroupsPage(webDriver);
    }

    @TestRail(id = "C2835")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void siteManagerShouldBeAbleToChangeRoleFromCollaboratorToContributor()
    {
        SiteModel siteModel = dataSite.usingUser(userModel.get()).createPublicRandomSite();
        UserModel collaborator = dataUser.usingAdmin().createRandomTestUser();
        dataUser.addUserToSite(collaborator, siteModel, SiteCollaborator);

        siteUsersPage
            .navigate(siteModel.getTitle())
            .assertSelectedRoleEqualsTo(COLLABORATOR.getValue(), collaborator.getUsername());

        siteUsersPage
            .changeRoleForMember(CONTRIBUTOR.getValue(), collaborator.getUsername())
            .assertSelectedRoleEqualsTo(CONTRIBUTOR.getValue(), collaborator.getUsername());

        siteDashboardPage
            .navigate(siteModel.getTitle());

        siteMembersDashlet
            .assertMemberRoleEqualsTo(CONTRIBUTOR.getValue());

        deleteSitesIfNotNull(siteModel);
    }

    @TestRail(id = "C2836")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void siteManagerShouldChangeRoleForGroup()
    {
        GroupModel groupModel = dataGroup.usingAdmin().createRandomGroup();
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        UserModel consumer = dataUser.usingAdmin().createRandomTestUser();
        SiteModel siteModel = dataSite.usingUser(manager).createPublicRandomSite();

        dataGroup.usingUser(consumer).addUserToGroup(groupModel);
        dataGroup.usingUser(manager).addGroupToSite(groupModel, siteModel, SiteManager);

        authenticateUsingCookies(manager);

        siteUsersPage
            .navigate(siteModel.getTitle())
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), manager.getUsername());

        siteUsersPage
            .openSiteGroupsPage()
            .searchGroupByName(groupModel.getDisplayName())
            .assertSiteMemberNameEqualsTo(groupModel.getDisplayName());

        siteGroupsPage
            .changeRoleForMember(CONSUMER.getValue(), groupModel.getDisplayName())
            .openSiteUsersPage()
            .assertSiteMemberNameEqualsTo(
                manager.getFirstName().concat(EMPTY_SPACE).concat(manager.getLastName()));

        authenticateUsingCookies(consumer);

        siteUsersPage
            .navigate(siteModel)
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), consumer.getUsername());
    }

    @TestRail(id = "C2837")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void siteManagerShouldChangeRoleToConsumer()
    {
        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        UserModel consumer = dataUser.usingAdmin().createRandomTestUser();
        SiteModel siteModel = dataSite.usingUser(manager).createPublicRandomSite();

        authenticateUsingCookies(manager);

        dataUser.addUserToSite(consumer, siteModel, SiteManager);

        siteUsersPage
            .navigate(siteModel.getTitle())
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), consumer.getUsername());

        siteUsersPage
            .changeRoleForMember(CONSUMER.getValue(), consumer.getUsername());

        authenticateUsingCookies(consumer);

        siteUsersPage
            .navigate(siteModel)
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), consumer.getUsername());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteSitesIfNotNull();
        deleteUsersIfNotNull(userModel.get());
    }
}