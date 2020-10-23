package org.alfresco.share.userDashboard.dashlets;

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
    @Autowired
    private SiteSearchDashlet siteSearchDashlet;

    private UserModel user;

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
        siteSearchDashlet.assertDashletTitleEquals(language.translate("siteSearchDashlet.title"))
            .assertSearchFieldIsDisplayed()
            .assertSearchResultDropdownIsDisplayed()
            .assertSearchButtonIsDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.SITE_SEARCH)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageIs(language.translate("siteSearchDashlet.balloonMessage"))
            .closeHelpBalloon()
            .assertAllLimitValuesAreDisplayed()
                .typeInSearch(RandomData.getRandomAlphanumeric())
                .clickSearchButton()
                .assertNoResultsIsDisplayed();
    }
}
