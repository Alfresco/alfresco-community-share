package org.alfresco.share.site.accessingExistingSites;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.accessingExistingSites.LeaveSiteDialog;
import org.alfresco.po.share.user.UserDashboardPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.constants.UserRole;;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 7/5/2016.
 */
@Slf4j
public class LeavingSiteTests extends BaseTest
{
    //@Autowired
    SiteDashboardPage siteDashboard;

    //@Autowired
    LeaveSiteDialog leaveSiteDialog;

    //@Autowired
    UserDashboardPage userDashboardPage;

    //@Autowired
    MySitesDashlet mySitesDashlet;

    //@Autowired
    SiteFinderPage siteFinderPage;
    private String dialogTitle;
    private String dialogMessage;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating two users");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        siteDashboard = new SiteDashboardPage(webDriver);
        leaveSiteDialog = new LeaveSiteDialog(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        mySitesDashlet = new MySitesDashlet(webDriver);
        siteFinderPage = new SiteFinderPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user1.get());
        deleteUsersIfNotNull(user2.get());
    }

    @TestRail (id = "C2926")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyLeaveSitePopup()
    {
        log.info("PreCondition2: Any site is created by "+user1);
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        log.info("PreCondition3: User1 is logged into Share");
        authenticateUsingLoginPage(user1.get());

        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName.get().getId());
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName.get().getId());

        log.info("STEP 1: Navigate to Site Dashboard for'" + siteName + "'");
        siteDashboard.navigate(siteName.get());
        log.info("STEP 2: Click on 'Site configuration options' icon -> Leave SiteService.");
        siteDashboard.openSiteConfiguration();
        siteDashboard.selectOptionFromSiteConfigurationDropDown("Leave Site");
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount)
        {
            try
            {

                assertEquals(leaveSiteDialog.getDialogTitle(), dialogTitle, "Dialog title is not as expected.");
                assertEquals(leaveSiteDialog.getDialogMessage(), dialogMessage, "Dialog message is not as expected.");
                assertTrue(leaveSiteDialog.isOkButtonDisplayed(), "Ok button should be displayed.");
                assertTrue(leaveSiteDialog.isCancelButtonDisplayed(), "Cancel button should be displayed.");
                assertTrue(leaveSiteDialog.isCloseButtonDisplayed(), "Close button should be displayed.");
                break;
            } catch (TimeoutException | NoSuchElementException e)
            {
                counter++;
                siteDashboard.openSiteConfiguration();
                siteDashboard.selectOptionFromSiteConfigurationDropDown("Leave Site");
            }

        }
        deleteSitesIfNotNull(siteName.get());
    }

    @TestRail (id = "C2927")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelLeavingSiteUsingSiteConfigurationOptions()
    {
        log.info("PreCondition2: Any site is created by "+user1);
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        log.info("PreCondition3: User2 is added to site with 'Manager' role");
        dataUser.usingUser(user1.get()).addUserToSite(user2.get(),siteName.get(), UserRole.SiteManager);

        log.info("PreCondition4: User1 is logged into Share");
        authenticateUsingLoginPage(user1.get());

        log.info("PreCondition5: "+siteName+" dashboard page is opened");
        siteDashboard.navigate(siteName.get());

        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName.get().getId());
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName.get().getId());

        log.info("STEP 1: Click on 'Site configuration options' icon -> Leave SiteService.");
        siteDashboard.openSiteConfiguration();
        siteDashboard.selectOptionFromSiteConfigurationDropDown("Leave Site");
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount)
        {
            try
            {

                assertEquals(leaveSiteDialog.getDialogTitle(), dialogTitle, "Dialog title is not as expected.");
                assertEquals(leaveSiteDialog.getDialogMessage(), dialogMessage, "Dialog message is not as expected.");
                log.info("STEP 2: Click 'Cancel' button.");
                leaveSiteDialog.clickCancelButton();
                assertFalse(leaveSiteDialog.isLeaveSiteDialogDisplayed(), "Popup should be closed.");
//                assertTrue(siteDashboard.getCurrentUrl().endsWith(siteName + "/dashboard"), "User should remain on " + siteName + "'s dashboard page.");
                log.info("STEP 3: Go to \"User's Dashboard\" page and verify \"My Sites\" dashlet.");
                userDashboardPage.navigateByMenuBar();
                assertTrue(mySitesDashlet.isSitePresent(siteName.get().getId()), siteName + " is displayed in the list of sites.");
                break;
            } catch (TimeoutException | NoSuchElementException e)
            {
                counter++;
                siteDashboard.openSiteConfiguration();
                siteDashboard.selectOptionFromSiteConfigurationDropDown("Leave Site");
            }
        }
        deleteSitesIfNotNull(siteName.get());
    }

    @TestRail (id = "C2928")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void leaveSiteUsingSiteFinder()
    {
        log.info("PreCondition2: Any site is created by "+user1);
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        log.info("PreCondition3: User2 is added to site with 'Manager' role");
        dataUser.usingUser(user1.get()).addUserToSite(user2.get(),siteName.get(), UserRole.SiteManager);

        log.info("PreCondition4: User1 is logged into Share");
        authenticateUsingLoginPage(user1.get());

        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName.get().getId());
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName.get().getId());

        log.info("STEP 1: Open 'Site Finder' page and search for '" + siteName + "'.");
        siteFinderPage.navigate();
        siteFinderPage.searchSiteWithName(siteName.get().getId());
        assertTrue(siteFinderPage.checkSiteWasFound(siteName.get().getId()), siteName + " is expected to be found.");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName.get().getId(), "Leave"), "'Leave' button is available for the site.");
        log.info("STEP 2: Click on 'Leave' button.");
        siteFinderPage.clickSiteButton(siteName.get().getId(), "Leave");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName.get().getId(), "Join"), "'Join' button is available for the site.");
        log.info("STEP 3: Go to \"User's Dashboard\" page and verify \"My Sites\" dashlet.");
        userDashboardPage.navigateByMenuBar();
        assertFalse(mySitesDashlet.isSitePresent(siteName.get().getId()), siteName.get() + " should no longer be listed in the list of sites.");

        deleteSitesIfNotNull(siteName.get());

    }

    @TestRail (id = "C2930")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void leaveSiteUsingSiteConfigurationOptions()
    {
        log.info("PreCondition1: Any site is created by "+user1);
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        log.info("PreCondition2: User2 is added to site with 'Manager' role");
        dataUser.usingUser(user1.get()).addUserToSite(user2.get(),siteName.get(), UserRole.SiteManager);

        log.info("PreCondition3: User1 is logged into Share");
        authenticateUsingLoginPage(user1.get());

        log.info("PreCondition4: "+siteName+" dashboard page is opened");
        siteDashboard.navigate(siteName.get().getId());

        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName.get().getId());
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName.get().getId());

        log.info("STEP 1: Click on 'Site configuration options' icon -> Leave SiteService.");
        siteDashboard.openSiteConfiguration();
        siteDashboard.selectOptionFromSiteConfigurationDropDown("Leave Site");


        assertEquals(leaveSiteDialog.getDialogTitle(), dialogTitle, "Dialog title is not as expected.");
        assertEquals(leaveSiteDialog.getDialogMessage(), dialogMessage, "Dialog message is not as expected.");
        log.info("STEP 2: Click 'OK' button.");
        leaveSiteDialog.clickOKButton();

        userDashboardPage.assertUserDashboardPageIsOpened();
        log.info("STEP 3: Verify sites listed on 'My Sites' dashlet.");
        assertFalse(mySitesDashlet.isSitePresent(siteName.get().getId()), siteName + " should no longer be listed in the list of sites.");

        deleteSitesIfNotNull(siteName.get());
    }

    @TestRail (id = "C2931")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void leaveSiteWithoutConfirmation()
    {
        log.info("PreCondition2: Any site is created by "+user1);
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        log.info("PreCondition3: User1 is logged into Share");
        authenticateUsingLoginPage(user1.get());

        log.info("PreCondition4: "+siteName+" dashboard page is opened");
        siteDashboard.navigate(siteName.get());

        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName.get().getId());
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName.get().getId());

        log.info("STEP 1: Click on 'Site configuration options' icon -> Leave SiteService.");
        siteDashboard
            .openSiteConfiguration()
            .selectOptionFromSiteConfigurationDropDown("Leave Site");
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount)
        {
            try
            {
                assertEquals(leaveSiteDialog.getDialogTitle(), dialogTitle, "Dialog title is not as expected.");
                assertEquals(leaveSiteDialog.getDialogMessage(), dialogMessage, "Dialog message is not as expected.");
                log.info("STEP 2: Click Close (x) button.");
                leaveSiteDialog.clickCloseButton();
                assertFalse(leaveSiteDialog.isLeaveSiteDialogDisplayed(), "Popup should be closed");
                assertTrue(siteDashboard.getCurrentUrl().endsWith(siteName.get().getId() + "/dashboard"), "User should remain on " + siteName.get() + "'s dashboard page.");
                log.info("STEP 3: Go to \"User's Dashboard\" page and verify \"My Sites\" dashlet.");
                userDashboardPage.navigateByMenuBar();
                assertTrue(mySitesDashlet.isSitePresent(siteName.get().getId()), siteName.get().getId() + " is displayed in the list of sites.");
                break;
            } catch (TimeoutException | NoSuchElementException e)
            {
                counter++;
                siteDashboard
                    .openSiteConfiguration()
                    .selectOptionFromSiteConfigurationDropDown("Leave Site");
            }
        }
        deleteSitesIfNotNull(siteName.get());
    }
}
