package org.alfresco.share.searching;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarUtility;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class BasicSearchTests extends ContextAwareWebTest
{
    @Autowired
    Toolbar toolbar;

    @Autowired
    SearchPage searchPage;

    @Autowired
    UserProfilePage userProfilePage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    CalendarUtility calendarUtility;

    String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    String userName1 = "profileUser1-" + uniqueIdentifier;
    String userName2 = "profileUser2-" + uniqueIdentifier;
    String firstName = "FirstName";
    String lastName1 = "LastName1";
    String lastName2 = "LastName2";
    String siteName1 = "Site1-" + uniqueIdentifier;
    String siteName2 = "Site2-" + uniqueIdentifier;
    String description = "Description-" + uniqueIdentifier;
    String docName1 = "TestDoc1-" + uniqueIdentifier;
    String docName2 = "Document2-" + uniqueIdentifier;
    String docContent = "content of the file.";
    String calendarEvent = "EventTitle-" + uniqueIdentifier;
    String wikiPage = "WikiPage-" + uniqueIdentifier;
    String discussion = "discussionTitle-" + uniqueIdentifier;
    String blogPost = "BlogPost-" + uniqueIdentifier;
    String link = "Link-" + uniqueIdentifier;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, firstName, lastName1);
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, firstName, lastName2);
        siteService.create(userName1, password, domain, siteName1, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName2, password, domain, siteName2, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName1, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, docName1, docContent);
        contentService.createDocument(userName1, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, docName2, docContent);
        sitePagesService.addCalendarEvent(userName1, password, siteName1, calendarEvent, "EventLocation", "description of the event",
                calendarUtility.firstDayOfCW(), calendarUtility.firstDayOfCW(), "", "", false, "tag");
        sitePagesService.createWiki(userName1, password, siteName1, wikiPage, "content of the wiki page", null);
        sitePagesService.createDiscussion(userName1, password, siteName1, discussion, "text", null);
        sitePagesService.createBlogPost(userName1, password, siteName1, blogPost, "content of the blog", false, null);
        sitePagesService.createLink(userName1, password, siteName1, link, "url", "description of link", false, null);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);

        siteService.delete(adminUser, adminPassword, siteName1);
        siteService.delete(adminUser, adminPassword, siteName2);
    }

    @TestRail(id = "C5933")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyLiveSearchableContent()
    {
        setupAuthenticatedSession(userName1, password);
        LOG.info("STEP1: Verify Search toolbar");
        assertTrue(toolbar.isSearchIconDisplayed(), "Search icon is displayed.");
        assertTrue(toolbar.isSearchBoxDisplayed(), "Search input field is displayed.");
        assertEquals(toolbar.getSearchBoxPlaceholder(), language.translate("toolbar.searchInputPlaceholder"), "Search box placeholder:");
        assertTrue(toolbar.isClearSearchBoxDisplayed(), "Clear search box is displayed.");

        LOG.info("STEP2: Fill in toolbar search field with a user name");
        toolbar.searchInToolbar(userName2);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(userName2), "Live search result list contains the searched user.");

        LOG.info("STEP3: Click a user link from search results");
        toolbar.clickResultFromLiveSearch(userName2);
        assertEquals(userProfilePage.getPageTitle(), "Alfresco » User Profile Page", "The user1 is redirected to:");

        LOG.info("STEP4: Fill in toolbar search field with a site name");
        toolbar.searchInToolbar(siteName1);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(siteName1), "Live search result list contains the searched site.");

        LOG.info("STEP5: Click a site link from search results");
        toolbar.clickResult(siteName1, siteDashboardPage);
        assertEquals(siteDashboardPage.getPageTitle(), "Alfresco » Site Dashboard", "The user1 is redirected to:");

        LOG.info("STEP6: Fill in toolbar search field with a document name");
        toolbar.searchInToolbar(docName1);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(docName1), "Live search result list contains the searched document.");

        LOG.info("STEP7: Click a document link from search results");
        toolbar.clickResult(docName1, documentDetailsPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "The user1 is redirected to:");

        LOG.info("STEP8: Fill in toolbar search field with a wiki page");
        toolbar.searchInToolbar(wikiPage);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(wikiPage), "Live search result list contains the searched wiki page");

        LOG.info("STEP9: Fill in toolbar search field with a blog post");
        toolbar.searchInToolbar(blogPost);
        getBrowser().waitInSeconds(2);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(blogPost), "Live search result list contains the searched blog post");

        LOG.info("STEP10: Fill in toolbar search field with a calendar event");
        toolbar.searchInToolbar(calendarEvent);
        getBrowser().waitInSeconds(2);
        assertFalse(toolbar.isLiveSearchResultsListDisplayed(), "No live search results are displayed");

        LOG.info("STEP11: Fill in toolbar search field with a discussion topic");
        toolbar.searchInToolbar(discussion);
        assertFalse(toolbar.isLiveSearchResultsListDisplayed(), "No live search results are displayed");

        LOG.info("STEP12: Fill in toolbar search field with a link");
        toolbar.searchInToolbar(link);
        assertFalse(toolbar.isLiveSearchResultsListDisplayed(), "No live search results are displayed");

        cleanupAuthenticatedSession();
    }

    @Bug(id = "TBD")
    @TestRail(id = "C5945")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifySearchPage()
    {
        setupAuthenticatedSession(userName1, password);
        LOG.info("STEP1: Enter the document name in the toolbar search field and press Enter");
        toolbar.search(docName1);
        assertEquals(searchPage.getRelativePath(), "share/page/dp/ws/faceted-search#searchTerm=%s&scope=repo&sortField=Relevance",
                "User is redirected to Search page.");

        LOG.info("STEP2: Verify page title");
        assertEquals(searchPage.getPageHeader(), "Search", "Search page title:");

        LOG.info("STEP3: Verify search section");
        assertTrue(searchPage.isSearchInLabelDisplayed(), "'Search in' label is displayed.");
        assertTrue(searchPage.getNumberOfResultsText().contains(" - results found"), "Section with number of results is displayed");
        assertTrue(searchPage.isSearchButtonDisplayed(), "Search button is displayed.");

        LOG.info("STEP4: Click \"Search In\" dropdown");
        searchPage.clickSearchInDropdown();
        assertEquals(searchPage.getSearchInDropdownValues(), "All Sites, Repository", "'Search in' dropdown");
        assertEquals(searchPage.getSearchInDropdownSelectedValue(), "Repository", "'Search in' dropdown has default selected value:");

        LOG.info("STEP5: Verify \"Filter by\" section");
        ArrayList<String> expectedList = new ArrayList<>(Arrays.asList("Creator", "File Type", "Modifier", "Created", "Size", "Modified"));
        for (String anExpectedList : expectedList)
        {
            assertTrue(searchPage.getFilterTypeList().contains(anExpectedList), "Filter is not present in 'Filter by' section!");
        }
        
        LOG.info("STEP6: Click 'Sort' dropdown");
        searchPage.clickSortDropdown();
        assertTrue(searchPage.isSortDropdownComplete(),
                "Dropdown for results sorting is displayed with options: \"Relevance\", \"Name\", \"Title\", \"Description\", \"Author\", \"Modifier\", \"Modified date\", \"Creator\", \"Created date\", \"Size\", \"Mime type\" and \"Type\" ");

        LOG.info("STEP7: Click \"Views\" dropdown");
        searchPage.clickViewsDropdown();
        ArrayList<String> expectedViewsDropdown = new ArrayList<>(Arrays.asList("Detailed View", "Gallery View"));
        assertEquals(searchPage.getViewsDropdownOptions().toString(), expectedViewsDropdown.toString(), "Views dropdown option=");
        getBrowser().refresh();

        LOG.info("STEP8: Hover over a result");
        searchPage.mouseOverResult(docName1);
        assertTrue(searchPage.isActionsLinkDisplayed(), "The \"Actions\" menu is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7706")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifySearchInDropdownOptions()
    {
        setupAuthenticatedSession(userName1, password);
        siteDashboardPage.navigate(siteName1);

        LOG.info("STEP1: Enter document name in the live search field and press Enter");
        toolbar.search(docName1);
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "User is redirected to:");

        LOG.info("STEP2: Select the site name option from 'Search in' dropdown");
        searchPage.selectOptionFromSearchIn(siteName1);
        assertTrue(searchPage.isResultFound(docName1), docName1 + " is displayed in results list.");

        LOG.info("STEP3: Select \"All Sites\" option from 'Search in' dropdown");
        searchPage.selectOptionFromSearchIn("All Sites");
        assertTrue(searchPage.isResultFound(docName1), docName1 + " is displayed in results list.");

        LOG.info("STEP4: Select \"Repository\" option from 'Search in' dropdown");
        searchPage.selectOptionFromSearchIn("Repository");
        assertTrue(searchPage.isResultFound(docName1), docName1 + " is displayed in results list.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C6168")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void defaultPlaceholderText()
    {
        setupAuthenticatedSession(userName1, password);
        LOG.info("STEP1: Verify Live Search placeholder text");
        assertEquals(toolbar.getSearchBoxPlaceholder(), language.translate("toolbar.searchInputPlaceholder"), "Live search box placeholder text");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C8145")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyDetailedView()
    {
        setupAuthenticatedSession(userName1, password);

        LOG.info("STEP1: Enter the document name in the toolbar search field and press 'Enter'");
        toolbar.search(docName1);
        assertEquals(searchPage.getRelativePath(), "share/page/dp/ws/faceted-search#searchTerm=%s&scope=repo&sortField=Relevance",
                "User is redirected to Search page.");

        LOG.info("STEP2: Verify the default view");
        assertTrue(searchPage.isSearchResultsListInDetailedView(), "Detailed view is displayed.");

        LOG.info("STEP3: Change the view to \"Gallery View\" from 'Views' dropdown");
        searchPage.clickGalleryView();
        getBrowser().waitInSeconds(4);
        assertTrue(searchPage.isSearchResultsListInGalleryView(), "Results are displayed in Gallery View");
        getBrowser().refresh();

        LOG.info("STEP4: Change the view to \"Detailed View\"");
        searchPage.clickDetailedView();
        getBrowser().waitInSeconds(4);
        assertTrue(searchPage.isSearchResultsListInDetailedView(), "Results are displayed in Detailed View.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C8146")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyGalleryView()
    {
        setupAuthenticatedSession(userName1, password);
        LOG.info("STEP1: Enter the document name in the toolbar search field and press Enter");
        toolbar.search(docName1);
        assertEquals(searchPage.getRelativePath(), "share/page/dp/ws/faceted-search#searchTerm=%s&scope=repo&sortField=Relevance",
                "User is redirected to Search page.");

        LOG.info("STEP2: Change the view to \"Gallery View\" from 'Views' dropdown and verify results section");
        searchPage.clickGalleryView();
        assertTrue(searchPage.isSearchResultsListInGalleryView(), "Each search result is displayed as a thumbnail.");
        assertTrue(searchPage.isSliderGalleryViewDisplayed(), "Slider is displayed.");
        assertTrue(searchPage.getNumberOfResultsText().contains(" - results found"), "Number of results");
        searchPage.clickSortDropdown();
        assertTrue(searchPage.isSortDropdownComplete(),
                "Dropdown for results sorting is displayed with options: \"Relevance\", \"Name\", \"Title\", \"Description\", \"Author\", \"Modifier\", \"Modified date\", \"Creator\", \"Created date\", \"Size\", \"Mime type\" and \"Type\" ");
        searchPage.clickViewsDropdown();
        ArrayList<String> expectedViewsDropdown = new ArrayList<>(Arrays.asList("Detailed View", "Gallery View"));
        assertEquals(searchPage.getViewsDropdownOptions().toString(), expectedViewsDropdown.toString(), "Views dropdown option=");

        searchPage.clickDetailedView();
        cleanupAuthenticatedSession();
    }
}