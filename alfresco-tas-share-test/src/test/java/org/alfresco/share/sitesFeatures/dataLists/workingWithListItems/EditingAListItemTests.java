package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;
import org.alfresco.dataprep.CMISUtil.Status;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ContactListFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ToDoAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EditingAListItemTests extends ContextAwareWebTest
{

    @Autowired
    protected EditItemPopUp editItemPopUp;
    @Autowired
    DataListsService dataLists;
    @Autowired
    DataListsPage dataListsPage;
    private String userName;
    private String siteName;

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        setupAuthenticatedSession(userName, password);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6391")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editingAMandatoryFieldOfAListItem()
    {
        LOG.info("Preconditions: Create a second user");
        String userTest = String.format("userTest%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);

        LOG.info("Preconditions: Create a 'test.xlsx' file");
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);

        LOG.info("Preconditions: Create a new 'To Do' List with an item");
        String toDoListName = "toDo" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.TODO_LIST, toDoListName, "To Do list description");
        DateTime currentDate = new DateTime();
        dataLists.addToDoItem(adminUser, adminPassword, siteName, toDoListName, "itemTitle", currentDate.toDate(), 1, Status.IN_PROGRESS, "notes", userTest,
            Collections.singletonList("test.xlsx"));

        dataListsPage.navigate(siteName);
        dataListsPage.clickToDoListItem(toDoListName);

        LOG.info("Step 1: Click the 'Edit' button for the to do list item to be edited.");
        dataListsPage.currentContent.editItem(Arrays.asList("itemTitle", "1", "In Progress", userTest + " " + userTest, "test.xlsx"));

        LOG.info("Step 2: Edit the title and click on 'Save' button.");
        editItemPopUp.editContent(ToDoAgendaFields.Title.toString(), "new Title");
        editItemPopUp.clickSave();
        Assert.assertEquals("The pop-up message isn't as expected.", "Data Item updated successfully", dataListsPage.currentContent.messageDisplayed());

        LOG.info("Step 3: Check the new firstName for the To do list item.");
        Assert.assertEquals("The data list was not updated.", dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("new Title", currentDate.toString("EEE dd MMM yyyy HH:mm") + ":00", "1", "In Progress", userTest + " " + userTest, "test.xlsx")), true);
        userService.delete(adminUser, adminPassword, userTest);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userTest);
    }

    @TestRail (id = "C6392")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editingANonMandatoryFieldOfAListItem()
    {

        LOG.info("Preconditions: Create a new 'Contact' List with an item");
        String contactListName = "contact" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataLists.addContactListItem(adminUser, adminPassword, siteName, contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes");

        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactListName);

        LOG.info("Step 1: Click the 'Edit' button for the contact list item to be edited.");
        dataListsPage.currentContent.editItem(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes"));

        LOG.info("Step 2: Edit the firstName and click on 'Save' button.");
        editItemPopUp.editContent(ContactListFields.FirstName.toString(), "new Name");
        editItemPopUp.clickSave();
        Assert.assertEquals("The pop-up message isn't as expected.", "Data Item updated successfully", dataListsPage.currentContent.messageDisplayed());

        LOG.info("Step 3: Check the new firstName for the Contact list item.");
        Assert.assertEquals("The data list was not updated.", dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("new Name", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes")), true);

    }

}
