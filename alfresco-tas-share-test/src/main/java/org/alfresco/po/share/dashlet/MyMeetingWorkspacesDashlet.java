package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

@PageObject
public class MyMeetingWorkspacesDashlet extends Dashlet<MyMeetingWorkspacesDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.my-meeting-workspaces")
    protected HtmlElement dashletContainer;

    @FindBy (css = "div.dashlet-padding h3")
    protected static HtmlElement defaultDashletMessage;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
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

}
