package org.alfresco.po.share.site;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class AssignedToUserPopUp extends SelectPopUpPage
{
    @RenderWebElement
    @FindBy(css = "button[id$=issueAssignedTo-cntrl-ok-button]")
    private WebElement okButton;

    @RenderWebElement
    @FindBy(css = "button[id$=issueAssignedTo-cntrl-cancel-button]")
    private WebElement cancelButton;

    @FindBy(css = "[class*=picker-header] input[id*='issueAssignedTo-cntrl-picker-searchText']")
    private WebElement searchField;

    @FindBy(css = "button[id$='searchButton-button']")
    private WebElement searchButton;

    private By addIcon = By.cssSelector("a[class$='issueAssignedTo-cntrl'] [class*=addIcon]");

    public void searchUser(String userName)
    {
        browser.waitUntilElementVisible(By.cssSelector("[class*=picker-header] input[id*='issueAssignedTo-cntrl-picker-searchText']"));
        searchField.sendKeys(userName);
        searchButton.click();
        browser.waitUntilElementVisible(By.cssSelector("a[class$='issueAssignedTo-cntrl'] [class*=addIcon]"));
    }

    @Override
    public void clickOkButton()
    {
        okButton.click();
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    @Override
    public void clickAddIcon(String item)
    {
        browser.waitUntilElementVisible(addIcon);
        selectDetailsRow(item).findElement(addIcon).click();
    }

}
