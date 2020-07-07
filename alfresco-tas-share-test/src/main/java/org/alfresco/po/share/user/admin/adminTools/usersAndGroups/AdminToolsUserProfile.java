package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class AdminToolsUserProfile extends SharePage<AdminToolsUserProfile>
{
    @RenderWebElement
    @FindBy (xpath = "//button[contains(text(),'Edit User')]")
    private WebElement EditUserButton;

    @RenderWebElement
    @FindBy (xpath = "//button[contains(text(),'Delete User')]")
    private WebElement DeleteUserButton;

    @FindBy (css = "span[id*='default-view-groups']")
    private WebElement addedGroups;

    @Override
    public String getRelativePath()
    {
        String usrName = getUserName();

        return "share/page/console/admin-console/users#state=panel%3Dview%26userid%3D" + usrName + "%26search%3D";
    }

    public AdminToolsUserProfile navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }

    public String getUserGroup()
    {
        return browser.waitUntilElementVisible(addedGroups).getText();
    }
}
