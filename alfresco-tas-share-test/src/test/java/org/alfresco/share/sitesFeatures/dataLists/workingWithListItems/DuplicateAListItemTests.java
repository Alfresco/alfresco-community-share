package org.alfresco.share.sitesFeatures.dataLists.workingWithListItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditItemPopUp;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class DuplicateAListItemTests extends ContextAwareWebTest
{

    @Autowired
    DataListsPage dataListsPage;
    
    @Autowired
    DataListsService dataLists;
    
    @Autowired
    DataUtil dataUtil;
    
    @Autowired
    protected EditItemPopUp editItemPopUp;
    
    private String userName;
    private String siteName;
    private List<Page> pagesToAdd = new ArrayList<Page>();
    
    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        pagesToAdd.add(Page.DATALISTS);
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        setupAuthenticatedSession(userName, password);
    }
    
    @TestRail(id = "C6391")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void editingAMandatoryFieldOfAListItem()
    {      
        
        logger.info("Preconditions: Create a new 'Contact' List with an item");
        String contactListName = "contact" + System.currentTimeMillis();
        dataLists.createDataList(adminUser, adminPassword, siteName, DataList.CONTACT_LIST, contactListName, "Contact list description");
        dataLists.addContactListItem(adminUser, adminPassword, siteName, contactListName, "firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes");

        dataListsPage.navigate(siteName);
        dataListsPage.clickContactListItem(contactListName);
        
        logger.info("Step 1: Select the list item of the Contact list.");
        Assert.assertEquals("The data list is not displayed.", dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes")), true);
        Assert.assertEquals("The row added is unique.", dataListsPage.currentContent.duplicatedRows(Arrays.asList("new Name", "user", "test@test.com", "test Company", "test", "123456", "+41256422", "testNotes")), false);
        
        logger.info("Step 2: Click the 'Duplicate' button for the contact list item to be edited.");
        dataListsPage.currentContent.duplicateItem(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes"));
        dataListsPage.waitUntilMessageDisappears();
        Assert.assertEquals("The data list item was not duplicated.", dataListsPage.currentContent.isListItemDisplayed(Arrays.asList("firstName", "lastName", "test@test.com", "companyName", "jobTitle", "123456", "+41256422", "testNotes")), true);

        
    }
}
