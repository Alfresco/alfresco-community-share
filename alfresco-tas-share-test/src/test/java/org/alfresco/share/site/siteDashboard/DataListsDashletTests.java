package org.alfresco.share.site.siteDashboard;

import static org.alfresco.utility.data.RandomData.getRandomAlphanumeric;

import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteDataListsDashlet;
import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DataListsDashletTests extends AbstractSiteDashboardDashletsTests
{
    private final String EXPECTED_DASHLET_TITLE = "siteDataList.dashletTitle";
    private final String LIST_ITEM_TITLE = String.format("C5569%s", getRandomAlphanumeric());
    private final String LIST_ITEM_DESCRIPTION = String.format("C5569%s", getRandomAlphanumeric());
    private final String EXPECTED_EMPTY_LIST_MESSAGE = "siteDataList.emptyListMessage";
    private final String EXPECTED_HELP_BALLOON_MESSAGE = "siteDataList.helpBalloonMessage";;
    private final String TO_DO_LIST_NAME = String.format("C5569%s", getRandomAlphanumeric());

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    public SiteDataListsDashlet siteDataListsDashlet;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_DATA_LISTS, 1);
    }

    @TestRail (id = "C5568")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void checkDisplaySpecificMessageWhenSiteDataListsIsEmpty()
    {
        siteDataListsDashlet
            .assertDashletHelpIconIsDisplayed(DashletHelpIcon.DATA_LISTS)
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .assertCreateDataListLinkDisplayed()
            .assertDisplayedMessageIs(language.translate(EXPECTED_EMPTY_LIST_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.DATA_LISTS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5569")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void shouldDisplaySiteDataListsDashletWhenTwoListsItemsAreCreated()
    {
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.EVENT_LIST, LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION);
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.TODO_LIST, TO_DO_LIST_NAME, LIST_ITEM_DESCRIPTION);

        siteDashboardPage.navigate(siteModel);
        siteDataListsDashlet
            .assertDataListItemTitleIsDisplayed(LIST_ITEM_TITLE)
            .assertDataListItemTitleIsDisplayed(TO_DO_LIST_NAME)
            .clickListItemByTitle(LIST_ITEM_TITLE)
            .assertDataListPageIsOpened()
            .assertDataListItemTitleEquals(LIST_ITEM_TITLE);
    }

    @TestRail (id = "C5570")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayInListsCreatedDataList()
    {
        String dataListTitle = getRandomAlphanumeric();
        String dataListDescription = getRandomAlphanumeric();
        siteDashboardPage.navigate(siteModel);
        siteDataListsDashlet.clickOnCreateDataListLink()
            .selectType(CreateDataListDialog.DataListTypes.ContactList.title)
            .typeTitle(dataListTitle)
            .typeDescription(dataListDescription)
            .clickSaveButton();
        siteDashboardPage.navigate(siteModel);

        siteDataListsDashlet
            .assertDataListItemTitleIsDisplayed(dataListTitle)
            .assertDataListItemDescriptionEquals(dataListTitle, dataListDescription);
    }

    @TestRail (id = "C5570")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayEmptyListsWhenCancelDataListCreation()
    {
        siteDashboardPage.navigate(siteModel);
        siteDataListsDashlet.clickOnCreateDataListLink()
            .clickCancelButton()
            .assertNoDataListSelectedMessageIsDisplayed();
    }

    // TODO: To be moved in permission package
    @TestRail (id = "C5571")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void verifySiteDataListsDashletUserWithConsumerRole()
    {
        dataListsService.createDataList(userModel.getUsername(), userModel.getPassword(),
            siteModel.getId(), DataList.EVENT_LIST, LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION);

        UserModel siteConsumer = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel).addUserToSite(siteConsumer, siteModel, UserRole.SiteConsumer);
        setupAuthenticatedSession(siteConsumer);

        siteDashboardPage.navigate(siteModel.getId());
        siteDataListsDashlet
            .assertDashletHelpIconIsDisplayed(DashletHelpIcon.DATA_LISTS)
            .assertDataListItemTitleIsDisplayed(LIST_ITEM_TITLE)
            .assertDataListItemDescriptionEquals(LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION)
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .clickOnHelpIcon(DashletHelpIcon.DATA_LISTS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .clickListItemByTitle(LIST_ITEM_TITLE)
            .assertDataListPageIsOpened()
            .assertDataListItemTitleEquals(LIST_ITEM_TITLE);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTest()
    {
        deleteSites(siteModel);
        removeUserFromAlfresco(userModel);
    }
}
