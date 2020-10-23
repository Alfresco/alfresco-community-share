package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

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

    public RemoveUserFromGroupDialog assertDialogTitleIsCorrect()
    {
        LOG.info("Assert 'Remove User from Group' dialog title is displayed");
        Assert.assertEquals(dialogHeader.getText(), language.translate("adminTools.groups.removeUser.title"),
            "Remove user from group title is correct");
        return this;
    }

    public RemoveUserFromGroupDialog assertRemoveUserDialogMessageIsCorrect(UserModel user)
    {
        LOG.info("Assert remove dialog message for user {} is correct", user.getUsername());
        String userFormat = String.format("%s %s (%s)",
            user.getFirstName(), user.getLastName(), user.getUsername());
        String expectedDialogMessage = String.format(language.translate("adminTools.groups.removeUser.message"), userFormat);
        LOG.info(String.format("Assert dialog message is %s", dialogMessage));
        Assert.assertEquals(dialogMessage.getText(), expectedDialogMessage,
            "Remove user from group dialog message is correct");
        return this;
    }

    public RemoveUserFromGroupDialog assertYesButtonIsDisplayed()
    {
        LOG.info("Assert Yes button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(buttonsList.get(0)), "Yes button is displayed");
        return this;
    }

    public RemoveUserFromGroupDialog assertNoButtonIsDisplayed()
    {
        LOG.info("Assert No button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(buttonsList.get(1)), "No button is displayed");
        return this;
    }

    public void clickYes()
    {
        LOG.info("Click Yes");
        browser.waitUntilElementClickable(buttonsList.get(0)).click();
        waitUntilMessageDisappears();
    }
}
