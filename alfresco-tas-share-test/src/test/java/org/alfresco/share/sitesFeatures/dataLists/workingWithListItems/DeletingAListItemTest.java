package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class DeletingAListItemTest extends BaseTest
{
    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;

   // @Autowired
    DataListsPage dataListsPage;

    //@Autowired
    DeleteDialog deleteDialog;

    String random = RandomData.getRandomAlphanumeric();
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    String contactListName = "contactList";

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        dataListsPage = new DataListsPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataListsService.DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataListsService.addContactListItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle",
            "123456", "+41256422", "testNotes");

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickContactListItem(contactListName);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C6399")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteSingleItemList()
    {
        log.info("STEP1: Click on Delete icon from Actions section of the data list item");
        dataListsPage.clickDeleteButtonForListItem();
        assertEquals(deleteDialog.getMessage(), "Are you sure you want to delete this item?", "Delete dialog message=");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "Delete dialog - Delete button is displayed.");

        log.info("STEP2: Click the Delete button on the Delete Item form");
        deleteDialog.confirmDeletion();
        assertTrue(dataListsPage.noListItemsDisplayed(), "Data list item is displayed in Data List items list.");
    }
}