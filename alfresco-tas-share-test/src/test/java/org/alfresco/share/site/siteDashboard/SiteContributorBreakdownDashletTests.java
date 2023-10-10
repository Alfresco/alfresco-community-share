package org.alfresco.share.site.siteDashboard;

import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.alfresco.utility.model.FileModel.getRandomFileModel;
import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import java.util.Arrays;
import org.alfresco.po.share.dashlet.SiteContributorBreakdownDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class SiteContributorBreakdownDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String TODAY = "siteContributorBreakdown.dropdown.Today";
    private static final String LAST_7_DAYS = "siteContributorBreakdown.dropdown.Last7Days";
    private static final String LAST_30_DAYS = "siteContributorBreakdown.dropdown.Last30Days";
    private static final String PAST_YEAR = "siteContributorBreakdown.dropdown.PastYear";
    private static final String DATE_RANGE = "siteContributorBreakdown.dropdown.DateRange";
    private static final String EXPECTED_DASHLET_TITLE = "siteContributorBreakdown.dropdown.DashletTitle";
    private static final String EXPECTED_EMPTY_MESSAGE = "siteContributorBreakdown.DashletEmptyMessage";
    private static final int EXPECTED_PIE_CHART_SIZE = 1;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SiteContributorBreakdownDashlet siteContributorBreakdownDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteContributorBreakdownDashlet = new SiteContributorBreakdownDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.SITE_CONTRIB_BREAKDOWN, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C202732")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkSpecificMessageWhenSiteContributorBreakdownDashletIsEmpty()
    {
        siteDashboardPage.navigate(site.get());
        siteContributorBreakdownDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .assertDashletEmptyMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE))
            .openFilterDropDown()
            .assertDropdownFilterEquals(Arrays.asList(
                language.translate(TODAY),
                language.translate(LAST_7_DAYS),
                language.translate(LAST_30_DAYS),
                language.translate(PAST_YEAR),
                language.translate(DATE_RANGE)));
}

    @TestRail (id = "C202304")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayUserFullNameInUserProfilePageWhenClickedFromPieChart()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteContributorBreakdownDashlet
            .assertPieChartSizeEquals(EXPECTED_PIE_CHART_SIZE)
            .clickPieChartUsername()
                .assertUserProfilePageIsOpened();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
