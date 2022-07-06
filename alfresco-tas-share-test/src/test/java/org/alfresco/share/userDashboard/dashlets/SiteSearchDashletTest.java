package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;

public class SiteSearchDashletTest extends AbstractUserDashboardDashletsTests
{
    private static final String EXPECTED_10 = "10";
    private static final String EXPECTED_25 = "25";
    private static final String EXPECTED_50 = "50";
    private static final String EXPECTED_100 = "100";
    private static final String EXPECTED_NO_RESULTS_FOUND_MESSAGE = "siteSearchDashlet.noResults";
    private static final String EXPECTED_TITLE = "siteSearchDashlet.title";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteSearchDashlet.balloonMessage";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    private SiteSearchDashlet siteSearchDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteSearchDashlet = new SiteSearchDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), DashboardCustomization.UserDashlet.SITE_SEARCH, 1, 3);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2423, C2424")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void siteSearchDashletWithNoResultsTest()
    {
        userDashboardPage.navigate(user.get());
        siteSearchDashlet.assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .clickOnHelpIcon(DashletHelpIcon.SITE_SEARCH)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed()
            .openSearchFilterDropdown()
            .assertDropdownValuesEqual(asList(EXPECTED_10, EXPECTED_25, EXPECTED_50, EXPECTED_100))
                .typeInSearch(RandomData.getRandomAlphanumeric())
                .clickSearchButton()
            .assertNoResultsFoundMessageEquals(
                language.translate(EXPECTED_NO_RESULTS_FOUND_MESSAGE));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
}
