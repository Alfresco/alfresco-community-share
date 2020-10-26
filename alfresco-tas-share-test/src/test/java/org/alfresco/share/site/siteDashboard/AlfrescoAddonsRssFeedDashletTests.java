package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AlfrescoAddonsRssFeedDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_DIALOG_TITLE = "Enter Feed URL:";
    private static final String EXPECTED_DASHLET_TITLE = "rssAlfrescoAddonsFeedDashlet.title";
    private static final String RSS_FEED_URL = "https://www.feedforall.com/sample.xml";
    private static final String NUMBER_OF_ITEMS_DISPLAYED = "5";
    private static final String EXPECTED_URL = "feedforall.com";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "rssAddonsFeedDashlet.helpBalloonMessage";
    private static final int SECOND_LINK = 1;
    private static final int EXPECTED_LIST_EMPTY_SIZE = 0;

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    private RssFeedDashlet rssFeedDashlet;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(userModel);

        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        addDashlet(siteModel, Dashlets.ALFRESCO_ADDONS_RSS_FEED, 1);
    }

    @TestRail (id = "C5568")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void checkDisplaySpecificMessageWhenAlfrescoAddonsRssFeedListIsEmpty()
    {
        rssFeedDashlet
            .assertDashletHelpIconIsDisplayed(DashletHelpIcon.RSS_FEED)
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .assertListSizeEquals(EXPECTED_LIST_EMPTY_SIZE)
            .clickOnHelpIcon(DashletHelpIcon.RSS_FEED)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail(id = "C2793")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldConfigureAlfrescoAddonsRssFeedDashlet()
    {
        siteDashboardPage.navigate(siteModel);
        rssFeedDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .configureDashlet()
            .assertDialogTitleEqualsWithExpected(EXPECTED_DIALOG_TITLE)
            .setUrlValue(RSS_FEED_URL)
            .selectNumberOfItemsToDisplay(NUMBER_OF_ITEMS_DISPLAYED)
            .selectOpenLinksInNewWindowCheckboxFromDialog()
            .clickOk();
        rssFeedDashlet
            .assertListSizeEquals(Integer.parseInt(NUMBER_OF_ITEMS_DISPLAYED))
            .clickOnHelpIcon(DashletHelpIcon.RSS_FEED)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertHelpBalloonIsNotDisplayed()
            .clickOnRssLink(SECOND_LINK)
            .assertRssFeedLinkIsOpenedInNewBrowserTab(EXPECTED_URL);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}