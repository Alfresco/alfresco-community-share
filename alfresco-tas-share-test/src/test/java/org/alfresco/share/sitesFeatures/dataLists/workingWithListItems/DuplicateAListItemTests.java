package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Slf4j
public class DuplicateAListItemTests extends BaseTest
{
    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;

    //@Autowired
    protected EditItemPopUp editItemPopUp;
    //@Autowired
    DataListsPage dataListsPage;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

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

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);

        authenticateUsingLoginPage(userName.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C6391")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editingAMandatoryFieldOfAListItem()
    {

        log.info("Preconditions: Create a new 'Contact' List with an item");
        String contactListName = "contact" + System.currentTimeMillis();
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataListsService.addContactListItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes");

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickContactListItem(contactListName);

        log.info("Step 1: Select the list item of the Contact list.");
        List<String> expectedList = Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes" );
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
        assertTrue(dataListsPage.isNewListItemRowAdded("1 - 1 of 1"), "New Row not added");

        log.info("Step 2: Click the 'Duplicate' button for the contact list item to be edited.");
        dataListsPage.clickDuplicateButtonForListItem();
        dataListsPage.waitUntilNotificationMessageDisappears();
        assertTrue(dataListsPage.isNewListItemRowAdded("1 - 2 of 2"), "New Row not added");

    }
}
