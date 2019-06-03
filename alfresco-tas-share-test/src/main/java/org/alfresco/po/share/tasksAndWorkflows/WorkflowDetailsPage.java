package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class WorkflowDetailsPage extends SharePage<WorkflowDetailsPage>
{
    @Autowired
    TaskDetailsPage taskDetailsPage;

    @Autowired
    EditTaskPage editTaskPage;

    @RenderWebElement
    @FindBy (css = "div[class$='workflow-details-header'] h1")
    private WebElement workflowDetailsHeader;

    @RenderWebElement
    @FindBy (css = "div[id*='workflow-details'].form-fields")
    private WebElement workflowDetailsBody;

    @FindBy (css = "span[id$='_default-startedBy'] a")
    private WebElement startedByUser;

    @FindBy (css = "tbody.yui-dt-data tr td[class$='yui-dt-col-owner'] div a")
    private WebElement assignedToUser;

    @FindBy (css = "span[id$='_default-due']")
    private WebElement dueDate;

    @RenderWebElement
    @FindBy (css = "span[id$='_default-priority']")
    private WebElement priority;

    @RenderWebElement
    @FindBy (css = "span[id$='_default-message']")
    private WebElement message;

    @RenderWebElement
    @FindBy (css = "span[id$='default-status']")
    private WebElement status;

    @FindBy (css = "div[id*='workflowHistory']")
    private WebElement historyBlock;

    @FindBy (css = "a[class='task-edit']")
    private WebElement editTaskButton;

    @FindBy (css = "span[id$='recentTaskOutcome']")
    private WebElement recentOutcome;

    @FindBy (css = "div[id$='recentTaskOwnersComment']")
    private WebElement recentTaskComment;

    private String historyOutcome = "//td[contains(@class, 'col-owner')]//a[text()='%s']//ancestor::tr//td[contains(@class, 'col-state')]//div";

    private String historyComment = "//td[contains(@class, 'col-owner')]//a[text()='%s']//ancestor::tr//td[contains(@class, 'col-properties')]//div";

    private By taskDetailsButton = By.cssSelector("a.task-details");


    @Override
    public String getRelativePath()
    {
        return "share/page/workflow-details";
    }

    public String getWorkflowDetailsHeader()
    {
        return workflowDetailsHeader.getText();
    }

    public String getStartedByUser()
    {
        return startedByUser.getText();
    }

    public String getAssignedToUser()
    {
        int counter = 1;
        int retryCount = 5;
        String user = "";
        while (counter <= retryCount)
        {
            try
            {
                user = assignedToUser.getText();
                break;
            } catch (NoSuchElementException e)
            {
                browser.refresh();
                counter++;
                browser.waitInSeconds(5);
            }
        }
        return user;
    }

    public String getDueDate()
    {
        return dueDate.getText();
    }

    public String getPriority()
    {
        return priority.getText();
    }

    public String getMessage()
    {
        return message.getText();
    }

    public String getStatus()
    {
        return status.getText();
    }

    public String getRecentOutcome()
    {
        return recentOutcome.getText();
    }

    public String getRecentComment()
    {
        return recentTaskComment.getText();
    }

    public TaskDetailsPage clickTaskDetailsButton()
    {
        browser.findElement(taskDetailsButton).click();
        return (TaskDetailsPage) taskDetailsPage.renderedPage();
    }

    public EditTaskPage clickEditTaskButton()
    {
        browser.waitUntilElementVisible(editTaskButton).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public boolean isHistoryBlockPresent()
    {
        return browser.isElementDisplayed(historyBlock);
    }

    public String getHistoryOutcome(String completedByUser)
    {
        return browser.findElement(By.xpath(String.format(historyOutcome, completedByUser))).getText();
    }

    public String getHistoryComment(String completedByUser)
    {
        return browser.findElement(By.xpath(String.format(historyComment, completedByUser))).getText();
    }
}
