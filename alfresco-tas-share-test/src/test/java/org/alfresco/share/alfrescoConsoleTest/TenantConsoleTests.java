package org.alfresco.share.alfrescoConsoleTest;

import org.alfresco.po.adminconsole.consoles.TenantConsolePage;
import org.alfresco.share.ContextAwareWebAdminConsoleTest;
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
public class TenantConsoleTests extends ContextAwareWebAdminConsoleTest
{
    @Autowired
    private TenantConsolePage tenantConsolePage;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void tenantConsoleTest()
    {
        String tenantName = "tenant" + RandomData.getRandomAlphanumeric();
        LOG.info("Step 1: Navigate to Tenant Console");

        tenantConsolePage.navigate();
        Assert.assertTrue(tenantConsolePage.getPageTitle().startsWith(language.translate("tenantConsole.PageTitle")), "Page title is not empty");

        Assert.assertEquals(tenantConsolePage.createTenant(tenantName, adminPassword).trim(),
            "created tenant: " + tenantName.toLowerCase(), tenantName + " has not been created");
        Assert.assertEquals(tenantConsolePage.disableTenant(tenantName).trim(),
            "Disabled tenant: " + tenantName.toLowerCase(), tenantName + " has not been disabled");
        Assert.assertEquals(tenantConsolePage.enableTenant(tenantName).trim(),
            "Enabled tenant: " + tenantName.toLowerCase(), tenantName + " has not been enabled");
        Assert.assertTrue(tenantConsolePage.showTenants().contains("Enabled  - Tenant: " + tenantName.toLowerCase()), tenantName + " is not listed");
        Assert.assertEquals(tenantConsolePage.deleteTenant(tenantName).trim(),
            "Deleted tenant: " + tenantName.toLowerCase(), tenantName + " has not been deleted");
    }
}
