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
public class CopyMoveUnzipToDialog extends SelectDestinationDialog
{
    @FindAll(@FindBy(css = ".bdft button[id*='copyMoveTo']"))
    private List<WebElement> buttonsList;

    @FindBy(css = ".message")
    private WebElement message;

    @FindBy(css = "#ALF_COPY_MOVE_DIALOG span[class*='call-to-action']:first-child span[id*='alfresco_buttons_AlfButton']:first-child")
    private WebElement createLinkButtonFromSearchPage;

    @FindBy(css = "button[id*='link']")
    private WebElement createLinkButton;

    private By createLinkMessage = By.cssSelector("div[id*='message_c'] .bd .message");

    /**
     * Click on a button from the bottom of Copy/MoveTo dialog
     *
     * @param buttonName name of the button to be clicked (e.g: Move, Cancel)
     */
    public void clickButtton(String buttonName)
    {
        for (WebElement aButtonsList : buttonsList)
        {
            if (aButtonsList.getText().equals(buttonName))
                aButtonsList.click();
        }
        browser.waitInSeconds(3);
    }

    public SharePage clickCreateLink(SharePage page)
    {
        createLinkButton.click();
        browser.waitUntilElementDisappears(createLinkMessage, 15);

        return (SharePage) page.renderedPage();
    }

    /**
     * Verify presence of a given button
     *
     * @param buttonName name of the button to be checked
     * @return true if buttonName is displayed
     */
    public boolean isButtonDisplayed(String buttonName)
    {
        for (WebElement button : buttonsList)
        {
            String text = button.getText();
            if (text.equals(buttonName))
                return true;
        }
        return false;
    }

    /**
     * @return true if 'Create Link' button is displayed in 'Copy to' dialog from Search Page -> Actions
     */
    public boolean isCreateLinkDisplayedInCopyToDialogFromSearchPage()
    {
        return browser.isElementDisplayed(createLinkButtonFromSearchPage);
    }

    public String getMessage()
    {
        browser.waitUntilElementVisible(message);
        return message.getText();
    }
}