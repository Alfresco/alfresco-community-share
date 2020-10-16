package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteDataListsDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author bogdan.simion
 */
public class DataListsDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "Site Data Lists";
    private static final String EXPECTED_EMPTY_MESSAGE = "No lists to display";
    private static final String EXPECTED_BALLOON_MESSAGE = "This dashlet shows lists relevant to the site. Clicking a list opens it.";
    private static final String DIALOG_TITLE = "New List";
    private static final int EXPECTED_NUMBER_OF_LISTS_ITEMS = 2;
    private static final String LIST_ITEM_TITLE = String.format("C5569%s", RandomData.getRandomAlphanumeric());;
    private static final String todoListName = String.format("C5569%s", RandomData.getRandomAlphanumeric());
    private static final String description = "C5569Test";
    private static final String DIALOG_INPUT_TITLE = "Contact List";
    private static final String DIALOG_INPUT_DESCRIPTION = "Contacts";

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    public SiteDataListsDashlet siteDataListsDashlet;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Autowired
    private DataListsPage dataListsPage;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
    }

    @BeforeMethod
    public void beforeTest()
    {
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
    }

    @TestRail (id = "C5568")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplaySpecificMessageWhenSiteDataListsIsEmpty()
    {
        //Set up an authentication session and add a dashlet
        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_DATA_LISTS, 1);

        siteDataListsDashlet
            .assertDashletHelpIconIsDisplayed(DashletHelpIcon.DATA_LISTS)
            .assertDashletTitleIs(EXPECTED_TITLE)
            .assertCreateDataListLinkDisplayed()
            .assertDisplayedMessageIs(EXPECTED_EMPTY_MESSAGE);

        siteDataListsDashlet.clickOnHelpIcon(DashletHelpIcon.DATA_LISTS)
            .assertHelpBalloonMessageIs(EXPECTED_BALLOON_MESSAGE)
            .closeHelpBalloon()
            .assertBalloonIsNotDisplayed();
    }

    @TestRail (id = "C5569")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplaySiteDataListsDashletWhenTwoListsItemsAreCreated()
    {
        //Create two data lists with different data list types
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.EVENT_LIST, LIST_ITEM_TITLE, description);
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.TODO_LIST, todoListName, description);

        //Set up an authentication session in order above items to be visible when browser is opened
        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_DATA_LISTS, 1);

        siteDataListsDashlet.assertSiteDataListsItemsAreEqual(EXPECTED_NUMBER_OF_LISTS_ITEMS);
        siteDataListsDashlet.clickListItemByTitle(LIST_ITEM_TITLE);
        dataListsPage.assertDataListItemTitleEquals(LIST_ITEM_TITLE);
    }

    @TestRail (id = "C5570")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayInListsCreatedDataList()
    {
        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_DATA_LISTS, 1);

        dataListsPage
            .clickOnCreateDataListLink()
            .assertNewListDialogTitleEquals(DIALOG_TITLE)
            .selectContactListFromTypesOfListsAvailable()
            .setTitle(DIALOG_INPUT_TITLE)
            .assertDialogInputTitleEquals(DIALOG_INPUT_TITLE)
            .setDescription(DIALOG_INPUT_DESCRIPTION)
            .assertDialogInputDescriptionEquals(DIALOG_INPUT_DESCRIPTION)
            .clickDialogSaveButton()
            .assertDataListLinkDescriptionEquals(DIALOG_INPUT_TITLE);
    }

    @TestRail (id = "C5570")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayEmptyListsWhenCancelDataListCreation()
    {
        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_DATA_LISTS, 1);

        dataListsPage
            .clickOnCreateDataListLink()
            .assertNewListDialogTitleEquals(DIALOG_TITLE)
            .selectContactListFromTypesOfListsAvailable()
            .setTitle(DIALOG_INPUT_TITLE)
            .assertDialogInputTitleEquals(DIALOG_INPUT_TITLE)
            .setDescription(DIALOG_INPUT_DESCRIPTION)
            .assertDialogInputDescriptionEquals(DIALOG_INPUT_DESCRIPTION)
            .clickDialogCancelButton()
            .assertDataListUrlContains("data-lists");
    }

    @TestRail (id = "C5571")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed"  })
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
        userService.delete(adminUser, adminPassword, userNameSiteManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userNameSiteManager);
        userService.delete(adminUser, adminPassword, userNameSiteConsumer);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userNameSiteConsumer);
        siteService.delete(adminUser, adminPassword, siteName);

    }



    @AfterMethod
    public void afterTest()
    {
        deleteSites(siteModel);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
    }
}
