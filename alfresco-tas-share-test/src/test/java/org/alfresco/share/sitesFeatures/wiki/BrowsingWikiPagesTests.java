package org.alfresco.share.sitesFeatures.wiki;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Argint Alex
 */
public class BrowsingWikiPagesTests extends ContextAwareWebTest
{
    //@Autowired
    WikiListPage wikiListPage;

    private String uniqueIdentifier;
    private String userName1;
    private String userName2;
    private String siteName;
    private String description;
    private String siteTitle1;
    private String siteTitle2;
    private String siteTag;

    @BeforeClass (alwaysRun = true)
    public void setup() throws DataPreparationException
    {
        super.setup();

        uniqueIdentifier = RandomData.getRandomAlphanumeric();
        siteName = "siteName" + uniqueIdentifier;
        userName1 = "User1" + uniqueIdentifier;
        userName2 = "User2" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;
        siteTitle1 = "Page1" + uniqueIdentifier;
        siteTitle2 = "Page2" + uniqueIdentifier;
        siteTag = "test_tag" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, "lastName");
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, userName2, "lastName");
        siteService.create(userName1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName1, password, siteName, DashboardCustomization.Page.WIKI, null);
        userService.createSiteMember(userName1, password, userName2, siteName, "SiteManager");
        userService.createSiteMember(userName1, password, userName2, siteName, "SiteManager");

        setupAuthenticatedSession(userName1, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5548")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseWikiByTagTest()
    {
        LOG.info("Starting test C5548");
        LOG.info("Preconditions");
        List<String> siteTitles = new ArrayList<>();
        siteTitles.add("Page1");
        siteTitles.add("Page2");
        siteTitles.add("Page3");
        List<String> siteTags1 = new ArrayList<>();
        siteTags1.add("test_tag");
        List<String> siteTags2 = new ArrayList<>();
        siteTags2.add("p3");

        for (String siteTitle : siteTitles)
        {
            if (siteTitle != "Page3")
                sitePagesService.createWiki(userName1, password, siteName, siteTitle, siteTitle, siteTags1);
            else
                sitePagesService.createWiki(userName1, password, siteName, siteTitle, siteTitle, siteTags2);
        }

        wikiListPage.navigate(siteName);

        LOG.info("Step 1 : Click 'test_tag'  tag in 'Tags' area");
        wikiListPage.clickSpecificTag("test_tag");
        assertFalse(wikiListPage.isWikiPageDisplayed("Page3"), "Wiki 'Page3' is displayed");
        assertTrue(wikiListPage.isWikiPageDisplayed("Page2"), "Wiki 'Page2' is not displayed");
        assertTrue(wikiListPage.isWikiPageDisplayed("Page1"), "Wiki 'Page1' is not displayed");

        LOG.info("Step 2 : Click on 'p3' tag in 'Tags' area");
        wikiListPage.clickSpecificTag("p3");
        assertTrue(wikiListPage.isWikiPageDisplayed("Page3"), "Wiki 'Page3' is not displayed");
        assertFalse(wikiListPage.isWikiPageDisplayed("Page2"), "Wiki 'Page2' is displayed");
        assertFalse(wikiListPage.isWikiPageDisplayed("Page1"), "Wiki 'Page1' is displayed");

        LOG.info("Step 3 : Click on 'Show All Tags' filter");
        wikiListPage.clickShowAllTags();
        assertTrue(wikiListPage.isWikiPageDisplayed("Page3"), "Wiki 'Page3' is not displayed");
        assertTrue(wikiListPage.isWikiPageDisplayed("Page2"), "Wiki 'Page2' is not displayed");
        assertTrue(wikiListPage.isWikiPageDisplayed("Page1"), "Wiki 'Page1' is not displayed");
    }

    @TestRail (id = "C5549")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseWikiPagesAllPagesTest()
    {

        List<String> siteTitles = new ArrayList<>();
        siteTitles.add("Page1");
        siteTitles.add("Page2");
        List<String> siteTags1 = new ArrayList<>();
        siteTags1.add("test_tag");

        LOG.info("Creating wiki pages");
        for (String siteTitle : siteTitles)
        {
            sitePagesService.createWiki(userName1, password, siteName, "U1" + siteTitle, "U1" + siteTitle, siteTags1);
            sitePagesService.createWiki(userName2, password, siteName, "U2" + siteTitle, "U2" + siteTitle, siteTags1);
        }

        LOG.info("Logging in as user 1 and navigating to wiki list page for site");
        setupAuthenticatedSession(userName1, password);
        wikiListPage.navigate(siteName);
        wikiListPage.clickAllPagesFilter();

        LOG.info("Verify the correct creators of each page");
        List<String> displayedPages = wikiListPage.getWikiPageTitlesList();
        for (String displayedPage : displayedPages)
        {
            String creator = wikiListPage.getWikiPageCreator(displayedPage);

            switch (displayedPage)
            {
                case "U2Page2":
                    assertEquals(creator, userName2, "Incorrect creator displayed for page " + displayedPage);
                    break;
                case "U1Page2":
                    assertEquals(creator, userName1, "Incorrect creator displayed for page " + displayedPage);
                    break;
                case "U2Page1":
                    assertEquals(creator, userName2, "Incorrect creator displayed for page " + displayedPage);
                    break;
                case "U1Page1":
                    assertEquals(creator, userName1, "Incorrect creator displayed for page " + displayedPage);
                    break;
            }
        }
    }

    @TestRail (id = "C5550")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseWikiPagesMyPagesTest()
    {
        cleanupAuthenticatedSession();
        List<String> siteTitles = new ArrayList<>();
        siteTitles.add(siteTitle1);
        siteTitles.add(siteTitle2);
        List<String> siteTags1 = new ArrayList<>();
        siteTags1.add(siteTag);

        LOG.info("Creating wiki pages");
        for (String siteTitle : siteTitles)
        {
            sitePagesService.createWiki(userName1, password, siteName, "U1-" + siteTitle, "U1-" + siteTitle, siteTags1);
            sitePagesService.createWiki(userName2, password, siteName, "U2-" + siteTitle, "U2-" + siteTitle, siteTags1);
        }

        LOG.info("Logging in as user 1 and navigate to wiki list page for site");
        setupAuthenticatedSession(userName1, password);
        wikiListPage.navigate(siteName);

        LOG.info("STEP1: Click \"My Pages\" from Pages section");
        wikiListPage.clickMyPagesFilter();
        getBrowser().waitInSeconds(8);
        try
        {
            assertTrue(wikiListPage.isWikiPageDisplayed("U1-" + siteTitle1), "Wiki 'U1Page1' is displayed");
            assertTrue(wikiListPage.isWikiPageDisplayed("U1-" + siteTitle2), "Wiki 'U1Page2' is displayed");
        } catch (NoSuchElementException e)
        {
            getBrowser().refresh();
            wikiListPage.renderedPage();
            assertTrue(wikiListPage.isWikiPageDisplayed("U1-" + siteTitle1), "Wiki 'U1Page1' is displayed");
            assertTrue(wikiListPage.isWikiPageDisplayed("U1-" + siteTitle2), "Wiki 'U1Page2' is displayed");
        }
    }

    @TestRail (id = "C5554")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPageSummaryFromWikiListTest()
    {
        LOG.info("Starting C5554");
        LOG.info("Preconditions");
        List<String> siteTags = new ArrayList<>();
        siteTags.add("test_tag");
        List<String> siteDisplayedTags;
        String pageName = "Page1" + uniqueIdentifier;
        String pageContent = pageName + " content";

        sitePagesService.createWiki(userName1, password, siteName, pageName, pageContent, siteTags);

        LOG.info("Step 1 : Navigate to 'Wiki Page List' and verify all the displayed content for a wiki page");
        wikiListPage.navigate(siteName);

        LOG.info("Verify displayed wiki page title");
        assertTrue(wikiListPage.isWikiPageDisplayed(pageName), "Wiki page is not displayed");
        assertEquals(wikiListPage.getWikiPageCreator(pageName), userName1, "Wiki page creator is not correct");
        assertEquals(wikiListPage.getWikiPageCreationDate(pageName), wikiListPage.getWikiPageModificationDate(pageName),
            "The creation and modification dates are different");
        assertEquals(wikiListPage.getWikiPageContent(pageName), pageContent, "Wiki page content is not correct");
        siteDisplayedTags = wikiListPage.getWikiPageTags(pageName);
        assertEquals(siteDisplayedTags.size(), 1, "More ore less than one tag is displayed");
        assertEquals(siteDisplayedTags.get(0), siteTags.get(0), "The name of the tag is not correct");
    }
}