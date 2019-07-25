package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.taskListSimple;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.SimpleTaskAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class EditSimpleTaskListItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    String userName = String.format("User-%s", RandomData.getRandomAlphanumeric());
    String siteName = String.format("SiteName%s", RandomData.getRandomAlphanumeric());
    String taskListSimple = String.format("Simple task item name%s", RandomData.getRandomAlphanumeric());
    String itemTitle = "Simple task item title";
    String itemDescription = "Simple task item description";
    String itemComment = "Simple task item comment";
    CMISUtil.Priority itemPriority = CMISUtil.Priority.Low;
    CMISUtil.Status itemStatus = CMISUtil.Status.ON_HOLD;

    String newItemTitle = " edited ItemTitle";
    String newItemDescription = "edited ItemDescription";
    String newComments = "edited ItemComment";

    String newDateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.TASKS_SIMPLE, taskListSimple, "Simple Task list description");
        dataListsService.addTaskSimpleItem(adminUser, adminPassword, siteName, taskListSimple, itemTitle, itemDescription, null, itemPriority, itemStatus,
            itemComment);

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickSimpleTaskListItem(taskListSimple);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6588")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyEditDataItem()
    {
        LOG.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        List<String> item = Arrays.asList(itemTitle, itemDescription, null, itemPriority.toString(), itemStatus.getValue(), itemComment);
        // assertTrue(dataListsPage.currentContent.isListItemDisplayed(item), itemTitle + " simple task list item is displayed.");
        // dataListsPage.currentContent.editItem(item);
        dataListsPage.clickEditButtonForListItem();

        LOG.info("STEP2: Provide new input for the Title field");
        editItemPopUp.editContent(SimpleTaskAgendaFields.Title.toString(), newItemTitle);

        LOG.info("STEP3: Provide new input for the Description field");
        editItemPopUp.editContent(SimpleTaskAgendaFields.Description.toString(), newItemDescription);

        LOG.info("STEP4: Select Due Date");
        editItemPopUp.editContent(SimpleTaskAgendaFields.DueDate.toString(), newDateString);

        LOG.info("STEP5: Select a Priority from dropdown");
        editItemPopUp.selectDropDownItem(itemPriority.toString(), "simpletaskPriority");

        LOG.info("STEP6: Select a Status value from dropdown");
        editItemPopUp.selectDropDownItem(itemStatus.getValue(), "simpletaskStatus");

        LOG.info("STEP7: Provide input for the Comments field");
        editItemPopUp.editContent(SimpleTaskAgendaFields.Comments.toString(), newComments);

        LOG.info("STEP8: Click on Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.currentContent.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");
        item = Arrays.asList(newItemTitle, newItemDescription, newDateString, itemPriority.toString(), itemStatus.getValue(), newComments);
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(item), newItemTitle + " simple task list item is displayed.");
    }
}