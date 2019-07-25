package org.alfresco.share.searching.facetedSearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by Mirela Tifui on 12/29/2017.
 */
public class FacetedSearchShareTests extends ContextAwareWebTest
{
    @Autowired
    Toolbar toolbar;
    @Autowired
    SearchPage searchPage;
    @Autowired
    WikiPage wikiPage;
    @Autowired
    DataListsPage dataListsPage;

    SoftAssert softAssert = new SoftAssert();

    private String userName = "user" + RandomData.getRandomAlphanumeric();
    private String siteName = "site" + RandomData.getRandomAlphanumeric();
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
    private String listName = "listName";
    private String listDescription = "listDescription";
    private DateTime today = new DateTime();
    private Date startDate = today.toDate();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        List<DashboardCustomization.Page> pagesToAdd = new ArrayList<>();
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "test", "user");
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName1, docContent1);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName2, docContent2);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName3, docContent3);
        siteService.addPageToSite(adminUser, adminPassword, siteName, DashboardCustomization.Page.CALENDAR, null);
        siteService.addPageToSite(adminUser, adminPassword, siteName, DashboardCustomization.Page.WIKI, null);
        siteService.addPageToSite(adminUser, adminPassword, siteName, DashboardCustomization.Page.LINKS, null);
        siteService.addPageToSite(adminUser, adminPassword, siteName, DashboardCustomization.Page.BLOG, null);
        siteService.addPageToSite(adminUser, adminPassword, siteName, DashboardCustomization.Page.DISCUSSIONS, null);
        siteService.addPageToSite(adminUser, adminPassword, siteName, DashboardCustomization.Page.DATALISTS, null);
        sitePagesService.addCalendarEvent(userName, password, siteName, eventName, "here", eventDescription, startDate, startDate, "", "", false, "tag1");
        sitePagesService.createWiki(userName, password, siteName, wikiName, wikiContent, null);
        sitePagesService.createLink(userName, password, siteName, linkName, "https://url.com", linkDescription, false, null);
        sitePagesService.createBlogPost(userName, password, siteName, blogPostName, blogContent, false, null);
        sitePagesService.createDiscussion(userName, password, siteName, discussionName, discussionContent, null);
        dataListsService.createDataList(userName, password, siteName, DataListsService.DataList.CONTACT_LIST, listName, listDescription);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void tearDown()
    {
        userService.delete(adminUser, adminPassword, userName);
        siteService.delete(adminUser, adminPassword, domain, siteName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedDisjunction()
    {
        LOG.info("Step 1: Searching using disjunction (\"OR\"), check the result is properly highlighted");
        String searchExpression = docName1 + " OR " + docName2;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName1));
        Assert.assertTrue(searchPage.isNameHighlighted(docName1), docName1 + " is not highlighted");
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName2));
        Assert.assertTrue(searchPage.isNameHighlighted(docName2), docName2 + " is not highlighted");
        Assert.assertFalse(searchPage.isResultFoundWithRetry(docName3));
        Assert.assertFalse(searchPage.isNameHighlighted(docName3), docName3 + " is not highlighted");
    }

    @Bug (id = "To be raised")
    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedConjunction()
    {
        LOG.info("Step 1: Searching using conjunction (\"AND\"), the result is properly highlighted");
        String searchExpression = docName1 + " AND " + docName2;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName1));
        Assert.assertTrue(searchPage.isNameHighlighted(docName1), docName1 + " is not highlighted");
        Assert.assertFalse(searchPage.isResultFoundWithRetry(docName2));
        Assert.assertFalse(searchPage.isNameHighlighted(docName2), docName2 + " is not highlighted");
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName3));
        Assert.assertTrue(searchPage.isNameHighlighted(docName3), docName3 + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedNegationNOT()
    {
        LOG.info("Step 1: Searching using negation (\"NOT\"), the result is properly highlighted");
        String searchExpression = docName1 + " NOT " + docName2;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName1));
        Assert.assertFalse(searchPage.isResultFoundWithRetry(docName2));
        Assert.assertTrue(searchPage.isNameHighlighted(docName1), docName1 + " is not highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(docName2), docName2 + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedNegation()
    {
        LOG.info("Step 1: Searching using negation (\"!\"), the result is properly highlighted");
        String searchExpression = docName1 + " !" + docName2;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName1));
        Assert.assertFalse(searchPage.isResultFoundWithRetry(docName2));
        Assert.assertTrue(searchPage.isNameHighlighted(docName1), docName1 + " is not highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(docName2), docName2 + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedCalendarName()
    {
        LOG.info("Step 1: Searching file by Calendar Event name, the result is highlighted");
        toolbar.search(eventName);
        Assert.assertTrue(searchPage.isResultFound(eventName), eventName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(eventName), eventName + " is not highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(eventDescription), eventDescription + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedWikiNameAndSelection()
    {
        String expectedWikiPageTitle = "Alfresco » Wiki » " + wikiName;
        LOG.info("Step 1: Searching by Wiki page name, the result is highlighted");
        toolbar.search(wikiName);
        softAssert.assertTrue(searchPage.isResultFound(wikiName), wikiName + " is not found");
        softAssert.assertTrue(searchPage.isNameHighlighted(wikiName), wikiName + " is not highlighted");
        softAssert.assertFalse(searchPage.isContentHighlighted(wikiContent), wikiContent + " is highlighted");
        LOG.info("Step 2: Click wiki name");
        searchPage.clickContentName(wikiName, wikiPage);
        softAssert.assertEquals(wikiPage.getPageTitle(), expectedWikiPageTitle, expectedWikiPageTitle + " is not displayed");

    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedLinkName()
    {
        LOG.info("Step 1: Searching by Link page name, the result is highlighted");
        toolbar.search(linkName);
        Assert.assertTrue(searchPage.isResultFound(linkName), linkName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(linkName), linkName + " is not highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(linkDescription), linkDescription + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedBlogName()
    {
        LOG.info("Step 1: Searching by Blog page name, the result is highlighted");
        toolbar.search(blogPostName);
        Assert.assertTrue(searchPage.isResultFound(blogPostName), blogPostName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(blogPostName), blogPostName + " is not highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(blogContent), blogContent + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedDiscussionName()
    {
        LOG.info("Step 1: Searching by Discussion name, the result is highlighted");
        toolbar.search(discussionName);
        Assert.assertTrue(searchPage.isResultFound(discussionName), discussionName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(discussionName), discussionName + " is not highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(discussionContent), discussionContent + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedDataListName()
    {
        LOG.info("Step 1: Searching by Blog page name, the result is highlighted");
        toolbar.search(listName);
        Assert.assertTrue(searchPage.isResultFound(listName), listName + " is not found");
        Assert.assertTrue(searchPage.isNameHighlighted(listName), listName + " is not highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(listDescription), listDescription + " is highlighted");
        LOG.info("Step 2: Select data list title and check user is redirected to the correct page");
        searchPage.clickContentName(listName, dataListsPage);
        Assert.assertEquals(dataListsPage.getPageTitle(), "Alfresco » Data Lists");
        Assert.assertTrue(dataListsPage.isDataListTitleDisplayed(listName));
    }
}
