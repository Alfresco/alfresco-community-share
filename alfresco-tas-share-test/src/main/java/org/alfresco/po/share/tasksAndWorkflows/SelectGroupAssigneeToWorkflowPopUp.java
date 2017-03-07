package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SelectGroupAssigneeToWorkflowPopUp extends SelectPopUpPage
{
    @RenderWebElement
    @FindBy(css = "[id$=groupAssignee-cntrl-ok-button]")
    private WebElement okButton;

    @RenderWebElement
    @FindBy(css = "[id$=groupAssignee-cntrl-cancel-button]")
    private WebElement cancelButton;

    @FindBy(css = "[class*=picker-header] input[id*='groupAssignee-cntrl-picker-searchText']")
    private WebElement searchField;

    @FindBy(css = "button[id$='groupAssignee-cntrl-picker-searchButton-button']")
    private WebElement searchButton;

    public void searchGroup(String group)
    {
        browser.waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='groupAssignee-cntrl-picker-searchText']"));
        searchField.sendKeys(group);
        searchButton.click();
    }

    public void clearSearchField()
    {
        browser.waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='groupAssignee-cntrl-picker-searchText"));
        searchField.clear();
    }

    public void clickOkButton()
    {
        okButton.click();
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }
}
