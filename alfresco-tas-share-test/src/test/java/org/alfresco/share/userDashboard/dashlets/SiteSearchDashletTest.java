package org.alfresco.share.userDashboard.dashlets;

import static java.util.Arrays.asList;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SiteSearchDashletTest extends AbstractUserDashboardDashletsTests
{
    private static final String EXPECTED_10 = "10";
    private static final String EXPECTED_25 = "25";
    private static final String EXPECTED_50 = "50";
    private static final String EXPECTED_100 = "100";
    private static final String EXPECTED_NO_RESULTS_FOUND_MESSAGE = "siteSearchDashlet.noResults";
    private static final String EXPECTED_TITLE = "siteSearchDashlet.title";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteSearchDashlet.balloonMessage";

    private UserModel user;

    @Autowired
    private SiteSearchDashlet siteSearchDashlet;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.SITE_SEARCH, 1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2423, C2424")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void siteSearchDashletTest()
    {
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
}
