package org.alfresco.share.userDashboard;

import org.alfresco.po.share.DashboardCustomization.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
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

/**
 * @author bogdan.bocancea
 */
public class CustomizeUserDashboardTests extends ContextAwareWebTest
{
    @Autowired
    private CustomizeUserDashboardPage customizeUserDashboard;

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    SavedSearchDashlet savedSearchDashlet;

    private UserModel user;

    @BeforeClass(alwaysRun = true)
    public void setup()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(user);
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
            .reorderDashletsInColumn(Dashlets.MY_ACTIVITIES, Dashlets.MY_DOCUMENTS, 2, 2)
            .clickOk();

        Assert.assertTrue(userDashboard.isCustomizeUserDashboardDisplayed(), "User Dashboard page is not opened");
        Assert.assertTrue(userDashboard.isDashletAddedInPosition(Dashlets.MY_DOCUMENTS, 2, 1), "My Documents dashlet is not in column 2 position 1");
        Assert.assertTrue(userDashboard.isDashletAddedInPosition(Dashlets.MY_ACTIVITIES, 2, 2), "My Activities dashlet is not in column 2 position 2");
    }

    @TestRail (id = "C2855")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void changeDashboardLayout()
    {
        String userName = "user2855-" + RandomData.getRandomAlphanumeric() + domain;
        userService.create(adminUser, adminPassword, userName, password, userName, "C2855", "lname");

        LOG.info("Step 1 - Press 'Customize Dashboard' on the dashboard banner");
        setupAuthenticatedSession(userName, password);
        customizeUserDashboard.navigate();
        Assert.assertTrue(customizeUserDashboard.getCurrentLayout().equals(Layout.TWO_COLUMNS_WIDE_RIGHT.description), "Two columns wide right layout is not displayed");

        LOG.info("Step 2 - Press 'Change Layout' to display all available dashboard layouts");
        customizeUserDashboard.clickChangeLayout();
        Assert.assertTrue(customizeUserDashboard.isOneColumnLayoutDisplayed(), "One column layout is not displayed");
        Assert.assertTrue(customizeUserDashboard.isTwoColumnsLayoutWideLeftDisplayed(), "Two columns wide left layout is not displayed");
        Assert.assertTrue(customizeUserDashboard.isThreeColumnsLayoutDisplayed(), "Three columns layout is not displayed");
        Assert.assertTrue(customizeUserDashboard.isFourColumnsLayoutDisplayed(), "Four columns layout is not displayed");

        LOG.info("Step 3 - Click the graphic of the desired layout and click 'Select' to the right of it");
        customizeUserDashboard.selectLayout(Layout.THREE_COLUMNS);
        Assert.assertTrue(customizeUserDashboard.isChangeLayoutButtonDisplayed(), "Change layout section is still displayed");

        LOG.info("Step 4 - Click 'Change Layout' once more");
        customizeUserDashboard.clickChangeLayout();
        Assert.assertTrue(customizeUserDashboard.isOneColumnLayoutDisplayed(), "One column layout is not displayed");
        Assert.assertTrue(customizeUserDashboard.isTwoColumnsLayoutWideLeftDisplayed(), "Two columns wide left layout is not displayed");
        Assert.assertTrue(customizeUserDashboard.isTwoColumnsLayoutWideLeftDisplayed(), "Two columns wide right layout is not displayed");
        Assert.assertTrue(customizeUserDashboard.isFourColumnsLayoutDisplayed(), "Four columns layout is not displayed");

        LOG.info("Step 5 - Click 'Cancel'");
        customizeUserDashboard.clickCancelNewLayout();
        Assert.assertTrue(customizeUserDashboard.isChangeLayoutButtonDisplayed(), "Change layout section is still displayed");
        Assert.assertTrue(customizeUserDashboard.getCurrentLayout().equals(Layout.THREE_COLUMNS.description), "Three columns layout is not displayed");

        LOG.info("Step 6 - Click 'OK' to save changes");
        customizeUserDashboard.clickOk();
        Assert.assertTrue(userDashboard.isCustomizeUserDashboardDisplayed(), "User Dashboard page is not opened " + getBrowser().getCurrentUrl());
        Assert.assertTrue(userDashboard.getNumerOfColumns() == 3, "User Dashboard page doesn't have 3 columns");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @TestRail (id = "C2857")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD, "tobefixed" })
    public void expandNarrowDashlets()
    {
        String userName = "user2855-" + RandomData.getRandomAlphanumeric() + domain;
        userService.create(adminUser, adminPassword, userName, password, userName, "C2855", "lname");

        LOG.info("Step 1 - Press 'Customize Dashboard' on the dashboard banner");
        setupAuthenticatedSession(userName, password);
        customizeUserDashboard.navigate();

        LOG.info("Step 2 - Drag a few dashlets that were not added by default to any column");
        customizeUserDashboard.addDashlet(Dashlets.SAVED_SEARCH, 1);

        LOG.info("Step 3 - Click OK button");
        customizeUserDashboard.clickOk();

        LOG.info("Step 4 - On Site Dashboard page verify that several dashlets have an expand ");
        getBrowser().executeJavaScript("scroll(0, 250);");
        Assert.assertTrue(mySitesDashlet.isDashletExpandable());
        Assert.assertTrue(savedSearchDashlet.isDashletExpandable());

        LOG.info("Step 5 - Try to expand all possible dashlets");
        int sizeBeforeMyProfile = mySitesDashlet.getDashletHeight();
        int sizeBeforeMyDiscussions = savedSearchDashlet.getDashletHeight();
        mySitesDashlet.resizeDashlet(500, 1);
//     savedSearchDashlet.resizeDashlet(600);
        int sizeAfterMyMeeting = mySitesDashlet.getDashletHeight();
        //   int sizeAfterMyDiscussions = savedSearchDashlet.getDashletHeight();
        Assert.assertTrue(sizeAfterMyMeeting > sizeBeforeMyProfile);
        //    Assert.assertTrue(sizeAfterMyDiscussions > sizeBeforeMyDiscussions);

        LOG.info("Step 6 - Try to narrow all possible dashlets at the minimum size");
        mySitesDashlet.resizeDashlet(-400, 0);
        //      savedSearchDashlet.resizeDashlet(-400);
        Assert.assertTrue(mySitesDashlet.getDashletHeight() < sizeAfterMyMeeting);
        //     Assert.assertTrue(savedSearchDashlet.getDashletHeight() < sizeAfterMyDiscussions);

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }
}
