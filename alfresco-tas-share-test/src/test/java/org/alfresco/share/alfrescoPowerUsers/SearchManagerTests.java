package org.alfresco.share.alfrescoPowerUsers;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.searching.SearchManagerPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
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

public class SearchManagerTests extends ContextAwareWebTest
{
    @Autowired
    SearchManagerPage searchManagerPage;

    @Autowired
    SearchPage searchPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    Toolbar toolbar;

    private String UserC8703 = String.format("UserC8703%s", RandomData.getRandomAlphanumeric());
    private String userC8704 = String.format("UserC8704%s", RandomData.getRandomAlphanumeric());
    private String userC8713 = String.format("UserC8713%s", RandomData.getRandomAlphanumeric());
    private String modifier1 = "firstName1 lastName1";
    private String alfrescoAdminsGroupName = "ALFRESCO_ADMINISTRATORS";
    private String alfrescoSearchAdministrators = "ALFRESCO_SEARCH_ADMINISTRATORS";
    private String siteC8703 = String.format("siteC8703%s", RandomData.getRandomAlphanumeric());
    private String description = "siteC8703 description";
    private String documentName = String.format("Doc%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, UserC8703, password, UserC8703 + domain, modifier1.split(" ")[0], modifier1.split(" ")[1]);
        userService.create(adminUser, adminPassword, userC8704, password, UserC8703 + domain, modifier1.split(" ")[0], modifier1.split(" ")[1]);
        userService.create(adminUser, adminPassword, userC8713, password, UserC8703 + domain, modifier1.split(" ")[0], modifier1.split(" ")[1]);
        siteService.create(UserC8703, password, domain, siteC8703, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(UserC8703, password, siteC8703, DocumentType.TEXT_PLAIN, documentName + "UserC8703", documentName + " content");
        groupService.addUserToGroup(adminUser, adminPassword, alfrescoSearchAdministrators, userC8713);
        setupAuthenticatedSession(userC8713, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, UserC8703);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + UserC8703);

        userService.delete(adminUser, adminPassword, userC8704);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC8704);

        userService.delete(adminUser, adminPassword, userC8713);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC8713);

        siteService.delete(adminUser, adminPassword, siteC8703);
    }

    @TestRail (id = "C8703")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void userHasSearchManagerRightsWhenAddedToALFRESCO_ADMINISTRATORS()
    {
        setupAuthenticatedSession(UserC8703, password);
        LOG.info("Step 1: Login with UserC8703 credentials and search for a keyword in search box and press Enter.");
        siteDashboardPage.navigate(siteC8703);
        toolbar.search("test");
        searchPage.renderedPage();
        Assert.assertFalse(searchPage.isSearchManagerDisplayed(), "'Search Manager' link is displayed on the page.");

        LOG.info("Step 2: Add user1 to the ALFRESCO_ADMINISTRATORS group, logout and log back in.");
        groupService.addUserToGroup(adminUser, adminPassword, alfrescoAdminsGroupName, UserC8703);
        userService.logout();
        getBrowser().refresh();
        setupAuthenticatedSession(UserC8703, password);
        Assert.assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "User is not directed to User Dashboard Page");

        LOG.info("Step 3: Type in a keyword in search box and press Enter.");
        toolbar.search("test");
        Assert.assertTrue(searchPage.isSearchManagerDisplayed(), "'Search Manager' link is not displayed on the page.");

        LOG.info("Step 4: Click the Search Manager link and confirm that user is directed to the Search Manager Page");
        searchPage.clickSearchManagerLink();
        searchManagerPage.renderedPage();
        Assert.assertEquals(searchManagerPage.getPageTitle(), "Alfresco » Search Manager", "Search Manager page is not accessed");
    }

    @TestRail (id = "C8704")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void userHasSearchManagerRightsWhenAddedToALFRESCO_SEARCH_ADMINISTRATORS()
    {
        setupAuthenticatedSession(userC8704, password);
        LOG.info("Step 1: Login with UserC8703 credentials and search for a keyword in search box and press Enter.");
        siteDashboardPage.navigate(siteC8703);
        toolbar.search("test");
        searchPage.renderedPage();
        Assert.assertFalse(searchPage.isSearchManagerDisplayed(), "'Search Manager' link is displayed on the page.");

        LOG.info("Step 2: Add user1 to the ALFRESCO_ADMINISTRATORS group, logout and log back in.");
        groupService.addUserToGroup(adminUser, adminPassword, alfrescoSearchAdministrators, userC8704);
        userService.logout();
        getBrowser().refresh();
        setupAuthenticatedSession(userC8704, password);
        Assert.assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "User is not directed to User Dashboard Page");

        LOG.info("Step 3: Type in a keyword in search box and press Enter.");
        toolbar.search("test");
        Assert.assertTrue(searchPage.isSearchManagerDisplayed(), "'Search Manager' link is not displayed on the page.");

        LOG.info("Step 4: Click the Search Manager link and confirm that user is directed to the Search Manager Page");
        searchManagerPage.navigate();
        Assert.assertEquals(searchManagerPage.getPageTitle(), "Alfresco » Search Manager", "Search Manager page is not accessed");
    }

    @TestRail (id = "C8713")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void editFilterProperty()
    {
        searchManagerPage.navigate();
        LOG.info("Step 1: Edit filter property and save changes");
        searchManagerPage.editFilterProperty("cm:created (Created Date)", "audio:album (Album)");

        LOG.info("Step 2: Check new filter property is present");
        getBrowser().refresh();
        Assert.assertEquals(searchManagerPage.getFilterProperty("filter_created", "audio:album (Album)"), "audio:album (Album)");
    }

}