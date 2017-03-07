package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;
import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.ConfigureWebViewDashletPopUp;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WebViewDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    WebViewDashlet webViewDashlet;

    @Autowired
    ConfigureWebViewDashletPopUp configureWebViewPopUp;

    private String linkTitle;
    private String url;

    @TestRail(id = "C5449")
    @Test
    public void checkWebViewDashlet()

    {
        /**
         * Precondition
         */
        LOG.info("Create site C5449");
        String user = "C5449User" + DataUtil.getUniqueIdentifier();
        String siteName = "C5449SiteName" + DataUtil.getUniqueIdentifier();
        String description = "C5449SiteDescription" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user, user, user + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, user, siteName, description, Visibility.PUBLIC);
        siteService.addDashlet(user, user, siteName, SiteDashlet.WEB_VIEW, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(adminUser, adminPassword);

        /**
         * Test Steps
         */
        LOG.info("Step 1: Verify Web View dashlet displays Help Icon, Edit Icon and Message 'No web page to display'");
        siteDashboard.navigate(siteName);
        webViewDashlet.renderedPage();
        assertEquals(webViewDashlet.getDefaultMessage(), "No web page to display.");
        Assert.assertTrue(webViewDashlet.isConfigureDashletIconDisplayed());
        Assert.assertTrue(webViewDashlet.isHelpIconDisplayed(DashletHelpIcon.WEB_VIEW));

        LOG.info("Step 2: Click '?' icon");
        webViewDashlet.clickOnHelpIcon(DashletHelpIcon.WEB_VIEW);
        assertEquals(webViewDashlet.getHelpBalloonMessage(),
                "This dashlet shows the website of your choice. Click the edit icon on the dashlet to change the web address."
                        + "\nClicking the dashlet title opens the website in a separate window.");

        LOG.info("Step 3: Click 'X' icon.");
        webViewDashlet.closeHelpBalloon();
        Assert.assertFalse(webViewDashlet.isBalloonDisplayed());

        LOG.info("Step 4: Click 'Edit' icon.");
        webViewDashlet.clickOnConfigureDashletIcon();
        Assert.assertTrue(webViewDashlet.isConfigureWebViewWindowDisplayed());

        LOG.info("Step 5: Verify 'Configure Web View Dashlet' window.");
        assertEquals(webViewDashlet.getConfigureWebViewDashletWindowTitle(), "Configure Web View Dashlet");
        Assert.assertTrue(webViewDashlet.isLinkTitleDisplayedOnConfigureWebViewWindow());
        Assert.assertTrue(webViewDashlet.isURLFieldDisplayedOnConfigureWebViewWindow());
        Assert.assertTrue(configureWebViewPopUp.isOkButtonDisplayed());
        Assert.assertTrue(configureWebViewPopUp.isCancelButtonDisplayed());
        Assert.assertTrue(configureWebViewPopUp.isCloseButtonDisplayed());

        LOG.info("Step 6: Click 'X' button.");
        configureWebViewPopUp.clickCloseButton();
        Assert.assertFalse(webViewDashlet.isConfigureWebViewWindowDisplayed());

    }

    @TestRail(id = "C5450")
    @Test

    public void configureWebViewDashlet()
    {
        linkTitle = "AlfrescoTest";
        url = "http://alfresco.com/";

        /**
         * Precondition
         */
        LOG.info("Create site C5450");
        String user = "C5450User" + DataUtil.getUniqueIdentifier();
        String siteName = "C5450SiteName" + DataUtil.getUniqueIdentifier();
        String description = "C5450SiteDescription" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user, user, user + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, user, siteName, description, Visibility.PUBLIC);
        siteService.addDashlet(user, user,   siteName, SiteDashlet.WEB_VIEW, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(adminUser, adminPassword);

        /**
         * Test Steps
         */
        LOG.info("Step 1: Navigate to site and Click Edit icon for Web View dashlet.");
        siteDashboard.navigate(siteName);
        webViewDashlet.renderedPage();
        webViewDashlet.clickOnConfigureDashletIcon();

        LOG.info("Step 2: Provide Input for fields Link Title and URL and click Ok");
        webViewDashlet.configureWebViewDashlet(linkTitle, url);

        LOG.info("Step 3: Verify that user is redirected to Site Dashboard and the information displayed by the Web View Dashlet");
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboard.getRelativePath(), expectedRelativePath, "User is redirected to site dashboard");
        assertEquals(webViewDashlet.getDashletTitle(), linkTitle, "Web View Dashlet title is updated.");
        String expectedSource = browser.findElement(By.cssSelector("iframe[class$='iframe-body']")).getAttribute("src");
        assertEquals(expectedSource, url);
    }

    @TestRail(id = "C5453")
    @Test

    public void configureWebViewDashletCancel()
    {
        linkTitle = "test";
        url = "http://test.com";
        /**
         * Precondition
         */
        String user = "C5453User" + DataUtil.getUniqueIdentifier();
        String siteName = "C5453SiteName" + DataUtil.getUniqueIdentifier();
        String description = "C5453SiteDescription" + DataUtil.getUniqueIdentifier();

        LOG.info("Create Site C5453");
        userService.create(adminUser, adminPassword, user, user, user + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, user, siteName, description, Visibility.PUBLIC);
        siteService.addDashlet(user, user, siteName, SiteDashlet.WEB_VIEW, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(adminUser, adminPassword);

        /**
         * Test Steps
         */
        LOG.info("Step 1: Navigate to site dashboard and click on Edit icon for 'Web View' dashlet.");
        siteDashboard.navigate(siteName);
        webViewDashlet.renderedPage();
        webViewDashlet.clickOnConfigureDashletIcon();

        LOG.info("Step 2: Provide Input for fields Link Title and URL and click Cancel");
        configureWebViewPopUp.setLinkTitleField(linkTitle);
        configureWebViewPopUp.setUrlField(url);
        configureWebViewPopUp.clickCancelButton();

        LOG.info("Step 3: Verify that user is redirected to Site Dashboard and the information displayed by the Web View Dashlet");
        browser.waitInSeconds(1);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboard.getRelativePath(), expectedRelativePath, "User is redirected to site dashboard");
        assertEquals(webViewDashlet.getDefaultMessage(), "No web page to display.");
        assertEquals(webViewDashlet.getDashletTitle(), "Web View");
    }
    
    @TestRail(id = "C5459")
    @Test
    
    public void configureWebViewDashletDisplayHTTPSPages()
    {
        linkTitle = "test";
        url = "https://www.alfresco.com/products";

        /**
         * Precondition
         */
        String user = "C5459User" + DataUtil.getUniqueIdentifier();
        String siteName = "C5459SiteName" + DataUtil.getUniqueIdentifier();
        String description = "C5459SiteDescription" + DataUtil.getUniqueIdentifier();
        
        LOG.info("Create Site C5459");
        userService.create(adminUser, adminPassword, user, user, user + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, user, siteName, description, Visibility.PUBLIC);
        siteService.addDashlet(user, user, siteName, SiteDashlet.WEB_VIEW, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(adminUser, adminPassword);
        
        LOG.info("Step 1: Navigate to site dashboard and click on Edit icon for 'Web View' dashlet.");
        siteDashboard.navigate(siteName);
        webViewDashlet.renderedPage();
        webViewDashlet.clickOnConfigureDashletIcon();
        
        LOG.info("Step 2: Provide Input for fields Link Title and URL and click OK");
        webViewDashlet.configureWebViewDashlet(linkTitle, url);
        
        LOG.info("Step 3: Verify that user is redirected to Site Dashboard and the information displayed by the Web View Dashlet");
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboard.getRelativePath(), expectedRelativePath, "User is redirected to site dashboard");
        assertEquals(webViewDashlet.getDashletTitle(), linkTitle);
        String expectedSource = browser.findElement(By.cssSelector("iframe[class$='iframe-body']")).getAttribute("src");
        assertEquals(expectedSource, url);
    }
    
}
