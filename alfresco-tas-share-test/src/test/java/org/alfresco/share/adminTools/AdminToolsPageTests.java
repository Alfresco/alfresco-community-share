package org.alfresco.share.adminTools;

import junit.framework.Assert;
import org.alfresco.common.EnvProperties;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/3/2017.
 */
public class AdminToolsPageTests extends ContextAwareWebTest
{
    @Autowired
    AdminToolsPage adminToolsPage;
    @Autowired
    EnvProperties envProperties;
    @Autowired
    SystemErrorPage systemErrorPage;
    private String userName = "adminCUser-" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + "@test.com", "admin", "console");
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp()
    {
        userService.delete(adminUser, adminPassword, userName);
    }

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void adminToolsAccessTest()
    {
        LOG.info("Step 1: Authenticate as non-admin user and check Admin Tools page is not available");
        setupAuthenticatedSession(userName, password);
        getBrowser().navigate().to(envProperties.getShareUrl() + "/page/console/admin-console/application");
        Assert.assertTrue(systemErrorPage.getErrorHeader().contains("Something's wrong with this page..."));
        cleanupAuthenticatedSession();
        LOG.info("Step 2; Authenticate as admin and check that Admin Tools page is available");
        setupAuthenticatedSession(adminUser, adminPassword);
        adminToolsPage.navigate();
        Assert.assertEquals(adminToolsPage.getPageTitle(), "Alfresco » Admin Tools");
    }
}
