package org.alfresco.share.searching;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.ConfirmDeletionDialog;
import org.alfresco.po.share.searching.CreateNewFilterDialog;
import org.alfresco.po.share.searching.SearchManagerPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.AddUserDialog;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.EditUserPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UserProfileAdminToolsPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j

public class SearchManagerTests extends BaseTest
{
    //@Autowired
    AdvancedSearchPage advancedSearchPage;
    // @Autowired
    SearchManagerPage searchManagerPage;
    SiteMembersPage siteMembersPage;
    AddUserDialog addUserDialog;
    //@Autowired
    SearchPage searchPage;
    //@Autowired
    CreateNewFilterDialog createNewFilterPopup;
    //@Autowired
    SiteDashboardPage siteDashboardPage;
    DocumentLibraryPage documentLibraryPage;
    DocumentDetailsPage documentDetailsPage;
    UserProfilePage userProfilePage;
    CreateContentPage createContent;
    EditPropertiesPage editPropertiesPage;
    UserProfileAdminToolsPage userProfileAdminToolsPage;
    //@Autowired
    UserDashboardPage userDashboardPage;
    //@Autowired
    MyDocumentsDashlet myDocumentsDashlet;
    EditUserPage editUserPage;
    //@Autowired
    Toolbar toolbar;
    //@Autowired
    ConfirmDeletionDialog confirmDeletionDialog;

    private UserModel testUser1;
    private UserModel testUser2;
    private UserModel testUser3;
    private SiteModel testSite1;
    private SiteModel testSite2;
    private SiteModel testSite3;
    private String modifier1;
    private String modifier2;
    private String modifier3;
    private String firstName1 = "firstName1";
    private String lastName1 = "lastName1";
    private String firstName2 = "firstName2";
    private String lastName2 = "lastName2";
    private String firstName3 = "firstName3";
    private String lastName3 = "lastName3";
    private String groupName = "ALFRESCO_SEARCH_ADMINISTRATORS";
    private String documentName = String.format("Doc%s", RandomData.getRandomAlphanumeric());
    private String filterId;
    private String filterName;
    @Autowired
    protected UserService userService;
    @Autowired
    protected ContentActions contentAction;

    @BeforeMethod (alwaysRun = true)
    public void setupTest(){

        log.info("Step 1: user creation using admin user.");
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        testUser2 = dataUser.usingAdmin().createRandomTestUser();
        testUser3 = dataUser.usingAdmin().createRandomTestUser();

        editUserPage = new EditUserPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        siteMembersPage = new SiteMembersPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        userProfilePage = new UserProfilePage(webDriver);
        advancedSearchPage = new AdvancedSearchPage(webDriver);
        confirmDeletionDialog = new ConfirmDeletionDialog(webDriver);
        createNewFilterPopup = new CreateNewFilterDialog(webDriver);
        addUserDialog = new AddUserDialog(webDriver);
        editPropertiesPage = new EditPropertiesPage(webDriver);
        searchManagerPage = new SearchManagerPage(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        myDocumentsDashlet = new MyDocumentsDashlet(webDriver);
        createContent = new CreateContentPage(webDriver);
        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
        userProfileAdminToolsPage = new UserProfileAdminToolsPage(webDriver);

        log.info("Step 2: Editing all the three users and also adding user1 to ALFRESCO_SEARCH_ADMINISTRATORS GROUP");
        UserModel editUser1 = testUser1;
        editUserPage.navigate(editUser1).editFirstName(firstName1).editLastNameField(lastName1).addGroup(groupName).clickSaveChanges();
        UserModel editUser2 = testUser2;
        editUserPage.navigate(editUser2).editFirstName(firstName2).editLastNameField(lastName2).clickSaveChanges();
        UserModel editUser3 = testUser3;
        editUserPage.navigate(editUser3).editFirstName(firstName3).editLastNameField(lastName3).clickSaveChanges();
        authenticateUsingLoginPage(editUser1);

        log.info("Step 3: Creating three different data site using user1 ");
        testSite1 = dataSite.usingUser(editUser1).createPublicRandomSite();
        testSite2 = dataSite.usingUser(editUser1).createPublicRandomSite();
        testSite3 = dataSite.usingUser(editUser1).createPublicRandomSite();

        // site1 members
        userService.createSiteMember(editUser1.getUsername(), editUser1.getPassword(), editUser2.getUsername(), testSite1.getId(), "SiteManager");
        userService.createSiteMember(editUser1.getUsername(), editUser1.getPassword(), editUser3.getUsername(), testSite1.getId(), "SiteManager");

        // site1 documents
        contentService.createDocument(editUser1.getUsername(), editUser1.getPassword(), testSite1.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "1", documentName + " content");
        contentService.createDocument(editUser1.getUsername(), editUser1.getPassword(), testSite1.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "2", documentName + " content");
        contentService.createDocument(editUser2.getUsername(), editUser2.getPassword(), testSite1.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "21", documentName + " content");
        contentService.createDocument(editUser2.getUsername(), editUser2.getPassword(), testSite1.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "22", documentName + " content");
        contentService.createDocument(editUser2.getUsername(), editUser2.getPassword(), testSite1.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "23", documentName + " content");
        contentService.createDocument(editUser3.getUsername(), editUser3.getPassword(), testSite1.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "31", documentName + " content");
        contentAction.addSingleTag(editUser1.getUsername(), editUser1.getPassword(), testSite1.getId(), documentName + "1", "tag1");
        contentAction.addSingleTag(editUser1.getUsername(), editUser1.getPassword(), testSite1.getId(), documentName + "2", "tag2");

        // site2 documents
        contentService.createDocument(editUser1.getUsername(), editUser1.getPassword(), testSite2.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "3", documentName + " content");
        contentService.createDocument(editUser1.getUsername(), editUser1.getPassword(), testSite2.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "4", documentName + " content");
        contentAction.addSingleTag(editUser1.getUsername(), editUser1.getPassword(), testSite2.getId(), documentName + "3", "tag3");
        contentAction.addSingleTag(editUser1.getUsername(), editUser1.getPassword(), testSite2.getId(), documentName + "4", "tag4");

        // site3 documents
        contentService.createDocument(editUser1.getUsername(), editUser1.getPassword(), testSite3.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "5", documentName + " content");
        contentService.createDocument(editUser1.getUsername(), editUser1.getPassword(), testSite3.getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName + "6", documentName + " content");
        contentAction.addSingleTag(editUser1.getUsername(), editUser1.getPassword(), testSite3.getId(), documentName + "5", "tag5");
        contentAction.addSingleTag(editUser1.getUsername(), editUser1.getPassword(), testSite3.getId(), documentName + "6", "tag6");

        authenticateUsingLoginPage(editUser1);
        userDashboardPage.navigate(editUser1);

    }

    @AfterMethod
    public void removeAddedFiles()
    {
        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);
        deleteUsersIfNotNull(testUser3);
        deleteSitesIfNotNull(testSite1);
        deleteSitesIfNotNull(testSite2);
        deleteSitesIfNotNull(testSite3);

    }


