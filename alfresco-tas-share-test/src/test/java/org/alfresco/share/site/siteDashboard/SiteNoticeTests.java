package org.alfresco.share.site.siteDashboard;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteNoticeDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Argint Alex
 */
public class SiteNoticeTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "siteNoticeDashlet.title";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteNoticeDashlet.helpBalloonMessage";
    private static final String TITLE = "title".concat(randomAlphabetic(3));
    private static final String DOCUMENT_TEXT = "body text".concat(randomAlphabetic(3));
    private static final String EXPECTED_NO_DOCUMENT_TEXT_CONFIGURED = "siteNoticeDashlet.noSetTextDocument";

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    private SiteNoticeDashlet siteNoticeDashlet;

    @BeforeClass
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();

        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_NOTICE, 1);
        siteDashboardPage.navigate(siteModel);
    }

    @TestRail (id = "C5556")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void checkSpecificMessageWhenSiteNoticeDashletIsNotConfigured()
    {
        siteNoticeDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_NOTICE)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed()
            .assertSiteNoticeMessageEquals(language.translate(EXPECTED_NO_DOCUMENT_TEXT_CONFIGURED));
    }

    @TestRail (id = "C5558")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldSuccessFullyConfigureSiteNoticeDashlet()
    {
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
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldCancelSiteNoticeDashletConfiguration()
    {
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

    @AfterClass
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}