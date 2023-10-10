package org.alfresco.po.share.site.link;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class DeleteLinkPopUp extends ShareDialog
{
    @FindBy (xpath = "//button[contains(text(), 'Delete')]")
    private WebElement deleteLinkButton;

    @FindBy (xpath = "//button[contains(text(), 'Cancel')]")
    private WebElement cancelDeleteLinkButton;

    @FindBy (css = "[id=prompt] [class=bd]")
    private WebElement deleteLinkMessage;

    public void clickOnDeleteLinkButtonLinkDetailsPage()
    {
        deleteLinkButton.click();
    }

    public void clickOnCancelDeleteLink()
    {
        cancelDeleteLinkButton.click();
    }

    public void isDeleteButtonDisplayed()
    {
        deleteLinkButton.isDisplayed();
    }

    public void isCancelDeleteLinkButtonDisplayed()
    {
        cancelDeleteLinkButton.isDisplayed();
    }

    public String getDeleteLinkMessage()
    {
        return deleteLinkMessage.getText();
    }

    public void clickOnDeleteLinkButtonLinksPage()
    {
        deleteLinkButton.click();
        browser.refresh();
    }
}
