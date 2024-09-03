package org.alfresco.share.sitesFeatures.dataLists;

import static org.alfresco.po.share.site.SiteConfigurationOptions.CUSTOMIZE_SITE;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j
public class AccessingDataListsComponentTests extends BaseTest
{
    @Autowired
    DataUser dataUser;

    @Autowired
    DataSite dataSite;
    private SiteDashboardPage siteDashboardPage;

    private CustomizeSitePage customizeSitePage;

    private DataListsPage dataListsPage;

    @Autowired
    ContentService contentService;

    @Autowired
    UserService userService;

    @Autowired
    DataListsService dataListsService;

    @Autowired
    SiteService siteService;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void precondition()
    {
        log.info("Precondition: Any Test user is created");
        dataListsPage = new DataListsPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        customizeSitePage = new CustomizeSitePage (webDriver);
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        siteService.addPageToSite(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        siteService.delete(user.get().getUsername(), user.get().getPassword(), siteName.get().getId());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C5844")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, TestGroup.INTEGRATION })
    public void onlySiteManagerIsAbleToRenameDataListsFeatures() throws DataPreparationException
    {
        log.info("Preconditions: Create userCollaborator, userContributor and userConsumer");
        UserModel testUser = dataUser.createRandomTestUser();
        SiteModel testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        DataUser.ListUserWithRoles ls = dataUser.addUsersWithRolesToSite(testSite, UserRole.SiteCollaborator, UserRole.SiteConsumer, UserRole.SiteContributor);
        ls.getOneUserWithRole(UserRole.SiteCollaborator);
        log.info("Step 1: Access 'Customize Site'");
        authenticateUsingLoginPage(testUser);

        siteDashboardPage.navigate(testSite.getTitle());
        siteDashboardPage.openSiteConfiguration();
        siteDashboardPage.selectCustomizeSite();
        customizeSitePage.addPageToSite(SitePageType.DATA_LISTS);
        Assert.assertTrue(customizeSitePage.isPageAddedToCurrentPages(SitePageType.DATA_LISTS));
        log.info("Step 2: Rename 'Data Lists' feature");
        customizeSitePage.renameSitePage(SitePageType.DATA_LISTS, "Test");
        Assert.assertTrue(customizeSitePage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "Data Lists wasn't rename correctly");
        customizeSitePage.saveChanges();
        log.info("Step 3&4: Check the new name of the 'Data Lists' feature on the 'Site Dashboard'");
        siteDashboardPage.navigate(testSite.getTitle());
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "Data Lists wasn't rename correctly");
        log.info("Step 5: Logout from the site manager account and login with the user account that has the Consumer role.");
        authenticateUsingLoginPage(user.get());
        log.info("Step 6: Access Test site and check the available Site Configuration Options.");
        siteDashboardPage.navigate(testSite.getTitle());
        siteDashboardPage.openSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"));
        log.info("Step 7: Check the Data Lists feature name displayed on the Site Dashboard");
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "The actual name of 'Data Lists' feature is not as expected");
        log.info("Step 8: Logout from the site Consumer role user account and login with the user account that has the Collaborator role.");
        authenticateUsingLoginPage(user.get());
        log.info("Step 9: Access Test site and check the available Site Configuration Options.");
        siteDashboardPage.navigate(testSite.getTitle());
        siteDashboardPage.openSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"));
        log.info("Step 10: Check the Data Lists feature name displayed on the Site Dashboard");
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "The actual name of 'Data Lists' feature is not as expected");
        log.info("Step 11: Logout from the site Collaborator role user account and login with the user account that has the Contributor role.");
        authenticateUsingLoginPage(user.get());
        log.info("Step 9: Access Test site and check the available Site Configuration Options.");
        siteDashboardPage.navigate(testSite.getTitle());
        siteDashboardPage.openSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"));
        log.info("Step 10: Check the Data Lists feature name displayed on the Site Dashboard");
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "The actual name of 'Data Lists' feature is not as expected");
    }

    @TestRail (id = "C5846")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browsingPaneDisplay()
    {
        log.info("Preconditions: Create multiple Lists");
        List<String> createdDataLists = new ArrayList<>(2);
        for (int i = 0; i < 2; i++)
        {
            String contactList = "link" + System.currentTimeMillis();
            dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, contactList, "contact link description");
            createdDataLists.add(contactList);
        }
        authenticateUsingLoginPage(user.get());
        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: The browsing pane displays a list of all existing data lists");
        Assert.assertTrue(createdDataLists.equals(dataListsPage.getListsItemsTitle()), "The actual and expected lists name are not the same.");
    }

    @TestRail (id = "C5845")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewListsFromDataLists()
    {
        log.info("Preconditions: Create a new List");
        String listName = "listC5845";
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact link description");

        authenticateUsingLoginPage(user.get());
        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: The browsing pane displays a list of all existing data lists");
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(listName), "The actual and expected lists name are not the same.");

        log.info("Step 2: Click the created list displayed under Lists view.");
        dataListsPage = dataListsPage.clickContactListItem(listName);
        Assert.assertTrue(dataListsPage.allFilterOptionsAreDisplayed(), "Not all filters are displayed.");
        Assert.assertTrue(dataListsPage.isNewItemButtonDisplayed(), "'New Item' button is not displayed.");
        Assert.assertTrue(dataListsPage.isSelectButtonDisplayed(), "'Select' button is not displayed.");
        Assert.assertFalse(dataListsPage.isSelectItemsButtonEnabled(), "'Select items' button is enabled.");

        log.info("Step 3: Mouse over the list displayed under Lists");
        Assert.assertTrue(dataListsPage.isEditButtonDisplayedForList(listName), "'Edit' button is displayed.");
        Assert.assertTrue(dataListsPage.isDeleteButtonDisplayedForList(listName), "'Delete' button is displayed.");
    }
}
