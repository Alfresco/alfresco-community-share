package org.alfresco.po.share.user;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class CustomizeUserDashboardPage extends DashboardCustomization<CustomizeUserDashboardPage>
{
    @RenderWebElement
    @FindBy (css = "div[id$='default-welcome-preference']")
    private WebElement getStartedPanel;

    @RenderWebElement
    @FindBy (css = "input[id$='welcomePanelEnabled']")
    private WebElement showOnDashboardRadio;

    @FindBy (css = "input[id$='welcomePanelDisabled']")
    private WebElement hideFromDashboardRadio;

    @Override
    public String getRelativePath()
    {
        return "share/page/customise-user-dashboard";
    }

    public CustomizeUserDashboardPage selectShowGetStartedPanelOnDashboard()
    {
        LOG.info("Select Show on dashboard");
        showOnDashboardRadio.click();
        return this;
    }

    public CustomizeUserDashboardPage selectHideStartedPanelFromDashboard()
    {
        LOG.info("Select hide from Dashboard");
        hideFromDashboardRadio.click();
        return this;
    }

    public CustomizeUserDashboardPage assertShowOnDashboardIsNotSelected()
    {
        LOG.info("Assert Show on dashboard is NOT selected");
        Assert.assertFalse(showOnDashboardRadio.isSelected(), "Show on dashboard is not selected");
        return this;
    }

    public CustomizeUserDashboardPage assertHideFromDashboardIsSelected()
    {
        LOG.info("Assert Hide from dashboard is selected");
        Assert.assertTrue(hideFromDashboardRadio.isSelected(), "Hide from dashboard is selected");
        return this;
    }

    public CustomizeUserDashboardPage assertShowOnDashboardIsSelected()
    {
        LOG.info("Assert Show on dashboard is selected");
        Assert.assertTrue(showOnDashboardRadio.isSelected(), "Show on dashboard is not selected");
        return this;
    }
}
