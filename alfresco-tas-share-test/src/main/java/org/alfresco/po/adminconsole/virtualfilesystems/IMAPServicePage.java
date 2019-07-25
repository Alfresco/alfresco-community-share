package org.alfresco.po.adminconsole.virtualfilesystems;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class IMAPServicePage extends AdminConsolePage<IMAPServicePage>
{
    public static final String IMAP_OBJECT = "Alfresco:Type=Configuration,Category=imap,id1=default";
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
        return "alfresco/s/admin/admin-imap";
    }

    public enum ImapService
    {
        imapServerEnabled("IMAP Server Enabled:", "imap.server.enabled"),
        hostname("Hostname:", "imap.server.host"),
        mailToDefault("Mail TO Default:", "imap.mail.to.default"),
        mailFromDefault("Mail FROM Default:", "imap.mail.from.default"),
        enableImap("Enable IMAP:", "imap.server.imap.enabled"),
        imapPort("Port:", "imap.server.port"),
        enableImaps("Enable IMAPS:", "imap.server.imaps.enabled"),
        imapsPort("Port:", "imap.server.imaps.port");

        private String label, jmxAttribute;

        ImapService(String label, String jmxAttribute)
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
