package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.ConfigureWebViewDashletPopUp;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WebViewTests extends ContextAwareWebTest{

    @Autowired
    UserDashboardPage userDashboardPage;
    
    @Autowired
    WebViewDashlet webViewDashlet;
    
    @Autowired
    ConfigureWebViewDashletPopUp configureWebViewPopUp;
    
    private String userName;
    
    @BeforeClass(alwaysRun = true)
    public void setup() throws DataPreparationException {
        super.setup();
        userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        userService.addDashlet(userName, password, UserDashlet.WEB_VIEW, DashletLayout.THREE_COLUMNS, 3, 1);

        setupAuthenticatedSession(userName, password);
    }
    
    @TestRail(id = "C2143")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void webViewDashlet()
    {  
        userDashboardPage.navigate(userName);
        
        LOG.info("Step 1: Verify 'Web View' dahslet");
        Assert.assertEquals(webViewDashlet.getDashletTitle(), "Web View");
        Assert.assertEquals(webViewDashlet.getDefaultMessage(), "No web page to display.");
        Assert.assertTrue(webViewDashlet.isConfigureDashletIconDisplayed());
        Assert.assertTrue(webViewDashlet.isHelpIconDisplayed(DashletHelpIcon.WEB_VIEW));
        
        LOG.info("Step 2: Click Help icon");
        webViewDashlet.clickOnHelpIcon(DashletHelpIcon.WEB_VIEW);
        Assert.assertTrue(webViewDashlet.isBalloonDisplayed());
        Assert.assertEquals(webViewDashlet.getHelpBalloonMessage(), "This dashlet shows the website of your choice. Click the edit icon on the dashlet "
                + "to change the web address.\nClicking the dashlet title opens the website in a separate window.");
        
        LOG.info("Step 3: Close ballon popup");
        webViewDashlet.closeHelpBalloon();
        Assert.assertFalse(webViewDashlet.isBalloonDisplayed());
        
        LOG.info("Step 4: Click 'Configure this dashlet' icon");
        webViewDashlet.clickOnConfigureDashletIcon();
        
        LOG.info("Step 5: Verify 'Configure Web View Dashlet' window");
        Assert.assertEquals(configureWebViewPopUp.getPopUpTitle(), "Configure Web View Dashlet");
        Assert.assertTrue(configureWebViewPopUp.isLinkTitleFieldDisplayed());
        Assert.assertTrue(configureWebViewPopUp.isUrlFieldDisplayed());
        Assert.assertTrue(configureWebViewPopUp.isOkButtonDisplayed());
        Assert.assertTrue(configureWebViewPopUp.isCloseButtonDisplayed());
        Assert.assertTrue(configureWebViewPopUp.isCancelButtonDisplayed());
        
        LOG.info("Step 6: Close 'Configure Web View Dashlet' window");
        configureWebViewPopUp.clickCloseButton();
        
    }
}
