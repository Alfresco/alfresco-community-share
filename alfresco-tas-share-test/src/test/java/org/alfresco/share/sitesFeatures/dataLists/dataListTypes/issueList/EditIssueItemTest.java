package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.issueList;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.IssueFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditIssueItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    String random = RandomData.getRandomAlphanumeric();
    String userName = "User-" + random;
    String userAssignee = "userAssignee-" + random;
    String siteName = "SiteName-" + random;
    String listName = "List name" + random;
    String itemTitle = "Item title";
    String itemId = "item ID-" + random;
    String itemDescription = "item description";
    String itemComment = "item comment";
    CMISUtil.Priority itemPriority = CMISUtil.Priority.Normal;
    CMISUtil.Status itemStatus = CMISUtil.Status.NOT_STARTED;
    String itemFile = "testFile1";

    String newItemTitle = " edited ItemTitle";
    String newItemID = "edited ItemID";
    String newItemDescription = "edited ItemDescription";
    String newItemComments = "edited ItemComment";
    String newItemDate = "01/11/2016";
    String updatedItemDate = "Thu 10 Nov 2016";
    String newItemStatus = CMISUtil.Status.COMPLETE.getValue();
    String newItemPriority = CMISUtil.Priority.High.toString();
    String attachedFile = "testDoc.txt";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        userService.create(adminUser, adminPassword, userAssignee, password, userAssignee + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.ISSUE_LIST, listName, "Issue List description");
        
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + itemFile);
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + attachedFile);
        dataListsService.addIssueListItem(adminUser, adminPassword, siteName, listName, itemId, itemTitle, Collections.singletonList(userName), itemStatus, itemPriority,
                itemDescription, null, itemComment, Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickIssueListItem(listName);
    }

    @TestRail(id = "C6713")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void saveEditItem()
    {
        LOG.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        // List<String> item = Arrays.asList(itemId, itemTitle, userName, itemStatus.getValue(), itemPriority.toString(), itemDescription, "", itemComment,
        // itemFile);
        // dataListsPage.currentContent.editItem(item);
        dataListsPage.clickEditButtonForListItem();

        LOG.info("STEP2: Fill in 'Edit Data' form fields with valid data");
        editItemPopUp.editContent(IssueFields.Id.toString(), newItemID);
        editItemPopUp.editContent(IssueFields.Title.toString(), newItemTitle);
        editItemPopUp.addAssignedTo(userAssignee);
        editItemPopUp.selectDropDownItem(newItemStatus, "issueStatus");
        editItemPopUp.selectDropDownItem(newItemPriority, "issuePriority");
        editItemPopUp.editContent(IssueFields.Description.toString(), newItemDescription);
        editItemPopUp.editContent(IssueFields.DueDate.toString(), newItemDate);
        editItemPopUp.editContent(IssueFields.Comments.toString(), newItemComments);
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        LOG.info("STEP3: Click Save button");
        editItemPopUp.clickSave();
        List<String> attachmentsList = Arrays.asList(attachedFile, itemFile);
        List<String> expectedItem = Arrays.asList(newItemID, newItemTitle, userAssignee, newItemStatus, newItemPriority, newItemDescription, updatedItemDate,
                newItemComments, attachmentsList.toString());
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(expectedItem), newItemTitle + " issue list item is displayed.");
    }
}