package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class ViewTaskPage extends SharePage<ViewTaskPage>
{
    @RenderWebElement
    @FindBy (css = "a[href*='workflow-details']")
    private Link workflowDetailsLink;

    @FindBy (css = "div[class$='task-details-header'] h1")
    private Link taskDetails;

    @FindBy (css = "div[class='invite-task-title'] span")
    private Link inviteTaskDetails;

    @Override
    public String getRelativePath()
    {

        return "share/page/task-details";
    }

    public String getRequestDetails()
    {
        return taskDetails.getText();
    }

    public String getInviteTaskTitle()
    {
        return inviteTaskDetails.getText();
    }

}
