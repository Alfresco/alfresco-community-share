package org.alfresco.share.site.siteDashboard;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.enums.ActivitiesDaysRangeFilter;
import org.alfresco.po.enums.ActivitiesFilter;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

public class SiteActivitiesTests extends AbstractSiteDashboardDashletsTests
{
    private SiteActivitiesDashlet siteActivitiesDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteActivitiesDashlet = new SiteActivitiesDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2803")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void checkSiteActivitiesDashletWithNoActivities()
    {
        siteDashboardPage.navigate(site.get());
        siteActivitiesDashlet
            .assertEmptyDashletMessageEquals()
            .assertRssFeedButtonIsDisplayed().assertDashletTitleEquals(language.translate("siteActivities.title"))
            .clickOnHelpIcon(DashletHelpIcon.MY_ACTIVITIES)
                .assertBalloonMessageIsDisplayed()
                .assertHelpBalloonMessageEquals(language.translate("siteActivities.help"))
                .closeHelpBalloon()
            .assertActivitiesFilterHasAllOptions()
            .assertSelectedActivityFilterContains(language.translate("activitiesDashlet.filter.everyone"))
            .assertItemsFilterHasAllOptions()
            .assertSelectedItemFilterContains(language.translate("activitiesDashlet.filter.allItems"))
            .assertHistoryFilterHasAllOptions()
            .assertSelectedHistoryOptionContains(language.translate("activitiesDashlet.filter.last7days"))
            .assertRssFeedContainsExpectedUrForSiteActivity(site.get());
    }

    @TestRail (id = "C2809")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void verifyDateRangeFilter()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file).assertThat().existsInRepo();

        siteDashboardPage.navigate(site.get());
        siteActivitiesDashlet
            .assertSelectedHistoryOptionContains(language.translate("activitiesDashlet.filter.last7days"))
            .assertAddDocumentActivityIsDisplayed(user.get(), file)
            .selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter.TODAY)
                .assertAddDocumentActivityIsDisplayed(user.get(), file)
            .selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter.FOURTEEN_DAYS)
                .assertAddDocumentActivityIsDisplayed(user.get(), file)
            .selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter.TWENTY_EIGHT_DAYS)
                .assertAddDocumentActivityIsDisplayed(user.get(), file);
    }

    @TestRail (id = "C12833, C12834")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void verifyGroupAddActivityIsDisplayed()
    {
        GroupModel groupToAdd = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.usingUser(user.get())
            .addGroupToSite(groupToAdd, site.get(), UserRole.SiteCollaborator);

        siteDashboardPage.navigate(site.get());
        siteActivitiesDashlet.assertAddGroupToSiteWithRoleActivityIsDisplayed(groupToAdd, site.get(), UserRole.SiteCollaborator);

        dataGroup.usingAdmin().deleteGroup(groupToAdd);
    }

    @TestRail (id = "C12835")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void verifyUpdateGroupRoleActivityIsDisplayed()
    {
        GroupModel groupToUpdate = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.usingUser(user.get()).addGroupToSite(groupToUpdate, site.get(), UserRole.SiteCollaborator);
        dataGroup.usingUser(user.get()).updateGroupRole(groupToUpdate, site.get(), UserRole.SiteManager);

        siteDashboardPage.navigate(site.get());
        siteActivitiesDashlet.assertUpdateGroupRoleActivityIsDisplayed(groupToUpdate, UserRole.SiteManager);

        dataGroup.usingAdmin().deleteGroup(groupToUpdate);
    }

    @TestRail (id = "C12838")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void verifyRemoveGroupFromSiteActivityIsDisplayed()
    {
        GroupModel groupToRemove = dataGroup.usingAdmin().createRandomGroup();
        getDataGroup().usingUser(user.get()).addGroupToSite(groupToRemove, site.get(), UserRole.SiteCollaborator);
        getDataGroup().usingUser(user.get()).removeGroupFromSite(groupToRemove, site.get());

        siteDashboardPage.navigate(site.get());
        siteActivitiesDashlet.assertRemoveGroupFromSiteActivityIsDisplayed(groupToRemove, site.get());

        dataGroup.usingAdmin().deleteGroup(groupToRemove);
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void verifyDocumentLinkFromAddActivity()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file).assertThat().existsInRepo();

        siteDashboardPage.navigate(site.get());
        siteActivitiesDashlet
            .assertAddDocumentActivityIsDisplayed(user.get(), file)
            .clickDocumentLinkForAddActivity(user.get(), file)
                .assertDocumentTitleEquals(file);
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void verifyUserFilter()
    {
        UserModel invitedUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(user.get()).addUserToSite(invitedUser, site.get(), UserRole.SiteCollaborator);

        FileModel managerFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel collaboratorFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get()).usingSite(site.get())
            .createFile(managerFile).assertThat().existsInRepo()
            .authenticateUser(invitedUser)
                .usingSite(site.get()).createFile(collaboratorFile).assertThat().existsInRepo();

        siteDashboardPage.navigate(site.get());
        siteActivitiesDashlet.selectActivityFilter(ActivitiesFilter.MY_ACTIVITIES)
            .assertAddDocumentActivityIsDisplayed(user.get(), managerFile)
            .assertAddDocumentActivityIsNotDisplayedForUser(invitedUser, collaboratorFile)
            .selectActivityFilter(ActivitiesFilter.EVERYONE_ELSE_ACTIVITIES)
                .assertAddDocumentActivityIsDisplayed(invitedUser, collaboratorFile)
                .assertAddDocumentActivityIsNotDisplayedForUser(user.get(), managerFile)
            .selectActivityFilter(ActivitiesFilter.EVERYONE_ACTIVITIES)
                .assertAddDocumentActivityIsDisplayed(user.get(), managerFile)
                .assertAddDocumentActivityIsDisplayed(invitedUser, collaboratorFile);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
