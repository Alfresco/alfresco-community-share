package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.testng.Assert;

public class SystemErrorPage extends BasePages
{
    @RenderWebElement
    private By errorHeader = By.cssSelector(".alf-error-header");

    public SystemErrorPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public String getErrorHeader()
    {
        return getBrowser().waitUntilElementVisible(errorHeader).getText();
    }

    public SystemErrorPage assertSomethingIsWrongWithThePageMessageIsDisplayed()
    {
        LOG.info("Assert Something is wrong with the page message is displayed");
        Assert.assertTrue(getErrorHeader().contains(language.translate("systemError.header")));
        return this;
    }
}
