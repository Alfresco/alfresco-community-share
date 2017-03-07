package org.alfresco.share.sitesFeatures.dataLists.workingWithMultipleListItems;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Argint Alex on 9/22/2016.
 */
public class SelectingMultipleListItems extends ContextAwareWebTest {

    @Autowired
    DataListsService dataListsService;

    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    ContactListSelectedContent contactListSelectedContent;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;
    private String contactList;
    private List<String> contacts = new ArrayList<String>();

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
        for (int i = 0; i <= 9; i++) {
            dataListsService.addContactListItem(userName, password, siteName, contactList, "FirstName" + i, "LastName" + i,
                    "E-mail" + i, "Company" + i, "JobTitle" + i, "PhoneOffice" + i, "PhoneMobile" + i, "Notes" + i);
        }

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactList);
        browser.waitInSeconds(2);
        contactListSelectedContent.setBrowser(browser);
    }

    @TestRail(id = "C6403")
    @Test
    public void selectAllItemsTest() {

        LOG.info("Starting test C6403");
        setup("C6403");

        LOG.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContent.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        LOG.info("Verifying that all items are unselected");
        for (int i = 0; i <= 9; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertFalse(contactListSelectedContent.isItemChecked(contacts), "Item at line " + i + " is checked");
            contacts.clear();
        }

        LOG.info("Step 2: Click 'Select All' and verify that all items have been selected");
        contactListSelectedContent.clickSelectAllOption();

        for (int i = 0; i <= 9; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertTrue(contactListSelectedContent.isItemChecked(contacts), "Item at line " + i + " is not checked");
            contacts.clear();
        }
    }

    @TestRail(id = "C6404")
    @Test
    public void selectAllByUsingInvertSelection() {

        LOG.info("Starting test C6404");
        setup("C6404");

        LOG.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContent.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        LOG.info("Verifying that all items are unselected");
        for (int i = 0; i <= 9; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertFalse(contactListSelectedContent.isItemChecked(contacts), "Item at line " + i + " is checked");
            contacts.clear();
        }

        LOG.info("Step 2: Click 'Invert Selection' and verify that all items have been selected");
        contactListSelectedContent.clickInvertSelectionOption();

        for (int i = 0; i <= 9; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertTrue(contactListSelectedContent.isItemChecked(contacts), "Item at line " + i + " is not checked");
            contacts.clear();
        }
    }

    @TestRail(id = "C6405")
    @Test
    public void deselectByUsingInvertSelection() {
        LOG.info("Starting test C6405");
        setup("C6405");
        contactListSelectedContent.clickSelectAllOption();

        LOG.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContent.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        LOG.info("Verifying that all items are selected");
        for (int i = 0; i <= 9; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertTrue(contactListSelectedContent.isItemChecked(contacts), "Item at line " + i + " is not checked");
            contacts.clear();
        }

        LOG.info("Step 2: Click 'Invert Selection' and verify that all items have been unselected");
        contactListSelectedContent.clickInvertSelectionOption();

        for (int i = 0; i <= 9; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertFalse(contactListSelectedContent.isItemChecked(contacts), "Item at line " + i + " is checked");
            contacts.clear();
        }
    }

    @TestRail(id = "C6406")
    @Test
    public void deselectByUsingSelectNone() {
        LOG.info("Starting test C6406");
        setup("C6406");
        contactListSelectedContent.clickSelectAllOption();

        LOG.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContent.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        LOG.info("Verifying that all items are selected");
        for (int i = 0; i <= 9; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertTrue(contactListSelectedContent.isItemChecked(contacts), "Item at line " + i + " is not checked");
            contacts.clear();
        }

        LOG.info("Step 2: Click 'None' and verify that all items have been unselected");
        contactListSelectedContent.clickSelectNoneOption();

        for (int i = 0; i <= 9; i++) {
            contacts.add("FirstName" + i);
            contacts.add("LastName" + i);
            contacts.add("E-mail" + i);
            contacts.add("Company" + i);
            contacts.add("JobTitle" + i);
            contacts.add("PhoneOffice" + i);
            contacts.add("PhoneMobile" + i);
            contacts.add("Notes" + i);
            Assert.assertFalse(contactListSelectedContent.isItemChecked(contacts), "Item at line " + i + " is checked");
            contacts.clear();
        }

    }
}
