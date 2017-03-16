package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.ConfigureSavedSearchDashletPopUp;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SavedSearchTests extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboardPage;
    
    @Autowired
    SavedSearchDashlet savedSearchDashlet;
    
    @Autowired
    ConfigureSavedSearchDashletPopUp configureSavedSearchPopUp;
    
    private String userName;
    
    @BeforeClass
    public void setup()
    {
        super.setup();
        userName = "User1" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + "@tests.com", userName, userName);
        userService.addDashlet(userName, DataUtil.PASSWORD, DashboardCustomization.UserDashlet.SAVED_SEARCH, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);

        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
    }
    
    @TestRail(id = "C2427")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void savedSearchDashlet()
    {
        userDashboardPage.navigate(userName);
        
        logger.info("Step 1: Verify 'Saved Search' dahslet");
        Assert.assertEquals(savedSearchDashlet.getDashletTitle(), "Saved Search");
        Assert.assertEquals(savedSearchDashlet.getDefaultMessage(), "No results found.");
        Assert.assertTrue(savedSearchDashlet.isConfigureDashletIconDisplayed());
        Assert.assertTrue(savedSearchDashlet.isHelpIconDisplayed(DashletHelpIcon.SAVED_SEARCH));
        
        logger.info("Step 2: Click Help icon");
        savedSearchDashlet.clickOnHelpIcon(DashletHelpIcon.SAVED_SEARCH);
        Assert.assertTrue(savedSearchDashlet.isBalloonDisplayed());
        Assert.assertEquals(savedSearchDashlet.getHelpBalloonMessage(), "Use this dashlet to set up a search and view the results."
                + "\nConfigure the dashlet to save the search and set the title text of the dashlet."
                + "\nOnly a Site Manager can configure the search and title - this dashlet is ideal for generating report views in a site.");

        logger.info("Step 3: Close ballon popup");
        savedSearchDashlet.closeHelpBalloon();
        Assert.assertFalse(savedSearchDashlet.isBalloonDisplayed());
        
        logger.info("Step 4: Click 'Configure this dashlet' icon");
        savedSearchDashlet.clickOnConfigureDashletIcon();
        
        logger.info("Step 5: Verify 'Enter Search Term' window");
        Assert.assertEquals(configureSavedSearchPopUp.getPopUpTitle(), "Enter Search Term");
        Assert.assertTrue(configureSavedSearchPopUp.isSearchTermFieldDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isTitleFieldDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isSearchLimitDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isOkButtonDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isCloseButtonDisplayed());
        Assert.assertTrue(configureSavedSearchPopUp.isCancelButtonDisplayed());
        
        logger.info("Step 6: Close 'Enter Search Term' window");
        configureSavedSearchPopUp.clickCloseButton();
    }
}
