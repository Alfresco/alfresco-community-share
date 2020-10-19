package org.alfresco.share.sitesFeatures.dataLists;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp.DataListTypes;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateDataListsTests extends ContextAwareWebTest
{

    @Autowired
    private DataListsPage dataListsPage;

    @Autowired
    private CreateDataListPopUp createDataListPopUp;

    @Autowired
    private DataListsService dataLists;

    private String userName;
    private String siteName;
    private String dataListName = "Test List";

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, password);
    }

    @BeforeMethod (alwaysRun = true)
    public void createSite()
    {
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsPage.navigate(siteName);
        createDataListPopUp.clickCancelFormButton();
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


    @TestRail (id = "C5861")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewList()
    {
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.title);
        createDataListPopUp.isExpectedTypeSelected(DataListTypes.ContactList.title);

        LOG.info("Step 3: Provide an input for Title.");
        createDataListPopUp.typeTitleName(dataListName);

        LOG.info("Step 4: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");

        LOG.info("Step 5: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was not created.");
    }

    @TestRail (id = "C5862")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
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
        Assert.assertFalse(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was created.");
        Assert.assertTrue(dataListsPage.noListDisplayed(), "It shouldn't be any list created.");
    }

    @TestRail (id = "C5863")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCantBeCreatedWithEmptyTitle()
    {
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.title);

        LOG.info("Step 3: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");

        LOG.info("Step 4: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertEquals(createDataListPopUp.invalidTitleBalloonMessage(), ("The value cannot be empty."), "The pop-up message isn't as expected.");
        Assert.assertFalse(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was created.");
        Assert.assertTrue(dataListsPage.noListDisplayed(), "It shouldn't be any list created.");
    }

    @TestRail (id = "C5864")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void newListCreatedWithEmptyDescription()
    {
        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.title);

        LOG.info("Step 3: Provide an input for Title.");
        createDataListPopUp.typeTitleName(dataListName);

        LOG.info("Step 4: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was not created.");
    }

    @TestRail (id = "C5865")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCreatedWithExistingTitle()
    {
        LOG.info("Preconditions: Create a new List with 'Test list' name.");
        String listName = "Test list";
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact link description");

        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.title);

        LOG.info("Step 3: Provide 'Test list' for Title.");
        createDataListPopUp.typeTitleName(dataListName);

        LOG.info("Step 4: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was not created.");
    }

    @TestRail (id = "C5881")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreationOfNewDataList()
    {

        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.title);

        LOG.info("Step 3: Provide 'Test list' for Title.");
        createDataListPopUp.typeTitleName(dataListName);

        LOG.info("Step 4: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");

        LOG.info("Step 5: Click on the 'Cancel' button.");
        createDataListPopUp.clickCancelFormButton();
        Assert.assertFalse(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was created.");
        Assert.assertTrue(dataListsPage.noListDisplayed(), "It shouldn't be any list created.");
    }

    @TestRail (id = "C5882")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void closeCreationWindowOfNewDataList()
    {

        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.title);

        LOG.info("Step 3: Provide 'Test list' for Title.");
        createDataListPopUp.typeTitleName(dataListName);

        LOG.info("Step 4: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");

        LOG.info("Step 5: Click on the 'X' button to close the 'New List' form.");
        createDataListPopUp.clickClose();
        Assert.assertFalse(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was created.");
        Assert.assertTrue(dataListsPage.noListDisplayed(), "It shouldn't be any list created.");
    }

    @TestRail (id = "C5892")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListDoesNotContainAnyListItems()
    {

        LOG.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        LOG.info("Step 2: Select the type of list you want to create.");
        createDataListPopUp.selectType(DataListTypes.ContactList.title);

        LOG.info("Step 3: Provide 'Test list' for Title.");
        createDataListPopUp.typeTitleName(dataListName);

        LOG.info("Step 4: Provide an input for Description.");
        createDataListPopUp.typeDescription("Description");

        LOG.info("Step 5: Click on the 'Save' button.");
        createDataListPopUp.clickSaveButton();
        Assert.assertEquals(dataListsPage.successfullyCreatedDataListMessage(), "New Data List '" + dataListName + "' successfully created.", "The message of successfully creation isn't as expected.");
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was not created.");

        LOG.info("Step 6: Select the 'Test List' created from the Lists browsing panel.");
        dataListsPage.clickContactListItem(dataListName);
        Assert.assertTrue(dataListsPage.currentContent.areNavigationLinksDisplayed(), "The navigation links are not displayed.");
        Assert.assertFalse(dataListsPage.currentContent.isAnyListItemDisplayed(), "Some List Items are displayed.");
    }
}
