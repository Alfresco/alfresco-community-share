package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WorkflowDetailsPage extends SharePage2<WorkflowDetailsPage>
{
    private final By workflowDetailsHeader = By.cssSelector("div[class$='workflow-details-header'] h1");
    private final By startedByUser = By.cssSelector("span[id$='_default-startedBy'] a");
    private final By assignedToUser = By.cssSelector("tbody.yui-dt-data tr td[class$='yui-dt-col-owner'] div a");
    private final By dueDate = By.cssSelector("span[id$='_default-due']");
    private final By priority = By.cssSelector("span[id$='_default-priority']");
    private final By message = By.cssSelector("span[id$='_default-message']");
    private final By status = By.cssSelector("span[id$='default-status']");
    private final By historyBlock = By.cssSelector("div[id*='workflowHistory']");
    private final By editTaskButton = By.cssSelector("a[class='task-edit']");
    private final By recentOutcome = By.cssSelector("span[id$='recentTaskOutcome']");
    private final By recentTaskComment = By.cssSelector("div[id$='recentTaskOwnersComment']");
    private final By taskDetailsButton = By.cssSelector("a.task-details");

    private String historyOutcome = "//td[contains(@class, 'col-owner')]//a[text()='%s']//ancestor::tr//td[contains(@class, 'col-state')]//div";
    private String historyComment = "//td[contains(@class, 'col-owner')]//a[text()='%s']//ancestor::tr//td[contains(@class, 'col-properties')]//div";

    public WorkflowDetailsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/workflow-details";
    }

    public String getWorkflowDetailsHeader()
    {
        waitInSeconds(2);
        return getElementText(workflowDetailsHeader);
    }

    public String getStartedByUser()
    {
        return getElementText(startedByUser);
    }

    public String getAssignedToUser()
    {
        return getElementText(assignedToUser);
    }

    public String getDueDate()
    {
        return getElementText(dueDate);
    }

    public String getPriority()
    {
        return getElementText(priority);
    }

    public String getMessage()
    {
        return getElementText(message);
    }

    public String getStatus()
    {
        return getElementText(status);
    }

    public String getRecentOutcome()
    {
        return getElementText(recentOutcome);
    }

    public String getRecentComment()
    {
        return getElementText(recentTaskComment);
    }

    public TaskDetailsPage clickTaskDetailsButton()
    {
        clickElement(taskDetailsButton);
        return new TaskDetailsPage(webDriver);
    }

    public EditTaskPage clickEditTaskButton()
    {
        clickElement(editTaskButton);
        return new EditTaskPage(webDriver);
    }

    public boolean isHistoryBlockPresent()
    {
        waitUntilElementIsVisible(historyBlock);
        return isElementDisplayed(historyBlock);
    }

    public String getHistoryOutcome(String completedByUser)
    {
        return findElement(By.xpath(String.format(historyOutcome, completedByUser))).getText();
    }

    public String getHistoryComment(String completedByUser)
    {
        waitInSeconds(2);
        return findElement(By.xpath(String.format(historyComment, completedByUser))).getText();
    }
}