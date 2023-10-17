package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ToDoAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j
public class EditToDoListTest extends BaseTest
{
    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;

   // @Autowired
    DataListsPage dataListsPage;

    //@Autowired
    EditItemPopUp editItemPopUp;

    int priority = 1;
    String itemFile = "testFile1";
    String attachedFile = "testDoc.txt";
    private String random = RandomData.getRandomAlphanumeric();
    private String todoListName = "To Do list" + random;
    private String itemTitle = "item Title" + random;
    private String dataListDescription = "test datalist";
    private CMISUtil.Status status = CMISUtil.Status.IN_PROGRESS;
    private String notes = "Notes";
    private String doc1 = "doc";
    private DateTime today = new DateTime();
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<UserModel> asigneeName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private Date dueDateToday = today.toDate();
    private String titleInputToEdit;

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test users are created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        asigneeName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        dataListsPage = new DataListsPage(webDriver);
        editItemPopUp = new EditItemPopUp(webDriver);

        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + itemFile);
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + attachedFile);
        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
        dataListsService.createDataList(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), DataList.TODO_LIST, todoListName, dataListDescription);
        dataListsService.addToDoItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), todoListName, itemTitle, dueDateToday, priority, status, notes, null,
            Collections.singletonList(itemFile));

        authenticateUsingLoginPage(userName.get());
        dataListsPage.navigate(siteName.get());
        dataListsPage.clickToDoListItem(todoListName);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + asigneeName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
        deleteUsersIfNotNull(asigneeName.get());
    }

    @TestRail (id = "C10352")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPossibilityToEditItem()
    {
        titleInputToEdit = "test edited title";
        String dueTime = "00:45";
        String dueTimeInListItem = "00:45:00";
        String priorityEdited = "3";
        DateTime dueDateTomorrow;
        dueDateTomorrow = today.plusDays(1);
        String notesEdited = "Test edited notes";
        String dueDateTomorrowString = dueDateTomorrow.toString("dd/M/yyyy");
        String dueDateTomorrowStringformated = dueDateTomorrow.toString("EEE d MMM yyyy");
        String newItemStatus = CMISUtil.Status.IN_PROGRESS.getValue();

        log.info("Step 1: Click the Edit button in the Actions field of the List Item");
        dataListsPage.clickEditButtonForListItem();

        log.info("Step 2: Edit Title input");
        editItemPopUp.editContent(ToDoAgendaFields.Title.toString(), titleInputToEdit);

        log.info("Step 3: Click on Due Date and select a different Due Date");
        editItemPopUp.editContent(ToDoAgendaFields.DueDate.toString(), dueDateTomorrowString);

        log.info("Step 4: Edit the HH:MM filed input");
        editItemPopUp.editContent(ToDoAgendaFields.DueTime.toString(), dueTime);

        log.info("Step 5: Edit Priority");
        editItemPopUp.editContent(ToDoAgendaFields.Priority.toString(), priorityEdited);

        log.info("Step 6: Edit Status");
        editItemPopUp.selectDropDownItem(newItemStatus, "todoStatus");

        log.info("Step 7: Edit the Notes value");
        editItemPopUp.editContent(ToDoAgendaFields.Notes.toString(), notesEdited);

        log.info("Step 8: Edit the Assignee field, search for assignee2 and assign user to list item.");
        editItemPopUp.addAssignedToToDo(asigneeName.get().getUsername());

        log.info(
            "Step 9: Edit the Attachments filed, add new attachment.f.e. from the Select... window navigate to data list (click Document Library and select data list). "
                + "Click the + button for the documentLibrary and Ok button.");
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        log.info("Step 10: Click on Save");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");

        String assignee = asigneeName.get().getFirstName() + " " + asigneeName.get().getLastName();
        List<String> expectedList = Arrays.asList(titleInputToEdit,dueDateTomorrowStringformated + " " + dueTimeInListItem, priorityEdited, newItemStatus, assignee );
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }

        List<String> attachmentFiles = Arrays.asList(itemFile+"\n"+attachedFile);
        for (String attachments : attachmentFiles)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(attachments), "Attachments updated.");
        }
    }
}