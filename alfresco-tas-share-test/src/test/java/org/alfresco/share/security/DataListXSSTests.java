package org.alfresco.share.security;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.common.DataProviderClass;

import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;

import org.alfresco.po.enums.DataListTypes;
import org.alfresco.po.share.site.CustomizeSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContentPage;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.NewItemPopupForm;
import org.alfresco.po.share.site.dataLists.EventAgendaSelectedContentItem;
import org.alfresco.po.share.site.dataLists.MeetingAgendaListSelectedContentPage;
import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;

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

import static org.alfresco.po.share.dashlet.Dashlets.SITE_DATA_LISTS;

@Slf4j

public class DataListXSSTests extends BaseTest
{
    @Autowired
    protected DataListsService dataListsService;
    CreateNewItemPopUp createNewItemPopUp;
    ContactListSelectedContentPage contactListSelectedContentPage;
    EventAgendaSelectedContentItem eventAgendaSelectedContentItem;
    MeetingAgendaListSelectedContentPage meetingAgendaListSelectedContentPage;
    CustomizeSiteDashboardPage customizeSiteDashboardPage;
    SiteDashboardPage siteDashboardPage;
    CreateDataListDialog createDataListDialog;
    DataListsPage dataListsPage;
    private UserModel testUser;
    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String contactListName = "ContactList" + uniqueIdentifier;
    private String eventAgendaList = "EventAgendaList" + uniqueIdentifier;
    private String meetingAgendaList = "MeetingAgendaList" + uniqueIdentifier;
    private String password = "password";
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    private void beforeMethod()
    {
        log.info("PreCondition: Creating a TestUser1");
        String identifier = RandomData.getRandomAlphanumeric();
        final String user = "test" + identifier;
        testUser = dataUser.usingAdmin().createUser(user, password);
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        siteName.set(getDataSite().usingUser(testUser).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser);

        siteDashboardPage = new SiteDashboardPage(webDriver);
        dataListsPage = new DataListsPage(webDriver);
        createDataListDialog = new CreateDataListDialog(webDriver);
        customizeSiteDashboardPage = new CustomizeSiteDashboardPage(webDriver);
        contactListSelectedContentPage = new ContactListSelectedContentPage(webDriver);
        createNewItemPopUp = new CreateNewItemPopUp(webDriver);
        eventAgendaSelectedContentItem = new EventAgendaSelectedContentItem(webDriver);
        meetingAgendaListSelectedContentPage = new MeetingAgendaListSelectedContentPage(webDriver);

        log.info("Precondition : Any user logged in Share.");
        authenticateUsingLoginPage(testUser);

        log.info("Precondition: Create a new 'Contact List'.");
        dataListsService.createDataList(testUser.getUsername(), password, siteName.get().getId(), DataList.CONTACT_LIST, contactListName, "Description.");

        log.info("Precondition: Create a new 'Event Agenda'.");
        dataListsService.createDataList(testUser.getUsername(), password, siteName.get().getId(), DataList.EVENT_AGENDA, eventAgendaList, "Description.");

        log.info("Precondition: Create a new 'Meeting Agenda'.");
        dataListsService.createDataList(testUser.getUsername(), password, siteName.get().getId(), DataList.MEETING_AGENDA, meetingAgendaList, "Description.");
    }

    @AfterMethod(alwaysRun = true)
    private void afterMethod()
    {
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(testUser);
    }


