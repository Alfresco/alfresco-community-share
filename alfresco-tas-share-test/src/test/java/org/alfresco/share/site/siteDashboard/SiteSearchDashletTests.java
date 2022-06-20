package org.alfresco.share.site.siteDashboard;

import static java.util.Arrays.asList;
import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.alfresco.utility.model.FileModel.getRandomFileModel;
import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SiteSearchDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "siteSearchDashlet.title";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteSearchDashlet.balloonMessage";
    private static final String RANDOM_ALPHANUMERIC = RandomData.getRandomAlphanumeric();
    private static final String EXPECTED_NO_RESULTS_FOUND_MESSAGE = "siteSearchDashlet.noResults";

    private static final String EXPECTED_10 = "10";
    private static final String EXPECTED_25 = "25";
    private static final String EXPECTED_50 = "50";
    private static final String EXPECTED_100 = "100";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SiteSearchDashlet siteSearchDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteSearchDashlet = new SiteSearchDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.SITE_SEARCH, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2775")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkDisplaySpecificMessageWhenNoSearchResultsReturned()
    {
        siteDashboardPage.navigate(site.get());
        siteSearchDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .clickOnHelpIcon(DashletHelpIcon.SITE_SEARCH)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();

        siteSearchDashlet
            .openSearchFilterDropdown()
            .assertDropdownValuesEqual(asList(EXPECTED_10, EXPECTED_25, EXPECTED_50, EXPECTED_100))
            .typeInSearch(RANDOM_ALPHANUMERIC)
            .clickSearchButton()
            .assertNoResultsFoundMessageEquals(language.translate(EXPECTED_NO_RESULTS_FOUND_MESSAGE));
    }

    @TestRail (id = "C2424")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplaySearchResult()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get()).usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteSearchDashlet
            .typeInSearch(fileModel.getName())
            .clickSearchButton()
            .assertReturnedSearchResultEquals(fileModel.getName());
    }

    @TestRail (id = "C588829")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayFileNameInDocumentDetailsPageWhenAccessedFromSiteSearchDashlet()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get()).usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteSearchDashlet
            .typeInSearch(fileModel.getName())
            .clickSearchButton()
            .clickFileLinkName(fileModel.getName())
                .assertDocumentTitleEquals(fileModel);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
