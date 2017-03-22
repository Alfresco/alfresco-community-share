package org.alfresco.share.sitesFeatures.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.DeleteListPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import junit.framework.Assert;

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
    private List<Page> pagesToAdd = new ArrayList<Page>();
    
    @BeforeMethod(alwaysRun = true)
    public void setupTests()
    {
        pagesToAdd.add(Page.DATALISTS);
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        createDataListPopUp.clickCancelFormButton();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        getBrowser().refresh();
    }
    
    @TestRail(id = "C5911")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deletingExistingListWithSiteManagerUser()
    {       
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);
        
        logger.info("Step 2: Click on the 'Delete' button.");
        deleteListPopUp.clickDeleteButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "Successfully deleted list");
        
        logger.info("Step 3: Check that list has been deleted and is no longed displayed in the Lists section.");
        Assert.assertEquals("The list is displayed.", dataListsPage.getListsDisplayName().contains("first list"), false);
        Assert.assertEquals("At least one list is displayed.", dataListsPage.noListDisplayed(), true);
    }
    
    @TestRail(id = "C5912")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deletingOwnExistingList()
    {       
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);
        
        logger.info("Step 2: Click on the 'Delete' button.");
        deleteListPopUp.clickDeleteButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "Successfully deleted list");
        
        logger.info("Step 3: Check that list has been deleted and is no longed displayed in the Lists section.");
        Assert.assertEquals("The list is displayed.", dataListsPage.getListsDisplayName().contains("first list"), false);
        Assert.assertEquals("At least one list is displayed.", dataListsPage.noListDisplayed(), true);
    }
    
    @TestRail(id = "C5915")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeletingDataList()
    {   
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel.");
        Assert.assertEquals("The 'Delete' button is not displayed.", true, dataListsPage.isDeleteButtonDisplayedForList(listName));
        
        logger.info("Step 2: Click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);
        
        logger.info("Step 3: Click on the 'Cancel' button.");
        deleteListPopUp.clickCancelButton();
        
        logger.info("Step 4: Check that list has not been deleted and is still displayed in the Lists section.");
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsDisplayName().contains("first list"), true);
    }
}
