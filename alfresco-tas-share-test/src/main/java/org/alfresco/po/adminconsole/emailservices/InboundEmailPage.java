package org.alfresco.po.adminconsole.emailservices;

import org.alfresco.common.Utils;
import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class InboundEmailPage extends AdminConsolePage<InboundEmailPage>
{
    @FindBy (className = "intro")
    WebElement intro;
    @FindBy (className = "info")
    WebElement info;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.enabled")
    WebElement enabled;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.inbound.unknownUser")
    WebElement unknownUser;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.allowed.senders")
    WebElement allowedSenders;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.connections.max")
    WebElement maximumServerConnections;
    @FindBy (id = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.auth.enabled")
    WebElement smtpAuthenticationEnabled;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.port")
    WebElement emailServerPort;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.domain")
    WebElement emailServerDomain;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.blocked.senders")
    WebElement blockedSenders;
    @FindBy (name = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.inbound.emailContributorsAuthority")
    WebElement emailAuthenticationGroup;
    @FindBy (id = "Alfresco:Type=Configuration,Category=email,id1=inbound|email.handler.folder.overwriteDuplicates")
    WebElement overwriteDuplicates;
    @FindBy (name = "tls-support")
    WebElement tlsSupport;
    @FindBy (css = "input[type=submit]")
    Link saveButton;
    @FindBy (className = "cancel")
    Link cancelButton;
    @FindBy (className = "message")
    WebElement message;
    @FindBy (css = ".message>a")
    Link xButton;

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

    public String getUnknownUser()
    {
        return unknownUser.getAttribute("value");
    }

    public String getEnabled()
    {
        return enabled.getAttribute("value");
    }

    public String getAllowedSenders()
    {
        return allowedSenders.getAttribute("value");
    }

    public String getMaximumServerConnections()
    {
        return maximumServerConnections.getAttribute("value");
    }

    public String getSmtpAuthenticationEnabled()
    {
        return smtpAuthenticationEnabled.getAttribute("value");
    }

    public String getEmailServerPort()
    {
        return emailServerPort.getAttribute("value");
    }

    public String getEmailServerDomain()
    {
        return emailServerDomain.getAttribute("value");
    }

    public String getBlockedSenders()
    {
        return blockedSenders.getAttribute("value");
    }

    public String getEmailAuthenticationGroup()
    {
        return emailAuthenticationGroup.getAttribute("value");
    }

    public String getOverwriteDuplicates()
    {
        return overwriteDuplicates.getAttribute("value");
    }

    public String getTlsSupport()
    {
        Select select = new Select(tlsSupport);
        WebElement option = select.getFirstSelectedOption();
        ;
        return option.getText();
    }

    public String getMessage()
    {
        String messageText = message.getText();
        xButton.click();
        return messageText;
    }

    public void clickSave()
    {
        saveButton.click();
        browser.waitUntilElementVisible(message);
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

    public void setAllowedSenders(String value)
    {
        Utils.clearAndType(allowedSenders, value);
    }

    public void clickEnabled()
    {
        By checkboxEnabled = By.id("emailEnabled");
        browser.findElement(checkboxEnabled).click();
    }

    public void setEmailServerDomain(String value)
    {
        Utils.clearAndType(emailServerDomain, value);
    }

    public void setUnknownUser(String value)
    {
        Utils.clearAndType(unknownUser, value);
    }


    public void setMaximumServerConnections(String value)
    {
        maximumServerConnections.clear();
        maximumServerConnections.sendKeys(value);
    }

    public void setEmailServerPort(String value)
    {
        emailServerPort.clear();
        emailServerPort.sendKeys(value);
    }


    public void setEmailAuthenticationGroup(String value)
    {
        emailAuthenticationGroup.clear();
        emailAuthenticationGroup.sendKeys(value);
    }
}
