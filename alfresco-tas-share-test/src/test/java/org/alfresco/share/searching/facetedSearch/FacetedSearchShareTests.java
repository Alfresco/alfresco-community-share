package org.alfresco.share.searching.facetedSearch;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;


import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.*;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.alfresco.utility.report.Bug;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;



@Slf4j

/**
 * Created by Mirela Tifui on 12/29/2017.
 */
public class FacetedSearchShareTests extends BaseTest
{
    //@Autowired
    Toolbar toolbar;
    //@Autowired
    SearchPage searchPage;
    //@Autowired
    WikiPage wikiPage;
    DocumentLibraryPage documentLibraryPage;
    DocumentDetailsPage documentDetailsPage;
    CustomizeSitePage customizeSite;
    DashboardCustomization dashboardCustomization;
    UserProfilePage userProfilePage;
    CreateContentPage createContent;

    DataListsPage dataListsPage;
    SiteDashboardPage siteDashboardPage;
    SiteContentDashlet siteContentDashlet;
    DataListsService dataListsService;


    SoftAssert softAssert = new SoftAssert();
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    @Autowired
    private SiteService siteService;
    @Autowired
    private SitePagesService sitePagesService;



    private String docName1 = "testfile1_" + RandomData.getRandomAlphanumeric();
    private String docName2 = "testfile2_" + RandomData.getRandomAlphanumeric();
    private String docName3 = "testfile3_" + RandomData.getRandomAlphanumeric();
    private String docContent1 = "content1";
    private String docContent2 = "content2";
    private String docContent3 = "content3";
    private String eventName = "testEvent";
    private String eventDescription = "testEventDescription";
    private String wikiName = "testWiki";
    private String wikiContent = "test_wiki_content";
    private String linkName = "testLink";
    private String linkDescription = "description of link";
    private String blogPostName = "testBlogPost";
    private String blogContent = "content of the blog";
    private String discussionName = "testDiscussion";
    private String discussionContent = "discussionContent";
    private final String password = "password";
    private String listName = "contactList";
    private String listDescription = "listDescription";
    private DateTime today = new DateTime();
    private Date startDate = today.toDate();

