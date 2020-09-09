package org.alfresco.share.userDashboard;

import org.alfresco.po.share.DashboardCustomization.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */
public class CustomizeUserDashboardTests extends ContextAwareWebTest
{
    @Autowired
    private CustomizeUserDashboardPage customizeUserDashboard;

    @Autowired
    private MyTasksDashlet myTasksDashlet;

    @Autowired
    private MyActivitiesDashlet myActivitiesDashlet;

    private UserModel user, changeLayoutUser;

    @BeforeClass(alwaysRun = true)
    public void setup()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        changeLayoutUser = dataUser.createRandomTestUser();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(user,changeLayoutUser);
    }

    @TestRail (id = "C2853")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void changeDefaultDashlets()
    {
        setupAuthenticatedSession(user);
        customizeUserDashboard.navigate()
            .clickAddDashlet()
            .assertAvailableDashletsSectionIsDisplayed()
            .clickCloseAvailabeDashlets()
            .assertAvailableDashletsSectionIsNotDisplayed()
            .clickAddDashlet()
            .addDashlet(Dashlets.SAVED_SEARCH, 1)
                .assertDashletIsAddedInColumn(Dashlets.SAVED_SEARCH, 1)
            .removeDashlet(Dashlets.SAVED_SEARCH, 1)
                .assertDashletIsNotAddedInColumn(Dashlets.SAVED_SEARCH, 1)
            .moveAddedDashletInColumn(Dashlets.MY_SITES, 1, 2)
                .assertDashletIsAddedInColumn(Dashlets.MY_SITES, 2)
            .reorderDashletsInColumn(Dashlets.MY_ACTIVITIES, Dashlets.MY_DOCUMENTS, 2, 2)
            .clickOk();
        userDashboard.assertCustomizeUserDashboardIsDisplayed()
            .assertDashletIsAddedInPosition(Dashlets.MY_DOCUMENTS, 2, 1)
            .assertDashletIsAddedInPosition(Dashlets.MY_ACTIVITIES, 2, 2);
    }

    @TestRail (id = "C2855")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void changeDashboardLayout()
    {
        setupAuthenticatedSession(changeLayoutUser);
        customizeUserDashboard.navigate()
            .assertCurrentLayoutIs(Layout.TWO_COLUMNS_WIDE_RIGHT)
            .clickChangeLayout()
                .assertOneColumnLayoutIsDisplayed()
                .assertTwoColumnsLayoutWideLeftIsDisplayed()
                .assertThreeColumnsLayoutIsDisplayed()
                .assertFourColumnsLayoutIsDisplayed()
            .selectLayout(Layout.THREE_COLUMNS)
                .assertChangeLayoutButtonIsDisplayed()
            .clickChangeLayout()
                .assertOneColumnLayoutIsDisplayed()
                .assertTwoColumnsLayoutWideLeftIsDisplayed()
                .assertTwoColumnsLayoutWideRightIsDisplayed()
                .assertFourColumnsLayoutIsDisplayed()
            .clickCancelNewLayout()
                .assertCurrentLayoutIs(Layout.THREE_COLUMNS)
            .clickOk();
        userDashboard.assertNumberOfDashletColumnsIs(3);
    }

    @TestRail (id = "C2857")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void expandNarrowDashlets()
    {
        setupAuthenticatedSession(user);
        myTasksDashlet.assertDashletIsExpandable();
        myActivitiesDashlet.assertDashletIsExpandable();

        int sizeBefore = myTasksDashlet.getDashletHeight();
        myTasksDashlet.resizeDashlet(50, 1);
        int sizeAfter = myTasksDashlet.getDashletHeight();
        Assert.assertTrue(sizeAfter > sizeBefore, "Dashlet size is increased");
        myTasksDashlet.resizeDashlet(-300, 0);
        Assert.assertTrue(myTasksDashlet.getDashletHeight() < sizeAfter);

        sizeBefore = myActivitiesDashlet.getDashletHeight();
        myActivitiesDashlet.resizeDashlet(200, 1);
        sizeAfter = myActivitiesDashlet.getDashletHeight();
        Assert.assertTrue(sizeAfter > sizeBefore, "Dashlet size is increased");
        myActivitiesDashlet.resizeDashlet(-300, 0);
        Assert.assertTrue(myActivitiesDashlet.getDashletHeight() < sizeAfter);
    }
}
