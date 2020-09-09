package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class WebViewDashlet extends Dashlet<WebViewDashlet>
{
    @FindBy (css = "div.dashlet.webview div[class$='titleBarActionIcon edit']")
    protected WebElement configureDashletIcon;

    @FindBy (css = "h3[class$='default-body']")
    protected WebElement defaultDashletMessage;

    @FindBy (css = "div.dashlet.webview div[class$='titleBarActions']")
    protected WebElement titleBar;

    @FindBy (css = "div[style*='cursor: move']")
    protected WebElement configureWebViewDashletTitle;

    @RenderWebElement
    @FindBy (css = "div.dashlet.webview")
    protected WebElement dashletContainer;

    @Autowired
    private ConfigureWebViewDashletPopUp configureWebViewPopUp;

    private By configureWebViewDashletWindow = By.cssSelector("div[class$='yui-panel-container yui-dialog shadow']");
    private By linkTitleField = By.cssSelector("*[name='webviewTitle']");
    private By URLField = By.cssSelector("*[name='url']");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Retrieves the default dashlet message.
     *
     * @return String
     */
    public String getDefaultMessage()
    {
        return defaultDashletMessage.getText();
    }

    public WebViewDashlet assertNoWebPageMessageIsDisplayed()
    {
        Assert.assertEquals(defaultDashletMessage.getText(), language.translate("webViewDashlet.defaultMessage"));
        return this;
    }

    /**
     * Click on configure dashlet icon.
     */
    public ConfigureWebViewDashletPopUp clickConfigureDashlet()
    {
        browser.mouseOver(titleBar);
        configureDashletIcon.click();
        return (ConfigureWebViewDashletPopUp) configureWebViewPopUp.renderedPage();
    }

    public WebViewDashlet configureWebViewDashlet(String linkTitle, String url)
    {
        configureWebViewPopUp.setLinkTitleField(linkTitle);
        configureWebViewPopUp.setUrlField(url);
        configureWebViewPopUp.clickOk();
        return (WebViewDashlet) this.renderedPage();
    }

    /**
     * Returns if configure dashlet icon is displayed on this dashlet.
     *
     * @return True if the configure dashlet icon displayed else false.
     */
    public WebViewDashlet assertConfigureDashletIconDisplayed()
    {
        browser.mouseOver(titleBar);
        Assert.assertTrue(browser.isElementDisplayed(configureDashletIcon), "Configure icon is displayed");
        return this;
    }

    /**
     * Returns Configure Web View Dashlet Window Title
     *
     * @return String with the text displayed for Web View Dashlet Window.
     */

    public String getConfigureWebViewDashletWindowTitle()
    {
        return configureWebViewDashletTitle.getText();
    }

    /**
     * Returns true if Configure Web View Window is displayed.
     *
     * @return true if Configure Web View Window is displayed else false.
     */
    public boolean isConfigureWebViewWindowDisplayed()
    {
        return browser.isElementDisplayed(configureWebViewDashletWindow);
    }

    /**
     * Returns true if Link Title field is displayed on Configure Web View Window.
     *
     * @return true if Link Title field is displayed on Configure Web View Window else false.
     */

    public boolean isLinkTitleDisplayedOnConfigureWebViewWindow()
    {
        return browser.isElementDisplayed(linkTitleField);
    }

    /**
     * Returns true if URL field is displayed on Configure Web View Window.
     *
     * @return true if URL field is displayed on Configure Web View Window else false;
     */
    public boolean isURLFieldDisplayedOnConfigureWebViewWindow()
    {
        return browser.isElementDisplayed(URLField);
    }

}
