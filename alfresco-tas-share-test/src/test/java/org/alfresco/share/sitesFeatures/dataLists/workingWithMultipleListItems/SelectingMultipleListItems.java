package org.alfresco.share.sitesFeatures.dataLists.workingWithMultipleListItems;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Argint Alex on 9/22/2016.
 */
public class SelectingMultipleListItems extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    ContactListSelectedContent contactListSelectedContent;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;
    private String contactList;
    private List<String> contacts = new ArrayList<>();

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }


    public void setup(String id)
    {

        LOG.info("Preconditions for test " + id);
        uniqueIdentifier = String.format("-%s-%s", id, RandomData.getRandomAlphanumeric());
        siteName = "siteName" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;
        contactList = "ContactList" + uniqueIdentifier;

        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, DashboardCustomization.Page.DATALISTS, null);
        dataListsService.createDataList(userName, password, siteName, DataListsService.DataList.CONTACT_LIST, contactList, description);
        for (int i = 0; i <= 9; i++)
        {
            dataListsService.addContactListItem(userName, password, siteName, contactList, "FirstName" + i, "LastName" + i,
                "E-mail" + i, "Company" + i, "JobTitle" + i, "PhoneOffice" + i, "PhoneMobile" + i, "Notes" + i);
        }

        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactList);
        getBrowser().waitInSeconds(2);
        contactListSelectedContent.setBrowser(getBrowser());
    }

    @TestRail (id = "C6403")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void selectAllItemsTest()
    {

        LOG.info("Starting test C6403");
        setup("C6403");

        LOG.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContent.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        LOG.info("Verifying that all items are unselected");
        for (int i = 0; i <= 9; i++)
        {
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

        for (int i = 0; i <= 9; i++)
        {
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
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6404")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void selectAllByUsingInvertSelection()
    {

        LOG.info("Starting test C6404");
        setup("C6404");

        LOG.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContent.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        LOG.info("Verifying that all items are unselected");
        for (int i = 0; i <= 9; i++)
        {
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

        for (int i = 0; i <= 9; i++)
        {
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
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C6405")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deselectByUsingInvertSelection()
    {
        LOG.info("Starting test C6405");
        setup("C6405");
        contactListSelectedContent.clickSelectAllOption();

        LOG.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContent.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        LOG.info("Verifying that all items are selected");
        for (int i = 0; i <= 9; i++)
        {
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

        for (int i = 0; i <= 9; i++)
        {
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
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C6406")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deselectByUsingSelectNone()
    {
        LOG.info("Starting test C6406");
        setup("C6406");
        contactListSelectedContent.clickSelectAllOption();

        LOG.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContent.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContent.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        LOG.info("Verifying that all items are selected");
        for (int i = 0; i <= 9; i++)
        {
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

        for (int i = 0; i <= 9; i++)
        {
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
        siteService.delete(adminUser, adminPassword, siteName);


    }
}
