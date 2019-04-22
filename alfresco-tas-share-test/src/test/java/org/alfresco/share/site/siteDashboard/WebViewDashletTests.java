package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.ConfigureWebViewDashletPopUp;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

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
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void checkWebViewDashlet()

    {
        /**
         * Precondition
         */
        LOG.info("Create site C5449");
        String user = String.format("C5449User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("C5449SiteName%s", RandomData.getRandomAlphanumeric());
        String description = String.format("C5449SiteDescription%s", RandomData.getRandomAlphanumeric());

        userService.create(adminUser, adminPassword, user, user, user + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, user, siteName, description, SiteService.Visibility.PUBLIC);
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

        userService.delete(adminUser,adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser,adminPassword,siteName );


    }

    @TestRail(id = "C5450")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void configureWebViewDashlet()
    {
        linkTitle = "AlfrescoTest";
        url = "http://alfresco.com/";

        /**
         * Precondition
         */
        LOG.info("Create site C5450");
        String user = String.format("C5450User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("C5450SiteName%s", RandomData.getRandomAlphanumeric());
        String description = String.format("C5450SiteDescription%s", RandomData.getRandomAlphanumeric());

        userService.create(adminUser, adminPassword, user, user, user + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, user, siteName, description, SiteService.Visibility.PUBLIC);
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
        String expectedSource = getBrowser().findElement(By.cssSelector("iframe[class$='iframe-body']")).getAttribute("src");
        assertEquals(expectedSource, url);

        userService.delete(adminUser,adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser,adminPassword,siteName );
    }

    @TestRail(id = "C5453")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void configureWebViewDashletCancel()
    {
        linkTitle = "test";
        url = "http://test.com";
        /**
         * Precondition
         */
        String user = String.format("C5453User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("C5453SiteName%s", RandomData.getRandomAlphanumeric());
        String description = String.format("C5453SiteDescription%s", RandomData.getRandomAlphanumeric());

        LOG.info("Create Site C5453");
        userService.create(adminUser, adminPassword, user, user, user + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, user, siteName, description, SiteService.Visibility.PUBLIC);
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
        getBrowser().waitInSeconds(1);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboard.getRelativePath(), expectedRelativePath, "User is redirected to site dashboard");
        assertEquals(webViewDashlet.getDefaultMessage(), "No web page to display.");
        assertEquals(webViewDashlet.getDashletTitle(), "Web View");

        userService.delete(adminUser,adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser,adminPassword,siteName );
    }
    
    @TestRail(id = "C5459")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    
    public void configureWebViewDashletDisplayHTTPSPages()
    {
        linkTitle = "test";
        url = "https://www.alfresco.com/products";

        /**
         * Precondition
         */
        String user = String.format("C5459User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("C5459SiteName%s", RandomData.getRandomAlphanumeric());
        String description = String.format("C5459SiteDescription%s", RandomData.getRandomAlphanumeric());
        
        LOG.info("Create Site C5459");
        userService.create(adminUser, adminPassword, user, user, user + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, user, siteName, description, SiteService.Visibility.PUBLIC);
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
        String expectedSource = getBrowser().findElement(By.cssSelector("iframe[class$='iframe-body']")).getAttribute("src");
        assertEquals(expectedSource, url);

        userService.delete(adminUser,adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser,adminPassword,siteName );
    }
    
}
