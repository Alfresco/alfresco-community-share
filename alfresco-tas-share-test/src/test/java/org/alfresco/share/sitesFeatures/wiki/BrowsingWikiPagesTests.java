package org.alfresco.share.sitesFeatures.wiki;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;

import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;

import org.alfresco.utility.exception.DataPreparationException;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * Created by Argint Alex
 */
public class BrowsingWikiPagesTests extends BaseTest
{
    //@Autowired
    WikiListPage wikiListPage;
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    @Autowired
    UserService userService;
    private String uniqueIdentifier;
    private String description;
    private String siteTitle1;
    private String siteTitle2;
    private String siteTag;
    private final ThreadLocal<UserModel> userName1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userName2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void setup() throws DataPreparationException
    {
        uniqueIdentifier = RandomData.getRandomAlphanumeric();
        description = "description" + uniqueIdentifier;
        siteTitle1 = "Page1" + uniqueIdentifier;
        siteTitle2 = "Page2" + uniqueIdentifier;
        siteTag = "test_tag" + uniqueIdentifier;

        log.info("Precondition: Any Test user is created");
        userName1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Precondition: Test user2 is created");
        userName2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site is created");
        siteName.set(getDataSite().usingUser(userName1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName1.get());

        siteService.addPageToSite(userName1.get().getUsername(), userName1.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.WIKI, null);
        userService.createSiteMember(userName1.get().getUsername(), userName1.get().getPassword(), userName2.get().getUsername(), siteName.get().getId(), "SiteManager");
        userService.createSiteMember(userName1.get().getUsername(), userName1.get().getPassword(), userName2.get().getUsername(), siteName.get().getId(), "SiteManager");

        wikiListPage = new WikiListPage(webDriver);


        authenticateUsingLoginPage(userName1.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName1.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName2.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName1.get());
        deleteUsersIfNotNull(userName2.get());
    }

    @TestRail (id = "C5548")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseWikiByTagTest()
    {
        log.info("Starting test C5548");
        log.info("Preconditions");
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
                sitePagesService.createWiki(userName1.get().getUsername(), userName1.get().getPassword(), siteName.get().getId(), siteTitle, siteTitle, siteTags1);
            else
                sitePagesService.createWiki(userName1.get().getUsername(), userName1.get().getPassword(), siteName.get().getId(), siteTitle, siteTitle, siteTags2);
        }

        wikiListPage.navigate(siteName.get());

        log.info("Step 1 : Click 'test_tag'  tag in 'Tags' area");
        wikiListPage.clickSpecificTag("test_tag");
        assertFalse(wikiListPage.isWikiPageDisplayed("Page3"), "Wiki 'Page3' is displayed");
        assertTrue(wikiListPage.isWikiPageDisplayed("Page2"), "Wiki 'Page2' is not displayed");
        assertTrue(wikiListPage.isWikiPageDisplayed("Page1"), "Wiki 'Page1' is not displayed");

        log.info("Step 2 : Click on 'p3' tag in 'Tags' area");
        wikiListPage.clickSpecificTag("p3");
        assertTrue(wikiListPage.isWikiPageDisplayed("Page3"), "Wiki 'Page3' is not displayed");
        assertFalse(wikiListPage.isWikiPageDisplayed("Page2"), "Wiki 'Page2' is displayed");
        assertFalse(wikiListPage.isWikiPageDisplayed("Page1"), "Wiki 'Page1' is displayed");

        log.info("Step 3 : Click on 'Show All Tags' filter");
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

        log.info("Creating wiki pages");
        for (String siteTitle : siteTitles)
        {
            sitePagesService.createWiki(userName1.get().getUsername(), userName1.get().getPassword(), siteName.get().getId(), "U1" + siteTitle, "U1" + siteTitle, siteTags1);
            sitePagesService.createWiki(userName2.get().getUsername(), userName2.get().getPassword(), siteName.get().getId(), "U2" + siteTitle, "U2" + siteTitle, siteTags1);
        }

        log.info("Logging in as user 1 and navigating to wiki list page for site");
        authenticateUsingCookies(userName1.get());
        wikiListPage.navigate(siteName.get());
        wikiListPage.clickAllPagesFilter();

        log.info("Verify the correct creators of each page");
        List<String> displayedPages = wikiListPage.getWikiPageTitlesList();
        for (String displayedPage : displayedPages)
        {
            String creator = wikiListPage.getWikiPageCreator(displayedPage);

            switch (displayedPage)
            {
                case "U2Page2":
                    assertEquals(creator, userName2.get().getFirstName(), "Incorrect creator displayed for page " + displayedPage);
                    break;
                case "U1Page2":
                    assertEquals(creator, userName1.get().getFirstName(), "Incorrect creator displayed for page " + displayedPage);
                    break;
                case "U2Page1":
                    assertEquals(creator, userName2.get().getFirstName(), "Incorrect creator displayed for page " + displayedPage);
                    break;
                case "U1Page1":
                    assertEquals(creator, userName1.get().getFirstName(), "Incorrect creator displayed for page " + displayedPage);
                    break;
            }
        }
    }

