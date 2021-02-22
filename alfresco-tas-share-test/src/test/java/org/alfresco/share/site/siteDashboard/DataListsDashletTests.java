package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import static org.alfresco.utility.data.RandomData.getRandomAlphanumeric;

import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.enums.DataListTypes;
import org.alfresco.po.share.dashlet.SiteDataListsDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataListsDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_EMPTY_DATA_LIST_MESSAGE = "dataListPage.noListSelected.message";
    private final String EXPECTED_DASHLET_TITLE = "siteDataList.dashletTitle";
    private final String LIST_ITEM_TITLE = String.format("C5569%s", getRandomAlphanumeric());
    private final String LIST_ITEM_DESCRIPTION = String.format("C5569%s", getRandomAlphanumeric());
    private final String EXPECTED_EMPTY_LIST_MESSAGE = "siteDataList.emptyListMessage";
    private final String EXPECTED_HELP_BALLOON_MESSAGE = "siteDataList.helpBalloonMessage";;
    private final String TO_DO_LIST_NAME = String.format("C5569%s", getRandomAlphanumeric());

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private ThreadLocal<DataListsService> dataListsService = new ThreadLocal<>();

    public SiteDataListsDashlet siteDataListsDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteDataListsDashlet = new SiteDataListsDashlet(webDriver);
        dataListsService.set(applicationContext.getBean(DataListsService.class));

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.SITE_DATA_LIST, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5568")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkDisplaySpecificMessageWhenSiteDataListsIsEmpty()
    {
        siteDashboardPage.navigate(site.get());
        siteDataListsDashlet
            .assertDashletHelpIconIsDisplayed(DashletHelpIcon.DATA_LISTS)
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .assertCreateDataListLinkDisplayed()
            .assertEmptyListMessageEquals(language.translate(EXPECTED_EMPTY_LIST_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.DATA_LISTS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5569")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplaySiteDataListsDashletWhenTwoListsItemsAreCreated()
    {
        dataListsService.get().createDataList(user.get().getUsername(), user.get().getPassword(),
            site.get().getId(), DataList.EVENT_LIST, LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION);
        dataListsService.get().createDataList(user.get().getUsername(), user.get().getPassword(),
            site.get().getId(), DataList.TODO_LIST, TO_DO_LIST_NAME, LIST_ITEM_DESCRIPTION);

        siteDashboardPage.navigate(site.get());
        siteDataListsDashlet
            .assertDataListItemTitleIsDisplayed(LIST_ITEM_TITLE)
            .assertDataListItemTitleIsDisplayed(TO_DO_LIST_NAME)
            .clickListItemByTitle(LIST_ITEM_TITLE)
                .assertDataListPageIsOpened()
                .assertDataListItemTitleEquals(LIST_ITEM_TITLE);
    }

    @TestRail (id = "C5570")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayInListsCreatedDataList()
    {
        String dataListTitle = getRandomAlphanumeric();
        String dataListDescription = getRandomAlphanumeric();
        siteDashboardPage.navigate(site.get());
        siteDataListsDashlet.clickOnCreateDataListLink()
            .selectType(DataListTypes.CONTACT_LIST.title)
            .typeTitle(dataListTitle)
            .typeDescription(dataListDescription)
            .clickSaveButton();
        siteDashboardPage.navigate(site.get());

        siteDataListsDashlet
            .assertDataListItemTitleIsDisplayed(dataListTitle)
            .assertDataListItemDescriptionEquals(dataListTitle, dataListDescription);
    }

    @TestRail (id = "C5570")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayEmptyListsWhenCancelDataListCreation()
    {
        siteDashboardPage.navigate(site.get());
        siteDataListsDashlet.clickOnCreateDataListLink()
            .clickCancelButton()
                .assertEmptyListMessageEquals(language.translate(EXPECTED_EMPTY_DATA_LIST_MESSAGE));
    }

    @TestRail (id = "C5571")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void verifySiteDataListsDashletUserWithConsumerRole()
    {
        dataListsService.get().createDataList(user.get().getUsername(), user.get().getPassword(),
            site.get().getId(), DataList.EVENT_LIST, LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION);

        UserModel siteConsumer = dataUser.usingAdmin().createRandomTestUser();
        getDataUser().usingUser(user.get()).addUserToSite(siteConsumer, site.get(), UserRole.SiteConsumer);
        authenticateUsingCookies(siteConsumer);

        siteDashboardPage.navigate(site.get());
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

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
