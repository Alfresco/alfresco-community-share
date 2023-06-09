package org.alfresco.share.site;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.EnvProperties;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;

import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DeleteSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
@Slf4j
/**
 * @author Laura.Capsa
 */
public class DeleteSiteTests extends BaseTest
{
    @Autowired
    protected UserService userService;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected ContentService contentService;
    @Autowired
    EnvProperties envProperties;

    //@Autowired
    Toolbar toolbar;

    //@Autowired
    SiteFinderPage siteFinderPage;

    //@Autowired
    DeleteSiteDialog deleteSiteDialog;

    //@Autowired
    SearchPage searchFromToolbarPage;

    //@Autowired
    SystemErrorPage systemErrorPage;

    //@Autowired
    MySitesDashlet mySitesDashlet;

    //@Autowired
    SitesManagerPage sitesManagerPage;

    //@Autowired
    SiteDashboardPage siteDashboardPage;
    UserDashboardPage userDashboard;
    String fileNameC2280 = String.format("fileC2280-%s", RandomData.getRandomAlphanumeric());
    String fileNameC2280_1 = String.format("fileC2280-%s", RandomData.getRandomAlphanumeric());
    private String adminUser = "admin";
    private String adminPassword = "admin";
    private String password= "password";
    private final ThreadLocal<UserModel> userC2280 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC2281 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2280_1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2280_2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameUserCanNotDelete = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC2282 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC2283 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC2284 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2284 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC2289_1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC2289_2 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC2291 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2291 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2292 = new ThreadLocal<>();
    SoftAssert softAssert = new SoftAssert();

