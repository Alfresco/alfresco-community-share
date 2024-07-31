package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RssFeedDashletTest extends AbstractUserDashboardDashletsTests
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
        addDashlet(user.get(), DashboardCustomization.UserDashlet.RSS_FEED, 1, 3);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2162")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD, "RSSFeedDashletTest" })
    public void verifyNewsFeedDashlet()
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