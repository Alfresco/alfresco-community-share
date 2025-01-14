package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RssFeedDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_DIALOG_TITLE = "Enter Feed URL:";
    private static final String RSS_FEED_URL = "https://www.feedforall.com/sample.xml";
    private static final String EXPECTED_DASHLET_TITLE_AFTER_CONFIGURATION = "rssAddonsFeedDashlet.title.afterConfig";
    private static final String NUMBER_OF_ITEMS_DISPLAYED = "5";
    private static final String EXPECTED_URL = "feedforall.com";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "rssFeedDashlet.helpBalloonMessage";
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
        addDashlet(user.get(), site.get(), SiteDashlet.RSS_FEED, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5568")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD, "RSS_FEED" })
    public void checkDisplaySpecificMessageWhenRssFeedListIsEmpty()
    {
        siteDashboardPage.navigate(site.get());
        rssFeedDashlet
            .assertFeedListIsEmpty()
            .clickOnHelpIcon(DashletHelpIcon.RSS_FEED)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C2795")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD, "RSS_FEED" })
    public void shouldConfigureRSSFeedDashlet()
    {
        siteDashboardPage.navigate(site.get());
        rssFeedDashlet.configureDashlet()
            .assertDialogTitleEquals(EXPECTED_DIALOG_TITLE)
            .setUrlValue(RSS_FEED_URL)
            .selectNumberOfItemsToDisplay(NUMBER_OF_ITEMS_DISPLAYED)
            .selectOpenLinksInNewWindowCheckboxFromDialog()
            .clickOk();
        rssFeedDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE_AFTER_CONFIGURATION))
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