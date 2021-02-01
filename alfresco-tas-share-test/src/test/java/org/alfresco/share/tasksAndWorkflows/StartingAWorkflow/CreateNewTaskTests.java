package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
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
public class CreateNewTaskTests extends ContextAwareWebTest
{
    //@Autowired
    SelectDocumentPopupPage selectDocumentPopupPage;

    @Autowired
    WorkflowService workflowService;

    //@Autowired
    DocumentLibraryPage documentLibraryPage;

    //@Autowired
    DocumentDetailsPage documentDetailsPage;

    //@Autowired
    HeaderMenuBar headerMenuBar;

    //@Autowired
    StartWorkflowPage startWorkflowPage;

    //@Autowired
    SelectPopUpPage selectPopUpPage;

    //@Autowired
    MyTasksDashlet myTasksDashlet;

    //@Autowired
    EditTaskPage editTaskPage;

    //@Autowired
    CustomizeUserDashboardPage customizeUserDashboardPage;

    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String testUser = String.format("testUser%s", uniqueIdentifier);
    private String testUser2 = String.format("testUser2%s", uniqueIdentifier);
    private String C8345username = String.format("C8345username%s", uniqueIdentifier);
    private String C286291usernameA = String.format("C286291usernameA%s", uniqueIdentifier);
    private String C286291usernameB = String.format("C286291usernameB%s", uniqueIdentifier);
    private String siteName = String.format("siteName%s", uniqueIdentifier);
    private String C286291siteName = String.format("C286291siteName%s", uniqueIdentifier);
    private String docName = String.format("docName%s", uniqueIdentifier);
    private String docName1 = String.format("docName1%s", uniqueIdentifier);
    private String folderName = String.format("folderName%s", uniqueIdentifier);
    private String docContent = String.format("docContent%s", uniqueIdentifier);
    private String workflowMessage = "PRR_workflow_descripion";
    private String newTaskName = "New Task";
    private String file1 = "myFile.txt";
    private String file2 = "C42550Doc.docx";
    private ArrayList<String> noItems = new ArrayList<>();
    private String date = DataUtil.formatDate(new Date(), DataUtil.DATE_FORMAT);

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, testUser2, password, testUser2 + domain, "firstName2", "lastName2");
        userService.create(adminUser, adminPassword, C8345username, password, C8345username + domain, "C8345firstName",
            "C8345lastName");
        userService.create(adminUser, adminPassword, C286291usernameA, password, C286291usernameA + domain, "C286291firstNameA",
            "C286291lastNameA");
        userService.create(adminUser, adminPassword, C286291usernameB, password, C286291usernameB + domain, "C286291firstNameB",
            "C286291lastNameB");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.create(C286291usernameA, password, domain, C286291siteName, C286291siteName, SiteService.Visibility.PUBLIC);
        contentService.createFolder(C286291usernameA, password, folderName, C286291siteName);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, "/");
        contentService.uploadFileInRepository(adminUser, adminPassword, folderName, testDataFolder + file1);
        contentService.uploadFileInRepository(adminUser, adminPassword, folderName, testDataFolder + file2);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.HTML, docName1, docContent);
        contentService.createDocument(C286291usernameA, password, C286291siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName,
            docContent);
        contentService.createDocumentInFolder(C286291usernameA, password, C286291siteName, folderName, CMISUtil.DocumentType.HTML,
            docName1, docContent);

    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
        userService.delete(adminUser, adminPassword, testUser2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser2);
        siteService.delete(adminUser, adminPassword, siteName);
    }

