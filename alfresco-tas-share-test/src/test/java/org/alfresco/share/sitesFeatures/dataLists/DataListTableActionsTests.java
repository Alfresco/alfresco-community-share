package org.alfresco.share.sitesFeatures.dataLists;

import java.util.Arrays;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.ManageContactListItems;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataListTableActionsTests extends ContextAwareWebTest
{
    private final String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("description%s", RandomData.getRandomAlphanumeric());
    @Autowired
    DataListsPage dataListsPage;
    @Autowired
    ManageContactListItems manageContactListItems;
    @Autowired
    ContactListSelectedContent contactListSelectedContent;
    private String contactList;

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);

        setupAuthenticatedSession(userName, password);
    }

    @BeforeMethod (alwaysRun = true)
    public void createSite()
    {
        LOG.info("Preconditions for Data List Table Actions test");

        contactList = String.format("ContactList%s", RandomData.getRandomAlphanumeric());
        dataListsService.createDataList(userName, password, siteName, DataListsService.DataList.CONTACT_LIST, contactList, description);
        dataListsService.addContactListItem(userName, password, siteName, contactList, "FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
            "PhoneMobile", "Notes");

        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactList);
        getBrowser().waitInSeconds(2);
        contactListSelectedContent.setBrowser(getBrowser());
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C6864")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void duplicateAction()
    {
        LOG.info("Step 1: Hover over the new item and verify Edit, Duplicate, delete actions are visible");

        Assert.assertTrue(manageContactListItems.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItems.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItems.isDeleteActionDisplayed(), "Delete action is not displayed");

        LOG.info("Step 2: Click Duplicate action and verify the new item is displayed in the table");
        manageContactListItems.clickDuplicateAction();

        contactListSelectedContent.setBrowser(getBrowser());

        getBrowser().waitInSeconds(3);
        Assert.assertEquals(contactListSelectedContent.duplicatedRows(Arrays.asList("FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
            "PhoneMobile", "Notes")), true);

        LOG.info("Step 3: Click Edit action and verify it has identical data with source row");

        manageContactListItems.clickEditAction();
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(manageContactListItems.areCorrectValuesDisplayedOnEditPopup("FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
            "PhoneMobile"), "The values from Edit Data Item form are not correcly displayed");

    }

    @TestRail (id = "C6865")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void deleteAction()
    {
        LOG.info("Step 1: Hover over the new item and verify Edit, Duplicate, delete actions are visible");

        Assert.assertTrue(manageContactListItems.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItems.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItems.isDeleteActionDisplayed(), "Delete action is not displayed");

        LOG.info("Step 2: Click Delete action and verify Delete Item pop-up");

        manageContactListItems.clickDeleteAction();
        Assert.assertTrue(manageContactListItems.isDeletePopUpDisplayed(), "Delete pop-up is not displayed");
        Assert.assertTrue(manageContactListItems.isDeleteButtonDisplayedOnDeletePopup(), "Delete button is not displayed");
        Assert.assertTrue(manageContactListItems.isCancelButtonDisplayedOnDeletePopup(), "Cancel button is not displayed");

        LOG.info("Step 3: Click Delete on the Delete Item pop-up and verify the item is deleted (data list contains no items)");

        manageContactListItems.confirmDeleteAction();

        Assert.assertTrue(manageContactListItems.isNoListItemsDisplayed(), "Data item was not deleted");

    }

    @TestRail (id = "C6867")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void editAction()
    {
        LOG.info("Step 1: Hover over the new item and verify Edit, Duplicate, Delete actions are visible");

        Assert.assertTrue(manageContactListItems.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItems.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItems.isDeleteActionDisplayed(), "Delete action is not displayed");

        LOG.info("Step 2: Click Edit action and verify Edit Data Item window is opened");

        manageContactListItems.clickEditAction();
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(manageContactListItems.isEditItemWindowDisplayed(), "Edit Item window is not displayed");

        LOG.info("Steps 3,4: Edit data, save the changes and verify the changes are saved");

        manageContactListItems.editContactItem("editedFirstName", "editedLastName", "editedEmail", "editedCompany", "editedJob", "editedPhoneOffice",
            "editedPhoneMobile", "editedNotes");

        contactListSelectedContent.setBrowser(getBrowser());
        Assert.assertEquals(contactListSelectedContent.isListItemDisplayed(Arrays.asList("editedFirstName", "editedLastName", "editedEmail", "editedCompany",
            "editedJob", "editedPhoneOffice", "editedPhoneMobile", "editedNotes")), true);

    }

}
