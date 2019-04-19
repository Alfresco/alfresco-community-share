package org.alfresco.po.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class CopyMoveUnzipToDialog extends SelectDestinationDialog {
    @FindAll(@FindBy(css = "div[id='ALF_COPY_MOVE_DIALOG'] span[class*='alfresco-buttons-AlfButton']"))
    private List<WebElement> buttonsList;

    @FindBy(id = "NOTIFICATION_PROMPT")
    private WebElement message;

    @FindBy(css = "#ALF_COPY_MOVE_DIALOG span[class*='call-to-action']:first-child span[id*='alfresco_buttons_AlfButton']:first-child span[id$='label']")
    private WebElement createLinkButtonFromSearchPage;

    @FindBy(css = "button[id$='_default-copyMoveTo-link-button']")
    private WebElement createLinkButton;

    @FindBy(css="button[id$='_default-copyMoveTo-ok-button']")
    private WebElement unzipCopyMoveButton;

    @FindBy(css="button[id$='_default-copyMoveTo-cancel-button']")
    private WebElement cancelButton;

    private By createLinkMessage = By.cssSelector("div[id*='message_c'] .bd .message");

    /**
     * Click on a button from the bottom of Copy/MoveTo dialog
     *
     * @param buttonName name of the button to be clicked (e.g: Move, Cancel)
     */
    public void clickButton(String buttonName) {
        for (WebElement aButtonsList : buttonsList) {
            if (aButtonsList.getText().equals(buttonName))
                aButtonsList.click();
        }
    }

    public SharePage clickCreateLink(SharePage page) {
        browser.waitUntilElementClickable(createLinkButton, 5).click();
        getBrowser().waitUntilElementDisappears(createLinkMessage, 15);
        return (SharePage) page.renderedPage();
    }

    public boolean isCreateLinkButtonDisplayed() {
        return getBrowser().isElementDisplayed(createLinkButton);
    }

    /**
     * @return true if 'Create Link' button is displayed in 'Copy to' dialog from Search Page -> Actions
     */
    public boolean isCreateLinkDisplayedInCopyToDialogFromSearchPage() {
        browser.waitUntilElementVisible(createLinkButtonFromSearchPage);
        return browser.isElementDisplayed(createLinkButtonFromSearchPage);
    }

    public String getMessage() {
        return message.getText();
    }

    public boolean isCreateLinkButtonDisplayedCopyToDialog() {
        return getBrowser().isElementDisplayed(createLinkButton);
    }

    public SharePage clickUnzipButton(SharePage page) {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton, 3).click();
        return (SharePage) page.renderedPage();
    }

    public SharePage clickCopyButton(SharePage page) {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton, 3).click();
        return (SharePage) page.renderedPage();
    }

    public SharePage clickCancelButton(SharePage page) {
        getBrowser().waitUntilElementClickable(cancelButton, 3).click();
        return (SharePage) page.renderedPage();
    }

    public SharePage clickMoveButton(SharePage page) {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton, 3).click();
        return (SharePage) page.renderedPage();
    }
}