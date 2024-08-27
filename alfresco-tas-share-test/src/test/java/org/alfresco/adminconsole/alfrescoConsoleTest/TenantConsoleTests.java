package org.alfresco.adminconsole.alfrescoConsoleTest;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.adminconsole.consoles.TenantConsolePages;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/2/2017.
 */
@Slf4j
public class TenantConsoleTests extends BaseTest
{
    private TenantConsolePages tenantConsolePages;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void tenantConsoleTest()
    {
        tenantConsolePages = new TenantConsolePages(webDriver);
        String tenantName = "tenant" + RandomData.getRandomAlphanumeric();
        log.info("Step 1: Navigate to Tenant Console");

        tenantConsolePages.navigate();
        Assert.assertTrue(tenantConsolePages.getPageTitle().startsWith(language.translate("tenantConsole.PageTitle")), "Page title is not empty");

        Assert.assertEquals(tenantConsolePages.createTenant(tenantName, "admin").trim(),
            "created tenant: " + tenantName.toLowerCase(), tenantName + " has not been created");
        Assert.assertEquals(tenantConsolePages.disableTenant(tenantName).trim(),
            "Disabled tenant: " + tenantName.toLowerCase(), tenantName + " has not been disabled");
        Assert.assertEquals(tenantConsolePages.enableTenant(tenantName).trim(),
            "Enabled tenant: " + tenantName.toLowerCase(), tenantName + " has not been enabled");
        Assert.assertTrue(tenantConsolePages.showTenants().contains("Enabled  - Tenant: " + tenantName.toLowerCase()), tenantName + " is not listed");
        Assert.assertEquals(tenantConsolePages.deleteTenant(tenantName).trim(),
            "Deleted tenant: " + tenantName.toLowerCase(), tenantName + " has not been deleted");
    }
}
