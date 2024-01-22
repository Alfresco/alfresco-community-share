package org.alfresco.po.share.site.link;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DeleteLinkPopUp extends SiteCommon<DeleteLinkPopUp> {
    private By deleteLinkButton = By.xpath("//button[contains(text(), 'Delete')]");
    private By cancelDeleteLinkButton = By.xpath("//button[contains(text(), 'Cancel')]");
    private By deleteLinkMessage = By.cssSelector("[id=prompt] [class=bd]");

    public DeleteLinkPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links", getCurrentSiteName());
    }

    public void clickOnDeleteLinkButtonLinkDetailsPage()
    {
        clickElement(deleteLinkButton);
    }

    public void clickOnCancelDeleteLink()
    {
        clickElement(cancelDeleteLinkButton);
    }

    public void isDeleteButtonDisplayed()
    {
        findElement(deleteLinkButton).isDisplayed();
    }

    public void isCancelDeleteLinkButtonDisplayed()
    {
        findElement(cancelDeleteLinkButton).isDisplayed();
    }

    public String getDeleteLinkMessage()
    {
        return findElement(deleteLinkMessage).getText();
    }

    public void clickOnDeleteLinkButtonLinksPage()
    {
        clickElement(deleteLinkButton);
    }
}
