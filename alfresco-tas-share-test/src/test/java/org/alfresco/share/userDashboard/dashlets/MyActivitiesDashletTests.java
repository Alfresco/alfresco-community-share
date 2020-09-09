package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyActivitiesDashletTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private MyActivitiesDashlet myActivitiesDashlet;

    private UserModel user, invitedUser;
    private SiteModel testSite;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        invitedUser = dataUser.createRandomTestUser();
        testSite = dataSite.usingUser(user).createPublicRandomSite();
        setupAuthenticatedSession(user);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        dataSite.usingAdmin().deleteSite(testSite);
    }

    @TestRail (id = "C2111")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD }, priority = 1)
    public void checkActivitiesDashletWithNoActivities()
    {
        myActivitiesDashlet.assertDashletTitleIs(language.translate("myActivitiesDashlet.title"))
            .assertEmptyDashletMessageIsCorrect()
            .assertRssFeedButtonIsDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.MY_ACTIVITIES)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageIs(language.translate("myActivitiesDashlet.helpMessage"))
            .closeHelpBalloon()
                .assertActivitiesFilterHasAllOptions()
                .assertSelectedActivityFilterIs(language.translate("activitiesDashlet.filter.everyone"))
                .assertItemsFilterHasAllOptions()
                .assertSelectedItemFilterIs(language.translate("activitiesDashlet.filter.allItems"))
                .assertHistoryFilterHasAllOptions()
                .assertSelectedHistoryOptionIs(language.translate("activitiesDashlet.filter.last7days"));
    }

    @TestRail (id = "C2112")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD }, priority = 2)
    public void checkCreateDocumentAndFolderActivity()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        cmisApi.authenticateUser(user).usingSite(testSite)
            .createFolder(testFolder).assertThat().existsInRepo()
            .createFile(testFile).assertThat().existsInRepo();
        userDashboard.navigate(user);
        myActivitiesDashlet.assertAddDocumentActivityIsDisplayed(user, testFile, testSite)
            .assertAddedFolderActivityIsDisplayed(user, testFolder, testSite)
            .clickUserFromAddedDocumentActivity(user, testFile, testSite)
                .assertUserProfilePageIsOpened();
        userDashboard.navigate(user);
        myActivitiesDashlet.clickDocumentLinkForAddActivity(user, testFile, testSite)
            .assertDocumentDetailsPageIsOpened()
            .assertDocumentDetailsPageIsOpenedForFile(testFile);
        userDashboard.navigate(user);
        myActivitiesDashlet.assertPreviewedDocumentActivityIsDisplayed(user, testFile, testSite);
    }


    @TestRail (id = "C2113")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD }, priority = 3)
    public void checkUpdateDocumentActivity()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.authenticateUser(user).usingSite(testSite)
            .createFile(testFile).update("new content");
        userDashboard.navigate(user);
        myActivitiesDashlet.assertAddDocumentActivityIsDisplayed(user, testFile, testSite)
            .assertUpdateDocumentActivityIsDisplayed(user, testFile, testSite);
    }

    @TestRail (id = "C2114")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD }, priority = 4)
    public void checkDeleteDocumentActivity()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        cmisApi.authenticateUser(user).usingSite(testSite)
            .createFile(testFile).assertThat().existsInRepo()
            .createFolder(testFolder).assertThat().existsInRepo()
                .usingResource(testFile).delete().assertThat().doesNotExistInRepo()
                .usingResource(testFolder).deleteFolderTree().assertThat().doesNotExistInRepo();
        myActivitiesDashlet.assertDeleteDocumentActivityIsDisplayed(user, testFile, testSite)
            .assertDeletedFolderActivityIsDisplayed(user, testFolder, testSite);
    }

    @TestRail (id = "C2117")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD }, priority = 5)
    public void checkUsersFilter()
    {
        invitedUser.setUserRole(UserRole.SiteCollaborator);
        restApi.authenticateUser(user).withCoreAPI()
            .usingSite(testSite).addPerson(invitedUser);
        FileModel managerFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel collaboratorFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.authenticateUser(user).usingSite(testSite)
            .createFile(managerFile).assertThat().existsInRepo()
            .authenticateUser(invitedUser)
                .usingSite(testSite).createFile(collaboratorFile).assertThat().existsInRepo();

        myActivitiesDashlet.selectActivityFilter(MyActivitiesDashlet.ActivitiesFilter.MY_ACTIVITIES)
            .assertAddDocumentActivityIsDisplayed(user, managerFile, testSite)
            .assertAddDocumentActivityIsNotDisplayedFor(invitedUser, collaboratorFile, testSite)
                .selectActivityFilter(MyActivitiesDashlet.ActivitiesFilter.EVERYONE_ELSE_ACTIVITIES)
                    .assertAddDocumentActivityIsDisplayed(invitedUser, collaboratorFile, testSite)
                    .assertAddDocumentActivityIsNotDisplayedFor(user, managerFile, testSite)
                .selectActivityFilter(MyActivitiesDashlet.ActivitiesFilter.EVERYONE_ACTIVITIES)
                    .assertAddDocumentActivityIsDisplayed(user, managerFile, testSite)
                    .assertAddDocumentActivityIsDisplayed(invitedUser, collaboratorFile, testSite);
    }
}
