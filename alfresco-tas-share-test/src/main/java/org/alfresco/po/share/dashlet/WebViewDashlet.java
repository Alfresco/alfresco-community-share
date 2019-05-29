package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

@PageObject
public class WebViewDashlet extends Dashlet<WebViewDashlet>
{
    @FindBy (css = "div.dashlet.webview div[class$='titleBarActionIcon edit']")
    protected static List<WebElement> configureDashletIcon;
    @FindBy (css = "h3[class$='default-body']")
    protected static HtmlElement defaultDashletMessage;
    @FindBy (css = "div.dashlet.webview div[class$='titleBarActions']")
    protected static WebElement titleBar;
    @FindBy (css = "div[style*='cursor: move']")
    protected static WebElement configureWebViewDashletTitle;
    @RenderWebElement
    @FindBy (css = "div.dashlet.webview")
    protected HtmlElement dashletContainer;
    @Autowired
    ConfigureWebViewDashletPopUp configureWebViewPopUp;
    private By configureWebViewDashletWindow = By.cssSelector("div[class$='yui-panel-container yui-dialog shadow']");

    private By linkTitleField = By.cssSelector("*[name='webviewTitle']");

    private By URLField = By.cssSelector("*[name='url']");

    @Override
    public String getDashletTitle()
    {
        if (dashletContainer.findElement(dashletTitle).findElement(By.cssSelector("a")).getText().equals(""))
        {
            return dashletContainer.findElement(dashletTitle).getText();
        }

        return dashletContainer.findElement(dashletTitle).findElement(By.cssSelector("a")).getText();
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

    /**
     * Click on configure dashlet icon.
     */
    public ConfigureWebViewDashletPopUp clickOnConfigureDashletIcon()
    {
        configureDashletIcon.get(0).click();
        return (ConfigureWebViewDashletPopUp) configureWebViewPopUp.renderedPage();
    }

    /**
     * Configure the web view dashlet.
     *
     * @param linkTitle
     * @param url
     */
    public WebViewDashlet configureWebViewDashlet(String linkTitle, String url)
    {
        configureWebViewPopUp.setLinkTitleField(linkTitle);
        configureWebViewPopUp.setUrlField(url);
        configureWebViewPopUp.clickOkButton();
        return (WebViewDashlet) this.renderedPage();
    }

    /**
     * Returns if configure dashlet icon is displayed on this dashlet.
     *
     * @return True if the configure dashlet icon displayed else false.
     */
    public boolean isConfigureDashletIconDisplayed()
    {
        browser.mouseOver(titleBar);
        return configureDashletIcon.size() > 0;
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
