package org.alfresco.share.site.members;

import static java.util.Arrays.asList;
import static org.alfresco.po.enums.GroupRoles.COLLABORATOR;
import static org.alfresco.po.enums.GroupRoles.CONSUMER;
import static org.alfresco.po.enums.GroupRoles.CONTRIBUTOR;
import static org.alfresco.po.enums.GroupRoles.MANAGER;
import static org.alfresco.utility.constants.UserRole.SiteCollaborator;
import static org.alfresco.utility.constants.UserRole.SiteConsumer;
import static org.alfresco.utility.constants.UserRole.SiteContributor;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import org.alfresco.po.share.site.members.AddSiteGroupsPage;
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

public class AddSiteGroupsTest extends BaseTest
{
    private final String EXPECTED_ONE_GROUPS_ADDED_0_FAILURES = "peopleFinder.successfullyGroupAdded";
    private final String EXPECTED_NOTIFICATION_MESSAGE = "peopleFinder.emptyValueSearchNotification";
    private final String EXPECTED_NO_GROUPS_FOUND_LABEL = "peopleFinder.noGroupsFound";
    private final String NON_EXISTING_GROUP = randomAlphabetic(4);
    private final String EMPTY_INPUT = "";
    private final String EMPTY_SPACE = " ";

    private AddSiteGroupsPage addSiteGroupsPage;
    private SiteGroupsPage siteGroupsPage;
    private SiteUsersPage siteUsersPage;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();
    private final ThreadLocal<GroupModel> groupModel = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(getDataUser().usingAdmin().createRandomTestUser());
        siteModel.set(getDataSite().usingUser(userModel.get()).createPublicRandomSite());
        groupModel.set(dataGroup.usingAdmin().createRandomGroup());

        authenticateUsingCookies(userModel.get());

