package org.alfresco.po.share.user;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author bogdan.bocancea
 */
public class CustomizeUserDashboardPage extends DashboardCustomization<CustomizeUserDashboardPage>
{
    private final By showOnDashboardRadio = By.cssSelector( "input[id$='welcomePanelEnabled']");
    private final By hideFromDashboardRadio = By.cssSelector("input[id$='welcomePanelDisabled']");

    public CustomizeUserDashboardPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/customise-user-dashboard";
    }

    public CustomizeUserDashboardPage selectShowGetStartedPanelOnDashboard()
    {
        LOG.info("Select Show on dashboard");
        webElementInteraction.clickElement(showOnDashboardRadio);
        return this;
    }

    public CustomizeUserDashboardPage selectHideStartedPanelFromDashboard()
    {
        LOG.info("Select hide from Dashboard");
        webElementInteraction.clickElement(hideFromDashboardRadio);
        return this;
    }

    public CustomizeUserDashboardPage assertShowOnDashboardIsNotSelected()
    {
        LOG.info("Assert Show on dashboard is NOT selected");
        assertFalse(webElementInteraction.findElement(showOnDashboardRadio).isSelected(), "Show on dashboard is not selected");
        return this;
    }

    public CustomizeUserDashboardPage assertHideFromDashboardIsSelected()
    {
        LOG.info("Assert Hide from dashboard is selected");
        assertTrue(webElementInteraction.findElement(hideFromDashboardRadio).isSelected(), "Hide from dashboard is selected");
        return this;
    }

    public CustomizeUserDashboardPage assertShowOnDashboardIsSelected()
    {
        LOG.info("Assert Show on dashboard is selected");
        assertTrue(webElementInteraction.findElement(showOnDashboardRadio).isSelected(), "Show on dashboard is not selected");
        return this;
    }
}
