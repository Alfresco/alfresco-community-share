package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.web.annotation.PageObject;

@PageObject
public class TenantConsolePage extends ConsolePage<TenantConsolePage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/admin/admin-tenantconsole";
    }
}
