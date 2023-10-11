package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.meetingAgenda;

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

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.MeetingAgendaFields;
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
public class  EditMeetingAgendaItemTest extends BaseTest
{
    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;

    //@Autowired
    DataListsPage dataListsPage;

    //@Autowired
    EditItemPopUp editItemPopUp;

    String random = RandomData.getRandomAlphanumeric();
    String meetingAgendaList = "meetingAgenda-" + random;
    String itemReference = "reference-" + random;
    String itemTitle = "meetingItemTitle" + random;
    String itemDescription = "description";
    String itemFile = "testFile1.txt";
    String newItemReference = "Updated Reference";
    String newItemTitle = "Updated Title";
    String newItemDescription = "Updated Description";
    String newItemTime = "01/11/2016";
    String updatedItemTime = "Thu 10 Nov 2016";
    String newOwner = "Updated Owner";
    String fileToAttach = "testDoc.txt";
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

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataListsService.DataList.MEETING_AGENDA, meetingAgendaList,
            "Meeting Agenda list description.");

        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + itemFile);
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + fileToAttach);
        dataListsService.addMeetingItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), meetingAgendaList, itemReference, itemTitle, itemDescription, null, userName.get().getUsername(),
            Collections.singletonList(itemFile));

        authenticateUsingLoginPage(userName.get());
        dataListsPage.navigate(siteName.get());
        dataListsPage.clickMeetingAgendaListItem(meetingAgendaList);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C10356")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPossibilityToEditItem()
    {
        log.info("STEP1: Click Edit icon for the item");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Provide new input for the Reference field");
        editItemPopUp.editContent(MeetingAgendaFields.Reference.toString(), newItemReference);

        log.info("STEP3: Provide new input for the Title field");
        editItemPopUp.editContent(MeetingAgendaFields.Title.toString(), newItemTitle);

        log.info("STEP4: Provide new input for the Description field");
        editItemPopUp.editContent(MeetingAgendaFields.Description.toString(), newItemDescription);

        log.info("STEP5: Provide new input for the Time (mins) field");
        editItemPopUp.editContent(MeetingAgendaFields.Time.toString(), newItemTime);

        log.info("STEP6: Provide new input for the Time (mins) field");
        editItemPopUp.editContent(MeetingAgendaFields.Owner.toString(), newOwner);

        log.info("STEP7: Add new Attachment to data list item");
        editItemPopUp.addAttachmentFromDocumentLibrary(fileToAttach);

        log.info("STEP8: Click on Save button");
        editItemPopUp.clickSave();

        assertEquals(dataListsPage.messageDisplayed(), "Data Item updated successfully", "The pop-up message isn't as expected.");

        List<String> expectedList = Arrays.asList(newItemReference, newItemTitle, newItemDescription, newItemTime, newOwner);
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }

        List<String> attachmentFiles = Arrays.asList(itemFile+"\n"+fileToAttach);
        for (String attachments : attachmentFiles)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(attachments), "Attachments updated.");
        }
    }
}