    @BeforeMethod (alwaysRun = true)
    public void testSetup()  {
        log.info("Precondition2: Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());
        log.info("Precondition3: Test Site is created");
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        searchPage = new SearchPage(webDriver);
        userProfilePage = new UserProfilePage(webDriver);
        toolbar = new Toolbar(webDriver);
        wikiPage = new WikiPage(webDriver);
        dataListsPage = new DataListsPage(webDriver);
        createContent = new CreateContentPage(webDriver);
        customizeSite = new CustomizeSitePage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        siteContentDashlet = new SiteContentDashlet(webDriver);
        dashboardCustomization = new DashboardCustomization();
        dataListsService = new DataListsService();
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" })
    public void testHighlightedDisjunction()
    {
        log.info("Step 1: Searching using disjunction (\"OR\"), check the result is properly highlighted");
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName1).typeContent(docContent1).clickCreate();
        documentLibraryPage.navigate();        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName2).typeContent(docContent2).clickCreate();
        documentLibraryPage.navigate();
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName3).typeContent(docContent3).clickCreate();
        documentLibraryPage.navigate();
        String searchExpression = docName1 + " OR " + docName2;
        documentLibraryPage.navigate(site.get());
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName1));
        Assert.assertTrue(searchPage.isNameHighlighted(docName1), docName1 + " is not highlighted");
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName2));
        Assert.assertTrue(searchPage.isNameHighlighted(docName2), docName2 + " is not highlighted");
        Assert.assertFalse(searchPage.isResultFound(docName3));
        Assert.assertFalse(searchPage.isNameHighlighted(docName3), docName3 + " is  highlighted");
    }

    @Bug (id = "To be raised")
    @Test (enabled = false, groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testHighlightedConjunction()
    {
        log.info("Step 1: Searching using conjunction (\"AND\"), the result is properly highlighted");
        String searchExpression = docName1 + " AND " + docName3;
        documentLibraryPage.navigate(site.get());
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName1));
        Assert.assertTrue(searchPage.isNameHighlighted(docName1), docName1 + " is not highlighted");
        Assert.assertFalse(searchPage.isResultFound(docName2));
        Assert.assertFalse(searchPage.isNameHighlighted(docName2), docName2 + " is  highlighted");
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName3));
        Assert.assertTrue(searchPage.isNameHighlighted(docName3), docName3 + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testHighlightedNegationNOT()
    {
        log.info("Step 1: Searching using negation (\"NOT\"), the result is properly highlighted");
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName1).typeContent(docContent1).clickCreate();
        documentLibraryPage.navigate();
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName2).typeContent(docContent2).clickCreate();
        documentLibraryPage.navigate();
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName3).typeContent(docContent3).clickCreate();
        documentLibraryPage.navigate();
        String searchExpression = docName1 + " NOT " + docName2;
        documentLibraryPage.navigate(site.get());
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName1));
        Assert.assertFalse(searchPage.isResultFound(docName2));
        Assert.assertTrue(searchPage.isNameHighlighted(docName1), docName1 + " is not highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(docName2), docName2 + " is  highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testHighlightedNegation()
    {
        log.info("Step 1: Searching using negation (\"!\"), the result is properly highlighted");
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName1).typeContent(docContent1).clickCreate();
        documentLibraryPage.navigate();
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName2).typeContent(docContent2).clickCreate();
        documentLibraryPage.navigate();
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent.typeName(docName3).typeContent(docContent3).clickCreate();
        documentLibraryPage.navigate();
        String searchExpression = docName1 + " !" + docName2;
        documentLibraryPage.navigate(site.get());
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName1));
        Assert.assertFalse(searchPage.isResultFound(docName2));
        Assert.assertTrue(searchPage.isNameHighlighted(docName1), docName1 + " is not highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(docName2), docName2 + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" })
    public void testHighlightedCalendarName()
    {
        log.info("Step 1: Searching file by Calendar Event name, the result is highlighted");
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = site.get().getId();
        String phrase = "phrase" + identifier;
        documentLibraryPage.navigate(site.get());
        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.CALENDAR);
        siteService.addPagesToSite(userName, password, siteName,sitePages);
        sitePagesService.addCalendarEvent(userName, password, siteName, eventName,
            "", phrase, today.toDate(), today.toDate(), "", "", false, null);
        toolbar.searchAndEnterSearch(eventName);
        Assert.assertTrue(searchPage.isResultFound(eventName), eventName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(eventName), eventName + " is not highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(eventDescription), eventDescription + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" })
    public void testHighlightedWikiNameAndSelection()
    {
        String expectedWikiPageTitle = "Alfresco » Wiki » " + wikiName;
        log.info("Step 1: Searching by Wiki page name, the result is highlighted");
        log.info("Step 1: Searching file by Calendar Event name, the result is highlighted");
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = site.get().getId();
        String phrase = "phrase" + identifier;
        documentLibraryPage.navigate(site.get());
        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.WIKI);
        siteService.addPagesToSite(userName, password, siteName,sitePages);
        sitePagesService.createWiki(userName, password, siteName,  wikiName, phrase, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, wikiName,
            "", phrase, today.toDate(), today.toDate(), "", "", false, null);
        toolbar.searchAndEnterSearch(wikiName);
        softAssert.assertTrue(searchPage.isResultFound(wikiName), wikiName + " is not found");
        softAssert.assertTrue(searchPage.isNameHighlighted(wikiName), wikiName + " is not highlighted");
        softAssert.assertFalse(searchPage.isContentHighlighted(wikiContent), wikiContent + " is highlighted");
        log.info("Step 2: Click wiki name");
        searchPage.clickOnContentName(wikiName);
        softAssert.assertEquals(wikiPage.getWikiCurrentPageTitle(), expectedWikiPageTitle, expectedWikiPageTitle + " is not displayed");

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" })
    public void testHighlightedLinkName()
    {
        log.info("Step 1: Searching by Link page name, the result is highlighted");
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = site.get().getId();
        String phrase = "phrase" + identifier;
        documentLibraryPage.navigate(site.get());
        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.LINKS);
        siteService.addPagesToSite(userName, password, siteName,sitePages);
        sitePagesService.createLink(userName, password, siteName, linkName,
            "https://www.alfresco.com", phrase, false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, linkName,
            "", phrase, today.toDate(), today.toDate(), "", "", false, null);
        toolbar.searchAndEnterSearch(linkName);
        Assert.assertTrue(searchPage.isResultFound(linkName), linkName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(linkName), linkName + " is not highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(linkDescription), linkDescription + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" })
    public void testHighlightedBlogName()
    {
        log.info("Step 1: Searching by Blog page name, the result is highlighted");
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = site.get().getId();
        String phrase = "phrase" + identifier;
        documentLibraryPage.navigate(site.get());
        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.BLOG);
        siteService.addPagesToSite(userName, password, siteName,sitePages);
        sitePagesService.createBlogPost(userName, password, siteName, blogPostName, phrase,
            false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, blogPostName,
            "", phrase, today.toDate(), today.toDate(), "", "", false, null);
        toolbar.searchAndEnterSearch(blogPostName);
        Assert.assertTrue(searchPage.isResultFound(blogPostName), blogPostName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(blogPostName), blogPostName + " is not highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(blogContent), blogContent + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testHighlightedDiscussionName()
    {
        log.info("Step 1: Searching by Discussion name, the result is highlighted");
        log.info("Step 1: Searching file by Calendar Event name, the result is highlighted");
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = site.get().getId();
        String phrase = "phrase" + identifier;
        documentLibraryPage.navigate(site.get());
        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.DISCUSSIONS);
        siteService.addPagesToSite(userName, password, siteName,sitePages);
        sitePagesService.createDiscussion(userName, password, siteName, discussionName,
            phrase, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, discussionName,
            "", phrase, today.toDate(), today.toDate(), "", "", false, null);
        toolbar.searchAndEnterSearch(discussionName);
        Assert.assertTrue(searchPage.isResultFound(discussionName), discussionName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(discussionName), discussionName + " is not highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(discussionContent), discussionContent + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "SinglePipelineFailure" })
    public void testHighlightedDataListName()
    {
        log.info("Step 1: Searching by Blog page name, the result is highlighted");
        log.info("Step 1: Searching file by Calendar Event name, the result is highlighted");
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = user.get().getUsername();
        String siteName = site.get().getId();
        String phrase = "phrase" + identifier;
        documentLibraryPage.navigate(site.get());
        log.info("Data create as per pre condition");
        List<Page> sitePages = new ArrayList<>();
        sitePages.add(Page.DATALISTS);
        siteService.addPagesToSite(userName, password, siteName,sitePages);
        documentLibraryPage.navigate(site.get());
        documentLibraryPage.createContactDataList(listName);
        sitePagesService.addCalendarEvent(userName, password, siteName, listName,
            "", phrase, today.toDate(), today.toDate(), "", "", false, null);
        toolbar.searchAndEnterSearch(listName);
        Assert.assertTrue(searchPage.isResultFound(listName), listName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(listName), listName + " is not highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(listDescription), listDescription + " is highlighted");
        log.info("Step 2: Select data list title and check user is redirected to the correct page");
        searchPage.clickOnContentName(listName);
        dataListsPage.assertDataListPageIsOpened();
    }
}