//    @Bug (id = "MNT-17015 won't fix")
    @TestRail (id = "C8344")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewTaskAndAssignToYourself()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow(newTaskName);

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);
        cleanupAuthenticatedSession();
    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8345")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewTaskAndAssignToAnotherUser()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow(newTaskName);

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription(workflowMessage);
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(C8345username);
        selectPopUpPage.clickAddIcon("C8345firstName C8345lastName (" + C8345username + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        LOG.info("STEP 5: Logout then login as '" + C8345username + "'.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(C8345username, password);
        userDashboard.navigate(C8345username);
        assertTrue(myTasksDashlet.isTaskPresent(workflowMessage), "Task is not present in Active tasks");

        cleanupAuthenticatedSession();
    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8376")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void cancelStartingWorkflow()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow(newTaskName);

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on cancel button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.cancelStartWorkflow();
        Assert.assertTrue(getBrowser().getCurrentUrl().endsWith("documentlibrary"),
            "User '" + testUser + "' is successfully redirected to document library page");

        cleanupAuthenticatedSession();
    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
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
        startWorkflowPage.selectAWorkflow(newTaskName);

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on start workflow button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser);
        selectPopUpPage.clickAddIcon("firstName lastName (" + testUser + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for '" + docName + "'.");
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName1), "Missing start workflow icon for '" + docName1 + "'.");

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C286291")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewTaskAddItem()
    {
        LOG.info("Precondition 1: Login as first user '" + C286291usernameA + "'.");
        setupAuthenticatedSession(C286291usernameA, password);
        documentLibraryPage.navigate(C286291siteName);

        LOG.info("Precondition 2:  Hover over a file, click More then Start Workflow");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW);

        LOG.info("Precondition 3: New Task workflow is selected;");
        startWorkflowPage.selectAWorkflow(newTaskName);

        //Check if the item selected at the start of the workflow is displayed as attached into the Items List block
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName),
            "Document '" + docName + "' is not displayed as attached to workflow.");

        LOG.info("Precondition 4: Message fields id filled with a correct data, assignee is selected;");
        startWorkflowPage.addWorkflowDescription("PRR Task Message");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(C286291usernameB);
        selectPopUpPage.clickAddIcon("C286291firstNameB C286291lastNameB (" + C286291usernameB + ")");
        selectPopUpPage.clickOkButton();

        LOG.info("STEP 1: Click Add button;");
        startWorkflowPage.clickAddItemsButton();
        Assert.assertTrue(selectDocumentPopupPage.isSelectDocumentPopupPageHeaderDisplayed(), "'Add Items Popup' did not open.");

        //Check if the document used to start the workflow is displayed in PopUp but his 'Add' button is not
        Assert.assertTrue(selectDocumentPopupPage.isSearchedItemDisplayed(docName),
            "The document used to start the workflow is not displayed in the PopUp.");
        Assert.assertFalse(selectPopUpPage.isAddIconDisplayed(docName),
            "'Add' button is displayed for '" + docName + "' even if it shouldn't.");

        LOG.info("STEP 2: Go to folder where any content is located;");
        selectPopUpPage.clickItem(folderName);

        LOG.info("STEP 3: Verify impossibilities to open item (item does not look like link);");
        Assert.assertFalse(selectDocumentPopupPage.isItemClickable(docName1),
            "Document '" + docName1 + "' is displayed as a link.");

        LOG.info("STEP 4: Click Add button;");
        //Check if the document has 'Add' ('+') button and click on it
        Assert.assertTrue(selectPopUpPage.isAddIconDisplayed(docName1),
            "Add '+' button is not displayed for '" + docName1 + "' document.");
        selectPopUpPage.clickAddIcon(docName1);

        //Check if the Add button disappeared from the left column and the item is now displayed also on the right column with 'Remove' button
        Assert.assertFalse(selectPopUpPage.isAddIconDisplayed(docName1),
            "Add '+' button is still displayed for '" + docName1 + "' document, although it was pressed.");
        Assert.assertTrue(selectDocumentPopupPage.isSearchedItemDisplayed(docName1),
            "Document '" + docName1 + "' disappeared from the left column although it should remain displayed.");
        Assert.assertTrue(selectDocumentPopupPage.isSelectedItemDisplayed(docName1),
            "Document '" + docName1 + "' is not displayed in right column but it should.");
        Assert.assertTrue(selectPopUpPage.isRemoveIconDisplayed(docName1),
            "'Remove' button is not displayed for the selected item.");

        LOG.info("STEP 5: Click 'OK'.");
        selectPopUpPage.clickOkButton();
        Assert.assertFalse(selectDocumentPopupPage.isSelectDocumentPopupPageHeaderDisplayed(),
            "'Add Item' PopUp couldn't be closed.");

        //Check if 'Items List' block contains now the first and the previous attached documents.
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName),
            "Document '" + docName + "' is not displayed as attached to workflow.");
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName1),
            "Document '" + docName1 + "' is not displayed as attached to workflow.");

        LOG.info("STEP 6: Fill 'Message' field and click 'Start workflow' button.");
        startWorkflowPage.addWorkflowDescription(workflowMessage);
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentLibraryPage.isDocumentListDisplayed(),
            "After starting the workflow the browser wasn't redirected to 'Document Library' page.");

        LOG.info("STEP 7: Login as Assignee user.");
        setupAuthenticatedSession(C286291usernameB, password);
        userDashboard.navigate(C286291usernameB);

        LOG.info("STEP 8: Check if 'My Task' dashlet is displayed in user dashboard.");
        addMyTaskDashletToDashboard();

        LOG.info("STEP 9: Click the task's name.");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(workflowMessage),
            "'" + workflowMessage + "' task is not displayed in user's 'My Tasks' dashlet, but it should.");
        myTasksDashlet.clickTaskName(workflowMessage);
        //Assert.assertTrue(editTaskPage.getPageTitle().contains("Edit Task"), "Edit task page should be displayed!");

        LOG.info("STEP 10: Verify items list.");
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName),
            "Document '" + docName + "' is not displayed as attached to workflow.");
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName1),
            "Document '" + docName1 + "' is not displayed as attached to workflow.");

        cleanupAuthenticatedSession();
    }


    @TestRail (id = "C286360")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewTaskViaSelectedItemsSingleDocument()
    {
        LOG.info("Precondition 1: Any user logged in Share;");
        setupAuthenticatedSession(testUser, password);

        LOG.info("Precondition 2: Add My Tasks dashlet to My Dashboard;");
        addMyTaskDashletToDashboard();

        LOG.info("Precondition 3: Any site is created;");
        LOG.info("Precondition 4: Any content is added to the Document Library;");

        LOG.info("Precondition 5: Document library page is opened;");
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Select checkbox at the top left part of document.");
        documentLibraryPage.clickCheckBox(docName);
        Assert.assertTrue(documentLibraryPage.isContentSelected(docName), "Document '" + docName + "' wasn't selected.");

        LOG.info("STEP 2: Expand Selected Items menu.");
        LOG.info("STEP 3: Click Start Workflow action.");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW);

        LOG.info("STEP 4: Select Adhoc task (New Task).");
        startWorkflowPage.selectAWorkflow(newTaskName);
        Assert.assertTrue(startWorkflowPage.isWorkflowFormDisplayed(), "Workflow form is not displayed.");

        LOG.info("STEP 5: Fill in message text area with a valid data.");
        startWorkflowPage.addWorkflowDescription("C286360_" + workflowMessage);

        LOG.info("STEP 6: Click Calendar icon.");
        startWorkflowPage.clickDatePickerIcon();
        Assert.assertTrue(startWorkflowPage.isCalendarDisplayed(),
            "Calendar is not displayed after 'Date Picker Icon' was clicked.");

        LOG.info("STEP 7: Click any date.");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        Assert.assertEquals(date, startWorkflowPage.getWorkflowDueDateInputValue(),
            "The displayed date does not correspond with the selected one.");

        LOG.info("STEP 8: Click Assign to button.");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();

        LOG.info("STEP 9: Enter existent username and click Search button.");
        selectPopUpPage.search(testUser2);

        LOG.info("STEP 10: Click Select>> button.");
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + testUser2 + ")");

        LOG.info("STEP 11: Click OK button.");
        selectPopUpPage.clickOkButton();

        LOG.info("STEP 12: Leave Notify me check box unchecked.");
        startWorkflowPage.toggleSendEmailCheckBox(false);
        Assert.assertEquals(startWorkflowPage.getSendEmailCheckBoxValue(), "false", "The checkbox couldn't be unchecked.");

        LOG.info("STEP 13: Click OK button.");
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentLibraryPage.isDocumentListDisplayed(),
            "After starting the workflow the browser wasn't redirected to 'Document Library' page.");

        LOG.info("STEP 14: Log in as assignee user.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(testUser2, password);

        LOG.info("STEP 15: Add My tasks dahslet to My Dashboard;");
        addMyTaskDashletToDashboard();

        LOG.info("STEP 16: Verify Task is present.");
        Assert.assertTrue(myTasksDashlet.isTaskPresent("C286360_" + workflowMessage),
            "'" + "C286360_" + workflowMessage + "' task is not displayed in user's 'My Tasks' dashlet, but it should.");

        cleanupAuthenticatedSession();
    }


    @TestRail (id = "C286446")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskViaDetailsPage()
    {
        LOG.info("Precondition 1: Any user logged in Share;");
        setupAuthenticatedSession(testUser, password);

        LOG.info("Precondition 2: Add My Tasks dashlet to My Dashboard;");
        addMyTaskDashletToDashboard();

        LOG.info("Precondition 3: Any site is created;");
        LOG.info("Precondition 4: Any content is added to the Document Library;");

        LOG.info("Precondition 5: Content details page is opened;");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(docName);
        Assert.assertTrue(documentDetailsPage.isDocDetailsPageHeaderDisplayed(), "Document Details Page wasn't opened after clicking on files name.");

        LOG.info("STEP 1: Click Start Workflow action;");
        documentDetailsPage.clickDocumentActionsOption("Start Workflow");

        LOG.info("STEP 2: Select New Task;");
        startWorkflowPage.selectAWorkflow(newTaskName);
        Assert.assertTrue(startWorkflowPage.isWorkflowFormDisplayed(), "Workflow form is not displayed.");

        LOG.info("STEP 3: Fill in message text area with a valid data;");
        startWorkflowPage.addWorkflowDescription("C286446_" + workflowMessage);

        LOG.info("STEP 4: Click Calendar icon;");
        startWorkflowPage.clickDatePickerIcon();
        Assert.assertTrue(startWorkflowPage.isCalendarDisplayed(),
            "Calendar is not displayed after 'Date Picker Icon' was clicked.");

        LOG.info("STEP 5: Click any date;");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        Assert.assertEquals(date, startWorkflowPage.getWorkflowDueDateInputValue(),
            "The displayed date does not correspond with the selected one.");

        LOG.info("STEP 6: Click Assign to button;");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();

        LOG.info("STEP 7: Enter existent username and click Search button;");
        selectPopUpPage.search(testUser2);

        LOG.info("STEP 8: Click Select>> button;");
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + testUser2 + ")");

        LOG.info("STEP 9: Click OK button;");
        selectPopUpPage.clickOkButton();

        LOG.info("STEP 10: Leave Notify me check box unchecked;");
        startWorkflowPage.toggleSendEmailCheckBox(false);
        Assert.assertEquals(startWorkflowPage.getSendEmailCheckBoxValue(), "false", "The checkbox couldn't be unchecked.");

        LOG.info("STEP 11: Click Start Workflow button;");
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentDetailsPage.isDocDetailsPageHeaderDisplayed(),
            "After starting the workflow the browser wasn't redirected to 'Document Library' page.");

        LOG.info("STEP 12: Log in as assignee user;");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(testUser2, password);

        LOG.info("STEP 13: Add My tasks dahslet to My Dashboard;");
        addMyTaskDashletToDashboard();

        LOG.info("STEP 14: Verify Task is present;");
        Assert.assertTrue(myTasksDashlet.isTaskPresent("C286446_" + workflowMessage),
            "'" + "C286446_" + workflowMessage + "' task is not displayed in user's 'My Tasks' dashlet, but it should.");

        cleanupAuthenticatedSession();
    }


    @TestRail (id = "C286469")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void createNewTaskRemoveAll()
    {
        LOG.info("Precondition 1: Any folder is created in Repository with admin user;");
        LOG.info("Precondition 2: Any content is added to the folder;");

        LOG.info("Precondition 3: Any user logged in Share;");
        setupAuthenticatedSession(testUser, password);

        LOG.info("Precondition 4: Add My Tasks dashlet to My Dashboard;");
        addMyTaskDashletToDashboard();

        LOG.info("Precondition 5: Any site is created;");
        LOG.info("Precondition 6: Any content is added to the Document Library;");

        LOG.info("Precondition 7: Start workflow page is opened for the document;");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.START_WORKFLOW);

        LOG.info("Precondition 8: New Task workflow is selected;");
        startWorkflowPage.selectAWorkflow(newTaskName);
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName),
            "Document '" + docName + "' is not displayed as attached to workflow.");

        LOG.info("Precondition 9: Message field is filled with a correct data, assignee is selected;");
        startWorkflowPage.addWorkflowDescription("C286469_" + workflowMessage);
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser2);
        selectPopUpPage.clickAddIcon("firstName2 lastName2 (" + testUser2 + ")");
        selectPopUpPage.clickOkButton();

        LOG.info("Precondition 10: Several items are added;");
        startWorkflowPage.clickAddItemsButton();
        selectDocumentPopupPage.selectBreadcrumbPath("Company Home");
        selectPopUpPage.clickItem(folderName);
        selectDocumentPopupPage.clickAddIcon(file1);
        selectDocumentPopupPage.clickAddIcon(file2);
        selectPopUpPage.clickOkButton();

        //Check if the selected items are listed in "Items block"
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(file1),
            "Document '" + file1 + "' is not displayed as attached to workflow.");
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(file2),
            "Document '" + file2 + "' is not displayed as attached to workflow.");

        LOG.info("STEP 1: Click Remove all button;");
        startWorkflowPage.clickRemoveAllItemsButton();
        Assert.assertEquals(startWorkflowPage.getItemsList(), noItems.toString(), "Attached 'Items' list is not empty.");

        LOG.info("STEP 2: Click Start workflow button;");
        startWorkflowPage.clickStartWorkflow();

        LOG.info("STEP 3: Login as Assignee user;");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(testUser2, password);

        LOG.info("STEP 4: Add My Tasks dashlet to My Dashboard;");
        addMyTaskDashletToDashboard();

        LOG.info("STEP 5: Verify there are no items in Items part;");
        myTasksDashlet.clickTaskName("C286469_" + workflowMessage);
        Assert.assertEquals(editTaskPage.getItemsList(), noItems.toString(), "Attached 'Items' list is not empty.");
    }

    @TestRail (id = "C286368")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.TASKS })
    public void startWorkflowViaMore()
    {
        LOG.info("Precondition: My Tasks dashlet is added to My Dashboard;");
        setupAuthenticatedSession(testUser, password);

        addMyTaskDashletToDashboard();

        LOG.info("Precondition: Any content is added to the Document Library;");
        String documentName = "C286368";
        String documentContent = "TestRail id: C286368";
        contentService
            .createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, documentName, documentContent);

        LOG.info("Precondition: Document library page is opened;");
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Hover over a file and click More then Start Workflow.");
        documentLibraryPage.clickDocumentLibraryItemAction(documentName, ItemActions.START_WORKFLOW);
        Assert.assertTrue(startWorkflowPage.isStartWorkflowDropDownVisible(), "Start Workflow page is not opened");

        LOG.info("STEP 2: Click on 'Please select a workflow' button.");
        startWorkflowPage.clickStartWorkflowDropDown();
        Assert.assertTrue(startWorkflowPage.isWorkflowMenuVisible(), "Workflow menu is not displayed");

        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("New Task");
        Assert.assertTrue(startWorkflowPage.isWorkflowFormVisible(), "Workflow form is not visible");

        LOG.info("STEP 4: Fill in message text area with a valid data.");
        startWorkflowPage.addWorkflowDescription(workflowMessage);

        LOG.info("STEP 5: Click Calendar icon.");
        startWorkflowPage.clickDatePickerIcon();
        Assert.assertTrue(startWorkflowPage.isCalendarDisplayed(),
            "Calendar is not displayed after 'Date Picker Icon' was clicked.");

        LOG.info("STEP 6: Click any date;");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        Assert.assertEquals(date, startWorkflowPage.getWorkflowDueDateInputValue(),
            "The displayed date does not correspond with the selected one.");

        LOG.info("STEP 7: Click Assign to button.");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        Assert.assertTrue(selectPopUpPage.isSearchButtonDisplayed(), "\"Select...\" popup is not visible");

        LOG.info("STEP 8: Enter existent username and click Search button.");
        selectPopUpPage.search(testUser2);
        Assert.assertTrue(selectPopUpPage.isStringPresentInSearchList(testUser2), "User is not present in search results.");

        LOG.info("STEP 9: Click the icon button to add the user.");
        selectPopUpPage.clickAddIcon(testUser2);
        Assert.assertTrue(selectPopUpPage.isStringPresentInSelectedList(testUser2), "User was not selected.");

        LOG.info("STEP 10: Click OK button;");
        selectPopUpPage.clickOkButton();
        Assert.assertFalse(selectPopUpPage.isSearchButtonDisplayed(), "\"Select...\" popup did not close");

        LOG.info("STEP 11: Leave Notify me check box unchecked.");
        startWorkflowPage.toggleSendEmailCheckBox(false);
        Assert.assertTrue(startWorkflowPage.getSendEmailCheckBoxValue().equalsIgnoreCase("false"),
            "Send Email Notifications checkbox is checked");

        LOG.info("STEP 12: Click Start Workflow button;");
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(documentName),
            "Missing start workflow icon for" + documentName);

        LOG.info("STEP 13: Log in as assignee user.");
        setupAuthenticatedSession(testUser2, password);

        LOG.info("STEP 14: Add My tasks dahslet to My Dashboard.");
        userDashboard.navigate(testUser2);
        addMyTaskDashletToDashboard();
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks",
            "'My Tasks' dashlet is not displayed in user's dashboard.");

        LOG.info("STEP 15: Verify Task is present;");
        Assert.assertEquals(myTasksDashlet.selectTask(workflowMessage).getText(), workflowMessage,
            "Task is not present in 'My Tasks' dashlet");

        LOG.info("Clean up");
        contentService.deleteFolder(testUser, password, siteName, documentName);
        cleanupAuthenticatedSession();
    }

    /**
     * Add 'MyTask' dashlet to user dashboard if it is not already displayed.
     * And then check if it was successfully added.
     */
    private void addMyTaskDashletToDashboard()
    {
        if (!myTasksDashlet.isDashletDisplayed(Dashlet.DashletHelpIcon.MY_TASKS))
        {
            userService.addDashlet(testUser, password, UserDashlet.MY_TASKS, DashletLayout.THREE_COLUMNS, 3, 1);
        }
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
    }
}