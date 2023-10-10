package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class RenameWikiMainPagePopup extends ShareDialog
{
    @FindBy (css = "[id$=default-renameTo]")
    private WebElement renameInput;

    @FindBy (css = "[id$=default-rename-save-button-button]")
    private WebElement savePageMainNameButton;

    @FindBy (css = "[class=container-close]")
    private WebElement closePopup;

    public void clearWikiTitle()
    {
        renameInput.clear();
    }

    public void typeNewMainPageName(String newName)
    {
        renameInput.sendKeys(newName);
    }

    public void closeRenamePopup()
    {
        browser.waitUntilElementVisible(closePopup);
        closePopup.click();
    }

    public void clickOnSaveButton()
    {
        savePageMainNameButton.click();
        browser.waitInSeconds(2);
    }
}
