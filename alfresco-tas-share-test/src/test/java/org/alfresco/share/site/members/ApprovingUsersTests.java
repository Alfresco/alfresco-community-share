package org.alfresco.share.site.members;

import static org.alfresco.utility.constants.UserRole.SiteCollaborator;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.ViewTaskPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ApprovingUsersTests extends BaseTest
{
    private final String EMPTY_SPACE = " ";
    private final String REQUEST_TO_JOIN = "requestToJoin.site";
    private final String REVIEW_INVITATION = "requestToJoin.reviewInvitation";
    private final String USER = "requestToJoin.user";
    private final String REQUESTED_TO_JOIN = "requestedToJoin.user";
    private final String SITE = "site";
    private final String TASK_MESSAGE="requestToJoin.taskMessage";

    private final String COMPLETED_TASKS = "myTasksPage.completed.title";
    private final String MY_TASKS = "myTasksDashlet.title";
    private final String ACTIVE_TASKS = "myTasksPage.active.title";

    private SiteFinderPage siteFinderPage;
    private MyTasksPage myTasksPage;
    private EditTaskPage editTaskPage;
    private ViewTaskPage viewTaskPage;
    private MySitesDashlet mySitesDashlet;
    private UserDashboardPage userDashboardPage;
    private MyTasksDashlet myTasksDashlet;
    private SiteDashboardPage siteDashboardPage;

    private final ThreadLocal<UserModel> managerUser = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void beforeMethod()
    {
        managerUser.set(dataUser.usingAdmin().createRandomTestUser());
        siteModel.set(dataSite.usingUser(managerUser.get()).createModeratedRandomSite());

        setupAuthenticatedSession(managerUser.get());

        siteFinderPage = new SiteFinderPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);
        viewTaskPage = new ViewTaskPage(webDriver);
        mySitesDashlet = new MySitesDashlet(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        myTasksDashlet = new MyTasksDashlet(webDriver);
    }

    @TestRail(id = "C2461")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldApproveUserToJoinSiteFromMyTaskPage()
    {
        UserModel userModel = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(userModel);

        siteFinderPage.navigate()
            .searchSiteWithName(siteModel.get().getId())
            .requestToJoinSite(siteModel.get().getId());

        setupAuthenticatedSession(managerUser.get());

        myTasksPage
            .navigateToMyTasks()
            .assertTaskNameEqualsTo(siteModel.get().getId())
            .editTask(siteModel.get().getId());

        editTaskPage
            .insertTaskComment(randomAlphanumeric(5))
            .approveTask();

        myTasksPage
            .navigateToCompletedTasks()
            .assertTaskNameEqualsTo(siteModel.get().getId())
            .viewTask(siteModel.get().getId());

        viewTaskPage
            .assertRequestDetailsEqualTo(language.translate(REQUEST_TO_JOIN).concat(EMPTY_SPACE)
                .concat(siteModel.get().getId()).concat(EMPTY_SPACE)
                .concat(language.translate(REVIEW_INVITATION)));

        viewTaskPage
            .assertInviteTaskTitleEqualsTo(
                language.translate(USER).concat(EMPTY_SPACE).concat(userModel.getUsername())
                    .concat(EMPTY_SPACE).concat(
                    language.translate(REQUESTED_TO_JOIN).concat(EMPTY_SPACE)
                        .concat(siteModel.get().getId()).concat(EMPTY_SPACE)
                        .concat(language.translate(SITE))));

        setupAuthenticatedSession(userModel);

        mySitesDashlet
            .accessSite(siteModel.get().getId());

        siteDashboardPage
            .assertPageHeadersEqualsTo(siteModel.get().getId());
    }

    @TestRail (id = "C2462")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldApproveUserFromMyTasksDashlet()
    {
        UserModel userModel = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(userModel);
        String taskName = String.format(language.translate(TASK_MESSAGE), siteModel.get().getTitle());

        siteFinderPage.navigate()
            .searchSiteWithName(siteModel.get().getId())
            .requestToJoinSite(siteModel.get().getId());

        setupAuthenticatedSession(managerUser.get());

        userDashboardPage.navigate(managerUser.get());
        myTasksDashlet
            .assertDashletTitleEquals(language.translate(MY_TASKS))
            .assertTaskNameEqualsTo(taskName)
            .editTask(taskName);

        editTaskPage.approveTask();
        userDashboardPage.navigate(managerUser.get());

        myTasksDashlet
            .selectFilterTaskOption(language.translate(COMPLETED_TASKS))
            .assertTaskOptionEqualsTo(language.translate(COMPLETED_TASKS))
            .assertTaskNameEqualsTo(taskName);

        setupAuthenticatedSession(userModel);

        mySitesDashlet
            .accessSite(siteModel.get().getId());

        siteDashboardPage
            .assertPageHeadersEqualsTo(siteModel.get().getId());
    }

    @TestRail (id = "C2463")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldRejectUserFromMyTasksPage()
    {
        UserModel userModel = dataUser.usingAdmin().createRandomTestUser();
        String taskName = String.format(language.translate(TASK_MESSAGE), siteModel.get().getTitle());

        setupAuthenticatedSession(userModel);

        siteFinderPage.navigate()
            .searchSiteWithName(siteModel.get().getId())
            .requestToJoinSite(siteModel.get().getId());

        setupAuthenticatedSession(managerUser.get());

        myTasksPage
            .navigateToMyTasks()
            .editTask(taskName);

        editTaskPage
            .insertTaskComment(randomAlphanumeric(5))
            .rejectTask();

        myTasksPage
            .assertRejectedTaskIsNotDisplayedInActiveTasks(taskName)
            .navigateToCompletedTasks()
            .assertTaskNameEqualsTo(siteModel.get().getId());

        setupAuthenticatedSession(userModel);

        mySitesDashlet
            .accessSite(siteModel.get().getId());

        siteDashboardPage
            .assertPageHeadersEqualsTo(siteModel.get().getId());
        siteDashboardPage
            .assertSiteVisibilityEqualsTo(Visibility.MODERATED.name());
    }

    @TestRail (id = "C2464")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldRejectUserFromMyTasksDashlet()
    {
        UserModel userModel = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(userModel);
        String taskName = String.format(language.translate(TASK_MESSAGE), siteModel.get().getTitle());

        siteFinderPage.navigate()
            .searchSiteWithName(siteModel.get().getId())
            .requestToJoinSite(siteModel.get().getId());

        setupAuthenticatedSession(managerUser.get());

        userDashboardPage.navigate(managerUser.get());
        myTasksDashlet
            .selectFilterTaskOption(language.translate(ACTIVE_TASKS))
            .assertDashletTitleEquals(language.translate(MY_TASKS))
            .assertTaskNameEqualsTo(taskName);

        myTasksDashlet
            .editTask(taskName)
            .rejectTask();

        myTasksPage
            .assertRejectedTaskIsNotDisplayedInActiveTasks(taskName);

        myTasksDashlet
            .selectFilterTaskOption(language.translate(COMPLETED_TASKS))
            .assertTaskNameEqualsTo(taskName);

        setupAuthenticatedSession(userModel);

        mySitesDashlet
            .accessSite(siteModel.get().getId());

        siteDashboardPage
            .assertPageHeadersEqualsTo(siteModel.get().getId());
    }

    @TestRail (id = "C2549")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void onlySiteManagerShouldApproveRequestToJoinSite()
    {
        UserModel collaborator = dataUser.usingAdmin().createRandomTestUser();
        UserModel userModel = dataUser.usingAdmin().createRandomTestUser();

        dataUser.usingUser(managerUser.get())
            .addUserToSite(collaborator, siteModel.get(), SiteCollaborator);

        String taskName = String.format(language.translate(TASK_MESSAGE), siteModel.get().getTitle());

        setupAuthenticatedSession(userModel);

        siteFinderPage.navigate();
        siteFinderPage
            .searchSiteWithName(siteModel.get().getId())
            .requestToJoinSite(siteModel.get().getId());

        setupAuthenticatedSession(collaborator);

        myTasksPage
            .navigateToMyTasks()
            .assertRejectedTaskIsNotDisplayedInActiveTasks(taskName);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteSitesIfNotNull(siteModel.get());
        deleteUsersIfNotNull(managerUser.get());
    }
}