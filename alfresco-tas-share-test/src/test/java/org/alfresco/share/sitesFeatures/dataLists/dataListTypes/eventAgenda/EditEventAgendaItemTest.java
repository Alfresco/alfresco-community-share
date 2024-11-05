package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.eventAgenda;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.EventAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class EditEventAgendaItemTest extends BaseTest
{
    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;
    CreateNewItemPopUp createNewItemPopUp;

    //@Autowired
    DataListsPage dataListsPage;

    //@Autowired
    EditItemPopUp editItemPopUp;

    String random = RandomData.getRandomAlphanumeric();
    String listName = "eventAgenda-" + random;
    String itemReference = "reference-" + random;
    String itemSessionName = "Event agenda item session name";
    String file = "testFile1";
    String fileToAttach = "testDoc.txt";
    String newItemReference = "Updated Reference";
    String startTime = "12/11/2016";
    String endTime = "10/12/2016";
    String newItemSessionName = "Updated Session Name";
    String newItemPresenter = "Update Presenter";
    String newItemAudience = "Updated Audience";
    String newItemNotes = "Updated Notes";
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
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
        createNewItemPopUp = new CreateNewItemPopUp(webDriver);

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
        dataListsService.createDataList(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), DataListsService.DataList.EVENT_AGENDA, listName, "Event Agenda list description.");

        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + file);
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + fileToAttach);
        dataListsService.addEventAgendaItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), listName, itemReference, null, null, itemSessionName, null, null, null,
            Collections.singletonList(file));

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickEventAgendaListItem(listName);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());

    }

    @TestRail (id = "C6598, C6604, C6607")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void AddingNewItem() {
        log.info("STEP1: Click New Item for the List item");
        dataListsPage.clickNewItemButtons();

        log.info("STEP2: Adding Items on Reference, Presenter & Other Fields");
        createNewItemPopUp.fillCreateNewEventAgendaItem(Arrays.asList("testReference", "12", "13", "testSessionName", "testPresenterWithWildcards_{<%@#&>/?\\|}", "testAudience", "testNotes"));

        log.info("STEP3: Adding Attachments");
        createNewItemPopUp.addAttachmentFromDocumentLibrary(fileToAttach);
        createNewItemPopUp.clickSave();

        log.info("STEP4: Verify Added Items are saved in list item");
        List<String> expectedList = Arrays.asList("testReference", "12", "13", "testSessionName", "testPresenterWithWildcards_{<%@#&>/?\\|}", "testAudience", "testNotes");
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list items updated.");
            assertTrue(dataListsPage.isAttachmentsUpdated(fileToAttach), "Attachments added");
        }
    }

    @TestRail (id = "C10537")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPossibilityToEditItem() {
        log.info("STEP1: Click Edit icon for the item");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Provide new input for the Reference field");
        editItemPopUp.editContent(EventAgendaFields.Reference.toString(), newItemReference);
        editItemPopUp.editContent(EventAgendaFields.StartTime.toString(), startTime);
        editItemPopUp.editContent(EventAgendaFields.EndTime.toString(), endTime);
        editItemPopUp.editContent(EventAgendaFields.SessionName.toString(), newItemSessionName);
        editItemPopUp.editContent(EventAgendaFields.Presenter.toString(), newItemPresenter);
        editItemPopUp.editContent(EventAgendaFields.Audience.toString(), newItemAudience);
        editItemPopUp.editContent(EventAgendaFields.Notes.toString(), newItemNotes);

        log.info("STEP3: Click Attachments/Select button and select doc. Click on the + button to add the attachment and then Ok to confirm");
        editItemPopUp.removeAttachments(file);
        editItemPopUp.clickSave();

        dataListsPage.clickEditButtonForListItem();
        editItemPopUp.addAttachmentFromDocumentLibrary(fileToAttach);

        log.info("STEP4: Click on Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.messageDisplayed(), "Data Item updated successfully", "Edited Data Item message");
        List<String> expectedList = Arrays.asList(newItemReference, startTime, endTime, newItemSessionName,
            newItemPresenter, newItemAudience, newItemNotes, fileToAttach);
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }

    @TestRail (id = "C10538")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void CancelEditingEventAgenda() {
        log.info("STEP1: Click Edit icon for the item");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Provide new input for the Reference field");
        editItemPopUp.editContent(EventAgendaFields.Reference.toString(), newItemReference);
        editItemPopUp.editContent(EventAgendaFields.StartTime.toString(), startTime);
        editItemPopUp.editContent(EventAgendaFields.EndTime.toString(), endTime);
        editItemPopUp.editContent(EventAgendaFields.SessionName.toString(), newItemSessionName);
        editItemPopUp.editContent(EventAgendaFields.Presenter.toString(), newItemPresenter);
        editItemPopUp.editContent(EventAgendaFields.Audience.toString(), newItemAudience);
        editItemPopUp.editContent(EventAgendaFields.Notes.toString(), newItemNotes);

        log.info("STEP3: Click Attachments/Select button and select doc. Click on the + button to add the attachment and then Ok to confirm");
        editItemPopUp.removeAttachments(file);

        log.info("STEP4: Click on Cancel button");
        editItemPopUp.clickCancel();

        List<String> expectedList = Arrays.asList(itemReference, itemSessionName, file);
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }
}