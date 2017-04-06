package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.utility.web.HtmlPage;
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

    @RenderWebElement
    @FindBy(css = "div[class$='workflow-details-header'] h1")
    private WebElement workflowDetailsHeader;

    @FindBy(css = "span[id$='_default-startedBy'] a")
    private WebElement startedByUser;

    @FindBy(css = "tbody.yui-dt-data tr td[class$='yui-dt-col-owner'] div a")
    private WebElement assignedToUser;
    @FindBy(css = "span[id$='_default-due']")
    private WebElement dueDate;

    @FindBy(css = "span[id$='_default-priority']")
    private WebElement priority;

    @RenderWebElement
    @FindBy(css = "span[id$='_default-message']")
    private WebElement message;

    private By taskDetailsButton = By.cssSelector("a.task-details");

    @Override
    public String getRelativePath() { return "share/page/workflow-details"; }

    public String getWorkflowDetailsHeader() { return workflowDetailsHeader.getText(); }

    public String getStartedByUser() { return startedByUser.getText(); }

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
            }
            catch (NoSuchElementException e)
            {
                browser.refresh();
                counter++;
                browser.waitInSeconds(5);
            }
        }
        return user;
    }

    public String getDueDate() { return dueDate.getText(); }

    public String getPriority() { return priority.getText(); }

    public String getMessage() { return message.getText(); }

    public HtmlPage clickTaskDetailsButton()
    {
        browser.findElement(taskDetailsButton).click();
        return taskDetailsPage.renderedPage();
    }
}
