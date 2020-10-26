package org.alfresco.share.site.siteDashboard;

import java.util.Arrays;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyDiscussionsDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_DASHLET_TITLE = "myDiscussionDashlet.title";
    private static final String EXPECTED_EMPTY_TOPICS_MESSAGE = "myDiscussionDashlet.noTopicsMessage";
    private static final String EXPECTED_MY_TOPICS = "myDiscussionDashlet.myTopics";
    private static final String EXPECTED_ALL_TOPICS = "myDiscussionDashlet.allTopics";
    private static final String EXPECTED_LAST_DAY = "myDiscussionDashlet.lastDay";
    private static final String EXPECTED_LAST_7_DAYS = "myDiscussionDashlet.last7Days";
    private static final String EXPECTED_LAST_14_DAYS = "myDiscussionDashlet.last14Days";
    private static final String EXPECTED_LAST_28_DAYS = "myDiscussionDashlet.last28Days";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "myDiscussionDashlet.helpBalloonMessage";

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    private MyDiscussionsDashlet myDiscussionsDashlet;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(userModel);

        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        addDashlet(siteModel, Dashlets.MY_DISCUSSIONS, 1);
    }

    @TestRail (id = "C2791")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplaySpecificMessageWhenMyDiscussionDashletHasNoTopics()
    {
        siteDashboardPage.navigate(siteModel);
        myDiscussionsDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .assertNoTopicsMessageEquals(language.translate(EXPECTED_EMPTY_TOPICS_MESSAGE))
            .assertMyTopicsDropdownOptionsEqual(Arrays.asList(
                language.translate(EXPECTED_MY_TOPICS),
                language.translate(EXPECTED_ALL_TOPICS)))

            .assertHistoryDropdownOptionsEqual(Arrays.asList(
                language.translate(EXPECTED_LAST_DAY),
                language.translate(EXPECTED_LAST_7_DAYS),
                language.translate(EXPECTED_LAST_14_DAYS),
                language.translate(EXPECTED_LAST_28_DAYS)))

            .clickOnHelpIcon(DashletHelpIcon.MY_DISCUSSIONS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
       removeUserFromAlfresco(userModel);
       deleteSites(siteModel);
    }
}