    @TestRail (id = "C5550")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseWikiPagesMyPagesTest()
    {
        List<String> siteTitles = new ArrayList<>();
        siteTitles.add(siteTitle1);
        siteTitles.add(siteTitle2);
        List<String> siteTags1 = new ArrayList<>();
        siteTags1.add(siteTag);

        log.info("Creating wiki pages");
        for (String siteTitle : siteTitles)
        {
            sitePagesService.createWiki(userName1.get().getUsername(), userName1.get().getPassword(), siteName.get().getId(), "U1-" + siteTitle, "U1-" + siteTitle, siteTags1);
            sitePagesService.createWiki(userName1.get().getUsername(), userName1.get().getPassword(), siteName.get().getId(), "U2-" + siteTitle, "U2-" + siteTitle, siteTags1);
        }

        log.info("Logging in as user 1 and navigate to wiki list page for site");
        authenticateUsingCookies(userName1.get());
        wikiListPage.navigate(siteName.get());

        log.info("STEP1: Click \"My Pages\" from Pages section");
        wikiListPage.clickMyPagesFilter();
        try
        {
            assertTrue(wikiListPage.isWikiPageDisplayed("U1-" + siteTitle1), "Wiki 'U1Page1' is displayed");
            assertTrue(wikiListPage.isWikiPageDisplayed("U1-" + siteTitle2), "Wiki 'U1Page2' is displayed");
        } catch (NoSuchElementException e)
        {
            assertTrue(wikiListPage.isWikiPageDisplayed("U1-" + siteTitle1), "Wiki 'U1Page1' is displayed");
            assertTrue(wikiListPage.isWikiPageDisplayed("U1-" + siteTitle2), "Wiki 'U1Page2' is displayed");
        }
    }

    @TestRail (id = "C5554")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPageSummaryFromWikiListTest()
    {
        log.info("Starting C5554");
        log.info("Preconditions");
        List<String> siteTags = new ArrayList<>();
        siteTags.add("test_tag");
        List<String> siteDisplayedTags;
        String pageName = "Page1" + uniqueIdentifier;
        String pageContent = pageName + " content";

        sitePagesService.createWiki(userName1.get().getUsername(), userName1.get().getPassword(), siteName.get().getId(), pageName, pageContent, siteTags);

        log.info("Step 1 : Navigate to 'Wiki Page List' and verify all the displayed content for a wiki page");
        wikiListPage.navigate(siteName.get());

        log.info("Verify displayed wiki page title");
        assertTrue(wikiListPage.isWikiPageDisplayed(pageName), "Wiki page is not displayed");
        assertEquals(wikiListPage.getWikiPageCreator(pageName), userName1.get().getFirstName(), "Wiki page creator is not correct");
        assertEquals(wikiListPage.getWikiPageCreationDate(pageName), wikiListPage.getWikiPageModificationDate(pageName),
            "The creation and modification dates are different");
        assertEquals(wikiListPage.getWikiPageContent(pageName), pageContent, "Wiki page content is not correct");
        siteDisplayedTags = wikiListPage.getWikiPageTags(pageName);
        assertEquals(siteDisplayedTags.size(), 1, "More ore less than one tag is displayed");
        assertEquals(siteDisplayedTags.get(0), siteTags.get(0), "The name of the tag is not correct");
    }
}