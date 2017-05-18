package org.alfresco.share.site.members;

import org.alfresco.common.DataUtil;
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
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
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
    private SiteDashboardPage siteDashboardPage;

    private String userManager;
    private String userTest;
    private String siteName;
    protected String taskName;

    @BeforeMethod(alwaysRun = true)
    public void createSite()
    {
        userManager = String.format("User1%s", DataUtil.getUniqueIdentifier());
        userTest = String.format("User2%s", DataUtil.getUniqueIdentifier());
        siteName = String.format("Site%s", DataUtil.getUniqueIdentifier());
        taskName = String.format("Request to join %s site", siteName);

        userService.create(adminUser, adminPassword, userManager, password, userManager + domain, userManager, userManager);
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        siteService.create(userManager, password, domain, siteName, siteName, Visibility.MODERATED);

        setupAuthenticatedSession(userTest, password);
    }

    @TestRail(id = "C2461")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void approvingUsersUsingMyTasksPage()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        siteFinderPage.navigate();
        siteFinderPage.searchSite(siteName);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteName));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteName, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

        LOG.info("Step 3: Login second user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);

        LOG.info("Step 4: Go to 'My Tasks' page");
        menuNavigationBar.goTo(myTasksPage);
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteName));

        LOG.info("Step 5: Click 'Edit Task'");
        myTasksPage.clickEditTask(siteName);

        LOG.info("Step 6: Approve the task");
        editTaskPage.approve("Approve", myTasksPage);
        Assert.assertFalse(myTasksPage.checkTaskWasFound(siteName));

        LOG.info("Step 7: Click on 'Completed' task");
        myTasksPage.clickCompletedTasks();
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteName));

        LOG.info("Step 8: Click on 'View task' button");
        myTasksPage.clickViewTask(siteName);
        Assert.assertEquals(viewTaskPage.getRequestDetails(), "Details: Request to join " + siteName + " site (Review Invitation)");
        Assert.assertEquals(viewTaskPage.getInviteTaskTitle(), "User " + userTest + " has requested to join the " + siteName + " Site.");

        LOG.info("Step 9: Login first user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userTest, password);

        LOG.info("Step 10: Verify 'My sites' dashlet from user's dashboard page");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName));
        mySitesDashlet.accessSite(siteName);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteName);
    }

    @TestRail(id = "C2462")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void approvingUsersUsingMyTasksDashlet()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        siteFinderPage.navigate();
        siteFinderPage.searchSite(siteName);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteName));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteName, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

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
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName));
        mySitesDashlet.accessSite(siteName);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteName);
    }

    @TestRail(id = "C2463")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void rejectingUsersUsingMyTasksPage()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        menuNavigationBar.goTo(siteFinderPage);
        siteFinderPage.searchSite(siteName);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteName));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteName, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

        LOG.info("Step 3: Login second user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);

        LOG.info("Step 4: Go to 'My Tasks' page");
        menuNavigationBar.goTo(myTasksPage);
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteName));

        LOG.info("Step 5: Click 'Edit Task'");
        myTasksPage.clickEditTask(siteName);

        LOG.info("Step 6: Reject the task");
        editTaskPage.reject("Reject", myTasksPage);
        Assert.assertFalse(myTasksPage.checkTaskWasFound(siteName));

        LOG.info("Step 7: Click on 'Completed' task");
        myTasksPage.clickCompletedTasks();
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteName));

        LOG.info("Step 8: Login first user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userTest, password);

        LOG.info("Step 9: Verify 'My sites' dashlet from user's dashboard page");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName));

        LOG.info("Step 10: Open 'Site Finder' page and search for 'moderatedSite'");
        menuNavigationBar.goTo(siteFinderPage);
        siteFinderPage.searchSite(siteName);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteName));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 11: Click on the site");
        siteFinderPage.accessSite(siteName);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteName);
        Assert.assertEquals(siteDashboardPage.getSiteVisibility(), "Moderated");
    }

    @TestRail(id = "C2464")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void rejectingUsersUsingMyTasksDashlet()
    {
        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        menuNavigationBar.goTo(siteFinderPage);
        siteFinderPage.searchSite(siteName);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteName));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteName, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

        LOG.info("Step 3: Login second user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);

        LOG.info("Step 4: Verify 'My Tasks' dashlet");
        userDashboardPage.navigate(userManager);
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
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName));

        LOG.info("Step 10: Open 'Site Finder' page and search for 'moderatedSite'");
        menuNavigationBar.goTo(siteFinderPage);
        siteFinderPage.searchSite(siteName);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteName));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 11: Click on the site");
        siteFinderPage.accessSite(siteName);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteName);
        Assert.assertEquals(siteDashboardPage.getSiteVisibility(), "Moderated");
    }
/*
    @TestRail(id = "C2549")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void onlySiteManagersApproveRejectRequestToJoinSite()
    {

        LOG.info("Preconditions: Create userCollaborator, userContributor and userConsumer");
        //List<UserData> usersData = DataUtil.createUsersWithRoles(Arrays.asList("SiteCollaborator", "SiteContributor", "SiteConsumer"), userManager, siteName);

        LOG.info("Step 1: Open 'Site Finder' page and search for 'moderatedSite'");
        menuNavigationBar.goTo(siteFinderPage);
        siteFinderPage.searchSite(siteName);
        Assert.assertTrue(siteFinderPage.checkSiteWasFound(siteName));
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed.");

        LOG.info("Step 2: Click 'Request to Join' button");
        siteFinderPage.clickSiteButton(siteName, "Request to Join");
        Assert.assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button is expected to be displayed.");

        for (UserData user : usersData)
        {
            LOG.info("Login as " + user.getUserRole());
            cleanupAuthenticatedSession();
            setupAuthenticatedSession(user.getUserName(), password);

            LOG.info("Verify 'My Tasks' dashlet");
            userDashboardPage.navigate(user.getUserName());
            Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "Task is not present in Active tasks");

            LOG.info("Verify 'My Tasks' page");
            menuNavigationBar.goTo(myTasksPage);
            Assert.assertFalse(myTasksPage.checkTaskWasFound(siteName), "Task is not present in Active tasks");
        }

        LOG.info("Step 3: Login as 'userManager'");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);

        LOG.info("Step 4: Verify 'My Tasks' page");
        menuNavigationBar.goTo(myTasksPage);
        Assert.assertTrue(myTasksPage.checkTaskWasFound(siteName), "Task is present in Active tasks");

        LOG.info("Step 5: Verify 'My Tasks' dashlet");
        userDashboardPage.navigate(userManager);
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "Task is present in Active tasks");

    }
    */
}