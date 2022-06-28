package org.alfresco.po.share.tasksAndWorkflows;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
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
        log.info("Assert View task page is opened");
        waitUntilElementIsVisible(workflowDetailsLink);
        assertTrue(getCurrentUrl().contains(getRelativePath()), "View task page is opened");
        assertTrue(isElementDisplayed(workflowDetailsLink), "Workflow details link is displayed");
        return this;
    }

    public ViewTaskPage assertRequestDetailsEqualTo(String expectedRequestDetails)
    {
        log.info("Assert request details equal to {}", expectedRequestDetails);
        String actualRequestDetails = getElementText(taskDetails);
        assertEquals(actualRequestDetails, expectedRequestDetails);
        return this;
    }

    public ViewTaskPage assertInviteTaskTitleEqualsTo(String expectedInviteTaskTitle)
    {
        log.info("Assert invite task title equals to {}", expectedInviteTaskTitle);
        String actualInviteTaskTitle = getElementText(inviteTaskDetails);
        assertEquals(actualInviteTaskTitle, expectedInviteTaskTitle);
        return this;
    }

    public WorkflowDetailsPage clickWorkflowDetailsLink()
    {
        clickElement(workflowDetailsLink);
        return new WorkflowDetailsPage(webDriver);
    }
}