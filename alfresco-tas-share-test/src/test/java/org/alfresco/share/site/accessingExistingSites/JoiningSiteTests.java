package org.alfresco.share.site.accessingExistingSites;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.accessingExistingSites.RequestSentDialog;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 7/7/2016.
 */
public class JoiningSiteTests extends ContextAwareWebTest
{
    @Autowired
    SiteFinderPage siteFinderPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteUsersPage siteUsersPage;

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    MyTasksPage myTasksPage;

    @Autowired
    EditTaskPage editTaskPage;

    @Autowired
    RequestSentDialog requestSentDialog;

    @Autowired
    Notification notification;

    private String user1 = String.format("testUser1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("testUser2%s", RandomData.getRandomAlphanumeric());
    private String user2FirstName = "user2_firstName";
    private String user2LastName = "user2_lastName";
    private String siteNameC2833 = String.format("SiteName-C2833-%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("description%s", RandomData.getRandomAlphanumeric());
    private String siteNameC2823 = String.format("SiteName-C2823-%s", RandomData.getRandomAlphanumeric());
    private String siteNameC3053 = String.format("SiteName-C3053-%s", RandomData.getRandomAlphanumeric());
    private String siteNameC2831 = String.format("SiteName-C2831-%s", RandomData.getRandomAlphanumeric());
    private String siteNameC3059 = String.format("SiteName-C3059-%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2FirstName, user2LastName);
        siteService.create(user1, password, domain, siteNameC2833, description, SiteService.Visibility.MODERATED);
        siteService.create(user1, password, domain, siteNameC2823, description, SiteService.Visibility.PUBLIC);
        siteService.create(user1, password, domain, siteNameC3053, description, SiteService.Visibility.PUBLIC);
        siteService.create(user1, password, domain, siteNameC2831, description, SiteService.Visibility.MODERATED);
        siteService.create(user1, password, domain, siteNameC3059, description, SiteService.Visibility.MODERATED);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
        siteService.delete(adminUser, adminPassword, siteNameC2833);
        siteService.delete(adminUser, adminPassword, siteNameC2823);
        siteService.delete(adminUser, adminPassword, siteNameC3053);
        siteService.delete(adminUser, adminPassword, siteNameC2831);
        siteService.delete(adminUser, adminPassword, siteNameC3059);
    }

    @TestRail (id = "C2823")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void joinPublicSiteFromSiteFinderPage()
    {
        setupAuthenticatedSession(user2, password);
        siteFinderPage.navigate();
        LOG.info("STEP 1: Enter site name (" + siteNameC2823 + ") in textbox and click on 'Search' button.");
        siteFinderPage.searchSiteWithRetry(siteNameC2823);
        assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2823), "Site is expected to be found.");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2823, "Join"), "'Join' button is expected to be displayed for " + siteNameC2823);
        LOG.info("STEP 2: Click on " + siteNameC2823 + "'s name link. Click on 'Site Members' link.");
        siteUsersPage.navigate(siteNameC2823);
        assertFalse(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should not be displayed in the list of members for " + siteNameC2823);
        LOG.info("STEP 3: Return to 'Site Finder' page and click 'Join' button for " + siteNameC2823);
        siteFinderPage.navigate();
        siteFinderPage.searchSiteWithRetry(siteNameC2823);
        siteFinderPage.clickSiteButton(siteNameC2823, "Join");
        assertEquals(notification.getDisplayedNotification(), "Successfully added user " + user2 + " to site " + siteNameC2823);
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2823, "Leave"), "'Leave' button appears in place of 'Join' button for " + siteNameC2823);
        LOG.info("STEP 4: Click on " + siteNameC2823 + "'s name link. Click on 'Site Members' link.");
        siteUsersPage.navigate(siteNameC2823);
        assertTrue(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should be displayed in the list of members for " + siteNameC2823);
        assertTrue(siteUsersPage.isRoleSelected("Consumer", user2FirstName + " " + user2LastName), user2 + " should have Consumer role.");
        LOG.info("STEP 5: Go to 'User Dashboard' page and verify 'My Sites' dashlet.");
        userDashboardPage.navigate(user2);
        assertTrue(mySitesDashlet.isSitePresent(siteNameC2823), siteNameC2823 + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail (id = "C3053")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void joinPublicSiteFromSiteDashboardPage()
    {
        setupAuthenticatedSession(user2, password);
        LOG.info("STEP 1: Open " + siteNameC3053 + " dashboard  and click 'Site configuration options' -> 'Join Site'.");
        siteDashboardPage.navigate(siteNameC3053);
        siteDashboardPage.clickSiteConfiguration();
        siteDashboardPage.clickOptionInSiteConfigurationDropDown("Join Site", siteDashboardPage);
        LOG.info("STEP 2: Click on 'Site Members' link.");
        getBrowser().waitInSeconds(4);

        siteUsersPage.navigate(siteNameC3053);
        getBrowser().waitInSeconds(8);
        assertTrue(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should be displayed in the list of members for " + siteNameC3053);

        assertTrue(siteUsersPage.isRoleSelected("Consumer", user2FirstName + " " + user2LastName), user2 + " should have Consumer role.");
        LOG.info("STEP 3: Click again on 'Site Configuration Options' icon.");
        getBrowser().waitInSeconds(7);
        siteDashboardPage.clickSiteConfiguration();
        assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Join Site"), "'Join Site' action should be available in the 'Site Configuration Options' drop-down menu.");
        LOG.info("STEP 4: Go to 'User Dashboard' page and verify 'My Sites' dashlet.");
        userDashboardPage.navigate(user2);
        assertTrue(mySitesDashlet.isSitePresent(siteNameC3053), siteNameC3053 + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail (id = "C2831")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void requestToJoinModeratedSiteFromSiteFinderPage()
    {
        setupAuthenticatedSession(user2, password);
        siteFinderPage.navigate();
        LOG.info("STEP 1: Enter site name (" + siteNameC2831 + ") in textbox and click on 'Search' button.");
        getBrowser().waitInSeconds(5);
        siteFinderPage.searchSiteWithRetry(siteNameC2831);
        assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2831), "Site is expected to be found.");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2831, "Request to Join"), "'Request to Join' button is expected to be displayed for " + siteNameC2831);
        LOG.info("STEP 2: Click on 'Request to Join' button.");
        try
        {
            siteFinderPage.clickSiteButton(siteNameC2831, "Request to Join");
            assertEquals(notification.getDisplayedNotification(), "Successfully requested to join site " + siteNameC2831);
            assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2831, "Cancel Request"), "'Cancel Request' button appears in place of 'Request to Join' button for " + siteNameC2831);
        } catch (TimeoutException e)
        {
            getBrowser().refresh();
            siteFinderPage.searchSiteWithRetry(siteNameC2831);
            assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2831, "Cancel Request"), "'Cancel Request' button appears in place of 'Request to Join' button for " + siteNameC2831);
        }
        LOG.info("STEP 3: Logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
        myTasksPage.navigate();
        assertTrue(myTasksPage.checkTaskWasFound(siteNameC2831), "'Request to join " + siteNameC2831 + " site' task is expected to be displayed in 'Active Tasks'.");
        LOG.info("STEP 4: Click on 'Request to join " + siteNameC2831 + " site' task.");
        myTasksPage.clickEditTask(siteNameC2831);
        LOG.info("STEP 5: Click on 'Approve' button.");
        editTaskPage.approve("Approve", myTasksPage);
        myTasksPage.clickCompletedTasks();
        assertTrue(myTasksPage.checkTaskWasFound(siteNameC2831), "'Request to join " + siteNameC2831 + " site' task is expected to be displayed in 'Completed Tasks'.");
        LOG.info("STEP 6: Open 'Site Members' page for " + siteNameC2831 + ".");
        siteUsersPage.navigate(siteNameC2831);
        assertTrue(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should be displayed in the list of members for " + siteNameC2831);
        assertTrue(siteUsersPage.isRoleSelected("Consumer", user2FirstName + " " + user2LastName), user2 + " should have Consumer role.");
        LOG.info("STEP 7: Logout and login to Share as " + user2 + ".");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        LOG.info("STEP 8: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertTrue(mySitesDashlet.isSitePresent(siteNameC2831), siteNameC2831 + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail (id = "C3059")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void requestToJoinModeratedSiteFromSiteDashboardPage()
    {
        String dialogMessage = String.format(language.translate("requestToJoin.dialogMessage"), siteNameC3059);
        setupAuthenticatedSession(user2, password);
        LOG.info("STEP 1: Open 'Site Dashboard' page for " + siteNameC3059);
        siteDashboardPage.navigate(siteNameC3059);
        LOG.info("STEP 2: Click on 'Site configuration option' icon.");
        siteDashboardPage.clickSiteConfiguration();
        assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Request to Join"), "'Request to Join' action should be available in the 'Site Configuration Options' drop-down menu.");
        LOG.info("STEP 3: Click 'Request to Join' button.");
        try
        {
            siteDashboardPage.clickOptionInSiteConfigurationDropDown("Request to Join", requestSentDialog);
            assertEquals(requestSentDialog.getDialogTitle(), "Request Sent", "'Request Sent' pop-up is displayed.");
            assertEquals(requestSentDialog.getDialogMessage(), dialogMessage, "'Request Sent' pop-up has the expected message.");
            LOG.info("STEP 4: Click 'OK' button.");
            requestSentDialog.clickOKButton();
        } catch (TimeoutException | NoSuchElementException e)
        {
            getBrowser().refresh();
            siteDashboardPage.clickSiteConfiguration();
            assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Request to Join"), "'Request to Join' action should be available in the 'Site Configuration Options' drop-down menu.");
            assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Cancel Request"), "'Cancel Request' action should be available in the 'Site Configuration Options' drop-down menu.");
        }
        LOG.info("STEP 5: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertFalse(mySitesDashlet.isSitePresent(siteNameC3059), siteNameC3059 + " is not displayed on 'My Sites' dashlet.");
        LOG.info("STEP 6: Logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
        myTasksPage.navigateByMenuBar();
        assertTrue(myTasksPage.checkTaskWasFound(siteNameC3059), "'Request to join " + siteNameC3059 + " site' task is expected to be displayed in 'Active Tasks'.");
        LOG.info("STEP 7: Click on 'Request to join " + siteNameC3059 + " site' task.");
        myTasksPage.clickEditTask(siteNameC3059);
        LOG.info("STEP 8: Click on 'Approve' button.");
        editTaskPage.approve("Approve", myTasksPage);
        myTasksPage.clickCompletedTasks();
        assertTrue(myTasksPage.checkTaskWasFound(siteNameC3059), "'Request to join " + siteNameC3059 + " site' task is expected to be displayed in 'Completed Tasks'.");
        LOG.info("STEP 9: Open 'Site Members' page for " + siteNameC3059 + ".");
        siteUsersPage.navigate(siteNameC3059);
        assertTrue(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should be displayed in the list of members for " + siteNameC3059);
        assertTrue(siteUsersPage.isRoleSelected("Consumer", user2FirstName + " " + user2LastName), user2 + " should have Consumer role.");
        LOG.info("STEP 10: Logout and login to Share as " + user2 + ".");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        LOG.info("STEP 11: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertTrue(mySitesDashlet.isSitePresent(siteNameC3059), siteNameC3059 + " is displayed on 'My Sites' dashlet.");
        LOG.info("STEP 12: Open 'Site Dashboard' page for " + siteNameC3059 + " and click on 'Site configuration option' icon.");
        siteDashboardPage.navigate(siteNameC3059);
        siteDashboardPage.clickSiteConfiguration();
        assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");
    }

    @TestRail (id = "C2833")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelRequestToJoinModeratedSite()
    {
        setupAuthenticatedSession(user2, password);
        siteFinderPage.navigate();
        LOG.info("STEP 1: Enter site name (" + siteNameC2833 + ") in textbox and click on 'Search' button.");
        siteFinderPage.searchSiteWithRetry(siteNameC2833);
        assertTrue(siteFinderPage.checkSiteWasFound(siteNameC2833), "Site is expected to be found.");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2833, "Request to Join"), "'Request to Join' button is expected to be displayed for " + siteNameC2833);
        LOG.info("STEP 2: Click on 'Request to Join' button.");
        try
        {
            siteFinderPage.clickSiteButton(siteNameC2833, "Request to Join");
            assertEquals(notification.getDisplayedNotification(), "Successfully requested to join site " + siteNameC2833);
            assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2833, "Cancel Request"), "'Cancel Request' button appears in place of 'Request to Join' button for " + siteNameC2833);
        } catch (TimeoutException e)
        {
            getBrowser().refresh();
            siteFinderPage.searchSiteWithRetry(siteNameC2833);
            assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2833, "Cancel Request"), "'Cancel Request' button appears in place of 'Request to Join' button for " + siteNameC2833);
        }
        LOG.info("STEP 3: Logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
        myTasksPage.navigateByMenuBar();
        assertTrue(myTasksPage.checkTaskWasFound(siteNameC2833), "'Request to join " + siteNameC2833 + " site' task is expected to be displayed in 'Active Tasks'.");
        LOG.info("STEP 4: Logout and login to Share as " + user2 + ". Open again 'Site Finder' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        siteFinderPage.navigate();
        LOG.info("STEP 5: Search site name (" + siteNameC2833 + ") and click on 'Cancel Request' button. ");
        siteFinderPage.searchSiteWithRetry(siteNameC2833);
        siteFinderPage.clickSiteButton(siteNameC2833, "Cancel Request");
        assertEquals(notification.getDisplayedNotification(), "Successfully cancelled request to join site " + siteNameC2833 + "");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteNameC2833, "Request to Join"), "'Request to Join' button appears in place of 'Cancel Request' button for " + siteNameC2833);
        LOG.info("STEP 6: Logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
        myTasksPage.navigateByMenuBar();
        assertFalse(myTasksPage.checkTaskWasFound(siteNameC2833), "'Request to join " + siteNameC2833 + " site' task is no longer displayed.");
    }
}
