package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.taskListAdvanced;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.AdvancedTaskAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
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
    DataListsService dataLists;

    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    private List<DashboardCustomization.Page> pagesToAdd = new ArrayList<>();
    String userName = "User-" + DataUtil.getUniqueIdentifier();
    String userAssignee = "UserA-" + DataUtil.getUniqueIdentifier();
    String siteName = "SiteName-" + DataUtil.getUniqueIdentifier();
    String listName = "Advanced task item name" + DataUtil.getUniqueIdentifier();
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

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(DashboardCustomization.Page.DATALISTS);
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        userService.create(adminUser, adminPassword, userAssignee, password, "@tests.com", userAssignee, userAssignee);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        dataLists.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.TASKS_ADVANCED, listName, "Advanced Task list description.");

        String path = srcRoot + "testdata" + File.separator;
        contentService.uploadFileInSite(userName, password, siteName, path + attachedFile);
        contentService.uploadFileInSite(userName, password, siteName, path + itemFile);
        dataLists.addTaskAdvancedItem(adminUser, adminPassword, siteName, listName, itemTitle, itemDescription, null, null, Collections.singletonList(userName),
                itemPriority, itemStatus, 0, itemComment, Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickAdvancedTaskListItem(listName);
    }

    @TestRail(id = "C10354")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
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

        cleanupAuthenticatedSession();
    }
}