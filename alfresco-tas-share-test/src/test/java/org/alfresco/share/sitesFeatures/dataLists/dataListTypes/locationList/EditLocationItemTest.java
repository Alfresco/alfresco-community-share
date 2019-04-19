package org.alfresco.share.sitesFeatures.dataLists.dataListTypes.locationList;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.LocationFields;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditLocationItemTest extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    EditItemPopUp editItemPopUp;
    
    String random = RandomData.getRandomAlphanumeric();
    String userName = "User-" + random;
    String siteName = "SiteName-" + random;
    String listName = "List name" + random;
    String itemTitle = "Item title";
    String itemFile = "testFile1";

    String newItemTitle = " edited ItemTitle";
    String itemAddress1 = "itemAddress1";
    String itemAddress2 = "itemAddress2";
    String itemAddress3 = "itemAddress3";
    String itemZipCode = "itemZipCode";
    String itemState = "itemState";
    String itemCountry = "itemCountry";
    String itemDescription = "itemDescription";
    String attachedFile = "testDoc.txt";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataListsService.DataList.LOCATION_LIST, listName, "Location List description");
        
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + itemFile);
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + attachedFile);
        dataListsService.addLocationItem(adminUser, adminPassword, siteName, listName, itemTitle, "", "", "", "", "", "", "", Collections.singletonList(itemFile));

        setupAuthenticatedSession(userName, password);
        dataListsPage.navigate(siteName);
        dataListsPage.clickLocationListItem(listName);
    }

    @TestRail(id = "C6795")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void saveEditItem()
    {
        LOG.info("STEP1: Click 'Edit' icon for the simple task list item to be edited");
        //List<String> item = Arrays.asList(itemTitle, "", "", "", "", "", "", "", itemFile);
        // dataListsPage.currentContent.editItem(item);
        dataListsPage.clickEditButtonForListItem();

        LOG.info("STEP2: Fill in 'Edit Data' form fields with valid data");
        editItemPopUp.editContent(LocationFields.Title.toString(), newItemTitle);
        editItemPopUp.editContent(LocationFields.AddressLine1.toString(), itemAddress1);
        editItemPopUp.editContent(LocationFields.AddressLine2.toString(), itemAddress2);
        editItemPopUp.editContent(LocationFields.AddressLine3.toString(), itemAddress3);
        editItemPopUp.editContent(LocationFields.ZipCode.toString(), itemZipCode);
        editItemPopUp.editContent(LocationFields.State.toString(), itemState);
        editItemPopUp.editContent(LocationFields.Country.toString(), itemCountry);
        editItemPopUp.editContent(LocationFields.Description.toString(), itemDescription);

        LOG.info("STEP3: Click Attachments/ Select button. Navigate to Document Library/ test.docx and click the + button for test.docx then OK to confirm.");
        editItemPopUp.addAttachmentFromDocumentLibrary(attachedFile);

        LOG.info("STEP4: Click Save button");
        editItemPopUp.clickSave();
        List<String> attachmentsList = Arrays.asList(attachedFile, itemFile);
        List<String> expectedItem = Arrays.asList(newItemTitle, itemAddress1, itemAddress2, itemAddress3, itemZipCode, itemState, itemCountry, itemDescription,
                attachedFile);
        assertTrue(dataListsPage.currentContent.isListItemDisplayed(expectedItem), newItemTitle + " issue list item is displayed.");
    }
}