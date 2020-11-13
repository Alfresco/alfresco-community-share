package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteProfileDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SiteProfileDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "siteProfileDashlet.title";
    private static final String WELCOME_TO = "siteProfileDashlet.bodyTitle";
    private static final String MANAGER_LABEL = "siteProfileDashlet.managerLabel";
    private static final String VISIBILITY_LABEL = "siteProfileDashlet.visibilityLabel";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteProfileDashlet.helpBalloonMessage";
    private static final String EMPTY_SPACE = " ";

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    private SiteProfileDashlet siteProfileDashlet;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();

        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_PROFILE, 1);
        siteDashboardPage.navigate(siteModel);
    }

    @TestRail (id = "C2811")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplaySiteProfileDashletDetails()
    {
        siteProfileDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .assertSiteWelcomeMessageEquals(
                language.translate(WELCOME_TO).concat(siteModel.getId()));

        siteProfileDashlet
            .assertSiteDescriptionEquals(
                siteModel.getTitle().concat(siteModel.getVisibility().name()));

        siteProfileDashlet
            .assertSiteManagerEquals(language.translate(MANAGER_LABEL),
                userModel.getFirstName().concat(EMPTY_SPACE.concat(userModel.getLastName())));

        siteProfileDashlet
            .assertSiteVisibilityEquals(language.translate(VISIBILITY_LABEL),
                siteModel.getVisibility().name());

        siteProfileDashlet
            .clickOnHelpIcon(DashletHelpIcon.SITE_PROFILE)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C588828")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayUsernameInUserProfilePageWhenAccessedFromSiteProfileDashlet()
    {
         siteProfileDashlet
             .clickSiteManagerLink(
                 userModel.getFirstName().concat(EMPTY_SPACE.concat(userModel.getLastName())))
             .assertUsernameEquals(userModel.getFirstName(), userModel.getLastName());
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
