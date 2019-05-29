package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SelectAssigneePopUp extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "[id*=default-peopleFinder-search-text]")
    private WebElement userFinderSearchTextInput;

    @RenderWebElement
    @FindBy (css = "[id*=default-peopleFinder-search-button-button]")
    private WebElement userFinderSearchButton;

    @FindBy (css = "[class*=button-button]")
    private WebElement selectButton;

    public void enterUserToSearch(String user)
    {
        userFinderSearchTextInput.sendKeys(user);
    }

    public void clickOnSearchButton()
    {
        userFinderSearchButton.click();
    }

    public void clickOnSelectButton()
    {
        selectButton.click();
    }
}
