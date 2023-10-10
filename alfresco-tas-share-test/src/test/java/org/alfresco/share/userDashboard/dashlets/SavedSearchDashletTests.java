package org.alfresco.share.userDashboard.dashlets;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SavedSearchDashletTests extends AbstractUserDashboardDashletsTests
{
    private static final String EXPECTED_NO_RESULTS_FOUND_MESSAGE = "savedSearchDashlet.noResults";
    private static final String EXPECTED_FOLDER_PATH = "In folder: /Company Home/Shared";
    private static final String DIALOG_TITLE_INPUT_VALUE = "valid search";
    private static final String EXPECTED_BALLOON_MESSAGE = "savedSearchDashlet.balloonMessage";
    private static final String EXPECTED_DIALOG_CONFIG_TITLE = "savedSearchDashlet.config.title";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private SavedSearchDashlet savedSearchDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        savedSearchDashlet = new SavedSearchDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), DashboardCustomization.UserDashlet.SAVED_SEARCH, 1, 3);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2427")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void checkSavedSearchDashlet()
    {
        userDashboardPage.navigate(user.get());
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

    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void checkResultsWithRandomString()
    {
        userDashboardPage.navigate(user.get());
        savedSearchDashlet.configureDashlet()
            .setSearchTermField(RandomData.getRandomAlphanumeric())
            .setTitleField(RandomData.getRandomAlphanumeric())
            .clickOk();
        savedSearchDashlet
            .assertNoResultsFoundMessageEquals(language.translate(EXPECTED_NO_RESULTS_FOUND_MESSAGE));
    }

    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void checkValidSavedSearchResult()
    {
        FileModel searchFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingShared().createFile(searchFile).assertThat().existsInRepo();
        userDashboardPage.navigate(user.get());
        savedSearchDashlet.configureDashlet()
            .setTitleField(DIALOG_TITLE_INPUT_VALUE)
            .setSearchTermField(searchFile.getName())
            .clickOk();
        savedSearchDashlet.assertFileIsDisplayed(searchFile.getName())
            .assertInFolderPathEquals(searchFile.getName(), EXPECTED_FOLDER_PATH);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
    }
}
