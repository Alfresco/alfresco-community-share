package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        return waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public MyMeetingWorkspacesDashlet assertNoMeetingWorkspacesMessageIsDisplayed()
    {
        assertEquals(getElementText(defaultDashletMessage),
            language.translate("myMeetingWorkspacesDashlet.noMeeting"));
        return this;
    }
}
