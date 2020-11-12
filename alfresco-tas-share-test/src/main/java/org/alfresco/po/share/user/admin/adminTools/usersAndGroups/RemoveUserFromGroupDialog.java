package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.testng.Assert;

import static org.testng.Assert.assertTrue;

public class RemoveUserFromGroupDialog extends ShareDialog2
{
    @RenderWebElement
    private By dialogHeader = By.cssSelector("#prompt_h");
    @RenderWebElement
    private By dialogMessage = By.cssSelector("#prompt .bd");
    private By buttonsList = By.cssSelector(".button-group button");

    public RemoveUserFromGroupDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public RemoveUserFromGroupDialog assertDialogTitleIsCorrect()
    {
        LOG.info("Assert 'Remove User from Group' dialog title is displayed");
        Assert.assertEquals(getBrowser().findElement(dialogHeader).getText(), language.translate("adminTools.groups.removeUser.title"),
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
        Assert.assertEquals(getBrowser().findElement(dialogMessage).getText(), expectedDialogMessage,
            "Remove user from group dialog message is correct");
        return this;
    }

    public RemoveUserFromGroupDialog assertYesButtonIsDisplayed()
    {
        LOG.info("Assert Yes button is displayed");
        assertTrue(getBrowser().isElementDisplayed(getBrowser().findElements(buttonsList).get(0)), "Yes button is displayed");
        return this;
    }

    public RemoveUserFromGroupDialog assertNoButtonIsDisplayed()
    {
        LOG.info("Assert No button is displayed");
        assertTrue(getBrowser().isElementDisplayed(getBrowser().findElements(buttonsList).get(1)), "No button is displayed");
        return this;
    }

    public void clickYes()
    {
        LOG.info("Click Yes");
        getBrowser().waitUntilElementClickable(getBrowser().findElements(buttonsList).get(0)).click();
        waitUntilNotificationMessageDisappears();
    }
}
