package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.BasePages;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

public class UploadUserResultsPage extends BasePages
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