    @TestRail (id = "C286626")
    @Test(groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests"}, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class, enabled = false)
    public void creatingContactListWithXSS(String XSSString)
    {
        log.info("Precondition 1: Navigate to site dashboard.");
        siteDashboardPage.navigate(siteName.get().getId());

        log.info("Precondition 2: Add 'Site Data List' dashlet to site dashboard.");
        customizeSiteDashboardPage
            .navigate(siteName.get())
            .clickAddDashlet()
            .addDashlet(SITE_DATA_LISTS, 1)
            .clickOk();
        siteDashboardPage
            .assertsiteDataListDashletIsAdded();

        log.info("STEP 1: Click 'New List' button from 'Site Data List' dashlet.");
        dataListsPage.clickOnCreateDataListLink();
        Assert.assertTrue(createDataListDialog.isNewListPopupDisplayed(), "'New List' PopUp is not displayed.");

        log.info("Step 1.1 => Check if all the expected list types are displayed");
        Assert.assertTrue(createDataListDialog.isDataListComplete(), "One or more list types from 'Data List' is missing.");

        log.info("Step 1.2 => Check if Contact list contains the right description");
        Assert.assertEquals(createDataListDialog.getTypeOfListDescription(DataListTypes.CONTACT_LIST), DataListTypes.CONTACT_LIST.description, "'Contact List' description is not correct.");

        log.info("Step 1.3 => Check if there are following controls: Title (mandatory) field, Description text area, Submit button (disable till Title is not filled, Cancel button available.");
        Assert.assertTrue(createDataListDialog.getTitleLabelText().contains("Title:"), "'Title input' label is not 'Title:'.");
        Assert.assertTrue(createDataListDialog.isTitleFieldDisplayed(), "'Title' input field is not displayed.");
        Assert.assertTrue(createDataListDialog.isTitleMandatoryIndicatorDisplayed(), "'Title' mandatory indicator is not displayed.");

        Assert.assertEquals(createDataListDialog.getDescriptionLabelText(), "Description:", "'Description' label is not 'Description'.");
        Assert.assertTrue(createDataListDialog.isDescriptionFieldDisplayed(), "'Description' field is not displayed.");

        Assert.assertTrue(createDataListDialog.isSaveButtonDisplayed(), "'Save' button is not displayed.");
        Assert.assertTrue(createDataListDialog.isCancelButtonDisplayed(), "'Cancel' button is not displayed.");

        log.info("STEP 2: Select the 'Contact list' type;");
        createDataListDialog.selectType(DataListTypes.CONTACT_LIST.title);
        Assert.assertTrue(createDataListDialog.isExpectedTypeSelected(DataListTypes.CONTACT_LIST.title), "'" + DataListTypes.CONTACT_LIST.title + "' is not highlighted.");

        log.info("STEP 3: Enter XSS string '" + XSSString + "' into 'Title' (mandatory) field.");
        createDataListDialog.typeTitle(XSSString);
        Assert.assertEquals(createDataListDialog.getTitleValue(), XSSString, "XSS string '" + XSSString + "' was not entered in 'Title' field.");
        Assert.assertTrue(createDataListDialog.isNewListPopupDisplayed(), "'New List' PopUp is not displayed.");

        log.info("STEP 4: Enter XSS string '" + XSSString + "' into 'Description' field.");
        createDataListDialog.typeDescription(XSSString);
        Assert.assertEquals(createDataListDialog.getDescriptionValue(), XSSString, "XSS string '" + XSSString + "' was not entered in 'Description' field.");
        Assert.assertTrue(createDataListDialog.isNewListPopupDisplayed(), "'New List' PopUp is not displayed.");

        log.info("STEP 5: Click 'Submit' button.");
        createDataListDialog.clickSaveButton();
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(XSSString), "The created data list is not displayed in the list as created.");

        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), meetingAgendaList);
        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), eventAgendaList);
        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), contactListName);
    }

    @TestRail (id = "C286627")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingContactListItemWithXSS(String XSSString)
    {
        List<String> listOfXSSStrings = Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString);

        log.info("Precondition: Go to 'Data Lists Page'.");
        dataListsPage.navigate(siteName.get().getId());
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(contactListName), "The created data list is not displayed in the list as created.");

        log.info("STEP 1: Click on the created Contact list's name in Lists section;");
        dataListsPage.clickContactListItem(contactListName);

        log.info("STEP 1.1 => Check if: List is highlighted;");
//        Assert.assertTrue(dataListsPage.isExpectedListSelected(contactListName), "'" + contactListName + "' is not highlighted.");

        log.info("STEP 1.2 => Check if: Correct information and description are displayed at the right;");
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(contactListName), "The actual and expected lists name are not the same.");
        Assert.assertTrue(dataListsPage.isNewItemButtonDisplayed(), "'New Item' button is not displayed.");

        log.info("STEP 1.3 => Check if: New list button at the top is enabled;");
