package org.alfresco.po.adminconsole.virtualfilesystems;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class FileServersPage extends AdminConsolePage<FileServersPage>
{
    public static final String FILE_SERVERS_OBJECT = "Alfresco:Type=Configuration,Category=fileServers,id1=default";
    @RenderWebElement
    @FindBy (className = "intro")
    WebElement intro;

    @Override
    public String getInfoPage()
    {
        return "";
    }

    @Override
    public String getIntroPage()
    {
        return intro.getText();
    }

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-fileservers";
    }

    public enum FILE_SERVERS_FIELDS
    {
        FILE_SYSTEM_NAME("File System Name:", "filesystem.name"),
        CIFS_ENABLED("CIFS Enabled:", "cifs.enabled"),
        SERVER_NAME("Server Name:", "cifs.serverName"),
        HOST_ANNOUNCE("Host Announce:", "cifs.hostannounce"),
        SESSION_TIMEOUT("Session Timeout (seconds):", "cifs.sessionTimeout"),
        DOMAIN("Domain:", "cifs.domain"),
        FTP_ENABLED("FTP Enabled:", "ftp.enabled"),
        PORT("Port:", "ftp.port"),
        DATAPORT_FROM("Dataport From:", "ftp.dataPortFrom"),
        DATAPORT_TO("Dataport To:", "ftp.dataPortTo");

        private String label, jmxAttribute;

        FILE_SERVERS_FIELDS(String label, String jmxAttribute)
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