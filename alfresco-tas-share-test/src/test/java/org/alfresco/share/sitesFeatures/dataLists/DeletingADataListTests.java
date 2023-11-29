package org.alfresco.share.sitesFeatures.dataLists;

import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.DeleteListPopUp;
import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j

public class DeletingADataListTests extends BaseTest
{
    DataListsPage dataListsPage;
    @Autowired
    DataListsService dataLists;
    @Autowired
    SiteService siteService;
    CreateDataListDialog createDataListDialog;
    DeleteListPopUp deleteListPopUp;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String listName = "first list";

    @BeforeMethod (alwaysRun = true)
    public void precondition()
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
        deleteListPopUp = new DeleteListPopUp(webDriver);

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        createDataListDialog.clickCancelButton();
        dataLists.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact link description");

        dataListsPage.pageRefresh();
    }

    @AfterMethod (alwaysRun = true)
    public void cleanupMethod()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());

    }

    @TestRail (id = "C5911")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deletingExistingListWithSiteManagerUser()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);

        log.info("Step 2: Click on the 'Delete' button.");
        deleteListPopUp.clickDeleteButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "Successfully deleted list");

        log.info("Step 3: Check that list has been deleted and is no longed displayed in the Lists section.");
        Assert.assertEquals("At least one list is displayed.", dataListsPage.noListDisplayed(), true);
    }

    @TestRail (id = "C5912")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deletingOwnExistingList()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);

        log.info("Step 2: Click on the 'Delete' button.");
        deleteListPopUp.clickDeleteButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "Successfully deleted list");

        log.info("Step 3: Check that list has been deleted and is no longed displayed in the Lists section.");
        Assert.assertEquals("At least one list is displayed.", dataListsPage.noListDisplayed(), true);
    }

    @TestRail (id = "C5915")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingDataList()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel.");
        Assert.assertEquals("The 'Delete' button is not displayed.", true, dataListsPage.isDeleteButtonDisplayedForList(listName));

        log.info("Step 2: Click on the 'Delete' button.");
        dataListsPage.clickDeleteButtonForList(listName);

        log.info("Step 3: Click on the 'Cancel' button.");
        deleteListPopUp.clickCancelButton();

        log.info("Step 4: Check that list has not been deleted and is still displayed in the Lists section.");
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsItemsTitle().contains("first list"), true);
    }
}
