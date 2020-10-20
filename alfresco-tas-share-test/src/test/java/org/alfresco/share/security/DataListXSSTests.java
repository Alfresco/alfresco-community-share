package org.alfresco.share.security;

import java.util.Arrays;
import java.util.List;

import org.alfresco.common.DataProviderClass;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.SiteDataListsDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent;
import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.po.share.site.dataLists.CreateDataListDialog.DataListTypes;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.NewItemPopupForm;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EventAgendaSelectedContent;
import org.alfresco.po.share.site.dataLists.MeetingAgendaListSelectedContent;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DataListXSSTests extends ContextAwareWebTest
{
    @Autowired
    CreateNewItemPopUp createNewItemPopUp;

    @Autowired
    ContactListSelectedContent contactListSelectedContent;

    @Autowired
    EventAgendaSelectedContent eventAgendaSelectedContent;

    @Autowired
    MeetingAgendaListSelectedContent meetingAgendaListSelectedContent;

    @Autowired
    SiteDataListsDashlet siteDataListsDashlet;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    CreateDataListDialog createDataListDialog;

    @Autowired
    DataListsPage dataListsPage;

    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String testUser = "TestUser_" + uniqueIdentifier;
    private String siteName = "SiteName" + uniqueIdentifier;
    private String contactListName = "ContactList" + uniqueIdentifier;
    private String eventAgendaList = "EventAgendaList" + uniqueIdentifier;
    private String meetingAgendaList = "MeetingAgendaList" + uniqueIdentifier;

    @BeforeClass (alwaysRun = true)
    private void beforeClass()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "testUser_firstName", "testUser_lastName");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);

        LOG.info("Precondition : Any user logged in Share.");
        setupAuthenticatedSession(testUser, password);

        LOG.info("Precondition: Create a new 'Contact List'.");
        dataListsService.createDataList(testUser, password, siteName, DataList.CONTACT_LIST, contactListName, "Description.");

        LOG.info("Precondition: Create a new 'Event Agenda'.");
        dataListsService.createDataList(testUser, password, siteName, DataList.EVENT_AGENDA, eventAgendaList, "Description.");

        LOG.info("Precondition: Create a new 'Meeting Agenda'.");
        dataListsService.createDataList(testUser, password, siteName, DataList.MEETING_AGENDA, meetingAgendaList, "Description.");
    }

    @AfterClass (alwaysRun = false)
    private void afterClass()
    {
        dataListsService.deleteDataList(testUser, password, siteName, contactListName);
        dataListsService.deleteDataList(testUser, password, siteName, eventAgendaList);
        dataListsService.deleteDataList(testUser, password, siteName, meetingAgendaList);

        cleanupAuthenticatedSession();
        userService.delete(adminUser, adminPassword, testUser);
    }


    @TestRail (id = "C286626")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests", "tobefixed" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingContactListWithXSS(String XSSString)
    {
        LOG.info("Precondition 1: Navigate to site dashboard.");
        siteDashboardPage.navigate(siteName);

        LOG.info("Precondition 2: Add 'Site Data List' dashlet to site dashboard.");
        addSiteDataListDashletToSiteDashboard();

        LOG.info("STEP 1: Click 'New List' button from 'Site Data List' dashlet.");
        dataListsPage.clickOnCreateDataListLink();
        Assert.assertTrue(createDataListDialog.isNewListPopupDisplayed(), "'New List' PopUp is not displayed.");

        LOG.info("Step 1.1 => Check if all the expected list types are displayed");
        Assert.assertTrue(createDataListDialog.isDataListComplete(), "One or more list types from 'Data List' is missing.");

        LOG.info("Step 1.2 => Check if Contact list contains the right description");
        Assert.assertEquals(createDataListDialog.getTypeOfListDescription(DataListTypes.ContactList), DataListTypes.ContactList.description, "'Contact List' description is not correct.");

        LOG.info("Step 1.3 => Check if there are following controls: Title (mandatory) field, Description text area, Submit button (disable till Title is not filled, Cancel button available.");
        Assert.assertTrue(createDataListDialog.getTitleLabelText().contains("Title:"), "'Title input' label is not 'Title:'.");
        Assert.assertTrue(createDataListDialog.isTitleFieldDisplayed(), "'Title' input field is not displayed.");
        Assert.assertTrue(createDataListDialog.isTitleMandatoryIndicatorDisplayed(), "'Title' mandatory indicator is not displayed.");

        Assert.assertEquals(createDataListDialog.getDescriptionLabelText(), "Description:", "'Description' label is not 'Description'.");
        Assert.assertTrue(createDataListDialog.isDescriptionFieldDisplayed(), "'Description' field is not displayed.");

        Assert.assertTrue(createDataListDialog.isSaveButtonDisplayed(), "'Save' button is not displayed.");
        Assert.assertTrue(createDataListDialog.isCancelButtonDisplayed(), "'Cancel' button is not displayed.");

        LOG.info("STEP 2: Select the 'Contact list' type;");
        createDataListDialog.selectType(DataListTypes.ContactList.title);
        Assert.assertTrue(createDataListDialog.isExpectedTypeSelected(DataListTypes.ContactList.title), "'" + DataListTypes.ContactList.title + "' is not highlighted.");

        LOG.info("STEP 3: Enter XSS string '" + XSSString + "' into 'Title' (mandatory) field.");
        createDataListDialog.typeTitle(XSSString);
        Assert.assertEquals(createDataListDialog.getTitleValue(), XSSString, "XSS string '" + XSSString + "' was not entered in 'Title' field.");
        Assert.assertTrue(createDataListDialog.isNewListPopupDisplayed(), "'New List' PopUp is not displayed.");

        LOG.info("STEP 4: Enter XSS string '" + XSSString + "' into 'Description' field.");
        createDataListDialog.typeDescription(XSSString);
        Assert.assertEquals(createDataListDialog.getDescriptionValue(), XSSString, "XSS string '" + XSSString + "' was not entered in 'Description' field.");
        Assert.assertTrue(createDataListDialog.isNewListPopupDisplayed(), "'New List' PopUp is not displayed.");

        LOG.info("STEP 5: Click 'Submit' button.");
        createDataListDialog.clickSaveButton();
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(XSSString), "The created data list is not displayed in the list as created.");
    }

    @TestRail (id = "C286627")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingContactListItemWithXSS(String XSSString)
    {
        List<String> listOfXSSStrings = Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString);

        LOG.info("Precondition: Go to 'Data Lists Page'.");
        dataListsPage.navigate(siteName);
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(contactListName), "The created data list is not displayed in the list as created.");

        LOG.info("STEP 1: Click on the created Contact list's name in Lists section;");
        dataListsPage.clickContactListItem(contactListName);

        LOG.info("STEP 1.1 => Check if: List is highlighted;");
        Assert.assertTrue(dataListsPage.isExpectedListSelected(contactListName), "'" + contactListName + "' is not highlighted.");

        LOG.info("STEP 1.2 => Check if: Correct information and description are displayed at the right;");
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(contactListName), "The actual and expected lists name are not the same.");
        Assert.assertTrue(dataListsPage.isNewItemButtonDisplayed(), "'New Item' button is not displayed.");

        LOG.info("STEP 1.3 => Check if: New list button at the top is enabled;");
