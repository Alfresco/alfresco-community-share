package org.alfresco.share.sitesFeatures.dataLists;

import junit.framework.Assert;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditListDetailsPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EditingTheListDetailsTests extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    CreateDataListPopUp createDataListPopUp;

    @Autowired
    EditListDetailsPopUp editListDetailsPopUp;

    private String userName;
    private String siteName;
    private String listName = "first list";

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
    }

    @BeforeMethod (alwaysRun = true)
    public void precondition()
    {
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        createDataListPopUp.clickCancelFormButton();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");
        getBrowser().refresh();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanupMethod()
    {
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C5894")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void modifyTitleAndDescriptionOfAnExistingList()
    {
        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        LOG.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUp.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUp.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUp.isSaveButtonDisplayed(), true);

        LOG.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");

        LOG.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUp.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");

        LOG.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsDisplayName().contains("new Title"), true);
    }

    @TestRail (id = "C5895")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelModifyingTitleAndDescriptionOfAnExistingList()
    {
        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        LOG.info("Step 2: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");

        LOG.info("Step 3: Click on the 'Cancel' button.");
        editListDetailsPopUp.clickCancelButton();

        LOG.info("Step 4: Check and confirm that the list not has been updated.");
        Assert.assertEquals("The updated list is displayed.", dataListsPage.getListsDisplayName().contains("new Title"), false);
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsDisplayName().contains(listName), true);
    }

    @TestRail (id = "C5896")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void closeEditFormWithoutSavingChanges()
    {
        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        LOG.info("Step 2: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");

        LOG.info("Step 3: Click on the 'X' button.");
        editListDetailsPopUp.clickCloseButton();

        LOG.info("Step 4: Check and confirm that the list not has been updated.");
        Assert.assertEquals("The updated list is displayed.", dataListsPage.getListsDisplayName().contains("new Title"), false);
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsDisplayName().contains(listName), true);
    }

    @TestRail (id = "C5898")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editingExistingListWithSiteCollaboratorUser()
    {

        LOG.info("Preconditions: Create a user with 'Collaborator' role");
        String collaborator = String.format("Collaborator%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, collaborator, password, "collaborator@tests.com", "collaborator", "collaborator");
        userService.createSiteMember(userName, password, collaborator, siteName, "SiteCollaborator");
        setupAuthenticatedSession(collaborator, password);
        dataListsPage.navigate(siteName);

        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        LOG.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUp.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUp.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUp.isSaveButtonDisplayed(), true);

        LOG.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");

        LOG.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUp.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");

        LOG.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsDisplayName().contains("new Title"), true);

        userService.delete(adminUser, adminPassword, collaborator);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + collaborator);
    }

    @TestRail (id = "C5899")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void contributorRoleIsNotAbleToEditExistingList()
    {

        LOG.info("Preconditions: Create a user with 'Contributor' role");
        String contributor = String.format("Contributor%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, contributor, password, "collaborator@tests.com", "collaborator", "collaborator");
        userService.createSiteMember(userName, password, contributor, siteName, "SiteContributor");
        setupAuthenticatedSession(contributor, password);
        dataListsPage.navigate(siteName);

        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        Assert.assertEquals("The edit button is enabled", dataListsPage.isEditButtonDisabled(listName), true);

        LOG.info("Step 2: Click on the Edit button.");
        dataListsPage.clickOnDisabledEditButton(listName);

        userService.delete(adminUser, adminPassword, contributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + contributor);


    }

    @TestRail (id = "C5900")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void consumerRoleIsNotAbleToEditExistingList()
    {

        LOG.info("Preconditions: Create a user with 'Consumer' role");
        String consumer = String.format("Consumer%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, consumer, password, "collaborator@tests.com", "collaborator", "collaborator");
        userService.createSiteMember(userName, password, consumer, siteName, "SiteConsumer");
        setupAuthenticatedSession(consumer, password);
        dataListsPage.navigate(siteName);

        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        Assert.assertEquals("The edit button is enabled", dataListsPage.isEditButtonDisabled(listName), true);

        LOG.info("Step 2: Click on the Edit button.");
        dataListsPage.clickOnDisabledEditButton(listName);

        userService.delete(adminUser, adminPassword, consumer);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + consumer);

    }

    @TestRail (id = "C5901")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void contributorRoleIsAbleToEditListCreatedBySameUser()
    {

        LOG.info("Preconditions: Create a user with 'Collaborator' role and a list");
        String contributor = String.format("Contributor%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, contributor, password, "Contributor@tests.com", "Contributor", "Contributor");
        userService.createSiteMember(userName, password, contributor, siteName, "SiteContributor");
        setupAuthenticatedSession(contributor, password);
        dataListsPage.navigate(siteName);

        String ownDataList = "ownDataList";
        dataListsService.createDataList(contributor, password, siteName, DataList.CONTACT_LIST, ownDataList, "contact link description");
        getBrowser().refresh();

        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(ownDataList);

        LOG.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUp.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUp.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUp.isSaveButtonDisplayed(), true);

        LOG.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");

        LOG.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUp.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");

        LOG.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsDisplayName().contains("new Title"), true);

        userService.delete(adminUser, adminPassword, contributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + contributor);

    }

    @TestRail (id = "C5904")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void siteManagerIsAbleToEditList()
    {

        LOG.info("Preconditions: Create a user with 'Collaborator' role and a list");
        String manager = String.format("Manager%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, manager, password, "manager@tests.com", "manager", "manager");
        userService.createSiteMember(userName, password, manager, siteName, "SiteManager");
        setupAuthenticatedSession(manager, password);
        dataListsPage.navigate(siteName);

        LOG.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        LOG.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUp.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUp.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUp.isSaveButtonDisplayed(), true);

        LOG.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUp.modifyTitle("new Title");
        editListDetailsPopUp.modifyDescription("new description");

        LOG.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUp.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");

        LOG.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsDisplayName().contains("new Title"), true);

        userService.delete(adminUser, adminPassword, manager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + manager);

    }
}
