package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.contactList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ContactListFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class EditContactListItemTest extends BaseTest
{
    //@Autowired
    DataListsPage dataListsPage;

    //@Autowired
    EditItemPopUp editItemPopUp;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;

    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    String contactListName = "contactList";
    String editedFirstName = "editedFirstName";
    String editedLastName = "editedLastName";
    String editedEmail = "editedEmail";
    String editedCompany = "editedCompany";
    String editedJobTitle = "editedJobTitle";
    String editedPhoneOffice = "0123456789";
    String editedPhoneMobile = "0123456780";
    String editedNotes = "editedNotes";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        dataListsPage = new DataListsPage(webDriver);
        editItemPopUp = new EditItemPopUp(webDriver);

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
        dataListsService.createDataList(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), DataListsService.DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataListsService.addContactListItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle",
            "123456", "+41256422", "testNotes");

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickContactListItem(contactListName);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());

    }

    @TestRail (id = "C6540")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPossibilityToCancelEditItem() {
        log.info("STEP1: Click the 'Edit' icon for the contact list item to be edited");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Edit All Contact List Item fields");
        editItemPopUp
            .editContent(ContactListFields.FirstName.toString(), editedFirstName)
            .editContent(ContactListFields.LastName.toString(), editedLastName)
            .editContent(ContactListFields.Email.toString(), editedEmail)
            .editContent(ContactListFields.Company.toString(), editedCompany)
            .editContent(ContactListFields.JobTitle.toString(), editedJobTitle)
            .editContent(ContactListFields.PhoneOffice.toString(), editedPhoneOffice)
            .editContent(ContactListFields.PhoneMobile.toString(), editedPhoneMobile)
            .editContent(ContactListFields.Notes.toString(), editedNotes);

        log.info("STEP3: Click Cancel button & Verify Edited details are not saved");
        editItemPopUp.clickCancel();
        List<String> expectedList = Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle",
            "123456", "+41256422", "testNotes");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is not updated.");
        }
    }

    @TestRail (id = "C6549")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPossibilityToEditItem() throws InterruptedException {
        log.info("STEP1: Click the 'Edit' icon for the contact list item to be edited");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Edit the First Name");
        editItemPopUp.editContent(ContactListFields.FirstName.toString(), editedFirstName);

        log.info("STEP3: Edit Last Name field ");
        editItemPopUp.editContent(ContactListFields.LastName.toString(), editedLastName);

        log.info("STEP4: Edit Email field");
        editItemPopUp.editContent(ContactListFields.Email.toString(), editedEmail);

        log.info("STEP5: Edit Company field");
        editItemPopUp.editContent(ContactListFields.Company.toString(), editedCompany);

        log.info("STEP6: Edit Job title field");
        editItemPopUp.editContent(ContactListFields.JobTitle.toString(), editedJobTitle);

        log.info("STEP: Edit Phone (Office) field");
        editItemPopUp.editContent(ContactListFields.PhoneOffice.toString(), editedPhoneOffice);

        log.info("STEP: Edit Phone (Mobile) field");
        editItemPopUp.editContent(ContactListFields.PhoneMobile.toString(), editedPhoneMobile);

        log.info("STEP: Edit Notes text area");
        editItemPopUp.editContent(ContactListFields.Notes.toString(), editedNotes);

        log.info("STEP10: Click Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");
        List<String> expectedList = Arrays.asList(editedFirstName, editedLastName, editedEmail, editedCompany, editedJobTitle, editedPhoneOffice,
            editedPhoneMobile, editedNotes);
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }
}