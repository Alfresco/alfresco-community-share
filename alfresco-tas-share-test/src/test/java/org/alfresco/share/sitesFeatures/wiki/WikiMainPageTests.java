package org.alfresco.share.sitesFeatures.wiki;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.wiki.EditWikiPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.nechita
 */

@Slf4j
public class WikiMainPageTests extends BaseTest
{
    @Autowired
    SiteService siteService;
    private WikiMainPage wikiMainPage;
    private EditWikiPage editWikiPage;
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String wikiPageContent = "content";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());
        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.WIKI, null);

        wikiMainPage = new WikiMainPage(webDriver);
        editWikiPage = new EditWikiPage(webDriver);

        authenticateUsingLoginPage(testUser.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(testUser.get());
    }

    @TestRail (id = "C5496")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createWikiMainPage()
    {
        log.info("STEP 1: Click on edit wiki page link");
        wikiMainPage.navigate(siteName.get().getId());
        wikiMainPage.clickEditPageLink();

        log.info("STEP 2: Type some content to the text box, then click on Save button");
        editWikiPage.saveWikiContent(wikiPageContent);
        Assert.assertEquals(wikiMainPage.getWikiPageContent(), wikiPageContent,
            "Wrong wiki page!, expected " + wikiPageContent + " but found " + wikiMainPage.getWikiPageContent());
    }

    @TestRail (id = "C5509")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreationOfWikiMainPage()
    {
        log.info("STEP 1: Click on edit wiki page link");
        wikiMainPage.navigate(siteName.get().getId());
        wikiMainPage.clickEditPageLink();

        log.info("STEP 2: Type some content to the text box, then click on Cancel button");
        editWikiPage.cancelWikiContent(wikiPageContent);
        Assert.assertTrue(wikiMainPage.getWikiPageContent().isEmpty(), "Wiki main page should be empty!");
    }
}
