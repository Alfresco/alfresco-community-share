package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class RemoveUserFromGroupDialog extends BaseDialogComponent
{
    private final By dialogHeader = By.xpath(".//div[@id='prompt' and contains(@style, 'visibility: inherit;')]/div[@id='prompt_h' and contains(@style, 'cursor: move;')]");
    private final By dialogMessage = By.cssSelector("#prompt .bd");
    private final By buttonsList = By.cssSelector(".button-group button");

    public RemoveUserFromGroupDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public RemoveUserFromGroupDialog assertDialogTitleIsCorrect()
    {
        log.info("Assert 'Remove User from Group' dialog title is displayed");
        assertEquals(getElementText(dialogHeader), language.translate("adminTools.groups.removeUser.title"),
            "Remove user from group dialog title is not correct");
        return this;
    }

    public RemoveUserFromGroupDialog assertRemoveUserDialogMessageIsCorrect(UserModel user)
    {
        log.info("Assert remove dialog message for user {} is correct", user.getUsername());
        String userFormat = String.format("%s %s (%s)",
            user.getFirstName(), user.getLastName(), user.getUsername());
        String expectedDialogMessage = String.format(language.translate("adminTools.groups.removeUser.message"), userFormat);
        log.info("Assert dialog message is {}", dialogMessage);
        assertEquals(findElement(dialogMessage).getText(), expectedDialogMessage,
            "Remove user from group dialog message is correct");
        return this;
    }

    public RemoveUserFromGroupDialog assertYesButtonIsDisplayed()
    {
        log.info("Assert Yes button is displayed");
        assertTrue(isElementDisplayed(findElements(buttonsList).get(0)), "Yes button is displayed");
        return this;
    }

    public RemoveUserFromGroupDialog assertNoButtonIsDisplayed()
    {
        log.info("Assert No button is displayed");
        assertTrue(isElementDisplayed(findElements(buttonsList).get(1)), "No button is displayed");
        return this;
    }

    public void clickYes()
    {
        log.info("Click Yes");
        waitUntilElementIsVisible(findElements(buttonsList).get(0));
        clickElement(findElements(buttonsList).get(0));
        waitUntilNotificationMessageDisappears();
    }
}
