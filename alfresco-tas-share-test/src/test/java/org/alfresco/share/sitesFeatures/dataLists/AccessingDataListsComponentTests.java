package org.alfresco.share.sitesFeatures.dataLists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.common.UserData;
import org.alfresco.dataprep.DataListsService;
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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AccessingDataListsComponentTests extends ContextAwareWebTest
{
    
    @Autowired
    DataUtil dataUtil;
    
    @Autowired
    SiteDashboardPage siteDashboardPage;
    
    @Autowired
    CustomizeSitePage customizeSitePage;
    
    @Autowired
    DataListsService dataLists;
    
    @Autowired
    DataListsPage dataListsPage;
    
    private String userName;
    private String siteName;
    private List<Page> pagesToAdd = new ArrayList<Page>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(Page.DATALISTS);
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        setupAuthenticatedSession(userName, password);
    }
    
    @TestRail(id = "C5844")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void onlySiteManagerIsAbleToRenameDataListsFeatures()
    {    
        siteDashboardPage.navigate(siteName);
        
        logger.info("Preconditions: Create userCollaborator, userContributor and userConsumer");
        List<UserData> usersData = dataUtil.createUsersWithRoles(Arrays.asList("SiteCollaborator", "SiteContributor", "SiteConsumer"), userName, siteName);
        
        logger.info("Step 1: Access 'Customize Site'");
        siteDashboardPage.clickSiteConfiguration();
        siteDashboardPage.clickCustomizeSite();
        Assert.assertTrue(customizeSitePage.isPageAddedToCurrentPages(SitePageType.DATA_LISTS));
        
        logger.info("Step 2: Rename 'Data Lists' feature");
        customizeSitePage.renamePage(SitePageType.DATA_LISTS, "Test");
        Assert.assertTrue(customizeSitePage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "Data Lists wasn't rename correctly");
        customizeSitePage.clickOk();
        
        logger.info("Step 3: Check the new name of the 'Data Lists' feature on the 'Site Dashboard'");
        Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
        Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "Data Lists wasn't rename correctly");

        for(UserData user: usersData)
        {
            logger.info("Login as " + user.getUserRole());
            cleanupAuthenticatedSession();
            setupAuthenticatedSession(user.getUserName(), DataUtil.PASSWORD);
            
            logger.info("'Customize Site' is not accessible");
            siteDashboardPage.navigate(siteName);
            siteDashboardPage.clickSiteConfiguration();
            Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"));
            
            logger.info("Check the new name of the 'Data Lists' feature on the 'Site Dashboard'");
            Assert.assertTrue(siteDashboardPage.isPageAddedToDashboard(SitePageType.DATA_LISTS));
            Assert.assertTrue(siteDashboardPage.getPageDisplayName(SitePageType.DATA_LISTS).equals("Test"), "The actual name of 'Data Lists' feature is not as expected");
        } 
    }
    
    @TestRail(id = "C5846")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browsingPaneDisplay()
    {    
        logger.info("Preconditions: Create multiple Lists");
        List<String> createdDataLists = new ArrayList<String>(2);
        for(int i=0; i<2; i++)
        {
            String contactList = "link" + System.currentTimeMillis();
            dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, contactList, "contact link description");
            createdDataLists.add(contactList);
        }
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: The browsing pane displays a list of all existing data lists");
        Assert.assertTrue(createdDataLists.equals(dataListsPage.getListsDisplayName()), "The actual and expected lists name are not the same.");
    }
    
    @TestRail(id = "C5845")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewListsFromDataLists()
    {    
        logger.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: The browsing pane displays a list of all existing data lists");
        Assert.assertTrue(dataListsPage.getListsDisplayName().contains(listName), "The actual and expected lists name are not the same.");
        Assert.assertTrue(dataListsPage.isNewListButtonDisplayed(), "New List button is not displayed.");
        
        logger.info("Step 2: Click the created list displayed under Lists view.");
        dataListsPage = dataListsPage.clickContactListItem(listName);
        Assert.assertTrue(dataListsPage.isNewListButtonDisplayed(), "'New List' button is not displayed.");
        Assert.assertTrue(dataListsPage.currentContent.allFilterOptionsAreDisplayed(), "Not all filters are displayed.");
        Assert.assertTrue(dataListsPage.currentContent.isNewItemButtonDisplayed(), "'New Item' button is not displayed.");
        Assert.assertTrue(dataListsPage.currentContent.isSelectButtonDisplayed(), "'Select' button is not displayed.");
        Assert.assertFalse(dataListsPage.currentContent.isSelectItemsButtonEnabled(), "'Select items' button is enabled.");
        
        logger.info("Step 3: Hoover mouse over the list displayed under Lists");
        Assert.assertTrue(dataListsPage.isEditButtonDisplayedForList(listName), "'Edit' button is not displayed.");
        Assert.assertTrue(dataListsPage.isDeleteButtonDisplayedForList(listName), "'Delete' button is not displayed.");
    }
}
