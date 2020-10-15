package org.alfresco.share.site.siteDashboard;

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

    @TestRail(id = "C2793")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldConfigureAlfrescoAddonsRssFeedDashlet()
    {
        rssFeedDashlet.configureDashlet()
            .assertDialogTitleEqualsWithExpected(EXPECTED_DIALOG_TITLE)
            .setUrlValue(RSS_FEED_URL)
            .selectNumberOfItemsToDisplay(NUMBER_OF_ITEMS_DISPLAYED)
            .assertNumberOfItemsToDisplayFromDropDownIs(NUMBER_OF_ITEMS_DISPLAYED)
            .selectOpenLinksInNewWindowCheckboxFromDialog()
            .assertNewWindowIsChecked()
            .clickOk();
        rssFeedDashlet.assertDashletTitleContains(DASHLET_TITLE)
            .clickOnRssLink(SECOND_LINK);
        rssFeedDashlet.assertRssFeedLinkIsOpened(EXPECTED_URL);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}