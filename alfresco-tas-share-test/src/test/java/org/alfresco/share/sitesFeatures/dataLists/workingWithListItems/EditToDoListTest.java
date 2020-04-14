package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EditToDoListTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;
    int priority = 1;
    String itemFile = "testFile1";
    String attachedFile = "testDoc.txt";
    private String random = RandomData.getRandomAlphanumeric();
    private String todoListName = "To Do list" + random;
    private String itemTitle = "item Title" + random;
    private String dataListDescription = "test datalist";
    private String asigneeName = "assignee2" + random;
    private CMISUtil.Status status = CMISUtil.Status.IN_PROGRESS;
    private String notes = "Notes";
    private String doc1 = "doc";
    private DateTime today = new DateTime();
    private String userName = "User" + random;
    private String siteName = "SiteName" + random;
    private Date dueDateToday = today.toDate();
    private String titleInputToEdit;

    @BeforeClass (alwaysRun = false)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        userService.create(adminUser, adminPassword, asigneeName, password, asigneeName + domain, "fName", "lName");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);

        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + itemFile);
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + attachedFile);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(userName, password, siteName, DataList.TODO_LIST, todoListName, dataListDescription);
        dataListsService.addToDoItem(userName, password, siteName, todoListName, itemTitle, dueDateToday, priority, status, notes, null,
            Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickToDoListItem(todoListName);
    }

    @AfterMethod (alwaysRun = false)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        userService.delete(adminUser, adminPassword, asigneeName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + asigneeName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C10352")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void verifyPossibilityToEditItem()
    {
        titleInputToEdit = "test edited title";
        String dueTime = "00:45";
        String priorityEdited = "3";
        DateTime dueDateTomorrow;
        dueDateTomorrow = today.plusDays(1);
        String notesEdited = "Test edited notes";
        String dueDateTomorrowString = dueDateTomorrow.toString("dd/M/yyyy");
        String dueDateTomorrowStringformated = dueDateTomorrow.toString("EEE d MMM yyyy");
        String newItemStatus = CMISUtil.Status.IN_PROGRESS.getValue();

        LOG.info("Step 1: Click the Edit button in the Actions field of the List Item");
        dataListsPage.clickEditButtonForListItem();

        LOG.info("Step 2: Edit Title input");
        editItemPopUp.editContent(ToDoAgendaFields.Title.toString(), titleInputToEdit);

        LOG.info("Step 3: Click on Due Date and select a different Due Date");
        editItemPopUp.editContent(ToDoAgendaFields.DueDate.toString(), dueDateTomorrowString);

        LOG.info("Step 4: Edit the HH:MM filed input");
        editItemPopUp.editContent(ToDoAgendaFields.DueTime.toString(), dueTime);

        LOG.info("Step 5: Edit Priority");
        editItemPopUp.editContent(ToDoAgendaFields.Priority.toString(), priorityEdited);

        LOG.info("Step 6: Edit Status");
        editItemPopUp.selectDropDownItem(newItemStatus, "todoStatus");

        LOG.info("Step 7: Edit the Notes value");
        editItemPopUp.editContent(ToDoAgendaFields.Notes.toString(), notesEdited);

        LOG.info("Step 8: Edit the Assignee field, search for assignee2 and assign user to list item.");
        editItemPopUp.addAssignedToToDo(asigneeName);

        LOG.info(
            "Step 9: Edit the Attachments filed, add new attachment.f.e. from the Select... window navigate to data list (click Document Library and select data list). "
                + "Click the + button for the documentLibrary and Ok button.");
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        LOG.info("Step 10: Click on Save");
        editItemPopUp.clickSave();
        List<String> expectedItem = Arrays.asList(titleInputToEdit, dueDateTomorrowStringformated + " " + dueTime, priorityEdited, newItemStatus,
            notesEdited, asigneeName, attachedFile);
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(expectedItem), titleInputToEdit + " issue list item is displayed.");
    }
}