package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class UploadResults extends SharePage<UploadResults>
{
    @RenderWebElement
    @FindBy (css = "button[id*='default-csv-goback-button-button']")
    private WebElement goBackButton;

    @Autowired
    UsersPage usersPage;

    public UsersPage clickGoBack()
    {
        goBackButton.click();
        return (UsersPage) usersPage.renderedPage();
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
