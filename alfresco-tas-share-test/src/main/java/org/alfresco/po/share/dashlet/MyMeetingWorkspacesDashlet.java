package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

@PageObject
public class MyMeetingWorkspacesDashlet extends Dashlet<MyMeetingWorkspacesDashlet>
{
    @FindBy (css = "div.dashlet-padding h3")
    private WebElement defaultDashletMessage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.my-meeting-workspaces")
    protected WebElement dashletContainer;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public MyMeetingWorkspacesDashlet assertNoMeetingWorkspacesMessageIsDisplayed()
    {
        Assert.assertEquals(defaultDashletMessage.getText(), language.translate("myMeetingWorkspacesDashlet.noMeeting"));
        return this;
    }
}
