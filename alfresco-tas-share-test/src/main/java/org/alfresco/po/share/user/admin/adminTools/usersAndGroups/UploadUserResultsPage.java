package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.SharePageObject2;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UploadUserResultsPage extends SharePageObject2
{
    private UsersPage usersPage;

    @RenderWebElement
    private By goBackButton = By.cssSelector("button[id*='default-csv-goback-button-button']");

    public UploadUserResultsPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
        usersPage = new UsersPage(browser);
    }

    public UsersPage clickGoBack()
    {
        clickElement(goBackButton);
        return (UsersPage) usersPage.renderedPage();
    }
}
