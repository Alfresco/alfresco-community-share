package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.eventAgenda;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.EventAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class EditEventAgendaItemTest extends ContextAwareWebTest
{
    //@Autowired
    DataListsPage dataListsPage;

    //@Autowired
    EditItemPopUp editItemPopUp;

    String random = RandomData.getRandomAlphanumeric();
    String userName = "User-" + random;
    String siteName = "SiteName-" + random;
    String listName = "eventAgenda-" + random;
    String itemReference = "reference-" + random;
    String itemSessionName = "Event agenda item session name";
    String file = "testFile1";
    String fileToAttach = "testDoc.txt";

    String newItemReference = "Updated Reference";
    String startTime = "12/11/2016";
    String updatedItemStartTime = "Sat 12 Nov 2016 ";
    String endTime = "10/12/2016";
    String updatedItemEndTime = "Sat 10 Dec 2016";
    String newItemSessionName = "Updated Session Name";
    String newItemPresenter = "Update Presenter";
    String newItemAudience = "Updated Audience";
    String newItemNotes = "Updated Notes";

    @BeforeClass (alwaysRun = false)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.EVENT_AGENDA, listName, "Event Agenda list description.");

        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + file);
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + fileToAttach);
        dataListsService.addEventAgendaItem(userName, password, siteName, listName, itemReference, null, null, itemSessionName, null, null, null,
            Collections.singletonList(file));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickEventAgendaListItem(listName);
    }

    @AfterClass (alwaysRun = false)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C10537")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void verifyPossibilityToEditItem()
    {
        LOG.info("STEP1: Click Edit icon for the item");
        // dataListsPage.currentContent.editItem(Arrays.asList(itemReference, null, null, itemSessionName, null, null, null,
        // file));
        dataListsPage.clickEditButtonForListItem();

        LOG.info("STEP2: Provide new input for the Reference field");
        editItemPopUp.editContent(EventAgendaFields.Reference.toString(), newItemReference);
        editItemPopUp.editContent(EventAgendaFields.StartTime.toString(), startTime);
        editItemPopUp.editContent(EventAgendaFields.EndTime.toString(), endTime);
        editItemPopUp.editContent(EventAgendaFields.SessionName.toString(), newItemSessionName);
        editItemPopUp.editContent(EventAgendaFields.Presenter.toString(), newItemPresenter);
        editItemPopUp.editContent(EventAgendaFields.Audience.toString(), newItemAudience);
        editItemPopUp.editContent(EventAgendaFields.Notes.toString(), newItemNotes);

        LOG.info("STEP3: Click Attachments/Select button and select doc. Click on the + button to add the attachment and then Ok to confirm");
        //editItemPopUp.removeAttachments(file);
        editItemPopUp.addAttachmentFromDocumentLibrary(fileToAttach);

        LOG.info("STEP4: Click on Save button");
        editItemPopUp.clickSave();
        assertEquals(dataListsPage.currentContent.messageDisplayed(), "Data Item updated successfully", "Edited Data Item message");
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(Arrays.asList(newItemReference, updatedItemStartTime, updatedItemEndTime, newItemSessionName,
            newItemPresenter, newItemAudience, newItemNotes, fileToAttach)), "Event agenda item " + newItemReference + " - Data updated");
    }
}