package org.alfresco.share.searching.advancedSearch;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class ContentSearchStructureTest extends ContextAwareWebTest
{
    @Autowired
    Toolbar toolbar;

    @Autowired
    SearchPage searchPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    NewContentDialog newContentDialog;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    private DateTime today = new DateTime();
    private DateTime tomorrow = today.plusDays(1);

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5951")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH }, enabled = false)
    public void searchByAWordOrPhraseAnywhereItExists()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;
        String word = "word" + identifier;
        String phrase = "phrase" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.CALENDAR);
        sitePages.add(DashboardCustomization.Page.LINKS);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, word + ".txt", phrase);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, phrase + ".txt", word);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + "1.txt", word);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + "2.txt", word);
        sitePagesService.createWiki(userName, password, siteName, word + " wiki", phrase, null);
        sitePagesService.createWiki(userName, password, siteName, phrase + " wiki", word, null);
        sitePagesService.createBlogPost(userName, password, siteName, word + " blog", phrase, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, phrase + " blog", word, false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, word + " calendar", "", phrase, today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, phrase + " calendar", "", word, today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.createDiscussion(userName, password, siteName, word + " discussion", phrase, null);
        sitePagesService.createDiscussion(userName, password, siteName, phrase + " discussion", word, null);
        sitePagesService.createLink(userName, password, siteName, word + " link", "https://www.alfresco.com", phrase, false, null);
        sitePagesService.createLink(userName, password, siteName, phrase + " link", "https://www.alfresco.com", word, false, null);

        setupAuthenticatedSession(userName, password);

        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(identifier + "1.txt");
        documentDetailsPage.clickOnCommentDocument();
        documentDetailsPage.addComment(word);

        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(identifier + "2.txt");
        documentDetailsPage.clickOnCommentDocument();
        documentDetailsPage.addComment(phrase);

        LOG.info("STEP 1 - Fill in search field with a word (e.g: \"word\" or \"=word\") and click \"Search\" button or \"Enter\"");
        getBrowser().waitInSeconds(10);
        toolbar.search(word);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(word + ".txt"), word + ".txt is displayed");
        assertTrue(searchPage.isResultFound(phrase + ".txt"), phrase + ".txt is displayed");
        assertTrue(searchPage.isResultFound(identifier + "1.txt"), identifier + "1.txt is displayed");
        assertTrue(searchPage.isResultFound(identifier + "2.txt"), identifier + "2.txt is displayed");
        assertTrue(searchPage.isResultFound(word + "_wiki"), word + " wiki is displayed");
        assertTrue(searchPage.isResultFound(phrase + " wiki"), phrase + " wiki is displayed");
        assertTrue(searchPage.isResultFound(word + " blog"), word + " blog is displayed");
        assertTrue(searchPage.isResultFound(phrase + " blog"), phrase + " blog is displayed");
        assertTrue(searchPage.isResultFound(word + " calendar"), word + " calendar is displayed");
        assertTrue(searchPage.isResultFound(phrase + " calendar"), phrase + " calendar is displayed");
        assertTrue(searchPage.isResultFound(word + " discussion"), word + " discussion is displayed");
        assertTrue(searchPage.isResultFound(phrase + " discussion"), phrase + " discussion is displayed");
        assertTrue(searchPage.isResultFound(word + " link"), word + " link is displayed");
        assertTrue(searchPage.isResultFound(phrase + " link"), phrase + " link is displayed");

        LOG.info("STEP 2 - Fill in search field with a phrase and click \"Search\" button or \"Enter\"");
        toolbar.search(phrase);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(word + ".txt"), word + ".txt is displayed");
        assertTrue(searchPage.isResultFound(phrase + ".txt"), phrase + ".txt is displayed");
        assertTrue(searchPage.isResultFound(word + " wiki"), word + " wiki is displayed");
        assertTrue(searchPage.isResultFound(phrase + "_wiki"), phrase + " wiki is displayed");
        assertTrue(searchPage.isResultFound(word + " blog"), word + " blog is displayed");
        assertTrue(searchPage.isResultFound(phrase + " blog"), phrase + " blog is displayed");
        assertTrue(searchPage.isResultFound(word + " calendar"), word + " calendar is displayed");
        assertTrue(searchPage.isResultFound(phrase + " calendar"), phrase + " calendar is displayed");
        assertTrue(searchPage.isResultFound(word + " discussion"), word + " discussion is displayed");
        assertTrue(searchPage.isResultFound(phrase + " discussion"), phrase + " discussion is displayed");
        assertTrue(searchPage.isResultFound(word + " link"), word + " link is displayed");
        assertTrue(searchPage.isResultFound(phrase + " link"), phrase + " link is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5970")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH }, enabled = false)
    public void searchByMultipleWords()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String identifier1 = RandomData.getRandomAlphanumeric();
        String identifier2 = RandomData.getRandomAlphanumeric();
        String identifier3 = RandomData.getRandomAlphanumeric();
        String word1 = "word" + identifier1;
        String word2 = "word" + identifier2;
        String word3 = "word" + identifier3;
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.CALENDAR);
        sitePages.add(DashboardCustomization.Page.LINKS);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + "1.txt", word1 + " " + word2 + " " + word3);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + "2.txt", word3 + " " + word2 + " " + word1);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, word1 + " " + word2 + " " + word3 + " 1.txt", identifier + "3");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, word3 + " " + word2 + " " + word1 + " 2.txt", identifier + "4");
        sitePagesService.createWiki(userName, password, siteName, word1 + " " + word2 + " " + word3 + " wiki", word3 + " " + word2 + " " + word1, null);
        sitePagesService.createWiki(userName, password, siteName, word3 + " " + word2 + " " + word1 + " wiki", word1 + " " + word2 + " " + word3, null);
        sitePagesService.createBlogPost(userName, password, siteName, word1 + " " + word2 + " " + word3 + " blog", word3 + " " + word2 + " " + word1, false,
            null);
        sitePagesService.createBlogPost(userName, password, siteName, word3 + " " + word2 + " " + word1 + " blog", word1 + " " + word2 + " " + word3, false,
            null);
        sitePagesService.addCalendarEvent(userName, password, siteName, word1 + " " + word2 + " " + word3 + " calendar", "", word3 + " " + word2 + " " + word1,
            today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, word3 + " " + word2 + " " + word1 + " calendar", "", word1 + " " + word2 + " " + word3,
            today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.createDiscussion(userName, password, siteName, word1 + " " + word2 + " " + word3 + " discussion", word3 + " " + word2 + " " + word1,
            null);
        sitePagesService.createDiscussion(userName, password, siteName, word3 + " " + word2 + " " + word1 + " discussion", word1 + " " + word2 + " " + word3,
            null);
        sitePagesService.createLink(userName, password, siteName, word1 + " " + word2 + " " + word3 + " link", "https://www.alfresco.com",
            word3 + " " + word2 + " " + word1, false, null);
        sitePagesService.createLink(userName, password, siteName, word3 + " " + word2 + " " + word1 + " link", "https://www.alfresco.com",
            word1 + " " + word2 + " " + word3, false, null);

        setupAuthenticatedSession(userName, password);

        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(identifier + "1.txt");
        documentDetailsPage.clickOnCommentDocument();
        documentDetailsPage.addComment(word1 + " " + word2 + " " + word3);

        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(identifier + "2.txt");
        documentDetailsPage.clickOnCommentDocument();
        documentDetailsPage.addComment(word3 + " " + word2 + " " + word1);

        LOG.info(
            "STEP 1 - Fill in search field with words (e.g: \"word1 word2 word3\" or \"word1 AND word2 AND word3\") and click \"Search\" button or \"Enter\"");
        getBrowser().waitInSeconds(10);
        toolbar.search(word1 + " AND " + word2 + " AND " + word3);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(identifier + "1.txt"), identifier + "1.txt is displayed");
        assertTrue(searchPage.isResultFound(identifier + "2.txt"), identifier + "2.txt is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " 1.txt"), word1 + " " + word2 + " " + word3 + " 1.txt is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " 2.txt"), word3 + " " + word2 + " " + word1 + " 2.txt is displayed");
        assertTrue(searchPage.isResultFound(word1 + "_" + word2 + "_" + word3 + "_wiki"), word1 + " " + word2 + " " + word3 + " wiki is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " wiki"), word3 + " " + word2 + " " + word1 + " wiki is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " blog"), word1 + " " + word2 + " " + word3 + " blog is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " blog"), word3 + " " + word2 + " " + word1 + " blog is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " calendar"), word1 + " " + word2 + " " + word3 + " calendar is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " calendar"), word3 + " " + word2 + " " + word1 + " calendar is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " discussion"), word1 + " " + word2 + " " + word3 + " discussion is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " discussion"), word3 + " " + word2 + " " + word1 + " discussion is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " link"), word1 + " " + word2 + " " + word3 + " link is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " link"), word3 + " " + word2 + " " + word1 + " link is displayed");

        LOG.info("STEP 2 - Fill in search field with the same words, but in a different order (e.g: \"word3 word2 word1\" or \"word2 AND word1 AND word3\")");
        toolbar.search(word3 + " " + word2 + " " + word1);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(identifier + "1.txt"), identifier + "1.txt is displayed");
        assertTrue(searchPage.isResultFound(identifier + "2.txt"), identifier + "2.txt is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " 1.txt"), word1 + " " + word2 + " " + word3 + " 1.txt is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " 2.txt"), word3 + " " + word2 + " " + word1 + " 2.txt is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " wiki"), word1 + " " + word2 + " " + word3 + " wiki is displayed");
        assertTrue(searchPage.isResultFound(word3 + "_" + word2 + "_" + word1 + "_wiki"), word3 + " " + word2 + " " + word1 + " wiki is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " blog"), word1 + " " + word2 + " " + word3 + " blog is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " blog"), word3 + " " + word2 + " " + word1 + " blog is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " calendar"), word1 + " " + word2 + " " + word3 + " calendar is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " calendar"), word3 + " " + word2 + " " + word1 + " calendar is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " discussion"), word1 + " " + word2 + " " + word3 + " discussion is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " discussion"), word3 + " " + word2 + " " + word1 + " discussion is displayed");
        assertTrue(searchPage.isResultFound(word1 + " " + word2 + " " + word3 + " link"), word1 + " " + word2 + " " + word3 + " link is displayed");
        assertTrue(searchPage.isResultFound(word3 + " " + word2 + " " + word1 + " link"), word3 + " " + word2 + " " + word1 + " link is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @Bug (id = "ACE-5789")
    @TestRail (id = "C5971")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH }, enabled = false)
    public void searchByAnyWords()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String identifier1 = RandomData.getRandomAlphanumeric();
        String identifier2 = RandomData.getRandomAlphanumeric();
        String identifier3 = RandomData.getRandomAlphanumeric();
        String word1 = "word" + identifier1;
        String word2 = "word" + identifier2;
        String word3 = "word" + identifier3;
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.CALENDAR);
        sitePages.add(DashboardCustomization.Page.LINKS);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + "1.txt", word1);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, word2 + " .txt", identifier + "2.txt");
        sitePagesService.createWiki(userName, password, siteName, word1 + " wiki", "", null);
        sitePagesService.createWiki(userName, password, siteName, identifier + " wiki", word3, null);
        sitePagesService.createBlogPost(userName, password, siteName, word2 + " blog", "", false, null);
        sitePagesService.createBlogPost(userName, password, siteName, identifier + " blog", word3, false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, word3 + " calendar", "", "", today.toDate(), tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, identifier + " calendar", "", word2, today.toDate(), tomorrow.toDate(), "", "", false,
            null);
        sitePagesService.createDiscussion(userName, password, siteName, word3 + " discussion", "", null);
        sitePagesService.createLink(userName, password, siteName, word2 + " link", "https://www.alfresco.com", "", false, null);
        sitePagesService.createLink(userName, password, siteName, identifier + " link", "https://www.alfresco.com", word1, false, null);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(identifier + "1.txt");
        documentDetailsPage.clickOnCommentDocument();
        documentDetailsPage.addComment(word3);

        getBrowser().waitInSeconds(10);
        LOG.info("STEP 1 - Fill in search field with words (e.g: \"word1 OR word2 OR word3\") and click \"Search\" button or \"Enter\"");
        toolbar.search(word1 + " OR " + word2 + " OR " + word3);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFoundWithRetry(identifier + "1.txt"), identifier + "1.txt is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(word2 + " .txt"), word2 + " .txt is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(word1 + "_wiki"), word1 + " wiki is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(identifier + " wiki"), identifier + " wiki is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(word2 + " blog"), word2 + " blog is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(identifier + " blog"), identifier + " blog is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(word3 + " calendar"), word3 + " calendar is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(identifier + " calendar"), identifier + " calendar is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(word3 + " discussion"), word3 + " discussion is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(word2 + " link"), word2 + " link is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(identifier + " link"), identifier + " link is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5935")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyTitle()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.BLOG);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        sitePagesService.createBlogPost(userName, password, siteName, "file1" + identifier, "", false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "file2" + identifier, "file1" + identifier, false, null);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Fill in search field with \"title:X\" (e.g: \"title:file1\") and click \"Search\" button");
        toolbar.search("title:file1" + identifier);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("file1" + identifier), "file1" + identifier + " is displayed");

        LOG.info("STEP 2 - Fill in search field with a name (e.g: \"file1\") and click \"Search\" button");
        toolbar.search("file1" + identifier);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("file1" + identifier), "file1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("file2" + identifier), "file2" + identifier + " is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5936")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyName()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.BLOG);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createFolder(userName, password, "file1" + identifier, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.PDF, "testcontent_10." + identifier + ".pdf", "");
        sitePagesService.createBlogPost(userName, password, siteName, "file2" + identifier, "file1" + identifier, false, null);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Fill in search field with \"name:X\" (e.g: \"name:file1\") and click \"Search\" button");
        toolbar.search("name:file1" + identifier);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("file1" + identifier), "file1" + identifier + " is displayed");

        LOG.info("STEP 2 - Fill in search field with a name (e.g: \"file1\") and click \"Search\" button");
        toolbar.search("file1" + identifier);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("file1" + identifier), "file1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("file2" + identifier), "file2" + identifier + " is displayed");

        LOG.info("STEP 3 - Fill in the search field with for e.g. name: testcontent_10.10.pdf and click \"Search\" button");
        toolbar.search("testcontent_10." + identifier + ".pdf");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("testcontent_10." + identifier + ".pdf"), "testcontent_10." + identifier + ".pdf is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5937")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyDescription()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;
        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.BLOG);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        sitePagesService.createBlogPost(userName, password, siteName, "file1" + identifier, "", false, null);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickFolderLink();
        newContentDialog.fillInDetails("file2" + identifier, "", "file1" + identifier);
        newContentDialog.clickSaveButton();
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1 - Fill in search field with \"description:X\" (e.g: \"description:file1\") and click \"Search\" button");
        toolbar.search("description:file1" + identifier);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("file2" + identifier), "file2" + identifier + " is displayed");

        LOG.info("STEP 2 - Fill in search field with a name (e.g: \"file1\") and click \"Search\" button");
        toolbar.search("file1" + identifier);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("file1" + identifier), "file1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("file2" + identifier), "file2" + identifier + " is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5938")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyCreated()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + ".txt", "");
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);

        LOG.info("STEP 1 - Fill in search field with \"created:today\"");
        toolbar.search("created:today");
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn(siteName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(identifier + ".txt"), identifier + ".txt" + " is displayed");

        LOG.info("STEP 2 - Enter a query using different date formats and click Search button");
        toolbar.search("created:" + DataUtil.formatDate(new Date(), "yyyy-MMM-dd'T'"));
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn("All Sites");
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn(siteName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(identifier + ".txt"), identifier + ".txt" + " is displayed");

        LOG.info("STEP 3 - Enter a query using: now/NOW(today/TODAY)\n" + "(e.g: created: [\"2010-01-12\" TO TODAY], created: [\"2010-01-12\" TO NOW] )\n"
            + "and click Search button");
        toolbar.search("created:[\"2010-01-12\" TO NOW]");
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn("All Sites");
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn(siteName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(identifier + ".txt"), identifier + ".txt" + " is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5939")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyModified()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, identifier + ".txt", "");
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);

        LOG.info("STEP 1 - Fill in search field with \"modified:today\"");
        toolbar.search("modified:today");
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn(siteName);
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(identifier + ".txt"), identifier + ".txt" + " is displayed");

        LOG.info("STEP 2 - Enter a query using different date formats and click Search button");
        toolbar.search("modified:" + DataUtil.formatDate(new Date(), "yyyy-MM-dd"));
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn("All Sites");
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn(siteName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(identifier + ".txt"), identifier + ".txt" + " is displayed");

        LOG.info("STEP 3 - Enter a query using: now/NOW(today/TODAY)\n" + "(e.g: created: [\"2010-01-12\" TO TODAY], created: [\"2010-01-12\" TO NOW] )\n"
            + "and click Search button");
        toolbar.search("modified:[\"2010-01-12\" TO NOW]");
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn("All Sites");
        searchPage.clickSearchInDropdown();
        searchPage.selectOptionFromSearchIn(siteName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound(identifier + ".txt"), identifier + ".txt" + " is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5940")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyCreator()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.CALENDAR);
        sitePages.add(DashboardCustomization.Page.LINKS);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createFolder(userName, password, "folder" + identifier, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file" + identifier, "");
        sitePagesService.createWiki(userName, password, siteName, "wiki" + identifier, "", null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog" + identifier, "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, "calendar" + identifier, "", "", today.toDate(), tomorrow.toDate(), "", "", false,
            null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion" + identifier, "", null);
        sitePagesService.createLink(userName, password, siteName, "link" + identifier, "https://www.alfresco.com", "", false, null);

        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Fill in search field with \"creator:username\" and click Search button");
        toolbar.search("creator:" + userName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("folder" + identifier), "folder" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("file" + identifier), "file" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("wiki" + identifier), "wiki" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("blog" + identifier), "blog" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("calendar" + identifier), "calendar" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("discussion" + identifier), "discussion" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("link" + identifier), "link" + identifier + " is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5941")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyModifier()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.CALENDAR);
        sitePages.add(DashboardCustomization.Page.LINKS);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createFolder(userName, password, "folder" + identifier, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file" + identifier, "");
        sitePagesService.createWiki(userName, password, siteName, "wiki" + identifier, "", null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog" + identifier, "", false, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, "calendar" + identifier, "", "", today.toDate(), tomorrow.toDate(), "", "", false,
            null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion" + identifier, "", null);
        sitePagesService.createLink(userName, password, siteName, "link" + identifier, "https://www.alfresco.com", "", false, null);

        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Fill in search field with \"modifier:username\" and click Search button");
        toolbar.search("modifier:" + userName);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("folder" + identifier), "folder" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("file" + identifier), "file" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("wiki" + identifier), "wiki" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("blog" + identifier), "blog" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("calendar" + identifier), "calendar" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("discussion" + identifier), "discussion" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("link" + identifier), "link" + identifier + " is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5950")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPropertyText()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;
        String word = "word" + identifier;

        List<DashboardCustomization.Page> sitePages = new ArrayList<>();
        sitePages.add(DashboardCustomization.Page.WIKI);
        sitePages.add(DashboardCustomization.Page.BLOG);
        sitePages.add(DashboardCustomization.Page.DISCUSSIONS);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, sitePages);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file1" + identifier, word);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file2" + identifier, "2" + word);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file3" + identifier, word + "3");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, "file4" + identifier, "4" + word + "4");
        sitePagesService.createWiki(userName, password, siteName, "wiki1" + identifier, word, null);
        sitePagesService.createWiki(userName, password, siteName, "wiki2" + identifier, "2" + word, null);
        sitePagesService.createWiki(userName, password, siteName, "wiki3" + identifier, word + "3", null);
        sitePagesService.createWiki(userName, password, siteName, "wiki4" + identifier, "4" + word + "4", null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog1" + identifier, word, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog2" + identifier, "2" + word, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog3" + identifier, word + "3", false, null);
        sitePagesService.createBlogPost(userName, password, siteName, "blog4" + identifier, "4" + word + "4", false, null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion1" + identifier, word, null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion2" + identifier, "2" + word, null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion3" + identifier, word + "3", null);
        sitePagesService.createDiscussion(userName, password, siteName, "discussion4" + identifier, "4" + word + "4", null);

        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Fill in search field with 'TEXT:" + word + "' and click 'Search' button");
        toolbar.search("TEXT:" + word);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("file1" + identifier), "file1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("wiki1" + identifier), "wiki1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("blog1" + identifier), "blog1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("discussion1" + identifier), "discussion1" + identifier + " is displayed");

        LOG.info("STEP 2 - Fill in search field with \"TEXT:*word*\" and click Search button");
        toolbar.search("TEXT:*" + word + "*");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult, (int) properties.getImplicitWait());
        assertEquals(searchPage.getPageTitle(), language.translate("searchPage.pageTitle"), "Page title");
        assertTrue(searchPage.isResultFound("file1" + identifier), "file1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("file2" + identifier), "file2" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("file3" + identifier), "file3" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("file4" + identifier), "file4" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("wiki1" + identifier), "wiki1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("wiki2" + identifier), "wiki2" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("wiki3" + identifier), "wiki3" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("wiki4" + identifier), "wiki4" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("blog1" + identifier), "blog1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("blog2" + identifier), "blog2" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("blog3" + identifier), "blog3" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("blog4" + identifier), "blog4" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("discussion1" + identifier), "discussion1" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("discussion2" + identifier), "discussion2" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("discussion3" + identifier), "discussion3" + identifier + " is displayed");
        assertTrue(searchPage.isResultFound("discussion4" + identifier), "discussion4" + identifier + " is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }
}