package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RssFeedDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_DIALOG_TITLE = "Enter Feed URL:";
    private static final String RSS_FEED_URL = "https://www.feedforall.com/sample.xml";
    private static final String EXPECTED_DASHLET_TITLE_BEFORE_CONFIGURATION = "rssAddonsFeedDashlet.title";
    private static final String EXPECTED_DASHLET_TITLE_AFTER_CONFIGURATION = "rssAddonsFeedDashlet.title.afterConfig";
    private static final String NUMBER_OF_ITEMS_DISPLAYED = "5";
    private static final String EXPECTED_URL = "feedforall.com";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "rssFeedDashlet.helpBalloonMessage";
    private static final int SECOND_LINK = 1;
    private static final int EXPECTED_EMPTY_LIST_SIZE = 0;

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    private RssFeedDashlet rssFeedDashlet;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();

        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.RSS_FEED, 1);
    }

    @TestRail (id = "C5568")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void checkDisplaySpecificMessageWhenRssFeedListIsEmpty()
    {
        rssFeedDashlet
            .assertDashletTitleEquals(language.translate(
                EXPECTED_DASHLET_TITLE_BEFORE_CONFIGURATION).concat(" can't be read"))
            .assertListSizeEquals(EXPECTED_EMPTY_LIST_SIZE)
            .clickOnHelpIcon(DashletHelpIcon.RSS_FEED)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C2795")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldConfigureRSSFeedDashlet()
    {
        siteDashboardPage.navigate(siteModel);
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
            .assertHelpBalloonIsNotDisplayed()
            .clickOnRssLink(SECOND_LINK)
            .assertRssFeedLinkIsOpenedInNewBrowserTab(EXPECTED_URL);
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}