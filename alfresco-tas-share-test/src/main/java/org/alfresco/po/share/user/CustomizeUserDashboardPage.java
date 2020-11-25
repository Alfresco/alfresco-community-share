package org.alfresco.po.share.user;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author bogdan.bocancea
 */
public class CustomizeUserDashboardPage extends DashboardCustomization<CustomizeUserDashboardPage>
{
    @RenderWebElement
    private final By showOnDashboardRadio = By.cssSelector( "input[id$='welcomePanelEnabled']");
    private final By hideFromDashboardRadio = By.cssSelector("input[id$='welcomePanelDisabled']");

    public CustomizeUserDashboardPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/customise-user-dashboard";
    }

    public CustomizeUserDashboardPage selectShowGetStartedPanelOnDashboard()
    {
        LOG.info("Select Show on dashboard");
        clickElement(showOnDashboardRadio);
        return this;
    }

    public CustomizeUserDashboardPage selectHideStartedPanelFromDashboard()
    {
        LOG.info("Select hide from Dashboard");
        clickElement(hideFromDashboardRadio);
        return this;
    }

    public CustomizeUserDashboardPage assertShowOnDashboardIsNotSelected()
    {
        LOG.info("Assert Show on dashboard is NOT selected");
        assertFalse(getBrowser().findElement(showOnDashboardRadio).isSelected(), "Show on dashboard is not selected");
        return this;
    }

    public CustomizeUserDashboardPage assertHideFromDashboardIsSelected()
    {
        LOG.info("Assert Hide from dashboard is selected");
        assertTrue(getBrowser().findElement(hideFromDashboardRadio).isSelected(), "Hide from dashboard is selected");
        return this;
    }

    public CustomizeUserDashboardPage assertShowOnDashboardIsSelected()
    {
        LOG.info("Assert Show on dashboard is selected");
        assertTrue(getBrowser().findElement(showOnDashboardRadio).isSelected(), "Show on dashboard is not selected");
        return this;
    }
}
