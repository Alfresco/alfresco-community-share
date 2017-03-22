package org.alfresco.share.sitesFeatures.dataLists.workingWithMultipleListItems;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Argint on 9/29/2016.
 */
public class PerformingActionsOnMultipleListItems extends ContextAwareWebTest {

    @Autowired
    ContactListSelectedContent contactListSelectedContent;

    @Autowired
    DataListsService dataListsService;

    @Autowired
    DataListsPage dataListsPage;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;
    private String contactList;
    private List<String> contacts = new ArrayList<String>();
    private List<String> results = new ArrayList<String>();

    public void setup(String id) {

        LOG.info("Preconditions for test " + id);
        uniqueIdentifier = "-" + id + "-" + DataUtil.getUniqueIdentifier();
        siteName = "siteName" + uniqueIdentifier;
        userName = "User" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;
        contactList = "ContactList" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        siteService.addPageToSite(userName, DataUtil.PASSWORD, siteName, DashboardCustomization.Page.DATALISTS, null);
        dataListsService.createDataList(userName, password, siteName, DataListsService.DataList.CONTACT_LIST, contactList, description);
        for (int i = 0; i <= 3; i++) {
            dataListsService.addContactListItem(userName, password, siteName, contactList, "FirstName" + i, "LastName" + i,
                    "E-mail" + i, "Company" + i, "JobTitle" + i, "PhoneOffice" + i, "PhoneMobile" + i, "Notes" + i);
        }

        setupAuthenticatedSession(userName, password);
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
    }

}
