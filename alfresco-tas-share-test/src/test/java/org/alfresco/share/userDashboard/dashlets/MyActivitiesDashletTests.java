package org.alfresco.share.userDashboard.dashlets;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.enums.ActivitiesFilter;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MyActivitiesDashletTests extends AbstractUserDashboardDashletsTests
{
    private MyActivitiesDashlet myActivitiesDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> testSite = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        myActivitiesDashlet = new MyActivitiesDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        testSite.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2111")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkActivitiesDashletWithNoActivities()
    {
        userDashboardPage.navigate(user.get());
        myActivitiesDashlet
            .assertEmptyDashletMessageEquals()
            .assertRssFeedButtonIsDisplayed().assertDashletTitleEquals(language.translate("myActivitiesDashlet.title"))
            .clickOnHelpIcon(DashletHelpIcon.MY_ACTIVITIES)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("myActivitiesDashlet.helpMessage"))
            .closeHelpBalloon()
                .assertActivitiesFilterHasAllOptions()
                .assertSelectedActivityFilterContains(language.translate("activitiesDashlet.filter.everyone"))
                .assertItemsFilterHasAllOptions()
                .assertSelectedItemFilterContains(language.translate("activitiesDashlet.filter.allItems"))
                .assertHistoryFilterHasAllOptions()
                .assertSelectedHistoryOptionContains(language.translate("activitiesDashlet.filter.last7days"));
    }

    @TestRail (id = "C2112")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkCreateDocumentAndFolderActivity()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get()).usingSite(testSite.get())
            .createFolder(testFolder).assertThat().existsInRepo()
            .createFile(testFile).assertThat().existsInRepo();

        userDashboardPage.navigate(user.get());
        myActivitiesDashlet.assertAddDocumentActivityIsDisplayed(user.get(), testFile, testSite.get())
            .assertAddedFolderActivityIsDisplayed(user.get(), testFolder, testSite.get())
            .clickUserFromAddedDocumentActivity(user.get(), testFile, testSite.get())
                .assertUserProfilePageIsOpened();
    }

    @TestRail (id = "C2113")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkUpdateDocumentActivity()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get()).usingSite(testSite.get())
            .createFile(testFile).update("new content");
        userDashboardPage.navigate(user.get());
        myActivitiesDashlet.assertAddDocumentActivityIsDisplayed(user.get(), testFile, testSite.get())
            .assertUpdateDocumentActivityIsDisplayed(user.get(), testFile, testSite.get());
    }

    @TestRail (id = "C2114")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkDeleteDocumentActivity()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get()).usingSite(testSite.get())
            .createFile(testFile).assertThat().existsInRepo()
            .createFolder(testFolder).assertThat().existsInRepo()
                .usingResource(testFile).delete().assertThat().doesNotExistInRepo()
                .usingResource(testFolder).deleteFolderTree().assertThat().doesNotExistInRepo();
        userDashboardPage.navigate(user.get());
        myActivitiesDashlet.assertDeleteDocumentActivityIsDisplayed(user.get(), testFile, testSite.get())
            .assertDeletedFolderActivityIsDisplayed(user.get(), testFolder, testSite.get());
    }

    @TestRail (id = "C2117")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkUsersFilter()
    {
        UserModel invitedUser = getDataUser().usingAdmin().createRandomTestUser();
        getDataUser().usingUser(user.get()).addUserToSite(invitedUser, testSite.get(), UserRole.SiteCollaborator);

        FileModel managerFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel collaboratorFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get()).usingSite(testSite.get())
            .createFile(managerFile).assertThat().existsInRepo()
            .authenticateUser(invitedUser)
                .usingSite(testSite.get()).createFile(collaboratorFile).assertThat().existsInRepo();

        userDashboardPage.navigate(user.get());
        myActivitiesDashlet.selectActivityFilter(ActivitiesFilter.MY_ACTIVITIES)
            .assertAddDocumentActivityIsDisplayed(user.get(), managerFile, testSite.get())
            .assertAddDocumentActivityIsNotDisplayedForUser(invitedUser, collaboratorFile, testSite.get())
                .selectActivityFilter(ActivitiesFilter.EVERYONE_ELSE_ACTIVITIES)
                    .assertAddDocumentActivityIsDisplayed(invitedUser, collaboratorFile, testSite.get())
                    .assertAddDocumentActivityIsNotDisplayedForUser(user.get(), managerFile, testSite.get())
                .selectActivityFilter(ActivitiesFilter.EVERYONE_ACTIVITIES)
                    .assertAddDocumentActivityIsDisplayed(user.get(), managerFile, testSite.get())
                    .assertAddDocumentActivityIsDisplayed(invitedUser, collaboratorFile, testSite.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        dataSite.usingAdmin().deleteSite(testSite.get());
    }
}
