package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class DeletingAListItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsService dataLists;

    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    DeleteDialog deleteDialog;

    private List<DashboardCustomization.Page> pagesToAdd = new ArrayList<>();

    String random = DataUtil.getUniqueIdentifier();
    String userName = "User-" + random;
    String siteName = "SiteName-" + random;
    String contactListName = "contactList";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(DashboardCustomization.Page.DATALISTS);
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        dataLists.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataLists.addContactListItem(adminUser, adminPassword, siteName, contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle",
                "123456", "+41256422", "testNotes");

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactListName);
    }

    @TestRail(id = "C6399")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteSingleItemList()
    {
        LOG.info("STEP1: Click on Delete icon from Actions section of the data list item");
        List<String> item = Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes");
        dataListsPage.currentContent.deleteItem(item);
        assertEquals(deleteDialog.getMessage(), "Are you sure you want to delete this item?", "Delete dialog message=");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "Delete dialog - Delete button is displayed.");

        LOG.info("STEP2: Click the Delete button on the Delete Item form");
        deleteDialog.clickDelete();
        assertFalse(dataListsPage.currentContent.isAnyListItemDisplayed(), "Data list item is displayed in Data List items list.");

        cleanupAuthenticatedSession();
    }
}