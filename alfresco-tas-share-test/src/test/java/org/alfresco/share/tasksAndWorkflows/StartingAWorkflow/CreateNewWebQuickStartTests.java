package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import static org.testng.Assert.assertTrue;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * @author iulia.cojocea
 */
@Deprecated
/* @deprecated for ACS 6.0, 5.1.5 and above *
 */
public class CreateNewWebQuickStartTests extends ContextAwareWebTest
{
    //@Autowired
    DocumentLibraryPage documentLibraryPage;

    //@Autowired
    StartWorkflowPage startWorkflowPage;

    //@Autowired
    SelectPopUpPage selectPopUpPage;

//    @Autowired
    MyTasksDashlet myTasksDashlet;

    //@Autowired
    UserDashboardPage userDashboardPage;

    private String user1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String docName = String.format("docName-%s", RandomData.getRandomAlphanumeric());
    private String docContent = String.format("docContent-%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName1", "lastName1");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName2", "lastName2");
        siteService.create(user1, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(user1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
        siteService.delete(adminUser, adminPassword, siteName);
    }


//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8352")
    //@Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewWebQuickStartReviewAndPublishSectionStructure()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'Web Quick Start: Review & Publish Section Structure' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Web Quick Start: Review & Publish Section Structure");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(user2);
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + user2 + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
//        assertTrue(myTasksDashlet.assertTaskNameEqualsTo("WorkflowTest"), "Task is not present in Active tasks");

        cleanupAuthenticatedSession();
    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8353")
    // @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewWebQuickStartReviewAndPublishWebAssets()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'Web Quick Start: Review & Publish Web Assets' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("Web Quick Start: Review & Publish Web Assets");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(user2);
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + user2 + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
//        assertTrue(myTasksDashlet.assertTaskNameEqualsTo("WorkflowTest"), "Task is not present in Active tasks");
    }
}