//        Assert.assertTrue(dataListsPage.assertNewListButtonIsDisplayed(), "'New List' button is not displayed.");

        LOG.info("STEP 1.4 => Check if: Table with the contact lists is displayed;");
        Assert.assertTrue(contactListSelectedContent.isTableHeaderComplete(), "A column is missing from the 'Item List' table.");

        LOG.info("STEP 2: Click New Item button and fill all the fields :");
        dataListsPage.clickNewItemButton();
        Assert.assertTrue(dataListsPage.isNewItemPopupFormDisplayed(CreateNewItemPopUp.NewItemPopupForm.ContactList), "'Create New Item' dialog for 'Contact List' is not displayed.");
        createNewItemPopUp.fillCreateNewContactItem(listOfXSSStrings);

        LOG.info("STEP 3: Click 'Submit' button;");
        createNewItemPopUp.clickSave();
        contactListSelectedContent.setBrowser(getBrowser());
        Assert.assertTrue(contactListSelectedContent.isTableHeaderComplete(), "The table is not displayed.");
        Assert.assertTrue(contactListSelectedContent.isListItemDisplayed(listOfXSSStrings), "The '" + XSSString + "' item was not found in the new item table list.");
    }


    @TestRail (id = "C286628")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingEventAgendaItemWithXSS(String XSSString)
    {
        List<String> listOfXSSStrings = Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString);

        LOG.info("Precondition 1: Navigate to site dashboard.");
        siteDashboardPage.navigate(siteName);

        LOG.info("Precondition 2: Add 'Site Data List' dashlet to site dashboard.");
        addSiteDataListDashletToSiteDashboard();

        LOG.info("STEP 1: Go to 'Data Lists Page'.");
        dataListsPage.navigate(siteName);
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(eventAgendaList), "The created '" + eventAgendaList + "' data list is not displayed in the list as created.");

        LOG.info("STEP 2: Click on the created Data List");
        dataListsPage.clickEventAgendaListItem(eventAgendaList);
        Assert.assertTrue(eventAgendaSelectedContent.isTableHeaderComplete(), "A column is missing from the 'Item List' table.");

        LOG.info("STEP 3: Click on New Item button.");
        dataListsPage.clickNewItemButton();
        Assert.assertTrue(dataListsPage.isNewItemPopupFormDisplayed(NewItemPopupForm.EventAgenda), "'Create New Item' dialog for 'Event Agenda' is not displayed.");
        Assert.assertTrue(createNewItemPopUp.areNewItemEventAgendaFieldsDisplayed(), "A field is missing from 'Create New Event Agenda Item' Popup.");
        Assert.assertTrue(createNewItemPopUp.isSelectAttachmentButtonLocatorDisplayed(), "Attachment 'Select' button is not displayed.");
        Assert.assertTrue(createNewItemPopUp.isSaveButtonDisplayed(), "'Save Button' is not displayed in 'Create New Item' popup.");
        Assert.assertTrue(createNewItemPopUp.isCancelButtonDisplayed(), "'Cancel Button' is not displayed in 'Create New Item' popup.");

        LOG.info("STEP 4: Fill in all the fields with '" + XSSString + "' string.");
        createNewItemPopUp.fillCreateNewEventAgendaItem(Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString));
        Assert.assertTrue(createNewItemPopUp.areAllEventAgendaFieldsFilledWithSpecificString(XSSString), "A field is not filled with the expected '" + XSSString + "' string.");

        LOG.info("STEP 5: Click on Submit button.");
        createNewItemPopUp.clickSave();
        eventAgendaSelectedContent.setBrowser(getBrowser());
        Assert.assertTrue(eventAgendaSelectedContent.isTableHeaderComplete(), "The table is not displayed.");
        Assert.assertTrue(eventAgendaSelectedContent.isListItemDisplayed(listOfXSSStrings), "The '" + XSSString + "' item was not found in the new item table list.");
    }

    @TestRail (id = "C286629")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingMeetingAgendaItemWithXSS(String XSSString)
    {
        List<String> listOfXSSStrings = Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString, XSSString);

        LOG.info("Precondition 1: Navigate to site dashboard.");
        siteDashboardPage.navigate(siteName);

        LOG.info("Precondition 2: Add 'Site Data List' dashlet to site dashboard.");
        addSiteDataListDashletToSiteDashboard();

        LOG.info("STEP 1: Go to 'Data Lists Page'.");
        dataListsPage.navigate(siteName);
        Assert.assertTrue(dataListsPage.getListsItemsTitle().contains(meetingAgendaList), "The created '" + meetingAgendaList + "' data list is not displayed in the list as created.");

        LOG.info("STEP 2: Click on the created Data List");
        dataListsPage.clickEventAgendaListItem(meetingAgendaList);
        Assert.assertTrue(meetingAgendaListSelectedContent.isTableHeaderComplete(), "A column is missing from the 'Item List' table.");

        LOG.info("STEP 3: Click on New Item button.");
        dataListsPage.clickNewItemButton();
        Assert.assertTrue(dataListsPage.isNewItemPopupFormDisplayed(NewItemPopupForm.MeetingAgenda), "'Create New Item' dialog for 'Meeting Agenda' is not displayed.");
        Assert.assertTrue(createNewItemPopUp.areNewItemMeetingAgendaFieldsDisplayed(), "A field is missing from 'Create New Meeting Agenda Item' Popup.");
        Assert.assertTrue(createNewItemPopUp.isSelectAttachmentButtonLocatorDisplayed(), "Attachment 'Select' button is not displayed.");
        Assert.assertTrue(createNewItemPopUp.isSaveButtonDisplayed(), "'Save Button' is not displayed in 'Create New Item' popup.");
        Assert.assertTrue(createNewItemPopUp.isCancelButtonDisplayed(), "'Cancel Button' is not displayed in 'Create New Item' popup.");

        LOG.info("STEP 4: Fill in all the fields with '" + XSSString + "' string.");
        createNewItemPopUp.fillCreateNewMeetingAgendaItem(Arrays.asList(XSSString, XSSString, XSSString, XSSString, XSSString), null, null);
        Assert.assertTrue(createNewItemPopUp.areAllMeetingAgendaFieldsFilledWithSpecificString(XSSString), "A field is not filled with the expected '" + XSSString + "' string.");

        LOG.info("STEP 5: Click on Submit button.");
        createNewItemPopUp.clickSave();
        meetingAgendaListSelectedContent.setBrowser(getBrowser());
        Assert.assertTrue(meetingAgendaListSelectedContent.isTableHeaderComplete(), "The table is not displayed.");
        Assert.assertTrue(meetingAgendaListSelectedContent.isListItemDisplayed(listOfXSSStrings), "The '" + XSSString + "' item was not found in the new item table list.");
    }


    /**
     * Add 'Site Data List' dashlet to user's site dashboard if it is not already displayed
     * And then check if it was successfully added.
     */
    private void addSiteDataListDashletToSiteDashboard()
    {
        if (!siteDataListsDashlet.isDashletDisplayed(Dashlet.DashletHelpIcon.DATA_LISTS))
        {
            siteService.addDashlet(testUser, password, siteName, SiteDashlet.SITE_DATA_LIST, DashletLayout.THREE_COLUMNS, 3, 1);
            siteDashboardPage.refresh();
        }
        Assert.assertEquals(siteDataListsDashlet.getDashletTitle(), "Site Data Lists", "'Site Data List' dashlet is not displayed in user's site dashboard.");
    }


}
