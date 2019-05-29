package org.alfresco.po.adminconsole.emailservices;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class InboundEmailPage extends AdminConsolePage<InboundEmailPage>
{
    @FindBy (className = "intro")
    WebElement intro;
    @FindBy (className = "info")
    WebElement info;
    @FindBy (id = "emailEnabled")
    WebElement enabled;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.inbound.unknownUser")
    WebElement unknownuser;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.allowed.senders")
    WebElement allowedsenders;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.connections.max")
    WebElement maximumserverconnections;
    @FindBy (id = "email.server.auth.enabled")
    WebElement smtpauthenticationenabled;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.port")
    WebElement emailserverport;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.domain")
    WebElement emailserverdomain;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.blocked.senders")
    WebElement blockedsenders;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.inbound.emailContributorsAuthority")
    WebElement emailauthenticationgroup;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-inboundemail";
    }

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

}
