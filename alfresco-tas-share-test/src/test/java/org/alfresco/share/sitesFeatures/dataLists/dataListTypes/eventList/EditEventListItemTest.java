package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.eventList;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.EventListFields;
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
public class EditEventListItemTest extends BaseTest
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
    String listName = "List name" + random;
    String itemTitle = "Item title";
    String itemFile = "testFile1";
    String newItemTitle = "edited ItemTitle";
    String newItemDescription = "edited ItemDescription";
    String newItemLocation = "edited ItemLocation";
    String newItemRegistrations = "edited registrations";
    String startDate = "01/11/2016";
    String startTime = "10:15";
    String itemStartDate = "Tue 1 Nov 2016 " + startTime + ":00";
    String endDate = "10/12/2016";
    String endTime = "20:30";
    String itemEndDate = "Sat 10 Dec 2016 " + endTime + ":00";
    String newItemNotes = "notes";
    String attachedFile = "testDoc.txt";
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
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataListsService.DataList.EVENT_LIST, listName, "Event List description");

        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword() , siteName.get().getId(), testDataFolder + itemFile);
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword() , siteName.get().getId(), testDataFolder + attachedFile);
        dataListsService.addEventListItem(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), listName, itemTitle, "", "", null, null, "", "", Collections.singletonList(itemFile));

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickEventListItem(listName);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C6696")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void saveEditItem()
    {
        log.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Fill in 'Edit Data' form fields with valid data");
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

        log.info("STEP3: Click Save button");
        editItemPopUp.clickSave();
        log.info(itemStartDate + ", " + itemEndDate);
        List<String> expectedList = Arrays.asList(newItemTitle, newItemDescription, newItemLocation, itemStartDate, itemEndDate, newItemRegistrations,
            newItemNotes );
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }
    }
}