    @TestRail (id = "C6274")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 3)
    public void verifySearchManagerPage() {
        List<String> expectedTableColumns = Arrays.asList("Filter ID", "Filter Name", "Filter Property", "Filter Type", "Show with Search Results",
            "Default Filter", "Filter Availability");
        List<String> defaultFilters = Arrays.asList("faceted-search.facet-menu.facet.creator", "faceted-search.facet-menu.facet.formats",
            "faceted-search.facet-menu.facet.created", "faceted-search.facet-menu.facet.size", "faceted-search.facet-menu.facet.modifier",
            "faceted-search.facet-menu.facet.modified");

        log.info("Step 1: Open 'Advanced Search' page and click 'Search' button.");
        advancedSearchPage.navigate();
        advancedSearchPage.clickOnFirstSearchButtons();

        assertTrue(searchPage.SearchManagerButtonDisplayed());

        log.info("Step 2: Click on 'Search Manager' link.");
        searchPage.clickSearchManagerLink();
        assertTrue(searchManagerPage.isCreateNewFilterDisplayed(), "'Create New Filter' button");
        assertEquals(searchManagerPage.getFiltersTableColumns(), expectedTableColumns, "Filters table has columns: " + expectedTableColumns);

        log.info("Step 3: Verify the default filters available on 'Search Manager' page.");
        for (String filter : defaultFilters)
        {
            assertTrue(searchManagerPage.isFilterAvailable(filter), "The following default filter is available: " + filter);
        }
    }

    @Bug (id = "ACE-5698")
    @TestRail (id = "C6275")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, enabled = false)
    public void createNewSearchFilter()
    {
        filterId = String.format("filter.site%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("Site%s", RandomData.getRandomAlphanumeric());

        searchManagerPage.navigate();

        log.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        log.info("STEP 2: Add any 'Filter ID' and 'Filter Name'.");
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);

        log.info("STEP 3: Select any property from 'Filter Property' drop-down list (e.g.: 'Site').");
        createNewFilterPopup.selectFromFilterProperty("Site");

        log.info("STEP 4: Click 'Save' button.");
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        log.info("STEP 5: Open 'Advanced Search' page. Enter 'testFile' on 'Keywords' input field and click 'Search' button.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(documentName);
        advancedSearchPage.clickFirstSearchButton();

        log.info("STEP 6: Verify the new created filter.");
        assertTrue(searchPage.isFilterTypePresent(filterName), "The new filter ('Site') is displayed on 'Search Results' page, on 'Filter by' section");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, testSite1.getTitle()), testSite1 + " option is displayed under the Site filter.");
        assertEquals(searchPage.getFilterOptionHits(testSite1.getTitle()), "6", testSite1 + " has 6 hits.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, testSite2.getTitle()), testSite2 + " option is displayed under the Site filter.");
        assertEquals(searchPage.getFilterOptionHits(testSite2.getTitle()), "2", testSite2 + " has 2 hits.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, testSite3.getTitle()), testSite3 + " option is displayed under the Site filter.");
        assertEquals(searchPage.getFilterOptionHits(testSite3.getTitle()), "2", testSite3 + " has 2 hits.");

        log.info("STEP 7: Click on " + testSite1 + " option.");
        searchPage.clickFilterOption(testSite1.getTitle(), filterId);
        assertTrue(searchPage.isSearchResultsAsExpected(
                Arrays.asList(documentName + "1", documentName + "2", documentName + "21", documentName + "22", documentName + "23", documentName + "31")),
            "Only site1 files are displayed on the search results.");

        log.info("STEP 8: Click on " + testSite2 + " option.");
        // getBrowser().navigate().back();
        searchPage.clickFilterOption(testSite2.getTitle(), filterId);
        assertTrue(searchPage.isSearchResultsAsExpected(Arrays.asList(documentName + "3", documentName + "4")),
            "Only site2 files are displayed on the search results.");

        log.info("STEP 9: Click on " + testSite3 + " option.");
        //  getBrowser().navigate().back();
        searchPage.clickFilterOption(testSite3.getTitle(), filterId);
        assertTrue(searchPage.isSearchResultsAsExpected(Arrays.asList(documentName + "5", documentName + "6")),
            "Only site 3 files are displayed on the search results.");
    }

    @TestRail (id = "C6283")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE, "SearchTests" }, priority = 4)
    public void verifyFilterAvailabilityProperty() throws InterruptedException {
        filterId = String.format("tag-filter%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("tagFilter%s", RandomData.getRandomAlphanumeric());

        searchManagerPage.navigate();

        log.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        log.info("STEP 2: Add any 'Filter ID' and 'Filter Name'.");
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);

        log.info("STEP 3: Select 'Tag' property for 'Filter Property' field.");
        createNewFilterPopup.selectFromFilterProperty("Tag");

        log.info("STEP 4: Go to 'Filter Availability' field and select 'Selected sites' option from the drop-down.");
        createNewFilterPopup.selectInFilterAvailability("Selected sites");

        log.info(
            "STEP 5: Click 'Add a new entry' button from 'Sites' section and select 'site1' from the drop-down list with available sites. Click 'Save the current entry' icon for 'site1'.");
        createNewFilterPopup.addSite(testSite1.getTitle());

        log.info(
            "STEP 6: Click 'Add a new entry' button from 'Sites' section and select 'site2' from the drop-down list with available sites. Click 'Save the current entry' icon for 'site2'.");
        createNewFilterPopup.addSite(testSite2.getTitle());

        log.info("STEP 7: Click 'Save' button.");
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        log.info("STEP 8: Go to site1's dashboard. Type '" + documentName + "' on the 'Search box' from 'Alfresco Toolbar' and press 'Enter' key.");
        siteDashboardPage.navigate(testSite1);
        toolbar.search(documentName);

        log.info("STEP 9: Select 'site1' option in 'Search in' filter.");
        searchPage.selectOptionFromSearchIn(testSite1.getTitle());
        assertTrue(searchPage.isFilterTypePresent(filterName), "The new filter ('tagFilter') is displayed on 'Search Results' page, on 'Filter by' section");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, "tag1"), " tag1 option is displayed under the 'tagFilter' filter.");

        assertEquals(searchPage.getFilterOptionHits("tag1"), "1", "tag1 has 1 hit.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, "tag2"), " tag2 option is displayed under the 'tagFilter' filter.");
        assertEquals(searchPage.getFilterOptionHits("tag2"), "1", "tag2 has 1 hit.");


        log.info("STEP 10: Go to site2's dashboard. Type '" + documentName + "' on the 'Search box' from 'Alfresco Toolbar' and press 'Enter' key.");
        siteDashboardPage.navigate(testSite2);
        toolbar.search(documentName);

        log.info("STEP 11: Select 'site2' option in 'Search in' filter.");
        searchPage.selectOptionFromSearchIn(testSite2.getTitle());
        assertTrue(searchPage.isFilterPresent(filterName), "The new filter ('tagFilter') is displayed on 'Search Results' page, on 'Filter by' section");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, "tag3"), " tag3 option is displayed under the 'tagFilter' filter.");


        assertEquals(searchPage.getFilterOptionHits("tag3"), "1", "tag3 has 1 hit.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, "tag4"), " tag4 option is displayed under the 'tagFilter' filter.");
        assertEquals(searchPage.getFilterOptionHits("tag4"), "1", "tag4 has 1 hit.");

        log.info("STEP 12: Go to site3's dashboard. Type '" + documentName + "' on the 'Search box' from 'Alfresco Toolbar' and press 'Enter' key.");
        siteDashboardPage.navigate(testSite3);
        toolbar.search(documentName);

        log.info("STEP 11: Select 'site1' option in 'Search in' filter.");
        searchPage.selectOptionFromSearchIn(testSite3.getTitle());
        assertFalse(searchPage.isFilterPresent(filterName),
            "The new filter ('tagFilter') should not be displayed on 'Search Results' page, on 'Filter by' section");
    }

    @TestRail (id = "C6307")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SearchTests", TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 5)
    public void verifyNumberOfFiltersProperty() {
        modifier1 = firstName1+" "+lastName1;
        modifier2 = firstName2+" "+lastName2;
        modifier3 = firstName3+" "+lastName3;
        filterId = "filter_modifier";

        searchManagerPage.navigate();

        log.info("STEP 1: Click on the 'Filter ID' for any available filter from 'Search Manager' page (e.g.: 'filter_modifier').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");
        log.info("STEP 2: Go to the 'Number of Filters' field and select '2' value. Click 'Save' button.");
        createNewFilterPopup.typeNumberOfFilters("2");
        createNewFilterPopup.typeMinimumFilterLength("4");
        createNewFilterPopup.typeMinimumRequiredResults("1");
        createNewFilterPopup.clickSave();

        log.info("STEP 3: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.searchAndEnterAgain(documentName);

        log.info("STEP 4: Verify 'Modifier' filter from 'Filter by' section.");
        assertTrue(searchPage.isTheFilterOptionVisible(modifier1), modifier1 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isTheFilterOptionVisible(modifier2), modifier2 + " option is displayed under the Modifier filter.");

        log.info("STEP 5: Click on 'Show More' link.");
        searchPage.clickShowMore();
        assertTrue(searchPage.isTheFilterOptionVisible(modifier1), modifier1 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isTheFilterOptionVisible(modifier2), modifier2 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isTheFilterOptionVisible(modifier3), modifier3 + " option is displayed under the Modifier filter.");

        log.info("STEP 6: Click on 'Show Fewer' link.");
        searchPage.clickShowFewer();
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier1), modifier1 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier2), modifier2 + " option is displayed under the Modifier filter.");
    }


    @TestRail (id = "C6308")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE })
    public void verifyMinimumFilterLengthProperty() {
        //test skipped due to edge case where minimufilterlengthproperty does not work, but no bug was opened
        filterId = "filter_modifier";

        searchManagerPage.navigate();

        log.info("STEP 1: Click on the 'Filter ID' for any available filter from 'Search Manager' page (e.g.: 'filter_modifier').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");

        log.info("STEP 2: Go to the 'Minimum Filter Length' field and select '20' value. Click 'Save' button.");
        createNewFilterPopup.typeNumberOfFilters("5"); // revert to default value
        createNewFilterPopup.typeMinimumFilterLength("20");
        createNewFilterPopup.typeMinimumRequiredResults("1");// revert to default value
        createNewFilterPopup.clickSave();

        log.info("STEP 3: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.search(documentName);

        log.info("STEP 4: Verify 'Modifier' filter from 'Filter by' section.");

        assertFalse(searchPage.isFilterOptionDisplayed(filterId, modifier1), modifier1 + " option is not displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier2), modifier2 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier3), modifier3 + " option is displayed under the Modifier filter.");
    }

    @TestRail (id = "C6309")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SearchTests", TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 6)
    public void verifyMinimumRequiredResultsProperty() throws InterruptedException {
        modifier1 = firstName1+" "+lastName1;
        modifier2 = firstName2+" "+lastName2;

        filterId = "filter_modifier";

        searchManagerPage.navigate();

        log.info("STEP 1: Click on the 'Filter ID' for any available filter from 'Search Manager' page (e.g.: 'filter_modifier').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");

        log.info("STEP 2: Go to the 'Minimum Required Results' field and select '3' value. Click 'Save' button.");
        createNewFilterPopup.typeNumberOfFilters("5"); // revert to default value
        createNewFilterPopup.typeMinimumFilterLength("4"); // revert to default value
        createNewFilterPopup.typeMinimumRequiredResults("3");
        createNewFilterPopup.clickSave();

        log.info("STEP 3: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.searchAndEnterAgain(documentName);

        log.info("STEP 4: Verify 'Modifier' filter from 'Filter by' section.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier1), modifier1 + " option is displayed under the Modifier filter.");
        assertTrue(searchPage.isFilterOptionDisplayed(filterId, modifier2), modifier1 + " option is displayed under the Modifier filter.");
    }

    @TestRail (id = "C6288")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 7)
    public void createNewSearchFilterWithoutSaving()
    {
        filterId = String.format("close-filter%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("close.filter%s", RandomData.getRandomAlphanumeric());

        searchManagerPage.navigate();

        log.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        log.info("STEP 2: Add any 'Filter ID' and 'Filter Name'.");
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);

        log.info("STEP 3: Select any property from 'Filter Property' drop-down list (e.g.: 'Site').");
        createNewFilterPopup.selectFromFilterProperty("Site");

        log.info("STEP 4: Click 'Close' (X) button.");
        createNewFilterPopup.clickClose();
        assertFalse(searchManagerPage.isFilterAvailable(filterName), "The new filter is not displayed on 'Search Manager' page.");
    }

    @TestRail (id = "C6287")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 8)
    public void cancelCreatingNewSearchFilter()
    {
        filterId = String.format("cancel-filter%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("cancel.filter%s", RandomData.getRandomAlphanumeric());

        searchManagerPage.navigate();

        log.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();

        log.info("STEP 2: Add any 'Filter ID' and 'Filter Name'.");
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);

        log.info("STEP 3: Select any property from 'Filter Property' drop-down list (e.g.: 'Site').");
        createNewFilterPopup.selectFromFilterProperty("Site");

        log.info("STEP 4: Click 'Cancel' button.");
        createNewFilterPopup.clickCancel();
        assertFalse(searchManagerPage.isFilterAvailable(filterName), "The new filter is not displayed on 'Search Manager' page.");
    }

    @TestRail (id = "C6284")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 9)
    public void modifyExistingSearchFilter()
    {
        filterId = String.format("filterC6284%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("testFilterC6284%s", RandomData.getRandomAlphanumeric());
        String newFilterName = "newFilterC6284";

        searchManagerPage.navigate();

        log.info("STEP 1: Create a new filter with following properties:\n" + "- Filter ID: filter1\n" + "- Filter Name: testFilter1\n"
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

        log.info("STEP 2: On 'Search Manager' page, click on the new filter's id ('filter1'). Modify the filter's properties as below:\n"
            + "- Filter Name: testFilter2\n" + "- Show with Search Results: No\n" + "- Filter Property: Tag\n" + "- Sort By: Z-A\n"
            + "- Number of Filters: 7\n" + "- Minimum Filter Length: 3\n" + "- Minimum Required Results: 3\n"
            + "- Filter Availability: Selected Sites (Sites: site1, site2)");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");
        createNewFilterPopup.typeFilterName(newFilterName);
        createNewFilterPopup.deselectShowWithSearchResults();
        createNewFilterPopup.selectFromFilterProperty("Tag");
        createNewFilterPopup.selectFromSortBy("Z-A");
        createNewFilterPopup.typeNumberOfFilters("7");
        createNewFilterPopup.typeMinimumFilterLength("3");
        createNewFilterPopup.typeMinimumRequiredResults("3");
        createNewFilterPopup.selectInFilterAvailability("Selected sites");
        createNewFilterPopup.addSite(testSite1.getTitle());
        createNewFilterPopup.addSite(testSite2.getTitle());

        log.info("STEP 3: Click 'Save' button.");
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), newFilterName, "Filter name is changed.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Tag", "Filter property is changed.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "No", "Filter Show is changed.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Selected sites", "Filter availability is changed.");

        log.info("STEP 4: Click on the filter's id ('filter1').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");
        assertEquals(createNewFilterPopup.getFilterNameValue(), newFilterName, "Filter Name: testFilter2");
        assertFalse(createNewFilterPopup.isShowWithSearchResultsChecked(), "Show with Search Results: No");
        assertEquals(createNewFilterPopup.getSortBy(), "Z-A", "Sort By: Z-A");
        assertEquals(createNewFilterPopup.getNumberOfFilters(), "7", "Number of Filters: 7");
        assertEquals(createNewFilterPopup.getMiniFilterLength(), "3", "Minimum Filter Length: 3");
        assertEquals(createNewFilterPopup.getMiniRequiredResults(), "3", "Minimum Required Results: 3");
        assertEquals(createNewFilterPopup.getFilterAvailability(), "Selected sites", "Filter Availability: Selected Sites");
    }

    @TestRail (id = "C6314")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 10)
    public void modifySearchFilterWithoutSaving()
    {
        filterId = String.format("filterC6314%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("testFilterC6314%s", RandomData.getRandomAlphanumeric());
        String newFilterName = "newFilterC6314";

        searchManagerPage.navigate();

        log.info("STEP 1: Create a new filter with following properties:\n" + "- Filter ID: filter1\n" + "- Filter Name: testFilter1\n"
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

        log.info("STEP 2: On 'Search Manager' page, click on the new filter's id ('filter1'). Modify the filter's properties as below:\n"
            + "- Filter Name: testFilter2\n" + "- Show with Search Results: No\n" + "- Filter Property: Tag\n" + "- Sort By: Z-A\n"
            + "- Number of Filters: 7\n" + "- Minimum Filter Length: 3\n" + "- Minimum Required Results: 3\n"
            + "- Filter Availability: Selected Sites (Sites: site1, site2)");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");
        createNewFilterPopup.typeFilterName(newFilterName);
        createNewFilterPopup.clickShowWithSearchResults();
        createNewFilterPopup.selectFromFilterProperty("Tag");
        createNewFilterPopup.selectFromSortBy("Z-A");
        createNewFilterPopup.typeNumberOfFilters("7");
        createNewFilterPopup.typeMinimumFilterLength("3");
        createNewFilterPopup.typeMinimumRequiredResults("3");
        createNewFilterPopup.selectInFilterAvailability("Selected sites");
        createNewFilterPopup.addSite(testSite1.getTitle());
        createNewFilterPopup.addSite(testSite2.getTitle());

        log.info("STEP 3: Click 'Close' button.");
        createNewFilterPopup.clickClose();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), filterName, "Filter name hasn't changed.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Site", "Filter property hasn't changed.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "Yes", "Filter Show hasn't changed.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Everywhere", "Filter availability hasn't changed.");

        log.info("STEP 4: Click on the filter's id ('filter1').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");
        assertEquals(createNewFilterPopup.getFilterNameValue(), filterName, "Filter Name: " + filterName);
        assertTrue(createNewFilterPopup.isShowWithSearchResultsChecked(), "Show with Search Results: Yes");
        assertEquals(createNewFilterPopup.getSortBy(), "A-Z", "Sort By: A-Z");
        assertEquals(createNewFilterPopup.getNumberOfFilters(), "10", "Number of Filters: 10");
        assertEquals(createNewFilterPopup.getMiniFilterLength(), "1", "Minimum Filter Length: 1");
        assertEquals(createNewFilterPopup.getMiniRequiredResults(), "1", "Minimum Required Results: 1");
        assertEquals(createNewFilterPopup.getFilterAvailability(), "Everywhere", "Filter Availability: Everywhere");
    }

    @TestRail (id = "C6299")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 11)
    public void cancelModifyingExistingSearchFilter()
    {
        filterId = String.format("filterC6299%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("testFilterC6299%s", RandomData.getRandomAlphanumeric());
        String newFilterName = "newFilterC6299";

        searchManagerPage.navigate();

        log.info("STEP 1: Create a new filter with following properties:\n" + "- Filter ID: filter1\n" + "- Filter Name: testFilter1\n"
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

        log.info("STEP 2: On 'Search Manager' page, click on the new filter's id ('filter1'). Modify the filter's properties as below:\n"
            + "- Filter Name: testFilter2\n" + "- Show with Search Results: No\n" + "- Filter Property: Tag\n" + "- Sort By: Z-A\n"
            + "- Number of Filters: 7\n" + "- Minimum Filter Length: 3\n" + "- Minimum Required Results: 3\n"
            + "- Filter Availability: Selected Sites (Sites: site1, site2)");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");
        createNewFilterPopup.typeFilterName(newFilterName);
        createNewFilterPopup.clickShowWithSearchResults();
        createNewFilterPopup.selectFromFilterProperty("Tag");
        createNewFilterPopup.selectFromSortBy("Z-A");
        createNewFilterPopup.typeNumberOfFilters("7");
        createNewFilterPopup.typeMinimumFilterLength("3");
        createNewFilterPopup.typeMinimumRequiredResults("3");
        createNewFilterPopup.selectInFilterAvailability("Selected sites");
        createNewFilterPopup.addSite(testSite1.getTitle());
        createNewFilterPopup.addSite(testSite2.getTitle());

        log.info("STEP 3: Click 'Cancel' button.");
        createNewFilterPopup.clickCancel();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "The new filter is displayed on 'Search Manager' page.");
        assertEquals(searchManagerPage.getFilterName(filterId), filterName, "Filter name hasn't changed.");
        assertEquals(searchManagerPage.getFilterProperty(filterId), "Site", "Filter property hasn't changed.");
        assertEquals(searchManagerPage.getShowWithSearchResults(filterId), "Yes", "Filter Show hasn't changed.");
        assertEquals(searchManagerPage.getFilterAvailability(filterId), "Everywhere", "Filter availability hasn't changed.");

        log.info("STEP 4: Click on the filter's id ('filter1').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");
        assertEquals(createNewFilterPopup.getFilterNameValue(), filterName, "Filter Name: " + filterName);
        assertTrue(createNewFilterPopup.isShowWithSearchResultsChecked(), "Show with Search Results: Yes");
        assertEquals(createNewFilterPopup.getSortBy(), "A-Z", "Sort By: A-Z");
        assertEquals(createNewFilterPopup.getNumberOfFilters(), "10", "Number of Filters: 10");
        assertEquals(createNewFilterPopup.getMiniFilterLength(), "1", "Minimum Filter Length: 1");
        assertEquals(createNewFilterPopup.getMiniRequiredResults(), "1", "Minimum Required Results: 1");
        assertEquals(createNewFilterPopup.getFilterAvailability(), "Everywhere", "Filter Availability: Everywhere");
    }

    @TestRail (id = "C6286")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 12)
    public void switchOnOffShowWithSearchResults()
    {
        filterId = "filter_creator";
        filterName = "Creator";

        searchManagerPage.navigate();

        log.info("STEP 1: Click on any filter ID for a default filter (e.g.: 'filter_creator').");
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");

        log.info("STEP 2: Uncheck 'Show with Search Results' check box.");

        log.info("STEP 3: Click 'Save' button.");
        createNewFilterPopup.deselectShowWithSearchResults();
        createNewFilterPopup.clickSave();

        log.info("STEP 4: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.search(documentName);
        assertFalse(searchPage.isFilterTypePresent(filterName), "The 'Creator' filter is not displayed on 'Filter by:' section.");

        log.info("STEP 5: Open again 'Search Manager' page and click on the filter ID ('filter_creator').");
        searchManagerPage.navigate();
        searchManagerPage.clickFilterId(filterId);
        assertTrue(createNewFilterPopup.getDialogTitle().equals(filterId), "The dialog box for editing the filter is opened.");

        log.info("STEP 6: Check 'Show with Search Results' check box.");
        createNewFilterPopup.clickShowWithSearchResults();

        log.info("STEP 7: Click 'Save' button.");
        createNewFilterPopup.clickSave();

        log.info("STEP 8: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.search(documentName);
        assertTrue(searchPage.isFilterTypePresent(filterName), "The 'Creator' filter is displayed on 'Filter by:' section.");
    }

    @TestRail (id = "C6311")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 13)
    public void modifySearchFilterName()
    {
        filterId = String.format("filterC6311%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("testFilterC6311%s", RandomData.getRandomAlphanumeric());
        String newFilterName = "newFilterC6311";

        searchManagerPage.navigate();

        log.info("STEP 1: Click 'Create New Filter' button and create any custom filter with '" + filterName + "' name.");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        log.info("STEP 2: Hover over the 'Filter Name' for the '" + filterName + "' filter. Click on 'Edit' icon and modify the filter name to '"
            + newFilterName + "'. Click 'Save' button.");
        searchManagerPage.editFilterName(filterName, newFilterName);
        assertTrue(searchManagerPage.isFilterAvailable(newFilterName), "The new filter is displayed on 'Search Manager' page.");
    }

    @TestRail (id = "C6312")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 14)
    public void cancelModifyingSearchFilterName()
    {
        filterId = String.format("filterC6312%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("testFilterC6312%s", RandomData.getRandomAlphanumeric());
        String newFilterName = "newFilterC6312";

        searchManagerPage.navigate();

        log.info("STEP 1: Click 'Create New Filter' button and create any custom filter with '" + filterName + "' name.");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        log.info("STEP 2: Hover over the 'Filter Name' for the '" + filterName + "' filter. Click on 'Edit' icon and modify the filter name to '"
            + newFilterName + "'. Click 'Save' button.");
        searchManagerPage.cancelEditFilterName(filterName, newFilterName);
        assertFalse(searchManagerPage.isFilterAvailable(newFilterName), "The new filter is displayed on 'Search Manager' page.");
    }

    @TestRail (id = "C6303")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 15)
    public void deleteSearchFilter()
    {
        filterId = String.format("filterC6303%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("testFilterC6303%s", RandomData.getRandomAlphanumeric());
        String defaultFilter = "filter_creator";

        searchManagerPage.navigate();

        log.info("STEP 1: Click 'Create New Filter' button and create any custom filter with '" + filterName + "' name.");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        log.info("STEP 2: Try to delete any default filter available on 'Search Manager' page");
        assertFalse(searchManagerPage.isFilterDeletable(defaultFilter),
            "There is no 'Delete' button present for the filter, as default filters can't be deleted.");

        log.info("STEP 3: Click on 'Delete' button for the custom filter created.");
        searchManagerPage.clickDeleteFilter(filterId);
        assertEquals(confirmDeletionDialog.getDialogTitle(), language.translate("confirmDeletion.title"), "Popup is displayed.");
        assertEquals(confirmDeletionDialog.getDialogMessage(), String.format(language.translate("confirmDeletion.message"), filterId),
            "Popup message is displayed.");
        log.info("STEP 4: Click 'Yes' button.");
        confirmDeletionDialog.clickOKButton();
        assertFalse(searchManagerPage.isFilterAvailable(filterId), "Filter is no longer listed on 'Search Manager' page.");
    }

    @TestRail (id = "C6305")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 16)
    public void cancelDeletingSearchFilter()
    {
        filterId = String.format("filterC6305%s", RandomData.getRandomAlphanumeric());
        filterName = String.format("testFilterC6305%s", RandomData.getRandomAlphanumeric());

        searchManagerPage.navigate();

        log.info("STEP 1: Click 'Create New Filter' button and create any custom filter with '" + filterName + "' name.");
        searchManagerPage.createNewFilter();
        createNewFilterPopup.typeFilterId(filterId);
        createNewFilterPopup.typeFilterName(filterName);
        createNewFilterPopup.clickSave();
        assertTrue(searchManagerPage.isFilterAvailable(filterName), "The new filter is displayed on 'Search Manager' page.");

        log.info("STEP 2: Click on 'Delete' button for the custom filter created.");
        searchManagerPage.clickDeleteFilter(filterId);
        assertEquals(confirmDeletionDialog.getDialogTitle(), language.translate("confirmDeletion.title"), "Popup is displayed.");
        assertEquals(confirmDeletionDialog.getDialogMessage(), String.format(language.translate("confirmDeletion.message"), filterId),
            "Popup message is displayed.");

        log.info("STEP 3: Click 'No' button.");
        confirmDeletionDialog.clickNoButton();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "Filter is still listed on 'Search Manager' page.");

        log.info("STEP 4: Click on 'Delete' button for the custom filter created.");
        searchManagerPage.clickDeleteFilter(filterId);
        assertEquals(confirmDeletionDialog.getDialogTitle(), language.translate("confirmDeletion.title"), "Popup is displayed.");
        assertEquals(confirmDeletionDialog.getDialogMessage(), String.format(language.translate("confirmDeletion.message"), filterId),
            "Popup message is displayed.");

        log.info("STEP 5: Click 'Close' button.");
        confirmDeletionDialog.clickCloseButton();
        assertTrue(searchManagerPage.isFilterAvailable(filterId), "Filter is still listed on 'Search Manager' page.");
    }

    @TestRail (id = "C6285")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE }, priority = 1)
    public void changeSearchFiltersOrder()
    {
        filterId = "filter_creator";
        filterName = "Creator";
        String filterId2 = "filter_modifier";
        String filterName2 = "Modifier";
        boolean result = false;

        searchManagerPage.navigate();

        log.info("STEP 1: Hover over 'Move-Up' icon for any existing filter.");
        assertEquals(searchManagerPage.getUpTooltipForFilter(filterId), String.format(language.translate("searchManager.reorder.up"), filterId), "Up tooltip");

        log.info("STEP 2: Hover over 'Move-Down' icon for any existing filter.");
        assertEquals(searchManagerPage.getDownTooltipForFilter(filterId), String.format(language.translate("searchManager.reorder.down"), filterId),
            "Down tooltip");

        log.info("STEP 3: Move up 'filter_modifier' and move down 'filter_created'");
        int filterIdPosition = searchManagerPage.getFilterPosition(filterId);
        int filterId2Position = searchManagerPage.getFilterPosition(filterId2);
        if (filterIdPosition < filterId2Position)
        {
            searchManagerPage.moveFilterUp(filterId2);
            filterId2Position--;
            while (filterIdPosition < filterId2Position)
            {
                searchManagerPage.moveFilterDown(filterId);
                filterIdPosition++;
            }
        } else
        {
            searchManagerPage.moveFilterDown(filterId2);
            filterId2Position++;
            while (filterIdPosition > filterId2Position)
            {
                searchManagerPage.moveFilterUp(filterId);
                filterIdPosition--;
            }
            result = true;
        }

        filterIdPosition = searchManagerPage.getFilterPosition(filterId);
        filterId2Position = searchManagerPage.getFilterPosition(filterId2);
        assertEquals(searchManagerPage.getFilterPosition(filterId), filterIdPosition, "Filter " + filterId + " should be on a different row!");
        assertEquals(searchManagerPage.getFilterPosition(filterId2), filterId2Position, "Filter " + filterId2 + " should be on a different row!");

        log.info("STEP 4: Type '" + documentName + "' on the search box from 'Alfresco Toolbar' and press 'Enter' key.");
        toolbar.searchAndEnterSearch(documentName);
        if (result)
        {
            assertTrue(searchPage.getFilterTypePosition(filterName) < searchPage.getFilterTypePosition(filterName2), "The 'Created' filter should be on top of the 'Modifier' filter.");
        } else
        {
            assertFalse(searchPage.getFilterTypePosition(filterName) > searchPage.getFilterTypePosition(filterName2), "The 'Modifier' filter should be on top of the 'Created' filter.");
        }

    }

    @TestRail (id = "C6313")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE, "SearchTests" }, priority = 2)
    public void verifySitesSection(){

        searchManagerPage.navigate();
        log.info("STEP 1: Click 'Create New Filter' button.");
        searchManagerPage.createNewFilter();


        log.info("STEP 2: Go to 'Filter Availability' field and select 'Selected sites' option from the drop-down.");
        createNewFilterPopup.selectInFilterAvailability("Selected sites");

        log.info("STEPS 3-5: Click 'Add a new entry' button from 'Sites' section and select 'site1' from the drop-down list with available sites. Click 'Save the current entry' icon for 'site1'.");
        createNewFilterPopup.addSite(testSite1.getTitle());
        assertEquals(createNewFilterPopup.getCurrentSelectedSites()[0], testSite1.getTitle(), "'site1' is successfully added to 'Sites' section. ");

        log.info("STEPS 6-7: Click on 'Edit the current entry' icon () for 'site1' and choose another site from the drop-down list (e.g.: 'site2'). Click 'Save the current entry' icon for 'site1'.");
        createNewFilterPopup.clickEditIcon();
        createNewFilterPopup.editSiteName( testSite2.getTitle());
        assertEquals(createNewFilterPopup.getSelectedSiteDetail(), testSite2.getTitle(), "'site2' is successfully added to 'Sites' section (instead of 'site1').");

        log.info("STEPS 8-9: Click on 'Edit the current entry' icon () for 'site2' and choose another site from the drop-down list (e.g.: 'site3'). Click 'Cancel editing the current entry' icon");
        createNewFilterPopup.cancelEditSiteName(testSite3.getTitle());
        assertEquals(createNewFilterPopup.getSelectedSiteDetail(), testSite2.getTitle(), "'site2' is successfully added to 'Sites' section (instead of 'site3').");

        log.info("STEP 10: Click 'Delete the current entry' icon () for 'site2'");
        createNewFilterPopup.deleteSite(testSite2.getTitle());
        assertEquals(createNewFilterPopup.getCurrentSelectedSites().length, 0, "'site2' is removed from 'Sites' section.");

        log.info("STEPS 11-12: Click 'Add a new entry' button from 'Sites' section and select 'site3' from the drop-down list with available sites. Click 'Cancel editing the current entry' icon");
        createNewFilterPopup.cancelAddSiteName(testSite3.getTitle());
        assertEquals(createNewFilterPopup.getCurrentSelectedSites().length, 0, "'site3' is not added to 'Sites' section.");
    }
}
