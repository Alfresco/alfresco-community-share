package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class ViewTaskPage extends SharePage<ViewTaskPage>
{
    @Autowired
    WorkflowDetailsPage workflowDetailsPage;

    @RenderWebElement
    @FindBy (css = "a[href*='workflow-details']")
    private WebElement workflowDetailsLink;

    @FindBy (css = "div[class$='task-details-header'] h1")
    private WebElement taskDetails;

    @FindBy (css = "div[class='invite-task-title'] span")
    private WebElement inviteTaskDetails;

    @Override
    public String getRelativePath()
    {
        return "share/page/task-details";
    }

    public ViewTaskPage assertViewTaskPageIsOpened()
    {
        LOG.info("Assert View task page is opened");
        Assert.assertTrue(browser.getCurrentUrl().contains(getRelativePath()), "View task page is opened");
        Assert.assertTrue(browser.isElementDisplayed(workflowDetailsLink), "Workflow details link is displayed");
        return this;
    }

    public String getRequestDetails()
    {
        return taskDetails.getText();
    }

    public String getInviteTaskTitle()
    {
        return inviteTaskDetails.getText();
    }

    public WorkflowDetailsPage clickWorkflowDetailsLink()
    {
        workflowDetailsLink.click();
        return (WorkflowDetailsPage) workflowDetailsPage.renderedPage();
    }

}
