package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class DeletingAListItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    DeleteDialog deleteDialog;

    String random = RandomData.getRandomAlphanumeric();
    String userName = "User-" + random;
    String siteName = "SiteName-" + random;
    String contactListName = "contactList";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataListsService.addContactListItem(adminUser, adminPassword, siteName, contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle",
            "123456", "+41256422", "testNotes");

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactListName);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6399")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void deleteSingleItemList()
    {
        LOG.info("STEP1: Click on Delete icon from Actions section of the data list item");
        List<String> item = Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes");
        dataListsPage.currentContent.deleteItem(item);
        assertEquals(deleteDialog.getMessage(), "Are you sure you want to delete this item?", "Delete dialog message=");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "Delete dialog - Delete button is displayed.");

        LOG.info("STEP2: Click the Delete button on the Delete Item form");
        deleteDialog.clickDelete(dataListsPage);
        assertFalse(dataListsPage.currentContent.isAnyListItemDisplayed(), "Data list item is displayed in Data List items list.");

        cleanupAuthenticatedSession();
    }
}