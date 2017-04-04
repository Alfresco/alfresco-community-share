package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
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
public class CreateNewTaskTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    HeaderMenuBar headerMenuBar;
    
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

    private String testUser = String.format("testUser%s", DataUtil.getUniqueIdentifier());
    private String user2 = String.format("User2%s", DataUtil.getUniqueIdentifier());
    private String siteName = String.format("siteName%s", DataUtil.getUniqueIdentifier());
    private String docName = String.format("docName%s", DataUtil.getUniqueIdentifier());
    private String docName1 = String.format("docName1%s", DataUtil.getUniqueIdentifier());
    private String docContent = String.format("docContent%s", DataUtil.getUniqueIdentifier());
    private String startWorkflowAction = "Start Workflow";

    @BeforeClass(alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName2", "lastName2");
        siteService.create(testUser, password, domain, siteName, siteName, Visibility.PUBLIC);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.HTML, docName1, docContent);
    }

    @Bug(id = "MNT-17015")
    @TestRail(id = "C8344")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewTaskAndAssignToYourself()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectWorkflowToStartFromDropdownList("New Task");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.clickOnDatePickerIcon();
        startWorkflowPage.selectCurrentDate();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectAssigneeButton();
        selectAssigneeToWorkflowPopUp.searchUser(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);
    }

    @Bug(id = "MNT-17015")
    @TestRail(id = "C8345")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void createNewTaskAndAssignToAnotherUser()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectWorkflowToStartFromDropdownList("New Task");

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
        getBrowser().waitInSeconds(3);
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
        assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
        cleanupAuthenticatedSession();
    }

    @Bug(id = "MNT-17015")
    @TestRail(id = "C8376")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void cancelStartingWorkflow()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectWorkflowToStartFromDropdownList("New Task");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on cancel button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.clickOnDatePickerIcon();
        startWorkflowPage.selectCurrentDate();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectAssigneeButton();
        selectAssigneeToWorkflowPopUp.searchUser(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.cancelStartWorkflow();
    }

    @Bug(id = "MNT-17015")
    @TestRail(id = "C8388")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void startWorkflowForMultipleFiles()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Select multiple files from 'Document Library ' then click 'Selected Items...' dropdown button, then click 'Start Workflow...'");
        documentLibraryPage.clickCheckBox(docName);
        documentLibraryPage.clickCheckBox(docName1);
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Start Workflow...");

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        startWorkflowPage.selectAWorkflow();

        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectWorkflowToStartFromDropdownList("New Task");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on start workflow button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.clickOnDatePickerIcon();
        startWorkflowPage.selectCurrentDate();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectAssigneeButton();
        selectAssigneeToWorkflowPopUp.searchUser(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName1), "Missing start workflow icon for" + docName1);
    }
}