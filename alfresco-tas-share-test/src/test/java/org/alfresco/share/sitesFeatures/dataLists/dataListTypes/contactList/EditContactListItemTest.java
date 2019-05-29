package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.contactList;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ContactListFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditContactListItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    String userName = String.format("User-%s", RandomData.getRandomAlphanumeric());
    String siteName = String.format("SiteName%s", RandomData.getRandomAlphanumeric());
    String contactListName = "contactList";
    String editedFirstName = "editedFirstName";
    String editedLastName = "editedLastName";
    String editedEmail = "editedEmail";
    String editedCompany = "editedCompany";
    String editedJobTitle = "editedJobTitle";
    String editedPhoneOffice = "0123456789";
    String editedPhoneMobile = "0123456780";
    String editedNotes = "editedNotes";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataListsService.addContactListItem(adminUser, adminPassword, siteName, contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle",
            "123456", "+41256422", "testNotes");

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactListName);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C6549")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPossibilityToEditItem()
    {
        LOG.info("STEP1: Click the 'Edit' icon for the contact list item to be edited");
        dataListsPage.currentContent
            .editItem(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes"));

        LOG.info("STEP2: Edit the First Name");
        editItemPopUp.editContent(ContactListFields.FirstName.toString(), editedFirstName);

        LOG.info("STEP3: Edit Last Name field ");
        editItemPopUp.editContent(ContactListFields.LastName.toString(), editedLastName);

        LOG.info("STEP4: Edit Email field");
        editItemPopUp.editContent(ContactListFields.Email.toString(), editedEmail);

        LOG.info("STEP5: Edit Company field");
        editItemPopUp.editContent(ContactListFields.Company.toString(), editedCompany);

        LOG.info("STEP6: Edit Job title field");
        editItemPopUp.editContent(ContactListFields.JobTitle.toString(), editedJobTitle);

        LOG.info("STEP: Edit Phone (Office) field");
        editItemPopUp.editContent(ContactListFields.PhoneOffice.toString(), editedPhoneOffice);

        LOG.info("STEP: Edit Phone (Mobile) field");
        editItemPopUp.editContent(ContactListFields.PhoneMobile.toString(), editedPhoneMobile);

        LOG.info("STEP: Edit Notes text area");
        editItemPopUp.editContent(ContactListFields.Notes.toString(), editedNotes);

        LOG.info("STEP10: Click Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.currentContent.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");
        List<String> expectedList = Arrays.asList(editedFirstName, editedLastName, editedEmail, editedCompany, editedJobTitle, editedPhoneOffice,
            editedPhoneMobile, editedNotes);
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(expectedList), "Data list item is updated.");
    }
}