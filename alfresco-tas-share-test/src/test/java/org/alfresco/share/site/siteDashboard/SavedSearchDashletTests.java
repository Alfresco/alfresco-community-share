package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.ConfigureSavedSearchDashletPopUp;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SavedSearchDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;
    
    @Autowired
    SavedSearchDashlet savedSearchDashlet;
    
    @Autowired
    ConfigureSavedSearchDashletPopUp configureSavedSearchPopUp;

    private String userName = "User" + DataUtil.getUniqueIdentifier();
    private String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
    
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        siteService.addDashlet(userName, DataUtil.PASSWORD, siteName, SiteDashlet.SAVED_SEARCH, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }
    
    @TestRail(id = "C2787")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void savedSearchDashlet()
    {
        siteDashboardPage.navigate(siteName);
        
        LOG.info("Step 1: Verify 'Saved Search' dahslet");
        assertEquals(savedSearchDashlet.getDashletTitle(), "Saved Search");
        assertEquals(savedSearchDashlet.getDefaultMessage(), "No results found.");
        assertTrue(savedSearchDashlet.isConfigureDashletIconDisplayed());
        assertTrue(savedSearchDashlet.isHelpIconDisplayed(DashletHelpIcon.SAVED_SEARCH));

        LOG.info("Step 2: Click Help icon");
        savedSearchDashlet.clickOnHelpIcon(DashletHelpIcon.SAVED_SEARCH);
        assertTrue(savedSearchDashlet.isBalloonDisplayed());
        assertEquals(savedSearchDashlet.getHelpBalloonMessage(), "Use this dashlet to set up a search and view the results."
                + "\nConfigure the dashlet to save the search and set the title text of the dashlet."
                + "\nOnly a Site Manager can configure the search and title - this dashlet is ideal for generating report views in a site.");

        LOG.info("Step 3: Close balloon popup");
        savedSearchDashlet.closeHelpBalloon();
        assertFalse(savedSearchDashlet.isBalloonDisplayed());

        LOG.info("Step 4: Click 'Configure this dashlet' icon");
        savedSearchDashlet.clickOnConfigureDashletIcon();

        LOG.info("Step 5: Verify 'Enter Search Term' window");
        assertEquals(configureSavedSearchPopUp.getPopUpTitle(), "Enter Search Term");
        assertTrue(configureSavedSearchPopUp.isSearchTermFieldDisplayed());
        assertTrue(configureSavedSearchPopUp.isTitleFieldDisplayed());
        assertTrue(configureSavedSearchPopUp.isSearchLimitDisplayed());
        assertTrue(configureSavedSearchPopUp.isOkButtonDisplayed());
        assertTrue(configureSavedSearchPopUp.isCloseButtonDisplayed());
        assertTrue(configureSavedSearchPopUp.isCancelButtonDisplayed());

        LOG.info("Step 6: Close 'Enter Search Term' window");
        configureSavedSearchPopUp.clickCloseButton();
    }
}
