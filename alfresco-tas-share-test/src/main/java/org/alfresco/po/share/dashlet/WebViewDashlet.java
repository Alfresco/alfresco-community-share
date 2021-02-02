package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WebViewDashlet extends Dashlet<WebViewDashlet>
{
    private final By configureDashletIcon = By.cssSelector("div.dashlet.webview div[class$='titleBarActionIcon edit']");
    private final By defaultDashletMessage = By.cssSelector("h3[class$='default-body']");
    private final By titleBarActions = By.cssSelector("div.dashlet.webview div[class$='titleBarActions']");
    private final By titleBar = By.cssSelector("div.dashlet.webview .title");
    private final By dashletContainer = By.cssSelector( "div.dashlet.webview");

    public WebViewDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public WebViewDashlet assertNoWebPageMessageIsDisplayed()
    {
        assertEquals(webElementInteraction.getElementText(defaultDashletMessage), language.translate("webViewDashlet.defaultMessage"));
        return this;
    }

    public ConfigureWebViewDashletPopUp clickConfigureDashlet()
    {
        webElementInteraction.mouseOver(titleBar);
        webElementInteraction.mouseOver(titleBarActions);
        webElementInteraction.clickElement(configureDashletIcon);

        return new ConfigureWebViewDashletPopUp(webDriver);
    }

    public WebViewDashlet assertConfigureDashletIconDisplayed()
    {
        webElementInteraction.mouseOver(titleBar);
        webElementInteraction.mouseOver(titleBarActions);
        assertTrue(webElementInteraction.isElementDisplayed(configureDashletIcon), "Configure icon is displayed");
        return this;
    }
}
