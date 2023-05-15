package org.alfresco.share.site.accessingExistingSites;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.po.share.site.members.SiteMembersPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
public class MovingAroundASiteTests extends BaseTest
{
   // @Autowired
    SiteDashboardPage siteDashboard;

    //@Autowired
    CustomizeSitePage customizeSite;

    DocumentLibraryPage documentLibraryPage;

    SiteMembersPage siteMembersPage;

    SiteDashboardPage siteDashboardPage;

    //@Autowired
    AddSiteUsersPage addSiteUsersPage;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating two users");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        authenticateUsingLoginPage(user1.get());

        siteDashboard = new SiteDashboardPage(webDriver);
        customizeSite = new CustomizeSitePage(webDriver);
        addSiteUsersPage = new AddSiteUsersPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        siteMembersPage = new SiteMembersPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C3034")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyDefaultAreas() {
        log.info("PreCondition2: Any site is created by "+user1);
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        log.info("STEP 1: Go to the created site." + siteName + "and verify the default areas available.");
        siteDashboard.navigate(siteName.get().getId());
        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isSiteMembersLinkDisplayed(), "Site Members is a default area on the site.");

        log.info("STEP 2: Click on 'Document Library' link.");
        siteDashboard.navigateToDocumentLibraryPage();
        documentLibraryPage.assertBrowserPageTitleIs("Alfresco » Document Library");

        log.info("STEP 3: Click on 'Site Members' link.");
        siteDashboard.clickSiteMembers();
        siteMembersPage.assertBrowserPageTitleIs("Alfresco » Site Members");

        log.info("STEP 4: Click on 'Site Dashboard' link.");
        siteDashboard.clickSiteDashboard();
        siteDashboardPage.assertBrowserPageTitleIs("Alfresco » Site Dashboard");

        deleteSitesIfNotNull(siteName.get());

    }

    @TestRail (id = "C3035")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addAdditionalFeatures() {
        log.info("PreCondition2: Any site is created by "+user1);
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        log.info("STEP 1: Add the other available pages to the site, in the following order (using 'Customize site' feature)");
        customizeSite.navigate(siteName.get().getId());
        customizeSite.addPageToSite(SitePageType.CALENDER);
        customizeSite.addPageToSite(SitePageType.WIKI);
        customizeSite.addPageToSite(SitePageType.DISCUSSIONS);
        customizeSite.addPageToSite(SitePageType.BLOG);
        customizeSite.addPageToSite(SitePageType.LINKS);
        customizeSite.addPageToSite(SitePageType.DATA_LISTS);
        customizeSite.saveChanges();
        siteDashboard.waitUntilNotificationMessageDisappears();

        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.CALENDER), "Calendar is available on the the site's header");
        assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.WIKI), "Wikis available on the the site's header");
        assertTrue(siteDashboard.isMoreLinkDisplayed(), "More menu is available on the the site's header.");

        log.info("STEP 2: Click on 'More' menu.");
        siteDashboard.clickMoreLink();
        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Discussions"), "'Discussions' link is displayed in the 'More' menu.");
        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Blog"), "'Blog' link is displayed in the 'More' menu.");
//        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Links"), "'Links' link is displayed in the 'More' menu.");
//        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Data Lists"), "'Data Lists' link is displayed in the 'More' menu.");
        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Site Members"), "'Site Members' link is displayed in the 'More' menu.");

        log.info("STEP 3: Click on 'Calendar' link.");
        siteDashboard.clickLinkFromHeaderNavigationMenu(SitePageType.CALENDER);
        siteDashboard.assertBrowserPageTitleIs("Alfresco » Calendar");

        log.info("STEP 4: Click on 'Wiki' link.");
        siteDashboard.clickLinkFromHeaderNavigationMenu(SitePageType.WIKI);
        siteDashboard.assertBrowserPageTitleIs("Alfresco » Wiki » Main_Page");

        log.info("STEP 5: Click on 'Discussions' link from 'More' menu.");
        siteDashboard.clickLinkFromMoreMenu("Discussions");
        siteDashboard.assertBrowserPageTitleIs("Alfresco » Discussions");

        log.info("STEP 6: Click on 'Blog' link from 'More' menu.");
        siteDashboard.clickLinkFromMoreMenu("Blog");
        siteDashboard.assertBrowserPageTitleIs("Alfresco » Blog");

//        log.info("STEP 7: Click on 'Links' link from 'More' menu.");
//        siteDashboard.clickLinkFromMoreMenu("Links");
//        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/links"), "'Links' page is opened.");

        log.info("STEP 8: Click on 'Site Members' link from 'More' menu.");
        siteDashboard.clickLinkFromMoreMenu("Site Members");
        siteDashboard.assertBrowserPageTitleIs("Alfresco » Site Members");

//        log.info("STEP 9: Click on 'Data Lists' link from 'More' menu.");
//        siteDashboard.clickLinkFromMoreMenu("Data Lists");
//        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/data-lists"), "'Data Lists' page is opened.");

        deleteSitesIfNotNull(siteName.get());

    }

    @TestRail (id = "C3036")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void siteFeaturesAreAvailableOnAnyPageFromTheSite()
    {
        log.info("PreCondition2: Any site is created by "+user1);
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        siteDashboard.navigate(siteName.get());
        log.info("STEP 1: Click on 'Document Library' link.");
        siteDashboard.navigateToDocumentLibraryPage();
        documentLibraryPage.assertBrowserPageTitleIs("Alfresco » Document Library");

        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isSiteMembersLinkDisplayed(), "Site Members is a default area on the site.");

        log.info("STEP 2: Click on 'Site Members' link.");
        siteDashboard.clickSiteMembers();
        siteDashboard.assertBrowserPageTitleIs("Alfresco » Site Members");

        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isSiteMembersLinkDisplayed(), "Site Members is a default area on the site.");

        log.info("STEP 3: Open any other page from the site (e.g.: 'Add Users' page).");
        addSiteUsersPage.navigate(siteName.get());
        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isSiteMembersLinkDisplayed(), "Site Members is a default area on the site.");

        deleteSitesIfNotNull(siteName.get());
    }
}
