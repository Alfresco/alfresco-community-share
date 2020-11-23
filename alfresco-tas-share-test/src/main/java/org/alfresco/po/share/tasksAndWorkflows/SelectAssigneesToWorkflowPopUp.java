package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SelectAssigneesToWorkflowPopUp extends SelectPopUpPage
{
    @RenderWebElement
    private By okButton = By.cssSelector("[id$=assignees-cntrl-ok-button]");
    private By cancelButton = By.cssSelector("[id$=assignees-cntrl-cancel-button]");
    private By searchField = By.cssSelector("[class*=picker-header] input[id*='assignees-cntrl-picker-searchText']");

    @FindBy (css = "button[id$='assignees-cntrl-picker-searchButton-button']")
    private By searchButton;

    public SelectAssigneesToWorkflowPopUp(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public void searchUser(String userName)
    {
        getBrowser().waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='assignees-cntrl-picker-searchText']"));
        getBrowser().findElement(searchField).sendKeys(userName);
        getBrowser().findElement(searchButton).click();
    }

    public void clearSearchField()
    {
        getBrowser().waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='assignees-cntrl-picker-searchText']"));
        getBrowser().findElement(searchField).clear();
    }

    public void clickOkButton()
    {
        getBrowser().findElement(okButton).click();
    }

    public void clickCancelButton()
    {
        getBrowser().findElement(cancelButton).click();
    }
}
