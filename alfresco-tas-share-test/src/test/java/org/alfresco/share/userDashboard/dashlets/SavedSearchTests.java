package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.ConfigureSavedSearchDashletPopUp;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SavedSearchTests extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SavedSearchDashlet savedSearchDashlet;

    @Autowired
    ConfigureSavedSearchDashletPopUp configureSavedSearchPopUp;

    private String userName;
    private String siteName = "SearchResultSite" + RandomData.getRandomAlphanumeric();
    private String docName = "SearchResultDoc" + RandomData.getRandomAlphanumeric();
    private String content = "search results";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        userService.addDashlet(userName, password, DashboardCustomization.UserDashlet.SAVED_SEARCH, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, content);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @TestRail (id = "C2427")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD, "tobefixed"  })
    public void savedSearchDashlet()
    {
        userDashboardPage.navigate(userName);

        LOG.info("Step 1: Verify 'Saved Search' dahslet");
        Assert.assertEquals(savedSearchDashlet.getDashletTitle(), "Saved Search");
        Assert.assertEquals(savedSearchDashlet.getDefaultMessage(), "No results found.");
        Assert.assertTrue(savedSearchDashlet.isConfigureDashletIconDisplayed());
        Assert.assertTrue(savedSearchDashlet.isHelpIconDisplayed(DashletHelpIcon.SAVED_SEARCH));

        LOG.info("Step 2: Click Help icon");
        savedSearchDashlet.clickOnHelpIcon(DashletHelpIcon.SAVED_SEARCH);
        Assert.assertTrue(savedSearchDashlet.isBalloonDisplayed());
        Assert.assertEquals(savedSearchDashlet.getHelpBalloonMessage(), "Use this dashlet to set up a search and view the results."
            + "\nConfigure the dashlet to save the search and set the title text of the dashlet."
            + "\nOnly a Site Manager can configure the search and title - this dashlet is ideal for generating report views in a site.");

        LOG.info("Step 3: Close ballon popup");
        savedSearchDashlet.closeHelpBalloon();
        Assert.assertFalse(savedSearchDashlet.isBalloonDisplayed());

        LOG.info("Step 4: Click 'Configure this dashlet' icon");
        savedSearchDashlet.clickOnConfigureDashletIcon();

        LOG.info("Step 5: Verify 'Enter Search Term' window");
        Assert.assertEquals(configureSavedSearchPopUp.getPopUpTitle(), "Enter Search Term");
        Assert.assertTrue(configureSavedSearchPopUp.isSearchTermFieldDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isTitleFieldDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isSearchLimitDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isOkButtonDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isCloseButtonDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isCancelButtonDisplayed());

        LOG.info("Step 6: Close 'Enter Search Term' window");
        configureSavedSearchPopUp.clickCloseButton();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.USER_DASHBOARD }, priority = 1)
    public void getSearchItemsWithEmptyResult()
    {
        String title = "RandomGeneratedString";
        LOG.info("Step 1: Edit saved search and search for item that does not exits so that no results are returned.");
        userDashboardPage.navigate(userName);
        savedSearchDashlet.clickOnConfigureDashletIcon();
        configureSavedSearchPopUp.setSearchTermField(RandomData.getRandomAlphanumeric());
        configureSavedSearchPopUp.setTitleField("RandomGeneratedString");
        configureSavedSearchPopUp.clickOkButton();
        userDashboardPage.renderedPage();
        Assert.assertEquals(savedSearchDashlet.getDashletTitle(), title, " is not the title of the Dashlet");
        Assert.assertEquals(savedSearchDashlet.getResultsText(), "No results found.", "Results are found");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.USER_DASHBOARD }, priority = 3)
    public void verifySavedSearchResult()
    {
        String title = "valid search";
        LOG.info("Step 1: Navigate to user dashboard and search for exiting document");
        userDashboardPage.navigate(userName);
        savedSearchDashlet.renderedPage();
        savedSearchDashlet.clickOnConfigureDashletIcon();
        configureSavedSearchPopUp.setTitleField(title);
        configureSavedSearchPopUp.setSearchTermField(docName);
        configureSavedSearchPopUp.clickOkButton();
        userDashboardPage.renderedPage();
        LOG.info("Step 2: Verify the saved search results");
        Assert.assertEquals(savedSearchDashlet.getDashletTitle(), title, "Title is not correct");
        Assert.assertTrue(savedSearchDashlet.isSearchResultItemDisplayed(docName), docName + " is not displayed");
        Assert.assertTrue(savedSearchDashlet.isSearchResultItemDisplayed("Document in site " + siteName));
        Assert.assertTrue(savedSearchDashlet.isSearchResultItemDisplayed("In folder: /"));
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.USER_DASHBOARD }, priority = 2)
    public void verifySavedSearchLimit()
    {
        userDashboardPage.navigate(userName);
        savedSearchDashlet.renderedPage();
        savedSearchDashlet.clickOnConfigureDashletIcon();
        Assert.assertTrue(configureSavedSearchPopUp.isLimitValueDisplayed("10"), "10 limit value is not displayed");
        Assert.assertTrue(configureSavedSearchPopUp.isLimitValueDisplayed("25"), "25 limit value is not displayed");
        Assert.assertTrue(configureSavedSearchPopUp.isLimitValueDisplayed("50"), "50 limit value is not displayed");
        Assert.assertTrue(configureSavedSearchPopUp.isLimitValueDisplayed("100"), "100 limit value is not displayed");
        Assert.assertFalse(configureSavedSearchPopUp.isLimitValueDisplayed("5"), "5 limit value is displayed");
        Assert.assertFalse(configureSavedSearchPopUp.isLimitValueDisplayed("0"), "0 limit value is displayed");
        Assert.assertFalse(configureSavedSearchPopUp.isLimitValueDisplayed("99"), "99 limit value is displayed");
    }

}
