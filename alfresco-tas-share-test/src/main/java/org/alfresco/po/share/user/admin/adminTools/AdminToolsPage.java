package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.testng.Assert;

public class AdminToolsPage extends SharePage2<AdminToolsPage> implements AccessibleByMenuBar
{
    @RenderWebElement
    private By toolsLinksList = By.cssSelector(".tool-link");
    @RenderWebElement
    private By adminToolsBody = By.id("alfresco-console");
    private By modulePackage = By.cssSelector("[href='module-package']");
    private By toolsList = By.cssSelector(".tool-link");

    public AdminToolsPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/application";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public AdminToolsPage navigateByMenuBar()
    {
        return (AdminToolsPage) new Toolbar(browser).clickAdminTools().renderedPage();
    }

    public AdminToolsPage assertAdminToolsPageIsOpened()
    {
        Assert.assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "Admin tools url is not correct");
        Assert.assertTrue(getBrowser().isElementDisplayed(adminToolsBody), "Admin tools page is not opened");
        return this;
    }

    public void navigateToNodeFromToolsPanel(String toolName)
    {
        getBrowser().findFirstElementWithValue(toolsLinksList, toolName).click();
    }

    public AdminToolsPage assertToolIsAvailable(String toolName)
    {
        LOG.info(String.format("Assert '%s' is displayed in Admin Tools Page", toolName));
        Assert.assertTrue(getBrowser().isElementDisplayed(
            getBrowser().findFirstElementWithValue(toolsLinksList, toolName)),
            String.format("%s is not displayed in Admin Tools page", toolName));
        return this;
    }
}