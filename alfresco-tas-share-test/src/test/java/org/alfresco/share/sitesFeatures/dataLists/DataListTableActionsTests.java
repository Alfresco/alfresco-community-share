package org.alfresco.share.sitesFeatures.dataLists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent;
import org.alfresco.po.share.site.dataLists.CreateDataListPopUp;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.ManageContactListItems;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataListTableActionsTests extends ContextAwareWebTest

{

    @Autowired
    DataUtil dataUtil;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    CustomizeSitePage customizeSitePage;

    @Autowired
    DataListsService dataLists;

    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    CreateDataListPopUp createDataListPopUp;

    @Autowired
    ManageContactListItems manageContactListItems;

    @Autowired
    ContactListSelectedContent contactListSelectedContent;

    @Autowired
    DataListsService dataListsService;

    private String userName;
    private String siteName;
    private List<Page> pagesToAdd = new ArrayList<Page>();
    private String description;
    private String contactList;
    String contactListName = "contact" + System.currentTimeMillis();

    @BeforeMethod
    public void setupTest()
    {
        logger.info("Preconditions for Data List Table Actions test");
        pagesToAdd.add(Page.DATALISTS);
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        description = "description" + DataUtil.getUniqueIdentifier();
        contactList = "ContactList" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);

        dataListsService.createDataList(userName, password, siteName, DataListsService.DataList.CONTACT_LIST, contactList, description);

        dataListsService.addContactListItem(userName, password, siteName, contactList, "FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
                "PhoneMobile", "Notes");

        setupAuthenticatedSession(userName, password);

        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactList);
        getBrowser().waitInSeconds(2);
        contactListSelectedContent.setBrowser(getBrowser());

    }

    @TestRail(id = "C6864")
    @Test
    public void duplicateAction()

    {
        logger.info("Step 1: Hover over the new item and verify Edit, Duplicate, delete actions are visible");

        Assert.assertTrue(manageContactListItems.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItems.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItems.isDeleteActionDisplayed(), "Delete action is not displayed");

        logger.info("Step 2: Click Duplicate action and verify the new item is displayed in the table");
        manageContactListItems.clickDuplicateAction();

        contactListSelectedContent.setBrowser(getBrowser());

        getBrowser().waitInSeconds(3);
        Assert.assertEquals(contactListSelectedContent.duplicatedRows(Arrays.asList("FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
                "PhoneMobile", "Notes")), true);

        logger.info("Step 3: Click Edit action and verify it has identical data with source row");

        manageContactListItems.clickEditAction();
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(manageContactListItems.areCorrectValuesDisplayedOnEditPopup("FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
                "PhoneMobile"), "The values from Edit Data Item form are not correcly displayed");

    }

    @TestRail(id = "C6865")
    @Test
    public void deleteAction()
    {

        logger.info("Step 1: Hover over the new item and verify Edit, Duplicate, delete actions are visible");

        Assert.assertTrue(manageContactListItems.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItems.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItems.isDeleteActionDisplayed(), "Delete action is not displayed");

        logger.info("Step 2: Click Delete action and verify Delete Item pop-up");

        manageContactListItems.clickDeleteAction();
        Assert.assertTrue(manageContactListItems.isDeletePopUpDisplayed(), "Delete pop-up is not displayed");
        Assert.assertTrue(manageContactListItems.isDeleteButtonDisplayedOnDeletePopup(), "Delete button is not displayed");
        Assert.assertTrue(manageContactListItems.isCancelButtonDisplayedOnDeletePopup(), "Cancel button is not displayed");

        logger.info("Step 3: Click Delete on the Delete Item pop-up and verify the item is deleted (data list contains no items)");

        manageContactListItems.confirmDeleteAction();

        Assert.assertTrue(manageContactListItems.isNoListItemsDisplayed(), "Data item was not deleted");

    }

    @TestRail(id = "C6867")
    @Test
    public void editAction()

    {

        logger.info("Step 1: Hover over the new item and verify Edit, Duplicate, Delete actions are visible");

        Assert.assertTrue(manageContactListItems.isEditActionDisplayed(), "Edit action is not displayed");
        Assert.assertTrue(manageContactListItems.isDuplicateActionDisplayed(), "Duplicate action is not displayed");
        Assert.assertTrue(manageContactListItems.isDeleteActionDisplayed(), "Delete action is not displayed");

        logger.info("Step 2: Click Edit action and verify Edit Data Item window is opened");

        manageContactListItems.clickEditAction();
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(manageContactListItems.isEditItemWindowDisplayed(), "Edit Item window is not displayed");

        logger.info("Steps 3,4: Edit data, save the changes and verify the changes are saved");

        manageContactListItems.editContactItem("editedFirstName", "editedLastName", "editedEmail", "editedCompany", "editedJob", "editedPhoneOffice",
                "editedPhoneMobile", "editedNotes");

        contactListSelectedContent.setBrowser(getBrowser());
        Assert.assertEquals(contactListSelectedContent.isListItemDisplayed(Arrays.asList("editedFirstName", "editedLastName", "editedEmail", "editedCompany",
                "editedJob", "editedPhoneOffice", "editedPhoneMobile", "editedNotes")), true);

    }

}
