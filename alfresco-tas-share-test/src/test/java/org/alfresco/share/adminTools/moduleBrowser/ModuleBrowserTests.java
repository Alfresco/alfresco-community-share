package org.alfresco.share.adminTools.moduleBrowser;

import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.ModuleBrowserPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Created by Mirela Tifui on 11/28/2016.
 */
public class ModuleBrowserTests extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    AdminToolsPage adminToolsPage;

    @Autowired
    ModuleBrowserPage moduleBrowserPage;

    @Autowired
    Toolbar toolbar;

    private String userName = "C9498User"+ RandomData.getRandomAlphanumeric();

    @BeforeClass(alwaysRun = true)

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
    }

    @TestRail(id ="C9498")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void accessModuleBrowserPage()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        LOG.info("Step 1: Navigate to Admin Tools page");
        adminToolsPage.navigateByMenuBar();
        Assert.assertEquals(adminToolsPage.getPageTitle(), "Alfresco » Admin Tools", "Admin Tools page is not displayed");

        LOG.info("Step 2: Check and confirm that Module Browser is available under Admin tools");
        Assert.assertTrue(adminToolsPage.isToolAvailable("Module Browser"), "Module Browser is not displayed");

        LOG.info("Step 3: Click on Module Browser option from Tools");
        adminToolsPage.navigateToNodeFromToolsPanel("Module Browser", moduleBrowserPage);
        Assert.assertTrue(getBrowser().getCurrentUrl().contains("module-package"), "User User is not on Module Browser Page");

        LOG.info("Step 4: Logout Admin user and Login with userName into Share");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName, password);
        userDashboardPage.navigate(userName);
        LOG.info("Step 5: Check and confirm that AdminTools  link is not available on the Share page");
        Assert.assertFalse(toolbar.isAdminToolsDisplayed(), "Admin Tools is displayed for user without Admin rights");
        cleanupAuthenticatedSession();
    }

    @TestRail(id ="C9499")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void verifyTheAccessModuleBrowserPageInfo()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        adminToolsPage.navigateByMenuBar();

        LOG.info("Step 1: Click Module Browser option on the Admin Tools page");
        adminToolsPage.navigateToNodeFromToolsPanel("Module Browser", moduleBrowserPage);
        Assert.assertTrue(moduleBrowserPage.isTitleHeaderDisplayed(), "Title table header is not available on the Module Browser Page");
        Assert.assertTrue(moduleBrowserPage.isDescriptionHeaderDisplayed(), "Description table header is not available on the Module Browser Page");
        Assert.assertTrue(moduleBrowserPage.isVersionHeaderDisplayed(), "Version table header is not available on the Module Browser Page");

        LOG.info("Step 2: Check module availability on the Module Browser Page");

        Assert.assertTrue(moduleBrowserPage.isModuleAvailable("Alfresco / Google Docs Share Module"), "Google Docs Share Module is not available");
    }
}
