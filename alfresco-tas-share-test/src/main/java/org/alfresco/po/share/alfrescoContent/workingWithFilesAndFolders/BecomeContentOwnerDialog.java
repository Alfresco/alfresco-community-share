package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class BecomeContentOwnerDialog extends ShareDialog
{
    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @FindBy(id = "prompt_h")
    private WebElement title;

    @FindBy(css = "#prompt .bd")
    private WebElement message;

    @FindBy(css = ".button-group button")
    private List<WebElement> buttonList;

    public boolean isDialogDisplayed()
    {
        return browser.isElementDisplayed(message);
    }

    public String getMessage()
    {
        return message.getText();
    }

    public String getHeader()
    {
        return title.getText();
    }

    /**
     * Click on any button from the dialog
     *
     * @param buttonName to be clicked
     */
    public DocumentDetailsPage clickButton(String buttonName)
    {
        for (WebElement button : buttonList)
        {
            if (button.getText().equals(buttonName))
                button.click();
        }
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }
}
