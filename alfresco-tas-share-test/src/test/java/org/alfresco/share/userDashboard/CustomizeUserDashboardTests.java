package org.alfresco.share.userDashboard;

import org.alfresco.po.share.DashboardCustomizationImpl.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */
public class CustomizeUserDashboardTests extends ContextAwareWebTest
{
    @Autowired
    CustomizeUserDashboardPage customizeUserDashboard;

    @Autowired
    UserDashboardPage userDashboard;

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    SavedSearchDashlet savedSearchDashlet;

    @TestRail(id = "C2853")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void changeDefaultDashlets()
    {
        String userName = String.format("user2853-%s%s", RandomData.getRandomAlphanumeric(), domain);
        userService.create(adminUser, adminPassword, userName, password, userName, "C2853", "lname");

        LOG.info("Step 1 - Click 'Customize Dashboard' on the dashboard banner");
        setupAuthenticatedSession(userName, password);
        customizeUserDashboard.navigate();
        Assert.assertTrue(customizeUserDashboard.getCurrentLayout().equals(Layout.TWO_COLUMNS_WIDE_RIGHT.description),
                "Two columns wide right layout is not displayed");

        LOG.info("Step 2 - Click 'Add Dashlets' to display the available dashlets");
        customizeUserDashboard.clickAddDashlet();
        Assert.assertTrue(customizeUserDashboard.isAvailableDashletListDisplayed(), "Available dashlets section is not opened");

        LOG.info("Step 3 - Click 'Close' link to the top right of the section");
        customizeUserDashboard.clickCloseAvailabeDashlets();
        Assert.assertFalse(customizeUserDashboard.isAvailableDashletListDisplayed(), "Available dashlets section is still opened");

        LOG.info("Step 4 - Open 'Add Dashlets' section again. Move the scrollbar up and down");
        customizeUserDashboard.clickAddDashlet();

        LOG.info("Step 5 - Click the any dashlet and drag it to the selected column");
        customizeUserDashboard.addDashlet(Dashlets.SAVED_SEARCH, 1);
        Assert.assertTrue(customizeUserDashboard.isDashletAddedInColumn(Dashlets.SAVED_SEARCH, 1), "Saved Search dashlet is not added in column 1");

        LOG.info("Step 6 - Click the unwanted dashlet and drag it to the garbage can");
        customizeUserDashboard.removeDashlet(Dashlets.SAVED_SEARCH, 1);
        Assert.assertFalse(customizeUserDashboard.isDashletAddedInColumn(Dashlets.SAVED_SEARCH, 1), "Saved Search dashlet is still in column 1");

        LOG.info("Step 7 - Click and drag the dashlets within and across columns to configure the display order");
        customizeUserDashboard.moveAddedDashletInColumn(Dashlets.MY_SITES, 1, 2);
        Assert.assertTrue(customizeUserDashboard.isDashletAddedInColumn(Dashlets.MY_SITES, 2), "My Sites dashlet is not added in column 2");
        customizeUserDashboard.reorderDashletsInColumn(Dashlets.MY_ACTIVITIES, Dashlets.MY_DOCUMENTS, 2);

        LOG.info("Step 8 - Click OK to save changes");
        customizeUserDashboard.clickOk();
        Assert.assertTrue(userDashboard.isCustomizeUserDashboardDisplayed(), "User Dashboard page is not opened");
        Assert.assertTrue(userDashboard.isDashletAddedInPosition(Dashlets.MY_DOCUMENTS, 2, 1), "My Documents dashlet is not in column 2 position 1");
        Assert.assertTrue(userDashboard.isDashletAddedInPosition(Dashlets.MY_ACTIVITIES, 2, 2), "My Activities dashlet is not in column 2 position 2");

        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @TestRail(id = "C2855")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
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

        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @TestRail(id = "C2857")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
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
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(mySitesDashlet.isDashletExpandable());
        Assert.assertTrue(savedSearchDashlet.isDashletExpandable());

        LOG.info("Step 5 - Try to expand all possible dashlets");
        int sizeBeforeMyProfile = mySitesDashlet.getDashletHeight();
        int sizeBeforeMyDiscussions = savedSearchDashlet.getDashletHeight();
        mySitesDashlet.resizeDashlet(500,1);
   //     savedSearchDashlet.resizeDashlet(600);
        int sizeAfterMyMeeting = mySitesDashlet.getDashletHeight();
    //   int sizeAfterMyDiscussions = savedSearchDashlet.getDashletHeight();
        Assert.assertTrue(sizeAfterMyMeeting > sizeBeforeMyProfile);
    //    Assert.assertTrue(sizeAfterMyDiscussions > sizeBeforeMyDiscussions);

        LOG.info("Step 6 - Try to narrow all possible dashlets at the minimum size");
        mySitesDashlet.resizeDashlet(-400,0);
  //      savedSearchDashlet.resizeDashlet(-400);
        Assert.assertTrue(mySitesDashlet.getDashletHeight() < sizeAfterMyMeeting);
   //     Assert.assertTrue(savedSearchDashlet.getDashletHeight() < sizeAfterMyDiscussions);

        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }
}
