package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

public class MyDiscussionsDashletTests extends AbstractUserDashboardDashletsTests
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

    private MyDiscussionsDashlet myDiscussionsDashlet;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        myDiscussionsDashlet = new MyDiscussionsDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), DashboardCustomization.UserDashlet.MY_DISCUSSIONS, 1, 3);
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2774")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD  })
    public void checkMyDiscussionsDashlet()
    {
        userDashboardPage.navigate(user.get());
        myDiscussionsDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .assertNoTopicsMessageEquals(language.translate(EXPECTED_EMPTY_TOPICS_MESSAGE));
        userDashboardPage.navigate(user.get());
        myDiscussionsDashlet
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
            .closeHelpBalloon();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
    }
}
