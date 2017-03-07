package org.alfresco.share.sitesFeatures.wiki;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.wiki.CreateWikiPage;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.nechita
 */

public class CreateNewWikiTests extends ContextAwareWebTest
{

    @Autowired
    WikiMainPage wikiMainPage;

    @Autowired
    WikiPage wikiPage;

    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    CreateWikiPage createWikiPage;

    private List<Page> pagesToAdd = new ArrayList<Page>();
    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName;
    private String wikiPageHeader = "Create Wiki Page";
    private String wikiPageTitle;
    private String wikiPageContent;

    @BeforeMethod
    public void setup()
    {
        super.setup();
        pagesToAdd.add(Page.WIKI);
        userService.create(adminUser, adminPassword, testUser, password, "@tests.com", "firstName", "lastName");
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C5504")
    @Test
    public void createNewWikiPage()
    {
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();
        wikiPageContent = "WikiContent" + DataUtil.getUniqueIdentifier();

        // precondition
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        LOG.info("STEP 2: Add a title and a content for the new wiki page");
        createWikiPage.typeWikiPageTitle(wikiPageTitle);
        createWikiPage.typeWikiPageContent(wikiPageContent);

        LOG.info("STEP 3: Click on Save button");
        createWikiPage.saveWikiPage();

        LOG.info("STEP 4: Click on wiki page list");
        wikiPage.clickOnWikiListLink();
        Assert.assertTrue("Wiki page is not displayed in the list!", wikiListPage.getWikiPageTitlesList().contains(wikiPageTitle));
    }

    @TestRail(id = "C5504")
    @Test
    public void cancelCreationOfNewWikiPage()
    {
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();
        wikiPageContent = "WikiContent" + DataUtil.getUniqueIdentifier();

        // precondition
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        wikiMainPage.navigate(siteName);

        LOG.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        LOG.info("STEP 2: Add a title and a content for the new wiki page");
        createWikiPage.typeWikiPageTitle(wikiPageTitle);
        createWikiPage.typeWikiPageContent(wikiPageContent);

        LOG.info("STEP 3: Click on Cancel button");
        createWikiPage.cancelWikiPageAndLeavePage();

        LOG.info("STEP 4: Check that wiki page is not present on the list");
        Assert.assertTrue("There are wiki pages displayed in the list!", wikiListPage.getWikiPageTitlesList().isEmpty());
        Assert.assertTrue("Wrong message displayed!", wikiListPage.noWikiPageDisplayed().equals("There are currently no pages to display"));
    }

    @TestRail(id = "C5524")
    @Test
    public void createWikiPageFromWikiPageList()
    {
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        wikiPageTitle = "WikiPage" + DataUtil.getUniqueIdentifier();
        wikiPageContent = "WikiContent" + DataUtil.getUniqueIdentifier();
        String tagName = "tag" + DataUtil.getUniqueIdentifier();

        // precondition
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        wikiListPage.navigate(siteName);

        LOG.info("STEP 1: Click on new page button");
        wikiMainPage.clickWikiNewPage();
        Assert.assertEquals(createWikiPage.getWikiPageTitle(), wikiPageHeader);

        LOG.info("STEP 2: Add a title, a content for the new wiki page and a tag");
        createWikiPage.typeWikiPageTitle(wikiPageTitle);
        createWikiPage.typeWikiPageContent(wikiPageContent);
        createWikiPage.addTag(tagName);
        Assert.assertTrue("Tag is not displayed!", createWikiPage.getWikiPageTagsList().contains(tagName));

        LOG.info("STEP 3: Click on Save button");
        createWikiPage.saveWikiPage();

        LOG.info("STEP 4: Navigate to Wiki Page List page");
        wikiPage.clickOnWikiListLink();
        Assert.assertTrue("Tag is not displayed in the list!", wikiListPage.getTagsList().contains(tagName + " (1)"));
    }
}
