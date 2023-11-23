package org.alfresco.share.sitesFeatures.dataLists;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.*;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.*;
import static org.testng.Assert.assertTrue;


@Slf4j
public class DataListTableActionsTests extends BaseTest
{
    @Autowired
    ManageContactListItems manageContactListItems;

    @Autowired
    ContactListSelectedContent contactListSelectedContent;

    @Autowired
    DataListsService dataListsService;

    @Autowired
    SiteService siteService;

    private String contactList;
    private DataListsPage dataListsPage;
    private ContactListSelectedContentPage contactListSelectedContentPage;
    private ManageContactListItemPages manageContactListItemPages;
    private final String description = String.format("description%s", RandomData.getRandomAlphanumeric());
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void precondition()
    {
        log.info("Precondition: Any Test user is created");
        dataListsPage = new DataListsPage(webDriver);
        manageContactListItemPages = new ManageContactListItemPages(webDriver);
        contactListSelectedContentPage = new ContactListSelectedContentPage(webDriver);
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        siteService.addPageToSite(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
        authenticateUsingLoginPage(user.get());

        log.info("Preconditions for Data List Table Actions test");
        contactList = String.format("ContactList%s", RandomData.getRandomAlphanumeric());
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataListsService.DataList.CONTACT_LIST, contactList, description);
        dataListsService.addContactListItem(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), contactList, "FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
            "PhoneMobile", "Notes");

        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickContactListItem(contactList);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        siteService.delete(user.get().getUsername(), user.get().getPassword(), siteName.get().getId());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user.get());
    }


    @TestRail (id = "C6864")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void duplicateAction()
    {
        log.info("Step 1: Hover over the new item and verify Edit, Duplicate, delete actions are visible");
        Assert.assertTrue(manageContactListItemPages.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItemPages.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItemPages.isDeleteActionDisplayed(), "Delete action is not displayed");

        log.info("Step 2: Click Duplicate action and verify the new item is displayed in the table");
        manageContactListItemPages.clickDuplicateAction();
        List<String> expectedList = Arrays.asList("FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice", "PhoneMobile", "Notes");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }

        log.info("Step 3: Click Edit action and verify it has identical data with source row");
        manageContactListItemPages.clickEditAction();
        Assert.assertTrue(manageContactListItemPages.areCorrectValuesDisplayedOnEditPopup("FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
            "PhoneMobile"), "The values from Edit Data Item form are not correcly displayed");
    }

    @TestRail (id = "C6865")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteAction()
    {
        log.info("Step 1: Hover over the new item and verify Edit, Duplicate, delete actions are visible");
        Assert.assertTrue(manageContactListItemPages.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItemPages.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItemPages.isDeleteActionDisplayed(), "Delete action is not displayed");

        log.info("Step 2: Click Delete action and verify Delete Item pop-up");
        manageContactListItemPages.clickDeleteAction();
        Assert.assertTrue(manageContactListItemPages.isDeletePopUpDisplayed(), "Delete pop-up is not displayed");
        Assert.assertTrue(manageContactListItemPages.isDeleteButtonDisplayedOnDeletePopup(), "Delete button is not displayed");
        Assert.assertTrue(manageContactListItemPages.isCancelButtonDisplayedOnDeletePopup(), "Cancel button is not displayed");

        log.info("Step 3: Click Delete on the Delete Item pop-up and verify the item is deleted (data list contains no items)");
        manageContactListItemPages.confirmDeleteAction();
        Assert.assertTrue(manageContactListItemPages.isNoListItemsDisplayed(), "Data item was not deleted");
    }

    @TestRail (id = "C6867")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editAction()
    {
        log.info("Step 1: Hover over the new item and verify Edit, Duplicate, Delete actions are visible");
        Assert.assertTrue(manageContactListItemPages.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItemPages.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItemPages.isDeleteActionDisplayed(), "Delete action is not displayed");

        log.info("Step 2: Click Edit action and verify Edit Data Item window is opened");
        manageContactListItemPages.clickEditAction();
        Assert.assertTrue(manageContactListItemPages.isEditItemWindowDisplayed(), "Edit Item window is not displayed");

        log.info("Steps 3,4: Edit data, save the changes and verify the changes are saved");
        manageContactListItemPages.editContactItem("editedFirstName", "editedLastName", "editedEmail", "editedCompany", "editedJob", "editedPhoneOffice",
            "editedPhoneMobile", "editedNotes");
        List<String> expectedList = Arrays.asList("editedFirstName", "editedLastName", "editedEmail", "editedCompany", "editedJob", "editedPhoneOffice", "editedPhoneMobile", "editedNotes");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }
}
