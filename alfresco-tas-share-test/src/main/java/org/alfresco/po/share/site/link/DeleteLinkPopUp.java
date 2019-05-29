package org.alfresco.po.share.site.link;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class DeleteLinkPopUp extends ShareDialog
{
    @Autowired
    LinkPage linkPage;

    @RenderWebElement
    @FindBy (xpath = "//button[contains(text(), 'Delete')]")
    private WebElement deleteLinkButton;

    @RenderWebElement
    @FindBy (xpath = "//button[contains(text(), 'Cancel')]")
    private WebElement cancelDeleteLinkButton;

    @FindBy (css = "[id=prompt] [class=bd]")
    private WebElement deleteLinkMessage;

    public LinkPage clickOnDeleteLinkButtonLinkDetailsPage()
    {
        deleteLinkButton.click();
        return (LinkPage) linkPage.renderedPage();
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
