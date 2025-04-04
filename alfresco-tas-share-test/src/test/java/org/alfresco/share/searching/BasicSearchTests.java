package org.alfresco.share.searching;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarUtility;
import org.alfresco.po.share.toolbar.Toolbar;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class BasicSearchTests extends BaseTest
{
    //@Autowired
    Toolbar toolbar;

    //@Autowired
    SearchPage searchPage;

    //@Autowired
    SiteDashboardPage siteDashboardPage;
    @Autowired
    CalendarUtility calendarUtility;
    @Autowired
    private SitePagesService sitePagesService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private SiteService siteService;
    String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String password = "password";
    String docName1 = "TestDoc1-" + uniqueIdentifier;
    String docName2 = "Document2-" + uniqueIdentifier;
    String docContent = "content of the file.";
    String calendarEvent = "EventTitle-" + uniqueIdentifier;
    String wikiPage = "WikiPage-" + uniqueIdentifier;
    String discussion = "discussionTitle-" + uniqueIdentifier;
    String blogPost = "BlogPost-" + uniqueIdentifier;
    String link = "Link-" + uniqueIdentifier;
    private FileModel testFile;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating a TestUser1");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a TestUser2");
        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        siteModel.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user1.get());
        deleteUsersIfNotNull(user2.get());
        deleteSitesIfNotNull(siteModel.get());
        deleteAllCookiesIfNotNull();
    }

    @TestRail (id = "C5933")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyLiveSearchableContent() {
        String userName1 = user1.get().getUsername();
        String userName2 = user2.get().getUsername();
        String siteName = siteModel.get().getId();

        log.info("Data creation as per pre condition");
        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.CALENDAR);
        sitePages.add(DashboardCustomization.Page.LINKS);
        siteService.addPagesToSite(userName1, password, siteName, sitePages);

        contentService.createDocument(userName1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName1, docContent);
        contentService.createDocument(userName1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName2, docContent);
        sitePagesService.addCalendarEvent(userName1, password, siteName, calendarEvent, "EventLocation", "description of the event",
            calendarUtility.firstDayOfCW(), calendarUtility.firstDayOfCW(), "", "", false, "tag");
        sitePagesService.createWiki(userName1, password, siteName, wikiPage, "content of the wiki page", null);
        sitePagesService.createDiscussion(userName1, password, siteName, discussion, "text", null);
        sitePagesService.createBlogPost(userName1, password, siteName, blogPost, "content of the blog", false, null);
        sitePagesService.createLink(userName1, password, siteName, link, "url", "description of link", false, null);

        authenticateUsingLoginPage(user1.get());

        log.info("STEP1: Verify Search toolbar");
        toolbar.assertSearchInputIsDisplayed().assertSearchIconIsDisplayed();
        assertEquals(toolbar.getSearchBoxPlaceholder(), language.translate("toolbar.searchInputPlaceholder"), "Search box placeholder:");
        assertTrue(toolbar.isClearSearchBoxDisplayed(), "Clear search box is displayed.");

        log.info("STEP2: Fill in toolbar search field with a user name");
        toolbar.searchInToolbar(userName2);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(userName2), "Live search result list contains the searched user.");

        log.info("STEP3: Click a user link from search results");
        toolbar.clickResultFromLiveSearch(userName2);

        log.info("STEP4: Fill in toolbar search field with a site name");
        toolbar.searchInToolbar(siteName);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(siteName), "Live search result list contains the searched site.");

        log.info("STEP5: Click a site link from search results");
        toolbar.clickResult(siteName);

        log.info("STEP6: Fill in toolbar search field with a document name");
        toolbar.searchInToolbar(docName1);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(docName1), "Live search result list contains the searched document.");

        log.info("STEP7: Click a document link from search results");
        toolbar.clickResult(docName1);

        log.info("STEP8: Fill in toolbar search field with a wiki page");
        toolbar.searchInToolbar(wikiPage);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(wikiPage), "Live search result list contains the searched wiki page");

        log.info("STEP9: Fill in toolbar search field with a blog post");
        toolbar.searchInToolbar(blogPost);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(blogPost), "Live search result list contains the searched blog post");

        log.info("STEP10: Fill in toolbar search field with a calendar event");
        toolbar.searchInToolbar(calendarEvent);
        assertFalse(toolbar.is_LiveSearchResultsListDisplayed(calendarEvent), "No live search results are displayed");

        log.info("STEP11: Fill in toolbar search field with a discussion topic");
        toolbar.searchInToolbar(discussion);
        assertFalse(toolbar.is_LiveSearchResultsListDisplayed(discussion), "No live search results are displayed");

        log.info("STEP12: Fill in toolbar search field with a link");
        toolbar.searchInToolbar(link);
        assertFalse(toolbar.is_LiveSearchResultsListDisplayed(link), "No live search results are displayed");
    }

    @TestRail (id = "C5945")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifySearchPage()
    {
        log.info("Precondition: Creating random file in the site under document library");
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(siteModel.get()).createFile(testFile).assertThat().existsInRepo();
        authenticateUsingLoginPage(user1.get());

        log.info("STEP1: Enter the document name in the toolbar search field and press Enter");
        toolbar
            .search(testFile.getName());
        searchPage
            .assertBrowserPageTitleIs("Alfresco » Search");

        log.info("STEP2: Verify page title");
        searchPage
            .assertCreatedDataIsDisplayed(testFile.getName());

        log.info("STEP3: Verify search section");
        assertTrue(searchPage.isSearchInLabelDisplayed(), "'Search in' label is displayed.");
        assertTrue(searchPage.getNumberOfResultsText().contains(" - results found"), "Section with number of results is displayed");
        assertTrue(searchPage.isSearchButtonDisplayed(), "Search button is displayed.");

        log.info("STEP4: Click \"Search In\" dropdown");
        searchPage.clickSearchInDropdown();
        assertEquals(searchPage.getSearchInDropdownValues(), "All Sites, Repository", "'Search in' dropdown");
        assertEquals(searchPage.getSearchInDropdownSelectedValue(), "Repository", "'Search in' dropdown has default selected value:");

        log.info("STEP5: Verify \"Filter by\" section");
        ArrayList<String> expectedList = new ArrayList<>(Arrays.asList("Creator", "File Type", "Modifier", "Created", "Size", "Modified"));
        for (String anExpectedList : expectedList)
        {
            assertTrue(searchPage.getFilterTypeList().contains(anExpectedList), "Filter is not present in 'Filter by' section!");
        }

        log.info("STEP6: Click 'Sort' dropdown");
        searchPage.clickSortDropdown();
        assertTrue(searchPage.isSortDropdownComplete(),
            "Dropdown for results sorting is displayed with options: \"Relevance\", \"Name\", \"Title\", \"Description\", \"Author\", \"Modifier\", \"Modified date\", \"Creator\", \"Created date\", \"Size\", \"Mime type\" and \"Type\" ");

        log.info("STEP7: Click \"Views\" dropdown");
        searchPage.clickViewsDropdown();
        ArrayList<String> expectedViewsDropdown = new ArrayList<>(Arrays.asList("Detailed View", "Gallery View"));
        assertEquals(searchPage.getViewsDropdownOptions().toString(), expectedViewsDropdown.toString(), "Views dropdown option=");

        log.info("STEP8: Hover over a result");
        searchPage.mouseOverResult(testFile.getName());
        assertTrue(searchPage.isActionsLinkDisplayed(), "The \"Actions\" menu is displayed.");
    }

    @TestRail (id = "C7706")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifySearchInDropdownOptions()
    {
        log.info("Precondition: Creating random file in the site under document library");
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(siteModel.get()).createFile(testFile).assertThat().existsInRepo();
        authenticateUsingLoginPage(user1.get());

        String siteName = siteModel.get().getId();

        log.info("STEP1: Enter document name in the live search field and press Enter");
        siteDashboardPage
            .navigate(siteName);
        toolbar
            .search(testFile.getName());

        log.info("STEP2: Select the site name option from 'Search in' dropdown");
        searchPage
            .selectOptionFromSearchIn(siteName)
            .assertCreatedDataIsDisplayed(testFile.getName());

        log.info("STEP3: Select \"All Sites\" option from 'Search in' dropdown");
        searchPage
            .selectOptionFromSearchIn("All Sites")
            .assertCreatedDataIsDisplayed(testFile.getName());

        log.info("STEP4: Select \"Repository\" option from 'Search in' dropdown");
        searchPage
            .selectOptionFromSearchIn("Repository")
            .assertCreatedDataIsDisplayed(testFile.getName());

    }

    @TestRail (id = "C6168")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH  })
    public void defaultPlaceholderText()
    {
        authenticateUsingLoginPage(user1.get());
        log.info("STEP1: Verify Live Search placeholder text");
        assertEquals(toolbar.getSearchBoxPlaceholder(), language.translate("toolbar.searchInputPlaceholder"), "Live search box placeholder text");
    }

    @TestRail (id = "C8145")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH})
    public void verifyDetailedView() {

        log.info("Precondition: Creating random file in the site under document library");
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(siteModel.get()).createFile(testFile).assertThat().existsInRepo();

        authenticateUsingLoginPage(user1.get());

        log.info("STEP1: Enter the document name in the toolbar search field and press 'Enter'");
        toolbar.search(testFile.getName());
        searchPage.assertBrowserPageTitleIs("Alfresco » Search");

        log.info("STEP2: Verify the default view");
        toolbar.search(testFile.getName());
        searchPage.assertCreatedDataIsDisplayed(testFile.getName());
        assertTrue(searchPage.isSearchResultsListInDetailedView(), "Detailed view is displayed.");

        log.info("STEP3: Change the view to \"Gallery View\" from 'Views' dropdown");
        searchPage.clickGalleryView();
        assertTrue(searchPage.isSearchResultsListInGalleryView(), "Results are displayed in Gallery View");

        log.info("STEP4: Change the view to \"Detailed View\"");
        searchPage.clickDetailedView();
        assertTrue(searchPage.isSearchResultsListInDetailedView(), "Results are displayed in Detailed View.");
    }

    @TestRail (id = "C8146")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyGalleryView()
    {
        log.info("Precondition: Creating random file in the site under document library");
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(siteModel.get()).createFile(testFile).assertThat().existsInRepo();

        authenticateUsingLoginPage(user1.get());

        log.info("STEP1: Enter the document name in the toolbar search field and press Enter");
        toolbar.search(testFile.getName());
        searchPage.assertBrowserPageTitleIs("Alfresco » Search");
        searchPage.assertCreatedDataIsDisplayed(testFile.getName());

        log.info("STEP2: Change the view to \"Gallery View\" from 'Views' dropdown and verify results section");
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
    }

    @TestRail (id = "MNT-24838")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyPDFView()
    {
        log.info("Precondition: Creating random file in the site under document library");
        testFile = FileModel.getRandomFileModel(FileType.PDF);
        getCmisApi().usingSite(siteModel.get()).createFile(testFile).assertThat().existsInRepo();

        authenticateUsingLoginPage(user1.get());

        log.info("STEP1: Enter the document name in the toolbar search field and press Enter");
        toolbar.search(testFile.getName());
        searchPage.assertBrowserPageTitleIs("Alfresco » Search");
        searchPage.assertCreatedDataIsDisplayed(testFile.getName());

        log.info("Step 2: Check that the image is previewed from the search results");
        searchPage.clickContentThumbnailByName(testFile.getName());
        Assert.assertTrue(searchPage.isContentPreviewed(testFile.getName()), "File is previewed");
    }
}