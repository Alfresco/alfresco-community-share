package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SavedSearchDashletTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private SavedSearchDashlet savedSearchDashlet;

    private UserModel user;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.SAVED_SEARCH, 1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2427")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD }, priority = 1)
    public void checkSavedSearchDashlet()
    {
        savedSearchDashlet.assertDashletTitleEquals(language.translate("savedSearchDashlet.title"))
            .assertNoResultsMessageIsDisplayed()
            .assertConfigureDashletButtonIsDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.SAVED_SEARCH)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("savedSearchDashlet.balloonMessage"))
            .closeHelpBalloon()
            .clickConfigureDashlet()
                .assertDialogTitleEqualsWithExpected(language.translate("savedSearchDashlet.config.title"))
                .assertSearchTermFieldIsDisplayed()
                .assertTitleFieldIsDisplayed()
                .assertSearchLimitIsDisplayed()
                .assertOKButtonIsDisplayed()
                .assertCancelButtonIsDisplayed()
                .assertCloseButtonIsDisplayed()
                .assertAllLimitValuesAreDisplayed()
                .clickClose();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.USER_DASHBOARD }, priority = 2)
    public void checkResultsWithRandomString()
    {
        savedSearchDashlet.clickConfigureDashlet()
            .setSearchTermField(RandomData.getRandomAlphanumeric())
            .setTitleField(RandomData.getRandomAlphanumeric())
            .clickOk();
        savedSearchDashlet.assertNoResultsMessageIsDisplayed();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.USER_DASHBOARD }, priority = 3)
    public void checkValidSavedSearchResult()
    {
        FileModel searchFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.authenticateUser(user).usingShared().createFile(searchFile).assertThat().existsInRepo();
        savedSearchDashlet.clickConfigureDashlet()
            .setTitleField("valid search")
            .setSearchTermField(searchFile.getName())
            .clickOk();
        savedSearchDashlet.assertFileIsDisplayed(searchFile)
            .assertInFolderPathIs(searchFile.getName(), "/Company Home/Shared");
    }
}
