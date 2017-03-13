package org.alfresco.share.searching;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.searching.*;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 8/16/2016.
 */
public class SearchManagerTests extends ContextAwareWebTest
{
    @Autowired
    AdvancedSearchPage advancedSearchPage;

    @Autowired
    SearchManagerPage searchManagerPage;

    @Autowired
    SearchPage searchPage;

    @Autowired
    CreateNewFilterPopup createNewFilterPopup;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    MyDocumentsDashlet myDocumentsDashlet;

    @Autowired
    Toolbar toolbar;

    @Autowired
    EditFilterPopup editFilterPopup;

    @Autowired
    ConfirmDeletionDialog confirmDeletionDialog;

    private String user1 = "user1" + DataUtil.getUniqueIdentifier();
    private String user2 = "testUser2" + DataUtil.getUniqueIdentifier();
    private String user3 = "testUser3" + DataUtil.getUniqueIdentifier();
    private String modifier1 = "firstName1 lastName1";
    private String modifier2 = "firstName2 lastName2";
    private String modifier3 = "firstName3 lastName3";
    private String groupName = "ALFRESCO_SEARCH_ADMINISTRATORS";
    private String site1 = "Site1" + DataUtil.getUniqueIdentifier();
    private String site2 = "Site2" + DataUtil.getUniqueIdentifier();
    private String site3 = "Site3" + DataUtil.getUniqueIdentifier();
    private String documentName = "Doc" + DataUtil.getUniqueIdentifier();
    private String filterId;
    private String filterName;

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, modifier1.split(" ")[0], modifier1.split(" ")[1]);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, modifier2.split(" ")[0], modifier2.split(" ")[1]);
        userService.create(adminUser, adminPassword, user3, password, user3 + domain, modifier3.split(" ")[0], modifier3.split(" ")[1]);
        groupService.addUserToGroup(adminUser, adminPassword, groupName, user1);

        siteService.create(user1, password, domain, site1, site1 + " description", Site.Visibility.PUBLIC);
        siteService.create(user1, password, domain, site2, site2 + " description", Site.Visibility.PUBLIC);
        siteService.create(user1, password, domain, site3, site3 + " description", Site.Visibility.PUBLIC);

        // site1 members
        userService.createSiteMember(user1, password, user2, site1, "SiteManager");
        userService.createSiteMember(user1, password, user3, site1, "SiteManager");

        // site1 documents
        contentService.createDocument(user1, password, site1, DocumentType.TEXT_PLAIN, documentName + "1", documentName + " content");
        contentService.createDocument(user1, password, site1, DocumentType.TEXT_PLAIN, documentName + "2", documentName + " content");
        contentService.createDocument(user2, password, site1, DocumentType.TEXT_PLAIN, documentName + "21", documentName + " content");
        contentService.createDocument(user2, password, site1, DocumentType.TEXT_PLAIN, documentName + "22", documentName + " content");
        contentService.createDocument(user2, password, site1, DocumentType.TEXT_PLAIN, documentName + "23", documentName + " content");
        contentService.createDocument(user3, password, site1, DocumentType.TEXT_PLAIN, documentName + "31", documentName + " content");
        contentAction.addSingleTag(user1, password, site1, documentName + "1", "tag1");
        contentAction.addSingleTag(user1, password, site1, documentName + "2", "tag2");

        // site2 documents
        contentService.createDocument(user1, password, site2, DocumentType.TEXT_PLAIN, documentName + "3", documentName + " content");
        contentService.createDocument(user1, password, site2, DocumentType.TEXT_PLAIN, documentName + "4", documentName + " content");
        contentAction.addSingleTag(user1, password, site2, documentName + "3", "tag3");
        contentAction.addSingleTag(user1, password, site2, documentName + "4", "tag4");

        // site3 documents
        contentService.createDocument(user1, password, site3, DocumentType.TEXT_PLAIN, documentName + "5", documentName + " content");
        contentService.createDocument(user1, password, site3, DocumentType.TEXT_PLAIN, documentName + "6", documentName + " content");
        contentAction.addSingleTag(user1, password, site3, documentName + "5", "tag5");
        contentAction.addSingleTag(user1, password, site3, documentName + "6", "tag6");

        setupAuthenticatedSession(user1, password);
        userDashboardPage.navigate(user1);
        myDocumentsDashlet.waitForDocument();
    }

    @TestRail(id = "C6274")
    @Test
    public void verifySearchManagerPage()
    {
        List<String> expectedTableColumns = Arrays.asList("Filter ID", "Filter Name", "Filter Property", "Filter Type", "Show with Search Results",
                "Default Filter", "Filter Availability");
        List<String> defaultFilters = Arrays.asList("faceted-search.facet-menu.facet.creator", "faceted-search.facet-menu.facet.formats",
                "faceted-search.facet-menu.facet.created", "faceted-search.facet-menu.facet.size", "faceted-search.facet-menu.facet.modifier",
                "faceted-search.facet-menu.facet.modified");

        LOG.info("Step 1: Open 'Advanced Search' page and click 'Search' button.");
        advancedSearchPage.navigate();
        advancedSearchPage.click1stSearch();
        assertTrue(searchPage.isSearchManagerDisplayed(), "'Search Manager' link is displayed on the page.");

        LOG.info("Step 2: Click on 'Search Manager' link.");
        searchPage.clickSearchManagerLink();
        assertTrue(searchManagerPage.isCreateNewFilterDisplayed(), "'Create New Filter' button");
        assertEquals(searchManagerPage.getFiltersTableColumns(), expectedTableColumns, "Filters table has columns: " + expectedTableColumns);

        LOG.info("Step 3: Verify the default filters available on 'Search Manager' page.");
        for (String filter : defaultFilters)
        {
            getBrowser().waitInSeconds(3);
            assertTrue(searchManagerPage.isFilterAvailable(filter), "The following default filter is available: " + filter);
        }
    }

    @TestRail(id = "C6275")
    @Test
    public void createNewSearchFilter()
    {
        filterId = "filter.site" + DataUtil.getUniqueIdentifier();
        filterName = "Site" + DataUtil.getUniqueIdentifier();

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        LOG.info("STEP 2: Add any 'Filter ID' and 'Filter Name'.");
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);

        LOG.info("STEP 3: Select any property from 'Filter Property' drop-down list (e.g.: 'Site').");
        createNewFilterPopup.selectFromFilterProperty("Site");

        LOG.info("STEP 4: Click 'Save' button.");
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        LOG.info("STEP 5: Open 'Advanced Search' page. Enter 'testFile' on 'Keywords' input field and click 'Search' button.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(documentName);
        advancedSearchPage.click1stSearch();

        LOG.info("STEP 6: Verify the new created filter.");
        assertTrue(searchPage.isFilterTypePresent(filterName), "The new filter ('Site') is displayed on 'Search Results' page, on 'Filter by' section");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, site1), site1 + " option is displayed under the Site filter.");
        assertEquals(searchPage.getFilterOptionHits(site1), "6", site1 + " has 6 hits.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, site2), site2 + " option is displayed under the Site filter.");
        assertEquals(searchPage.getFilterOptionHits(site2), "2", site2 + " has 2 hits.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, site3), site3 + " option is displayed under the Site filter.");
        assertEquals(searchPage.getFilterOptionHits(site3), "2", site3 + " has 2 hits.");

        LOG.info("STEP 7: Click on " + site1 + " option.");
        searchPage.clickFilterOption(site1, filterId);
        assertTrue(searchPage.isSearchResultsAsExpected(
                Arrays.asList(documentName + "1", documentName + "2", documentName + "21", documentName + "22", documentName + "23", documentName + "31")),
                "Only site1 files are displayed on the search results.");

        LOG.info("STEP 8: Click on " + site2 + " option.");
        getBrowser().navigate().back();
        searchPage.clickFilterOption(site2, filterId);
        assertTrue(searchPage.isSearchResultsAsExpected(Arrays.asList(documentName + "3", documentName + "4")),
                "Only site2 files are displayed on the search results.");

        LOG.info("STEP 9: Click on " + site3 + " option.");
        getBrowser().navigate().back();
        searchPage.clickFilterOption(site3, filterId);
        assertTrue(searchPage.isSearchResultsAsExpected(Arrays.asList(documentName + "5", documentName + "6")),
                "Only site 3 files are displayed on the search results.");
    }

    @TestRail(id = "C6283")
    @Test
    public void verifyFilterAvailabilityProperty()
    {
        filterId = "tag-filter" + DataUtil.getUniqueIdentifier();
        filterName = "tagFilter" + DataUtil.getUniqueIdentifier();

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        LOG.info("STEP 2: Add any 'Filter ID' and 'Filter Name'.");
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);

        LOG.info("STEP 3: Select 'Tag' property for 'Filter Property' field.");
        createNewFilterPopup.selectFromFilterProperty("Tag");

        LOG.info("STEP 4: Go to 'Filter Availability' field and select 'Selected sites' option from the drop-down.");
        createNewFilterPopup.selectFromFilterAvailability("Selected sites");

        LOG.info(
                "STEP 5: Click 'Add a new entry' button from 'Sites' section and select 'site1' from the drop-down list with available sites. Click 'Save the current entry' icon for 'site1'.");
        createNewFilterPopup.addSite(site1);

        LOG.info(
                "STEP 6: Click 'Add a new entry' button from 'Sites' section and select 'site2' from the drop-down list with available sites. Click 'Save the current entry' icon for 'site2'.");
        createNewFilterPopup.addSite(site2);

        LOG.info("STEP 7: Click 'Save' button.");
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        LOG.info("STEP 8: Go to site1's dashboard. Type '" + documentName + "' on the 'Search box' from 'Alfresco Toolbar' and press 'Enter' key.");
        siteDashboardPage.navigate(site1);
        toolbar.search(documentName);

        LOG.info("STEP 9: Select 'site1' option in 'Search in' filter.");
        searchPage.selectOptionFromSearchIn(site1);
        assertTrue(searchPage.isFilterTypePresent(filterName), "The new filter ('tagFilter') is displayed on 'Search Results' page, on 'Filter by' section");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, "tag1"), " tag1 option is displayed under the 'tagFilter' filter.");
        assertEquals(searchPage.getFilterOptionHits("tag1"), "1", "tag1 has 1 hit.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, "tag2"), " tag2 option is displayed under the 'tagFilter' filter.");
        assertEquals(searchPage.getFilterOptionHits("tag2"), "1", "tag2 has 1 hit.");

        LOG.info("STEP 10: Go to site2's dashboard. Type '" + documentName + "' on the 'Search box' from 'Alfresco Toolbar' and press 'Enter' key.");
        siteDashboardPage.navigate(site2);
        toolbar.search(documentName);

        LOG.info("STEP 11: Select 'site2' option in 'Search in' filter.");
        searchPage.selectOptionFromSearchIn(site2);
        assertTrue(searchPage.isFilterTypePresent(filterName), "The new filter ('tagFilter') is displayed on 'Search Results' page, on 'Filter by' section");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, "tag3"), " tag3 option is displayed under the 'tagFilter' filter.");
        assertEquals(searchPage.getFilterOptionHits("tag3"), "1", "tag3 has 1 hit.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, "tag4"), " tag4 option is displayed under the 'tagFilter' filter.");
        assertEquals(searchPage.getFilterOptionHits("tag4"), "1", "tag4 has 1 hit.");

        LOG.info("STEP 12: Go to site3's dashboard. Type '" + documentName + "' on the 'Search box' from 'Alfresco Toolbar' and press 'Enter' key.");
        siteDashboardPage.navigate(site3);
        toolbar.search(documentName);

        LOG.info("STEP 11: Select 'site1' option in 'Search in' filter.");
        searchPage.selectOptionFromSearchIn(site3);
        assertFalse(searchPage.isFilterTypePresent(filterName),
                "The new filter ('tagFilter') should not be displayed on 'Search Results' page, on 'Filter by' section");
    }

    @TestRail(id = "C6307")
    @Test
    public void verifyNumberOfFiltersProperty()
    {
        filterId = "filter_modifier";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click on the 'Filter ID' for any available filter from 'Search Manager' page (e.g.: 'filter_modifier').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");

        LOG.info("STEP 2: Go to the 'Number of Filters' field and select '2' value. Click 'Save' button.");
        editFilterPopup.typeNumberOfFilters("2");
        editFilterPopup.typeMinimumFilterLength("4"); // revert to default value
        editFilterPopup.typeMinimumRequiredResults("1");// revert to default value
        editFilterPopup.clickSave();

        LOG.info("STEP 3: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.search(documentName);

        LOG.info("STEP 4: Verify 'Modifier' filter from 'Filter by' section.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier1), modifier1 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier2), modifier2 + " option is displayed under the Modifier filter.");
        assertFalse(searchPage.isFilterOptionDisplayed(filterId, modifier3), modifier3 + " option is not displayed under the Modifier filter.");

        LOG.info("STEP 5: Click on 'Show More' link.");
        searchPage.clickShowMore();
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier1), modifier1 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier2), modifier2 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier3), modifier3 + " option is displayed under the Modifier filter.");

        LOG.info("STEP 6: Click on 'Show Fewer' link.");
        searchPage.clickShowFewer();
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier1), modifier1 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier2), modifier2 + " option is displayed under the Modifier filter.");
        assertFalse(searchPage.isFilterOptionDisplayed(filterId, modifier3), modifier3 + " option is not displayed under the Modifier filter.");
    }

    @TestRail(id = "C6308")
    @Test
    public void verifyMinimumFilterLengthProperty()
    {
        filterId = "filter_modifier";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click on the 'Filter ID' for any available filter from 'Search Manager' page (e.g.: 'filter_modifier').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");

        LOG.info("STEP 2: Go to the 'Minimum Filter Length' field and select '20' value. Click 'Save' button.");
        editFilterPopup.typeNumberOfFilters("5"); // revert to default value
        editFilterPopup.typeMinimumFilterLength("20");
        editFilterPopup.typeMinimumRequiredResults("1");// revert to default value
        editFilterPopup.clickSave();

        LOG.info("STEP 3: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.search(documentName);

        LOG.info("STEP 4: Verify 'Modifier' filter from 'Filter by' section.");
        assertFalse(searchPage.isFilterOptionDisplayed(filterId, modifier1), modifier1 + " option is not displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier2), modifier2 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier3), modifier3 + " option is displayed under the Modifier filter.");
    }

    @TestRail(id = "C6309")
    @Test
    public void verifyMinimumRequiredResultsProperty()
    {
        filterId = "filter_modifier";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click on the 'Filter ID' for any available filter from 'Search Manager' page (e.g.: 'filter_modifier').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");

        LOG.info("STEP 2: Go to the 'Minimum Required Results' field and select '3' value. Click 'Save' button.");
        editFilterPopup.typeNumberOfFilters("5"); // revert to default value
        editFilterPopup.typeMinimumFilterLength("4"); // revert to default value
        editFilterPopup.typeMinimumRequiredResults("3");
        editFilterPopup.clickSave();

        LOG.info("STEP 3: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.search(documentName);

        LOG.info("STEP 4: Verify 'Modifier' filter from 'Filter by' section.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier1), modifier1 + " option is not displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier2), modifier2 + " option is displayed under the Modifier filter.");
        assertFalse(searchPage.isFilterOptionDisplayed(filterId, modifier3), modifier3 + " option is not displayed under the Modifier filter.");
    }

    @TestRail(id = "C6288")
    @Test
    public void createNewSearchFilterWithoutSaving()
    {
        filterId = "close-filter" + DataUtil.getUniqueIdentifier();
        filterName = "close.filter" + DataUtil.getUniqueIdentifier();

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        LOG.info("STEP 2: Add any 'Filter ID' and 'Filter Name'.");
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);

        LOG.info("STEP 3: Select any property from 'Filter Property' drop-down list (e.g.: 'Site').");
        createNewFilterPopup.selectFromFilterProperty("Site");

        LOG.info("STEP 4: Click 'Close' (X) button.");
        createNewFilterPopup.clickClose();
        assertFalse(searchManagerPage.isFilterAvailable(filterName), "The new filter is not displayed on 'Search Manager' page.");
    }

    @TestRail(id = "C6287")
    @Test
    public void cancelCreatingNewSearchFilter()
    {
        filterId = "cancel-filter" + DataUtil.getUniqueIdentifier();
        filterName = "cancel.filter" + DataUtil.getUniqueIdentifier();

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        LOG.info("STEP 2: Add any 'Filter ID' and 'Filter Name'.");
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);

        LOG.info("STEP 3: Select any property from 'Filter Property' drop-down list (e.g.: 'Site').");
        createNewFilterPopup.selectFromFilterProperty("Site");

        LOG.info("STEP 4: Click 'Cancel' button.");
        createNewFilterPopup.clickCancel();
        assertFalse(searchManagerPage.isFilterAvailable(filterName), "The new filter is not displayed on 'Search Manager' page.");
    }

    @TestRail(id = "C6284")
    @Test
    public void modifyExistingSearchFilter()
    {
        filterId = "filterC6284" + DataUtil.getUniqueIdentifier();
        filterName = "testFilterC6284" + DataUtil.getUniqueIdentifier();
        String newFilterName = "newFilterC6284";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Create a new filter with following properties:\n" + "- Filter ID: filter1\n" + "- Filter Name: testFilter1\n"
                + "- Show with Search Results: Yes\n" + "- Filter Property: Site\n" + "- Sort By: A-Z\n" + "- Number of Filters: 10\n"
                + "- Minimum Filter Length: 1\n" + "- Minimum Required Results: 1\n" + "- Filter Availability: Everywhere");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), filterName, "Filter name is correct.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Site", "Filter property  has default value.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "Yes", "Filter Show  has default value.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Everywhere", "Filter availability has default value.");

        LOG.info("STEP 2: On 'Search Manager' page, click on the new filter's id ('filter1'). Modify the filter's properties as below:\n"
                + "- Filter Name: testFilter2\n" + "- Show with Search Results: No\n" + "- Filter Property: Tag\n" + "- Sort By: Z-A\n"
                + "- Number of Filters: 7\n" + "- Minimum Filter Length: 3\n" + "- Minimum Required Results: 3\n"
                + "- Filter Availability: Selected Sites (Sites: site1, site2)");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");
        editFilterPopup.typeFilterName(newFilterName);
        editFilterPopup.clickShowWithSearchResults();
        editFilterPopup.selectFromFilterProperty("Tag");
        editFilterPopup.selectFromSortBy("Z-A");
        editFilterPopup.typeNumberOfFilters("7");
        editFilterPopup.typeMinimumFilterLength("3");
        editFilterPopup.typeMinimumRequiredResults("3");
        createNewFilterPopup.selectFromFilterAvailability("Selected sites");
        createNewFilterPopup.addSite(site1);
        createNewFilterPopup.addSite(site2);

        LOG.info("STEP 3: Click 'Save' button.");
        editFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), newFilterName, "Filter name is changed.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Tag", "Filter property is changed.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "No", "Filter Show is changed.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Selected sites", "Filter availability is changed.");

        LOG.info("STEP 4: Click on the filter's id ('filter1').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");
        assertEquals(editFilterPopup.getFilterNameValue(), newFilterName, "Filter Name: testFilter2");
        assertFalse(editFilterPopup.isShowWithSearchResultsChecked(), "Show with Search Results: No");
        assertEquals(editFilterPopup.getSortBy(), "Z-A", "Sort By: Z-A");
        assertEquals(editFilterPopup.getNoFilters(), "7", "Number of Filters: 7");
        assertEquals(editFilterPopup.getMinimumFilterLength(), "3", "Minimum Filter Length: 3");
        assertEquals(editFilterPopup.getMinimumRequiredResults(), "3", "Minimum Required Results: 3");
        assertEquals(editFilterPopup.getFilterAvailability(), "Selected sites", "Filter Availability: Selected Sites");
        String[] expectedSelectedSites = { site1, site2 };
        assertEqualsNoOrder(editFilterPopup.getCurrentSelectedSites(), expectedSelectedSites, "Selected Sites (Sites: site1, site2)");
    }

    @TestRail(id = "C6314")
    @Test
    public void modifySearchFilterWithoutSaving()
    {
        filterId = "filterC6314" + DataUtil.getUniqueIdentifier();
        filterName = "testFilterC6314" + DataUtil.getUniqueIdentifier();
        String newFilterName = "newFilterC6314";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Create a new filter with following properties:\n" + "- Filter ID: filter1\n" + "- Filter Name: testFilter1\n"
                + "- Show with Search Results: Yes\n" + "- Filter Property: Site\n" + "- Sort By: A-Z\n" + "- Number of Filters: 10\n"
                + "- Minimum Filter Length: 1\n" + "- Minimum Required Results: 1\n" + "- Filter Availability: Everywhere");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), filterName, "Filter name is correct.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Site", "Filter property  has default value.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "Yes", "Filter Show  has default value.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Everywhere", "Filter availability has default value.");

        LOG.info("STEP 2: On 'Search Manager' page, click on the new filter's id ('filter1'). Modify the filter's properties as below:\n"
                + "- Filter Name: testFilter2\n" + "- Show with Search Results: No\n" + "- Filter Property: Tag\n" + "- Sort By: Z-A\n"
                + "- Number of Filters: 7\n" + "- Minimum Filter Length: 3\n" + "- Minimum Required Results: 3\n"
                + "- Filter Availability: Selected Sites (Sites: site1, site2)");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");
        editFilterPopup.typeFilterName(newFilterName);
        editFilterPopup.clickShowWithSearchResults();
        editFilterPopup.selectFromFilterProperty("Tag");
        editFilterPopup.selectFromSortBy("Z-A");
        editFilterPopup.typeNumberOfFilters("7");
        editFilterPopup.typeMinimumFilterLength("3");
        editFilterPopup.typeMinimumRequiredResults("3");
        createNewFilterPopup.selectFromFilterAvailability("Selected sites");
        createNewFilterPopup.addSite(site1);
        createNewFilterPopup.addSite(site2);

        LOG.info("STEP 3: Click 'Close' button.");
        editFilterPopup.clickClose();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), filterName, "Filter name hasn't changed.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Site", "Filter property hasn't changed.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "Yes", "Filter Show hasn't changed.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Everywhere", "Filter availability hasn't changed.");

        LOG.info("STEP 4: Click on the filter's id ('filter1').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");
        assertEquals(editFilterPopup.getFilterNameValue(), filterName, "Filter Name: " + filterName);
        assertTrue(editFilterPopup.isShowWithSearchResultsChecked(), "Show with Search Results: Yes");
        assertEquals(editFilterPopup.getSortBy(), "A-Z", "Sort By: A-Z");
        assertEquals(editFilterPopup.getNoFilters(), "10", "Number of Filters: 10");
        assertEquals(editFilterPopup.getMinimumFilterLength(), "1", "Minimum Filter Length: 1");
        assertEquals(editFilterPopup.getMinimumRequiredResults(), "1", "Minimum Required Results: 1");
        assertEquals(editFilterPopup.getFilterAvailability(), "Everywhere", "Filter Availability: Everywhere");
    }

    @TestRail(id = "C6299")
    @Test
    public void cancelModifyingExistingSearchFilter()
    {
        filterId = "filterC6299" + DataUtil.getUniqueIdentifier();
        filterName = "testFilterC6299" + DataUtil.getUniqueIdentifier();
        String newFilterName = "newFilterC6299";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Create a new filter with following properties:\n" + "- Filter ID: filter1\n" + "- Filter Name: testFilter1\n"
                + "- Show with Search Results: Yes\n" + "- Filter Property: Site\n" + "- Sort By: A-Z\n" + "- Number of Filters: 10\n"
                + "- Minimum Filter Length: 1\n" + "- Minimum Required Results: 1\n" + "- Filter Availability: Everywhere");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), filterName, "Filter name is correct.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Site", "Filter property  has default value.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "Yes", "Filter Show  has default value.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Everywhere", "Filter availability has default value.");

        LOG.info("STEP 2: On 'Search Manager' page, click on the new filter's id ('filter1'). Modify the filter's properties as below:\n"
                + "- Filter Name: testFilter2\n" + "- Show with Search Results: No\n" + "- Filter Property: Tag\n" + "- Sort By: Z-A\n"
                + "- Number of Filters: 7\n" + "- Minimum Filter Length: 3\n" + "- Minimum Required Results: 3\n"
                + "- Filter Availability: Selected Sites (Sites: site1, site2)");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");
        editFilterPopup.typeFilterName(newFilterName);
        editFilterPopup.clickShowWithSearchResults();
        editFilterPopup.selectFromFilterProperty("Tag");
        editFilterPopup.selectFromSortBy("Z-A");
        editFilterPopup.typeNumberOfFilters("7");
        editFilterPopup.typeMinimumFilterLength("3");
        editFilterPopup.typeMinimumRequiredResults("3");
        createNewFilterPopup.selectFromFilterAvailability("Selected sites");
        createNewFilterPopup.addSite(site1);
        createNewFilterPopup.addSite(site2);

        LOG.info("STEP 3: Click 'Cancel' button.");
        editFilterPopup.clickCancel();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), filterName, "Filter name hasn't changed.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Site", "Filter property hasn't changed.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "Yes", "Filter Show hasn't changed.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Everywhere", "Filter availability hasn't changed.");

        LOG.info("STEP 4: Click on the filter's id ('filter1').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");
        assertEquals(editFilterPopup.getFilterNameValue(), filterName, "Filter Name: " + filterName);
        assertTrue(editFilterPopup.isShowWithSearchResultsChecked(), "Show with Search Results: Yes");
        assertEquals(editFilterPopup.getSortBy(), "A-Z", "Sort By: A-Z");
        assertEquals(editFilterPopup.getNoFilters(), "10", "Number of Filters: 10");
        assertEquals(editFilterPopup.getMinimumFilterLength(), "1", "Minimum Filter Length: 1");
        assertEquals(editFilterPopup.getMinimumRequiredResults(), "1", "Minimum Required Results: 1");
        assertEquals(editFilterPopup.getFilterAvailability(), "Everywhere", "Filter Availability: Everywhere");
    }

    @TestRail(id = "C6286")
    @Test
    public void switchOnOffShowWithSearchResults()
    {
        filterId = "filter_creator";
        filterName = "Creator";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click on any filter ID for a default filter (e.g.: 'filter_creator').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");

        LOG.info("STEP 2: Uncheck 'Show with Search Results' check box.");
        editFilterPopup.clickShowWithSearchResults();

        LOG.info("STEP 3: Click 'Save' button.");
        editFilterPopup.clickSave();

        LOG.info("STEP 4: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.search(documentName);
        assertFalse(searchPage.isFilterTypePresent(filterName), "The 'Creator' filter is not displayed on 'Filter by:' section.");

        LOG.info("STEP 5: Open again 'Search Manager' page and click on the filter ID ('filter_creator').");
        searchManagerPage.navigate();
        searchManagerPage.clickFilterId(filterId);
        assertTrue(editFilterPopup.isDialogDisplayed(filterId), "The dialog box for editing the filter is opened.");

        LOG.info("STEP 6: Check 'Show with Search Results' check box.");
        editFilterPopup.clickShowWithSearchResults();

        LOG.info("STEP 7: Click 'Save' button.");
        editFilterPopup.clickSave();

        LOG.info("STEP 8: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.search(documentName);
        assertTrue(searchPage.isFilterTypePresent(filterName), "The 'Creator' filter is displayed on 'Filter by:' section.");
    }

    @TestRail(id = "C6311")
    @Test
    public void modifySearchFilterName()
    {
        filterId = "filterC6311" + DataUtil.getUniqueIdentifier();
        filterName = "testFilterC6311" + DataUtil.getUniqueIdentifier();
        String newFilterName = "newFilterC6311";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button and create any custom filter with '" + filterName + "' name.");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        LOG.info("STEP 2: Hover over the 'Filter Name' for the '" + filterName + "' filter. Click on 'Edit' icon and modify the filter name to '"
                + newFilterName + "'. Click 'Save' button.");
        searchManagerPage.editFilterName(filterName, newFilterName);
        assertTrue(searchManagerPage.isFilterAvailable(newFilterName), "The new filter is displayed on 'Search Manager' page.");
    }

    @TestRail(id = "C6312")
    @Test
    public void cancelModifyingSearchFilterName()
    {
        filterId = "filterC6312" + DataUtil.getUniqueIdentifier();
        filterName = "testFilterC6312" + DataUtil.getUniqueIdentifier();
        String newFilterName = "newFilterC6312";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button and create any custom filter with '" + filterName + "' name.");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        LOG.info("STEP 2: Hover over the 'Filter Name' for the '" + filterName + "' filter. Click on 'Edit' icon and modify the filter name to '"
                + newFilterName + "'. Click 'Save' button.");
        searchManagerPage.cancelEditFilterName(filterName, newFilterName);
        assertFalse(searchManagerPage.isFilterAvailable(newFilterName), "The new filter is displayed on 'Search Manager' page.");
    }

    @TestRail(id = "C6303")
    @Test
    public void deleteSearchFilter()
    {
        filterId = "filterC6303" + DataUtil.getUniqueIdentifier();
        filterName = "testFilterC6303" + DataUtil.getUniqueIdentifier();
        String defaultFilter = "filter_creator";

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button and create any custom filter with '" + filterName + "' name.");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        LOG.info("STEP 2: Try to delete any default filter available on 'Search Manager' page");
        assertFalse(searchManagerPage.isFilterDeletable(defaultFilter),
                "There is no 'Delete' button present for the filter, as default filters can't be deleted.");

        LOG.info("STEP 3: Click on 'Delete' button for the custom filter created.");
        searchManagerPage.deleteFilter(filterId);
        assertEquals(confirmDeletionDialog.getDialogTitle(), language.translate("confirmDeletion.title"), "Popup is displayed.");
        assertEquals(confirmDeletionDialog.getDialogMessage(), String.format(language.translate("confirmDeletion.message"), filterId),
                "Popup message is displayed.");
        LOG.info("STEP 4: Click 'Yes' button.");
        confirmDeletionDialog.clickOKButton();
        assertFalse(searchManagerPage.isFilterAvailable(filterId), "Filter is no longer listed on 'Search Manager' page.");
    }

    @TestRail(id = "C6305")
    @Test
    public void cancelDeletingSearchFilter()
    {
        filterId = "filterC6305" + DataUtil.getUniqueIdentifier();
        filterName = "testFilterC6305" + DataUtil.getUniqueIdentifier();

        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button and create any custom filter with '" + filterName + "' name.");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        LOG.info("STEP 2: Click on 'Delete' button for the custom filter created.");
        searchManagerPage.deleteFilter(filterId);
        assertEquals(confirmDeletionDialog.getDialogTitle(), language.translate("confirmDeletion.title"), "Popup is displayed.");
        assertEquals(confirmDeletionDialog.getDialogMessage(), String.format(language.translate("confirmDeletion.message"), filterId),
                "Popup message is displayed.");

        LOG.info("STEP 3: Click 'No' button.");
        confirmDeletionDialog.clickNoButton();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "Filter is still listed on 'Search Manager' page.");

        LOG.info("STEP 4: Click on 'Delete' button for the custom filter created.");
        searchManagerPage.deleteFilter(filterId);
        assertEquals(confirmDeletionDialog.getDialogTitle(), language.translate("confirmDeletion.title"), "Popup is displayed.");
        assertEquals(confirmDeletionDialog.getDialogMessage(), String.format(language.translate("confirmDeletion.message"), filterId),
                "Popup message is displayed.");

        LOG.info("STEP 5: Click 'Close' button.");
        confirmDeletionDialog.clickCloseButton();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "Filter is still listed on 'Search Manager' page.");
    }

    @TestRail(id = "C6285")
    @Test
    public void changeSearchFiltersOrder()
    {
        filterId = "filter_creator";
        filterName = "Creator";
        String filterId2 = "filter_modifier";
        String filterName2 = "Modifier";
        boolean result = false;

        searchManagerPage.navigate();

        LOG.info("STEP 1: Hover over 'Move-Up' icon for any existing filter.");
        assertEquals(searchManagerPage.getUpTooltipForFilter(filterId), String.format(language.translate("searchManager.reorder.up"), filterId), "Up tooltip");

        LOG.info("STEP 2: Hover over 'Move-Down' icon for any existing filter.");
        assertEquals(searchManagerPage.getDownTooltipForFilter(filterId), String.format(language.translate("searchManager.reorder.down"), filterId),
                "Down tooltip");

        LOG.info("STEP 3: Move up 'filter_modifier' and move down 'filter_created'");
        int filterIdPosition = searchManagerPage.getFilterPosition(filterId);
        int filterId2Position = searchManagerPage.getFilterPosition(filterId2);
        if(filterIdPosition < filterId2Position)
        {
        	searchManagerPage.moveFilterUp(filterId2);
        	filterId2Position--;
        	while(filterIdPosition < filterId2Position)
        	{
        		searchManagerPage.moveFilterDown(filterId);
        		filterIdPosition ++;
        	}
        }
        else
        {
        	searchManagerPage.moveFilterDown(filterId2);
        	filterId2Position++;
        	while(filterIdPosition > filterId2Position)
        	{
        		searchManagerPage.moveFilterUp(filterId);
        		filterIdPosition --;
        	}
        	result = true;
        }

        filterIdPosition = searchManagerPage.getFilterPosition(filterId);
        filterId2Position = searchManagerPage.getFilterPosition(filterId2);
        assertEquals(searchManagerPage.getFilterPosition(filterId), filterIdPosition, "Filter " + filterId + " should be on a different row!");
        assertEquals(searchManagerPage.getFilterPosition(filterId2), filterId2Position, "Filter " + filterId2 + " should be on a different row!");

        LOG.info("STEP 4: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.renderedPage();
        toolbar.search(documentName);
        if(result == true)
        {
        	assertTrue(searchPage.getFilterTypePosition(filterName) < searchPage.getFilterTypePosition(filterName2), "The 'Created' filter should be on top of the 'Modifier' filter.");
        }
        else
        {
        	assertTrue(searchPage.getFilterTypePosition(filterName) > searchPage.getFilterTypePosition(filterName2), "The 'Modifier' filter should be on top of the 'Created' filter.");
        }
        
    }

    @TestRail(id = "C6313")
    @Test
    public void verifySitesSection()
    {
        searchManagerPage.navigate();

        LOG.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        LOG.info("STEP 2: Go to 'Filter Availability' field and select 'Selected sites' option from the drop-down.");
        createNewFilterPopup.selectFromFilterAvailability("Selected sites");

        LOG.info(
                "STEPS 3-5: Click 'Add a new entry' button from 'Sites' section and select 'site1' from the drop-down list with available sites. Click 'Save the current entry' icon for 'site1'.");
        createNewFilterPopup.addSite(site1);
        assertEquals(createNewFilterPopup.getCurrentSelectedSites()[0], site1, "'site1' is successfully added to 'Sites' section. ");

        LOG.info(
                "STEPS 6-7: Click on 'Edit the current entry' icon () for 'site1' and choose another site from the drop-down list (e.g.: 'site2'). Click 'Save the current entry' icon for 'site1'.");
        createNewFilterPopup.editSite(site1, site2);
        assertEquals(createNewFilterPopup.getCurrentSelectedSites()[0], site2, "'site2' is successfully added to 'Sites' section (instead of 'site1').");

        LOG.info(
                "STEPS 8-9: Click on 'Edit the current entry' icon () for 'site2' and choose another site from the drop-down list (e.g.: 'site3'). Click 'Cancel editing the current entry' icon");
        createNewFilterPopup.cancelEditSite(site2, site3);
        assertEquals(createNewFilterPopup.getCurrentSelectedSites()[0], site2, "'site2' is successfully added to 'Sites' section (instead of 'site1').");

        LOG.info("STEP 10: Click 'Delete the current entry' icon () for 'site2'");
        createNewFilterPopup.deleteSite(site2);
        assertEquals(createNewFilterPopup.getCurrentSelectedSites().length, 0, "'site2' is removed from 'Sites' section.");

        LOG.info(
                "STEPS 11-12: Click 'Add a new entry' button from 'Sites' section and select 'site3' from the drop-down list with available sites. Click 'Cancel editing the current entry' icon");
        createNewFilterPopup.cancelAddSite(site3);
        assertEquals(createNewFilterPopup.getCurrentSelectedSites().length, 0, "'site3' is not added to 'Sites' section.");
    }
    
    @AfterClass(alwaysRun=true)
    public void deleteFilters()
    {
    	searchManagerPage.navigate();
    	searchManagerPage.deleteAllNonDefaultFilters();
    }
    
}
