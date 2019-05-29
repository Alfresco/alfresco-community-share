package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SelectAssigneeToWorkflowPopUp extends SelectPopUpPage
{
    @RenderWebElement
    @FindBy (css = "[id$=assignee-cntrl-ok-button]")
    private WebElement okButton;

    @RenderWebElement
    @FindBy (css = "[id$=assignee-cntrl-cancel-button]")
    private WebElement cancelButton;

    @FindBy (css = "[class*=picker-header] input[id*='assignee-cntrl-picker-searchText']")
    private WebElement searchField;

    @FindBy (css = "button[id$='searchButton-button']")
    private WebElement searchButton;

    public void searchUser(String userName)
    {
        browser.waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='assignee-cntrl-picker-searchText']"));
        searchField.sendKeys(userName);
        searchButton.click();
    }

    public void clearSearchField()
    {
        browser.waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='assignee-cntrl-picker-searchText']"));
        searchField.clear();
    }

    public void clickOkButton()
    {
        getBrowser().waitUntilElementVisible(okButton);
        getBrowser().waitUntilElementClickable(okButton).click();
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }
}
