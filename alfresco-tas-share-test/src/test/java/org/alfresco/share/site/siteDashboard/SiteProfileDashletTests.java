package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteProfileDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SiteProfileDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "siteProfileDashlet.title";
    private static final String WELCOME_TO = "siteProfileDashlet.bodyTitle";
    private static final String MANAGER_LABEL = "siteProfileDashlet.managerLabel";
    private static final String VISIBILITY_LABEL = "siteProfileDashlet.visibilityLabel";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteProfileDashlet.helpBalloonMessage";
    private static final String EMPTY_SPACE = " ";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SiteProfileDashlet siteProfileDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteProfileDashlet = new SiteProfileDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.SITE_PROFILE, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2811")
    @Test (groups = {TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplaySiteProfileDashletDetails()
    {
        siteDashboardPage.navigate(site.get());
        siteProfileDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .assertSiteWelcomeMessageEquals("Welcome to " + site.get().getId());

        siteProfileDashlet
            .assertSiteDescriptionEquals(
                site.get().getTitle().concat(site.get().getVisibility().name()));

        siteProfileDashlet
            .assert_SiteManagerEquals(language.translate(MANAGER_LABEL)+ " ",
                user.get().getFirstName().concat(EMPTY_SPACE.concat(user.get().getLastName())));

        siteProfileDashlet
            .assert_SiteVisibilityEquals(language.translate(VISIBILITY_LABEL)+" ", site.get().getVisibility().name(), 2);

        siteProfileDashlet
            .clickOnHelpIcon(DashletHelpIcon.SITE_PROFILE)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C588828")
    @Test (groups = {TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayUsernameInUserProfilePageWhenAccessedFromSiteProfileDashlet()
    {
        siteDashboardPage.navigate(site.get());
        siteProfileDashlet
            .clickSiteManagerLink(
                user.get().getFirstName().concat(EMPTY_SPACE.concat(user.get().getLastName())))
                    .assertUserProfilePageIsOpened();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
