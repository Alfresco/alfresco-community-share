package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.locationList;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.LocationFields;
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
public class EditLocationItemTest extends BaseTest
{
    //@Autowired
    DataListsPage dataListsPage;

    //@Autowired
    EditItemPopUp editItemPopUp;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected DataListsService dataListsService;
    String random = RandomData.getRandomAlphanumeric();
    String listName = "List name" + random;
    String itemTitle = "Item title";
    String itemFile = "testFile1";
    String newItemTitle = "edited ItemTitle";
    String itemAddress1 = "itemAddress1";
    String itemAddress2 = "itemAddress2";
    String itemAddress3 = "itemAddress3";
    String itemZipCode = "itemZipCode";
    String itemState = "itemState";
    String itemCountry = "itemCountry";
    String itemDescription = "itemDescription";
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
        dataListsService.createDataList(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DataListsService.DataList.LOCATION_LIST, listName, "Location List description");

        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + itemFile);
        contentService.uploadFileInSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), testDataFolder + attachedFile);
        dataListsService.addLocationItem(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), listName, itemTitle, "", "", "", "", "", "", "", Collections.singletonList(itemFile));

        authenticateUsingLoginPage(userName.get());

        dataListsPage.navigate(siteName.get());
        dataListsPage.clickLocationListItem(listName);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C6795")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void saveEditItem()
    {
        log.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        dataListsPage.clickEditButtonForListItem();

        log.info("STEP2: Fill in 'Edit Data' form fields with valid data");
        editItemPopUp.editContent(LocationFields.Title.toString(), newItemTitle);
        editItemPopUp.editContent(LocationFields.AddressLine1.toString(), itemAddress1);
        editItemPopUp.editContent(LocationFields.AddressLine2.toString(), itemAddress2);
        editItemPopUp.editContent(LocationFields.AddressLine3.toString(), itemAddress3);
        editItemPopUp.editContent(LocationFields.ZipCode.toString(), itemZipCode);
        editItemPopUp.editContent(LocationFields.State.toString(), itemState);
        editItemPopUp.editContent(LocationFields.Country.toString(), itemCountry);
        editItemPopUp.editContent(LocationFields.Description.toString(), itemDescription);

        log.info("STEP3: Click Attachments/ Select button. Navigate to Document Library/ test.docx and click the + button for test.docx then OK to confirm.");
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        log.info("STEP4: Click Save button");
        editItemPopUp.clickSave();

        List<String> expectedList = Arrays.asList(newItemTitle, itemAddress1, itemAddress2, itemAddress3, itemZipCode, itemState, itemCountry, itemDescription);
        for (String anExpectedList : expectedList)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(anExpectedList), "Data list item is updated.");
        }

        List<String> attachmentFiles = Arrays.asList(itemFile+"\n"+attachedFile);
        for (String attachments : attachmentFiles)
        {
            assertTrue(dataListsPage.getFilterTypeList().contains(attachments), "Attachments updated.");
        }
    }
}