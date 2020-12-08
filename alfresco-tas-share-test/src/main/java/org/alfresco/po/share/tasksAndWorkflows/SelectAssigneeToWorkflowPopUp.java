package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SelectAssigneeToWorkflowPopUp extends SelectPopUpPage
{
    @RenderWebElement
    private By okButton = By.cssSelector("[id$=assignee-cntrl-ok-button]");
    private By cancelButton = By.cssSelector("[id$=assignee-cntrl-cancel-button]");
    private By searchField = By.cssSelector("[class*=picker-header] input[id*='assignee-cntrl-picker-searchText']");
    private By searchButton = By.cssSelector("button[id$='searchButton-button']");

    public SelectAssigneeToWorkflowPopUp(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public void searchUser(String userName)
    {
        getBrowser().waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='assignee-cntrl-picker-searchText']"));
        getBrowser().findElement(searchField).sendKeys(userName);
        getBrowser().findElement(searchButton).click();
    }

    public void clearSearchField()
    {
        getBrowser().waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='assignee-cntrl-picker-searchText']"));
        getBrowser().findElement(searchField).clear();
    }

    public void clickOkButton()
    {
        getBrowser().waitUntilElementVisible(okButton);
        getBrowser().waitUntilElementClickable(okButton).click();
    }

    public void clickCancelButton()
    {
        getBrowser().findElement(cancelButton).click();
    }
}
