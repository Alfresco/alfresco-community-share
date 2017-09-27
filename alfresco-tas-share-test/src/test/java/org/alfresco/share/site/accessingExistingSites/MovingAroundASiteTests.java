package org.alfresco.share.site.accessingExistingSites;

import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class MovingAroundASiteTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    CustomizeSitePage customizeSite;

    @Autowired
    AddSiteUsersPage addSiteUsersPage;

    private String user = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String siteName;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C3034")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyDefaultAreas()
    {
        siteName = String.format("Site-C3034-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);

        LOG.info("STEP 1: Go to the created site." + siteName + "and verify the default areas available.");
        siteDashboard.navigate(siteName);
        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isSiteMembersLinkDisplayed(), "Site Members is a default area on the site.");

        LOG.info("STEP 2: Click on 'Document Library' link.");
        siteDashboard.clickDocumentLibrary();
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/documentlibrary"), "'Document Library' page is opened.");

        LOG.info("STEP 3: Click on 'Site Members' link.");
        siteDashboard.clickSiteMembers();
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/site-members"), "'Site Members' page is opened.");

        LOG.info("STEP 4: Click on 'Site Dashboard' link.");
        siteDashboard.clickSiteDashboard();
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/dashboard"), "'Site Dashboard' page is opened.");
    }

    @TestRail(id = "C3035")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addAdditionalFeatures()
    {
        siteName = String.format("Site-C3035-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        LOG.info("STEP 1: Add the other available pages to the site, in the following order (using 'Customize site' feature)");
        customizeSite.navigate(siteName);
        customizeSite.addPageToSite(SitePageType.CALENDER);
        customizeSite.addPageToSite(SitePageType.WIKI);
        customizeSite.addPageToSite(SitePageType.DISCUSSIONS);
        customizeSite.addPageToSite(SitePageType.BLOG);
        customizeSite.addPageToSite(SitePageType.LINKS);
        customizeSite.addPageToSite(SitePageType.DATA_LISTS);
        customizeSite.clickOk();
        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.CALENDER), "Calendar is available on the the site's header");
        assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.WIKI), "Wikiis available on the the site's header");
        assertTrue(siteDashboard.isMoreLinkDisplayed(), "More menu is available on the the site's header.");

        LOG.info("STEP 2: Click on 'More' menu.");
        siteDashboard.clickMoreLink();
        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Discussions"), "'Discussions' link is displayed in the 'More' menu.");
        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Blog"), "'Blog' link is displayed in the 'More' menu.");
        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Links"), "'Links' link is displayed in the 'More' menu.");
        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Data Lists"), "'Data Lists' link is displayed in the 'More' menu.");
        assertTrue(siteDashboard.isLinkDisplayedInMoreMenu("Site Members"), "'Site Members' link is displayed in the 'More' menu.");

        LOG.info("STEP 3: Click on 'Calendar' link.");
        siteDashboard.clickLinkFromHeaderNavigationMenu(SitePageType.CALENDER);
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/calendar"), "'Calendar' page is opened.");

        LOG.info("STEP 4: Click on 'Wiki' link.");
        siteDashboard.clickLinkFromHeaderNavigationMenu(SitePageType.WIKI);
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/wiki-page?title=Main_Page"), "'Wiki' page is opened.");

        LOG.info("STEP 5: Click on 'Discussions' link from 'More' menu.");
        siteDashboard.clickLinkFromMoreMenu("Discussions");
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/discussions-topiclist"), "'Discussions' page is opened.");

        LOG.info("STEP 6: Click on 'Blog' link from 'More' menu.");
        siteDashboard.clickLinkFromMoreMenu("Blog");
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/blog-postlist"), "'Blog' page is opened.");

        LOG.info("STEP 7: Click on 'Links' link from 'More' menu.");
        siteDashboard.clickLinkFromMoreMenu("Links");
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/links"), "'Links' page is opened.");

        LOG.info("STEP 8: Click on 'Site Members' link from 'More' menu.");
        siteDashboard.clickLinkFromMoreMenu("Site Members");
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/site-members"), "'Site Members' page is opened.");

        LOG.info("STEP 9: Click on 'Data Lists' link from 'More' menu.");
        siteDashboard.clickLinkFromMoreMenu("Data Lists");
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/data-lists"), "'Data Lists' page is opened.");
    }

    @TestRail(id = "C3036")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void siteFeaturesAreAvailableOnAnyPageFromTheSite()
    {
        siteName = String.format("Site-C3036-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteDashboard.navigate(siteName);
        LOG.info("STEP 1: Click on 'Document Library' link.");
        siteDashboard.clickDocumentLibrary();
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/documentlibrary"), "'Document Library' page is opened.");
        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isSiteMembersLinkDisplayed(), "Site Members is a default area on the site.");

        LOG.info("STEP 2: Click on 'Site Members' link.");
        siteDashboard.clickSiteMembers();
        assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/site-members"), "'Site Members' page is opened.");
        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isSiteMembersLinkDisplayed(), "Site Members is a default area on the site.");

        LOG.info("STEP 3: Open any other page from the site (e.g.: 'Add Users' page).");
        addSiteUsersPage.navigate(siteName);
        assertTrue(siteDashboard.isSiteDashboardLinkDisplayed(), "Site Dashboard is a default area on the site.");
        assertTrue(siteDashboard.isDocumentLibraryLinkDisplayed(), "Document Library is a default area on the site.");
        assertTrue(siteDashboard.isSiteMembersLinkDisplayed(), "Site Members is a default area on the site.");
    }
}
