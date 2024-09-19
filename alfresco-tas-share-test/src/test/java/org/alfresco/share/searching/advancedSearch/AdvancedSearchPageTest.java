package org.alfresco.share.searching.advancedSearch;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j

public class AdvancedSearchPageTest extends BaseTest
{
    //@Autowired
    AdvancedSearchPage advancedSearchPage;
    @Autowired
    private SiteService siteService;
    @Autowired
    DataListsService dataList;
    @Autowired
    private SitePagesService sitePagesService;

    //@Autowired
    SearchPage searchPage;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();
    private final String password = "password";
    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("PreCondition: Creating a TestUser");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        searchPage = new SearchPage(webDriver);
        advancedSearchPage = new AdvancedSearchPage(webDriver);
    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(siteModel.get());
    }

    private void createPreconditions(String userName, String siteName, String identifier)
    {
        List<Page> sitePages = new ArrayList<>();
        DateTime today = new DateTime();
        DateTime tomorrow = today.plusDays(1);

        sitePages.add(Page.WIKI);
        sitePages.add(Page.BLOG);
        sitePages.add(Page.CALENDAR);
        sitePages.add(Page.DISCUSSIONS);
        sitePages.add(Page.LINKS);
        sitePages.add(Page.DATALISTS);
        siteService.addPagesToSite(userName, password, siteName, sitePages);

        sitePagesService.createWiki(userName, password, siteName, "test" + identifier + " wiki 1",
            "test " + identifier, null);
        sitePagesService.createWiki(userName, password, siteName, "test" + identifier + " wiki 2",
            "hello " + identifier, null);
        sitePagesService.createWiki(userName, password, siteName, identifier + " wiki 3",
            "test " + identifier, null);
        sitePagesService.createWiki(userName, password, siteName, identifier + " wiki 4",
            "hello " + identifier, null);

        sitePagesService.createBlogPost(userName, password, siteName, "test" + identifier + " blog 1",
            "test " + identifier, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "test" + identifier + " blog 2",
            "test hello " + identifier, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, identifier + " blog 3",
            "test " + identifier, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, identifier + " blog 4",
            "hello " + identifier, false, null);

        sitePagesService.addCalendarEvent(userName, password, siteName, "test" + identifier + " event 1",
            "", "test " + identifier, today.toDate(),
            tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, "test" + identifier + " event 2",
            "", "hello " + identifier, today.toDate(),
            tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, identifier + " event 3", "",
            "test " + identifier, today.toDate(),
            tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, identifier + " event 4", "",
            "hello " + identifier, today.toDate(),
            tomorrow.toDate(), "", "", false, null);

        sitePagesService.createDiscussion(userName, password, siteName, "test" + identifier + " topic 1",
            "test " + identifier, null);
        sitePagesService.createDiscussion(userName, password, siteName, "test" + identifier + " topic 2",
            "hello " + identifier, null);
        sitePagesService.createDiscussion(userName, password, siteName, identifier + " topic 3",
            "test " + identifier, null);
        sitePagesService.createDiscussion(userName, password, siteName, identifier + " topic 4",
            "hello " + identifier, null);

        sitePagesService.createLink(userName, password, siteName, "test" + identifier + " link 1",
            "https://www.alfresco.com", "test " + identifier,
            false, null);
        sitePagesService.createLink(userName, password, siteName, "test" + identifier + " link 2",
            "https://www.alfresco.com", "hello " + identifier,
            false, null);
        sitePagesService.createLink(userName, password, siteName, identifier + " link 3",
            "https://www.alfresco.com", "test " + identifier, false, null);
        sitePagesService.createLink(userName, password, siteName, identifier + " link 4",
            "https://www.alfresco.com", "hello " + identifier, false,
            null);

        dataList.createDataList(userName, password, siteName, DataListsService.DataList.TODO_LIST,
            "test" + identifier + " list 1", "test " + identifier);
        dataList.createDataList(userName, password, siteName, DataListsService.DataList.TODO_LIST,
            "test" + identifier + " list 2", "hello " + identifier);
        dataList.createDataList(userName, password, siteName, DataListsService.DataList.TODO_LIST,
            identifier + " list 3", "test" + identifier);
        dataList.createDataList(userName, password, siteName, DataListsService.DataList.TODO_LIST,
            identifier + " list 4", "hello " + identifier);
    }

    @TestRail (id = "C5888")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyAdvancedSearchPage() throws InterruptedException {
        authenticateUsingCookies(user.get());
        advancedSearchPage
            .navigate();

        log.info("STEP 1 - Verify page title");
        advancedSearchPage
            .assertPageTitle();

        log.info("STEP 2 - Verify buttons");
        advancedSearchPage
            .assertIsTopSearchButtonDisplayed();
        advancedSearchPage
            .assertIsBottomSearchButtonDisplayed();

       log.info("STEP 3 - Verify \"look for\" drop-down");
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .assertIsLookForDropdownOptionDisplayed("advancedSearchPage.lookForDropDown.content.label","advancedSearchPage.lookForDropDown.content.description");
        advancedSearchPage
            .assertIsLookForDropdownOptionDisplayed("advancedSearchPage.lookForDropDown.folders.label","advancedSearchPage.lookForDropDown.folders.description");
        log.info("STEP 4 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));
        advancedSearchPage
            .assertisKeywordsInputDisplayed();
        advancedSearchPage
            .assertisNameInputDisplayed();
        advancedSearchPage
            .assertIsTitleTextareaDisplayed();
        advancedSearchPage
            .assertIsDescriptionTextareaDisplayed();
        advancedSearchPage
            .assertIsMimetypeDropDownDisplayed();
        advancedSearchPage
            .assertIsDateFromPickerDisplayed();
        advancedSearchPage
            .assertIsDateToPickerDisplayed();
        advancedSearchPage
            .assertIsModifierInputDisplayed();
        log.info("STEP 5 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage
            .clickOnLookForDropdown();

        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        advancedSearchPage
            .assertisKeywordsInputDisplayed();
        Assert.assertTrue(advancedSearchPage.isFolderNameInputDisplayed(), "Name input is displayed");
        advancedSearchPage
            .assertIsFolderNameInputDisplayed();
        advancedSearchPage
            .assertIsFolderTitleTextareaDisplayed();
        advancedSearchPage
            .assertIsFolderDescriptionTextareaDisplayed();
    }

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5891")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SinglePipelineFailure",  "SearchTests" })
    public void searchByKeyword() {
        String identifier = RandomData.getRandomAlphanumeric();
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();
        createPreconditions(userName, siteName, identifier);
        authenticateUsingCookies(user.get());
        advancedSearchPage
            .navigate();
        log.info("STEP 1 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));
        log.info("STEP 2 - Fill in \"Keyword\" field with \"test\" and click \"Search\" button");
        advancedSearchPage
            .typeKeywords("test*");
        advancedSearchPage
            .clickFirstSearchButtonAndRefresh();
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + "_wiki_1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + "_wiki_2");
        searchPage
            .assertCreatedDataIsDisplayed(identifier + " wiki 3");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " blog 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " blog 2");
        searchPage
            .assertCreatedDataIsDisplayed(identifier + " blog 3");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " event 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " event 2");
        searchPage
            .assertCreatedDataIsDisplayed(identifier + " event 3");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " link 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " link 2");
        searchPage
            .assertCreatedDataIsDisplayed(identifier + " link 3");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " topic 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " topic 2");
        searchPage
            .assertCreatedDataIsDisplayed(identifier + " topic 3");
        log.info("STEP 3 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage
            .navigate();
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        log.info("STEP 4 - Fill in \"Keyword\" field with \"test\" and click \"Search\" button");
        advancedSearchPage
            .typeKeywords("test*");
        advancedSearchPage
            .clickFirstSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " list 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " list 2");
        searchPage
            .assertCreatedDataIsDisplayed(identifier + " list 3");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " topic 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " topic 2");
    }

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5907")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByName() {
        String identifier = RandomData.getRandomAlphanumeric();
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();
        createPreconditions(userName, siteName, identifier);
        authenticateUsingCookies(user.get());
        advancedSearchPage
            .navigate();
        log.info("STEP 1 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));
        log.info("STEP 2 - Fill in \"Name\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage
            .typeName("test*");
        advancedSearchPage
            .clickFirstSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + "_wiki_1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + "_wiki_2");
        log.info("STEP 3 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage
            .navigate();
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        log.info("STEP 4 - Fill in \"Name\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage
            .typeNameFolder("test*");
        advancedSearchPage
            .clickFirstSearchButton();
        searchPage
            .assertNoResultsFoundDisplayed();
    }

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5908")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByTitle() {
        String identifier = RandomData.getRandomAlphanumeric();
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();
        createPreconditions(userName, siteName, identifier);
        authenticateUsingCookies(user.get());
        advancedSearchPage
            .navigate();
        log.info("STEP 1 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));
        log.info("STEP 2 - Fill in \"Title\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage
            .typeTitle("test*");
        advancedSearchPage
            .clickFirstSearchButton();
                searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " wiki 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " wiki 2");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " blog 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " blog 2");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " topic 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " topic 2");
        log.info("STEP 3 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage
            .navigate();
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        log.info("STEP 4 - Fill in \"Title\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage
            .folderTypeTitle("test*");
        advancedSearchPage
            .clickFirstSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " list 1");
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " list 2");
    }

    @TestRail (id = "C5909")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "Searching"})
    public void searchByDescription() {

        String identifier = RandomData.getRandomAlphanumeric();
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();
        createPreconditions(userName, siteName, identifier);
        authenticateUsingCookies(user.get());
        advancedSearchPage
            .navigate();
        log.info("STEP 1 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));
        log.info("STEP 2 - Fill in \"Description\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage
            .typeDescription("test*");
        advancedSearchPage
            .clickFirstSearchButton();
        searchPage
            .assertNoResultsFoundDisplayed();
        log.info("STEP 3 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage.navigate();
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        log.info("STEP 4 - Fill in \"Description\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage
            .folderTypeDescription("test*");
        advancedSearchPage
            .clickFirstSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed("test" + identifier + " list 1");
        searchPage
            .assertCreatedDataIsDisplayed(identifier + " list 3");
    }
}
