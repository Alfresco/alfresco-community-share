package org.alfresco.share.sitesFeatures.dataLists;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent.ListColumns;
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

import static org.testng.Assert.assertTrue;

public class ViewingDataListsTests extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    private String userName;
    private String siteName;

    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        userName = "User" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, password);
    }

    @BeforeMethod(alwaysRun = true)
    public void precondition()
    {
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
    }
    
    @TestRail(id = "C5853")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void filterAreVisibleOnlyIfAListIsSelected()
    {       
        logger.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: Check the Items filter");
        Assert.assertFalse(dataListsPage.currentContent.allFilterOptionsAreDisplayed(), "The filter options are displayed.");
        
        logger.info("Step 2: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        
        logger.info("Step 3: Check the available filtering options.");
        assertTrue(dataListsPage.currentContent.allFilterOptionsAreDisplayed(), "Not all filters are displayed.");
    }
    
    @TestRail(id = "C5854")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void selectAListAndApplyAllFilter()
    {       
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }
    
    @TestRail(id = "C5855")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void selectAListAndApplyRecentlyAddedFilter()
    {       
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }
    
    @TestRail(id = "C5856")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void selectAListAndApplyRecentlyModifiedFilter()
    {       
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }
    
    @TestRail(id = "C5857")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void selectAListAndApplyCreatedByMeFilter()
    {       
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }
    
    @TestRail(id = "C5858")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multiplePagesViewedInDataListsExplorerPanel()
    {       
        logger.info("Preconditions: Create a new List and add new items to it");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        for(int i=0; i<51; i++)
        {
            dataListsService.addContactListItem(userName, password, siteName, listName, userName+i, userName+i, userName+i + domain, "companyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        assertTrue(dataListsPage.currentContent.areNavigationLinksDisplayed(), "The navigation links are not displayed.");
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "1", "The current page is not the expected one.");
        
        logger.info("Step 2: Click on the 'next' navigation item.");
        dataListsPage.currentContent.clickNextNavigationItem();
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "2", "The current page is not the expected one.");
        
        logger.info("Step 3: Click on the 'previous' navigation item.");
        dataListsPage.currentContent.clickPreviousNavigationItem();
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "1", "The current page is not the expected one.");
        
        logger.info("Step 3: Click on page number '2'.");
        dataListsPage.currentContent.clickOnSpecificPage("2");
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "2", "The current page is not the expected one.");
    }
    
    @TestRail(id = "C5859")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void dataListItemsSortedByColumn()
    {       
        logger.info("Preconditions: Create a new List and add two items to it, descendent ordered by Company Name");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        for(int i=0; i<2; i++)
        {
            dataListsService.addContactListItem(userName, password, siteName, listName, userName+i, userName+i, userName+i + domain, i+"comapanyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        
        logger.info("Step 2: In the table click 'Company' column headings and check the items are sorted by Company Name.");
        assertTrue(dataListsPage.currentContent.areItemsSortedByColumnAfterClickingTheColumn(ListColumns.Company.toString()), "The data list items are sorted by Company");
    }
    
    @TestRail(id = "C5860")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void aSinglePageOfListItemsViewed()
    {       
        logger.info("Preconditions: Create a new List and add two items to it, descendent ordered by Company Name");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        for(int i=0; i<2; i++)
        {
            dataListsService.addContactListItem(userName, password, siteName, listName, userName+i, userName+i, userName+i + domain, i+"comapanyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }
        
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        
        logger.info("Step 2: Click the 'next' navigation item.");
        dataListsPage.currentContent.clickNextNavigationItem();
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "1", "The current page is not the expected one.");
        
        logger.info("Step 3: Click on the 'previous' navigation item.");
        dataListsPage.currentContent.clickPreviousNavigationItem();
        Assert.assertEquals(dataListsPage.currentContent.getCurrentPageNumber(), "1", "The current page is not the expected one.");
    }
}