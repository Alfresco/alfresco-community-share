package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.meetingAgenda;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.MeetingAgendaFields;
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
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditMeetingAgendaItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    String random = RandomData.getRandomAlphanumeric();
    String userName = "User-" + random;
    String siteName = "SiteName-" + random;
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

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.MEETING_AGENDA, meetingAgendaList,
                "Meeting Agenda list description.");

        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + itemFile);
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + fileToAttach);
        dataListsService.addMeetingItem(userName, password, siteName, meetingAgendaList, itemReference, itemTitle, itemDescription, null, userName,
                Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickMeetingAgendaListItem(meetingAgendaList);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser,adminPassword,siteName );
    }

    @TestRail(id = "C10356")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyPossibilityToEditItem()
    {
        LOG.info("STEP1: Click Edit icon for the item");
        // dataListsPage.currentContent.editItem(Arrays.asList(itemReference, itemTitle, itemDescription, itemTime, userName, itemFile));
        dataListsPage.clickEditButtonForListItem();

        LOG.info("STEP2: Provide new input for the Reference field");
        editItemPopUp.editContent(MeetingAgendaFields.Reference.toString(), newItemReference);

        LOG.info("STEP3: Provide new input for the Title field");
        editItemPopUp.editContent(MeetingAgendaFields.Title.toString(), newItemTitle);

        LOG.info("STEP4: Provide new input for the Description field");
        editItemPopUp.editContent(MeetingAgendaFields.Description.toString(), newItemDescription);

        LOG.info("STEP5: Provide new input for the Time (mins) field");
        editItemPopUp.editContent(MeetingAgendaFields.Time.toString(), newItemTime);

        LOG.info("STEP6: Provide new input for the Time (mins) field");
        editItemPopUp.editContent(MeetingAgendaFields.Owner.toString(), newOwner);

        LOG.info("STEP7: Add new Attachment to data list item");
        editItemPopUp.addAttachmentFromDocumentLibrary(fileToAttach);

        LOG.info("STEP8: Click on Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.currentContent.messageDisplayed(), "Data Item updated successfully", "Edited Data Item message");
        List<String> expectedList = Arrays.asList(newItemReference, newItemTitle, newItemDescription, updatedItemTime, newOwner, fileToAttach);
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(expectedList), newItemTitle + " Meeting agenda item - Data updated");
    }
}