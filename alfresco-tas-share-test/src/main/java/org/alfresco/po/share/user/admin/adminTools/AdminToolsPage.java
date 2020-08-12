package org.alfresco.po.share.user.admin.adminTools;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.testng.Assert;

@Primary
@PageObject
public class AdminToolsPage extends SharePage<AdminToolsPage> implements AccessibleByMenuBar
{
    @Autowired
    private Toolbar toolbar;

    @RenderWebElement
    @FindAll (@FindBy (css = ".tool-link"))
    private List<WebElement> toolsLinksList;

    @RenderWebElement
    @FindBy (id = "alfresco-console")
    private WebElement adminToolsBody;

    @FindBy (css = "[href='module-package']")
    private WebElement modulePackage;

    private By toolsList = By.cssSelector(".tool-link");

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/application";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public AdminToolsPage navigateByMenuBar()
    {
        toolbar.clickAdminTools();
        return (AdminToolsPage) renderedPage();
    }

    public AdminToolsPage assertAdminToolsPageIsOpened()
    {
        Assert.assertTrue(browser.getCurrentUrl().contains(getRelativePath()), "Admin tools page is opened");
        Assert.assertTrue(browser.isElementDisplayed(adminToolsBody), "Admin tools page is opened");
        return this;
    }

    /**
     * Navigate in Tools panel by click on the specified tool name
     *
     * @param toolName to click on
     */
    public HtmlPage navigateToNodeFromToolsPanel(String toolName, HtmlPage page)
    {
        browser.findFirstElementWithValue(toolsLinksList, toolName).click();
        return page.renderedPage();
    }

    public boolean isToolAvailable(String toolName)
    {
        return browser.isElementDisplayed(browser.findFirstElementWithValue(toolsLinksList, toolName));
    }

    public AdminToolsPage assertToolIsAvailable(String toolName)
    {
        LOG.info(String.format("Assert '%s' is displayed in Admin Tools Page", toolName));
        Assert.assertTrue( browser.isElementDisplayed(browser.findFirstElementWithValue(toolsLinksList, toolName)),
            String.format("%s is displayed in Admin Tools page", toolName));
        return this;
    }
}