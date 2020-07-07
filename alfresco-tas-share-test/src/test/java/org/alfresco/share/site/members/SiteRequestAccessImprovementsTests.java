package org.alfresco.share.site.members;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.po.share.site.members.PendingInvitesPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class SiteRequestAccessImprovementsTests extends ContextAwareWebTest
{
    private final String firstName = "firstName";
    private final String lastName = "lastName";
    private final String email = firstName + domain;
    private final String description = "description";
    @Autowired
    DataSite siteApi;
    @Autowired
    DataUser dataUser;
    SiteModel moderated;
    UserModel userName1;
    @Autowired
    private UserDashboardPage userDashboardPage;
    @Autowired
    private SiteMembersPage siteMembersPage;
    @Autowired
    private PendingInvitesPage pendingInvitesPage;
    @Autowired
    private MyTasksPage myTasksPage;
    @Autowired
    private EditTaskPage editTaskPage;
    private String userName2, userName3, user2, group, task;

    @BeforeMethod
    public void setupTest()
    {
        final String uniqueIdentifier = RandomData.getRandomAlphanumeric();

        userName2 = "user2-" + uniqueIdentifier;
        userName3 = "user3-" + uniqueIdentifier;
        user2 = firstName + " " + lastName + "2" + " (" + userName2 + ")";
        group = "group" + uniqueIdentifier;


        userName1 = dataUser.createRandomTestUser();

        userService.create(adminUser, adminPassword, userName2, password, email, firstName, lastName + "2");
        userService.create(adminUser, adminPassword, userName3, password, email, firstName, lastName + "3");

        moderated = siteApi.usingUser(userName1).createModeratedRandomSite();

        task = "Request to join " + moderated.getTitle() + " site";

        userService.requestSiteMembership(userName2, password, moderated.getTitle());


        groupService.createGroup(adminUser, adminPassword, group);
        groupService.changeGroupRole(userName1.getUsername(), password, moderated.getTitle(), group, UserRole.SiteManager.toString());
        groupService.addUserToGroup(adminUser, adminPassword, group, userName3);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName1.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1.getUsername());

        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);
        userService.delete(adminUser, adminPassword, userName3);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName3);

        siteService.delete(adminUser, adminPassword, moderated.getTitle());

    }

    @TestRail (id = "C14280")
    @Test ( groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelSitePendingRequest()
    {
        LOG.info("Precondition: Cancel request");
        userService.removePendingSiteRequest(userName1.getUsername(), password, userName2, moderated.getTitle());

        LOG.info("Precondition: Login as site admin " + userName1);
        setupAuthenticatedSession(userName1.getUsername(), password);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Navigate to Site Members page -> Pending tab");
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        ArrayList<String> pendingRequestsList = new ArrayList<>();
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequestsList.toString(), "Pending Requests=");
    }

    @TestRail (id = "C14283")
    @Test ( groups = { TestGroup.SANITY, TestGroup.SITES })
    public void claimRequestPendingTaskAsGroupManager()
    {
        setupAuthenticatedSession(userName1.getUsername(), password);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Navigate to Site Members page -> Pending tab");
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        ArrayList<String> pendingRequestsList = new ArrayList<>(Arrays.asList(user2));
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequestsList.toString(), "Pending Requests=");
        pendingRequestsList.clear();

        LOG.info("STEP2: Navigate to My Tasks page. Edit task and click 'Claim' button");
        myTasksPage.navigate();
        assertEquals(myTasksPage.getPageTitle(), "Alfresco » My Tasks", "Displayed page=");
        myTasksPage.clickEditTask(task);
        assertEquals(editTaskPage.getPageTitle(), "Alfresco » Edit Task", "Displayed page=");
        editTaskPage.clickClaimButton();

        LOG.info("STEP3: Login as " + userName3 + " and navigate to Pending Invites page");
        setupAuthenticatedSession(userName3, password);
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequestsList.toString(), "Pending Requests=");
    }

    @TestRail (id = "C14284")
    @Test ( groups = { TestGroup.SANITY, TestGroup.SITES })
    public void releaseToPoolRequestPendingTaskAsGroupManager()
    {
        setupAuthenticatedSession(userName1.getUsername(), password);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Navigate to 'Pending Invites' page");
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        ArrayList<String> pendingRequestsList = new ArrayList<>(Arrays.asList(user2));
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequestsList.toString(), "Pending Requests=");
        pendingRequestsList.clear();

        LOG.info("STEP2: Navigate to My Tasks page. Edit task and click 'Claim' button");
        myTasksPage.navigate();
        assertEquals(myTasksPage.getPageTitle(), "Alfresco » My Tasks", "Displayed page=");
        myTasksPage.clickEditTask(task);
        assertEquals(editTaskPage.getPageTitle(), "Alfresco » Edit Task", "Displayed page=");
        editTaskPage.clickClaimButton();

        LOG.info("STEP3: Click 'Release to pool' button");
        editTaskPage.clickReleaseToPoolButton();

        LOG.info("STEP4: Login as " + userName3 + ". Navigate to Pending Invites page");
        setupAuthenticatedSession(userName3, password);
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        pendingRequestsList.add(user2);
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequestsList.toString(), "Pending Requests=");
    }

    @TestRail (id = "C14285")
    @Test ( groups = { TestGroup.SANITY, TestGroup.SITES })
    public void claimRequestPendingTaskAsGroupManagerAndRemoveUserFromGroup()
    {
        setupAuthenticatedSession(userName3, password);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Navigate to Site Members page -> Pending tab");
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        ArrayList<String> pendingRequests = new ArrayList<>(Arrays.asList(user2));
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequests.toString(), "Pending Requests=");

        LOG.info("STEP2: Navigate to My Tasks page. Edit task and click 'Claim' button");
        myTasksPage.navigate();
        assertEquals(myTasksPage.getPageTitle(), "Alfresco » My Tasks", "Displayed page=");
        myTasksPage.clickEditTask(task);
        assertEquals(editTaskPage.getPageTitle(), "Alfresco » Edit Task", "Displayed page=");
        editTaskPage.clickClaimButton();

        LOG.info("STEP3: Navigate to Pending Invites page");
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequests.toString(), "Pending Requests=");
    }

    @TestRail (id = "C14286")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void claimRequestPendingTaskAsSiteManager()
    {
        String userName4 = "user4" + RandomData.getRandomAlphanumeric();
        userService.create(adminUser, adminPassword, userName4, password, email, firstName, lastName + "4");
        userService.createSiteMember(userName1.getUsername(), password, userName4, moderated.getTitle(), UserRole.SiteManager.toString());

        setupAuthenticatedSession(userName1.getUsername(), password);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Navigate to Site Members page -> Pending tab");
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        ArrayList<String> pendingRequestsList = new ArrayList<>(Arrays.asList(user2));
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequestsList.toString(), "Pending Requests=");
        pendingRequestsList.clear();

        LOG.info("STEP2: Navigate to My Tasks page. Edit task and click 'Claim' button");
        myTasksPage.navigate();
        assertEquals(myTasksPage.getPageTitle(), "Alfresco » My Tasks", "Displayed page=");
        myTasksPage.clickEditTask(task);
        assertEquals(editTaskPage.getPageTitle(), "Alfresco » Edit Task", "Displayed page=");
        editTaskPage.clickClaimButton();

        LOG.info("STEP3: Login as " + userName4 + " and navigate to Pending Invites page");
        setupAuthenticatedSession(userName4, password);
        pendingInvitesPage.navigate(moderated.getTitle());
        assertEquals(pendingInvitesPage.getPageTitle(), "Alfresco » Pending", "Displayed page=");
        assertEquals(pendingInvitesPage.getPendingRequests().toString(), pendingRequestsList.toString(), "Pending Requests=");

        userService.delete(adminUser, adminPassword, userName4);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName4);
    }
}