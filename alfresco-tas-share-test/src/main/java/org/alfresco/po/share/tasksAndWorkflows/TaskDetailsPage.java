package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TaskDetailsPage extends SharePage2<TaskDetailsPage>
{
    private final By taskDetailsHeader = By.cssSelector("div[class$='task-details-header'] h1");
    private final By taskDetailsBody = By.cssSelector("div[id*='task-details'].form-fields");
    private final By owner = By.cssSelector("span[id$='_default_prop_taskOwner'] a");
    private final By dueDate = By.xpath("//span[text() = 'Due:']/../span[@class = 'viewmode-value']");
    private final By priority = By.xpath("//span[text() = 'Priority:']/../span[@class = 'viewmode-value']");
    private final By message = By.xpath("//span[text() = 'Message:']/../span[@class = 'viewmode-value']");
    private final By status = By.xpath("//span[text() = 'Status:']/../span[@class = 'viewmode-value']");
    private final By comment = By.xpath("//span[text() = 'Comment:']/../span[@class = 'viewmode-value']");
    private final By workflowDetailsButton = By.cssSelector("div.links a");
    private final By editButton = By.cssSelector("button[id$='default-edit-button']");

    public TaskDetailsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/task-details";
    }

    public String getTaskDetailsHeader()
    {
        waitInSeconds(2);
        return getElementText(taskDetailsHeader);
    }

    public String getStatus()
    {
        return getElementText(status);
    }

    public String getComment()
    {
        return getElementText(comment);
    }

    public WorkflowDetailsPage clickWorkflowDetailsButton()
    {
        clickElement(workflowDetailsButton);
        return new WorkflowDetailsPage(webDriver);
    }

    public EditTaskPage clickEditButton()
    {
        clickElement(editButton);
        return new EditTaskPage(webDriver);
    }
}