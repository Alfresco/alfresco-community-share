package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class BecomeContentOwnerDialog extends ShareDialog
{
    //@Autowired
    DocumentDetailsPage documentDetailsPage;

    @FindBy (id = "prompt_h")
    private WebElement title;

    @FindBy (css = "#prompt .bd")
    private WebElement message;

    @FindBy (css = ".button-group button")
    private List<WebElement> buttonList;

    private WebElement getButton(String buttonName)
    {
        return browser.findElement(By.xpath(String.format("//button[contains(@id, 'button')][text()='%s']", buttonName)));
    }

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
    public void clickButton(String buttonName)
    {
        getButton(buttonName).click();
        browser.waitInSeconds(2);
    }
}
