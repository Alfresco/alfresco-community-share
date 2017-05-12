package org.alfresco.share.sitesFeatures.dataLists;

import org.alfresco.common.DataUtil;
import org.alfresco.common.UserData;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessingDataListsComponentTests extends ContextAwareWebTest
{
    @Autowired
    DataUtil dataUtil;
    
    @Autowired
    SiteDashboardPage siteDashboardPage;
    
    @Autowired
    CustomizeSitePage customizeSitePage;
    
    @Autowired
    DataListsPage dataListsPage;
    
    private String userName;
    private String siteName;
    
    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        userName = String.format("User%s", DataUtil.getUniqueIdentifier());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
    }

    @BeforeMethod(alwaysRun = true)
    public void precondition()
    {
        siteName = String.format("siteName%s", DataUtil.getUniqueIdentifier());
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
    }
    
    @TestRail(id = "C5844")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void onlySiteManagerIsAbleToRenameDataListsFeatures()
    {
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        
        LOG.info("Preconditions: Create userCollaborator, userContributor and userConsumer");
        List<UserData> usersData = dataUtil.createUsersWithRoles(Arrays.asList("SiteCollaborator", "SiteContributor", "SiteConsumer"), userName, siteName);
        
        LOG.info("Step 1: Access 'Customize Site'");
        siteDashboardPage.clickSiteConfiguration();
        siteDashboardPage.clickCustomizeSite();
        Assert.assertTrue(customizeSitePage.isPageAddedToCurrentPages(SitePageType.DATA_LISTS));
        
        LOG.info("Step 2: Rename 'Data Lists' feature");
        customizeSitePage.renamePage(SitePageType.DATA_LISTS, "Test");
        Assert.assertTrue(customizeSitePage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "Data Lists wasn't rename correctly");
        customizeSitePage.clickOk();
        
        LOG.info("Step 3: Check the new name of the 'Data Lists' feature on the 'Site Dashboard'");
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "Data Lists wasn't rename correctly");

        for(UserData user: usersData)
        {
            LOG.info("Login as " + user.getUserRole());
            cleanupAuthenticatedSession();
            setupAuthenticatedSession(user.getUserName(), password);
            
            LOG.info("'Customize Site' is not accessible");
            siteDashboardPage.navigate(siteName);
            siteDashboardPage.clickSiteConfiguration();
            Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"));
            
            LOG.info("Check the new name of the 'Data Lists' feature on the 'Site Dashboard'");
            Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
            Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "The actual name of 'Data Lists' feature is not as expected");
        } 
    }
    
    @TestRail(id = "C5846")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browsingPaneDisplay()
    {    
        LOG.info("Preconditions: Create multiple Lists");
        List<String> createdDataLists = new ArrayList<>(2);
        for(int i=0; i<2; i++)
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
    
    @TestRail(id = "C5845")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
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
        Assert.assertTrue(dataListsPage.isEditButtonDisplayedForList(listName), "'Edit' button is displayed.");
        Assert.assertTrue(dataListsPage.isDeleteButtonDisplayedForList(listName), "'Delete' button is displayed.");
    }
}
