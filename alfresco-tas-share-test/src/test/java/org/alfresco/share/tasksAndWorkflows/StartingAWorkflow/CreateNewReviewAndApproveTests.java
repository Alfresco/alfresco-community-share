package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.GroupService;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j

/**
 * @author iulia.cojocea
 */

public class CreateNewReviewAndApproveTests extends BaseTest
{
    //@Autowired
    DocumentLibraryPage documentLibraryPage;

    //@Autowired
    StartWorkflowPage startWorkflowPage;

    //@Autowired
    SelectPopUpPage selectPopUpPage;
    @Autowired
    protected GroupService groupService;

//    @Autowired
    MyTasksDashlet myTasksDashlet;

   // @Autowired
    UserDashboardPage userDashboardPage;
    private String docName = String.format("docName%s", RandomData.getRandomAlphanumeric());
    private String docContent = String.format("docContent%s", RandomData.getRandomAlphanumeric());
    private String group = String.format("testGroup%s", RandomData.getRandomAlphanumeric());
    private String startWorkflowAction = "Start Workflow";
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user3 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating test user");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating test user");
        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating test user");
        user3.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteNameC2216 is created");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        startWorkflowPage = new StartWorkflowPage(webDriver);
        selectPopUpPage = new SelectPopUpPage(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        myTasksDashlet = new MyTasksDashlet(webDriver);

        authenticateUsingLoginPage(user1.get());
        contentService.createDocument(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getTitle(), CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        groupService.createGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), group);
        groupService.inviteGroupToSite(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), group, "SiteCollaborator");
        groupService.addUserToGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), group, user1.get().getUsername());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteUsersIfNotNull(user1.get());

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user2.get().getUsername());
        deleteUsersIfNotNull(user2.get());

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user3.get().getUsername());
        deleteUsersIfNotNull(user3.get());

        deleteSitesIfNotNull(siteName.get());
    }

    @TestRail (id = "C8351")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewReviewAndApproveSingleReviewer() {
        String firstName2 = user2.get().getFirstName();
        String lastName2 = user2.get().getLastName();
        log.info("Precondition");
        authenticateUsingCookies(user1.get());
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'Review And Approve (single reviewer)' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Review And Approve (single reviewer)");

        log.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(user2.get().getUsername());
        selectPopUpPage.clickAddIcon(firstName2 +" "+ lastName2 +" "+ "(" + user2.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        log.info("STEP 5: logout then login as user2.");
        authenticateUsingCookies(user2.get());
        userDashboardPage.navigate(user2.get());
        myTasksDashlet.assertTaskNameEqualsTo("WorkflowTest");
    }

    @TestRail (id = "C8349")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewReviewAndApproveOneOrMoreReviewers()
    {
        String firstName2 = user2.get().getFirstName();
        String lastName2 = user2.get().getLastName();

        String firstName3 = user3.get().getFirstName();
        String lastName3 = user3.get().getLastName();

        log.info("Precondition");
        authenticateUsingCookies(user1.get());
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'Review and Approve (one or more reviewers)' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Review and Approve (one or more reviewers)");

        log.info("STEP 4: Add message, select a Due date, priority, assign it to a user different then you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonMultipleAssignees();
        selectPopUpPage.search(user2.get().getUsername());
        selectPopUpPage.clickAddIcon(firstName2 +" "+ lastName2 +" "+ "(" + user2.get().getUsername() + ")");
        selectPopUpPage.search(user3.get().getUsername());
        selectPopUpPage.clickAddIcon(firstName3 +" "+ lastName3 +" "+ "(" + user3.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        log.info("STEP 5: logout then login as user2.");
        authenticateUsingCookies(user2.get());

        userDashboardPage.navigate(user2.get().getUsername());
        myTasksDashlet.assertTaskNameEqualsTo("WorkflowTest");

        log.info("STEP 6: logout then login as user3.");
        authenticateUsingCookies(user3.get());
        userDashboardPage.navigate(user3.get());
        myTasksDashlet.assertTaskNameEqualsTo("WorkflowTest");
    }

    @TestRail (id = "C8348")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewReviewAndApproveGroupReview()
    {
        log.info("Precondition");
        authenticateUsingCookies(user1.get());

        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'Review and Approve (group review)' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Review and Approve (group review)");

        log.info("STEP 4: Add message, select a Due date, priority, assign to a group and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickGroupSelectButton();
        selectPopUpPage.search(group);
        selectPopUpPage.clickAddIcon(group);
        selectPopUpPage.clickOkButton();

        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        log.info("STEP 5: Navigate to User Dashboard.");
        userDashboardPage.navigate(user1.get().getUsername());
        myTasksDashlet.assertTaskNameEqualsTo("WorkflowTest");
    }

    @TestRail (id = "C8350")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewReviewAndApprovePooledReview()
    {
        log.info("Precondition");
        authenticateUsingCookies(user1.get());
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'Review and Approve (pooled review)' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Review and Approve (pooled review)");

        log.info("STEP 4: Add message, select a Due date, priority, assign to a group and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickGroupSelectButton();
        selectPopUpPage.search(group);
        selectPopUpPage.clickAddIcon(group);
        selectPopUpPage.clickOkButton();

        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        log.info("STEP 5: Navigate to User Dashboard.");
        userDashboardPage.navigate(user1.get().getUsername());
        myTasksDashlet.assertTaskNameEqualsTo("WorkflowTest");
    }
}
