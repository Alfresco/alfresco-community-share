package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.taskListAdvanced;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.AdvancedTaskAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditAdvancedTaskListItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    String userName = String.format("User-%s", RandomData.getRandomAlphanumeric());
    String userAssignee = String.format("UserA-%s", RandomData.getRandomAlphanumeric());
    String siteName = String.format("SiteName-%s", RandomData.getRandomAlphanumeric());
    String listName = String.format("Advanced task item name%s", RandomData.getRandomAlphanumeric());
    String itemTitle = "Advanced task item title";
    String itemDescription = "Advanced task item description";
    String itemComment = "Advanced task item comment";
    String itemFile = "testFile1";
    CMISUtil.Priority itemPriority = CMISUtil.Priority.Low;
    CMISUtil.Status itemStatus = CMISUtil.Status.ON_HOLD;

    String newItemTitle = " edited ItemTitle";
    String newItemDescription = "edited ItemDescription";
    String newComments = "edited ItemComment";
    String newComplete = "5";
    String newItemStatus = CMISUtil.Status.IN_PROGRESS.getValue();
    String newItemPriority = CMISUtil.Priority.High.toString();
    String attachedFile = "testDoc.txt";

    String date = "29/09/2016";
    String date2 = "Thu 29 Sep 2016";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        userService.create(adminUser, adminPassword, userAssignee, password, userAssignee + domain, userAssignee, userAssignee);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.TASKS_ADVANCED, listName, "Advanced Task list description.");

        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + attachedFile);
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + itemFile);
        dataListsService.addTaskAdvancedItem(adminUser, adminPassword, siteName, listName, itemTitle, itemDescription, null, null, Collections.singletonList(userName),
            itemPriority, itemStatus, 0, itemComment, Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickAdvancedTaskListItem(listName);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        userService.delete(adminUser, adminPassword, userAssignee);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userAssignee);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C10354")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyEditDataItem()
    {
        LOG.info("STEP1: Click 'Edit' icon for the advanced task list item to be edited");
        dataListsPage.clickEditButtonForListItem();

        LOG.info("STEP2: Provide new input for Title field");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.Title.toString(), newItemTitle);

        LOG.info("STEP3: Provide new input for Description field");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.Description.toString(), newItemDescription);

        LOG.info("STEP4: Select Start Date");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.StartDate.toString(), date);

        LOG.info("STEP5: Select End Date");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.EndDate.toString(), date);

        LOG.info("STEP6: Edit the Assignee field");
        editItemPopUp.addAssignedToAdvancedTask(userAssignee);

        LOG.info("STEP7: Select a Priority from dropdown");
        editItemPopUp.selectDropDownItem(newItemPriority, "taskPriority");

        LOG.info("STEP8: Select a Status value from dropdown");
        editItemPopUp.selectDropDownItem(newItemStatus, "taskStatus");

        LOG.info("STEP9: Edit %Complete field");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.Complete.toString(), newComplete);

        LOG.info("STEP10: Provide input for Comments field");
        editItemPopUp.editContent(AdvancedTaskAgendaFields.Comments.toString(), newComments);

        LOG.info("STEP11: Edit Attachments, add new attachment");
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        LOG.info("STEP12: Click on Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.currentContent.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");
        List<String> attachments = Arrays.asList(attachedFile, itemFile);

        List<String> item = Arrays.asList(newItemTitle, newItemDescription, date2, date2, userAssignee, newItemPriority, newItemStatus, newComplete,
            newComments, attachments.toString());
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(item), newItemTitle + " Advanced task list item is displayed.");
    }
}