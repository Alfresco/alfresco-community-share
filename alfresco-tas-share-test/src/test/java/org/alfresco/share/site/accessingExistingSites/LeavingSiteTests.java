package org.alfresco.share.site.accessingExistingSites;

import org.alfresco.po.share.Notification;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.accessingExistingSites.LeaveSiteDialog;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 7/5/2016.
 */
public class LeavingSiteTests extends ContextAwareWebTest {
    @Autowired SiteDashboardPage siteDashboard;

    @Autowired LeaveSiteDialog leaveSiteDialog;

    @Autowired UserDashboardPage userDashboardPage;

    @Autowired MySitesDashlet mySitesDashlet;

    @Autowired SiteFinderPage siteFinderPage;

    @Autowired Notification notification;

    private String user1 = String.format("testUser1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("testUser2%s", RandomData.getRandomAlphanumeric());
    private String firstName = String.format("fName%s", RandomData.getRandomAlphanumeric());
    private String lastName = String.format("lName%s", RandomData.getRandomAlphanumeric());
    private String siteName;
    private String description = String.format("description%s", RandomData.getRandomAlphanumeric());
    private String dialogTitle;
    private String dialogMessage;

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, firstName, lastName);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
    }

    @TestRail(id = "C2926")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyLeaveSitePopup() {
        siteName = String.format("SiteName-C2926-%s", RandomData.getRandomAlphanumeric());
        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName);
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName);
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);
        LOG.info("STEP 1: Navigate to Site Dashboard for'" + siteName +"'");
        siteDashboard.navigate(siteName);
        LOG.info("STEP 2: Click on 'Site configuration options' icon -> Leave Site.");
        siteDashboard.clickSiteConfiguration();
        siteDashboard.clickOptionInSiteConfigurationDropDown("Leave Site", leaveSiteDialog);
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount) {
            try {
                assertEquals(leaveSiteDialog.getDialogTitle(), dialogTitle, "Dialog title is not as expected.");
                assertEquals(leaveSiteDialog.getDialogMessage(), dialogMessage, "Dialog message is not as expected.");
                assertTrue(leaveSiteDialog.isOkButtonDisplayed(), "Ok button should be displayed.");
                assertTrue(leaveSiteDialog.isCancelButtonDisplayed(), "Cancel button should be displayed.");
                assertTrue(leaveSiteDialog.isCloseButtonDisplayed(), "Close button should be displayed.");
                break;
            }
            catch (TimeoutException | NoSuchElementException e) {
                counter++;
                getBrowser().refresh();
                siteDashboard.clickSiteConfiguration();
                siteDashboard.clickOptionInSiteConfigurationDropDown("Leave Site", leaveSiteDialog);
            }
        }
    }

    @TestRail(id = "C2927")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelLeavingSiteUsingSiteConfigurationOptions() {
        siteName = String.format("SiteName-C2927-%s", RandomData.getRandomAlphanumeric());
        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName);
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName);
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteManager");
        setupAuthenticatedSession(user1, password);
        siteDashboard.navigate(siteName);
        LOG.info("STEP 1: Click on 'Site configuration options' icon -> Leave Site.");
        siteDashboard.clickSiteConfiguration();
        siteDashboard.clickOptionInSiteConfigurationDropDown("Leave Site", leaveSiteDialog);
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount) {
            try {
                assertEquals(leaveSiteDialog.getDialogTitle(), dialogTitle, "Dialog title is not as expected.");
                assertEquals(leaveSiteDialog.getDialogMessage(), dialogMessage, "Dialog message is not as expected.");
                LOG.info("STEP 2: Click 'Cancel' button.");
                leaveSiteDialog.clickCancelButton();
                assertTrue(leaveSiteDialog.isPopupHidden(), "Popup should be closed.");
                assertTrue(siteDashboard.getCurrentUrl().endsWith(siteName+"/dashboard"), "User should remain on " + siteName + "'s dashboard page.");
                LOG.info("STEP 3: Go to \"User's Dashboard\" page and verify \"My Sites\" dashlet.");
                userDashboardPage.navigateByMenuBar();
                assertTrue(mySitesDashlet.isSitePresent(siteName), siteName + " is displayed in the list of sites.");
                break;
            }
            catch (TimeoutException | NoSuchElementException e) {
                counter++;
                getBrowser().refresh();
                siteDashboard.clickSiteConfiguration();
                siteDashboard.clickOptionInSiteConfigurationDropDown("Leave Site", leaveSiteDialog);
            }
        }
    }

    @TestRail(id = "C2928")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void leaveSiteUsingSiteFinder() {
        siteName = String.format("SiteName-C2928-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteManager");
        setupAuthenticatedSession(user1, password);
        LOG.info("STEP 1: Open 'Site Finder' page and search for '"+siteName+"'.");
        siteFinderPage.navigateByMenuBar();
        siteFinderPage.searchSite(siteName);
        assertTrue(siteFinderPage.checkSiteWasFound(siteName), siteName + " is expected to be found.");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Leave"), "'Leave' button is available for the site.");
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount) {
            try {
                LOG.info("STEP 2: Click on 'Leave' button.");
                siteFinderPage.clickSiteButton(siteName, "Leave");
                assertEquals(notification.getDisplayedNotification(), "Successfully removed user " + user1 + " from site " + siteName, "Popup should be displayed");
                assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Join"), "'Join' button is available for the site.");
                LOG.info("STEP 3: Go to \"User's Dashboard\" page and verify \"My Sites\" dashlet.");
                userDashboardPage.navigateByMenuBar();
                assertFalse(mySitesDashlet.isSitePresent(siteName), siteName + " should no longer be listed in the list of sites.");
                break;
            }
            catch (TimeoutException | NoSuchElementException e) {
                counter++;
                getBrowser().refresh();
                siteFinderPage.searchSite(siteName);
                assertTrue(siteFinderPage.checkSiteWasFound(siteName), siteName + " is expected to be found.");
                assertFalse(siteFinderPage.isButtonDisplayedForSite(siteName, "Leave"), "'Leave' button is available for the site.");
                assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName, "Join"), "'Leave' button is available for the site.");
            }
        }
    }

    @TestRail(id = "C2930")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void leaveSiteUsingSiteConfigurationOptions() {
        siteName = String.format("SiteName-C2930-%s", RandomData.getRandomAlphanumeric());
        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName);
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName);
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteManager");
        setupAuthenticatedSession(user1, password);
        siteDashboard.navigate(siteName);
        LOG.info("STEP 1: Click on 'Site configuration options' icon -> Leave Site.");
        siteDashboard.clickSiteConfiguration();
        siteDashboard.clickOptionInSiteConfigurationDropDown("Leave Site", leaveSiteDialog);
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount) {
            try {
                assertEquals(leaveSiteDialog.getDialogTitle(), dialogTitle, "Dialog title is not as expected.");
                assertEquals(leaveSiteDialog.getDialogMessage(), dialogMessage, "Dialog message is not as expected.");
                LOG.info("STEP 2: Click 'OK' button.");
                leaveSiteDialog.clickOKButton();
                assertTrue(siteDashboard.getCurrentUrl().endsWith(user1+"/dashboard"), "User should be redirected to " + user1 + "'s dashboard page.");
                LOG.info("STEP 3: Verify sites listed on 'My Sites' dashlet.");
                assertFalse(mySitesDashlet.isSitePresent(siteName), siteName + " should no longer be listed in the list of sites.");
                break;
            }
            catch (TimeoutException | NoSuchElementException e) {
                counter++;
                getBrowser().refresh();
                siteDashboard.clickSiteConfiguration();
                siteDashboard.clickOptionInSiteConfigurationDropDown("Leave Site", leaveSiteDialog);
            }
        }
    }

    @TestRail(id = "C2931")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void leaveSiteWithoutConfirmation() {
        siteName = String.format("SiteName-C2931-%s", RandomData.getRandomAlphanumeric());
        dialogTitle = String.format(language.translate("leaveSite.dialogTitle"), siteName);
        dialogMessage = String.format(language.translate("leaveSite.dialogMessage"), siteName);
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);
        siteDashboard.navigate(siteName);
        LOG.info("STEP 1: Click on 'Site configuration options' icon -> Leave Site.");
        siteDashboard.clickSiteConfiguration();
        siteDashboard.clickOptionInSiteConfigurationDropDown("Leave Site", leaveSiteDialog);
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount) {
            try {
                assertEquals(leaveSiteDialog.getDialogTitle(), dialogTitle, "Dialog title is not as expected.");
                assertEquals(leaveSiteDialog.getDialogMessage(), dialogMessage, "Dialog message is not as expected.");
                LOG.info("STEP 2: Click Close (x) button.");
                leaveSiteDialog.clickCloseButton();
                assertTrue(leaveSiteDialog.isPopupHidden(), "Popup should be closed");
                assertTrue(siteDashboard.getCurrentUrl().endsWith(siteName+"/dashboard"), "User should remain on " + siteName + "'s dashboard page.");
                LOG.info("STEP 3: Go to \"User's Dashboard\" page and verify \"My Sites\" dashlet.");
                userDashboardPage.navigateByMenuBar();
                assertTrue(mySitesDashlet.isSitePresent(siteName), siteName + " is displayed in the list of sites.");
                break;
            }
            catch (TimeoutException | NoSuchElementException e) {
                counter++;
                getBrowser().refresh();
                siteDashboard.clickSiteConfiguration();
                siteDashboard.clickOptionInSiteConfigurationDropDown("Leave Site", leaveSiteDialog);
            }
        }
    }
}
