package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.EnterFeedURLPopUp;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * @author bogdan.simion
 */
public class AlfrescoAddonsRssFeedDashletTest extends ContextAwareWebTest
{
    @Autowired
    RssFeedDashlet rssFeedDashlet;

    @Autowired
    EnterFeedURLPopUp enterFeedURLPopUp;

    @Autowired
    UserDashboardPage userDashboardPage;

    @TestRail (id = "C2168")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void verifyAlfrescoAddonsNewsFeedDashlet()
    {
        String userName = String.format("C2168-%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName, "C2793", "lname");
        userService.addDashlet(userName, password, UserDashlet.ADDONS_RSS_FEED, DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Click Configure this dashlet icon");
        userDashboardPage.navigate(userName);
        rssFeedDashlet.clickOnConfigureRssFeedDashlet();
        Assert.assertEquals(enterFeedURLPopUp.getPopUpTitle(), "Enter Feed URL:", "Enter Feed URL form is displayed.");

        LOG.info("STEP 2 - Fill in URL field with valid data (e.g: http://feeds.reuters.com/reuters/businessNews");
        enterFeedURLPopUp.fillUrlField("http://feeds.reuters.com/reuters/businessNews");
        Assert.assertEquals(enterFeedURLPopUp.getUrlFieldText(), "http://feeds.reuters.com/reuters/businessNews", "The URL text is not valid");

        LOG.info("STEP 3 - From Number of items to display drop-down list choose any value (e.g: 5");
        enterFeedURLPopUp.selectNumberOfItemsToDisplay("5");
        Assert.assertTrue(enterFeedURLPopUp.isValueSelectedFromNoItemsToDisplayDropDown("5"), "The correct value is not selected");

        LOG.info("STEP 4 - Click Open links in new window check-box");
        enterFeedURLPopUp.checkNewWindowCheckbox();
        Assert.assertTrue(enterFeedURLPopUp.isNewWindowCheckBoxChecked(), "Check-box has not been selected");

        LOG.info("STEP 5 - Press OK button");
        getBrowser().waitInSeconds(5);
        enterFeedURLPopUp.clickOkButton();
        rssFeedDashlet.renderedPage();
        Assert.assertTrue(rssFeedDashlet.getDashletTitle().contains("Reuters: Business News"), " Feed information is updated: ");

        //Store the current window handle
        String currentWindow = getBrowser().getWindowHandle();

        LOG.info("Step 6: Click on any RSS news");
        rssFeedDashlet.clickOnRssLink(1);
        getBrowser().waitInSeconds(5);

        //Switch to new window opened
        for (String winHandle : getBrowser().getWindowHandles())
        {
            getBrowser().switchTo().window(winHandle);
            if (getBrowser().getCurrentUrl().contains("https://www.reuters.com"))
            {
                break;
            } else
            {
                getBrowser().switchTo().window(currentWindow);
            }
        }

        Assert.assertTrue(getBrowser().getCurrentUrl().contains("https://www.reuters.com"), "After clicking on RSS link, the title is: " + getBrowser().getCurrentUrl());
        closeWindowAndSwitchBack();

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }
}
