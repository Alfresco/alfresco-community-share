package org.alfresco.share.site.accessingExistingSites;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.accessingExistingSites.RequestSentDialog;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.user.UserDashboardPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

@Slf4j
public class JoiningSiteTests extends BaseTest
{
    //@Autowired
    SiteFinderPage siteFinderPage;

    //@Autowired
    UserDashboardPage userDashboardPage;

    //@Autowired
    SiteDashboardPage siteDashboardPage;

    //@Autowired
    SiteUsersPage siteUsersPage;

    MySitesDashlet mySitesDashlet;

    //@Autowired
    MyTasksPage myTasksPage;

    //@Autowired
    EditTaskPage editTaskPage;

    //@Autowired
    RequestSentDialog requestSentDialog;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2833 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2823 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC3053 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2831 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC3059 = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating two users");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating few Random Sites");
        siteNameC2833.set(getDataSite().usingUser(user1.get()).createModeratedRandomSite());
        getCmisApi().authenticateUser(user1.get());

        siteNameC2823.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        siteNameC3053.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        siteNameC2831.set(getDataSite().usingUser(user1.get()).createModeratedRandomSite());
        getCmisApi().authenticateUser(user1.get());

        siteNameC3059.set(getDataSite().usingUser(user1.get()).createModeratedRandomSite());
        getCmisApi().authenticateUser(user1.get());

        siteFinderPage = new SiteFinderPage(webDriver);
        siteUsersPage = new SiteUsersPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        mySitesDashlet = new MySitesDashlet(webDriver);
        myTasksPage = new MyTasksPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);
        requestSentDialog = new RequestSentDialog(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user1.get());
        deleteUsersIfNotNull(user2.get());
        deleteSitesIfNotNull(siteNameC2833.get());
        deleteSitesIfNotNull(siteNameC2823.get());
        deleteSitesIfNotNull(siteNameC3053.get());
        deleteSitesIfNotNull(siteNameC2831.get());
        deleteSitesIfNotNull(siteNameC3059.get());
        deleteAllCookiesIfNotNull();
    }

    @TestRail (id = "C2823")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void joinPublicSiteFromSiteFinderPage() {

        String user2FirstName = user2.get().getFirstName();
        String user2LastName = user2.get().getLastName();

        authenticateUsingCookies(user2.get());
        siteFinderPage.navigate();

        log.info("STEP 1: Enter site name (" + siteNameC2823 + ") in textbox and click on 'Search' button.");
        siteFinderPage
            .searchSiteWithName(siteNameC2823.get().getId())
            .assertSiteWasFound(siteNameC2823.get().getId())
            .assertIsButtonDisplayedForSite(siteNameC2823.get().getId(), "Join");

        log.info("STEP 2: Click on " + siteNameC2823 + "'s name link. Click on 'Site Members' link.");
        siteUsersPage
            .navigate(siteNameC2823.get().getId())
            .assertSiteMemberNameNotDisplayed(user2FirstName + " " + user2LastName); // user2 + " should not be displayed in the list of members for " + siteNameC2823);

        log.info("STEP 3: Return to 'Site Finder' page and click 'Join' button for " + siteNameC2823);
        siteFinderPage
            .navigate()
            .searchSiteWithName(siteNameC2823.get().getId())
            .clickSiteButton(siteNameC2823.get().getId(), "Join");
        siteFinderPage
            .assertIsButtonDisplayedForSite(siteNameC2823.get().getId(),"Leave");

        log.info("STEP 4: Click on " + siteNameC2823 + "'s name link. Click on 'Site Members' link.");
        siteUsersPage
            .navigate(siteNameC2823.get().getId())
            .assertSiteMemberNameEqualsTo(user2FirstName + " " + user2LastName) // user2 + " should be displayed in the list of members for " + siteNameC2823
            .assertSelectedRoleEqualsTo("Consumer", user2FirstName + " " + user2LastName); //user2 + " should have Consumer role."

        log.info("STEP 5: Go to 'User Dashboard' page and verify 'My Sites' dashlet.");
        userDashboardPage.navigate(user2.get());
        assertTrue(mySitesDashlet.isSitePresent(siteNameC2823.get().getId()), siteNameC2823 + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail (id = "C3053")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void joinPublicSiteFromSiteDashboardPage() {

        String user2FirstName = user2.get().getFirstName();
        String user2LastName = user2.get().getLastName();

        authenticateUsingCookies(user2.get());

        log.info("STEP 1: Open " + siteNameC3053 + " dashboard  and click 'Site configuration options' -> 'Join Site'.");
        siteDashboardPage
            .navigate(siteNameC3053.get().getId())
            .openSiteConfiguration()
            .selectOptionFromSiteConfigurationDropDown("Join Site")
            .waitUntilNotificationMessageDisappears();

        log.info("STEP 2: Click on 'Site Members' link.");
        siteUsersPage
            .navigate(siteNameC3053.get().getId())
            .assertSiteMemberNameEqualsTo(user2FirstName + " " + user2LastName) //user2 + " should be displayed in the list of members for " + siteNameC3053
            .assertSelectedRoleEqualsTo("Consumer", user2FirstName + " " + user2LastName); // user2 + " should have Consumer role."

        log.info("STEP 3: Click again on 'Site Configuration Options' icon.");
        siteDashboardPage
            .navigate(siteNameC3053.get().getId())
            .openSiteConfiguration();
        assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"),
           "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Join Site"),
            "'Join Site' action should be available in the 'Site Configuration Options' drop-down menu.");

        log.info("STEP 4: Go to 'User Dashboard' page and verify 'My Sites' dashlet.");
        userDashboardPage
            .navigate(user2.get());
        assertTrue(mySitesDashlet.isSitePresent(siteNameC3053.get().getId()), siteNameC3053 + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail (id = "C2831")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void requestToJoinModeratedSiteFromSiteFinderPage() {
        String user2FirstName = user2.get().getFirstName();
        String user2LastName = user2.get().getLastName();

        authenticateUsingCookies(user2.get());
        siteFinderPage.navigate();

        log.info("STEP 1: Enter site name (" + siteNameC2831 + ") in textbox and click on 'Search' button.");
        siteFinderPage
            .searchSiteWithName(siteNameC2831.get().getId())
            .assertSiteWasFound(siteNameC2831.get().getId())
            .assertIsButtonDisplayedForSite(siteNameC2831.get().getId(), "Request to Join"); //"'Request to Join' button is expected to be displayed for " + siteNameC2831

        log.info("STEP 2: Click on 'Request to Join' button.");
        siteFinderPage
            .clickSiteButton(siteNameC2831.get().getId(), "Request to Join");
        siteFinderPage
            .assertVerifyDisplayedNotification("Successfully requested to join site " + siteNameC2831.get().getId())
            .assertIsButtonDisplayedForSite(siteNameC2831.get().getId(), "Cancel Request"); //"'Cancel Request' button appears in place of 'Request to Join' button for " + siteNameC2831)

        log.info("STEP 3: logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        authenticateUsingCookies(user1.get());
        myTasksPage
            .navigate()
            .assertTaskNameEqualsTo(siteNameC2831.get().getId());

        log.info("STEP 4: Click on 'Request to join " + siteNameC2831 + " site' task.");
        myTasksPage
            .editTask(siteNameC2831.get().getId());

        log.info("STEP 5: Click on 'Approve' button.");
        editTaskPage
            .approveTask();
        myTasksPage
            .navigateToCompletedTasks()
            .assertTaskNameEqualsTo(siteNameC2831.get().getId()); // "'Request to join " + siteNameC2831 + " site' task is expected to be displayed in 'Active Tasks'"

        log.info("STEP 6: Open 'Site Members' page for " + siteNameC2831 + ".");
        siteUsersPage
            .navigate(siteNameC2831.get().getId())
            .assertSiteMemberNameEqualsTo(user2FirstName + " " + user2LastName)
            .assertSelectedRoleEqualsTo("Consumer", user2FirstName + " " + user2LastName);

        log.info("STEP 7: logout and login to Share as " + user2 + ".");
        authenticateUsingCookies(user2.get());

        log.info("STEP 8: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertTrue(mySitesDashlet.isSitePresent(siteNameC2831.get().getId()), siteNameC2831 + " is displayed on 'My Sites' dashlet.");
    }

    @TestRail (id = "C3059")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, })
    public void requestToJoinModeratedSiteFromSiteDashboardPage()
    {
        String user2FirstName = user2.get().getFirstName();
        String user2LastName = user2.get().getLastName();

        String dialogMessage = String.format(language.translate("requestToJoin.dialogMessage"), siteNameC3059.get().getId());
        authenticateUsingCookies(user2.get());

        log.info("STEP 1: Open 'Site Dashboard' page for " + siteNameC3059);
        siteDashboardPage.navigate(siteNameC3059.get().getId());

        log.info("STEP 2: Click on 'Site configuration option' icon.");
        siteDashboardPage.openSiteConfiguration();
        assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Request to Join"), "'Request to Join' action should be available in the 'Site Configuration Options' drop-down menu.");

        log.info("STEP 3: Click 'Request to Join' button.");
        siteDashboardPage.selectOptionFromSiteConfigurationDropDown("Request to Join");
        assertEquals(requestSentDialog.getDialogTitle(), "Request Sent", "'Request Sent' pop-up is displayed.");
        assertEquals(requestSentDialog.getDialogMessage(), dialogMessage, "'Request Sent' pop-up has the expected message.");

        log.info("STEP 4: Click 'OK' button.");
        requestSentDialog.clickOKButton();

        log.info("STEP 5: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertFalse(mySitesDashlet.isSitePresent(siteNameC3059.get().getId()), siteNameC3059 + " is not displayed on 'My Sites' dashlet.");

        log.info("STEP 6: logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        authenticateUsingCookies(user1.get());
        myTasksPage.navigateByMenuBar();
        myTasksPage.assertTaskNameEqualsTo(siteNameC3059.get().getId()); // "'Request to join " + siteNameC3059 + " site' task is expected to be displayed in 'Active Tasks'"

        log.info("STEP 7: Click on 'Request to join " + siteNameC3059 + " site' task.");
        myTasksPage.editTask(siteNameC3059.get().getId());

        log.info("STEP 8: Click on 'Approve' button.");
        editTaskPage.approveTask();
        myTasksPage.navigateToCompletedTasks();
        myTasksPage.assertTaskNameEqualsTo(siteNameC3059.get().getId());  //"'Request to join " + siteNameC3059 + " site' task is expected to be displayed in 'Completed Tasks'."

        log.info("STEP 9: Open 'Site Members' page for " + siteNameC3059 + ".");
        siteUsersPage.navigate(siteNameC3059.get().getId());
        siteUsersPage.assertSiteMemberNameEqualsTo(user2FirstName + " " + user2LastName); // user2 + " should be displayed in the list of members for " + siteNameC3059
        siteUsersPage.assertSelectedRoleEqualsTo("Consumer", user2FirstName + " " + user2LastName); // user2 + " should have Consumer role.

        log.info("STEP 10: logout and login to Share as " + user2 + ".");
        authenticateUsingCookies(user2.get());
        log.info("STEP 11: Verify 'My Sites' dashlet from 'User Dashboard' page.");
        assertTrue(mySitesDashlet.isSitePresent(siteNameC3059.get().getId()), siteNameC3059 + " is displayed on 'My Sites' dashlet.");

        log.info("STEP 12: Open 'Site Dashboard' page for " + siteNameC3059 + " and click on 'Site configuration option' icon.");
        siteDashboardPage.navigate(siteNameC3059.get().getId());
        siteDashboardPage.openSiteConfiguration();
        assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");
    }

    @TestRail (id = "C2833")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelRequestToJoinModeratedSite()
    {
        authenticateUsingCookies(user2.get());
        siteFinderPage.navigate();

        log.info("STEP 1: Enter site name (" + siteNameC2833 + ") in textbox and click on 'Search' button.");
        siteFinderPage
            .searchSiteWithName(siteNameC2833.get().getId())
            .assertSiteWasFound(siteNameC2833.get().getId())
            .assertIsButtonDisplayedForSite(siteNameC2833.get().getId(), "Request to Join"); //"'Request to Join' button is expected to be displayed for " + siteNameC2833

        log.info("STEP 2: Click on 'Request to Join' button.");
        siteFinderPage
            .clickSiteButton(siteNameC2833.get().getId(), "Request to Join");

        log.info("STEP 3: logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        authenticateUsingCookies(user1.get());
        myTasksPage
            .navigateByMenuBar()
            .assertTaskNameEqualsTo(siteNameC2833.get().getId()); //"'Request to join " + siteNameC2833 + " site' task is expected to be displayed in 'Active Tasks'."

        log.info("STEP 4: logout and login to Share as " + user2 + ". Open again 'Site Finder' page.");
        authenticateUsingCookies(user2.get());
        siteFinderPage.navigate();

        log.info("STEP 5: Search site name (" + siteNameC2833 + ") and click on 'Cancel Request' button. ");
        siteFinderPage
            .searchSiteWithName(siteNameC2833.get().getId())
            .clickSiteButton(siteNameC2833.get().getId(), "Cancel Request");
        siteFinderPage
            .assertVerifyDisplayedNotification("Successfully cancelled request to join site " + siteNameC2833.get().getId() + "")
            .isButtonDisplayedForSite(siteNameC2833.get().getId(), "Request to Join"); // "'Request to Join' button appears in place of 'Cancel Request' button for " + siteNameC2833

        log.info("STEP 6: logout and login to Share as " + user1 + ". Open 'My Tasks' page.");
        authenticateUsingCookies(user1.get());
        myTasksPage.navigateByMenuBar().assertNoTaskIsDisplayed();
    }
}
