package org.alfresco.po.adminconsole.emailservices;

import org.alfresco.common.Utils;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.alfresco.utility.report.log.Step.STEP;

public class InboundEmailPages<T> extends SharePage2<InboundEmailPages<T>> {

    private By intro = By.className("intro");
    private By info = By.className("info");
    private By enabled = By.name("Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.enabled");
    private By unknownUser = By.name("Alfresco:Type=Configuration,Category=email,id1=inbound|email.inbound.unknownUser");
    private By allowedSenders = By.name("Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.allowed.senders");
    private By maximumServerConnections = By.name("Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.connections.max");
    private By smtpAuthenticationEnabled = By.id("Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.auth.enabled");
    private By emailServerPort = By.name("Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.port");
    private By emailServerDomain = By.name("Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.domain");
    private By blockedSenders = By.name("Alfresco:Type=Configuration,Category=email,id1=inbound|email.server.blocked.senders");
    private By emailAuthenticationGroup = By.name("Alfresco:Type=Configuration,Category=email,id1=inbound|email.inbound.emailContributorsAuthority");
    private By saveButton = By.cssSelector("input[type=submit]");
    private By cancelButton = By.className("cancel");
    private By message = By.className("message");
    private By xButton = By.cssSelector(".message>a");
    private By clickSaveButton = By.cssSelector(".submission.buttons>input[type='submit']");

    public InboundEmailPages(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/s/enterprise/admin/admin-inboundemail";
    }

    public InboundEmailPages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public String getInfoPage()
    {
        return findElement(info).getText();
    }

    public String getIntroPage()
    {
        return findElement(intro).getText();
    }

    public String getUnknownUser()
    {
        return findElement(unknownUser).getAttribute("value");
    }

    public String getEnabled()
    {
        return findElement(enabled).getAttribute("value");
    }

    public String getAllowedSenders()
    {
        return findElement(allowedSenders).getAttribute("value");
    }

    public String getMaximumServerConnections()
    {
        return findElement(maximumServerConnections).getAttribute("value");
    }

    public String getEmailServerPort()
    {
        return findElement(emailServerPort).getAttribute("value");
    }

    public String getEmailServerDomain()
    {
        return findElement(emailServerDomain).getAttribute("value");
    }

    public String getBlockedSenders()
    {
        return findElement(blockedSenders).getAttribute("value");
    }

    public String getEmailAuthenticationGroup()
    {
        return findElement(emailAuthenticationGroup).getAttribute("value");
    }

    public String getMessage()
    {
        String messageText = findElement(message).getText();
        findElement(xButton).click();
        return messageText;
    }

    public void clickSave()
    {
        scrollToElement(findElement(saveButton));
        findElement(saveButton).click();
        waitUntilElementIsVisible(message);
    }

    public void clickCancel()
    {
        findElement(cancelButton).click();
    }

    public void setAllowedSenders(String value)
    {
        Utils.clearAndType(findElement(allowedSenders), value);
    }

    public void clickEnabled()
    {
        By checkboxEnabled = By.id("emailEnabled");
        findElement(checkboxEnabled).click();
    }

    public void setEmailServerDomain(String value)
    {
        Utils.clearAndType(findElement(emailServerDomain), value);
    }

    public void clickSaveButton()
    {
        waitUntilElementClickable(clickSaveButton).click();
    }
}
