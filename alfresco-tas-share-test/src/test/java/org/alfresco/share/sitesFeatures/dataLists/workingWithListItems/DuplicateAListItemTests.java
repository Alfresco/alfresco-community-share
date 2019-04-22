package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import junit.framework.Assert;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
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

public class DuplicateAListItemTests extends ContextAwareWebTest
{
    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    protected EditItemPopUp editItemPopUp;
    
    private String userName;
    private String siteName;
    
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(userName, password, siteName, Page.DATALISTS, null);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser,adminPassword,siteName );
    }
    
    @TestRail(id = "C6391")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editingAMandatoryFieldOfAListItem()
    {      
        
        LOG.info("Preconditions: Create a new 'Contact' List with an item");
        String contactListName = "contact" + System.currentTimeMillis();
        dataListsService.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataListsService.addContactListItem(adminUser, adminPassword, siteName, contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes");

        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactListName);
        
        LOG.info("Step 1: Select the list item of the Contact list.");
        Assert.assertEquals("The data list is not displayed.", dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes")), true);
        Assert.assertEquals("The row added is unique.", dataListsPage.currentContent.duplicatedRows(Arrays.asList("new Name", "user", "test@test.com", "test Company", "test", "123456", "+41256422", "testNotes")), false);
        
        LOG.info("Step 2: Click the 'Duplicate' button for the contact list item to be edited.");
        dataListsPage.currentContent.duplicateItem(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes"));
        dataListsPage.waitUntilMessageDisappears();
        Assert.assertEquals("The data list item was not duplicated.", dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes")), true);

        
    }
}
