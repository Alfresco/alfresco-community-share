package org.alfresco.share.site.siteDashboard;

import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.alfresco.utility.model.FileModel.getRandomFileModel;
import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SavedSearchDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_DASHLET_TITLE = "savedSearchDashlet.title";
    private static final String EXPECTED_NO_RESULTS_FOUND_MESSAGE = "savedSearchDashlet.noResults";
    private static final String EXPECTED_SEARCH_BALLOON_MESSAGE = "savedSearchDashlet.balloonMessage";
    private static final String EXPECTED_FOLDER_LABEL = "savedSearchDashlet.item.inFolderPath";
    private static final String DIALOG_TITLE_INPUT_VALUE = "valid search";
    private static final String EXPECTED_DIALOG_TITLE = "savedSearchDashlet.config.title";
    private static final String FOLDER_LINK_PATH = "/";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SavedSearchDashlet savedSearchDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        savedSearchDashlet = new SavedSearchDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.SAVED_SEARCH, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2787")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkNotDisplayResultsWhenDashletConfigurationIsCancelled()
    {
        siteDashboardPage.navigate(site.get());
        savedSearchDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE));
        savedSearchDashlet
            .configureDashlet()
            .assertDialogTitleEquals(language.translate(EXPECTED_DIALOG_TITLE))
            .setTitleField(RandomData.getRandomAlphanumeric())
            .setSearchTermField(RandomData.getRandomAlphanumeric())
            .clickCancelButton();
        savedSearchDashlet
            .assertNoResultsFoundMessageEquals(language.translate(EXPECTED_NO_RESULTS_FOUND_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.SAVED_SEARCH)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_SEARCH_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C588500")
    @Test(groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplaySearchResultsWhenDashletConfigurationIsSaved()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi()
            .authenticateUser(user.get())
            .usingSite(site.get())
            .createFile(fileModel)
            .assertThat()
            .existsInRepo();

        siteDashboardPage.navigate(site.get());
        savedSearchDashlet
            .configureDashlet()
            .setTitleField(DIALOG_TITLE_INPUT_VALUE)
            .setSearchTermField(fileModel.getName())
            .clickOk();
        savedSearchDashlet
            .assertFileIsDisplayed(fileModel.getName())
            .assertInFolderPathEquals(fileModel.getName(),
                String.format(language.translate(EXPECTED_FOLDER_LABEL), FOLDER_LINK_PATH));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
