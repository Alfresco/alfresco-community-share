package org.alfresco.share.alfrescoConsoleTest;

import org.alfresco.po.adminconsole.consoles.TenantConsolePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/2/2017.
 */
public class TenantConsoleTests extends ContextAwareWebTest
{
    @Autowired
    TenantConsolePage tenantConsolePage;

    private String userName = "tenantUser" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + "@test.com", "tenant", "user");
    }

    @AfterClass (alwaysRun = true)
    public void testCleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
    }

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void tenantConsoleTest() throws Exception
    {
        String tenantName = "tenant" + RandomData.getRandomAlphanumeric();
        LOG.info("Step 1: Navigate to Tenant Console");
        tenantConsolePage.setBrowser(getBrowser());
        tenantConsolePage.navigate();
        Assert.assertEquals(tenantConsolePage.getPageTitle(), language.translate("tenantConsole.PageTitle"), "Page title is not as accepted");
        LOG.info("Step 2: Create tenant");
        tenantConsolePage.createTenant(tenantName, adminPassword);
        Assert.assertEquals(tenantConsolePage.getResults().trim(), "created tenant: " + tenantName.toLowerCase(), tenantName + " has not been created");
        tenantConsolePage.disableTenant(tenantName);
        Assert.assertEquals(tenantConsolePage.getResults().trim(), "Disabled tenant: " + tenantName.toLowerCase(), tenantName + " has not been disabled");
        tenantConsolePage.enableTenant(tenantName);
        Assert.assertEquals(tenantConsolePage.getResults().trim(), "Enabled tenant: " + tenantName.toLowerCase(), tenantName + " has not been enabled");
        tenantConsolePage.showTenants();
        Assert.assertTrue(tenantConsolePage.getResults().contains("Enabled  - Tenant: " + tenantName.toLowerCase()), tenantName + " is not listed");
        tenantConsolePage.deleteTenant(tenantName);
        Assert.assertEquals(tenantConsolePage.getResults().trim(), "Deleted tenant: " + tenantName.toLowerCase(), tenantName + " has not been deleted");
    }
}
