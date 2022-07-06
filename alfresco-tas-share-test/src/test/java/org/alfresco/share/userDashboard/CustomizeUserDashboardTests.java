package org.alfresco.share.userDashboard;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.enums.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CustomizeUserDashboardTests extends BaseTest
{
    private CustomizeUserDashboardPage customizeUserDashboard;
    private MyTasksDashlet myTasksDashlet;
    private MyActivitiesDashlet myActivitiesDashlet;

    private ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void dataPrep()
    {
        customizeUserDashboard = new CustomizeUserDashboardPage(webDriver);
        myTasksDashlet = new MyTasksDashlet(webDriver);
        myActivitiesDashlet = new MyActivitiesDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2853")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void changeDefaultDashlets()
    {
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
            .moveDashletDownInColumn(Dashlets.MY_ACTIVITIES, 2)
            .clickOk();
        userDashboardPage.assertCustomizeUserDashboardIsDisplayed()
            .assertDashletIsAddedInPosition(Dashlets.MY_DOCUMENTS, 2, 1);
    }

    @TestRail (id = "C2855")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void changeDashboardLayout()
    {
        authenticateUsingCookies(user.get());
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
        userDashboardPage.assertNumberOfDashletColumnsIs(3);
    }

    @TestRail (id = "C2857")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void expandNarrowDashlets()
    {
        userDashboardPage.navigate(user.get());

        myTasksDashlet.assertDashletIsExpandable();
        myActivitiesDashlet.assertDashletIsExpandable();

        int sizeBefore = myTasksDashlet.getDashletHeight();
        myTasksDashlet.resizeDashlet(20, 1);
        int sizeAfter = myTasksDashlet.getDashletHeight();
        assertTrue(sizeAfter > sizeBefore, "Dashlet size is increased");
        myTasksDashlet.resizeDashlet(-30, 0);
        assertTrue(myTasksDashlet.getDashletHeight() < sizeAfter);

        sizeBefore = myActivitiesDashlet.getDashletHeight();
        myActivitiesDashlet.resizeDashlet(50, 1);
        sizeAfter = myActivitiesDashlet.getDashletHeight();
        assertTrue(sizeAfter > sizeBefore, "Dashlet size is increased");
        myActivitiesDashlet.resizeDashlet(-100, 0);
        assertTrue(myActivitiesDashlet.getDashletHeight() < sizeAfter);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(user.get());
    }
}
