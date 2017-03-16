package org.alfresco.share.sitesFeatures.wiki;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.wiki.EditWikiPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
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

    private List<Page> pagesToAdd = new ArrayList<Page>();
    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName;
    private String wikiPageContent = "content";

    @BeforeMethod
    public void setup()
    {
        super.setup();
        pagesToAdd.add(Page.WIKI);
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, testUser);
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C5496")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createWikiMainPage()
    {
        // precondition
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        wikiPage.navigate(siteName);

        LOG.info("STEP 1: Click on edit wiki page link");
        wikiPage.clickEditPageLink();

        LOG.info("STEP 2: Type some content to the text box, then click on Save button");
        editWikiPage.saveWikiContent(wikiPageContent);
        Assert.assertEquals(wikiPage.getWikiPageContent(), wikiPageContent,
                "Wrong wiki page!, expected " + wikiPageContent + " but found " + wikiPage.getWikiPageContent());
    }

    @TestRail(id = "C5509")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelCreationOfWikiMainPage()
    {
        // precondition
        siteName = "siteName" + DataUtil.getUniqueIdentifier();
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(testUser, password, siteName, pagesToAdd);
        wikiPage.navigate(siteName);

        LOG.info("STEP 1: Click on edit wiki page link");
        wikiPage.clickEditPageLink();

        LOG.info("STEP 2: Type some content to the text box, then click on Cancel button");
        editWikiPage.cancelWikiContent(wikiPageContent);
        Assert.assertTrue(wikiPage.getWikiPageContent().isEmpty(), "Wiki main page should be empty!");
    }
}
