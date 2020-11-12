package org.alfresco.share.searching;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class SiteFinderTests extends ContextAwareWebTest
{
    //@Autowired
    SiteFinderPage siteFinderPage;

    //@Autowired
    UserDashboardPage userDashboardPage;

    String user1 = String.format("profileUser1-%s", RandomData.getRandomAlphanumeric());
    String user2 = String.format("profileUser2-%s", RandomData.getRandomAlphanumeric());
    String userFirstName = "firstName";
    String user2LastName = "lastName2";
    String siteName1 = String.format("SiteName1-%s", RandomData.getRandomAlphanumeric());
    String siteName2 = String.format("SiteName2-%s", RandomData.getRandomAlphanumeric());
    String description = String.format("Description-%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, userFirstName, "lastName1");
        siteService.create(user1, password, domain, siteName1, description, SiteService.Visibility.MODERATED);
        siteService.create(user1, password, domain, siteName2, description, SiteService.Visibility.PRIVATE);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, userFirstName, user2LastName);
        setupAuthenticatedSession(user1, password);
    }

    @AfterClass
    public void removeAddedFiles()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
        siteService.delete(adminUser, adminPassword, siteName1);
        siteService.delete(adminUser, adminPassword, siteName2);

    }


    @TestRail (id = "C5876")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void siteFinderPage()
    {
        siteFinderPage.navigate();

        LOG.info("STEP1: Verify page title");
        assertEquals(siteFinderPage.getPageTitle(), "Alfresco » Site Finder", "Page title");

        LOG.info("STEP2: Verify search section");
        assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search input field is displayed");
        assertTrue(siteFinderPage.isSearchButtonDisplayed(), "Search button is displayed");
        assertEquals(siteFinderPage.getSearchMessage(), language.translate("siteFinder.helpMessage"), "Help message");

        LOG.info("STEP3: Fill in search field and click \"Search\" button");
        siteFinderPage.searchSiteWithRetry(siteName1);
        assertTrue(siteFinderPage.isSearchResultsListDisplayed(), "Search results list contains list of sites");
        assertTrue(siteFinderPage.isSiteNameListDisplayed(), "Search results list contains site title");
        assertTrue(siteFinderPage.isSiteDescriptionListDisplayed(), "Search results list contains site description");
        assertTrue(siteFinderPage.isSiteVisibilityListDisplayed(), "Search results list contains site visibility");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName1, "Leave"), "Leave button displayed for site " + siteName1);
        assertTrue(siteFinderPage.isButtonDisplayedForSite(siteName1, "Delete"), "Delete button displayed for site " + siteName1);

        LOG.info("STEP4: Click a site link from the search results");
        siteFinderPage.accessSite(siteName1);
        assertEquals(siteFinderPage.getPageTitle(), "Alfresco » Site Dashboard", "User is redirected to " + siteName1 + " site dashboard page ");
    }

    @TestRail (id = "C7574")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void usernameWithSpaceCanAccessSiteFinder()
    {
        LOG.info("STEP1: Click \"Sites\" -> \"Site Finder\" link from the toolbar");
        toolbar.clickSites().clickSiteFinder();
        assertEquals(siteFinderPage.getPageTitle(), "Alfresco » Site Finder", "Site Finder page is displayed");
    }

    @TestRail (id = "C5814")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void fullOrPartialSitename()
    {
        siteFinderPage.navigate();

        LOG.info("STEP1: Enter the partial site name in the search field and click the search button");
        siteFinderPage.searchSiteWithRetry(siteName1.substring(0, 17));
        assertTrue(siteFinderPage.checkSiteWasFound(siteName1), "Site " + siteName1 + " is displayed in search result section");

        LOG.info("STEP2: Enter the full site name in the search field and click the search button");
        siteFinderPage.searchSiteWithRetry(siteName1);
        assertTrue(siteFinderPage.checkSiteWasFound(siteName1), "Site " + siteName1 + " is displayed in search result section");
    }

    @TestRail (id = "C7169")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void moderatedSiteLabel()
    {
        siteFinderPage.navigate();

        LOG.info("STEP1: Enter the moderated site's name into the search field and click the search button");
        siteFinderPage.searchSiteWithRetry(siteName1);
        assertTrue(siteFinderPage.checkSiteWasFound(siteName1), "Site " + siteName1 + "is displayed in search result section");
        assertEquals(siteFinderPage.getVisibilityLabel(), "Moderated", " \"Moderated\" label is displayed below " + siteName1 + " site");
    }

    @TestRail (id = "C7195")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void privateSiteLabel()
    {
        siteFinderPage.navigate();

        LOG.info("STEP1: Enter the private site's name into the search field and click the search button");
        siteFinderPage.searchSiteWithRetry(siteName2);
        assertTrue(siteFinderPage.checkSiteWasFound(siteName2), "Site " + siteName2 + "is displayed in search result section");
        assertEquals(siteFinderPage.getVisibilityLabel(), "Private", " \"Private\" label is displayed below " + siteName2 + " site");

        LOG.info("STEP2: Logout and log in as user2");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        assertEquals(userDashboardPage.getPageHeader(), userFirstName + " " + user2LastName + " Dashboard", user2 + "'s user dashboard is displayed");

        LOG.info("STEP3: Open \"Site Finder\" page");
        siteFinderPage.navigate();

        LOG.info("STEP1: Enter the private site's name into the search field and click the search button");
        siteFinderPage.searchSite(siteName2);
        assertEquals(siteFinderPage.getSearchMessage(), language.translate("siteFinder.noResults"), "Displayed message:");
        assertFalse(siteFinderPage.checkSiteWasFound(siteName2), "No results displayed in search result section");

        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user1, password);
    }
}
