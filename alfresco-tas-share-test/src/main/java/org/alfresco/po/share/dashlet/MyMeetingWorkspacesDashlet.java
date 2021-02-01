package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

public class MyMeetingWorkspacesDashlet extends Dashlet<MyMeetingWorkspacesDashlet>
{
    private final By defaultDashletMessage = By.cssSelector("div.dashlet-padding h3");
    private final By dashletContainer = By.cssSelector("div.dashlet.my-meeting-workspaces");

    public MyMeetingWorkspacesDashlet(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public MyMeetingWorkspacesDashlet assertNoMeetingWorkspacesMessageIsDisplayed()
    {
        Assert.assertEquals(webElementInteraction.getElementText(defaultDashletMessage),
            language.translate("myMeetingWorkspacesDashlet.noMeeting"));
        return this;
    }
}
