package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ToDoAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static org.testng.Assert.assertTrue;

public class EditToDoListTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    private String random = DataUtil.getUniqueIdentifier();
    private String todoListName = "To Do list" + random;
    private String itemTitle = "item Title" + random;
    private String dataListDescription = "test datalist";
    private String asigneeName = "assignee2" + random;
    private CMISUtil.Status status = CMISUtil.Status.IN_PROGRESS;
    private String notes = "Notes";
    private String doc1 = "doc";
    private List<DashboardCustomization.Page> pagesToAdd = new ArrayList<>();
    private DateTime today = new DateTime();
    private String userName = "User" + random;
    private String siteName = "SiteName" + random;
    private Date dueDateToday = today.toDate();
    int priority = 1;
    private String titleInputToEdit;
    String itemFile = "testFile1";
    String attachedFile = "testDoc.txt";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(Page.DATALISTS);
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        userService.create(adminUser, adminPassword, asigneeName, password, "@tests.com", "fName", "lName");
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        String path = srcRoot + "testdata" + File.separator;
        contentService.uploadFileInSite(userName, password, siteName, path + itemFile);
        contentService.uploadFileInSite(userName, password, siteName, path + attachedFile);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        datalistService.createDataList(userName, password, siteName, DataList.TODO_LIST, todoListName, dataListDescription);
        datalistService.addToDoItem(userName, password, siteName, todoListName, itemTitle, dueDateToday, priority, status, notes, null,
                Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickToDoListItem(todoListName);
    }

    @TestRail(id = "C10352")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
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

        logger.info("Step 1: Click the Edit button in the Actions field of the List Item");
        dataListsPage.clickEditButtonForListItem();

        logger.info("Step 2: Edit Title input");
        editItemPopUp.editContent(ToDoAgendaFields.Title.toString(), titleInputToEdit);

        logger.info("Step 3: Click on Due Date and select a different Due Date");
        editItemPopUp.editContent(ToDoAgendaFields.DueDate.toString(), dueDateTomorrowString);

        logger.info("Step 4: Edit the HH:MM filed input");
        editItemPopUp.editContent(ToDoAgendaFields.DueTime.toString(), dueTime);

        logger.info("Step 5: Edit Priority");
        editItemPopUp.editContent(ToDoAgendaFields.Priority.toString(), priorityEdited);

        logger.info("Step 6: Edit Status");
        editItemPopUp.selectDropDownItem(newItemStatus, "todoStatus");

        logger.info("Step 7: Edit the Notes value");
        editItemPopUp.editContent(ToDoAgendaFields.Notes.toString(), notesEdited);

        logger.info("Step 8: Edit the Assignee field, search for assignee2 and assign user to list item.");
        editItemPopUp.addAssignedToToDo(asigneeName);

        logger.info(
                "Step 9: Edit the Attachments filed, add new attachment.f.e. from the Select... window navigate to data list (click Document Library and select data list). "
                        + "Click the + button for the documentLibrary and Ok button.");
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        logger.info("Step 10: Click on Save");
        editItemPopUp.clickSave();
        List<String> expectedItem = Arrays.asList(titleInputToEdit, dueDateTomorrowStringformated + " " + dueTime, priorityEdited, newItemStatus,
                notesEdited, asigneeName, attachedFile);
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(expectedItem), titleInputToEdit + " issue list item is displayed.");
    }
}