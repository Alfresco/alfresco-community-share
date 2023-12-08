package org.alfresco.share.sitesFeatures.dataLists;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.ContactListSelectedContent.ContactListColumns;
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
public class ViewingDataListsTests extends BaseTest
{
    //@Autowired
    DataListsPage dataListsPage;
    @Autowired
    SiteService siteService;
    @Autowired
    DataListsService dataListsService;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    String domain = "@test.com";

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


        authenticateUsingLoginPage(userName.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanupMethod()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C5853")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void filterAreVisibleOnlyIfAListIsSelected()
    {
        log.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact link description");

        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: Check the Items filter");
        Assert.assertFalse(dataListsPage.allFilterOptionsAreDisplayed(), "The filter options are displayed.");

        log.info("Step 2: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);

        log.info("Step 3: Check the available filtering options.");
        assertTrue(dataListsPage.allFilterOptionsAreDisplayed(), "Not all filters are displayed.");
    }

    @TestRail (id = "C5854")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void selectAListAndApplyAllFilter()
    {
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }

    @TestRail (id = "C5855")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void selectAListAndApplyRecentlyAddedFilter()
    {
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }

    @TestRail (id = "C5856")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void selectAListAndApplyRecentlyModifiedFilter()
    {
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }

    @TestRail (id = "C5857")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void selectAListAndApplyCreatedByMeFilter()
    {
        // TO_DO
        // Precondition
        // Data list items created eight days ago and six days ago, modified eight days ago and six days ago are added to the DataList by User1. -> can not create data list items any number of days in the past or the future
    }

    @TestRail (id = "C5858")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multiplePagesViewedInDataListsExplorerPanel()
    {
        log.info("Preconditions: Create a new List and add new items to it");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact link description");

        for (int i = 0; i < 51; i++)
        {
            dataListsService.addContactListItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), listName, userName.get().getFirstName() + i, userName.get().getLastName() + i, userName.get().getUsername() + i + domain, "companyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }

        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);
        assertTrue(dataListsPage.areNavigationLinksDisplayed(), "The navigation links are not displayed.");
        Assert.assertEquals(dataListsPage.getCurrentPageNumber(), "1", "The current page is not the expected one.");

        log.info("Step 2: Click on the 'next' navigation item.");
        dataListsPage.clickNextNavigationItem();
        Assert.assertEquals(dataListsPage.getCurrentPageNumber(), "2", "The current page is not the expected one.");

        log.info("Step 3: Click on the 'previous' navigation item.");
        dataListsPage.clickPreviousNavigationItem();
        Assert.assertEquals(dataListsPage.getCurrentPageNumber(), "1", "The current page is not the expected one.");

        log.info("Step 3: Click on page number '2'.");
        dataListsPage.clickOnSpecificPage("2");
        Assert.assertEquals(dataListsPage.getCurrentPageNumber(), "2", "The current page is not the expected one.");
    }

    @TestRail (id = "C5859")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void dataListItemsSortedByColumn()
    {
        log.info("Preconditions: Create a new List and add two items to it, descendent ordered by Company Name");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact link description");

        for (int i = 0; i < 2; i++)
        {
            dataListsService.addContactListItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), listName, userName.get().getFirstName() + i, userName.get().getLastName() + i, userName.get().getUsername() + i + domain, i + "comapanyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }

        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);

        log.info("Step 2: In the table click 'Company' column headings and check the items are sorted by Company Name.");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(ContactListColumns.Company.toString()), "The data list items are sorted by Company");
    }

    @TestRail (id = "C5860")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void aSinglePageOfListItemsViewed()
    {
        log.info("Preconditions: Create a new List and add two items to it, descendent ordered by Company Name");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact link description");

        for (int i = 0; i < 2; i++)
        {
            dataListsService.addContactListItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), listName, userName.get().getFirstName() + i, userName.get().getLastName() + i, userName.get().getUsername() + i + domain, i + "comapanyName", "jobTitle", "phoneOffice", "phoneMobile", "notes");
        }

        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickContactListItem(listName);

        log.info("Step 2: Click the 'next' navigation item.");
        dataListsPage.clickNextNavigationItem();
        Assert.assertEquals(dataListsPage.getCurrentPageNumber(), "1", "The current page is not the expected one.");

        log.info("Step 3: Click on the 'previous' navigation item.");
        dataListsPage.clickPreviousNavigationItem();
        Assert.assertEquals(dataListsPage.getCurrentPageNumber(), "1", "The current page is not the expected one.");
    }
}