//        Assert.assertTrue(dataListsPage.assertNewListButtonIsDisplayed(), "'New List' button is not displayed.");

        log.info("STEP 1.4 => Check if: Table with the contact lists is displayed;");
        Assert.assertTrue(contactListSelectedContentPage.isTableHeaderComplete(), "A column is missing from the 'Item List' table.");

        log.info("STEP 2: Click New Item button and fill all the fields :");
        dataListsPage.clickNewItemButton();
        Assert.assertTrue(dataListsPage.isNewItemPopupFormDisplayed(CreateNewItemPopUp.NewItemPopupForm.ContactList), "'Create New Item' dialog for 'Contact List' is not displayed.");
        createNewItemPopUp.fillCreateNewContactItem(listOfXSSStrings);

        log.info("STEP 3: Click 'Submit' button;");
        createNewItemPopUp.clickSave();
        Assert.assertTrue(contactListSelectedContentPage.isTableHeaderComplete(), "The table is not displayed.");
        Assert.assertTrue(contactListSelectedContentPage.isListItemDisplayed(listOfXSSStrings), "The '" + XSSString + "' item was not found in the new item table list.");

        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), meetingAgendaList);
        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), eventAgendaList);
    }


    @TestRail (id = "C286628")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingEventAgendaItemWithXSS(String XSSString)
    {
        List<String> listOfXSSStrings = Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString);

        log.info("Precondition 1: Navigate to site dashboard.");
        siteDashboardPage.navigate(siteName.get().getId());

        log.info("STEP 1: Go to 'Data Lists Page'.");
        dataListsPage.navigate(siteName.get().getId());
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(eventAgendaList), "The created '" + eventAgendaList + "' data list is not displayed in the list as created.");

        log.info("STEP 2: Click on the created Data List");
        dataListsPage.clickEventAgendaListItem(eventAgendaList);
        Assert.assertTrue(eventAgendaSelectedContentItem.isTableHeaderComplete(), "A column is missing from the 'Item List' table.");

        log.info("STEP 3: Click on New Item button.");
        dataListsPage.clickNewItemButton();
        Assert.assertTrue(dataListsPage.isNewItemPopupFormDisplayed(NewItemPopupForm.EventAgenda), "'Create New Item' dialog for 'Event Agenda' is not displayed.");
        Assert.assertTrue(createNewItemPopUp.areNewItemEventAgendaFieldsDisplayed(), "A field is missing from 'Create New Event Agenda Item' Popup.");
        Assert.assertTrue(createNewItemPopUp.isSelectAttachmentButtonLocatorDisplayed(), "Attachment 'Select' button is not displayed.");
        Assert.assertTrue(createNewItemPopUp.isSaveButtonDisplayed(), "'Save Button' is not displayed in 'Create New Item' popup.");
        Assert.assertTrue(createNewItemPopUp.isCancelButtonDisplayed(), "'Cancel Button' is not displayed in 'Create New Item' popup.");

        log.info("STEP 4: Fill in all the fields with '" + XSSString + "' string.");
        createNewItemPopUp.fillCreateNewEventAgendaItem(Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString));
        Assert.assertTrue(createNewItemPopUp.areAllEventAgendaFieldsFilledWithSpecificString(XSSString), "A field is not filled with the expected '" + XSSString + "' string.");

        log.info("STEP 5: Click on Submit button.");
        createNewItemPopUp.clickSave();
        Assert.assertTrue(eventAgendaSelectedContentItem.isTableHeaderComplete(), "The table is not displayed.");
        Assert.assertTrue(eventAgendaSelectedContentItem.isListItemDisplayed(listOfXSSStrings), "The '" + XSSString + "' item was not found in the new item table list.");

        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), contactListName);
        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), meetingAgendaList);
    }

    @TestRail (id = "C286629")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingMeetingAgendaItemWithXSS(String XSSString)
    {
        List<String> listOfXSSStrings = Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString);

        log.info("Precondition 1: Navigate to site dashboard.");
        siteDashboardPage.navigate(siteName.get().getId());

        log.info("STEP 1: Go to 'Data Lists Page'.");
        dataListsPage.navigate(siteName.get().getId());
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(meetingAgendaList), "The created '" + meetingAgendaList + "' data list is not displayed in the list as created.");

        log.info("STEP 2: Click on the created Data List");
        dataListsPage.clickEventAgendaListItem(meetingAgendaList);
        Assert.assertTrue(meetingAgendaListSelectedContentPage.isTableHeaderComplete(), "A column is missing from the 'Item List' table.");

        log.info("STEP 3: Click on New Item button.");
        dataListsPage.clickNewItemButton();
        Assert.assertTrue(dataListsPage.isNewItemPopupFormDisplayed(NewItemPopupForm.MeetingAgenda), "'Create New Item' dialog for 'Meeting Agenda' is not displayed.");
        Assert.assertTrue(createNewItemPopUp.areNewItemMeetingAgendaFieldsDisplayed(), "A field is missing from 'Create New Meeting Agenda Item' Popup.");
        Assert.assertTrue(createNewItemPopUp.isSelectAttachmentButtonLocatorDisplayed(), "Attachment 'Select' button is not displayed.");
        Assert.assertTrue(createNewItemPopUp.isSaveButtonDisplayed(), "'Save Button' is not displayed in 'Create New Item' popup.");
        Assert.assertTrue(createNewItemPopUp.isCancelButtonDisplayed(), "'Cancel Button' is not displayed in 'Create New Item' popup.");

        log.info("STEP 4: Fill in all the fields with '" + XSSString + "' string.");
        createNewItemPopUp.fillCreateNewMeetingAgendaItem(Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString), null, null);
        Assert.assertTrue(createNewItemPopUp.areAllMeetingAgendaFieldsFilledWithSpecificString(XSSString), "A field is not filled with the expected '" + XSSString + "' string.");

        log.info("STEP 5: Click on Submit button.");
        createNewItemPopUp.clickSave();
        Assert.assertTrue(meetingAgendaListSelectedContentPage.isTableHeaderComplete(), "The table is not displayed.");
        Assert.assertTrue(meetingAgendaListSelectedContentPage.isListItemDisplayed(listOfXSSStrings), "The '" + XSSString + "' item was not found in the new item table list.");

        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), eventAgendaList);
        dataListsService.deleteDataList(testUser.getUsername(), password, siteName.get().getId(), contactListName);
    }
}
