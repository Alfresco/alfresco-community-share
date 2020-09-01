package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.EnterFeedURLPopUp;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AlfrescoAddonsRssFeedDashletTest extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private RssFeedDashlet rssFeedDashlet;

    @Autowired
    private EnterFeedURLPopUp enterFeedURLPopUp;

    private UserModel user;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.ALFRESCO_ADDONS_RSS_FEED, 1);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2168")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void verifyAlfrescoAddonsNewsFeedDashlet()
    {
        rssFeedDashlet.configureDashlet()
            .assertPopUpTitleIs(language.translate("rssFeedDashlet.configureDialogTitle"));
        enterFeedURLPopUp.setUrlField(cnnRSSFeed)
            .selectNumberOfItemsToDisplay(5)
            .assertValueSelectInNrOfItemsToDisplayIs(5)
            .checkNewWindow()
            .assertNewWindowIsChecked()
            .clickOkButton();
        rssFeedDashlet.asserDashletTitleContains("CNN.com")
            .clickOnRssLink(1)
                .assertRssFeedLinkIsOpened(cnnFeedTabTitle)
            .clickOnRssLink(3)
                .assertRssFeedLinkIsOpened(cnnFeedTabTitle);
    }
}
