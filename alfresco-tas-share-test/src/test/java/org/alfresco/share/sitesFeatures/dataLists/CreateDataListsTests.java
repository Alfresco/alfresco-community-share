package org.alfresco.share.sitesFeatures.dataLists;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp.DataListTypes;
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

public class CreateDataListsTests extends ContextAwareWebTest
{

    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    CreateDataListPopUp createDataListPopUp;
    
    @Autowired
    DataListsService dataLists;
    
    private String userName;
    private String siteName;
    private String dataListName = "Test List";
    
    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        userName = String.format("User%s", DataUtil.getUniqueIdentifier());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, password);
    }
    
    @BeforeMethod(alwaysRun = true)
    public void createSite()
    {
        siteName = String.format("siteName%s", DataUtil.getUniqueIdentifier());
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsPage.navigate(siteName);
        createDataListPopUp.clickCancelFormButton();
    }
    
    @TestRail(id = "C5861")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewList()
    {       
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();
        
        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.toString());
        createDataListPopUp.isExpectedTypeSelected(DataListTypes.ContactList.toString());
        
        LOG.info("Step 3: Provide an input for Title.");
        createDataListPopUp.typeTitleName(dataListName);
        
        LOG.info("Step 4: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");
        
        LOG.info("Step 5: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertEquals(dataListsPage.getListsDisplayName().contains(dataListName), true, "The data list was not created.");
    }
    
    @TestRail(id = "C5862")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCantBeCreatedWithoutSelectingListType()
    {       
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();
        
        LOG.info("Step 2: Provide an input for Title.");
        createDataListPopUp.typeTitleName(dataListName);
        
        LOG.info("Step 3: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");
        
        LOG.info("Step 4: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertEquals(createDataListPopUp.getInvalidDataListBalloonMessage(), ("The value cannot be empty."), "The pop-up message isn't as expected.");
        Assert.assertEquals(dataListsPage.getListsDisplayName().contains(dataListName), false, "The data list was created.");
        Assert.assertEquals(dataListsPage.noListDisplayed(), true, "It shouldn't be any list created.");
    }
    
    @TestRail(id = "C5863")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCantBeCreatedWithEmptyTitle()
    {       
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();
        
        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.toString());
        
        LOG.info("Step 3: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");
        
        LOG.info("Step 4: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertEquals(createDataListPopUp.invalidTitleBalloonMessage(), ("The value cannot be empty."), "The pop-up message isn't as expected.");
        Assert.assertEquals(dataListsPage.getListsDisplayName().contains(dataListName), false,  "The data list was created.");
        Assert.assertEquals(dataListsPage.noListDisplayed(), true, "It shouldn't be any list created.");
    }
    
    @TestRail(id = "C5864")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCreatedWithEmptyDescription()
    {       
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();
        
        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.toString());
        
        LOG.info("Step 3: Provide an input for Title.");
        createDataListPopUp.typeTitleName(dataListName);
        
        LOG.info("Step 4: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertEquals(dataListsPage.getListsDisplayName().contains(dataListName), true,  "The data list was not created.");
    }
    
    @TestRail(id = "C5865")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCreatedWithExistingTitle()
    {    
        LOG.info("Preconditions: Create a new List with 'Test list' name.");
        String listName = "Test list";
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();
        
        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.toString());
        
        LOG.info("Step 3: Provide 'Test list' for Title.");
        createDataListPopUp.typeTitleName(dataListName);
        
        LOG.info("Step 4: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertEquals(dataListsPage.getListsDisplayName().contains(dataListName), true, "The data list was not created.");
    }
    
    @TestRail(id = "C5881")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreationOfNewDataList()
    {    
        
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();
        
        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.toString());
        
        LOG.info("Step 3: Provide 'Test list' for Title.");
        createDataListPopUp.typeTitleName(dataListName);
        
        LOG.info("Step 4: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");
        
        LOG.info("Step 5: Click on the 'Cancel' button.");
        createDataListPopUp.clickCancelFormButton();
        Assert.assertEquals(dataListsPage.getListsDisplayName().contains(dataListName), false, "The data list was created.");
        Assert.assertEquals(dataListsPage.noListDisplayed(), true, "It shouldn't be any list created.");
    }
    
    @TestRail(id = "C5882")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void closeCreationWindowOfNewDataList()
    {    
        
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();
        
        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.toString());
        
        LOG.info("Step 3: Provide 'Test list' for Title.");
        createDataListPopUp.typeTitleName(dataListName);
        
        LOG.info("Step 4: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");
        
        LOG.info("Step 5: Click on the 'X' button to close the 'New List' form.");
        createDataListPopUp.clickClose();
        Assert.assertEquals(dataListsPage.getListsDisplayName().contains(dataListName), false, "The data list was created.");
        Assert.assertEquals(dataListsPage.noListDisplayed(), true, "It shouldn't be any list created.");
    }
    
    @TestRail(id = "C5892")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListDoesNotContainAnyListItems()
    {    
        
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();
        
        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.toString());
        
        LOG.info("Step 3: Provide 'Test list' for Title.");
        createDataListPopUp.typeTitleName(dataListName);
        
        LOG.info("Step 4: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");
        
        LOG.info("Step 5: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertEquals(dataListsPage.successfullyCreatedDataListMessage(), "New Data List '"+dataListName+"' successfully created.", "The message of successfully creation isn't as expected.");
        Assert.assertEquals(dataListsPage.getListsDisplayName().contains(dataListName), true, "The data list was not created.");
        
        LOG.info("Step 6: Select the 'Test List' created from the Lists browsing panel.");
        dataListsPage.clickContactListItem(dataListName);
        Assert.assertEquals(dataListsPage.currentContent.areNavigationLinksDisplayed(), true, "The navigation links are not displayed.");
        Assert.assertEquals(dataListsPage.currentContent.isAnyListItemDisplayed(), false, "Some List Items are displayed.");
    }
}
