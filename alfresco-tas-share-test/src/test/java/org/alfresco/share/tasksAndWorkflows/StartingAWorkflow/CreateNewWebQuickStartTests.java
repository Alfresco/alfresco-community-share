package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.SelectAssigneeToWorkflowPopUp;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author iulia.cojocea
 */
public class CreateNewWebQuickStartTests extends ContextAwareWebTest
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

    private String user1 = "User1" + DataUtil.getUniqueIdentifier();
    private String user2 = "User2" + DataUtil.getUniqueIdentifier();
    private String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
    private String docName = "docName-" + DataUtil.getUniqueIdentifier();
    private String docContent = "docContent-" + DataUtil.getUniqueIdentifier();
    private String startWorkflowAction = "Start Workflow";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName1", "lastName1");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName2", "lastName2");
        siteService.create(user1, password, domain, siteName, siteName, Visibility.PUBLIC);
        content.createDocument(user1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
    }

    @Bug(id = "MNT-17015")
    @TestRail(id = "C8352")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewWebQuickStartReviewAndPublishSectionStructure()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'Web Quick Start: Review & Publish Section Structure' from the drop-down list.");
        startWorkflowPage.selectWorkflowToStartFromDropdownList("Web Quick Start: Review & Publish Section Structure");

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
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
        assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");

        cleanupAuthenticatedSession();
    }

    @Bug(id = "MNT-17015")
    @TestRail(id = "C8353")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewWebQuickStartReviewAndPublishWebAssets()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'Web Quick Start: Review & Publish Web Assets' from the drop-down list.");
        startWorkflowPage.selectWorkflowToStartFromDropdownList("Web Quick Start: Review & Publish Web Assets");

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
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
        assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
    }
}