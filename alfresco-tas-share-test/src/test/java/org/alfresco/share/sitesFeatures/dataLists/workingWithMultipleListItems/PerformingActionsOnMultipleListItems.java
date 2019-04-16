package org.alfresco.share.sitesFeatures.dataLists.workingWithMultipleListItems;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Argint on 9/29/2016.
 */
public class PerformingActionsOnMultipleListItems extends ContextAwareWebTest
{

    @Autowired
    ContactListSelectedContent contactListSelectedContent;

    @Autowired
    DataListsPage dataListsPage;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;
    private String contactList;
    private List<String> contacts = new ArrayList<>();
    private List<String> results = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void createUser()
    {
        userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    public void setup(String id) {

        LOG.info("Preconditions for test " + id);
        uniqueIdentifier = "-" + id + "-" + RandomData.getRandomAlphanumeric();
        siteName = "siteName" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;
        contactList = "ContactList" + uniqueIdentifier;

        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, DashboardCustomization.Page.DATALISTS, null);
        dataListsService.createDataList(userName, password, siteName, DataListsService.DataList.CONTACT_LIST, contactList, description);
        for (int i = 0; i <= 3; i++) {
            dataListsService.addContactListItem(userName, password, siteName, contactList, "FirstName" + i, "LastName" + i,
                    "E-mail" + i, "Company" + i, "JobTitle" + i, "PhoneOffice" + i, "PhoneMobile" + i, "Notes" + i);
        }

        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactList);
        getBrowser().waitInSeconds(2);
        contactListSelectedContent.setBrowser(getBrowser());
        contactListSelectedContent.clickSelectAllOption();

    }

    @TestRail(id = "C6396")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void duplicateMultipleListItems() {

        LOG.info("Starting test C6396");
        setup("C6396");

        LOG.info("Step 1: Click 'Select Items..' button");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonDisplayed(), "Select Items button is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonEnabled(), "Select Items button is not enabled");
        contactListSelectedContent.clickSelectedItemsButton();
        Assert.assertTrue(contactListSelectedContent.isDuplicateItemsDisplayed(), "Duplicate Items option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeleteSelectedItemsDisplayed(), "Delete itemsOption is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeselectSelectedItemsDisplayed(), "Deselect selected items option is not displayed");
        contactListSelectedContent.clickSelectedItemsButton();

        LOG.info("Step 2: Click 'Duplicate' option and verify that ");
        contactListSelectedContent.clickDuplicateItemsOption();
        getBrowser().waitInSeconds(2);

        for (int i = 0; i <= 3; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            results = contactListSelectedContent.returnNumberOfCheckedAndUncheckedItems(contacts);
            Assert.assertEquals(results.get(0), "1", "Number of checked items for row " + i + " is not 1");
            Assert.assertEquals(results.get(1), "1", "Number of unchecked items for row" + i + " is not 1");
            contacts.clear();
            results.clear();
        }
        siteService.delete(adminUser,adminPassword,siteName );

    }

    @TestRail(id = "C6398")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteMultipleItems() {

        LOG.info("Starting test C6398");
        setup("C6398");

        LOG.info("Step 1: Click 'Select Items..' button");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonDisplayed(), "Select Items button is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonEnabled(), "Select Items button is not enabled");
        contactListSelectedContent.clickSelectedItemsButton();
        Assert.assertTrue(contactListSelectedContent.isDuplicateItemsDisplayed(), "Duplicate Items option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeleteSelectedItemsDisplayed(), "Delete itemsOption is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeselectSelectedItemsDisplayed(), "Deselect selected items option is not displayed");
        contactListSelectedContent.clickSelectedItemsButton();

        LOG.info("Step 2: Click 'Duplicate' option and verify that ");
        contactListSelectedContent.clickDeleteItemsOption(true);
        getBrowser().waitInSeconds(2);

        for (int i = 0; i <= 3; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            List<WebElement> row = contactListSelectedContent.findRow(contacts);
            Assert.assertEquals(row.size(), 0, "Item on line " + i + " was not deleted");
            contacts.clear();
        }
        siteService.delete(adminUser,adminPassword,siteName );

    }

    @TestRail(id = "C6407")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deselectAllItems() {
        LOG.info("Starting test C6407");
        setup("C6407");

        LOG.info("Step 1: Click 'Select Items..' button");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonDisplayed(), "Select Items button is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectItemsButtonEnabled(), "Select Items button is not enabled");
        contactListSelectedContent.clickSelectedItemsButton();
        Assert.assertTrue(contactListSelectedContent.isDuplicateItemsDisplayed(), "Duplicate Items option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeleteSelectedItemsDisplayed(), "Delete itemsOption is not displayed");
        Assert.assertTrue(contactListSelectedContent.isDeselectSelectedItemsDisplayed(), "Deselect selected items option is not displayed");
        contactListSelectedContent.clickSelectedItemsButton();

        LOG.info("Step 2: Click 'Duplicate' option and verify that ");
        contactListSelectedContent.clickDeselectAllItemsOption();
        getBrowser().waitInSeconds(2);

        for (int i = 0; i <= 3; i++) {
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
        siteService.delete(adminUser,adminPassword,siteName );

    }

}
