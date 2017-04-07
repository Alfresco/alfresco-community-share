package org.alfresco.share.site.accessingExistingSites;

import org.alfresco.common.DataUtil;
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
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

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

    private String user1 = String.format("testUser1%s", DataUtil.getUniqueIdentifier());
    private String user2 = String.format("testUser2%s", DataUtil.getUniqueIdentifier());
    private String user2FirstName = "user2_firstName";
    private String user2LastName = "user2_lastName";
    private String siteName;
    private String description = String.format("description%s", DataUtil.getUniqueIdentifier());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2FirstName, user2LastName);
    }

    @TestRail(id = "C2823")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void joinPublicSiteFromSiteFinderPage()
    {
        //precondition
        siteName = String.format("SiteName-C2823-%s", DataUtil.getUniqueIdentifier());
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(user2, password);
        siteFinderPage.navigate();

        LOG.info("STEP 1: Enter site name (" + siteName + ") in textbox and click on 'Search' button.");
        siteFinderPage.searchSite(siteName);
        assertTrue(siteFinderPage.checkSiteWasFound(siteName), "Site is expected to be found.");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Join"), "'Join' button is expected to be displayed for " + siteName);

        LOG.info("STEP 2: Click on " + siteName + "'s name link. Click on 'Site Members' link.");
        siteUsersPage.navigate(siteName);
        assertFalse(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should not be displayed in the list of members for " + siteName);

        LOG.info("STEP 3: Return to 'Site Finder' page and click 'Join' button for " + siteName);
        siteFinderPage.navigate();
        siteFinderPage.searchSite(siteName);
        siteFinderPage.clickSiteButton(siteName, "Join");
        assertEquals(notification.getDisplayedNotification(), "Successfully added user " + user2 + " to site " + siteName);
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Leave"), "'Leave' button appears in place of 'Join' button for " + siteName);

        LOG.info("STEP 4: Click on " + siteName + "'s name link. Click on 'Site Members' link.");
        siteUsersPage.navigate(siteName);
        assertTrue(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should be displayed in the list of members for " + siteName);
        assertTrue(siteUsersPage.isRoleSelected("Consumer", user2FirstName + " " + user2LastName), user2 + " should have Consumer role.");

        LOG.info("STEP 5: Go to 'User Dashboard' page and verify 'My Sites' dashlet.");
        userDashboardPage.navigate(user2);
        assertTrue(mySitesDashlet.isSitePresent(siteName), siteName + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail(id = "C3053")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void joinPublicSiteFromSiteDashboardPage()
    {
        //precondition
        siteName = String.format("SiteName-C3053-%s", DataUtil.getUniqueIdentifier());
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP 1: Open " + siteName + " dashboard  and click 'Site configuration options' -> 'Join Site'.");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickSiteConfiguration();
        siteDashboardPage.clickOptionInSiteConfigurationDropDown("Join Site", siteDashboardPage);
        
        LOG.info("STEP 2: Click on 'Site Members' link.");
        siteUsersPage.navigate(siteName);
        assertTrue(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should be displayed in the list of members for " + siteName);
        assertTrue(siteUsersPage.isRoleSelected("Consumer", user2FirstName + " " + user2LastName), user2 + " should have Consumer role.");

        LOG.info("STEP 3: Click again on 'Site Configuration Options' icon.");
        siteDashboardPage.clickSiteConfiguration();
        getBrowser().waitInSeconds(2);
        assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Join Site"), "'Join Site' action should be available in the 'Site Configuration Options' drop-down menu.");

        LOG.info("STEP 4: Go to 'User Dashboard' page and verify 'My Sites' dashlet.");
        userDashboardPage.navigate(user2);
        assertTrue(mySitesDashlet.isSitePresent(siteName), siteName + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail(id = "C2831")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void requestToJoinModeratedSiteFromSiteFinderPage()
    {
        //precondition
        siteName = String.format("SiteName-C2831-%s", DataUtil.getUniqueIdentifier());
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.MODERATED);
        setupAuthenticatedSession(user2, password);
        siteFinderPage.navigate();

        LOG.info("STEP 1: Enter site name (" + siteName + ") in textbox and click on 'Search' button.");
        siteFinderPage.searchSite(siteName);
        assertTrue(siteFinderPage.checkSiteWasFound(siteName), "Site is expected to be found.");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed for " + siteName);

        LOG.info("STEP 2: Click on 'Request to Join' button.");
        try
        {
            siteFinderPage.clickSiteButton(siteName, "Request to Join");
            assertEquals(notification.getDisplayedNotification(), "Successfully requested to join site " + siteName);
            assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button appears in place of 'Request to Join' button for " + siteName);
        }
        catch (TimeoutException e)
        {
            getBrowser().refresh();
            siteFinderPage.searchSite(siteName);
            assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button appears in place of 'Request to Join' button for " + siteName);
        }

        LOG.info("STEP 3: Logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
        myTasksPage.navigate();
        assertTrue(myTasksPage.checkTaskWasFound(siteName), "'Request to join " + siteName + " site' task is expected to be displayed in 'Active Tasks'.");

        LOG.info("STEP 4: Click on 'Request to join " + siteName + " site' task.");
        myTasksPage.clickEditTask(siteName);
        
        LOG.info("STEP 5: Click on 'Approve' button.");
        editTaskPage.approve("Approve", myTasksPage);
        myTasksPage.clickCompletedTasks();
        assertTrue(myTasksPage.checkTaskWasFound(siteName), "'Request to join " + siteName + " site' task is expected to be displayed in 'Completed Tasks'.");

        LOG.info("STEP 6: Open 'Site Members' page for " + siteName + ".");
        siteUsersPage.navigate(siteName);
        assertTrue(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should be displayed in the list of members for " + siteName);
        assertTrue(siteUsersPage.isRoleSelected("Consumer", user2FirstName + " " + user2LastName), user2 + " should have Consumer role.");

        LOG.info("STEP 7: Logout and login to Share as " + user2 + ".");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP 8: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertTrue(mySitesDashlet.isSitePresent(siteName), siteName + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail(id = "C3059")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void requestToJoinModeratedSiteFromSiteDashboardPage()
    {
        //precondition
        siteName = String.format("SiteName-C3059-%s", DataUtil.getUniqueIdentifier());
        String dialogMessage = String.format(language.translate("requestToJoin.dialogMessage"), siteName);
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.MODERATED);
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP 1: Open 'Site Dashboard' page for " + siteName);
        siteDashboardPage.navigate(siteName);

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
        }
        catch (TimeoutException | NoSuchElementException e)
        {
            getBrowser().refresh();
            siteDashboardPage.clickSiteConfiguration();
            assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Request to Join"), "'Request to Join' action should be available in the 'Site Configuration Options' drop-down menu.");
            assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Cancel Request"), "'Cancel Request' action should be available in the 'Site Configuration Options' drop-down menu.");
        }

        LOG.info("STEP 5: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertFalse(mySitesDashlet.isSitePresent(siteName), siteName + " is not displayed on 'My Sites' dashlet.");

        LOG.info("STEP 6: Logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
        myTasksPage.navigateByMenuBar();
        assertTrue(myTasksPage.checkTaskWasFound(siteName), "'Request to join " + siteName + " site' task is expected to be displayed in 'Active Tasks'.");

        LOG.info("STEP 7: Click on 'Request to join " + siteName + " site' task.");
        myTasksPage.clickEditTask(siteName);

        LOG.info("STEP 8: Click on 'Approve' button.");
        editTaskPage.approve("Approve", myTasksPage);
        myTasksPage.clickCompletedTasks();
        assertTrue(myTasksPage.checkTaskWasFound(siteName), "'Request to join " + siteName + " site' task is expected to be displayed in 'Completed Tasks'.");

        LOG.info("STEP 9: Open 'Site Members' page for " + siteName + ".");
        siteUsersPage.navigate(siteName);
        assertTrue(siteUsersPage.isASiteMember(user2FirstName + " " + user2LastName), user2 + " should be displayed in the list of members for " + siteName);
        assertTrue(siteUsersPage.isRoleSelected("Consumer", user2FirstName + " " + user2LastName), user2 + " should have Consumer role.");

        LOG.info("STEP 10: Logout and login to Share as " + user2 + ".");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP 11: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertTrue(mySitesDashlet.isSitePresent(siteName), siteName + " is displayed on 'My Sites' dashlet.");

        LOG.info("STEP 12: Open 'Site Dashboard' page for " + siteName + " and click on 'Site configuration option' icon.");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickSiteConfiguration();
        assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");
    }

    @TestRail(id = "C2833")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelRequestToJoinModeratedSite()
    {
        //precondition
        siteName = String.format("SiteName-C2833-%s", DataUtil.getUniqueIdentifier());
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.MODERATED);
        setupAuthenticatedSession(user2, password);
        siteFinderPage.navigate();

        LOG.info("STEP 1: Enter site name (" + siteName + ") in textbox and click on 'Search' button.");
        siteFinderPage.searchSite(siteName);
        assertTrue(siteFinderPage.checkSiteWasFound(siteName), "Site is expected to be found.");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button is expected to be displayed for " + siteName);

        LOG.info("STEP 2: Click on 'Request to Join' button.");
        try
        {
            siteFinderPage.clickSiteButton(siteName, "Request to Join");
            assertEquals(notification.getDisplayedNotification(), "Successfully requested to join site " + siteName);
            assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button appears in place of 'Request to Join' button for " + siteName);
        }
        catch (TimeoutException e)
        {
            getBrowser().refresh();
            siteFinderPage.searchSite(siteName);
            assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Cancel Request"), "'Cancel Request' button appears in place of 'Request to Join' button for " + siteName);
        }

        LOG.info("STEP 3: Logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
        myTasksPage.navigateByMenuBar();
        assertTrue(myTasksPage.checkTaskWasFound(siteName), "'Request to join " + siteName + " site' task is expected to be displayed in 'Active Tasks'.");

        LOG.info("STEP 4: Logout and login to Share as " + user2 + ". Open again 'Site Finder' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        siteFinderPage.navigate();

        LOG.info("STEP 5: Search site name (" + siteName + ") and click on 'Cancel Request' button. ");
        siteFinderPage.searchSite(siteName);
        siteFinderPage.clickSiteButton(siteName, "Cancel Request");
        assertEquals(notification.getDisplayedNotification(), "Successfully cancelled request to join site " + siteName + "");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Request to Join"), "'Request to Join' button appears in place of 'Cancel Request' button for " + siteName);

        LOG.info("STEP 6: Logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
        myTasksPage.navigateByMenuBar();
        assertFalse(myTasksPage.checkTaskWasFound(siteName), "'Request to join " + siteName + " site' task is no longer displayed.");
    }
}
