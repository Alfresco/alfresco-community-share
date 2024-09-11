package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.taskListAdvanced;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.AdvancedTaskAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;

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
public class EditAdvancedTaskListItemTest extends BaseTest
{
    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;

    //@Autowired
    DataListsPage dataListsPage;

    //@Autowired
    EditItemPopUp editItemPopUp;
    String listName = String.format("Advanced task item name%s", RandomData.getRandomAlphanumeric());
    String itemTitle = "Advanced task item title";
    String itemDescription = "Advanced task item description";
    String itemComment = "Advanced task item comment";
    String itemFile = "testFile1";
    CMISUtil.Priority itemPriority = CMISUtil.Priority.Low;
    CMISUtil.Status itemStatus = CMISUtil.Status.ON_HOLD;
    String newItemTitle = "edited ItemTitle";
    String newItemDescription = "edited ItemDescription";
    String newComments = "edited ItemComment";
    String newComplete = "5";
    String newItemStatus = CMISUtil.Status.IN_PROGRESS.getValue();
    String newItemPriority = CMISUtil.Priority.High.toString();
    String attachedFile = "testDoc.txt";

    String date = "29/09/2016";
    String date2 = "Thu 29 Sep 2016";
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userAssignee = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        userAssignee.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        dataListsPage = new DataListsPage(webDriver);
        editItemPopUp = new EditItemPopUp(webDriver);

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataListsService.DataList.TASKS_ADVANCED, listName, "Advanced Task list description.");

        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + attachedFile);
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + itemFile);
        dataListsService.addTaskAdvancedItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), listName, itemTitle, itemDescription, null, null, Collections.singletonList(userName.get().getUsername()),
            itemPriority, itemStatus, 0, itemComment, Collections.singletonList(itemFile));

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickAdvancedTaskListItem(listName);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userAssignee.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
        deleteUsersIfNotNull(userAssignee.get());
    }

    @TestRail (id = "C10354")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyEditDataItem()
    {
        log.info("STEP1: Click 'Edit' icon for the advanced task list item to be edited");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Provide new input for Title field");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.Title.toString(), newItemTitle);

        log.info("STEP3: Provide new input for Description field");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.Description.toString(), newItemDescription);

        log.info("STEP4: Select Start Date");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.StartDate.toString(), date);

        log.info("STEP5: Select End Date");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.EndDate.toString(), date);

        log.info("STEP6: Edit the Assignee field");
        editItemPopUp.addAssignedToAdvancedTask(userAssignee.get().getUsername());

        log.info("STEP7: Select a Priority from dropdown");
        editItemPopUp.selectDropDownItem(newItemPriority, "taskPriority");

        log.info("STEP8: Select a Status value from dropdown");
        editItemPopUp.selectDropDownItem(newItemStatus, "taskStatus");

        log.info("STEP9: Edit %Complete field");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.Complete.toString(), newComplete);

        log.info("STEP10: Provide input for Comments field");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.Comments.toString(), newComments);

        log.info("STEP11: Edit Attachments, add new attachment");
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        log.info("STEP12: Click on Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");

        List<String> expectedList = Arrays.asList(newItemTitle, newItemDescription, date2, date2, newItemPriority, newItemStatus, newComplete, newComments);
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }

        log.info("verify Assignee field");
        String user1 = userName.get().getFirstName() + " " + userName.get().getLastName();
        String user2 = userAssignee.get().getFirstName() + " " + userAssignee.get().getLastName();

        List<String> userList = Arrays.asList(user1+"\n"+user2);
        for (String users : userList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(users), "Assignee list item is updated.");
        }

        log.info("verify Attachments field");
        String attachmentOrder1 = attachedFile+"\n"+itemFile;
        String attachmentOrder2 = itemFile+"\n"+attachedFile;

        if (dataListsPage.getFilterTypeList().contains(attachmentOrder1)){
            assertTrue(dataListsPage.isAttachmentsUpdated(attachmentOrder1), "Attachments updated");
        } else if (dataListsPage.getFilterTypeList().contains(attachmentOrder2)) {
            assertTrue(dataListsPage.isAttachmentsUpdated(attachmentOrder2), "Attachments updated");
        } else throw new RuntimeException("Assertion Failure: Check Attachment column files & order of files in UI");
    }
}