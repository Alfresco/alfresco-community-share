package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SelectAssigneePopUp extends BaseDialogComponent
{
    private final By userFinderSearchTextInput = By.cssSelector("[id*=default-peopleFinder-search-text]");
    private final By userFinderSearchButton = By.cssSelector("[id*=default-peopleFinder-search-button-button]");
    private final By selectButton = By.cssSelector("[class*=button-button]");

    public SelectAssigneePopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void enterUserToSearch(String user)
    {
        webElementInteraction.clearAndType(userFinderSearchTextInput, user);
    }

    public void clickOnSearchButton()
    {
        webElementInteraction.clickElement(userFinderSearchButton);
    }

    public void clickOnSelectButton()
    {
        webElementInteraction.clickElement(selectButton);
    }
}
