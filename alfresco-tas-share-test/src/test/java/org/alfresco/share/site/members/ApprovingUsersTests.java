package org.alfresco.share.site.members;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.navigation.MenuNavigationBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.ViewTaskPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ApprovingUsersTests extends ContextAwareWebTest
{
    @Autowired
    MenuNavigationBar menuNavigationBar;

    @Autowired
    SiteFinderPage siteFinderPage;

    @Autowired
    MyTasksPage myTasksPage;

    @Autowired
    EditTaskPage editTaskPage;

    @Autowired
    ViewTaskPage viewTaskPage;

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    MyTasksDashlet myTasksDashlet;
    @Autowired
    DataUser dataUser;
    @Autowired
    DataSite dataSite;
    UserModel testUser;
    UserModel managerUser;
    UserModel collaboratorUser;
    UserModel contributorUser;
    UserModel consumerUser;
    SiteModel moderatedSite;
    @Autowired
    private SiteDashboardPage siteDashboardPage;
    private String userManager = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String userTest = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String siteNameC2461 = "SiteC2461" + RandomData.getRandomAlphanumeric();
    private String siteNameC2462 = "SiteC2462" + RandomData.getRandomAlphanumeric();
    private String siteNameC2463 = "SiteC2463" + RandomData.getRandomAlphanumeric();
    private String siteNameC2464 = "SiteC2464" + RandomData.getRandomAlphanumeric();
    private String siteNameC2549 = "SiteC2549" + RandomData.getRandomAlphanumeric();
    private String taskName;

    @BeforeClass (alwaysRun = true)
    public void setupTest() throws DataPreparationException
    {
        testUser = dataUser.createRandomTestUser();
        managerUser = dataUser.createRandomTestUser();
        collaboratorUser = dataUser.createRandomTestUser();
        contributorUser = dataUser.createRandomTestUser();
        consumerUser = dataUser.createRandomTestUser();
        moderatedSite = dataSite.usingUser(managerUser).createModeratedRandomSite();
        userService.create(adminUser, adminPassword, userManager, password, userManager + domain, userManager, userManager);
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        siteService.create(userManager, password, domain, siteNameC2461, siteNameC2461, SiteService.Visibility.MODERATED);
        siteService.create(userManager, password, domain, siteNameC2462, siteNameC2462, SiteService.Visibility.MODERATED);
        siteService.create(userManager, password, domain, siteNameC2463, siteNameC2463, SiteService.Visibility.MODERATED);
        siteService.create(userManager, password, domain, siteNameC2464, siteNameC2464, SiteService.Visibility.MODERATED);
        siteService.create(userManager, password, domain, siteNameC2549, siteNameC2549, SiteService.Visibility.MODERATED);
        dataUser.addUserToSite(collaboratorUser, moderatedSite, UserRole.SiteCollaborator);
        dataUser.addUserToSite(contributorUser, moderatedSite, UserRole.SiteContributor);
        dataUser.addUserToSite(consumerUser, moderatedSite, UserRole.SiteConsumer);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userManager);

        userService.delete(adminUser, adminPassword, userTest);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userTest);

        userService.delete(adminUser, adminPassword, collaboratorUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + collaboratorUser.getUsername());

        userService.delete(adminUser, adminPassword, testUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser.getUsername());

        userService.delete(adminUser, adminPassword, managerUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + managerUser.getUsername());

        userService.delete(adminUser, adminPassword, contributorUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + contributorUser.getUsername());

        userService.delete(adminUser, adminPassword, consumerUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + consumerUser.getUsername());


        siteService.delete(adminUser, adminPassword, siteNameC2461);
        siteService.delete(adminUser, adminPassword, siteNameC2462);
        siteService.delete(adminUser, adminPassword, siteNameC2463);

        siteService.delete(adminUser, adminPassword, siteNameC2464);
        siteService.delete(adminUser, adminPassword, siteNameC2549);
        siteService.delete(adminUser, adminPassword, moderatedSite.getTitle());

    }

    @BeforeMethod (alwaysRun = true)
    public void beforeMethod()
    {
        setupAuthenticatedSession(userTest, password);
        getBrowser().waitInSeconds(3);
        siteFinderPage.navigate();
    }

    @TestRail (id = "C2461")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void approvingUsersUsingMyTasksPage()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        taskName = String.format("Request to join %s site", siteNameC2461);
        siteFinderPage.navigate();
        siteFinderPage.searchSiteWithRetry(siteNameC2461);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2461));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2461, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteNameC2461, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2461, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

        LOG.info("Step 3: Login second user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);

        LOG.info("Step 4: Go to 'My Tasks' page");
        myTasksPage.navigateByMenuBar();
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteNameC2461));

        LOG.info("Step 5: Click 'Edit Task'");
        myTasksPage.clickEditTask(siteNameC2461);

        LOG.info("Step 6: Approve the task");
        editTaskPage.approve("Approve", myTasksPage);
        Assert.assertFalse(myTasksPage.checkTaskWasFound(siteNameC2461));

        LOG.info("Step 7: Click on 'Completed' task");
        myTasksPage.clickCompletedTasks();
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteNameC2461));

        LOG.info("Step 8: Click on 'View task' button");
        myTasksPage.clickViewTask(siteNameC2461);
        Assert.assertEquals(viewTaskPage.getRequestDetails(), "Details: Request to join " + siteNameC2461 + " site (Review Invitation)");
        Assert.assertEquals(viewTaskPage.getInviteTaskTitle(), "User " + userTest + " has requested to join the " + siteNameC2461 + " Site.");

        LOG.info("Step 9: Login first user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userTest, password);

        LOG.info("Step 10: Verify 'My sites' dashlet from user's dashboard page");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteNameC2461));
        mySitesDashlet.accessSite(siteNameC2461);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteNameC2461);
    }

    @TestRail (id = "C2462")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void approvingUsersUsingMyTasksDashlet()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        siteFinderPage.navigate();
        taskName = String.format("Request to join %s site", siteNameC2462);
        siteFinderPage.searchSiteWithRetry(siteNameC2462);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2462));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2462, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteNameC2462, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2462, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

        LOG.info("Step 3: Login second user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);

        LOG.info("Step 4: Verify 'My Tasks' dashlet");
        userDashboardPage.navigate(userManager);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "My Tasks Dashlet title is not correct");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName));

        LOG.info("Step 5: Click 'Edit Task'");
        myTasksDashlet.clickEditTask(taskName);

        LOG.info("Step 6: Approve the task");
        editTaskPage.approve("Approve", myTasksDashlet);
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName));

        LOG.info("Step 7: Switch the filter to 'Completed tasks'");
        userDashboardPage.navigate(userManager);
        myTasksDashlet.selectOptionFromTaskFilters("Completed Tasks");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName));

        LOG.info("Step 8: Login first user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userTest, password);

        LOG.info("Step 9: Verify 'My sites' dashlet from user's dashboard page");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteNameC2462));
        mySitesDashlet.accessSite(siteNameC2462);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteNameC2462);
    }

    @TestRail (id = "C2463")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void rejectingUsersUsingMyTasksPage()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        siteFinderPage.navigate();
        siteFinderPage.searchSiteWithRetry(siteNameC2463);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2463));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2463, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteNameC2463, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2463, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

        LOG.info("Step 3: Login second user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);

        LOG.info("Step 4: Go to 'My Tasks' page");
        menuNavigationBar.goTo(myTasksPage);
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteNameC2463));

        LOG.info("Step 5: Click 'Edit Task'");
        myTasksPage.clickEditTask(siteNameC2463);

        LOG.info("Step 6: Reject the task");
        editTaskPage.reject("Reject", myTasksPage);
        Assert.assertFalse(myTasksPage.checkTaskWasFound(siteNameC2463));

        LOG.info("Step 7: Click on 'Completed' task");
        myTasksPage.clickCompletedTasks();
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteNameC2463));

        LOG.info("Step 8: Login first user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userTest, password);

        LOG.info("Step 9: Verify 'My sites' dashlet from user's dashboard page");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteNameC2463));

        LOG.info("Step 10: Open 'Site Finder' page and search for 'moderatedSite'");
        menuNavigationBar.goTo(siteFinderPage);
        siteFinderPage.searchSiteWithRetry(siteNameC2463);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2463));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2463, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 11: Click on the site");
        siteFinderPage.accessSite(siteNameC2463);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteNameC2463);
        Assert.assertEquals(siteDashboardPage.getSiteVisibility(), "Moderated");
    }

    @TestRail (id = "C2464")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void rejectingUsersUsingMyTasksDashlet()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        taskName = String.format("Request to join %s site", siteNameC2464);
        siteFinderPage.navigate();
        siteFinderPage.searchSiteWithRetry(siteNameC2464);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2464));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2464, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteNameC2464, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2464, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

        LOG.info("Step 3: Login second user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);

        LOG.info("Step 4: Verify 'My Tasks' dashlet");
        userDashboardPage.navigate(userManager);
        myTasksDashlet.selectOptionFromTaskFilters("Active Tasks");
        getBrowser().waitInSeconds(3);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "My Tasks Dashlet title is not correct");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "Task is not present in Active tasks");

        LOG.info("Step 5: Click 'Edit Task'");
        myTasksDashlet.clickEditTask(taskName);

        LOG.info("Step 6: Reject the task");
        editTaskPage.reject("Reject", myTasksDashlet);
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "Task is present");

        LOG.info("Step 7: Switch the filter to 'Completed tasks'");
        myTasksDashlet.selectOptionFromTaskFilters("Completed Tasks");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "Task is not present in Completed tasks");

        LOG.info("Step 8: Login first user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userTest, password);

        LOG.info("Step 9: Verify 'My sites' dashlet from user's dashboard page");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteNameC2464));

        LOG.info("Step 10: Open 'Site Finder' page and search for 'moderatedSite'");
        menuNavigationBar.goTo(siteFinderPage);
        siteFinderPage.searchSiteWithRetry(siteNameC2464);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2464));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2464, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 11: Click on the site");
        siteFinderPage.accessSite(siteNameC2464);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteNameC2464);
        Assert.assertEquals(siteDashboardPage.getSiteVisibility(), "Moderated");
    }

    @TestRail (id = "C2549")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void onlySiteManagersApproveRejectRequestToJoinSite()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        taskName = String.format("Request to join %s site", moderatedSite.getTitle());
        siteFinderPage.navigate();
        siteFinderPage.searchSiteWithRetry(moderatedSite.getTitle());
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(moderatedSite.getTitle()));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(moderatedSite.getTitle(), "Request to Join"),
            "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(moderatedSite.getTitle(), "Request to Join");
        Assert.assertEquals(siteFinderPage.getButtonCancelRequestText("Cancel Request"), "Cancel Request", "Cancel Request button is not displayed");
        cleanupAuthenticatedSession();

        LOG.info("Step 3: Check My Tasks for consumer user");
        setupAuthenticatedSession(consumerUser.getUsername(), password);

        LOG.info("Verify 'My Tasks' dashlet");
        userDashboardPage.navigate(consumerUser.getUsername());
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "Task is not present in Active tasks");

        LOG.info("Verify 'My Tasks' page");
        menuNavigationBar.goTo(myTasksPage);
        Assert.assertFalse(myTasksPage.checkTaskWasFound(moderatedSite.getTitle()), "Task is not present in Active tasks");
        cleanupAuthenticatedSession();

        LOG.info("Step 4: Check My Tasks for Collaborator User");
        setupAuthenticatedSession(collaboratorUser.getUsername(), password);

        LOG.info("Verify 'My Tasks' dashlet");
        userDashboardPage.navigate(collaboratorUser.getUsername());
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "Task is not present in Active tasks");

        LOG.info("Verify 'My Tasks' page");
        menuNavigationBar.goTo(myTasksPage);
        Assert.assertFalse(myTasksPage.checkTaskWasFound(moderatedSite.getTitle()), "Task is not present in Active tasks");
        cleanupAuthenticatedSession();

        LOG.info("Step 5: Check My Tasks for Contributor user");
        setupAuthenticatedSession(contributorUser.getUsername(), password);

        LOG.info("Verify 'My Tasks' dashlet");
        userDashboardPage.navigate(contributorUser.getUsername());
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "Task is not present in Active tasks");

        LOG.info("Verify 'My Tasks' page");
        menuNavigationBar.goTo(myTasksPage);
        Assert.assertFalse(myTasksPage.checkTaskWasFound(moderatedSite.getTitle()), "Task is not present in Active tasks");
        cleanupAuthenticatedSession();

        LOG.info("Step 6: Login as 'userManager'");
        setupAuthenticatedSession(managerUser.getUsername(), password);

        LOG.info("Step 7: Verify 'My Tasks' page");
        menuNavigationBar.goTo(myTasksPage);
        Assert.assertTrue(myTasksPage.checkTaskWasFound(moderatedSite.getTitle()), "Task is present in Active tasks");

        LOG.info("Step 8: Verify 'My Tasks' dashlet");
        userDashboardPage.navigate(managerUser.getUsername());
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "Task is present in Active tasks");
    }
}