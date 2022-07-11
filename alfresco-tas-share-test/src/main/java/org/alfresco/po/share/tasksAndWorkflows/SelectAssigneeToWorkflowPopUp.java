package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.site.SelectPopUpPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SelectAssigneeToWorkflowPopUp extends SelectPopUpPage
{
    private final By okButton = By.cssSelector("[id$=assignee-cntrl-ok-button]");
    private final By searchField = By.cssSelector("[class*=picker-header] input[id*='assignee-cntrl-picker-searchText']");
    private final By searchButton = By.cssSelector("button[id$='searchButton-button']");

    public SelectAssigneeToWorkflowPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void searchUser(String userName)
    {
        waitUntilElementIsVisible(By.cssSelector("[class*=picker-header] input[id*='assignee-cntrl-picker-searchText']"));
        findElement(searchField).sendKeys(userName);
        clickElement(searchButton);
    }

    public void clickOkButton()
    {
        waitUntilElementIsVisible(okButton);
        clickElement(okButton);
    }
}
