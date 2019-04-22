package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp;
import org.alfresco.po.share.site.dataLists.DataListsPage;
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

import java.io.File;
import java.util.Arrays;

public class CreatingAListItemTests extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    CreateNewItemPopUp createNewItemPopUp;

    private final String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String folderName = String.format("Folder%s", RandomData.getRandomAlphanumeric());
    private final String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        setupAuthenticatedSession(userName, password);
    }
    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser,adminPassword,siteName );
    }

    @TestRail(id = "C6374")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void provideAllRequiredInfoAndCancel() {
        LOG.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact list description");
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(listName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        LOG.info("Step 3: Click the 'Cancel' button.");
        createNewItemPopUp.clickCancel();
        Assert.assertEquals(dataListsPage.currentContent.isAnyListItemDisplayed(), false, "At least one list item is displayed.");
    }
    
    @TestRail(id = "C6375")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void provideAllRequiredInfoAndClose() {
        LOG.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact list description");
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(listName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        LOG.info("Step 3: Click the 'Cancel' button.");
        createNewItemPopUp.clickCloseButton();
        Assert.assertEquals(dataListsPage.currentContent.isAnyListItemDisplayed(), false, "At least one list item is displayed.");
    }
    
    @TestRail(id = "C6379")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewContactListItem() {
        LOG.info("Preconditions: Create a new Contact List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact list description");
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(listName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes")), true, "The data list was not created.");
    }
    
    @TestRail(id = "C6380")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewEventAgendaItem() {
        LOG.info("Preconditions: Create a new Event Agenda");
        String agendaName = "agenda" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.EVENT_AGENDA, agendaName, "event agenda description");
        dataListsPage.navigate(siteName);
        dataListsPage.clickEventAgendaListItem(agendaName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewEventAgendaItem(Arrays.asList("test", "12", "13", "test", "test", "test", "testNotes"));
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test", "12", "13", "test", "test", "test", "testNotes")), true, "The data list was not created.");
    }
    
    @TestRail(id = "C6381")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewEventListItem() {
        LOG.info("Preconditions: Create a new Event List and a 'pptx' file");
        String eventName = "event" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.EVENT_LIST, eventName, "event list description");
        dataListsPage.navigate(siteName);
        dataListsPage.clickEventListItem(eventName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewEventItem(Arrays.asList("test", "description", "location", "1/1/2017", "12:00", "2/2/2017", "12:00",  "test", "testNotes"), folderName, "MultiPageDocument.pptx");
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test", "description", "location", "Sun 1 Jan 2017 12:00:00", "Thu 2 Feb 2017 12:00:00", "test", "MultiPageDocument.pptx", "testNotes")), true, "The data list was not created.");
    }
    
    @TestRail(id = "C6382")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewIssueListItem() {
        LOG.info("Preconditions: Create a new Issue List, a second user and a 'xlsx' file");
        String issueName = "issue" + System.currentTimeMillis();
        String userTest = String.format("userTest%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.ISSUE_LIST, issueName, "issue list description");
        dataListsPage.navigate(siteName);
        dataListsPage.clickIssueListItem(issueName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewIssueItem(Arrays.asList("0001", "Issue list title", "Test description", "1/1/2017", "comment for test item"), folderName, "MultiPageDocument.pptx", userTest, "Complete", "High");
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("0001", "Issue list title", userTest+" "+userTest, "Complete", "High", "Test description", "Sun 1 Jan 2017", "comment for test item", "MultiPageDocument.pptx")), true, "The data list was not created.");

        userService.delete(adminUser,adminPassword, userTest);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userTest);
    }
    
    @TestRail(id = "C6383")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewLocationListItem() {
        LOG.info("Preconditions: Create a new Location List and a 'test.xlsx' file");
        String taskName = "location" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.LOCATION_LIST, taskName, "location list description");
        dataListsPage.navigate(siteName);
        dataListsPage.clickLocationListItem(taskName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewLocationItem(Arrays.asList("test title", "Test Street, No. 1, Test, Test", "Test Street, No. 2, Test, Test", "Test Street, No. 3, Test, Test", "123456", "Test", "Test", "Test description"), folderName, "test.xlsx");
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test title", "Test Street, No. 1, Test, Test", "Test Street, No. 2, Test, Test", "Test Street, No. 3, Test, Test", "123456", "Test", "Test", "Test description", "test.xlsx")), true, "The data list was not created.");
    }
    
    @TestRail(id = "C6384")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewMeetingAgendaListItem() {
        LOG.info("Preconditions: Create a new Meeting Agenda List and a 'test.xlsx' file");
        String meetingName = "meeting" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.MEETING_AGENDA, meetingName, "location list description");
        dataListsPage.navigate(siteName);
        dataListsPage.clickMeetingAgendaListItem(meetingName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewMeetingAgendaItem(Arrays.asList("Meeting agenda reference", " test meeting agenda item", "Meeting agenda description test", "125", "test user"), folderName, "test.xlsx");
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("Meeting agenda reference", "test meeting agenda item", "Meeting agenda description test", "125", "test user", "test.xlsx")), true, "The data list was not created.");
    }
    
    @TestRail(id = "C6385")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewAdvancedTaskListItem() {
        LOG.info("Preconditions: Create a new Advanced Task List, a second user and a 'test.xlsx' file");
        String userTest = String.format("userTest%s", RandomData.getRandomAlphanumeric());
        String advancedTaskName = "advanced" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.TASKS_ADVANCED, advancedTaskName, "advanced task list description");
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        dataListsPage.navigate(siteName);
        dataListsPage.clickAdvancedTaskListItem(advancedTaskName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewAdvancedTaskItem(Arrays.asList("Task List Advanced test", "Task List Advanced test description", "1/1/2017", "2/2/2017", "10", " test comment"), folderName, "test.xlsx", userTest, "Complete", "High");
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("Task List Advanced test", "Task List Advanced test description", "Sun 1 Jan 2017", "Thu 2 Feb 2017", userTest+" "+userTest, "High", "Complete", "10", "test comment", "test.xlsx")), true, "The data list was not created.");
        userService.delete(adminUser,adminPassword, userTest);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userTest);
    }
    
    @TestRail(id = "C6386")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewSimpleTaskListItem() {
        LOG.info("Preconditions: Create a new Simple Task List");
        String userTest = String.format("userTest%s", RandomData.getRandomAlphanumeric());
        String simpleTaskName = "simple" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.TASKS_SIMPLE, simpleTaskName, "simple task list description");
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        dataListsPage.navigate(siteName);
        dataListsPage.clickSimpleTaskListItem(simpleTaskName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewSimpleTaskItem(Arrays.asList("Test Title", "test description", "1/1/2017", "Test comments"), "Complete", "High");
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("Test Title", "test description", "Sun 1 Jan 2017", "High", "Complete", "Test comments")), true, "The data list was not created.");
        userService.delete(adminUser,adminPassword, userTest);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userTest);
    }
    
    @TestRail(id = "C6386")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewToDoListItem() {
        LOG.info("Preconditions: Create a new To Do List, a second user and a 'test.xlsx' file");
        String userTest = String.format("userTest%s", RandomData.getRandomAlphanumeric());
        String toDoName = "toDo" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.TODO_LIST, toDoName, "To Do list description");
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        dataListsPage.navigate(siteName);
        dataListsPage.clickToDoListItem(toDoName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewToDoItem(Arrays.asList("Test title", "1/1/2017", "12:00", "1", "test notes"), folderName, "test.xlsx", userTest, "Complete");
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("Test title", "Sun 1 Jan 2017 12:00:00", "1", "Complete", userTest+" "+userTest, "test.xlsx")), true, "The data list was not created.");
        userService.delete(adminUser,adminPassword, userTest);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userTest);
    }
    
    @Bug(id = "ACE-4226")
    @TestRail(id = "C6386")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewVisitorFeedbackListItem() {
        LOG.info("Preconditions: Create a new Visitor Feedback List and a 'test.xlsx' file");
        String userTest = String.format("userTest%s", RandomData.getRandomAlphanumeric());
        String visitorName = "toDo" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.TODO_LIST, visitorName, "Visitor Feedback list description");
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        dataListsPage.navigate(siteName);
        dataListsPage.clickVisitorFeedbackListItem(visitorName);
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        LOG.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewVisitorItem(Arrays.asList("test@test.com", "Test feedback", "test", "test comment", "2", "Test User", "www.test.com"));
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test@test.com", "Test feedback", "test", "test comment", "2", "Test User", "www.test.com")), true, "The data list was not created.");
        userService.delete(adminUser,adminPassword, userTest);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userTest);
    }
}
