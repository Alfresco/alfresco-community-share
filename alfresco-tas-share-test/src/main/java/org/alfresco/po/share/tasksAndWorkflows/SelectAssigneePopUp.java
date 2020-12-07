package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

public class SelectAssigneePopUp extends BaseDialogComponent
{
    @RenderWebElement
    private final By userFinderSearchTextInput = By.cssSelector("[id*=default-peopleFinder-search-text]");
    private final By userFinderSearchButton = By.cssSelector("[id*=default-peopleFinder-search-button-button]");
    private final By selectButton = By.cssSelector("[class*=button-button]");

    public SelectAssigneePopUp(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public void enterUserToSearch(String user)
    {
        getBrowser().findElement(userFinderSearchTextInput).sendKeys(user);
    }

    public void clickOnSearchButton()
    {
        getBrowser().findElement(userFinderSearchButton).click();
    }

    public void clickOnSelectButton()
    {
        getBrowser().findElement(selectButton).click();
    }
}