        addSiteGroupsPage = new AddSiteGroupsPage(webDriver);
        siteGroupsPage = new SiteGroupsPage(webDriver);
        siteUsersPage = new SiteUsersPage(webDriver);
    }

    @TestRail (id = "C2777")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddGroupWithManagerRole()
    {
        addSiteGroupsPage
            .navigate(siteModel.get())
            .searchGroupByName(groupModel.get().getDisplayName())
            .assertGroupNameEquals(groupModel.get().getDisplayName())
            .addGroupWithName(groupModel.get().getDisplayName())
            .assertInvitedGroupNameEqualsTo(groupModel.get().getDisplayName());

        addSiteGroupsPage
            .assertFilterRolesEqualTo(asList(
                MANAGER.getValue(),
                COLLABORATOR.getValue(),
                CONTRIBUTOR.getValue(),
                CONSUMER.getValue()));

        addSiteGroupsPage
            .setGroupRole(groupModel.get().getDisplayName(), MANAGER.getValue())
            .addGroups()
            .goBackToSiteGroupsPage();

        siteGroupsPage
            .assertSiteMemberNameEqualsTo(groupModel.get().getDisplayName())
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), groupModel.get().getDisplayName());

        siteGroupsPage
            .assertRemoveGroupButtonIsDisplayed(groupModel.get().getDisplayName());
    }

    @TestRail (id = "C2778")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddGroupWithCollaboratorRole()
    {
        addSiteGroupsPage
            .navigate(siteModel.get())
            .searchGroupByName(groupModel.get().getDisplayName())
            .assertGroupNameEquals(groupModel.get().getDisplayName())
            .addGroupWithName(groupModel.get().getDisplayName())
            .assertInvitedGroupNameEqualsTo(groupModel.get().getDisplayName());

        addSiteGroupsPage
            .assertFilterRolesEqualTo(asList(
                MANAGER.getValue(),
                COLLABORATOR.getValue(),
                CONTRIBUTOR.getValue(),
                CONSUMER.getValue()));

        addSiteGroupsPage
            .setGroupRole(groupModel.get().getDisplayName(), COLLABORATOR.getValue())
            .addGroups()
            .goBackToSiteGroupsPage();

        siteGroupsPage
            .assertSiteMemberNameEqualsTo(groupModel.get().getDisplayName())
            .assertSelectedRoleEqualsTo(COLLABORATOR.getValue(), groupModel.get().getDisplayName());

        siteGroupsPage
            .assertRemoveGroupButtonIsDisplayed(groupModel.get().getDisplayName());
    }

    @TestRail (id = "C2779")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddGroupWithContributorRole()
    {
        addSiteGroupsPage
            .navigate(siteModel.get())
            .searchGroupByName(groupModel.get().getDisplayName())
            .assertGroupNameEquals(groupModel.get().getDisplayName())
            .addGroupWithName(groupModel.get().getDisplayName())
            .assertInvitedGroupNameEqualsTo(groupModel.get().getDisplayName());

        addSiteGroupsPage
            .assertFilterRolesEqualTo(asList(
                MANAGER.getValue(),
                COLLABORATOR.getValue(),
                CONTRIBUTOR.getValue(),
                CONSUMER.getValue()));

        addSiteGroupsPage
            .setGroupRole(groupModel.get().getDisplayName(), CONTRIBUTOR.getValue())
            .addGroups()
            .goBackToSiteGroupsPage();

        siteGroupsPage
            .assertSiteMemberNameEqualsTo(groupModel.get().getDisplayName())
            .assertSelectedRoleEqualsTo(CONTRIBUTOR.getValue(), groupModel.get().getDisplayName());

        siteGroupsPage
            .assertRemoveGroupButtonIsDisplayed(groupModel.get().getDisplayName());
    }

    @TestRail (id = "C2780")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddGroupWithConsumerRole()
    {
        addSiteGroupsPage
            .navigate(siteModel.get())
            .searchGroupByName(groupModel.get().getDisplayName())
            .assertGroupNameEquals(groupModel.get().getDisplayName())
            .addGroupWithName(groupModel.get().getDisplayName())
            .assertInvitedGroupNameEqualsTo(groupModel.get().getDisplayName());

        addSiteGroupsPage
            .assertFilterRolesEqualTo(asList(
                MANAGER.getValue(),
                COLLABORATOR.getValue(),
                CONTRIBUTOR.getValue(),
                CONSUMER.getValue()));

        addSiteGroupsPage
            .setGroupRole(groupModel.get().getDisplayName(), CONSUMER.getValue())
            .addGroups()
            .goBackToSiteGroupsPage();

        siteGroupsPage
            .assertSiteMemberNameEqualsTo(groupModel.get().getDisplayName())
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), groupModel.get().getDisplayName());

        siteGroupsPage
            .assertRemoveGroupButtonIsDisplayed(groupModel.get().getDisplayName());
    }

    @TestRail (id = "C2785")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayNotificationMessageWhenSearchGroupWithoutInput()
    {
        addSiteGroupsPage
            .navigate(siteModel.get())
            .searchGroupByName(EMPTY_INPUT)
            .waitUntilNotificationMessageDisappears();

        addSiteGroupsPage
            .assertLastNotificationMessageEquals(language.translate(EXPECTED_NOTIFICATION_MESSAGE));
    }

    @TestRail (id = "C2785")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplaySpecificMessageWhenNoGroupIsFound()
    {
        addSiteGroupsPage
            .navigate(siteModel.get())
            .searchGroupByName(NON_EXISTING_GROUP);

        addSiteGroupsPage
            .assertNoGroupsFoundLabelEqualsTo(language.translate(EXPECTED_NO_GROUPS_FOUND_LABEL));
    }

    @TestRail (id = "C2786")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void onlySiteManagerCanAddGroupToSite()
    {
        GroupModel collaboratorGroup = dataGroup.usingAdmin().createRandomGroup();
        GroupModel contributorGroup = dataGroup.usingAdmin().createRandomGroup();
        GroupModel consumerGroup = dataGroup.usingAdmin().createRandomGroup();

        UserModel collaborator = dataUser.usingAdmin().createRandomTestUser();
        UserModel contributor = dataUser.usingAdmin().createRandomTestUser();
        UserModel consumer = dataUser.usingAdmin().createRandomTestUser();

        dataGroup.usingUser(collaborator).addUserToGroup(collaboratorGroup);
        dataGroup.usingUser(contributor).addUserToGroup(contributorGroup);
        dataGroup.usingUser(consumer).addUserToGroup(consumerGroup);

        dataGroup.usingUser(userModel.get())
            .addGroupToSite(collaboratorGroup, siteModel.get(), SiteCollaborator);

        dataGroup.usingUser(userModel.get())
            .addGroupToSite(contributorGroup, siteModel.get(), SiteContributor);

        dataGroup.usingUser(userModel.get())
            .addGroupToSite(consumerGroup, siteModel.get(), SiteConsumer);

        authenticateUsingCookies(collaborator);
        siteGroupsPage.navigate(siteModel.get());
        siteGroupsPage.assertAddGroupsButtonIsNotDisplayed();

        authenticateUsingCookies(contributor);
        siteGroupsPage.navigate(siteModel.get());
        siteGroupsPage.assertAddGroupsButtonIsNotDisplayed();

        authenticateUsingCookies(consumer);
        siteGroupsPage.navigate(siteModel.get());
        siteGroupsPage.assertAddGroupsButtonIsNotDisplayed();
    }

    @TestRail (id = "C2846")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldGroupMembersBeAddedToUsersList()
    {
        UserModel userToAddInGroup = getDataUser().usingAdmin().createRandomTestUser();
        dataGroup.usingUser(userToAddInGroup).addUserToGroup(groupModel.get());

        addSiteGroupsPage
            .navigate(siteModel.get())
            .addGroupWorkflow(groupModel.get().getDisplayName(), MANAGER.getValue());

        addSiteGroupsPage
            .assertLastNotificationMessageEquals(language.translate(EXPECTED_ONE_GROUPS_ADDED_0_FAILURES));

        siteUsersPage.navigate(siteModel.get());

        siteGroupsPage.searchGroupByName(userToAddInGroup.getUsername());
        siteGroupsPage.assertSiteMemberNameEqualsTo(userToAddInGroup.getFirstName()
            .concat(EMPTY_SPACE).concat(userToAddInGroup.getLastName()));

        siteUsersPage.assertRemoveButtonIsDisabledForUser(userToAddInGroup.getUsername());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteSitesIfNotNull(siteModel.get());
        deleteUsersIfNotNull(userModel.get());
        dataGroup.deleteGroup(groupModel.get());
    }
}