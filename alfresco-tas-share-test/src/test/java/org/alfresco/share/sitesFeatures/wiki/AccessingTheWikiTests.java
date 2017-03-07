package org.alfresco.share.sitesFeatures.wiki;


import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.wiki.CreateWikiPage;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Argint Alex
 */
public class AccessingTheWikiTests extends ContextAwareWebTest {

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    CustomizeSitePage customizeSitePage;

    @Autowired
    WikiMainPage wikiMainPage;

    @Autowired
    WikiListPage wikiListPage;


    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;

    public void setup(String id) {
        super.setup();

        uniqueIdentifier = "-" + id + "-" + DataUtil.getUniqueIdentifier();
        siteName = "siteName" + uniqueIdentifier;
        userName = "User" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;


        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        siteService.addPageToSite(userName, DataUtil.PASSWORD, siteName, DashboardCustomization.Page.WIKI, null);
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
    }

    @TestRail(id = "C5493")
    @Test
    public void accessWikiMainPageTest() {

        LOG.info("Starting test C5493");
        setup("C5493");

        LOG.info("Step 1 : Click on wiki tab and inspect the elements on the wiki main page");
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.WIKI);
        Assert.assertTrue(wikiMainPage.isNewPageButtonDisplayed(), "'New page' button is not displayed");
        Assert.assertTrue(wikiMainPage.isDeleteButtonDisplayed(), "'Delete' button is not displayed");
        Assert.assertFalse(wikiMainPage.isDeleteButtonEnabled(), "'Delete' button is enabled");
        Assert.assertTrue(wikiMainPage.isRenameButtonDisplayed(), "'Rename' button is not displayed");
        Assert.assertTrue(wikiMainPage.isDisabledViewLinkDisplayed(), "'View' link is not displayed");
        Assert.assertTrue(wikiMainPage.isEditPagelinkDisplayed(), "'Edit Page' link is not displayed");
        Assert.assertTrue(wikiMainPage.isDetailsLinkDisplayed(), "'Details' link is not displayed");

        LOG.info("Step 2 : Open customize site and rename 'Wiki' to 'newWiki'");
        customizeSitePage.navigate(siteName);
        customizeSitePage.renamePage(SitePageType.WIKI, "newWiki");
        Assert.assertEquals(customizeSitePage.getPageDisplayName(SitePageType.WIKI), "newWiki", "Wiki page not renamed correctly");

        LOG.info("Step 3 : Click 'Ok' button and verify that the wiki page has been renamed on site dashboard");
        customizeSitePage.clickOk();
        browser.waitInSeconds(2);
        Assert.assertEquals(siteDashboardPage.getPageDisplayName(SitePageType.WIKI), "newWiki", "'Wiki' page not correctly renamed on site dashboard");

        LOG.info("Step 4 : Click on 'newWiki' link and verify that 'Main Page' is opeend");
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.WIKI);
        Assert.assertTrue(wikiMainPage.isNewPageButtonDisplayed(), "Wiki 'Main Page' is not opened");
    }

    @TestRail(id = "C5494")
    @Test
    public void accesWikiPageListTest() {

        List<String> siteTitles = new ArrayList<>();
        siteTitles.add("Page1");
        siteTitles.add("Page2");


        LOG.info("Starting test C5494");
        setup("C5494");
        wikiMainPage.navigate(siteName);
        for (int i = 0; i < siteTitles.size(); i++) {
            sitePagesService.createWiki(userName, DataUtil.PASSWORD, siteName, siteTitles.get(i), siteTitles.get(i), null);
        }
        wikiMainPage.navigate(siteName);

        LOG.info("Step 1 : Click on 'Wiki Page List' link");
        wikiMainPage.clickOnWikiListLink();
        List<String> actualPageList = wikiListPage.getWikiPageTitlesList();

        Assert.assertEquals(actualPageList.size(), siteTitles.size(), "The number of listed wiki pages is not the same with the number of created wiki pages");
        for (int i = 0; i < actualPageList.size(); i++) {
            // the first wiki page listed is the last created
            Assert.assertEquals(actualPageList.get(i), siteTitles.get(actualPageList.size() - i - 1), "The page " + siteTitles.get(i) + " is not listed in the wiki page list");
        }

        LOG.info("Step 2 : Verify the actions available for wiki pages");
        for (int i = 0; i < actualPageList.size(); i++) {
            Assert.assertTrue(wikiListPage.areActionsAvailableForPage(actualPageList.get(i)), "One or more actions are not available for " + actualPageList.get(i) + " wiki page");
        }

        LOG.info("Step 3 : Click on 'Main Page' button");
        wikiListPage.clickMainPageButton();
        wikiMainPage.isRenameWikiMainPagePanelDisplayed();
    }

}
