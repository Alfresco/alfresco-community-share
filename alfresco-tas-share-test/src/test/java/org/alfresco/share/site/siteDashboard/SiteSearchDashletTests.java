package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SiteSearchDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteSearchDashlet siteSearchDashlet;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String docName = "siteSearchDoc" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.SITE_SEARCH, DashletLayout.THREE_COLUMNS, 3, 1);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, "Test content");
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        siteService.delete(adminUser, adminPassword, domain, siteName);
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2775")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed"  })
    public void siteSearchDashletTest()
    {
        LOG.info("Step 1: Verify Site Search dashlet");
        siteDashboardPage.navigate(siteName);
        assertEquals(siteSearchDashlet.getDashletTitle(), "Site Search", "Name of the dashlet is expected to be 'Site Search'.");
        assertTrue(siteSearchDashlet.isSearchFieldDisplayed(), "Dashlet is expected to have a search field.");
        assertTrue(siteSearchDashlet.isDropDownMenuDisplayed(), "Dashlet is expected to have a drop down menu with the number of items to be displayed.");
        assertTrue(siteSearchDashlet.isHelpIconDisplayed(DashletHelpIcon.SITE_SEARCH), "Dashlet is expected to have a help icon.");

        LOG.info("Step 2: Click on \"?\" icon");
        siteSearchDashlet.clickOnHelpIcon(DashletHelpIcon.SITE_SEARCH);
        assertTrue(siteSearchDashlet.isBalloonDisplayed(), "Help balloon is expected to be displayed.");
        assertEquals(siteSearchDashlet.getHelpBalloonMessage(),
            "Use this dashlet to perform a site search and view the results.\nClicking the item name takes you to the details page so you can preview or work with the item.");

        LOG.info("Step 3: Click on \"X\" icon");
        siteSearchDashlet.closeHelpBalloon();
        assertFalse(siteSearchDashlet.isBalloonDisplayed(), "Help balloon is expected to be hidden.");

        LOG.info("Step 4: Click on drop down menu");
        assertTrue(siteSearchDashlet.checkValuesFromDropDownList());

        LOG.info("Step 5: Click on \"Search\" button");
        siteSearchDashlet.clickSearchButton();
        assertTrue(siteSearchDashlet.isMessageDisplayedInDashlet("No results found."), "'No results found.' message is expected to be displayed.");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", "SiteDashboard" })
    public void searchAvailableItemsTest()
    {
        LOG.info("Step 1: Navigate to site dashboard and perform search in site search dashlet");
        siteDashboardPage.navigate(siteName);
        siteSearchDashlet.sendInputToSearchField(docName);
        siteSearchDashlet.clickSearchButton();
        Assert.assertTrue(siteSearchDashlet.isResultDisplayed(docName), docName + " is not displayed in search results");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", "SiteDashboard" })
    public void searchNoResultsAreReturnedTest()
    {
        String nonExitingDocName = "NonExisting";
        LOG.info("Step 1: Navigate to site dashboard and perform search in site search dashlet");
        siteDashboardPage.navigate(siteName);
        siteSearchDashlet.sendInputToSearchField(nonExitingDocName);
        siteSearchDashlet.clickSearchButton();
        Assert.assertFalse(siteSearchDashlet.isResultDisplayed(nonExitingDocName), nonExitingDocName + " is displayed and it shhould not be");
        Assert.assertTrue(siteSearchDashlet.isMessageDisplayedInDashlet("No results found."), "'No results found.' message is expected to be displayed.");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", "SiteDashboard" })
    public void searchWithSearchLimit()
    {
        //setup done inside the test as it would increase time for all tests as it is run in a @BeforeClass
        int docCount = 0;
        while (docCount < 29)
        {
            String random = RandomData.getRandomAlphanumeric();
            String docName = "docNameTest_" + random;
            LOG.info("Document " + docCount + " is being created");
            contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, "Test content");
            docCount++;
        }
        //testSteps
        siteDashboardPage.navigate(siteName);
        siteSearchDashlet.setSize("25");
        siteSearchDashlet.sendInputToSearchField("docNameTest");
        siteSearchDashlet.clickSearchButton();
        Assert.assertTrue(siteSearchDashlet.isSearchLimitSetTo("25"), "Size limit is not set to 25");
        Assert.assertEquals(siteSearchDashlet.getResultsNumber(), 25, "Results number is not as expected");
    }
}
