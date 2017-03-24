package org.alfresco.share.sitesFeatures.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditListDetailsPopUp;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class EditingTheListDetailsTests extends ContextAwareWebTest
{

    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    DataListsService dataLists;
    
    @Autowired
    CreateDataListPopUp createDataListPopUp;
    
    @Autowired
    EditListDetailsPopUp editListDetailsPopUp;
    
    private String userName;
    private String siteName;
    private String listName = "first list";
    private List<Page> pagesToAdd = new ArrayList<Page>();
    
    @BeforeMethod(alwaysRun = true)
    public void setup()
    {
        super.setup();
        pagesToAdd.add(Page.DATALISTS);
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        createDataListPopUp.clickCancelFormButton();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        getBrowser().refresh();
    }
    
    @TestRail(id = "C5894")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void modifyTitleAndDescriptionOfAnExistingList()
    {       
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);
        
        logger.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUp.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUp.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUp.isSaveButtonDisplayed(), true);
        
        logger.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");
        
        logger.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUp.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");
        
        logger.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsDisplayName().contains("new Title"), true);
    }
    
    @TestRail(id = "C5895")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelModifyingTitleAndDescriptionOfAnExistingList()
    {       
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);
        
        logger.info("Step 2: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");
        
        logger.info("Step 3: Click on the 'Cancel' button.");
        editListDetailsPopUp.clickCancelButton();
        
        logger.info("Step 4: Check and confirm that the list not has been updated.");
        Assert.assertEquals("The updated list is displayed.", dataListsPage.getListsDisplayName().contains("new Title"), false);
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsDisplayName().contains(listName), true);
    }
    
    @TestRail(id = "C5896")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void closeEditFormWithoutSavingChanges()
    {       
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);
        
        logger.info("Step 2: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");
        
        logger.info("Step 3: Click on the 'X' button.");
        editListDetailsPopUp.clickCloseButton();
        
        logger.info("Step 4: Check and confirm that the list not has been updated.");
        Assert.assertEquals("The updated list is displayed.", dataListsPage.getListsDisplayName().contains("new Title"), false);
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsDisplayName().contains(listName), true);
    }
    
    @TestRail(id = "C5898")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editingExistingListWithSiteCollaboratorUser()
    {      
        
        logger.info("Preconditions: Create a user with 'Collaborator' role");
        String collaborator = "Collaborator" + DataUtil.getUniqueIdentifier();
        userService.create(properties.getAdminUser(), properties.getAdminPassword(), collaborator, password, "collaborator@tests.com", "collaborator", "collaborator");
        userService.createSiteMember(userName, password, collaborator, siteName, "SiteCollaborator");
        setupAuthenticatedSession(collaborator, password);
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);
        
        logger.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUp.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUp.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUp.isSaveButtonDisplayed(), true);
        
        logger.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");
        
        logger.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUp.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");
        
        logger.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsDisplayName().contains("new Title"), true);
    }
    
    @TestRail(id = "C5899")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void contributorRoleIsNotAbleToEditExistingList()
    {      
        
        logger.info("Preconditions: Create a user with 'Contributor' role");
        String contributor = "Contributor" + DataUtil.getUniqueIdentifier();
        userService.create(properties.getAdminUser(), properties.getAdminPassword(), contributor, password, "collaborator@tests.com", "collaborator", "collaborator");
        userService.createSiteMember(userName, password, contributor, siteName, "SiteContributor");
        setupAuthenticatedSession(contributor, password);
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        Assert.assertEquals("The edit button is enabled", dataListsPage.isEditButtonDisabled(listName), true);
        
        logger.info("Step 2: Click on the Edit button.");
        dataListsPage.clickOnDisabledEditButton(listName);
        
    }
    
    @TestRail(id = "C5900")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void consumerRoleIsNotAbleToEditExistingList()
    {      
        
        logger.info("Preconditions: Create a user with 'Consumer' role");
        String consumer = "Consumer" + DataUtil.getUniqueIdentifier();
        userService.create(properties.getAdminUser(), properties.getAdminPassword(), consumer, password, "collaborator@tests.com", "collaborator", "collaborator");
        userService.createSiteMember(userName, password, consumer, siteName, "SiteConsumer");
        setupAuthenticatedSession(consumer, password);
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        Assert.assertEquals("The edit button is enabled", dataListsPage.isEditButtonDisabled(listName), true);
        
        logger.info("Step 2: Click on the Edit button.");
        dataListsPage.clickOnDisabledEditButton(listName);
        
    }
    
    @TestRail(id = "C5901")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void contributorRoleIsAbleToEditListCreatedBySameUser()
    {      
        
        logger.info("Preconditions: Create a user with 'Collaborator' role and a list");
        String contributor = "Contributor" + DataUtil.getUniqueIdentifier();
        userService.create(properties.getAdminUser(), properties.getAdminPassword(), contributor, password, "Contributor@tests.com", "Contributor", "Contributor");
        userService.createSiteMember(userName, password, contributor, siteName, "SiteContributor");
        setupAuthenticatedSession(contributor, password);
        dataListsPage.navigate(siteName);
        
        String ownDataList = "ownDataList";
        dataLists.createDataList(contributor, password, siteName, DataList.CONTACT_LIST, ownDataList, "contact link description");
        getBrowser().refresh();
        
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(ownDataList);
        
        logger.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUp.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUp.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUp.isSaveButtonDisplayed(), true);
        
        logger.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");
        
        logger.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUp.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");
        
        logger.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsDisplayName().contains("new Title"), true);
        
    }
    
    @TestRail(id = "C5904")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void siteManagerIsAbleToEditList()
    {      
        
        logger.info("Preconditions: Create a user with 'Collaborator' role and a list");
        String manager = "Manager" + DataUtil.getUniqueIdentifier();
        userService.create(properties.getAdminUser(), properties.getAdminPassword(), manager, password, "manager@tests.com", "manager", "manager");
        userService.createSiteMember(userName, password, manager, siteName, "SiteManager");
        setupAuthenticatedSession(manager, password);
        dataListsPage.navigate(siteName);
        
        logger.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);
        
        logger.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUp.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUp.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUp.isSaveButtonDisplayed(), true);
        
        logger.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");
        
        logger.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUp.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");
        
        logger.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsDisplayName().contains("new Title"), true);
        
    }
}
