package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteNoticeDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SiteNoticeTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "siteNoticeDashlet.title";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteNoticeDashlet.helpBalloonMessage";
    private static final String TITLE = "title".concat(randomAlphabetic(3));
    private static final String DOCUMENT_TEXT = "body text".concat(randomAlphabetic(3));
    private static final String EXPECTED_NO_DOCUMENT_TEXT_CONFIGURED = "siteNoticeDashlet.noSetTextDocument";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SiteNoticeDashlet siteNoticeDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteNoticeDashlet = new SiteNoticeDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.SITE_NOTICE, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5556")
    @Test (groups = {TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkSpecificMessageWhenSiteNoticeDashletIsNotConfigured()
    {
        siteDashboardPage.navigate(site.get());
        siteNoticeDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .clickOnHelpIcon(DashletHelpIcon.SITE_NOTICE)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed()
            .assertSiteNoticeMessageEquals(language.translate(EXPECTED_NO_DOCUMENT_TEXT_CONFIGURED));
    }

    @TestRail (id = "C5558")
    @Test (groups = {TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldSuccessFullyConfigureSiteNoticeDashlet()
    {
        siteDashboardPage.navigate(site.get());
        siteNoticeDashlet
            .openConfigurationDialog()
            .setSiteNoticeDashletTitle(TITLE)
            .setSiteNoticeDashletDocumentText(DOCUMENT_TEXT);

        siteNoticeDashlet
            .clickDialogOkButton();

        siteNoticeDashlet
            .assertDashletTitleEquals(TITLE)
            .assertSiteNoticeMessageEquals(DOCUMENT_TEXT);
    }

    @TestRail (id = "C5559")
    @Test (groups = {TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldCancelSiteNoticeDashletConfiguration()
    {
        siteDashboardPage.navigate(site.get());
        siteNoticeDashlet
            .openConfigurationDialog()
            .setSiteNoticeDashletTitle(TITLE)
            .setSiteNoticeDashletDocumentText(DOCUMENT_TEXT);

        siteNoticeDashlet
            .clickDialogCancelButton();

        siteNoticeDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .assertSiteNoticeMessageEquals(language.translate(EXPECTED_NO_DOCUMENT_TEXT_CONFIGURED));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}