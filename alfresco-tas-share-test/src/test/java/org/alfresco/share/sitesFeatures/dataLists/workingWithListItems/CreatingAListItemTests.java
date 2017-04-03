package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

public class CreatingAListItemTests extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    CreateNewItemPopUp createNewItemPopUp;
    
    @Autowired
    DataUtil dataUtil;
    
    private String userName;
    private String siteName;

    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        userName = "User" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, password);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteName = String.format("siteName%s", DataUtil.getUniqueIdentifier());
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
    }
    
    @TestRail(id = "C6374")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void provideAllRequiredInfoAndCancel()
    {       
        LOG.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact list description");
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(listName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        
        LOG.info("Step 3: Click the 'Cancel' button.");
        createNewItemPopUp.clickCancel();
        Assert.assertEquals(dataListsPage.currentContent.isAnyListItemDisplayed(), false, "At least one list item is displayed.");

    }
    
    @TestRail(id = "C6375")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void provideAllRequiredInfoAndClose()
    {       
        LOG.info("Preconditions: Create a new List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact list description");
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(listName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        
        LOG.info("Step 3: Click the 'Cancel' button.");
        createNewItemPopUp.clickCloseButton();
        Assert.assertEquals(dataListsPage.currentContent.isAnyListItemDisplayed(), false, "At least one list item is displayed.");

    }
    
    @TestRail(id = "C6379")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewContactListItem()
    {       
        LOG.info("Preconditions: Create a new Contact List");
        String listName = "list" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, listName, "contact list description");
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(listName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewContactItem(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes"));
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test", "user", "test.user@test.com", "test Company", "test", "123456", "+41256422", "testNotes")), true, "The data list was not created.");

    }
    
    @TestRail(id = "C6380")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewEventAgendaItem()
    {       
        LOG.info("Preconditions: Create a new Event Agenda");
        String agendaName = "agenda" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.EVENT_AGENDA, agendaName, "event agenda description");
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickEventAgendaListItem(agendaName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewEventAgendaItem(Arrays.asList("test", "12", "13", "test", "test", "test", "testNotes"));
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test", "12", "13", "test", "test", "test", "testNotes")), true, "The data list was not created.");

    }
    
    @TestRail(id = "C6381")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewEventListItem()
    {       
        LOG.info("Preconditions: Create a new Event List and a 'pptx' file");
        String eventName = "event" + System.currentTimeMillis();
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.EVENT_LIST, eventName, "event list description");
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickEventListItem(eventName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewEventItem(Arrays.asList("test", "description", "location", "1/1/2017", "12:00", "2/2/2017", "12:00",  "test", "testNotes"), folderName, "MultiPageDocument.pptx");
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test", "description", "location", "Sun 1 Jan 2017 12:00:00", "Thu 2 Feb 2017 12:00:00", "test", "MultiPageDocument.pptx", "testNotes")), true, "The data list was not created.");

    }
    
    @TestRail(id = "C6382")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewIssueListItem()
    {       
        LOG.info("Preconditions: Create a new Issue List, a second user and a 'xlsx' file");
        String issueName = "issue" + System.currentTimeMillis();
        String userTest = "User" + DataUtil.getUniqueIdentifier();
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.ISSUE_LIST, issueName, "issue list description");
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickIssueListItem(issueName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewIssueItem(Arrays.asList("0001", "Issue list title", "Test description", "1/1/2017", "comment for test item"), folderName, "MultiPageDocument.pptx", userTest, "Complete", "High");
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("0001", "Issue list title", userTest+" "+userTest, "Complete", "High", "Test description", "Sun 1 Jan 2017", "comment for test item", "MultiPageDocument.pptx")), true, "The data list was not created.");

    }
    
    @TestRail(id = "C6383")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewLocationListItem()
    {       
        LOG.info("Preconditions: Create a new Location List and a 'test.xlsx' file");
        String taskName = "location" + System.currentTimeMillis();
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.LOCATION_LIST, taskName, "location list description");
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickLocationListItem(taskName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewLocationItem(Arrays.asList("test title", "Test Street, No. 1, Test, Test", "Test Street, No. 2, Test, Test", "Test Street, No. 3, Test, Test", "123456", "Test", "Test", "Test description"), folderName, "test.xlsx");
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test title", "Test Street, No. 1, Test, Test", "Test Street, No. 2, Test, Test", "Test Street, No. 3, Test, Test", "123456", "Test", "Test", "Test description", "test.xlsx")), true, "The data list was not created.");

    }
    
    @TestRail(id = "C6384")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewMeetingAgendaListItem()
    {       
        LOG.info("Preconditions: Create a new Meeting Agenda List and a 'test.xlsx' file");
        String meetingName = "meeting" + System.currentTimeMillis();
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.MEETING_AGENDA, meetingName, "location list description");
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickMeetingAgendaListItem(meetingName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewMeetingAgendaItem(Arrays.asList("Meeting agenda reference", " test meeting agenda item", "Meeting agenda description test", "125", "test user"), folderName, "test.xlsx");
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("Meeting agenda reference", "test meeting agenda item", "Meeting agenda description test", "125", "test user", "test.xlsx")), true, "The data list was not created.");

    }
    
    @TestRail(id = "C6385")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewAdvancedTaskListItem()
    {       
        LOG.info("Preconditions: Create a new Advanced Task List, a second user and a 'test.xlsx' file");
        String userTest = "User" + DataUtil.getUniqueIdentifier();
        String advancedTaskName = "advanced" + System.currentTimeMillis();
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.TASKS_ADVANCED, advancedTaskName, "advanced task list description");
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickAdvancedTaskListItem(advancedTaskName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewAdvancedTaskItem(Arrays.asList("Task List Advanced test", "Task List Advanced test description", "1/1/2017", "2/2/2017", "10", " test comment"), folderName, "test.xlsx", userTest, "Complete", "High");
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("Task List Advanced test", "Task List Advanced test description", "Sun 1 Jan 2017", "Thu 2 Feb 2017", userTest+" "+userTest, "High", "Complete", "10", "test comment", "test.xlsx")), true, "The data list was not created.");

    }
    
    @TestRail(id = "C6386")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewSimpleTaskListItem()
    {       
        LOG.info("Preconditions: Create a new Simple Task List");
        String userTest = "User" + DataUtil.getUniqueIdentifier();
        String simpleTaskName = "simple" + System.currentTimeMillis();
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.TASKS_SIMPLE, simpleTaskName, "simple task list description");
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickSimpleTaskListItem(simpleTaskName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewSimpleTaskItem(Arrays.asList("Test Title", "test description", "1/1/2017", "Test comments"), "Complete", "High");
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("Test Title", "test description", "Sun 1 Jan 2017", "High", "Complete", "Test comments")), true, "The data list was not created.");

    }
    
    @TestRail(id = "C6386")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewToDoListItem()
    {       
        LOG.info("Preconditions: Create a new To Do List, a second user and a 'test.xlsx' file");
        String userTest = "User" + DataUtil.getUniqueIdentifier();
        String toDoName = "toDo" + System.currentTimeMillis();
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.TODO_LIST, toDoName, "To Do list description");
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickToDoListItem(toDoName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewToDoItem(Arrays.asList("Test title", "1/1/2017", "12:00", "1", "test notes"), folderName, "test.xlsx", userTest, "Complete");
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("Test title", "Sun 1 Jan 2017 12:00:00", "1", "Complete", userTest+" "+userTest, "test.xlsx")), true, "The data list was not created.");

    }
    
    @Bug(id = "ACE-4226")
    @TestRail(id = "C6386")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewVisitorFeedbackListItem()
    {      
        LOG.info("This test fails due to: 'https://issues.alfresco.com/jira/browse/ACE-4226?jql=text%20~%20%22visitor%20feedback%22' issue");
        
        LOG.info("Preconditions: Create a new Visitor Feedback List and a 'test.xlsx' file");
        String userTest = "User" + DataUtil.getUniqueIdentifier();
        String visitorName = "toDo" + System.currentTimeMillis();
        String folderName = "testFolder";
        String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC6381" + File.separator;
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.TODO_LIST, visitorName, "Visitor Feedback list description");
        userService.create(adminUser, adminPassword, userTest, password, userTest + domain, userTest, userTest);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFilesInFolder(testDataFolder, userName, password, siteName, folderName);
        
        dataListsPage.navigate(siteName);
        dataListsPage.clickVisitorFeedbackListItem(visitorName);
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 1: Click the 'New Item' button");
        dataListsPage.clickNewItemButton();
        getBrowser().waitInSeconds(3);
        
        LOG.info("Step 2: Provide all required informations.");
        dataUtil.fillCreateNewVisitorItem(Arrays.asList("test@test.com", "Test feedback", "test", "test comment", "2", "Test User", "www.test.com"));
        
        LOG.info("Step 3: Click the 'Save' button.");
        createNewItemPopUp.clickSave();
        Assert.assertEquals(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("test@test.com", "Test feedback", "test", "test comment", "2", "Test User", "www.test.com")), true, "The data list was not created.");

    }
}
