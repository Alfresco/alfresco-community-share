package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
    SelectPopUpPage selectPopUpPage;

    @Autowired
    MyTasksDashlet myTasksDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String docName = String.format("docName%s", RandomData.getRandomAlphanumeric());
    private String docName1 = String.format("docName1%s", RandomData.getRandomAlphanumeric());
    private String docContent = String.format("docContent%s", RandomData.getRandomAlphanumeric());
    private String startWorkflowAction = "Start Workflow";

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName2", "lastName2");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.HTML, docName1, docContent);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @Bug (id = "MNT-17015 won't fix")
    @TestRail (id = "C8344")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskAndAssignToYourself()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("New Task");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButton();
        selectPopUpPage.search(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow(documentLibraryPage);
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);
    }

    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8345")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskAndAssignToAnotherUser()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("New Task");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowTest");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButton();
        selectPopUpPage.search(user2);
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + user2 + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow(documentLibraryPage);
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as user2.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
        assertTrue(myTasksDashlet.isTaskPresent("WorkflowTest"), "Task is not present in Active tasks");
        cleanupAuthenticatedSession();
    }

    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8376")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void cancelStartingWorkflow()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, startWorkflowAction, startWorkflowPage);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("New Task");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on cancel button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButton();
        selectPopUpPage.search(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.cancelStartWorkflow();
        Assert.assertTrue(getBrowser().getCurrentUrl().endsWith("documentlibrary"), "User is successfully redirected to document library page");
    }

    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8388")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
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
        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("New Task");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on start workflow button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButton();
        selectPopUpPage.search(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow(documentLibraryPage);
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName1), "Missing start workflow icon for" + docName1);
    }
}