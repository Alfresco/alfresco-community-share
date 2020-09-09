package org.alfresco.share.userDashboard.dashlets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.EnterFeedURLPopUp;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RssFeedDashletTest extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private RssFeedDashlet rssFeedDashlet;

    private UserModel user;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.RSS_FEED, 1);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2162")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void verifyAlfrescoAddonsNewsFeedDashlet()
    {
        rssFeedDashlet.configureDashlet()
            .assertPopUpTitleIs(language.translate("rssFeedDashlet.configureDialogTitle"))
                .setUrlField(cnnRSSFeed)
                .selectNumberOfItemsToDisplay(5)
                .assertValueSelectInNrOfItemsToDisplayIs(5)
                .checkNewWindow()
                .assertNewWindowIsChecked()
                .clickOk();
        rssFeedDashlet.asserDashletTitleContains("CNN.com")
            .clickOnRssLink(1)
            .assertRssFeedLinkIsOpened(cnnFeedTabTitle)
            .clickOnRssLink(3)
            .assertRssFeedLinkIsOpened(cnnFeedTabTitle);
    }
}