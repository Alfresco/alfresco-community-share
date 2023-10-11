package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.issueList;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.IssueFields;
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
public class EditIssueItemTest extends BaseTest
{
    //@Autowired
    DataListsPage dataListsPage;

   // @Autowired
    EditItemPopUp editItemPopUp;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;

    String random = RandomData.getRandomAlphanumeric();
    String listName = "List name" + random;
    String itemTitle = "Item title";
    String itemId = "item ID-" + random;
    String itemDescription = "item description";
    String itemComment = "item comment";
    CMISUtil.Priority itemPriority = CMISUtil.Priority.Normal;
    CMISUtil.Status itemStatus = CMISUtil.Status.NOT_STARTED;
    String itemFile = "testFile1";
    String newItemTitle = "edited ItemTitle";
    String newItemID = "edited ItemID";
    String newItemDescription = "edited ItemDescription";
    String newItemComments = "edited ItemComment";
    String newItemDate = "01/11/2016";
    String updatedItemDate = "Tue 1 Nov 2016";
    String newItemStatus = CMISUtil.Status.COMPLETE.getValue();
    String newItemPriority = CMISUtil.Priority.High.toString();
    String attachedFile = "testDoc.txt";
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
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataListsService.DataList.ISSUE_LIST, listName, "Issue List description");

        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + itemFile);
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + attachedFile);
        dataListsService.addIssueListItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), listName, itemId, itemTitle, Collections.singletonList(userName.get().getUsername()), itemStatus, itemPriority,
            itemDescription, null, itemComment, Collections.singletonList(itemFile));

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickIssueListItem(listName);
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

    @TestRail (id = "C6713")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void saveEditItem()
    {
        log.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Fill in 'Edit Data' form fields with valid data");
        editItemPopUp.editContent(IssueFields.Id.toString(), newItemID);
        editItemPopUp.editContent(IssueFields.Title.toString(), newItemTitle);
        editItemPopUp.addAssignedTo(userAssignee.get().getUsername());
        editItemPopUp.selectDropDownItem(newItemStatus, "issueStatus");
        editItemPopUp.selectDropDownItem(newItemPriority, "issuePriority");
        editItemPopUp.editContent(IssueFields.Description.toString(), newItemDescription);
        editItemPopUp.editContent(IssueFields.DueDate.toString(), newItemDate);
        editItemPopUp.editContent(IssueFields.Comments.toString(), newItemComments);
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        log.info("STEP3: Click Save button");
        editItemPopUp.clickSave();

        List<String> expectedList = Arrays.asList(newItemID, newItemTitle, newItemStatus, newItemPriority, newItemDescription, updatedItemDate, newItemComments);
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }
}