package org.alfresco.po.share.alfrescoContent.organizingContent;

import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Laura.Capsa
 */
@PageObject
public class EmptyTrashcanDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (id = "prompt_h")
    private WebElement header;

    @RenderWebElement
    @FindBy (css = "#prompt .bd")
    private WebElement message;

    @FindBy (css = ".button-group button")
    private List<WebElement> buttonsList;

    public String getDialogHeader()
    {
        if (browser.isElementDisplayed(header))
            return header.getText();
        return "'Empty trashcan' isn't displayed!";
    }

    public String getMessage()
    {
        return message.getText();
    }

    /**
     * Click on a form button
     *
     * @param buttonName to be clicked: OK, Cancel
     */
    public void clickButton(String buttonName)
    {
        browser.findFirstElementWithValue(buttonsList, buttonName).click();
    }
}
