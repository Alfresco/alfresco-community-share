package org.alfresco.po.share.user;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DashboardCustomization;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author bogdan.bocancea
 */
@Slf4j
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
        log.info("Select Show on dashboard");
        clickElement(showOnDashboardRadio);
        return this;
    }

    public CustomizeUserDashboardPage selectHideStartedPanelFromDashboard()
    {
        log.info("Select hide from Dashboard");
        clickElement(hideFromDashboardRadio);
        return this;
    }

    public CustomizeUserDashboardPage assertShowOnDashboardIsNotSelected()
    {
        log.info("Assert Show on dashboard is NOT selected");
        assertFalse(findElement(showOnDashboardRadio).isSelected(), "Show on dashboard is not selected");
        return this;
    }

    public CustomizeUserDashboardPage assertHideFromDashboardIsSelected()
    {
        log.info("Assert Hide from dashboard is selected");
        assertTrue(findElement(hideFromDashboardRadio).isSelected(), "Hide from dashboard is selected");
        return this;
    }

    public CustomizeUserDashboardPage assertShowOnDashboardIsSelected()
    {
        log.info("Assert Show on dashboard is selected");
        assertTrue(findElement(showOnDashboardRadio).isSelected(), "Show on dashboard is not selected");
        return this;
    }
}
