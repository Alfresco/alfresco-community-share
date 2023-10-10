package org.alfresco.po.adminconsole.general;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SystemSettingsPage extends AdminConsolePage<SystemSettingsPage>
{
    public static final String SYSTEM_SETTINGS_OBJECT = "Alfresco:Type=Configuration,Category=sysAdmin,id1=default";
    @RenderWebElement
    @FindBy (className = "title")
    WebElement title;
    @RenderWebElement
    @FindBy (className = "intro")
    WebElement intro;
    @RenderWebElement
    @FindBy (className = "info")
    WebElement info;

    @Override
    public String getInfoPage()
    {
        return info.getText();
    }

    @Override
    public String getIntroPage()
    {
        return intro.getText();
    }

    @Override
    public String getPageTitle()
    {
        return title.getText();
    }

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-systemsettings";
    }

    public enum SYSTEM_SETTINGS_LABELS
    {
        REPOSITORY_CONTEXT("Repository Context:", "alfresco.context"),
        REPOSITORY_HOSTNAME("Repository Hostname:", "alfresco.host"),
        SERVER_ALLOW_WRITES("Server Allow Writes:", "server.allowWrite"),
        ALFRESCO_PROTOCOL("Protocol:", "alfresco.protocol"),
        ALFRESCO_PORT("Port:", "alfresco.port"),
        ALLOWED_USERS("Allowed Users:", "server.allowedusers"),
        MAXIMUM_USERS("Maximum Users:", "server.maxusers"),
        SHARE_CONTEXT("Share Context:", "share.context"),
        SHARE_PORT("Port:", "share.port"),
        SHARE_PROTOCOL("Protocol:", "share.protocol"),
        SHARE_HOSTNAME("Share Hostname:", "share.host"),
        SITE_PUBLIC_GROUPS("Site Public Group:", "site.public.group");

        private String label, jmxAttribute;

        SYSTEM_SETTINGS_LABELS(String label, String jmxAttribute)
        {
            this.label = label;
            this.jmxAttribute = jmxAttribute;
        }

        public String getLabel()
        {
            return label;
        }

        public String getJmxAttribute()
        {
            return jmxAttribute;
        }
    }

}
