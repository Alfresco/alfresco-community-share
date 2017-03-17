package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.SelectAssigneeToWorkflowPopUp;
import org.alfresco.po.share.tasksAndWorkflows.SelectAssigneesToWorkflowPopUp;
import org.alfresco.po.share.tasksAndWorkflows.SelectGroupAssigneeToWorkflowPopUp;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */

public class CreateNewReviewAndApproveTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @Autowired
    SelectAssigneeToWorkflowPopUp selectAssigneeToWorkflowPopUp;

    @Autowired
    SelectPopUpPage selectPopUpPage;

    @Autowired
    MyTasksDashlet myTasksDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SelectAssigneesToWorkflowPopUp selectAssigneesToWorkflowPopUp;

    @Autowired
    SelectGroupAssigneeToWorkflowPopUp selectGroupAssigneeToWorkflowPopUp;

    private String user1 = "User1" + DataUtil.getUniqueIdentifier();
    private String user2 = "User2" + DataUtil.getUniqueIdentifier();
    private String user3 = "User3" + DataUtil.getUniqueIdentifier();
    private String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
    private String docName = "docName" + DataUtil.getUniqueIdentifier();
    private String docContent = "docContent" + DataUtil.getUniqueIdentifier();
    private String group = "testGroup" + DataUtil.getUniqueIdentifier();
    private String startWorkflowAction = "Start Workflow";
    private Alert alert;
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName1", "lastName1");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName2", "lastName2");
        userService.create(adminUser, adminPassword, user3, password, user3 + domain, "firstName3", "lastName3");
        siteService.create(user1, password, domain, siteName, siteName, Visibility.PUBLIC);
        contentService.createDocument(user1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        groupService.createGroup(adminUser, adminPassword, group);
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, group, "SiteCollaborator");
        groupService.addUserToGroup(adminUser, adminPassword, group, user1);
    }

    @TestRail(id = "C8351")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewReviewAndApproveSingleReviewer()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        getBrowser().waitInSeconds(4);
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'Review And Approve (single reviewer)' from the drop-down list.");
        getBrowser().waitInSeconds(4);
        startWorkflowPage.selectWorkflowToStartFromDropdownList("Review And Approve (single reviewer)");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.clickOnDatePickerIcon();
        startWorkflowPage.selectCurrentDate();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectAssigneeButton();
        selectAssigneeToWorkflowPopUp.searchUser(user2);
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + user2 + ")");
        selectAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        if(startWorkflowPage.isAlertPresent())
        {
            getBrowser().handleModalDialogAcceptingAlert();
        }
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
        Assert.assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
    }

    @TestRail(id = "C8349")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewReviewAndApproveOneOrMoreReviewers()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        getBrowser().waitInSeconds(4);
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'Review and Approve (one or more reviewers)' from the drop-down list.");
        getBrowser().waitInSeconds(4);
        startWorkflowPage.selectWorkflowToStartFromDropdownList("Review and Approve (one or more reviewers)");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to a user different then you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.clickOnDatePickerIcon();
        startWorkflowPage.selectCurrentDate();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectAssigneesButton();
        selectAssigneesToWorkflowPopUp.searchUser(user2);
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + user2 + ")");
        selectAssigneesToWorkflowPopUp.clearSearchField();
        selectAssigneesToWorkflowPopUp.searchUser(user3);
        selectPopUpPage.clickAddIcon("firstName3 lastName3 (" + user3 + ")");
        selectAssigneesToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        if(startWorkflowPage.isAlertPresent())
        {
            getBrowser().handleModalDialogAcceptingAlert();
        }
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
        Assert.assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");

        LOG.info("STEP 6: Logout then login as user3.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user3, password);
        userDashboardPage.navigate(user3);
        Assert.assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C8348")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewReviewAndApproveGroupReview()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        getBrowser().waitInSeconds(4);
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'Review and Approve (group review)' from the drop-down list.");
        getBrowser().waitInSeconds(4);
        startWorkflowPage.selectWorkflowToStartFromDropdownList("Review and Approve (group review)");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign to a group and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.clickOnDatePickerIcon();
        startWorkflowPage.selectCurrentDate();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectGroupAssigneeButton();
        selectGroupAssigneeToWorkflowPopUp.searchGroup(group);
        selectPopUpPage.clickAddIcon(group);
        selectGroupAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        if (startWorkflowPage.isAlertPresent())
        {
            getBrowser().handleModalDialogAcceptingAlert();
        }
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Navigate to User Dashboard.");
        userDashboardPage.navigate(user1);
        Assert.assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
    }

    @TestRail(id = "C8350")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewReviewAndApprovePooledReview()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        getBrowser().waitInSeconds(4);
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'Review and Approve (pooled review)' from the drop-down list.");
        getBrowser().waitInSeconds(4);
        startWorkflowPage.selectWorkflowToStartFromDropdownList("Review and Approve (pooled review)");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign to a group and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.clickOnDatePickerIcon();
        startWorkflowPage.selectCurrentDate();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectGroupAssigneeButton();
        selectGroupAssigneeToWorkflowPopUp.searchGroup(group);
        selectPopUpPage.clickAddIcon(group);
        selectGroupAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        getBrowser().waitInSeconds(6);
        if (startWorkflowPage.isAlertPresent())
        {
            getBrowser().handleModalDialogAcceptingAlert();
        }
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Navigate to User Dashboard.");
        userDashboardPage.navigate(user1);
        Assert.assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
    }
}
