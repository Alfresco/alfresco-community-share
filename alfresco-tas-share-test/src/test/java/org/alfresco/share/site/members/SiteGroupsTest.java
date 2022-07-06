package org.alfresco.share.site.members;

import static org.alfresco.po.enums.GroupRoles.COLLABORATOR;
import static org.alfresco.po.enums.GroupRoles.CONSUMER;
import static org.alfresco.po.enums.GroupRoles.CONTRIBUTOR;
import static org.alfresco.utility.constants.UserRole.SiteCollaborator;
import static org.alfresco.utility.constants.UserRole.SiteConsumer;
import static org.alfresco.utility.constants.UserRole.SiteContributor;

import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SiteGroupsTest extends BaseTest
{
    private SiteGroupsPage siteGroupsPage;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();
    private final ThreadLocal<GroupModel> groupModel = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(dataUser.createRandomTestUser());
        siteModel.set(dataSite.usingUser(userModel.get()).createPublicRandomSite());
        groupModel.set(dataGroup.usingAdmin().createRandomGroup());

        authenticateUsingCookies(userModel.get());

        siteGroupsPage = new SiteGroupsPage(webDriver);
    }

    @TestRail(id = "C2819")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayAllSiteGroups()
    {
        GroupModel collaboratorGroup = dataGroup.usingAdmin().createRandomGroup();
        GroupModel contributorGroup = dataGroup.usingAdmin().createRandomGroup();

        dataGroup.usingUser(userModel.get())
            .addGroupToSite(collaboratorGroup, siteModel.get(), SiteCollaborator);

        dataGroup.usingUser(userModel.get())
            .addGroupToSite(contributorGroup, siteModel.get(), SiteContributor);

        siteGroupsPage
            .navigate(siteModel.get())
            .assertSiteMemberNameEqualsTo(collaboratorGroup.getDisplayName())
            .assertSiteMemberNameEqualsTo(contributorGroup.getDisplayName());

        siteGroupsPage
            .assertSelectedRoleEqualsTo(COLLABORATOR.getValue(), collaboratorGroup.getDisplayName())
            .assertSelectedRoleEqualsTo(CONTRIBUTOR.getValue(), contributorGroup.getDisplayName());

        siteGroupsPage
            .assertRemoveGroupButtonIsDisplayed(collaboratorGroup.getDisplayName())
            .assertRemoveGroupButtonIsDisplayed(contributorGroup.getDisplayName());
    }

    @TestRail(id = "C2821")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayGroupFullNameWhenSearch()
    {
        GroupModel consumerGroup = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.usingUser(userModel.get()).addGroupToSite(consumerGroup, siteModel.get(), SiteConsumer);

        siteGroupsPage.navigate(siteModel.get());
        siteGroupsPage
            .searchGroupByName(consumerGroup.getDisplayName())
            .assertSiteMemberNameEqualsTo(consumerGroup.getDisplayName())
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), consumerGroup.getDisplayName());

        siteGroupsPage
            .assertRemoveGroupButtonIsDisplayed(consumerGroup.getDisplayName());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}