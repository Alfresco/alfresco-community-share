package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.eventList;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.EventListFields;
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

import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditEventListItemTest extends ContextAwareWebTest
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
    String listName = "List name" + random;
    String itemTitle = "Item title";
    String itemFile = "testFile1";

    String newItemTitle = " edited ItemTitle";
    String newItemDescription = "edited ItemDescription";
    String newItemLocation = "edited ItemLocation";
    String newItemRegistrations = "edited registrations";
    String startDate = "01/11/2016";
    String startTime = "10:15";
    String itemStartDate = "Thu 10 Nov 2016 " + startTime + ":00";
    String endDate = "10/12/2016";
    String endTime = "20:30";
    String itemEndDate = "Sat 10 Dec 2016 " + endTime + ":00";
    String newItemNotes = "notes";
    String attachedFile = "testDoc.txt";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(DashboardCustomization.Page.DATALISTS);
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        dataLists.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.EVENT_LIST, listName, "Issue List description");

        String path = srcRoot + "testdata" + File.separator;
        contentService.uploadFileInSite(userName, password, siteName, path + itemFile);
        contentService.uploadFileInSite(userName, password, siteName, path + attachedFile);
        dataLists.addEventListItem(adminUser, adminPassword, siteName, listName, itemTitle, "", "", null, null, "", "", Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickEventListItem(listName);
    }

    @TestRail(id = "C6696")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void saveEditItem()
    {
        LOG.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        // List<String> item = Arrays.asList(itemTitle, "", "", "", "", "", "", itemFile);
        // dataListsPage.currentContent.editItem(item);
        dataListsPage.clickEditButtonForListItem();

        LOG.info("STEP2: Fill in 'Edit Data' form fields with valid data");
        editItemPopUp.editContent(EventListFields.Title.toString(), newItemTitle);
        editItemPopUp.editContent(EventListFields.Description.toString(), newItemDescription);
        editItemPopUp.editContent(EventListFields.Location.toString(), newItemLocation);
        editItemPopUp.editContent(EventListFields.StartDate.toString(), startDate);
        editItemPopUp.editContent(EventListFields.StartTime.toString(), startTime);
        editItemPopUp.editContent(EventListFields.EndDate.toString(), endDate);
        editItemPopUp.editContent(EventListFields.EndTime.toString(), endTime);
        editItemPopUp.editContent(EventListFields.Registrations.toString(), newItemRegistrations);
        editItemPopUp.editContent(EventListFields.Notes.toString(), newItemNotes);
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        LOG.info("STEP3: Click Save button");
        editItemPopUp.clickSave();
        LOG.info(itemStartDate + ", " + itemEndDate);
        List<String> expectedItem = Arrays.asList(newItemTitle, newItemDescription, newItemLocation, itemStartDate, itemEndDate, newItemRegistrations,
                newItemNotes, attachedFile);
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(expectedItem), newItemTitle + " issue list item is displayed.");

        cleanupAuthenticatedSession();
    }
}