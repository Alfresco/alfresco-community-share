package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.taskListSimple;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.SimpleTaskAgendaFields;
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
/**
 * @author Laura.Capsa
 */
public class EditSimpleTaskListItemTest extends BaseTest
{
    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;

   // @Autowired
    DataListsPage dataListsPage;

    //@Autowired
    EditItemPopUp editItemPopUp;

    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    String taskListSimple = String.format("Simple task item name%s", RandomData.getRandomAlphanumeric());
    String itemTitle = "Simple task item title";
    String itemDescription = "Simple task item description";
    String itemComment = "Simple task item comment";
    CMISUtil.Priority itemPriority = CMISUtil.Priority.Low;
    CMISUtil.Status itemStatus = CMISUtil.Status.ON_HOLD;

    String newItemTitle = "edited ItemTitle";
    String newItemDescription = "edited ItemDescription";
    String newComments = "edited ItemComment";
    String newDateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    private String formatDate(DateTime date)
    {
        return date.toString("EE d MMM yyyy");
    }

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
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataListsService.DataList.TASKS_SIMPLE, taskListSimple, "Simple Task list description");
        dataListsService.addTaskSimpleItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), taskListSimple, itemTitle, itemDescription, null, itemPriority, itemStatus,
            itemComment);

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickSimpleTaskListItem(taskListSimple);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C6587")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void VerifyEditDataItemForSimpleTaskItem() {
        log.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        dataListsPage
            .clickEditButtonForListItem();

        log.info("STEP2: Verify Edit Data item form");
        editItemPopUp
            .verifyDataItemForm(SimpleTaskAgendaFields.Title.toString())
            .verifyDataItemForm(SimpleTaskAgendaFields.Description.toString())
            .verifyDataItemForm(SimpleTaskAgendaFields.DueDate.toString())
            .verifyDataItemForm(CreateNewItemPopUp.DropDownLists.simpletaskPriority.toString())
            .verifyDataItemForm(CreateNewItemPopUp.DropDownLists.simpletaskPriority.toString())
            .verifyDataItemForm(SimpleTaskAgendaFields.Comments.toString());
        editItemPopUp
            .assertCalendarIconIsDisplayed()
            .assertSaveButtonIsDisplayed()
            .assertCancelButtonIsDisplayed()
            .assertCloseIconIsDisplayed();
        editItemPopUp
            .assertVerifyDropDownOptions("High")
            .assertVerifyDropDownOptions("Normal")
            .assertVerifyDropDownOptions("Low");
        editItemPopUp
            .assertVerifyDropDownOptions("Not Started")
            .assertVerifyDropDownOptions("In Progress")
            .assertVerifyDropDownOptions("Complete")
            .assertVerifyDropDownOptions("On Hold");

    }

    @TestRail (id = "C6588")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void verifyEditDataItem()
    {
        log.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Provide new input for the Title field");
        editItemPopUp.editContent(SimpleTaskAgendaFields.Title.toString(), newItemTitle);

        log.info("STEP3: Provide new input for the Description field");
        editItemPopUp.editContent(SimpleTaskAgendaFields.Description.toString(), newItemDescription);

        log.info("STEP4: Select Due Date");
        editItemPopUp.editContent(SimpleTaskAgendaFields.DueDate.toString(), newDateString);

        log.info("STEP5: Select a Priority from dropdown");
        editItemPopUp.selectDropDownItem(itemPriority.toString(), "simpletaskPriority");

        log.info("STEP6: Select a Status value from dropdown");
        editItemPopUp.selectDropDownItem(itemStatus.getValue(), "simpletaskStatus");

        log.info("STEP7: Provide input for the Comments field");
        editItemPopUp.editContent(SimpleTaskAgendaFields.Comments.toString(), newComments);

        log.info("STEP8: Click on Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");

        DateTime today = new DateTime();
        List<String> expectedList = Arrays.asList(newItemTitle, newItemDescription, formatDate(today), itemPriority.toString(), itemStatus.getValue(), newComments);
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "simple task list item is updated.");
        }
    }
}