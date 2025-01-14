package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.eventList;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EventListSelectedContent;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

@Slf4j

public class Sorting extends BaseTest
{

    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    DataListsPage dataListsPage;
    @Autowired
    SiteService      siteService;
    @Autowired
    DataListsService dataListsService;
    String itemTitle        = "Item title";
    String itemDescription  = "item description";
    String itemLocation     = "item location";
    String itemRegistration = "item registration";
    String itemNotes        = "item notes";
    String itemFile         = "testFile1";

    @BeforeMethod(alwaysRun = true)
    public void precondition()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin()
                         .createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get())
                         .createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        siteService.addPageToSite(userName.get()
                                      .getUsername(), userName.get()
                                      .getPassword(), siteName.get()
                                      .getId(), DashboardCustomization.Page.DATALISTS, null);

        dataListsPage = new DataListsPage(webDriver);

        authenticateUsingCookies(userName.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupMethod()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(),
                                        "/User Homes/" + userName.get()
                                            .getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail(id = "C6552")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void sortDataListItemsByColumn()
    {
        log.info("Preconditions: Create a new List and add two items to it, descendent ordered by Company Name");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get()
            .getId(), DataListsService.DataList.EVENT_LIST, listName, "Event list description");

        contentService.uploadFileInSite(userName.get()
                                            .getUsername(), userName.get()
                                            .getPassword(), siteName.get()
                                            .getId(), testDataFolder + itemFile);
        for (int i = 0; i < 2; i++)
        {
            dataListsService.addEventListItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get()
                                                  .getId(), listName, itemTitle, itemDescription, itemLocation, DateTime.now()
                                                  .toDate(), DateTime.now()
                                                  .toDate(), itemRegistration, itemNotes,
                                              Collections.singletonList(itemFile));
        }

        dataListsPage.navigate(siteName.get()
                                   .getId());

        log.info("Step 1: Click the created list displayed under Lists view.");
        dataListsPage.clickIssueListItem(listName);

        log.info("Step 2: In the table click column headings and check the items are sorted by Name.");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(
                       EventListSelectedContent.EventListColumns.Tittle.toString()),
                   "The data list items are sorted by First Name");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(
                       EventListSelectedContent.EventListColumns.Description.toString()),
                   "The data list items are sorted by First Name");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(
                       EventListSelectedContent.EventListColumns.Location.toString()),
                   "The data list items are sorted by First Name");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(
                       EventListSelectedContent.EventListColumns.StartDate.toString()),
                   "The data list items are sorted by First Name");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(
                       EventListSelectedContent.EventListColumns.EndDate.toString()),
                   "The data list items are sorted by First Name");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(
                       EventListSelectedContent.EventListColumns.Registrations.toString()),
                   "The data list items are sorted by First Name");
        assertTrue(dataListsPage.areItemsSortedByColumnAfterClickingTheColumn(
                       EventListSelectedContent.EventListColumns.EventNote.toString()),
                   "The data list items are sorted by First Name");

    }
}
