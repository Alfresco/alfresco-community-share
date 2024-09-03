package org.alfresco.share.searching.advancedSearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.common.DataUtil;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.DashboardCustomization;

import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ContentSearchStructureTest extends BaseTest
{
    Toolbar toolbar;
    SearchPage searchPage;
    AdvancedSearchPage advancedSearchPage;
    DocumentLibraryPage documentLibraryPage;
    DocumentDetailsPage documentDetailsPage;
    NewFolderDialog newContentDialog;
    SiteDashboardPage siteDashboardPage;
    @Autowired
    private SiteService siteService;
    @Autowired
    private SitePagesService sitePagesService;
    @Autowired
    private ContentService contentService;


    private DateTime today = new DateTime();
    private DateTime tomorrow = today.plusDays(1);
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();
    private final String password = "password";


    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("PreCondition: Creating a TestUser");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        //contentService = new ContentService();
        searchPage = new SearchPage(webDriver);
        advancedSearchPage = new AdvancedSearchPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        newContentDialog = new NewFolderDialog(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        toolbar = new Toolbar(webDriver);
    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(siteModel.get());
    }

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5951")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByAWordOrPhraseAnywhereItExists()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();
        String word = "word" + identifier;
        String phrase = "phrase" + identifier;

        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.WIKI);
        sitePages.add(Page.BLOG);
        sitePages.add(Page.CALENDAR);
        sitePages.add(Page.LINKS);
        siteService.addPagesToSite(userName, password, siteName, sitePages);

        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            word + ".txt", phrase);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            phrase + ".txt", "word" +identifier);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            identifier + "1.txt", word);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            identifier + "2.txt", word);

        sitePagesService.createWiki(userName, password, siteName, word + " wiki", phrase, null);
        sitePagesService.createWiki(userName, password, siteName, phrase + " wiki", word, null);

        sitePagesService.createBlogPost(userName, password, siteName, word + " blog", phrase,
            false, null);
        sitePagesService.createBlogPost(userName, password, siteName, phrase + " blog", word,
            false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, word + " calendar",
            "", phrase, today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, phrase + " calendar",
            "", word, today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.createDiscussion(userName, password, siteName, word + " discussion",
            phrase, null);
        sitePagesService.createDiscussion(userName, password, siteName, phrase + " discussion",
            word, null);
        sitePagesService.createLink(userName, password, siteName, word + " link",
            "https://www.alfresco.com", phrase, false, null);
        sitePagesService.createLink(userName, password, siteName, phrase + " link",
            "https://www.alfresco.com", word, false, null);

        authenticateUsingCookies(user.get());

        documentLibraryPage
            .navigate(siteName)
            .clickOnFile(identifier + "1.txt");
        documentDetailsPage
            .clickOnCommentDocument();
        documentDetailsPage
            .addComment(word);

        documentLibraryPage
            .navigate(siteName)
            .clickOnFile(identifier + "2.txt");
        documentDetailsPage
            .clickOnCommentDocument();
        documentDetailsPage
            .addComment(phrase);

        log.info("STEP 1 - Fill in search field with a word (e.g: \"word\" or \"=word\") and click \"Search\" button or \"Enter\"");
        toolbar
            .search(word);
        searchPage
            .assertCreatedDataIsDisplayed(word + ".txt")
            .assertCreatedDataIsDisplayed(identifier + "1.txt")
            .assertCreatedDataIsDisplayed(identifier + "2.txt")
            .assertCreatedDataIsDisplayed(word + "_wiki")
            .assertCreatedDataIsDisplayed(phrase + "_wiki")
            .assertCreatedDataIsDisplayed(word + " blog")
            .assertCreatedDataIsDisplayed(phrase + " blog")
            .assertCreatedDataIsDisplayed(word + " calendar")
            .assertCreatedDataIsDisplayed(phrase + " calendar")
            .assertCreatedDataIsDisplayed(word + " discussion")
            .assertCreatedDataIsDisplayed(phrase + " discussion")
            .assertCreatedDataIsDisplayed(word + " link")
            .assertCreatedDataIsDisplayed(phrase + " link")
            .assertCreatedDataIsDisplayed(phrase + ".txt");

        log.info("STEP 2 - Fill in search field with a phrase and click \"Search\" button or \"Enter\"");
        toolbar
            .search(phrase);
        searchPage
            .assertCreatedDataIsDisplayed(word + ".txt")
            .assertCreatedDataIsDisplayed(phrase + ".txt")
            .assertCreatedDataIsDisplayed(word + "_wiki")
            .assertCreatedDataIsDisplayed(phrase + "_wiki")
            .assertCreatedDataIsDisplayed(word + " blog")
            .assertCreatedDataIsDisplayed(phrase + " blog")
            .assertCreatedDataIsDisplayed(word + " calendar")
            .assertCreatedDataIsDisplayed(phrase + " calendar")
            .assertCreatedDataIsDisplayed(word + " discussion")
            .assertCreatedDataIsDisplayed(phrase + " discussion")
            .assertCreatedDataIsDisplayed(word + " link")
            .assertCreatedDataIsDisplayed(phrase + " link");

    }

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5970")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByMultipleWords()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String identifier1 = RandomData.getRandomAlphanumeric();
        String identifier2 = RandomData.getRandomAlphanumeric();
        String identifier3 = RandomData.getRandomAlphanumeric();
        String word1 = "word" + identifier1;
        String word2 = "word" + identifier2;
        String word3 = "word" + identifier3;
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.WIKI);
        sitePages.add(Page.BLOG);
        sitePages.add(Page.CALENDAR);
        sitePages.add(Page.LINKS);
        siteService.addPagesToSite(userName, password, siteName, sitePages);

        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            identifier + "1.txt", word1 + " " + word2 + " " + word3);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            identifier + "2.txt", word3 + " " + word2 + " " + word1);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            word1 + " " + word2 + " " + word3 + " 1.txt", identifier + "3");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            word3 + " " + word2 + " " + word1 + " 2.txt", identifier + "4");
        sitePagesService.createWiki(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " wiki", word3 + " " + word2 + " " + word1, null);
        sitePagesService.createWiki(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " wiki", word1 + " " + word2 + " " + word3, null);
        sitePagesService.createBlogPost(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " blog", word3 + " " + word2 + " " + word1, false,
            null);
        sitePagesService.createBlogPost(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " blog", word1 + " " + word2 + " " + word3, false,
            null);
        sitePagesService.addCalendarEvent(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " calendar", "", word3 + " " + word2 + " " + word1,
            today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " calendar", "", word1 + " " + word2 + " " + word3,
            today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.createDiscussion(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " discussion", word3 + " " + word2 + " " + word1,
            null);
        sitePagesService.createDiscussion(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " discussion", word1 + " " + word2 + " " + word3,
            null);
        sitePagesService.createLink(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " link", "https://www.alfresco.com",
            word3 + " " + word2 + " " + word1, false, null);
        sitePagesService.createLink(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " link", "https://www.alfresco.com",
            word1 + " " + word2 + " " + word3, false, null);

        authenticateUsingCookies(user.get());

        documentLibraryPage
            .navigate(siteName)
            .clickOnFile(identifier + "1.txt");
        documentDetailsPage
            .clickOnCommentDocument();
        documentDetailsPage
            .addComment(word1 + " " + word2 + " " + word3);
        documentLibraryPage
            .navigate(siteName)
            .clickOnFile(identifier + "2.txt");
        documentDetailsPage
            .clickOnCommentDocument();
        documentDetailsPage
            .addComment(word3 + " " + word2 + " " + word1);

        log.info(
            "STEP 1 - Fill in search field with words (e.g: \"word1 word2 word3\" or \"word1 AND word2 AND word3\") and click \"Search\" button or \"Enter\"");
        toolbar
            .search(word1 + " AND " + word2 + " AND " + word3);
        searchPage
            .assertCreatedDataIsDisplayed(identifier + "1.txt")
            .assertCreatedDataIsDisplayed(identifier + "2.txt")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " 1.txt")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " 2.txt")
            .assertCreatedDataIsDisplayed(word1 + "_" + word2 + "_" + word3 + "_wiki")
            .assertCreatedDataIsDisplayed(word3 + "_" + word2 + "_" + word1 + "_wiki")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " blog")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " blog")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " calendar")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " calendar")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " discussion")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " discussion")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " link")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " link");

        log.info("STEP 2 - Fill in search field with the same words, but in a different order (e.g: \"word3 word2 word1\" or \"word2 AND word1 AND word3\")");
        toolbar.search(word3 + " " + word2 + " " + word1);
        searchPage
            .assertCreatedDataIsDisplayed(identifier + "1.txt")
            .assertCreatedDataIsDisplayed(identifier + "2.txt")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " 1.txt")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " 2.txt")
            .assertCreatedDataIsDisplayed(word1 + "_" + word2 + "_" + word3 + "_wiki")
            .assertCreatedDataIsDisplayed(word3 + "_" + word2 + "_" + word1 + "_wiki")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " blog")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " blog")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " calendar")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " calendar")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " discussion")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " discussion")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " link")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " link");

    }

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5971")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SinglePipelineFailure" }, enabled = true)
    public void searchByAnyWords()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String identifier1 = RandomData.getRandomAlphanumeric();
        String identifier2 = RandomData.getRandomAlphanumeric();
        String identifier3 = RandomData.getRandomAlphanumeric();
        String word1 = "word" + identifier1;
        String word2 = "word" + identifier2;
        String word3 = "word" + identifier3;
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.WIKI);
        sitePages.add(Page.BLOG);
        sitePages.add(Page.CALENDAR);
        sitePages.add(Page.LINKS);
        siteService.addPagesToSite(userName, password, siteName, sitePages);

        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            identifier + "1.txt", word1 + " " + word2 + " " + word3);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            identifier + "2.txt", word3 + " " + word2 + " " + word1);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            word1 + " " + word2 + " " + word3 + " 1.txt", identifier + "3");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            word3 + " " + word2 + " " + word1 + " 2.txt", identifier + "4");
        sitePagesService.createWiki(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " wiki", word3 + " " + word2 + " " + word1, null);
        sitePagesService.createWiki(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " wiki", word1 + " " + word2 + " " + word3, null);
        sitePagesService.createBlogPost(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " blog", word3 + " " + word2 + " " + word1, false,
            null);
        sitePagesService.createBlogPost(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " blog", word1 + " " + word2 + " " + word3, false,
            null);
        sitePagesService.addCalendarEvent(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " calendar", "", word3 + " " + word2 + " " + word1,
            today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " calendar", "", word1 + " " + word2 + " " + word3,
            today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.createDiscussion(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " discussion", word3 + " " + word2 + " " + word1,
            null);
        sitePagesService.createDiscussion(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " discussion", word1 + " " + word2 + " " + word3,
            null);
        sitePagesService.createLink(userName, password, siteName,
            word1 + " " + word2 + " " + word3 + " link", "https://www.alfresco.com",
            word3 + " " + word2 + " " + word1, false, null);
        sitePagesService.createLink(userName, password, siteName,
            word3 + " " + word2 + " " + word1 + " link", "https://www.alfresco.com",
            word1 + " " + word2 + " " + word3, false, null);

        authenticateUsingCookies(user.get());

        documentLibraryPage
            .navigate(siteName)
            .clickOnFile(identifier + "1.txt");
        documentDetailsPage
            .clickOnCommentDocument();
        documentDetailsPage
            .addComment(word1 + " " + word2 + " " + word3);
        documentLibraryPage
            .navigate(siteName)
            .clickOnFile(identifier + "2.txt");
        documentDetailsPage
            .clickOnCommentDocument();
        documentDetailsPage
            .addComment(word3 + " " + word2 + " " + word1);

        log.info(
            "STEP 1 - Fill in search field with words (e.g: \"word1 word2 word3\" or \"word1 OR word2 OR word3\") and click \"Search\" button or \"Enter\"");
        toolbar
            .search(word1 + " OR " + word2 + " OR " + word3);
        searchPage
            .assertCreatedDataIsDisplayed(identifier + "1.txt")
            .assertCreatedDataIsDisplayed(identifier + "2.txt")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " 1.txt")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " 2.txt")
            .assertCreatedDataIsDisplayed(word1 + "_" + word2 + "_" + word3 + "_wiki")
            .assertCreatedDataIsDisplayed(word3 + "_" + word2 + "_" + word1 + "_wiki")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " blog")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " blog")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " calendar")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " calendar")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " discussion")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " discussion")
            .assertCreatedDataIsDisplayed(word1 + " " + word2 + " " + word3 + " link")
            .assertCreatedDataIsDisplayed(word3 + " " + word2 + " " + word1 + " link");
    }

    @TestRail (id = "C5935")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyTitle()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.BLOG);

        siteService.addPagesToSite(userName, password, siteName, sitePages);

        sitePagesService.createBlogPost(userName, password, siteName, "file1" + identifier, "", false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "file2" + identifier, "file1" + identifier, false, null);

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Fill in search field with \"title:X\" (e.g: \"title:file1\") and click \"Search\" button");
        toolbar.search("title:file1" + identifier);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("file1" + identifier);

        log.info("STEP 2 - Fill in search field with a name (e.g: \"file1\") and click \"Search\" button");
        toolbar.search("file1" + identifier);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("file1" + identifier)
            .assertCreatedDataIsDisplayed("file2" + identifier);
    }

    @TestRail (id = "C5936")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyName()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.BLOG);

        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createFolder(userName, password, "file1" + identifier, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.PDF, "testcontent_10." + identifier + ".pdf", "");
        sitePagesService.createBlogPost(userName, password, siteName, "file2" + identifier, "file1" + identifier, false, null);

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Fill in search field with \"name:X\" (e.g: \"name:file1\") and click \"Search\" button");
        toolbar.search("name:file1" + identifier);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("file1" + identifier);

        log.info("STEP 2 - Fill in search field with a name (e.g: \"file1\") and click \"Search\" button");
        toolbar.search("file1" + identifier);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("file1" + identifier)
            .assertCreatedDataIsDisplayed("file2" + identifier);

        log.info("STEP 3 - Fill in the search field with for e.g. name: testcontent_10.10.pdf and click \"Search\" button");
        toolbar
            .search("testcontent_10." + identifier + ".pdf");
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("testcontent_10." + identifier + ".pdf");
    }

    @TestRail (id = "C5937")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyDescription()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.BLOG);

        siteService.addPagesToSite(userName, password, siteName, sitePages);
        sitePagesService.createBlogPost(userName, password, siteName, "file1" + identifier, "", false, null);

        authenticateUsingLoginPage(user.get());
        documentLibraryPage
            .navigate(siteName)
            .clickCreateButton();
        documentLibraryPage
            .clickFolderLink();
        newContentDialog
            .fillInDetails("file2" + identifier, "", "file1" + identifier)
            .clickSave();
        documentLibraryPage
            .navigate(siteName);

        log.info("STEP 1 - Fill in search field with \"description:X\" (e.g: \"description:file1\") and click \"Search\" button");
        toolbar.search("description:file1" + identifier);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("file2" + identifier);

        log.info("STEP 2 - Fill in search field with a name (e.g: \"file1\") and click \"Search\" button");
        toolbar.search("file1" + identifier);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("file1" + identifier)
            .assertCreatedDataIsDisplayed("file2" + identifier);
    }

    @TestRail (id = "C5938")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyCreated()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + ".txt", "");
        authenticateUsingLoginPage(user.get());
        siteDashboardPage.navigate(siteName);

        log.info("STEP 1 - Fill in search field with \"created:today\"");
        toolbar.search("created:today");
        searchPage
            .clickSearchInDropdown()
            .selectOptionFromSearchIn(siteName)
            .pageRefresh()
            .assertCreatedDataIsDisplayed(identifier + ".txt");

        log.info("STEP 2 - Enter a query using different date formats and click Search button");
        toolbar.search("created:" + DataUtil.formatDate(new Date(), "yyyy-MMM-dd'T'"));
        searchPage
            .clickSearchInDropdown()
            .selectOptionFromSearchIn("All Sites")
            .clickSearchInDropdown()
            .selectOptionFromSearchIn(siteName)
            .pageRefresh()
            .assertCreatedDataIsDisplayed(identifier + ".txt");

        log.info("STEP 3 - Enter a query using: now/NOW(today/TODAY)\n" + "(e.g: created: [\"2010-01-12\" TO TODAY], created: [\"2010-01-12\" TO NOW] )\n"
            + "and click Search button");
        toolbar
            .search("created:[\"2010-01-12\" TO NOW]");
        searchPage
            .clickSearchInDropdown()
            .selectOptionFromSearchIn("All Sites")
            .clickSearchInDropdown()
            .selectOptionFromSearchIn(siteName)
            .pageRefresh()
            .assertCreatedDataIsDisplayed(identifier + ".txt");
    }

    @TestRail (id = "C5939")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyModified()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + ".txt", "");
        authenticateUsingLoginPage(user.get());
        siteDashboardPage
            .navigate(siteName);

        log.info("STEP 1 - Fill in search field with \"modified:today\"");
        toolbar.search("modified:today").clickSearchInDropdown().clickSearchInDropdown();
        searchPage
            .clickSearchInDropdown()
            .selectOptionFromSearchIn(siteName)
            .pageRefresh()
            .selectOptionFromSearchIn(siteName)
            .pageRefresh()
            .assertCreatedDataIsDisplayed(identifier + ".txt");

        log.info("STEP 2 - Enter a query using different date formats and click Search button");
        toolbar
            .search("modified:" + DataUtil.formatDate(new Date(), "yyyy-MM-dd"));
        searchPage
            .clickSearchInDropdown()
            .selectOptionFromSearchIn("All Sites")
            .clickSearchInDropdown()
            .selectOptionFromSearchIn(siteName)
            .pageRefresh().assertCreatedDataIsDisplayed(identifier + ".txt");

        log.info("STEP 3 - Enter a query using: now/NOW(today/TODAY)\n" + "(e.g: created: [\"2010-01-12\" TO TODAY], created: [\"2010-01-12\" TO NOW] )\n"
            + "and click Search button");
        toolbar
            .search("modified:[\"2010-01-12\" TO NOW]");
        searchPage
            .clickSearchInDropdown()
            .selectOptionFromSearchIn("All Sites")
            .clickSearchInDropdown()
            .selectOptionFromSearchIn(siteName)
            .pageRefresh().assertCreatedDataIsDisplayed(identifier + ".txt");
