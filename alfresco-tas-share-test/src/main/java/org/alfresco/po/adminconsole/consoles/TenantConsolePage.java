package org.alfresco.po.adminconsole.consoles;

import org.alfresco.po.adminconsole.Notifications;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.network.JmxBuilder;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

@PageObject
public class TenantConsolePage extends ConsolePage<TenantConsolePage>
{

    @Autowired
    private JmxBuilder jmxBuilder;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/admin/admin-tenantconsole";
    }

    /**
     * Gets root content store directory for tenant.
     *
     * @return - tenant location
     * @throws Exception
     */
    public String getRootContentStore() throws Exception
    {
        String baseDir = (String) jmxBuilder.getJmxClient().readProperty("Alfresco:Name=GlobalProperties", "dir.root");
        String contentDir = (String) jmxBuilder.getJmxClient().readProperty("Alfresco:Name=GlobalProperties", "dir.contentstore");
        return contentDir.replace("${dir.root}", baseDir);
    }

    /**
     * Create Tenant.
     *
     * @param tenant   - tenant domain
     * @param password - tenant admin password
     * @return - result after creation
     * @throws Exception
     */
    public String createTenant(String tenant, String password)
    {
        executeCommand(String.format("create %s %s", tenant, password));
        waitForResult(String.format("created tenant: %s", tenant.toLowerCase()));
        return results.getText();
    }

    /**
     * Create Tenant with Root.
     *
     * @param tenant     - tenant domain
     * @param password   - tenant admin password
     * @param tenantRoot - root content store dir
     * @return - result after creation
     * @throws Exception
     */
    public String createTenant(String tenant, String password, String tenantRoot)
    {
        executeCommand(String.format("create %s %s %s", tenant, password, tenantRoot));
        waitForResult(String.format("created tenant: %s", tenant));
        return results.getText();
    }

    /**
     * Delete Tenant.
     *
     * @param tenant - tenant domain
     * @return - result after Delete tenant
     * @throws Exception
     */
    public String deleteTenant(String tenant)
    {
        executeCommand(String.format("delete %s", tenant));
        waitForResult(String.format("Deleted tenant: %s", tenant.toLowerCase()));
        return results.getText();
    }

    /**
     * Disable Tenant.
     *
     * @param tenant - tenant domain
     * @return - result after Disabled tenant
     * @throws Exception
     */
    public String disableTenant(String tenant)
    {
        executeCommand(String.format("disable %s", tenant));
        waitForResult(String.format("Disabled tenant: %s", tenant.toLowerCase()));
        return results.getText();
    }

    /**
     * Enable Tenant.
     *
     * @param tenant - tenant domain
     * @return - result after Enable tenant
     * @throws Exception
     */
    public String enableTenant(String tenant)
    {
        executeCommand(String.format("enable %s", tenant));
        waitForResult(String.format("Enabled tenant: %s", tenant.toLowerCase()));
        return results.getText();
    }

    /**
     * Change Admin Password Tenant.
     *
     * @param tenant      - tenant domain
     * @param newPassword - tenant admin password (new)
     * @return - result after password of tenant was changed
     * @throws Exception
     */
    public String changeAdminPasswordTenant(String tenant, String newPassword)
    {
        executeCommand(String.format("changeAdminPassword %s %s", tenant, newPassword));
        return getResults();
    }

    /**
     * Show Tenants.
     *
     * @return - result with all enabled/disabled tenants
     * @throws Exception
     */
    public String showTenants()
    {
        executeCommand("show tenants");
        Utility.waitToLoopTime(1, "Wait for show tenants command");
        return browser.waitUntilElementVisible(results).getText();
    }

    /**
     * Show Tenant.
     *
     * @param tenant - tenant domain
     * @return - result with only specified tenant
     * @throws Exception
     */
    public String showTenant(String tenant)
    {
        executeCommand(String.format("enable %s", tenant));
        return results.getText();
    }

    /**
     * Verifies 'ShowTenants' Result.
     *
     * @param action - command used (enable/disable/delete)
     * @param result - result displayed after cmd 'show tenants'
     * @param tenant - list with tenants username created
     * @param root   - roots where tenants are created
     */
    public void verifyShowTenantsResult(ConsoleCommand action, String result, List<String> tenant, String... root)
    {

        for (int i = 0; i < tenant.size(); i++)
        {
            switch (action)
            {
                case ENABLE:
                    Assert.assertTrue(result.contains(String.format(Notifications.ENABLED_TENANT_RESULT, tenant.get(i),
                        Arrays.asList(root).get(i))),
                        String.format("Tenant '%s' is found in Result and is Enabled.", tenant.get(i)));
                    break;
                case DISABLE:
                    Assert.assertTrue(result.contains(String.format(Notifications.DISABLED_TENANT_RESULT, tenant.get(i),
                        Arrays.asList(root).get(i))),
                        String.format("Tenant '%s' is found in Result and is Disabled.", tenant.get(i)));
                    break;
                case DELETE:
                    Assert.assertFalse(result.contains(String.format(Notifications.ENABLED_TENANT_RESULT, tenant.get(i),
                        Arrays.asList(root).get(i))),
                        String.format("Tenant '%s' is NOT found in Result.", tenant.get(i)));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid console command: " + action);
            }
        }
    }

    /**
     * Verifies tenant 'Result' after a specific command.
     *
     * @param action - command used (enable/disable/delete/import/export/changeAdminPassword)
     * @param result - result displayed after cmd execution
     * @param tenant - username of the tenant
     */
    public void verifyTenantResult(ConsoleCommand action, String result, UserModel tenant)
    {
        switch (action)
        {
            case ENABLE:
                Assert.assertEquals(result, String.format(Notifications.ENABLED_TENANT, tenant.getUsername()),
                    String.format("Tenant '%s' is Enabled.", tenant.getUsername()));
                break;
            case DISABLE:
                Assert.assertEquals(result, String.format(Notifications.DISABLED_TENANT, tenant.getUsername()),
                    String.format("Tenant '%s' is Disabled.", tenant.getUsername()));
                break;
            case CREATE:
                Assert.assertEquals(result, String.format(Notifications.CREATE_TENANT, tenant.getUsername()),
                    String.format("Tenant '%s' is Created.", tenant.getUsername()));
                break;
            case DELETE:
                Assert.assertEquals(result, String.format(Notifications.DELETE_TENANT, tenant.getUsername()),
                    String.format("Tenant '%s' is Deleted.", tenant.getUsername()));
                break;
            case IMPORT:
                Assert.assertEquals(result, String.format(Notifications.IMPORT_TENANT, tenant.getUsername()),
                    String.format("Tenant '%s' is Imported.", tenant.getUsername()));
                break;
            case EXPORT:
                Assert.assertEquals(result, String.format(Notifications.EXPORT_TENANT, tenant.getUsername()),
                    String.format("Tenant '%s' is Exported.", tenant.getUsername()));
                break;
            case CHANGE_ADMIN_PASSWORD:
                Assert.assertEquals(result, String.format("", "Password has been changed."));
                break;
            default:
                throw new IllegalArgumentException("Invalid console command: " + action);
        }
    }

    public enum ConsoleCommand
    {
        CREATE("create"), ENABLE("enable"), DISABLE("disable"), DELETE("delete"),
        SHOW_TENANTS("show tenants"), SHOW_TENANT("show tenant"), CHANGE_ADMIN_PASSWORD("changeAdminPassword"),
        EXPORT("export"), IMPORT("import");

        private String cmd;

        ConsoleCommand(String cmd)
        {
            this.cmd = cmd;
        }
    }
}
