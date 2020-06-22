package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
    SelectPopUpPage selectPopUpPage;

    @Autowired
    MyTasksDashlet myTasksDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    private String user1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String user3 = String.format("User3%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String docName = String.format("docName%s", RandomData.getRandomAlphanumeric());
    private String docContent = String.format("docContent%s", RandomData.getRandomAlphanumeric());
    private String group = String.format("testGroup%s", RandomData.getRandomAlphanumeric());
    private String startWorkflowAction = "Start Workflow";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName1", "lastName1");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName2", "lastName2");
        userService.create(adminUser, adminPassword, user3, password, user3 + domain, "firstName3", "lastName3");
        siteService.create(user1, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(user1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        groupService.createGroup(adminUser, adminPassword, group);
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, group, "SiteCollaborator");
        groupService.addUserToGroup(adminUser, adminPassword, group, user1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
        userService.delete(adminUser, adminPassword, user3);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user3);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C8351")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewReviewAndApproveSingleReviewer()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'Review And Approve (single reviewer)' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Review And Approve (single reviewer)");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(user2);
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + user2 + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow(documentLibraryPage);
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
        Assert.assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
    }

    @TestRail (id = "C8349")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewReviewAndApproveOneOrMoreReviewers()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'Review and Approve (one or more reviewers)' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Review and Approve (one or more reviewers)");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to a user different then you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonMultipleAssignees();
        selectPopUpPage.search(user2);
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + user2 + ")");
        selectPopUpPage.search(user3);
        selectPopUpPage.clickAddIcon("firstName3 lastName3 (" + user3 + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow(documentLibraryPage);
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

    @TestRail (id = "C8348")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewReviewAndApproveGroupReview()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'Review and Approve (group review)' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Review and Approve (group review)");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign to a group and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickGroupSelectButton();
        selectPopUpPage.search(group);
        selectPopUpPage.clickAddIcon(group);
        selectPopUpPage.clickOkButton();

        startWorkflowPage.clickStartWorkflow(documentLibraryPage);
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Navigate to User Dashboard.");
        userDashboardPage.navigate(user1);
        Assert.assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
    }

    @TestRail (id = "C8350")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewReviewAndApprovePooledReview()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'Review and Approve (pooled review)' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Review and Approve (pooled review)");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign to a group and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickGroupSelectButton();
        selectPopUpPage.search(group);
        selectPopUpPage.clickAddIcon(group);
        selectPopUpPage.clickOkButton();

        startWorkflowPage.clickStartWorkflow(documentLibraryPage);
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Navigate to User Dashboard.");
        userDashboardPage.navigate(user1);
        Assert.assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
    }
}
