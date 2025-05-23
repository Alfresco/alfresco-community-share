package org.alfresco.share.tasksAndWorkflows.StartingAWorkflow;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.dataprep.UserService;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
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
public class CreateNewTaskTests extends BaseTest
{
    //@Autowired
    SelectDocumentPopupPage selectDocumentPopupPage;

    @Autowired
    WorkflowService workflowService;
    @Autowired
    protected UserService userService;

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

    UserDashboardPage userDashboard;
    private String docName = String.format("docName%s",  RandomData.getRandomAlphanumeric());
    private String docName1 = String.format("docName1%s",  RandomData.getRandomAlphanumeric());
    private String folderName = String.format("folderName%s",  RandomData.getRandomAlphanumeric());
    private String docContent = String.format("docContent%s",  RandomData.getRandomAlphanumeric());
    private String workflowMessage = "PRR_workflow_descripion";
    private String newTaskName = "New Task";
    private String file1 = "myFile.txt";
    private String file2 = "C42550Doc.docx";
    private ArrayList<String> noItems = new ArrayList<>();
    private String date = DataUtil.formatDate(new Date(), DataUtil.DATE_FORMAT);
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<UserModel> testUser2 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8345username = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C286291usernameA = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C286291usernameB = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> C286291siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void testSetup()
    {
        log.info("PreCondition: Creating a test user");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a test user");
        testUser2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a test user");
        C8345username.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a test user");
        C286291usernameA.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating test user");
        C286291usernameB.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        log.info("PreCondition: Site C286291siteName is created");
        C286291siteName.set(getDataSite().usingUser(C286291usernameA.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(C286291usernameA.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        startWorkflowPage = new StartWorkflowPage(webDriver);
        selectPopUpPage = new SelectPopUpPage(webDriver);
        selectDocumentPopupPage = new SelectDocumentPopupPage(webDriver);
        userDashboard = new UserDashboardPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        headerMenuBar = new HeaderMenuBar(webDriver);
        selectDocumentPopupPage = new SelectDocumentPopupPage(webDriver);
        myTasksDashlet = new MyTasksDashlet(webDriver);
        editTaskPage = new EditTaskPage(webDriver);

        contentService.createFolder(C286291usernameA.get().getUsername(),C286291usernameA.get().getPassword(), folderName, C286291siteName.get().getId());
        contentService.createFolderInRepository(getAdminUser().getUsername(), getAdminUser().getPassword(), folderName, "/");
        contentService.uploadFileInRepository(getAdminUser().getUsername(), getAdminUser().getPassword(), folderName, testDataFolder + file1);
        contentService.uploadFileInRepository(getAdminUser().getUsername(), getAdminUser().getPassword(), folderName, testDataFolder + file2);
        contentService.createDocument(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocument(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.HTML, docName1, docContent);
        contentService.createDocument(C286291usernameA.get().getUsername(), C286291usernameA.get().getPassword(), C286291siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, docName,
            docContent);
        contentService.createDocumentInFolder(C286291usernameA.get().getUsername(), C286291usernameA.get().getPassword(), C286291siteName.get().getId(), folderName, CMISUtil.DocumentType.HTML,
            docName1, docContent);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/" + folderName);
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser2.get().getUsername());

        deleteUsersIfNotNull(testUser.get());
        deleteUsersIfNotNull(testUser2.get());
        deleteUsersIfNotNull(C8345username.get());
        deleteUsersIfNotNull(C286291usernameA.get());
        deleteUsersIfNotNull(C286291usernameB.get());
        deleteSitesIfNotNull(siteName.get());
        deleteSitesIfNotNull(C286291siteName.get());
    }

//    @Bug (id = "MNT-17015 won't fix")
    @TestRail (id = "C8344")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskAndAssignToYourself()
    {
        String firstName = testUser.get().getFirstName();
        String lastName = testUser.get().getLastName();

        log.info("Precondition");
        authenticateUsingLoginPage(testUser.get());
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow(newTaskName);

        log.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser.get().getUsername());
        selectPopUpPage.clickAddIcon(firstName +" "+ lastName +" "+ "(" + testUser.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);
    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8345")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskAndAssignToAnotherUser()
    {
        String C8345firstName = C8345username.get().getFirstName();
        String C8345lastName = C8345username.get().getLastName();

        log.info("Precondition");
        authenticateUsingCookies(testUser.get());
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow(newTaskName);

        log.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription(workflowMessage);
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(C8345username.get().getUsername());
        selectPopUpPage.clickAddIcon(C8345firstName +" "+ C8345lastName +" "+ "(" + C8345username.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for" + docName);

        log.info("STEP 5: logout then login as '" + C8345username + "'.");
        authenticateUsingCookies(C8345username.get());
        userDashboard.navigate(C8345username.get());
        myTasksDashlet.assertTaskNameEqualsTo(workflowMessage);
    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8376")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void cancelStartingWorkflow()
    {
        String firstName = testUser.get().getFirstName();
        String lastName = testUser.get().getLastName();

        log.info("Precondition");
        authenticateUsingLoginPage(testUser.get());
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow(newTaskName);

        log.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on cancel button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser.get().getUsername());
        selectPopUpPage.clickAddIcon(firstName +" "+ lastName +" "+ "(" + testUser.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.cancelStartWorkflow();
        documentLibraryPage.assertBrowserPageTitleIs("Alfresco » Document Library");

    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8388")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void startWorkflowForMultipleFiles() {
        String firstName = testUser.get().getFirstName();
        String lastName = testUser.get().getLastName();

        log.info("Precondition");
        authenticateUsingLoginPage(testUser.get());
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Select multiple files from 'Document Library ' then click 'Selected Items...' dropdown button, then click 'Start Workflow...'");
        documentLibraryPage.clickCheckBox(docName);
        documentLibraryPage.clickCheckBox(docName1);
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Start Workflow...");

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow(newTaskName);

        log.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on start workflow button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser.get().getUsername());
        selectPopUpPage.clickAddIcon(firstName +" "+ lastName +" "+ "(" + testUser.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();

        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName), "Missing start workflow icon for '" + docName + "'.");
        assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(docName1), "Missing start workflow icon for '" + docName1 + "'.");

    }

    @TestRail (id = "C286291")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskAddItem() {
        String C286291firstNameB = C286291usernameB.get().getFirstName();
        String C286291lastNameB = C286291usernameB.get().getLastName();

        log.info("Precondition 1: login as first user '" + C286291usernameA + "'.");
        authenticateUsingCookies(C286291usernameA.get());
        documentLibraryPage.navigate(C286291siteName.get());

        log.info("Precondition 2:  Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("Precondition 3: New Task workflow is selected;");
        startWorkflowPage.selectAWorkflow(newTaskName);

        //Check if the item selected at the start of the workflow is displayed as attached into the Items List block
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName),
            "Document '" + docName + "' is not displayed as attached to workflow.");

        log.info("Precondition 4: Message fields id filled with a correct data, assignee is selected;");
        startWorkflowPage.addWorkflowDescription("PRR Task Message");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(C286291usernameB.get().getUsername());
        selectPopUpPage.clickAddIcon(C286291firstNameB +" "+ C286291lastNameB +" "+ "(" + C286291usernameB.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();

        log.info("STEP 1: Click Add button;");
        startWorkflowPage.clickAddItemsButton();
        Assert.assertTrue(selectDocumentPopupPage.isSelectDocumentPopupPageHeaderDisplayed(), "'Add Items Popup' did not open.");

        //Check if the document used to start the workflow is displayed in PopUp but his 'Add' button is not
        Assert.assertTrue(selectDocumentPopupPage.isSearchedItemDisplayed(docName),
            "The document used to start the workflow is not displayed in the PopUp.");
        Assert.assertFalse(selectPopUpPage.isAddIconDisplayed(docName),
            "'Add' button is displayed for '" + docName + "' even if it shouldn't.");

        log.info("STEP 2: Go to folder where any content is located;");
        selectDocumentPopupPage.click_Item(folderName);

        log.info("STEP 3: Verify impossibilities to open item (item does not look like link);");
        Assert.assertFalse(selectDocumentPopupPage.isItemClickable(docName1),
            "Document '" + docName1 + "' is displayed as a link.");

        log.info("STEP 4: Click Add button;");
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

        log.info("STEP 5: Click 'OK'.");
        selectPopUpPage.clickOkButton();
        Assert.assertFalse(selectDocumentPopupPage.isSelectDocumentPopupPageHeaderDisplayed(),
            "'Add Item' PopUp couldn't be closed.");

        //Check if 'Items List' block contains now the first and the previous attached documents.
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName),
            "Document '" + docName + "' is not displayed as attached to workflow.");
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName1),
            "Document '" + docName1 + "' is not displayed as attached to workflow.");

        log.info("STEP 6: Fill 'Message' field and click 'Start workflow' button.");
        startWorkflowPage.addWorkflowDescription(workflowMessage);
        startWorkflowPage.clickStartWorkflow();
        documentLibraryPage.assertBrowserPageTitleIs("Alfresco » Document Library");

        log.info("STEP 7: login as Assignee user.");
        authenticateUsingCookies(C286291usernameB.get());
        userDashboard.navigate(C286291usernameB.get());

        log.info("STEP 8: Check if 'My Task' dashlet is displayed in user dashboard.");
        addMyTaskDashletToDashboard();

        log.info("STEP 9: Click the task's name.");
        myTasksDashlet.assertTaskNameEqualsTo(workflowMessage);

        myTasksDashlet.clickTaskName(workflowMessage);
        Assert.assertTrue(editTaskPage.getPage_Title().contains("Edit Task"), "Edit task page should be displayed!");

        log.info("STEP 10: Verify items list.");
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName),
            "Document '" + docName + "' is not displayed as attached to workflow.");
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName1),
            "Document '" + docName1 + "' is not displayed as attached to workflow.");
    }


    @TestRail (id = "C286360")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskViaSelectedItemsSingleDocument()
    {
        String firstName2 = testUser2.get().getFirstName();
        String lastName2 = testUser2.get().getLastName();

        log.info("Precondition 1: Any user logged in Share;");
        authenticateUsingCookies(testUser.get());

        log.info("Precondition 2: Add My Tasks dashlet to My Dashboard;");
        addMyTaskDashletToDashboard();

        log.info("Precondition 3: Any site is created;");
        log.info("Precondition 4: Any content is added to the Document Library;");

        log.info("Precondition 5: Document library page is opened;");
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Select checkbox at the top left part of document.");
        documentLibraryPage.clickCheckBox(docName);
        Assert.assertTrue(documentLibraryPage.isContentSelected(docName), "Document '" + docName + "' wasn't selected.");

        log.info("STEP 2: Expand Selected Items menu.");
        log.info("STEP 3: Click Start Workflow action.");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 4: Select Adhoc task (New Task).");
        startWorkflowPage.selectAWorkflow(newTaskName);
        Assert.assertTrue(startWorkflowPage.isWorkflowFormDisplayed(), "Workflow form is not displayed.");

        log.info("STEP 5: Fill in message text area with a valid data.");
        startWorkflowPage.addWorkflowDescription("C286360_" + workflowMessage);

        log.info("STEP 6: Click Calendar icon.");
        startWorkflowPage.clickDatePickerIcon();
        Assert.assertTrue(startWorkflowPage.isCalendarDisplayed(),
            "Calendar is not displayed after 'Date Picker Icon' was clicked.");

        log.info("STEP 7: Click any date.");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        Assert.assertEquals(date, startWorkflowPage.getWorkflowDueDateInputValue(),
            "The displayed date does not correspond with the selected one.");

        log.info("STEP 8: Click Assign to button.");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();

        log.info("STEP 9: Enter existent username and click Search button.");
        selectPopUpPage.search(testUser2.get().getUsername());

        log.info("STEP 10: Click Select>> button.");
        selectPopUpPage.clickAddIcon(firstName2 +" "+ lastName2 +" "+ "(" + testUser2.get().getUsername() + ")");

        log.info("STEP 11: Click OK button.");
        selectPopUpPage.clickOkButton();

        log.info("STEP 12: Leave Notify me check box unchecked.");
        startWorkflowPage.toggleSendEmailCheckBox(false);
        Assert.assertEquals(startWorkflowPage.getSendEmailCheckBoxValue(), "false", "The checkbox couldn't be unchecked.");

        log.info("STEP 13: Click OK button.");
        startWorkflowPage.clickStartWorkflow();
        documentLibraryPage.assertBrowserPageTitleIs("Alfresco » Document Library");

        log.info("STEP 14: log in as assignee user.");
        authenticateUsingCookies(testUser2.get());

        log.info("STEP 15: Add My tasks dahslet to My Dashboard;");
        addMyTaskDashletToDashboard();

        log.info("STEP 16: Verify Task is present.");
        myTasksDashlet.assertTaskNameEqualsTo("C286360_" + workflowMessage);
    }


    @TestRail (id = "C286446")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskViaDetailsPage() {
        String firstName2 = testUser2.get().getFirstName();
        String lastName2 = testUser2.get().getLastName();

        log.info("Precondition 1: Any user logged in Share;");
        authenticateUsingCookies(testUser.get());

        log.info("Precondition 2: Add My Tasks dashlet to My Dashboard;");
        addMyTaskDashletToDashboard();

        log.info("Precondition 3: Any site is created;");
        log.info("Precondition 4: Any content is added to the Document Library;");

        log.info("Precondition 5: Content details page is opened;");
        documentLibraryPage.navigate(siteName.get());
        documentLibraryPage.clickOnFile(docName);
        Assert.assertTrue(documentDetailsPage.isDocDetailsPageHeaderDisplayed(), "Document Details Page wasn't opened after clicking on files name.");

        log.info("STEP 1: Click Start Workflow action;");
        documentDetailsPage.clickDocumentActionsOption("Start Workflow");

        log.info("STEP 2: Select New Task;");
        startWorkflowPage.selectAWorkflow(newTaskName);
        Assert.assertTrue(startWorkflowPage.isWorkflowFormDisplayed(), "Workflow form is not displayed.");

        log.info("STEP 3: Fill in message text area with a valid data;");
        startWorkflowPage.addWorkflowDescription("C286446_" + workflowMessage);

        log.info("STEP 4: Click Calendar icon;");
        startWorkflowPage.clickDatePickerIcon();
        Assert.assertTrue(startWorkflowPage.isCalendarDisplayed(),
            "Calendar is not displayed after 'Date Picker Icon' was clicked.");

        log.info("STEP 5: Click any date;");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        Assert.assertEquals(date, startWorkflowPage.getWorkflowDueDateInputValue(),
            "The displayed date does not correspond with the selected one.");

        log.info("STEP 6: Click Assign to button;");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();

        log.info("STEP 7: Enter existent username and click Search button;");
        selectPopUpPage.search(testUser2.get().getUsername());

        log.info("STEP 8: Click Select>> button;");
        selectPopUpPage.clickAddIcon(firstName2 +" "+ lastName2 +" "+ "(" + testUser2.get().getUsername() + ")");

        log.info("STEP 9: Click OK button;");
        selectPopUpPage.clickOkButton();

        log.info("STEP 10: Leave Notify me check box unchecked;");
        startWorkflowPage.toggleSendEmailCheckBox(false);
        Assert.assertEquals(startWorkflowPage.getSendEmailCheckBoxValue(), "false", "The checkbox couldn't be unchecked.");

        log.info("STEP 11: Click Start Workflow button;");
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentDetailsPage.isDocDetailsPageHeaderDisplayed(),
            "After starting the workflow the browser wasn't redirected to 'Document Library' page.");

        log.info("STEP 12: log in as assignee user;");
        authenticateUsingCookies(testUser2.get());

        log.info("STEP 13: Add My tasks dahslet to My Dashboard;");
        addMyTaskDashletToDashboard();

        log.info("STEP 14: Verify Task is present;");
        myTasksDashlet.assertTaskNameEqualsTo("C286446_" + workflowMessage);
    }


    @TestRail (id = "C286469")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void createNewTaskRemoveAll() {
        String firstName2 = testUser2.get().getFirstName();
        String lastName2 = testUser2.get().getLastName();

        log.info("Precondition 1: Any folder is created in Repository with admin user;");
        log.info("Precondition 2: Any content is added to the folder;");

        log.info("Precondition 3: Any user logged in Share;");
        authenticateUsingCookies(testUser.get());

        log.info("Precondition 4: Add My Tasks dashlet to My Dashboard;");
        addMyTaskDashletToDashboard();

        log.info("Precondition 5: Any site is created;");
        log.info("Precondition 6: Any content is added to the Document Library;");

        log.info("Precondition 7: Start workflow page is opened for the document;");
        documentLibraryPage.navigate(siteName.get());
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("Precondition 8: New Task workflow is selected;");
        startWorkflowPage.selectAWorkflow(newTaskName);
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(docName),
            "Document '" + docName + "' is not displayed as attached to workflow.");

        log.info("Precondition 9: Message field is filled with a correct data, assignee is selected;");
        startWorkflowPage.addWorkflowDescription("C286469_" + workflowMessage);
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser2.get().getUsername());
        selectPopUpPage.clickAddIcon(firstName2 +" "+ lastName2 +" "+ "(" + testUser2.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();

        log.info("Precondition 10: Several items are added;");
        startWorkflowPage.clickAddItemsButton();
        selectDocumentPopupPage.selectBreadcrumbPath("Company Home");
        selectPopUpPage.click_Item(folderName);
        selectDocumentPopupPage.clickAddIcon(file1);
        selectDocumentPopupPage.clickAddIcon(file2);
        selectPopUpPage.clickOkButton();

        //Check if the selected items are listed in "Items block"
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(file1),
            "Document '" + file1 + "' is not displayed as attached to workflow.");
        Assert.assertTrue(startWorkflowPage.getItemsList().contains(file2),
            "Document '" + file2 + "' is not displayed as attached to workflow.");

        log.info("STEP 1: Click Remove all button;");
        startWorkflowPage.clickRemoveAllItemsButton();
        Assert.assertEquals(startWorkflowPage.getItemsList(), noItems.toString(), "Attached 'Items' list is not empty.");

        log.info("STEP 2: Click Start workflow button;");
        startWorkflowPage.clickStartWorkflow();

        log.info("STEP 3: login as Assignee user;");
        authenticateUsingCookies(testUser2.get());

        log.info("STEP 4: Add My Tasks dashlet to My Dashboard;");
        addMyTaskDashletToDashboard();

        log.info("STEP 5: Verify there are no items in Items part;");
        myTasksDashlet.clickTaskName("C286469_" + workflowMessage);
        Assert.assertEquals(editTaskPage.getItemsList(), noItems.toString(), "Attached 'Items' list is not empty.");
    }

    @TestRail (id = "C286368")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.TASKS })
    public void startWorkflowViaMore() {
        log.info("Precondition: My Tasks dashlet is added to My Dashboard;");
        authenticateUsingCookies(testUser.get());

        addMyTaskDashletToDashboard();

        log.info("Precondition: Any content is added to the Document Library;");
        String documentName = "C286368";
        String documentContent = "TestRail id: C286368";
        contentService
            .createDocument(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, documentName, documentContent);

        log.info("Precondition: Document library page is opened;");
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file and click More then Start Workflow.");
        documentLibraryPage.selectItemAction(documentName, ItemActions.START_WORKFLOW);
        Assert.assertTrue(startWorkflowPage.isStartWorkflowDropDownVisible(), "Start Workflow page is not opened");

        log.info("STEP 2: Click on 'Please select a workflow' button.");
        startWorkflowPage.clickStartWorkflowDropDown();
        Assert.assertTrue(startWorkflowPage.isWorkflowMenuVisible(), "Workflow menu is not displayed");

        log.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("New Task");
        Assert.assertTrue(startWorkflowPage.isWorkflowFormVisible(), "Workflow form is not visible");

        log.info("STEP 4: Fill in message text area with a valid data.");
        startWorkflowPage.addWorkflowDescription(workflowMessage);

        log.info("STEP 5: Click Calendar icon.");
        startWorkflowPage.clickDatePickerIcon();
        Assert.assertTrue(startWorkflowPage.isCalendarDisplayed(),
            "Calendar is not displayed after 'Date Picker Icon' was clicked.");

        log.info("STEP 6: Click any date;");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        Assert.assertEquals(date, startWorkflowPage.getWorkflowDueDateInputValue(),
            "The displayed date does not correspond with the selected one.");

        log.info("STEP 7: Click Assign to button.");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        Assert.assertTrue(selectPopUpPage.isSearchButtonDisplayed(), "\"Select...\" popup is not visible");

        log.info("STEP 8: Enter existent username and click Search button.");
        selectPopUpPage.search(testUser2.get().getUsername());
        Assert.assertTrue(selectPopUpPage.isStringPresentInSearchList(testUser2.get().getUsername()), "User is not present in search results.");

        log.info("STEP 9: Click the icon button to add the user.");
        selectPopUpPage.clickAddIcon(testUser2.get().getUsername());
        Assert.assertTrue(selectPopUpPage.isStringPresentInSelectedList(testUser2.get().getUsername()), "User was not selected.");

        log.info("STEP 10: Click OK button;");
        selectPopUpPage.clickOkButton();
        Assert.assertFalse(selectPopUpPage.isSearchButtonDisplayed(), "\"Select...\" popup did not close");

        log.info("STEP 11: Leave Notify me check box unchecked.");
        startWorkflowPage.toggleSendEmailCheckBox(false);
        Assert.assertTrue(startWorkflowPage.getSendEmailCheckBoxValue().equalsIgnoreCase("false"),
            "Send Email Notifications checkbox is checked");

        log.info("STEP 12: Click Start Workflow button;");
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(documentLibraryPage.isActiveWorkflowsIconDisplayed(documentName),
            "Missing start workflow icon for" + documentName);

        log.info("STEP 13: log in as assignee user.");
        authenticateUsingCookies(testUser2.get());

        log.info("STEP 14: Add My tasks dahslet to My Dashboard.");
        userDashboard.navigate(testUser2.get());
        addMyTaskDashletToDashboard();
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks",
            "'My Tasks' dashlet is not displayed in user's dashboard.");

        log.info("STEP 15: Verify Task is present;");
        Assert.assertEquals(myTasksDashlet.selectTask(workflowMessage).getText(), workflowMessage,
            "Task is not present in 'My Tasks' dashlet");

        log.info("Clean up");
        contentService.deleteFolder(testUser.get().getUsername(),testUser.get().getPassword(), siteName.get().getId(), documentName);
    }

    @TestRail (id = "C8499")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void deleteWorkflowCancel() {
        String firstName = testUser.get().getFirstName();
        String lastName = testUser.get().getLastName();

        log.info("Precondition");
        authenticateUsingLoginPage(testUser.get());
        documentLibraryPage.navigate(siteName.get());

        log.info("STEP 1: Hover over a file, click More then Start Workflow");
        documentLibraryPage.selectItemAction(docName, ItemActions.START_WORKFLOW);

        log.info("STEP 2: Click on 'Please select a workflow' button");
        log.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow(newTaskName);

        log.info("STEP 4: Add message, select a Due date, priority, assign it to you and click on cancel button");
        startWorkflowPage.addWorkflowDescription("WorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(testUser.get().getUsername());
        selectPopUpPage.clickAddIcon(firstName + " " + lastName + " " + "(" + testUser.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.cancelStartWorkflow();
        documentLibraryPage.assertBrowserPageTitleIs("Alfresco » Document Library");
    }

    /**
     * Add 'MyTask' dashlet to user dashboard if it is not already displayed.
     * And then check if it was successfully added.
     */
    private void addMyTaskDashletToDashboard()
    {
        if (!myTasksDashlet.isDashletDisplayed(DashletHelpIcon.MY_TASKS))
        {
            userService.addDashlet(testUser.get().getUsername(), testUser.get().getPassword(), UserDashlet.MY_TASKS, DashletLayout.THREE_COLUMNS, 3, 1);
        }
//        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
    }
}