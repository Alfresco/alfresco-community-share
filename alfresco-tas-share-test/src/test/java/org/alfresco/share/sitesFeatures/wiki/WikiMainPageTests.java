package org.alfresco.share.sitesFeatures.wiki;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.wiki.EditWikiPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author iulia.nechita
 */

public class WikiMainPageTests extends ContextAwareWebTest
{
    @Autowired
    WikiMainPage wikiPage;

    @Autowired
    EditWikiPage editWikiPage;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName;
    private String wikiPageContent = "content";

    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, testUser);
        setupAuthenticatedSession(testUser, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);

    }

    @TestRail(id = "C5496")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createWikiMainPage()
    {
        // precondition
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        wikiPage.navigate(siteName);

        LOG.info("STEP 1: Click on edit wiki page link");
        wikiPage.clickEditPageLink();

        LOG.info("STEP 2: Type some content to the text box, then click on Save button");
        editWikiPage.saveWikiContent(wikiPageContent);
        Assert.assertEquals(wikiPage.getWikiPageContent(), wikiPageContent,
                "Wrong wiki page!, expected " + wikiPageContent + " but found " + wikiPage.getWikiPageContent());
        siteService.delete(adminUser,adminPassword,siteName );

    }

    @TestRail(id = "C5509")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreationOfWikiMainPage()
    {
        // precondition
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, Page.WIKI, null);
        wikiPage.navigate(siteName);

        LOG.info("STEP 1: Click on edit wiki page link");
        wikiPage.clickEditPageLink();

        LOG.info("STEP 2: Type some content to the text box, then click on Cancel button");
        editWikiPage.cancelWikiContent(wikiPageContent);
        Assert.assertTrue(wikiPage.getWikiPageContent().isEmpty(), "Wiki main page should be empty!");
        siteService.delete(adminUser,adminPassword,siteName );

    }
}
