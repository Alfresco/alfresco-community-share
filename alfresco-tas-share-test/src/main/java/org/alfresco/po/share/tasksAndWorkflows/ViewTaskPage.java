package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ViewTaskPage extends SharePage2<ViewTaskPage>
{
    private final By workflowDetailsLink = By.cssSelector("a[href*='workflow-details']");
    private final By taskDetails = By.cssSelector("div[class$='task-details-header'] h1");
    private final By inviteTaskDetails = By.cssSelector("div[class='invite-task-title'] span");

    public ViewTaskPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/task-details";
    }

    public ViewTaskPage assertViewTaskPageIsOpened()
    {
        LOG.info("Assert View task page is opened");
        webElementInteraction.waitUntilElementIsVisible(workflowDetailsLink);
        Assert.assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "View task page is opened");
        Assert.assertTrue(webElementInteraction.isElementDisplayed(workflowDetailsLink), "Workflow details link is displayed");
        return this;
    }

    public String getRequestDetails()
    {
        return webElementInteraction.getElementText(taskDetails);
    }

    public String getInviteTaskTitle()
    {
        return webElementInteraction.getElementText(inviteTaskDetails);
    }

    public WorkflowDetailsPage clickWorkflowDetailsLink()
    {
        webElementInteraction.clickElement(workflowDetailsLink);
        return new WorkflowDetailsPage(webDriver);
    }
}