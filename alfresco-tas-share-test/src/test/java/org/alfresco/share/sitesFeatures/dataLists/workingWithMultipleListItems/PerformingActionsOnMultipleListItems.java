package org.alfresco.share.sitesFeatures.dataLists.workingWithMultipleListItems;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContentPage;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * Created by Alex Argint on 9/29/2016.
 */
public class PerformingActionsOnMultipleListItems extends BaseTest
{
    ContactListSelectedContentPage contactListSelectedContent;
    // @Autowired
    DataListsPage dataListsPage;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;
    private String uniqueIdentifier;
    private String description;
    private String contactList;
    private List<String> contacts = new ArrayList<>();
    private List<String> results = new ArrayList<>();
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void createUser()
    {
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        dataListsPage = new DataListsPage(webDriver);
        contactListSelectedContent = new ContactListSelectedContentPage(webDriver);

        authenticateUsingLoginPage(userName.get());

    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteUsersIfNotNull(userName.get());
    }

    public void setup(String id)
    {

        log.info("Preconditions for test " + id);
        uniqueIdentifier = "-" + id + "-" + RandomData.getRandomAlphanumeric();
        description = "description" + uniqueIdentifier;
        contactList = "ContactList" + uniqueIdentifier;

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.DATALISTS, null);
        dataListsService.createDataList(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), DataListsService.DataList.CONTACT_LIST, contactList, description);
        for (int i = 0; i <= 3; i++)
        {
            dataListsService.addContactListItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), contactList, "FirstName" + i, "LastName" + i,
                "E-mail" + i, "Company" + i, "JobTitle" + i, "PhoneOffice" + i, "PhoneMobile" + i, "Notes" + i);
        }

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickContactListItem(contactList);
        contactListSelectedContent.clickSelectAllOption();
    }

    @TestRail (id = "C6396")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void duplicateMultipleListItems() {

        log.info("Starting test C6396");
        setup("C6396");

        log.info("Step 1: Click 'Select Items..' button");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonDisplayed(), "Select Items button is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonEnabled(), "Select Items button is not enabled");
        contactListSelectedContent.clickSelectedItemsButton();
        Assert.assertTrue(contactListSelectedContent.isDuplicateItemsDisplayed(), "Duplicate Items option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeleteSelectedItemsDisplayed(), "Delete itemsOption is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeselectSelectedItemsDisplayed(), "Deselect selected items option is not displayed");
        contactListSelectedContent.clickSelectedItemsButton();

        log.info("Step 2: Click 'Duplicate' option and verify that ");
        contactListSelectedContent.clickDuplicateItemsOption();

        for (int i = 0; i <= 3; i++)
        {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            contactListSelectedContent.waitUntilNotificationMessageDisappears();
            results = contactListSelectedContent.returnNumberOfCheckedAndUncheckedItems(contacts);
            Assert.assertEquals(results.get(0), "1", "Number of checked items for row " + i + " is not 1");
            Assert.assertEquals(results.get(1), "1", "Number of unchecked items for row" + i + " is not 1");
            contacts.clear();
            results.clear();
        }
        deleteSitesIfNotNull(siteName.get());
    }

    @TestRail (id = "C6398")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteMultipleItems() {

        log.info("Starting test C6398");
        setup("C6398");

        log.info("Step 1: Click 'Select Items..' button");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonDisplayed(), "Select Items button is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonEnabled(), "Select Items button is not enabled");
        contactListSelectedContent.clickSelectedItemsButton();
        Assert.assertTrue(contactListSelectedContent.isDuplicateItemsDisplayed(), "Duplicate Items option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeleteSelectedItemsDisplayed(), "Delete itemsOption is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeselectSelectedItemsDisplayed(), "Deselect selected items option is not displayed");
        contactListSelectedContent.clickSelectedItemsButton();

        log.info("Step 2: Click 'Duplicate' option and verify that ");
        contactListSelectedContent.clickDeleteItemsOption(true);

        for (int i = 0; i <= 3; i++)
        {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);

            contactListSelectedContent.waitUntilNotificationMessageDisappears();
            List<WebElement> row = contactListSelectedContent.findRow(contacts);
            Assert.assertEquals(row.size(), 0, "Item on line " + i + " was not deleted");
            contacts.clear();
        }
        deleteSitesIfNotNull(siteName.get());
    }

    @TestRail (id = "C6407")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deselectAllItems()
    {
        log.info("Starting test C6407");
        setup("C6407");

        log.info("Step 1: Click 'Select Items..' button");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonDisplayed(), "Select Items button is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonEnabled(), "Select Items button is not enabled");
        contactListSelectedContent.clickSelectedItemsButton();
        Assert.assertTrue(contactListSelectedContent.isDuplicateItemsDisplayed(), "Duplicate Items option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeleteSelectedItemsDisplayed(), "Delete itemsOption is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeselectSelectedItemsDisplayed(), "Deselect selected items option is not displayed");
        contactListSelectedContent.clickSelectedItemsButton();

        log.info("Step 2: Click 'Duplicate' option and verify that ");
        contactListSelectedContent.clickDeselectAllItemsOption();

        for (int i = 0; i <= 3; i++)
        {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertFalse(contactListSelectedContent.isItemChecked(contacts), "Item at row " + i + " is checked");
            contacts.clear();
        }
        deleteSitesIfNotNull(siteName.get());
    }

}
