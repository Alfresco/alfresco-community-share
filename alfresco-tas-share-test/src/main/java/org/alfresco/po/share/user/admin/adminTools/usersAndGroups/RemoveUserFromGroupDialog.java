package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class RemoveUserFromGroupDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "#prompt_h")
    private WebElement dialogHeader;

    @RenderWebElement
    @FindBy (css = "#prompt .bd")
    private WebElement dialogMessage;

    @FindAll (@FindBy (css = ".button-group button"))
    private List<WebElement> buttonsList;

    public String getDialogHeader()
    {
        return dialogHeader.getText();
    }

    public String getDialogMessage()
    {
        return dialogMessage.getText();
    }

    public boolean isYesButtonDisplayed()
    {
        return browser.isElementDisplayed(buttonsList.get(0));
    }

    public boolean isNoButtonDisplayed()
    {
        return browser.isElementDisplayed(buttonsList.get(1));
    }

    public void clickYesButton()
    {
        buttonsList.get(0).click();
        waitUntilMessageDisappears();
    }
}
