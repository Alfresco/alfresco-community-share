package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;


import java.io.File;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.*;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;

import static org.alfresco.common.Utils.srcRoot;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Slf4j
public class CreatingAListItemTests extends BaseTest
{
    @Autowired
    DataListsService dataListsService;

    @Autowired
    UserService userService;

    @Autowired
    SiteService siteService;

    private final String folderName = String.format("Folder%s", RandomData.getRandomAlphanumeric());
    private DataListsPage dataListsPage;
    private CreateNewItemPopUp createNewItemPopUp;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userTest = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
        contentService.uploadFilesInFolder(testDataFolder, user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), folderName);
        siteService.addPageToSite(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.DATALISTS, null);

        dataListsPage = new DataListsPage(webDriver);
        createNewItemPopUp = new CreateNewItemPopUp(webDriver);

        authenticateUsingLoginPage(user.get());
        dataListsPage.navigate(siteName.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user.get());

    }

    @TestRail (id = "C6374")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void provideAllRequiredInfoAndCancel()
    {
        log.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickContactListItem(listName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        log.info("Step 3: Click the 'Cancel' button.");
        createNewItemPopUp.clickCancel();
        List<String> expectedList = Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes");
        for (String anExpectedList : expectedList)
        {
            assertFalse(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is not updated.");
        }
    }

    @TestRail (id = "C6375")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void provideAllRequiredInfoAndClose()
    {
        log.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickContactListItem(listName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        log.info("Step 3: Click the 'Cancel' button.");
        createNewItemPopUp.clickCloseButton();
        List<String> expectedList = Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes");
        for (String anExpectedList : expectedList)
        {
            assertFalse(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is not updated.");
        }
    }

    @TestRail (id = "C6379")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewContactListItem()
    {
        log.info("Preconditions: Create a new Contact List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.CONTACT_LIST, listName, "contact list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickContactListItem(listName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C6380")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewEventAgendaItem()
    {
        log.info("Preconditions: Create a new Event Agenda");
        String agendaName = "agenda" + System.currentTimeMillis();
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.EVENT_AGENDA, agendaName, "event agenda description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickEventAgendaListItem(agendaName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewEventAgendaItem(Arrays.asList("test", "12", "13", "test", "test", "test", "testNotes"));
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("test", "12", "13", "test", "test", "test", "testNotes");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C6381")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewEventListItem()
    {
        log.info("Preconditions: Create a new Event List and a 'pptx' file");
        String eventName = "event" + System.currentTimeMillis();
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.EVENT_LIST, eventName, "event list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickEventListItem(eventName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewEventItem(Arrays.asList("test", "description", "location", "1/1/2017", "12:00", "2/2/2017", "12:00", "test", "testNotes"), folderName, "MultiPageDocument.pptx");
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("test", "description", "location", "Sun 1 Jan 2017 12:00:00", "Thu 2 Feb 2017 12:00:00", "test", "MultiPageDocument.pptx", "testNotes");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C6382")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewIssueListItem()
    {
        log.info("Preconditions: Create a new Issue List, a second user and a 'xlsx' file");
        String issueName = "issue" + System.currentTimeMillis();
        log.info("Precondition: Any Test user is created");
        userTest.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        log.info("PreCondition: Site siteName is created");
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.ISSUE_LIST, issueName, "issue list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickIssueListItem(issueName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewIssueItem(Arrays.asList("0001", "Issue list title", "Test description", "1/1/2017", "comment for test item"), folderName, "MultiPageDocument.pptx", userTest.get().getUsername(), "Complete", "High");
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("0001", "Issue list title", "Complete", "High", "Test description", "Sun 1 Jan 2017", "comment for test item", "MultiPageDocument.pptx");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C6383")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewLocationListItem()
    {
        log.info("Preconditions: Create a new Location List and a 'test.xlsx' file");
        String taskName = "location" + System.currentTimeMillis();
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.LOCATION_LIST, taskName, "location list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickLocationListItem(taskName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewLocationItem(Arrays.asList("test title", "Test Street, No. 1, Test, Test", "Test Street, No. 2, Test, Test", "Test Street, No. 3, Test, Test", "123456", "Test", "Test", "Test description"), folderName, "test.xlsx");
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("test title", "Test Street, No. 1, Test, Test", "Test Street, No. 2, Test, Test", "Test Street, No. 3, Test, Test", "123456", "Test", "Test", "Test description", "test.xlsx");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C6384")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewMeetingAgendaListItem()
    {
        log.info("Preconditions: Create a new Meeting Agenda List and a 'test.xlsx' file");
        String meetingName = "meeting" + System.currentTimeMillis();
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.MEETING_AGENDA, meetingName, "location list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickMeetingAgendaListItem(meetingName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewMeetingAgendaItem(Arrays.asList("Meeting agenda reference", " test meeting agenda item", "Meeting agenda description test", "125", "test user"), folderName, "test.xlsx");
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("Meeting agenda reference", "test meeting agenda item", "Meeting agenda description test", "125", "test user", "test.xlsx");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C6385")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewAdvancedTaskListItem()
    {
        log.info("Preconditions: Create a new Advanced Task List, a second user and a 'test.xlsx' file");
        String advancedTaskName = "advanced" + System.currentTimeMillis();
        log.info("Precondition: Any Test user is created");
        userTest.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.TASKS_ADVANCED, advancedTaskName, "advanced task list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickAdvancedTaskListItem(advancedTaskName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewAdvancedTaskItem(Arrays.asList("Task List Advanced test", "Task List Advanced test description", "1/1/2017", "2/2/2017", "10", " test comment"), folderName, "test.xlsx", userTest.get().getUsername(), "Complete", "High");
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("Task List Advanced test", "Task List Advanced test description", "Sun 1 Jan 2017", "Thu 2 Feb 2017", "High", "Complete", "10", "test comment", "test.xlsx");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C6386")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewSimpleTaskListItem()
    {
        log.info("Preconditions: Create a new Simple Task List");
        String simpleTaskName = "simple" + System.currentTimeMillis();
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.TASKS_SIMPLE, simpleTaskName, "simple task list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickSimpleTaskListItem(simpleTaskName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewSimpleTaskItem(Arrays.asList("Test Title", "test description", "1/1/2017", "Test comments"), "Complete", "High");
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("Test Title", "test description", "Sun 1 Jan 2017", "High", "Complete", "Test comments");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C6386")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewToDoListItem()
    {
        log.info("Preconditions: Create a new To Do List, a second user and a 'test.xlsx' file");
        String toDoName = "toDo" + System.currentTimeMillis();
        log.info("Precondition: Any Test user is created");
        userTest.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.TODO_LIST, toDoName, "To Do list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickToDoListItem(toDoName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewToDoItem(Arrays.asList("Test title", "1/1/2017", "12:00", "1", "test notes"), folderName, "test.xlsx", userTest.get().getUsername(), "Complete");
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("Test title", "Sun 1 Jan 2017 12:00:00", "1", "Complete", "test.xlsx");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @Bug (id = "ACE-4226")
    @TestRail (id = "C6386")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewVisitorFeedbackListItem() {
        log.info("Preconditions: Create a new Visitor Feedback List and a 'test.xlsx' file");
        String visitorName = "toDo" + System.currentTimeMillis();
        log.info("Precondition: Any Test user is created");
        userTest.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataList.TODO_LIST, visitorName, "Visitor Feedback list description");
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickVisitorFeedbackListItem(visitorName);
        log.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButtons();
        log.info("Step 2: Provide all required informations.");
        createNewItemPopUp.fillCreateNewVisitorItem(Arrays.asList("Test title", "1/1/2017", "12:00", "1", "test notes"));
        log.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        List<String> expectedList = Arrays.asList("Test title", "Sun 1 Jan 2017 12:00:00", "1", "Not Started");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }
}
