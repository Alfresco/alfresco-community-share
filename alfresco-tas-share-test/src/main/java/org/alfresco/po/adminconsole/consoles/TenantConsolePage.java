package org.alfresco.po.adminconsole.consoles;

import org.alfresco.po.adminconsole.Notifications;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.network.JmxBuilder;
import org.alfresco.utility.web.annotation.PageObject;
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
    public String createTenant(String tenant, String password) throws Exception
    {
        return executeCommand(String.format("create %s %s", tenant, password));
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
    public String createTenant(String tenant, String password, String tenantRoot) throws Exception
    {
        return executeCommand(String.format("create %s %s %s", tenant, password, tenantRoot));
    }

    /**
     * Delete Tenant.
     *
     * @param tenant - tenant domain
     * @return - result after Delete tenant
     * @throws Exception
     */
    public String deleteTenant(String tenant) throws Exception
    {
        return executeCommand(String.format("delete %s", tenant));
    }

    /**
     * Disable Tenant.
     *
     * @param tenant - tenant domain
     * @return - result after Disabled tenant
     * @throws Exception
     */
    public String disableTenant(String tenant) throws Exception
    {
        return executeCommand(String.format("disable %s", tenant));
    }

    /**
     * Enable Tenant.
     *
     * @param tenant - tenant domain
     * @return - result after Enable tenant
     * @throws Exception
     */
    public String enableTenant(String tenant) throws Exception
    {
        return executeCommand(String.format("enable %s", tenant));
    }

    /**
     * Change Admin Password Tenant.
     *
     * @param tenant      - tenant domain
     * @param newPassword - tenant admin password (new)
     * @return - result after password of tenant was changed
     * @throws Exception
     */
    public String changeAdminPasswordTenant(String tenant, String newPassword) throws Exception
    {
        return executeCommand(String.format("changeAdminPassword %s %s", tenant, newPassword));
    }

    /**
     * Show Tenants.
     *
     * @return - result with all enabled/disabled tenants
     * @throws Exception
     */
    public String showTenants() throws Exception
    {
        return executeCommand("show tenants");
    }

    /**
     * Show Tenant.
     *
     * @param tenant - tenant domain
     * @return - result with only specified tenant
     * @throws Exception
     */
    public String showTenant(String tenant) throws Exception
    {
        return executeCommand(String.format("enable %s", tenant));
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
}