    @BeforeMethod(alwaysRun = true)
    public void testSetup()
    {
        log.info("Precondition: Test user userC2280  is created");
        userC2280.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteNameC2280_1 is created");
        siteNameC2280_1.set(getDataSite().usingUser(userC2280.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userC2280.get());

        log.info("PreCondition: Site siteNameC2280_2 is created");
        siteNameC2280_2.set(getDataSite().usingUser(userC2280.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userC2280.get());

        contentService.createDocument(userC2280.get().getUsername(), password, siteNameC2280_1.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileNameC2280, "fileC2280");
        contentService.createDocument(userC2280.get().getUsername(), password, siteNameC2280_2.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileNameC2280_1, "fileC2280");

        log.info("Precondition: Test user userC2281 is created");
        userC2281.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteNameUserCanNotDelete is created ");
        siteNameUserCanNotDelete.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(adminUser, adminPassword, userC2281.get().getUsername(), siteNameUserCanNotDelete.get().getId(), "SiteContributor");

        log.info("Precondition: User userC2282 is created");
        userC2282.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(adminUser, adminPassword, userC2282.get().getUsername(), siteNameUserCanNotDelete.get().getId(), "SiteCollaborator");

        log.info("Precondition: User userC2283 is created");
        userC2283.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(adminUser, adminPassword, userC2283.get().getUsername(), siteNameUserCanNotDelete.get().getId(), "SiteConsumer");

        log.info("Precondition: User userC2284 is created");
        userC2284.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteNameC2284 is created");
        siteNameC2284.set(getDataSite().usingUser(userC2284.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Precondition: test user userC2289_1 is created");
        userC2289_1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Precondition: test user userC2289_2 is created");
        userC2289_2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Precondition: User userC2291 is created");
        userC2291.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteNameC2291 is created");
        siteNameC2291.set(getDataSite().usingUser(userC2291.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteNameC2292 is created");
        siteNameC2292.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        siteFinderPage = new SiteFinderPage(webDriver);
        deleteSiteDialog = new DeleteSiteDialog(webDriver);
        userDashboard = new UserDashboardPage(webDriver);
        toolbar = new Toolbar(webDriver);
        searchFromToolbarPage = new SearchPage(webDriver);
        systemErrorPage = new SystemErrorPage(webDriver);
        mySitesDashlet = new MySitesDashlet(webDriver);
        sitesManagerPage = new SitesManagerPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteSitesIfNotNull(siteNameC2280_1.get());
        deleteSitesIfNotNull(siteNameC2280_2.get());
        deleteSitesIfNotNull(siteNameUserCanNotDelete.get());
        deleteSitesIfNotNull(siteNameC2284.get());
        deleteSitesIfNotNull(siteNameC2291.get());
        deleteSitesIfNotNull(siteNameC2292.get());

        deleteUsersIfNotNull(userC2280.get());
        deleteUsersIfNotNull(userC2281.get());
        deleteUsersIfNotNull(userC2282.get());
        deleteUsersIfNotNull(userC2283.get());
        deleteUsersIfNotNull(userC2284.get());
        deleteUsersIfNotNull(userC2289_1.get());
        deleteUsersIfNotNull(userC2289_2.get());
        deleteUsersIfNotNull(userC2291.get());
    }

    @TestRail (id = "C2280")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsManagerFromSiteFinder()
    {
        authenticateUsingLoginPage(userC2280.get());

        log.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        siteFinderPage.navigate();
        siteFinderPage.assertBrowserPageTitleIs("Alfresco » Site Finder");

        log.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameC2280_1.get().getId());
        assertEquals(siteFinderPage.checkSiteWasFound(siteNameC2280_1.get().getId()), true, "Site is found.");

        log.info("STEP3: Verify options available for the site");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameC2280_1.get().getId(), "Delete"), true, "Delete option is displayed.");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameC2280_1.get().getId(), "Leave"), true, "Leave option is displayed.");

        log.info("STEP4: Click on 'Delete' button");
        siteFinderPage.clickSiteButton(siteNameC2280_1.get().getId(), "Delete");
        assertTrue(deleteSiteDialog.isPopupDisplayed(), "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteNameC2280_1.get().getId() + "''?"), true, "Confirm delete message is correct.");

        log.info("STEP5: Click on \"Delete\" button from popup");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true, "Second confirm delete message is correct");

        log.info("STEP6: Click \"Yes\" button");
        deleteSiteDialog.clickYes();
        assertFalse(siteFinderPage.isSiteNameDisplayed(siteNameC2280_1.get().getId()), "The site isn't displayed on \"Site Finder\" page.");

        log.info("STEP7: Search for the file created within the site");
        userDashboard.navigateByMenuBar();
        toolbar.searchAndEnterSearch(fileNameC2280);
        searchFromToolbarPage.assertNoResultsFoundDisplayed();
//        assertFalse(searchFromToolbarPage.isResultFound(fileNameC2280), String.format("File is found %s", fileNameC2280));

        log.info("STEP8: Open created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteNameC2280_1.get().getId() + "/dashboard";
        webDriver.get().navigate().to(url);
        assertEquals(systemErrorPage.getErrorHeader(), language.translate("systemError.header"), "Error message is displayed.");
    }

    @TestRail (id = "C2286")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsManagerFromDashlet()
    {
        authenticateUsingLoginPage(userC2280.get());
        userDashboard.navigate(userC2280.get());

        log.info("STEP1&2: Hover over the created site from \"My sites\" dashlet. Click on \"Delete\" button");
        mySitesDashlet.clickDelete(siteNameC2280_2.get().getId());
        assertTrue(deleteSiteDialog.isPopupDisplayed(), "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteNameC2280_2.get().getId() + "''?"), true, "Confirm delete message is correct.");

        log.info("STEP3: Click again on \"Delete\" button.");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true, "Second confirm delete message is correct");

        log.info("STEP4: Click \"Yes\" button");
        deleteSiteDialog.clickYes();
        assertFalse(mySitesDashlet.isSitePresent(siteNameC2280_2.get().getId()), "Site isn't displayed in 'MySites' dashlet.");

        log.info("STEP5: Search for the file created within the site");
        toolbar.search(siteNameC2280_2.get().getId());
        assertEquals(searchFromToolbarPage.getNumberOfResultsText(), "0 - results found", "No results");

        log.info("STEP6: Open created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteNameC2280_2.get().getId() + "/dashboard";
        webDriver.get().navigate().to(url);
        assertEquals(systemErrorPage.getErrorHeader(), language.translate("systemError.header"), "Error message is displayed.");
    }

    @TestRail (id = "C2281, C2287")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsContributorFromSiteFinder()
    {
        authenticateUsingLoginPage(userC2281.get());

        log.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbar.clickSites().clickSiteFinder();
        softAssert.assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        log.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameUserCanNotDelete.get().getId());
        softAssert.assertEquals(siteFinderPage.checkSiteWasFound(siteNameUserCanNotDelete.get().getId()), true, "Site is found.");

        log.info("STEP3: Verify options available for the site");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete.get().getId(), "Delete"), false, "Delete option is not displayed.");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete.get().getId(), "Leave"), true, "Leave option is displayed.");
        userDashboard.navigate(userC2281.get());

        log.info("Step 4: Verify options available on My Sites Dashlet");
        mySitesDashlet.hoverSite(siteNameUserCanNotDelete.get().getId());
        softAssert.assertEquals(mySitesDashlet.isDeleteButtonDisplayed(), false, "Delete button isn't displayed.");
        softAssert.assertAll();
    }

    @TestRail (id = "C2282, C2288")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsCollaboratorFromSiteFinder()
    {
        authenticateUsingLoginPage(userC2282.get());

        log.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbar.clickSites().clickSiteFinder();
        softAssert.assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        log.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameUserCanNotDelete.get().getId());
        softAssert.assertEquals(siteFinderPage.checkSiteWasFound(siteNameUserCanNotDelete.get().getId()), true, "Site is found.");

        log.info("STEP3: Verify options available for the site");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete.get().getId(), "Delete"), false, "Delete option is not displayed.");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete.get().getId(), "Leave"), true, "Leave option is displayed.");

        log.info("Step 4: Verify options available on My Sites Dashlet");
        userDashboard.navigateByMenuBar();
        mySitesDashlet.hoverSite(siteNameUserCanNotDelete.get().getId());
        softAssert.assertEquals(mySitesDashlet.isDeleteButtonDisplayed(), false, "Delete button isn't displayed.");
        softAssert.assertAll();
    }

    @TestRail (id = "C2283, C2289")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsConsumerFromSiteFinder()
    {
        authenticateUsingLoginPage(userC2283.get());

        log.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        siteFinderPage.navigateByMenuBar();
        softAssert.assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        log.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameUserCanNotDelete.get().getId());
        softAssert.assertEquals(siteFinderPage.checkSiteWasFound(siteNameUserCanNotDelete.get().getId()), true, "Site is found.");

        log.info("STEP3: Verify options available for the site");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete.get().getId(), "Delete"), false, "Delete option is not displayed.");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete.get().getId(), "Leave"), true, "Leave option is displayed.");

        log.info("Step 4: Verify options available on My Sites Dashlet");
        userDashboard.navigate(userC2283.get());
        mySitesDashlet.hoverSite(siteNameUserCanNotDelete.get().getId());
        assertEquals(mySitesDashlet.isDeleteButtonDisplayed(), false, "Delete button isn't displayed.");
        softAssert.assertAll();
    }

    @TestRail (id = "C2284")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeleteSiteFromSiteFinder()
    {
        authenticateUsingLoginPage(userC2284.get());

        log.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        siteFinderPage.navigateByMenuBar();
        assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        log.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameC2284.get().getId());
        assertEquals(siteFinderPage.checkSiteWasFound(siteNameC2284.get().getId()), true, "Site is found.");

        log.info("STEP3: Click on 'Delete' button");
        siteFinderPage.clickSiteButton(siteNameC2284.get().getId(), "Delete");
        assertEquals(deleteSiteDialog.isPopupDisplayed(), true, "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteNameC2284.get().getId() + "''?"), true, "Confirm delete message is correct.");

        log.info("STEP4: Click on 'Cancel' button");
        deleteSiteDialog.clickCancel();
        siteFinderPage.searchSiteWithName(siteNameC2284.get().getId());
        assertEquals(siteFinderPage.checkSiteWasFound(siteNameC2284.get().getId()), true, "Site is found, it wasn't deleted.");

        log.info("STEP5: Click again on 'Delete' button");
        siteFinderPage.clickSiteButton(siteNameC2284.get().getId(), "Delete");
        assertEquals(deleteSiteDialog.isPopupDisplayed(), true, "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteNameC2284.get().getId() + "''?"), true, "Confirm delete message is correct.");

        log.info("STEP6: Click on 'Delete' button");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true, "Second confirm delete message is correct");
        deleteSiteDialog.clickNo();
        siteFinderPage.searchSiteWithName(siteNameC2284.get().getId());
        assertEquals(siteFinderPage.checkSiteWasFound(siteNameC2284.get().getId()), true, "Site is found, it wasn't deleted.");
    }

    @TestRail (id = "C2292")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES }, enabled = true)
    public void cancelDeleteSiteFromSitesManager() {

        authenticateUsingLoginPage(getAdminUser());
        log.info("STEP1: Open \"Site Manager\" page");
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.isSitesTableDisplayed(), true, "Site Manager page is displayed.");

        log.info("STEP2: Click on \"Actions\" -> \"Delete\" button for \"siteA\"");
        sitesManagerPage.usingSite(siteNameC2292.get()).clickDelete().assertConfirmMessageFromSiteManagerIsCorrect(siteNameC2292.get().getId())
            .clickCancelFromSitesManager();
        sitesManagerPage.usingSite(siteNameC2292.get()).assertSiteIsDisplayed(siteNameC2292.get().getId());

        log.info("STEP4: Open the created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteNameC2292.get().getId() + "/dashboard";
        webDriver.get().navigate().to(url);
        siteDashboardPage.assertSiteHeaderTitleIs(siteNameC2292.get());
    }
}