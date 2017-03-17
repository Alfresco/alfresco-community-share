package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SiteSearchTest extends ContextAwareWebTest
{
    @Autowired
    SiteSearchDashlet siteSearchDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    private String userName = "User" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)
    public void setup()
    {
        super.setup();
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        setupAuthenticatedSession(userName, password);
        userService.addDashlet(userName, password, UserDashlet.SITE_SEARCH, DashletLayout.THREE_COLUMNS, 3, 1);
    }

    @TestRail(id = "C2423")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void siteSearchDashletTest()
    {
        logger.info("Step 1: Verify Site Search dashlet");
        userDashboardPage.navigate(userName);
        Assert.assertEquals(siteSearchDashlet.getDashletTitle(), "Site Search");
        siteSearchDashlet.isSearchFieldDisplayed();
        siteSearchDashlet.isDropDownMenuDisplayed();
        siteSearchDashlet.isHelpIconDisplayed(DashletHelpIcon.SITE_SEARCH);

        logger.info("Step 2: Click on \"?\" icon");
        siteSearchDashlet.clickOnHelpIcon(DashletHelpIcon.SITE_SEARCH);
        siteSearchDashlet.isBalloonDisplayed();
        Assert.assertEquals(
                siteSearchDashlet.getHelpBalloonMessage(),
                "Use this dashlet to perform a site search and view the results.\nClicking the item name takes you to the details page so you can preview or work with the item.");

        logger.info("Step 3: Click on \"X\" icon");
        siteSearchDashlet.closeHelpBalloon();
        Assert.assertFalse(siteSearchDashlet.isBalloonDisplayed());

        logger.info("Step 4: Click on drop down menu");
        Assert.assertTrue(siteSearchDashlet.checkValuesFromDropDownList());
    }
}
