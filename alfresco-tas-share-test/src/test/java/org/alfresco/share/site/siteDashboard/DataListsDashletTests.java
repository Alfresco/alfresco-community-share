package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteDataListsDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.Test;
/**
 * @author bogdan.simion
 */
public class DataListsDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDataListsDashlet siteDataListsDashlet;
    @Autowired
    SiteDashboardPage siteDashboardPage;
    @Autowired
    DataListsPage dataListsPage;

    @TestRail(id = "C5568")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifySiteDataListsDashletNoListCreated()
    {
        String userName = String.format("user5568-%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("C5568%s", RandomData.getRandomAlphanumeric());

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C5568", "C5568");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.SITE_DATA_LIST, DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 1 - Verify Site Data Lists dahslet.");
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(siteDataListsDashlet.isHelpIconDisplayed(DashletHelpIcon.DATA_LISTS), "Data list helpIcon is not displayed");
        Assert.assertEquals(siteDataListsDashlet.getDashletTitle(), "Site Data Lists", "Dashlet title unavailable");
        Assert.assertTrue(siteDataListsDashlet.isCreateDataListLinkDisplayed(), "Create data list link is not displayed");
        Assert.assertEquals(siteDataListsDashlet.getMessageDisplayed(), "No lists to display", "Message not found");

        LOG.info("Step 2 - Click ? icon.");
        siteDataListsDashlet.clickOnHelpIcon(DashletHelpIcon.DATA_LISTS);

        Assert.assertEquals(siteDataListsDashlet.getHelpBalloonMessage(), "This dashlet shows lists relevant to the site. Clicking a list opens it.",
                "No text found");

        LOG.info("Step 3 - Click X icon.");
        siteDataListsDashlet.closeHelpBalloon();
        Assert.assertFalse(siteDataListsDashlet.isBalloonDisplayed(), "Balloon is not displayed");
    }

    @TestRail(id = "C5569")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifySiteDataListsDashletTwoListsCreated()
    {
        String userName = String.format("userC5569-%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("C5569%s", RandomData.getRandomAlphanumeric());
        String eventListName = String.format("C5569%s", RandomData.getRandomAlphanumeric());
        String todoListName = String.format("C5569%s", RandomData.getRandomAlphanumeric());
        String description = "C5569Test";
        int numberOfLists = 2;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C5568", "C5568");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.SITE_DATA_LIST, DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        dataListsService.createDataList(userName, password, siteName, DataList.EVENT_LIST, eventListName, description);
        dataListsService.createDataList(userName, password, siteName, DataList.TODO_LIST, todoListName, description);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 1 - Verify Site Data Lists dahslet.");
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(siteDataListsDashlet.isHelpIconDisplayed(DashletHelpIcon.DATA_LISTS), "Data list helpIcon is not displayed");
        Assert.assertTrue(siteDataListsDashlet.isCreateDataListLinkDisplayed(), "Create data list link is not displayed");
        Assert.assertTrue(siteDataListsDashlet.areSiteDataListsItemsDisplayed(), "Data list items not displayed");
        Assert.assertEquals(siteDataListsDashlet.getDashletTitle(), "Site Data Lists", "Dashlet title unavailable");
        Assert.assertEquals(siteDataListsDashlet.numberOfListItems(), numberOfLists, "Number of lists different than 2");

        LOG.info("Step 2 - Click on one of the Data Lists title link.");
        siteDataListsDashlet.clickOnFirstListItem();
        dataListsPage.setListItemSelectedContent();
        Assert.assertTrue(dataListsPage.isDataListTitleDisplayed(eventListName), "Data list title is not displayed");
        Assert.assertTrue(dataListsPage.currentContent.isDataListContentDisplayed(), "Data list content is not displayed");
        Assert.assertTrue(dataListsPage.isNewListButtonDisplayed(), "New List action button is not displayed");
        Assert.assertTrue(dataListsPage.currentContent.isSelectItemsButtonDisplayed(), "Selected Items action button is not enabled");
        Assert.assertTrue(dataListsPage.currentContent.isSelectButtonDisplayed(), "Select button is not displayed");
        Assert.assertTrue(dataListsPage.currentContent.isSelectAllButtonOptionDisplayed(), "Select All button is not displayed");
        Assert.assertTrue(dataListsPage.currentContent.isSelectNoneButtonOptionDisplayed(), "Select None button is not displayed");
        Assert.assertTrue(dataListsPage.currentContent.isInvertSelectionButtonOptionEnabled(), "Invert Selection button is not displayed");
        Assert.assertTrue(dataListsPage.currentContent.areNavigationLinksDisplayed(), "The navigation links are not displayed.");
        Assert.assertTrue(dataListsPage.isListWithCreatedListsDisplayed(), "List with created lists is not displayed");
        Assert.assertTrue(dataListsPage.currentContent.allFilterOptionsAreDisplayed(), "Not all filters are displayed.");
    }

    @TestRail(id = "C5570")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifySiteDataListsDashletCreateDataList()
    {
        String userName = String.format("userC5570-%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("C5570%s", RandomData.getRandomAlphanumeric());
        String listTitle = "Contact List";
        String listDescription = "Contacts";

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C5570", "C5570");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.SITE_DATA_LIST, DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);

        LOG.info("Step 1 - Click Create Data List icon on the Site Content Dashlet.");
        siteDataListsDashlet.clickOnCreateDataListLink();
        Assert.assertEquals(siteDataListsDashlet.getNewListWindowText(), "New List", "New List window is not opened");

        LOG.info("Step 2 - Select one of the types of lists available And fill in the Info for Title and Description fields.");
        siteDataListsDashlet.selectContactListFromTypesOfListsAvailable();
        siteDataListsDashlet.insertTitleAndDescriptionForDataList(listTitle, listDescription);
        Assert.assertEquals(siteDataListsDashlet.getListTitleTextInput(), listTitle, "List title provided was not accepted");
        Assert.assertEquals(siteDataListsDashlet.getListDescriptionTextInput(), listDescription, "List description provided was not accepted");
        Assert.assertTrue(siteDataListsDashlet.areSaveAndCancelButtonDisplayed(), "Save button or Cancel button are not displayed");

        LOG.info("Step 3 - Click on Cancel button.");
        siteDataListsDashlet.clickOnNewListCancelButton();
        Assert.assertFalse(siteDataListsDashlet.isNewListWindowDisplayed(), "New list window is displayed");
        Assert.assertTrue(siteDataListsDashlet.isDataListPageTheCurrentPage(), "The current page is not Data List details page");

        LOG.info("Step 4 - Return to Site Dashboard.");
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(siteDataListsDashlet.isSiteDashboardPageTheCurrentPage(siteName), "The current page is not Site Dashboard page");

        LOG.info("Step 5 - Click Create Data List icon on the Site Content Dashlet.");
        siteDataListsDashlet.clickOnCreateDataListLink();
        getBrowser().waitInSeconds(3);
        Assert.assertEquals(siteDataListsDashlet.getNewListWindowText(), "New List", "New List window is not opened");

        LOG.info("Step 6 - Select one of the types of lists available And fill in the Info for Title and Description fields.");
        siteDataListsDashlet.selectContactListFromTypesOfListsAvailable();
        siteDataListsDashlet.insertTitleAndDescriptionForDataList(listTitle, listDescription);
        Assert.assertEquals(siteDataListsDashlet.getListTitleTextInput(), listTitle, "List title provided was not accepted");
        Assert.assertEquals(siteDataListsDashlet.getListDescriptionTextInput(), listDescription, "List description provided was not accepted");
        Assert.assertTrue(siteDataListsDashlet.areSaveAndCancelButtonDisplayed(), "Save button or Cancel button are not displayed");

        LOG.info("Step 7 - Click on Save button.");
        siteDataListsDashlet.clickOnNewListSaveButton();
        getBrowser().waitInSeconds(4);
        Assert.assertTrue(siteDataListsDashlet.isDataListLinkDisplayed(listDescription), "Data list link is not displayed");
    }

    @TestRail(id = "C5571")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifySiteDataListsDashletUserWithConsumerRole()
    {
        String userNameSiteManager = String.format("userC5571-%s", RandomData.getRandomAlphanumeric());
        String userNameSiteConsumer = String.format("userC5571Consumer-%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("C5571%s", RandomData.getRandomAlphanumeric());
        String listName = "ContactList";
        String description = "C5571Test";

        userService.create(adminUser, adminPassword, userNameSiteManager, password, userNameSiteManager + domain, "C5568SiteManager", "C5568SiteManager");
        userService.create(adminUser, adminPassword, userNameSiteConsumer, password, userNameSiteConsumer + domain, "userC5571Consumer", "userC5571Consumer");
        siteService.create(userNameSiteManager, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userNameSiteManager, password, siteName, SiteDashlet.SITE_DATA_LIST, DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        dataListsService.createDataList(userNameSiteManager, password, siteName, DataList.CONTACT_LIST, listName, description);
        userService.createSiteMember(userNameSiteManager, password, userNameSiteConsumer, siteName, "SiteConsumer");

        setupAuthenticatedSession(userNameSiteConsumer, password);
        siteDashboardPage.navigate(siteName);

        LOG.info("Step 1 - Verify Site Data Lists dahslet.");
        Assert.assertTrue(siteDataListsDashlet.isHelpIconDisplayed(DashletHelpIcon.DATA_LISTS), "Data list helpIcon is not displayed");
        Assert.assertTrue(siteDataListsDashlet.isDataListItemDisplayed(), "Data list is not available");
        Assert.assertEquals(siteDataListsDashlet.getDashletTitle(), "Site Data Lists", "Dashlet title unavailable");
        Assert.assertFalse(siteDataListsDashlet.isCreateDataListLinkDisplayed(), "Create data list link is displayed");

        LOG.info("Step 2 - Click ? icon.");
        siteDataListsDashlet.clickOnHelpIcon(DashletHelpIcon.DATA_LISTS);
        Assert.assertEquals(siteDataListsDashlet.getHelpBalloonMessage(), "This dashlet shows lists relevant to the site. Clicking a list opens it.",
                "No help balloon found");

        LOG.info("Step 3 - Click on the Data List title link from Site Data Lists dashlet.");
        siteDataListsDashlet.clickOnTheFirstDataListTitleLink();
        Assert.assertTrue(siteDataListsDashlet.isDataListPageTheCurrentPage(), "The current page is not Data List details page");

    }
}
