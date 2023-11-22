package org.alfresco.share.sitesFeatures.dataLists;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.enums.DataListTypes;
import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j
public class CreateDataListsTests extends BaseTest
{

    //@Autowired
    private DataListsPage dataListsPage;

    //@Autowired
    private CreateDataListDialog createDataListDialog;
    @Autowired
    SiteService siteService;
    @Autowired
    private DataListsService dataLists;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String dataListName = "Test List";

    @BeforeMethod (alwaysRun = true)
    public void preConditions()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);

        dataListsPage = new DataListsPage(webDriver);
        createDataListDialog = new CreateDataListDialog(webDriver);

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        createDataListDialog.clickCancelButton();
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C5861")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewList()
    {
        log.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        log.info("Step 2: Select the type of list you want to create.");
        createDataListDialog.selectType(DataListTypes.CONTACT_LIST.title);
        createDataListDialog.isExpectedTypeSelected(DataListTypes.CONTACT_LIST.title);

        log.info("Step 3: Provide an input for Title.");
        createDataListDialog.typeTitle(dataListName);

        log.info("Step 4: Provide an input for Description.");
        createDataListDialog.typeDescription("Description");

        log.info("Step 5: Click on the 'Save' button.");
        createDataListDialog.clickSaveButton();
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was not created.");
    }

    @TestRail (id = "C5862")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCantBeCreatedWithoutSelectingListType()
    {
        log.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        log.info("Step 2: Provide an input for Title.");
        createDataListDialog.typeTitle(dataListName);

        log.info("Step 3: Provide an input for Description.");
        createDataListDialog.typeDescription("Description");

        log.info("Step 4: Click on the 'Save' button.");
        createDataListDialog.clickSaveButton();
        Assert.assertEquals(createDataListDialog.getInvalidDataListBalloonMessage(), ("The value cannot be empty."), "The pop-up message isn't as expected.");
        Assert.assertTrue(dataListsPage.noListDisplayed(), "It shouldn't be any list created.");
    }

    @TestRail (id = "C5863")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCantBeCreatedWithEmptyTitle()
    {
        log.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        log.info("Step 2: Select the type of list you want to create.");
        createDataListDialog.selectType(DataListTypes.CONTACT_LIST.title);

        log.info("Step 3: Provide an input for Description.");
        createDataListDialog.typeDescription("Description");

        log.info("Step 4: Click on the 'Save' button.");
        createDataListDialog.clickSaveButton();
        Assert.assertEquals(createDataListDialog.invalidTitleBalloonMessage(), ("The value cannot be empty."), "The pop-up message isn't as expected.");
        Assert.assertTrue(dataListsPage.noListDisplayed(), "It shouldn't be any list created.");
    }

    @TestRail (id = "C5864")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCreatedWithEmptyDescription()
    {
        log.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        log.info("Step 2: Select the type of list you want to create.");
        createDataListDialog.selectType(DataListTypes.CONTACT_LIST.title);

        log.info("Step 3: Provide an input for Title.");
        createDataListDialog.typeTitle(dataListName);

        log.info("Step 4: Click on the 'Save' button.");
        createDataListDialog.clickSaveButton();
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was not created.");
    }

    @TestRail (id = "C5865")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListCreatedWithExistingTitle()
    {
        log.info("Preconditions: Create a new List with 'Test list' name.");
        String listName = "Test list";
        dataLists.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact link description");

        log.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        log.info("Step 2: Select the type of list you want to create.");
        createDataListDialog.selectType(DataListTypes.CONTACT_LIST.title);

        log.info("Step 3: Provide 'Test list' for Title.");
        createDataListDialog.typeTitle(dataListName);

        log.info("Step 4: Click on the 'Save' button.");
        createDataListDialog.clickSaveButton();
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was not created.");
    }

    @TestRail (id = "C5881")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreationOfNewDataList()
    {

        log.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        log.info("Step 2: Select the type of list you want to create.");
        createDataListDialog.selectType(DataListTypes.CONTACT_LIST.title);

        log.info("Step 3: Provide 'Test list' for Title.");
        createDataListDialog.typeTitle(dataListName);

        log.info("Step 4: Provide an input for Description.");
        createDataListDialog.typeDescription("Description");

        log.info("Step 5: Click on the 'Cancel' button.");
        createDataListDialog.clickCancelButton();
        Assert.assertTrue(dataListsPage.noListDisplayed(), "It shouldn't be any list created.");
    }

    @TestRail (id = "C5882")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void closeCreationWindowOfNewDataList()
    {

        log.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        log.info("Step 2: Select the type of list you want to create.");
        createDataListDialog.selectType(DataListTypes.CONTACT_LIST.title);

        log.info("Step 3: Provide 'Test list' for Title.");
        createDataListDialog.typeTitle(dataListName);

        log.info("Step 4: Provide an input for Description.");
        createDataListDialog.typeDescription("Description");

        log.info("Step 5: Click on the 'X' button to close the 'New List' form.");
        createDataListDialog.clickClose();
        Assert.assertTrue(dataListsPage.noListDisplayed(), "It shouldn't be any list created.");
    }

    @TestRail (id = "C5892")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void newListDoesNotContainAnyListItems()
    {

        log.info("Step 1: On the Data Lists page click on the New List button.");
        dataListsPage.clickOnNewListButton();

        log.info("Step 2: Select the type of list you want to create.");
        createDataListDialog.selectType(DataListTypes.CONTACT_LIST.title);

        log.info("Step 3: Provide 'Test list' for Title.");
        createDataListDialog.typeTitle(dataListName);

        log.info("Step 4: Provide an input for Description.");
        createDataListDialog.typeDescription("Description");

        log.info("Step 5: Click on the 'Save' button.");
        createDataListDialog.clickSave();
        Assert.assertEquals(dataListsPage.successfullyCreatedDataListMessage(), "New Data List '" + dataListName + "' successfully created.", "The message of successfully creation isn't as expected.");
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(dataListName), "The data list was not created.");

        log.info("Step 6: Select the 'Test List' created from the Lists browsing panel.");
        dataListsPage.clickContactListItem(dataListName);
        Assert.assertTrue(dataListsPage.areNavigationLinksDisplayed(), "The navigation links are not displayed.");
        Assert.assertFalse(dataListsPage.isAnyListItemDisplayed(), "Some List Items are displayed.");
    }
}
