package org.alfresco.share.sitesFeatures.dataLists;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class AccessingDataListsComponentTests extends ContextAwareWebTest
{
    @Autowired
    DataUser dataUser;

    @Autowired
    DataSite dataSite;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    CustomizeSitePage customizeSitePage;

    @Autowired
    DataListsPage dataListsPage;

    private String userName;
    private String siteName;

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
    }


    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C5844")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void onlySiteManagerIsAbleToRenameDataListsFeatures() throws DataPreparationException
    {
        LOG.info("Preconditions: Create userCollaborator, userContributor and userConsumer");
        UserModel testUser = dataUser.createRandomTestUser();
        SiteModel testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        DataUser.ListUserWithRoles ls = dataUser.addUsersWithRolesToSite(testSite, UserRole.SiteCollaborator, UserRole.SiteConsumer, UserRole.SiteContributor);
        ls.getOneUserWithRole(UserRole.SiteCollaborator);
        LOG.info("Step 1: Access 'Customize Site'");
        setupAuthenticatedSession(testUser.getUsername(), testUser.getPassword());

        siteDashboardPage.navigate(testSite.getTitle());
        siteDashboardPage.clickSiteConfiguration();
        siteDashboardPage.clickCustomizeSite();
        customizeSitePage.renderedPage();
        customizeSitePage.addPageToSite(SitePageType.DATA_LISTS);
        Assert.assertTrue(customizeSitePage.isPageAddedToCurrentPages(SitePageType.DATA_LISTS));
        LOG.info("Step 2: Rename 'Data Lists' feature");
        customizeSitePage.renamePage(SitePageType.DATA_LISTS, "Test");
        Assert.assertTrue(customizeSitePage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "Data Lists wasn't rename correctly");
        customizeSitePage.clickOk();
        LOG.info("Step 3&4: Check the new name of the 'Data Lists' feature on the 'Site Dashboard'");
        siteDashboardPage.navigate(testSite.getTitle());
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "Data Lists wasn't rename correctly");
        LOG.info("Step 5: Logout from the site manager account and login with the user account that has the Consumer role.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(ls.getOneUserWithRole(UserRole.SiteConsumer).getUsername(), password);
        LOG.info("Step 6: Access Test site and check the available Site Configuration Options.");
        siteDashboardPage.navigate(testSite.getTitle());
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"));
        LOG.info("Step 7: Check the Data Lists feature name displayed on the Site Dashboard");
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "The actual name of 'Data Lists' feature is not as expected");
        LOG.info("Step 8: Logout from the site Consumer role user account and login with the user account that has the Collaborator role.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(ls.getOneUserWithRole(UserRole.SiteCollaborator).getUsername(), password);
        LOG.info("Step 9: Access Test site and check the available Site Configuration Options.");
        siteDashboardPage.navigate(testSite.getTitle());
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"));
        LOG.info("Step 10: Check the Data Lists feature name displayed on the Site Dashboard");
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "The actual name of 'Data Lists' feature is not as expected");
        LOG.info("Step 11: Logout from the site Collaborator role user account and login with the user account that has the Contributor role.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(ls.getOneUserWithRole(UserRole.SiteContributor).getUsername(), password);
        LOG.info("Step 9: Access Test site and check the available Site Configuration Options.");
        siteDashboardPage.navigate(testSite.getTitle());
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"));
        LOG.info("Step 10: Check the Data Lists feature name displayed on the Site Dashboard");
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "The actual name of 'Data Lists' feature is not as expected");

        userService.delete(adminUser, adminPassword, testUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser.getUsername());
        userService.delete(adminUser, adminPassword, ls.getOneUserWithRole(UserRole.SiteConsumer).getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + ls.getOneUserWithRole(UserRole.SiteConsumer).getUsername());
        userService.delete(adminUser, adminPassword, ls.getOneUserWithRole(UserRole.SiteCollaborator).getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + ls.getOneUserWithRole(UserRole.SiteCollaborator).getUsername());
        userService.delete(adminUser, adminPassword, ls.getOneUserWithRole(UserRole.SiteContributor).getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + ls.getOneUserWithRole(UserRole.SiteContributor).getUsername());
        siteService.delete(adminUser, adminPassword, testSite.getTitle());

    }

    @TestRail (id = "C5846")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browsingPaneDisplay()
    {
        LOG.info("Preconditions: Create multiple Lists");
        List<String> createdDataLists = new ArrayList<>(2);
        for (int i = 0; i < 2; i++)
        {
            String contactList = "link" + System.currentTimeMillis();
            dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, contactList, "contact link description");
            createdDataLists.add(contactList);
        }
        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);

        LOG.info("Step 1: The browsing pane displays a list of all existing data lists");
        Assert.assertTrue(createdDataLists.equals(dataListsPage.getListsDisplayName()), "The actual and expected lists name are not the same.");
    }

    @TestRail (id = "C5845")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewListsFromDataLists()
    {
        LOG.info("Preconditions: Create a new List");
        String listName = "listC5845";
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);

        LOG.info("Step 1: The browsing pane displays a list of all existing data lists");
        Assert.assertTrue(dataListsPage.getListsDisplayName().contains(listName), "The actual and expected lists name are not the same.");
        Assert.assertTrue(dataListsPage.isNewListButtonDisplayed(), "New List button is not displayed.");

        LOG.info("Step 2: Click the created list displayed under Lists view.");
        dataListsPage = dataListsPage.clickContactListItem(listName);
        Assert.assertTrue(dataListsPage.isNewListButtonDisplayed(), "'New List' button is not displayed.");
        Assert.assertTrue(dataListsPage.currentContent.allFilterOptionsAreDisplayed(), "Not all filters are displayed.");
        Assert.assertTrue(dataListsPage.currentContent.isNewItemButtonDisplayed(), "'New Item' button is not displayed.");
        Assert.assertTrue(dataListsPage.currentContent.isSelectButtonDisplayed(), "'Select' button is not displayed.");
        Assert.assertFalse(dataListsPage.currentContent.isSelectItemsButtonEnabled(), "'Select items' button is enabled.");

        LOG.info("Step 3: Mouse over the list displayed under Lists");
        getBrowser().waitInSeconds(5);
        Assert.assertTrue(dataListsPage.isEditButtonDisplayedForList(listName), "'Edit' button is displayed.");
        Assert.assertTrue(dataListsPage.isDeleteButtonDisplayedForList(listName), "'Delete' button is displayed.");
    }
}
