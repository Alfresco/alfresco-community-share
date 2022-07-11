package org.alfresco.share.site.members;

import static org.alfresco.utility.constants.UserRole.SiteManager;

import org.alfresco.po.share.site.members.PendingInvitesPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RequestSiteAccessTests extends BaseTest
{
    private final String REQUEST_TO_JOIN = "editTaskPage.requestToJoin.site";
    private final String ALFRESCO_EDIT_TASK = "editTaskPage.title";
    private final String ALFRESCO_PENDING = "invitation.status";
    private final String ALFRESCO_USER_DASHBOARD = "userDashboard.PageTitle";
    private final String EMPTY_SPACE = " ";

    private PendingInvitesPage pendingInvitesPage;
    private MyTasksPage myTasksPage;
    private EditTaskPage editTaskPage;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<UserModel> requester = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> moderatedSite = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(dataUser.createRandomTestUser());
        requester.set(dataUser.createRandomTestUser());
        moderatedSite.set(dataSite.usingUser(userModel.get()).createModeratedRandomSite());

        authenticateUsingCookies(userModel.get());

        pendingInvitesPage = new PendingInvitesPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);
    }

    @TestRail(id = "C14280")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldNotDisplayUserRequestToJoinSiteWhenCancel()
    {
        getUserService().requestSiteMembership(requester.get().getUsername(), requester.get().getPassword(),
            moderatedSite.get().getId());

        getUserService().removePendingSiteRequest(requester.get().getUsername(), requester.get().getPassword(),
            requester.get().getUsername(), moderatedSite.get().getId());

        authenticateUsingCookies(userModel.get());

        pendingInvitesPage
            .assertBrowserPageTitleIs(language.translate(ALFRESCO_USER_DASHBOARD))
            .navigate(moderatedSite.get())
            .assertBrowserPageTitleIs(language.translate(ALFRESCO_PENDING));

        pendingInvitesPage.assertUserHasNoPendingRequest(
            requester.get().getFirstName().concat(EMPTY_SPACE).concat(requester.get().getLastName()));
    }

    @TestRail(id = "C14283")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldNotDisplayUserRequestInPending()
    {
        UserModel userGroupManager = dataUser.usingAdmin().createRandomTestUser();
        GroupModel group = dataGroup.usingAdmin().createRandomGroup();

        dataGroup.usingUser(userGroupManager).addUserToGroup(group);

        getUserService().requestSiteMembership(requester.get().getUsername(), requester.get().getPassword(),
            moderatedSite.get().getId());

        authenticateUsingCookies(userModel.get());

        pendingInvitesPage.navigate(moderatedSite.get())
            .assertUserHasPendingRequest(
                requester.get().getFirstName().concat(EMPTY_SPACE).concat(requester.get().getLastName()));

        myTasksPage
            .navigateToMyTasks()
            .editTask(getSiteJoinRequestTaskName(moderatedSite.get()))
            .assertBrowserPageTitleIs(language.translate(ALFRESCO_EDIT_TASK));

        editTaskPage
            .clickClaimButton()
            .assertReleaseToPoolButtonIsDisplayed();

        authenticateUsingCookies(userGroupManager);

        pendingInvitesPage.navigate(moderatedSite.get())
            .assertUserHasNoPendingRequest(
                requester.get().getFirstName().concat(EMPTY_SPACE).concat(requester.get().getLastName()));

        dataGroup.deleteGroup(group);
    }

    @TestRail(id = "C14284")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldReleaseTaskPendingRequestAsGroupManager()
    {
        UserModel userGroupManager = dataUser.usingAdmin().createRandomTestUser();
        GroupModel group = dataGroup.usingAdmin().createRandomGroup();

        dataGroup.usingUser(userGroupManager).addUserToGroup(group);
        dataGroup.usingUser(userModel.get()).addGroupToSite(group, moderatedSite.get(), SiteManager);

        getUserService().requestSiteMembership(requester.get().getUsername(), requester.get().getPassword(),
            moderatedSite.get().getId());

        authenticateUsingCookies(userModel.get());

        pendingInvitesPage
            .navigate(moderatedSite.get())
            .assertUserHasPendingRequest(
                requester.get().getFirstName().concat(EMPTY_SPACE).concat(requester.get().getLastName()));

        myTasksPage
            .navigateToMyTasks()
            .editTask(getSiteJoinRequestTaskName(moderatedSite.get()))
            .assertBrowserPageTitleIs(language.translate(ALFRESCO_EDIT_TASK));

        editTaskPage
            .clickClaimButton()
            .assertReleaseToPoolButtonIsDisplayed();

        editTaskPage
            .clickReleaseToPoolButton();
        myTasksPage
            .editTask(getSiteJoinRequestTaskName(moderatedSite.get()))
            .assertClaimButtonIsDisplayed();

        authenticateUsingCookies(userGroupManager);
        pendingInvitesPage
            .navigate(moderatedSite.get())
            .assertBrowserPageTitleIs(language.translate(ALFRESCO_PENDING));

        pendingInvitesPage.assertUserHasPendingRequest(
            requester.get().getFirstName().concat(EMPTY_SPACE).concat(requester.get().getLastName()));

        dataGroup.deleteGroup(group);
    }

    @TestRail(id = "C14286")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldClaimTaskPendingRequestAsSiteManager()
    {
        getUserService()
            .requestSiteMembership(requester.get().getUsername(), requester.get().getPassword(),
                moderatedSite.get().getId());

        authenticateUsingCookies(userModel.get());

        pendingInvitesPage.navigate(moderatedSite.get())
            .assertUserHasPendingRequest(
                requester.get().getFirstName().concat(EMPTY_SPACE).concat(requester.get().getLastName()));

        myTasksPage
            .navigateToMyTasks()
            .editTask(getSiteJoinRequestTaskName(moderatedSite.get()))
            .assertBrowserPageTitleIs(language.translate(ALFRESCO_EDIT_TASK));

        authenticateUsingCookies(requester.get());

        pendingInvitesPage.navigate(moderatedSite.get())
            .assertUserHasNoPendingRequest(
                requester.get().getFirstName().concat(EMPTY_SPACE).concat(requester.get().getLastName()));
    }

    private String getSiteJoinRequestTaskName(SiteModel siteToJoin)
    {
        return String.format(language.translate(REQUEST_TO_JOIN), siteToJoin.getId());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteSitesIfNotNull(moderatedSite.get());
        deleteUsersIfNotNull(userModel.get(), requester.get());
    }
}