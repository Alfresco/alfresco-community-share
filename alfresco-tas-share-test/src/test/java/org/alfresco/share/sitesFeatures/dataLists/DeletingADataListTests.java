package org.alfresco.share.sitesFeatures.dataLists;

import junit.framework.Assert;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.DeleteListPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeletingADataListTests extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    DataListsService dataLists;
    
    @Autowired
    CreateDataListPopUp createDataListPopUp;
    
    @Autowired
    DeleteListPopUp deleteListPopUp;
    
    private String userName;
    private String siteName;
    private String listName = "first list";

    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, password);
    }

    @BeforeMethod(alwaysRun = true)
    public void precondition()
    {
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsPage.navigate(siteName);
        createDataListPopUp.clickCancelFormButton();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        getBrowser().refresh();
    }
    
    @TestRail(id = "C5911")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deletingExistingListWithSiteManagerUser()
    {       
        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);
        
        LOG.info("Step 2: Click on the 'Delete' button.");
        deleteListPopUp.clickDeleteButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "Successfully deleted list");
        
        LOG.info("Step 3: Check that list has been deleted and is no longed displayed in the Lists section.");
        Assert.assertEquals("The list is displayed.", dataListsPage.getListsDisplayName().contains("first list"), false);
        Assert.assertEquals("At least one list is displayed.", dataListsPage.noListDisplayed(), true);
    }
    
    @TestRail(id = "C5912")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deletingOwnExistingList()
    {       
        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);
        
        LOG.info("Step 2: Click on the 'Delete' button.");
        deleteListPopUp.clickDeleteButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "Successfully deleted list");
        
        LOG.info("Step 3: Check that list has been deleted and is no longed displayed in the Lists section.");
        Assert.assertEquals("The list is displayed.", dataListsPage.getListsDisplayName().contains("first list"), false);
        Assert.assertEquals("At least one list is displayed.", dataListsPage.noListDisplayed(), true);
    }
    
    @TestRail(id = "C5915")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingDataList()
    {   
        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel.");
        Assert.assertEquals("The 'Delete' button is not displayed.", true, dataListsPage.isDeleteButtonDisplayedForList(listName));
        
        LOG.info("Step 2: Click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);
        
        LOG.info("Step 3: Click on the 'Cancel' button.");
        deleteListPopUp.clickCancelButton();
        
        LOG.info("Step 4: Check that list has not been deleted and is still displayed in the Lists section.");
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsDisplayName().contains("first list"), true);
    }
}
