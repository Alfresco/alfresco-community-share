package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.network.JmxBuilder;
import org.alfresco.utility.web.annotation.PageObject;
import org.springframework.beans.factory.annotation.Autowired;

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

    public String getRootContentStore() throws Exception {
        String baseDir = (String) jmxBuilder.getJmxClient().readProperty("Alfresco:Name=GlobalProperties", "dir.root");
        String contentDir = (String) jmxBuilder.getJmxClient().readProperty("Alfresco:Name=GlobalProperties", "dir.contentstore");
        return contentDir.replace("${dir.root}", baseDir);
    }
}
