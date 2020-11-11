package org.alfresco.share.site.siteDashboard;

import static org.alfresco.utility.model.FileModel.getRandomFileModel;

import java.util.Arrays;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteContributorBreakdownDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 3/10/2017.
 */
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

    private UserModel userModel;
    private SiteModel siteModel;
    private FileModel fileModel;

    @Autowired
    private SiteContributorBreakdownDashlet siteContributorBreakdownDashlet;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(userModel);
        cmisApi.authenticateUser(userModel);

        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        addDashlet(siteModel, Dashlets.SITE_CONTRIBUTOR_BREAKDOWN, 1);
    }

    @TestRail (id = "C202732")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void checkSpecificMessageWhenSiteContributorBreakdownDashletIsEmpty()
    {
        siteDashboardPage.navigate(siteModel);
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
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayUserFullNameInUserProfilePageWhenClickedFromPieChart()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
        siteContributorBreakdownDashlet
            .assertPieChartSizeEquals(EXPECTED_PIE_CHART_SIZE)
            .clickPieChartUsername()
            .assertUsernameEquals(userModel.getFirstName(), userModel.getLastName());
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
