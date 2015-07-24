/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import org.alfresco.po.share.site.document.SharedFilesPage;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify dashboard page elements are in place.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
public class DashBoardPageTest extends AbstractTest
{
    /**
     * Test process of accessing dashboard page.
     *
     * @throws Exception
     */
    
    private String userGetStartedPanel = "userGetStartedPanel" + System.currentTimeMillis();
    DashBoardPage dashBoard;

    @Test(groups = "alfresco-one")
    public void loadDashBoard() throws Exception
    {
        dashBoard = loginAs(username, password);

        Assert.assertTrue(dashBoard.isLogoPresent());
        // TODO remove this condition once bug Cloud-881 is fixed
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (!version.isCloud())
        {
            Assert.assertTrue(dashBoard.titlePresent());
            Assert.assertEquals("Administrator Dashboard", dashBoard.getPageTitle());
        }
        String copyright = dashBoard.getCopyRight();
        Assert.assertTrue(copyright.contains("Alfresco Software"));
    }

    @Test(dependsOnMethods = "loadDashBoard", groups = "alfresco-one")
    public void refreshPage() throws Exception
    {
        //Were already logged in from the previous test.
        drone.refresh();
        DashBoardPage dashBoard = drone.getCurrentPage().render();
        Assert.assertNotNull(dashBoard);
    }

    @Test(dependsOnMethods = "refreshPage", groups = "alfresco-one")
    public void checkTopLogoUrl()
    {
        DashBoardPage dashBoardPage = drone.getCurrentPage().render();
        Assert.assertNotNull(dashBoardPage.getTopLogoUrl());
    }

    @Test(dependsOnMethods = "refreshPage", groups = "alfresco-one")
    public void checkFooterLogoUrl()
    {
        DashBoardPage dashBoardPage = drone.getCurrentPage().render();
        Assert.assertNotNull(dashBoardPage.getFooterLogoUrl());
    }

    @Test(dependsOnMethods = "checkFooterLogoUrl", groups = "alfresco-one")
    public void checkOpenAboutPopUpLogo()
    {
        DashBoardPage dashBoardPage = drone.getCurrentPage().render();
        AboutPopUp aboutPopUp = dashBoardPage.openAboutPopUp();
        Assert.assertNotNull(aboutPopUp.getLogoUrl());
    }
    
    @Test(dependsOnMethods = "checkOpenAboutPopUpLogo", groups = "alfresco-one")
    public void clickViewTutorialsLink()
    {
        drone.refresh();
        DashBoardPage dashBoardPage = drone.getCurrentPage().render();
        dashBoardPage.clickTutorialsLink();
        String mainWindow = drone.getWindowHandle();
        waitInSeconds(3);
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (!version.isCloud())
        {
            Assert.assertTrue(isWindowOpened("Alfresco One video tutorials"));
        }
        else
        {
            Assert.assertTrue(isWindowOpened("Video tutorials"));
        }      
        drone.closeWindow();
        drone.switchToWindow(mainWindow);        
    }
    
    @Test(dependsOnMethods = "clickViewTutorialsLink", groups = "Enterprise-only")
    public void checkVersionsFromPopUpLogo()
    {
        drone.refresh();
        DashBoardPage dashBoardPage = drone.getCurrentPage().render();
        AboutPopUp aboutPopUp = dashBoardPage.openAboutPopUp();
        String versionsDetail = aboutPopUp.getVersionsDetail();
        Assert.assertTrue(aboutPopUp.isVersionsDetailDisplayed());
        Assert.assertTrue(versionsDetail.contains("Aikau") && versionsDetail.contains("Spring Surf") && versionsDetail.contains("Spring WebScripts"));
    }
    
    @Test(dependsOnMethods = "refreshPage", enabled = false, groups = "nonGrid")
    public void testKeysForHeaderBar() throws Exception
    {
        drone.refresh();
        dashBoard.inputFromKeyborad(Keys.TAB);
        dashBoard.inputFromKeyborad(Keys.ARROW_RIGHT);
        dashBoard.inputFromKeyborad(Keys.ARROW_RIGHT);
        dashBoard.inputFromKeyborad(Keys.RETURN);
        Assert.assertTrue(drone.getCurrentPage().render() instanceof SharedFilesPage);
    }
    
    /**
     * Verifies that Get Started Panel can be removed from user dashboard page
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "checkVersionsFromPopUpLogo", groups = "Enterprise-only")
    public void testHideGetStartedPanelFromUserDashboard() throws Exception
    {
        drone.refresh();
        
        ShareUtil.logout(drone);
        createEnterpriseUser(userGetStartedPanel);
                
        dashBoard = loginAs(userGetStartedPanel, UNAME_PASSWORD).render();
        
        HideGetStartedPanel  hideGetStartedPanel = dashBoard.clickOnHideGetStartedPanelButton().render();
        dashBoard = hideGetStartedPanel.clickOnHideGetStartedPanelOkButton().render();
 
        Assert.assertFalse(dashBoard.panelExists(dashBoard.getGetStartedPanelTitle()));
                
    }
    
    /**
     * Verifies that Get Started Panel on user dashboard page can be restored from Customise User dashboard page
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "testHideGetStartedPanelFromUserDashboard", groups = "Enterprise-only")
    public void testShowGetStartedPanelFromCustomiseUserDashboard() throws Exception
    {
        //go to Customise User Dashboard page and click on Show radio button
        CustomiseUserDashboardPage customiseUserDashboardPage = dashBoard.getNav().selectCustomizeUserDashboard().render();
        
        //click on show get started panel radio button
        customiseUserDashboardPage = customiseUserDashboardPage.clickOnShowOnDashboardRadioButton().render();
        
        //click on OK button
        dashBoard = customiseUserDashboardPage.selectOk().render();
        
        //check that get Started Panel is restored on the user dashboard
        Assert.assertTrue(dashBoard.panelExists(dashBoard.getGetStartedPanelTitle()));
                
    }
    
    @Test(dependsOnMethods = "testShowGetStartedPanelFromCustomiseUserDashboard", groups = "Enterprise-only")
    public void testHideGetStartedPanelFromCustomiseUserDashboard() throws Exception
    {
        //go to Customise User Dashboard page and click on Hide radio button 
        CustomiseUserDashboardPage customiseUserDashboardPage = dashBoard.getNav().selectCustomizeUserDashboard().render();
        
        //click on hide get started panel radio button
        customiseUserDashboardPage = customiseUserDashboardPage.clickOnHideOnDashboardRadioButton().render();
        
        //click on OK button
        dashBoard = customiseUserDashboardPage.selectOk().render();
        
        //check that get Started Panel is not present on the user dashboard
        Assert.assertFalse(dashBoard.panelExists(dashBoard.getGetStartedPanelTitle()));
                
    }
}

