package org.alfresco.share.sitesFeatures.dataLists.workingWithMultipleListItems;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContent;
import org.alfresco.po.share.site.dataLists.ContactListSelectedContentPage;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.*;


/**
 * Created by Argint Alex on 9/22/2016.
 */
@Slf4j
public class SelectingMultipleListItems extends BaseTest
{
    @Autowired
    ContactListSelectedContent contactListSelectedContent;

    @Autowired
    DataListsService dataListsService;

    @Autowired
    SiteService siteService;

    private ContactListSelectedContentPage contactListSelectedContentPage;
    private DataListsPage dataListsPage;
    private String uniqueIdentifier;
    private String description;
    private String contactList;
    private List<String> contacts = new ArrayList<>();

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    public void setup(String id)
    {

        log.info("Preconditions for test " + id);
        uniqueIdentifier = String.format("-%s-%s", id, RandomData.getRandomAlphanumeric());
        description = "description" + uniqueIdentifier;
        contactList = "ContactList" + uniqueIdentifier;

        dataListsPage = new DataListsPage(webDriver);
        contactListSelectedContentPage = new ContactListSelectedContentPage(webDriver);

        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        siteService.addPageToSite(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.DATALISTS, null);
        dataListsService.createDataList(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DataListsService.DataList.CONTACT_LIST, contactList, description);
        for (int i = 0; i <= 9; i++)
        {
            dataListsService.addContactListItem(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), contactList, "FirstName" + i, "LastName" + i,
                "E-mail" + i, "Company" + i, "JobTitle" + i, "PhoneOffice" + i, "PhoneMobile" + i, "Notes" + i);
        }
        authenticateUsingLoginPage(user.get());
        dataListsPage.navigate(siteName.get().getId());
        dataListsPage.clickContactListItem(contactList);
    }


    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C6403")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void selectAllItemsTest()
    {

        log.info("Starting test C6403");
        setup("C6403");

        log.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContentPage.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContentPage.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContentPage.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContentPage.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        log.info("Verifying that all items are unselected");
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
            Assert.assertFalse(contactListSelectedContentPage.isItemChecked(contacts), "Item at line " + i + " is checked");
            contacts.clear();
        }

        log.info("Step 2: Click 'Select All' and verify that all items have been selected");
        contactListSelectedContentPage.clickSelectAllOption();

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
            Assert.assertTrue(contactListSelectedContentPage.isItemChecked(contacts), "Item at line " + i + " is not checked");
            contacts.clear();
        }
        siteService.delete(user.get().getUsername(), user.get().getPassword(), siteName.get().getId());
    }

    @TestRail (id = "C6404")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void selectAllByUsingInvertSelection()
    {

        log.info("Starting test C6404");
        setup("C6404");

        log.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContentPage.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContentPage.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContentPage.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContentPage.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        log.info("Verifying that all items are unselected");
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
            Assert.assertFalse(contactListSelectedContentPage.isItemChecked(contacts), "Item at line " + i + " is checked");
            contacts.clear();
        }

        log.info("Step 2: Click 'Invert Selection' and verify that all items have been selected");
        contactListSelectedContentPage.clickInvertSelectionOption();

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
            Assert.assertTrue(contactListSelectedContentPage.isItemChecked(contacts), "Item at line " + i + " is not checked");
            contacts.clear();
        }
        siteService.delete(user.get().getUsername(), user.get().getPassword(), siteName.get().getId());
    }

    @TestRail (id = "C6405")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deselectByUsingInvertSelection()
    {
        log.info("Starting test C6405");
        setup("C6405");
        contactListSelectedContentPage.clickSelectAllOption();

        log.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContentPage.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContentPage.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContentPage.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContentPage.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        log.info("Verifying that all items are selected");
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
            Assert.assertTrue(contactListSelectedContentPage.isItemChecked(contacts), "Item at line " + i + " is not checked");
            contacts.clear();
        }

        log.info("Step 2: Click 'Invert Selection' and verify that all items have been unselected");
        contactListSelectedContentPage.clickInvertSelectionOption();

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
            Assert.assertFalse(contactListSelectedContentPage.isItemChecked(contacts), "Item at line " + i + " is checked");
            contacts.clear();
        }
        siteService.delete(user.get().getUsername(), user.get().getPassword(), siteName.get().getId());
    }

    @TestRail (id = "C6406")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deselectByUsingSelectNone()
    {
        log.info("Starting test C6406");
        setup("C6406");
        contactListSelectedContentPage.clickSelectAllOption();

        log.info("Step 1: Click the Select button on the Contact List");
        Assert.assertTrue(contactListSelectedContentPage.isSelectButtonDisplayed(), "'Select' button is not displayed'");
        Assert.assertTrue(contactListSelectedContentPage.isInvertSelectionButtonOptionEnabled(), "Invert Selection option is not displayed");
        Assert.assertTrue(contactListSelectedContentPage.isSelectAllButtonOptionDisplayed(), "Select All option is not displayed");
        Assert.assertTrue(contactListSelectedContentPage.isSelectNoneButtonOptionDisplayed(), "None option is not displayed");

        log.info("Verifying that all items are selected");
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
            Assert.assertTrue(contactListSelectedContentPage.isItemChecked(contacts), "Item at line " + i + " is not checked");
            contacts.clear();
        }

        log.info("Step 2: Click 'None' and verify that all items have been unselected");
        contactListSelectedContentPage.clickSelectNoneOption();

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
            Assert.assertFalse(contactListSelectedContentPage.isItemChecked(contacts), "Item at line " + i + " is checked");
            contacts.clear();
        }
        siteService.delete(user.get().getUsername(), user.get().getPassword(), siteName.get().getId());
    }
}
