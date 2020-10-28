package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.AbstractActivitiesDashlet.ActivitiesDaysRangeFilter;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SiteActivitiesTests extends AbstractSiteDashboardDashletsTests
{
    @Autowired
    private SiteActivitiesDashlet siteActivitiesDashlet;

    private UserModel testUser;
    private SiteModel testSite;

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        testUser = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        cmisApi.authenticateUser(testUser);
        setupAuthenticatedSession(testUser);
    }

    @TestRail (id = "C2803")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void checkSiteActivitiesDashletWithNoActivities()
    {
        siteDashboardPage.navigate(testSite);
        siteActivitiesDashlet
            .assertEmptyDashletMessageEquals()
            .assertRssFeedButtonIsDisplayed().assertDashletTitleEquals(language.translate("siteActivities.title"))
            .clickOnHelpIcon(Dashlet.DashletHelpIcon.MY_ACTIVITIES)
                .assertBalloonMessageIsDisplayed()
                .assertHelpBalloonMessageEquals(language.translate("siteActivities.help"))
                .closeHelpBalloon()
            .assertActivitiesFilterHasAllOptions()
            .assertSelectedActivityFilterContains(language.translate("activitiesDashlet.filter.everyone"))
            .assertItemsFilterHasAllOptions()
            .assertSelectedItemFilterContains(language.translate("activitiesDashlet.filter.allItems"))
            .assertHistoryFilterHasAllOptions()
            .assertSelectedHistoryOptionContains(language.translate("activitiesDashlet.filter.last7days"));
    }

    @TestRail (id = "C2809")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyDateRangeFilter()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.usingSite(testSite).createFile(file).assertThat().existsInRepo();

        siteDashboardPage.navigate(testSite);
        siteActivitiesDashlet
            .assertSelectedHistoryOptionContains(language.translate("activitiesDashlet.filter.last7days"))
            .assertAddDocumentActivityIsDisplayed(testUser, file)
            .selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter.TODAY)
                .assertAddDocumentActivityIsDisplayed(testUser, file)
            .selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter.FOURTEEN_DAYS)
                .assertAddDocumentActivityIsDisplayed(testUser, file)
            .selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter.TWENTY_EIGHT_DAYS)
                .assertAddDocumentActivityIsDisplayed(testUser, file);
    }

    @TestRail (id = "C12833, C12834")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyGroupAddActivityIsDisplayed()
    {
        GroupModel groupToAdd = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.usingUser(testUser).addGroupToSite(groupToAdd, testSite, UserRole.SiteCollaborator);

        siteDashboardPage.navigate(testSite);
        siteActivitiesDashlet.assertAddGroupToSiteWithRoleActivityIsDisplayed(groupToAdd, testSite, UserRole.SiteCollaborator);

        dataGroup.usingAdmin().deleteGroup(groupToAdd);
    }

    @TestRail (id = "C12835")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyUpdateGroupRoleActivityIsDisplayed()
    {
        GroupModel groupToUpdate = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.usingUser(testUser).addGroupToSite(groupToUpdate, testSite, UserRole.SiteCollaborator);
        dataGroup.usingUser(testUser).updateGroupRole(groupToUpdate, testSite, UserRole.SiteManager);

        siteDashboardPage.navigate(testSite);
        siteActivitiesDashlet.assertUpdateGroupRoleActivityIsDisplayed(groupToUpdate, UserRole.SiteManager);

        dataGroup.usingAdmin().deleteGroup(groupToUpdate);
    }

    @TestRail (id = "C12838")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyRemoveGroupFromSiteActivityIsDisplayed()
    {
        GroupModel groupToRemove = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.usingUser(testUser).addGroupToSite(groupToRemove, testSite, UserRole.SiteCollaborator);
        dataGroup.usingUser(testUser).removeGroupFromSite(groupToRemove, testSite);

        siteDashboardPage.navigate(testSite);
        siteActivitiesDashlet.assertRemoveGroupFromSiteActivityIsDisplayed(groupToRemove, testSite);

        dataGroup.usingAdmin().deleteGroup(groupToRemove);
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyDocumentLinkFromAddActivity()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.usingSite(testSite).createFile(file).assertThat().existsInRepo();

        siteDashboardPage.navigate(testSite);
        siteActivitiesDashlet
            .assertAddDocumentActivityIsDisplayed(testUser, file)
            .clickDocumentLinkForAddActivity(testUser, file)
                .assertDocumentDetailsPageIsOpened()
                .assertDocumentTitleEquals(file);
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyRSSFeedSiteActivitiesDashlet()
    {
        siteDashboardPage.navigate(testSite);
        siteActivitiesDashlet.assertRssFeedContainsExpectedUrForSiteActivity(testSite);
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyUserFilter()
    {
        UserModel invitedUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(testUser).addUserToSite(invitedUser, testSite, UserRole.SiteCollaborator);

        FileModel managerFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel collaboratorFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.authenticateUser(testUser).usingSite(testSite)
            .createFile(managerFile).assertThat().existsInRepo()
            .authenticateUser(invitedUser)
                .usingSite(testSite).createFile(collaboratorFile).assertThat().existsInRepo();

        siteDashboardPage.navigate(testSite);
        siteActivitiesDashlet.selectActivityFilter(MyActivitiesDashlet.ActivitiesFilter.MY_ACTIVITIES)
            .assertAddDocumentActivityIsDisplayed(testUser, managerFile)
            .assertAddDocumentActivityIsNotDisplayedForUser(invitedUser, collaboratorFile)
            .selectActivityFilter(MyActivitiesDashlet.ActivitiesFilter.EVERYONE_ELSE_ACTIVITIES)
                .assertAddDocumentActivityIsDisplayed(invitedUser, collaboratorFile)
                .assertAddDocumentActivityIsNotDisplayedForUser(testUser, managerFile)
            .selectActivityFilter(MyActivitiesDashlet.ActivitiesFilter.EVERYONE_ACTIVITIES)
                .assertAddDocumentActivityIsDisplayed(testUser, managerFile)
                .assertAddDocumentActivityIsDisplayed(invitedUser, collaboratorFile);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(testUser);
        deleteSites(testSite);
    }
}
