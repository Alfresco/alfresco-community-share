package org.alfresco.share.adminTools.moduleBrowser;

import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.ModuleBrowserPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * UI tests for Admin Tools > Module browser page
 */
public class ModuleBrowserTests extends ContextAwareWebTest
{
    @Autowired
    private UserDashboardPage userDashboardPage;

    @Autowired
    private AdminToolsPage adminToolsPage;

    @Autowired
    private ModuleBrowserPage moduleBrowserPage;

    @Autowired
    private Toolbar toolbar;

    private UserModel user;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C9498")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void accessModuleBrowserPage()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        LOG.info("Step 1: Navigate to Admin Tools page");
        adminToolsPage.navigate();
        adminToolsPage.assertToolIsAvailable(language.translate("moduleBrowser.title"));

        LOG.info("Step 3: Click on Module Browser option from Tools");
        adminToolsPage.navigateToNodeFromToolsPanel("Module Browser", moduleBrowserPage);
        assertCurrentUrlContains("module-package");

        LOG.info("Step 4: Logout Admin user and Login with userName into Share");
        setupAuthenticatedSession(user);
        userDashboardPage.navigate(user);
        toolbar.assertAdminToolsIsNotDisplayed();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9499")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyTheAccessModuleBrowserPageInfo()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        moduleBrowserPage.navigate();
        moduleBrowserPage.assertModuleTableHeadersAreDisplayed()
            .assertGoogleDocsModuleIsPresent();
    }
}
