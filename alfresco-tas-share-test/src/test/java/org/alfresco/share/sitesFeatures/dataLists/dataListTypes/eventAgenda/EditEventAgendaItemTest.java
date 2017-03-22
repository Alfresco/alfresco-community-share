package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.eventAgenda;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.EventAgendaFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditEventAgendaItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsService dataLists;

    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;

    private List<DashboardCustomization.Page> pagesToAdd = new ArrayList<>();

    String random = DataUtil.getUniqueIdentifier();
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

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(DashboardCustomization.Page.DATALISTS);
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        dataLists.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.EVENT_AGENDA, listName, "Event Agenda list description.");

        String path = srcRoot + "testdata" + File.separator;
        contentService.uploadFileInSite(userName, password, siteName, path + file);
        contentService.uploadFileInSite(userName, password, siteName, path + fileToAttach);
        dataLists.addEventAgendaItem(userName, password, siteName, listName, itemReference, null, null, itemSessionName, null, null, null,
                Collections.singletonList(file));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickEventAgendaListItem(listName);
    }

    @TestRail(id = "C10537")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
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