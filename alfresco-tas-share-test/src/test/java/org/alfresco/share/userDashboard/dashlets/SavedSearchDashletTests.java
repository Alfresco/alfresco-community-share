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
    private static final String EXPECTED_NO_RESULTS_FOUND_MESSAGE = "savedSearchDashlet.noResults";
    private static final String EXPECTED_FOLDER_PATH = "In folder: /Company Home/Shared";
    private static final String DIALOG_TITLE_INPUT_VALUE = "valid search";
    private static final String EXPECTED_BALLOON_MESSAGE = "savedSearchDashlet.balloonMessage";
    private static final String EXPECTED_DIALOG_CONFIG_TITLE = "savedSearchDashlet.config.title";

    private UserModel user;

    @Autowired
    private SavedSearchDashlet savedSearchDashlet;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.SAVED_SEARCH, 1);
    }

    @TestRail (id = "C2427")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD }, priority = 1)
    public void checkSavedSearchDashlet()
    {
        savedSearchDashlet.assertDashletTitleEquals(language.translate("savedSearchDashlet.title"))
            .assertNoResultsFoundMessageEquals(language.translate(EXPECTED_NO_RESULTS_FOUND_MESSAGE))
            .assertConfigureDashletButtonIsDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.SAVED_SEARCH)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .configureDashlet()
                .assertDialogTitleEquals(language.translate(EXPECTED_DIALOG_CONFIG_TITLE))
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
        savedSearchDashlet.configureDashlet()
            .setSearchTermField(RandomData.getRandomAlphanumeric())
            .setTitleField(RandomData.getRandomAlphanumeric())
            .clickOk();
        savedSearchDashlet
            .assertNoResultsFoundMessageEquals(language.translate(EXPECTED_NO_RESULTS_FOUND_MESSAGE));
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.USER_DASHBOARD }, priority = 3)
    public void checkValidSavedSearchResult()
    {
        FileModel searchFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.authenticateUser(user).usingShared().createFile(searchFile).assertThat().existsInRepo();
        savedSearchDashlet.configureDashlet()
            .setTitleField(DIALOG_TITLE_INPUT_VALUE)
            .setSearchTermField(searchFile.getName())
            .clickOk();
        savedSearchDashlet.assertFileIsDisplayed(searchFile.getName())
            .assertInFolderPathEquals(searchFile.getName(), EXPECTED_FOLDER_PATH);
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(user);
    }
}
