package org.alfresco.share.site.members;

import static org.alfresco.po.enums.GroupRoles.COLLABORATOR;
import static org.alfresco.po.enums.GroupRoles.CONSUMER;
import static org.alfresco.po.enums.GroupRoles.CONTRIBUTOR;
import static org.alfresco.po.enums.GroupRoles.MANAGER;

import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AddSiteMembersTests extends BaseTest
{
    private final int ONE_USER = 1;
    private final int TWO_USERS = 2;
    private final String EMPTY_SPACE = " ";

    private final String INFO_BALLOON_MESSAGE = "addUsersPage.setUserRolePanel.InfoIcon";
    private final String PAGE_TITLE = "alfrescoDocumentation.pageTitle";
    private final String SET_USER_ROLE_MESSAGE = "addUsersPage.setUserRolePanel.defaultMessage";
    private final String ADD_USER_TO_SITE_MESSAGE = "addUsersPage.addUsersToSitePanel.defaultMessage";
    private final String SEARCH_MESSAGE = "addUsersPage.searchPanel.defaultMessage";

    private AddSiteUsersPage addSiteUsersPage;
    private SiteMembersPage siteMembersPage;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(dataUser.usingAdmin().createRandomTestUser());
        siteModel.set(dataSite.usingUser(userModel.get()).createPublicRandomSite());

        authenticateUsingCookies(userModel.get());

        addSiteUsersPage = new AddSiteUsersPage(webDriver);
        siteMembersPage = new SiteMembersPage(webDriver);
    }

    @TestRail (id = "C2824")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayEmptyMessagesForUserPanels()
    {
        addSiteUsersPage
            .navigate(siteModel.get())
            .assertSearchUserMessageEqualsTo(language.translate(SEARCH_MESSAGE))
            .assertSetUserRoleInfoIconIsDisplayed()
            .assertSetUserRoleMessageEqualsTo(language.translate(SET_USER_ROLE_MESSAGE))
            .assertAddUsersToSiteMessageEqualsTo(language.translate(ADD_USER_TO_SITE_MESSAGE));
    }

    // Should be enabled after acs release 7.0.0
    @TestRail (id = "C3114")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES }, enabled = false)
    public void shouldOpenNewBrowserTabWhenClickSeeMore()
    {
        addSiteUsersPage
            .navigate(siteModel.get())
            .clickInfoIcon()
            .assertInfoBalloonTextEqualsTo(language.translate(INFO_BALLOON_MESSAGE));

        addSiteUsersPage
            .clickSeeMoreLink()
            .assertPageTitleOpenedInNewTabEquals(language.translate(PAGE_TITLE));
    }

    @TestRail (id = "C2409")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddManagerMemberToSite()
    {
        UserModel user = dataUser.usingAdmin().createRandomTestUser();

        addSiteUsersPage
            .navigate(siteModel.get())
            .searchUserWithName(user.getUsername())
            .clickSelectUserButton(user.getUsername())
            .setUserRole(user.getUsername(), MANAGER.getValue())
            .addUsersToSite();

        addSiteUsersPage
            .assertTotalUserIs(ONE_USER)
            .assertUserNameEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()))

            .assertUserRoleEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()),
                MANAGER.getValue());

        addSiteUsersPage.clickSiteMembers();
        siteMembersPage
            .assertSiteMemberNameEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()))
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), user.getUsername())
            .assertSiteMemberRemoveButtonIsEnabled(user.getUsername());
    }

    @TestRail (id = "C2410")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddCollaboratorMemberToSite()
    {
        UserModel user = dataUser.usingAdmin().createRandomTestUser();

        addSiteUsersPage
            .navigate(siteModel.get())
            .searchUserWithName(user.getUsername())
            .clickSelectUserButton(user.getUsername())
            .setUserRole(user.getUsername(), COLLABORATOR.getValue())
            .addUsersToSite();

        addSiteUsersPage
            .assertTotalUserIs(ONE_USER)
            .assertUserNameEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()))

            .assertUserRoleEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()),
                COLLABORATOR.getValue());

        addSiteUsersPage.clickSiteMembers();
        siteMembersPage
            .assertSiteMemberNameEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()))
            .assertSelectedRoleEqualsTo(COLLABORATOR.getValue(), user.getUsername())
            .assertSiteMemberRemoveButtonIsEnabled(user.getUsername());
    }

    @TestRail (id = "C2411")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddConsumerMemberToSite()
    {
        UserModel user = dataUser.usingAdmin().createRandomTestUser();

        addSiteUsersPage
            .navigate(siteModel.get())
            .searchUserWithName(user.getUsername())
            .clickSelectUserButton(user.getUsername())
            .setUserRole(user.getUsername(), CONSUMER.getValue())
            .addUsersToSite();

        addSiteUsersPage
            .assertTotalUserIs(ONE_USER)
            .assertUserNameEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()))

            .assertUserRoleEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()),
                CONSUMER.getValue());

        addSiteUsersPage.clickSiteMembers();
        siteMembersPage
            .assertSiteMemberNameEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()))
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), user.getUsername())
            .assertSiteMemberRemoveButtonIsEnabled(user.getUsername());
    }

    @TestRail (id = "C2412")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddContributorMemberToSite()
    {
        UserModel user = dataUser.usingAdmin().createRandomTestUser();

        addSiteUsersPage
            .navigate(siteModel.get())
            .searchUserWithName(user.getUsername())
            .clickSelectUserButton(user.getUsername())
            .setUserRole(user.getUsername(), CONTRIBUTOR.getValue())
            .addUsersToSite();

        addSiteUsersPage
            .assertTotalUserIs(ONE_USER)
            .assertUserNameEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()))

            .assertUserRoleEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()),
                CONTRIBUTOR.getValue());

        addSiteUsersPage.clickSiteMembers();
        siteMembersPage
            .assertSiteMemberNameEqualsTo(
                user.getFirstName().concat(EMPTY_SPACE).concat(user.getLastName()))
            .assertSelectedRoleEqualsTo(CONTRIBUTOR.getValue(), user.getUsername())
            .assertSiteMemberRemoveButtonIsEnabled(user.getUsername());
    }

    @TestRail (id = "C2413")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void shouldAddUsersWithContributorRoleToSite()
    {
        UserModel firstUser = dataUser.usingAdmin().createRandomTestUser();
        String formattedFirstUser = firstUser.getFirstName().concat(EMPTY_SPACE).concat(firstUser.getLastName());

        UserModel secondUser = dataUser.usingAdmin().createRandomTestUser();
        String formattedSecondUser = secondUser.getFirstName().concat(EMPTY_SPACE).concat(secondUser.getLastName());

        addSiteUsersPage
            .navigate(siteModel.get())
            .searchUserWithName(firstUser.getUsername())
            .clickSelectUserButton(firstUser.getUsername());

        addSiteUsersPage
            .searchUserWithName(secondUser.getUsername())
            .clickSelectUserButton(secondUser.getUsername());

        addSiteUsersPage
            .setAllRolesTo(CONTRIBUTOR.getValue())
            .addUsersToSite();

        addSiteUsersPage.assertTotalUserIs(TWO_USERS);

        addSiteUsersPage
            .assertUserNameEqualsTo(formattedFirstUser)
            .assertUserRoleEqualsTo(formattedFirstUser, CONTRIBUTOR.getValue());

        addSiteUsersPage
            .assertUserNameEqualsTo(formattedSecondUser)
            .assertUserRoleEqualsTo(formattedSecondUser, CONTRIBUTOR.getValue());

        addSiteUsersPage.clickSiteMembers();
        siteMembersPage
            .assertSiteMemberNameEqualsTo(formattedFirstUser)
            .assertSelectedRoleEqualsTo(CONTRIBUTOR.getValue(), firstUser.getUsername())
            .assertSiteMemberRemoveButtonIsEnabled(firstUser.getUsername());

        siteMembersPage
            .assertSiteMemberNameEqualsTo(formattedSecondUser)
            .assertSelectedRoleEqualsTo(CONTRIBUTOR.getValue(), secondUser.getUsername())
            .assertSiteMemberRemoveButtonIsEnabled(secondUser.getUsername());
    }

    @TestRail (id = "C2829")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void shouldAddUsersWithContributorAndConsumerRolesToSite()
    {
        UserModel firstUser = dataUser.usingAdmin().createRandomTestUser();
        String formattedFirstUser = firstUser.getFirstName().concat(EMPTY_SPACE).concat(firstUser.getLastName());

        UserModel secondUser = dataUser.usingAdmin().createRandomTestUser();
        String formattedSecondUser = secondUser.getFirstName().concat(EMPTY_SPACE).concat(secondUser.getLastName());

        addSiteUsersPage.navigate(siteModel.get());

        addSiteUsersPage
            .searchUserWithName(firstUser.getUsername())
            .clickSelectUserButton(firstUser.getUsername())
            .setUserRole(firstUser.getUsername(), CONTRIBUTOR.getValue());

        addSiteUsersPage
            .searchUserWithName(secondUser.getUsername())
            .clickSelectUserButton(secondUser.getUsername())
            .setUserRole(secondUser.getUsername(), CONSUMER.getValue());

        addSiteUsersPage.addUsersToSite();
        addSiteUsersPage.assertTotalUserIs(TWO_USERS);

        addSiteUsersPage
            .assertUserNameEqualsTo(formattedFirstUser)
            .assertUserRoleEqualsTo(formattedFirstUser, CONTRIBUTOR.getValue());

        addSiteUsersPage
            .assertUserNameEqualsTo(formattedSecondUser)
            .assertUserRoleEqualsTo(formattedSecondUser, CONSUMER.getValue());

        addSiteUsersPage.clickSiteMembers();
        siteMembersPage
            .assertSiteMemberNameEqualsTo(formattedFirstUser)
            .assertSelectedRoleEqualsTo(CONTRIBUTOR.getValue(), firstUser.getUsername())
            .assertSiteMemberRemoveButtonIsEnabled(firstUser.getUsername());

        siteMembersPage
            .assertSiteMemberNameEqualsTo(formattedSecondUser)
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), secondUser.getUsername())
            .assertSiteMemberRemoveButtonIsEnabled(secondUser.getUsername());
    }

    @TestRail (id = "C2414")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void shouldRemoveUserFromSelectRolesPanel()
    {
        UserModel userToRemove = dataUser.usingAdmin().createRandomTestUser();

        addSiteUsersPage
            .navigate(siteModel.get())
            .searchUserWithName(userToRemove.getUsername())
            .clickSelectUserButton(userToRemove.getUsername())
            .removeUser(userToRemove.getUsername())
            .assertSetUserRoleMessageEqualsTo(language.translate(SET_USER_ROLE_MESSAGE));
    }

    @AfterMethod(alwaysRun = true)
    public void testCleanup()
    {
        deleteSitesIfNotNull(siteModel.get());
        deleteUsersIfNotNull(userModel.get());
    }
}
