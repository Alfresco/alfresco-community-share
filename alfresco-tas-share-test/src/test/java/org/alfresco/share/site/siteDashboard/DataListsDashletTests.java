package org.alfresco.share.site.siteDashboard;

import static org.alfresco.utility.data.RandomData.getRandomAlphanumeric;

import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteDataListsDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author bogdan.simion
 */
public class DataListsDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_DASHLET_TITLE = "siteDataList.dashletTitle";
    private static final String LIST_ITEM_TITLE = String.format("C5569%s", getRandomAlphanumeric());
    private static final String LIST_ITEM_DESCRIPTION = String.format("C5569%s", getRandomAlphanumeric());
    private static final String EXPECTED_EMPTY_LIST_MESSAGE = "siteDataList.emptyListMessage";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "siteDataList.helpBalloonMessage";
    private static final String EXPECTED_DATA_LISTS_URL = "siteDataList.url";
    private static final int EXPECTED_NUMBER_OF_LISTS_ITEMS = 2;

    private static final String EXPECTED_DIALOG_TITLE = "siteDataList.dialogTitle";
    private static final String DIALOG_INPUT_TITLE = "siteDataList.dialogInputTitle";
    private static final String DIALOG_INPUT_DESCRIPTION = "siteDataList.dialogInputDescription";

    private static final String TO_DO_LIST_NAME = String.format("C5569%s", getRandomAlphanumeric());

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
        setupAuthenticatedSession(userModel);

        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        addDashlet(siteModel, Dashlets.SITE_DATA_LISTS, 1);
    }

    @TestRail (id = "C5568")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES}, priority = 1)
    public void shouldDisplaySpecificMessageWhenSiteDataListsIsEmpty()
    {
        siteDataListsDashlet
            .assertDashletHelpIconIsDisplayed(DashletHelpIcon.DATA_LISTS)
            .assertDashletTitleIs(language.translate(EXPECTED_DASHLET_TITLE))
            .assertCreateDataListLinkDisplayed()
            .assertDisplayedMessageIs(language.translate(EXPECTED_EMPTY_LIST_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.DATA_LISTS)
            .assertHelpBalloonMessageIs(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonIsNotDisplayed();
    }

    @TestRail (id = "C5569")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplaySiteDataListsDashletWhenTwoListsItemsAreCreated()
    {
        //Create two data lists with different data list types
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.EVENT_LIST, LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION);
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.TODO_LIST, TO_DO_LIST_NAME, LIST_ITEM_DESCRIPTION);

        siteDashboardPage.navigate(siteModel.getId());
        siteDataListsDashlet
            .assertDataListItemTitleIsDisplayed(LIST_ITEM_TITLE)
            .assertSiteDataListsItemsAreEqual(EXPECTED_NUMBER_OF_LISTS_ITEMS)
            .clickListItemByTitle(LIST_ITEM_TITLE);

        dataListsPage.assertDataListItemTitleEquals(LIST_ITEM_TITLE);
    }

    @TestRail (id = "C5570")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayInListsCreatedDataList()
    {
        siteDashboardPage.navigate(siteModel.getId());
        dataListsPage
            .clickOnCreateDataListLink()
            .assertNewListDialogTitleEquals(language.translate(EXPECTED_DIALOG_TITLE))
            .selectContactListFromTypesOfListsAvailable()
            .setTitle(language.translate(DIALOG_INPUT_TITLE))
            .assertDialogInputTitleEquals(language.translate(DIALOG_INPUT_TITLE))
            .setDescription(language.translate(DIALOG_INPUT_DESCRIPTION))
            .assertDialogInputDescriptionEquals(language.translate(DIALOG_INPUT_DESCRIPTION))
            .clickDialogSaveButton()
            .assertDataListLinkDescriptionEquals(language.translate(DIALOG_INPUT_TITLE));
    }

    @TestRail (id = "C5570")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayEmptyListsWhenCancelDataListCreation()
    {
        siteDashboardPage.navigate(siteModel.getId());
        dataListsPage
            .clickOnCreateDataListLink()
            .assertNewListDialogTitleEquals(language.translate(EXPECTED_DIALOG_TITLE))
            .selectContactListFromTypesOfListsAvailable()
            .setTitle(language.translate(DIALOG_INPUT_TITLE))
            .assertDialogInputTitleEquals(language.translate(DIALOG_INPUT_TITLE))
            .setDescription(language.translate(DIALOG_INPUT_DESCRIPTION))
            .assertDialogInputDescriptionEquals(language.translate(DIALOG_INPUT_DESCRIPTION))
            .clickDialogCancelButton()
            .assertDataListUrlContains(language.translate(EXPECTED_DATA_LISTS_URL));
    }

    // TODO: To be moved in permission package
    @TestRail (id = "C5571")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void verifySiteDataListsDashletUserWithConsumerRole()
    {
        //Create two data lists with different data list types
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.EVENT_LIST, LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION);
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.TODO_LIST, TO_DO_LIST_NAME, LIST_ITEM_DESCRIPTION);

        UserModel siteConsumer = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel).addUserToSite(siteConsumer, siteModel, UserRole.SiteConsumer);
        setupAuthenticatedSession(siteConsumer);

        //navigate to created site
        siteDashboardPage.navigate(siteModel.getId());

        siteDataListsDashlet
            .assertDashletHelpIconIsDisplayed(DashletHelpIcon.DATA_LISTS)
            .assertDataListItemTitleIsDisplayed(LIST_ITEM_TITLE)
            .assertDataListItemTitleEquals(LIST_ITEM_TITLE)
            .assertDataListItemDescriptionEquals(LIST_ITEM_DESCRIPTION)
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .clickOnHelpIcon(DashletHelpIcon.DATA_LISTS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .clickListItemByTitle(LIST_ITEM_TITLE);

        dataListsPage.assertDataListUrlContains(language.translate(EXPECTED_DATA_LISTS_URL));
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTest()
    {
        deleteSites(siteModel);
        removeUserFromAlfresco(userModel);
    }
}
