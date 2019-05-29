package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class TaskDetailsPage extends SharePage<TaskDetailsPage>
{
    @Autowired
    EditTaskPage editTaskPage;

    @Autowired
    WorkflowDetailsPage workflowDetailsPage;

    @RenderWebElement
    @FindBy (css = "div[class$='task-details-header'] h1")
    private WebElement taskDetailsHeader;

    @RenderWebElement
    @FindBy (css = "div[id*='task-details'].form-fields")
    private WebElement taskDetailsBody;

    @FindBy (css = "span[id$='_default_prop_taskOwner'] a")
    private WebElement owner;

    @FindBy (xpath = "//span[text() = 'Due:']/../span[@class = 'viewmode-value']")
    private WebElement dueDate;

    @FindBy (xpath = "//span[text() = 'Priority:']/../span[@class = 'viewmode-value']")
    private WebElement priority;

    @FindBy (xpath = "//span[text() = 'Message:']/../span[@class = 'viewmode-value']")
    private WebElement message;

    @FindBy (xpath = "//span[text() = 'Status:']/../span[@class = 'viewmode-value']")
    private WebElement status;

    @FindBy (xpath = "//span[text() = 'Comment:']/../span[@class = 'viewmode-value']")
    private WebElement comment;

    private By workflowDetailsButton = By.cssSelector("div.links a");
    private By editButton = By.cssSelector("button[id$='default-edit-button']");

    @Override
    public String getRelativePath()
    {
        return "share/page/task-details";
    }

    public String getTaskDetailsHeader()
    {
        return taskDetailsHeader.getText();
    }

    public String getStatus()
    {
        return status.getText();
    }

    public String getComment()
    {
        return comment.getText();
    }

    public HtmlPage clickWorkflowDetailsButton()
    {
        browser.findElement(workflowDetailsButton).click();
        return workflowDetailsPage.renderedPage();
    }

    public EditTaskPage clickEditButton()
    {
        browser.findElement(editButton).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }
}
