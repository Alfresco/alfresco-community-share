package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UploadUserResultsPage extends BasePage
{
    private final By goBackButton = By.cssSelector("button[id*='default-csv-goback-button-button']");

    public UploadUserResultsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public UsersPage clickGoBack()
    {
        webElementInteraction.waitUntilElementIsVisible(goBackButton);
        webElementInteraction.clickElement(goBackButton);
        waitUntilNotificationMessageDisappears();
        return new UsersPage(webDriver);
    }
}
