package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyDiscussionsDashletTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private MyDiscussionsDashlet myDiscussionsDashlet;

    private UserModel user;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.MY_DISCUSSIONS, 1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2774")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD  })
    public void checkMyDiscussionsDashlet()
    {
        userDashboard.navigate(user);
        myDiscussionsDashlet.assertDashletTitleEquals(language.translate("myDiscussionDashlet.title"))
            .assertNoTopicsMessageIsDisplayed()
            .assertTopicDropdownHasAllOptions()
            .assertHistoryDropdownHasAllOptions()
            .clickOnHelpIcon(DashletHelpIcon.MY_DISCUSSIONS)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageIs(language.translate("myDiscussionDashlet.helpBalloonMessage"))
            .closeHelpBalloon();
    }
}
