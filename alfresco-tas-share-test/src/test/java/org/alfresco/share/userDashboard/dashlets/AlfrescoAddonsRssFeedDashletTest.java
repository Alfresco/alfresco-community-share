package org.alfresco.share.userDashboard.dashlets;

import static org.alfresco.dataprep.DashboardCustomization.UserDashlet;

import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class AlfrescoAddonsRssFeedDashletTest extends AbstractUserDashboardDashletsTests
{
    private final String sampleRssFeed = "https://www.feedforall.com/sample.xml";
    private final String sampleRssFeedTitle = "feedforall.com";

    private RssFeedDashlet rssFeedDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        rssFeedDashlet = new RssFeedDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), UserDashlet.ADDONS_RSS_FEED, 1, 3);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2168")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD, "RSSFeedDashletTest" })
    public void verifyAlfrescoAddonsNewsFeedDashlet()
    {
        userDashboardPage.navigate(user.get());
        rssFeedDashlet.configureDashlet()
            .assertDialogTitleEquals(language.translate("rssFeedDashlet.configureDialogTitle"))
            .setUrlValue(sampleRssFeed)
            .selectNumberOfItemsToDisplay(5)
            .assertNumberOfItemsToDisplayFromDropDownIs(String.valueOf(5))
            .selectOpenLinksInNewWindowCheckboxFromDialog()
            .assertNewWindowIsChecked()
            .clickOk();
        rssFeedDashlet.assertDashletTitleContains("FeedForAll Sample Feed")
            .clickOnRssLink(1)
            .assertRssFeedLinkIsOpenedInNewBrowserTab(sampleRssFeedTitle);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
}
