package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class AlfrescoAddonsRssFeedDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_DIALOG_TITLE = "Enter Feed URL:";
    private static final String RSS_FEED_URL = "https://www.feedforall.com/sample.xml";
    private static final String NUMBER_OF_ITEMS_DISPLAYED = "5";
    private static final String EXPECTED_URL = "feedforall.com";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "rssAddonsFeedDashlet.helpBalloonMessage";
    private static final int SECOND_LINK = 1;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private RssFeedDashlet rssFeedDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        rssFeedDashlet = new RssFeedDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.ADDONS_RSS_FEED, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5568")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD, "RSS_FEED" })
    public void checkDisplaySpecificMessageWhenAlfrescoAddonsRssFeedListIsEmpty()
    {
        siteDashboardPage.navigate(site.get());
        rssFeedDashlet
            .assertDashletHelpIconIsDisplayed(DashletHelpIcon.RSS_FEED)
            .assertFeedListIsEmpty()
            .clickOnHelpIcon(DashletHelpIcon.RSS_FEED)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail(id = "C2793")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD, "RSS_FEED" })
    public void shouldConfigureAlfrescoAddonsRssFeedDashlet()
    {
        siteDashboardPage.navigate(site.get());
        rssFeedDashlet
            .configureDashlet()
            .assertDialogTitleEquals(EXPECTED_DIALOG_TITLE)
            .setUrlValue(RSS_FEED_URL)
            .selectNumberOfItemsToDisplay(NUMBER_OF_ITEMS_DISPLAYED)
            .selectOpenLinksInNewWindowCheckboxFromDialog()
            .clickOk();
        rssFeedDashlet
            .assertListSizeEquals(Integer.parseInt(NUMBER_OF_ITEMS_DISPLAYED))
            .clickOnHelpIcon(DashletHelpIcon.RSS_FEED)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed()
            .clickOnRssLink(SECOND_LINK)
            .assertRssFeedLinkIsOpenedInNewBrowserTab(EXPECTED_URL);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}