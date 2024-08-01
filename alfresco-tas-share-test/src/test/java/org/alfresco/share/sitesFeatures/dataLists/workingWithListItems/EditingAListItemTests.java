package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import static org.alfresco.common.Utils.srcRoot;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil.Status;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ContactListFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ToDoAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;

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
@Slf4j

public class EditingAListItemTests extends BaseTest
{
    //@Autowired
    protected EditItemPopUp editItemPopUp;
    @Autowired
    protected SiteService siteService;
    @Autowired
    DataListsService dataLists;
    //@Autowired
    DataListsPage dataListsPage;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userTest = new ThreadLocal<>();
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
        editItemPopUp = new EditItemPopUp(webDriver);

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
        log.info("Preconditions: Create a second user");
        userTest.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Preconditions: Create a 'test.xlsx' file");
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());
        contentService.uploadFilesInFolder(testDataFolder, userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), folderName);

        log.info("Preconditions: Create a new 'To Do' List with an item");
        String toDoListName = "toDo" + System.currentTimeMillis();
        dataLists.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.TODO_LIST, toDoListName, "To Do list description");
        DateTime currentDate = new DateTime();
        dataLists.addToDoItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), toDoListName, "itemTitle", currentDate.toDate(), 1, Status.IN_PROGRESS, "notes", userTest.get().getUsername(),
            Collections.singletonList("test.xlsx"));

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickToDoListItem(toDoListName);

        log.info("Step 1: Click the 'Edit' button for the to do list item to be edited.");
        dataListsPage.clickEditButtonForListItem();

        log.info("Step 2: Edit the title and click on 'Save' button.");
        editItemPopUp.editContent(ToDoAgendaFields.Title.toString(), "new Title");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");

        log.info("Step 3: Check the new firstName for the To do list item.");
        List<String> expectedList = Arrays.asList("new Title", currentDate.toString("EEE dd MMM yyyy HH:mm") + ":00", "1", "In Progress", userTest.get().getFirstName() + " " + userTest.get().getLastName(), "test.xlsx" );
        if (dataListsPage.getFilterTypeList().contains(expectedList))
            for (String anExpectedList : expectedList)
            {
                assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
            }
        else
        {
            List<String> newExpectedList = Arrays.asList("new Title", currentDate.toString("EEE d MMM yyyy HH:mm") + ":00", "1", "In Progress", userTest.get().getFirstName() + " " + userTest.get().getLastName(), "test.xlsx" );
            for (String an_ExpectedList : newExpectedList)
            {
                assertTrue(dataListsPage.getFilterTypeList().contains(an_ExpectedList), "Data list item is updated.");
            }
        }

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userTest.get().getUsername());
        deleteUsersIfNotNull(userTest.get());
    }

    @TestRail (id = "C6392")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editingANonMandatoryFieldOfAListItem()
    {
        log.info("Preconditions: Create a new 'Contact' List with an item");
        String contactListName = "contact" + System.currentTimeMillis();
        dataLists.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataLists.addContactListItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes");

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickContactListItem(contactListName);

        log.info("Step 1: Click the 'Edit' button for the contact list item to be edited.");
        dataListsPage.clickEditButtonForListItem();

        log.info("Step 2: Edit the firstName and click on 'Save' button.");
        editItemPopUp.editContent(ContactListFields.FirstName.toString(), "new Name");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");

        log.info("Step 3: Check the new firstName for the Contact list item.");
        List<String> expectedList = Arrays.asList("new Name", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes" );
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

}