//        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        //    assertTrue(searchPage.isResultFound(identifier + ".txt"), identifier + ".txt" + " is displayed");
    }

    @TestRail (id = "C5940")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SinglePipelineFailure" })
    public void searchByPropertyCreator()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.CALENDAR);
        sitePages.add(DashboardCustomization.Page.LINKS);

        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createFolder(userName, password, "folder" + identifier, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file" + identifier, "");
        sitePagesService.createWiki(userName, password, siteName, "wiki" + identifier, "", null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog" + identifier, "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, "calendar" + identifier, "", "", today.toDate(), tomorrow.toDate(), "", "", false,
            null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion" + identifier, "", null);
        sitePagesService.createLink(userName, password, siteName, "link" + identifier, "https://www.alfresco.com", "", false, null);

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Fill in search field with \"creator:username\" and click Search button");
        toolbar.search("creator:" + userName);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("folder" + identifier)
            .assertCreatedDataIsDisplayed("file" + identifier)
            .assertCreatedDataIsDisplayed("wiki" + identifier)
            .assertCreatedDataIsDisplayed("blog" + identifier)
            .assertCreatedDataIsDisplayed("calendar" + identifier)
            .assertCreatedDataIsDisplayed("discussion" + identifier)
            .assertCreatedDataIsDisplayed("link" + identifier);
    }

    @TestRail (id = "C5941")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyModifier()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.CALENDAR);
        sitePages.add(DashboardCustomization.Page.LINKS);

        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createFolder(userName, password, "folder" + identifier, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file" + identifier, "");
        sitePagesService.createWiki(userName, password, siteName, "wiki" + identifier, "", null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog" + identifier, "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, "calendar" + identifier, "", "", today.toDate(), tomorrow.toDate(), "", "", false,
            null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion" + identifier, "", null);
        sitePagesService.createLink(userName, password, siteName, "link" + identifier, "https://www.alfresco.com", "", false, null);

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Fill in search field with \"modifier:username\" and click Search button");
        toolbar.search("modifier:" + userName);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("folder" + identifier)
            .assertCreatedDataIsDisplayed("file" + identifier)
            .assertCreatedDataIsDisplayed("wiki" + identifier)
            .assertCreatedDataIsDisplayed("blog" + identifier)
            .assertCreatedDataIsDisplayed("calendar" + identifier)
            .assertCreatedDataIsDisplayed("discussion" + identifier)
            .assertCreatedDataIsDisplayed("link" + identifier);

    }
    @TestRail (id = "C5950")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH})
    public void searchByPropertyText()
    {
        siteModel.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = siteModel.get().getId();
        String word = "word" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.DISCUSSIONS);


        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file1" + identifier, word);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file2" + identifier, "2 " + word);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file3" + identifier, word + "3");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file4" + identifier, "4 " + word + "4");
        sitePagesService.createWiki(userName, password, siteName, "wiki1" + identifier, word, null);
        sitePagesService.createWiki(userName, password, siteName, "wiki2" + identifier, "2 " + word, null);
        sitePagesService.createWiki(userName, password, siteName, "wiki3" + identifier, word + "3", null);
        sitePagesService.createWiki(userName, password, siteName, "wiki4" + identifier, "4 " + word + "4", null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog1" + identifier, word, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog2" + identifier, "2 " + word, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog3" + identifier, word + "3", false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog4" + identifier, "4 " + word + "4", false, null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion1" + identifier, word, null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion2" + identifier, "2 " + word, null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion3" + identifier, word + "3", null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion4" + identifier, "4 " + word + "4", null);

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Fill in search field with 'TEXT:" + word + "' and click 'Search' button");
        toolbar.search("TEXT:" + word);
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("file1" + identifier)
            .assertCreatedDataIsDisplayed("wiki1" + identifier)
            .assertCreatedDataIsDisplayed("blog1" + identifier)
            .assertCreatedDataIsDisplayed("discussion1" + identifier);

        log.info("STEP 2 - Fill in search field with \"TEXT:*word*\" and click Search button");
        toolbar.search("TEXT:*" + word + "*");
        searchPage
            .pageRefresh()
            .assertCreatedDataIsDisplayed("file1" + identifier)
            .assertCreatedDataIsDisplayed("file2" + identifier)
            .assertCreatedDataIsDisplayed("file3" + identifier)
            .assertCreatedDataIsDisplayed("file4" + identifier)
            .assertCreatedDataIsDisplayed("wiki1" + identifier)
            .assertCreatedDataIsDisplayed("wiki2" + identifier)
            .assertCreatedDataIsDisplayed("wiki3" + identifier)
            .assertCreatedDataIsDisplayed("wiki4" + identifier)
            .assertCreatedDataIsDisplayed("blog1" + identifier)
            .assertCreatedDataIsDisplayed("blog2" + identifier)
            .assertCreatedDataIsDisplayed("blog3" + identifier)
            .assertCreatedDataIsDisplayed("blog4" + identifier)
            .assertCreatedDataIsDisplayed("discussion1" + identifier)
            .assertCreatedDataIsDisplayed("discussion2" + identifier)
            .assertCreatedDataIsDisplayed("discussion3" + identifier)
            .assertCreatedDataIsDisplayed("discussion4" + identifier);
    }
}